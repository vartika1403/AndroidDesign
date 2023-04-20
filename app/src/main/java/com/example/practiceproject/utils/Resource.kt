package com.example.practiceproject.utils

import com.example.practiceproject.data.Mandi

sealed class Resource {
    object Loading : Resource()
    data class Success(val data : Mandi) : Resource()
    data class Error(val error: String = "") : Resource()
}
