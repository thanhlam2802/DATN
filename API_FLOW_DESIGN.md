# Super Admin Bus Management - API Flow Design

## 🎯 Tổng quan hệ thống

Hệ thống Super Admin Bus Management được thiết kế theo mô hình **multi-tenant** với các nhà xe (providers) khác nhau. Super Admin có quyền quản trị toàn bộ hệ thống, bao gồm:

- **Quản lý Providers**: Ban/Unban, set policies
- **Quản lý Dictionaries**: Categories, Seat Maps, Route Catalog
- **Workflow Approval**: Routes, Pricebooks, Promotions
- **Data Quality**: Alerts, Validation, Audit Trail
- **Bulk Operations**: Import/Export, Bulk Generate

## 📋 Luồng hoạt động chính

### 1. OVERVIEW DASHBOARD

#### 1.1 KPI Dashboard
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant Service as Dashboard Service
    participant DB as Database
    
    FE->>API: GET /api/super-admin/bus/dashboard/kpis?date=2025-01-20
    API->>Service: DashboardService.getKPIs(date)
    Service->>DB: Query bookings, occupancy, cancellations, providers
    DB-->>Service: Aggregated data
    Service-->>API: KPIData {bookingsToday, occupancyRate, cancellations, activeProviders}
    API-->>FE: JSON Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/dashboard/kpis?date=YYYY-MM-DD`
- `GET /api/super-admin/bus/dashboard/alerts?limit=50&query=string`

#### 1.2 Data Quality Alerts
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant AlertService as Alert Service
    participant Validator as Data Validator
    
    FE->>API: GET /api/super-admin/bus/dashboard/alerts
    API->>AlertService: AlertService.getAlerts(filters)
    AlertService->>Validator: Check price overlaps, duplicate slots, missing data
    Validator-->>AlertService: Alert list
    AlertService-->>API: AlertData[]
    API-->>FE: JSON Response
```

### 2. DICTIONARIES MANAGEMENT

#### 2.1 Bus Categories Flow
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant CategoryService as Category Service
    participant DB as Database
    
    Note over FE,DB: CRUD Operations
    FE->>API: POST /api/super-admin/bus/global/categories
    API->>CategoryService: CategoryService.create(categoryData)
    CategoryService->>DB: Validate unique code, insert
    DB-->>CategoryService: Created category
    CategoryService-->>API: CategoryResponse
    API-->>FE: Success/Error Response
    
    FE->>API: GET /api/super-admin/bus/global/categories?query=string
    API->>CategoryService: CategoryService.search(query)
    CategoryService->>DB: SELECT with filters
    DB-->>CategoryService: Category list
    CategoryService-->>API: Category[]
    API-->>FE: JSON Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/global/categories?query=string`
- `POST /api/super-admin/bus/global/categories`
- `PUT /api/super-admin/bus/global/categories/{code}`
- `DELETE /api/super-admin/bus/global/categories/{code}`

#### 2.2 Seat Maps Flow
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant SeatMapService as Seat Map Service
    participant Validator as Seat Validator
    
    FE->>API: POST /api/super-admin/bus/global/seat-maps
    API->>SeatMapService: SeatMapService.create(seatMapData)
    SeatMapService->>Validator: Validate seat layout, total seats
    Validator-->>SeatMapService: Validation result
    SeatMapService->>DB: Insert seat map
    DB-->>SeatMapService: Created seat map
    SeatMapService-->>API: SeatMapResponse
    API-->>FE: Success/Error Response
```

### 3. PROVIDERS MANAGEMENT

