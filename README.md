# JARVIS — Akash AI Dashboard (V27)

This update preserves the existing V26 dashboard and adds a GitHub Pages-friendly settings and voice layer.

## V27 additions
- Changeable website entry password. Initial password is provided separately to the site owner.
- SHA-256 password hash stored in the current browser.
- Continuous Bengali/English voice session with optional JARVIS wake-word filtering.
- AI replies are spoken through the browser's current audio output route; a Bluetooth headset should be paired first in Android Settings.
- Settings controls for provider preference, Gemini key, local AI endpoint, optional OpenAI key field, voice language, browser permission requests, Contact Picker, file selection, battery and network status.
- New code is isolated in `jarvis-enhancements.js` and `jarvis-enhancements.css`; existing project features and assets are retained.

## Important
GitHub Pages is static hosting. The password gate is a convenience lock, not secure server authentication. API keys saved in browser storage are not secret. Always keep `.env` and real API keys out of public GitHub repositories. See [GITHUB-PAGES-SETUP-BN.md](GITHUB-PAGES-SETUP-BN.md) for publishing steps and the exact browser/Android limitations.

The Android companion sources are starter code. They are not a verified installable APK in this ZIP. Background wake-word, phone-wide contacts/files, SMS, flashlight, volume, robust geofencing, Find My Phone, ESP32 and PC control require further native companion/hardware/backend implementation and testing.

## Local run
```bash
npm install
npm start
```
Open `http://localhost:3000`.
