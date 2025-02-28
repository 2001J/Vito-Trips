<template>
  <div class="profile-container">
    <h1>My Profile</h1>
    
    <div v-if="!isAuthenticated" class="not-authenticated">
      <p>Please log in to view your profile.</p>
      <router-link to="/login" class="btn-primary">Log In</router-link>
    </div>
    
    <div v-else-if="loading" class="loading">Loading profile information...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    
    <div v-else class="profile-content">
      <div class="profile-section">
        <h2>Personal Information</h2>
        <form @submit.prevent="updateProfile" class="profile-form">
          <div class="form-group">
            <label for="name">Full Name</label>
            <input type="text" id="name" v-model="profile.name" required>
          </div>
          
          <div class="form-group">
            <label for="email">Email</label>
            <input type="email" id="email" v-model="profile.email" required>
          </div>
          
          <div class="form-group">
            <label for="phone">Phone Number</label>
            <input type="tel" id="phone" v-model="profile.phone">
          </div>
          
          <div class="form-group">
            <label for="address">Address</label>
            <textarea id="address" v-model="profile.address" rows="3"></textarea>
          </div>
          
          <button type="submit" class="btn-primary" :disabled="updating">
            {{ updating ? 'Saving...' : 'Save Changes' }}
          </button>
        </form>
      </div>
      
      <div class="profile-section">
        <h2>Preferences</h2>
        <div class="preferences-form">
          <div class="form-group">
            <label>Preferred Travel Regions</label>
            <div class="checkbox-group">
              <div v-for="(region, index) in regions" :key="index" class="checkbox-item">
                <input type="checkbox" :id="'region-' + index" v-model="profile.preferredRegions" :value="region">
                <label :for="'region-' + index">{{ region }}</label>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label>Interests</label>
            <div class="checkbox-group">
              <div v-for="(interest, index) in interests" :key="index" class="checkbox-item">
                <input type="checkbox" :id="'interest-' + index" v-model="profile.interests" :value="interest">
                <label :for="'interest-' + index">{{ interest }}</label>
              </div>
            </div>
          </div>
          
          <div class="form-group">
            <label for="notifications">Email Notifications</label>
            <div class="toggle-switch">
              <input type="checkbox" id="notifications" v-model="profile.emailNotifications">
              <label for="notifications"></label>
              <span>{{ profile.emailNotifications ? 'Enabled' : 'Disabled' }}</span>
            </div>
          </div>
          
          <button @click="savePreferences" class="btn-primary" :disabled="updating">
            {{ updating ? 'Saving...' : 'Save Preferences' }}
          </button>
        </div>
      </div>
      
      <div class="profile-section">
        <h2>Account Security</h2>
        <form @submit.prevent="changePassword" class="security-form">
          <div class="form-group">
            <label for="currentPassword">Current Password</label>
            <input type="password" id="currentPassword" v-model="passwordChange.currentPassword" required>
          </div>
          
          <div class="form-group">
            <label for="newPassword">New Password</label>
            <input type="password" id="newPassword" v-model="passwordChange.newPassword" required>
          </div>
          
          <div class="form-group">
            <label for="confirmPassword">Confirm New Password</label>
            <input type="password" id="confirmPassword" v-model="passwordChange.confirmPassword" required>
          </div>
          
          <button type="submit" class="btn-primary" :disabled="updating">
            {{ updating ? 'Updating...' : 'Change Password' }}
          </button>
        </form>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'UserProfile',
  data() {
    return {
      loading: true,
      error: null,
      updating: false,
      profile: {
        name: '',
        email: '',
        phone: '',
        address: '',
        preferredRegions: [],
        interests: [],
        emailNotifications: true
      },
      passwordChange: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      regions: [
        'Europe',
        'Asia',
        'Africa',
        'North America',
        'South America',
        'Oceania'
      ],
      interests: [
        'Wildlife Conservation',
        'Cultural Experiences',
        'Adventure Activities',
        'Sustainable Agriculture',
        'Marine Conservation',
        'Volunteer Opportunities'
      ]
    };
  },
  computed: {
    isAuthenticated() {
      return !!this.$store.state.token;
    }
  },
  async created() {
    if (!this.isAuthenticated) {
      this.loading = false;
      return;
    }
    
    try {
      // In a real app, this would be an API call to get user profile
      await new Promise(resolve => setTimeout(resolve, 500));
      
      // Mock user data
      if (this.$store.state.user) {
        this.profile.name = this.$store.state.user.name || '';
        this.profile.email = this.$store.state.user.email || '';
        this.profile.phone = '555-123-4567';
        this.profile.address = '123 Eco Street, Green City, 12345';
        this.profile.preferredRegions = ['Europe', 'Asia'];
        this.profile.interests = ['Wildlife Conservation', 'Cultural Experiences'];
        this.profile.emailNotifications = true;
      }
    } catch (error) {
      this.error = 'Failed to load profile information. Please try again later.';
      console.error(error);
    } finally {
      this.loading = false;
    }
  },
  methods: {
    async updateProfile() {
      this.updating = true;
      
      try {
        // In a real app, this would be an API call to update profile
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Update store with new name
        this.$store.commit('setUser', {
          ...this.$store.state.user,
          name: this.profile.name,
          email: this.profile.email
        });
        
        alert('Profile updated successfully!');
      } catch (error) {
        this.error = 'Failed to update profile. Please try again.';
        console.error(error);
      } finally {
        this.updating = false;
      }
    },
    
    async savePreferences() {
      this.updating = true;
      
      try {
        // In a real app, this would be an API call to save preferences
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        alert('Preferences saved successfully!');
      } catch (error) {
        this.error = 'Failed to save preferences. Please try again.';
        console.error(error);
      } finally {
        this.updating = false;
      }
    },
    
    async changePassword() {
      if (this.passwordChange.newPassword !== this.passwordChange.confirmPassword) {
        this.error = 'New passwords do not match';
        return;
      }
      
      this.updating = true;
      
      try {
        // In a real app, this would be an API call to change password
        await new Promise(resolve => setTimeout(resolve, 1000));
        
        // Reset form
        this.passwordChange.currentPassword = '';
        this.passwordChange.newPassword = '';
        this.passwordChange.confirmPassword = '';
        
        alert('Password changed successfully!');
      } catch (error) {
        this.error = 'Failed to change password. Please try again.';
        console.error(error);
      } finally {
        this.updating = false;
      }
    }
  }
};
</script>