#### 3.1 Provider CRUD & Status Management
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant ProviderService as Provider Service
    participant AuthService as Auth Service
    participant DB as Database
    
    Note over FE,DB: Provider Management
    FE->>API: GET /api/super-admin/bus/providers?status=ACTIVE
    API->>ProviderService: ProviderService.getProviders(filters)
    ProviderService->>DB: Query providers with stats
    DB-->>ProviderService: Provider list with counts
    ProviderService-->>API: Provider[]
    API-->>FE: JSON Response
    
    FE->>API: POST /api/super-admin/bus/providers/{id}/ban
    API->>ProviderService: ProviderService.banProvider(id, reason)
    ProviderService->>AuthService: Revoke access tokens
    ProviderService->>DB: Update status to BANNED
    DB-->>ProviderService: Updated provider
    ProviderService-->>API: Success response
    API-->>FE: Success/Error Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/providers?query=string&status=ACTIVE|BANNED`
- `POST /api/super-admin/bus/providers`
- `PUT /api/super-admin/bus/providers/{id}`
- `POST /api/super-admin/bus/providers/{id}/ban`
- `POST /api/super-admin/bus/providers/{id}/unban`

### 4. ROUTES MANAGEMENT (Workflow Approval)

#### 4.1 Route Creation & Approval Flow
```mermaid
sequenceDiagram
    participant Provider as Provider
    participant FE as Frontend
    participant API as API Gateway
    participant RouteService as Route Service
    participant ModerationService as Moderation Service
    participant NotificationService as Notification Service
    
    Note over Provider,NotificationService: Route Creation & Approval
    Provider->>FE: Create new route
    FE->>API: POST /api/super-admin/bus/routes
    API->>RouteService: RouteService.create(routeData)
    RouteService->>DB: Validate unique (owner, route_code), insert as DRAFT
    DB-->>RouteService: Created route
    RouteService-->>API: RouteResponse
    API-->>FE: Success Response
    
    Provider->>FE: Submit for review
    FE->>API: POST /api/super-admin/bus/routes/{id}/submit-review
    API->>RouteService: RouteService.submitForReview(id)
    RouteService->>DB: Update status to PENDING_REVIEW
    RouteService->>ModerationService: Add to approval queue
    RouteService->>NotificationService: Notify super admin
    RouteService-->>API: Success response
    API-->>FE: Success Response
    
    Note over Provider,NotificationService: Super Admin Review
    FE->>API: POST /api/super-admin/bus/routes/{id}/approve
    API->>RouteService: RouteService.approve(id, reason)
    RouteService->>DB: Update status to APPROVED
    RouteService->>NotificationService: Notify provider
    RouteService-->>API: Success response
    API-->>FE: Success Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/routes?owner=string&origin=string&destination=string&status=DRAFT|PENDING_REVIEW|APPROVED|REJECTED|ARCHIVED`
- `POST /api/super-admin/bus/routes`
- `PUT /api/super-admin/bus/routes/{id}`
- `POST /api/super-admin/bus/routes/{id}/submit-review`
- `POST /api/super-admin/bus/routes/{id}/approve`
- `POST /api/super-admin/bus/routes/{id}/reject`

### 5. PRICEBOOKS MANAGEMENT

#### 5.1 Pricebook Creation & Approval Flow
```mermaid
sequenceDiagram
    participant Provider as Provider
    participant FE as Frontend
    participant API as API Gateway
    participant PricebookService as Pricebook Service
    participant PriceValidator as Price Validator
    participant ModerationService as Moderation Service
    
    Note over Provider,ModerationService: Pricebook Creation
    Provider->>FE: Create pricebook
    FE->>API: POST /api/super-admin/bus/pricebooks
    API->>PricebookService: PricebookService.create(pricebookData)
    PricebookService->>PriceValidator: Validate no overlap prices
    PriceValidator-->>PricebookService: Validation result
    PricebookService->>DB: Insert pricebook as DRAFT
    DB-->>PricebookService: Created pricebook
    PricebookService-->>API: PricebookResponse
    API-->>FE: Success Response
    
    Note over Provider,ModerationService: Price Resolution
    FE->>API: GET /api/super-admin/bus/prices/resolve?owner=string&route=string&category=string&onDate=YYYY-MM-DD
    API->>PricebookService: PricebookService.resolvePrice(params)
    PricebookService->>DB: Find applicable pricebook
    PricebookService->>SurchargeService: Apply surcharges
    PricebookService-->>API: ResolvedPrice {base, final, surcharges}
    API-->>FE: JSON Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/pricebooks?owner=string&status=string&onDate=YYYY-MM-DD`
- `POST /api/super-admin/bus/pricebooks`
- `PUT /api/super-admin/bus/pricebooks/{id}`
- `POST /api/super-admin/bus/pricebooks/{id}/approve`
- `POST /api/super-admin/bus/pricebooks/{id}/reject`
- `GET /api/super-admin/bus/prices/resolve?owner=string&route=string&category=string&onDate=YYYY-MM-DD`

### 6. SLOTS MANAGEMENT

#### 6.1 Slot Creation & Bulk Generation
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant SlotService as Slot Service
    participant ConflictChecker as Conflict Checker
    participant DB as Database
    
    Note over FE,DB: Bulk Slot Generation
    FE->>API: POST /api/super-admin/bus/slots/bulk-generate
    API->>SlotService: SlotService.bulkGenerate(rule)
    SlotService->>ConflictChecker: Check for time conflicts
    ConflictChecker-->>SlotService: Conflict report
    SlotService->>DB: Insert valid slots
    DB-->>SlotService: Generated slots
    SlotService-->>API: BulkGenerateResponse {success, conflicts}
    API-->>FE: JSON Response
    
    Note over FE,DB: Conflict Check
    FE->>API: GET /api/super-admin/bus/slots/conflict-check?owner=string&route=string&date=YYYY-MM-DD
    API->>SlotService: SlotService.checkConflicts(params)
    SlotService->>DB: Query overlapping slots
    DB-->>SlotService: Conflict list
    SlotService-->>API: ConflictReport
    API-->>FE: JSON Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/slots?owner=string&route=string&date=YYYY-MM-DD`
