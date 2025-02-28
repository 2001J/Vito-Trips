<template>
  <div class="booking-history-container">
    <h1>My Bookings</h1>
    
    <div v-if="!isAuthenticated" class="not-authenticated">
      <p>Please log in to view your booking history.</p>
      <router-link to="/login" class="btn-primary">Log In</router-link>
    </div>
    
    <div v-else-if="loading" class="loading">Loading your bookings...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="bookings.length === 0" class="no-bookings">
      <p>You don't have any bookings yet.</p>
      <router-link to="/tours" class="btn-primary">Browse Tours</router-link>
    </div>
    
    <div v-else class="bookings-list">
      <div class="booking-filters">
        <div class="filter-group">
          <label for="statusFilter">Filter by Status</label>
          <select id="statusFilter" v-model="filters.status">
            <option value="">All Statuses</option>
            <option value="upcoming">Upcoming</option>
            <option value="completed">Completed</option>
            <option value="cancelled">Cancelled</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label for="sortBy">Sort by</label>
          <select id="sortBy" v-model="filters.sortBy">
            <option value="date-desc">Date (Newest First)</option>
            <option value="date-asc">Date (Oldest First)</option>
            <option value="price-desc">Price (Highest First)</option>
            <option value="price-asc">Price (Lowest First)</option>
          </select>
        </div>
      </div>
      
      <div class="booking-cards">
        <div v-for="booking in filteredBookings" :key="booking.id" class="booking-card">
          <div class="booking-header" :class="{ 'upcoming': booking.status === 'upcoming', 'completed': booking.status === 'completed', 'cancelled': booking.status === 'cancelled' }">
            <div class="booking-id">Booking #{{ booking.id }}</div>
            <div class="booking-status">{{ formatStatus(booking.status) }}</div>
          </div>
          
          <div class="booking-content">
            <div class="tour-info">
              <h3>{{ booking.tourName }}</h3>
              <p class="tour-location">{{ booking.location }}</p>
              <div class="booking-dates">
                <div class="date-item">
                  <span class="label">Booking Date:</span>
                  <span class="value">{{ formatDate(booking.bookingDate) }}</span>
                </div>
                <div class="date-item">
                  <span class="label">Travel Date:</span>
                  <span class="value">{{ formatDate(booking.travelDate) }}</span>
                </div>
              </div>
            </div>
            
            <div class="booking-details">
              <div class="detail-item">
                <span class="label">Travelers:</span>
                <span class="value">{{ booking.travelers }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Total Price:</span>
                <span class="value price">${{ booking.totalPrice }}</span>
              </div>
              <div class="detail-item">
                <span class="label">Payment Status:</span>
                <span class="value" :class="{ 'paid': booking.paymentStatus === 'paid', 'pending': booking.paymentStatus === 'pending' }">
                  {{ formatPaymentStatus(booking.paymentStatus) }}
                </span>
              </div>
            </div>
          </div>
          
          <div class="booking-actions">
            <router-link :to="'/tours/' + booking.tourId" class="btn-secondary">View Tour</router-link>
            
            <button v-if="booking.status === 'upcoming'" @click="cancelBooking(booking.id)" class="btn-danger">
              Cancel Booking
            </button>
            
            <button v-if="booking.paymentStatus === 'pending'" @click="proceedToPayment(booking.id)" class="btn-primary">
              Complete Payment
            </button>
            
            <button v-if="booking.status === 'completed'" @click="leaveReview(booking.id)" class="btn-secondary">
              Leave Review
            </button>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BookingHistory',
  data() {
    return {
      loading: true,
      error: null,
      bookings: [],
      filters: {
        status: '',
        sortBy: 'date-desc'
      }
    };
  },
  computed: {
    isAuthenticated() {
      return !!this.$store.state.token;
    },
    filteredBookings() {
      let result = [...this.bookings];
      
      // Apply status filter
      if (this.filters.status) {
        result = result.filter(booking => booking.status === this.filters.status);
      }
      
      // Apply sorting
      result.sort((a, b) => {
        switch (this.filters.sortBy) {
          case 'date-desc':
            return new Date(b.travelDate) - new Date(a.travelDate);
          case 'date-asc':
            return new Date(a.travelDate) - new Date(b.travelDate);
          case 'price-desc':
            return b.totalPrice - a.totalPrice;
          case 'price-asc':
            return a.totalPrice - b.totalPrice;
          default:
            return 0;
        }
      });
      
      return result;
    }
  },
  async created() {
    if (!this.isAuthenticated) {
      this.loading = false;
      return;
    }
    
    try {
      // In a real app, this would be an API call to get bookings
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // Mock booking data
      this.bookings = [
        {
          id: 12345,
          tourId: 1,
          tourName: 'Eco Adventure Tour',
          location: 'Costa Rica',
          bookingDate: new Date(2023, 1, 15),
          travelDate: new Date(2023, 6, 10),
          travelers: 2,
          totalPrice: 2598,
          status: 'upcoming',
          paymentStatus: 'paid'
        },
        {
          id: 12346,
          tourId: 2,
          tourName: 'Sustainable Safari',
          location: 'Kenya',
          bookingDate: new Date(2022, 11, 5),
          travelDate: new Date(2023, 3, 20),
          travelers: 1,
          totalPrice: 1899,
          status: 'completed',
          paymentStatus: 'paid'
        },
        {
          id: 12347,
          tourId: 3,
          tourName: 'Rainforest Conservation Experience',
          location: 'Brazil',
          bookingDate: new Date(2023, 2, 8),
          travelDate: new Date(2023, 8, 15),
          travelers: 2,
          totalPrice: 3199,
          status: 'upcoming',
          paymentStatus: 'pending'
        },
        {
          id: 12348,
          tourId: 4,
          tourName: 'Alpine Eco Retreat',
          location: 'Switzerland',
          bookingDate: new Date(2022, 9, 12),
          travelDate: new Date(2022, 12, 5),
          travelers: 3,
          totalPrice: 4500,
          status: 'cancelled',
          paymentStatus: 'refunded'
        }
      ];
    } catch (error) {
      this.error = 'Failed to load your bookings. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatDate(date) {
      return new Date(date).toLocaleDateString('en-US', {
        year: 'numeric',
        month: 'long',
        day: 'numeric'
      });
    },
    formatStatus(status) {
      switch (status) {
        case 'upcoming':
          return 'Upcoming';
        case 'completed':
          return 'Completed';
        case 'cancelled':
          return 'Cancelled';
        default:
          return status;
      }
    },
    formatPaymentStatus(status) {
      switch (status) {
        case 'paid':
          return 'Paid';
        case 'pending':
          return 'Pending';
        case 'refunded':
          return 'Refunded';
        default:
          return status;
      }
    },
    async cancelBooking(bookingId) {
      if (!confirm('Are you sure you want to cancel this booking? Cancellation fees may apply.')) {
        return;
      }
      
      try {
        // In a real app, this would be an API call to cancel booking
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Update booking status locally
        const booking = this.bookings.find(b => b.id === bookingId);
        if (booking) {
          booking.status = 'cancelled';
          booking.paymentStatus = 'refunded';
        }
        
        alert('Booking cancelled successfully.');
      } catch (error) {
        this.error = 'Failed to cancel booking. Please try again.';
        console.error(error);
      }
    },
    proceedToPayment(bookingId) {
      this.$router.push(`/payment/${bookingId}`);
    },
    leaveReview(bookingId) {
      // In a real app, this would navigate to a review form
      alert('Review functionality would open here.');
    }
  }
};
</script>

