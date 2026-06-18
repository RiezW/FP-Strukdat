# Dokumentasi Teknis — Library Knowledge Navigator

**Final Project Struktur Data 2 — Opsi 4**

Dokumen ini menjelaskan setiap file source code Java secara lengkap. Setiap kode ditampilkan utuh lalu dijelaskan blok per blok (per fungsi/method).

---

## Daftar Isi

1. [Topic.java](#1-topicjava)
2. [TrieNode.java](#2-trienodejava)
3. [Trie.java](#3-triejava)
4. [TopicGraph.java](#4-topicgraphjava)
5. [DFSTraversal.java](#5-dfstraversaljava)
6. [CycleDetector.java](#6-cycledetectorjava)
7. [TopologicalSort.java](#7-topologicalsortjava)
8. [Main.java](#8-mainjava)

---

## 1. `Topic.java`

File: `src/model/Topic.java`

Kelas ini adalah model data (POJO — Plain Old Java Object) yang merepresentasikan satu topik atau buku pembelajaran dalam sistem.

```java
package model;

public class Topic {
    private String id;
    private String title;
    private String category;
    private String description;
    private int duration;
    private int publishYear;

    public Topic(String id, String title, String category, String description, int duration, int publishYear) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.description = description;
        this.duration = duration;
        this.publishYear = publishYear;
    }

    public Topic(String id, String title, String category, String description, int duration) {
        this(id, title, category, description, duration, 2020);
    }

    public Topic(String id, String title, String category, String description) {
        this(id, title, category, description, 0, 2020);
    }

    public Topic(String id, String title) {
        this(id, title, "General", "", 0, 2020);
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public int getDuration() { return duration; }
    public int getPublishYear() { return publishYear; }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) - %d Jam - Terbit: %d", id, title, category, duration, publishYear);
    }
}
```

### Penjelasan per Blok

**Deklarasi Field (Atribut)**

```java
private String id;
private String title;
private String category;
private String description;
private int duration;
private int publishYear;
```

Menyimpan 6 atribut setiap topik. Semuanya `private` agar hanya bisa diakses lewat method getter (prinsip enkapsulasi OOP). `id` adalah identitas unik seperti `T01`, `T02`, dst.

---

**Constructor Utama (6 Parameter)**

```java
public Topic(String id, String title, String category, String description, int duration, int publishYear) {
    this.id = id;
    this.title = title;
    ...
}
```

Constructor ini menerima semua 6 atribut sekaligus. Kata kunci `this.` digunakan untuk membedakan antara field milik objek dengan parameter yang namanya sama.

---

**Constructor Overloading (3 Versi Singkat)**

```java
public Topic(String id, String title, String category, String description, int duration) {
    this(id, title, category, description, duration, 2020);
}
public Topic(String id, String title, String category, String description) {
    this(id, title, category, description, 0, 2020);
}
public Topic(String id, String title) {
    this(id, title, "General", "", 0, 2020);
}
```

Java mendukung *constructor overloading* — beberapa constructor dengan jumlah parameter berbeda. Ketiga constructor ini memanggil constructor utama (`this(...)`) dengan nilai default untuk parameter yang tidak diberikan:
- `publishYear` default = `2020`
- `duration` default = `0`
- `category` default = `"General"`
- `description` default = `""`

Ini memudahkan pembuatan objek `Topic` sederhana (misalnya untuk demo) tanpa perlu mengisi semua atribut.

---

**Getter Methods**

```java
public String getId() { return id; }
public String getTitle() { return title; }
...
```

Setiap atribut memiliki getter masing-masing. Karena field dideklarasikan `private`, kelas lain tidak bisa langsung mengaksesnya — mereka harus memanggil getter ini.

---

**Method `toString()`**

```java
@Override
public String toString() {
    return String.format("[%s] %s (%s) - %d Jam - Terbit: %d", id, title, category, duration, publishYear);
}
```

Method ini di-*override* dari kelas `Object` (parent semua kelas di Java). Ketika sebuah objek `Topic` dicetak dengan `System.out.println(topic)`, Java otomatis memanggil method ini. Hasilnya berformat seperti: `[T01] Pengantar Pemrograman (Dasar) - 4 Jam - Terbit: 2008`.

---

## 2. `TrieNode.java`

File: `src/tree/TrieNode.java`

Kelas ini merepresentasikan satu node di dalam struktur Trie. Setiap karakter dari judul topik disimpan sebagai sebuah node.

```java
package tree;

import java.util.HashMap;
import java.util.Map;

public class TrieNode {
    Map<Character, TrieNode> children;
    boolean isEndOfWord;
    String topicId;

    public TrieNode() {
        children = new HashMap<>();
        isEndOfWord = false;
        topicId = null;
    }
}
```

### Penjelasan per Blok

**Field `children`**

```java
Map<Character, TrieNode> children;
```

Menyimpan peta dari sebuah karakter ke node anaknya. Contoh: node `'a'` bisa punya anak `'l'` yang menuju node `'l'`, dst. Digunakan `HashMap` agar akses ke child node berdasarkan karakter berjalan dalam O(1). Ini yang membentuk cabang pohon Trie.

---

**Field `isEndOfWord`**

```java
boolean isEndOfWord;
```

Penanda apakah node ini merupakan akhir dari sebuah kata (judul topik). Satu node bisa sekaligus menjadi akhir kata dan memiliki children (misalnya "OOP" adalah akhir kata, tapi juga memiliki lanjutan "OOP - Dasar", "OOP - Inheritance", dst.).

---

**Field `topicId`**

```java
String topicId;
```

Menyimpan ID topik (seperti `T10`) hanya jika `isEndOfWord` bernilai `true`. Digunakan untuk mengambil data lengkap `Topic` dari `topicMap` di kelas `Trie`.

---

**Constructor**

```java
public TrieNode() {
    children = new HashMap<>();
    isEndOfWord = false;
    topicId = null;
}
```

Setiap node baru diinisialisasi dengan map kosong, belum menjadi akhir kata, dan belum punya topicId. Node baru dibuat setiap kali ditemukan karakter baru saat proses `insert`.

---

## 3. `Trie.java`

File: `src/tree/Trie.java`

Kelas ini mengimplementasikan struktur data Trie (Prefix Tree) yang digunakan untuk pencarian topik berdasarkan kata kunci awalan (prefix). Pencarian di Trie berjalan O(L) di mana L adalah panjang prefix, jauh lebih efisien dari pencarian linear O(N) pada list biasa.

```java
package tree;

import model.Topic;
import java.util.*;

public class Trie {

    private TrieNode root;
    private Map<String, Topic> topicMap;

    public Trie() {
        root = new TrieNode();
        topicMap = new HashMap<>();
    }

    public void insert(Topic topic) {
        topicMap.put(topic.getId(), topic);
        String title = topic.getTitle().toLowerCase();
        TrieNode current = root;

        for (char ch : title.toCharArray()) {
            current.children.putIfAbsent(ch, new TrieNode());
            current = current.children.get(ch);
        }

        current.isEndOfWord = true;
        current.topicId = topic.getId();
    }

    public List<Topic> searchByPrefix(String prefix) {
        List<Topic> results = new ArrayList<>();
        TrieNode current = root;
        String lowerPrefix = prefix.toLowerCase();

        for (char ch : lowerPrefix.toCharArray()) {
            if (!current.children.containsKey(ch)) {
                return results;
            }
            current = current.children.get(ch);
        }

        collectAllTopics(current, results);
        return results;
    }

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

    private void collectAllTopics(TrieNode node, List<Topic> results) {
        if (node.isEndOfWord && node.topicId != null) {
            Topic t = topicMap.get(node.topicId);
            if (t != null) results.add(t);
        }
        for (TrieNode child : node.children.values()) {
            collectAllTopics(child, results);
        }
    }

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
```

### Penjelasan per Blok

**Field dan Constructor**

```java
private TrieNode root;
private Map<String, Topic> topicMap;

public Trie() {
    root = new TrieNode();
    topicMap = new HashMap<>();
}
```

`root` adalah titik awal (pintu masuk) dari seluruh pohon Trie. Semua pencarian dan penyisipan dimulai dari node ini. `topicMap` adalah peta pendamping yang menyimpan objek `Topic` lengkap, dengan kunci berupa `topicId`. Ini diperlukan karena Trie hanya menyimpan karakter; untuk mendapatkan data lengkap topik, perlu dicari di `topicMap` menggunakan `topicId` yang tersimpan di node.

---

**Method `insert(Topic topic)`**

```java
public void insert(Topic topic) {
    topicMap.put(topic.getId(), topic);
    String title = topic.getTitle().toLowerCase();
    TrieNode current = root;

    for (char ch : title.toCharArray()) {
        current.children.putIfAbsent(ch, new TrieNode());
        current = current.children.get(ch);
    }

    current.isEndOfWord = true;
    current.topicId = topic.getId();
}
```

Menyisipkan satu topik ke dalam Trie. Prosesnya:
1. Simpan objek `Topic` ke `topicMap`.
2. Konversi judul ke huruf kecil agar pencarian *case-insensitive*.
3. Untuk setiap karakter judul, cek apakah node untuk karakter itu sudah ada. Jika belum, buat node baru (`putIfAbsent`). Lalu pindah ke node tersebut.
4. Setelah semua karakter diproses, tandai node terakhir sebagai akhir kata (`isEndOfWord = true`) dan simpan `topicId`.

Kompleksitas: **O(L)** di mana L adalah panjang judul.

---

**Method `searchByPrefix(String prefix)`**

```java
public List<Topic> searchByPrefix(String prefix) {
    List<Topic> results = new ArrayList<>();
    TrieNode current = root;
    String lowerPrefix = prefix.toLowerCase();

    for (char ch : lowerPrefix.toCharArray()) {
        if (!current.children.containsKey(ch)) {
            return results; // prefix tidak ditemukan
        }
        current = current.children.get(ch);
    }

    collectAllTopics(current, results);
    return results;
}
```

Mencari semua topik yang judulnya diawali dengan `prefix`. Prosesnya:
1. Telusuri Trie karakter demi karakter mengikuti prefix. Jika suatu karakter tidak ditemukan, berarti tidak ada topik dengan prefix tersebut → kembalikan list kosong.
2. Setelah mencapai ujung node prefix, panggil `collectAllTopics` untuk mengumpulkan semua topik yang berada di bawah node tersebut (semua kemungkinan lanjutan dari prefix ini).

Kompleksitas: **O(L + K)** di mana L = panjang prefix, K = jumlah hasil yang cocok.

---

**Method `searchExact(String title)`**

```java
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
```

Mencari topik dengan judul yang **tepat sama** (exact match). Berbeda dengan `searchByPrefix`, method ini hanya berhasil jika node terakhir dari pencarian ditandai `isEndOfWord = true`. Jika tidak (artinya string yang dicari hanya merupakan awalan dari topik lain), method mengembalikan `null`.

Kompleksitas: **O(L)** di mana L adalah panjang judul.

---

**Method `collectAllTopics(TrieNode node, List<Topic> results)` (Private Helper)**

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

Method rekursif yang melakukan **DFS (Depth-First Search)** pada Trie untuk mengumpulkan semua topik dari suatu node ke bawah. Logikanya:
1. Jika node saat ini adalah akhir kata, tambahkan topik terkait ke `results`.
2. Rekursif ke semua node anak.

Digunakan oleh `searchByPrefix` dan `displayAll`.

---

**Method `delete(String title)` dan `deleteHelper(...)` (Rekursif)**

```java
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
```

Menghapus satu topik dari Trie menggunakan pendekatan **rekursif post-order** (proses dilakukan setelah rekursi kembali). Cara kerjanya:

**Base case** (saat `index == word.length()`, artinya sudah sampai node terakhir):
- Jika node ini bukan akhir kata → kata tidak ada, return `false`.
- Hapus `topicId` dari `topicMap`.
- Tandai `isEndOfWord = false`.
- Return `true` (boleh hapus node ini) hanya jika node ini tidak punya anak.

**Kasus rekursif:**
- Pergi ke node anak sesuai karakter berikutnya.
- Setelah rekursi kembali, jika anak boleh dihapus (`shouldDeleteChild = true`), hapus referensi ke anak tersebut.
- Node saat ini juga boleh dihapus jika tidak lagi punya anak dan bukan akhir kata lain.

Strategi ini memastikan node yang masih digunakan oleh kata/topik lain **tidak ikut terhapus**.

Kompleksitas: **O(L)**.

---

**Method `displayAll()`**

```java
public void displayAll() {
    System.out.println("\n=== SEMUA TOPIK DALAM TRIE ===");
    List<Topic> all = new ArrayList<>();
    collectAllTopics(root, all);
    ...
}
```

Menampilkan semua topik yang tersimpan dalam Trie dengan cara melakukan DFS dari `root` menggunakan `collectAllTopics`. Digunakan pada Menu 8.

---

## 4. `TopicGraph.java`

File: `src/graph/TopicGraph.java`

Kelas inti yang mengimplementasikan Directed Graph menggunakan Adjacency List untuk merepresentasikan hubungan prasyarat antar topik. Edge `A → B` berarti "Topik A adalah prasyarat dari Topik B".

```java
package graph;

import model.Topic;
import java.util.*;

public class TopicGraph {

    private Map<String, List<String>> adjacencyList;
    private Map<String, List<String>> reverseList;
    private Map<String, Topic> topics;

    public TopicGraph() {
        adjacencyList = new LinkedHashMap<>();
        reverseList = new LinkedHashMap<>();
        topics = new LinkedHashMap<>();
    }

    public void addTopic(Topic topic) {
        String id = topic.getId();
        topics.put(id, topic);
        adjacencyList.putIfAbsent(id, new ArrayList<>());
        reverseList.putIfAbsent(id, new ArrayList<>());
    }

    public void addPrerequisite(String prerequisiteTitleOrId, String dependentTitleOrId) {
        String prereqId = findIdByTitleOrId(prerequisiteTitleOrId);
        String depId = findIdByTitleOrId(dependentTitleOrId);

        if (prereqId == null) {
            throw new IllegalArgumentException("Topik prasyarat tidak ditemukan: " + prerequisiteTitleOrId);
        }
        if (depId == null) {
            throw new IllegalArgumentException("Topik dependen tidak ditemukan: " + dependentTitleOrId);
        }
        adjacencyList.get(prereqId).add(depId);
        reverseList.get(depId).add(prereqId);
    }

    private String findIdByTitleOrId(String identifier) {
        if (topics.containsKey(identifier)) {
            return identifier;
        }
        for (Topic t : topics.values()) {
            if (t.getTitle().equalsIgnoreCase(identifier)) {
                return t.getId();
            }
        }
        return null;
    }

    public List<String> getDependents(String topicId) {
        return adjacencyList.getOrDefault(topicId, new ArrayList<>());
    }

    public List<String> getPrerequisites(String topicId) {
        return reverseList.getOrDefault(topicId, new ArrayList<>());
    }

    public Map<String, Integer> computeInDegrees() {
        Map<String, Integer> inDegree = new LinkedHashMap<>();
        for (String id : topics.keySet()) {
            inDegree.put(id, 0);
        }
        for (String id : topics.keySet()) {
            for (String dep : adjacencyList.get(id)) {
                inDegree.put(dep, inDegree.get(dep) + 1);
            }
        }
        return inDegree;
    }

    public void displayGraph() {
        System.out.println("\n=== GRAPH PRASYARAT (Adjacency List) ===");
        System.out.println("Format: Topik > [Topik yang membutuhkan ini]\n");
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

    public boolean removeTopic(String id) {
        if (!topics.containsKey(id)) {
            return false;
        }
        topics.remove(id);
        adjacencyList.remove(id);
        for (List<String> dependents : adjacencyList.values()) {
            dependents.remove(id);
        }
        reverseList.remove(id);
        for (List<String> prereqs : reverseList.values()) {
            prereqs.remove(id);
        }
        return true;
    }

    public boolean removePrerequisite(String prereqId, String depId) {
        if (!topics.containsKey(prereqId) || !topics.containsKey(depId)) {
            return false;
        }
        List<String> deps = adjacencyList.get(prereqId);
        List<String> prereqs = reverseList.get(depId);

        boolean removedFromDeps = (deps != null) && deps.remove(depId);
        boolean removedFromPrereqs = (prereqs != null) && prereqs.remove(prereqId);

        return removedFromDeps || removedFromPrereqs;
    }

    public void updateTopic(String id, Topic newTopic) {
        topics.put(id, newTopic);
    }
}
```

### Penjelasan per Blok

**Field dan Constructor**

```java
private Map<String, List<String>> adjacencyList;
private Map<String, List<String>> reverseList;
private Map<String, Topic> topics;
```

Graf ini menggunakan **dua adjacency list** secara bersamaan:

- `adjacencyList`: untuk setiap topik, menyimpan daftar topik yang **membutuhkan** topik ini sebagai prasyarat. `"T01" → ["T02", "T03"]` artinya T02 dan T03 membutuhkan T01 sebagai prasyarat.
- `reverseList`: kebalikannya — untuk setiap topik, menyimpan daftar **prasyarat langsung** topik ini. `"T03" → ["T01", "T02"]` artinya T03 membutuhkan T01 dan T02.
- `topics`: menyimpan objek `Topic` lengkap, diakses dengan `topicId`.

Menggunakan `LinkedHashMap` agar urutan penyisipan data terjaga saat ditampilkan.

Kompleksitas ruang: **O(V + E)** — hanya menyimpan yang ada, bukan seluruh matriks V×V.

---

**Method `addTopic(Topic topic)`**

```java
public void addTopic(Topic topic) {
    String id = topic.getId();
    topics.put(id, topic);
    adjacencyList.putIfAbsent(id, new ArrayList<>());
    reverseList.putIfAbsent(id, new ArrayList<>());
}
```

Menambahkan node baru ke graf. Topik disimpan di `topics`, dan disiapkan dua list kosong — satu di `adjacencyList` dan satu di `reverseList` — siap diisi relasi di kemudian hari. Menggunakan `putIfAbsent` untuk menghindari penimpaan data yang sudah ada.

Kompleksitas: **O(1)**.

---

**Method `addPrerequisite(String prerequisiteTitleOrId, String dependentTitleOrId)`**

```java
public void addPrerequisite(String prerequisiteTitleOrId, String dependentTitleOrId) {
    String prereqId = findIdByTitleOrId(prerequisiteTitleOrId);
    String depId = findIdByTitleOrId(dependentTitleOrId);
    ...
    adjacencyList.get(prereqId).add(depId);
    reverseList.get(depId).add(prereqId);
}
```

Menambahkan satu edge prasyarat. Method ini fleksibel: menerima baik ID (`"T01"`) maupun judul (`"Pengantar Pemrograman"`). Secara internal keduanya dikonversi ke ID menggunakan `findIdByTitleOrId`. Setelah itu, relasi dicatat di **kedua arah** secara bersamaan:
- `adjacencyList[prereqId]` ditambah `depId` → "topik mana yang bergantung pada T01"
- `reverseList[depId]` ditambah `prereqId` → "apa saja prasyarat T02"

Kompleksitas: **O(1)** untuk penambahan ke list.

---

**Method `findIdByTitleOrId(String identifier)` (Private Helper)**

```java
private String findIdByTitleOrId(String identifier) {
    if (topics.containsKey(identifier)) {
        return identifier;
    }
    for (Topic t : topics.values()) {
        if (t.getTitle().equalsIgnoreCase(identifier)) {
            return t.getId();
        }
    }
    return null;
}
```

Mencari ID topik berdasarkan input yang bisa berupa ID atau judul. Pertama dicek apakah input adalah ID yang valid. Jika tidak, dilakukan pencarian linear di semua topik untuk mencocokkan judul (case-insensitive). Mengembalikan `null` jika tidak ditemukan.

---

**Method `getDependents` dan `getPrerequisites`**

```java
public List<String> getDependents(String topicId) {
    return adjacencyList.getOrDefault(topicId, new ArrayList<>());
}

public List<String> getPrerequisites(String topicId) {
    return reverseList.getOrDefault(topicId, new ArrayList<>());
}
```

Dua method akses cepat yang memanfaatkan kedua adjacency list. `getDependents` menjawab "siapa yang butuh topik ini?" dan `getPrerequisites` menjawab "apa yang harus dipelajari sebelum topik ini?". Menggunakan `getOrDefault` untuk menghindari `NullPointerException` jika ID tidak ditemukan.

---

**Method `computeInDegrees()`**

```java
public Map<String, Integer> computeInDegrees() {
    Map<String, Integer> inDegree = new LinkedHashMap<>();
    for (String id : topics.keySet()) {
        inDegree.put(id, 0);
    }
    for (String id : topics.keySet()) {
        for (String dep : adjacencyList.get(id)) {
            inDegree.put(dep, inDegree.get(dep) + 1);
        }
    }
    return inDegree;
}
```

Menghitung **in-degree** (jumlah prasyarat) setiap topik. Digunakan oleh Kahn's Algorithm di `TopologicalSort`. Prosesnya:
1. Inisialisasi semua topik dengan in-degree = 0.
2. Untuk setiap edge `A → B` (A adalah prasyarat B), tambahkan 1 ke in-degree B.

Topik dengan in-degree = 0 adalah topik yang tidak punya prasyarat (titik awal belajar).

Kompleksitas: **O(V + E)**.

---

**Method `displayGraph()`**

```java
public void displayGraph() {
    ...
    for (String id : adjacencyList.keySet()) {
        Topic t = topics.get(id);
        List<String> deps = adjacencyList.get(id);
        ...
        StringJoiner sj = new StringJoiner(", ", "[", "]");
        for (String dep : deps) {
            sj.add(topics.get(dep).getTitle());
        }
        ...
    }
}
```

Mencetak seluruh adjacency list ke konsol. Untuk setiap topik, ditampilkan daftar topik yang bergantung padanya. Menggunakan `StringJoiner` untuk menggabungkan judul topik dengan pemisah koma secara rapi.

---

**Method `removeTopic(String id)`**

```java
public boolean removeTopic(String id) {
    if (!topics.containsKey(id)) return false;

    topics.remove(id);
    adjacencyList.remove(id);
    for (List<String> dependents : adjacencyList.values()) {
        dependents.remove(id);
    }
    reverseList.remove(id);
    for (List<String> prereqs : reverseList.values()) {
        prereqs.remove(id);
    }
    return true;
}
```

Menghapus sebuah topik beserta **semua relasi** yang melibatkannya, baik sebagai prasyarat maupun sebagai dependen. Langkah-langkahnya:
1. Hapus dari `topics`.
2. Hapus entry topik dari `adjacencyList` (semua edge keluar darinya).
3. Iterasi semua list di `adjacencyList` untuk menghapus `id` yang mungkin muncul sebagai dependen topik lain.
4. Hal yang sama dilakukan untuk `reverseList`.

Ini memastikan tidak ada referensi "menggantung" ke topik yang sudah dihapus.

---

**Method `removePrerequisite(String prereqId, String depId)`**

```java
public boolean removePrerequisite(String prereqId, String depId) {
    ...
    boolean removedFromDeps = (deps != null) && deps.remove(depId);
    boolean removedFromPrereqs = (prereqs != null) && prereqs.remove(prereqId);
    return removedFromDeps || removedFromPrereqs;
}
```

Menghapus satu relasi prasyarat spesifik (satu edge) tanpa menghapus topiknya. Dihapus dari kedua adjacency list (`adjacencyList` dan `reverseList`) agar keduanya tetap konsisten.

---

**Method `updateTopic(String id, Topic newTopic)`**

```java
public void updateTopic(String id, Topic newTopic) {
    topics.put(id, newTopic);
}
```

Mengganti data topik di `topics` map dengan objek `Topic` baru. Relasi (edge) tidak berubah karena relasi disimpan berdasarkan ID, bukan objek `Topic` itu sendiri.

---

## 5. `DFSTraversal.java`

File: `src/graph/DFSTraversal.java`

Kelas ini mengimplementasikan algoritma DFS (Depth-First Search) untuk menelusuri hubungan prasyarat antar topik, baik ke arah prasyarat (mundur) maupun ke arah dependen (maju).

```java
package graph;

import model.Topic;
import java.util.*;

public class DFSTraversal {

    private TopicGraph graph;

    public DFSTraversal(TopicGraph graph) {
        this.graph = graph;
    }

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

        order.remove(topicId);

        if (order.isEmpty()) {
            System.out.println("  > Topik ini tidak memiliki prasyarat.");
        } else {
            System.out.println("  Urutan prasyarat yang harus dipelajari lebih dulu:");
            Collections.reverse(order);
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

    public void showAllDependents(String topicId) {
        if (!graph.containsTopic(topicId)) {
            System.out.println("Topik tidak ditemukan: " + topicId);
            return;
        }

        Topic topic = graph.getTopic(topicId);
        System.out.println("\n=== TOPIK YANG MEMBUTUHKAN: " + topic.getTitle() + " ===");

        Set<String> visited = new LinkedHashSet<>();
        dfsForward(topicId, visited, "  ", true);

        visited.remove(topicId);
        if (visited.isEmpty()) {
            System.out.println("  Tidak ada topik yang bergantung pada topik ini.");
        }
    }

    private void dfsForward(String topicId, Set<String> visited, String indent, boolean isRoot) {
        visited.add(topicId);
        List<String> dependents = graph.getDependents(topicId);
        for (String dep : dependents) {
            Topic t = graph.getTopic(dep);
            System.out.printf("%s> %s%n", indent, t.getTitle());
            if (!visited.contains(dep)) {
                dfsForward(dep, visited, indent + "  ", false);
            }
        }
    }

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
```

### Penjelasan per Blok

**Constructor**

```java
public DFSTraversal(TopicGraph graph) {
    this.graph = graph;
}
```

Menerima referensi ke `TopicGraph` yang akan ditelusuri. Semua operasi DFS di kelas ini dilakukan pada graf yang sama.

---

**Method `showAllPrerequisites(String topicId)`**

```java
public void showAllPrerequisites(String topicId) {
    ...
    Set<String> visited = new LinkedHashSet<>();
    List<String> order = new ArrayList<>();
    dfsPrerequisites(topicId, visited, order, 0);

    order.remove(topicId);
    Collections.reverse(order);
    ...
}
```

Menampilkan semua prasyarat sebuah topik, baik langsung maupun tidak langsung. Menggunakan DFS ke arah terbalik (dari dependen ke prasyarat) dengan memanggil `graph.getPrerequisites()`. Setelah DFS selesai, `order` berisi topik dalam urutan *post-order* (topik yang selesai diproses paling belakang ada di akhir list). Karena prasyarat paling dasar selesai diproses lebih dulu dan masuk ke `order` lebih awal, dilakukan `Collections.reverse` agar tampil dari yang paling mendasar ke yang paling lanjut.

Kompleksitas: **O(V + E)**.

---

**Method `dfsPrerequisites(...)` (Private Rekursif)**

```java
private void dfsPrerequisites(String topicId, Set<String> visited, List<String> order, int depth) {
    visited.add(topicId);
    List<String> prereqs = graph.getPrerequisites(topicId);
    for (String prereq : prereqs) {
        if (!visited.contains(prereq)) {
            dfsPrerequisites(prereq, visited, order, depth + 1);
        }
    }
    order.add(topicId); // post-order: tambahkan setelah semua prasyarat selesai
}
```

Inti DFS rekursif ke arah prasyarat (menggunakan `reverseList`). Set `visited` mencegah satu topik dikunjungi dua kali (menghindari loop tak terbatas). Penambahan ke `order` dilakukan di **akhir** (post-order), sehingga topik yang paling mendasar akan masuk duluan.

---

**Method `showAllDependents(String topicId)`**

```java
public void showAllDependents(String topicId) {
    ...
    Set<String> visited = new LinkedHashSet<>();
    dfsForward(topicId, visited, "  ", true);
    ...
}
```

Kebalikan dari `showAllPrerequisites` — menampilkan topik mana saja yang **bergantung** pada topik ini (langsung maupun tidak langsung). Menggunakan DFS maju (forward) dengan `graph.getDependents()`.

---

**Method `dfsForward(...)` (Private Rekursif)**

```java
private void dfsForward(String topicId, Set<String> visited, String indent, boolean isRoot) {
    visited.add(topicId);
    List<String> dependents = graph.getDependents(topicId);
    for (String dep : dependents) {
        Topic t = graph.getTopic(dep);
        System.out.printf("%s> %s%n", indent, t.getTitle());
        if (!visited.contains(dep)) {
            dfsForward(dep, visited, indent + "  ", false);
        }
    }
}
```

DFS maju rekursif yang menelusuri `adjacencyList`. Parameter `indent` bertambah dua spasi setiap level rekursi, menghasilkan tampilan hirarkis (tree-like) di konsol. Topik yang sudah dikunjungi tetap dicetak namun tidak direkursi ulang (untuk menampilkan relasi tanpa loop).

---

**Method `reachableFrom(String startId)`**

```java
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
```

Mengembalikan Set berisi semua node (termasuk `startId`) yang dapat dicapai dari `startId` mengikuti arah edge. Berguna untuk analisis konektivitas graf. Tidak mencetak apapun; hanya mengumpulkan ID ke dalam Set.

---

## 6. `CycleDetector.java`

File: `src/graph/CycleDetector.java`

Kelas ini mendeteksi adanya siklus dalam directed graph menggunakan algoritma **DFS 3-Warna**. Siklus dalam graf prasyarat berarti terdapat relasi melingkar (A butuh B, B butuh C, C butuh A) yang menyebabkan tidak ada titik awal belajar yang valid.

```java
package graph;

import model.Topic;
import java.util.*;

public class CycleDetector {

    private static final int WHITE = 0;
    private static final int GRAY  = 1;
    private static final int BLACK = 2;

    private TopicGraph graph;

    public CycleDetector(TopicGraph graph) {
        this.graph = graph;
    }

    public boolean detectCycle() {
        Map<String, Integer> color = new HashMap<>();
        Map<String, String> parent = new HashMap<>();
        List<String> cyclePath = new ArrayList<>();

        for (String id : graph.getTopics().keySet()) {
            color.put(id, WHITE);
            parent.put(id, null);
        }

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

    private boolean dfsDetect(String current, Map<String, Integer> color,
                               Map<String, String> parent, List<String> cyclePath) {
        color.put(current, GRAY);

        for (String neighbor : graph.getDependents(current)) {
            if (color.get(neighbor) == GRAY) {
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
        }

        color.put(current, BLACK);
        return false;
    }

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
    }
}
```

### Penjelasan per Blok

**Konstanta Warna**

```java
private static final int WHITE = 0;
private static final int GRAY  = 1;
private static final int BLACK = 2;
```

Tiga warna yang merepresentasikan status pemrosesan setiap node dalam DFS:
- **WHITE (0)**: Node belum pernah dikunjungi sama sekali.
- **GRAY (1)**: Node sedang dalam proses DFS — ada di *call stack* rekursif yang aktif saat ini.
- **BLACK (2)**: Node sudah selesai diproses sepenuhnya (semua tetangganya sudah dikunjungi).

Kunci deteksi siklus: jika saat menelusuri tetangga ditemukan node yang masih **GRAY**, berarti ada *back-edge* — jalur yang mengarah kembali ke leluhur dalam DFS tree saat ini — yang menandakan siklus.

---

**Method `detectCycle()`**

```java
public boolean detectCycle() {
    Map<String, Integer> color = new HashMap<>();
    Map<String, String> parent = new HashMap<>();
    List<String> cyclePath = new ArrayList<>();

    for (String id : graph.getTopics().keySet()) {
        color.put(id, WHITE);
        parent.put(id, null);
    }

    for (String id : graph.getTopics().keySet()) {
        if (color.get(id) == WHITE) {
            if (dfsDetect(id, color, parent, cyclePath)) {
                printCycleInfo(cyclePath);
                return true;
            }
        }
    }
    ...
}
```

Fungsi utama yang mengorkestrasi deteksi siklus di seluruh graf. Prosesnya:
1. Inisialisasi semua node sebagai WHITE, parent sebagai null.
2. Iterasi semua node — jika masih WHITE (belum dikunjungi), jalankan DFS dari sana. Ini penting karena graf bisa terdiri dari beberapa komponen yang tidak terhubung.
3. Jika `dfsDetect` menemukan siklus, tampilkan informasinya dan return `true`.
4. Jika semua node diproses tanpa siklus, return `false` (graf adalah DAG yang valid).

Kompleksitas: **O(V + E)**.

---

**Method `dfsDetect(...)` (Private Rekursif)**

```java
private boolean dfsDetect(String current, ...) {
    color.put(current, GRAY); // masuk ke call stack

    for (String neighbor : graph.getDependents(current)) {
        if (color.get(neighbor) == GRAY) {
            // SIKLUS DITEMUKAN: neighbor adalah leluhur di call stack saat ini
            // Rekonstruksi path siklus...
            return true;
        }
        if (color.get(neighbor) == WHITE) {
            parent.put(neighbor, current);
            if (dfsDetect(neighbor, color, parent, cyclePath)) {
                return true;
            }
        }
        // Jika BLACK: sudah selesai diproses sebelumnya, aman dilewati
    }

    color.put(current, BLACK); // selesai diproses
    return false;
}
```

DFS rekursif dengan pewarnaan 3-warna:
- Saat **masuk** ke node: warnai GRAY.
- Untuk setiap tetangga: jika GRAY → siklus terdeteksi; jika WHITE → rekursi ke sana; jika BLACK → lewati (aman).
- Saat **keluar** dari node (semua tetangga selesai): warnai BLACK.

Ketika siklus ditemukan, path siklus direkonstruksi menggunakan map `parent` dengan cara menelusuri balik dari node yang membentuk siklus ke awal siklus.

---

**Method `printCycleInfo(List<String> cyclePath)`**

```java
private void printCycleInfo(List<String> cyclePath) {
    System.out.println("\nSIKLUS TERDETEKSI!...");
    for (int i = 0; i < cyclePath.size(); i++) {
        ...
        System.out.printf("   \"%s\" > ", t.getTitle());
        ...
    }
    System.out.println("\nSolusi: Hapus salah satu edge yang membentuk siklus.");
}
```

Menampilkan informasi siklus yang ditemukan dalam format yang mudah dibaca. Mencetak jalur siklus sebagai rangkaian judul topik yang membentuk lingkaran, serta saran solusi untuk menghilangkan siklus.

---

## 7. `TopologicalSort.java`

File: `src/graph/TopologicalSort.java`

Kelas ini mengimplementasikan Topological Sort menggunakan **Kahn's Algorithm (BFS-based)** untuk menghasilkan urutan belajar yang valid berdasarkan hubungan prasyarat.

```java
package graph;

import model.Topic;
import java.util.*;

public class TopologicalSort {

    private TopicGraph graph;

    public TopologicalSort(TopicGraph graph) {
        this.graph = graph;
    }

    public List<String> sort() {
        System.out.println("\n=== REKOMENDASI URUTAN BELAJAR (Topological Sort) ===");
        System.out.println("Algoritma: Kahn's Algorithm (BFS-based)\n");

        Map<String, Integer> inDegree = graph.computeInDegrees();

        System.out.println("In-degree awal (jumlah prasyarat per topik):");
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            Topic t = graph.getTopic(entry.getKey());
            System.out.printf("  %-35s : %d prasyarat%n", t.getTitle(), entry.getValue());
        }

        Queue<String> queue = new LinkedList<>();
        for (Map.Entry<String, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        List<String> sortedOrder = new ArrayList<>();
        System.out.println("\nProses Kahn's Algorithm:");
        int step = 1;
        while (!queue.isEmpty()) {
            String current = queue.poll();
            sortedOrder.add(current);
            Topic currentTopic = graph.getTopic(current);

            System.out.printf("  Step %d: Ambil \"%s\" (in-degree=0)%n",
                    step++, currentTopic.getTitle());

            for (String dependent : graph.getDependents(current)) {
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.add(dependent);
                    System.out.printf("         > \"%s\" siap dipelajari (in-degree jadi 0)%n",
                            graph.getTopic(dependent).getTitle());
                }
            }
        }

        if (sortedOrder.size() != graph.getTopicCount()) {
            System.out.println("\nPeringatan: Graph mengandung siklus!");
            return null;
        }

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
                    inDegree.put(dependent, inDegree.get(dependent) + 1);
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
                inDegree.put(dependent, inDegree.get(dependent) - 1);
                if (inDegree.get(dependent) == 0) {
                    queue.add(dependent);
                }
            }
        }

        System.out.println("\n========================================");
        System.out.println("LEARNING PATH MENUJU " + graph.getTopic(targetId).getTitle());
        System.out.println("========================================\n");
        for (int i = 0; i < sortedOrder.size(); i++) {
            Topic t = graph.getTopic(sortedOrder.get(i));
            System.out.printf("%2d. %s (%s)%n", i + 1, t.getTitle(), t.getId());
        }

        return sortedOrder;
    }
}
```

### Penjelasan per Blok

**Method `sort()` — Topological Sort Seluruh Graf**

```java
// Step 1: Hitung in-degree
Map<String, Integer> inDegree = graph.computeInDegrees();

// Step 2: Masukkan semua node in-degree=0 ke queue
Queue<String> queue = new LinkedList<>();
for (...) {
    if (entry.getValue() == 0) queue.add(entry.getKey());
}

// Step 3: Proses queue (Kahn's Algorithm)
while (!queue.isEmpty()) {
    String current = queue.poll();
    sortedOrder.add(current);
    for (String dependent : graph.getDependents(current)) {
        inDegree.put(dependent, inDegree.get(dependent) - 1);
        if (inDegree.get(dependent) == 0) queue.add(dependent);
    }
}

// Step 4: Validasi - jika ada siklus, tidak semua node terproses
if (sortedOrder.size() != graph.getTopicCount()) return null;
```

Mengimplementasikan **Kahn's Algorithm** untuk seluruh graf. Cara kerjanya:
1. Hitung in-degree semua node (jumlah prasyarat per topik).
2. Masukkan semua node dengan in-degree = 0 (tidak punya prasyarat) ke antrian.
3. Ambil satu node dari antrian, tambahkan ke hasil. Untuk setiap topik yang bergantung padanya, kurangi in-degree-nya. Jika in-degree-nya jadi 0 (semua prasyaratnya sudah diambil), masukkan ke antrian.
4. Ulangi hingga antrian kosong.
5. Jika jumlah topik yang terproses kurang dari total topik, berarti ada siklus (Kahn's Algorithm tidak bisa memproses node dalam siklus karena in-degree-nya tidak pernah bisa menjadi 0).

Kompleksitas: **O(V + E)**.

---

**Method `collectPrerequisites(String topicId, Set<String> requiredTopics)` (Private Rekursif)**

```java
private void collectPrerequisites(String topicId, Set<String> requiredTopics) {
    if (requiredTopics.contains(topicId)) return; // sudah dikumpulkan, stop
    requiredTopics.add(topicId);
    for (String prereq : graph.getPrerequisites(topicId)) {
        collectPrerequisites(prereq, requiredTopics);
    }
}
```

Mengumpulkan semua topik yang dibutuhkan untuk mempelajari `topicId`, termasuk topik target itu sendiri dan semua prasyaratnya secara rekursif (prasyarat dari prasyarat, dst.). Hasilnya disimpan dalam `requiredTopics`. Pengecekan `requiredTopics.contains(topicId)` di awal mencegah rekursi tak terbatas.

---

**Method `sortForTopic(String targetId)` — Learning Path untuk Topik Tertentu**

```java
public List<String> sortForTopic(String targetId) {
    // 1. Kumpulkan hanya topik yang relevan (prasyarat dari targetId)
    Set<String> requiredTopics = new LinkedHashSet<>();
    collectPrerequisites(targetId, requiredTopics);

    // 2. Hitung in-degree HANYA untuk subset ini
    Map<String, Integer> inDegree = new LinkedHashMap<>();
    for (String id : requiredTopics) { inDegree.put(id, 0); }
    for (String id : requiredTopics) {
        for (String dependent : graph.getDependents(id)) {
            if (requiredTopics.contains(dependent)) { // hanya hitung edge internal
                inDegree.put(dependent, inDegree.get(dependent) + 1);
            }
        }
    }

    // 3. Jalankan Kahn's Algorithm pada subset
    ...
}
```

Versi terfokus dari Topological Sort — menghasilkan learning path **hanya** untuk topik yang dibutuhkan menuju `targetId`, bukan seluruh 25 topik di dataset. Lebih praktis bagi pengguna yang hanya ingin tahu jalur belajar untuk satu tujuan tertentu. Prosesnya identik dengan `sort()`, hanya saja bekerja pada subset graf (V' node dan E' edge), bukan seluruh graf.

Kompleksitas: **O(V' + E')** di mana V' dan E' adalah ukuran subset prasyarat.

---

## 8. `Main.java`

File: `src/Main.java`

Kelas utama yang menjadi titik masuk program. Mengelola tampilan menu, menerima input pengguna, dan mengorkestrasi semua fitur program.

```java
import graph.*;
import model.Topic;
import tree.Trie;
import java.util.*;

public class Main {

    static TopicGraph graph = new TopicGraph();
    static Trie trie = new Trie();
    static DFSTraversal dfs = new DFSTraversal(graph);
    static CycleDetector cycleDetector = new CycleDetector(graph);
    static TopologicalSort topoSort = new TopologicalSort(graph);
    static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔══════════...╗");
        System.out.println("║  LIBRARY KNOWLEDGE NAVIGATOR  ║");
        System.out.println("╚══════════...╝");

        loadDataset();

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Pilih menu [1-9]: ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1": featureSearchByPrefix(); break;
                case "2": featureShowPrerequisites(); break;
                case "3": featureLearningOrder(); break;
                case "4": featureDetectCycle(); break;
                case "5": featureDisconnectedTopics(); break;
                case "6": featureInsertData(); break;
                case "7": featureUpdateOrDelete(); break;
                case "8": graph.displayGraph(); trie.displayAll(); break;
                case "9": running = false; break;
                default: System.out.println("Pilihan tidak valid.");
            }
        }
        scanner.close();
    }
    // ... (method-method fitur di bawah)
}
```

### Penjelasan per Blok

**Deklarasi Variabel Global (Static)**

```java
static TopicGraph graph = new TopicGraph();
static Trie trie = new Trie();
static DFSTraversal dfs = new DFSTraversal(graph);
static CycleDetector cycleDetector = new CycleDetector(graph);
static TopologicalSort topoSort = new TopologicalSort(graph);
static Scanner scanner = new Scanner(System.in);
```

Semua objek utama program dideklarasikan sebagai `static` agar bisa diakses dari semua method `static` di kelas ini tanpa perlu membuat instance `Main`. `graph` dan `trie` adalah dua struktur data utama. `dfs`, `cycleDetector`, dan `topoSort` adalah algoritma yang semuanya menerima referensi ke `graph` yang sama — perubahan pada `graph` otomatis terlihat oleh ketiganya.

---

**Method `main(String[] args)` — Loop Utama Program**

```java
public static void main(String[] args) {
    loadDataset();
    boolean running = true;
    while (running) {
        printMenu();
        String input = scanner.nextLine().trim();
        switch (input) {
            case "1": featureSearchByPrefix(); break;
            ...
            case "9": running = false; break;
        }
    }
    scanner.close();
}
```

Titik masuk program. Setelah dataset dimuat, program masuk ke loop utama yang terus berjalan sampai pengguna memilih menu 9 (Keluar). Setiap input dibaca sebagai string (bukan `int`) menggunakan `scanner.nextLine().trim()` untuk menghindari isu dengan newline character yang sering terjadi jika memakai `nextInt()`.

---

**Method `featureSearchByPrefix()` — Menu 1**

```java
static void featureSearchByPrefix() {
    System.out.print("Masukkan kata kunci / prefix: ");
    String prefix = scanner.nextLine().trim();
    if (prefix.isEmpty()) { ... return; }

    List<Topic> results = trie.searchByPrefix(prefix);

    if (!results.isEmpty()) {
        // Tampilkan hasil
        System.out.print("Lihat prasyarat salah satu topik? (masukkan nomor / 0 untuk skip): ");
        String choice = scanner.nextLine().trim();
        try {
            int idx = Integer.parseInt(choice) - 1;
            if (idx >= 0 && idx < results.size()) {
                dfs.showAllPrerequisites(results.get(idx).getId());
            }
        } catch (NumberFormatException ignored) {}
    }
}
```

Mengimplementasikan fitur pencarian berdasarkan prefix menggunakan Trie. Setelah hasil ditampilkan, pengguna diberi opsi untuk langsung melihat prasyarat salah satu topik hasil pencarian. Input nomor yang tidak valid (bukan angka) ditangkap dengan `try-catch NumberFormatException` dan diabaikan, program tetap berjalan normal.

---

**Method `featureShowPrerequisites()` — Menu 2**

```java
static void featureShowPrerequisites() {
    // Tampilkan daftar semua topik dengan nomor urut
    List<String> ids = new ArrayList<>(graph.getTopics().keySet());
    for (String id : ids) { ... }

    System.out.print("Masukkan nomor topik: ");
    int idx = Integer.parseInt(input) - 1;
    if (idx >= 0 && idx < ids.size()) {
        String selectedId = ids.get(idx);
        dfs.showAllPrerequisites(selectedId);
        dfs.showAllDependents(selectedId);
    }
}
```

Menampilkan daftar semua topik bernomor agar pengguna bisa memilih. Setelah topik dipilih, ditampilkan dua informasi sekaligus: semua prasyaratnya (via `showAllPrerequisites`) dan semua topik yang bergantung padanya (via `showAllDependents`).

---

**Method `featureLearningOrder()` — Menu 3**

```java
static void featureLearningOrder() {
    // Cek siklus terlebih dahulu sebelum topological sort
    boolean hasCycle = cycleDetector.detectCycle();

    if (!hasCycle) {
        System.out.print("Masukkan ID topik tujuan (contoh: T20): ");
        String target = scanner.nextLine().trim().toUpperCase();
        topoSort.sortForTopic(target);
    }
}
```

Meminta tujuan topik dari pengguna lalu menghasilkan learning path menuju topik tersebut. Sebelum melakukan topological sort, program **selalu memeriksa siklus terlebih dahulu** menggunakan `cycleDetector.detectCycle()`. Ini penting karena topological sort tidak valid jika graf mengandung siklus.

---

**Method `featureDetectCycle()` dan `demoCycleDetection()` — Menu 4**

```java
static void featureDetectCycle() {
    cycleDetector.detectCycle();

    System.out.print("Jalankan demo siklus? (y/n): ");
    if (confirm.equalsIgnoreCase("y")) {
        demoCycleDetection();
    }
}

static void demoCycleDetection() {
    TopicGraph cycleGraph = new TopicGraph();
    Topic tx = new Topic("TX", "Topik X Demo", ...);
    Topic ty = new Topic("TY", "Topik Y Demo", ...);
    Topic tz = new Topic("TZ", "Topik Z Demo", ...);
    cycleGraph.addTopic(tx); cycleGraph.addTopic(ty); cycleGraph.addTopic(tz);
    // Buat siklus: X → Y → Z → X
    cycleGraph.addPrerequisite("Topik X Demo", "Topik Y Demo");
    cycleGraph.addPrerequisite("Topik Y Demo", "Topik Z Demo");
    cycleGraph.addPrerequisite("Topik Z Demo", "Topik X Demo"); // siklus!

    CycleDetector demoDetector = new CycleDetector(cycleGraph);
    demoDetector.detectCycle();
}
```

Pertama menjalankan deteksi siklus pada dataset asli (yang seharusnya tidak ada siklus). Kemudian menawarkan demo dengan membuat **graf terpisah** (`cycleGraph`) yang sengaja mengandung siklus `TX → TY → TZ → TX` untuk membuktikan kemampuan deteksi siklus. Graf demo ini dibuat secara isolasi — tidak mempengaruhi dataset asli.

---

**Method `featureDisconnectedTopics()` — Menu 5**

```java
static void featureDisconnectedTopics() {
    List<Topic> isolated = new ArrayList<>();
    List<Topic> noPrereqs = new ArrayList<>();
    List<Topic> noDependents = new ArrayList<>();

    for (String id : graph.getTopics().keySet()) {
        boolean hasPrereq = !graph.getPrerequisites(id).isEmpty();
        boolean hasDep = !graph.getDependents(id).isEmpty();

        if (!hasPrereq && !hasDep)  isolated.add(graph.getTopic(id));       // terisolir total
        else if (!hasPrereq)        noPrereqs.add(graph.getTopic(id));       // topik dasar
        else if (!hasDep)           noDependents.add(graph.getTopic(id));    // topik puncak
    }
    ...
}
```

Menganalisis konektivitas setiap topik dalam graf dan mengelompokkannya ke dalam tiga kategori:
- **Terisolir**: Tidak punya prasyarat DAN tidak ada yang membutuhkannya → topik mandiri.
- **Topik Dasar**: Tidak punya prasyarat (in-degree = 0) → titik awal belajar yang valid.
- **Topik Puncak**: Tidak ada yang bergantung padanya (out-degree = 0) → tujuan akhir belajar.

---

**Method `featureInsertData()` — Menu 6**

```java
static void featureInsertData() {
    String generatedId = generateNextTopicId();  // ID otomatis
    String id = scanner.nextLine().trim().toUpperCase();
    if (id.isEmpty()) id = generatedId;

    if (graph.containsTopic(id)) { ... return; }  // validasi duplikat ID
    // input title, category, description, duration, year
    // validasi durasi/tahun tidak negatif
    List<String> prerequisiteIds = readPrerequisiteIds(id, title);

    Topic newTopic = new Topic(id, title, category, description, duration, year);
    graph.addTopic(newTopic);
    trie.insert(newTopic);
    for (String prereqId : prerequisiteIds) {
        graph.addPrerequisite(prereqId, id);
    }
}
```

Mengimplementasikan fitur **Create (CRUD)**. Proses validasi dilakukan sebelum data disimpan:
- ID tidak boleh duplikat.
- Judul tidak boleh kosong atau duplikat.
- Durasi dan tahun tidak boleh negatif.
- Prasyarat harus benar-benar ada di sistem.
- Topik tidak boleh menjadi prasyarat dirinya sendiri.

Jika valid, topik baru ditambahkan ke **kedua struktur data** sekaligus: `graph.addTopic()` dan `trie.insert()`.

---

**Method `readPrerequisiteIds(String newId, String newTitle)`**

```java
static List<String> readPrerequisiteIds(String newId, String newTitle) {
    // Tampilkan daftar topik yang ada
    String input = scanner.nextLine().trim();
    if (input.isEmpty()) return new ArrayList<>(); // tidak ada prasyarat

    Set<String> uniqueIds = new LinkedHashSet<>();
    for (String rawPrereq : input.split(",")) {
        String prereqId = findTopicIdByTitleOrId(prereq.trim());
        if (prereqId == null) { ... return null; }         // prasyarat tidak ada
        if (prereqId.equalsIgnoreCase(newId)) { ... return null; } // self-loop
        uniqueIds.add(prereqId);
    }
    return new ArrayList<>(uniqueIds);
}
```

Helper untuk membaca daftar prasyarat dari input pengguna. Mendukung input ganda dipisah koma (misalnya `T06,T07` atau `Array dan String,Fungsi dan Prosedur`). Menggunakan `LinkedHashSet` untuk menghilangkan duplikat secara otomatis. Mengembalikan `null` jika ada prasyarat yang tidak valid (sehingga insert dibatalkan di pemanggil).

---

**Method `featureUpdateOrDelete()` dan sub-feature-nya — Menu 7**

```java
static void featureUpdateOrDelete() {
    // Sub-menu loop
    switch (subChoice) {
        case "1": subFeatureUpdateTopic(); break;
        case "2": subFeatureDeleteTopic(); break;
        case "3": subFeatureDeletePrerequisite(); break;
        case "4": inSubMenu = false; break;
    }
}
```

Menyediakan 3 operasi manajemen data (Update/Delete dalam CRUD):

**`subFeatureUpdateTopic()`**: Membaca data topik yang akan diubah, menampilkan data saat ini, lalu meminta input baru (tekan Enter untuk melewati field yang tidak ingin diubah). Jika judul berubah, entry lama di Trie dihapus dulu (`trie.delete`) kemudian judul baru diinsert ulang (`trie.insert`). Relasi graf tidak perlu diubah karena tetap menggunakan ID yang sama.

**`subFeatureDeleteTopic()`**: Meminta konfirmasi (`y/n`) sebelum menghapus. Jika dikonfirmasi, hapus dari Trie (`trie.delete`) dan hapus dari graf beserta semua relasi terkaitnya (`graph.removeTopic`).

**`subFeatureDeletePrerequisite()`**: Menghapus satu relasi edge prasyarat spesifik antara dua topik tanpa menghapus topik itu sendiri.

---

**Method `generateNextTopicId()`**

```java
static String generateNextTopicId() {
    int max = 0;
    for (String id : graph.getTopics().keySet()) {
        if (id.matches("T\\d+")) {
            max = Math.max(max, Integer.parseInt(id.substring(1)));
        }
    }
    return String.format("T%02d", max + 1);
}
```

Menghasilkan ID otomatis berikutnya dengan mencari nomor tertinggi dari ID yang sudah ada (format `T` diikuti angka). Jika ID tertinggi adalah `T25`, method ini menghasilkan `T26`. `String.format("T%02d", ...)` memastikan format dua digit (misalnya `T01` bukan `T1`).

---

**Method `loadDataset()`**

```java
static void loadDataset() {
    Topic[] topics = {
        new Topic("T01", "Pengantar Pemrograman", "Dasar", "...", 4, 2008),
        new Topic("T02", "Variabel dan Tipe Data", "Dasar", "...", 6, 2009),
        // ... 25 topik total
    };
    for (Topic t : topics) {
        graph.addTopic(t);
        trie.insert(t);
    }

    String[][] edges = {
        { "Pengantar Pemrograman", "Variabel dan Tipe Data" }, // T01 → T02
        { "Pengantar Pemrograman", "Operator dan Ekspresi" },  // T01 → T03
        // ... 41 edge total
    };
    for (String[] edge : edges) {
        graph.addPrerequisite(edge[0], edge[1]);
    }

    System.out.printf("Dataset dimuat: %d topik, %d relasi prasyarat%n", ...);
}
```

Memuat dataset secara **hardcoded in-memory** (tidak membaca file CSV saat startup). Dataset berisi:
- **25 node (topik)**: dari `T01` Pengantar Pemrograman hingga `T25` Design Patterns.
- **41 edge (relasi prasyarat)**: dikelompokkan per kategori (Dasar, OOP, Algoritma, Struktur Data, Advanced).

Setiap topik dimasukkan ke **kedua struktur data** sekaligus (`graph.addTopic` dan `trie.insert`). Edge menggunakan nama judul (bukan ID) untuk keterbacaan; konversi ke ID dilakukan otomatis oleh `addPrerequisite` via `findIdByTitleOrId`.

Data bersifat *in-memory* secara sengaja: perubahan saat runtime (insert/update/delete) tidak disimpan permanen, sehingga program bisa di-restart ke kondisi bersih kapan saja. Ini berguna untuk demo presentasi.

---

## Ringkasan Kompleksitas Waktu

| Operasi | Struktur / Algoritma | Kompleksitas |
|---|---|---|
| Insert topik ke Trie | Trie | O(L) |
| Search prefix di Trie | Trie + DFS collect | O(L + K) |
| Search exact di Trie | Trie | O(L) |
| Delete dari Trie | Trie rekursif | O(L) |
| Tambah topik ke Graf | HashMap | O(1) |
| Tambah edge prasyarat | ArrayList | O(1) |
| DFS prasyarat / dependen | Graf + DFS | O(V + E) |
| Deteksi Siklus | DFS 3-Warna | O(V + E) |
| Topological Sort seluruh graf | Kahn's Algorithm | O(V + E) |
| Topological Sort subset (sortForTopic) | Kahn's pada subset | O(V' + E') |
| Hitung in-degree | Iterasi semua edge | O(V + E) |

Keterangan: V = jumlah node/topik, E = jumlah edge/relasi, L = panjang string/prefix, K = jumlah hasil pencarian, V'/E' = ukuran subset prasyarat target.
