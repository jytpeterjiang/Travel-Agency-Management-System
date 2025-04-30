# Travel Agency Management System (TAMS)

A comprehensive management system for travel agencies to handle travel bookings, itineraries, and customer information.

## Overview

The Travel Agency Management System (TAMS) provides a platform for managing all aspects of a travel agency's operations, including:

- Travel package management
- Customer information management
- Booking processing
- Itinerary planning
- Activity management
- Customer reviews and ratings
- Reports and analytics

## Features

- **Package Management**: Create, update, and delete travel packages with details such as destination, duration, accommodations, activities, and pricing.
- **Customer Management**: Register new customers, update customer information, and track customer booking history.
- **Booking Process**: Allow customers to browse packages, make reservations, and process payments.
- **Itinerary Management**: Create and modify detailed day-by-day itineraries for each package or custom trip.
- **Activity Management**: Maintain a database of activities that can be added to packages or itineraries.
- **Review System**: Collect and display customer reviews and ratings for travel packages.
- **Reporting**: Generate reports on bookings, revenue, and popular destinations.

## Technical Implementation

The system is built using Java and Swing GUI, implementing several key concepts:

- **Object-Oriented Design**: Inheritance, polymorphism, interfaces, abstract classes, method overloading and overriding.
- **File I/O**: JSON-based data persistence for all entities.
- **Exception Handling**: Custom exceptions for business logic validation.
- **Graphical User Interface**: Intuitive Swing-based interface with tabbed navigation.
- **Design Patterns**: MVC architecture for separation of concerns.

## Project Structure

The application follows the MVC (Model-View-Controller) architecture pattern:

- **Model** (`src/tams/model/`): Contains all the business entities
  - Core classes: `TravelPackage`, `CustomTrip`, `Customer`, `Booking`, `Payment`
  - Support classes: `Itinerary`, `ItineraryDay`, `Activity`, `Review`
  - Enums: `BookingStatus`, `PaymentMethod`, `PaymentStatus`
  - Interfaces: `Bookable`, `Reviewable`
  - Abstract classes: `TravelService`

- **View** (`src/tams/view/`): Contains the GUI components
  - Main container: `MainWindow` (with tabbed interface)
  - Abstract base: `BasePanel`
  - Feature panels: `PackagesPanel`, `TravelPackagesPanel`, `CustomersPanel`, `BookingsPanel`, `ActivitiesPanel`, `ReviewsPanel`, `ReportsPanel`

- **Controller** (`src/tams/controller/`): Handles application logic
  - `TravelAgencyController`: Coordinates between views and model objects

- **Util** (`src/tams/util/`): Helper classes
  - `DataManager`: Handles file I/O operations and data persistence

- **Exceptions** (`src/tams/exceptions/`): Custom exceptions
  - `BookingException`: For booking-related errors
  - `PaymentProcessException`: For payment processing errors

## Getting Started

### Prerequisites

- Java 11 or higher
- JSON library for data persistence

### Installation

1. Clone this repository
2. Compile the project:
   ```
   javac -d bin src/tams/**/*.java
   ```
3. Run the application:
   ```
   java -cp bin tams.Main
   ```

## Data Storage

The application uses JSON files for data persistence:
- `customers.json`: Customer information
- `packages.json`: Travel package details
- `bookings.json`: Booking records
- `reviews.json`: Customer reviews
- `activities.json`: Activity information

## OOP Concepts Demonstrated

- **Inheritance**: The system uses inheritance to create class hierarchies (e.g., `TravelService` as parent of `TravelPackage` and `CustomTrip`)
- **Interfaces**: `Bookable` and `Reviewable` interfaces define contracts for classes
- **Abstract Classes**: `TravelService` and `BasePanel` provide base functionality for child classes
- **Polymorphism**: Different implementations of methods like `calculateTotalPrice()` in subclasses
- **Encapsulation**: Private attributes with public getters/setters across the model classes
- **Method Overloading**: Multiple methods with the same name but different parameters
- **Method Overriding**: Customized implementations of parent class methods

## Exception Handling

The system includes custom exceptions:
- `BookingException`: Handles errors in the booking process
- `PaymentProcessException`: Manages payment-related errors

## Future Improvements

- Database integration (replacing file-based storage)
- Enhanced reporting capabilities
- User authentication system
- Mobile application interface
- Email notification system

## Author

Peter Jiang 