package com.snn.repostock

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.bottom_sheet_persistent_profile.*
import kotlinx.android.synthetic.main.fragment_profile.*


class FragmentProfile : Fragment() {
    private lateinit var database: DatabaseReference
    private lateinit var worker: Worker

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val user = arguments?.getString("user").toString()

        Firebase.database.reference.child(user).get().addOnSuccessListener {
            val iterator = it.value as HashMap<*, *>

            worker = Worker(
                user,
                iterator["name"] as String,
                iterator["phoneNumber"] as String,
                iterator["mail"] as String,
                iterator["salary"] as Long,
                iterator["promotion"] as Long,
                iterator["gift"] as Long,
                iterator["hours"] as List<Int>,
                iterator["sales"] as List<Int>,
                iterator["customers"] as List<Int>,
                iterator["isAdmin"] as Boolean
            )

            setup()
        }.addOnFailureListener {
            Log.e("firebase", "Error getting data", it)
        }

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    private fun setup() {
        text_view_profile_name.text = worker.name
        text_view_profile_id.text = worker.uid
        text_view_profile_telephone.text = worker.phoneNumber
        text_view_profile_mail.text = worker.mail

        aa_chart_view_daily_salary.aa_drawChartWithChartModel(
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
                                    arrayOf("Salary", worker.salary),
                                    arrayOf("Promotion", worker.promotion),
                                    arrayOf("Gift Card", worker.gift)
                                )
                            )
                    )
                )
        )

        aa_chart_view_work_hours.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Spline)
                .title("Hours")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Hours")
                            .data(
                                worker.hours.toTypedArray()
                            )
                    )
                )
        )

        aa_chart_view_daily_sales.aa_drawChartWithChartModel(
            AAChartModel()
                .chartType(AAChartType.Column)
                .title("Sales")
                .yAxisTitle("")
                .series(
                    arrayOf(
                        AASeriesElement()
                            .name("Customers")
                            .data(
                                worker.customers.toTypedArray()
                            ),
                        AASeriesElement()
                            .name("Sales")
                            .data(
                                worker.sales.toTypedArray()
                            )
                    )
                )
        )
    }
}