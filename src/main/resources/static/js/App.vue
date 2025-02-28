<template>
  <div id="app">
    <header>
      <nav class="navbar">
        <div class="navbar-brand">
          <router-link to="/" class="logo">
            <img src="/img/logo.png" alt="VitoTrips Logo" />
            <span>VitoTrips</span>
          </router-link>
        </div>
        
        <div class="navbar-menu">
          <div class="navbar-start">
            <router-link to="/" class="navbar-item">Home</router-link>
            <router-link to="/tours" class="navbar-item">Tours</router-link>
            <router-link to="/volunteer" class="navbar-item" v-if="isAuthenticated">Volunteer</router-link>
          </div>
          
          <div class="navbar-end">
            <template v-if="isAuthenticated">
              <div class="navbar-item has-dropdown" :class="{ 'is-active': showDropdown }" @click="toggleDropdown">
                <a class="navbar-link">
                  {{ currentUser ? currentUser.name : 'Account' }}
                </a>
                
                <div class="navbar-dropdown">
                  <router-link to="/profile" class="navbar-item">Profile</router-link>
                  <router-link to="/bookings" class="navbar-item">My Bookings</router-link>
                  <router-link to="/admin" class="navbar-item" v-if="isAdmin">Admin Dashboard</router-link>
                  <router-link to="/operator" class="navbar-item" v-if="isOperator">Operator Dashboard</router-link>
                  <hr class="navbar-divider">
                  <a class="navbar-item" @click="logout">Logout</a>
                </div>
              </div>
            </template>
            <template v-else>
              <router-link to="/login" class="navbar-item">Login</router-link>
              <router-link to="/register" class="navbar-item">Register</router-link>
            </template>
          </div>
        </div>
      </nav>
    </header>
    
    <main>
      <div class="container">
        <div v-if="error" class="error-notification">
          {{ error }}
          <button class="close-button" @click="clearError">&times;</button>
        </div>
        
        <div v-if="loading" class="loading-overlay">
          <div class="spinner"></div>
          <p>Loading...</p>
        </div>
        
        <transition name="fade" mode="out-in">
          <router-view></router-view>
        </transition>
      </div>
    </main>
    
    <footer>
      <div class="container">
        <div class="footer-content">
          <div class="footer-section">
            <h3>About VitoTrips</h3>
            <p>Sustainable travel experiences that benefit local communities and preserve natural environments.</p>
          </div>
          
          <div class="footer-section">
            <h3>Quick Links</h3>
            <ul>
              <li><router-link to="/">Home</router-link></li>
              <li><router-link to="/tours">Tours</router-link></li>
              <li><router-link to="/volunteer">Volunteer</router-link></li>
              <li><a href="/about">About Us</a></li>
              <li><a href="/contact">Contact</a></li>
            </ul>
          </div>
          
          <div class="footer-section">
            <h3>Connect With Us</h3>
            <div class="social-links">
              <a href="#" class="social-link"><i class="fab fa-facebook"></i></a>
              <a href="#" class="social-link"><i class="fab fa-twitter"></i></a>
              <a href="#" class="social-link"><i class="fab fa-instagram"></i></a>
              <a href="#" class="social-link"><i class="fab fa-linkedin"></i></a>
            </div>
          </div>
        </div>
        
        <div class="footer-bottom">
          <p>&copy; {{ new Date().getFullYear() }} VitoTrips. All rights reserved.</p>
        </div>
      </div>
    </footer>
  </div>
</template>

<script>
import { mapGetters, mapState, mapActions, mapMutations } from 'vuex';

export default {
  name: 'App',
  data() {
    return {
      showDropdown: false
    };
  },
  computed: {
    ...mapState(['loading', 'error']),
    ...mapGetters(['isAuthenticated', 'currentUser', 'isAdmin', 'isOperator'])
  },
  methods: {
    ...mapActions(['logout']),
    ...mapMutations(['setError']),
    
    toggleDropdown() {
      this.showDropdown = !this.showDropdown;
    },
    
    clearError() {
      this.setError(null);
    }
  },
  created() {
    // Close dropdown when clicking outside
    document.addEventListener('click', (e) => {
      const dropdown = this.$el.querySelector('.has-dropdown');
      if (dropdown && !dropdown.contains(e.target)) {
        this.showDropdown = false;
      }
    });
  },
  mounted() {
    // Add event listener for ESC key to close dropdown
    document.addEventListener('keydown', (e) => {
      if (e.key === 'Escape') {
        this.showDropdown = false;
      }
    });
  }
};
</script>

