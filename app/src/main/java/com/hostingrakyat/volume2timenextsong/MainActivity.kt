package com.hostingrakyat.volume2timenextsong

import android.accessibilityservice.AccessibilityServiceInfo
import android.content.ComponentName
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.accessibility.AccessibilityManager
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.color.MaterialColors
import com.hostingrakyat.volume2timenextsong.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnToggle.setOnClickListener {
            startActivity(Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS))
        }
    }

    override fun onResume() {
        super.onResume()
        updateStatus()
    }

    private fun updateStatus() {
        val enabled = isServiceEnabled()
        if (enabled) {
            binding.tvStatus.setText(R.string.status_enabled)
            binding.tvStatus.setTextColor(getColor(R.color.status_enabled))
            binding.btnToggle.setText(R.string.btn_disable)
        } else {
            binding.tvStatus.setText(R.string.status_disabled)
            binding.tvStatus.setTextColor(getColor(R.color.status_disabled))
            binding.btnToggle.setText(R.string.btn_enable)
        }
    }

    private fun isServiceEnabled(): Boolean {
        val am = getSystemService(ACCESSIBILITY_SERVICE) as AccessibilityManager
        val target = ComponentName(this, VolumeKeyService::class.java)
        return am.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            .any { info ->
                info.resolveInfo.serviceInfo.let { si ->
                    si.packageName == target.packageName && si.name == target.className
                }
            }
    }
}
