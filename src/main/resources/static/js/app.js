// Use global Vue, VueRouter, and Vuex from CDN
// import Vue from 'vue';
// import VueRouter from 'vue-router';
// import Vuex from 'vuex';
// import App from './App.vue';

// Simple component definitions instead of imports
const Home = {
  template: `
    <div class="home">
      <h1>Welcome to VitoTrips</h1>
      <p>Discover sustainable travel experiences that benefit local communities and preserve natural environments.</p>
      <div class="cta">
        <router-link to="/tours" class="btn btn-primary">Browse Tours</router-link>
      </div>
    </div>
  `
};

const Login = {
  data() {
    return {
      email: '',
      password: '',
      error: null
    };
  },
  computed: {
    registrationSuccess() {
      return this.$store.state.registrationSuccess;
    }
  },
  template: `
    <div class="login">
      <h1>Login</h1>
      <div v-if="registrationSuccess" class="success-message">
        Registration successful! Please login with your credentials.
      </div>
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="email" required>
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required>
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <button type="submit" class="btn btn-primary">Login</button>
      </form>
      <p>Don't have an account? <router-link to="/register">Register</router-link></p>
    </div>
  `,
  methods: {
    async login() {
      try {
        await this.$store.dispatch('login', {
          email: this.email,
          password: this.password
        });
        this.$router.push('/');
      } catch (error) {
        this.error = error.message;
      }
    }
  },
  mounted() {
    // Clear registration success message when component is destroyed
    this.$store.commit('setRegistrationSuccess', false);
  }
};

const Register = {
  data() {
    return {
      name: '',
      email: '',
      password: '',
      role: 'ROLE_TRAVELER', // Default role
      error: null
    };
  },
  template: `
    <div class="register">
      <h1>Register</h1>
      <form @submit.prevent="register">
        <div class="form-group">
          <label for="name">Name</label>
          <input type="text" id="name" v-model="name" required>
        </div>
        <div class="form-group">
          <label for="email">Email</label>
          <input type="email" id="email" v-model="email" required>
        </div>
        <div class="form-group">
          <label for="password">Password</label>
          <input type="password" id="password" v-model="password" required>
        </div>
        <div class="form-group">
          <label for="role">Role</label>
          <select id="role" v-model="role">
            <option value="ROLE_TRAVELER">Traveler</option>
            <option value="ROLE_TOUR_OPERATOR">Tour Operator</option>
          </select>
        </div>
        <div v-if="error" class="error">{{ error }}</div>
        <button type="submit" class="btn btn-primary">Register</button>
      </form>
      <p>Already have an account? <router-link to="/login">Login</router-link></p>
    </div>
  `,
  methods: {
    async register() {
      try {
        await this.$store.dispatch('register', {
          name: this.name,
          email: this.email,
          password: this.password,
          role: this.role
        });
        this.$router.push('/login');
      } catch (error) {
        this.error = error.message;
      }
    }
  }
};

const TourList = {
  template: `
    <div class="tour-list">
      <h1>Available Tours</h1>
      <div v-if="loading">Loading...</div>
      <div v-else-if="error">{{ error }}</div>
      <div v-else class="tours-grid">
        <div v-for="tour in tours" :key="tour.id" class="tour-card">
          <h3>{{ tour.tourName }}</h3>
          <p>{{ tour.description }}</p>
          <router-link :to="'/tours/' + tour.id" class="btn btn-secondary">View Details</router-link>
        </div>
      </div>
    </div>
  `,
  computed: {
    tours() {
      return this.$store.state.tours;
    },
    loading() {
      return this.$store.state.loading;
    },
    error() {
      return this.$store.state.error;
    }
  },
  created() {
    this.$store.dispatch('fetchTours');
  }
};

const Profile = {
  computed: {
    user() {
      return this.$store.state.user;
    },
    isAuthenticated() {
      return !!this.$store.state.token;
    }
  },
  template: `
    <div class="profile">
      <h1>User Profile</h1>
      <div v-if="!isAuthenticated" class="error">
        You must be logged in to view this page.
      </div>
      <div v-else>
        <div class="profile-info">
          <div class="profile-field">
            <strong>Name:</strong> {{ user.name }}
          </div>
          <div class="profile-field">
            <strong>Email:</strong> {{ user.email }}
          </div>
          <div class="profile-field">
            <strong>Role:</strong> {{ user.role }}
          </div>
        </div>
        <div class="profile-actions">
          <button class="btn btn-danger" @click="logout">Logout</button>
        </div>
      </div>
    </div>
  `,
  methods: {
    logout() {
      this.$store.dispatch('logout');
      this.$router.push('/login');
    }
  }
};

// Use plugins
// Vue.use(VueRouter);
// Vue.use(Vuex);

// Create router
const router = new VueRouter({
  mode: 'history',
  routes: [
    { path: '/', component: Home },
    { path: '/login', component: Login },
    { path: '/register', component: Register },
    { path: '/tours', component: TourList },
    { path: '/profile', component: Profile, meta: { requiresAuth: true } }
  ]
});

