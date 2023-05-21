package com.example.myapplication

import android.R
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.ActivityMainBinding
import com.example.myapplication.databinding.FavoritesRoutineListBinding
import com.example.myapplication.databinding.FragmentFavoritesBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [FavoritesFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding

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
        // Inflate the layout for this fragment
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        setUpRoutinesList()
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpRoutinesList()
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
         * @return A new instance of fragment FavoritesFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            FavoritesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
    private fun getRoutinesList(): ArrayList<FavoritesModel> {

        //creating the instance of DatabaseHandler class
        val databaseHandler: FavoritesHandler = FavoritesHandler(requireContext())

        //calling the viewRoutine method of the DatabaseHandler class to read the records
        return databaseHandler.viewRoutine()
    }

    private fun setUpRoutinesList() {
        if (getRoutinesList().size > 0) {

            binding.favorites.visibility = View.VISIBLE
            binding.noFavorites.visibility = View.GONE

            // Set the LayoutManager that this RecyclerView will use.
            binding.routinesList.layoutManager = LinearLayoutManager(requireContext())

            // Adapter class is initialized and list is passed in the param.
            val itemAdapter = FavoritesFragmentAdapter(
                requireContext(),
                getRoutinesList(),
                object : OnItemClickListener {
                    override fun onItemClick(position: Int) {
                        // Handle the item click event
                        val clickedItem = getRoutinesList()[position]
                        removeFavorites(clickedItem)
                    }
                })

            //adapter instance is set to the recyclerview to inflate the items
            binding.routinesList.adapter = itemAdapter

        } else {
            binding.favorites.visibility = View.GONE
            binding.noFavorites.visibility = View.VISIBLE
        }
    }

    private fun removeFavorites(favorite: FavoritesModel) {
        val builder = AlertDialog.Builder(requireContext())
        //set title for alert dialog
        builder.setTitle("Remove from Favorites?")
        //set message for alert dialog
        builder.setMessage("${favorite.routineName}")

        builder.setPositiveButton("REMOVE") { dialog, _ ->

            dialog.dismiss()

            val databaseHandler: FavoritesHandler = FavoritesHandler(requireContext())

            val status =
                databaseHandler.deleteRoutine(
                    FavoritesModel(
                        favorite.id,
                        favorite.routineName,
                        favorite.lastRun,
                        favorite.notificationText
                    )
                )
            if (status > -1) {
                Toast.makeText(requireContext(), "Removed from Favorites", Toast.LENGTH_LONG).show()
                setUpRoutinesList()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Failure to remove from Favorites",
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