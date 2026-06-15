package graph;

import model.Topic;
import java.util.*;

public class TopologicalSort {

    private TopicGraph graph;

    public TopologicalSort(TopicGraph graph) {
        this.graph = graph;
    }

    /**
     * Menjalankan Topological Sort dan menampilkan urutan belajar.
     * HARUS dipanggil setelah CycleDetector memastikan tidak ada siklus.
     *
     * @return List<String> urutan topicId yang direkomendasikan, atau null jika ada siklus
     */
    public List<String> sort() {
        System.out.println("\n=== REKOMENDASI URUTAN BELAJAR (Topological Sort) ===");
        System.out.println("Algoritma: Kahn's Algorithm (BFS-based)\n");

        // Step 1: Hitung in-degree setiap node
        Map<String, Integer> inDegree = graph.computeInDegrees();

        System.out.println("In-degree awal (jumlah prasyarat per topik):");
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            Topic t = graph.getTopic(entry.getKey());
            System.out.printf("  %-35s : %d prasyarat%n", t.getTitle(), entry.getValue());
        }

        // Step 2: Masukkan semua node dengan in-degree=0 ke queue
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> sortedOrder = new ArrayList<>();

        // Step 3-6: Proses queue
        System.out.println("\nProses Kahn's Algorithm:");
        int step = 1;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedOrder.add(current);
            Topic currentTopic = graph.getTopic(current);

            System.out.printf("  Step %d: Ambil \"%s\" (in-degree=0)%n",
                    step++, currentTopic.getTitle());

            // Kurangi in-degree semua dependen
            for (String dependent : graph.getDependents(current)) {
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.add(dependent);
                    System.out.printf("         → \"%s\" siap dipelajari (in-degree jadi 0)%n",
                            graph.getTopic(dependent).getTitle());
                }
            }
        }

        // Step 7: Cek jika ada siklus (tidak semua node terproses)
        if (sortedOrder.size() != graph.getTopicCount()) {
            System.out.println("\nPeringatan: Graph mengandung siklus!");
            System.out.println("   Topological sort tidak dapat diselesaikan.");
            return null;
        }

        // Tampilkan hasil
        System.out.println("\n╔══════════════════════════════════════════════════════════╗");
        System.out.println("║           URUTAN BELAJAR YANG DIREKOMENDASIKAN           ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝\n");
        for (int i = 0; i < sortedOrder.size(); i++) {
            Topic t = graph.getTopic(sortedOrder.get(i));
            List<String> prereqs = graph.getPrerequisites(sortedOrder.get(i));

            System.out.printf("  %2d. %-35s", i + 1, t.getTitle());
            if (prereqs.isEmpty()) {
                System.out.print(" [Tidak ada prasyarat]");
            } else {
                StringJoiner sj = new StringJoiner(", ", "[Prasyarat: ", "]");
                for (String p : prereqs) {
                    sj.add(graph.getTopic(p).getTitle());
                }
                System.out.print(" " + sj);
            }
            System.out.println();
        }

        return sortedOrder;
    }

    private void collectPrerequisites(String topicId, Set<String> requiredTopics) {

        if (requiredTopics.contains(topicId)) {
            return;
        }

        requiredTopics.add(topicId);

        for (String prereq : graph.getPrerequisites(topicId)) {
            collectPrerequisites(prereq, requiredTopics);
        }
    }

    public List<String> sortForTopic(String targetId) {

    if (!graph.containsTopic(targetId)) {
        System.out.println("Topik tidak ditemukan: " + targetId);
        return null;
    }

    Set<String> requiredTopics = new LinkedHashSet<>();

    collectPrerequisites(targetId, requiredTopics);

    Map<String, Integer> inDegree = new LinkedHashMap<>();

    for (String id : requiredTopics) {
        inDegree.put(id, 0);
    }

    for (String id : requiredTopics) {

        for (String dependent : graph.getDependents(id)) {

            if (requiredTopics.contains(dependent)) {
                inDegree.put(
                        dependent,
                        inDegree.get(dependent) + 1
                );
            }
        }
    }

    Queue<String> queue = new LinkedList<>();

    for (String id : requiredTopics) {
        if (inDegree.get(id) == 0) {
            queue.add(id);
        }
    }

    List<String> sortedOrder = new ArrayList<>();

    while (!queue.isEmpty()) {

        String current = queue.poll();
        sortedOrder.add(current);

        for (String dependent : graph.getDependents(current)) {

            if (!requiredTopics.contains(dependent)) {
                continue;
            }

            inDegree.put(
                    dependent,
                    inDegree.get(dependent) - 1
            );

            if (inDegree.get(dependent) == 0) {
                queue.add(dependent);
            }
        }
    }

    System.out.println("\n========================================");
    System.out.println("LEARNING PATH MENUJU "
            + graph.getTopic(targetId).getTitle());
    System.out.println("========================================\n");

    for (int i = 0; i < sortedOrder.size(); i++) {

        Topic t = graph.getTopic(sortedOrder.get(i));

        System.out.printf(
                "%2d. %s (%s)%n",
                i + 1,
                t.getTitle(),
                t.getId()
        );
    }

    return sortedOrder;
}
}