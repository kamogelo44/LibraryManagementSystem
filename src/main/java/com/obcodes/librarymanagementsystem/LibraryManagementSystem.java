/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package com.obcodes.librarymanagementsystem;

/**
 *
 * @author Obakeng Phale
 */
public class LibraryManagementSystem {

    public static void main(String[] args) {
        //tests to see if the classes and methods work as intended.
        Book book = new Book("Introduction to Slavery", "John Van Reebek" );
        Member member = new Member("Lukas Mashamahite");
        System.out.println("Book: ");
        System.out.println("Member ID: "+ member.getMemberID());
        
    }
}
