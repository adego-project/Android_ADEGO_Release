package com.seogaemo.android_adego.view.main

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.seogaemo.android_adego.R
import com.seogaemo.android_adego.data.PlanResponse
import com.seogaemo.android_adego.data.PlanStatus
import com.seogaemo.android_adego.databinding.ActiveViewBinding
import com.seogaemo.android_adego.databinding.ActivityMainBinding
import com.seogaemo.android_adego.databinding.DisabledViewBinding
import com.seogaemo.android_adego.databinding.NoPromiseViewBinding
import com.seogaemo.android_adego.util.Util.copyToClipboard
import com.seogaemo.android_adego.util.Util.getLink
import com.seogaemo.android_adego.util.Util.getPlan
import com.seogaemo.android_adego.util.Util.parseDateTime
import com.seogaemo.android_adego.view.alarm.AlarmActivity
import com.seogaemo.android_adego.view.auth.LoginActivity
import com.seogaemo.android_adego.view.plan.PlanActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private lateinit var binding: ActivityMainBinding

    private lateinit var mMap: GoogleMap
    private lateinit var mapFragment: SupportMapFragment

    private var placeDate: String? = null
    private var planStatus: PlanStatus? = null

    private val timeUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == Intent.ACTION_TIME_TICK) {
                placeDate?.let {
                    findViewById<TextView>(R.id.next_text).text = calculateDifference(it)
                    if (isDateEnd(it)) {
                        bindingView()
                        removeTimeReceiver()
                    }
                }
            }
        }
    }

    private val loginReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            if (intent?.action == "ACTION_LOGIN_REQUIRED") {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                finishAffinity()
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        LocalBroadcastManager.getInstance(this).registerReceiver(loginReceiver, IntentFilter("ACTION_LOGIN_REQUIRED"))

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this@MainActivity)

        binding.settingButton.setOnClickListener {
            startActivity(Intent(this@MainActivity, SettingActivity::class.java))
            overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
        }

        askNotificationPermission()
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

        mMap.setOnMarkerClickListener(this@MainActivity)
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(37.627717208553854, 126.92327919682702), 16.0F))
    }

    private fun bindingView() {
        CoroutineScope(Dispatchers.IO).launch {
            val plan = getPlan(this@MainActivity)
            planStatus = determinePlanStatus(plan)

            if (planStatus == PlanStatus.DISABLED) placeDate = plan?.date
            withContext(Dispatchers.Main) {
                planStatus?.let { setBottomLayout(it, plan) }
                if (::mMap.isInitialized) {
                    mMap.clear()
                    plan?.let {
                        val place = it.place
                        mMap.addMarker(
                            MarkerOptions()
                                .icon(BitmapDescriptorFactory.fromResource(R.drawable.default_marker))
                                .position(LatLng(place.y.toDouble(), place.x.toDouble()))
                        )
                        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(place.y.toDouble(), place.x.toDouble()), 16.0F))
                    }
                }
            }
        }
        mapFragment.onResume()
    }

    private fun removeTimeReceiver() {
        unregisterReceiver(timeUpdateReceiver)
    }

    private fun determinePlanStatus(plan: PlanResponse?): PlanStatus {
        return if (plan == null) {
            PlanStatus.NO_PROMISE
        } else if (plan.isAlarmAvailable) {
            PlanStatus.ACTIVE
        } else {
            PlanStatus.DISABLED
        }
    }

    private fun calculateDifference(input: String): String {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:m:s")
        val inputDateTime = LocalDateTime.parse(input, formatter)

        val koreaZone = ZoneId.of("Asia/Seoul")
        val currentKoreaDateTime = ZonedDateTime.now(koreaZone).toLocalDateTime()

        val duration = Duration.between(currentKoreaDateTime, inputDateTime)

        val days = duration.toDays()
        val hours = duration.toHours() % 24
        val minutes = duration.toMinutes() % 60

        return "${days}일 ${hours}시간 ${minutes}분 뒤 시작돼요"
    }

    private fun setBottomLayout(state: PlanStatus, promiseInfo: PlanResponse? = null) {
        val inflater: LayoutInflater = layoutInflater
        val view = when (state) {
            PlanStatus.DISABLED -> {
                DisabledViewBinding.inflate(inflater, binding.includeContainer, false).apply {
                    this.sharedButton.setOnClickListener {
                        startActivity(
                            Intent.createChooser(
                                Intent(Intent.ACTION_SEND_MULTIPLE).apply {
                                    type = "text/plain"

                                    CoroutineScope(Dispatchers.IO).launch {
                                        val link = getLink(this@MainActivity)
                                        if (link != null) {
                                            withContext(Dispatchers.Main) {
                                                this@MainActivity.copyToClipboard(link.link)
                                                Toast.makeText(this@MainActivity, "초대 링크가 클립보드에 복사됐어요", Toast.LENGTH_SHORT).show()

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

                    this.nameText.text = promiseInfo!!.name

                    val (date, time) = parseDateTime(promiseInfo.date)
                    this.dateText.text = date
                    this.timeText.text = time

                    this.locationText.text = promiseInfo.place.name

                    this.nextText.text = calculateDifference(promiseInfo.date)

                    registerReceiver(timeUpdateReceiver, IntentFilter(Intent.ACTION_TIME_TICK))
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
                        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                    }
                }
            }
            PlanStatus.NO_PROMISE -> {
                NoPromiseViewBinding.inflate(inflater, binding.includeContainer, false).apply {
                    this.nextButton.setOnClickListener {
                        startActivity(Intent(this@MainActivity, PlanActivity::class.java))
                        overridePendingTransition(R.anim.anim_slide_in_from_right_fade_in, R.anim.anim_fade_out)
                    }
                }
            }
        }

        binding.includeContainer.removeAllViews()
        binding.includeContainer.addView(view.root)

    }

    override fun onResume() {
        super.onResume()
        bindingView()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
        if (planStatus == PlanStatus.DISABLED) unregisterReceiver(timeUpdateReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(loginReceiver)
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment.onLowMemory()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        if (this::mMap.isInitialized) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(marker.position, 16.0F))
            return true
        } else {
            return false
        }
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (!isGranted) {
            askNotificationPermission()
        }
    }

    private fun askNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {
                    Toast.makeText(this@MainActivity, "알람 권한을 허용해주세요", Toast.LENGTH_SHORT).show()
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }
    }

    private fun isDateEnd(dateString: String): Boolean {
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'H:m:ss")
        val inputDateTime = LocalDateTime.parse(dateString, formatter)

        val koreaZone = ZoneId.of("Asia/Seoul")
        val currentKoreaDateTime = LocalDateTime.now(koreaZone)

        return inputDateTime.isBefore(currentKoreaDateTime) || inputDateTime.isEqual(currentKoreaDateTime)
    }
}