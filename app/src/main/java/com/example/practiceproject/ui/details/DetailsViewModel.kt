package com.example.practiceproject.ui.details

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.practiceproject.utils.Resource
import com.example.practiceproject.data.Mandi
import com.example.practiceproject.data.Seller
import com.example.practiceproject.data.Village
import com.example.practiceproject.repository.DetailsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class DetailsViewModel constructor(private val detailsRepository: DetailsRepository) : ViewModel() {
    private val _mandiData = MutableLiveData<Resource>()
    val mandiData : LiveData<Resource> get() = _mandiData
    private val _sellerName = MutableStateFlow("")
    private val _loyaltyCardId = MutableStateFlow("")
    val loyaltyCardId : Flow<String> = _loyaltyCardId
    private val _grossWeight = MutableStateFlow("")
    private val _grossPrice = MutableStateFlow("")
    val grossPrice : Flow<String> = _grossPrice
    private var registeredLoyaltyIndex : String = ""
    private var unRegisteredLoyaltyIndex : String = ""
    private var loyaltyIndex : Float = 0.0F
    private var mapOfSellerNameToSeller : MutableMap<String, Seller> = mutableMapOf()
    private var mapOfSellerIdToSeller : MutableMap<String, Seller> = mutableMapOf()

    fun getDataFromJson(context: Context) {
        viewModelScope.launch {
            _mandiData.postValue(Resource.Loading)
            detailsRepository.loadDataFromJson(context).collect { resource ->
                when(resource) {
                       is Resource.Success -> {
                           resource.data.let {
                               mandi -> _mandiData.postValue(Resource.Success(mandi))
                               registeredLoyaltyIndex = mandi.registered_loyality_index
                               unRegisteredLoyaltyIndex = mandi.unregistered_loyality_index
                               loyaltyIndex = unRegisteredLoyaltyIndex.toFloat()
                               mapDetails(mandi)
                           }
                       }
                       is Resource.Error -> {
                           _mandiData.postValue(resource)
                       }
                    else -> _mandiData.postValue(resource)
                   }
            }
        }
    }

    private fun mapDetails(data: Mandi) {
        val sellerList : List<Seller> = data.sellers
        for (seller in sellerList) {
            mapOfSellerNameToSeller.put(seller.name, seller)
            if (seller.loyalty_card_id.isNotEmpty()) {
                mapOfSellerIdToSeller.put(seller.loyalty_card_id, seller)
            }
        }
    }

    val detailsEntered: Flow<Boolean> = combine(_sellerName,
        _loyaltyCardId, _grossWeight) { sellerName, loyaltyCardId, grossWeight ->
        val isSellerNameCorrect = sellerName.isNotEmpty()
        val isLoyaltyCardId = loyaltyCardId.isNotEmpty()
        val grossWeightCorrect = grossWeight.isNotEmpty()
        return@combine (isSellerNameCorrect or isLoyaltyCardId) and grossWeightCorrect
    }

    fun setSellerName(sellerName: String) {
        _sellerName.value = sellerName
        if(mapOfSellerNameToSeller[sellerName] != null) {
            _loyaltyCardId.value = mapOfSellerNameToSeller[sellerName]?.loyalty_card_id.orEmpty()
        }
    }

    fun setLoyaltyCardId(loyaltyCardId: String) {
        _loyaltyCardId.value = loyaltyCardId
       setLoyaltyIndex(loyaltyCardId)
    }

    private fun setLoyaltyIndex(loyaltyCardId: String) {
        if (loyaltyCardId.isNotEmpty() && registeredLoyaltyIndex.isNotEmpty()) {
            loyaltyIndex = registeredLoyaltyIndex.toFloat()
        }
    }

    fun setGrossWeight(weight: String, village : Village) {
        _grossWeight.value = weight

        if (weight.isNotEmpty()) {
            calculatePrice(weight.toFloat(),village)
        }
    }

    fun setVillage(village: Village) {
        val weight = _grossWeight.value
        if (weight.isNotEmpty()) {
            calculatePrice(weight.toFloat(), village)
        }
    }

    private fun calculatePrice(weight: Float, village: Village) {
        val price = village.price.toFloat()
        _grossPrice.value = "${price*weight*loyaltyIndex} INR"
    }
}