/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.obcodes.librarymanagementsystem.models;
import java.util.Random;
/**
 *
 * @author Obakeng Phale
 */
public class Book {
  private String title;
  private String author;
  private long ISBN;
  private String status ="Available";
  
    public Book(String title, String author, long ISBN, String status){
    this.ISBN = generateISBN();
    this.title = title;
    this.author = author;
    this.status = status;
    }
  
    //auto-genetate ISBN
    private long generateISBN(){ 
        Random random = new Random();
        //ISBN =number generator
        long min = 9780000000000L; // Smallest 13-digit number
        long max = 9789999999999L; // Largest 13-digit number
        return min + (long)(random.nextDouble() * (max - min + 1));
    }
    
    public long getISBN(){
        return ISBN;
    }
    
    public String getTitle(){
        return title;
    }
    
    public String getAuthor(){
        return author;
    }
    
    public String getStatus(){
        return status;
    }
    
    public void setStatus(String status){
        this.status = status;
    }
    
}
