package com.greekk;

import java.util.Objects;

public class Person {
    String name;
    String surname;
    float height;
    float weight;

    //constructors
    public void Person(String name){
        this.name = name;
    }
    public void Person(String name, String surname){
        this.name = name;
        this.surname = surname;
    }
    public void Person(float height){
        this.height = height;
    }
    public void Person(float height, float weight){
        this.height = height;
        this.weight = weight;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return this.surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public float getHeight() {
        return this.height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getWeight() {
        return this.weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Float.compare(person.height, height) == 0 &&
                Float.compare(person.weight, weight) == 0 &&
                Objects.equals(name, person.name) &&
                Objects.equals(surname, person.surname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, surname, height, weight);
    }
}
