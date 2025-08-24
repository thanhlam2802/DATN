# Bus Management API Tests

## Tổng quan
Bộ test này bao gồm các test cases cho BusManagement API, được chia thành 3 loại:

1. **Controller Tests** - Test các REST endpoints
2. **Service Tests** - Test business logic
3. **Integration Tests** - Test end-to-end với database

## Cấu trúc Test

### 1. Controller Tests (`BusManagementControllerTest.java`)
- **Mục đích**: Test các REST API endpoints
- **Framework**: Spring Boot Test + MockMvc
- **Mock**: Service layer được mock hoàn toàn
- **Coverage**: Tất cả 5 tabs (Overview, Providers, Routes, Slots, Moderation)

**Test cases chính:**
- ✅ GET `/api/admin/bus-management/overview/kpis`
- ✅ GET `/api/admin/bus-management/overview/alerts`
- ✅ GET `/api/admin/bus-management/providers`
- ✅ POST `/api/admin/bus-management/providers`
- ✅ PUT `/api/admin/bus-management/providers/{id}`
- ✅ POST `/api/admin/bus-management/providers/{id}/ban`
- ✅ GET `/api/admin/bus-management/routes`
- ✅ POST `/api/admin/bus-management/routes`
- ✅ POST `/api/admin/bus-management/routes/{id}/submit-review`
- ✅ POST `/api/admin/bus-management/routes/{id}/approve`
- ✅ POST `/api/admin/bus-management/routes/{id}/reject`
- ✅ GET `/api/admin/bus-management/slots`
- ✅ POST `/api/admin/bus-management/slots/bulk-generate`
- ✅ GET `/api/admin/bus-management/slots/{id}/detail`
- ✅ GET `/api/admin/bus-management/moderation`
- ✅ POST `/api/admin/bus-management/moderation/{id}/approve`
- ✅ POST `/api/admin/bus-management/moderation/{id}/reject`
- ✅ GET `/api/admin/bus-management/moderation/{id}/diff`

### 2. Service Tests (`BusManagementServiceTest.java`)
- **Mục đích**: Test business logic trong service layer
- **Framework**: JUnit 5 + Mockito
- **Mock**: Tất cả repositories được mock
- **Coverage**: Tất cả methods trong BusManagementServiceFixed

**Test cases chính:**
- ✅ `getOverviewKPIs()` - Tính toán KPI
- ✅ `getDataQualityAlerts()` - Phát hiện cảnh báo
- ✅ `getProviders()` - Lấy danh sách providers
- ✅ `createProvider()` - Tạo provider mới
- ✅ `banProvider()` - Ban provider
- ✅ `getRoutes()` - Lấy danh sách routes
- ✅ `createRoute()` - Tạo route mới
- ✅ `submitRouteReview()` - Submit review
- ✅ `approveRoute()` - Approve route
- ✅ `rejectRoute()` - Reject route
- ✅ `getSlots()` - Lấy danh sách slots
- ✅ `bulkGenerateSlots()` - Tạo slots hàng loạt
- ✅ `getSlotDetail()` - Chi tiết slot
- ✅ `getModerationQueue()` - Queue moderation
- ✅ `approveModeration()` - Approve moderation
- ✅ `rejectModeration()` - Reject moderation
- ✅ `getModerationDiff()` - Xem diff

### 3. Integration Tests (`BusManagementIntegrationTest.java`)
- **Mục đích**: Test end-to-end với database thật
- **Framework**: Spring Boot Test + H2 Database
- **Database**: In-memory H2 database
- **Coverage**: Test toàn bộ flow từ controller đến database

**Test cases chính:**
- ✅ Tạo test data trong database
- ✅ Test CRUD operations với database thật
- ✅ Test business logic với data persistence
- ✅ Test transaction rollback
- ✅ Test data integrity

## Chạy Tests

### 1. Chạy tất cả tests
```bash
# Maven
mvn test

# Gradle
./gradlew test
```

### 2. Chạy từng loại test
```bash
# Chỉ chạy Controller tests
mvn test -Dtest=BusManagementControllerTest

# Chỉ chạy Service tests
mvn test -Dtest=BusManagementServiceTest

# Chỉ chạy Integration tests
mvn test -Dtest=BusManagementIntegrationTest
```

### 3. Chạy test cụ thể
```bash
# Chạy test method cụ thể
mvn test -Dtest=BusManagementControllerTest#getOverviewKPIs_ShouldReturnKPIs

# Chạy test với pattern
mvn test -Dtest=*Test#*ShouldReturn*
```

### 4. Chạy với coverage
```bash
# Với JaCoCo coverage
mvn clean test jacoco:report

# Xem coverage report
open target/site/jacoco/index.html
```

## Test Data

