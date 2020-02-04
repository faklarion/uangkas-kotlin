package com.studio.faisal.uangkas

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.RadioGroup
import android.widget.Toast
import com.andexert.library.RippleView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.studio.faisal.uangkas.helper.Config
import org.json.JSONObject

class AddActivity : AppCompatActivity() {
    var radio_status: RadioGroup? = null
    var edit_jumlah: EditText? = null
    var edit_keterangan: EditText? = null
    var btn_simpan: Button? = null
    var rip_simpan: RippleView? = null
    var status: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add)
        status = ""
        radio_status = findViewById<View>(R.id.radio_status) as RadioGroup
        edit_jumlah = findViewById<View>(R.id.edit_jumlah) as EditText
        edit_keterangan = findViewById<View>(R.id.edit_keterangan) as EditText
        btn_simpan = findViewById<View>(R.id.btn_simpan) as Button
        rip_simpan = findViewById<View>(R.id.rip_simpan) as RippleView
        radio_status!!.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.radio_masuk -> status = "MASUK"
                R.id.radio_keluar -> status = "KELUAR"
            }
            Log.d("Log status", status)
        }
        btn_simpan!!.setOnClickListener { }
        rip_simpan!!.setOnRippleCompleteListener {
            if (status == "" || edit_jumlah!!.text.toString() == "") {
                Toast.makeText(applicationContext, "Isi data dengan benar",
                        Toast.LENGTH_LONG).show()
            } else {
                _save()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Tambah"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun _save() {
        AndroidNetworking.post(Config.host + "add.php")
                .addBodyParameter("status", status)
                .addBodyParameter("jumlah", edit_jumlah!!.text.toString())
                .addBodyParameter("keterangan", edit_keterangan!!.text.toString())
                .setPriority(Priority.MEDIUM)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) { // do anything with response
                        Log.d("response", response.toString())
                        if (response.optString("response").toString() == "success") {
                            Toast.makeText(applicationContext, "Berhasil disimpan",
                                    Toast.LENGTH_LONG).show()
                            finish()
                        } else {
                            Toast.makeText(applicationContext, "Gagal",
                                    Toast.LENGTH_LONG).show()
                        }
                    }

                    override fun onError(error: ANError) { // handle error
                    }
                })
    }
}