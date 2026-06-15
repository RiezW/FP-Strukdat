package graph;

import model.Topic;
import java.util.*;

/**
 * Deteksi Siklus (Cycle Detection) menggunakan DFS 3-Warna.
 *
 * Algoritma 3-Warna:
 *   WHITE (0) = Belum dikunjungi
 *   GRAY  (1) = Sedang diproses (ada di call stack aktif)
 *   BLACK (2) = Sudah selesai diproses
 *
 * Jika saat DFS kita menemukan node GRAY,
 * berarti ada back-edge → SIKLUS TERDETEKSI!
 *
 * Mengapa 3 warna?
 *   Hanya 2 warna (visited/unvisited) tidak cukup untuk directed graph.
 *   Node yang sudah BLACK dari jalur lain bukan berarti ada siklus.
 *   Hanya node GRAY (di call stack yang sama) yang menandai siklus.
 */
public class CycleDetector {

    private static final int WHITE = 0;
    private static final int GRAY  = 1;
    private static final int BLACK = 2;

    private TopicGraph graph;

    public CycleDetector(TopicGraph graph) {
        this.graph = graph;
    }

    /**
     * Mendeteksi apakah graph memiliki siklus.
     * Jika ada siklus, menampilkan topik-topik yang terlibat.
     *
     * @return true jika ada siklus, false jika tidak (DAG)
     */
    public boolean detectCycle() {
        Map<String, Integer> color = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        List<String> cyclePath = new ArrayList<>();

        // Inisialisasi semua node sebagai WHITE
        for (String id : graph.getTopics().keySet()) {
            color.put(id, WHITE);
            parent.put(id, null);
        }

        // Jalankan DFS dari setiap node yang belum dikunjungi
        for (String id : graph.getTopics().keySet()) {
            if (color.get(id) == WHITE) {
                if (dfsDetect(id, color, parent, cyclePath)) {
                    printCycleInfo(cyclePath);
                    return true;
                }
            }
        }

        System.out.println("\nTidak ditemukan siklus. Graph adalah DAG (Directed Acyclic Graph).");
        System.out.println("   Topological Sort dapat dilakukan dengan aman.");
        return false;
    }

    /**
     * DFS rekursif dengan pewarnaan 3-warna.
     *
     * @return true jika siklus ditemukan
     */
    private boolean dfsDetect(String current, Map<String, Integer> color,
                               Map<String, String> parent, List<String> cyclePath) {
        // Tandai node sebagai GRAY (sedang diproses)
        color.put(current, GRAY);

        for (String neighbor : graph.getDependents(current)) {
            if (color.get(neighbor) == GRAY) {
                // Ketemu node GRAY → SIKLUS!
                // Rekonstruksi path siklus
                cyclePath.add(neighbor);
                cyclePath.add(current);
                String step = parent.get(current);
                while (step != null && !step.equals(neighbor)) {
                    cyclePath.add(step);
                    step = parent.get(step);
                }
                cyclePath.add(neighbor);
                Collections.reverse(cyclePath);
                return true;
            }
            if (color.get(neighbor) == WHITE) {
                parent.put(neighbor, current);
                if (dfsDetect(neighbor, color, parent, cyclePath)) {
                    return true;
                }
            }
            // Jika BLACK: sudah diproses sebelumnya, tidak ada masalah
        }

        // Tandai node sebagai BLACK (selesai)
        color.put(current, BLACK);
        return false;
    }

    /**
     * Menampilkan informasi siklus yang ditemukan.
     */
    private void printCycleInfo(List<String> cyclePath) {
        System.out.println("\nSIKLUS TERDETEKSI! Prasyarat bermasalah ditemukan.");
        System.out.println("   Topological Sort TIDAK dapat dilakukan.");
        System.out.println("\n   Path siklus:");

        for (int i = 0; i < cyclePath.size(); i++) {
            String id = cyclePath.get(i);
            Topic t = graph.getTopic(id);
            if (i < cyclePath.size() - 1) {
                System.out.printf("   \"%s\" > ", t != null ? t.getTitle() : id);
            } else {
                System.out.printf("\"%s\" (kembali ke awal > SIKLUS!)%n",
                        t != null ? t.getTitle() : id);
            }
        }

        System.out.println("\nSolusi: Hapus salah satu edge yang membentuk siklus.");
        System.out.println("      Contoh: jika A>B>C>A, hapus edge C>A.");
    }
}