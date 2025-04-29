# Travel Agency Management System (TAMS)

A comprehensive management system for travel agencies to handle travel bookings, itineraries, and customer information.

## Overview

The Travel Agency Management System (TAMS) provides a platform for managing all aspects of a travel agency's operations, including:

- Travel package management
- Customer information management
- Booking processing
- Itinerary planning
- Customer reviews and ratings
- Reports and analytics

## Features

- **Package Management**: Create, update, and delete travel packages with details such as destination, duration, accommodations, activities, and pricing.
- **Customer Management**: Register new customers, update customer information, and track customer preferences and booking history.
- **Booking Process**: Allow customers to browse packages, make reservations, and process payments.
- **Itinerary Management**: Create and modify detailed day-by-day itineraries for each package or custom trip.
- **Review System**: Collect and display customer reviews and ratings for travel packages.
- **Reporting**: Generate reports on sales, popular destinations, and customer satisfaction metrics.

## Technical Implementation

The system is built using Java and Swing GUI, implementing several key concepts:

- **Object-Oriented Design**: Inheritance, polymorphism, interfaces, abstract classes, method overloading and overriding.
- **File I/O**: JSON-based data persistence for all entities.
- **Exception Handling**: Custom exceptions for business logic validation.
- **Graphical User Interface**: Intuitive Swing-based interface with tabbed navigation.
- **Design Patterns**: MVC architecture, observer pattern, etc.

## Getting Started

### Prerequisites

- Java 11 or higher
- Maven (for dependency management)

### Installation

1. Clone this repository
2. Build the project with Maven:
   ```
   mvn clean package
   ```
3. Run the application:
   ```
   java -jar target/tams-1.0-SNAPSHOT-jar-with-dependencies.jar
   ```

## Project Structure

- `src/tams/model/` - Data models (Customer, TravelPackage, Booking, etc.)
- `src/tams/view/` - GUI components
- `src/tams/controller/` - Application logic
- `src/tams/util/` - Utilities for data management, etc.
- `src/tams/exceptions/` - Custom exception classes

## Future Improvements

- Database integration
- REST API for online bookings
- Reporting and analytics dashboard
- Mobile application
- Integration with third-party travel services

## License

This project is open source, under the MIT license.

## Author

Peter Jiang 