<style scoped>
.booking-history-container {
  max-width: 1000px;
  margin: 0 auto;
  padding: 20px;
}

.booking-filters {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 20px;
  background-color: #f9f9f9;
  padding: 15px;
  border-radius: 8px;
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

.filter-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.booking-cards {
  display: flex;
  flex-direction: column;
  gap: 20px;
}

.booking-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.booking-header {
  display: flex;
  justify-content: space-between;
  padding: 15px;
  background-color: #f0f0f0;
  color: #333;
}

.booking-header.upcoming {
  background-color: #4CAF50;
  color: white;
}

.booking-header.completed {
  background-color: #2196F3;
  color: white;
}

.booking-header.cancelled {
  background-color: #F44336;
  color: white;
}

.booking-id {
  font-weight: bold;
}

.booking-content {
  padding: 20px;
  display: flex;
  flex-wrap: wrap;
  gap: 30px;
}

.tour-info {
  flex: 2;
  min-width: 300px;
}

.tour-location {
  color: #666;
  margin-bottom: 15px;
}

.booking-dates {
  margin-top: 15px;
}

.date-item {
  margin-bottom: 10px;
}

.booking-details {
  flex: 1;
  min-width: 200px;
}

.detail-item {
  margin-bottom: 10px;
}

.label {
  color: #666;
  font-size: 14px;
  display: block;
}

.value {
  font-weight: bold;
}

.value.price {
  color: #4CAF50;
}

.value.paid {
  color: #4CAF50;
}

.value.pending {
  color: #FFC107;
}

.booking-actions {
  padding: 15px 20px;
  background-color: #f9f9f9;
  display: flex;
  gap: 10px;
  flex-wrap: wrap;
}

.btn-primary, .btn-secondary, .btn-danger {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  text-decoration: none;
  display: inline-block;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-danger {
  background-color: #F44336;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary:hover {
  background-color: #0b7dda;
}

.btn-danger:hover {
  background-color: #d32f2f;
}

.not-authenticated, .no-bookings {
  text-align: center;
  padding: 50px;
}

.loading, .error {
  text-align: center;
  padding: 20px;
}

.error {
  color: red;
}
</style> 