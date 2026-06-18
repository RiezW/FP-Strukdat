# Panduan & Cheat Sheet Presentasi Final Project Struktur Data
## Library Knowledge Navigator

Dokumen ini berisi rangkuman metode, alur kerja, tautan file, dan **potongan kode (code block) utama** dari aplikasi Anda. Gunakan dokumen ini sebagai panduan cepat ketika diminta untuk **menunjukkan kode** (*live audit*) saat presentasi.

---

## 🟢 BAGIAN 1: TREE (TRIE)
Struktur data Trie digunakan untuk **pencarian cepat berbasis awalan (prefix/autocomplete)** dari judul topik.

### 1. Memasukkan Judul Topik ke Trie
* **File:** [Trie.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/tree/Trie.java#L37-L51)
* **Method:** `insert(Topic topic)`
* **Potongan Kode:**
```java
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
```

---

### 2. Mencari Topik Berdasarkan Prefix (Awalan)
* **File:** [Trie.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/tree/Trie.java#L59-L75)
* **Method:** `searchByPrefix(String prefix)`
* **Potongan Kode:**
```java
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
```

---

### 3. Rekursi Pengumpulan Data Trie (DFS)
* **File:** [Trie.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/tree/Trie.java#L103-L111)
* **Method:** `collectAllTopics(TrieNode node, List<Topic> results)`
* **Potongan Kode:**
```java
    private void collectAllTopics(TrieNode node, List<Topic> results) {
        if (node.isEndOfWord && node.topicId != null) {
            Topic t = topicMap.get(node.topicId);
            if (t != null) results.add(t);
        }
        for (TrieNode child : node.children.values()) {
            collectAllTopics(child, results);
        }
    }
```

---

## 🔵 BAGIAN 2: GRAPH REPRESENTATION (ADJACENCY LIST)
Struktur data Directed Graph menggunakan **Adjacency List** untuk menyimpan hubungan prasyarat.

### 1. Deklarasi Struktur Adjacency List
* **File & Lokasi:** [TopicGraph.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/TopicGraph.java#L28-L32) (Deklarasi variabel instansiasi Map)
* **Potongan Kode:**
```java
    // adjacencyList[topicId] = list of topicId yang membutuhkan topik ini sebagai prasyarat
    private Map<String, List<String>> adjacencyList;

    // reverseList[topicId] = list of prasyarat dari topik ini
    private Map<String, List<String>> reverseList;

    // Menyimpan data topik
    private Map<String, Topic> topics;
```

---

### 2. Menghubungkan Dua Topik (Menambah Edge)
* **File & Lokasi:** [TopicGraph.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/TopicGraph.java#L62-L76) (Dalam method `addPrerequisite`)
* **Method:** `addPrerequisite(String prerequisiteTitleOrId, String dependentTitleOrId)`
* **Potongan Kode:**
```java
    public void addPrerequisite(String prerequisiteTitleOrId, String dependentTitleOrId) {
        String prereqId = findIdByTitleOrId(prerequisiteTitleOrId);
        String depId = findIdByTitleOrId(dependentTitleOrId);

        if (prereqId == null) {
            throw new IllegalArgumentException("Topik prasyarat tidak ditemukan: " + prerequisiteTitleOrId);
        }
        if (depId == null) {
            throw new IllegalArgumentException("Topik dependen tidak ditemukan: " + dependentTitleOrId);
        }
        // prerequisiteId → dependentId
        adjacencyList.get(prereqId).add(depId);
        // reversenya: dependentId membutuhkan prerequisiteId
        reverseList.get(depId).add(prereqId);
    }
```

---

### 3. Menghitung In-Degree (Banyaknya Prasyarat Masuk)
* **File & Lokasi:** [TopicGraph.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/TopicGraph.java#L109-L121) (Dalam method `computeInDegrees`)
* **Method:** `computeInDegrees()`
* **Potongan Kode:**
```java
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
```

---

## 🟠 BAGIAN 3: PENERAPAN OOP (OBJECT-ORIENTED PROGRAMMING)
Paradigma OOP diterapkan secara eksplisit pada beberapa kelas berikut:

### 1. Encapsulation (Enkapsulasi)
* **File & Lokasi:** [Topic.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/model/Topic.java#L10-L15) (Deklarasi variabel private) dan getter di baris 38-43.
* **Penjelasan:** Variabel-variabel instansiasi seperti `id`, `title`, dan `category` dideklarasikan sebagai `private` dan diakses secara publik menggunakan metode *getter* demi keamanan data.
* **Potongan Kode:**
```java
public class Topic {
    private String id;
    private String title;
    private String category;
    // ... getter untuk mengakses data secara aman ...
    public String getId() { return id; }
    public String getTitle() { return title; }
}
```

### 2. Polymorphism (Polimorfisme) & Overloading
* **Constructor Overloading:** Terletak di [Topic.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/model/Topic.java#L17-L36). Terdapat 4 konstruktor berbeda untuk instansiasi objek `Topic` secara fleksibel sesuai dengan input yang tersedia.
* **Method Overriding:** Terletak di [Topic.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/model/Topic.java#L45-L48) (Meng-override method `toString()` bawaan Java class `Object`).
* **Potongan Kode:**
```java
    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %d Jam - Terbit: %d", id, title, category, duration, publishYear);
    }
```

---

## 🟡 BAGIAN 4: ALGORITMA UTAMA (ALGORITHMS)

### 1. Deteksi Siklus (3-Warna DFS)
* **File:** [CycleDetector.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/CycleDetector.java#L71-L103)
* **Method:** `dfsDetect(String current, Map<String, Integer> color, Map<String, String> parent, List<String> cyclePath)`
* **Potongan Kode:**
```java
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
```

---

### 2. Rekomendasi Urutan Belajar (Topological Sort - Kahn's Algorithm)
* **File:** [TopologicalSort.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/TopologicalSort.java#L20-L94)
* **Method:** `sort()`
* **Potongan Kode:**
```java
    public List<String> sort() {
        System.out.println("\n=== REKOMENDASI URUTAN BELAJAR (Topological Sort) ===");
        System.out.println("Algoritma: Kahn's Algorithm (BFS-based)\n");

        // Step 1: Hitung in-degree setiap node
        Map<String, Integer> inDegree = graph.computeInDegrees();
        // ... (Kode print status in-degree awal) ...

        // Step 2: Masukkan semua node dengan in-degree=0 ke queue
        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> sortedOrder = new ArrayList<>();

        // Step 3-6: Proses queue
        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedOrder.add(current);
            // ... (Kode print step pengambilan node) ...

            // Kurangi in-degree semua dependen
            for (String dependent : graph.getDependents(current)) {
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.add(dependent);
                }
            }
        }

        // Step 7: Cek jika ada siklus (tidak semua node terproses)
        if (sortedOrder.size() != graph.getTopicCount()) {
            System.out.println("\nPeringatan: Graph mengandung siklus!");
            return null;
        }

        return sortedOrder;
    }
```

---

### 3. Penelusuran Prasyarat Lengkap Secara DFS
* **File:** [DFSTraversal.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/DFSTraversal.java#L63-L72)
* **Method:** `dfsPrerequisites(String topicId, Set<String> visited, List<String> order, int depth)`
* **Potongan Kode:**
```java
    private void dfsPrerequisites(String topicId, Set<String> visited, List<String> order, int depth) {
        visited.add(topicId);
        List<String> prereqs = graph.getPrerequisites(topicId); // Membaca dari reverseList
        for (String prereq : prereqs) {
            if (!visited.contains(prereq)) {
                dfsPrerequisites(prereq, visited, order, depth + 1);
            }
        }
        order.add(topicId); // Post-order: dimasukkan ke list setelah prasyaratnya dikunjungi
    }
```

---

## 💻 BAGIAN 5: MAPPING MENU UTAMA & BLOCK CODE (MAIN.JAVA)
Setiap pilihan menu pada program utama dipetakan ke method tertentu di dalam file [Main.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java). Berikut detail pemetaan menu beserta potongan kode fungsinya:

### Menu 1: Cari topik berdasarkan prefix
* **Method Pendukung:** `featureSearchByPrefix()`
* **Lokasi Kode:** [Main.java L111-143](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L111-L143)
* **Potongan Kode:**
```java
    static void featureSearchByPrefix() {
        System.out.print("Masukkan kata kunci / prefix: ");
        String prefix = scanner.nextLine().trim();
        // ... validasi kosong ...
        List<Topic> results = trie.searchByPrefix(prefix);
        // ... print hasil & tawarkan tampilkan prasyarat ...
    }
```

---

### Menu 2: Tampilkan prasyarat sebuah topik
* **Method Pendukung:** `featureShowPrerequisites()`
* **Lokasi Kode:** [Main.java L149-172](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L149-L172)
* **Potongan Kode:**
```java
    static void featureShowPrerequisites() {
        // ... cetak daftar topik & minta input nomor ...
        String selectedId = ids.get(idx);
        dfs.showAllPrerequisites(selectedId); // Tampilkan prasyarat lengkap
        System.out.println();
        dfs.showAllDependents(selectedId);    // Tampilkan dependen lengkap
    }
```

---

### Menu 3: Rekomendasikan urutan belajar
* **Method Pendukung:** `featureLearningOrder()`
* **Lokasi Kode:** [Main.java L178-202](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L178-L202)
* **Potongan Kode:**
```java
    static void featureLearningOrder() {
        boolean hasCycle = cycleDetector.detectCycle(); // Cek siklus terlebih dahulu
        if (!hasCycle) {
            // ... cetak daftar topik & minta input ID target ...
            topoSort.sortForTopic(target); // Cari path ke topik tujuan
        }
    }
```

---

### Menu 4: Deteksi siklus prasyarat
* **Method Pendukung:** `featureDetectCycle()`
* **Lokasi Kode:** [Main.java L208-221](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L208-L221)
* **Potongan Kode:**
```java
    static void featureDetectCycle() {
        System.out.println("\n=== DETEKSI SIKLUS PRASYARAT ===");
        cycleDetector.detectCycle(); // Memanggil kelas CycleDetector
        // ... demo siklus buatan jika diinginkan ...
    }
```

---

### Menu 5: Tampilkan topik tidak terhubung
* **Method Pendukung:** `featureDisconnectedTopics()`
* **Lokasi Kode:** [Main.java L245-286](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L245-L286)
* **Potongan Kode:**
```java
    static void featureDisconnectedTopics() {
        // ... inisialisasi list isolated, noPrereqs, noDependents ...
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
        // ... tampilkan data terisolir, dasar, puncak, dan statistik graph ...
    }
```

---

### Menu 6: Insert data topik baru
* **Method Pendukung:** `featureInsertData()`
* **Lokasi Kode:** [Main.java L292-357](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L292-L357)
* **Potongan Kode:**
```java
    static void featureInsertData() {
        // ... minta input data topik baru ...
        Topic newTopic = new Topic(id, title, category, description, duration, year);
        graph.addTopic(newTopic);
        trie.insert(newTopic);

        for (String prereqId : prerequisiteIds) {
            graph.addPrerequisite(prereqId, id);
        }
    }
```

---

### Menu 7: Update atau delete data
* **Method Pendukung:** `featureUpdateOrDelete()`
* **Lokasi Kode:** [Main.java L447-478](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L447-L478)
* **Potongan Kode:**
```java
    static void featureUpdateOrDelete() {
        // Menggunakan sub-menu:
        // 1. subFeatureUpdateTopic();        -> Main.java L480
        // 2. subFeatureDeleteTopic();        -> Main.java L567
        // 3. subFeatureDeletePrerequisite(); -> Main.java L595
    }
```

---

### Menu 8: Tampilkan semua data
* **Lokasi Kode:** Dipanggil langsung di switch-case di [Main.java L72-75](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L72-L75)
* **Potongan Kode:**
```java
                case "8":
                    graph.displayGraph(); // Tampilkan Adjacency List
                    trie.displayAll();    // Tampilkan semua data di Trie
                    break;
```

---

### Menu 9: Keluar
* **Lokasi Kode:** Dipanggil langsung di switch-case di [Main.java L76-79](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/Main.java#L76-L79)
* **Potongan Kode:**
```java
                case "9":
                    System.out.println("\nTerima kasih telah menggunakan Library Knowledge Navigator!");
                    running = false;
                    break;
```

---

## 💬 SIMULASI PERTANYAAN SIDANG & CONTOH JAWABAN

### 1. Pertanyaan: *"Tunjukkan di mana letak pendeteksian siklus pada Kahn's Algorithm!"*
* **Jawaban:** 
  > *"Buka file [TopologicalSort.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/TopologicalSort.java#L66-L70). Deteksi siklus terjadi di baris 66-70 pada method `sort()`. Kami mengecek apakah ukuran list hasil pengurutan `sortedOrder.size()` sama dengan total topik di graph `graph.getTopicCount()`. Jika tidak sama, berarti ada node yang tersisa karena in-degree-nya tidak pernah mencapai 0 akibat terjebak dalam hubungan siklik (siklus)."*

### 2. Pertanyaan: *"Tunjukkan di mana Anda mengimplementasikan rekursi DFS untuk mengambil semua cabang kata pada pencarian Trie!"*
* **Jawaban:**
  > *"Buka file [Trie.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/tree/Trie.java#L103-L111). Kami menggunakan fungsi pembantu rekursif bernama `collectAllTopics(TrieNode node, List<Topic> results)`. Method ini menggunakan algoritma DFS untuk menelusuri seluruh anak (*children*) dari node prefix saat ini secara rekursif, dan memasukkan objek topik ke dalam list hasil ketika mendapati node penanda akhir kata (`isEndOfWord == true`)."*

### 3. Pertanyaan: *"Bagaimana cara kerja pewarnaan 3-warna saat mendeteksi siklus?"*
* **Jawaban:**
  > *"Buka file [CycleDetector.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/graph/CycleDetector.java#L71-L103). Pada method `dfsDetect()`, kami membagi status node menjadi 3 warna:
  > - **WHITE (0)**: Node belum dikunjungi.
  > - **GRAY (1)**: Node sedang diproses dalam stack aktif. Jika saat DFS kami menemui node berstatus GRAY, artinya ada jalur memutar balik (back-edge) ke node yang sedang aktif diproses, yang berarti **siklus terdeteksi**.
  > - **BLACK (2)**: Node telah selesai diproses sepenuhnya beserta seluruh jalurnya."*

### 4. Pertanyaan: *"Di mana Anda menerapkan pilar OOP (seperti Enkapsulasi & Polimorfisme) dalam kode program Anda?"*
* **Jawaban:**
  > *"Buka file [Topic.java](file:///c:/Users/riezc/Documents/Kuliah/Mata%20Kuliah/Struktur%20Data/FP%20STRUKDAT%202/src/model/Topic.java). Penerapan OOP kami fokuskan pada pemodelan objek:
  > 1. **Enkapsulasi:** Semua variabel di kelas `Topic` dideklarasikan `private` (baris 10-15) dan dibatasi aksesnya dengan *getter* publik di bagian bawah (baris 38-43).
  > 2. **Polimorfisme (Overloading):** Kami melakukan overloading konstruktor (baris 17-36) agar objek topik bisa dibuat secara fleksibel.
  > 3. **Polimorfisme (Overriding):** Di baris 45-48, kami meng-override fungsi `toString()` agar format print objek rapi dan dapat dimodifikasi sendiri."*
