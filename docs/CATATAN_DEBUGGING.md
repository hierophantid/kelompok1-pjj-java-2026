# Catatan Debugging

> Bahan untuk Laporan bagian 6. Tiga bug di bawah benar-benar ditemukan saat aplikasi ini dibangun.
> Tambahkan atau ganti dengan bug yang kelompok temukan sendiri saat menjalankan atau memodifikasi kode,
> karena setiap anggota harus bisa menjelaskannya.

## Bug 1: Kolom persentase terurut sebagai teks

- **Gejala:** di tab Rekap, klik header kolom `%` untuk mengurutkan. Urutannya salah: `100,00%` muncul sebelum `40,00%` karena dibandingkan huruf demi huruf.
- **Cara ditemukan:** review kode `Tabel.java` sebelum aplikasi dijalankan.
- **Penyebab:** kolom persen diberi tipe penanda `Tabel.Persen` supaya bisa punya renderer sendiri. Kelas ini bukan `Comparable`, sehingga `TableRowSorter` membandingkan hasil `toString()` (teks), bukan angka.
- **Perbaikan:** membuat `Tabel.sorter(model)` yang meng-override `getComparator()` (pakai `Comparator.naturalOrder()` untuk `BigDecimal`) dan `useToString()` (false untuk kolom Persen).

## Bug 2: Path konfigurasi yang salah diam-diam diabaikan

- **Gejala:** aplikasi dijalankan dengan `-Ddanadesa.config=/tidak/ada.properties` tetapi tetap tersambung ke database tanpa pesan apa pun. Pengguna mengira memakai konfigurasi A, padahal yang terbaca file lain.
- **Cara ditemukan:** uji integrasi (skenario terakhir mengharapkan `KonfigurasiException`, tetapi hasilnya `null`).
- **Penyebab:** di `DatabaseConfig.muat()`, bila file pada path tidak ada, kode langsung lanjut mencari `db.properties` di classpath. Kebetulan ada `db.properties` di folder kerja (yang juga masuk classpath), jadi file itu yang terbaca.
- **Perbaikan:** jika system property `danadesa.config` diisi tetapi filenya tidak ada, langsung lempar `KonfigurasiException("File konfigurasi ... tidak ditemukan.")`. Fallback ke classpath hanya dipakai bila property tidak diisi.

## Bug 3: Nilai rupiah di tabel Rekap per Desa terpotong

- **Gejala:** kolom Kabupaten tampil `Kabupaten Suk...` dan nilai rupiah tampil `Rp 1.045.000.0...`, sehingga angka tidak bisa dibaca.
- **Cara ditemukan:** menjalankan aplikasi dan melihat tab Rekap.
- **Penyebab:** JTable memaksa 10 kolom muat di lebar yang tersedia, sehingga setiap kolom terlalu sempit untuk angka miliaran. Awalan "Rp" di setiap sel juga memakan ruang.
- **Perbaikan:** (1) menambah `Tabel.lebarKolom(...)` untuk mengatur lebar relatif tiap kolom; (2) kolom nominal di tabel rekap memakai tipe `Tabel.Angka` (tanpa "Rp", satuan ditulis di judul kolom, misalnya "Pagu (Rp)"); (3) kolom Kabupaten di rekap per desa dihapus karena sudah ada di tabel rekap per kabupaten (di file CSV tetap ada). Tipe `Angka` juga didaftarkan ke sorter numerik supaya tidak mengulang Bug 1.

## Bug 4: Tinggi tombol tidak seragam (tombol terlalu besar / terlalu pendek)

- **Gejala:** tombol "Tambah Desa" tampil sangat tinggi (±80 px). Setelah diperbaiki dengan client property FlatLaf `JComponent.minimumHeight`, tombol justru menjadi terlalu pendek (±20 px).
- **Cara ditemukan:** menjalankan aplikasi dan membandingkan tinggi tombol dengan kotak isian.
- **Penyebab:** (1) tombol diletakkan di `BorderLayout.CENTER` di dalam `CardLayout`, sehingga ikut meregang setinggi kartu terbesar (mode ubah punya dua baris tombol); (2) `JComponent.minimumHeight` hanya memengaruhi ukuran *minimum*, sedangkan `FlowLayout` memakai ukuran *preferred*, ditambah margin vertikal 0 membuat tombol pendek.
- **Perbaikan:** tombol dibungkus `FlowLayout` (tidak meregang), dan `Tema.tombol()` meng-override `getPreferredSize()` agar tinggi selalu `Tema.TINGGI_KONTROL` (34 px, sama dengan kotak isian) dengan lebar mengikuti teks.

---

## Error yang sengaja ditangani (untuk demo validasi)

| Skenario | Pesan yang muncul |
|----------|-------------------|
| Pagu diisi `abc` | Pagu harus berupa angka, contoh: 250000000 atau 250.000.000 |
| Nilai dikosongkan | Nilai penyaluran wajib diisi. |
| Tanggal `31-02-2026` | Tanggal penyaluran tidak valid. Gunakan format dd-MM-yyyy ... |
| Nilai melebihi sisa pagu | Nilai penyaluran Rp 300.000.000 melebihi sisa pagu desa Rp 197.100.000. |
| Catat Tahap III padahal Tahap II belum ada | Tahap III belum bisa dicatat karena Tahap II belum disalurkan. |
| Hapus desa yang sudah punya penyaluran | Desa ... tidak bisa dihapus karena sudah punya N data penyaluran. |
| MySQL mati / password salah | Tidak bisa terhubung ke database ... (aplikasi menutup dengan rapi) |
| `db.properties` tidak ada | File db.properties tidak ditemukan. Salin db.properties.example ... |
