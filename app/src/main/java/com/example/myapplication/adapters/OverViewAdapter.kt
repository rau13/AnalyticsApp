package com.example.myapplication.adapters

import android.content.Context
import android.icu.text.CompactDecimalFormat
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.viewpager.widget.PagerAdapter
import com.example.myapplication.R
import kotlinx.android.synthetic.main.card_item.view.*
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.math.roundToInt

class OverViewAdapter(private val context: Context, private val Model: ArrayList<OverViewModel>):PagerAdapter() {
    override fun getCount(): Int {
        return Model.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }
    @RequiresApi(Build.VERSION_CODES.N)
    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        var view = LayoutInflater.from(context).inflate(R.layout.card_item,container,false)
        val model = Model[position]
        val value = model.value
        val numbers = model.numbers
        val convertedNumber = CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(numbers)
        var percent_fact = 0
        if(numbers!=0){
            percent_fact  = (numbers!! * 100)/ model.plan!!
        }

        view.header_title.text = value
        view.header_values.text = "${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(numbers)}"
        view.progressBar.progress = percent_fact
        view.textView2.text = "$percent_fact%"

        view.setOnClickListener {
            if (numbers!=0){
                if(percent_fact<100){
                    view.textView2.text = "${CompactDecimalFormat.getInstance(Locale.getDefault(), CompactDecimalFormat.CompactStyle.SHORT).format(model.plan!! - numbers)}"
                }else{
                    view.textView2.text = "0"
                }
                android.os.Handler().postDelayed({

                    if (percent_fact < 100) {
                        view.textView2.text = "${percent_fact}%"
                    } else {
                        view.textView2.text = "$percent_fact%"
                    } }, 1000)
            }else{
                view.textView2.text = "0"
            }
        }
        container.addView(view, position)
        return view
    }
    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }
    @RequiresApi(Build.VERSION_CODES.N)
    fun Double.toRidePrice():String{
        val format: NumberFormat = NumberFormat.getCurrencyInstance()
        format.maximumFractionDigits = 0
        format.currency = Currency.getInstance("KZT")

        return format.format(this.roundToInt())
    }

}