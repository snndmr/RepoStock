package com.snn.repostock

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*


class FragmentProfile : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val database = Firebase.database
        val reference = database.getReference("profile")

        reference.setValue("Hello, World!")

        val workHours = (1..14).map { (7..14).random() }
        val numberOfCustomers = (1..14).map { (5..100).random() }
        val numberOfSales = numberOfCustomers.map { item -> item + (1..20).random() }

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
                                    arrayOf("Salary", 3336.2),
                                    arrayOf("Promotion", 223.8),
                                    arrayOf("Gift Card", 88.5)
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
                                workHours.toTypedArray()
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
                                numberOfCustomers.toTypedArray()
                            ),
                        AASeriesElement()
                            .name("Sales")
                            .data(
                                numberOfSales.toTypedArray()
                            )
                    )
                )
        )
    }
}