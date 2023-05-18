package com.example.myapplication

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.databinding.DialogUpdateBinding
import com.example.myapplication.databinding.FragmentFavoritesBinding
import com.example.myapplication.databinding.FragmentRoutinesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RoutinesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RoutinesFragment : Fragment() {

    private lateinit var activeLayout: LinearLayout
    private lateinit var inactiveLayout: LinearLayout

    private lateinit var binding: FragmentRoutinesBinding
    private lateinit var updateBinding: DialogUpdateBinding

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentRoutinesBinding.inflate(inflater, container, false)

        updateBinding = DialogUpdateBinding.inflate(inflater, container, false)
        setUpRoutinesList()

        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.floatingActionButton.setOnClickListener {
            val intent = Intent(context, NewRoutine::class.java)
            startActivity(intent)
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RoutinesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RoutinesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private fun getRoutinesList(): ArrayList<RoutineModelClass> {

        //creating the instance of DatabaseHandler class
        val databaseHandler: RoutineHandler = RoutineHandler(requireContext())

        //calling the viewRoutine method of the DatabaseHandler class to read the records
        return databaseHandler.viewRoutine()

    }

    private fun setUpRoutinesList() {
        if (getRoutinesList().size > 0) {

            binding.active.visibility = View.VISIBLE
            binding.notActive.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            binding.routinesList.layoutManager = LinearLayoutManager(requireContext())

            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = RoutineFragmentAdapter(
                requireContext(),
                getRoutinesList(),
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Handle the item click event
                        val clickedItem = getRoutinesList()[position]
                        updateRecordDialog(clickedItem)
//                        Toast.makeText(context, "Clicked: $clickedItem", Toast.LENGTH_SHORT).show()
                    }
                })

            //adapter instance is set to the recyclerview to inflate the items
            binding.routinesList.adapter = itemAdapter

        } else {
            binding.active.visibility = View.GONE
            binding.notActive.visibility = View.VISIBLE
        }
    }

    private fun updateRecordDialog(routineModel: RoutineModelClass) {

        val updateDialog = Dialog(requireContext(), R.style.Theme_Dialog)
        /*Set the screen content from a layout resource.

         The resource will be inflated, adding all top-level views to the screen.*/
        updateDialog.setContentView(R.layout.dialog_update)

        //set the editText values to the previous names
        updateBinding.routineName.setText(routineModel.routineName)
        updateBinding.notificationText.setText(routineModel.notificationText)

        //what happens when update is clicked
        updateBinding.tvUpdate.setOnClickListener{

            val name = updateBinding.routineName.text.toString()
            val text = updateBinding.notificationText.text.toString()

            val databaseHandler: RoutineHandler = RoutineHandler(requireContext())

            if (name.isNotEmpty() && text.isNotEmpty()) {
                val status =
                    databaseHandler.updateRoutine(
                        RoutineModelClass(
                            routineModel.id,
                            name,
                            routineModel.lastRun,
                            routineModel.notificationText
                        )
                    )
                if (status > -1) {
                    Toast.makeText(requireContext(), "Record Updated.", Toast.LENGTH_LONG).show()

                    setUpRoutinesList()

                    updateDialog.dismiss() // Dialog will be dismissed
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Name or Notification text cannot be blank",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        //what happens when delete is clicked
        updateBinding.tvDelete.setOnClickListener{
            deleteRecordAlertDialog(routineModel)
        }
        //what happens when cancel is clicked
        updateBinding.tvCancel.setOnClickListener{
            updateDialog.dismiss()
        }
        //Start the dialog and display it on screen.
        updateDialog.show()
    }

    private fun deleteRecordAlertDialog(routine: RoutineModelClass) {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Delete Record")
        //set message for alert dialog
        builder.setMessage("Are you sure you want to delete ${routine.routineName}?")
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        //performing positive action
        builder.setPositiveButton("Yes") { dialogInterface, _ ->

            //creating the instance of DatabaseHandler class
            val databaseHandler: RoutineHandler = RoutineHandler(requireContext())
            //calling the deleteEmployee method of DatabaseHandler class to delete record
            val status = databaseHandler.deleteRoutine(
                RoutineModelClass(
                    routine.id,
                    routine.routineName,
                    routine.lastRun,
                    routine.notificationText
                )
            )
            if (status > -1) {
                Toast.makeText(
                    requireContext(),
                    "Record deleted successfully.",
                    Toast.LENGTH_LONG
                ).show()

                setUpRoutinesList()
            }

            dialogInterface.dismiss() // Dialog will be dismissed
        }
        //performing negative action
        builder.setNegativeButton("No") { dialogInterface, _ ->
            dialogInterface.dismiss() // Dialog will be dismissed
        }
        // Create the AlertDialog
        val alertDialog: AlertDialog = builder.create()
        // Set other dialog properties
        alertDialog.setCancelable(false) // Will not allow user to cancel after clicking on remaining screen area.
        alertDialog.show()  // show the dialog to UI
    }

}