package graph;

import model.Topic;
import java.util.*;

/**
 * DFS (Depth-First Search) untuk menelusuri hubungan prasyarat antar topik.
 *
 * Kegunaan:
 * 1. Menampilkan SEMUA prasyarat dari sebuah topik (langsung & tidak langsung)
 * 2. Menemukan semua topik yang bergantung pada topik tertentu
 * 3. Dasar dari Cycle Detection
 *
 * Cara kerja DFS:
 *   - Mulai dari node awal
 *   - Kunjungi satu tetangga, telusuri sampai habis
 *   - Backtrack dan kunjungi tetangga berikutnya
 *   - Tandai node yang sudah dikunjungi agar tidak berulang
 */
public class DFSTraversal {

    private TopicGraph graph;

    public DFSTraversal(TopicGraph graph) {
        this.graph = graph;
    }

    /**
     * Menampilkan semua prasyarat dari topik (langsung & tidak langsung)
     * menggunakan DFS pada reverse graph (dari dependen ke prasyarat).
     *
     * @param topicId ID topik yang ingin diketahui prasyaratnya
     */
    public void showAllPrerequisites(String topicId) {
        if (!graph.containsTopic(topicId)) {
            System.out.println("Topik tidak ditemukan: " + topicId);
            return;
        }

        Topic topic = graph.getTopic(topicId);
        System.out.println("\n=== PRASYARAT LENGKAP UNTUK: " + topic.getTitle() + " ===");
        System.out.println("(Termasuk prasyarat tidak langsung)\n");

        Set<String> visited = new LinkedHashSet<>();
        List<String> order = new ArrayList<>();
        dfsPrerequisites(topicId, visited, order, 0);

        // Hapus topik utamanya sendiri dari hasil
        order.remove(topicId);

        if (order.isEmpty()) {
            System.out.println("  → Topik ini tidak memiliki prasyarat.");
        } else {
            System.out.println("  Urutan prasyarat yang harus dipelajari lebih dulu:");
            Collections.reverse(order); // tampilkan dari yang paling dasar
            for (int i = 0; i < order.size(); i++) {
                Topic t = graph.getTopic(order.get(i));
                System.out.printf("  %d. %s%n", i + 1, t.getTitle());
            }
        }
    }

    private void dfsPrerequisites(String topicId, Set<String> visited, List<String> order, int depth) {
        visited.add(topicId);
        List<String> prereqs = graph.getPrerequisites(topicId);
        for (String prereq : prereqs) {
            if (!visited.contains(prereq)) {
                dfsPrerequisites(prereq, visited, order, depth + 1);
            }
        }
        order.add(topicId);
    }

    /**
     * Menampilkan semua topik yang bisa diakses dari topicId
     * (semua yang bergantung pada topik ini secara langsung/tidak langsung).
     *
     * @param topicId ID topik awal
     */
    public void showAllDependents(String topicId) {
        if (!graph.containsTopic(topicId)) {
            System.out.println("Topik tidak ditemukan: " + topicId);
            return;
        }

        Topic topic = graph.getTopic(topicId);
        System.out.println("\n=== TOPIK YANG MEMBUTUHKAN: " + topic.getTitle() + " ===");
        System.out.println("(Termasuk yang tidak langsung)\n");

        Set<String> visited = new LinkedHashSet<>();
        dfsForward(topicId, visited, "  ", true);

        visited.remove(topicId); // hapus dirinya sendiri
        if (visited.isEmpty()) {
            System.out.println("  → Tidak ada topik yang bergantung pada topik ini.");
        }
    }

    private void dfsForward(String topicId, Set<String> visited, String indent, boolean isRoot) {
        visited.add(topicId);
        List<String> dependents = graph.getDependents(topicId);
        for (String dep : dependents) {
            Topic t = graph.getTopic(dep);
            System.out.printf("%s→ %s%n", indent, t.getTitle());
            if (!visited.contains(dep)) {
                dfsForward(dep, visited, indent + "  ", false);
            }
        }
    }

    /**
     * DFS umum: mengembalikan semua node yang dapat dicapai dari startId
     * (termasuk startId itu sendiri).
     *
     * @param startId Node awal
     * @return Set ID topik yang dapat dicapai
     */
    public Set<String> reachableFrom(String startId) {
        Set<String> visited = new LinkedHashSet<>();
        dfsForwardCollect(startId, visited);
        return visited;
    }

    private void dfsForwardCollect(String topicId, Set<String> visited) {
        visited.add(topicId);
        for (String dep : graph.getDependents(topicId)) {
            if (!visited.contains(dep)) {
                dfsForwardCollect(dep, visited);
            }
        }
    }
}