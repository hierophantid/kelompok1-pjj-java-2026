-- =====================================================================
-- Pencatatan Penyaluran Dana Desa - Capstone PJJ Java Fundamental 2026
-- SELURUH DATA DI FILE INI ADALAH DATA REKAAN (DUMMY).
-- Kode wilayah memakai awalan 99 (tidak dipakai kode provinsi riil),
-- nama kabupaten dan desa fiktif.
-- Jalankan di MySQL 8.x:  mysql -u root -p < sql/dana_desa.sql
-- =====================================================================

DROP DATABASE IF EXISTS dana_desa;
CREATE DATABASE dana_desa CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE dana_desa;

-- ---------------------------------------------------------------------
-- Tabel
-- ---------------------------------------------------------------------
CREATE TABLE kabupaten (
    id    INT AUTO_INCREMENT PRIMARY KEY,
    kode  CHAR(4)      NOT NULL,
    nama  VARCHAR(100) NOT NULL,
    CONSTRAINT uq_kabupaten_kode UNIQUE (kode)
) ENGINE = InnoDB;

CREATE TABLE desa (
    id            INT AUTO_INCREMENT PRIMARY KEY,
    kode          CHAR(10)      NOT NULL,
    nama          VARCHAR(100)  NOT NULL,
    kabupaten_id  INT           NOT NULL,
    pagu          DECIMAL(18,2) NOT NULL,
    CONSTRAINT uq_desa_kode UNIQUE (kode),
    CONSTRAINT ck_desa_pagu CHECK (pagu > 0),
    CONSTRAINT fk_desa_kabupaten FOREIGN KEY (kabupaten_id)
        REFERENCES kabupaten (id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

CREATE TABLE penyaluran (
    id       INT AUTO_INCREMENT PRIMARY KEY,
    desa_id  INT           NOT NULL,
    tahap    TINYINT       NOT NULL COMMENT '1 = Tahap I, 2 = Tahap II, 3 = Tahap III',
    tanggal  DATE          NOT NULL,
    nilai    DECIMAL(18,2) NOT NULL,
    CONSTRAINT uq_penyaluran_desa_tahap UNIQUE (desa_id, tahap),
    CONSTRAINT ck_penyaluran_tahap CHECK (tahap BETWEEN 1 AND 3),
    CONSTRAINT ck_penyaluran_nilai CHECK (nilai > 0),
    CONSTRAINT fk_penyaluran_desa FOREIGN KEY (desa_id)
        REFERENCES desa (id) ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE = InnoDB;

-- ---------------------------------------------------------------------
-- Data dummy
-- ---------------------------------------------------------------------
INSERT INTO kabupaten (id, kode, nama) VALUES
    (1, '9901', 'Kabupaten Sukamaju'),
    (2, '9902', 'Kabupaten Tirtajaya'),
    (3, '9903', 'Kabupaten Wanasari');

-- 15 desa (entitas utama)
INSERT INTO desa (id, kode, nama, kabupaten_id, pagu) VALUES
    (1, '9901012001', 'Mekarsari', 1, 1120000000.00),
    (2, '9901012002', 'Sindangjaya', 1, 985500000.00),
    (3, '9901012003', 'Cikahuripan', 1, 1045000000.00),
    (4, '9901022001', 'Sukaresmi', 1, 890000000.00),
    (5, '9901022002', 'Margahayu', 1, 1210750000.00),
    (6, '9902012001', 'Tirtamulya', 2, 1150000000.00),
    (7, '9902012002', 'Sumberagung', 2, 965000000.00),
    (8, '9902012003', 'Jatimulyo', 2, 1005200000.00),
    (9, '9902022001', 'Wonosari Lor', 2, 870000000.00),
    (10, '9902022002', 'Pagerharjo', 2, 1098000000.00),
    (11, '9903012001', 'Sungai Rambai', 3, 1320000000.00),
    (12, '9903012002', 'Tanjung Harapan', 3, 940000000.00),
    (13, '9903012003', 'Pasir Putih', 3, 1012400000.00),
    (14, '9903022001', 'Teluk Indah', 3, 905000000.00),
    (15, '9903022002', 'Bukit Raya', 3, 1185600000.00);

-- 26 penyaluran: pola 40% / 40% / 20%, sebagian desa belum lengkap
INSERT INTO penyaluran (desa_id, tahap, tanggal, nilai) VALUES
    (1, 1, '2026-03-02', 448000000.00),
    (1, 2, '2026-05-01', 448000000.00),
    (1, 3, '2026-09-01', 224000000.00),
    (2, 1, '2026-01-26', 394200000.00),
    (2, 2, '2026-04-22', 394200000.00),
    (3, 1, '2026-01-30', 418000000.00),
    (3, 2, '2026-05-29', 418000000.00),
    (3, 3, '2026-09-25', 209000000.00),
    (4, 1, '2026-01-27', 356000000.00),
    (5, 1, '2026-03-25', 484300000.00),
    (5, 2, '2026-05-08', 484300000.00),
    (6, 1, '2026-01-23', 460000000.00),
    (6, 2, '2026-04-24', 460000000.00),
    (6, 3, '2026-09-04', 230000000.00),
    (7, 1, '2026-03-13', 386000000.00),
    (8, 1, '2026-01-28', 402080000.00),
    (8, 2, '2026-05-13', 402080000.00),
    (10, 1, '2026-01-30', 439200000.00),
    (10, 2, '2026-06-22', 439200000.00),
    (10, 3, '2026-09-04', 219600000.00),
    (11, 1, '2026-01-27', 528000000.00),
    (11, 2, '2026-06-24', 528000000.00),
    (12, 1, '2026-02-04', 376000000.00),
    (14, 1, '2026-02-17', 362000000.00),
    (14, 2, '2026-06-26', 362000000.00),
    (15, 1, '2026-01-27', 474240000.00);

-- ---------------------------------------------------------------------
-- (Opsional) user khusus aplikasi. Ganti password sebelum dipakai,
-- lalu tulis user/password yang sama di db.properties (bukan di kode).
-- ---------------------------------------------------------------------
-- CREATE USER 'danadesa_app'@'localhost' IDENTIFIED BY 'ganti_password_ini';
-- GRANT SELECT, INSERT, UPDATE, DELETE ON dana_desa.* TO 'danadesa_app'@'localhost';
