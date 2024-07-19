package com.seogaemo.android_adego.view.plan

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.databinding.FragmentPlanFinishBinding
import com.seogaemo.android_adego.util.Util.convertDateFormat
import com.seogaemo.android_adego.util.Util.copyToClipboard
import com.seogaemo.android_adego.util.Util.getLink
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
                    activity.findViewById<LinearLayout>(R.id.back_button).visibility = View.INVISIBLE

                    binding.nameText.text = activity.planName

                    binding.dateText.text = convertDateFormat(activity.planDate)
                    binding.timeText.text = if (time[0].toInt() > 11) {
                        "오후 ${time[0].toInt() % 12}시 ${time[1]}분"
                    } else {
                        "오전 ${time[0]}시 ${time[1]}분"
                    }
                    binding.locationText.text = activity.planPlace

                    binding.sharedButton.setOnClickListener {
                        startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                    type = "text/plain"

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val link = getLink(requireActivity())
                                        if (link != null) {
                                            withContext(Dispatchers.Main) {
                                                requireActivity().copyToClipboard(link.link)
                                                Toast.makeText(requireContext(), "초대 링크가 클립보드에 복사됐어요", Toast.LENGTH_SHORT).show()

                                                val content = "약속에 초대됐어요!\n하단 링크를 통해 어떤 약속인지 확인하세요."
                                                putExtra(Intent.EXTRA_TEXT,"$content\n\n$link")
                                            }
                                        }
                                    }
                                },
                                "친구에게 초대 링크 공유하기"
                            )
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