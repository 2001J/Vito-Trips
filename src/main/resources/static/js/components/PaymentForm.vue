<template>
  <div class="payment-form">
    <h2>Payment Details</h2>
    <div v-if="loading" class="loading">
      <div class="spinner"></div>
      <p>Processing payment...</p>
    </div>
    <div v-else-if="error" class="error-message">
      {{ error }}
    </div>
    <div v-else-if="paymentSuccess" class="success-message">
      <h3>Payment Successful!</h3>
      <p>Your booking has been confirmed.</p>
      <button @click="redirectToBooking" class="btn btn-primary">View Booking</button>
    </div>
    <form v-else @submit.prevent="submitPayment">
      <div class="form-group">
        <label for="amount">Amount</label>
        <div class="amount-display">{{ formatCurrency(amount) }}</div>
      </div>
      
      <div class="form-group">
        <label for="card-element">Credit or debit card</label>
        <div id="card-element" class="form-control"></div>
        <div id="card-errors" role="alert" class="error-text"></div>
      </div>
      
      <div class="form-group">
        <label>
          <input type="checkbox" v-model="saveCard" />
          Save card for future payments
        </label>
      </div>
      
      <button type="submit" class="btn btn-primary" :disabled="!stripe || !elements || loading">
        Pay {{ formatCurrency(amount) }}
      </button>
    </form>
  </div>
</template>

<script>
export default {
  name: 'PaymentForm',
  props: {
    bookingId: {
      type: Number,
      required: true
    },
    amount: {
      type: Number,
      required: true
    },
    currency: {
      type: String,
      default: 'USD'
    }
  },
  data() {
    return {
      stripe: null,
      elements: null,
      cardElement: null,
      loading: false,
      error: null,
      paymentSuccess: false,
      saveCard: false,
      clientSecret: null
    };
  },
  mounted() {
    this.initStripe();
  },
  methods: {
    async initStripe() {
      try {
        // Load Stripe.js
        this.stripe = await Stripe(process.env.VUE_APP_STRIPE_PUBLIC_KEY);
        this.elements = this.stripe.elements();
        
        // Create card element
        this.cardElement = this.elements.create('card', {
          style: {
            base: {
              color: '#32325d',
              fontFamily: '"Helvetica Neue", Helvetica, sans-serif',
              fontSmoothing: 'antialiased',
              fontSize: '16px',
              '::placeholder': {
                color: '#aab7c4'
              }
            },
            invalid: {
              color: '#fa755a',
              iconColor: '#fa755a'
            }
          }
        });
        
        // Mount card element
        this.cardElement.mount('#card-element');
        
        // Handle validation errors
        this.cardElement.on('change', (event) => {
          const displayError = document.getElementById('card-errors');
          if (event.error) {
            displayError.textContent = event.error.message;
          } else {
            displayError.textContent = '';
          }
        });
        
        // Create payment intent
        await this.createPaymentIntent();
      } catch (error) {
        console.error('Error initializing Stripe:', error);
        this.error = 'Failed to initialize payment system. Please try again later.';
      }
    },
    
    async createPaymentIntent() {
      try {
        this.loading = true;
        const response = await fetch('/api/payments', {
          method: 'POST',
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${localStorage.getItem('token')}`
          },
          body: JSON.stringify({
            bookingId: this.bookingId,
            amount: this.amount,
            paymentMethod: 'card',
            savePaymentMethod: this.saveCard
          })
        });
        
        const data = await response.json();
        
        if (!response.ok) {
          throw new Error(data.message || 'Failed to create payment intent');
        }
        
        this.clientSecret = data.clientSecret;
      } catch (error) {
        console.error('Error creating payment intent:', error);
        this.error = error.message || 'Failed to create payment intent';
      } finally {
        this.loading = false;
      }
    },
    
    async submitPayment() {
      if (!this.stripe || !this.elements || !this.clientSecret) {
        this.error = 'Payment system is not ready. Please try again.';
        return;
      }
      
      try {
        this.loading = true;
        this.error = null;
        
        const result = await this.stripe.confirmCardPayment(this.clientSecret, {
          payment_method: {
            card: this.cardElement,
            billing_details: {
              // You can collect billing details here if needed
            }
          },
          setup_future_usage: this.saveCard ? 'off_session' : undefined
        });
        
        if (result.error) {
          // Show error to customer
          this.error = result.error.message;
        } else {
          // Payment succeeded
          if (result.paymentIntent.status === 'succeeded') {
            this.paymentSuccess = true;
            this.$emit('payment-success', result.paymentIntent);
          }
        }
      } catch (error) {
        console.error('Payment error:', error);
        this.error = 'An unexpected error occurred. Please try again.';
      } finally {
        this.loading = false;
      }
    },
    
    redirectToBooking() {
      this.$router.push(`/bookings/${this.bookingId}`);
    },
    
    formatCurrency(amount) {
      return new Intl.NumberFormat('en-US', {
        style: 'currency',
        currency: this.currency
      }).format(amount);
    }
  }
};
</script>

<style scoped>
.payment-form {
  max-width: 500px;
  margin: 0 auto;
  padding: 20px;
  background-color: #fff;
  border-radius: 8px;
  box-shadow: 0 2px 10px rgba(0, 0, 0, 0.1);
}

h2 {
  margin-bottom: 20px;
  color: #333;
  text-align: center;
}

.form-group {
  margin-bottom: 20px;
}

label {
  display: block;
  margin-bottom: 8px;
  font-weight: 500;
  color: #333;
}

.form-control {
  width: 100%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 4px;
  background-color: #fff;
  transition: border-color 0.2s;
}

.form-control:focus {
  border-color: #4a90e2;
  outline: none;
}

.amount-display {
  font-size: 24px;
  font-weight: bold;
  color: #333;
  padding: 10px;
  background-color: #f9f9f9;
  border-radius: 4px;
  text-align: center;
}

.btn {
  display: block;
  width: 100%;
  padding: 12px;
  border: none;
  border-radius: 4px;
  font-size: 16px;
  font-weight: 500;
  cursor: pointer;
  transition: background-color 0.2s;
}

.btn-primary {
  background-color: #4a90e2;
  color: white;
}

.btn-primary:hover {
  background-color: #3a80d2;
}

.btn:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.error-text {
  color: #e74c3c;
  margin-top: 5px;
  font-size: 14px;
}

.error-message {
  padding: 15px;
  background-color: #fdecea;
  color: #e74c3c;
  border-radius: 4px;
  margin-bottom: 20px;
}

.success-message {
  padding: 20px;
  background-color: #eafaf1;
  color: #27ae60;
  border-radius: 4px;
  text-align: center;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 30px;
}

.spinner {
  border: 4px solid rgba(0, 0, 0, 0.1);
  border-radius: 50%;
  border-top: 4px solid #4a90e2;
  width: 40px;
  height: 40px;
  animation: spin 1s linear infinite;
  margin-bottom: 15px;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}
</style> 