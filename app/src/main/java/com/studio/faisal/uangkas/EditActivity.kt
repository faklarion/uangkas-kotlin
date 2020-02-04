package com.studio.faisal.uangkas

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*
import com.andexert.library.RippleView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.studio.faisal.uangkas.helper.Config
import com.studio.faisal.uangkas.helper.CurrentDate
import org.json.JSONObject
import java.text.DecimalFormat
import java.text.NumberFormat

class EditActivity : AppCompatActivity() {
    var M = MainActivity()
    var radio_status: RadioGroup? = null
    var radio_masuk: RadioButton? = null
    var radio_keluar: RadioButton? = null
    var edit_jumlah: EditText? = null
    var edit_keterangan: EditText? = null
    var edit_tanggal: EditText? = null
    var btn_simpan: Button? = null
    var rip_simpan: RippleView? = null
    var status: String? = null
    var tanggal: String? = null
    var datePickerDialog: DatePickerDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        status = ""
        tanggal = ""
        radio_status = findViewById<View>(R.id.radio_status) as RadioGroup
        radio_masuk = findViewById<View>(R.id.radio_masuk) as RadioButton
        radio_keluar = findViewById<View>(R.id.radio_keluar) as RadioButton
        edit_jumlah = findViewById<View>(R.id.edit_jumlah) as EditText
        edit_keterangan = findViewById<View>(R.id.edit_keterangan) as EditText
        edit_tanggal = findViewById<View>(R.id.edit_tanggal) as EditText
        btn_simpan = findViewById<View>(R.id.btn_simpan) as Button
        rip_simpan = findViewById<View>(R.id.rip_simpan) as RippleView
        Detail()
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Edit"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    private fun Detail() {
        status = MainActivity.status
        when (MainActivity.status) {
            "MASUK" -> radio_masuk!!.isChecked = true
            "KELUAR" -> radio_keluar!!.isChecked = true
        }
        radio_status!!.setOnCheckedChangeListener { group, checkedId ->
            when (checkedId) {
                R.id.radio_masuk -> status = "MASUK"
                R.id.radio_keluar -> status = "KELUAR"
            }
            Log.d("Log status", status)
        }
        edit_jumlah!!.setText(MainActivity.jumlah)
        edit_keterangan!!.setText(MainActivity.keterangan)
        tanggal = MainActivity.tanggal2
        edit_tanggal!!.setText(MainActivity.tanggal)
        edit_tanggal!!.setOnClickListener {
            datePickerDialog = DatePickerDialog(this@EditActivity, OnDateSetListener { view, year, month_of_year, day_of_month ->
                // set day of month , month and year value in the edit text
                val numberFormat: NumberFormat = DecimalFormat("00")
                tanggal = year.toString() + "-" + numberFormat.format((month_of_year + 1).toLong()) + "-" +
                        numberFormat.format(day_of_month.toLong())
                edit_tanggal!!.setText(numberFormat.format(day_of_month.toLong()) + "/" +
                        numberFormat.format((month_of_year + 1).toLong()) +
                        "/" + year)
            }, CurrentDate.year, CurrentDate.month, CurrentDate.day)
            datePickerDialog!!.show()
        }
        rip_simpan!!.setOnRippleCompleteListener {
            if (status == "" || edit_jumlah!!.text.toString() == "") {
                Toast.makeText(applicationContext, "Isi data dengan benar",
                        Toast.LENGTH_LONG).show()
            } else {
                AndroidNetworking.post(Config.host + "update.php")
                        .addBodyParameter("transaksi_id", MainActivity.transaksi_id)
                        .addBodyParameter("status", status)
                        .addBodyParameter("jumlah", edit_jumlah!!.text.toString())
                        .addBodyParameter("keterangan", edit_keterangan!!.text.toString())
                        .addBodyParameter("tanggal", tanggal)
                        .setPriority(Priority.MEDIUM)
                        .build()
                        .getAsJSONObject(object : JSONObjectRequestListener {
                            override fun onResponse(response: JSONObject) { // do anything with response
                                Log.d("response", response.toString())
                                if (response.optString("response") == "success") {
                                    Toast.makeText(applicationContext, "Perubahan berhasil disimpan",
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
    }
}