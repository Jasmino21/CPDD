package com.cpe.infofarm

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.ResourcesCompat
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(private val veggiesList : ArrayList<Veggies>, private val veggiesListYesterday : ArrayList<Veggies>,
                private val veggiesListYesterday0 : ArrayList<Veggies>, private val veggiesListYesterday1 : ArrayList<Veggies>) :
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.vegetable_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentitem = veggiesList[position]
        val itemYesterday = veggiesListYesterday[position]
        val itemYesterday0 = veggiesListYesterday0[position]
        val itemYesterday1 = veggiesListYesterday1[position]
            holder.vegetableToday.text = currentitem.vegetable
            holder.lowPriceToday.text = "Low: ${currentitem.lowPrice}"
            holder.highPriceToday.text = "High: ${currentitem.highPrice}"
            holder.dateToday.text = currentitem.date

            holder.lowPriceYes.text = "Low: ${itemYesterday.lowPrice}"
            holder.highPriceYes.text = "High: ${itemYesterday.highPrice}"
            holder.dateYes.text = itemYesterday.date

            holder.lowPriceYes0.text = "Low: ${itemYesterday0.lowPrice}"
            holder.highPriceYes0.text = "High: ${itemYesterday0.highPrice}"
            holder.dateYes0.text = itemYesterday0.date

            holder.lowPriceYes1.text = "Low: ${itemYesterday1.lowPrice}"
            holder.highPriceYes1.text = "High: ${itemYesterday1.highPrice}"
            holder.dateYes1.text = itemYesterday1.date
            val diff1 = getSum(currentitem.highPrice, itemYesterday.highPrice)
            val diff2 = getSum(itemYesterday.highPrice, itemYesterday0.highPrice)
            val diff3 = getSum(itemYesterday0.highPrice, itemYesterday1.highPrice)

            holder.icon3.setImageResource(getIcon(diff1))
            holder.icon2.setImageResource(getIcon(diff2))
            holder.icon1.setImageResource(getIcon(diff3))
            holder.tv3.text = diff1.toString()
            holder.tv2.text = diff2.toString()
            holder.tv1.text = diff3.toString()
    }

    override fun getItemCount(): Int {
        return veggiesList.size
    }

    class MyViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val vegetableToday : TextView = itemView.findViewById(R.id.tvName)
        val lowPriceToday : TextView = itemView.findViewById(R.id.lowprice_today)
        val highPriceToday : TextView = itemView.findViewById(R.id.highprice_today)
        val dateToday : TextView = itemView.findViewById(R.id.date_today)

        val lowPriceYes : TextView = itemView.findViewById(R.id.lowprice_yesterday)
        val highPriceYes : TextView = itemView.findViewById(R.id.highprice_yesterday)
        val dateYes : TextView = itemView.findViewById(R.id.date_yesterday)

        val lowPriceYes0 : TextView = itemView.findViewById(R.id.lowprice_yesterday0)
        val highPriceYes0 : TextView = itemView.findViewById(R.id.highprice_yesterday0)
        val dateYes0 : TextView = itemView.findViewById(R.id.date_yesterday0)

        val lowPriceYes1 : TextView = itemView.findViewById(R.id.lowprice_yesterday1)
        val highPriceYes1 : TextView = itemView.findViewById(R.id.highprice_yesterday1)
        val dateYes1 : TextView = itemView.findViewById(R.id.date_yesterday1)

        val tv1: TextView = itemView.findViewById(R.id.tv1)
        val tv2: TextView = itemView.findViewById(R.id.tv2)
        val tv3: TextView = itemView.findViewById(R.id.tv3)
        val icon1: ImageView = itemView.findViewById(R.id.icon1)
        val icon2: ImageView = itemView.findViewById(R.id.icon2)
        val icon3: ImageView = itemView.findViewById(R.id.icon3)
    }

    private fun getSum(add1: String?, add2: String? ): Int{
        var diff = 0
        if (add1 == "" || add2 == ""){
            diff = 0
        }else{
            println("sum $add1 and $add2")
            diff = add1!!.toInt() - add2!!.toInt()
        }
        return diff
    }

    private fun getIcon(diff: Int): Int{
        var icon1 = 0
        if(diff > 0){
            icon1 = R.drawable.ic_baseline_arrow_drop_up_24
        }else if(diff < 0){
            icon1 = R.drawable.ic_baseline_arrow_drop_down_24
        }else{
            icon1 = R.drawable.ic_baseline_horizontal_rule_24
        }
        return icon1
    }

}