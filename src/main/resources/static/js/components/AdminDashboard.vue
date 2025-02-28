<template>
  <div class="admin-dashboard">
    <h1>Admin Dashboard</h1>
    
    <div v-if="!isAdmin" class="not-authorized">
      <p>You are not authorized to view this page.</p>
      <router-link to="/" class="btn-primary">Return to Home</router-link>
    </div>
    
    <div v-else-if="loading" class="loading">Loading dashboard data...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    
    <div v-else class="dashboard-content">
      <div class="dashboard-header">
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalUsers }}</div>
            <div class="stat-label">Total Users</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalTours }}</div>
            <div class="stat-label">Active Tours</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalBookings }}</div>
            <div class="stat-label">Total Bookings</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">${{ stats.revenue.toLocaleString() }}</div>
            <div class="stat-label">Total Revenue</div>
          </div>
        </div>
      </div>
      
      <div class="dashboard-tabs">
        <div class="tab-buttons">
          <button 
            v-for="tab in tabs" 
            :key="tab.id" 
            @click="activeTab = tab.id"
            :class="{ active: activeTab === tab.id }"
            class="tab-button"
          >
            {{ tab.name }}
          </button>
        </div>
        
        <div class="tab-content">
          <!-- Users Tab -->
          <div v-if="activeTab === 'users'" class="tab-panel">
            <div class="panel-header">
              <h2>User Management</h2>
              <div class="search-bar">
                <input type="text" v-model="userSearch" placeholder="Search users...">
              </div>
            </div>
            
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Email</th>
                  <th>Role</th>
                  <th>Joined</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="user in filteredUsers" :key="user.id">
                  <td>{{ user.id }}</td>
                  <td>{{ user.name }}</td>
                  <td>{{ user.email }}</td>
                  <td>{{ user.role }}</td>
                  <td>{{ formatDate(user.joinDate) }}</td>
                  <td>
                    <span :class="'status-badge ' + user.status.toLowerCase()">
                      {{ user.status }}
                    </span>
                  </td>
                  <td class="actions">
                    <button @click="editUser(user.id)" class="btn-small">Edit</button>
                    <button @click="toggleUserStatus(user.id)" class="btn-small" :class="{ 'btn-danger': user.status === 'Active' }">
                      {{ user.status === 'Active' ? 'Deactivate' : 'Activate' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- Tours Tab -->
          <div v-if="activeTab === 'tours'" class="tab-panel">
            <div class="panel-header">
              <h2>Tour Management</h2>
              <button @click="createTour" class="btn-primary">Add New Tour</button>
            </div>
            
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Name</th>
                  <th>Location</th>
                  <th>Duration</th>
                  <th>Price</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="tour in tours" :key="tour.id">
                  <td>{{ tour.id }}</td>
                  <td>{{ tour.name }}</td>
                  <td>{{ tour.location }}</td>
                  <td>{{ tour.duration }} days</td>
                  <td>${{ tour.price }}</td>
                  <td>
                    <span :class="'status-badge ' + tour.status.toLowerCase()">
                      {{ tour.status }}
                    </span>
                  </td>
                  <td class="actions">
                    <button @click="editTour(tour.id)" class="btn-small">Edit</button>
                    <button @click="toggleTourStatus(tour.id)" class="btn-small" :class="{ 'btn-danger': tour.status === 'Active' }">
                      {{ tour.status === 'Active' ? 'Deactivate' : 'Activate' }}
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- Bookings Tab -->
          <div v-if="activeTab === 'bookings'" class="tab-panel">
            <div class="panel-header">
              <h2>Booking Management</h2>
              <div class="filter-group">
                <select v-model="bookingFilter">
                  <option value="all">All Bookings</option>
                  <option value="pending">Pending</option>
                  <option value="confirmed">Confirmed</option>
                  <option value="completed">Completed</option>
                  <option value="cancelled">Cancelled</option>
                </select>
              </div>
            </div>
            
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>User</th>
                  <th>Tour</th>
                  <th>Date</th>
                  <th>Travelers</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="booking in filteredBookings" :key="booking.id">
                  <td>{{ booking.id }}</td>
                  <td>{{ booking.userName }}</td>
                  <td>{{ booking.tourName }}</td>
                  <td>{{ formatDate(booking.date) }}</td>
                  <td>{{ booking.travelers }}</td>
                  <td>${{ booking.total }}</td>
                  <td>
                    <span :class="'status-badge ' + booking.status.toLowerCase()">
                      {{ booking.status }}
                    </span>
                  </td>
                  <td class="actions">
                    <button @click="viewBookingDetails(booking.id)" class="btn-small">View</button>
                    <button 
                      v-if="booking.status === 'Pending'" 
                      @click="confirmBooking(booking.id)" 
                      class="btn-small btn-success"
                    >
                      Confirm
                    </button>
                    <button 
                      v-if="booking.status !== 'Cancelled' && booking.status !== 'Completed'" 
                      @click="cancelBooking(booking.id)" 
                      class="btn-small btn-danger"
                    >
                      Cancel
                    </button>
                  </td>
                </tr>
              </tbody>
            </table>
          </div>
          
          <!-- Reports Tab -->
          <div v-if="activeTab === 'reports'" class="tab-panel">
            <div class="panel-header">
              <h2>Reports</h2>
              <div class="date-range">
                <input type="date" v-model="reportDateRange.start">
                <span>to</span>
                <input type="date" v-model="reportDateRange.end">
                <button @click="generateReport" class="btn-primary">Generate</button>
              </div>
            </div>
            
            <div class="report-cards">
              <div class="report-card">
                <h3>Revenue by Tour</h3>
                <div class="chart-placeholder">
                  [Bar Chart Visualization]
                </div>
              </div>
              
              <div class="report-card">
                <h3>Bookings by Month</h3>
                <div class="chart-placeholder">
                  [Line Chart Visualization]
                </div>
              </div>
              
              <div class="report-card">
                <h3>Top Destinations</h3>
                <div class="chart-placeholder">
                  [Pie Chart Visualization]
                </div>
              </div>
              
              <div class="report-card">
                <h3>User Growth</h3>
                <div class="chart-placeholder">
                  [Area Chart Visualization]
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'AdminDashboard',
  data() {
    return {
      loading: true,
      error: null,
      activeTab: 'users',
      userSearch: '',
      bookingFilter: 'all',
      reportDateRange: {
        start: new Date(new Date().getFullYear(), new Date().getMonth() - 1, 1).toISOString().split('T')[0],
        end: new Date().toISOString().split('T')[0]
      },
      tabs: [
        { id: 'users', name: 'Users' },
        { id: 'tours', name: 'Tours' },
        { id: 'bookings', name: 'Bookings' },
        { id: 'reports', name: 'Reports' }
      ],
      stats: {
        totalUsers: 0,
        totalTours: 0,
        totalBookings: 0,
        revenue: 0
      },
      users: [],
      tours: [],
      bookings: []
    };
  },
  computed: {
    isAdmin() {
      return this.$store.state.user && this.$store.state.user.role === 'ADMIN';
    },
    filteredUsers() {
      if (!this.userSearch) return this.users;
      
      const search = this.userSearch.toLowerCase();
      return this.users.filter(user => 
        user.name.toLowerCase().includes(search) || 
        user.email.toLowerCase().includes(search)
      );
    },
    filteredBookings() {
      if (this.bookingFilter === 'all') return this.bookings;
      
      return this.bookings.filter(booking => 
        booking.status.toLowerCase() === this.bookingFilter
      );
    }
  },
  async created() {
    if (!this.isAdmin) {
      this.loading = false;
      return;
    }
    
    try {
      // In a real app, these would be API calls
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // Mock data
      this.stats = {
        totalUsers: 245,
        totalTours: 32,
        totalBookings: 1289,
        revenue: 328750
      };
      
      this.users = [
        { id: 1, name: 'John Doe', email: 'john@example.com', role: 'USER', joinDate: new Date(2022, 5, 15), status: 'Active' },
        { id: 2, name: 'Jane Smith', email: 'jane@example.com', role: 'USER', joinDate: new Date(2022, 6, 22), status: 'Active' },
        { id: 3, name: 'Bob Johnson', email: 'bob@example.com', role: 'TOUR_OPERATOR', joinDate: new Date(2022, 4, 10), status: 'Active' },
        { id: 4, name: 'Alice Brown', email: 'alice@example.com', role: 'USER', joinDate: new Date(2022, 7, 5), status: 'Inactive' },
        { id: 5, name: 'Charlie Wilson', email: 'charlie@example.com', role: 'ADMIN', joinDate: new Date(2022, 3, 1), status: 'Active' }
      ];
      
      this.tours = [
        { id: 1, name: 'Eco Adventure Tour', location: 'Costa Rica', duration: 7, price: 1299, status: 'Active' },
        { id: 2, name: 'Sustainable Safari', location: 'Kenya', duration: 10, price: 2499, status: 'Active' },
        { id: 3, name: 'Rainforest Conservation Experience', location: 'Brazil', duration: 14, price: 1899, status: 'Active' },
        { id: 4, name: 'Alpine Eco Retreat', location: 'Switzerland', duration: 5, price: 1599, status: 'Inactive' },
        { id: 5, name: 'Island Conservation Volunteer', location: 'Galapagos', duration: 21, price: 2899, status: 'Pending' }
      ];
      
      this.bookings = [
        { id: 12345, userName: 'John Doe', tourName: 'Eco Adventure Tour', date: new Date(2023, 6, 10), travelers: 2, total: 2598, status: 'Confirmed' },
        { id: 12346, userName: 'Jane Smith', tourName: 'Sustainable Safari', date: new Date(2023, 7, 15), travelers: 1, total: 2499, status: 'Pending' },
        { id: 12347, userName: 'Bob Johnson', tourName: 'Rainforest Conservation Experience', date: new Date(2023, 8, 5), travelers: 3, total: 5697, status: 'Confirmed' },
        { id: 12348, userName: 'Alice Brown', tourName: 'Alpine Eco Retreat', date: new Date(2023, 5, 20), travelers: 2, total: 3198, status: 'Completed' },
        { id: 12349, userName: 'Charlie Wilson', tourName: 'Island Conservation Volunteer', date: new Date(2023, 9, 12), travelers: 1, total: 2899, status: 'Cancelled' }
      ];
    } catch (error) {
      this.error = 'Failed to load dashboard data. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'short',
        day: 'numeric'
      });
    },
    editUser(userId) {
      // In a real app, this would open a user edit form
      alert(`Editing user with ID: ${userId}`);
    },
    toggleUserStatus(userId) {
      const user = this.users.find(u => u.id === userId);
      if (user) {
        user.status = user.status === 'Active' ? 'Inactive' : 'Active';
      }
    },
    createTour() {
      // In a real app, this would open a tour creation form
      alert('Creating a new tour');
    },
    editTour(tourId) {
      // In a real app, this would open a tour edit form
      alert(`Editing tour with ID: ${tourId}`);
    },
    toggleTourStatus(tourId) {
      const tour = this.tours.find(t => t.id === tourId);
      if (tour) {
        tour.status = tour.status === 'Active' ? 'Inactive' : 'Active';
      }
    },
    viewBookingDetails(bookingId) {
      // In a real app, this would open booking details
      alert(`Viewing booking details for ID: ${bookingId}`);
    },
    confirmBooking(bookingId) {
      const booking = this.bookings.find(b => b.id === bookingId);
      if (booking) {
        booking.status = 'Confirmed';
      }
    },
    cancelBooking(bookingId) {
      const booking = this.bookings.find(b => b.id === bookingId);
      if (booking) {
        booking.status = 'Cancelled';
      }
    },
    generateReport() {
      // In a real app, this would generate reports based on date range
      alert(`Generating report from ${this.reportDateRange.start} to ${this.reportDateRange.end}`);
    }
  }
};
</script>

