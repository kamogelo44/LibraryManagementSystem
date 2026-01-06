package com.obcodes.librarymanagementsystem.models;
import java.io.*;
import java.util.Random;

/**
 * @author Obakeng Phale
 */
public class Book implements Serializable {
  
    private static final long serialVersionUID = 1L;  
    private static final String FILENAME = "data/books.dat";
    
    private String title;
    private String author;
    private long ISBN;
    private String status = "Available";
  
    // Constructor
    public Book(String title, String author, long ISBN, String status) {
        this.title = title;
        this.author = author;
        // Use provided ISBN if valid, otherwise generate one
        this.ISBN = (ISBN > 0) ? ISBN : generateISBN();
        this.status = status;
    }
    
    // Overloaded constructor that auto-generates ISBN
    public Book(String title, String author, String status) {
        this.title = title;
        this.author = author;
        this.ISBN = generateISBN();
        this.status = status;
    }
  
    // Auto-generate ISBN
    private long generateISBN() { 
        Random random = new Random();
        long min = 9780000000000L;
        long max = 9789999999999L;
        return min + (long)(random.nextDouble() * (max - min + 1));
    }
    
    // Getters
    public long getISBN() {
        return ISBN;
    }
    
    public String getTitle() {
        return title;
    }
    
    public String getAuthor() {
        return author;
    }
    
    public String getStatus() {
        return status;
    }
    
    // Setters
    public void setTitle(String title) {
        this.title = title;
    }
    
    public void setAuthor(String author) {
        this.author = author;
    }
    
    public void setISBN(long ISBN) {
        this.ISBN = ISBN;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    // borrowBook() method
    public boolean borrowBook() {
        if ("Available".equalsIgnoreCase(status)) {
            status = "Borrowed";
            return true;
        }
        return false;
    }
    
    // returnBook() method (you'll likely need this too)
    public boolean returnBook() {
        if ("Borrowed".equalsIgnoreCase(status)) {
            status = "Available";
            return true;
        }
        return false;
    }
    
    // Save this book to file
    public void saveToFile() {
        try (FileOutputStream fileOut = new FileOutputStream(FILENAME);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            out.writeObject(this);
            System.out.println("Object Serialized and saved to " + FILENAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public String toString() {
        return "Book{" +
               "ISBN=" + ISBN +
               ", title='" + title + '\'' +
               ", author='" + author + '\'' +
               ", status='" + status + '\'' +
               '}';
    }
}