// Navigation guards
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('token');
  const userRole = localStorage.getItem('userRole');

  if (to.matched.some(record => record.meta.requiresAuth)) {
    if (!token) {
      next({
        path: '/login',
        query: { redirect: to.fullPath }
      });
    } else if (to.matched.some(record => record.meta.requiresAdmin) && userRole !== 'ADMIN') {
      next({ path: '/' });
    } else if (to.matched.some(record => record.meta.requiresOperator) && userRole !== 'TOUR_OPERATOR') {
      next({ path: '/' });
    } else {
      next();
    }
  } else {
    next();
  }
});

// Create store
const store = new Vuex.Store({
  state: {
    user: null,
    token: localStorage.getItem('token') || '',
    tours: [],
    bookings: [],
    loading: false,
    error: null,
    registrationSuccess: false
  },
  mutations: {
    setUser(state, user) {
      state.user = user;
    },
    setToken(state, token) {
      state.token = token;
      localStorage.setItem('token', token);
    },
    setUserRole(state, role) {
      localStorage.setItem('userRole', role);
    },
    clearAuth(state) {
      state.user = null;
      state.token = '';
      localStorage.removeItem('token');
      localStorage.removeItem('userRole');
    },
    setTours(state, tours) {
      state.tours = tours;
    },
    setBookings(state, bookings) {
      state.bookings = bookings;
    },
    setLoading(state, loading) {
      state.loading = loading;
    },
    setError(state, error) {
      state.error = error;
    },
    setRegistrationSuccess(state, value) {
      state.registrationSuccess = value;
    }
  },
  actions: {
    async login({ commit }, credentials) {
      commit('setLoading', true);
      commit('setError', null);

      try {
        // Using URLSearchParams for login since the backend expects form parameters
        const params = new URLSearchParams();
        params.append('email', credentials.email);
        params.append('password', credentials.password);

        // Change to use query parameters in the URL instead of form data
        const response = await fetch(`/api/v1/auth/login?email=${encodeURIComponent(credentials.email)}&password=${encodeURIComponent(credentials.password)}`, {
          method: 'POST',
          headers: {
            'Content-Type': 'application/x-www-form-urlencoded'
          }
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.error || 'Login failed');
        }

        commit('setUser', {
          email: data.email,
          name: data.name,
          role: data.role
        });
        commit('setToken', data.token);
        commit('setUserRole', data.role);

        return data;
      } catch (error) {
        commit('setError', error.message);
        throw error;
      } finally {
        commit('setLoading', false);
      }
    },

    async register({ commit }, userData) {
      commit('setLoading', true);
      commit('setError', null);

      try {
        const response = await fetch('/api/v1/auth/register', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json'
          },
          body: JSON.stringify(userData)
        });

        const data = await response.json();

        if (!response.ok) {
          throw new Error(data.error || 'Registration failed');
        }

        // Don't automatically log in the user
        // Just set a success message that will be displayed on the login page
        commit('setRegistrationSuccess', true);

        return data;
      } catch (error) {
        commit('setError', error.message);
        throw error;
      } finally {
        commit('setLoading', false);
      }
    },

    logout({ commit }) {
      commit('clearAuth');
      router.push('/');
    },

    async fetchTours({ commit, state }) {
      commit('setLoading', true);
      commit('setError', null);

      try {
        const response = await fetch('/api/v1/tours', {
          headers: {
            'Authorization': state.token ? `Bearer ${state.token}` : '',
            'Accept': 'application/json'
          }
        });

        if (!response.ok) {
          // For demo purposes, return mock data if API fails
          commit('setTours', [
            { id: 1, tourName: 'Eco Safari Adventure', description: 'Experience wildlife conservation in action' },
            { id: 2, tourName: 'Coastal Community Project', description: 'Help preserve marine ecosystems' },
            { id: 3, tourName: 'Mountain Village Stay', description: 'Authentic cultural immersion in remote villages' }
          ]);
          return;
        }

        const data = await response.json();
        commit('setTours', data);
      } catch (error) {
        commit('setError', error.message);
        // For demo purposes, return mock data if API fails
        commit('setTours', [
          { id: 1, tourName: 'Eco Safari Adventure', description: 'Experience wildlife conservation in action' },
          { id: 2, tourName: 'Coastal Community Project', description: 'Help preserve marine ecosystems' },
          { id: 3, tourName: 'Mountain Village Stay', description: 'Authentic cultural immersion in remote villages' }
        ]);
      } finally {
        commit('setLoading', false);
      }
    }
  }
});

// Create and mount the Vue instance
const app = new Vue({
  router,
  store,
  template: `
    <div id="app">
      <header>
        <nav>
          <div class="logo">
            <router-link to="/">VitoTrips</router-link>
          </div>
          <div class="nav-links">
            <router-link to="/">Home</router-link>
            <router-link to="/tours">Tours</router-link>
            <template v-if="!isAuthenticated">
              <router-link to="/login">Login</router-link>
              <router-link to="/register">Register</router-link>
            </template>
            <template v-else>
              <router-link to="/profile">My Profile</router-link>
              <a href="#" @click.prevent="logout">Logout</a>
            </template>
          </div>
        </nav>
      </header>
      <main>
        <router-view></router-view>
      </main>
      <footer>
        <p>&copy; 2025 VitoTrips - Sustainable Travel Experiences</p>
      </footer>
    </div>
  `,
  computed: {
    isAuthenticated() {
      return !!this.$store.state.token;
    }
  },
  methods: {
    logout() {
      this.$store.dispatch('logout');
    }
  }
}).$mount('#app'); 