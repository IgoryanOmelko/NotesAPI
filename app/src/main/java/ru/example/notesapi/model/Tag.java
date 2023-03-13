package ru.example.notesapi.model;

public class Tag {
    public int id;
    public String name;
    public boolean inChoice;

    @Override
    public String toString() {
        return name;
    }

    public int getID() {
        return id;
    }
}
