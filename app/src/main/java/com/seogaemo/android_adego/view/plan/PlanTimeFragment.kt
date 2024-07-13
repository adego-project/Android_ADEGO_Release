package com.seogaemo.android_adego.view.plan

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.seogaemo.android_adego.databinding.FragmentPlanTimeBinding
import com.seogaemo.android_adego.util.Util.convertDateFormat

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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}