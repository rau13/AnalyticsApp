package com.example.myapplication

import android.content.Intent
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.StrictMode
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.core.os.HandlerCompat.postDelayed
import com.example.myapplication.responses.BTRXLeads
import com.example.myapplication.responses.ResponseBTRXDeals
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.gson.Gson
import java.net.URL

class Login : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var email: EditText
    private lateinit var password: EditText
    private lateinit var button:Button
    private var totalDeals:String? = null
    private var totalLeads:String? = null
      private lateinit var database: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        supportActionBar!!.hide()
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)
        init()
    }
    private fun init(){
        auth = FirebaseAuth.getInstance()
        email = findViewById(R.id.editTextTextEmailAddress)
        password = findViewById(R.id.editTextTextPassword)
        button = findViewById(R.id.button)
        database = FirebaseDatabase.getInstance().getReference("Bitrix")
        totalLeads()
        totalDeals()
        Handler().postDelayed(
            {
                setBTXDataDeals()
                setBTXDataLeads()
            },1200)
        button.setOnClickListener{
            login()
            button.setTextColor(resources.getColor(R.color.white))
        }

    }
    private fun login() {
        val email = email.text.toString()
        val pass = password.text.toString()
        // calling signInWithEmailAndPassword(email, pass)
        // function using Firebase auth object
        // On successful response Display a Toast
        if(email.length != 0 && pass.length != 0){
        auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(this) {
            if (it.isSuccessful) {
                val intent = Intent(this, Overview::class.java)
                intent.putExtra("email", email)
                startActivity(intent)
                button.setTextColor(resources.getColor(R.color.green_chart))
                Toast.makeText(this, "Successfully LoggedIn", Toast.LENGTH_SHORT).show()
            } else
                Toast.makeText(this, "Log In failed ", Toast.LENGTH_SHORT).show()
        }
        }else{
            Toast.makeText(this, "Empty fields",Toast.LENGTH_SHORT).show()
        }
    }

    private fun setBTXDataLeads(){
        var response = URL("https://estero.bitrix24.kz/rest/57/4sr1m84vu05m46bf/crm.lead.list.json?start=$totalLeads").readText()
        var gson = Gson()
        var data = gson.fromJson(response, BTRXLeads::class.java)
        Log.d("Mylog","${data.total}")
        val delimiter1 = "T"
        val delimiter2 = "+"
        for(i in 0 until data.result!!.size){
            database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).get().addOnSuccessListener {
                if(!it.exists()){
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1, delimiter2)[1]).child("COMPANYTITLE").setValue(data.result!![i]!!.cOMPANYTITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("COMPANYID").setValue(data.result!![i]!!.cOMPANYID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("TITLE").setValue(data.result!![i]!!.tITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Comments").setValue(data.result!![i]!!.cOMMENTS)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Name").setValue(data.result!![i]!!.nAME)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("CONTACTID").setValue(data.result!![i]!!.cONTACTID)
                }else{
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1, delimiter2)[1]).child("COMPANYTITLE").setValue(data.result!![i]!!.cOMPANYTITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("COMPANYID").setValue(data.result!![i]!!.cOMPANYID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("TITLE").setValue(data.result!![i]!!.tITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Comments").setValue(data.result!![i]!!.cOMMENTS)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Name").setValue(data.result!![i]!!.nAME)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("CONTACTID").setValue(data.result!![i]!!.cONTACTID)
                }
            }
        }
    }
    private fun setBTXDataDeals(){
        val database =  FirebaseDatabase.getInstance().getReference("BitrixDeals")
        var response = URL("https://estero.bitrix24.kz/rest/57/4sr1m84vu05m46bf/crm.deal.list.json?start=$totalDeals").readText()
        var gson = Gson()
        var data = gson.fromJson(response, ResponseBTRXDeals::class.java)
        val delimiter1 = "T"
        val delimiter2 = "+"

        for(i in 0 until data.result!!.size){
            database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).get().addOnSuccessListener {
                if(!it.exists()){

                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("COMPANYID").setValue(data.result!![i]!!.cOMPANYID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("TITLE").setValue(data.result!![i]!!.tITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Name").setValue(data.result!![i]!!.sOURCEID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("CONTACTID").setValue(data.result!![i]!!.cONTACTID)
                }else{

                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("COMPANYID").setValue(data.result!![i]!!.cOMPANYID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("TITLE").setValue(data.result!![i]!!.tITLE)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("Name").setValue(data.result!![i]!!.sOURCEID)
                    database.child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[0]).child(data.result!![i]!!.dATECREATE.toString().split(delimiter1,delimiter2)[1]).child("CONTACTID").setValue(data.result!![i]!!.cONTACTID)
                }
            }



        }
    }
    private fun totalDeals(){
        val database =  FirebaseDatabase.getInstance().getReference("BitrixDeals")
        var response = URL("https://estero.bitrix24.kz/rest/57/4sr1m84vu05m46bf/crm.deal.list.json").readText()
        var gson = Gson()
        var data = gson.fromJson(response, ResponseBTRXDeals::class.java)
        totalDeals = data.total.toString()
    }
    private fun totalLeads(){
        var response = URL("https://estero.bitrix24.kz/rest/57/4sr1m84vu05m46bf/crm.lead.list.json").readText()
        var gson = Gson()
        var data = gson.fromJson(response, BTRXLeads::class.java)
        totalLeads = data.total.toString()
    }
}