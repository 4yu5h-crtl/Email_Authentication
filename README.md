# Email OTP Authentication System

A secure user registration system with email OTP verification built using Spring Boot and modern web technologies.

## Overview

This project implements a user registration system with the following features:
- User registration with name, age, email, and password
- Email OTP verification
- Secure password storage using BCrypt encryption
- Modern, responsive UI
- Session management
- RESTful API endpoints

## Tech Stack

### Backend
- **Java 17**
- **Spring Boot 3.2.3**
- **Spring Security**
- **Spring Data JPA**
- **Spring Mail**
- **MySQL Database**
- **Maven** (Build Tool)

### Frontend
- **HTML5**
- **CSS3**
- **JavaScript (ES6+)**
- **LocalStorage** for session management

## Prerequisites

Before running this project, make sure you have the following installed:

1. **Java Development Kit (JDK) 17**
   - Download from: [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) or [OpenJDK](https://adoptium.net/)
   - Set JAVA_HOME environment variable

2. **MySQL Server**
   - Download from: [MySQL Community Server](https://dev.mysql.com/downloads/mysql/)
   - Create a database named `email_auth`

3. **Maven**
   - Download from: [Apache Maven](https://maven.apache.org/download.cgi)
   - Add Maven to your system's PATH

4. **Gmail Account**
   - For sending OTP emails
   - Enable 2-Step Verification
   - Generate App Password

## Project Setup

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/email-auth.git
   cd email-auth
   ```

2. **Configure Database**
   - Open `src/main/resources/application.properties`
   - Update MySQL credentials:
     ```properties
     spring.datasource.username=your_mysql_username
     spring.datasource.password=your_mysql_password
     ```

3. **Configure Email**
   - In `application.properties`, update Gmail settings:
     ```properties
     spring.mail.username=your-email@gmail.com
     spring.mail.password=your-app-specific-password
     ```
   - To get App Password:
     1. Go to Google Account settings
     2. Security → 2-Step Verification → App passwords
     3. Generate new app password for "Mail"

4. **Build the Project**
   ```bash
   mvn clean install
   ```

5. **Run the Application**
   ```bash
   mvn spring-boot:run
   ```

## Testing the Application

1. **Start the Application**
   - The application will run on `http://localhost:8080`

2. **Registration Process**
   - Open `http://localhost:8080` in your browser
   - Fill in the registration form:
     - Full Name
     - Age
     - Email
     - Password
   - Click "Register"
   - Check your email for OTP

3. **OTP Verification**
   - Enter the OTP received in your email
   - Click "Verify OTP"
   - Upon successful verification, you'll be redirected to the welcome page

4. **Welcome Page**
   - View your registered email
   - Use the logout button to return to registration

## API Endpoints

### 1. Register User
```
POST /api/auth/register
Content-Type: application/json

{
    "name": "John Doe",
    "age": 25,
    "email": "john@example.com",
    "password": "yourpassword"
}
```

### 2. Verify OTP
```
POST /api/auth/verify-otp
Content-Type: application/json

{
    "email": "john@example.com",
    "otp": "123456"
}
```

## Security Features

- Password encryption using BCrypt
- Email verification
- OTP expiration (5 minutes)
- CSRF protection disabled for API endpoints
- Stateless session management
- Input validation
- Secure password storage

## Project Structure

```
email-auth/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── example/
│   │   │           └── emailauth/
│   │   │               ├── config/
│   │   │               ├── controller/
│   │   │               ├── model/
│   │   │               ├── repository/
│   │   │               ├── service/
│   │   │               └── EmailAuthApplication.java
│   │   └── resources/
│   │       ├── static/
│   │       │   ├── index.html
│   │       │   └── welcome.html
│   │       └── application.properties
└── pom.xml
```

## Contributing

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Spring Boot team for the excellent framework
- MySQL team for the database
- All contributors who help improve this project 
