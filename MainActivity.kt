package com.akash.jarvis

import android.accessibilityservice.AccessibilityService
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.ContactsContract
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.webkit.JavascriptInterface
import android.webkit.WebView
import org.json.JSONArray
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.util.Locale

class MainActivity : Activity(), TextToSpeech.OnInitListener {
    private lateinit var web: WebView
    private lateinit var tts: TextToSpeech
    private val ui = Handler(Looper.getMainLooper())
    private val prefs by lazy { getSharedPreferences("j", MODE_PRIVATE) }

    override fun onCreate(b: Bundle?) {
        super.onCreate(b)
        tts = TextToSpeech(this, this)
        web = WebView(this)
        setContentView(web)
        web.settings.javaScriptEnabled = true
        web.addJavascriptInterface(Bridge(), "Android")
        web.loadUrl("file:///android_asset/index.html")
        requestPermissions(arrayOf(android.Manifest.permission.READ_CONTACTS), 1)
    }

    override fun onInit(s: Int) { if (tts.setLanguage(Locale("bn", "IN")) < 0) tts.setLanguage(Locale.US) }

    private fun js(s: String) { ui.post { web.evaluateJavascript(s, null) } }
    private fun say(t: String) { tts.speak(t, TextToSpeech.QUEUE_ADD, null, "j"); js("log('J',${JSONObject.quote(t)})") }

