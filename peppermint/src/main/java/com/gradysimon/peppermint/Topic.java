package com.gradysimon.peppermint;

/**
 * Created by grady on 2/14/14.
 */
public class Topic implements Comparable<Topic>{
    private int id;
    private UserProfile author;
    private String text;

    public Topic(int id, UserProfile author, String text) {
        this.id = id;
        this.author = author;
        this.text = text;
    }
    public Topic() {
        // no-args constructor for gson
    }

    public String getText() {
        return this.text;
    }

    public UserProfile getAuthor() {
        return this.author;
    }

    public String toString() {
        return "Author: " + this.author.getWholeName() + "\t Text: " + this.text;
    }

    @Override
    public int compareTo(Topic topic) {
        return this.id - topic.id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Topic)) return false;

        Topic topic = (Topic) o;

        if (id != topic.id) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id;
    }
}