- `POST /api/super-admin/bus/slots`
- `POST /api/super-admin/bus/slots/bulk-generate`
- `GET /api/super-admin/bus/slots/conflict-check`

### 7. MODERATION QUEUE

#### 7.1 Approval Queue Management
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant ModerationService as Moderation Service
    participant AuditService as Audit Service
    participant NotificationService as Notification Service
    
    Note over FE,NotificationService: Approval Queue
    FE->>API: GET /api/super-admin/bus/moderation/queue?type=ROUTE&status=PENDING_REVIEW
    API->>ModerationService: ModerationService.getQueue(filters)
    ModerationService->>DB: Query pending items
    DB-->>ModerationService: Queue items
    ModerationService-->>API: ModerationItem[]
    API-->>FE: JSON Response
    
    Note over FE,NotificationService: Approve/Reject
    FE->>API: POST /api/super-admin/bus/{entity}/{id}/approve
    API->>ModerationService: ModerationService.approve(entity, id, reason)
    ModerationService->>AuditService: Log approval action
    ModerationService->>NotificationService: Notify submitter
    ModerationService-->>API: Success response
    API-->>FE: Success Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/moderation/queue?type=ROUTE|PRICEBOOK|PROMOTION&status=PENDING_REVIEW|APPROVED|REJECTED`
- `POST /api/super-admin/bus/{entity}/{id}/approve`
- `POST /api/super-admin/bus/{entity}/{id}/reject`

### 8. AUDIT TRAIL

#### 8.1 Audit Logging Flow
```mermaid
sequenceDiagram
    participant Service as Any Service
    participant AuditService as Audit Service
    participant DB as Database
    
    Note over Service,DB: Automatic Audit Logging
    Service->>AuditService: AuditService.log(entityType, entityId, action, before, after, actor)
    AuditService->>DB: Insert audit record
    DB-->>AuditService: Audit record created
    
    Note over Service,DB: Audit Query
    FE->>API: GET /api/super-admin/bus/audits?entityType=string&entityId=string&actor=string&from=YYYY-MM-DD&to=YYYY-MM-DD
    API->>AuditService: AuditService.getAudits(filters)
    AuditService->>DB: Query audit logs
    DB-->>AuditService: Audit records
    AuditService-->>API: Audit[]
    API-->>FE: JSON Response
