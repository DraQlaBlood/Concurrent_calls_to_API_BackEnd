package com.hatchways.backEnd.model;


import java.util.List;
import java.util.Objects;

public class Post implements Comparable<Post>{
    private int id;
    private String author;
    private  int authorId;
    private int likes;
    private double popularity;
    private int reads;
    private List<String> tags;

    public Post() {
    }

    public Post(int id, String author, int authorId, int likes, double popularity, int reads, List<String> tags) {
        this.id = id;
        this.author = author;
        this.authorId = authorId;
        this.likes = likes;
        this.popularity = popularity;
        this.reads = reads;
        this.tags = tags;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public int getAuthorId() {
        return authorId;
    }

    public void setAuthorId(int authorId) {
        this.authorId = authorId;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    public double getPopularity() {
        return popularity;
    }

    public void setPopularity(double popularity) {
        this.popularity = popularity;
    }

    public int getReads() {
        return reads;
    }

    public void setReads(int reads) {
        this.reads = reads;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Post)) return false;
        Post post = (Post) o;
        return getId() == post.getId();
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    @Override
    public String toString() {
        return "{" +
                "id=" + id +
                ", author='" + author + '\'' +
                ", authorId=" + authorId +
                ", likes=" + likes +
                ", popularity=" + popularity +
                ", reads=" + reads +
                ", tags=" + tags +
                '}';
    }

    @Override
    public int compareTo(Post o) {
        return this.id -o.id;
    }
}
