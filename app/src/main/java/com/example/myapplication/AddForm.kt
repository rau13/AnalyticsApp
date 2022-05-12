package com.example.myapplication

import android.content.pm.ActivityInfo
import android.icu.util.Calendar
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_add_form.*
import kotlinx.android.synthetic.main.activity_overview.*
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class AddForm : AppCompatActivity() {
    private var dates:ArrayList<String> = arrayListOf()
    lateinit var database: DatabaseReference
    private var value:String? = null
    private var text:String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_form)
        supportActionBar!!.hide()
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        database = FirebaseDatabase.getInstance().getReference("Dates")
        val languages = resources.getStringArray(R.array.values)
        // create an array adapter and pass the required parameter
        // in our case pass the context, drop down layout , and array.
        val arrayAdapter = ArrayAdapter(this, R.layout.dropdown_menu, languages)
        // get reference to the autocomplete text view
        val autocompleteTV = findViewById<AutoCompleteTextView>(R.id.autoCompleteTextView)
        // set adapter to the autocomplete tv to the arrayAdapter
        autocompleteTV.setAdapter(arrayAdapter)
        autocompleteTV.setOnItemClickListener { adapterView, view, i, l ->
            val selectedValue = arrayAdapter.getItem(i)
            when(selectedValue){
                "Охват" -> {
                    value = "Охват"
                    Log.d("Mylog", "$value")
                }
                "Показы" -> {
                    value = "Показы"
                }
                "Клики" ->{
                    value = "Клики"
                }
                "Стоимость 1000 охватов" ->{
                    value = "Стоимость 1000 охватов"
                }
                "CTR" ->{
                    value = "CTR"
                }
                "Сумма затрат" ->{
                    value = "Сумма затрат"
                }
                "Cумма затрат в тенге"->{
                    value = "Сумма затрат в тенге"
                }
                "План" ->{
                    value = "План"
                }

            }
        }
        button2.setOnClickListener {
            text = valueText.text.toString()
            setFBFirebase()
            Toast.makeText(this, "БД изменена", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun selectDate(view: View){

        dateRangePicker()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun dateRangePicker(){
        val dateRangePicker =
            MaterialDatePicker
                .Builder.dateRangePicker().setTheme(com.google.android.material.R.style.ThemeOverlay_MaterialComponents_MaterialCalendar)
                .setTitleText("Выберите дату")
                .build()

        dateRangePicker.show(
            supportFragmentManager,
            "date_range_picker"
        )

        dateRangePicker.addOnPositiveButtonClickListener { dateSelected ->
            val startDate = dateSelected.first
            val endDate = dateSelected.second
            updateLabel(startDate, endDate)
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLabel(timeStart:Long,timeEnd:Long?) {
        val myFormat = "yyyy-MM-dd"
        val date1 = Date(timeStart)
        val date2 = Date(timeEnd!!.toLong())
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        getDatesBetween(sdf.format(date1), sdf.format(date2))
        textViewDataForm.text = "${sdf.format(date1)}\n${sdf.format(date2)}"
    }
    private fun convertLongToTime(time: Long?): String {
        val date = Date(time!!.toLong())
        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault())

        return format.format(date)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun getDatesBetween(dateString1:String, dateString2:String):List<String> {

        val input = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        var date1: Date? = null
        var date2: Date? = null
        try
        {
            date1 = input.parse(dateString1)
            date2 = input.parse(dateString2)
        }
        catch (e: ParseException) {
            e.printStackTrace()
        }
        val cal1 = Calendar.getInstance()
        cal1.time = date1
        val cal2 = Calendar.getInstance()
        cal2.time = date2
        while (!cal1.after(cal2))
        {
            val output = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dates.add(output.format(cal1.time))
            cal1.add(Calendar.DATE, 1)
        }
        return dates
    }
    fun setFBFirebase(){
        for(i in 0 until dates.size){
            database.child(dates[i].toString()).get().addOnSuccessListener {
            if(!it.exists()){
                database.child(dates[i]).child(value!!).setValue(text.toString().toFloat())

            }else{
                database.child(dates[i]).child(value!!).setValue(text.toString().toFloat())
            }

        }
        }

    }

}