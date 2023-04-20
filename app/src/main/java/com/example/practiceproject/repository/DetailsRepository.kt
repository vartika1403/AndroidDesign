package com.example.practiceproject.repository

import android.content.Context
import com.example.practiceproject.utils.Resource
import com.example.practiceproject.data.Jiva
import com.example.practiceproject.R
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

open class DetailsRepository {

    suspend open fun loadDataFromJson(context: Context): Flow<Resource> {
       return flow {
           val item = parseJson(context)

           val itemsList = Gson().fromJson(item, Jiva::class.java)
           if (itemsList?.mandi != null) {
               emit(Resource.Success(itemsList.mandi))
           } else {
               emit(Resource.Error("list if empty"))
           }
       }.flowOn(Dispatchers.IO)
    }

   open  fun parseJson(context: Context): String {
        return context.resources.openRawResource(R.raw.mandi).bufferedReader().use{it.readText()}
    }
}