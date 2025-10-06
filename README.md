# Himmerland Booking System

Third semester group project for AAU

## Frontend

- **Framework**: React with TypeScript.
- **Build Tool**: Vite.
- **Type**: Single Page Application (SPA) with Client-Side Rendering (CSR).
- **CSS Framework**: Bootstrap v5.
  - **Documentation**: [Bootstrap v5 Documentation](https://getbootstrap.com/docs/5.3/getting-started/introduction/).
- **Code Formatting**: Use the Prettier extension in Visual Studio Code for formatting TypeScript/React files.
  - **Installation**: Install Prettier and use it frequently.
  - **Shortcut**: `ALT + SHIFT + F` (Windows).
- **React Component Creation**: Use the ES7+ extension for quickly creating new React components.
  - **Tip**: Write `rafce` when creating a component.
- **Browser Extension**: React Developer Tools.
- **Linting**: ESLint.
- **Testing**: Vitest + JSDOM for react components.

## Backend

- **Framework**: Spring Boot with Java.
- **Build Tool**: Gradle wrapper.
- **Development Database**: JPA interface with SQLite.
  - **Extension**: Browse database from within Visual Studio Code using SQLite Viewer extension.
  - **Application**: Or browse database externally using [DB Broswer (SQLite)](https://sqlitebrowser.org/dl/).
- **API Documentation**: OpenAPI available at [http://localhost:8080/swagger-ui/index.html](http://localhost:8080/swagger-ui/index.html).
- **Security**: Spring Security with Bcrypt password hashing.
- **User Authentication**: Managed through a JWT system.
- **Session Management**: Utilizes JWT for managing user sessions.
- **Testing**: Spring Boot Test.
  - **Unit Testing**: JUnit Jupiter for service methods.
  - **Integration Testing**: API endpoints tested using MockMvc
- **Postman**: TBD

## Setup Instructions

### Prerequisites

Must have installed:
  - Node.js
  - Java version 17
For Java, you probably have a newer version installed, but you need to have version 17 installed as well.
Link to download Java version 17 (https://www.oracle.com/java/technologies/javase/jdk17-archive-downloads.html).

### Development Setup
If using VSCode, install recommended extensions on initial pull.  

To setup dependencies, open the terminal and run:
```bash
npm run setup
```
Once setup

To run the frontend:
```bash
npm run frontend
```
To run the backend:
```bash
npm run backend
```
To run both:
```bash
npm start
```
To run tests for frontend:
```bash
npm run test:frontend
```
To run tests for backend:
```bash
npm run test:backend
```
To run tests for both:
```bash
npm run test
```
### Production Setup
TBD