    inner class Bridge {
        @JavascriptInterface fun listen() { ui.post {
            startActivityForResult(Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, "free_form")
                .putExtra(RecognizerIntent.EXTRA_LANGUAGE, "bn-IN"), 7) } }
        @JavascriptInterface fun cmd(t: String) { Thread { handle(t) }.start() }
        @JavascriptInterface fun settings(w: String) { ui.post {
            startActivity(Intent(if (w == "acc") Settings.ACTION_ACCESSIBILITY_SETTINGS else "android.settings.ACTION_NOTIFICATION_LISTENER_SETTINGS")) } }
        @JavascriptInterface fun save(k: String, m: String, c: String) { prefs.edit().putString("k", k).putString("m", m).putString("c", c).apply() }
        @JavascriptInterface fun load(): String = JSONObject().put("k", prefs.getString("k", ""))
            .put("m", prefs.getString("m", "gemini-2.5-flash")).put("c", prefs.getString("c", "91")).toString()
    }

    @Deprecated("simple")
    override fun onActivityResult(q: Int, r: Int, d: Intent?) {
        val t = d?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull() ?: return
        js("log('Me',${JSONObject.quote(t)})")
        Thread { handle(t) }.start()
    }

    private fun handle(t: String) {
        val key = prefs.getString("k", "") ?: ""
        try { act(if (key.isNotEmpty()) JSONObject(ask(key, t)) else local(t)) }
        catch (e: Exception) { say("সমস্যা: ${e.message}") }
    }

    private fun local(t: String): JSONObject {
        val s = t.lowercase(); val j = JSONObject()
        return when {
            "scroll" in s || "স্ক্রল" in s -> j.put("a", "scroll").put("d", if ("up" in s || "উপর" in s) "up" else "down")
            "notification" in s || "নোটিফিকেশন" in s -> j.put("a", "notif")
            "open" in s || "খোল" in s || "খুল" in s -> j.put("a", "open").put("app", s.replace("open", "").replace("খোলো", "").replace("খুলো", "").trim())
            else -> j.put("a", "chat").put("r", "সব কমান্ড বুঝতে সেটিংসে Gemini API key দিন।")
        }
    }

    private fun ask(key: String, t: String): String {
        val model = prefs.getString("m", "gemini-2.5-flash")
        val sys = "You are JARVIS on an Android phone. The user speaks Bengali or English. Reply ONLY with JSON: " +
            "{\"a\":\"open|whatsapp|scroll|notif|back|home|chat\",\"app\":\"app name in English as shown on phone\"," +
            "\"contact\":\"contact name in English letters\",\"contact_bn\":\"contact name as spoken\",\"msg\":\"message text\"," +
            "\"d\":\"up|down\",\"r\":\"short spoken reply in the user's language\"}. " +
            "Sending a WhatsApp message = a:whatsapp. Questions or chat = a:chat with the answer in r."
        val body = JSONObject()
            .put("system_instruction", JSONObject().put("parts", JSONArray().put(JSONObject().put("text", sys))))
            .put("contents", JSONArray().put(JSONObject().put("parts", JSONArray().put(JSONObject().put("text", t)))))
            .put("generationConfig", JSONObject().put("responseMimeType", "application/json"))
        val c = URL("https://generativelanguage.googleapis.com/v1beta/models/$model:generateContent?key=$key").openConnection() as HttpURLConnection
        c.requestMethod = "POST"; c.doOutput = true; c.setRequestProperty("Content-Type", "application/json")
        c.outputStream.write(body.toString().toByteArray())
        val resp = (if (c.responseCode in 200..299) c.inputStream else c.errorStream).bufferedReader().readText()
        return JSONObject(resp).getJSONArray("candidates").getJSONObject(0).getJSONObject("content")
            .getJSONArray("parts").getJSONObject(0).getString("text")
    }

    private fun act(j: JSONObject) {
        val r = j.optString("r")
        val acc = JarvisAccess.inst
        val need = "আগে Accessibility চালু করুন"
        when (j.optString("a")) {
            "open" -> say(if (openApp(j.optString("app"))) r.ifEmpty { "খুলছি" } else "অ্যাপটা পাইনি")
            "whatsapp" -> whats(j)
            "scroll" -> if (acc == null) say(need) else { acc.swipe(j.optString("d") != "up"); if (r.isNotEmpty()) say(r) }
            "back" -> if (acc == null) say(need) else acc.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK)
            "home" -> if (acc == null) say(need) else acc.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME)
            "notif" -> readNotif()
            else -> say(r.ifEmpty { "বুঝতে পারিনি" })
        }
    }

    private fun openApp(n: String): Boolean {
        val pm = packageManager; val q = n.lowercase()
        val l = pm.queryIntentActivities(Intent(Intent.ACTION_MAIN).addCategory(Intent.CATEGORY_LAUNCHER), 0)
        val m = l.firstOrNull { val x = it.loadLabel(pm).toString().lowercase(); x.isNotEmpty() && q.isNotEmpty() && (x in q || q in x) } ?: return false
        startActivity(pm.getLaunchIntentForPackage(m.activityInfo.packageName)); return true
    }

    private fun whats(j: JSONObject) {
        val num = findNum(j.optString("contact")) ?: findNum(j.optString("contact_bn"))
        if (num == null) { say("কন্টাক্টে নামটা পাইনি"); return }
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://wa.me/$num?text=" + Uri.encode(j.optString("msg")))).setPackage("com.whatsapp"))
        say(j.optString("r").ifEmpty { "পাঠাচ্ছি" })
        ui.postDelayed({ val a = JarvisAccess.inst; if (a == null) say("Send চাপতে Accessibility চালু করুন") else a.clickSend() }, 2500)
    }

    private fun findNum(n: String): String? {
        if (n.isBlank()) return null
        val col = ContactsContract.CommonDataKinds.Phone.NUMBER
        contentResolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, arrayOf(col),
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} LIKE ?", arrayOf("%$n%"), null)?.use { c ->
            if (c.moveToFirst()) {
                val p = c.getString(0); val d = p.filter { it.isDigit() }
                return if (p.startsWith("+")) d else (prefs.getString("c", "91") + d.trimStart('0'))
            }
        }
        return null
    }

    private fun readNotif() {
        val l = JarvisNotif.inst?.list()
        if (l == null) { say("আগে Notification access চালু করুন"); return }
        if (l.isEmpty()) { say("কোনো নোটিফিকেশন নেই"); return }
        l.forEachIndexed { i, s -> say("${i + 1}. $s") }
    }
}
