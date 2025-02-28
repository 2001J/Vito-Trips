<template>
  <div class="volunteer-container">
    <div class="volunteer-header">
      <h1>Volunteer Opportunities</h1>
      <p class="subtitle">Make a difference while you travel with our eco-friendly volunteer programs</p>
    </div>
    
    <div v-if="loading" class="loading">Loading volunteer opportunities...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    
    <div v-else class="volunteer-content">
      <div class="filters">
        <div class="filter-group">
          <label for="regionFilter">Filter by Region</label>
          <select id="regionFilter" v-model="filters.region">
            <option value="">All Regions</option>
            <option v-for="(region, index) in regions" :key="index" :value="region">{{ region }}</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label for="categoryFilter">Filter by Category</label>
          <select id="categoryFilter" v-model="filters.category">
            <option value="">All Categories</option>
            <option v-for="(category, index) in categories" :key="index" :value="category">{{ category }}</option>
          </select>
        </div>
        
        <div class="filter-group">
          <label for="durationFilter">Filter by Duration</label>
          <select id="durationFilter" v-model="filters.duration">
            <option value="">Any Duration</option>
            <option value="short">Short-term (1-2 weeks)</option>
            <option value="medium">Medium-term (2-4 weeks)</option>
            <option value="long">Long-term (1+ months)</option>
          </select>
        </div>
      </div>
      
      <div v-if="filteredOpportunities.length === 0" class="no-results">
        No volunteer opportunities match your current filters. Try adjusting your criteria.
      </div>
      
      <div v-else class="opportunities-grid">
        <div v-for="opportunity in filteredOpportunities" :key="opportunity.id" class="opportunity-card">
          <div class="opportunity-image">
            <img :src="opportunity.imageUrl || 'https://via.placeholder.com/400x250'" :alt="opportunity.title">
            <div class="opportunity-category">{{ opportunity.category }}</div>
          </div>
          
          <div class="opportunity-content">
            <h3>{{ opportunity.title }}</h3>
            <div class="opportunity-location">{{ opportunity.location }}</div>
            <p class="opportunity-description">{{ opportunity.description }}</p>
            
            <div class="opportunity-details">
              <div class="detail-item">
                <span class="icon">🗓️</span>
                <span>{{ formatDuration(opportunity.duration) }}</span>
              </div>
              <div class="detail-item">
                <span class="icon">💰</span>
                <span>{{ opportunity.cost > 0 ? '$' + opportunity.cost : 'Free' }}</span>
              </div>
              <div class="detail-item">
                <span class="icon">🏠</span>
                <span>{{ opportunity.accommodation }}</span>
              </div>
            </div>
            
            <div class="opportunity-impact">
              <h4>Impact</h4>
              <p>{{ opportunity.impact }}</p>
            </div>
            
            <div class="opportunity-actions">
              <button @click="showDetails(opportunity.id)" class="btn-primary">View Details</button>
              <button v-if="isAuthenticated" @click="applyNow(opportunity.id)" class="btn-secondary">Apply Now</button>
            </div>
          </div>
        </div>
      </div>
    </div>
    
    <!-- Modal for application form would go here in a real app -->
  </div>
</template>

