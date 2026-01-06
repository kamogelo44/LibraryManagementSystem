package com.obcodes.librarymanagementsystem.models;
import java.io.*;
import java.util.ArrayList;
import java.util.Random;

/**
 * @author Obakeng Phale
 */
public class Member implements Serializable {
    private static final long serialVersionUID = 1L;
    private static final String FILENAME = "data/members.dat";
    private static final int MAX_BORROWED_BOOKS = 5;
    
    private final long memberID;
    private String name;
    private ArrayList<Book> borrowedBooks;
   
    public Member(String name) {
        this.memberID = generateMemberID();
        this.name = name;
        this.borrowedBooks = new ArrayList<>();
    }
   
    // Auto-generate member ID
    private long generateMemberID() {
        long min = 100000000000L; // Start from 100 billion (12 digits)
        long max = 999999999999L; // Up to 999 billion (12 digits)
       
        Random random = new Random();
        return min + (long)(random.nextDouble() * (max - min + 1));
    }
   
    // Getters
    public long getMemberID() {
        return memberID;
    }
    
    public String getName() {
        return name;
    }
    
    public ArrayList<Book> getBorrowedBooks() {
        return borrowedBooks;
    }
    
    // Setters
    public void setName(String name) {
        this.name = name;
    }
   
    /**
     * Borrow a book if member hasn't reached the limit
     * @param book The book to borrow
     * @return true if successful, false otherwise
     */
    public boolean borrowBook(Book book) {
        // Check if member can borrow more books
        if (borrowedBooks.size() >= MAX_BORROWED_BOOKS) {
            System.out.println("Cannot borrow more books. Limit reached (" + MAX_BORROWED_BOOKS + ")");
            return false;
        }
        
        // Check if book is available
        if (!book.getStatus().equalsIgnoreCase("Available")) {
            System.out.println("Book is not available for borrowing");
            return false;
        }
        
        // Check if member already has this book
        if (borrowedBooks.contains(book)) {
            System.out.println("You already have this book");
            return false;
        }
        
        // Borrow the book
        borrowedBooks.add(book);
        book.setStatus("Borrowed");
        return true;
    }
   
    /**
     * Return a borrowed book
     * @param book The book to return
     * @return true if successful, false otherwise
     */
    public boolean returnBook(Book book) {
        if (borrowedBooks.remove(book)) {
            book.setStatus("Available");
            return true;
        }
        System.out.println("This book was not borrowed by this member");
        return false;
    }
    
    /**
     * Get the number of books currently borrowed
     * @return number of borrowed books
     */
    public int getBorrowedBooksCount() {
        return borrowedBooks.size();
    }
    
    /**
     * Check if member can borrow more books
     * @return true if member can borrow more books
     */
    public boolean canBorrowMore() {
        return borrowedBooks.size() < MAX_BORROWED_BOOKS;
    }
    
    /**
     * Save this member to file
     */
    public void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(FILENAME);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
            System.out.println("Member serialized and saved to " + FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "Member{" +
               "memberID=" + memberID +
               ", name='" + name + '\'' +
               ", borrowedBooks=" + borrowedBooks.size() +
               " book(s)" +
               '}';
    }
    
    /**
     * Display all borrowed books
     */
    public void displayBorrowedBooks() {
        if (borrowedBooks.isEmpty()) {
            System.out.println(name + " has no borrowed books.");
        } else {
            System.out.println(name + "'s borrowed books:");
            for (Book book : borrowedBooks) {
                System.out.println("  - " + book.getTitle() + " by " + book.getAuthor());
            }
        }
    }
}