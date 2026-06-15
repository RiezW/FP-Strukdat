

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

    public Topic(String id, String title, String category, String description, int duration) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.duration = duration;
    }

    public Topic(String id, String title, String category, String description) {
        this(id, title, category, description, 0);
    }

    public Topic(String id, String title) {
        this(id, title, "General", "", 0);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %d Jam", id, title, category, duration);
    }
}