<style scoped>
.profile-container {
  max-width: 800px;
  margin: 0 auto;
  padding: 20px;
}

.profile-content {
  display: flex;
  flex-direction: column;
  gap: 30px;
}

.profile-section {
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

input[type="text"],
input[type="email"],
input[type="tel"],
input[type="password"],
textarea {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
  font-size: 16px;
}

textarea {
  resize: vertical;
}

.checkbox-group {
  display: grid;
  grid-template-columns: repeat(auto-fill, minmax(200px, 1fr));
  gap: 10px;
}

.checkbox-item {
  display: flex;
  align-items: center;
}

.checkbox-item input[type="checkbox"] {
  margin-right: 8px;
}

.toggle-switch {
  display: flex;
  align-items: center;
}

.toggle-switch input {
  height: 0;
  width: 0;
  visibility: hidden;
  position: absolute;
}

.toggle-switch label {
  cursor: pointer;
  width: 50px;
  height: 25px;
  background: #ddd;
  display: block;
  border-radius: 25px;
  position: relative;
  margin-right: 10px;
}

.toggle-switch label:after {
  content: '';
  position: absolute;
  top: 3px;
  left: 3px;
  width: 19px;
  height: 19px;
  background: #fff;
  border-radius: 19px;
  transition: 0.3s;
}

.toggle-switch input:checked + label {
  background: #4CAF50;
}

.toggle-switch input:checked + label:after {
  left: calc(100% - 3px);
  transform: translateX(-100%);
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  padding: 12px 20px;
  border: none;
  border-radius: 4px;
  cursor: pointer;
  font-size: 16px;
}

.btn-primary:hover {
  background-color: #45a049;
}

.btn-primary:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.not-authenticated {
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