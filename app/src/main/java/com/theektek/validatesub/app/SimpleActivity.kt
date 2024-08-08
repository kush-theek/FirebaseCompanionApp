package com.theektek.validatesub.app

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.firestore.FirebaseFirestore
import com.theektek.validatesub.R
import com.theektek.validatesub.app.core.IdCheckActivity
import com.theektek.validatesub.app.data.CustomerAdapter
import com.theektek.validatesub.app.data.CustomerModel

class SimpleActivity : AppCompatActivity() {

    private val TAG = "SimpleActivity"
    //firebase
    private lateinit var firestore: FirebaseFirestore
    //ui-elements
    private lateinit var toId: ImageButton
    private lateinit var fetchData: AppCompatButton
    private lateinit var addData: AppCompatButton
    private lateinit var firebaseStatus: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var customerAdapter: CustomerAdapter
    private val customerList = mutableListOf<CustomerModel>()
    //custom loader
    private lateinit var loadingDialog: Dialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.simple)
        //init firestore
        firestore = FirebaseFirestore.getInstance()
        //init views and recyclers
        initProgressDialog()
        initViews()
        getCustomers()
    }

    private fun getCustomers() {
        showLoading()
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
                customerAdapter.notifyDataSetChanged()
                firebaseStatus.text = "Data set updated!"
            }
            .addOnFailureListener { exception ->
                Log.w(TAG, "Error getting documents: ", exception)
                firebaseStatus.text = "Error: ${exception.localizedMessage}"
            }
            .addOnCompleteListener {
                hideLoading()
            }
    }

    private fun addSampleData(customerId: String, customerName: String) {
        val customer = hashMapOf(
            "CustomerId" to customerId,
            "CustomerName" to customerName
        )

        firestore.collection("Customers")
            .add(customer)
            .addOnSuccessListener { reference ->
                firebaseStatus.text = "Data added: $reference"
            }
            .addOnFailureListener { exception ->
                firebaseStatus.text = "Error: ${exception.localizedMessage}"
            }
    }

    //full screen simple progress
    private fun initProgressDialog() {
        loadingDialog = Dialog(this).apply {
            requestWindowFeature(Window.FEATURE_NO_TITLE)
            setContentView(R.layout.layout_progress_dialog)
            setCancelable(false)
            window?.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            window?.setBackgroundDrawableResource(android.R.color.transparent)
        }
    }

    //todo : write a single reusable function to show and hide
    private fun showLoading() {
        if (!loadingDialog.isShowing) {
            loadingDialog.show()
        }
    }

    //todo : write a single reusable function to show and hide
    private fun hideLoading() {
        if (loadingDialog.isShowing) {
            loadingDialog.dismiss()
        }
    }

    private fun initViews() {
        toId = findViewById(R.id.btn_to_id)
        firebaseStatus = findViewById(R.id.fb_status)
        fetchData = findViewById(R.id.button_fetch)
        addData = findViewById(R.id.button_add)

        recyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        customerAdapter = CustomerAdapter(customerList)
        recyclerView.adapter = customerAdapter

        //button functions
        fetchData.setOnClickListener {
            getCustomers()
        }

        addData.setOnClickListener {
            showLoading()
            addSampleData("c03223e9-281e-43dc-913f-cca325f1ef59", "Queensland Health")
            addSampleData("a9b76f5c-4f42-4a8f-9b19-2b7040e89c45", "Sunshine Coast University")
            addSampleData("d45f7b2a-3168-4ec7-8b5e-3f7183b4e541", "Brisbane City Council")
            addSampleData("e67a8d4b-5846-4f77-8c32-9f2d1e34eaf4", "Gold Coast Tourism")
            addSampleData("1a23d4f7-6b2c-4c1e-9f4b-0b5a2e1d7f2a", "University of Queensland")
            addSampleData("8b29d7c5-1a73-4a9d-8f5d-3e8b9a2d3e6f", "Queensland Police Service")
            hideLoading()
        }

        toId.setOnClickListener {
            val intent = Intent(this, IdCheckActivity::class.java)
            startActivity(intent)
        }
    }
}
