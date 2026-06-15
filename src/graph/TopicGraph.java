

package graph;

import model.Topic;
import java.util.*;

/**
 * Directed Graph menggunakan Adjacency List untuk merepresentasikan
 * hubungan prasyarat antar topik/buku.
 *
 * Edge A → B berarti: "Topik A adalah PRASYARAT dari Topik B"
 * (harus pelajari A sebelum B)
 *
 * Struktur adjacency list:
 *   "Variabel" → ["Fungsi", "Array"]
 *   "Fungsi"   → ["Rekursi", "OOP"]
 *   "Array"    → ["Rekursi", "Sorting"]
 *
 * Kompleksitas:
 *   - Ruang: O(V + E)
 *   - Tambah node: O(1)
 *   - Tambah edge: O(1)
 *   - Iterasi tetangga: O(degree)
 */
public class TopicGraph {

    // adjacencyList[topicId] = list of topicId yang membutuhkan topik ini sebagai prasyarat
    private Map<String, List<String>> adjacencyList;

    // reverseList[topicId] = list of prasyarat dari topik ini
    private Map<String, List<String>> reverseList;

    // Menyimpan data topik
    private Map<String, Topic> topics;

    public TopicGraph() {
        adjacencyList = new LinkedHashMap<>();
        reverseList = new LinkedHashMap<>();
        topics = new LinkedHashMap<>();
    }

    /**
     * Menambahkan topik baru ke dalam graph.
     *
     * @param topic Topik yang akan ditambahkan
     */
    public void addTopic(Topic topic) {
        String id = topic.getId();
        topics.put(id, topic);
        adjacencyList.putIfAbsent(id, new ArrayList<>());
        reverseList.putIfAbsent(id, new ArrayList<>());
    }

    /**
     * Menambahkan edge prasyarat: prerequisiteId → dependentId
     * Artinya: prerequisiteId harus dipelajari SEBELUM dependentId
     *
     * @param prerequisiteId ID topik prasyarat
     * @param dependentId    ID topik yang membutuhkan prasyarat
     */
    public void addPrerequisite(String prerequisiteId, String dependentId) {
        if (!topics.containsKey(prerequisiteId)) {
            throw new IllegalArgumentException("Topik prasyarat tidak ditemukan: " + prerequisiteId);
        }
        if (!topics.containsKey(dependentId)) {
            throw new IllegalArgumentException("Topik dependen tidak ditemukan: " + dependentId);
        }
        // prerequisiteId → dependentId
        adjacencyList.get(prerequisiteId).add(dependentId);
        // reversenya: dependentId membutuhkan prerequisiteId
        reverseList.get(dependentId).add(prerequisiteId);
    }

    /**
     * Mendapatkan semua topik yang membutuhkan topicId sebagai prasyarat
     * (siapa yang bergantung pada topik ini).
     */
    public List<String> getDependents(String topicId) {
        return adjacencyList.getOrDefault(topicId, new ArrayList<>());
    }

    /**
     * Mendapatkan semua prasyarat langsung dari sebuah topik.
     */
    public List<String> getPrerequisites(String topicId) {
        return reverseList.getOrDefault(topicId, new ArrayList<>());
    }

    /**
     * Menghitung in-degree setiap node (berapa banyak prasyarat yang dibutuhkan).
     * Digunakan oleh Topological Sort.
     */
    public Map<String, Integer> computeInDegrees() {
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        for (String id : topics.keySet()) {
            inDegree.put(id, 0);
        }
        // in-degree = jumlah prasyarat
        for (String id : topics.keySet()) {
            for (String dep : adjacencyList.get(id)) {
                inDegree.put(dep, inDegree.get(dep) + 1);
            }
        }
        return inDegree;
    }

    /**
     * Menampilkan seluruh graph dalam format adjacency list.
     */
    public void displayGraph() {
        System.out.println("\n=== GRAPH PRASYARAT (Adjacency List) ===");
        System.out.println("Format: Topik → [Topik yang membutuhkan ini]\n");
        for (String id : adjacencyList.keySet()) {
            Topic t = topics.get(id);
            List<String> deps = adjacencyList.get(id);
            System.out.printf("  %-35s > ", t.getTitle());
            if (deps.isEmpty()) {
                System.out.print("[tidak ada dependen]");
            } else {
                StringJoiner sj = new StringJoiner(", ", "[", "]");
                for (String dep : deps) {
                    sj.add(topics.get(dep).getTitle());
                }
                System.out.print(sj);
            }
            System.out.println();
        }
    }

    public Map<String, Topic> getTopics() { return topics; }
    public Map<String, List<String>> getAdjacencyList() { return adjacencyList; }
    public boolean containsTopic(String id) { return topics.containsKey(id); }
    public Topic getTopic(String id) { return topics.get(id); }
    public int getTopicCount() { return topics.size(); }
    public int getEdgeCount() {
        int count = 0;
        for (List<String> edges : adjacencyList.values()) count += edges.size();
        return count;
    }
}