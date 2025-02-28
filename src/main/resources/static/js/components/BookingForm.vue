<template>
  <div class="booking-form-container">
    <h1>Book Your Eco-Tour</h1>
    
    <div v-if="loading" class="loading">Loading tour information...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="!tour" class="not-found">Tour not found</div>
    <div v-else class="booking-content">
      <div class="tour-summary">
        <h2>{{ tour.name }}</h2>
        <p class="tour-location">{{ tour.location }}</p>
        <div class="tour-details">
          <div class="detail">
            <span class="label">Duration:</span>
            <span class="value">{{ tour.duration }} days</span>
          </div>
          <div class="detail">
            <span class="label">Price:</span>
            <span class="value">${{ tour.price }} per person</span>
          </div>
        </div>
      </div>
      
      <form @submit.prevent="submitBooking" class="booking-form">
        <h3>Traveler Information</h3>
        
        <div class="form-group">
          <label for="travelDate">Select Travel Date</label>
          <select id="travelDate" v-model="booking.travelDate" required>
            <option value="">Select a date</option>
            <option v-for="(date, index) in tour.availableDates" :key="index" :value="date">
              {{ new Date(date).toLocaleDateString() }}
            </option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="travelers">Number of Travelers</label>
          <select id="travelers" v-model.number="booking.travelers" required>
            <option v-for="n in 10" :key="n" :value="n">{{ n }}</option>
          </select>
        </div>
        
        <div class="form-group">
          <label for="specialRequests">Special Requests</label>
          <textarea id="specialRequests" v-model="booking.specialRequests" rows="4"></textarea>
        </div>
        
        <h3>Contact Information</h3>
        
        <div class="form-group">
          <label for="name">Full Name</label>
          <input type="text" id="name" v-model="booking.name" required>
        </div>
        
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="booking.email" required>
        </div>
        
        <div class="form-group">
          <label for="phone">Phone Number</label>
          <input type="tel" id="phone" v-model="booking.phone" required>
        </div>
        
        <div class="booking-summary">
          <h3>Booking Summary</h3>
          <div class="summary-item">
            <span class="label">Tour Price:</span>
            <span class="value">${{ tour.price }} x {{ booking.travelers || 0 }}</span>
          </div>
          <div class="summary-item">
            <span class="label">Total:</span>
            <span class="value total">${{ totalPrice }}</span>
          </div>
        </div>
        
        <div class="form-actions">
          <button type="submit" class="btn-primary" :disabled="submitting">
            {{ submitting ? 'Processing...' : 'Proceed to Payment' }}
          </button>
        </div>
      </form>
    </div>
  </div>
</template>

<script>
export default {
  name: 'BookingForm',
  props: {
    tourId: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: true,
      error: null,
      tour: null,
      booking: {
        travelDate: '',
        travelers: 1,
        specialRequests: '',
        name: '',
        email: '',
        phone: ''
      },
      submitting: false
    };
  },
  computed: {
    totalPrice() {
      if (!this.tour) return 0;
      return this.tour.price * (this.booking.travelers || 0);
    }
  },
  async created() {
    try {
      // Get tour from store if available
      const tourFromStore = this.$store.state.tours.find(t => t.id == this.tourId);
      
      if (tourFromStore) {
        this.tour = {
          ...tourFromStore,
          availableDates: [
            new Date(2023, 5, 15),
            new Date(2023, 6, 10),
            new Date(2023, 7, 5)
          ]
        };
      } else {
        // Fallback mock data if not found in store
        this.tour = {
          id: this.tourId,
          name: 'Eco Adventure Tour',
          location: 'Costa Rica',
          price: 1299,
          duration: 7,
          availableDates: [
            new Date(2023, 5, 15),
            new Date(2023, 6, 10),
            new Date(2023, 7, 5)
          ]
        };
      }
      
      // Pre-fill user information if logged in
      if (this.$store.state.user) {
        this.booking.name = this.$store.state.user.name;
        this.booking.email = this.$store.state.user.email;
      }
    } catch (error) {
      this.error = 'Failed to load tour information. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    async submitBooking() {
      this.submitting = true;
      
      try {
        // In a real app, this would be an API call to create a booking
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Mock booking ID
        const bookingId = Math.floor(Math.random() * 10000);
        
        // Navigate to payment page
        this.$router.push(`/payment/${bookingId}`);
      } catch (error) {
        this.error = 'Failed to process your booking. Please try again.';
        console.error(error);
      } finally {
        this.submitting = false;
      }
    }
  }
};
</script>

<style scoped>
.booking-form-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.booking-content {
  display: grid;
  grid-template-columns: 1fr;
  gap: 30px;
}

.tour-summary {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin-bottom: 20px;
}

.tour-location {
  color: #666;
  margin-bottom: 15px;
}

.tour-details {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
}

.detail {
  flex: 1;
  min-width: 120px;
}

.label {
  display: block;
  color: #666;
  font-size: 14px;
}

.value {
  font-weight: bold;
  font-size: 16px;
}

.booking-form {
  background-color: #fff;
  padding: 20px;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input, select, textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

textarea {
  resize: vertical;
}

.booking-summary {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  margin: 20px 0;
}

.summary-item {
  display: flex;
  justify-content: space-between;
  margin-bottom: 10px;
}

.total {
  font-size: 20px;
  color: #4CAF50;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
  width: 100%;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.loading, .error, .not-found {
  text-align: center;
  padding: 50px;
}

.error {
  color: red;
}

@media (min-width: 768px) {
  .booking-content {
    grid-template-columns: 1fr 2fr;
  }
  
  .tour-summary {
    margin-bottom: 0;
  }
}
</style> 