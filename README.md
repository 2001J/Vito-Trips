# VitoTrips - Sustainable Travel Platform

VitoTrips is a web-based travel platform focused on sustainable tourism, connecting travelers with eco-friendly tours and volunteer opportunities that benefit local communities and preserve natural environments.

## Features

- **Sustainable Tours**: Browse and book eco-friendly travel experiences
- **Volunteer Opportunities**: Find and participate in meaningful volunteer projects
- **Secure Payments**: Integrated with Stripe for secure payment processing
- **User Profiles**: Manage bookings, preferences, and payment methods
- **Tour Operator Dashboard**: For tour providers to manage their offerings
- **Admin Dashboard**: For platform administrators to oversee operations

## Technology Stack

### Backend
- Java 21
- Spring Boot 3.2
- Spring Security with JWT
- Spring Data JPA
- PostgreSQL
- Redis for caching and session management
- Stripe API for payment processing

### Frontend
- Vue.js 2
- Vuex for state management
- Vue Router for client-side routing
- Axios for API communication
- Stripe.js for payment form integration

### DevOps
- Docker for containerization
- GitHub Actions for CI/CD
- SonarCloud for code quality analysis

## API Versioning

All API endpoints follow the versioned pattern:
```
/api/v1/{resource}
```

For example:
- Authentication: `/api/v1/auth/login`, `/api/v1/auth/register`
- Users: `/api/v1/users`
- Tours: `/api/v1/tours`
- Bookings: `/api/v1/bookings`

This versioning strategy allows for future API changes without breaking existing clients.

## Getting Started

### Prerequisites
- Java 21
- Maven
- Node.js and npm
- PostgreSQL
- Redis
- Docker (optional)

### Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/vitotrips.git
   cd vitotrips
   ```

2. Set up the database:
   ```bash
   # Create PostgreSQL database
   createdb vitotrips
   ```

3. Configure environment variables:
   - Copy `.env.example` to `.env` and update the values
   - Copy `src/main/resources/application.properties.example` to `src/main/resources/application.properties` and update with your database credentials

4. Build and run the backend:
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

5. Install frontend dependencies and run the development server:
   ```bash
   npm install
   npm run dev
   ```

6. Access the application at `http://localhost:8081`

### Docker Setup

Alternatively, you can use Docker to run the application:

```bash
./docker-run.sh
```

This will build and start all necessary containers (app, PostgreSQL, Redis).

## API Documentation

API documentation is available at `/swagger-ui.html` when the application is running.

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Acknowledgments

- Thanks to all contributors who have helped shape this project
- Special thanks to the sustainable tourism community for inspiration

