/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package dao;

import javax.swing.JOptionPane;

/**
 *
 * @author Kavini
 */
public class Tables {

    public static void main(String[] args) {
        try {
            String usertable = "create table user(id int AUTO_INCREMENT primary key,name varchar(200),email varchar(200), mobile varchar(10),address varchar(200),password varchar(200), security varchar(200), answer varchar(200), status varchar(20), UNIQUE (email))";
            String adminDetails = "insert into user(name,email,mobile,address,password,security,answer,status) values('Admin','admin@gmail.com','0718224561','Sri Lanka','admin','test','admintest','true') ";
            String reservationtableTable = "create table reservationtable(id int AUTO_INCREMENT primary key, name varchar(200))";
            String bookingTable = "create table booking(id int AUTO_INCREMENT primary key, name varchar(200),email varchar(200),mobile varchar(10),guests int,tableNo varchar(10),date varchar(10),start_time varchar(10),end_time varchar(10))";
            DbOperations.setDataOrDelete(usertable, "User Table Created Successfully");
            DbOperations.setDataOrDelete(adminDetails, "Admin Details Added Successfully");
            DbOperations.setDataOrDelete(reservationtableTable, "Reservation Tables Table Created Successfully");
            DbOperations.setDataOrDelete(bookingTable, "Bookings Table Created Successfully");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, e);
        }
    }
}
