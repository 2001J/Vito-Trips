<template>
  <div class="operator-dashboard">
    <h1>Tour Operator Dashboard</h1>
    
    <div v-if="!isOperator" class="not-authorized">
      <p>You are not authorized to view this page.</p>
      <router-link to="/" class="btn-primary">Return to Home</router-link>
    </div>
    
    <div v-else-if="loading" class="loading">Loading dashboard data...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    
    <div v-else class="dashboard-content">
      <div class="dashboard-header">
        <div class="stats-cards">
          <div class="stat-card">
            <div class="stat-value">{{ stats.activeTours }}</div>
            <div class="stat-label">Active Tours</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.upcomingBookings }}</div>
            <div class="stat-label">Upcoming Bookings</div>
          </div>
          <div class="stat-card">
            <div class="stat-value">{{ stats.totalTravelers }}</div>
            <div class="stat-label">Total Travelers</div>
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
          <!-- My Tours Tab -->
          <div v-if="activeTab === 'tours'" class="tab-panel">
            <div class="panel-header">
              <h2>My Tours</h2>
              <button @click="createTour" class="btn-primary">Create New Tour</button>
            </div>
            
            <table class="data-table">
              <thead>
                <tr>
                  <th>ID</th>
                  <th>Tour Name</th>
                  <th>Location</th>
                  <th>Duration</th>
                  <th>Price</th>
                  <th>Status</th>
                  <th>Bookings</th>
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
                  <td>{{ tour.bookings }}</td>
                  <td class="actions">
                    <button @click="editTour(tour.id)" class="btn-small">Edit</button>
                    <button @click="viewBookings(tour.id)" class="btn-small">View Bookings</button>
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
              <h2>Upcoming Bookings</h2>
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
                  <th>Tour</th>
                  <th>Customer</th>
                  <th>Travel Date</th>
                  <th>Travelers</th>
                  <th>Total</th>
                  <th>Status</th>
                  <th>Actions</th>
                </tr>
              </thead>
              <tbody>
                <tr v-for="booking in filteredBookings" :key="booking.id">
                  <td>{{ booking.id }}</td>
                  <td>{{ booking.tourName }}</td>
                  <td>{{ booking.customerName }}</td>
                  <td>{{ formatDate(booking.travelDate) }}</td>
                  <td>{{ booking.travelers }}</td>
                  <td>${{ booking.total }}</td>
                  <td>
                    <span :class="'status-badge ' + booking.status.toLowerCase()">
                      {{ booking.status }}
                    </span>
                  </td>
                  <td class="actions">
                    <button @click="viewBookingDetails(booking.id)" class="btn-small">Details</button>
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
          
          <!-- Calendar Tab -->
          <div v-if="activeTab === 'calendar'" class="tab-panel">
            <div class="panel-header">
              <h2>Tour Calendar</h2>
              <div class="month-navigation">
                <button @click="previousMonth" class="btn-icon">&lt;</button>
                <span class="current-month">{{ currentMonthName }} {{ currentYear }}</span>
                <button @click="nextMonth" class="btn-icon">&gt;</button>
              </div>
            </div>
            
            <div class="calendar">
              <div class="calendar-header">
                <div v-for="day in weekDays" :key="day" class="calendar-cell">{{ day }}</div>
              </div>
              <div class="calendar-body">
                <div 
                  v-for="(day, index) in calendarDays" 
                  :key="index" 
                  class="calendar-cell"
                  :class="{ 'other-month': !day.currentMonth, 'has-events': day.events.length > 0 }"
                >
                  <div class="day-number">{{ day.day }}</div>
                  <div v-if="day.events.length > 0" class="day-events">
                    <div 
                      v-for="(event, eventIndex) in day.events" 
                      :key="eventIndex" 
                      class="event-pill"
                      :class="event.type"
                      @click="viewEvent(event)"
                    >
                      {{ event.title }}
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
          
          <!-- Analytics Tab -->
          <div v-if="activeTab === 'analytics'" class="tab-panel">
            <div class="panel-header">
              <h2>Performance Analytics</h2>
              <div class="date-range">
                <select v-model="analyticsTimeframe">
                  <option value="30days">Last 30 Days</option>
                  <option value="90days">Last 90 Days</option>
                  <option value="year">This Year</option>
                  <option value="alltime">All Time</option>
                </select>
              </div>
            </div>
            
            <div class="analytics-cards">
              <div class="analytics-card">
                <h3>Bookings by Tour</h3>
                <div class="chart-placeholder">
                  [Bar Chart Visualization]
                </div>
              </div>
              
              <div class="analytics-card">
                <h3>Revenue Trend</h3>
                <div class="chart-placeholder">
                  [Line Chart Visualization]
                </div>
              </div>
              
              <div class="analytics-card">
                <h3>Customer Demographics</h3>
                <div class="chart-placeholder">
                  [Pie Chart Visualization]
                </div>
              </div>
              
              <div class="analytics-card">
                <h3>Booking Conversion Rate</h3>
                <div class="chart-placeholder">
                  [Funnel Chart Visualization]
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
  name: 'OperatorDashboard',
  data() {
    return {
      loading: true,
      error: null,
      activeTab: 'tours',
      bookingFilter: 'all',
      analyticsTimeframe: '30days',
      currentMonth: new Date().getMonth(),
      currentYear: new Date().getFullYear(),
      weekDays: ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'],
      tabs: [
        { id: 'tours', name: 'My Tours' },
        { id: 'bookings', name: 'Bookings' },
        { id: 'calendar', name: 'Calendar' },
        { id: 'analytics', name: 'Analytics' }
      ],
      stats: {
        activeTours: 0,
        upcomingBookings: 0,
        totalTravelers: 0,
        revenue: 0
      },
      tours: [],
      bookings: [],
      calendarEvents: []
    };
  },
  computed: {
    isOperator() {
      return this.$store.state.user && this.$store.state.user.role === 'TOUR_OPERATOR';
    },
    filteredBookings() {
      if (this.bookingFilter === 'all') return this.bookings;
      
      return this.bookings.filter(booking => 
        booking.status.toLowerCase() === this.bookingFilter
      );
    },
    currentMonthName() {
      return new Date(this.currentYear, this.currentMonth, 1).toLocaleString('default', { month: 'long' });
    },
    calendarDays() {
      const days = [];
      const firstDay = new Date(this.currentYear, this.currentMonth, 1);
      const lastDay = new Date(this.currentYear, this.currentMonth + 1, 0);
      
      // Add days from previous month to fill first week
      const firstDayOfWeek = firstDay.getDay();
      if (firstDayOfWeek > 0) {
        const prevMonthLastDay = new Date(this.currentYear, this.currentMonth, 0).getDate();
        for (let i = firstDayOfWeek - 1; i >= 0; i--) {
          const day = prevMonthLastDay - i;
          days.push({
            day,
            currentMonth: false,
            events: this.getEventsForDate(new Date(this.currentYear, this.currentMonth - 1, day))
          });
        }
      }
      
      // Add days of current month
      for (let day = 1; day <= lastDay.getDate(); day++) {
        days.push({
          day,
          currentMonth: true,
          events: this.getEventsForDate(new Date(this.currentYear, this.currentMonth, day))
        });
      }
      
      // Add days from next month to complete last week
      const remainingCells = 42 - days.length; // 6 rows of 7 days
      for (let day = 1; day <= remainingCells; day++) {
        days.push({
          day,
          currentMonth: false,
          events: this.getEventsForDate(new Date(this.currentYear, this.currentMonth + 1, day))
        });
      }
      
      return days;
    }
  },
  async created() {
    if (!this.isOperator) {
      this.loading = false;
      return;
    }
    
    try {
      // In a real app, these would be API calls
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // Mock data
      this.stats = {
        activeTours: 5,
        upcomingBookings: 12,
        totalTravelers: 47,
        revenue: 68950
      };
      
      this.tours = [
        { id: 1, name: 'Eco Adventure Tour', location: 'Costa Rica', duration: 7, price: 1299, status: 'Active', bookings: 8 },
        { id: 2, name: 'Sustainable Safari', location: 'Kenya', duration: 10, price: 2499, status: 'Active', bookings: 4 },
        { id: 3, name: 'Rainforest Conservation Experience', location: 'Brazil', duration: 14, price: 1899, status: 'Active', bookings: 6 },
        { id: 4, name: 'Alpine Eco Retreat', location: 'Switzerland', duration: 5, price: 1599, status: 'Inactive', bookings: 0 },
        { id: 5, name: 'Island Conservation Volunteer', location: 'Galapagos', duration: 21, price: 2899, status: 'Pending', bookings: 0 }
      ];
      
      this.bookings = [
        { id: 12345, tourName: 'Eco Adventure Tour', customerName: 'John Doe', travelDate: new Date(2023, 6, 10), travelers: 2, total: 2598, status: 'Confirmed' },
        { id: 12346, tourName: 'Sustainable Safari', customerName: 'Jane Smith', travelDate: new Date(2023, 7, 15), travelers: 1, total: 2499, status: 'Pending' },
        { id: 12347, tourName: 'Rainforest Conservation Experience', customerName: 'Bob Johnson', travelDate: new Date(2023, 8, 5), travelers: 3, total: 5697, status: 'Confirmed' },
        { id: 12348, tourName: 'Eco Adventure Tour', customerName: 'Alice Brown', travelDate: new Date(2023, 5, 20), travelers: 2, total: 3198, status: 'Completed' },
        { id: 12349, tourName: 'Sustainable Safari', customerName: 'Charlie Wilson', travelDate: new Date(2023, 9, 12), travelers: 1, total: 2899, status: 'Cancelled' }
      ];
      
      // Generate calendar events from bookings and tour departures
      this.calendarEvents = [
        ...this.bookings.map(booking => ({
          id: `booking-${booking.id}`,
          title: `${booking.tourName} (${booking.travelers})`,
          date: booking.travelDate,
          type: 'booking',
          data: booking
        })),
        // Add some tour departures
        {
          id: 'departure-1',
          title: 'Eco Adventure Tour',
          date: new Date(2023, this.currentMonth, 5),
          type: 'departure',
          data: { tourId: 1 }
        },
        {
          id: 'departure-2',
          title: 'Sustainable Safari',
          date: new Date(2023, this.currentMonth, 12),
          type: 'departure',
          data: { tourId: 2 }
        },
        {
          id: 'departure-3',
          title: 'Rainforest Experience',
          date: new Date(2023, this.currentMonth, 19),
          type: 'departure',
          data: { tourId: 3 }
        }
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
    createTour() {
      // In a real app, this would open a tour creation form
      alert('Creating a new tour');
    },
    editTour(tourId) {
      // In a real app, this would open a tour edit form
      alert(`Editing tour with ID: ${tourId}`);
    },
    viewBookings(tourId) {
      this.activeTab = 'bookings';
      // In a real app, this would filter bookings by tour
      alert(`Viewing bookings for tour ID: ${tourId}`);
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
    previousMonth() {
      if (this.currentMonth === 0) {
        this.currentMonth = 11;
        this.currentYear--;
      } else {
        this.currentMonth--;
      }
    },
    nextMonth() {
      if (this.currentMonth === 11) {
        this.currentMonth = 0;
        this.currentYear++;
      } else {
        this.currentMonth++;
      }
    },
    getEventsForDate(date) {
      return this.calendarEvents.filter(event => {
        const eventDate = new Date(event.date);
        return eventDate.getDate() === date.getDate() && 
               eventDate.getMonth() === date.getMonth() && 
               eventDate.getFullYear() === date.getFullYear();
      });
    },
    viewEvent(event) {
      if (event.type === 'booking') {
        this.viewBookingDetails(event.data.id);
      } else if (event.type === 'departure') {
        alert(`Tour departure: ${event.title}`);
      }
    }
  }
};
</script>

<style scoped>
.operator-dashboard {
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

.filter-group select, .date-range select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
  min-width: 200px;
}

.month-navigation {
  display: flex;
  align-items: center;
  gap: 15px;
}

.current-month {
  font-size: 18px;
  font-weight: bold;
}

.btn-icon {
  width: 30px;
  height: 30px;
  border-radius: 50%;
  border: none;
  background-color: #f0f0f0;
  cursor: pointer;
  font-size: 16px;
  display: flex;
  align-items: center;
  justify-content: center;
}

.btn-icon:hover {
  background-color: #e0e0e0;
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

.calendar {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
}

.calendar-header {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  background-color: #f0f0f0;
}

.calendar-body {
  display: grid;
  grid-template-columns: repeat(7, 1fr);
  grid-template-rows: repeat(6, 1fr);
}

.calendar-cell {
  min-height: 100px;
  border-right: 1px solid #eee;
  border-bottom: 1px solid #eee;
  padding: 5px;
  position: relative;
}

.calendar-header .calendar-cell {
  min-height: auto;
  padding: 10px;
  text-align: center;
  font-weight: bold;
}

.day-number {
  font-size: 14px;
  font-weight: bold;
  margin-bottom: 5px;
}

.other-month .day-number {
  color: #ccc;
}

.day-events {
  display: flex;
  flex-direction: column;
  gap: 3px;
}

.event-pill {
  font-size: 11px;
  padding: 3px 6px;
  border-radius: 10px;
  white-space: nowrap;
  overflow: hidden;
  text-overflow: ellipsis;
  cursor: pointer;
}

.event-pill.booking {
  background-color: #e8f5e9;
  color: #4CAF50;
}

.event-pill.departure {
  background-color: #e3f2fd;
  color: #2196F3;
}

.analytics-cards {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(500px, 1fr));
  gap: 20px;
}

.analytics-card {
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
  .stats-cards, .analytics-cards {
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
  
  .calendar-cell {
    min-height: 80px;
  }
}
</style> 