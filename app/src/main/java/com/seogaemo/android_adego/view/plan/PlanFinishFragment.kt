package com.seogaemo.android_adego.view.plan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.databinding.FragmentPlanFinishBinding
import com.seogaemo.android_adego.view.main.MainActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class PlanFinishFragment : Fragment() {

    private var _binding: FragmentPlanFinishBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanFinishBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val activity = requireActivity() as PlanActivity
        val time = activity.planTime.split(":")

        CoroutineScope(Dispatchers.IO).launch {
            val isSuccess = activity.setPlan(requireContext()) != null
            withContext(Dispatchers.Main) {
                if (isSuccess) {
                    activity.findViewById<LinearLayout>(R.id.back_button).visibility = View.GONE

                    binding.dateText.text = activity.planDate
                    binding.timeText.text = if (time[0].toInt() > 11) {
                        "오후 ${time[0].toInt() % 12}시 ${time[1]}분"
                    } else {
                        "오전 ${time[0]}시 ${time[1]}분"
                    }
                    binding.locationText.text = activity.planPlace

                    binding.sharedButton.setOnClickListener {
                        startActivity(
                            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                type = "text/plain"
                                val url = ""
                                val content = "약속에 초대됐어요!\n하단 링크를 통해 어떤 약속인지 확인하세요."
                                putExtra(Intent.EXTRA_TEXT,"$content\n\n$url")
                            }
                        )
                    }

                    binding.finishButton.setOnClickListener {
                        startActivity(Intent(requireActivity(), MainActivity::class.java))
                        requireActivity().finishAffinity()
                    }
                } else {
                    startActivity(Intent(requireActivity(), MainActivity::class.java))
                    requireActivity().finishAffinity()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}