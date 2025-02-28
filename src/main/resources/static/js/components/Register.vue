<template>
  <div class="register-container">
    <h1>Create an Account</h1>
    <form @submit.prevent="register">
      <div class="form-group">
        <label for="name">Full Name</label>
        <input type="text" id="name" v-model="user.name" required>
      </div>
      <div class="form-group">
        <label for="email">Email</label>
        <input type="email" id="email" v-model="user.email" required>
      </div>
      <div class="form-group">
        <label for="password">Password</label>
        <input type="password" id="password" v-model="user.password" required>
      </div>
      <div class="form-group">
        <label for="confirmPassword">Confirm Password</label>
        <input type="password" id="confirmPassword" v-model="user.confirmPassword" required>
      </div>
      <button type="submit" class="btn-primary" :disabled="loading">Register</button>
    </form>
    <p v-if="error" class="error-message">{{ error }}</p>
    <p>Already have an account? <router-link to="/login">Login here</router-link></p>
  </div>
</template>

<script>
export default {
  name: 'Register',
  data() {
    return {
      user: {
        name: '',
        email: '',
        password: '',
        confirmPassword: ''
      },
      loading: false,
      error: null
    };
  },
  methods: {
    async register() {
      if (this.user.password !== this.user.confirmPassword) {
        this.error = 'Passwords do not match';
        return;
      }
      
      this.loading = true;
      this.error = null;
      
      try {
        await this.$store.dispatch('register', {
          name: this.user.name,
          email: this.user.email,
          password: this.user.password
        });
        this.$router.push('/');
      } catch (error) {
        this.error = error.message;
      } finally {
        this.loading = false;
      }
    }
  }
};
</script>

<style scoped>
.register-container {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
}

.form-group {
  margin-bottom: 15px;
}

label {
  display: block;
  margin-bottom: 5px;
  font-weight: bold;
}

input {
  width: 100%;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.btn-primary {
  background-color: #4CAF50;
  color: white;
  padding: 10px 15px;
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

.error-message {
  color: red;
  margin-top: 10px;
}
</style> 