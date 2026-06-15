
import graph.*;
import model.Topic;
import tree.Trie;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║         LIBRARY KNOWLEDGE NAVIGATOR                     ║
 * ║         Final Project — Struktur Data                   ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Sistem pencarian topik dan rekomendasi urutan belajar
 * berdasarkan hubungan prasyarat antar materi.
 *
 * Struktur Data:
 *   - Trie         → Pencarian topik berdasarkan prefix
 *   - Graph (AL)   → Hubungan prasyarat antar topik (directed)
 *
 * Algoritma:
 *   - DFS              → Menelusuri hubungan prasyarat
 *   - Topological Sort → Rekomendasi urutan belajar
 *   - Cycle Detection  → Mendeteksi siklus prasyarat
 */
public class Main {

    static TopicGraph graph = new TopicGraph();
    static Trie trie = new Trie();
    static DFSTraversal dfs = new DFSTraversal(graph);
    static CycleDetector cycleDetector = new CycleDetector(graph);
    static TopologicalSort topoSort = new TopologicalSort(graph);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          LIBRARY KNOWLEDGE NAVIGATOR              ║");
        System.out.println("║     Sistem Pencarian & Rekomendasi Urutan Belajar       ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // Load dataset
        loadDataset();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Pilih menu [1-7]: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": featureSearchByPrefix();    break;
                case "2": featureShowPrerequisites(); break;
                case "3": featureLearningOrder();     break;
                case "4": featureDetectCycle();       break;
                case "5": featureDisconnectedTopics();break;
                case "6": graph.displayGraph(); trie.displayAll(); break;
                case "7":
                    System.out.println("\nTerima kasih telah menggunakan Library Knowledge Navigator!");
                    running = false;
                    break;
                default:
                    System.out.println("\n Pilihan tidak valid. Silakan pilih 1-7.");
            }
        }
        scanner.close();
    }

    // ─────────────────────────────────────────────────────────
    //  MENU
    // ─────────────────────────────────────────────────────────

    static void printMenu() {
        System.out.println("\n┌──────────────────────────────────────────┐");
        System.out.println("│              MENU UTAMA                  │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1. Cari topik berdasarkan prefix     │");
        System.out.println("│  2. Tampilkan prasyarat sebuah topik  │");
        System.out.println("│  3. Rekomendasikan urutan belajar     │");
        System.out.println("│  4. Deteksi siklus prasyarat          │");
        System.out.println("│  5. Tampilkan topik tidak terhubung   │");
        System.out.println("│  6. Tampilkan semua data              │");
        System.out.println("│  7. Keluar                            │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    // ─────────────────────────────────────────────────────────
    //  FITUR 1: Search by Prefix (Trie)
    // ─────────────────────────────────────────────────────────

    static void featureSearchByPrefix() {
        System.out.println("\n=== PENCARIAN TOPIK BERDASARKAN PREFIX ===");
        System.out.print("Masukkan kata kunci / prefix: ");
        String prefix = scanner.nextLine().trim();

        if (prefix.isEmpty()) {
            System.out.println("Prefix tidak boleh kosong.");
            return;
        }

        List<Topic> results = trie.searchByPrefix(prefix);

        System.out.printf("%nHasil pencarian untuk \"%s\":%n", prefix);
        if (results.isEmpty()) {
            System.out.println("  Tidak ada topik yang cocok.");
        } else {
            System.out.printf("  Ditemukan %d topik:%n", results.size());
            for (int i = 0; i < results.size(); i++) {
                System.out.printf("  %d. %s%n", i + 1, results.get(i));
            }

            // Tawarkan melihat prasyarat
            System.out.print("\nLihat prasyarat salah satu topik? (masukkan nomor / 0 untuk skip): ");
            String choice = scanner.nextLine().trim();
            try {
                int idx = Integer.parseInt(choice) - 1;
                if (idx >= 0 && idx < results.size()) {
                    dfs.showAllPrerequisites(results.get(idx).getId());
                }
            } catch (NumberFormatException ignored) {}
        }
    }

    // ─────────────────────────────────────────────────────────
    //  FITUR 2: Show Prerequisites (DFS)
    // ─────────────────────────────────────────────────────────

    static void featureShowPrerequisites() {
        System.out.println("\n=== PRASYARAT SEBUAH TOPIK ===");
        System.out.println("Topik yang tersedia:");
        int i = 1;
        List<String> ids = new ArrayList<>(graph.getTopics().keySet());
        for (String id : ids) {
            System.out.printf("  %2d. %s%n", i++, graph.getTopic(id).getTitle());
        }
        System.out.print("\nMasukkan nomor topik: ");
        String input = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(input) - 1;
            if (idx >= 0 && idx < ids.size()) {
                String selectedId = ids.get(idx);
                dfs.showAllPrerequisites(selectedId);
                System.out.println();
                dfs.showAllDependents(selectedId);
            } else {
                System.out.println("Nomor tidak valid.");
            }
        } catch (NumberFormatException e) {
            System.out.println("Input harus berupa angka.");
        }
    }

    // ─────────────────────────────────────────────────────────
    //  FITUR 3: Learning Order (Topological Sort)
    // ─────────────────────────────────────────────────────────

    static void featureLearningOrder() {

        System.out.println("\n=== REKOMENDASI URUTAN BELAJAR ===");

        // Cek siklus terlebih dahulu
        System.out.println("Memeriksa siklus prasyarat...");
        boolean hasCycle = cycleDetector.detectCycle();

        if (!hasCycle) {

            System.out.println("\nDaftar Topik yang Tersedia:");
            System.out.println("────────────────────────────────");

            for (Topic t : graph.getTopics().values()) {
                System.out.printf("%s - %s%n",
                        t.getId(),
                        t.getTitle());
            }

            System.out.print("\nMasukkan ID topik tujuan (contoh: T20): ");
            String target = scanner.nextLine().trim().toUpperCase();

            topoSort.sortForTopic(target);
        }
    }

    // ─────────────────────────────────────────────────────────
    //  FITUR 4: Cycle Detection
    // ─────────────────────────────────────────────────────────

    static void featureDetectCycle() {
        System.out.println("\n=== DETEKSI SIKLUS PRASYARAT ===");
        System.out.println("Menjalankan DFS 3-Warna (WHITE → GRAY → BLACK)...\n");
        cycleDetector.detectCycle();

        // Demo siklus buatan
        System.out.println("\n--- DEMO SIKLUS (Topik Buatan) ---");
        System.out.println("Tambahkan siklus buatan: TopikX > TopikY > TopikX");
        System.out.print("Jalankan demo siklus? (y/n): ");
        String confirm = scanner.nextLine().trim();
        if (confirm.equalsIgnoreCase("y")) {
            demoCycleDetection();
        }
    }

    static void demoCycleDetection() {
        // Buat graph baru dengan siklus
        TopicGraph cycleGraph = new TopicGraph();
        Topic tx = new Topic("TX", "Topik X Demo", "Demo", "");
        Topic ty = new Topic("TY", "Topik Y Demo", "Demo", "");
        Topic tz = new Topic("TZ", "Topik Z Demo", "Demo", "");
        cycleGraph.addTopic(tx);
        cycleGraph.addTopic(ty);
        cycleGraph.addTopic(tz);
        // Buat siklus: X → Y → Z → X
        cycleGraph.addPrerequisite("TX", "TY");
        cycleGraph.addPrerequisite("TY", "TZ");
        cycleGraph.addPrerequisite("TZ", "TX"); // siklus!

        CycleDetector demoDetector = new CycleDetector(cycleGraph);
        demoDetector.detectCycle();
    }

    // ─────────────────────────────────────────────────────────
    //  FITUR 5: Disconnected Topics
    // ─────────────────────────────────────────────────────────

    static void featureDisconnectedTopics() {
        System.out.println("\n=== TOPIK YANG TIDAK TERHUBUNG ===");
        System.out.println("Topik yang tidak memiliki prasyarat DAN tidak menjadi prasyarat siapapun:\n");

        List<Topic> isolated = new ArrayList<>();
        List<Topic> noPrereqs = new ArrayList<>();
        List<Topic> noDependents = new ArrayList<>();

        for (String id : graph.getTopics().keySet()) {
            boolean hasPrereq = !graph.getPrerequisites(id).isEmpty();
            boolean hasDep    = !graph.getDependents(id).isEmpty();

            if (!hasPrereq && !hasDep) {
                isolated.add(graph.getTopic(id));
            } else if (!hasPrereq) {
                noPrereqs.add(graph.getTopic(id));
            } else if (!hasDep) {
                noDependents.add(graph.getTopic(id));
            }
        }

        System.out.println("Topik Terisolir (tidak ada koneksi sama sekali):");
        if (isolated.isEmpty()) {
            System.out.println("  (tidak ada)");
        } else {
            for (Topic t : isolated) System.out.println("  - " + t.getTitle());
        }

        System.out.println("\nTopik Dasar (tidak ada prasyarat — titik awal belajar):");
        for (Topic t : noPrereqs) System.out.println(t.getTitle());

        System.out.println("\nTopik Puncak (tidak ada yang bergantung padanya — tujuan akhir):");
        for (Topic t : noDependents) System.out.println("  🏁 " + t.getTitle());

        // Cek konektivitas: temukan komponen terhubung
        System.out.println("\nInformasi Graph:");
        System.out.printf("  Total topik (node)  : %d%n", graph.getTopicCount());
        System.out.printf("  Total relasi (edge) : %d%n", graph.getEdgeCount());
    }

    // ─────────────────────────────────────────────────────────
    //  DATASET
    //  Minimal 25 node, 40 edge sesuai spesifikasi
    // ─────────────────────────────────────────────────────────

    static void loadDataset() {
        System.out.println("\nMemuat dataset...");

        // ── NODE (25 topik pemrograman) ──────────────────────
        Topic[] topics = {
            new Topic("T01", "Pengantar Pemrograman",          "Dasar",         "Konsep dasar pemrograman"),
            new Topic("T02", "Variabel dan Tipe Data",          "Dasar",         "Int, float, string, boolean"),
            new Topic("T03", "Operator dan Ekspresi",           "Dasar",         "Aritmatika, logika, relasional"),
            new Topic("T04", "Struktur Kontrol",                "Dasar",         "If-else, switch"),
            new Topic("T05", "Perulangan (Loop)",               "Dasar",         "For, while, do-while"),
            new Topic("T06", "Array dan String",                "Menengah",      "Array 1D, 2D, manipulasi string"),
            new Topic("T07", "Fungsi dan Prosedur",             "Menengah",      "Deklarasi, parameter, return value"),
            new Topic("T08", "Rekursi",                         "Menengah",      "Base case, recursive case"),
            new Topic("T09", "Pointer dan Memori",              "Menengah",      "Pointer, heap, stack"),
            new Topic("T10", "OOP - Dasar",                     "Menengah",      "Class, object, method"),
            new Topic("T11", "OOP - Inheritance",               "Menengah",      "Extends, super, override"),
            new Topic("T12", "OOP - Polymorphism",              "Lanjut",        "Overloading, overriding, abstract"),
            new Topic("T13", "OOP - Interface & Abstract",      "Lanjut",        "Interface, abstract class"),
            new Topic("T14", "Algoritma Sorting",               "Menengah",      "Bubble, selection, insertion, merge"),
            new Topic("T15", "Algoritma Searching",             "Menengah",      "Linear search, binary search"),
            new Topic("T16", "Kompleksitas Algoritma",          "Lanjut",        "Big O notation, analisis"),
            new Topic("T17", "Struktur Data - Stack & Queue",   "Menengah",      "LIFO, FIFO, implementasi"),
            new Topic("T18", "Struktur Data - Linked List",     "Menengah",      "Single, double, circular"),
            new Topic("T19", "Struktur Data - Tree",            "Lanjut",        "BST, AVL, traversal"),
            new Topic("T20", "Struktur Data - Graph",           "Lanjut",        "Adjacency list/matrix, DFS, BFS"),
            new Topic("T21", "Dynamic Programming",             "Lanjut",        "Memoization, tabulation"),
            new Topic("T22", "Greedy Algorithm",                "Lanjut",        "Greedy choice, optimal substructure"),
            new Topic("T23", "Divide and Conquer",              "Lanjut",        "Merge sort, quick sort, binary search"),
            new Topic("T24", "Pemrograman Fungsional",          "Lanjut",        "Lambda, higher-order function"),
            new Topic("T25", "Design Patterns",                 "Lanjut",        "Singleton, Factory, Observer, MVC"),
        };

        for (Topic t : topics) {
            graph.addTopic(t);
            trie.insert(t);
        }

        // ── EDGE (prasyarat → dependen) ──────────────────────
        // Format: addPrerequisite("PRASYARAT", "YANG_BUTUH_PRASYARAT")
        String[][] edges = {
            // Dasar
            {"T01", "T02"}, // Pengantar → Variabel
            {"T01", "T03"}, // Pengantar → Operator
            {"T02", "T03"}, // Variabel  → Operator
            {"T02", "T04"}, // Variabel  → Struktur Kontrol
            {"T03", "T04"}, // Operator  → Struktur Kontrol
            {"T04", "T05"}, // Kontrol   → Loop

            // Array, Fungsi, Rekursi
            {"T02", "T06"}, // Variabel  → Array
            {"T05", "T06"}, // Loop      → Array
            {"T04", "T07"}, // Kontrol   → Fungsi
            {"T07", "T08"}, // Fungsi    → Rekursi
            {"T06", "T08"}, // Array     → Rekursi
            {"T02", "T09"}, // Variabel  → Pointer
            {"T07", "T09"}, // Fungsi    → Pointer

            // OOP
            {"T07", "T10"}, // Fungsi    → OOP Dasar
            {"T10", "T11"}, // OOP Dasar → Inheritance
            {"T11", "T12"}, // Inherit   → Polymorphism
            {"T11", "T13"}, // Inherit   → Interface
            {"T12", "T13"}, // Poly      → Interface

            // Algoritma
            {"T06", "T14"}, // Array     → Sorting
            {"T07", "T14"}, // Fungsi    → Sorting
            {"T06", "T15"}, // Array     → Searching
            {"T14", "T16"}, // Sorting   → Kompleksitas
            {"T15", "T16"}, // Searching → Kompleksitas
            {"T08", "T23"}, // Rekursi   → Divide & Conquer
            {"T14", "T23"}, // Sorting   → Divide & Conquer

            // Struktur Data
            {"T09", "T17"}, // Pointer   → Stack & Queue
            {"T07", "T17"}, // Fungsi    → Stack & Queue
            {"T09", "T18"}, // Pointer   → Linked List
            {"T17", "T18"}, // Stack     → Linked List
            {"T18", "T19"}, // LL        → Tree
            {"T08", "T19"}, // Rekursi   → Tree
            {"T19", "T20"}, // Tree      → Graph
            {"T17", "T20"}, // Stack     → Graph (DFS pakai stack)

            // Advanced
            {"T08", "T21"}, // Rekursi   → DP
            {"T16", "T21"}, // Kompleks  → DP
            {"T16", "T22"}, // Kompleks  → Greedy
            {"T14", "T22"}, // Sorting   → Greedy
            {"T10", "T24"}, // OOP       → Pemrog Fungsional
            {"T07", "T24"}, // Fungsi    → Pemrog Fungsional
            {"T13", "T25"}, // Interface → Design Patterns
            {"T10", "T25"}, // OOP Dasar → Design Patterns
        };

        for (String[] edge : edges) {
            graph.addPrerequisite(edge[0], edge[1]);
        }

        System.out.printf("Dataset dimuat: %d topik, %d relasi prasyarat%n",
                graph.getTopicCount(), graph.getEdgeCount());
    }
}