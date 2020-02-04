package com.studio.faisal.uangkas

import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.andexert.library.RippleView
import com.studio.faisal.uangkas.helper.CurrentDate
import java.text.DecimalFormat
import java.text.NumberFormat

class FilterActivity : AppCompatActivity() {
    var M = MainActivity()
    var edit_dari: EditText? = null
    var edit_ke: EditText? = null
    var btn_filter: Button? = null
    var rip_filter: RippleView? = null
    var datePickerDialog: DatePickerDialog? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)
        edit_dari = findViewById<View>(R.id.edit_dari) as EditText
        edit_ke = findViewById<View>(R.id.edit_ke) as EditText
        btn_filter = findViewById<View>(R.id.btn_filter) as Button
        rip_filter = findViewById<View>(R.id.rip_filter) as RippleView
        edit_dari!!.setOnClickListener {
            datePickerDialog = DatePickerDialog(this@FilterActivity, OnDateSetListener { view, year, month_of_year, day_of_month ->
                val numberFormat: NumberFormat = DecimalFormat("00")
                MainActivity.tgl_dari = year.toString() + "-" + numberFormat.format(month_of_year + 1.toLong()) + "-" +
                        numberFormat.format(day_of_month.toLong())
                edit_dari!!.setText(numberFormat.format(day_of_month.toLong()) + "/" +
                        numberFormat.format(month_of_year + 1.toLong()) +
                        "/" + year)
            }, CurrentDate.year, CurrentDate.month, CurrentDate.day)
            datePickerDialog!!.show()
        }
        edit_ke!!.setOnClickListener {
            datePickerDialog = DatePickerDialog(this@FilterActivity, OnDateSetListener { view, year, month_of_year, day_of_month ->
                val numberFormat: NumberFormat = DecimalFormat("00")
                MainActivity.tgl_ke = year.toString() + "-" + numberFormat.format(month_of_year + 1.toLong()) + "-" +
                        numberFormat.format(day_of_month.toLong())
                edit_ke!!.setText(numberFormat.format(day_of_month.toLong()) + "/" +
                        numberFormat.format(month_of_year + 1.toLong()) +
                        "/" + year)
            }, CurrentDate.year, CurrentDate.month, CurrentDate.day)
            datePickerDialog!!.show()
        }
        rip_filter!!.setOnRippleCompleteListener {
            if (edit_dari!!.text.toString() == "" || edit_ke!!.text.toString() == "") {
                Toast.makeText(applicationContext, "Isi data dengan benar",
                        Toast.LENGTH_LONG).show()
            } else {
                MainActivity.filter = true
                MainActivity.text_filter!!.text = edit_dari!!.text.toString() + " - " +
                        edit_ke!!.text.toString()
                MainActivity.text_filter!!.visibility = View.VISIBLE
                finish()
            }
        }
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.title = "Atur Tanggal"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}