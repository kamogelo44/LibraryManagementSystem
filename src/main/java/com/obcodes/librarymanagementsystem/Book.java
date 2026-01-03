/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.obcodes.librarymanagementsystem;
import java.util.Random;
/**
 *
 * @author Obakeng Phale
 */
public class Book {
  private String title;
  private String author;
  private long ISBN;
  private String status ="Unavailable";
  
    public Book(String title, String author){
    this.ISBN = generateISBN();
    this.title = title;
    this.author = author;
  
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
}
