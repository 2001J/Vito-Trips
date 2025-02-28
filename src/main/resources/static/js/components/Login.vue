<template>
  <div class="login-container">
    <div class="auth-card">
      <h2>Welcome Back</h2>
      <p class="auth-subtitle">Sign in to your VitoTrips account</p>
      
      <div v-if="error" class="error-message">
        {{ error }}
      </div>
      
      <form @submit.prevent="login">
        <div class="form-group">
          <label for="email">Email</label>
          <input 
            type="email" 
            id="email" 
            v-model="credentials.email" 
            class="form-control" 
            placeholder="Enter your email"
            required
            :disabled="loading"
          >
        </div>
        
        <div class="form-group">
          <label for="password">Password</label>
          <div class="password-input">
            <input 
              :type="showPassword ? 'text' : 'password'" 
              id="password" 
              v-model="credentials.password" 
              class="form-control" 
              placeholder="Enter your password"
              required
              :disabled="loading"
            >
            <button 
              type="button" 
              class="toggle-password" 
              @click="togglePassword"
              :disabled="loading"
            >
              <i :class="showPassword ? 'fas fa-eye-slash' : 'fas fa-eye'"></i>
            </button>
          </div>
        </div>
        
        <div class="form-options">
          <label class="remember-me">
            <input type="checkbox" v-model="rememberMe" :disabled="loading">
            <span>Remember me</span>
          </label>
          
          <router-link to="/forgot-password" class="forgot-password">Forgot password?</router-link>
        </div>
        
        <button type="submit" class="btn btn-primary btn-block" :disabled="loading">
          <span v-if="loading" class="spinner-sm"></span>
          <span v-else>Sign In</span>
        </button>
      </form>
      
      <div class="auth-divider">
        <span>OR</span>
      </div>
      
      <div class="social-login">
        <button class="btn btn-social btn-google" :disabled="loading">
          <i class="fab fa-google"></i>
          Sign in with Google
        </button>
        
        <button class="btn btn-social btn-facebook" :disabled="loading">
          <i class="fab fa-facebook-f"></i>
          Sign in with Facebook
        </button>
      </div>
      
      <div class="auth-footer">
        <p>Don't have an account? <router-link to="/register">Sign up</router-link></p>
      </div>
    </div>
  </div>
</template>

<script>
import { mapActions } from 'vuex';

export default {
  name: 'Login',
  data() {
    return {
      credentials: {
        email: '',
        password: ''
      },
      rememberMe: false,
      showPassword: false,
      loading: false,
      error: null
    };
  },
  methods: {
    ...mapActions(['login']),
    
    togglePassword() {
      this.showPassword = !this.showPassword;
    },
    
    async handleLogin() {
      this.loading = true;
      this.error = null;
      
      try {
        await this.login(this.credentials);
        
        // Store remember me preference
        if (this.rememberMe) {
          localStorage.setItem('rememberMe', 'true');
        } else {
          localStorage.removeItem('rememberMe');
        }
        
        // Redirect to intended destination or home
        const redirectPath = this.$route.query.redirect || '/';
        this.$router.push(redirectPath);
      } catch (error) {
        this.error = error.message || 'Failed to sign in. Please check your credentials.';
      } finally {
        this.loading = false;
      }
    }
  },
  created() {
    // Check if user was remembered
    const rememberMe = localStorage.getItem('rememberMe');
    if (rememberMe) {
      this.rememberMe = true;
    }
    
    // Clear any previous errors
    this.error = null;
  }
};
</script>

<style scoped>
.login-container {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 80vh;
  padding: 40px 20px;
}

.auth-card {
  width: 100%;
  max-width: 450px;
  background-color: #ffffff;
  border-radius: 8px;
  box-shadow: 0 5px 20px rgba(0, 0, 0, 0.1);
  padding: 40px;
}

h2 {
  font-size: 28px;
  color: var(--text-color);
  margin-bottom: 10px;
  text-align: center;
}

.auth-subtitle {
  color: var(--dark-gray);
  text-align: center;
  margin-bottom: 30px;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: var(--text-color);
}

.form-control {
  width: 100%;
  padding: 12px 15px;
  border: 1px solid var(--medium-gray);
  border-radius: 4px;
  font-size: 16px;
  transition: border-color 0.2s;
}

.form-control:focus {
  border-color: var(--primary-color);
  outline: none;
}

.password-input {
  position: relative;
}

.toggle-password {
  position: absolute;
  right: 15px;
  top: 50%;
  transform: translateY(-50%);
  background: none;
  border: none;
  color: var(--dark-gray);
  cursor: pointer;
}

.form-options {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.remember-me {
  display: flex;
  align-items: center;
  cursor: pointer;
}

.remember-me input {
  margin-right: 8px;
}

.forgot-password {
  color: var(--primary-color);
  font-size: 14px;
}

.btn-block {
  width: 100%;
  margin-bottom: 20px;
}

.auth-divider {
  position: relative;
  text-align: center;
  margin: 20px 0;
}

.auth-divider::before {
  content: '';
  position: absolute;
  top: 50%;
  left: 0;
  right: 0;
  height: 1px;
  background-color: var(--medium-gray);
}

.auth-divider span {
  position: relative;
  background-color: #ffffff;
  padding: 0 15px;
  color: var(--dark-gray);
  font-size: 14px;
}

.social-login {
  display: flex;
  flex-direction: column;
  gap: 15px;
  margin-bottom: 20px;
}

.btn-social {
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 12px;
  border-radius: 4px;
  font-weight: 500;
  border: none;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-social i {
  margin-right: 10px;
}

.btn-google {
  background-color: #ffffff;
  color: #757575;
  border: 1px solid #dddddd;
}

.btn-google:hover {
  background-color: #f5f5f5;
}

.btn-facebook {
  background-color: #3b5998;
  color: #ffffff;
}

.btn-facebook:hover {
  background-color: #344e86;
}

.auth-footer {
  text-align: center;
  color: var(--dark-gray);
}

.auth-footer a {
  color: var(--primary-color);
  font-weight: 500;
}

.error-message {
  background-color: #fdecea;
  color: var(--danger-color);
  padding: 15px;
  border-radius: 4px;
  margin-bottom: 20px;
}

.spinner-sm {
  display: inline-block;
  width: 20px;
  height: 20px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: #ffffff;
  animation: spin 1s linear infinite;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

@media (max-width: 480px) {
  .auth-card {
    padding: 30px 20px;
  }
  
  .form-options {
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;
  }
  
  .forgot-password {
    align-self: flex-end;
  }
}
</style> 