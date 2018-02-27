package com.aurghyadip.librarymanagementlibrarian;


public class Book {
    public String title;
    public String author;
    public String description;
    public int copies;

    public Book() {
    }

    public Book(String title, String author, String description, int copies) {
        this.title = title;
        this.author = author;
        this.description = description;
        this.copies = copies;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public String getDescription() {
        return description;
    }

    public int getCopies() {
        return copies;
    }
}
