# Pencatatan Penyaluran Dana Desa

Capstone Action Learning PJJ Java Fundamental 2026, **Topik A**.
Aplikasi desktop Java Swing + MySQL untuk mengelola data desa, mencatat penyaluran Dana Desa per tahap (I/II/III), menolak penyaluran yang melebihi sisa pagu, serta menyusun rekap per desa dan per kabupaten.

> **Semua data di repositori ini adalah data rekaan (dummy).** Kode wilayah memakai awalan `99` (bukan kode provinsi riil), dan nama kabupaten/desa fiktif.

## Teknologi
- JDK 25 LTS (Eclipse Temurin), Apache NetBeans 31, proyek **Java with Maven**
- MySQL 8.x, driver `com.mysql:mysql-connector-j` (dependency Maven)
- FlatLaf 3.7.2 (`com.formdev:flatlaf`) untuk tampilan Swing modern
- JUnit 5 untuk unit test

## Cara Menjalankan

1. **Siapkan database.** Jalankan script SQL (membuat database `dana_desa`, 3 tabel, dan data dummy):
   ```bash
   mysql -u root -p < sql/dana_desa.sql
   ```
   Bisa juga dibuka dan dijalankan di MySQL Workbench / phpMyAdmin.
2. **Atur koneksi.** Salin `db.properties.example` menjadi `db.properties` di folder proyek (sejajar `pom.xml`), lalu isi:
   ```properties
   db.url=jdbc:mysql://localhost:3306/dana_desa?serverTimezone=Asia/Jakarta&useSSL=false&allowPublicKeyRetrieval=true
   db.user=root
   db.password=password_anda
   ```
   File `db.properties` sudah masuk `.gitignore`, jadi tidak ikut ter-commit.
3. **Buka di NetBeans.** File → Open Project → pilih folder proyek, lalu **Run Project (F6)**. Bila muncul error `${packageClassName}`, atur main class di Properties → Run → Main Class → `id.pjj.danadesa.App`.
4. **Menjalankan unit test.** Klik kanan proyek → **Test** (atau `mvn test`).

Lokasi `db.properties` bisa diganti dengan `-Ddanadesa.config=/path/ke/file.properties`.

## Struktur Proyek

```
src/main/java/id/pjj/danadesa/
├── App.java                 # titik masuk; merangkai DAO → Service → UI
├── config/                  # DatabaseConfig (baca db.properties), ConnectionFactory
├── model/                   # Entitas, EntitasDasar, Kabupaten, Desa, Penyaluran, Tahap, Rekap*
├── dao/                     # Repository<T>, AbstractJdbcRepository<T>, *Dao
├── service/                 # DesaService, PenyaluranService (transaksi), RekapService, CsvExporter
│   └── aturan/              # AturanPenyaluran + 4 implementasi, ValidatorPenyaluran
├── exception/               # AplikasiException, ValidasiException, MelebihiSisaPaguException, ...
├── ui/                      # MainFrame (sidebar), DashboardPanel, DesaPanel, PenyaluranPanel, RekapPanel
│   └── komponen/            # Tema, Kartu, StatKartu, Grafik*, BilahPersen, NavItem, ListTableModel<T>, Tabel, Form, Pesan
└── util/                    # Format (rupiah/tanggal), InputParser
src/test/java/...            # 35 unit test (aturan bisnis, parser, rekap, CSV)
sql/dana_desa.sql            # DDL + data dummy (3 kabupaten, 15 desa, 26 penyaluran)
docs/RANCANGAN.md            # identifikasi masalah, diagram class, ERD, alternatif desain
docs/CATATAN_DEBUGGING.md    # 4 bug: gejala, penyebab, perbaikan
```

## Pemenuhan Fitur Wajib

