
package tree;

import java.util.HashMap;
import java.util.Map;

/**
 * Node dalam struktur Trie.
 * Setiap node menyimpan karakter dan referensi ke anak-anaknya.
 */
public class TrieNode {
    // Map dari karakter ke child node (case-insensitive)
    Map<Character, TrieNode> children;

    // Menandai apakah node ini adalah akhir dari sebuah kata/topik
    boolean isEndOfWord;

    // Menyimpan topicId jika ini adalah akhir kata
    String topicId;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        topicId = null;
    }
}