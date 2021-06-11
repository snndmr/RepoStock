package com.snn.repostock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bottom_sheet_persistent_hire.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_product.*
import kotlinx.android.synthetic.main.bottom_sheet_persistent_worker.*
import kotlinx.android.synthetic.main.fragment_administrative.*
import kotlinx.android.synthetic.main.fragment_profile.*
import kotlinx.android.synthetic.main.fragment_stock.*
import java.util.*

class FragmentAdministrative : Fragment(), RecyclerViewClickListener {
    private val workers = arrayListOf<Worker>()
    private var filteredWorkers = arrayListOf<Worker>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Firebase.database.reference.child("workers").get().addOnSuccessListener {
            (it.value as HashMap<*, *>).forEach { (key, value) ->
                val iterator = value as HashMap<*, *>
                workers.add(
                    Worker(
                        key as String,
                        iterator["name"] as String,
                        iterator["phoneNumber"] as String,
                        iterator["mail"] as String,
                        iterator["salary"] as Double,
                        iterator["promotion"] as Double,
                        iterator["gift"] as Double,
                        iterator["hours"] as List<Int>,
                        iterator["sales"] as List<Int>,
                        iterator["customers"] as List<Int>,
                        iterator["isAdmin"] as Boolean
                    )
                )
            }
            setup()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }
        return inflater.inflate(R.layout.fragment_administrative, container, false)
    }

    private fun setup() {
        filteredWorkers.addAll(workers)

        val adapter = WorkerAdapter(filteredWorkers, this@FragmentAdministrative)
        recycler_view_workers.layoutManager = LinearLayoutManager(activity)
        recycler_view_workers.adapter = adapter

        recycler_view_workers.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0 || dy < 0 && floating_hire.isShown) floating_hire.hide()
            }

            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) floating_hire.show()
                super.onScrollStateChanged(recyclerView, newState)
            }
        })

        search_view_worker.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(qString: String): Boolean {
                filteredWorkers.clear()

                for (worker in workers) {
                    if (worker.name.lowercase().contains(qString.lowercase())) {
                        filteredWorkers.add(worker)
                    }
                }
                adapter.filter(filteredWorkers)
                return true
            }

            override fun onQueryTextSubmit(qString: String): Boolean {
                return true
            }
        })

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_hire)

        floating_hire.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

            bottomSheetBehavior.addBottomSheetCallback(object :
                BottomSheetBehavior.BottomSheetCallback() {

                override fun onSlide(bottomSheet: View, slideOffset: Float) {
                }

                override fun onStateChanged(bottomSheet: View, newState: Int) {
                    when (newState) {
                        BottomSheetBehavior.STATE_EXPANDED -> floating_hire.hide()
                        BottomSheetBehavior.STATE_COLLAPSED -> floating_hire.show()
                        else -> floating_hire.show()
                    }
                }
            })
        }

        cancel_button.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }

        hire_button.setOnClickListener {
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    override fun recyclerViewListClicked(position: Int) {
        text_view_admin_bottom_profile_id.text = filteredWorkers[position].uid
        text_view_admin_bottom_profile_name.text = filteredWorkers[position].name
        text_view_admin_bottom_profile_mail.text = filteredWorkers[position].mail
        text_view_admin_bottom_profile_telephone.text = filteredWorkers[position].phoneNumber

        aa_chart_view_admin_bottom_daily_salary.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Pie)
                .title("Salary")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Salary")
                            .data(
                                arrayOf(
                                    arrayOf("Salary", filteredWorkers[position].salary),
                                    arrayOf("Promotion", filteredWorkers[position].promotion),
                                    arrayOf("Gift Card", filteredWorkers[position].gift)
                                )
                            )
                    )
                )
        )

        aa_chart_view_admin_bottom_work_hours.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Spline)
                .title("Hours")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Hours")
                            .data(
                                filteredWorkers[position].hours.toTypedArray()
                            )
                    )
                )
        )

        aa_chart_view_admin_bottom_daily_sales.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Column)
                .title("Sales")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Customers")
                            .data(
                                filteredWorkers[position].customers.toTypedArray()
                            ),
                        AASeriesElement()
                            .name("Sales")
                            .data(
                                filteredWorkers[position].sales.toTypedArray()
                            )
                    )
                )
        )

        text_view_admin_worker_request.setOnClickListener {
            Log.e("TAG", "Worker Requested")
        }

        val bottomSheetBehavior = BottomSheetBehavior.from(bottom_sheet_worker)
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED

    }
}
