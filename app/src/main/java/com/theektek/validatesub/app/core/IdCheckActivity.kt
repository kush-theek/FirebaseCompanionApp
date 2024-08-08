package com.theektek.validatesub.app.core

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.google.firebase.firestore.FirebaseFirestore
import com.sap.cloud.mobile.fiori.formcell.NoteFormCell
import com.theektek.validatesub.R
import com.theektek.validatesub.app.WelcomeActivity
import com.theektek.validatesub.app.data.CustomerModel

/**
 * Example activity to check and compare customer id
 */
class IdCheckActivity : AppCompatActivity() {

    private val TAG = "IdCheckActivity"

    private lateinit var noteCell: NoteFormCell
    private lateinit var btnCheck: AppCompatButton
    private lateinit var btnGenerateUnique: AppCompatButton
    private lateinit var btnGenerateExisting: AppCompatButton

    private lateinit var firestore: FirebaseFirestore

    private val customerList = mutableListOf<CustomerModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_id_check)
        //init firestore
        firestore = FirebaseFirestore.getInstance()

        //init views
        noteCell = findViewById(R.id.note_cell)
        btnCheck = findViewById(R.id.button_check)
        btnGenerateUnique = findViewById(R.id.btn_unique)
        btnGenerateExisting = findViewById(R.id.btn_existing)
        //dummy initial value
        noteCell.value = "a2f6b8c1-9d32-4dbe-855e-0e4b4f18c828"

        //fetch customers initially
        getCustomers()

        //set listeners
        btnCheck.setOnClickListener {
            if (checkForExistingId()) {
                Toast.makeText(this, "Maintain Session", Toast.LENGTH_SHORT).show()
            } else {
                /**
                 * todo : show an alert before actually exiting
                 */
                Toast.makeText(this, "Exit App", Toast.LENGTH_SHORT).show()
                Intent(this, WelcomeActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
                    startActivity(this)
                }
            }
        }

        btnGenerateUnique.setOnClickListener {
            noteCell.value = "a2f6b8c1-9d32-4dbe-855e-0e4b4f18c828"
        }

        btnGenerateExisting.setOnClickListener {
            noteCell.value = getRandomExistingCustomerId()
        }
    }

    private fun getCustomers() {
        customerList.clear()
        firestore.collection("Customers")
            .get()
            .addOnSuccessListener { result ->
                for (document in result) {
                    val customer = CustomerModel(
                        customerId = document.getString("CustomerId") ?: "",
                        customerName = document.getString("CustomerName") ?: ""
                    )
                    customerList.add(customer)
                }
                // Enable the check button after data is loaded
                btnCheck.isEnabled = true
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                Toast.makeText(this, "Failed to fetch customers", Toast.LENGTH_SHORT).show()
            }
    }

    private fun checkForExistingId(): Boolean {
        return customerList.any { it.customerId == noteCell.value.toString() }
    }

    private fun getRandomExistingCustomerId(): String {
        return if (customerList.isNotEmpty()) {
            customerList.random().customerId
        } else {
            "No customers available"
        }
    }
}
