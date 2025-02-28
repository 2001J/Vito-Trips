<template>
  <div class="tour-list-container">
    <h1>Eco-Friendly Tours</h1>
    <div class="filters">
      <input type="text" v-model="searchQuery" placeholder="Search tours...">
      <select v-model="selectedRegion">
        <option value="">All Regions</option>
        <option value="Europe">Europe</option>
        <option value="Asia">Asia</option>
        <option value="Africa">Africa</option>
        <option value="North America">North America</option>
        <option value="South America">South America</option>
        <option value="Oceania">Oceania</option>
      </select>
    </div>
    <div v-if="loading" class="loading">Loading tours...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="filteredTours.length === 0" class="no-results">
      No tours found matching your criteria.
    </div>
    <div v-else class="tour-grid">
      <div v-for="tour in filteredTours" :key="tour.id" class="tour-card">
        <div class="tour-image">
          <img :src="tour.imageUrl || 'https://via.placeholder.com/300x200'" :alt="tour.name">
        </div>
        <div class="tour-info">
          <h3>{{ tour.name }}</h3>
          <p class="tour-location">{{ tour.location }}</p>
          <p class="tour-description">{{ tour.description }}</p>
          <div class="tour-details">
            <span class="tour-duration">{{ tour.duration }} days</span>
            <span class="tour-price">${{ tour.price }}</span>
          </div>
          <router-link :to="'/tours/' + tour.id" class="view-details">View Details</router-link>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'TourList',
  data() {
    return {
      searchQuery: '',
      selectedRegion: '',
      loading: false,
      error: null
    };
  },
  computed: {
    tours() {
      return this.$store.state.tours || [];
    },
    filteredTours() {
      return this.tours.filter(tour => {
        const matchesSearch = tour.name.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
                             tour.description.toLowerCase().includes(this.searchQuery.toLowerCase());
        const matchesRegion = !this.selectedRegion || tour.region === this.selectedRegion;
        return matchesSearch && matchesRegion;
      });
    }
  },
  async created() {
    this.loading = true;
    try {
      await this.$store.dispatch('fetchTours');
    } catch (error) {
      this.error = 'Failed to load tours. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  }
};
</script>

<style scoped>
.tour-list-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filters {
  display: flex;
  margin-bottom: 20px;
  gap: 10px;
}

.filters input, .filters select {
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.filters input {
  flex-grow: 1;
}

.tour-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(300px, 1fr));
  gap: 20px;
}

.tour-card {
  border: 1px solid #ddd;
  border-radius: 8px;
  overflow: hidden;
  transition: transform 0.3s ease;
}

.tour-card:hover {
  transform: translateY(-5px);
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.tour-image img {
  width: 100%;
  height: 200px;
  object-fit: cover;
}

.tour-info {
  padding: 15px;
}

.tour-location {
  color: #666;
  margin-bottom: 10px;
}

.tour-description {
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.tour-details {
  display: flex;
  justify-content: space-between;
  margin-bottom: 15px;
}

.tour-price {
  font-weight: bold;
  color: #4CAF50;
}

.view-details {
  display: block;
  text-align: center;
  background-color: #4CAF50;
  color: white;
  padding: 10px;
  border-radius: 4px;
  text-decoration: none;
}

.view-details:hover {
  background-color: #45a049;
}

.loading, .error, .no-results {
  text-align: center;
  padding: 20px;
}

.error {
  color: red;
}
</style> 