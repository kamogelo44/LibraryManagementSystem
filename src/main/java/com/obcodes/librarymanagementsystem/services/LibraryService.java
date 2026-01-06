package com.obcodes.librarymanagementsystem.services;

import com.obcodes.librarymanagementsystem.models.Library;
import com.obcodes.librarymanagementsystem.models.Member;
import com.obcodes.librarymanagementsystem.models.Book;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Service layer class that handles business logic for the library system.
 * Separates business logic from data models and file operations.
 * 
 * @author Obakeng Phale
 */
public class LibraryService {
    private Library library;
    private FileService fileService;
    
    /**
     * Constructor that initializes the service with a library instance
     * 
     * @param library The library instance to manage
     * @param fileService The file service for data persistence
     */
    public LibraryService(Library library, FileService fileService) {
        this.library = library;
        this.fileService = fileService;
    }
    
    /**
     * Alternative constructor that creates new instances
     */
    public LibraryService() {
        this.library = new Library();
        this.fileService = new FileService();
        // Load data automatically when creating service
        loadAllData();
    }
    
    /**
     * Registers a new member with the library
     * 
     * @param name The name of the member to register
     * @return The ID of the newly registered member
     * @throws IllegalArgumentException if name is null or empty
     */
    public long registerMember(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Member name cannot be empty");
        }
        
        Member newMember = new Member(name.trim());
        boolean added = library.addMember(newMember);
        