<script>
export default {
  name: 'VolunteerOpportunities',
  data() {
    return {
      loading: true,
      error: null,
      opportunities: [],
      filters: {
        region: '',
        category: '',
        duration: ''
      },
      regions: [
        'Africa',
        'Asia',
        'Europe',
        'North America',
        'South America',
        'Oceania'
      ],
      categories: [
        'Wildlife Conservation',
        'Marine Conservation',
        'Environmental Restoration',
        'Sustainable Agriculture',
        'Community Development',
        'Education'
      ]
    };
  },
  computed: {
    isAuthenticated() {
      return !!this.$store.state.token;
    },
    filteredOpportunities() {
      return this.opportunities.filter(opportunity => {
        // Filter by region
        if (this.filters.region && opportunity.region !== this.filters.region) {
          return false;
        }
        
        // Filter by category
        if (this.filters.category && opportunity.category !== this.filters.category) {
          return false;
        }
        
        // Filter by duration
        if (this.filters.duration) {
          if (this.filters.duration === 'short' && opportunity.durationWeeks > 2) {
            return false;
          } else if (this.filters.duration === 'medium' && (opportunity.durationWeeks < 2 || opportunity.durationWeeks > 4)) {
            return false;
          } else if (this.filters.duration === 'long' && opportunity.durationWeeks < 4) {
            return false;
          }
        }
        
        return true;
      });
    }
  },
  async created() {
    try {
      // In a real app, this would be an API call to get volunteer opportunities
      await new Promise(resolve => setTimeout(resolve, 800));
      
      // Mock data
      this.opportunities = [
        {
          id: 1,
          title: 'Sea Turtle Conservation',
          location: 'Costa Rica',
          region: 'North America',
          description: 'Help protect endangered sea turtles by patrolling beaches, relocating nests, and releasing hatchlings.',
          category: 'Wildlife Conservation',
          durationWeeks: 2,
          cost: 850,
          accommodation: 'Shared cabin',
          impact: 'Contribute to the protection of endangered sea turtle species and their habitats.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Sea+Turtle+Conservation'
        },
        {
          id: 2,
          title: 'Rainforest Reforestation',
          location: 'Brazil',
          region: 'South America',
          description: 'Plant trees and monitor forest growth in areas affected by deforestation in the Amazon rainforest.',
          category: 'Environmental Restoration',
          durationWeeks: 3,
          cost: 1200,
          accommodation: 'Eco-lodge',
          impact: 'Help restore critical rainforest habitat and combat climate change through carbon sequestration.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Rainforest+Reforestation'
        },
        {
          id: 3,
          title: 'Coral Reef Monitoring',
          location: 'Thailand',
          region: 'Asia',
          description: 'Assist marine biologists in monitoring coral health, removing invasive species, and collecting data.',
          category: 'Marine Conservation',
          durationWeeks: 1,
          cost: 950,
          accommodation: 'Beachfront bungalow',
          impact: 'Contribute to the preservation of coral reef ecosystems that support marine biodiversity.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Coral+Reef+Monitoring'
        },
        {
          id: 4,
          title: 'Organic Farming Initiative',
          location: 'Italy',
          region: 'Europe',
          description: 'Learn and practice sustainable farming techniques on an organic farm in the Italian countryside.',
          category: 'Sustainable Agriculture',
          durationWeeks: 4,
          cost: 600,
          accommodation: 'Farmhouse',
          impact: 'Support sustainable food production methods and reduce environmental impact of agriculture.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Organic+Farming'
        },
        {
          id: 5,
          title: 'Wildlife Sanctuary Support',
          location: 'Kenya',
          region: 'Africa',
          description: 'Assist with daily care of rescued wildlife, habitat maintenance, and conservation education.',
          category: 'Wildlife Conservation',
          durationWeeks: 6,
          cost: 1500,
          accommodation: 'On-site dormitory',
          impact: 'Support the rehabilitation of injured and orphaned wildlife and their eventual return to the wild.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Wildlife+Sanctuary'
        },
        {
          id: 6,
          title: 'Eco-School Teaching Program',
          location: 'Nepal',
          region: 'Asia',
          description: 'Teach environmental education in local schools and help develop sustainable practices.',
          category: 'Education',
          durationWeeks: 8,
          cost: 900,
          accommodation: 'Homestay',
          impact: 'Inspire the next generation to become environmental stewards through education.',
          imageUrl: 'https://via.placeholder.com/400x250?text=Eco+School+Teaching'
        }
      ];
    } catch (error) {
      this.error = 'Failed to load volunteer opportunities. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    formatDuration(weeks) {
      if (weeks < 1) {
        return 'Less than a week';
      } else if (weeks === 1) {
        return '1 week';
      } else if (weeks < 4) {
        return `${weeks} weeks`;
      } else if (weeks === 4) {
        return '1 month';
      } else {
        const months = Math.floor(weeks / 4);
        const remainingWeeks = weeks % 4;
        
        if (remainingWeeks === 0) {
          return months === 1 ? '1 month' : `${months} months`;
        } else {
          return `${months} month${months > 1 ? 's' : ''} and ${remainingWeeks} week${remainingWeeks > 1 ? 's' : ''}`;
        }
      }
    },
    showDetails(opportunityId) {
      // In a real app, this would navigate to a details page or open a modal
      alert(`Showing details for opportunity #${opportunityId}`);
    },
    applyNow(opportunityId) {
      if (!this.isAuthenticated) {
        this.$router.push('/login');
        return;
      }
      
      // In a real app, this would open an application form
      alert(`Opening application form for opportunity #${opportunityId}`);
    }
  }
};
</script>

<style scoped>
.volunteer-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.volunteer-header {
  text-align: center;
  margin-bottom: 30px;
}

.subtitle {
  color: #666;
  font-size: 18px;
  max-width: 800px;
  margin: 0 auto;
}

.filters {
  display: flex;
  flex-wrap: wrap;
  gap: 20px;
  margin-bottom: 30px;
  background-color: #f9f9f9;
  padding: 20px;
  border-radius: 8px;
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  margin-bottom: 8px;
  font-weight: bold;
}

.filter-group select {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

.opportunities-grid {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(350px, 1fr));
  gap: 30px;
}

.opportunity-card {
  background-color: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 2px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.opportunity-card:hover {
  transform: translateY(-5px);
}

.opportunity-image {
  position: relative;
  height: 200px;
}

.opportunity-image img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.opportunity-category {
  position: absolute;
  bottom: 0;
  right: 0;
  background-color: rgba(76, 175, 80, 0.9);
  color: white;
  padding: 5px 10px;
  font-size: 14px;
  border-top-left-radius: 8px;
}

.opportunity-content {
  padding: 20px;
}

.opportunity-location {
  color: #666;
  margin-bottom: 10px;
}

.opportunity-description {
  margin-bottom: 15px;
  display: -webkit-box;
  -webkit-line-clamp: 3;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.opportunity-details {
  display: flex;
  flex-wrap: wrap;
  gap: 15px;
  margin-bottom: 15px;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
}

.detail-item {
  display: flex;
  align-items: center;
  gap: 5px;
}

.icon {
  font-size: 18px;
}

.opportunity-impact {
  margin-bottom: 20px;
}

.opportunity-impact h4 {
  margin-bottom: 5px;
  color: #4CAF50;
}

.opportunity-actions {
  display: flex;
  gap: 10px;
}

.btn-primary, .btn-secondary {
  padding: 10px 15px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 14px;
  flex: 1;
  text-align: center;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
}

.btn-secondary {
  background-color: #2196F3;
  color: white;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-secondary:hover {
  background-color: #0b7dda;
}

.loading, .error, .no-results {
  text-align: center;
  padding: 50px;
}

.error {
  color: red;
}

@media (max-width: 768px) {
  .opportunities-grid {
    grid-template-columns: 1fr;
  }
}
</style> 