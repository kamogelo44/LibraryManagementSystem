/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.obcodes.librarymanagementsystem.models;
import java.util.HashMap;
/**
 *
 * @author Obakeng Phale
 */
public class Library {
    private HashMap<Long, Book> books;
    private HashMap<Long, Member> members;
    
    public Library(){
        books = new HashMap<>();
        members = new HashMap<>();
        
    }
}
