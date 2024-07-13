package com.seogaemo.android_adego.view.main

import android.content.Intent
import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.data.PlanStatus
import com.seogaemo.android_adego.databinding.ActiveViewBinding
import com.seogaemo.android_adego.databinding.ActivityMainBinding
import com.seogaemo.android_adego.databinding.DisabledViewBinding
import com.seogaemo.android_adego.databinding.NoPromiseViewBinding
import com.seogaemo.android_adego.util.Util.parseDateTime
import com.seogaemo.android_adego.view.alarm.AlarmActivity
import com.seogaemo.android_adego.view.plan.PlanActivity


class MainActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityMainBinding
    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)

        setBottomLayout(PlanStatus.NO_PROMISE)

        binding.settingButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
        }

    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        try {
            val success = mMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    this, R.raw.style_json
                )
            )
            if (!success) {
                Toast.makeText(this@MainActivity, "지도 불러오기 실패 다시 시도해주세요", Toast.LENGTH_SHORT).show()
            }
        } catch (e: Resources.NotFoundException) {
            Toast.makeText(this@MainActivity, "지도 불러오기 실패 다시 시도해주세요", Toast.LENGTH_SHORT).show()
        }

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.570454631, 126.992134289), 16.0F))
    }

    private fun setBottomLayout(state: PlanStatus, promiseInfo: PlanResponse? = null) {
        val inflater: LayoutInflater = layoutInflater
        val view = when (state) {
            PlanStatus.DISABLED -> {
                DisabledViewBinding.inflate(inflater, binding.includeContainer, false).apply {
                    this.sharedButton.setOnClickListener {
                        startActivity(
                            Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                type = "text/plain"
                                val url = ""
                                val content = "약속에 초대됐어요!\n하단 링크를 통해 어떤 약속인지 확인하세요."
                                putExtra(Intent.EXTRA_TEXT,"$content\n\n$url")
                            }
                        )
                    }

                    this.nameText.text = promiseInfo!!.name

                    val (date, time) = parseDateTime(promiseInfo.date)
                    this.dateText.text = date
                    this.timeText.text = time

                    this.locationText.text = promiseInfo.place.name
                }
            }
            PlanStatus.ACTIVE -> {
                ActiveViewBinding.inflate(inflater, binding.includeContainer, false).apply {
                    this.nameText.text = promiseInfo!!.name

                    val (date, time) = parseDateTime(promiseInfo.date)
                    this.dateText.text = date
                    this.timeText.text = time

                    this.locationText.text = promiseInfo.place.name

                    this.nextButton.setOnClickListener {
                        startActivity(Intent(this@MainActivity, AlarmActivity::class.java))
                    }
                }
            }
            PlanStatus.NO_PROMISE -> {
                NoPromiseViewBinding.inflate(inflater, binding.includeContainer, false).apply {
                    this.nextButton.setOnClickListener {
                        startActivity(Intent(this@MainActivity, PlanActivity::class.java))
                    }
                }
            }
        }

        binding.includeContainer.removeAllViews()
        binding.includeContainer.addView(view.root)

    }
}