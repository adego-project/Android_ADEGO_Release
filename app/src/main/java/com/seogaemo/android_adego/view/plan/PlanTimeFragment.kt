package com.seogaemo.android_adego.view.plan

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.seogaemo.android_adego.databinding.FragmentPlanTimeBinding
import com.seogaemo.android_adego.util.Util.convertDateFormat
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Calendar


class PlanTimeFragment : Fragment() {

    private var _binding: FragmentPlanTimeBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanTimeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = (requireActivity() as PlanActivity)

        binding.dateText.text = convertDateFormat(activity.planDate)

        binding.nextButton.setOnClickListener {
            activity.planTime = "${binding.timeView.hour}:${binding.timeView.minute}:00"
            activity.addFragment(PlanPlaceFragment())
        }

        binding.timeView.setOnTimeChangedListener { timeView, hourOfDay, minute ->
            if (isToday(activity.planDate)) {
                val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                val currentMinute = Calendar.getInstance().get(Calendar.MINUTE)

                if (hourOfDay < currentHour || (hourOfDay == currentHour && minute < currentMinute)) {
                    timeView.hour = currentHour
                    timeView.minute = currentMinute
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun isToday(inputDate: String): Boolean {
        val currentDate = LocalDate.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")

        val formattedDate = currentDate.format(formatter)

        return formattedDate == inputDate
    }

}