        if (added) {
            // Auto-save after adding member
            saveMembersData();
            System.out.println("New member registered: " + newMember.getName() + 
                              " (ID: " + newMember.getMemberID() + ")");
            return newMember.getMemberID();
        } else {
            throw new IllegalStateException("Failed to register member. Member ID might already exist.");
        }
    }
    
    /**
     * Adds a new book to the library's collection
     * 
     * @param title The title of the book
     * @param author The author of the book
     * @return The ISBN of the newly added book
     * @throws IllegalArgumentException if title or author is empty
     */
    public long addNewBook(String title, String author) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        
        // Using constructor that auto-generates ISBN
        Book newBook = new Book(title.trim(), author.trim(), "Available");
        boolean added = library.addBook(newBook);
        
        if (added) {
            // Auto-save after adding book
            saveBooksData();
            System.out.println("New book added: '" + newBook.getTitle() + 
                              "' by " + newBook.getAuthor() + 
                              " (ISBN: " + newBook.getISBN() + ")");
            return newBook.getISBN();
        } else {
            throw new IllegalStateException("Failed to add book. ISBN might already exist.");
        }
    }
    
    /**
     * Adds a new book with specific ISBN
     * 
     * @param title The title of the book
     * @param author The author of the book
     * @param ISBN The ISBN of the book
     * @return The ISBN of the newly added book
     */
    public long addNewBook(String title, String author, long ISBN) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Book title cannot be empty");
        }
        if (author == null || author.trim().isEmpty()) {
            throw new IllegalArgumentException("Book author cannot be empty");
        }
        
        Book newBook = new Book(title.trim(), author.trim(), ISBN, "Available");
        boolean added = library.addBook(newBook);
        
        if (added) {
            saveBooksData();
            System.out.println("New book added: '" + newBook.getTitle() + 
                              "' by " + newBook.getAuthor() + 
                              " (ISBN: " + newBook.getISBN() + ")");
            return newBook.getISBN();
        } else {
            throw new IllegalStateException("Failed to add book. ISBN might already exist.");
        }
    }
    
    /**
     * Allows a member to borrow a book
     * 
     * @param memberID The ID of the member borrowing the book
     * @param ISBN The ISBN of the book to borrow
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if member or book not found
     */
    public boolean borrowBook(long memberID, long ISBN) {
        Member member = library.findMember(memberID);
        Book book = library.findBook(ISBN);
        
        if (member == null) {
            throw new IllegalArgumentException("Member with ID " + memberID + " not found");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book with ISBN " + ISBN + " not found");
        }
        
        // Use library's checkout method which handles all validations
        boolean success = library.checkoutBook(memberID, ISBN);
        
        if (success) {
            // Save both books and members since borrowing updates both
            saveBooksData();
            saveMembersData();
        }
        
        return success;
    }
    
    /**
     * Returns a borrowed book
     * 
     * @param memberID The ID of the member returning the book
     * @param ISBN The ISBN of the book to return
     * @return true if successful, false otherwise
     * @throws IllegalArgumentException if member or book not found
     */
    public boolean returnBook(long memberID, long ISBN) {
        Member member = library.findMember(memberID);
        Book book = library.findBook(ISBN);
        
        if (member == null) {
            throw new IllegalArgumentException("Member with ID " + memberID + " not found");
        }
        if (book == null) {
            throw new IllegalArgumentException("Book with ISBN " + ISBN + " not found");
        }
        
        // Use library's return method which handles all validations
        boolean success = library.returnBook(memberID, ISBN);
        
        if (success) {
            // Save both books and members since returning updates both
            saveBooksData();
            saveMembersData();
        }
        
        return success;
    }
    
    /**
     * Saves all library data to files using FileService
     * 
     * @return true if save was successful, false otherwise
     */
    public boolean saveAllData() {
        try {
            // Save books data
            boolean booksSaved = saveBooksData();
            
            // Save members data
            boolean membersSaved = saveMembersData();
            
            if (booksSaved && membersSaved) {
                System.out.println("All library data saved successfully");
                return true;
            } else {
                System.err.println("Failed to save some data");
                return false;
            }
        } catch (Exception e) {
            System.err.println("Failed to save library data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Loads all library data from files using FileService
     * 
     * @return true if load was successful, false otherwise
     */
    public boolean loadAllData() {
        try {
            // Load books
            HashMap<Long, Book> books = fileService.loadBooks();
            
            // Load members
            HashMap<Long, Member> members = fileService.loadMembers();
            
            // Clear existing data and add loaded data
            // We need to add books one by one to library
            for (Book book : books.values()) {
                library.addBook(book);
            }
            
            // Add members one by one
            for (Member member : members.values()) {
                library.addMember(member);
            }
            
            System.out.println("Library data loaded successfully");
            System.out.println("Books loaded: " + books.size());
            System.out.println("Members loaded: " + members.size());
            
            return true;
        } catch (Exception e) {
            System.err.println("Failed to load library data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Helper method to save books data
     * 
     * @return true if save was successful, false otherwise
     */
    private boolean saveBooksData() {
        try {
            // Get all books from library
            ArrayList<Book> bookList = library.getAllBooks();
            
            // Convert to HashMap for FileService
            HashMap<Long, Book> booksMap = new HashMap<>();
            for (Book book : bookList) {
                booksMap.put(book.getISBN(), book);
            }
            
            return fileService.saveBooks(booksMap);
        } catch (Exception e) {
            System.err.println("Failed to save books data: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Helper method to save members data
     * 
     * @return true if save was successful, false otherwise
     */
    private boolean saveMembersData() {
        try {
            // Get all members from library
            ArrayList<Member> memberList = library.getAllMembers();
            
            // Convert to HashMap for FileService
            HashMap<Long, Member> membersMap = new HashMap<>();
            for (Member member : memberList) {
                membersMap.put(member.getMemberID(), member);
            }
            
            return fileService.saveMembers(membersMap);
        } catch (Exception e) {
            System.err.println("Failed to save members data: " + e.getMessage());
            return false;
        }
    }
    
    // ================ GETTERS AND SETTERS ================
    
    public Library getLibrary() {
        return library;
    }
    
    public void setLibrary(Library library) {
        this.library = library;
    }
    
    public FileService getFileService() {
        return fileService;
    }
    
    public void setFileService(FileService fileService) {
        this.fileService = fileService;
    }
    
    // ================ QUERY METHODS ================
    
    /**
     * Gets all books in the library
     * 
     * @return String representation of all books
     */
    public String getAllBooks() {
        ArrayList<Book> books = library.getAllBooks();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL BOOKS ===\n");
        
        if (books.isEmpty()) {
            sb.append("No books in the library.\n");
        } else {
            for (Book book : books) {
                sb.append("ISBN: ").append(book.getISBN())
                  .append(", Title: ").append(book.getTitle())
                  .append(", Author: ").append(book.getAuthor())
                  .append(", Status: ").append(book.getStatus())
                  .append("\n");
            }
        }
        sb.append("=================\n");
        return sb.toString();
    }
    
    /**
     * Gets all registered members
     * 
     * @return String representation of all members
     */
    public String getAllMembers() {
        ArrayList<Member> members = library.getAllMembers();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== ALL MEMBERS ===\n");
        
        if (members.isEmpty()) {
            sb.append("No members registered.\n");
        } else {
            for (Member member : members) {
                sb.append("ID: ").append(member.getMemberID())
                  .append(", Name: ").append(member.getName())
                  .append(", Books Borrowed: ").append(member.getBorrowedBooksCount())
                  .append("\n");
            }
        }
        sb.append("===================\n");
        return sb.toString();
    }
    
    /**
     * Finds a book by title
     * 
     * @param title The title to search for
     * @return Information about the book, or "Book not found"
     */
    public String findBookByTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            return "Error: Book title cannot be empty.";
        }
        
        ArrayList<Book> results = library.searchBooksByTitle(title.trim());
        
        if (results.isEmpty()) {
            return "No books found with title containing: '" + title + "'";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOOKS FOUND (").append(results.size()).append(") ===\n");
        for (Book book : results) {
            sb.append("ISBN: ").append(book.getISBN())
              .append(", Title: ").append(book.getTitle())
              .append(", Author: ").append(book.getAuthor())
              .append(", Status: ").append(book.getStatus())
              .append("\n");
        }
        sb.append("======================\n");
        return sb.toString();
    }
    
    /**
     * Finds a book by ISBN
     * 
     * @param isbn The ISBN to search for
     * @return Information about the book, or "Book not found"
     */
    public String findBookByISBN(long isbn) {
        Book book = library.findBook(isbn);
        
        if (book == null) {
            return "Book not found with ISBN: " + isbn;
        }
        
        return String.format(
            "Book Found:\nISBN: %d\nTitle: %s\nAuthor: %s\nStatus: %s",
            book.getISBN(), book.getTitle(), book.getAuthor(), book.getStatus());
    }
    
    /**
     * Finds a member by ID
     * 
     * @param memberID The member ID to search for
     * @return Information about the member, or "Member not found"
     */
    public String findMemberByID(long memberID) {
        Member member = library.findMember(memberID);
        
        if (member == null) {
            return "Member not found with ID: " + memberID;
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Member Found:\n")
          .append("ID: ").append(member.getMemberID()).append("\n")
          .append("Name: ").append(member.getName()).append("\n")
          .append("Books Borrowed: ").append(member.getBorrowedBooksCount()).append("\n");
        
        if (!member.getBorrowedBooks().isEmpty()) {
            sb.append("Borrowed Books:\n");
            for (Book book : member.getBorrowedBooks()) {
                sb.append("  - ").append(book.getTitle())
                  .append(" by ").append(book.getAuthor())
                  .append(" (ISBN: ").append(book.getISBN()).append(")\n");
            }
        }
        return sb.toString();
    }
    
    /**
     * Gets books borrowed by a specific member
     * 
     * @param memberID The member ID
     * @return String of borrowed books, or error message
     */
    public String getMemberBorrowedBooks(long memberID) {
        Member member = library.findMember(memberID);
        
        if (member == null) {
            return "Member not found with ID: " + memberID;
        }
        
        if (member.getBorrowedBooks().isEmpty()) {
            return member.getName() + " has no borrowed books.";
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("Books borrowed by ").append(member.getName()).append(":\n");
        for (Book book : member.getBorrowedBooks()) {
            sb.append("- ").append(book.getTitle())
              .append(" by ").append(book.getAuthor())
              .append(" (ISBN: ").append(book.getISBN()).append(")\n");
        }
        return sb.toString();
    }
    
    /**
     * Gets all available books (not borrowed)
     * 
     * @return String of available books
     */
    public String getAvailableBooks() {
        ArrayList<Book> availableBooks = library.getAvailableBooks();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== AVAILABLE BOOKS ===\n");
        
        if (availableBooks.isEmpty()) {
            sb.append("No available books.\n");
        } else {
            for (Book book : availableBooks) {
                sb.append("ISBN: ").append(book.getISBN())
                  .append(", Title: ").append(book.getTitle())
                  .append(", Author: ").append(book.getAuthor())
                  .append("\n");
            }
        }
        sb.append("========================\n");
        return sb.toString();
    }
    
    /**
     * Gets all borrowed books
     * 
     * @return String of borrowed books
     */
    public String getBorrowedBooks() {
        ArrayList<Book> allBooks = library.getAllBooks();
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== BORROWED BOOKS ===\n");
        
        boolean hasBorrowedBooks = false;
        for (Book book : allBooks) {
            if ("Borrowed".equalsIgnoreCase(book.getStatus())) {
                sb.append("ISBN: ").append(book.getISBN())
                  .append(", Title: ").append(book.getTitle())
                  .append(", Author: ").append(book.getAuthor())
                  .append("\n");
                hasBorrowedBooks = true;
            }
        }
        
        if (!hasBorrowedBooks) {
            sb.append("No borrowed books.\n");
        }
        sb.append("=======================\n");
        return sb.toString();
    }
    
    /**
     * Search for books by author
     * 
     * @param author The author to search for
     * @return String of matching books
     */
    public String searchBooksByAuthor(String author) {
        if (author == null || author.trim().isEmpty()) {
            return "Error: Author name cannot be empty.";
        }
        
        ArrayList<Book> results = library.searchBooksByAuthor(author.trim());
        
        StringBuilder sb = new StringBuilder();
        sb.append("=== BOOKS BY ").append(author.toUpperCase()).append(" ===\n");
        
        if (results.isEmpty()) {
            sb.append("No books found by ").append(author).append("\n");
        } else {
            for (Book book : results) {
                sb.append("ISBN: ").append(book.getISBN())
                  .append(", Title: ").append(book.getTitle())
                  .append(", Status: ").append(book.getStatus())
                  .append("\n");
            }
        }
        sb.append("=========================\n");
        return sb.toString();
    }
    
    /**
     * Delete a book from the library
     * 
     * @param isbn The ISBN of the book to delete
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteBook(long isbn) {
        // Check if book is borrowed
        Book book = library.findBook(isbn);
        if (book != null && "Borrowed".equalsIgnoreCase(book.getStatus())) {
            System.out.println("Cannot delete book that is currently borrowed");
            return false;
        }
        
        boolean deleted = library.removeBook(isbn);
        if (deleted) {
            saveBooksData();
            System.out.println("Book with ISBN " + isbn + " deleted successfully");
        } else {
            System.out.println("Book with ISBN " + isbn + " not found");
        }
        return deleted;
    }
    
    /**
     * Remove a member from the library
     * 
     * @param memberID The ID of the member to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeMember(long memberID) {
        // Check if member has borrowed books
        Member member = library.findMember(memberID);
        if (member != null && !member.getBorrowedBooks().isEmpty()) {
            System.out.println("Cannot remove member who has borrowed books");
            return false;
        }
        
        boolean removed = library.removeMember(memberID);
        if (removed) {
            saveMembersData();
            System.out.println("Member with ID " + memberID + " removed successfully");
        } else {
            System.out.println("Member with ID " + memberID + " not found");
        }
        return removed;
    }
    
    /**
     * Displays library statistics
     * 
     * @return String with library statistics
     */
    public String getLibraryStats() {
        int totalBooks = library.getTotalBooks();
        int availableBooks = library.getAvailableBooksCount();
        int borrowedBooks = library.getBorrowedBooksCount();
        int totalMembers = library.getTotalMembers();
        
        // Count members with borrowed books
        int membersWithBooks = 0;
        ArrayList<Member> allMembers = library.getAllMembers();
        for (Member member : allMembers) {
            if (!member.getBorrowedBooks().isEmpty()) {
                membersWithBooks++;
            }
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append("\n=== LIBRARY STATISTICS ===\n")
          .append("Total Books: ").append(totalBooks).append("\n")
          .append("Available Books: ").append(availableBooks).append("\n")
          .append("Borrowed Books: ").append(borrowedBooks).append("\n")
          .append("Total Members: ").append(totalMembers).append("\n")
          .append("Active Members (with books): ").append(membersWithBooks).append("\n")
          .append("Inactive Members: ").append(totalMembers - membersWithBooks).append("\n");
        
        // Calculate borrowing percentage
        if (totalMembers > 0) {
            double activePercentage = (membersWithBooks * 100.0) / totalMembers;
            sb.append(String.format("Active Members Percentage: %.1f%%\n", activePercentage));
        }
        
        sb.append("===========================\n");
        return sb.toString();
    }
    
    /**
     * Clean up resources and save data
     */
    public void shutdown() {
        System.out.println("\nShutting down LibraryService...");
        boolean saved = saveAllData();
        if (saved) {
            System.out.println("All data saved successfully.");
        } else {
            System.out.println("Warning: Some data may not have been saved.");
        }
        System.out.println("LibraryService shutdown complete.");
    }
}