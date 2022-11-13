/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package models;

/**
 *
 * @author MoaathAlrajab
 */
public class Person {
    private String id;
    private String name;
    private String major;
    private int age;


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Person(String id, String name, String major, int age) {
        this.id = id;
        this.name = name;
        this.major = major;
        this.age = age;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String Major) {
        this.major = Major;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }
    
    
    
}
