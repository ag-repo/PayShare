package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.RadioButton
import androidx.core.view.get
import com.google.android.material.datepicker.MaterialDatePicker
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import java.util.*

class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)

        backViewPayment.setOnClickListener{
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        val today = Calendar.getInstance()

        val title = paymentName         //riferimento titolo della spesa
        val tot = paymentQuantity       //riferimento quantità della spesa
        val datePicker = paymentDate    //riferimento al datePicker



    }

}