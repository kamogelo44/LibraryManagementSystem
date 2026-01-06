/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.obcodes.librarymanagementsystem.models;
import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author Obakeng Phale
 */
public class Member {
   private final long memberID;
   private String name;
   private ArrayList<Book> borrowedBooks;
   
   public Member(String name){
   this.memberID = generateMemberID();
   this.name = name;
   this.borrowedBooks = new ArrayList<>();
   
   }
   //auto-generate ID
   private long generateMemberID(){
       long min = 000000000000L;
       long max = 999999999999L;
       
       Random random = new Random();
       return min + (long)(random.nextDouble() * (max - min + 1));
   }
   
   public long getMemberID(){
       return memberID;
   }
   public boolean borrowBook(Book book){
       if (borrowedBooks.size()<5){
           borrowedBooks.add(book);
           return true;
           
       }
       
       return false;
   }
   
   public boolean returnBook(Book book){
   
       return borrowedBooks.remove(book);
       
   }
   
   public ArrayList<Book> getBorrowedBooks(){
    return borrowedBooks;
   }
   
}