<style scoped>
.admin-dashboard {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dashboard-header {
  margin-bottom: 30px;
}

.stats-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(250px, 1fr));
  gap: 20px;
}

.stat-card {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-value {
  font-size: 32px;
  font-weight: bold;
  color: #4CAF50;
  margin-bottom: 5px;
}

.stat-label {
  color: #666;
  font-size: 16px;
}

.dashboard-tabs {
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  overflow: hidden;
}

.tab-buttons {
  display: flex;
  background-color: #f0f0f0;
}

.tab-button {
  padding: 15px 20px;
  background: none;
  border: none;
  cursor: pointer;
  font-size: 16px;
  font-weight: bold;
  color: #666;
  flex: 1;
  text-align: center;
  transition: background-color 0.3s;
}

.tab-button:hover {
  background-color: #e0e0e0;
}

.tab-button.active {
  background-color: #4CAF50;
  color: white;
}

.tab-content {
  padding: 20px;
}

.panel-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.search-bar input, .filter-group select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  min-width: 250px;
}

.date-range {
  display: flex;
  align-items: center;
  gap: 10px;
}

.date-range input {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.data-table {
  width: 100%;
  border-collapse: collapse;
}

.data-table th, .data-table td {
  padding: 12px 15px;
  text-align: left;
  border-bottom: 1px solid #ddd;
}

.data-table th {
  background-color: #f9f9f9;
  font-weight: bold;
}

.data-table tr:hover {
  background-color: #f5f5f5;
}

.status-badge {
  display: inline-block;
  padding: 5px 10px;
  border-radius: 20px;
  font-size: 12px;
  font-weight: bold;
  text-transform: uppercase;
}

.status-badge.active, .status-badge.confirmed {
  background-color: #e8f5e9;
  color: #4CAF50;
}

.status-badge.pending {
  background-color: #fff8e1;
  color: #FFC107;
}

.status-badge.inactive, .status-badge.cancelled {
  background-color: #ffebee;
  color: #F44336;
}

.status-badge.completed {
  background-color: #e3f2fd;
  color: #2196F3;
}

.actions {
  display: flex;
  gap: 5px;
}

.btn-small {
  padding: 5px 10px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 12px;
  background-color: #e0e0e0;
}

.btn-small:hover {
  background-color: #d0d0d0;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-success {
  background-color: #4CAF50;
  color: white;
}

.btn-success:hover {
  background-color: #45a049;
}

.btn-danger {
  background-color: #F44336;
  color: white;
}

.btn-danger:hover {
  background-color: #d32f2f;
}

.report-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
}

.report-card {
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 8px;
  padding: 20px;
}

.chart-placeholder {
  height: 250px;
  background-color: #f9f9f9;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #666;
  font-style: italic;
  margin-top: 15px;
}

.not-authorized, .loading, .error {
  text-align: center;
  padding: 50px;
}

.error {
  color: red;
}

@media (max-width: 768px) {
  .stats-cards, .report-cards {
    grid-template-columns: 1fr;
  }
  
  .tab-buttons {
    flex-direction: column;
  }
  
  .panel-header {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .data-table {
    display: block;
    overflow-x: auto;
  }
}
</style> 