<style>
/* Global styles */
:root {
  --primary-color: #4a90e2;
  --secondary-color: #27ae60;
  --accent-color: #f39c12;
  --text-color: #333333;
  --light-gray: #f5f5f5;
  --medium-gray: #e0e0e0;
  --dark-gray: #757575;
  --danger-color: #e74c3c;
  --success-color: #2ecc71;
}

* {
  box-sizing: border-box;
  margin: 0;
  padding: 0;
}

body {
  font-family: 'Roboto', 'Helvetica Neue', Arial, sans-serif;
  font-size: 16px;
  line-height: 1.6;
  color: var(--text-color);
  background-color: #ffffff;
}

.container {
  width: 100%;
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

a {
  color: var(--primary-color);
  text-decoration: none;
  transition: color 0.2s;
}

a:hover {
  color: #3a80d2;
}

button {
  cursor: pointer;
}

/* Navbar styles */
.navbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  background-color: #ffffff;
  box-shadow: 0 2px 5px rgba(0, 0, 0, 0.1);
}

.navbar-brand {
  display: flex;
  align-items: center;
}

.logo {
  display: flex;
  align-items: center;
  font-size: 24px;
  font-weight: 700;
  color: var(--primary-color);
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.navbar-menu {
  display: flex;
  justify-content: space-between;
  flex-grow: 1;
  margin-left: 30px;
}

.navbar-start, .navbar-end {
  display: flex;
  align-items: center;
}

.navbar-item {
  padding: 10px 15px;
  color: var(--text-color);
  font-weight: 500;
}

.navbar-item.router-link-exact-active {
  color: var(--primary-color);
}

.navbar-item:hover {
  color: var(--primary-color);
}

.has-dropdown {
  position: relative;
}

.navbar-link {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.navbar-dropdown {
  display: none;
  position: absolute;
  right: 0;
  top: 100%;
  background-color: #ffffff;
  min-width: 200px;
  box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
  z-index: 1;
  border-radius: 4px;
  overflow: hidden;
}

.has-dropdown.is-active .navbar-dropdown {
  display: block;
}

.navbar-divider {
  height: 1px;
  background-color: var(--medium-gray);
  margin: 5px 0;
  border: none;
}

/* Main content styles */
main {
  min-height: calc(100vh - 70px - 250px); /* Viewport height minus header and footer */
  padding: 30px 0;
}

.error-notification {
  background-color: #fdecea;
  color: var(--danger-color);
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
  position: relative;
}

.close-button {
  position: absolute;
  top: 10px;
  right: 10px;
  background: none;
  border: none;
  font-size: 20px;
  color: var(--danger-color);
}

.loading-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.8);
  display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center;
  z-index: 9999;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid var(--primary-color);
  width: 50px;
  height: 50px;
  animation: spin 1s linear infinite;
  margin-bottom: 20px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* Transition animations */
.fade-enter-active, .fade-leave-active {
  transition: opacity 0.3s;
}

.fade-enter, .fade-leave-to {
  opacity: 0;
}

/* Footer styles */
footer {
  background-color: #2c3e50;
  color: #ffffff;
  padding: 50px 0 20px;
}

.footer-content {
  display: flex;
  flex-wrap: wrap;
  justify-content: space-between;
  margin-bottom: 30px;
}

.footer-section {
  flex: 1;
  min-width: 250px;
  margin-bottom: 30px;
  padding-right: 30px;
}

.footer-section h3 {
  font-size: 18px;
  margin-bottom: 15px;
  color: #ffffff;
}

.footer-section p {
  color: #ecf0f1;
  line-height: 1.6;
}

.footer-section ul {
  list-style: none;
}

.footer-section ul li {
  margin-bottom: 10px;
}

.footer-section ul li a {
  color: #ecf0f1;
}

.footer-section ul li a:hover {
  color: var(--accent-color);
}

.social-links {
  display: flex;
  gap: 15px;
}

.social-link {
  display: flex;
  align-items: center;
  justify-content: center;
  width: 40px;
  height: 40px;
  background-color: rgba(255, 255, 255, 0.1);
  border-radius: 50%;
  color: #ffffff;
  transition: background-color 0.2s;
}

.social-link:hover {
  background-color: var(--primary-color);
  color: #ffffff;
}

.footer-bottom {
  text-align: center;
  padding-top: 20px;
  border-top: 1px solid rgba(255, 255, 255, 0.1);
}

/* Responsive styles */
@media (max-width: 768px) {
  .navbar-menu {
    flex-direction: column;
    margin-left: 0;
  }
  
  .navbar {
    flex-wrap: wrap;
  }
  
  .navbar-start, .navbar-end {
    width: 100%;
    justify-content: center;
    margin-top: 15px;
  }
  
  .footer-section {
    flex: 100%;
    padding-right: 0;
  }
}
</style> 