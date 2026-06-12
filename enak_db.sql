-- =========================================================
--  DATABASE E-NAK (Electronic Rapid Nutritional Assessment
--  for Kids) - Berdasarkan jurnal Wiyata Vol.10 No.02 2023
--  Cara pakai: buka phpMyAdmin atau MySQL CLI, lalu
--  jalankan script ini.
-- =========================================================

CREATE DATABASE IF NOT EXISTS enak_db
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE enak_db;

-- ---------------------------------------------------------
-- 1. Tabel ahli_gizi  (user login)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS ahli_gizi (
    id         INT          NOT NULL AUTO_INCREMENT,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    password   VARCHAR(100) NOT NULL,
    nama       VARCHAR(100) NOT NULL,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 2. Tabel pasien_anak
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS pasien_anak (
    id             INT          NOT NULL AUTO_INCREMENT,
    nrm            VARCHAR(20)  NOT NULL UNIQUE COMMENT 'Nomor Rekam Medis',
    nama           VARCHAR(100) NOT NULL,
    tempat_lahir   VARCHAR(100),
    tanggal_lahir  DATE,
    jenis_kelamin  ENUM('L','P') NOT NULL,
    nama_ibu       VARCHAR(100),
    nama_ayah      VARCHAR(100),
    no_telepon     VARCHAR(20),
    pendidikan     VARCHAR(50),
    berat_badan    DECIMAL(5,2) COMMENT 'kg',
    tinggi_badan   DECIMAL(5,2) COMMENT 'cm',
    alamat         TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 3. Tabel pasien_dewasa
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS pasien_dewasa (
    id             INT          NOT NULL AUTO_INCREMENT,
    nrm            VARCHAR(20)  NOT NULL UNIQUE,
    nama           VARCHAR(100) NOT NULL,
    tempat_lahir   VARCHAR(100),
    tanggal_lahir  DATE,
    jenis_kelamin  ENUM('L','P') NOT NULL,
    no_telepon     VARCHAR(20),
    pendidikan     VARCHAR(50),
    berat_badan    DECIMAL(5,2),
    tinggi_badan   DECIMAL(5,2),
    alamat         TEXT,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 4. Tabel penilaian_skrining_anak
--    A = Diagnosis Implikasi Gizi  (0-1)
--    B = Asupan Gizi Anak          (0-1)
--    C = Skor Z                    (0-1)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS penilaian_skrining_anak (
    id           INT NOT NULL AUTO_INCREMENT,
    pasien_id    INT NOT NULL,
    skor_a       INT NOT NULL DEFAULT 0 COMMENT 'Diagnosis Implikasi Gizi',
    skor_b       INT NOT NULL DEFAULT 0 COMMENT 'Asupan Gizi Anak',
    skor_c       INT NOT NULL DEFAULT 0 COMMENT 'Skor Z',
    total_skor   INT GENERATED ALWAYS AS (skor_a + skor_b + skor_c) STORED,
    interpretasi VARCHAR(20) COMMENT 'Low Risk / Medium Risk / High Risk',
    tindak_lanjut TEXT,
    tanggal      DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (pasien_id) REFERENCES pasien_anak(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 5. Tabel penilaian_skrining_dewasa
--    A = Skrining asupan makanan 3 bulan terakhir
--    B = Penurunan BB 3 bulan terakhir
--    C = Mobilitas
--    D = Gangguan kejiwaan 3 bulan terakhir
--    E = Masalah neuropsikologis
--    F = Body Mass Index (BMI)
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS penilaian_skrining_dewasa (
    id           INT NOT NULL AUTO_INCREMENT,
    pasien_id    INT NOT NULL,
    skor_a       INT NOT NULL DEFAULT 0,
    skor_b       INT NOT NULL DEFAULT 0,
    skor_c       INT NOT NULL DEFAULT 0,
    skor_d       INT NOT NULL DEFAULT 0,
    skor_e       INT NOT NULL DEFAULT 0,
    skor_f       INT NOT NULL DEFAULT 0,
    total_skor   INT GENERATED ALWAYS AS (skor_a+skor_b+skor_c+skor_d+skor_e+skor_f) STORED,
    interpretasi VARCHAR(20),
    tindak_lanjut TEXT,
    tanggal      DATETIME DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    FOREIGN KEY (pasien_id) REFERENCES pasien_dewasa(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- 6. Tabel hasil_asesmen
-- ---------------------------------------------------------
CREATE TABLE IF NOT EXISTS hasil_asesmen (
    id            INT          NOT NULL AUTO_INCREMENT,
    nama_pasien   VARCHAR(100) NOT NULL,
    jenis_pasien  ENUM('anak','dewasa') NOT NULL,
    skor_akhir    INT          NOT NULL,
    interpretasi  VARCHAR(20)  NOT NULL,
    tindak_lanjut TEXT,
    tanggal       DATETIME     DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id)
) ENGINE=InnoDB;

-- ---------------------------------------------------------
-- Data awal: akun ahli gizi default
-- password: admin123
-- ---------------------------------------------------------
INSERT INTO ahli_gizi (username, password, nama)
VALUES ('admin', 'admin123', 'Admin Gizi');

-- ---------------------------------------------------------
-- Data contoh pasien anak
-- ---------------------------------------------------------
INSERT INTO pasien_anak (nrm, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
    nama_ibu, nama_ayah, no_telepon, pendidikan, berat_badan, tinggi_badan, alamat)
VALUES
    ('PA-001', 'Budi Santoso',    'Surabaya',  '2018-03-15', 'L', 'Siti', 'Andi', '081234567890', 'TK', 18.5, 110.0, 'Jl. Melati No.1 Surabaya'),
    ('PA-002', 'Dina Rahayu',     'Jakarta',   '2019-07-22', 'P', 'Rina', 'Doni', '082345678901', 'TK', 16.0, 105.0, 'Jl. Mawar No.2 Jakarta'),
    ('PA-003', 'Fajar Nugroho',   'Bandung',   '2017-11-05', 'L', 'Maya', 'Eko',  '083456789012', 'SD', 22.0, 120.0, 'Jl. Anggrek No.3 Bandung'),
    ('PA-004', 'Hana Putri',      'Yogyakarta','2020-01-30', 'P', 'Dewi', 'Rudi', '084567890123', '-',  14.5, 98.0,  'Jl. Kenanga No.4 Yogyakarta'),
    ('PA-005', 'Ilham Setiawan',  'Semarang',  '2016-08-18', 'L', 'Lena', 'Bayu', '085678901234', 'SD', 25.0, 125.0, 'Jl. Dahlia No.5 Semarang');

-- Data contoh penilaian skrining anak
INSERT INTO penilaian_skrining_anak (pasien_id, skor_a, skor_b, skor_c, interpretasi, tindak_lanjut)
VALUES
    (1, 0, 1, 0, 'Low Risk',    'Monitoring rutin setiap bulan'),
    (2, 1, 1, 1, 'High Risk',   'Rujuk ke dokter spesialis gizi anak segera'),
    (3, 0, 0, 1, 'Low Risk',    'Edukasi gizi kepada orang tua'),
    (4, 1, 1, 0, 'Medium Risk', 'Intervensi gizi dalam 1 minggu'),
    (5, 0, 1, 1, 'Medium Risk', 'Pantau asupan makan selama 2 minggu');

-- Data contoh pasien dewasa
INSERT INTO pasien_dewasa (nrm, nama, tempat_lahir, tanggal_lahir, jenis_kelamin,
    no_telepon, pendidikan, berat_badan, tinggi_badan, alamat)
VALUES
    ('PD-001', 'Ahmad Fauzi',    'Surabaya', '1985-04-10', 'L', '081111111111', 'S1', 70.0, 170.0, 'Jl. Merdeka No.10'),
    ('PD-002', 'Bunga Larasati', 'Malang',   '1990-09-25', 'P', '082222222222', 'D3', 55.0, 160.0, 'Jl. Pahlawan No.11');

-- Data contoh penilaian skrining dewasa
INSERT INTO penilaian_skrining_dewasa (pasien_id, skor_a, skor_b, skor_c, skor_d, skor_e, skor_f, interpretasi, tindak_lanjut)
VALUES
    (1, 0, 1, 0, 0, 0, 1, 'Low Risk',    'Monitoring rutin'),
    (2, 1, 2, 1, 0, 1, 2, 'High Risk',   'Intervensi gizi intensif');
