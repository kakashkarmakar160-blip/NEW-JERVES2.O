package com.akash.jarvis

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.GestureDescription
import android.content.Intent
import android.graphics.Path
import android.os.Handler
import android.os.Looper
import android.service.notification.NotificationListenerService
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityNodeInfo

class JarvisAccess : AccessibilityService() {
    companion object { var inst: JarvisAccess? = null }
    override fun onServiceConnected() { inst = this }
    override fun onUnbind(i: Intent?): Boolean { inst = null; return super.onUnbind(i) }
    override fun onAccessibilityEvent(e: AccessibilityEvent?) {}
    override fun onInterrupt() {}

    fun swipe(down: Boolean) {
        val m = resources.displayMetrics
        val x = m.widthPixels / 2f; val a = m.heightPixels * 0.75f; val b = m.heightPixels * 0.25f
        val p = Path()
        if (down) { p.moveTo(x, a); p.lineTo(x, b) } else { p.moveTo(x, b); p.lineTo(x, a) }
        dispatchGesture(GestureDescription.Builder().addStroke(GestureDescription.StrokeDescription(p, 0, 350)).build(), null, null)
    }

    fun clickSend(tries: Int = 8) {
        val r = rootInActiveWindow
        val n = r?.findAccessibilityNodeInfosByViewId("com.whatsapp:id/send")?.firstOrNull() ?: r?.let { find(it) }
        if (n != null) { n.performAction(AccessibilityNodeInfo.ACTION_CLICK); return }
        if (tries > 0) Handler(Looper.getMainLooper()).postDelayed({ clickSend(tries - 1) }, 800)
    }

    private fun find(n: AccessibilityNodeInfo): AccessibilityNodeInfo? {
        val d = n.contentDescription?.toString()?.lowercase()
        if (d == "send" || d == "পাঠান") return n
        for (i in 0 until n.childCount) n.getChild(i)?.let { c -> find(c)?.let { return it } }
        return null
    }
}

class JarvisNotif : NotificationListenerService() {
    companion object { var inst: JarvisNotif? = null }
    override fun onListenerConnected() { inst = this }
    override fun onListenerDisconnected() { inst = null }

    fun list(): List<String> = activeNotifications.filter { it.packageName != packageName }.mapNotNull { s ->
        val e = s.notification.extras
        val t = e.getCharSequence("android.title") ?: ""
        val x = e.getCharSequence("android.text") ?: ""
        if (t.isEmpty() && x.isEmpty()) null else "$t: $x"
    }
}
