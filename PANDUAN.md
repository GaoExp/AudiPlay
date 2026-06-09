# Panduan Penggunaan Mini Player

## Daftar Isi

- [Persyaratan](#persyaratan)
- [Memulai](#memulai)
- [Navigasi](#navigasi)
- [Fitur](#fitur)
- [Izin](#izin)

## Persyaratan

- Android 8.0 (API 26) atau lebih baru
- Izin akses audio/media

## Memulai

1. Install APK Mini Player
2. Berikan izin akses audio saat diminta
3. Aplikasi akan otomatis memindai file audio di perangkat

## Navigasi

Aplikasi menggunakan **Navigation Drawer** (sidebar) yang bisa dibuka dengan:
- Tap ikon hamburger (☰) di kiri atas
- Swipe dari tepi kiri layar

Drawer terdiri dari menu berikut (setiap menu menampilkan jumlah item):

| Menu           | Ikon              | Fungsi                          |
|----------------|-------------------|---------------------------------|
| Lagu           | Nota musik        | Daftar semua lagu               |
| Favorit        | Hati              | Lagu yang ditandai favorit      |
| Artis          | Siluet orang      | Daftar artis                    |
| Album          | CD                | Daftar album                    |
| Playlist       | Antrian           | Daftar playlist                 |
| Audio Lainnya  | Nota + gelombang  | File audio non-musik            |
| Folder         | Folder            | Folder musik per direktori      |
| System Picker  | Monitor           | Impor audio via system picker   |
| Pengaturan     | Gear              | Pengaturan aplikasi             |

Header drawer menampilkan total statistik perpustakaan: jumlah lagu, total durasi, dan total ukuran penyimpanan.

## Fitur

### Statistik Perpustakaan
- Setiap daftar (Lagu, Artis, Album, Folder, Playlist) menampilkan header dengan total item, total lagu, total durasi, dan total ukuran
- Setiap item di Artis, Album, Folder, dan Playlist menampilkan jumlah lagu, total durasi, dan total ukuran
- Navigation Drawer header menampilkan total statistik perpustakaan dan setiap menu menampilkan jumlah item

### Daftar Lagu (Songs)
- Menampilkan semua lagu yang terdeteksi
- Header statistik: total item, total lagu, total durasi, total ukuran
- Cari lagu dengan kolom pencarian
- Urutkan lagu (A-Z, Z-A, durasi, tanggal)
- Tap lagu untuk memutar
- Menu opsi (tambah ke favorit, tambah ke playlist)

### Favorit (Favorites)
- Menampilkan lagu yang ditandai favorit
- Tap untuk memutar
- Hapus dari favorit

### Playlist
- Buat playlist baru
- Rename playlist
- Hapus playlist
- Tambah/hapus lagu dari playlist
- Lihat detail isi playlist
- Setiap playlist menampilkan total lagu, total durasi, dan total ukuran
- Header statistik: total playlist, total lagu, total durasi, total ukuran
- Export playlist (M3U per playlist / JSON semua playlist) — menu ⋮ di toolbar detail playlist
- Import playlist (M3U / JSON) — long-press FAB `+` di daftar playlist
- Auto-scan — file .m3u/.m3u8 di folder musik otomatis terdeteksi sebagai playlist

### Bottom Sheet Player
- Mini player bar di bagian bawah saat lagu diputar (judul, artis, album art, play/pause)
- Tap atau swipe up pada bar untuk membuka pemutaran penuh
- Tampilan penuh: cover art, judul, artis, info teknis (sample rate, bitrate, codec), seek slider, progress time
- Kontrol pemutaran: Shuffle, Previous, Play/Pause, Next, Repeat
- Tombol Favorit di pojok kanan atas
- Tombol menu (⁝) — Tambah ke Playlist, Rincian lagu, Bagikan, Hapus file
- Swipe down untuk menutup tampilan penuh kembali ke mini player

### Pemutaran Background
- Musik tetap berjalan saat aplikasi di-minimize
- Notifikasi dengan kontrol play/pause, next, previous
- Notifikasi dengan tampilan informasi lagu

### Pengaturan (Settings)
- **Keep Screen On** — Layar tetap menyala (info: membatalkan batas waktu layar)
- **Putar Audio Diatas Aplikasi Lain** — Lanjut pemutaran saat buka app lain (info: tidak ada gangguan pemutaran)
- **Batasi Pemindaian** — Toggle: ON = hanya folder yang diizinkan dipindai; OFF = semua folder dipindai (Folder Dikecualikan tetap diterapkan)
- **Folder yang Diizinkan** — Folder yang dipindai saat Batasi Pemindaian aktif (redup saat nonaktif, tap untuk mengaktifkan)
- **Folder Dikecualikan** — Folder yang selalu dilewati saat pemindaian (tidak tergantung toggle Batasi Pemindaian)
- **Lihat Folder Audio** — Buka FolderListActivity untuk melihat/mengelola semua folder audio: mode Kontrol Grup (kelompok Diizinkan/Dikecualikan/Belum Ditentukan, drag antar section) dan Visual Daftar (daftar flat, warna hijau/merah sesuai status)
- **Format Audio** — Filter ekstensi format file yang dipindai (Audio, Rekaman, MIDI, Video, Stream)
- **Ikon Aplikasi** — Pilih dari 5 ikon alternatif untuk aplikasi di layar utama (Nota Musik, Putar, Nota Musik Gelap, Putar Gelap, Antrian)
- **Lihat Dokumentasi** — Buka pembaca dokumentasi in-app (README, PANDUAN, STRUKTUR, CHANGELOG)

## Izin

| Izin                          | Fungsi                                   |
|-------------------------------|------------------------------------------|
| `READ_MEDIA_AUDIO` (13+)      | Membaca file audio                       |
| `READ_EXTERNAL_STORAGE` (≤12) | Membaca storage (audio)                  |
| `POST_NOTIFICATIONS` (13+)    | Menampilkan notifikasi pemutaran         |
| `FOREGROUND_SERVICE`          | Menjalankan service background           |
| `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Service media playback            |
| `BLUETOOTH_CONNECT`           | Kontrol Bluetooth (headset)              |

---
