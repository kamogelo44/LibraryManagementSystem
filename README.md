# Library Management System

## Project Overview
A comprehensive Java-based Library Management System that handles book cataloging, member management, borrowing operations, and persistent data storage. The system provides a complete solution for managing library operations with a user-friendly console interface.

## Project Status
 Completed - All core features implemented and tested

## Technologies Used
- Java 21 - core programming language
   
- Maven - Build automation and dependency management

- Serialization - Object persistence for data storage

- Collections Framework - ArrayList & HashMap for data management

- File I/O Operations - Data persistence with error handling
# System Architecture

## Core Components
<img width="668" height="525" alt="image" src="https://github.com/user-attachments/assets/f7325c2a-bde7-435d-b692-67cc19720245" />

# Class Structure
1. Main - Console user interface with menu system

2. LibraryService - Business logic layer (Service Pattern)

3. Library - Core library operations and data management

4. FileService - File operations and data persistence

5. Book - Book model with attributes and operations

6. Member - Member model with borrowing capabilities

# Installation & Setup
- Prerequisites
- Java Development Kit (JDK) 21 or higher
- Maven 3.6 or higher

Basic terminal/command line knowledge

# Installation Steps
1. Clone the Repository
   git clone https://github.com/obcodes/LibraryManagementSystem.git
   cd LibraryManagementSystem
2. Build the Project
   mvn clean compile
3. Run the Application
  mvn exec:java -Dexec.mainClass="com.obcodes.librarymanagementsystem.Main"
4. Alternative: Package as JAR
  mvn package
  java -jar target/LibraryManagementSystem-1.0-SNAPSHOT.jar

# Usage Guide
## Starting the System
When you run the application, it will:
1. Initialize the system and create necessary directories

2. Load existing data from files (if available)

3. Display the main menu

# Main Menu Options
1.  Add New Book          - Add a new book to the catalog
2.  Register New Member   - Register a new library member
3.  Borrow a Book         - Checkout a book to a member
4.  Return a Book         - Return a borrowed book
5.  Search for a Book     - Search books by title/ISBN/author
6.  Search for a Member   - Find a member by ID
7.  Display All Books     - View books (all/available/borrowed)
8.  Display All Members   - View all registered members
9.  Show Statistics       - View library statistics
10. Run Tests             - Run system tests (developer feature)
11. Exit System           - Save data and exit

# Data Files Structure
LibraryManagementSystem/
├── data/
│   ├── books.dat         # Book catalog (serialized)
│   ├── members.dat       # Member database (serialized)
│   └── backups/
│       ├── books_YYYYMMDD_HHMMSS.dat
│       └── members_YYYYMMDD_HHMMSS.dat

# Backup System
The system automatically:

- Creates backups before saving data

- Keeps the 5 most recent backups

- Can restore from backups if data is corrupted

# Key Features in Detail
## Book Management
- ISBN Generation: Auto-generates 13-digit ISBN numbers

- Status Tracking: Real-time tracking of book availability

- Validation: Prevents duplicate ISBN entries

- Safety Checks: Prevents deletion of borrowed books

## Member Management
- ID Generation: Auto-generates 12-digit unique member IDs

- Borrowing Limits: Enforces 5-book limit per member

- Safety Checks: Prevents removal of members with borrowed books

## Error Handling
- Input Validation: Validates all user inputs

- File Corruption Recovery: Attempts to restore from backups

- Graceful Degradation: Continues operation even if some features fail

## Data Persistence
- Automatic Saving: Saves data after every operation

- Backup System: Maintains multiple backup versions

- Corruption Detection: Detects and recovers from file corruption

# Code Examples
## Adding a Book
// Automatic ISBN generation
long isbn = libraryService.addNewBook("The Great Gatsby", "F. Scott Fitzgerald");

// Custom ISBN
long customISBN = libraryService.addNewBook("1984", "George Orwell", 9780451524935L);

## Registering a Member
long memberID = libraryService.registerMember("John Doe");
// Returns: 123456789012 (12-digit unique ID)

## Borrowing a Book
boolean success = libraryService.borrowBook(memberID, isbn);
// Validates: Book availability, member borrowing limit
## Project Structure
src/main/java/com/obcodes/librarymanagementsystem/
├── Main.java                    # User interface
├── services/
│   ├── LibraryService.java      # Business logic
│   └── FileService.java         # File operations
└── models/
    ├── Library.java            # Core library operations
    ├── Book.java              # Book model
    └── Member.java            # Member model
# Development Principles
## Devsign patterns implemented:
1. Service Layer Pattern - Separation of business logic from data models

2. Model-View-Controller - Separation of concerns (Console as View)

3. Singleton-like Services - Centralized service management

## Best Practices
- Separation of Concerns: Clear division between UI, business logic, and data layers

- Error Handling: Comprehensive try-catch blocks with user-friendly messages

- Input Validation: Validation at both UI and service layers

- Documentation: Complete JavaDoc comments for all public methods

- Code Organization: Logical package structure with clear responsibilities

# License
This project is open source and available for educational purposes.

# Author
Obakeng Phale - Software Developer & Student

# Acknowledgments
- Java Collections Framework documentation

- Maven documentation and community

- NetBeans IDE for development support

This project demonstrates practical application of Java OOP principles, file I/O operations, and software design patterns in a real-world library management scenario.

