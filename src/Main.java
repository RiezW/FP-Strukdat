
import graph.*;
import model.Topic;
import tree.Trie;

import java.util.*;

/**
 * ╔══════════════════════════════════════════════════════════╗
 * ║ LIBRARY KNOWLEDGE NAVIGATOR                              ║
 * ║ Final Project — Struktur Data                            ║
 * ╚══════════════════════════════════════════════════════════╝
 *
 * Sistem pencarian topik dan rekomendasi urutan belajar
 * berdasarkan hubungan prasyarat antar materi.
 *
 * Struktur Data:
 * - Trie → Pencarian topik berdasarkan prefix
 * - Graph (AL) → Hubungan prasyarat antar topik (directed)
 *
 * Algoritma:
 * - DFS → Menelusuri hubungan prasyarat
 * - Topological Sort → Rekomendasi urutan belajar
 * - Cycle Detection → Mendeteksi siklus prasyarat
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
        System.out.println("║          LIBRARY KNOWLEDGE NAVIGATOR                     ║");
        System.out.println("║     Sistem Pencarian & Rekomendasi Urutan Belajar        ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");

        // Load dataset
        loadDataset();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Pilih menu [1-8]: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    featureSearchByPrefix();
                    break;
                case "2":
                    featureShowPrerequisites();
                    break;
                case "3":
                    featureLearningOrder();
                    break;
                case "4":
                    featureDetectCycle();
                    break;
                case "5":
                    featureDisconnectedTopics();
                    break;
                case "6":
                    featureInsertData();
                    break;
                case "7":
                    graph.displayGraph();
                    trie.displayAll();
                    break;
                case "8":
                    System.out.println("\nTerima kasih telah menggunakan Library Knowledge Navigator!");
                    running = false;
                    break;
                default:
                    System.out.println("\n Pilihan tidak valid. Silakan pilih 1-8.");
            }
        }
        scanner.close();
    }

    // ─────────────────────────────────────────────────────────
    // MENU
    // ─────────────────────────────────────────────────────────

    static void printMenu() {
        System.out.println("\n┌──────────────────────────────────────────┐");
        System.out.println("│              MENU UTAMA                  │");
        System.out.println("├──────────────────────────────────────────┤");
        System.out.println("│  1. Cari topik berdasarkan prefix        │");
        System.out.println("│  2. Tampilkan prasyarat sebuah topik     │");
        System.out.println("│  3. Rekomendasikan urutan belajar        │");
        System.out.println("│  4. Deteksi siklus prasyarat             │");
        System.out.println("│  5. Tampilkan topik tidak terhubung      │");
        System.out.println("│  6. Insert data topik baru               │");
        System.out.println("│  7. Tampilkan semua data                 │");
        System.out.println("│  8. Keluar                               │");
        System.out.println("└──────────────────────────────────────────┘");
    }

    // ─────────────────────────────────────────────────────────
    // FITUR 1: Search by Prefix (Trie)
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
            } catch (NumberFormatException ignored) {
            }
        }
    }

    // ─────────────────────────────────────────────────────────
    // FITUR 2: Show Prerequisites (DFS)
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
    // FITUR 3: Learning Order (Topological Sort)
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
    // FITUR 4: Cycle Detection
    // ─────────────────────────────────────────────────────────

    static void featureDetectCycle() {
        System.out.println("\n=== DETEKSI SIKLUS PRASYARAT ===");
        System.out.println("Menjalankan DFS 3-Warna (WHITE > GRAY > BLACK)...\n");
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
        Topic tx = new Topic("TX", "Topik X Demo", "Demo", "", 5);
        Topic ty = new Topic("TY", "Topik Y Demo", "Demo", "", 5);
        Topic tz = new Topic("TZ", "Topik Z Demo", "Demo", "", 5);
        cycleGraph.addTopic(tx);
        cycleGraph.addTopic(ty);
        cycleGraph.addTopic(tz);
        // Buat siklus: X → Y → Z → X
        cycleGraph.addPrerequisite("Topik X Demo", "Topik Y Demo");
        cycleGraph.addPrerequisite("Topik Y Demo", "Topik Z Demo");
        cycleGraph.addPrerequisite("Topik Z Demo", "Topik X Demo"); // siklus!

        CycleDetector demoDetector = new CycleDetector(cycleGraph);
        demoDetector.detectCycle();
    }

    // ─────────────────────────────────────────────────────────
    // FITUR 5: Disconnected Topics
    // ─────────────────────────────────────────────────────────

    static void featureDisconnectedTopics() {
        System.out.println("\n=== TOPIK YANG TIDAK TERHUBUNG ===");
        System.out.println("Topik yang tidak memiliki prasyarat DAN tidak menjadi prasyarat siapapun:\n");

        List<Topic> isolated = new ArrayList<>();
        List<Topic> noPrereqs = new ArrayList<>();
        List<Topic> noDependents = new ArrayList<>();

        for (String id : graph.getTopics().keySet()) {
            boolean hasPrereq = !graph.getPrerequisites(id).isEmpty();
            boolean hasDep = !graph.getDependents(id).isEmpty();

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
            for (Topic t : isolated)
                System.out.println("  - " + t.getTitle());
        }

        System.out.println("\nTopik Dasar (tidak ada prasyarat - titik awal belajar):");
        for (Topic t : noPrereqs)
            System.out.println(t.getTitle());

        System.out.println("\nTopik Puncak (tidak ada yang bergantung padanya - tujuan akhir):");
        for (Topic t : noDependents)
            System.out.println(t.getTitle());

        // Cek konektivitas: temukan komponen terhubung
        System.out.println("\nInformasi Graph:");
        System.out.printf("  Total topik (node)  : %d%n", graph.getTopicCount());
        System.out.printf("  Total relasi (edge) : %d%n", graph.getEdgeCount());
    }

    // ─────────────────────────────────────────────────────────
    // FITUR 6: Insert Data
    // ─────────────────────────────────────────────────────────

    static void featureInsertData() {
        System.out.println("\n=== INSERT DATA TOPIK BARU ===");

        String generatedId = generateNextTopicId();
        System.out.printf("ID otomatis berikutnya: %s%n", generatedId);
        System.out.print("Masukkan ID topik (Enter untuk pakai ID otomatis): ");
        String id = scanner.nextLine().trim().toUpperCase();
        if (id.isEmpty()) {
            id = generatedId;
        }

        if (graph.containsTopic(id)) {
            System.out.println("ID sudah digunakan. Insert dibatalkan.");
            return;
        }

        System.out.print("Masukkan judul topik: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("Judul tidak boleh kosong. Insert dibatalkan.");
            return;
        }
        if (isTitleExists(title)) {
            System.out.println("Judul topik sudah ada. Insert dibatalkan.");
            return;
        }

        System.out.print("Masukkan kategori: ");
        String category = scanner.nextLine().trim();
        if (category.isEmpty()) {
            category = "General";
        }

        System.out.print("Masukkan deskripsi: ");
        String description = scanner.nextLine().trim();

        int duration = readInteger("Masukkan durasi belajar (jam): ");
        if (duration < 0) {
            System.out.println("Durasi tidak boleh negatif. Insert dibatalkan.");
            return;
        }

        int year = readInteger("Masukkan tahun materi: ");
        if (year < 0) {
            System.out.println("Tahun tidak boleh negatif. Insert dibatalkan.");
            return;
        }

        List<String> prerequisiteIds = readPrerequisiteIds(id, title);
        if (prerequisiteIds == null) {
            System.out.println("Insert dibatalkan karena prasyarat tidak valid.");
            return;
        }

        Topic newTopic = new Topic(id, title, category, description, duration, year);
        graph.addTopic(newTopic);
        trie.insert(newTopic);

        for (String prereqId : prerequisiteIds) {
            graph.addPrerequisite(prereqId, id);
        }

        System.out.printf("%nTopik \"%s\" berhasil ditambahkan.%n", title);
        System.out.printf("Total sekarang: %d topik, %d relasi prasyarat.%n",
                graph.getTopicCount(), graph.getEdgeCount());
    }

    static int readInteger(String prompt) {
        System.out.print(prompt);
        String input = scanner.nextLine().trim();
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    static List<String> readPrerequisiteIds(String newId, String newTitle) {
        System.out.println("\nDaftar Topik yang Bisa Dijadikan Prasyarat:");
        for (Topic t : graph.getTopics().values()) {
            System.out.printf("  %s - %s%n", t.getId(), t.getTitle());
        }

        System.out.println("\nMasukkan prasyarat dipisah koma.");
        System.out.print("Bisa memakai ID atau judul topik (kosongkan jika tidak ada): ");
        String input = scanner.nextLine().trim();
        List<String> prerequisiteIds = new ArrayList<>();

        if (input.isEmpty()) {
            return prerequisiteIds;
        }

        Set<String> uniqueIds = new LinkedHashSet<>();
        for (String rawPrereq : input.split(",")) {
            String prereq = rawPrereq.trim();
            if (prereq.isEmpty()) {
                continue;
            }

            String prereqId = findTopicIdByTitleOrId(prereq);
            if (prereqId == null) {
                System.out.println("Prasyarat tidak ditemukan: " + prereq);
                return null;
            }
            if (prereqId.equalsIgnoreCase(newId) || prereq.equalsIgnoreCase(newTitle)) {
                System.out.println("Topik baru tidak boleh menjadi prasyarat untuk dirinya sendiri.");
                return null;
            }
            uniqueIds.add(prereqId);
        }

        prerequisiteIds.addAll(uniqueIds);
        return prerequisiteIds;
    }

    static String findTopicIdByTitleOrId(String identifier) {
        String normalizedId = identifier.toUpperCase();
        if (graph.containsTopic(normalizedId)) {
            return normalizedId;
        }

        for (Topic t : graph.getTopics().values()) {
            if (t.getTitle().equalsIgnoreCase(identifier)) {
                return t.getId();
            }
        }
        return null;
    }

    static boolean isTitleExists(String title) {
        for (Topic t : graph.getTopics().values()) {
            if (t.getTitle().equalsIgnoreCase(title)) {
                return true;
            }
        }
        return false;
    }

    static String generateNextTopicId() {
        int max = 0;
        for (String id : graph.getTopics().keySet()) {
            if (id.matches("T\\d+")) {
                try {
                    max = Math.max(max, Integer.parseInt(id.substring(1)));
                } catch (NumberFormatException ignored) {
                }
            }
        }
        return String.format("T%02d", max + 1);
    }

    // ─────────────────────────────────────────────────────────
    // DATASET
    // Minimal 25 node, 40 edge sesuai spesifikasi
    // ─────────────────────────────────────────────────────────

    static void loadDataset() {
        System.out.println("\nMemuat dataset...");

        // ── NODE (25 topik pemrograman) ──────────────────────
        Topic[] topics = {
                new Topic("T01", "Pengantar Pemrograman", "Dasar", "Konsep dasar pemrograman", 4, 2008),
                new Topic("T02", "Variabel dan Tipe Data", "Dasar", "Int, float, string, boolean", 6, 2009),
                new Topic("T03", "Operator dan Ekspresi", "Dasar", "Aritmatika, logika, relasional", 6, 2009),
                new Topic("T04", "Struktur Kontrol", "Dasar", "If-else, switch", 8, 2010),
                new Topic("T05", "Perulangan (Loop)", "Dasar", "For, while, do-while", 8, 2010),
                new Topic("T06", "Array dan String", "Menengah", "Array 1D, 2D, manipulasi string", 12, 2010),
                new Topic("T07", "Fungsi dan Prosedur", "Menengah", "Deklarasi, parameter, return value", 10, 2009),
                new Topic("T08", "Rekursi", "Menengah", "Base case, recursive case", 12, 2008),
                new Topic("T09", "Pointer dan Memori", "Menengah", "Pointer, heap, stack", 14, 2007),
                new Topic("T10", "OOP - Dasar", "Menengah", "Class, object, method", 16, 2011),
                new Topic("T11", "OOP - Inheritance", "Menengah", "Extends, super, override", 14, 2011),
                new Topic("T12", "OOP - Polymorphism", "Lanjut", "Overloading, overriding, abstract", 20, 2012),
                new Topic("T13", "OOP - Interface & Abstract", "Lanjut", "Interface, abstract class", 18, 2012),
                new Topic("T14", "Algoritma Sorting", "Menengah", "Bubble, selection, insertion, merge", 16, 2008),
                new Topic("T15", "Algoritma Searching", "Menengah", "Linear search, binary search", 12, 2009),
                new Topic("T16", "Kompleksitas Algoritma", "Lanjut", "Big O notation, analisis", 24, 2010),
                new Topic("T17", "Struktur Data - Stack & Queue", "Menengah", "LIFO, FIFO, implementasi", 15, 2008),
                new Topic("T18", "Struktur Data - Linked List", "Menengah", "Single, double, circular", 18, 2009),
                new Topic("T19", "Struktur Data - Tree", "Lanjut", "BST, AVL, traversal", 25, 2011),
                new Topic("T20", "Struktur Data - Graph", "Lanjut", "Adjacency list/matrix, DFS, BFS", 30, 2012),
                new Topic("T21", "Dynamic Programming", "Lanjut", "Memoization, tabulation", 28, 2010),
                new Topic("T22", "Greedy Algorithm", "Lanjut", "Greedy choice, optimal substructure", 22, 2011),
                new Topic("T23", "Divide and Conquer", "Lanjut", "Merge sort, quick sort, binary search", 20, 2009),
                new Topic("T24", "Pemrograman Fungsional", "Lanjut", "Lambda, higher-order function", 20, 2012),
                new Topic("T25", "Design Patterns", "Lanjut", "Singleton, Factory, Observer, MVC", 24, 2013),
        };

        for (Topic t : topics) {
            graph.addTopic(t);
            trie.insert(t);
        }

        // ── EDGE (prasyarat → dependen) ──────────────────────
        // Format: addPrerequisite("PRASYARAT", "YANG_BUTUH_PRASYARAT")
        String[][] edges = {
                // Dasar
                { "Pengantar Pemrograman", "Variabel dan Tipe Data" }, // T01 -> T02
                { "Pengantar Pemrograman", "Operator dan Ekspresi" }, // T01 -> T03
                { "Variabel dan Tipe Data", "Operator dan Ekspresi" }, // T02 -> T03
                { "Variabel dan Tipe Data", "Struktur Kontrol" }, // T02 -> T04
                { "Operator dan Ekspresi", "Struktur Kontrol" }, // T03 -> T04
                { "Struktur Kontrol", "Perulangan (Loop)" }, // T04 -> T05

                // Array, Fungsi, Rekursi
                { "Variabel dan Tipe Data", "Array dan String" }, // T02 -> T06
                { "Perulangan (Loop)", "Array dan String" }, // T05 -> T06
                { "Struktur Kontrol", "Fungsi dan Prosedur" }, // T04 -> T07
                { "Fungsi dan Prosedur", "Rekursi" }, // T07 -> T08
                { "Array dan String", "Rekursi" }, // T06 -> T08
                { "Variabel dan Tipe Data", "Pointer dan Memori" }, // T02 -> T09
                { "Fungsi dan Prosedur", "Pointer dan Memori" }, // T07 -> T09

                // OOP
                { "Fungsi dan Prosedur", "OOP - Dasar" }, // T07 -> T10
                { "OOP - Dasar", "OOP - Inheritance" }, // T10 -> T11
                { "OOP - Inheritance", "OOP - Polymorphism" }, // T11 -> T12
                { "OOP - Inheritance", "OOP - Interface & Abstract" }, // T11 -> T13
                { "OOP - Polymorphism", "OOP - Interface & Abstract" }, // T12 -> T13

                // Algoritma
                { "Array dan String", "Algoritma Sorting" }, // T06 -> T14
                { "Fungsi dan Prosedur", "Algoritma Sorting" }, // T07 -> T14
                { "Array dan String", "Algoritma Searching" }, // T06 -> T15
                { "Algoritma Sorting", "Kompleksitas Algoritma" }, // T14 -> T16
                { "Algoritma Searching", "Kompleksitas Algoritma" }, // T15 -> T16
                { "Rekursi", "Divide and Conquer" }, // T08 -> T23
                { "Algoritma Sorting", "Divide and Conquer" }, // T14 -> T23

                // Struktur Data
                { "Pointer dan Memori", "Struktur Data - Stack & Queue" }, // T09 -> T17
                { "Fungsi dan Prosedur", "Struktur Data - Stack & Queue" }, // T07 -> T17
                { "Pointer dan Memori", "Struktur Data - Linked List" }, // T09 -> T18
                { "Struktur Data - Stack & Queue", "Struktur Data - Linked List" }, // T17 -> T18
                { "Struktur Data - Linked List", "Struktur Data - Tree" }, // T18 -> T19
                { "Rekursi", "Struktur Data - Tree" }, // T08 -> T19
                { "Struktur Data - Tree", "Struktur Data - Graph" }, // T19 -> T20
                { "Struktur Data - Stack & Queue", "Struktur Data - Graph" }, // T17 -> T20

                // Advanced
                { "Rekursi", "Dynamic Programming" }, // T08 -> T21
                { "Kompleksitas Algoritma", "Dynamic Programming" }, // T16 -> T21
                { "Kompleksitas Algoritma", "Greedy Algorithm" }, // T16 -> T22
                { "Algoritma Sorting", "Greedy Algorithm" }, // T14 -> T22
                { "OOP - Dasar", "Pemrograman Fungsional" }, // T10 -> T24
                { "Fungsi dan Prosedur", "Pemrograman Fungsional" }, // T07 -> T24
                { "OOP - Interface & Abstract", "Design Patterns" }, // T13 -> T25
                { "OOP - Dasar", "Design Patterns" }, // T10 -> T25
        };

        for (String[] edge : edges) {
            graph.addPrerequisite(edge[0], edge[1]);
        }

        System.out.printf("Dataset dimuat: %d topik, %d relasi prasyarat%n",
                graph.getTopicCount(), graph.getEdgeCount());
    }
}
