package com.seogaemo.android_adego.view.plan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seogaemo.android_adego.databinding.FragmentPlanDateBinding
import com.seogaemo.android_adego.view.plan.calendar.FutureDateDecorator
import com.seogaemo.android_adego.view.plan.calendar.PastDateDecorator

class PlanDateFragment : Fragment() {

    private var _binding: FragmentPlanDateBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanDateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.calendarView.apply {
            this.addDecorators(PastDateDecorator(requireContext()), FutureDateDecorator(requireContext()))
            this.setTitleFormatter { day ->
                val calendarHeaderElements = day.toString().split("-")
                val calendarHeaderBuilder = StringBuilder()

                calendarHeaderBuilder.append(calendarHeaderElements[0]).append("년 ")
                    .append(calendarHeaderElements[1]).append("월")

                calendarHeaderBuilder.toString()
            }
            this.setOnDateChangedListener { _, date, _ ->
                val activity = (requireActivity() as PlanActivity)

                activity.planDate = "${date}T"
                activity.addFragment(PlanDateFragment())
            }
        }

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}