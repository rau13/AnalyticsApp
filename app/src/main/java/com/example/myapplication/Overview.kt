package com.example.myapplication

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.icu.text.CompactDecimalFormat
import android.icu.util.Calendar
import android.os.*
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager.widget.ViewPager
import com.example.myapplication.adapters.CustomMarkerView
import com.example.myapplication.adapters.FixedSpeedScroller
import com.example.myapplication.adapters.OverViewAdapter
import com.example.myapplication.adapters.OverViewModel
import com.example.myapplication.responses.BTRXLeads
import com.example.myapplication.responses.ResponseBTRXDeals
import com.example.myapplication.responses.ResponseFB
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.firebase.database.*
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_overview.*
import java.lang.Runnable
import java.lang.reflect.Field
import java.net.URL
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt


class Overview : AppCompatActivity() {
    private val today_date = arrayListOf<String?>()
    private var cpm:ArrayList<String?> = arrayListOf()
    private var ctr = arrayListOf<String?>()
    private var spend = arrayListOf<String?>()
    private var clicks = arrayListOf<String?>()
    private var frequency = arrayListOf<String?>()
    private var impression = arrayListOf<String?>()
    private var ohvat = arrayListOf<String?>()
    private lateinit var myModelList: ArrayList<OverViewModel>
    private lateinit var myAdapter: OverViewAdapter
    private lateinit var viewpager: ViewPager
    private lateinit var database: DatabaseReference
    private var dates:ArrayList<String> = arrayListOf()
    private var endDateRange:Long? = null
    private var daysCount:Int? = null
    private var childrensDeals: ArrayList<String> = arrayListOf()
    private var totalDeals: ArrayList<String> = arrayListOf()
    private var valuesDeals: ArrayList<Int> = arrayListOf()
    private var childrens: ArrayList<String> = arrayListOf()
    private var values: ArrayList<Int> = arrayListOf()
    private var total:ArrayList<String> = arrayListOf()
    private var ohvat_fb:ArrayList<Any?> = arrayListOf()
    private var pokazi_fb:ArrayList<Any?> = arrayListOf()
    private var clicks_fb:ArrayList<Any?> = arrayListOf()
    private var Ctr_fb:ArrayList<Any?> = arrayListOf()
    private var fb1000:ArrayList<Any?> = arrayListOf()
    private var sumspendfb:ArrayList<Any?> = arrayListOf()
    private var sumspendtg:ArrayList<Any?> = arrayListOf()
    private var chastotafb:ArrayList<Any?> = arrayListOf()
    private var email:String? = null
    private var nextLeads:Int = 354
    private var nextDeals:Int = 1574
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_overview)
        init()
   }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun init(){
       supportActionBar!!.hide()
       val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
       StrictMode.setThreadPolicy(policy)
       this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
       myModelList = arrayListOf()
       viewpager = findViewById(R.id.Cards)
       viewpager.setOffscreenPageLimit(10)
        database = FirebaseDatabase.getInstance().getReference("Dates")
       val extras = intent.extras
       if (extras != null) {
           email = extras.getString("email")
           //The key argument here must match that used in the other activity
       }
       if (!email.equals("rauan@gmail.com")){
           textView20.visibility = View.GONE
       }
       val mscroller: Field = viewpager.javaClass.getDeclaredField("mScroller")
       mscroller.isAccessible = true
       val scroller = FixedSpeedScroller(viewpager.context)
       mscroller.set(viewpager, scroller)
        getFB()
        val calendarForFirstOpen = Calendar.getInstance()

        calendarForFirstOpen.set(Calendar.YEAR, today_date[0].toString().split("-")[0].toInt())
        calendarForFirstOpen.set(Calendar.MONTH, today_date[0].toString().split("-")[1].toInt()-1)
        calendarForFirstOpen.set(Calendar.DAY_OF_MONTH,today_date[0].toString().split("-")[2].toInt()+1)
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val dateRange = sdf.format(calendarForFirstOpen.time)

        dates.add(today_date[0].toString())
        dates.add(dateRange.toString())
       getFireBaseData()

       Log.d("Mylog","$ohvat_fb")
       myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toInt() }.sum(),25000*daysCount!!))
       myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toInt() }.sum(),30000*daysCount!!))
       myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toInt() }.sum(), 800*daysCount!!))
       myModelList.add(OverViewModel("Частота", chastotafb.map { it.toString().toInt() }.average().toInt(), 2*daysCount!!))
       myModelList.add(OverViewModel("CTR", Ctr_fb.map { it.toString().toInt() }.average().toInt(),3*daysCount!!))
       myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toInt() }.sum(),4*daysCount!!))
       myModelList.add(OverViewModel("Сумма затрат",sumspendfb.map { it.toString().toInt() }.sum(),150*daysCount!!))
       myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toInt() }.sum(),70129*daysCount!!))


       myAdapter = OverViewAdapter(this, myModelList)
       viewpager.adapter = myAdapter
       viewpager.setPadding(100, 0, 100, 0)

       

       daysCount = dates.count()
       textViewDataOverview.text = "${dates[0]}\n${dates[1]}"
       DataTextViewAll.text = "${dates[0]}\n${dates[1]}"
       val calendar = Calendar.getInstance()
       calendar.set(Calendar.YEAR, today_date[0]!!.split("-")[0].toInt())
       calendar.set(Calendar.MONTH, today_date[0]!!.split("-")[1].toInt()-1)
       calendar.set(Calendar.DAY_OF_MONTH,today_date[0]!!.split("-")[2].toInt())
       endDateRange = calendar.timeInMillis

      
       getBTRXDeals()
       getBTRXLeads()

        setFBFirebase()


       Handler().postDelayed({

           loadcards()

       },4000)

       viewpager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
           override fun onPageScrolled(
               position: Int,
               positionOffset: Float,
               positionOffsetPixels: Int
           ) {

           }

           override fun onPageSelected(position: Int) {
               val value = myModelList[position].value
               when(value){
                   "Охват" -> chart(ohvat_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Показы" -> chart(pokazi_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Клики" -> chart(clicks_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Частота" -> chart(chastotafb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "CTR" -> chart(Ctr_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Стоимость 1000 охватов" -> chart(fb1000.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Сумма затрат" -> chart(sumspendfb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
                   "Сумма затрат в тенге" -> chart(sumspendtg.map { it.toString().toFloat().toInt()} as ArrayList<Int>)
                   "Сделки" -> chart(valuesDeals)
                   "Лиды" -> chart(values)
               }

           }

           override fun onPageScrollStateChanged(state: Int) {

           }
       })
        

   }
    private fun loadcards(){
        myModelList.clear()
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map { it.toString().toFloat() }.average().toInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("CTR", Ctr_fb.map { it.toString().toFloat() }.average().toInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(), 500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        textView13_OverView.text = "Сделки:\n${totalDeals.count()}"
        textView14_OverView.text = "Лиды:\n${total.count()}"

        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        percent_Fact()
    }
    private fun getBTRXDeals(){
        val database = FirebaseDatabase.getInstance().getReference("BitrixDeals")

        database.get().addOnSuccessListener {
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dates.forEach{
                        for (dataKeys in dataSnapshot.child(it).children){
                            childrensDeals.add(dataKeys.key.toString())

                            totalDeals.add(dataKeys.key.toString())
                            Log.d("Mylog","$childrens")
                        }




                        valuesDeals.add(childrensDeals.count())
                        childrensDeals.clear()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Mylog", "onCancelled", databaseError.toException())
                }
            })
        }
        Handler().postDelayed({loadcards()},1500)

    }
    private fun getBTRXLeads(){
        val database = FirebaseDatabase.getInstance().getReference("Bitrix")
        database.get().addOnSuccessListener {
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    dates.forEach{

                        for (dataKeys in dataSnapshot.child(it).children){
                            childrens.add(dataKeys.key.toString())
                            total.add(dataKeys.key.toString())
                            Log.d("Mylog","$childrens")
                        }
                        values.add(childrens.count())
                        childrens.clear()
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Log.w("Mylog", "onCancelled", databaseError.toException())
                }
            })
        }
        Handler().postDelayed({loadcards()},1500)


    }
    @SuppressLint("SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getFireBaseData(){
        Log.d("Mylog","$dates")
        daysCount = dates.count()
        dates.forEach{
            Log.d("Mylog","$it")
            database.child(it).get().addOnSuccessListener {

                if (it.exists()){
                    ohvat_fb.add(it.child("Охват").value)
                    pokazi_fb.add(it.child("Показы").value)
                    clicks_fb.add(it.child("Клики").value)
                    Ctr_fb.add(it.child("CTR").value)
                    fb1000.add(it.child("Стоимость 1000 охватов").value)
                    sumspendfb.add(it.child("Сумма затрат").value)
                    sumspendtg.add(it.child("Сумма затрат в тенге").value)
                    chastotafb.add(it.child("Частота").value)
                    textView3_OverView.text  = "Охват:\n${
                        CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(ohvat_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView4_OverView.text = "Показы:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(pokazi_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView5_OverView.text = "Клики:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(clicks_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView6_OverView.text = "Частота:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(chastotafb.toArray().map { it.toString().toFloat().roundToInt() }.average())}"
                    textView8_OverView.text = "CTR:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(Ctr_fb.toArray().map { it.toString().toFloat().roundToInt() }.average())}"
                    textView10_OverView.text = "Стоимость 1000 охватов:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(fb1000.toArray().map{it.toString().toFloat()}.sum().roundToInt())}"
                    textView11_OverView.text = "Сумма затрат в долларах:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(sumspendfb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}$"
                    textView12_OverView.text = "Сумма затрат в тенге:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(sumspendtg.toArray().map{it.toString().toFloat()}.sum().roundToInt())}"
                    textView3.text =  "Охват:\n${
                        CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(ohvat_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView4.text = "Показы:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(pokazi_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView5.text = "Клики:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(clicks_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView6.text = "Сумма затрат в долларах:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(sumspendfb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}$"
                    loadcards()
                    Handler().postDelayed({chart(ohvat_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)},1200)
                    Log.d("Mylog","Охват: $ohvat_fb")

                } else{
                    textView3_OverView.text  = "Охват:\n${
                        CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(ohvat_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView4_OverView.text = "Показы:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(pokazi_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView5_OverView.text = "Клики:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(clicks_fb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView6_OverView.text = "Частота:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(chastotafb.toArray().map { it.toString().toFloat().roundToInt() }.average())}"
                    textView8_OverView.text = "CTR:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(Ctr_fb.toArray().map { it.toString().toFloat().roundToInt() }.average())}"
                    textView10_OverView.text = "Стоимость 1000 охватов:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(fb1000.toArray().map{it.toString().toFloat()}.sum().roundToInt())}"
                    textView11_OverView.text = "Сумма затрат в долларах:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(sumspendfb.toArray().map { it.toString().toFloat() }.sum().roundToInt())}"
                    textView12_OverView.text = "Сумма затрат в тенге:\n${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(sumspendtg.toArray().map{it.toString().toFloat()}.sum().roundToInt())}"
                }
            }
        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
     fun selectDate(view: View){
        dateRangePicker()
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun dateRangePicker(){
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
            total.clear()
            dates.clear()
            childrens.clear()
            values.clear()
            totalDeals.clear()
            childrensDeals.clear()
            valuesDeals.clear()
            ohvat_fb.clear()
            pokazi_fb.clear()
            chastotafb.clear()
            clicks_fb.clear()
            fb1000.clear()
            Ctr_fb.clear()
            sumspendtg.clear()
            sumspendfb.clear()
            val startDate = dateSelected.first
            val endDate = dateSelected.second
            Cards.isEnabled = false
            Cards.visibility = View.GONE
            
            updateLabel(startDate, endDate!!.toLong())
            LoadingBar.visibility = View.VISIBLE

           var i = LoadingBar.progress
            Thread(Runnable {
                // this loop will run until the value of i becomes 99
                Looper.prepare()
                while (i < 100) {
                    i += 1
                    // Update the progress bar and display the current value
                    Handler().post(Runnable {
                        LoadingBar.progress = i
                        // setting current progress to the textview

                    })
                    try {
                        Thread.sleep(100)
                    } catch (e: InterruptedException) {
                        e.printStackTrace()
                    }
                }

                // setting the visibility of the progressbar to invisible
                // or you can use View.GONE instead of invisible
                // View.GONE will remove the progressbar

            }).start()

            Handler().postDelayed({
                LoadingBar.visibility = View.GONE
                Cards.visibility = View.VISIBLE},1000)


        }
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateLabel(timeStart:Long,timeEnd:Long?) {
        val myFormat = "yyyy-MM-dd"
        val date1 = Date(timeStart)
        val date2 = Date(timeEnd!!.toLong())
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val dateVoronka = findViewById<TextView>(R.id.textViewDataVoronka)
        getDatesBetween(sdf.format(date1),sdf.format(date2))
        getBTRXLeads()
        getBTRXDeals()
        getFireBaseData()
        Handler().postDelayed({percent_Fact()},1200)
        Handler().postDelayed({chart(ohvat_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)},1200)
        textViewDataOverview.text = "${sdf.format(date1)}\n${sdf.format(date2)}"
        DataTextViewAll.text = "${sdf.format(date1)}\n${sdf.format(date2)}"
        dateVoronka.text = "${sdf.format(date1)}\n${sdf.format(date2)}"
        days30Stat.setTextColor(resources.getColor(R.color.white))
        days3Stat.setTextColor(resources.getColor(R.color.white))
        days7Stat.setTextColor(resources.getColor(R.color.white))



    }
    private fun convertLongToTime(time: Long?): String {
        val date = Date(time!!.toLong())
        val format = SimpleDateFormat(
            "yyyy-MM-dd",
            Locale.getDefault())

        return format.format(date)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDatesBetween(dateString1:String, dateString2:String):List<String> {

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


    private fun chart(values:ArrayList<Int>){

        Log.d("Mylog","$dates")
        try{
            Handler().postDelayed({

                val dataChart = findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.LineChart)

                dates.map { it.toString() }
                Log.d("Mylog","$dates")
                val lineentry = ArrayList<Entry>()

                for(i in 0 until values.size){

                    lineentry.add(Entry(values[i]!!.toString().toFloat(),i))
                }
                val linedataset = LineDataSet(lineentry, "")
                linedataset.color = resources.getColor(R.color.light_green)
                linedataset.setDrawCubic(true)

                linedataset.notifyDataSetChanged()

                val data = LineData(dates, linedataset)
                dataChart.setNoDataText("")
                dataChart.data = data
                dataChart.invalidate()
                val xAxis = dataChart.xAxis
                xAxis.position = XAxis.XAxisPosition.BOTTOM
                //xAxis.isEnabled = false
                xAxis.textColor = resources.getColor(R.color.white)
                xAxis.textSize = 6.5f
                xAxis.setDrawAxisLine(false)
                dataChart.setDescription(null)
                val yAxisRight = dataChart.axisRight
                yAxisRight.isEnabled = false
                val yAxisLeft = dataChart.axisLeft
                yAxisLeft.isEnabled = true
                yAxisLeft.textColor = resources.getColor(R.color.white)
                yAxisLeft.granularity = 3f
                linedataset.lineWidth = 3f
                dataChart.isScaleYEnabled = false
                dataChart.getAxisLeft().setDrawGridLines(false);
                dataChart.getXAxis().setDrawGridLines(false)
                linedataset.valueTextSize = 8f
                linedataset.setDrawValues(false)
                dataChart.setTouchEnabled(true);
                //dataChart.zoom(2f,1f,0f,0f)
                linedataset.isHighlightEnabled = true
                linedataset.setDrawHighlightIndicators(false)
                val mv = CustomMarkerView(
                    this,
                    R.layout.tvcontent
                )
                dataChart.setMarkerView(mv)
                dataChart.animateXY(250,250)
            },1150)
        }catch (e:Exception){
            Log.d("Mylog","$e, chart")
        }

    }
    @RequiresApi(Build.VERSION_CODES.N)
    private fun getDaysAgo(daysAgo: Int, day: Int, month: Int, year: Int): String? {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.YEAR, year)
        calendar.set(Calendar.MONTH, month-1)
        calendar.set(Calendar.DAY_OF_MONTH,day)
        calendar.add(Calendar.DAY_OF_MONTH, -daysAgo)
        Log.d("Mylog","${calendar.time}")
        val myFormat = "yyyy-MM-dd"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        val dateRange = sdf.format(calendar.time)
        return dateRange
    }
    @RequiresApi(Build.VERSION_CODES.N)
     fun days7(view:View){
        val dataChart = findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.LineChart)
        days7Stat.isEnabled = false
        days3Stat.isEnabled = false
        days30Stat.isEnabled = false
        days7Stat.setTextColor(resources.getColor(R.color.light_green))
        days3Stat.setTextColor(resources.getColor(R.color.white))
        days30Stat.setTextColor(resources.getColor(R.color.white))
        dataChart.clear()
        total.clear()
        dates.clear()
        childrens.clear()
        values.clear()
        totalDeals.clear()
        childrensDeals.clear()
        valuesDeals.clear()
        ohvat_fb.clear()
        pokazi_fb.clear()
        clicks_fb.clear()
        chastotafb.clear()
        Ctr_fb.clear()
        fb1000.clear()
        sumspendfb.clear()
        sumspendtg.clear()
        val date = convertLongToTime(endDateRange).split("-")
        val dateRange = getDaysAgo(7,date[2].toInt(),date[1].toInt(),date[0].toInt())
        getDatesBetween(dateRange.toString(),convertLongToTime(endDateRange))
        getFireBaseData()
        getBTRXLeads()
        getBTRXDeals()
        chart(ohvat_fb.map{ it.toString().toFloat().toInt() } as ArrayList<Int>)

        textViewDataOverview.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        DataTextViewAll.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        Handler().postDelayed({

            // This method will be executed once the timer is over
            days7Stat.setEnabled(true);
            days3Stat.isEnabled = true
            days30Stat.isEnabled = true


        },3500)

    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun days3(view:View){

        val dataChart = findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.LineChart)
        days3Stat.isEnabled = false
        days7Stat.isEnabled = false
        days30Stat.isEnabled = false
        days3Stat.setTextColor(resources.getColor(R.color.light_green))
        days7Stat.setTextColor(resources.getColor(R.color.white))
        days30Stat.setTextColor(resources.getColor(R.color.white))
        dataChart.clear()
        total.clear()
        dates.clear()
        childrens.clear()
        values.clear()
        totalDeals.clear()
        childrensDeals.clear()
        valuesDeals.clear()
        ohvat_fb.clear()
        pokazi_fb.clear()
        clicks_fb.clear()
        chastotafb.clear()
        Ctr_fb.clear()
        fb1000.clear()
        sumspendfb.clear()
        sumspendtg.clear()
        val date = convertLongToTime(endDateRange).split("-")
        val dateRange = getDaysAgo(3,date[2].toInt(),date[1].toInt(),date[0].toInt())
        getDatesBetween(dateRange.toString(),convertLongToTime(endDateRange))
        getFireBaseData()
        getBTRXLeads()
        getBTRXDeals()
        chart(ohvat_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)


        textViewDataOverview.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        DataTextViewAll.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        Handler().postDelayed({

            // This method will be executed once the timer is over
            days3Stat.setEnabled(true);
            days7Stat.isEnabled = true
            days30Stat.isEnabled = true
            //textButton.setTextColor(resources.getColor(R.color.white))

        },3500)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun days30(view:View){
        val dataChart = findViewById<com.github.mikephil.charting.charts.LineChart>(R.id.LineChart)
        days30Stat.isEnabled = false
        days7Stat.isEnabled = false
        days3Stat.isEnabled = false
        days30Stat.setTextColor(resources.getColor(R.color.light_green))
        days3Stat.setTextColor(resources.getColor(R.color.white))
        days7Stat.setTextColor(resources.getColor(R.color.white))
        dataChart.clear()
        total.clear()
        dates.clear()
        childrens.clear()
        values.clear()
        totalDeals.clear()
        childrensDeals.clear()
        valuesDeals.clear()
        ohvat_fb.clear()
        pokazi_fb.clear()
        clicks_fb.clear()
        chastotafb.clear()
        Ctr_fb.clear()
        fb1000.clear()
        sumspendfb.clear()
        sumspendtg.clear()
        val date = convertLongToTime(endDateRange).split("-")
        val dateRange = getDaysAgo(date[2].toInt()-1,date[2].toInt(),date[1].toInt(),date[0].toInt())
        getDatesBetween(dateRange.toString(),convertLongToTime(endDateRange))

        getFireBaseData()
        getBTRXLeads()
        getBTRXDeals()
        chart(ohvat_fb.map{ it.toString().toFloat().toInt() } as ArrayList<Int>)
        textViewDataOverview.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        DataTextViewAll.text = "${dateRange.toString()}\n${convertLongToTime(endDateRange)}"
        Handler().postDelayed({

            // This method will be executed once the timer is over
            days30Stat.setEnabled(true);
            days7Stat.isEnabled = true
            days3Stat.isEnabled = true


        },3800)
    }
     fun viewAll(view: View){
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)

        view.visibility = View.VISIBLE
    }
    fun viewBack(view:View){
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
    }
    private fun percent_Fact(){
        var percentFactOhvat = (ohvat_fb.map { it.toString().toFloat() }.sum()*100).roundToInt()/(25000*daysCount!!)
        var percentFactPokazi = (pokazi_fb.map { it.toString().toFloat() }.sum()*100).roundToInt()/(30000*daysCount!!)
        var percentFactClicks = (clicks_fb.map { it.toString().toFloat() }.sum()*100).roundToInt()/(800*daysCount!!)
        var percentFactChastota = (chastotafb.map { it.toString().toFloat() }.sum()*100).roundToInt()/(2*daysCount!!)
        var percentFactCtr = (Ctr_fb.map { it.toString().toFloat() }.sum()*100).roundToInt()/(3*daysCount!!)
        var percentFactCpm = (fb1000.map { it.toString().toFloat() }.sum()*100).roundToInt()/(4*daysCount!!)
        var percentFactSpend = (sumspendfb.map { it.toString().toFloat() }.sum().roundToInt()*100)/(150*daysCount!!)
        var percentFactSpendTg = (sumspendtg.map { it.toString().toFloat() }.sum().roundToInt()*100)/(70129*daysCount!!)
        var percentFactDeals = (totalDeals.count()*100)/(500*daysCount!!)
        var percentFactLeads = (total.count()*100)/(500*daysCount!!)
        Log.d("Mylog","$percentFactOhvat")
        progressBar2_OverView.progress = percentFactOhvat.toInt()
        progressBar3_OverView.progress = percentFactPokazi.toInt()
        progressBar5_OverView.progress = percentFactClicks.toInt()
        progressBar6_OverView.progress = percentFactChastota.toInt()
        progressBar7_OverView.progress = percentFactCtr.toInt()
        progressBar8_OverView.progress = percentFactCpm.toInt()
        progressBar9_OverView.progress = percentFactSpend.toInt()
        progressBar10_OverView.progress = percentFactSpendTg.toInt()
        progressBar11_OverView.progress = percentFactDeals
        progressBar12_OverView.progress = percentFactLeads
        progressBar2.progress = percentFactOhvat
        progressBar3.progress = percentFactPokazi
        progressBar4.progress = percentFactClicks
        progressBar5.progress = percentFactSpend
    }
    @RequiresApi(Build.VERSION_CODES.N)
     fun ohvatCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))


        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(ohvat_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
     fun pokaziCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(pokazi_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
     fun clicksCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(clicks_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
     fun chastotaCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(chastotafb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
   fun ctrCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(Ctr_fb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
     fun fb1000Card(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(fb1000.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
    fun sumspendCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(sumspendfb.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
    fun sumspendtg(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(sumspendtg.map { it.toString().toFloat().toInt() } as ArrayList<Int>)
    }
   fun dealsCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(valuesDeals)
    }
   fun leadsCard(view:View){
        myModelList.clear()
        myModelList.add(OverViewModel("Лиды", total.count(), 500*daysCount!!))
        myModelList.add(OverViewModel("Сделки", totalDeals.count(),500*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в тенге",sumspendtg.map { it.toString().toFloat() }.sum().roundToInt(),70129*daysCount!!))
        myModelList.add(OverViewModel("Сумма затрат в долларах",sumspendfb.map { it.toString().toFloat() }.sum().roundToInt(),150*daysCount!!))
        myModelList.add(OverViewModel("Стоимость 1000 охватов",fb1000.map { it.toString().toFloat() }.sum().roundToInt(),4*daysCount!!))
        myModelList.add(OverViewModel("CTR",Ctr_fb.map { it.toString().toFloat() }.sum().roundToInt(),3*daysCount!!))
        myModelList.add(OverViewModel("Частота", chastotafb.map{ it.toString().toFloat() }.sum().roundToInt(), 2*daysCount!!))
        myModelList.add(OverViewModel("Клики", clicks_fb.map { it.toString().toFloat() }.sum().roundToInt(), 800*daysCount!!))
        myModelList.add(OverViewModel("Показы",pokazi_fb.map { it.toString().toFloat() }.sum().roundToInt(),30000*daysCount!!))
        myModelList.add(OverViewModel("Охват", ohvat_fb.map { it.toString().toFloat() }.sum().roundToInt(),25000*daysCount!!))
        myAdapter = OverViewAdapter(this, myModelList)
        viewpager.adapter = myAdapter
        viewpager.setPadding(100, 0, 100, 0)
        val view = findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAll)
        view.visibility = View.GONE
        chart(values)
    }
    fun EditDB(view:View){
        val intent = Intent(this, AddForm::class.java)
        startActivity(intent)
    }


    //set data from FB to Firebase(data range)
    private fun setData(){
        for(i in 10..16){
            var response = URL("Facebook Rest API")
            var data = gson.fromJson(response, ResponseFB::class.java)
            var today_date1 = arrayListOf<String?>()
            var cpm1 = arrayListOf<String?>()
            var ctr1 = arrayListOf<String?>()
            var spend1 = arrayListOf<String?>()
            var clicks1 = arrayListOf<String?>()
            var frequency1 = arrayListOf<String?>()
            var impression1 = arrayListOf<String?>()
            var ohvat1 = arrayListOf<String?>()
            for(i in 0 until data.data!!.size){
                today_date1.add(data.data!!.get(i)?.dateStop)
                cpm1.add( data.data!!.get(i)?.cpm)
                ctr1.add(data.data!!.get(i)?.ctr)
                spend1.add(data.data!!.get(i)?.spend)
                clicks1.add(data.data!!.get(i)?.clicks)
                frequency1.add(data.data!!.get(i)?.frequency)
                impression1.add(data.data!!.get(i)?.impressions)
                ohvat1.add(data.data!!.get(i)?.reach)
                database.child(today_date1[0].toString()).get().addOnSuccessListener {
                    if (!it.exists()) {
                        database.child(today_date1[0].toString()).child("Охват")
                            .setValue(ohvat1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Показы")
                            .setValue(impression1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Клики")
                            .setValue(clicks1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Частота")
                            .setValue(frequency1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("CTR")
                            .setValue(ctr1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Стоимость 1000 охватов")
                            .setValue(cpm1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Сумма затрат")
                            .setValue(spend1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Сумма затрат в тенге").setValue(spend1.map { it!!.toDouble()*470f.toDouble()  }.sum().toString())
                        database.child(today_date1[0].toString()).child("Курс валюты").setValue("470").toString()

                    } else {
                        database.child(today_date1[0].toString()).child("Охват")
                            .setValue(ohvat1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Показы")
                            .setValue(impression1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Клики")
                            .setValue(clicks1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Частота")
                            .setValue(frequency1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("CTR")
                            .setValue(ctr1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Стоимость 1000 охватов")
                            .setValue(cpm1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Сумма затрат")
                            .setValue(spend1.map { it!!.toFloat() }.sum().toString())
                        database.child(today_date1[0].toString()).child("Сумма затрат в тенге").setValue(spend1.map { it!!.toDouble()*470f.toDouble()  }.sum().toString())
                        database.child(today_date1[0].toString()).child("Курс валюты").setValue("434").toString()

                    }

                }

            }
            Log.d("Mylog","$today_date1, $cpm1, $ctr1, $spend1, $clicks1, $frequency1, $impression1, $ohvat1")
        }
    }
    private fun setFBFirebase(){
        //Date
        database.child(today_date[0].toString()).get().addOnSuccessListener {
            if(!it.exists()){
                database.child(today_date[0].toString()).child("Охват").setValue(ohvat.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Показы").setValue(impression.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Клики").setValue(clicks.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Частота").setValue(frequency.map{it!!.toFloat()}.sum().toString())
                database.child(today_date[0].toString()).child("CTR").setValue(ctr.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Стоимость 1000 охватов").setValue(cpm.map{it!!.toFloat()}.sum().toString())
                database.child(today_date[0].toString()).child("Сумма затрат").setValue(spend.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Сумма затрат в тенге").setValue(spend.map { it!!.toDouble()*434!!.toDouble()  }.sum().toString())
                database.child(today_date[0].toString()).child("Курс валюты").setValue(434).toString()
            }else{
                database.child(today_date[0].toString()).child("Охват").setValue(ohvat.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Показы").setValue(impression.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Клики").setValue(clicks.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Частота").setValue(frequency.map{it!!.toFloat()}.sum().toString())
                database.child(today_date[0].toString()).child("CTR").setValue(ctr.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Стоимость 1000 охватов").setValue(cpm.map{it!!.toFloat()}.sum().toString())
                database.child(today_date[0].toString()).child("Сумма затрат").setValue(spend.map { it!!.toFloat() }.sum().toString())
                database.child(today_date[0].toString()).child("Сумма затрат в тенге").setValue(spend.map { it!!.toDouble()*434!!.toDouble()  }.sum().toString())
                database.child(today_date[0].toString()).child("Курс валюты").setValue(434).toString()
            }

        }
    }
    //get data from Facebook (today)
    private fun getFB(){
        var response = URL("Facebook Rest API").readText()
        var gson = Gson()
        var data = gson.fromJson(response, ResponseFB::class.java)
        for(i in 0 until data.data!!.size){
            today_date.add(data.data!!.get(i)?.dateStart)
            cpm.add( data.data!!.get(i)?.cpm)
            ctr.add(data.data!!.get(i)?.ctr)
            spend.add(data.data!!.get(i)?.spend)
            clicks.add(data.data!!.get(i)?.clicks)
            frequency.add(data.data!!.get(i)?.frequency)
            impression.add(data.data!!.get(i)?.impressions)
            ohvat.add(data.data!!.get(i)?.reach)
            Log.d("Mylog","$today_date")
        }
    }
    fun Voronka(view:View){
        viewAll.visibility = View.GONE
        Voronka.visibility = View.VISIBLE
    }
    fun Back(view:View){
        Voronka.visibility = View.GONE
        viewAll.visibility = View.VISIBLE
    }
}




