package com.obcodes.librarymanagementsystem.services;

import com.obcodes.librarymanagementsystem.models.Book;
import com.obcodes.librarymanagementsystem.models.Member;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * FileService handles all file operations for the Library Management System
 * Provides data persistence through serialization
 * @author Obakeng Phale
 */
public class FileService {
    // File paths
    private static final String DATA_DIR = "data";
    private static final String BACKUP_DIR = "data/backups";
    private static final String BOOKS_FILE = DATA_DIR + "/books.dat";
    private static final String MEMBERS_FILE = DATA_DIR + "/members.dat";
    
    // Backup file naming
    private static final DateTimeFormatter BACKUP_FORMATTER = 
        DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    
    /**
     * Initialize FileService - creates necessary directories
     */
    public FileService() {
        initializeDirectories();
    }
    
    /**
     * Create data and backup directories if they don't exist
     */
    private void initializeDirectories() {
        try {
            Path dataPath = Paths.get(DATA_DIR);
            Path backupPath = Paths.get(BACKUP_DIR);
            
            if (!Files.exists(dataPath)) {
                Files.createDirectories(dataPath);
                System.out.println("Created data directory: " + DATA_DIR);
            }
            
            if (!Files.exists(backupPath)) {
                Files.createDirectories(backupPath);
                System.out.println("Created backup directory: " + BACKUP_DIR);
            }
        } catch (IOException e) {
            System.err.println("Error creating directories: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    // ==================== BOOK OPERATIONS ====================
    
    /**
     * Save books to file
     * @param books HashMap of books to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveBooks(HashMap<Long, Book> books) {
        if (books == null) {
            System.err.println("Cannot save null books collection");
            return false;
        }
        
        // Create backup before saving
        createBackup(BOOKS_FILE);
        
        try (FileOutputStream fileOut = new FileOutputStream(BOOKS_FILE);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            
            out.writeObject(books);
            System.out.println("Successfully saved " + books.size() + " book(s) to " + BOOKS_FILE);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error saving books: " + e.getMessage());
            e.printStackTrace();
            
            // Attempt to restore from backup
            restoreFromBackup(BOOKS_FILE);
            return false;
        }
    }
    
    /**
     * Load books from file
     * @return HashMap of books, or empty HashMap if file doesn't exist or error occurs
     */
    @SuppressWarnings("unchecked")
    public HashMap<Long, Book> loadBooks() {
        File file = new File(BOOKS_FILE);
        
        // If file doesn't exist, return empty HashMap
        if (!file.exists()) {
            System.out.println("Books file not found. Starting with empty collection.");
            return new HashMap<>();
        }
        
        try (FileInputStream fileIn = new FileInputStream(BOOKS_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            
            HashMap<Long, Book> books = (HashMap<Long, Book>) in.readObject();
            System.out.println("Successfully loaded " + books.size() + " book(s) from " + BOOKS_FILE);
            return books;
            
        } catch (FileNotFoundException e) {
            System.out.println("Books file not found. Starting with empty collection.");
            return new HashMap<>();
            
        } catch (EOFException e) {
            System.err.println("Books file is empty or corrupted. Starting with empty collection.");
            // Try to restore from backup
            if (restoreFromBackup(BOOKS_FILE)) {
                return loadBooks(); // Try loading again after restore
            }
            return new HashMap<>();
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading books: " + e.getMessage());
            e.printStackTrace();
            
            // Try to restore from backup
            if (restoreFromBackup(BOOKS_FILE)) {
                return loadBooks(); // Try loading again after restore
            }
            return new HashMap<>();
        }
    }
    
    // ==================== MEMBER OPERATIONS ====================
    
    /**
     * Save members to file
     * @param members HashMap of members to save
     * @return true if saved successfully, false otherwise
     */
    public boolean saveMembers(HashMap<Long, Member> members) {
        if (members == null) {
            System.err.println("Cannot save null members collection");
            return false;
        }
        
        // Create backup before saving
        createBackup(MEMBERS_FILE);
        
        try (FileOutputStream fileOut = new FileOutputStream(MEMBERS_FILE);
             ObjectOutputStream out = new ObjectOutputStream(fileOut)) {
            
            out.writeObject(members);
            System.out.println("Successfully saved " + members.size() + " member(s) to " + MEMBERS_FILE);
            return true;
            
        } catch (IOException e) {
            System.err.println("Error saving members: " + e.getMessage());
            e.printStackTrace();
            
            // Attempt to restore from backup
            restoreFromBackup(MEMBERS_FILE);
            return false;
        }
    }
    
    /**
     * Load members from file
     * @return HashMap of members, or empty HashMap if file doesn't exist or error occurs
     */
    @SuppressWarnings("unchecked")
    public HashMap<Long, Member> loadMembers() {
        File file = new File(MEMBERS_FILE);
        
        // If file doesn't exist, return empty HashMap
        if (!file.exists()) {
            System.out.println("Members file not found. Starting with empty collection.");
            return new HashMap<>();
        }
        
        try (FileInputStream fileIn = new FileInputStream(MEMBERS_FILE);
             ObjectInputStream in = new ObjectInputStream(fileIn)) {
            
            HashMap<Long, Member> members = (HashMap<Long, Member>) in.readObject();
            System.out.println("Successfully loaded " + members.size() + " member(s) from " + MEMBERS_FILE);
            return members;
            
        } catch (FileNotFoundException e) {
            System.out.println("Members file not found. Starting with empty collection.");
            return new HashMap<>();
            
        } catch (EOFException e) {
            System.err.println("Members file is empty or corrupted. Starting with empty collection.");
            // Try to restore from backup
            if (restoreFromBackup(MEMBERS_FILE)) {
                return loadMembers(); // Try loading again after restore
            }
            return new HashMap<>();
            
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Error loading members: " + e.getMessage());
            e.printStackTrace();
            
            // Try to restore from backup
            if (restoreFromBackup(MEMBERS_FILE)) {
                return loadMembers(); // Try loading again after restore
            }
            return new HashMap<>();
        }
    }
    
    // ==================== BACKUP OPERATIONS ====================
    
    /**
     * Create a backup of a file before modifying it
     * @param filePath Path to the file to backup
     * @return true if backup created successfully, false otherwise
     */
    private boolean createBackup(String filePath) {
        File sourceFile = new File(filePath);
        
        // Only create backup if source file exists
        if (!sourceFile.exists()) {
            return false;
        }
        
        try {
            // Generate backup filename with timestamp
            String fileName = sourceFile.getName();
            String timestamp = LocalDateTime.now().format(BACKUP_FORMATTER);
            String backupFileName = fileName.replace(".dat", "_" + timestamp + ".dat");
            Path backupPath = Paths.get(BACKUP_DIR, backupFileName);
            
            // Copy file to backup location
            Files.copy(sourceFile.toPath(), backupPath, StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Backup created: " + backupPath);
            
            // Clean old backups (keep only last 5)
            cleanOldBackups(fileName);
            
            return true;
            
        } catch (IOException e) {
            System.err.println("Error creating backup: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Restore a file from the most recent backup
     * @param filePath Path to the file to restore
     * @return true if restored successfully, false otherwise
     */
    private boolean restoreFromBackup(String filePath) {
        try {
            File sourceFile = new File(filePath);
            String fileName = sourceFile.getName();
            String baseFileName = fileName.replace(".dat", "");
            
            // Find the most recent backup
            File backupDir = new File(BACKUP_DIR);
            File[] backups = backupDir.listFiles((dir, name) -> 
                name.startsWith(baseFileName) && name.endsWith(".dat"));
            
            if (backups == null || backups.length == 0) {
                System.err.println("No backup files found for " + fileName);
                return false;
            }
            
            // Sort by last modified (most recent first)
            File mostRecentBackup = backups[0];
            for (File backup : backups) {
                if (backup.lastModified() > mostRecentBackup.lastModified()) {
                    mostRecentBackup = backup;
                }
            }
            
            // Restore the backup
            Files.copy(mostRecentBackup.toPath(), sourceFile.toPath(), 
                      StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Restored from backup: " + mostRecentBackup.getName());
            return true;
            
        } catch (IOException e) {
            System.err.println("Error restoring from backup: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Clean old backup files, keeping only the most recent ones
     * @param fileName Base filename to clean backups for
     */
    private void cleanOldBackups(String fileName) {
        try {
            String baseFileName = fileName.replace(".dat", "");
            File backupDir = new File(BACKUP_DIR);
            
            File[] backups = backupDir.listFiles((dir, name) -> 
                name.startsWith(baseFileName) && name.endsWith(".dat"));
            
            if (backups == null || backups.length <= 5) {
                return; // Keep all if 5 or fewer
            }
            
            // Sort by last modified (oldest first)
            java.util.Arrays.sort(backups, 
                (f1, f2) -> Long.compare(f1.lastModified(), f2.lastModified()));
            
            // Delete oldest backups, keep only 5 most recent
            int toDelete = backups.length - 5;
            for (int i = 0; i < toDelete; i++) {
                if (backups[i].delete()) {
                    System.out.println("Deleted old backup: " + backups[i].getName());
                }
            }
            
        } catch (Exception e) {
            System.err.println("Error cleaning old backups: " + e.getMessage());
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Check if books file exists
     * @return true if file exists, false otherwise
     */
    public boolean booksFileExists() {
        return new File(BOOKS_FILE).exists();
    }
    
    /**
     * Check if members file exists
     * @return true if file exists, false otherwise
     */
    public boolean membersFileExists() {
        return new File(MEMBERS_FILE).exists();
    }
    
    /**
     * Delete all data files (for testing/reset purposes)
     * @return true if deleted successfully, false otherwise
     */
    public boolean deleteAllData() {
        boolean booksDeleted = true;
        boolean membersDeleted = true;
        
        File booksFile = new File(BOOKS_FILE);
        File membersFile = new File(MEMBERS_FILE);
        
        if (booksFile.exists()) {
            booksDeleted = booksFile.delete();
        }
        
        if (membersFile.exists()) {
            membersDeleted = membersFile.delete();
        }
        
        if (booksDeleted && membersDeleted) {
            System.out.println("All data files deleted successfully");
            return true;
        } else {
            System.err.println("Error deleting some data files");
            return false;
        }
    }
    
    /**
     * Get file information
     * @param filePath Path to the file
     * @return String with file information
     */
    public String getFileInfo(String filePath) {
        File file = new File(filePath);
        
        if (!file.exists()) {
            return "File does not exist: " + filePath;
        }
        
        long sizeInBytes = file.length();
        double sizeInKB = sizeInBytes / 1024.0;
        String lastModified = new java.util.Date(file.lastModified()).toString();
        
        return String.format("File: %s\nSize: %.2f KB\nLast Modified: %s", 
                           filePath, sizeInKB, lastModified);
    }
    
    /**
     * Display information about all data files
     */
    public void displayFilesInfo() {
        System.out.println("\n=== DATA FILES INFORMATION ===");
        System.out.println(getFileInfo(BOOKS_FILE));
        System.out.println("\n" + getFileInfo(MEMBERS_FILE));
        System.out.println("==============================\n");
    }
}