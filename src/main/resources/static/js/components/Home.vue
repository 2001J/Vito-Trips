<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero" :style="{ backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.5), rgba(0, 0, 0, 0.5)), url(${require('img/hero-bg.jpg')})` }">
      <div class="hero-content">
        <h1>Travel with Purpose</h1>
        <p class="hero-subtitle">Discover sustainable travel experiences that benefit local communities and preserve natural environments.</p>
        <div class="hero-buttons">
          <router-link to="/tours" class="btn btn-primary">Explore Tours</router-link>
          <router-link to="/volunteer" class="btn btn-outline">Volunteer Opportunities</router-link>
        </div>
      </div>
    </section>
    
    <!-- Features Section -->
    <section class="features">
      <div class="container">
        <h2 class="section-title">Why Choose VitoTrips?</h2>
        
        <div class="feature-grid">
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-leaf"></i>
            </div>
            <h3>Sustainable Travel</h3>
            <p>All our tours are designed with sustainability in mind, minimizing environmental impact while maximizing positive social outcomes.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-users"></i>
            </div>
            <h3>Community Support</h3>
            <p>We partner with local communities to ensure that tourism benefits those who need it most, creating economic opportunities.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-hands-helping"></i>
            </div>
            <h3>Volunteer Opportunities</h3>
            <p>Make a difference during your travels by participating in meaningful volunteer projects with local organizations.</p>
          </div>
          
          <div class="feature-card">
            <div class="feature-icon">
              <i class="fas fa-globe-americas"></i>
            </div>
            <h3>Authentic Experiences</h3>
            <p>Immerse yourself in local cultures and traditions with authentic experiences led by knowledgeable local guides.</p>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Popular Tours Section -->
    <section class="popular-tours">
      <div class="container">
        <h2 class="section-title">Popular Destinations</h2>
        
        <div v-if="loading" class="loading-container">
          <div class="spinner"></div>
          <p>Loading tours...</p>
        </div>
        
        <div v-else-if="error" class="error-message">
          {{ error }}
        </div>
        
        <div v-else-if="popularTours.length === 0" class="no-tours">
          <p>No tours available at the moment. Please check back later.</p>
        </div>
        
        <div v-else class="tour-grid">
          <div v-for="tour in popularTours" :key="tour.id" class="tour-card">
            <div class="tour-image">
              <img :src="tour.imageUrl || require('img/placeholder-tour.jpg')" :alt="tour.name">
              <div class="tour-badge" v-if="tour.featured">Featured</div>
            </div>
            <div class="tour-content">
              <h3>{{ tour.name }}</h3>
              <div class="tour-meta">
                <span><i class="fas fa-map-marker-alt"></i> {{ tour.location }}</span>
                <span><i class="fas fa-calendar-alt"></i> {{ tour.duration }} days</span>
              </div>
              <p class="tour-description">{{ truncateText(tour.description, 100) }}</p>
              <div class="tour-footer">
                <span class="tour-price">${{ tour.price }}</span>
                <router-link :to="'/tours/' + tour.id" class="btn btn-sm">View Details</router-link>
              </div>
            </div>
          </div>
        </div>
        
        <div class="view-all">
          <router-link to="/tours" class="btn btn-outline">View All Tours</router-link>
        </div>
      </div>
    </section>
    
    <!-- Testimonials Section -->
    <section class="testimonials">
      <div class="container">
        <h2 class="section-title">What Our Travelers Say</h2>
        
        <div class="testimonial-slider">
          <div v-for="(testimonial, index) in testimonials" :key="index" class="testimonial-card">
            <div class="testimonial-content">
              <p class="testimonial-text">"{{ testimonial.text }}"</p>
              <div class="testimonial-author">
                <img :src="testimonial.avatar" :alt="testimonial.name" class="testimonial-avatar">
                <div class="testimonial-info">
                  <h4>{{ testimonial.name }}</h4>
                  <p>{{ testimonial.location }}</p>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </section>
    
    <!-- Call to Action Section -->
    <section class="cta" :style="{ backgroundImage: `linear-gradient(rgba(0, 0, 0, 0.7), rgba(0, 0, 0, 0.7)), url(${require('img/cta-bg.jpg')})` }">
      <div class="container">
        <div class="cta-content">
          <h2>Ready to Start Your Journey?</h2>
          <p>Join us in creating a more sustainable future through responsible travel.</p>
          <router-link to="/register" class="btn btn-primary">Sign Up Now</router-link>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapState } from 'vuex';

export default {
  name: 'Home',
  data() {
    return {
      popularTours: [],
      loading: false,
      error: null,
      testimonials: [
        {
          text: "Vito Trips provided an incredible experience that combined adventure with meaningful cultural exchange. The local guides were knowledgeable and passionate.",
          name: "Sarah Johnson",
          location: "New York, USA",
          avatar: require('img/testimonials/sarah.jpg')
        },
        {
          text: "My sustainable tour through Costa Rica was life-changing. I appreciated how every aspect of the trip was designed to minimize environmental impact.",
          name: "David Chen",
          location: "Vancouver, Canada",
          avatar: require('img/testimonials/david.jpg')
        },
        {
          text: "The community-based tourism experience allowed me to connect with local families and understand their way of life. It was authentic and respectful.",
          name: "Emma Rodriguez",
          location: "Madrid, Spain",
          avatar: require('img/testimonials/emma.jpg')
        }
      ]
    };
  },
  computed: {
    ...mapState(['loading', 'error'])
  },
  methods: {
    truncateText(text, maxLength) {
      if (!text) return '';
      return text.length > maxLength ? text.substring(0, maxLength) + '...' : text;
    },
    async fetchPopularTours() {
      try {
        const response = await fetch('/api/tours/popular');
        const data = await response.json();
        
        if (!response.ok) {
          throw new Error(data.error || 'Failed to fetch popular tours');
        }
        
        this.popularTours = data;
      } catch (error) {
        console.error('Error fetching popular tours:', error);
        this.$store.commit('setError', error.message);
      }
    }
  },
  created() {
    this.fetchPopularTours();
  }
};
</script>

<style scoped>
/* Hero Section */
.hero[data-v-df1b50d4] {
  height: 600px;
  background-size: cover;
  background-position: center;
  display: flex;
  align-items: center;
  justify-content: center;
  text-align: center;
  color: #ffffff;
  margin-bottom: 60px;
}

.hero-content {
  max-width: 800px;
  padding: 0 20px;
}

.hero h1 {
  font-size: 48px;
  font-weight: 700;
  margin-bottom: 20px;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.3);
}

.hero-subtitle {
  font-size: 20px;
  margin-bottom: 30px;
  text-shadow: 0 1px 2px rgba(0, 0, 0, 0.3);
}

.hero-buttons {
  display: flex;
  justify-content: center;
  gap: 20px;
}

.btn {
  display: inline-block;
  padding: 12px 24px;
  border-radius: 4px;
  font-weight: 500;
  text-align: center;
  transition: all 0.3s;
}

.btn-primary {
  background-color: var(--primary-color);
  color: #ffffff;
  border: none;
}

.btn-primary:hover {
  background-color: #3a80d2;
  color: #ffffff;
}

.btn-outline {
  background-color: transparent;
  color: #ffffff;
  border: 2px solid #ffffff;
}

.btn-outline:hover {
  background-color: #ffffff;
  color: var(--primary-color);
}

/* Features Section */
.features {
  padding: 60px 0;
  background-color: var(--light-gray);
}

.section-title {
  text-align: center;
  font-size: 32px;
  margin-bottom: 40px;
  color: var(--text-color);
}

.feature-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 30px;
}

.feature-card {
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  text-align: center;
  transition: transform 0.3s;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  width: 70px;
  height: 70px;
  background-color: var(--primary-color);
  color: #ffffff;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  margin: 0 auto 20px;
  font-size: 28px;
}

.feature-card h3 {
  font-size: 20px;
  margin-bottom: 15px;
  color: var(--text-color);
}

.feature-card p {
  color: var(--dark-gray);
  line-height: 1.6;
}

/* Popular Tours Section */
.popular-tours {
  padding: 60px 0;
}

.tour-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 30px;
  margin-bottom: 40px;
}

.tour-card {
  background-color: #ffffff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
  transition: transform 0.3s;
}

.tour-card:hover {
  transform: translateY(-5px);
}

.tour-image {
  height: 200px;
  position: relative;
  overflow: hidden;
}

.tour-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
  transition: transform 0.5s;
}

.tour-card:hover .tour-image img {
  transform: scale(1.1);
}

.tour-badge {
  position: absolute;
  top: 15px;
  right: 15px;
  background-color: var(--accent-color);
  color: #ffffff;
  padding: 5px 10px;
  border-radius: 4px;
  font-size: 12px;
  font-weight: 500;
}

.tour-content {
  padding: 20px;
}

.tour-content h3 {
  font-size: 20px;
  margin-bottom: 10px;
  color: var(--text-color);
}

.tour-meta {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
  font-size: 14px;
  color: var(--dark-gray);
}

.tour-description {
  color: var(--dark-gray);
  margin-bottom: 15px;
  line-height: 1.6;
}

.tour-footer {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.tour-price {
  font-size: 20px;
  font-weight: 700;
  color: var(--primary-color);
}

.btn-sm {
  padding: 8px 16px;
  font-size: 14px;
  background-color: var(--primary-color);
  color: #ffffff;
  border-radius: 4px;
}

.view-all {
  text-align: center;
}

.view-all .btn-outline {
  border: 2px solid var(--primary-color);
  color: var(--primary-color);
}

.view-all .btn-outline:hover {
  background-color: var(--primary-color);
  color: #ffffff;
}

.loading-container {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 40px;
}

.error-message {
  background-color: #fdecea;
  color: var(--danger-color);
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.no-tours {
  text-align: center;
  padding: 40px;
  color: var(--dark-gray);
}

/* Testimonials Section */
.testimonials {
  padding: 60px 0;
  background-color: var(--light-gray);
}

.testimonial-slider {
  display: flex;
  overflow-x: auto;
  gap: 30px;
  padding: 20px 0;
  scrollbar-width: none; /* Firefox */
}

.testimonial-slider::-webkit-scrollbar {
  display: none; /* Chrome, Safari, Edge */
}

.testimonial-card {
  flex: 0 0 350px;
  background-color: #ffffff;
  border-radius: 8px;
  padding: 30px;
  box-shadow: 0 5px 15px rgba(0, 0, 0, 0.05);
}

.testimonial-text {
  font-style: italic;
  margin-bottom: 20px;
  color: var(--text-color);
  line-height: 1.6;
}

.testimonial-author {
  display: flex;
  align-items: center;
}

.testimonial-avatar {
  width: 50px;
  height: 50px;
  border-radius: 50%;
  object-fit: cover;
  margin-right: 15px;
}

.testimonial-info h4 {
  font-size: 16px;
  margin-bottom: 5px;
  color: var(--text-color);
}

.testimonial-info p {
  font-size: 14px;
  color: var(--dark-gray);
}

/* Call to Action Section */
.cta[data-v-df1b50d4] {
  padding: 80px 0;
  background-size: cover;
  background-position: center;
  color: #ffffff;
  text-align: center;
}

.cta-content h2 {
  font-size: 36px;
  margin-bottom: 20px;
}

.cta-content p {
  font-size: 18px;
  margin-bottom: 30px;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

/* Responsive Styles */
@media (max-width: 768px) {
  .hero {
    height: 500px;
  }
  
  .hero h1 {
    font-size: 36px;
  }
  
  .hero-subtitle {
    font-size: 18px;
  }
  
  .hero-buttons {
    flex-direction: column;
    gap: 15px;
  }
  
  .feature-grid {
    grid-template-columns: 1fr;
  }
  
  .tour-grid {
    grid-template-columns: 1fr;
  }
  
  .testimonial-card {
    flex: 0 0 300px;
  }
  
  .cta-content h2 {
    font-size: 28px;
  }
  
  .cta-content p {
    font-size: 16px;
  }
}
</style> 