package com.greekk;

import java.util.UUID;

public class Manager extends Person{
    private String position;
    private UUID id;

    public Manager(){
    }

    public Manager(String position){
        this.position = position;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getPosition(){
        return this.position.toString();
    }

    public void setPosition(String position){
        this.position = position;
    }

    public void sellMerch(String tshirt){
        System.out.println("I'm have sell tshirt " + "!!");
    }

    public void sellMerch(String tshirt, String trousers){
        System.out.println("I'm have sell tshirt " + tshirt + "and trousers " + trousers + "!!");
    }

    public void sellMerch(String tshirt, String trousers, String socks){
        System.out.println("I'm have sell tshirt " + tshirt + ", trousers " + trousers + " and socks "+ socks + "!!");
    }

}
