package com.pab.trivku.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.pab.trivku.R
import com.pab.trivku.data.models.Destination
import com.pab.trivku.data.models.Favorite
import com.pab.trivku.data.models.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

@Database(
    entities = [Destination::class, User::class, Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // panggil file Dao (Data Access Objek) -> Logika bisnis
    abstract fun destinationDao(): DestinationDao
    abstract fun userDao(): UserDao
    abstract fun favoriteDao(): FavoriteDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val scope = CoroutineScope(SupervisorJob())

                val builder = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "destination_database"
                )
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .addCallback(object : RoomDatabase.Callback() {
                        override fun onCreate(db: SupportSQLiteDatabase) {
                            super.onCreate(db)
                            // Gunakan scope untuk insert data di background
                            INSTANCE?.let { database ->
                                scope.launch(Dispatchers.IO) {
                                    database.destinationDao().insertAll(getStaticDestinations())
                                }
                            }
                        }
                    })

                val instance = builder.build()
                INSTANCE = instance
                instance
            }
        }

        // deklarasikan data statis yang hanya di create di awal installasi apps
        private fun getStaticDestinations(): List<Destination> {
            return listOf(
                Destination(
                    name = "Telaga Nilem",
                    location = "Pasawahan",
                    imageResource = R.drawable.telaga_nilem,
                    describe = "Telaga Nilem punya air sebening kaca yang menenangkan. Bahkan pengunjung bisa melihat ikan berenang dari atas permukaan. Kedalamannya bervariasi dan cocok untuk berenang santai. Suasana sekitar telaga dikelilingi pepohonan rindang. Tempat ini sangat cocok untuk healing dan foto-foto cantik. Tempat ini masuk dalam daftar wisata kuningan untuk healing yang populer."
                ),
                Destination(
                    name = "Telaga Cicerem",
                    location = "Kaduela",
                    imageResource = R.drawable.telaga_cicerem,
                    describe = "Telaga Cicerem dikenal juga sebagai Telaga Biru karena warna airnya. Airnya sangat jernih, sampai-sampai dasar telaga terlihat jelas. Ikan warna-warni berenang bebas dan jadi daya tarik utama. Ada spot ayunan pohon di atas air yang jadi favorit wisatawan. Pemandangannya indah dan sangat cocok untuk healing. Tempat ini sering viral sebagai wisata kuningan terbaru yang Instagramable."
                ),
                Destination(
                    name = "Telaga Remis",
                    location = "Kaduela",
                    imageResource = R.drawable.telaga_remis,
                    describe = "Telaga Remis berada di lereng Gunung Ciremai dengan luas sekitar 3,3 hektar. Nama remis diambil dari sejenis kerang kuning yang hidup di sini. Suasana di sekitarnya sangat tenang dan rindang. Udaranya sejuk, cocok untuk piknik dan lepas penat. Tempat ini juga jadi spot favorit untuk wisata keluarga. Salah satu wisata alam kuningan yang cocok untuk rehat dari hiruk pikuk kota."
                ),
                Destination(
                    name = "Cibulan",
                    location = "Maniskidul",
                    imageResource = R.drawable.cibulan,
                    describe = "Cibulan dikenal sebagai tempat wisata kuningan tertua yang masih ramai dikunjungi. Kolam alaminya dihuni ikan dewa, endemik khas daerah ini. Konon, ikan ini adalah jelmaan prajurit Prabu Siliwangi. Wisatawan bisa berenang bersama ikan jinak tersebut di kolam utama. Di sisi kolam, terdapat tujuh mata air yang dikenal sakral. Suasananya sejuk karena berada di kaki Gunung Ciremai."
                ),
                Destination(
                    name = "Wisata Cipaniis",
                    location = "Singkup",
                    imageResource = R.drawable.wisata_cipaniis,
                    describe = "Wisata Cipaniis menyuguhkan sungai jernih dengan suasana alami. Airnya dipercaya bisa menyembuhkan penyakit kulit. Tempat ini juga digunakan sebagai bumi perkemahan dan irigasi warga. Pengunjung bisa berenang atau menjajal body rafting yang seru. Pepohonan pinus di sekitarnya membuat udara makin sejuk. Wisata Cipaniis berada di Desa Singkup, Kecamatan Pasawahan, Kuningan. Lokasinya mudah diakses dan cocok untuk wisata keluarga atau rombongan sekolah."
                ),
                Destination(
                    name = "Gedung Sjahrir",
                    location = "Cilimus",
                    imageResource = R.drawable.gedung_sjahrir,
                    describe = "Gedung Sjahrir berada di kawasan Gedung Perundingan Linggarjati. Dulu, tempat ini digunakan delegasi Indonesia saat perundingan 1946. Sutan Sjahrir dan timnya menginap di gedung ini selama negosiasi. Bangunan bersejarah ini kini masih berdiri dengan baik. Gedung ini cocok dikunjungi untuk wisata edukasi sejarah sekaligus mengenang perjuangan kemerdekaan."
                ),
                Destination(
                    name = "Situs Lingga",
                    location = "Darma",
                    imageResource = R.drawable.situs_lingga,
                    describe = "Situs ini terletak di Desa Sagarahiang dan dipercaya sudah ada sebelum masehi. Batu Lingga digunakan untuk melihat waktu oleh raja-raja terdahulu. Desa ini juga disebut sebagai desa tertua di Kuningan. Wisatawan bisa menjelajahi area situs yang masih terjaga. Lokasinya dikelilingi alam hijau dan udara pegunungan yang sejuk. Situs Lingga cocok dikunjungi bagi penyuka sejarah dan kepercayaan budaya lokal."
                ),
                Destination(
                    name = "Petilasan Prabu Siliwangi",
                    location = "Manis Kidul",
                    imageResource = R.drawable.petilasan_prabu_siliwangi,
                    describe = "Di dalam kawasan Cibulan, ada situs tujuh sumur yang dipercaya sakral. Sumur ini diyakini muncul saat Prabu Siliwangi menghentakkan tongkatnya. Setiap sumur memiliki nama dan fungsi berbeda, seperti Sumur Kejayaan dan Sumur Pengabulan. Pengunjung biasanya datang untuk berdoa atau mengambil air sumur. Tempat ini cocok untuk wisata religi dan budaya. Petilasan ini berada di Desa Manis Kidul, tak jauh dari kolam Cibulan."
                ),
                Destination(
                    name = "Gedung Perundingan Linggarjati",
                    location = "Cilimus",
                    imageResource = R.drawable.gedung_perundingan_linggarjati,
                    describe = "Gedung ini jadi saksi penting sejarah kemerdekaan Indonesia. Di sinilah Perundingan Linggarjati antara Indonesia dan Belanda digelar pada 1946. Terletak di kaki Gunung Ciremai, suasana sekitarnya sejuk dan tenang. Gedung ini masih berdiri kokoh dan terbuka untuk umum. Wisatawan bisa melihat langsung ruang perundingan dan artefak sejarah lainnya. Tempat ini cocok untuk wisata edukatif sambil menikmati keindahan alam sekitarnya."
                ),
                Destination(
                    name = "Museum Situs Purbakala Cipari",
                    location = "Cigugur",
                    imageResource = R.drawable.museum_situs_purbakala_cipari,
                    describe = "Museum ini menampilkan peninggalan zaman megalitikum yang ditemukan sejak 1972. Koleksinya beragam, seperti menhir, kapak batu, hingga gerabah purba. Lokasinya tidak jauh dari pusat kota Kuningan. Suasana di sekitar museum pun asri dan cocok untuk belajar sejarah. Banyak pelajar dan peneliti berkunjung ke sini untuk mengenal kehidupan masa lampau. Museum Situs Purbakala Cipari jadi salah satu destinasi wisata kuningan yang edukatif dan tak membosankan."
                )
            )
        }
    }
}