| No | Ketentuan | Implementasi |
|----|-----------|--------------|
| 1 | OOP: ≥3 class domain terenkapsulasi, inheritance/interface, polimorfisme | `Kabupaten`, `Desa`, `Penyaluran` (field private + getter/setter) mewarisi `EntitasDasar` → `Entitas`. Polimorfisme: `List<AturanPenyaluran>` di `ValidatorPenyaluran`; `kolomCsv()` pada `RekapDesa`/`RekapKabupaten`/`RekapTotal` di `CsvExporter` |
| 2 | Pemisahan lapisan | paket `model`, `dao`, `ui` (+ `service`, `config`) |
| 3 | ≥2 tabel berelasi; CRUD 1 entitas, tambah entitas ke-2 | `kabupaten` ← `desa` ← `penyaluran` (FK). CRUD penuh **Desa**; tambah + hapus **Penyaluran** |
| 4 | Semua query PreparedStatement | `AbstractJdbcRepository` – semua nilai lewat parameter `?` |
| 5 | Kredensial di file konfigurasi | `DatabaseConfig` membaca `db.properties`; hanya `db.properties.example` yang di-commit |
| 6 | Collections untuk rekap | `RekapService`: `Map<Integer, Map<Tahap, BigDecimal>>`, `EnumMap`, `TreeMap`, `List` |
| 7 | Validasi & exception handling, ≥1 custom exception | `InputParser`, `ValidatorDesa`, `MelebihiSisaPaguException`, dll.; semua error tampil sebagai dialog tanpa aplikasi berhenti |
| 8 | Antarmuka | GUI Swing: sidebar menu dengan 4 halaman (Dashboard, Data Desa, Penyaluran, Rekap) |

## Fitur Pengembangan (Nilai Tambah)

| Fitur | Implementasi |
|-------|--------------|
| GUI Swing | `ui/` – tema navy & silver (FlatLaf), sidebar, Dashboard dengan 3 grafik Java2D buatan sendiri (kolom bertumpuk per bulan, batang pagu vs tersalur per kabupaten, donat status desa), pencarian desa, bilah persentase |
| Ekspor rekap ke CSV | Tombol **Ekspor CSV...** → `rekap_desa_*.csv` & `rekap_kabupaten_*.csv` (pemisah `;`, UTF-8 BOM, langsung rapi di Excel) |
| Stream API & lambda | `RekapService` (`groupingBy`, `reducing`, rekap per bulan dengan `TreeMap<YearMonth, …>`), `ListTableModel` (kolom didefinisikan dengan lambda) |
| Thread terpisah | `SwingWorker` untuk memuat rekap dan ekspor CSV (`RekapPanel`), dengan progress bar |
| Class generik | `Repository<T extends Entitas>`, `AbstractJdbcRepository<T>`, `ListTableModel<T>`, `RowMapper<T>` |
| Transaksi commit/rollback | `PenyaluranService.catat()` & `hapus()`: `setAutoCommit(false)` → `SELECT … FOR UPDATE` → validasi → simpan → `commit`, atau `rollback` bila gagal |
| Unit test JUnit | 35 test: aturan penyaluran, validasi desa, parser input, rekap, CSV |
| Riwayat commit Git semua anggota | lihat saran pembagian peran di bawah |

## Saran Pembagian Peran & Alur Git

| Peran | Bagian yang dikuasai & di-commit |
|-------|----------------------------------|
| Anggota 1 (Ketua) | `model/`, `sql/`, laporan & video |
| Anggota 2 | `config/`, `dao/` (koneksi, PreparedStatement, transaksi) |
| Anggota 3 | `service/` + `service/aturan/` + unit test |
| Anggota 4 | `ui/` (Swing, SwingWorker, ekspor CSV) |
| Anggota 5 | `docs/` (rancangan, catatan debugging), pengujian manual, screenshot |

Supaya riwayat commit mencerminkan kontribusi: setiap anggota bekerja di branch sendiri (`git checkout -b fitur-dao`), commit bagian masing-masing, lalu merge/PR ke `main`.

## Skenario Video Demo (≤ 5 menit)
1. Tunjukkan `db.properties.example` dan bahwa `db.properties` tidak ada di repo (30 detik).
2. Halaman **Data Desa**: tambah desa baru → ubah pagunya → hapus. Lalu coba isi pagu `abc` untuk memunculkan validasi (1,5 menit).
3. Halaman **Penyaluran**: pilih *9901012002 – Sindangjaya*, isi Tahap III senilai `300.000.000`, lalu tunjukkan bahwa ditolak karena melebihi sisa pagu. Kemudian catat `197.100.000` dan lihat berhasil (1,5 menit).
4. Halaman **Dashboard** lalu **Rekap**: tunjukkan rekap per desa/kabupaten dan total, urutkan kolom %, lalu **Ekspor CSV** dan buka hasilnya di Excel (1 menit).
5. Jalankan unit test di NetBeans dan tunjukkan semuanya lulus (30 detik).