```

**API Endpoints:**
- `GET /api/super-admin/bus/audits?entityType=string&entityId=string&actor=string&from=YYYY-MM-DD&to=YYYY-MM-DD`

## 🔐 Security & Authorization

### Authentication Flow
```mermaid
sequenceDiagram
    participant FE as Frontend
    participant API as API Gateway
    participant AuthService as Auth Service
    participant DB as Database
    
    FE->>API: Request with JWT token
    API->>AuthService: AuthService.validateToken(token)
    AuthService->>DB: Check token validity & permissions
    DB-->>AuthService: User & role info
    AuthService-->>API: Authorization result
    API->>Service: Proceed with request (if authorized)
```

### Role-Based Access Control
- **SUPER_ADMIN**: Full access to all endpoints
- **PROVIDER_ADMIN**: Limited to own provider data
- **AUDITOR**: Read-only access to audit logs

## 📊 Data Models

### Core Entities
```typescript
interface Provider {
  id: number
  code: string
  name: string
  status: 'ACTIVE' | 'BANNED'
  createdAt: string
  updatedAt: string
}

interface Route {
  id: number
  ownerCode: string
  routeCode: string
  origin: string
  destination: string
  status: 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED'
  submittedAt?: string
  approvedAt?: string
  approvedBy?: string
  rejectionReason?: string
}

interface Pricebook {
  id: number
  ownerCode: string
  name: string
  effectiveFrom: string
  effectiveTo: string
  status: 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED'
  lines: PricebookLine[]
}

interface PricebookLine {
  id: number
  routeCode: string
  busCategoryCode: string
  basePrice: number
}

interface AuditLog {
  id: number
  entityType: string
  entityId: string
  action: string
  actor: string
  before?: any
  after?: any
  reason?: string
  createdAt: string
}
```

## 🚀 Implementation Priority

### Phase 1: Foundation (Week 1-2)
1. **Dictionaries APIs** (Categories, Seat Maps, Route Catalog)
2. **Providers APIs** (CRUD, Ban/Unban)
3. **Basic Auth & RBAC**

### Phase 2: Core Business (Week 3-4)
1. **Routes APIs** (CRUD + Approval Workflow)
2. **Buses APIs** (CRUD)
3. **Pricebooks APIs** (CRUD + Approval Workflow)

### Phase 3: Advanced Features (Week 5-6)
1. **Slots APIs** (CRUD + Bulk Generate)
2. **Moderation APIs** (Approval Queue)
3. **Audit APIs** (Logging & Query)

### Phase 4: Enhancement (Week 7-8)
1. **Overview Dashboard APIs**
2. **Import/Export APIs**
3. **Holidays & Surcharges APIs**
4. **Locks APIs**

## 🔧 Technical Considerations

### Database Design
- **Multi-tenant architecture** with owner-based partitioning
- **Soft deletes** for audit trail preservation
- **Indexes** on frequently queried fields (owner, status, dates)
- **Constraints** for data integrity (unique combinations, foreign keys)

### Performance Optimization
- **Pagination** for large datasets
- **Caching** for dictionary data
- **Async processing** for bulk operations
- **Database connection pooling**

### Error Handling
- **Consistent error responses** across all APIs
- **Validation errors** with field-level details
- **Business logic errors** with actionable messages
- **Audit logging** for all errors

### Monitoring & Observability
- **Request/Response logging**
- **Performance metrics** (response time, throughput)
- **Error rate monitoring**
- **Business metrics** (approval rates, conflict rates)

