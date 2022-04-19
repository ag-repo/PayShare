package com.example.payshare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RadioButton

class RegisterNewPaymentActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)


        /*
        val linearLayout = R.id.registerLinearLayout
        /* PaymentSubject sarà la lista da cui selezionare chi ha pagato
        PaymentGroup sarà il gruppo di persone per cui il Subject ha pagato, compreso chi ha pagato */
        val radioButtonPaymentSubject = RadioButton(this)
        val radioButtonPaymentGroup = RadioButton(this)

        val radioButton1 = RadioButton(this)
        radioButton1.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton1.setText("numero 1")
            // radioButtonPaymentGroup.id

        val radioButton2 = RadioButton(this)
        radioButton2.layoutParams = LinearLayout.LayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT)
            radioButton2.setText("numero 2")
        //radioButtonPaymentGroup.id = 2

        val radioGroup = R.id.paymentSubjectRG
        if(radioGroup != null){
            radioGroup.addView(radioButtonPaymentGroup)
        }

        */

    }

}