package com.studio.faisal.uangkas

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.database.Cursor
import android.database.sqlite.SQLiteOpenHelper
import android.os.Bundle
import android.support.design.widget.FloatingActionButton
import android.support.v4.widget.SwipeRefreshLayout
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView.OnItemClickListener
import android.widget.ListView
import android.widget.SimpleAdapter
import android.widget.TextView
import android.widget.Toast
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.studio.faisal.uangkas.MainActivity
import com.studio.faisal.uangkas.helper.Config
import org.json.JSONException
import org.json.JSONObject
import java.text.NumberFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    var text_masuk: TextView? = null
    var text_keluar: TextView? = null
    var text_total: TextView? = null
    var list_kas: ListView? = null
    var swipe_refresh: SwipeRefreshLayout? = null
    var aruskas = ArrayList<HashMap<String, String?>>()
    var query_kas: String? = null
    var query_total: String? = null
    var sqliteHelper: SQLiteOpenHelper? = null
    var cursor: Cursor? = null
    // session-intro
    var sess_intro = 0
    var sharedPreferences: SharedPreferences? = null

    var editor: SharedPreferences.Editor? = null
    @SuppressLint("WrongConstant")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val toolbar = findViewById<View>(R.id.toolbar) as Toolbar
        setSupportActionBar(toolbar)
        sess_intro = 0
        sharedPreferences = getSharedPreferences("sess_pref_mhr",
                Context.MODE_APPEND)
        sess_intro = sharedPreferences!!.getInt("intro", 0)
        if (sess_intro == 0) { // set session
            sharedPreferences = getSharedPreferences("sess_pref_mhr", Context.MODE_PRIVATE)
            editor = sharedPreferences!!.edit()
            editor?.putInt("intro", 1)
            editor?.apply()
            // call intro
            startActivity(Intent(this@MainActivity,
                    IntroActivity::class.java))
        }
        LINK = Config.host + "list.php"
        transaksi_id = ""
        status = ""
        jumlah = ""
        keterangan = ""
        tanggal = ""
        tanggal2 = ""
        tgl_dari = ""
        tgl_ke = ""
        query_kas = ""
        query_total = ""
        filter = false
        text_filter = findViewById<View>(R.id.text_filter) as TextView
        text_masuk = findViewById<View>(R.id.text_masuk) as TextView
        text_keluar = findViewById<View>(R.id.text_keluar) as TextView
        text_total = findViewById<View>(R.id.text_total) as TextView
        list_kas = findViewById<View>(R.id.list_kas) as ListView
        swipe_refresh = findViewById<View>(R.id.swipe_refresh) as SwipeRefreshLayout
        swipe_refresh!!.setOnRefreshListener {
            query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC"
            query_total = "SELECT SUM(jumlah) AS total, " +
                    "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'MASUK') AS masuk, " +
                    "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'keluar') AS keluar " +
                    "FROM transaksi"
            LINK = Config.host + "list.php"
            KasAdapter()
            text_filter!!.visibility = View.GONE
        }
        val fab = findViewById<View>(R.id.fab) as FloatingActionButton
        fab.setOnClickListener {
            startActivity(Intent(this@MainActivity,
                    AddActivity::class.java))
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean { // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean { // Handle action bar item clicks here. The action bar will
// automatically handle clicks on the Home/Up button, so long
// as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        if (id == R.id.action_filter) {
            startActivity(Intent(this@MainActivity,
                    FilterActivity::class.java))
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun KasAdapter() {
        swipe_refresh!!.isRefreshing = true
        aruskas.clear()
        list_kas!!.adapter = null
        Log.d("link", LINK)
        AndroidNetworking.post(LINK)
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) { //do anything with response
                        val rupiahFormat = NumberFormat.getInstance(Locale.GERMANY)
                        text_masuk!!.text = rupiahFormat.format(response.optDouble("masuk"))
                        text_keluar!!.text = rupiahFormat.format(response.optDouble("keluar"))
                        text_total!!.text = rupiahFormat.format(response.optDouble("saldo"))
                        try {
                            val jsonArray = response.optJSONArray("result")
                            for (i in 0 until jsonArray.length()) {
                                val responses = jsonArray.getJSONObject(i)
                                val map = HashMap<String, String?>()
                                map["transaksi_id"] = responses.optString("transaksi_id")
                                map["status"] = responses.optString("status")
                                map["jumlah"] = responses.optString("jumlah")
                                map["keterangan"] = responses.optString("keterangan")
                                map["tanggal"] = responses.optString("tanggal")
                                map["tanggal2"] = responses.optString("tanggal2")
                                aruskas.add(map)
                            }
                            Adapter()
                        } catch (e: JSONException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(anError: ANError) {}
                })
    }

    private fun Adapter() {
        val simpleAdapter = SimpleAdapter(this, aruskas, R.layout.list_kas, arrayOf("transaksi_id", "status", "jumlah", "keterangan", "tanggal", "tanggal2"), intArrayOf(R.id.text_transaksi_id, R.id.text_status, R.id.text_jumlah, R.id.text_keterangan, R.id.text_tanggal, R.id.text_tanggal2))
        list_kas!!.adapter = simpleAdapter
        list_kas!!.onItemClickListener = OnItemClickListener { parent, view, position, id ->
            transaksi_id = (view.findViewById<View>(R.id.text_transaksi_id) as TextView).text.toString()
            status = (view.findViewById<View>(R.id.text_status) as TextView).text.toString()
            jumlah = (view.findViewById<View>(R.id.text_jumlah) as TextView).text.toString()
            keterangan = (view.findViewById<View>(R.id.text_keterangan) as TextView).text.toString()
            tanggal = (view.findViewById<View>(R.id.text_tanggal) as TextView).text.toString()
            tanggal2 = (view.findViewById<View>(R.id.text_tanggal2) as TextView).text.toString()
            ListMenu()
        }
        swipe_refresh!!.isRefreshing = false
    }

    override fun onResume() {
        super.onResume()
        query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi ORDER BY transaksi_id DESC"
        query_total = "SELECT SUM(jumlah) AS total, " +
                "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'MASUK') AS masuk, " +
                "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'KELUAR') AS keluar " +
                "FROM transaksi"
        if (filter) {
            query_kas = "SELECT *, strftime('%d/%m/%Y', tanggal) AS tgl FROM transaksi " +
                    "WHERE (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "') ORDER BY transaksi_id ASC"
            query_total = "SELECT SUM(jumlah) AS total, " +
                    "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'MASUK' AND (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "')), " +
                    "(SELECT SUM (jumlah) FROM transaksi WHERE status = 'KELUAR' AND (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "')) " +
                    "FROM transaksi " +
                    "WHERE (tanggal >= '" + tgl_dari + "') AND (tanggal <= '" + tgl_ke + "') "
            LINK = Config.host + "filter.php?from=" + tgl_dari + "&to=" + tgl_ke
            filter = false
        }
        KasAdapter()
    }

    private fun ListMenu() {
        val dialog = Dialog(this@MainActivity)
        dialog.setContentView(R.layout.list_menu)
        dialog.window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT)
        val text_edit = dialog.findViewById<View>(R.id.text_edit) as TextView
        val text_hapus = dialog.findViewById<View>(R.id.text_hapus) as TextView
        dialog.show()
        text_edit.setOnClickListener {
            dialog.dismiss()
            startActivity(Intent(this@MainActivity, EditActivity::class.java))
        }
        text_hapus.setOnClickListener {
            dialog.dismiss()
            Hapus()
        }
    }

    private fun Hapus() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Konfirmasi")
        builder.setMessage("Yakin untuk mengahapus transaksi ini?")
        builder.setPositiveButton(
                "Yes"
        ) { dialog, id ->
            dialog.dismiss()
            AndroidNetworking.post(Config.host + "delete.php")
                    .addBodyParameter("transaksi_id", transaksi_id)
                    .setPriority(Priority.MEDIUM)
                    .build()
                    .getAsJSONObject(object : JSONObjectRequestListener {
                        override fun onResponse(response: JSONObject) { // do anything with response
                            if (response.optString("response").toString() == "success") {
                                Toast.makeText(applicationContext, "Data berhasil dihapus",
                                        Toast.LENGTH_LONG).show()
                                KasAdapter()
                            } else {
                                Toast.makeText(applicationContext, "Gagal",
                                        Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onError(error: ANError) { // handle error
                        }
                    })
        }
        builder.setNegativeButton(
                "No"
        ) { dialog, id -> dialog.dismiss() }
        builder.show()
    }

    companion object {
        @JvmField
        var text_filter: TextView? = null
        var LINK: String? = null
        @JvmField
        var transaksi_id: String? = null
        @JvmField
        var status: String? = null
        @JvmField
        var jumlah: String? = null
        @JvmField
        var keterangan: String? = null
        @JvmField
        var tanggal: String? = null
        @JvmField
        var tanggal2: String? = null
        @JvmField
        var tgl_dari: String? = null
        @JvmField
        var tgl_ke: String? = null
        @JvmField
        var filter = false
    }
}