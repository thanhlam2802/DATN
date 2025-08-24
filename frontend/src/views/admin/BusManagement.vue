<template>
  <div class="min-h-screen bg-gray-50 p-6">
    <!-- Header -->
    <header class="mb-8">
      <div class="flex items-start justify-between gap-4">
        <div>
          <h1 class="text-3xl font-bold text-gray-900 mb-2">Super Admin · Bus (Lite)</h1>
          <p class="text-gray-600">Bản rút gọn: chỉ các chức năng cốt lõi để kiểm soát toàn hệ thống</p>
        </div>
        <div class="flex gap-3">
          <button 
            @click="refreshAll"
            class="px-4 py-2 bg-white border border-gray-300 rounded-lg text-gray-700 hover:bg-gray-50 transition-colors"
          >
            <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"></path>
            </svg>
            Refresh
          </button>
        </div>
      </div>
    </header>

    <!-- Tabs (Lite) -->
    <nav class="mb-8 border-b border-gray-200">
      <div class="flex flex-wrap gap-1">
        <button
          v-for="tab in tabs"
          :key="tab.id"
          @click="activeTab = tab.id"
          :class="[
            'px-4 py-2 text-sm font-medium rounded-t-lg transition-colors',
            activeTab === tab.id 
              ? 'bg-white text-blue-600 border-b-2 border-blue-600' 
              : 'text-gray-500 hover:text-gray-700 hover:bg-gray-100'
          ]"
        >
          {{ tab.label }}
        </button>
      </div>
    </nav>

    <!-- OVERVIEW -->
    <section v-if="activeTab === 'overview'" class="space-y-6">
      <!-- KPI Cards -->
      <div class="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        <div class="bg-white rounded-xl shadow-sm border border-gray-200 p-6" v-for="card in kpiCards" :key="card.key">
          <div class="flex items-center">
            <div :class="['p-2 rounded-lg', card.bg]">
              <component :is="card.icon" class="w-6 h-6" />
            </div>
            <div class="ml-4">
              <p class="text-sm font-medium text-gray-600">{{ card.label }}</p>
              <p class="text-2xl font-bold text-gray-900">{{ card.value }}</p>
            </div>
          </div>
        </div>
      </div>

      <!-- Alerts Table (data quality) -->
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Cảnh báo chất lượng dữ liệu</h3>
        </div>
        <div class="p-6">
          <div class="flex items-center gap-3 mb-4">
            <input 
              v-model="overviewFilters.query" 
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm theo từ khoá..." 
            />
            <button 
              @click="loadOverview"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              Lọc
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Loại</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Provider</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mô tả</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Khuyến nghị</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Thời điểm</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="(row, idx) in overviewAlerts" :key="idx" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.type }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ row.ownerName }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ row.description }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ row.suggestion }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-500">{{ row.at }}</td>
                </tr>
                <tr v-if="overviewAlerts.length === 0">
                  <td colspan="5" class="px-6 py-4 text-center text-sm text-gray-500">Không có dữ liệu</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- PROVIDERS -->
    <section v-if="activeTab === 'providers'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Providers</h3>
        </div>
        <div class="p-6">
          <div class="flex gap-3 mb-4">
            <input 
              v-model="providerFilters.query" 
              class="flex-1 px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
              placeholder="Tìm theo tên/mã..." 
            />
            <select 
              v-model="providerFilters.status" 
              class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500"
            >
              <option value="">-- Status --</option>
              <option value="ACTIVE">ACTIVE</option>
              <option value="BANNED">BANNED</option>
            </select>
            <button 
              @click="loadProviders"
              class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors"
            >
              Lọc
            </button>
            <button 
              @click="openProviderModal()"
              class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors"
            >
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm
            </button>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Mã</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Tên</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Routes</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Slots (7d)</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="p in providers" :key="p.code" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ p.code }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.name }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      p.status === 'ACTIVE' ? 'bg-green-100 text-green-800' : 'bg-red-100 text-red-800'
                    ]">
                      {{ p.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.routesCount }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ p.next7dSlots }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button @click="openProviderModal(p)" class="text-blue-600 hover:text-blue-900">Sửa</button>
                      <button v-if="p.status!=='BANNED'" @click="banProvider(p)" class="text-yellow-600 hover:text-yellow-900">Ban</button>
                      <button v-else @click="unbanProvider(p)" class="text-green-600 hover:text-green-900">Unban</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="providers.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- ROUTES -->
    <section v-if="activeTab === 'routes'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Routes</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input v-model="routeFilters.owner" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="Owner code..." />
            <input v-model="routeFilters.origin" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="Origin..." />
            <input v-model="routeFilters.destination" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="Destination..." />
            <select v-model="routeFilters.status" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
              <option value="">-- Status --</option>
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
            <button @click="loadRoutes" class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors">Lọc</button>
            <button @click="openRouteModal()" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Thêm tuyến
            </button>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Route Code</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Origin → Destination</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Future Slots</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="r in routes" :key="r.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ r.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.routeCode }}</td>
                  <td class="px-6 py-4 text-sm text-gray-900">{{ r.origin }} → {{ r.destination }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      r.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      r.status === 'PENDING_REVIEW' ? 'bg-yellow-100 text-yellow-800' :
                      r.status === 'REJECTED' ? 'bg-red-100 text-red-800' :
                      r.status === 'ARCHIVED' ? 'bg-gray-100 text-gray-800' :
                      'bg-blue-100 text-blue-800'
                    ]">
                      {{ r.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ r.futureSlots }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button @click="openRouteModal(r)" class="text-blue-600 hover:text-blue-900">Sửa</button>
                      <button v-if="r.status==='DRAFT'" @click="submitRouteReview(r)" class="text-yellow-600 hover:text-yellow-900">Submit review</button>
                      <button v-if="r.status==='PENDING_REVIEW'" @click="approveRoute(r)" class="text-green-600 hover:text-green-900">Approve</button>
                      <button v-if="r.status==='PENDING_REVIEW'" @click="rejectRoute(r)" class="text-red-600 hover:text-red-900">Reject</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="routes.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- SLOTS -->
    <section v-if="activeTab === 'slots'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Slots</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <input v-model="slotFilters.owner" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="Owner..." />
            <input v-model="slotFilters.route" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" placeholder="Route code..." />
            <input v-model="slotFilters.date" type="date" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500" />
            <button @click="loadSlots" class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors">Lọc</button>
            <button @click="openBulkGenerateModal" class="px-4 py-2 bg-blue-600 text-white rounded-lg hover:bg-blue-700 transition-colors">
              <svg class="w-4 h-4 inline mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M12 6v6m0 0v6m0-6h6m-6 0H6"></path>
              </svg>
              Bulk generate
            </button>
          </div>
          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Route</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Depart</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Bus</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Seats (sold/total)</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="s in slots" :key="s.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ s.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.routeCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.departTime }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.busPlate }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ s.sold }}/{{ s.total }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      s.status === 'ACTIVE' ? 'bg-green-100 text-green-800' :
                      s.status === 'CANCELLED' ? 'bg-red-100 text-red-800' :
                      'bg-gray-100 text-gray-800'
                    ]">
                      {{ s.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button @click="openSlotDetail(s)" class="text-blue-600 hover:text-blue-900">Chi tiết</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="slots.length===0">
                  <td colspan="7" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- MODERATION -->
    <section v-if="activeTab === 'moderation'" class="space-y-6">
      <div class="bg-white rounded-xl shadow-sm border border-gray-200">
        <div class="px-6 py-4 border-b border-gray-200">
          <h3 class="text-lg font-semibold text-gray-900">Approval Queue</h3>
        </div>
        <div class="p-6">
          <div class="flex flex-wrap gap-3 mb-4">
            <select v-model="moderationFilters.type" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
              <option value="">-- Type --</option>
              <option value="ROUTE">ROUTE</option>
            </select>
            <select v-model="moderationFilters.status" class="px-3 py-2 border border-gray-300 rounded-lg focus:ring-2 focus:ring-blue-500 focus:border-blue-500">
              <option value="">-- Status --</option>
              <option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option>
            </select>
            <button @click="loadModeration" class="px-4 py-2 bg-gray-100 text-gray-700 rounded-lg hover:bg-gray-200 transition-colors">Lọc</button>
          </div>

          <div class="overflow-x-auto">
            <table class="min-w-full divide-y divide-gray-200">
              <thead class="bg-gray-50">
                <tr>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Type</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Owner</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Submitted By</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">At</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Status</th>
                  <th class="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase tracking-wider">Actions</th>
                </tr>
              </thead>
              <tbody class="bg-white divide-y divide-gray-200">
                <tr v-for="m in moderationQueue" :key="m.id" class="hover:bg-gray-50">
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium text-gray-900">{{ m.type }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.ownerCode }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.submittedBy }}</td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm text-gray-900">{{ m.submittedAt }}</td>
                  <td class="px-6 py-4 whitespace-nowrap">
                    <span :class="[
                      'inline-flex px-2 py-1 text-xs font-semibold rounded-full',
                      m.status === 'APPROVED' ? 'bg-green-100 text-green-800' :
                      m.status === 'PENDING_REVIEW' ? 'bg-yellow-100 text-yellow-800' :
                      'bg-red-100 text-red-800'
                    ]">
                      {{ m.status }}
                    </span>
                  </td>
                  <td class="px-6 py-4 whitespace-nowrap text-sm font-medium">
                    <div class="flex gap-2">
                      <button @click="viewDiff(m)" class="text-blue-600 hover:text-blue-900">Diff</button>
                      <button v-if="m.status==='PENDING_REVIEW'" @click="approveModeration(m)" class="text-green-600 hover:text-green-900">Approve</button>
                      <button v-if="m.status==='PENDING_REVIEW'" @click="rejectModeration(m)" class="text-red-600 hover:text-red-900">Reject</button>
                    </div>
                  </td>
                </tr>
                <tr v-if="moderationQueue.length===0">
                  <td colspan="6" class="px-6 py-4 text-center text-sm text-gray-500">Trống</td>
                </tr>
              </tbody>
            </table>
          </div>
        </div>
      </div>
    </section>

    <!-- MODALS PLACEHOLDER (Only the ones we still use) -->
    <div v-if="modal.visible" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="text-lg font-semibold">{{ modal.title }}</h3>
          <button class="btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div v-if="modal.type === 'provider'">
            <label class="lbl">Code</label><input v-model="modal.form.code" class="input" />
            <label class="lbl">Name</label><input v-model="modal.form.name" class="input" />
            <label class="lbl">Status</label>
            <select v-model="modal.form.status" class="select">
              <option>ACTIVE</option>
              <option>BANNED</option>
            </select>
          </div>

          <div v-else-if="modal.type === 'route'">
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Route code</label><input v-model="modal.form.routeCode" class="input" />
            <label class="lbl">Origin</label><input v-model="modal.form.origin" class="input" />
            <label class="lbl">Destination</label><input v-model="modal.form.destination" class="input" />
            <label class="lbl">Status</label>
            <select v-model="modal.form.status" class="select">
              <option>DRAFT</option><option>PENDING_REVIEW</option><option>APPROVED</option><option>REJECTED</option><option>ARCHIVED</option>
            </select>
          </div>

          <div v-else-if="modal.type === 'bulkGenerate'">
            <p class="hint">Thiết lập rule tạo slots hàng loạt:</p>
            <label class="lbl">Owner</label><input v-model="modal.form.ownerCode" class="input" />
            <label class="lbl">Route code</label><input v-model="modal.form.routeCode" class="input" />
            <label class="lbl">Start date</label><input v-model="modal.form.startDate" type="date" class="input" />
            <label class="lbl">End date</label><input v-model="modal.form.endDate" type="date" class="input" />
            <label class="lbl">Days of week</label><input v-model="modal.form.daysOfWeek" class="input" placeholder="[1,2,3,4,5,6,7]" />
            <label class="lbl">Depart time</label><input v-model="modal.form.departTime" type="time" class="input" />
            <label class="lbl">Duration (minutes)</label><input v-model.number="modal.form.durationMinutes" type="number" class="input" />
          </div>

          <div v-else>
            <p>Modal nội dung...</p>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn" @click="closeModal">Đóng</button>
          <button class="btn btn-primary" @click="saveModal">Lưu</button>
        </div>
      </div>
    </div>

    <!-- JSON Viewer -->
    <div v-if="jsonViewer.visible" class="modal">
      <div class="modal-content">
        <div class="modal-header">
          <h3 class="text-lg font-semibold">JSON</h3>
          <button class="btn" @click="jsonViewer.visible=false">✕</button>
        </div>
        <pre class="json-pre">{{ jsonViewer.text }}</pre>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts">
import { ref, reactive, onMounted, computed, h } from 'vue'
import { busManagementApi, type Provider, type Route, type Slot, type KPIs, type Alert, type ModerationItem } from '@/services/busManagementApi'

/** TABS (Lite) */
const tabs = [
  { id: 'overview', label: 'Overview' },
  { id: 'providers', label: 'Providers' },
  { id: 'routes', label: 'Routes' },
  { id: 'slots', label: 'Slots' },
  { id: 'moderation', label: 'Moderation' },
] as const
const activeTab = ref<typeof tabs[number]['id']>('overview')

/** ICON components (tiny inline) */
const IconDoc = (props:any)=> h('svg', {class:'text-blue-600', fill:'none', stroke:'currentColor', viewBox:'0 0 24 24'}, [
  h('path',{ 'stroke-linecap':'round','stroke-linejoin':'round','stroke-width':'2', d:'M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z'})
])
const IconBar = (props:any)=> h('svg', {class:'text-green-600', fill:'none', stroke:'currentColor', viewBox:'0 0 24 24'}, [
  h('path',{ 'stroke-linecap':'round','stroke-linejoin':'round','stroke-width':'2', d:'M9 19v-6a2 2 0 00-2-2H5a2 2 0 00-2 2v6a2 2 0 002 2h2a2 2 0 002-2zm0 0V9a2 2 0 012-2h2a2 2 0 012 2v10m-6 0a2 2 0 002 2h2a2 2 0 002-2m0 0V5a2 2 0 012-2h2a2 2 0 012 2v14a2 2 0 01-2 2h-2a2 2 0 01-2-2z'})
])
const IconX = (props:any)=> h('svg', {class:'text-red-600', fill:'none', stroke:'currentColor', viewBox:'0 0 24 24'}, [
  h('path',{ 'stroke-linecap':'round','stroke-linejoin':'round','stroke-width':'2', d:'M6 18L18 6M6 6l12 12'})
])
const IconUsers = (props:any)=> h('svg', {class:'text-purple-600', fill:'none', stroke:'currentColor', viewBox:'0 0 24 24'}, [
  h('path',{ 'stroke-linecap':'round','stroke-linejoin':'round','stroke-width':'2', d:'M17 20h5v-2a3 3 0 00-5.356-1.857M17 20H7m10 0v-2c0-.656-.126-1.283-.356-1.857M7 20H2v-2a3 3 0 015.356-1.857M7 20v-2c0-.656.126-1.283.356-1.857m0 0a5.002 5.002 0 019.288 0M15 7a3 3 0 11-6 0 3 3 0 016 0zm6 3a2 2 0 11-4 0 2 2 0 014 0zM7 10a2 2 0 11-4 0 2 2 0 014 0z'})
])

/** BUSINESS TYPES (lite) */
type Status = 'DRAFT' | 'PENDING_REVIEW' | 'APPROVED' | 'REJECTED' | 'ARCHIVED' | 'ACTIVE' | 'BANNED'

/** STATE */
const kpis = reactive<{ bookingsToday?: number; occupancyRate?: number; cancellations?: number; activeProviders?: number }>({})
const overviewFilters = reactive({ query: '' })
const overviewAlerts = ref<{ type: string; ownerName: string; description: string; suggestion: string; at: string }[]>([])

const providers = ref<Provider[]>([])
const providerFilters = reactive({ query: '', status: '' as ''|'ACTIVE'|'BANNED' })

const routes = ref<Route[]>([])
const routeFilters = reactive({ owner: '', origin: '', destination: '', status: '' as Status|'' })

const slots = ref<Slot[]>([])
const slotFilters = reactive({ owner: '', route: '', date: '' })

const moderationQueue = ref<any[]>([])
const moderationFilters = reactive({ type: '' as ''|'ROUTE', status: '' as ''|Status })

// Modal & JSON viewer
const modal = reactive<{ visible: boolean; title?: string; type?: string; form: any }>({ visible: false, form: {} })
const jsonViewer = reactive<{ visible: boolean; text: string }>({ visible: false, text: '' })

/** Derived KPI cards */
const kpiCards = computed(() => ([
  { key:'book', label:'Bookings hôm nay', value: kpis.bookingsToday ?? '-', bg:'bg-blue-100', icon: IconDoc },
  { key:'occ',  label:'Tỉ lệ lấp đầy',   value: (kpis.occupancyRate ?? '-') + (kpis.occupancyRate?'%':''), bg:'bg-green-100', icon: IconBar },
  { key:'can',  label:'Huỷ/Hoãn',        value: kpis.cancellations ?? '-', bg:'bg-red-100', icon: IconX },
  { key:'act',  label:'Providers hoạt động', value: kpis.activeProviders ?? '-', bg:'bg-purple-100', icon: IconUsers },
]))

/** LOADERS (API thật) */
async function refreshAll(){
  await Promise.all([loadOverview(), loadProviders(), loadRoutes(), loadSlots(), loadModeration()])
}

async function loadOverview(){
  try {
    const [kpisResponse, alertsResponse] = await Promise.all([
      busManagementApi.getOverviewKPIs(),
      busManagementApi.getDataQualityAlerts(overviewFilters.query)
    ]);
    
    Object.assign(kpis, kpisResponse.data);
    overviewAlerts.value = alertsResponse.data;
  } catch (error) {
    console.error('Error loading overview:', error);
    // Fallback to mock data if API fails
    kpis.bookingsToday = 120; kpis.occupancyRate = 73; kpis.cancellations = 5; kpis.activeProviders = 12;
    overviewAlerts.value = [
      { type: 'PRICE_OVERLAP', ownerName: 'NXANH', description: 'Giá trùng hiệu lực trên SGN-DAD', suggestion: 'Điều chỉnh khoảng ngày / hợp nhất pricebook', at: new Date().toISOString() },
    ];
  }
}

async function loadProviders(){ 
  try {
    const response = await busManagementApi.getProviders(providerFilters.query, providerFilters.status);
    providers.value = response.data;
  } catch (error) {
    console.error('Error loading providers:', error);
    // Fallback to mock data
    providers.value = [{ id:1, code:'NXANH', name:'Nhà Xe Xanh', status:'ACTIVE', routesCount:12, next7dSlots:84 }];
  }
}

async function loadRoutes(){ 
  try {
    const response = await busManagementApi.getRoutes(
      routeFilters.owner, 
      routeFilters.origin, 
      routeFilters.destination, 
      routeFilters.status
    );
    routes.value = response.data;
  } catch (error) {
    console.error('Error loading routes:', error);
    // Fallback to mock data
    routes.value = [{ id:1, ownerCode:'NXANH', routeCode:'NXANH-SGN-DAD-01', origin:'Saigon', destination:'Da Nang', status:'DRAFT', futureSlots:10 }];
  }
}

async function loadSlots(){ 
  try {
    const response = await busManagementApi.getSlots(
      slotFilters.owner, 
      slotFilters.route, 
      slotFilters.date
    );
    slots.value = response.data;
  } catch (error) {
    console.error('Error loading slots:', error);
    // Fallback to mock data
    slots.value = [{ id:1, ownerCode:'NXANH', routeCode:'NXANH-SGN-DAD-01', departTime:'2025-01-10 22:00', busPlate:'51F-123.45', sold:12, total:40, status:'OPEN' }];
  }
}

async function loadModeration(){ 
  try {
    const response = await busManagementApi.getModerationQueue(
      moderationFilters.type, 
      moderationFilters.status
    );
    moderationQueue.value = response.data;
  } catch (error) {
    console.error('Error loading moderation:', error);
    // Fallback to mock data
    moderationQueue.value = [{ id:1, type:'ROUTE', ownerCode:'NXANH', submittedBy:'admin_b', submittedAt:new Date().toISOString(), status:'PENDING_REVIEW' }];
  }
}

/** ACTIONS */
function openProviderModal(row?: Provider){ 
  modal.visible=true; 
  modal.title=row?'Sửa provider':'Thêm provider'; 
  modal.type='provider'; 
  modal.form=row?{...row}:{ code:'', name:'', status:'ACTIVE' } 
}

async function banProvider(row: Provider){ 
  try {
    await busManagementApi.banProvider(row.id);
    await loadProviders();
  } catch (error) {
    console.error('Error banning provider:', error);
  }
}

async function unbanProvider(row: Provider){ 
  try {
    await busManagementApi.unbanProvider(row.id);
    await loadProviders();
  } catch (error) {
    console.error('Error unbanning provider:', error);
  }
}

function openRouteModal(row?: Route){ 
  modal.visible=true; 
  modal.title=row?'Sửa route':'Thêm route'; 
  modal.type='route'; 
  modal.form=row?{...row}:{ ownerCode:'', routeCode:'', origin:'', destination:'', status:'DRAFT' } 
}

async function submitRouteReview(row: Route){ 
  try {
    await busManagementApi.submitRouteReview(row.id);
    await loadRoutes();
  } catch (error) {
    console.error('Error submitting route review:', error);
  }
}

async function approveRoute(row: Route){ 
  try {
    await busManagementApi.approveRoute(row.id);
    await loadRoutes();
  } catch (error) {
    console.error('Error approving route:', error);
  }
}

async function rejectRoute(row: Route){ 
  try {
    await busManagementApi.rejectRoute(row.id);
    await loadRoutes();
  } catch (error) {
    console.error('Error rejecting route:', error);
  }
}

function openBulkGenerateModal(){ 
  modal.visible=true; 
  modal.title='Bulk Generate Slots'; 
  modal.type='bulkGenerate'; 
  modal.form={ ownerCode:'', routeCode:'', startDate:'', endDate:'', daysOfWeek:'[1,2,3,4,5,6,7]', departTime:'22:00', durationMinutes: 900 } 
}

async function openSlotDetail(row: Slot){ 
  try {
    const response = await busManagementApi.getSlotDetail(row.id);
    jsonViewer.visible = true;
    jsonViewer.text = JSON.stringify(response.data, null, 2);
  } catch (error) {
    console.error('Error loading slot detail:', error);
  }
}

async function viewDiff(row: any){ 
  try {
    const response = await busManagementApi.getModerationDiff(row.id);
    jsonViewer.visible = true;
    jsonViewer.text = JSON.stringify(response.data, null, 2);
  } catch (error) {
    console.error('Error loading diff:', error);
    jsonViewer.visible = true;
    jsonViewer.text = JSON.stringify(row, null, 2);
  }
}

async function approveModeration(row: any){ 
  try {
    await busManagementApi.approveModeration(row.id);
    await loadModeration();
  } catch (error) {
    console.error('Error approving moderation:', error);
  }
}

async function rejectModeration(row: any){ 
  try {
    await busManagementApi.rejectModeration(row.id);
    await loadModeration();
  } catch (error) {
    console.error('Error rejecting moderation:', error);
  }
}

/** Modal helpers */
function closeModal(){ modal.visible=false; modal.type=undefined; modal.title=undefined; modal.form={} }

async function saveModal(){ 
  try {
    if (modal.type === 'provider') {
      if (modal.form.id) {
        await busManagementApi.updateProvider(modal.form.id, modal.form);
      } else {
        await busManagementApi.createProvider(modal.form);
      }
      await loadProviders();
    } else if (modal.type === 'route') {
      if (modal.form.id) {
        await busManagementApi.updateRoute(modal.form.id, modal.form);
      } else {
        await busManagementApi.createRoute(modal.form);
      }
      await loadRoutes();
    } else if (modal.type === 'bulkGenerate') {
      const slotData = {
        ...modal.form,
        daysOfWeek: JSON.parse(modal.form.daysOfWeek || '[1,2,3,4,5,6,7]')
      };
      await busManagementApi.bulkGenerateSlots(slotData);
      await loadSlots();
    }
    closeModal();
  } catch (error) {
    console.error('Error saving modal:', error);
  }
}

onMounted(() => { refreshAll() })
</script>

<style scoped>
/* Minimal styles for modals and JSON viewer */
.modal { position: fixed; inset: 0; background: rgba(0,0,0,0.4); display: grid; place-items: center; z-index: 999; }
.modal-content { background: #fff; width: min(760px, 94vw); border-radius: 12px; padding: 12px; border: 1px solid #eee; }
.modal-header { display: flex; align-items: center; justify-content: space-between; border-bottom: 1px solid #f0f0f0; padding-bottom: 6px; margin-bottom: 8px; }
.modal-body { display: grid; gap: 8px; }
.modal-footer { display: flex; justify-content: flex-end; gap: 8px; margin-top: 8px; }
.lbl { font-size: 12px; color: #444; }
.hint { font-size: 12px; color: #6b7280; }
.json-pre { max-height: 60vh; overflow: auto; background: #0b1020; color: #e5e7eb; padding: 10px; border-radius: 8px; }
</style>
