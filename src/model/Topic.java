

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

    public Topic(String id, String title, String category, String description) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
    }

    public Topic(String id, String title) {
        this(id, title, "General", "");
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s)", id, title, category);
    }
}