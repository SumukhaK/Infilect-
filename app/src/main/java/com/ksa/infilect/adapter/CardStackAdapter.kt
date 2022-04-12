package com.ksa.infilect.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.ksa.infilect.databinding.ItemUserBinding
import com.ksa.infilect.models.RandomUsers
import com.ksa.infilect.models.Result
import com.ksa.infilect.util.UsersDiffUtils

class CardStackAdapter : RecyclerView.Adapter<CardStackAdapter.MyCardStackViewHolder>() {

    private var userList = emptyList<Result>()
    //lateinit var selectedUser: Result
    class MyCardStackViewHolder(private val rowBinding: ItemUserBinding): RecyclerView.ViewHolder(rowBinding.root) {

        fun bind(result: Result){
            rowBinding.userResult = result
            rowBinding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup) : MyCardStackViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ItemUserBinding.inflate(layoutInflater,parent,false)
                return MyCardStackViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyCardStackViewHolder {
        return MyCardStackViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyCardStackViewHolder, position: Int) {
        val currentUser = userList[position]
        //selectedUser = userList[position]
        holder.bind(currentUser)
    }

    override fun getItemCount(): Int {
        return userList.size
    }


    fun setData(newData:RandomUsers){
        val recipesDiffUtils = UsersDiffUtils(userList,newData.results)
        val diffUtilResult = DiffUtil.calculateDiff(recipesDiffUtils)
        userList = newData.results
        diffUtilResult.dispatchUpdatesTo(this)

    }
}