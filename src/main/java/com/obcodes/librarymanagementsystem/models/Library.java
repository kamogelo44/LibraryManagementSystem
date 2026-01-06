package com.obcodes.librarymanagementsystem.models;
import java.io.*;
import java.util.HashMap;
import java.util.ArrayList;

/**
 * Library Management System - Central management class
 * @author Obakeng Phale
 */
public class Library implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String LIBRARY_DATA_FILE = "data/library.dat";
    
    private HashMap<Long, Book> books;
    private HashMap<Long, Member> members;
    
    public Library() {
        books = new HashMap<>();
        members = new HashMap<>();
    }
    
    // ==================== BOOK MANAGEMENT ====================
    
    /**
     * Add a new book to the library
     * @param book The book to add
     * @return true if added successfully, false if ISBN already exists
     */
    public boolean addBook(Book book) {
        if (book == null) {
            System.out.println("Cannot add null book");
            return false;
        }
        
        if (books.containsKey(book.getISBN())) {
            System.out.println("Book with ISBN " + book.getISBN() + " already exists");
            return false;
        }
        
        books.put(book.getISBN(), book);
        System.out.println("Book added: " + book.getTitle());
        return true;
    }
    
    /**
     * Remove a book from the library
     * @param ISBN The ISBN of the book to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeBook(long ISBN) {
        Book book = books.remove(ISBN);
        if (book != null) {
            System.out.println("Book removed: " + book.getTitle());
            return true;
        }
        System.out.println("Book with ISBN " + ISBN + " not found");
        return false;
    }
    
    /**
     * Find a book by ISBN
     * @param ISBN The ISBN to search for
     * @return The book if found, null otherwise
     */
    public Book findBook(long ISBN) {
        return books.get(ISBN);
    }
    
    /**
     * Search for books by title (partial match, case-insensitive)
     * @param title The title to search for
     * @return List of matching books
     */
    public ArrayList<Book> searchBooksByTitle(String title) {
        ArrayList<Book> results = new ArrayList<>();
        String searchTerm = title.toLowerCase();
        
        for (Book book : books.values()) {
            if (book.getTitle().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        
        return results;
    }
    
    /**
     * Search for books by author (partial match, case-insensitive)
     * @param author The author to search for
     * @return List of matching books
     */
    public ArrayList<Book> searchBooksByAuthor(String author) {
        ArrayList<Book> results = new ArrayList<>();
        String searchTerm = author.toLowerCase();
        
        for (Book book : books.values()) {
            if (book.getAuthor().toLowerCase().contains(searchTerm)) {
                results.add(book);
            }
        }
        
        return results;
    }
    
    /**
     * Get all books in the library
     * @return ArrayList of all books
     */
    public ArrayList<Book> getAllBooks() {
        return new ArrayList<>(books.values());
    }
    
    /**
     * Get all available books
     * @return ArrayList of available books
     */
    public ArrayList<Book> getAvailableBooks() {
        ArrayList<Book> available = new ArrayList<>();
        for (Book book : books.values()) {
            if ("Available".equalsIgnoreCase(book.getStatus())) {
                available.add(book);
            }
        }
        return available;
    }
    
    // ==================== MEMBER MANAGEMENT ====================
    
    /**
     * Add a new member to the library
     * @param member The member to add
     * @return true if added successfully, false if member ID already exists
     */
    public boolean addMember(Member member) {
        if (member == null) {
            System.out.println("Cannot add null member");
            return false;
        }
        
        if (members.containsKey(member.getMemberID())) {
            System.out.println("Member with ID " + member.getMemberID() + " already exists");
            return false;
        }
        
        members.put(member.getMemberID(), member);
        System.out.println("Member added: " + member.getName());
        return true;
    }
    
    /**
     * Remove a member from the library
     * @param memberID The ID of the member to remove
     * @return true if removed successfully, false otherwise
     */
    public boolean removeMember(long memberID) {
        Member member = members.get(memberID);
        
        if (member == null) {
            System.out.println("Member with ID " + memberID + " not found");
            return false;
        }
        
        // Check if member has borrowed books
        if (!member.getBorrowedBooks().isEmpty()) {
            System.out.println("Cannot remove member. They have " + 
                             member.getBorrowedBooksCount() + " borrowed book(s)");
            return false;
        }
        
        members.remove(memberID);
        System.out.println("Member removed: " + member.getName());
        return true;
    }
    
    /**
     * Find a member by ID
     * @param memberID The member ID to search for
     * @return The member if found, null otherwise
     */
    public Member findMember(long memberID) {
        return members.get(memberID);
    }
    
    /**
     * Search for members by name (partial match, case-insensitive)
     * @param name The name to search for
     * @return List of matching members
     */
    public ArrayList<Member> searchMembersByName(String name) {
        ArrayList<Member> results = new ArrayList<>();
        String searchTerm = name.toLowerCase();
        
        for (Member member : members.values()) {
            if (member.getName().toLowerCase().contains(searchTerm)) {
                results.add(member);
            }
        }
        
        return results;
    }
    
    /**
     * Get all members in the library
     * @return ArrayList of all members
     */
    public ArrayList<Member> getAllMembers() {
        return new ArrayList<>(members.values());
    }
    
    // ==================== CHECKOUT/RETURN OPERATIONS ====================
    
    /**
     * Checkout a book to a member
     * @param memberID The member's ID
     * @param ISBN The book's ISBN
     * @return true if checkout successful, false otherwise
     */
    public boolean checkoutBook(long memberID, long ISBN) {
        // Find member
        Member member = findMember(memberID);
        if (member == null) {
            System.out.println("Error: Member with ID " + memberID + " not found");
            return false;
        }
        
        // Find book
        Book book = findBook(ISBN);
        if (book == null) {
            System.out.println("Error: Book with ISBN " + ISBN + " not found");
            return false;
        }
        
        // Check if book is available
        if (!"Available".equalsIgnoreCase(book.getStatus())) {
            System.out.println("Error: Book '" + book.getTitle() + "' is not available");
            return false;
        }
        
        // Check if member can borrow more books
        if (!member.canBorrowMore()) {
            System.out.println("Error: " + member.getName() + 
                             " has reached the maximum borrowing limit");
            return false;
        }
        
        // Perform checkout
        if (member.borrowBook(book)) {
            System.out.println("Success: '" + book.getTitle() + 
                             "' checked out to " + member.getName());
            return true;
        }
        
        return false;
    }
    
    /**
     * Return a book from a member
     * @param memberID The member's ID
     * @param ISBN The book's ISBN
     * @return true if return successful, false otherwise
     */
    public boolean returnBook(long memberID, long ISBN) {
        // Find member
        Member member = findMember(memberID);
        if (member == null) {
            System.out.println("Error: Member with ID " + memberID + " not found");
            return false;
        }
        
        // Find book
        Book book = findBook(ISBN);
        if (book == null) {
            System.out.println("Error: Book with ISBN " + ISBN + " not found");
            return false;
        }
        
        // Check if member has this book
        if (!member.getBorrowedBooks().contains(book)) {
            System.out.println("Error: " + member.getName() + 
                             " hasn't borrowed '" + book.getTitle() + "'");
            return false;
        }
        
        // Perform return
        if (member.returnBook(book)) {
            System.out.println("Success: '" + book.getTitle() + 
                             "' returned by " + member.getName());
            return true;
        }
        
        return false;
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get total number of books in library
     * @return Total book count
     */
    public int getTotalBooks() {
        return books.size();
    }
    
    /**
     * Get total number of members
     * @return Total member count
     */
    public int getTotalMembers() {
        return members.size();
    }
    
    /**
     * Get number of available books
     * @return Count of available books
     */
    public int getAvailableBooksCount() {
        return getAvailableBooks().size();
    }
    
    /**
     * Get number of borrowed books
     * @return Count of borrowed books
     */
    public int getBorrowedBooksCount() {
        int count = 0;
        for (Book book : books.values()) {
            if ("Borrowed".equalsIgnoreCase(book.getStatus())) {
                count++;
            }
        }
        return count;
    }
    
    /**
     * Display library statistics
     */
    public void displayStatistics() {
        System.out.println("\n=== LIBRARY STATISTICS ===");
        System.out.println("Total Books: " + getTotalBooks());
        System.out.println("Available Books: " + getAvailableBooksCount());
        System.out.println("Borrowed Books: " + getBorrowedBooksCount());
        System.out.println("Total Members: " + getTotalMembers());
        System.out.println("========================\n");
    }
    
    // ==================== DATA PERSISTENCE ====================
    
    /**
     * Save library data to file
     * @return true if saved successfully, false otherwise
     */
    public boolean saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(LIBRARY_DATA_FILE);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
            System.out.println("Library data saved successfully to " + LIBRARY_DATA_FILE);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving library data: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
    
    /**
     * Load library data from file
     * @return Library object if loaded successfully, new Library otherwise
     */
    public static Library loadFromFile() {
        try (FileInputStream fileIn = new FileInputStream(LIBRARY_DATA_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            Library library = (Library) in.readObject();
            System.out.println("Library data loaded successfully from " + LIBRARY_DATA_FILE);
            return library;
        } catch (FileNotFoundException e) {
            System.out.println("No saved data found. Creating new library.");
            return new Library();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading library data: " + e.getMessage());
            return new Library();
        }
    }
    
    @Override
    public String toString() {
        return "Library{" +
               "totalBooks=" + getTotalBooks() +
               ", availableBooks=" + getAvailableBooksCount() +
               ", borrowedBooks=" + getBorrowedBooksCount() +
               ", totalMembers=" + getTotalMembers() +
               '}';
    }
}