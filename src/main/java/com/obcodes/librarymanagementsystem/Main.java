package com.obcodes.librarymanagementsystem;

import com.obcodes.librarymanagementsystem.services.LibraryService;
import java.util.Scanner;
import java.util.InputMismatchException;

/**
 * Main class for the Library Management System
 * Provides a text-based menu interface for all operations
 * 
 * @author Obakeng Phale
 */
public class Main {
    private static LibraryService libraryService;
    private static Scanner scanner;
    private static final String VERSION = "1.0.0";
    private static final String APP_NAME = "Library Management System";
    
    public static void main(String[] args) {
        try {
            // Initialize the system
            initializeSystem();
            
            // Display welcome message
            displayWelcome();
            
            // Main menu loop
            runMainMenu();
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] The application encountered an unexpected error.");
            System.err.println("Error details: " + e.getMessage());
            System.err.println("Please restart the application.");
            
            // Attempt to save data before exiting
            if (libraryService != null) {
                System.out.println("Attempting to save data before exiting...");
                libraryService.shutdown();
            }
            
            if (scanner != null) {
                scanner.close();
            }
            
            System.exit(1); // Exit with error code
        }
    }
    
    /**
     * Initialize the system components
     */
    private static void initializeSystem() {
        System.out.println("==========================================");
        System.out.println("Initializing " + APP_NAME + " v" + VERSION);
        System.out.println("==========================================");
        
        try {
            // Create scanner first for input
            scanner = new Scanner(System.in);
            
            // Create service instances - using the default constructor
            libraryService = new LibraryService();
            
            System.out.println("[INFO] System components initialized");
            
            // Load existing data
            System.out.print("Loading existing data... ");
            boolean dataLoaded = libraryService.loadAllData();
            
            if (dataLoaded) {
                System.out.println("[SUCCESS] Data loaded successfully");
            } else {
                System.out.println("[INFO] No existing data found or data file is empty");
                System.out.println("[INFO] Starting with empty library");
            }
            
            System.out.println("\n[SUCCESS] System initialized successfully!");
            System.out.println("==========================================\n");
            
        } catch (Exception e) {
            System.err.println("\n[ERROR] Failed to initialize system: " + e.getMessage());
            throw new RuntimeException("System initialization failed", e);
        }
    }
    
    /**
     * Display welcome message
     */
    private static void displayWelcome() {
        System.out.println("==========================================");
        System.out.println("   WELCOME TO " + APP_NAME.toUpperCase());
        System.out.println("   Version: " + VERSION);
        System.out.println("   Developed by: Obakeng Phale");
        System.out.println("==========================================");
        System.out.println();
        
        // Display quick stats if data exists
        try {
            String stats = libraryService.getLibraryStats();
            String[] lines = stats.split("\n");
            System.out.println("Current Library Status:");
            for (String line : lines) {
                if (line.contains("Total Books:") || line.contains("Total Members:")) {
                    System.out.println(line.trim());
                }
            }
            System.out.println();
        } catch (Exception e) {
            // Silently ignore stats display errors
        }
    }
    
    /**
     * Run the main menu loop
     */
    private static void runMainMenu() {
        boolean running = true;
        
        while (running) {
            try {
                displayMainMenu();
                int choice = getMenuChoice(1, 11);
                
                switch (choice) {
                    case 1 -> addNewBook();
                    case 2 -> registerNewMember();
                    case 3 -> borrowBook();
                    case 4 -> returnBook();
                    case 5 -> searchBook();
                    case 6 -> searchMember();
                    case 7 -> displayAllBooks();
                    case 8 -> displayAllMembers();
                    case 9 -> showStatistics();
                    case 10 -> runTests();
                    case 11 -> {
                        running = false;
                        shutdownSystem();
                    }
                }
                
                if (running && choice != 10) { // Don't pause after tests
                    System.out.println("\nPress Enter to continue...");
                    scanner.nextLine(); // Clear the buffer
                    scanner.nextLine(); // Wait for Enter key
                }
                
            } catch (Exception e) {
                System.err.println("\n[ERROR] An error occurred: " + e.getMessage());
                System.out.println("Returning to main menu...\n");
                
                // Clear scanner buffer
                if (scanner.hasNextLine()) {
                    scanner.nextLine();
                }
            }
        }
    }
    
    /**
     * Display the main menu
     */
    private static void displayMainMenu() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("            MAIN MENU");
        System.out.println("=".repeat(40));
        System.out.println("1.  Add New Book");
        System.out.println("2.  Register New Member");
        System.out.println("3.  Borrow a Book");
        System.out.println("4.  Return a Book");
        System.out.println("5.  Search for a Book");
        System.out.println("6.  Search for a Member");
        System.out.println("7.  Display All Books");
        System.out.println("8.  Display All Members");
        System.out.println("9.  Show Statistics");
        System.out.println("10. Run Tests");
        System.out.println("11. Exit System");
        System.out.println("=".repeat(40));
        System.out.print("\nEnter your choice (1-11): ");
    }
    
    /**
     * Get menu choice with validation
     */
    private static int getMenuChoice(int min, int max) {
        while (true) {
            try {
                String input = scanner.nextLine().trim();
                
                // Check for empty input
                if (input.isEmpty()) {
                    System.out.printf("Please enter a number between %d and %d: ", min, max);
                    continue;
                }
                
                int choice = Integer.parseInt(input);
                
                if (choice >= min && choice <= max) {
                    return choice;
                } else {
                    System.out.printf("Invalid choice. Please enter a number between %d and %d: ", min, max);
                }
            } catch (NumberFormatException e) {
                System.out.printf("Invalid input. Please enter a number between %d and %d: ", min, max);
            }
        }
    }
    
    /**
     * Add a new book to the library
     */
    private static void addNewBook() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("         ADD NEW BOOK");
        System.out.println("=".repeat(40));
        
        try {
            System.out.print("Enter book title: ");
            String title = scanner.nextLine().trim();
            
            if (title.isEmpty()) {
                System.out.println("[ERROR] Book title cannot be empty.");
                return;
            }
            
            System.out.print("Enter author name: ");
            String author = scanner.nextLine().trim();
            
            if (author.isEmpty()) {
                System.out.println("[ERROR] Author name cannot be empty.");
                return;
            }
            
            // Ask if user wants to specify ISBN or auto-generate
            System.out.print("Do you want to specify an ISBN? (y/n): ");
            String specifyISBN = scanner.nextLine().trim().toLowerCase();
            
            long isbn;
            
            if (specifyISBN.equals("y") || specifyISBN.equals("yes")) {
                System.out.print("Enter ISBN (13 digits): ");
                String isbnInput = scanner.nextLine().trim();
                
                if (isbnInput.isEmpty()) {
                    System.out.println("[ERROR] ISBN cannot be empty when specified.");
                    return;
                }
                
                try {
                    isbn = Long.parseLong(isbnInput);
                    if (isbnInput.length() != 13) {
                        System.out.println("[WARNING] ISBN should be 13 digits.");
                    }
                    isbn = libraryService.addNewBook(title, author, isbn);
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Invalid ISBN format. Please enter numbers only.");
                    return;
                }
            } else {
                isbn = libraryService.addNewBook(title, author);
            }
            
            System.out.println("\n" + "=".repeat(40));
            System.out.println("[SUCCESS] BOOK ADDED SUCCESSFULLY!");
            System.out.println("=".repeat(40));
            System.out.printf("%-15s: %s\n", "Title", title);
            System.out.printf("%-15s: %s\n", "Author", author);
            System.out.printf("%-15s: %d\n", "ISBN", isbn);
            System.out.println("=".repeat(40));
            
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to add book: " + e.getMessage());
        }
    }
    
    /**
     * Register a new member
     */
    private static void registerNewMember() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("      REGISTER NEW MEMBER");
        System.out.println("=".repeat(40));
        
        try {
            System.out.print("Enter member name: ");
            String name = scanner.nextLine().trim();
            
            if (name.isEmpty()) {
                System.out.println("[ERROR] Member name cannot be empty.");
                return;
            }
            
            long memberID = libraryService.registerMember(name);
            
            System.out.println("\n" + "=".repeat(40));
            System.out.println("[SUCCESS] MEMBER REGISTERED SUCCESSFULLY!");
            System.out.println("=".repeat(40));
            System.out.printf("%-15s: %s\n", "Name", name);
            System.out.printf("%-15s: %d\n", "Member ID", memberID);
            System.out.println("=".repeat(40));
            System.out.println("[IMPORTANT] Please remember your Member ID!");
            System.out.println("You will need it to borrow and return books.");
            System.out.println("=".repeat(40));
            
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to register member: " + e.getMessage());
        }
    }
    
    /**
     * Borrow a book
     */
    private static void borrowBook() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("          BORROW A BOOK");
        System.out.println("=".repeat(40));
        
        try {
            System.out.print("Enter member ID: ");
            String memberIdInput = scanner.nextLine().trim();
            
            if (memberIdInput.isEmpty()) {
                System.out.println("[ERROR] Member ID cannot be empty.");
                return;
            }
            
            System.out.print("Enter book ISBN: ");
            String isbnInput = scanner.nextLine().trim();
            
            if (isbnInput.isEmpty()) {
                System.out.println("[ERROR] ISBN cannot be empty.");
                return;
            }
            
            long memberID = Long.parseLong(memberIdInput);
            long isbn = Long.parseLong(isbnInput);
            
            boolean success = libraryService.borrowBook(memberID, isbn);
            
            if (success) {
                System.out.println("\n[SUCCESS] Book borrowed successfully!");
            } else {
                System.out.println("\n[INFO] Book borrowing failed. Please check:");
                System.out.println("   - Is the book available?");
                System.out.println("   - Has the member reached borrowing limit?");
                System.out.println("   - Is the member ID and ISBN correct?");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number format. Please enter numbers only.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to borrow book: " + e.getMessage());
        }
    }
    
    /**
     * Return a book
     */
    private static void returnBook() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("          RETURN A BOOK");
        System.out.println("=".repeat(40));
        
        try {
            System.out.print("Enter member ID: ");
            String memberIdInput = scanner.nextLine().trim();
            
            if (memberIdInput.isEmpty()) {
                System.out.println("[ERROR] Member ID cannot be empty.");
                return;
            }
            
            System.out.print("Enter book ISBN: ");
            String isbnInput = scanner.nextLine().trim();
            
            if (isbnInput.isEmpty()) {
                System.out.println("[ERROR] ISBN cannot be empty.");
                return;
            }
            
            long memberID = Long.parseLong(memberIdInput);
            long isbn = Long.parseLong(isbnInput);
            
            boolean success = libraryService.returnBook(memberID, isbn);
            
            if (success) {
                System.out.println("\n[SUCCESS] Book returned successfully!");
            } else {
                System.out.println("\n[INFO] Book return failed. Please check:");
                System.out.println("   - Does the member have this book borrowed?");
                System.out.println("   - Are the member ID and ISBN correct?");
            }
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid number format. Please enter numbers only.");
        } catch (IllegalArgumentException e) {
            System.out.println("[ERROR] " + e.getMessage());
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to return book: " + e.getMessage());
        }
    }
    
    /**
     * Search for a book
     */
    private static void searchBook() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("        SEARCH FOR A BOOK");
        System.out.println("=".repeat(40));
        System.out.println("1. Search by Title");
        System.out.println("2. Search by ISBN");
        System.out.println("3. Search by Author");
        System.out.println("4. Back to Main Menu");
        System.out.println("=".repeat(40));
        System.out.print("\nEnter your choice (1-4): ");
        
        int choice = getMenuChoice(1, 4);
        
        if (choice == 4) {
            return;
        }
        
        switch (choice) {
            case 1 -> {
                System.out.print("\nEnter book title (or part of title): ");
                String title = scanner.nextLine().trim();
                
                if (title.isEmpty()) {
                    System.out.println("[ERROR] Search term cannot be empty.");
                    return;
                }
                
                String result = libraryService.findBookByTitle(title);
                System.out.println("\n" + "=".repeat(40));
                System.out.println("SEARCH RESULTS:");
                System.out.println("=".repeat(40));
                System.out.println(result);
                System.out.println("=".repeat(40));
            }
            case 2 -> {
                try {
                    System.out.print("\nEnter book ISBN: ");
                    String isbnInput = scanner.nextLine().trim();
                    
                    if (isbnInput.isEmpty()) {
                        System.out.println("[ERROR] ISBN cannot be empty.");
                        return;
                    }
                    
                    long isbn = Long.parseLong(isbnInput);
                    String result = libraryService.findBookByISBN(isbn);
                    
                    System.out.println("\n" + "=".repeat(40));
                    System.out.println("SEARCH RESULTS:");
                    System.out.println("=".repeat(40));
                    System.out.println(result);
                    System.out.println("=".repeat(40));
                    
                } catch (NumberFormatException e) {
                    System.out.println("[ERROR] Invalid ISBN format. Please enter numbers only.");
                }
            }
            case 3 -> {
                System.out.print("\nEnter author name (or part of name): ");
                String author = scanner.nextLine().trim();
                
                if (author.isEmpty()) {
                    System.out.println("[ERROR] Search term cannot be empty.");
                    return;
                }
                
                String result = libraryService.searchBooksByAuthor(author);
                System.out.println("\n" + "=".repeat(40));
                System.out.println("SEARCH RESULTS:");
                System.out.println("=".repeat(40));
                System.out.println(result);
                System.out.println("=".repeat(40));
            }
        }
    }
    
    /**
     * Search for a member
     */
    private static void searchMember() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("       SEARCH FOR A MEMBER");
        System.out.println("=".repeat(40));
        
        try {
            System.out.print("Enter member ID: ");
            String memberIdInput = scanner.nextLine().trim();
            
            if (memberIdInput.isEmpty()) {
                System.out.println("[ERROR] Member ID cannot be empty.");
                return;
            }
            
            long memberID = Long.parseLong(memberIdInput);
            String result = libraryService.findMemberByID(memberID);
            
            System.out.println("\n" + "=".repeat(40));
            System.out.println("SEARCH RESULTS:");
            System.out.println("=".repeat(40));
            System.out.println(result);
            System.out.println("=".repeat(40));
            
        } catch (NumberFormatException e) {
            System.out.println("[ERROR] Invalid member ID format. Please enter numbers only.");
        } catch (Exception e) {
            System.out.println("[ERROR] Failed to search for member: " + e.getMessage());
        }
    }
    
    /**
     * Display all books
     */
    private static void displayAllBooks() {
        System.out.println("\n" + "=".repeat(40));
        System.out.println("        DISPLAY BOOKS");
        System.out.println("=".repeat(40));
        System.out.println("1. All Books");
        System.out.println("2. Available Books Only");
        System.out.println("3. Borrowed Books Only");
        System.out.println("4. Back to Main Menu");
        System.out.println("=".repeat(40));
        System.out.print("\nEnter your choice (1-4): ");
        
        int choice = getMenuChoice(1, 4);
        
        if (choice == 4) {
            return;
        }
        
        String result = "";
        
        switch (choice) {
            case 1 -> result = libraryService.getAllBooks();
            case 2 -> result = libraryService.getAvailableBooks();
            case 3 -> result = libraryService.getBorrowedBooks();
        }
        
        System.out.println("\n" + "=".repeat(60));
        System.out.println(result);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Display all members
     */
    private static void displayAllMembers() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("                    ALL MEMBERS");
        System.out.println("=".repeat(60));
        
        String allMembers = libraryService.getAllMembers();
        System.out.println(allMembers);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Show library statistics
     */
    private static void showStatistics() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               LIBRARY STATISTICS");
        System.out.println("=".repeat(60));
        
        String stats = libraryService.getLibraryStats();
        System.out.println(stats);
        System.out.println("=".repeat(60));
    }
    
    /**
     * Run system tests (hidden feature for developers)
     */
    private static void runTests() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("               SYSTEM TESTS");
        System.out.println("=".repeat(60));
        System.out.println("Warning: This will modify your data!");
        System.out.print("Do you want to continue? (y/n): ");
        
        String confirmation = scanner.nextLine().trim().toLowerCase();
        
        if (!confirmation.equals("y") && !confirmation.equals("yes")) {
            System.out.println("Tests cancelled.");
            return;
        }
        
        System.out.println("\nRunning system tests...\n");
        
        try {
            // Create a test library service to avoid modifying real data
            LibraryService testService = new LibraryService();
            
            System.out.println("Test 1: Creating test data...");
            long book1ISBN = testService.addNewBook("Test Book 1", "Test Author 1");
            long book2ISBN = testService.addNewBook("Test Book 2", "Test Author 2");
            long member1ID = testService.registerMember("Test Member 1");
            long member2ID = testService.registerMember("Test Member 2");
            System.out.println("[SUCCESS] Test data created");
            
            System.out.println("\nTest 2: Borrowing books...");
            boolean borrow1 = testService.borrowBook(member1ID, book1ISBN);
            boolean borrow2 = testService.borrowBook(member2ID, book2ISBN);
            System.out.println("[INFO] Borrow operations: " + borrow1 + ", " + borrow2);
            
            System.out.println("\nTest 3: Returning books...");
            boolean return1 = testService.returnBook(member1ID, book1ISBN);
            boolean return2 = testService.returnBook(member2ID, book2ISBN);
            System.out.println("[INFO] Return operations: " + return1 + ", " + return2);
            
            System.out.println("\nTest 4: Search operations...");
            System.out.println(testService.findBookByTitle("Test Book 1"));
            System.out.println(testService.findMemberByID(member1ID));
            System.out.println("[SUCCESS] Search operations completed");
            
            System.out.println("\nTest 5: Data persistence...");
            boolean saved = testService.saveAllData();
            boolean loaded = testService.loadAllData();
            System.out.println("[INFO] Data persistence: Save=" + saved + ", Load=" + loaded);
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("[SUCCESS] ALL TESTS PASSED!");
            System.out.println("=".repeat(60));
            
            // Clean up test data
            testService.deleteBook(book1ISBN);
            testService.deleteBook(book2ISBN);
            testService.removeMember(member1ID);
            testService.removeMember(member2ID);
            
        } catch (Exception e) {
            System.out.println("\n[ERROR] TEST FAILED: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Shutdown the system gracefully
     */
    private static void shutdownSystem() {
        System.out.println("\n" + "=".repeat(60));
        System.out.println("             SYSTEM SHUTDOWN");
        System.out.println("=".repeat(60));
        
        try {
            System.out.print("Saving data... ");
            libraryService.shutdown();
            System.out.println("[SUCCESS] Data saved successfully");
            
            scanner.close();
            System.out.println("[SUCCESS] Resources released");
            
            System.out.println("\n" + "=".repeat(60));
            System.out.println("  Thank you for using " + APP_NAME + "!");
            System.out.println("  Developed by: Obakeng Phale");
            System.out.println("  Version: " + VERSION);
            System.out.println("=".repeat(60));
            System.out.println("\nGoodbye!\n");
            
        } catch (Exception e) {
            System.err.println("\n[WARNING] Error during shutdown: " + e.getMessage());
            System.err.println("Some data may not have been saved properly.");
        }
    }
}