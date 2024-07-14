package com.seogaemo.android_adego.view.plan

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity.INPUT_METHOD_SERVICE
import com.seogaemo.android_adego.databinding.FragmentPlanNameBinding
import com.seogaemo.android_adego.util.Util.keyboardDown

class PlanNameFragment : Fragment() {

    private var _binding: FragmentPlanNameBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameInput.apply {
            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    binding.nameLengthText.text = "약속 이름 (${binding.nameInput.text.length}/12)"
                    if (binding.nameInput.text.isNotEmpty()) {
                        binding.nextButton.visibility = View.VISIBLE
                        binding.noneButton.visibility = View.GONE
                    } else {
                        binding.nextButton.visibility = View.GONE
                        binding.noneButton.visibility = View.VISIBLE
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
            this.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    val manager = requireActivity().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
                    manager.hideSoftInputFromWindow(requireActivity().currentFocus!!.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
                    binding.nameInput.clearFocus()
                    true
                } else {
                    false
                }
            }
        }

        binding.nextButton.setOnClickListener {
            val activity = (requireActivity() as PlanActivity)
            keyboardDown(requireActivity())

            activity.planName = binding.nameInput.text.toString()
            activity.addFragment(PlanDateFragment())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}