
package tree;

import model.Topic;
import java.util.*;

/**
 * Trie (Prefix Tree) untuk pencarian topik berdasarkan prefix kata kunci.
 *
 * Cara kerja:
 * - Setiap karakter dari judul topik disimpan sebagai node
 * - Pencarian prefix berjalan O(m) dimana m = panjang prefix
 * - Mendukung autocomplete / pencarian parsial
 *
 * Contoh:
 *   Insert "Algoritma" → A-l-g-o-r-i-t-m-a (isEnd=true)
 *   Insert "Aljabar"   → A-l-j-a-b-a-r (isEnd=true)
 *   Search "Alg"       → returns ["Algoritma"]
 */
public class Trie {

    private TrieNode root;
    // Menyimpan mapping dari topicId ke Topic object
    private Map<String, Topic> topicMap;

    public Trie() {
        root = new TrieNode();
        topicMap = new HashMap<>();
    }

    /**
     * Menyisipkan judul topik ke dalam Trie.
     * Judul dikonversi ke lowercase untuk pencarian case-insensitive.
     *
     * @param topic Objek Topic yang akan disisipkan
     */
    public void insert(Topic topic) {
        topicMap.put(topic.getId(), topic);
        String title = topic.getTitle().toLowerCase();
        TrieNode current = root;

        for (char ch : title.toCharArray()) {
            // Buat node baru jika karakter belum ada
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }

        // Tandai akhir kata dan simpan topicId
        current.isEndOfWord = true;
        current.topicId = topic.getId();
    }

    /**
     * Mencari semua topik yang judulnya diawali dengan prefix tertentu.
     *
     * @param prefix Kata kunci pencarian (prefix)
     * @return List<Topic> semua topik yang cocok
     */
    public List<Topic> searchByPrefix(String prefix) {
        List<Topic> results = new ArrayList<>();
        TrieNode current = root;
        String lowerPrefix = prefix.toLowerCase();

        // Telusuri Trie sesuai prefix
        for (char ch : lowerPrefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return results; // Prefix tidak ditemukan
            }
            current = current.children.get(ch);
        }

        // Kumpulkan semua topik dari node prefix ini ke bawah (DFS)
        collectAllTopics(current, results);
        return results;
    }

    /**
     * Mencari topik yang judulnya TEPAT sama dengan keyword.
     *
     * @param title Judul lengkap topik
     * @return Topic jika ditemukan, null jika tidak
     */
    public Topic searchExact(String title) {
        TrieNode current = root;
        String lowerTitle = title.toLowerCase();

        for (char ch : lowerTitle.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return null;
            }
            current = current.children.get(ch);
        }

        if (current.isEndOfWord && current.topicId != null) {
            return topicMap.get(current.topicId);
        }
        return null;
    }

    /**
     * Helper: kumpulkan semua topik (DFS dari node tertentu).
     */
    private void collectAllTopics(TrieNode node, List<Topic> results) {
        if (node.isEndOfWord && node.topicId != null) {
            Topic t = topicMap.get(node.topicId);
            if (t != null) results.add(t);
        }
        for (TrieNode child : node.children.values()) {
            collectAllTopics(child, results);
        }
    }

    /**
     * Menghapus topik dari Trie berdasarkan judul.
     *
     * @param title Judul topik yang akan dihapus
     * @return true jika berhasil dihapus
     */
    public boolean delete(String title) {
        return deleteHelper(root, title.toLowerCase(), 0);
    }

    private boolean deleteHelper(TrieNode current, String word, int index) {
        if (index == word.length()) {
            if (!current.isEndOfWord) return false;
            if (current.topicId != null) topicMap.remove(current.topicId);
            current.isEndOfWord = false;
            current.topicId = null;
            return current.children.isEmpty();
        }
        char ch = word.charAt(index);
        TrieNode child = current.children.get(ch);
        if (child == null) return false;

        boolean shouldDeleteChild = deleteHelper(child, word, index + 1);
        if (shouldDeleteChild) {
            current.children.remove(ch);
            return current.children.isEmpty() && !current.isEndOfWord;
        }
        return false;
    }

    /**
     * Menampilkan semua topik yang ada dalam Trie.
     */
    public void displayAll() {
        System.out.println("\n=== SEMUA TOPIK DALAM TRIE ===");
        List<Topic> all = new ArrayList<>();
        collectAllTopics(root, all);
        if (all.isEmpty()) {
            System.out.println("(tidak ada topik)");
        } else {
            for (int i = 0; i < all.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, all.get(i));
            }
        }
    }

    public Map<String, Topic> getTopicMap() {
        return topicMap;
    }
}