### Test Entities
```java
// Test User
User testUser = new User();
testUser.setId(1);
testUser.setName("Test Provider");
testUser.setEmail("test@provider.com");

// Test Route
Route testRoute = new Route();
testRoute.setId(1);
testRoute.setOwner(testUser);
testRoute.setOriginLocation(originLocation);
testRoute.setDestinationLocation(destinationLocation);
testRoute.setStatus(RouteStatus.APPROVED);

// Test Bus
Bus testBus = new Bus();
testBus.setId(1);
testBus.setName("Test Bus");
testBus.setLicensePlate("51F-123.45");
testBus.setTotalSeats(40);
testBus.setOwner(testUser);

// Test BusSlot
BusSlot testSlot = new BusSlot();
testSlot.setId(1);
testSlot.setBus(testBus);
testSlot.setRoute(testRoute);
testSlot.setOwner(testUser);
testSlot.setSlotDate(LocalDate.now().plusDays(1));
testSlot.setDepartureTime(LocalTime.of(22, 0));
testSlot.setArrivalTime(LocalTime.of(13, 0));
testSlot.setPrice(new BigDecimal("250000"));
testSlot.setTotalSeats(40);
testSlot.setAvailableSeats(28);
testSlot.setStatus(BusSlotStatus.SCHEDULED);
```

## Test Configuration

### application-test.properties
```properties
# Test database configuration
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# JPA configuration
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true

# Test logging
logging.level.org.springframework.test=DEBUG
logging.level.org.hibernate.SQL=DEBUG
```

### Test Dependencies
```xml
<dependencies>
    <!-- JUnit 5 -->
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Spring Boot Test -->
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-test</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- Mockito -->
    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <scope>test</scope>
    </dependency>
    
    <!-- H2 Database -->
    <dependency>
        <groupId>com.h2database</groupId>
        <artifactId>h2</artifactId>
        <scope>test</scope>
    </dependency>
</dependencies>
```

## Best Practices

### 1. Test Naming Convention
```java
@Test
void methodName_Scenario_ExpectedBehavior() {
    // Test implementation
}

// Examples:
void getOverviewKPIs_ShouldReturnKPIs()
void createProvider_WithInvalidData_ShouldThrowException()
void banProvider_ShouldDeleteUserBuses()
```

### 2. Test Structure (AAA Pattern)
```java
@Test
void testMethod() {
    // Arrange (Given)
    // Setup test data and mocks
    
    // Act (When)
    // Execute the method under test
    
    // Assert (Then)
    // Verify the results
}
```

### 3. Mocking Best Practices
```java
// Mock repository methods
when(userRepository.findByBusesIsNotEmpty()).thenReturn(Arrays.asList(testUser));

// Verify method calls
verify(userRepository).save(any(User.class));

// Argument matchers
verify(routeRepository).save(argThat(route -> route.getStatus() == RouteStatus.APPROVED));
```

### 4. Test Data Management
```java
@BeforeEach
void setUp() {
    // Create test data that will be used across multiple tests
    testUser = createTestUser();
    testRoute = createTestRoute();
    testBus = createTestBus();
}

private User createTestUser() {
    User user = new User();
    user.setName("Test Provider");
    user.setEmail("test@provider.com");
    return user;
}
```

## Troubleshooting

### Common Issues

1. **Test fails with database connection error**
   - Check if H2 dependency is included
   - Verify test profile is active
   - Check application-test.properties

2. **Mock not working**
   - Ensure @MockBean is used for Spring Boot tests
   - Use @Mock for unit tests
   - Verify mock setup in @BeforeEach

3. **Transaction rollback not working**
   - Add @Transactional to test class
   - Use @Rollback annotation if needed
   - Check if test database is in-memory

4. **Test data not persisting**
   - Use @DirtiesContext if needed
   - Check if @Transactional is properly configured
   - Verify database configuration

### Debug Tips

```java
// Enable SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE

// Enable test logging
logging.level.org.springframework.test=DEBUG

// Print test data
System.out.println("Test data: " + objectMapper.writeValueAsString(testData));
```

## Coverage Goals

- **Controller Layer**: 100% endpoint coverage
- **Service Layer**: 100% method coverage
- **Business Logic**: 100% branch coverage
- **Integration**: 100% happy path coverage

## Performance

- **Unit Tests**: < 1 second per test
- **Integration Tests**: < 5 seconds per test
- **Total Test Suite**: < 30 seconds

## Continuous Integration

Tests sẽ được chạy tự động trong CI/CD pipeline:

```yaml
# GitHub Actions example
- name: Run Tests
  run: mvn test

- name: Generate Coverage Report
  run: mvn jacoco:report

- name: Upload Coverage
  uses: codecov/codecov-action@v3
  with:
    file: target/site/jacoco/jacoco.xml
```
