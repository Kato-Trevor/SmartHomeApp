package com.example.myapplication

import android.app.AlertDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.ActivityNewRoutineBinding
import com.example.myapplication.databinding.ActivityRoutineInputsBinding

class NewRoutine : AppCompatActivity() {

    private lateinit var binding: ActivityNewRoutineBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNewRoutineBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(this, RoutineInputs::class.java)
            startActivity(intent)
        }
        binding.backArrow.setOnClickListener {
            finish()
        }
        setUpRoutinesList()
    }

    private fun getRoutinesList(): ArrayList<RoutineModelClass> {

        //creating the instance of DatabaseHandler class
        val databaseHandler: RoutineHandler = RoutineHandler(this)

        //calling the viewRoutine method of the DatabaseHandler class to read the records
        return databaseHandler.viewRoutine()

    }

    private fun setUpRoutinesList() {
        if (getRoutinesList().size > 0) {
            binding.routinesList.visibility = View.VISIBLE

            // Set the LayoutManager that this RecyclerView will use.
            binding.routinesList.layoutManager = LinearLayoutManager(this)

            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = RoutineAdapter(
                this,
                getRoutinesList(),
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Handle the item click event
                        val clickedItem = getRoutinesList()[position]
                        addFavorites(clickedItem)
                    }
                })

            //adapter instance is set to the recyclerview to inflate the items
            binding.routinesList.adapter = itemAdapter

        } else {
            binding.routinesList.visibility = View.GONE
        }
    }

    private fun addFavorites(routine: RoutineModelClass){
        val builder = AlertDialog.Builder(this)
        //set title for alert dialog
        builder.setTitle("Add to Favorites?")
        //set message for alert dialog
        builder.setMessage("${routine.routineName}")

        builder.setPositiveButton("ADD") { dialog, _ ->

            dialog.dismiss()

            val databaseHandler: FavoritesHandler = FavoritesHandler(this)

            val status =
                databaseHandler.addRoutine(
                    FavoritesModel(
                        routine.id,
                        routine.routineName,
                        routine.lastRun,
                        routine.notificationText
                    )
                )
            if (status > -1) {
                Toast.makeText(this, "Added to Favorites", Toast.LENGTH_LONG).show()
                setUpRoutinesList()
            } else {
                Toast.makeText(
                    this,
                    "Failure in adding to Favorites",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
        builder.setNegativeButton("CANCEL") { dialog, _ ->
            dialog.cancel()
        }
        builder.show()
    }
}