package com.hostingrakyat.volume2timenextsong

import android.accessibilityservice.AccessibilityService
import android.content.Context
import android.media.AudioManager
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent

/**
 * Intercepts hardware volume-key events system-wide.
 * Two presses of any volume button within DOUBLE_PRESS_MS while music is active
 * dispatches KEYCODE_MEDIA_NEXT to the active media session, skipping to the next track.
 */
class VolumeKeyService : AccessibilityService() {

    companion object {
        private const val DOUBLE_PRESS_MS = 500L
    }

    private var lastPressTime = 0L
    private val audioManager by lazy {
        getSystemService(Context.AUDIO_SERVICE) as AudioManager
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (event.action != KeyEvent.ACTION_DOWN) return false

        val code = event.keyCode
        if (code != KeyEvent.KEYCODE_VOLUME_UP && code != KeyEvent.KEYCODE_VOLUME_DOWN) {
            return false
        }

        if (!audioManager.isMusicActive) return false

        val now = System.currentTimeMillis()
        if (now - lastPressTime <= DOUBLE_PRESS_MS) {
            // Second press within window → skip to next song
            skipToNext()
            lastPressTime = 0L   // reset so triple-press doesn't re-trigger
            return false         // still let the system adjust volume (non-disruptive)
        }

        lastPressTime = now
        return false
    }

    private fun skipToNext() {
        val down = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_MEDIA_NEXT)
        val up   = KeyEvent(KeyEvent.ACTION_UP,   KeyEvent.KEYCODE_MEDIA_NEXT)
        audioManager.dispatchMediaKeyEvent(down)
        audioManager.dispatchMediaKeyEvent(up)
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) = Unit
    override fun onInterrupt() = Unit
}
