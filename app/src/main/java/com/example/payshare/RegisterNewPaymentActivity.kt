package com.example.payshare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.android.synthetic.main.activity_register_new_payment.*
import kotlinx.android.synthetic.main.activity_register_new_payment.paymentName as paymentName1


class RegisterNewPaymentActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register_new_payment)

        val group = intent.extras?.get("group")
        Log.i("REGISTERNEWPAY", group.toString())

        backViewPayment.setOnClickListener{
            val intent = Intent(this, SummaryActivity::class.java)
            startActivity(intent)
        }

        addNewTransactionBtn.setOnClickListener{
            //prendo informazioni e creo oggetto transazione
            //DA SISTEMARE
            val newTransaction = Transaction(R.id.paymentName.toString(),"", arrayListOf(), R.id.paymentQuantity.toDouble())

            val intent = Intent(this, GroupActivity::class.java)
            //intent put extra
            startActivity(intent)
        }

        /*
        val title = paymentName         //riferimento titolo della spesa
        val tot = paymentQuantity       //riferimento quantità della spesa
        val pagatoDa = paymentSubjectRG.checkedRadioButtonId
        */
    }

}