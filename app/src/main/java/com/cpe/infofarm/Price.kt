package com.cpe.infofarm

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.*
import com.google.firebase.firestore.EventListener
import java.util.*
import kotlin.collections.ArrayList
import com.google.firebase.messaging.FirebaseMessaging
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Price : AppCompatActivity() {

    private lateinit var dbref : FirebaseFirestore
    private lateinit var veggieRecyclerView: RecyclerView
    private lateinit var veggiesArrayList : ArrayList<Veggies>
    private lateinit var tempVeggiesArrayList : ArrayList<Veggies>
    private lateinit var veggiesArrayListYesterday: ArrayList<Veggies>
    private lateinit var veggiesArrayListYesterday0: ArrayList<Veggies>
    private lateinit var veggiesArrayListYesterday1: ArrayList<Veggies>
    private lateinit var tempVeggiesArrayList2 : ArrayList<Veggies>
    private lateinit var tempVeggiesArrayList3 : ArrayList<Veggies>
    private lateinit var tempVeggiesArrayList4 : ArrayList<Veggies>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price)

        var firebaseMessaging = FirebaseMessaging.getInstance()
        firebaseMessaging.subscribeToTopic("new_price_update")
        veggieRecyclerView = findViewById(R.id.priceView)
        veggieRecyclerView.layoutManager = LinearLayoutManager(this)
        veggieRecyclerView.setHasFixedSize(true)

        veggiesArrayList = arrayListOf<Veggies>()
        tempVeggiesArrayList = arrayListOf<Veggies>()
        veggiesArrayListYesterday = arrayListOf<Veggies>()
        veggiesArrayListYesterday0 = arrayListOf<Veggies>()
        veggiesArrayListYesterday1 = arrayListOf<Veggies>()
        tempVeggiesArrayList2 = arrayListOf<Veggies>()
        tempVeggiesArrayList3 = arrayListOf<Veggies>()
        tempVeggiesArrayList4 = arrayListOf<Veggies>()

        getVeggiesData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {

        menuInflater.inflate(R.menu.menu_item, menu)
        val item = menu?.findItem(R.id.search_action)
        val searchView = item?.actionView as SearchView
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                tempVeggiesArrayList.clear()
                val searchText = newText!!.lowercase(Locale.getDefault())
                if (searchText.isNotEmpty()) {
                    veggiesArrayList.forEach {
                        if (it.vegetable!!.lowercase(Locale.getDefault()).contains(searchText)) {
                            tempVeggiesArrayList.add(it)
                        }
                    }

                    veggieRecyclerView.adapter!!.notifyDataSetChanged()

                }else{
                    tempVeggiesArrayList.clear()
                    tempVeggiesArrayList.addAll(veggiesArrayList)
                    veggieRecyclerView.adapter!!.notifyDataSetChanged()
                }

                return false
            }

        })

        return super.onCreateOptionsMenu(menu)
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.notification_menu-> {
                item.isChecked = !item.isChecked
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun getVeggiesData() {
        dbref = FirebaseFirestore.getInstance()
        val now = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LocalDateTime.now()
        } else {
            TODO("VERSION.SDK_INT < O")
        }
        val yesterday = now.minusDays(1)
        val yesterday0 = now.minusDays(2)
        val yesterday1 = now.minusDays(3)
        val formatter = DateTimeFormatter.ofPattern("MM-dd-yyyy")
        var currentDate = formatter.format(now)
        var yesterdayDate = formatter.format(yesterday)
        var yesterdayDate0 = formatter.format(yesterday0)
        var yesterdayDate1 = formatter.format(yesterday1)

        dbref.collection("Price").whereIn("date", listOf(currentDate, yesterdayDate, yesterdayDate0, yesterdayDate1)).orderBy("vegetable").
                addSnapshotListener(object : EventListener<QuerySnapshot>{
                    override fun onEvent(
                        value: QuerySnapshot?,
                        error: FirebaseFirestoreException?
                    ){
                        if (error != null){
                            Log.e("Firestore error:", error.message.toString())
                            return
                        }

                        for (dc : DocumentChange in value?.documentChanges!!){

                            if (dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Veggies::class.java).date == currentDate){
                                veggiesArrayList.add(dc.document.toObject(Veggies::class.java))

                            }else if(dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Veggies::class.java).date == yesterdayDate){
                                veggiesArrayListYesterday.add(dc.document.toObject(Veggies::class.java))
                            }else if(dc.type == DocumentChange.Type.ADDED && dc.document.toObject(Veggies::class.java).date == yesterdayDate0){
                                veggiesArrayListYesterday0.add(dc.document.toObject(Veggies::class.java))
                            }else{
                                veggiesArrayListYesterday1.add(dc.document.toObject(Veggies::class.java))
                            }
                        }
                        tempVeggiesArrayList.addAll(veggiesArrayList)
                        tempVeggiesArrayList2.addAll(veggiesArrayListYesterday)
                        tempVeggiesArrayList3.addAll(veggiesArrayListYesterday0)
                        tempVeggiesArrayList4.addAll(veggiesArrayListYesterday1)
                        veggieRecyclerView.adapter = MyAdapter(tempVeggiesArrayList, tempVeggiesArrayList2, tempVeggiesArrayList3, tempVeggiesArrayList4)

                    }
                })
    }
}