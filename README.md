# Library Knowledge Navigator

Final Project Struktur Data - aplikasi console berbasis Java untuk membantu pengguna mencari topik pembelajaran dan menentukan urutan belajar berdasarkan relasi prasyarat antar materi.

Project ini menggabungkan dua struktur data utama:

- **Trie / Prefix Tree** untuk pencarian topik berdasarkan prefix.
- **Directed Graph / Adjacency List** untuk menyimpan hubungan prasyarat antar topik.

Algoritma yang digunakan:

- **DFS Traversal** untuk menelusuri prasyarat langsung dan tidak langsung.
- **Topological Sort / Kahn's Algorithm** untuk membuat rekomendasi urutan belajar.
- **Cycle Detection DFS 3-Warna** untuk memastikan graph prasyarat tidak memiliki siklus.

## Daftar Isi

- [Deskripsi Masalah](#deskripsi-masalah)
- [Tujuan Project](#tujuan-project)
- [Fitur Program](#fitur-program)
- [Struktur Folder](#struktur-folder)
- [Dataset](#dataset)
- [Struktur Data yang Digunakan](#struktur-data-yang-digunakan)
- [Algoritma yang Digunakan](#algoritma-yang-digunakan)
- [Cara Menjalankan Program](#cara-menjalankan-program)
- [Contoh Output Program](#contoh-output-program)
- [Penjelasan File Source Code](#penjelasan-file-source-code)
- [Analisis Kompleksitas](#analisis-kompleksitas)
- [Catatan Fitur Insert](#catatan-fitur-insert)
- [Troubleshooting](#troubleshooting)
- [Kesimpulan](#kesimpulan)

## Deskripsi Masalah

Banyaknya materi pembelajaran membuat pengguna sulit menentukan urutan belajar yang benar. Beberapa topik tidak dapat dipelajari secara sembarangan karena membutuhkan pemahaman topik lain terlebih dahulu. Contohnya, topik `Struktur Data - Graph` membutuhkan pemahaman tentang `Struktur Data - Tree` dan `Struktur Data - Stack & Queue`.

Selain itu, pencarian topik secara manual menjadi tidak efisien ketika jumlah data bertambah. Pengguna harus membaca daftar topik satu per satu untuk menemukan materi yang dibutuhkan.

Project ini menyelesaikan dua masalah tersebut:

1. Mencari topik secara cepat menggunakan prefix.
2. Menentukan urutan belajar berdasarkan relasi prasyarat antar topik.

## Tujuan Project

Tujuan pembuatan aplikasi **Library Knowledge Navigator**:

1. Membantu pengguna menemukan topik pembelajaran secara cepat berdasarkan kata kunci atau prefix.
2. Menampilkan seluruh prasyarat dari sebuah topik, baik langsung maupun tidak langsung.
3. Memberikan rekomendasi urutan belajar yang valid berdasarkan hubungan prasyarat.
4. Mendeteksi siklus pada graph prasyarat agar jalur belajar tetap valid.
5. Menampilkan topik yang tidak terhubung, topik dasar, dan topik puncak.
6. Menyediakan fitur insert data topik baru saat program berjalan.

## Fitur Program

Program memiliki 8 menu utama:

| Menu | Fitur | Penjelasan |
| --- | --- | --- |
| 1 | Cari topik berdasarkan prefix | Mencari topik menggunakan Trie berdasarkan awalan judul. |
| 2 | Tampilkan prasyarat sebuah topik | Menampilkan prasyarat dan dependen dari topik yang dipilih. |
| 3 | Rekomendasikan urutan belajar | Menampilkan learning path menuju topik tujuan menggunakan Topological Sort. |
| 4 | Deteksi siklus prasyarat | Mengecek apakah graph memiliki siklus menggunakan DFS 3-Warna. |
| 5 | Tampilkan topik tidak terhubung | Menampilkan topik terisolir, topik dasar, dan topik puncak. |
| 6 | Insert data topik baru | Menambahkan topik baru ke Graph dan Trie saat runtime. |
| 7 | Tampilkan semua data | Menampilkan adjacency list graph dan semua data dalam Trie. |
| 8 | Keluar | Mengakhiri program. |

## Struktur Folder

Struktur project:

```text
FP-Strukdat-main/
├── README.md
├── data/
│   └── dataset.csv
├── src/
│   ├── Main.java
│   ├── graph/
│   │   ├── CycleDetector.java
│   │   ├── DFSTraversal.java
│   │   ├── TopicGraph.java
│   │   └── TopologicalSort.java
│   ├── model/
│   │   └── Topic.java
│   └── tree/
│       ├── Trie.java
│       └── TrieNode.java
└── bin/
    ├── Main.class
    ├── graph/
    ├── model/
    └── tree/
```

Keterangan:

- `src/` berisi source code Java.
- `bin/` berisi hasil compile `.class`.
- `data/dataset.csv` berisi dataset topik dalam format CSV.
- `README.md` berisi dokumentasi project.

## Dataset

Dataset berisi topik pembelajaran di bidang ilmu komputer dan pemrograman.

Ringkasan dataset:

| Properti | Nilai |
| --- | --- |
| Jumlah node | 25 topik |
| Jumlah edge | 41 relasi prasyarat |
| Format data | CSV dan hardcoded pada `loadDataset()` |
| Tipe graph | Directed Acyclic Graph (DAG) |
| Representasi graph | Adjacency List |

Kolom dataset:

| Kolom | Keterangan |
| --- | --- |
| `id` | ID unik topik, contoh `T01`. |
| `title` | Judul topik. |
| `category` | Kategori topik, seperti `Dasar`, `Menengah`, atau `Lanjut`. |
| `description` | Deskripsi singkat topik. |
| `duration` | Estimasi durasi belajar dalam jam. |
| `publishYear` | Tahun materi. |
| `prerequisites` | Daftar prasyarat topik. |

Contoh data:

```csv
id,title,category,description,duration,publishYear,prerequisites
T01,Pengantar Pemrograman,Dasar,Konsep dasar pemrograman,4,2008,
T02,Variabel dan Tipe Data,Dasar,"Int, float, string, boolean",6,2009,Pengantar Pemrograman
T20,Struktur Data - Graph,Lanjut,"Adjacency list/matrix, DFS, BFS",30,2012,"Struktur Data - Tree,Struktur Data - Stack & Queue"
```

Daftar topik utama:

| ID | Judul | Kategori | Durasi | Tahun | Prasyarat |
| --- | --- | --- | ---: | ---: | --- |
| T01 | Pengantar Pemrograman | Dasar | 4 | 2008 | - |
| T02 | Variabel dan Tipe Data | Dasar | 6 | 2009 | T01 |
| T03 | Operator dan Ekspresi | Dasar | 6 | 2009 | T01, T02 |
| T04 | Struktur Kontrol | Dasar | 8 | 2010 | T02, T03 |
| T05 | Perulangan (Loop) | Dasar | 8 | 2010 | T04 |
| T06 | Array dan String | Menengah | 12 | 2010 | T02, T05 |
| T07 | Fungsi dan Prosedur | Menengah | 10 | 2009 | T04 |
| T08 | Rekursi | Menengah | 12 | 2008 | T07, T06 |
| T09 | Pointer dan Memori | Menengah | 14 | 2007 | T02, T07 |
| T10 | OOP - Dasar | Menengah | 16 | 2011 | T07 |
| T11 | OOP - Inheritance | Menengah | 14 | 2011 | T10 |
| T12 | OOP - Polymorphism | Lanjut | 20 | 2012 | T11 |
| T13 | OOP - Interface & Abstract | Lanjut | 18 | 2012 | T11, T12 |
| T14 | Algoritma Sorting | Menengah | 16 | 2008 | T06, T07 |
| T15 | Algoritma Searching | Menengah | 12 | 2009 | T06 |
| T16 | Kompleksitas Algoritma | Lanjut | 24 | 2010 | T14, T15 |
| T17 | Struktur Data - Stack & Queue | Menengah | 15 | 2008 | T09, T07 |
| T18 | Struktur Data - Linked List | Menengah | 18 | 2009 | T09, T17 |
| T19 | Struktur Data - Tree | Lanjut | 25 | 2011 | T18, T08 |
| T20 | Struktur Data - Graph | Lanjut | 30 | 2012 | T19, T17 |
| T21 | Dynamic Programming | Lanjut | 28 | 2010 | T08, T16 |
| T22 | Greedy Algorithm | Lanjut | 22 | 2011 | T16, T14 |
| T23 | Divide and Conquer | Lanjut | 20 | 2009 | T08, T14 |
| T24 | Pemrograman Fungsional | Lanjut | 20 | 2012 | T10, T07 |
| T25 | Design Patterns | Lanjut | 24 | 2013 | T13, T10 |

## Struktur Data yang Digunakan

### 1. Directed Graph

Graph digunakan untuk merepresentasikan relasi prasyarat antar topik.

Makna edge:

```text
A -> B
```

Artinya: **A adalah prasyarat dari B**. B sebaiknya dipelajari setelah A.

Implementasi graph ada pada:

```text
src/graph/TopicGraph.java
```

Struktur internal:

```java
private Map<String, List<String>> adjacencyList;
private Map<String, List<String>> reverseList;
private Map<String, Topic> topics;
```

Keterangan:

- `adjacencyList`: menyimpan daftar topik yang bergantung pada sebuah topik.
- `reverseList`: menyimpan daftar prasyarat dari sebuah topik.
- `topics`: menyimpan data lengkap objek `Topic`.

Contoh:

```text
Fungsi dan Prosedur -> [Rekursi, Pointer dan Memori, OOP - Dasar, ...]
Struktur Data - Tree -> [Struktur Data - Graph]
```

### 2. Trie / Prefix Tree

Trie digunakan untuk pencarian topik berdasarkan prefix judul.

Implementasi ada pada:

```text
src/tree/Trie.java
src/tree/TrieNode.java
```

Struktur node Trie:

```java
Map<Character, TrieNode> children;
boolean isEndOfWord;
String topicId;
```

Cara kerja:

1. Judul topik dikonversi ke lowercase.
2. Setiap karakter judul dimasukkan sebagai node.
3. Node terakhir menyimpan `topicId`.
4. Saat pencarian prefix, program menelusuri karakter prefix lalu mengumpulkan semua topik di bawah node tersebut.

Contoh:

```text
Input prefix: OOP
Output:
- OOP - Polymorphism
- OOP - Dasar
- OOP - Interface & Abstract
- OOP - Inheritance
```

## Algoritma yang Digunakan

### 1. DFS Traversal

File:

```text
src/graph/DFSTraversal.java
```

Kegunaan:

- Menampilkan semua prasyarat dari sebuah topik.
- Menampilkan semua topik yang bergantung pada topik tertentu.
- Menelusuri relasi langsung dan tidak langsung.

Contoh konsep:

```text
Target: Rekursi
Prasyarat langsung: Fungsi dan Prosedur, Array dan String
Prasyarat tidak langsung: Struktur Kontrol, Variabel dan Tipe Data, Pengantar Pemrograman, ...
```

Kompleksitas:

```text
O(V + E)
```

### 2. Topological Sort / Kahn's Algorithm

File:

```text
src/graph/TopologicalSort.java
```

Kegunaan:

- Menghasilkan urutan belajar yang valid.
- Memastikan prasyarat muncul sebelum topik yang bergantung padanya.

Konsep:

1. Hitung in-degree setiap node.
2. Masukkan node dengan in-degree 0 ke queue.
3. Ambil node dari queue.
4. Kurangi in-degree node dependen.
5. Jika in-degree node dependen menjadi 0, masukkan ke queue.
6. Ulangi sampai queue kosong.

Kompleksitas:

```text
O(V + E)
```

### 3. Cycle Detection DFS 3-Warna

File:

```text
src/graph/CycleDetector.java
```

Kegunaan:

- Mendeteksi siklus dalam graph prasyarat.
- Mencegah Topological Sort dilakukan pada graph yang tidak valid.

State warna:

| Warna | Nilai | Keterangan |
| --- | ---: | --- |
| WHITE | 0 | Node belum dikunjungi. |
| GRAY | 1 | Node sedang diproses dalam call stack. |
| BLACK | 2 | Node selesai diproses. |

Jika DFS menemukan node `GRAY`, berarti terdapat back-edge dan graph memiliki siklus.

Kompleksitas:

```text
O(V + E)
```

### 4. Trie Insert dan Prefix Search

File:

```text
src/tree/Trie.java
```

Kegunaan:

- `insert(Topic topic)` untuk memasukkan judul topik ke Trie.
- `searchByPrefix(String prefix)` untuk mencari semua topik yang judulnya diawali prefix tertentu.
- `searchExact(String title)` untuk mencari judul yang sama persis.
- `delete(String title)` untuk menghapus topik dari Trie.

Kompleksitas:

| Operasi | Kompleksitas |
| --- | --- |
| Insert | O(L) |
| Search prefix | O(L + K) |
| Search exact | O(L) |
| Delete | O(L) |

Keterangan:

- `L` = panjang string/prefix.
- `K` = jumlah hasil yang dikumpulkan setelah prefix ditemukan.

## Cara Menjalankan Program

### Prasyarat

Pastikan Java sudah terinstall.

Cek versi Java:

```bash
java -version
javac -version
```

Project ini dapat dijalankan dengan Java JDK yang memiliki `javac`.

### 1. Masuk ke folder project

```bash
cd FP-Strukdat-main
```

### 2. Compile source code

macOS / Linux:

```bash
javac -d bin src/model/Topic.java src/tree/TrieNode.java src/tree/Trie.java src/graph/TopicGraph.java src/graph/DFSTraversal.java src/graph/CycleDetector.java src/graph/TopologicalSort.java src/Main.java
```

Windows PowerShell:

```powershell
javac -d bin src/model/Topic.java src/tree/TrieNode.java src/tree/Trie.java src/graph/TopicGraph.java src/graph/DFSTraversal.java src/graph/CycleDetector.java src/graph/TopologicalSort.java src/Main.java
```

Jika folder `bin` belum ada, buat dulu:

```bash
mkdir bin
```

### 3. Jalankan program

macOS / Linux:

```bash
java -cp bin Main
```

Windows PowerShell:

```powershell
java -cp bin Main
```

### 4. Jalankan dari file `.class` yang sudah tersedia

Jika folder `bin` sudah berisi file `.class`, program dapat langsung dijalankan:

```bash
java -cp bin Main
```

## Contoh Output Program

### Output awal

```text
╔══════════════════════════════════════════════════════════╗
║          LIBRARY KNOWLEDGE NAVIGATOR                     ║
║     Sistem Pencarian & Rekomendasi Urutan Belajar        ║
╚══════════════════════════════════════════════════════════╝

Memuat dataset...
Dataset dimuat: 25 topik, 41 relasi prasyarat

┌──────────────────────────────────────────┐
│              MENU UTAMA                  │
├──────────────────────────────────────────┤
│  1. Cari topik berdasarkan prefix        │
│  2. Tampilkan prasyarat sebuah topik     │
│  3. Rekomendasikan urutan belajar        │
│  4. Deteksi siklus prasyarat             │
│  5. Tampilkan topik tidak terhubung      │
│  6. Insert data topik baru               │
│  7. Tampilkan semua data                 │
│  8. Keluar                               │
└──────────────────────────────────────────┘
Pilih menu [1-8]:
```

### Contoh menu 1 - cari topik berdasarkan prefix

Input:

```text
1
OOP
0
8
```

Output:

```text
=== PENCARIAN TOPIK BERDASARKAN PREFIX ===
Masukkan kata kunci / prefix:

Hasil pencarian untuk "OOP":
  Ditemukan 4 topik:
  1. [T12] OOP - Polymorphism (Lanjut) - 20 Jam - Terbit: 2012
  2. [T10] OOP - Dasar (Menengah) - 16 Jam - Terbit: 2011
  3. [T13] OOP - Interface & Abstract (Lanjut) - 18 Jam - Terbit: 2012
  4. [T11] OOP - Inheritance (Menengah) - 14 Jam - Terbit: 2011
```

### Contoh menu 3 - rekomendasi urutan belajar

Input:

```text
3
T20
8
```

Output:

```text
=== REKOMENDASI URUTAN BELAJAR ===
Memeriksa siklus prasyarat...

Tidak ditemukan siklus. Graph adalah DAG (Directed Acyclic Graph).
   Topological Sort dapat dilakukan dengan aman.

Masukkan ID topik tujuan (contoh: T20):
========================================
LEARNING PATH MENUJU Struktur Data - Graph
========================================

 1. Pengantar Pemrograman (T01)
 2. Variabel dan Tipe Data (T02)
 3. Operator dan Ekspresi (T03)
 4. Struktur Kontrol (T04)
 5. Perulangan (Loop) (T05)
 6. Fungsi dan Prosedur (T07)
 7. Array dan String (T06)
 8. Pointer dan Memori (T09)
 9. Rekursi (T08)
10. Struktur Data - Stack & Queue (T17)
11. Struktur Data - Linked List (T18)
12. Struktur Data - Tree (T19)
13. Struktur Data - Graph (T20)
```

### Contoh menu 6 - insert data topik baru

Input contoh:

```text
6

Basis Data
Menengah
Konsep database relasional
12
2024
T06
8
```

Penjelasan input:

- Pilih menu `6`.
- Tekan Enter pada input ID agar program memakai ID otomatis `T26`.
- Judul: `Basis Data`.
- Kategori: `Menengah`.
- Deskripsi: `Konsep database relasional`.
- Durasi: `12`.
- Tahun materi: `2024`.
- Prasyarat: `T06`.
- Pilih `8` untuk keluar.

Output:

```text
=== INSERT DATA TOPIK BARU ===
ID otomatis berikutnya: T26
Masukkan ID topik (Enter untuk pakai ID otomatis):
Masukkan judul topik:
Masukkan kategori:
Masukkan deskripsi:
Masukkan durasi belajar (jam):
Masukkan tahun materi:

Daftar Topik yang Bisa Dijadikan Prasyarat:
  T01 - Pengantar Pemrograman
  T02 - Variabel dan Tipe Data
  ...
  T25 - Design Patterns

Masukkan prasyarat dipisah koma.
Bisa memakai ID atau judul topik (kosongkan jika tidak ada):

Topik "Basis Data" berhasil ditambahkan.
Total sekarang: 26 topik, 42 relasi prasyarat.
```

## Penjelasan File Source Code

### `src/Main.java`

File utama program.

Tanggung jawab:

- Menampilkan header aplikasi.
- Memanggil `loadDataset()`.
- Menampilkan menu.
- Menghubungkan pilihan menu dengan fitur.
- Menyediakan fitur insert data.

Fitur utama di file ini:

- `featureSearchByPrefix()`
- `featureShowPrerequisites()`
- `featureLearningOrder()`
- `featureDetectCycle()`
- `featureDisconnectedTopics()`
- `featureInsertData()`
- `loadDataset()`

### `src/model/Topic.java`

Model data untuk satu topik pembelajaran.

Atribut:

```java
private String id;
private String title;
private String category;
private String description;
private int duration;
private int publishYear;
```

Output `toString()`:

```text
[T10] OOP - Dasar (Menengah) - 16 Jam - Terbit: 2011
```

### `src/graph/TopicGraph.java`

Class graph utama untuk menyimpan data topik dan relasi prasyarat.

Method penting:

| Method | Fungsi |
| --- | --- |
| `addTopic(Topic topic)` | Menambahkan node/topik baru. |
| `addPrerequisite(String prerequisite, String dependent)` | Menambahkan edge prasyarat. |
| `getDependents(String topicId)` | Mengambil topik yang bergantung pada topik tertentu. |
| `getPrerequisites(String topicId)` | Mengambil prasyarat dari topik tertentu. |
| `computeInDegrees()` | Menghitung jumlah prasyarat setiap node. |
| `displayGraph()` | Menampilkan adjacency list. |

### `src/graph/DFSTraversal.java`

Class untuk traversal graph menggunakan DFS.

Method penting:

| Method | Fungsi |
| --- | --- |
| `showAllPrerequisites(String topicId)` | Menampilkan semua prasyarat topik. |
| `showAllDependents(String topicId)` | Menampilkan semua topik yang membutuhkan topik tersebut. |
| `reachableFrom(String startId)` | Mengembalikan semua node yang dapat dicapai dari node awal. |

### `src/graph/TopologicalSort.java`

Class untuk membuat urutan belajar menggunakan Kahn's Algorithm.

Method penting:

| Method | Fungsi |
| --- | --- |
| `sort()` | Topological sort untuk seluruh graph. |
| `sortForTopic(String targetId)` | Learning path khusus menuju topik target. |

### `src/graph/CycleDetector.java`

Class untuk mendeteksi siklus dengan DFS 3-Warna.

Method penting:

| Method | Fungsi |
| --- | --- |
| `detectCycle()` | Mengecek apakah graph memiliki siklus. |
| `dfsDetect(...)` | DFS rekursif untuk mencari back-edge. |

### `src/tree/Trie.java`

Class untuk pencarian prefix.

Method penting:

| Method | Fungsi |
| --- | --- |
| `insert(Topic topic)` | Memasukkan judul topik ke Trie. |
| `searchByPrefix(String prefix)` | Mencari topik berdasarkan prefix. |
| `searchExact(String title)` | Mencari topik dengan judul tepat. |
| `delete(String title)` | Menghapus judul dari Trie. |
| `displayAll()` | Menampilkan semua topik dalam Trie. |

### `src/tree/TrieNode.java`

Node untuk struktur Trie.

Isi node:

```java
Map<Character, TrieNode> children;
boolean isEndOfWord;
String topicId;
```

## Analisis Kompleksitas

| Operasi | Struktur / Algoritma | Kompleksitas |
| --- | --- | --- |
| Insert topik ke Graph | HashMap + adjacency list | O(1) |
| Insert topik ke Trie | Trie | O(L) |
| Search prefix | Trie + DFS collect | O(L + K) |
| Search exact | Trie | O(L) |
| Delete dari Trie | Trie rekursif | O(L) |
| DFS prasyarat | Graph + DFS | O(V + E) |
| Cycle Detection | DFS 3-Warna | O(V + E) |
| Topological Sort seluruh graph | Kahn's Algorithm | O(V + E) |
| Topological Sort target tertentu | Kahn's pada subset | O(V' + E') |
| Hitung in-degree | Iterasi semua edge | O(V + E) |

Keterangan:

- `V` = jumlah node/topik.
- `E` = jumlah edge/relasi prasyarat.
- `L` = panjang string/prefix.
- `K` = jumlah hasil pencarian.
- `V'` dan `E'` = node dan edge pada subset prasyarat target.

## Catatan Fitur Insert

Fitur insert pada menu 6 menambahkan data baru ke:

- `graph`, melalui `graph.addTopic(newTopic)`.
- `trie`, melalui `trie.insert(newTopic)`.
- edge prasyarat, melalui `graph.addPrerequisite(prereqId, id)`.

Validasi yang dilakukan:

- ID tidak boleh duplikat.
- Judul tidak boleh kosong.
- Judul tidak boleh duplikat.
- Durasi tidak boleh negatif.
- Tahun tidak boleh negatif.
- Prasyarat harus ditemukan berdasarkan ID atau judul.
- Topik baru tidak boleh menjadi prasyarat untuk dirinya sendiri.

Contoh penggunaan prasyarat:

```text
T06
```

atau:

```text
Array dan String
```

Untuk lebih dari satu prasyarat, pisahkan dengan koma:

```text
T06,T07
```

atau:

```text
Array dan String,Fungsi dan Prosedur
```

### Penting: data insert belum permanen

Pada versi source code ini, dataset program masih dimuat dari array hardcoded di method:

```java
loadDataset()
```

Artinya:

- Data yang ditambahkan lewat menu 6 hanya tersimpan selama program berjalan.
- Jika program ditutup lalu dijalankan ulang, data hasil insert akan hilang.
- File `data/dataset.csv` sudah tersedia sebagai representasi dataset, tetapi `Main.java` versi ini belum membaca CSV saat startup dan belum menyimpan insert ke CSV.

Jika ingin insert menjadi permanen, program perlu ditambah:

1. Loader CSV untuk membaca `data/dataset.csv`.
2. Writer CSV untuk menyimpan topik baru setelah insert.
3. Parser CSV yang aman untuk field yang mengandung koma, seperti deskripsi dan daftar prasyarat.

## Troubleshooting

### 1. `javac: command not found`

Java JDK belum terinstall atau belum masuk PATH.

Solusi:

- Install JDK.
- Cek dengan:

```bash
javac -version
java -version
```

### 2. `Error: Could not find or load main class Main`

Biasanya terjadi karena:

- Program belum dicompile.
- Menjalankan dari folder yang salah.
- Classpath salah.

Solusi:

```bash
cd FP-Strukdat-main
javac -d bin src/model/Topic.java src/tree/TrieNode.java src/tree/Trie.java src/graph/TopicGraph.java src/graph/DFSTraversal.java src/graph/CycleDetector.java src/graph/TopologicalSort.java src/Main.java
java -cp bin Main
```

### 3. `package graph does not exist`

Compile hanya `Main.java` tanpa menyertakan file package lain.

Solusi: compile semua file:

```bash
javac -d bin src/model/Topic.java src/tree/TrieNode.java src/tree/Trie.java src/graph/TopicGraph.java src/graph/DFSTraversal.java src/graph/CycleDetector.java src/graph/TopologicalSort.java src/Main.java
```

### 4. Input topik tidak ditemukan

Pastikan input sesuai:

- Untuk menu learning path, gunakan ID seperti `T20`.
- Untuk insert prasyarat, gunakan ID atau judul yang sudah ada.

Contoh benar:

```text
T06
```

atau:

```text
Array dan String
```

### 5. Insert berhasil tetapi hilang setelah restart

Itu sesuai behavior versi ini. Insert hanya runtime dan belum disimpan permanen ke CSV.

## What-If Analysis

### Jika jumlah node bertambah banyak

Dengan adjacency list, penggunaan memori tetap efisien untuk graph yang tidak terlalu padat. Kompleksitas DFS, cycle detection, dan topological sort tetap `O(V + E)`.

### Jika ada siklus prasyarat

Contoh siklus:

```text
A -> B -> C -> A
```

Graph menjadi tidak valid untuk topological sort. Program akan mendeteksi siklus menggunakan DFS 3-Warna dan memberi pesan bahwa topological sort tidak dapat dilakukan.

### Jika input prefix tidak ditemukan

Trie akan mengembalikan list kosong dan program menampilkan bahwa tidak ada topik yang cocok.

### Jika prasyarat insert salah

Program membatalkan insert dan menampilkan prasyarat yang tidak ditemukan.

## Kesimpulan

Library Knowledge Navigator menunjukkan bagaimana struktur data dapat digunakan untuk menyelesaikan masalah pembelajaran yang memiliki dependensi antar topik.

Kesimpulan utama:

1. Trie efektif untuk pencarian prefix karena pencarian tidak perlu membaca seluruh daftar topik satu per satu.
2. Directed Graph cocok untuk memodelkan hubungan prasyarat antar materi.
3. DFS mampu menelusuri prasyarat langsung dan tidak langsung.
4. Cycle Detection menjaga agar relasi prasyarat tidak membentuk siklus.
5. Topological Sort menghasilkan urutan belajar yang valid.
6. Kombinasi Trie dan Graph membuat program dapat melakukan pencarian cepat sekaligus navigasi relasi materi.

## Informasi Project

Judul project:

```text
Library Knowledge Navigator
```

Bahasa:

```text
Java
```

Jenis aplikasi:

```text
Console application
```

Struktur data utama:

```text
Trie, Directed Graph / Adjacency List
```

Algoritma utama:

```text
DFS, Cycle Detection DFS 3-Warna, Topological Sort Kahn's Algorithm
```
