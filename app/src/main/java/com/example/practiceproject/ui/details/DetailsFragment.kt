package com.example.practiceproject.ui.details

import android.os.Bundle
import android.provider.Settings.Global.getString
import android.text.InputType
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.practiceproject.utils.Resource
import com.example.practiceproject.data.Produce
import com.example.practiceproject.data.Village
import com.example.practiceproject.ui.orderconfirmation.OrderConfirmationFragment
import com.example.practiceproject.utils.toGone
import com.example.practiceproject.utils.toVisible
import com.example.practiceproject.utils.toast
import com.example.practiceproject.R
import com.example.practiceproject.databinding.FragmentDetailBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DetailFragment : Fragment() {
    private val detailsViewModel: DetailsViewModel by viewModels()
    private lateinit var village: Village
    private var sellerNameValue: String = ""
    private var price : String = ""
    private var weight : String = ""
    private var detailsEntered : Boolean = false

    private lateinit var fragmentDetailBinding : FragmentDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        fragmentDetailBinding = FragmentDetailBinding.inflate(layoutInflater)

        return fragmentDetailBinding.root
    }

    private fun displayUi() {
        fragmentDetailBinding.sellerDetail.apply {
            sellerName.apply {
                name.text = getString(R.string.seller_name)
                nameInputText.hint = getString(R.string.enter_seller_name_text)
                nameInputEditText.setText(sellerNameValue)
            }

            villageName.apply {
                name.text = getString(R.string.village_name)
                nameInputText.hint = getString(R.string.select_village_text)
            }

            loyalityCardDetails.apply {
                name.text = getString(R.string.loyalty_card_no)
                nameInputText.hint = getString(R.string.enter_loyalty_card_no)
            }

            grossWeightDetail.apply {
                name.text = getString(R.string.gross_weight)
                nameInputText.hint = getString(R.string.enter_weight)
                nameInputEditText.inputType = InputType.TYPE_CLASS_NUMBER
                nameInputEditText.setText(weight)
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loadDataFromJson()

        initListeners()

        detailsViewModel.mandiData.observe(viewLifecycleOwner, { resource ->
            when(resource) {
                is Resource.Loading -> fragmentDetailBinding.progressBar.toVisible()
                is Resource.Success -> {
                    fragmentDetailBinding.progressBar.toGone()
                    showSpinner(resource.data.villages)

                    updateUi()
                }
                else -> resource
            }
        })

        displayUi()
        collectFlow()
    }

    private fun updateUi() {
       fragmentDetailBinding.sellerDetail.sellerName.nameInputEditText.setText(sellerNameValue)
       fragmentDetailBinding.sellerDetail.grossWeightDetail.nameInputEditText.setText(weight)
    }

    private fun initListeners() {
        fragmentDetailBinding.sellerDetail.sellerName.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                sellerNameValue = it.toString()
            }
            detailsViewModel.setSellerName(sellerNameValue)
        }

        fragmentDetailBinding.sellerDetail.loyalityCardDetails.nameInputEditText.addTextChangedListener {
            detailsViewModel.setLoyaltyCardId(it.toString())
        }
        fragmentDetailBinding.sellerDetail.grossWeightDetail.nameInputEditText.addTextChangedListener {
            if (it.toString().isNotEmpty()) {
                weight = it.toString()
            }
            if (this::village.isInitialized) {
                detailsViewModel.setGrossWeight(it.toString(), village)
            }
        }

        fragmentDetailBinding.sellMyProduceButton.setOnClickListener {
            if (!detailsEntered) {
                context?.toast(getString(R.string.enter_details_text))
            } else {
                val produce = Produce(price = price, weight = weight)
                val orderConfirmationFragment = OrderConfirmationFragment.newInstance(sellerNameValue, produce)
                activity?.supportFragmentManager?.beginTransaction()
                    ?.replace(R.id.frame_layout, orderConfirmationFragment)?.addToBackStack(null)
                    ?.commit()
            }
        }
    }

    override fun onStop() {
        super.onStop()
        removeListerner()
    }


    private fun removeListerner() {
        fragmentDetailBinding.sellerDetail.sellerName.nameInputEditText.setText("")
        fragmentDetailBinding.sellerDetail.grossWeightDetail.nameInputEditText.setText("")
    }

    private fun collectFlow() {
        lifecycleScope.launch(Dispatchers.Main) {
            detailsViewModel.detailsEntered.collect { value ->
                detailsEntered = value
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            detailsViewModel.grossPrice.collect {
                    grossPrice -> fragmentDetailBinding.priceText.text = grossPrice
                    price = grossPrice
            }
        }

        lifecycleScope.launch(Dispatchers.Main) {
            detailsViewModel.loyaltyCardId.collect {
                loyaltyCardId ->
                fragmentDetailBinding.sellerDetail.loyalityCardDetails.nameInputEditText.setText(loyaltyCardId)
            }
        }
    }

    private fun showSpinner(villages: List<Village>) {
        val list: MutableList<String> = mutableListOf()
        for (village in villages) {
            list.add(village.name)
        }
        fragmentDetailBinding.sellerDetail.villageName.nameInputText.toGone()
        val spinner = fragmentDetailBinding.sellerDetail.villageName.spinner
        spinner.toVisible()
        val adapter = context?.let { ArrayAdapter(it, android.R.layout.simple_spinner_item, list) }
        adapter?.setDropDownViewResource(
            android.R.layout
                .simple_spinner_dropdown_item);
        spinner.adapter = adapter
        village = villages[spinner.selectedItemPosition]
        spinner.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                  village = villages[position]
                  detailsViewModel.setVillage(village)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
            }
        }
    }

    private fun loadDataFromJson() {
        context?.let {
            detailsViewModel.getDataFromJson(it)
        }
    }
}