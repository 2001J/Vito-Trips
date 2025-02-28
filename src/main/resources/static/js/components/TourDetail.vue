<template>
  <div class="tour-detail-container">
    <div v-if="loading" class="loading">Loading tour details...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="!tour" class="not-found">Tour not found</div>
    <div v-else class="tour-content">
      <div class="tour-header">
        <h1>{{ tour.name }}</h1>
        <div class="tour-location">{{ tour.location }}</div>
        <div class="tour-rating">
          <span v-for="i in 5" :key="i" class="star" :class="{ 'filled': i <= tour.rating }">★</span>
          <span class="rating-count">({{ tour.reviewCount || 0 }} reviews)</span>
        </div>
      </div>
      
      <div class="tour-gallery">
        <img :src="tour.imageUrl || 'https://via.placeholder.com/800x400'" :alt="tour.name" class="main-image">
      </div>
      
      <div class="tour-details">
        <div class="tour-info">
          <h2>About this tour</h2>
          <p>{{ tour.description }}</p>
          
          <h3>Eco-Friendly Features</h3>
          <ul class="eco-features">
            <li v-for="(feature, index) in tour.ecoFeatures" :key="index">
              {{ feature }}
            </li>
          </ul>
          
          <h3>Itinerary</h3>
          <div class="itinerary">
            <div v-for="(day, index) in tour.itinerary" :key="index" class="itinerary-day">
              <h4>Day {{ index + 1 }}</h4>
              <p>{{ day }}</p>
            </div>
          </div>
          
          <h3>What's Included</h3>
          <ul class="inclusions">
            <li v-for="(inclusion, index) in tour.inclusions" :key="index">
              {{ inclusion }}
            </li>
          </ul>
        </div>
        
        <div class="booking-sidebar">
          <div class="price-box">
            <div class="price">${{ tour.price }} <span class="per-person">per person</span></div>
            <div class="duration">{{ tour.duration }} days</div>
          </div>
          
          <div class="availability">
            <h3>Available Dates</h3>
            <div v-for="(date, index) in tour.availableDates" :key="index" class="date-option">
              {{ new Date(date).toLocaleDateString() }}
            </div>
          </div>
          
          <router-link :to="'/booking/' + tour.id" class="book-now-btn">Book Now</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TourDetail',
  props: {
    id: {
      type: [String, Number],
      required: true
    }
  },
  data() {
    return {
      loading: true,
      error: null,
      tour: null
    };
  },
  async created() {
    try {
      // Simulate API call to get tour details
      // In a real app, this would be an API call
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Get tour from store if available
      const tourFromStore = this.$store.state.tours.find(t => t.id == this.id);
      
      if (tourFromStore) {
        this.tour = {
          ...tourFromStore,
          rating: 4.5,
          reviewCount: 24,
          ecoFeatures: [
            'Carbon-neutral transportation',
            'Supports local conservation efforts',
            'Plastic-free accommodations',
            'Farm-to-table dining experiences'
          ],
          itinerary: [
            'Arrival and welcome dinner',
            'Guided nature walk and local community visit',
            'Sustainable farm tour and cooking class',
            'Free day for optional activities',
            'Departure day'
          ],
          inclusions: [
            'Eco-friendly accommodations',
            'All meals (locally sourced)',
            'Transportation during the tour',
            'Expert local guides',
            'Contribution to local conservation projects'
          ],
          availableDates: [
            new Date(2023, 5, 15),
            new Date(2023, 6, 10),
            new Date(2023, 7, 5)
          ]
        };
      } else {
        // Fallback mock data if not found in store
        this.tour = {
          id: this.id,
          name: 'Eco Adventure Tour',
          location: 'Costa Rica',
          description: 'Experience the beauty of Costa Rica while minimizing your environmental impact. This tour features sustainable accommodations, locally-sourced meals, and activities that support conservation efforts.',
          price: 1299,
          duration: 7,
          imageUrl: 'https://via.placeholder.com/800x400',
          rating: 4.5,
          reviewCount: 24,
          ecoFeatures: [
            'Carbon-neutral transportation',
            'Supports local conservation efforts',
            'Plastic-free accommodations',
            'Farm-to-table dining experiences'
          ],
          itinerary: [
            'Arrival and welcome dinner',
            'Guided nature walk and local community visit',
            'Sustainable farm tour and cooking class',
            'Free day for optional activities',
            'Departure day'
          ],
          inclusions: [
            'Eco-friendly accommodations',
            'All meals (locally sourced)',
            'Transportation during the tour',
            'Expert local guides',
            'Contribution to local conservation projects'
          ],
          availableDates: [
            new Date(2023, 5, 15),
            new Date(2023, 6, 10),
            new Date(2023, 7, 5)
          ]
        };
      }
    } catch (error) {
      this.error = 'Failed to load tour details. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.tour-detail-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.tour-header {
  margin-bottom: 20px;
}

.tour-location {
  font-size: 18px;
  color: #666;
  margin-bottom: 10px;
}

.tour-rating {
  margin-bottom: 20px;
}

.star {
  color: #ddd;
  font-size: 24px;
}

.star.filled {
  color: #FFD700;
}

.rating-count {
  color: #666;
  margin-left: 10px;
}

.tour-gallery {
  margin-bottom: 30px;
}

.main-image {
  width: 100%;
  height: auto;
  border-radius: 8px;
}

.tour-details {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 30px;
}

.tour-info h2, .tour-info h3 {
  margin-top: 30px;
  margin-bottom: 15px;
}

.eco-features, .inclusions {
  list-style-type: none;
  padding-left: 0;
}

.eco-features li, .inclusions li {
  padding: 8px 0;
  border-bottom: 1px solid #eee;
}

.itinerary-day {
  margin-bottom: 20px;
}

.booking-sidebar {
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
  position: sticky;
  top: 20px;
}

.price-box {
  margin-bottom: 20px;
  padding-bottom: 20px;
  border-bottom: 1px solid #eee;
}

.price {
  font-size: 28px;
  font-weight: bold;
  color: #4CAF50;
}

.per-person {
  font-size: 16px;
  font-weight: normal;
}

.duration {
  margin-top: 10px;
  font-size: 16px;
}

.availability {
  margin-bottom: 20px;
}

.date-option {
  padding: 10px;
  margin-bottom: 10px;
  background-color: #fff;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.book-now-btn {
  display: block;
  width: 100%;
  padding: 15px;
  background-color: #4CAF50;
  color: white;
  text-align: center;
  border-radius: 4px;
  text-decoration: none;
  font-size: 18px;
  font-weight: bold;
}

.book-now-btn:hover {
  background-color: #45a049;
}

.loading, .error, .not-found {
  text-align: center;
  padding: 50px;
}

.error {
  color: red;
}
</style> 