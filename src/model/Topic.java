

package model;

/**
 * Model data untuk merepresentasikan sebuah Topik / Buku
 * dalam Library Knowledge Navigator.
 */
public class Topic {
    private String id;
    private String title;
    private String category;
    private String description;
    private int duration;
    private int publishYear;

    public Topic(String id, String title, String category, String description, int duration, int publishYear) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.publishYear = publishYear;
    }

    public Topic(String id, String title, String category, String description, int duration) {
        this(id, title, category, description, duration, 2020);
    }

    public Topic(String id, String title, String category, String description) {
        this(id, title, category, description, 0, 2020);
    }

    public Topic(String id, String title) {
        this(id, title, "General", "", 0, 2020);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public int getPublishYear() { return publishYear; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %d Jam - Terbit: %d", id, title, category, duration, publishYear);
    }
}