package com.seogaemo.android_adego.view.plan

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.seogaemo.android_adego.data.AddressResponse
import com.seogaemo.android_adego.database.TokenManager
import com.seogaemo.android_adego.databinding.FragmentPlanPlaceBinding
import com.seogaemo.android_adego.network.RetrofitAPI
import com.seogaemo.android_adego.network.RetrofitClient
import com.seogaemo.android_adego.util.Util
import com.seogaemo.android_adego.view.auth.LoginActivity
import com.seogaemo.android_adego.view.plan.place.PlaceAdapter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlanPlaceFragment : Fragment() {

    private var _binding: FragmentPlanPlaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlanPlaceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.nameInput.apply {
            this.setOnEditorActionListener { _, actionId, _ ->
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    uiUpdate()
                    true
                } else {
                    false
                }
            }

            this.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    uiUpdate()
                }

                override fun afterTextChanged(s: Editable?) {
                }
            })
        }




    }

    private fun uiUpdate() {
        CoroutineScope(Dispatchers.IO).launch {
            val addressResponse = searchAddress(requireContext(), "${binding.nameInput.text}")
            withContext(Dispatchers.Main) {
                if (addressResponse != null) {
                    updateRecyclerView(requireContext(), addressResponse)
                }
            }
        }
    }

    private suspend fun searchAddress(context: Context, name: String): AddressResponse? {
        return try {
            withContext(Dispatchers.IO) {
                val retrofitAPI = RetrofitClient.getInstance().create(RetrofitAPI::class.java)
                val response = retrofitAPI.searchAddress("bearer ${TokenManager.accessToken}", name)
                if (response.isSuccessful) {
                    response.body()
                } else if (response.code() == 401) {
                    val getRefresh = Util.getRefresh()
                    if (getRefresh != null) {
                        TokenManager.refreshToken = getRefresh.refreshToken
                        TokenManager.accessToken = getRefresh.accessToken
                        searchAddress(context, name)
                    } else {
                        TokenManager.refreshToken = ""
                        TokenManager.accessToken = ""
                        startActivity(Intent(context, LoginActivity::class.java))
                        requireActivity().finishAffinity()
                        null
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "해당하는 주소가 없습니다", Toast.LENGTH_SHORT).show()
                    }
                    null
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Toast.makeText(context, "해당하는 주소가 없습니다", Toast.LENGTH_SHORT).show()
            }
            null
        }
    }

    private fun updateRecyclerView(context: Context, addressResponse: AddressResponse) {
        binding.locationList.layoutManager = LinearLayoutManager(context)
        binding.locationList.adapter = PlaceAdapter(addressResponse.documents)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}