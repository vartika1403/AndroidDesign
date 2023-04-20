package com.example.practiceproject.ui.orderconfirmation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.practiceproject.data.Produce
import com.example.practiceproject.R
import com.example.practiceproject.databinding.FragmentOrderConfirmationBinding

private const val NAME = "seller_name"
private const val PRODUCE = "produce"


class OrderConfirmationFragment : Fragment() {
    private var sellerName: String? = null
    private var produce: Produce? = null
    private lateinit var orderConfirmationBinding: FragmentOrderConfirmationBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            sellerName = it.getString(NAME)
            produce = it.getSerializable(PRODUCE) as Produce?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        orderConfirmationBinding = FragmentOrderConfirmationBinding.inflate(layoutInflater)

        displayUi()
        return orderConfirmationBinding.root
    }

    private fun displayUi() {
        orderConfirmationBinding.sellerConfirmationText.text = getString(R.string.order_confirmation_message, sellerName)
        orderConfirmationBinding.sellerConfirmationAmountText.text = getString(R.string.order_confirmation_price_message, produce?.price, produce?.weight)
    }

    companion object {
        @JvmStatic
        fun newInstance(name: String, produce: Produce) =
            OrderConfirmationFragment().apply {
                arguments = Bundle().apply {
                    putString(NAME, name)
                    putSerializable(PRODUCE, produce)
                }
            }
    }
}