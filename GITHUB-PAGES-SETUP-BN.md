# JARVIS — GitHub Pages সেটআপ

## প্রকাশ করার ধাপ
1. ZIP extract করে `JARVIS--main` ফোল্ডারের সব website file GitHub repository-র root-এ upload করো।
2. `.env` বা কোনো API secret কখনও GitHub-এ upload করবে না। এই ZIP থেকে `.env` বাদ দেওয়া হয়েছে; `.env.example` শুধু নমুনা।
3. GitHub repository → **Settings → Pages** খুলে **Deploy from a branch** নির্বাচন করো।
4. Branch হিসেবে `main` এবং folder হিসেবে `/ (root)` নির্বাচন করে Save করো।
5. GitHub যে Pages URL দেবে, সেটি HTTPS-এ খুলে ব্যবহার করো।

## নতুন সংযোজন (V27)
- Password gate: প্রথম পাসওয়ার্ড মালিককে আলাদাভাবে দেওয়া হয়েছে; Settings থেকে পরিবর্তন করা যায়। Browser-এ SHA-256 hash রাখা হয়।
- Live voice session: Bengali/English speech recognition, continuous listening session এবং AI reply voice output।
- Wake-word filter: voice session চালু থাকা অবস্থায় `JARVIS/জার্ভিস` শব্দ শুনে command process করতে পারে।
- Bluetooth headset: Android Settings থেকে আগে headset pair করো; Chrome-এর microphone/speech audio Android-এর current audio route ব্যবহার করতে পারে। Website Bluetooth headset pairing API নয়।
- Settings: AI provider preference, Gemini key, OpenAI key field (শুধু local save; API integration নয়), Local AI endpoint, browser permissions, Contacts picker, file picker, battery/network status।

## গুরুত্বপূর্ণ নিরাপত্তা ও সীমাবদ্ধতা
- GitHub Pages static hosting। এখানে দেওয়া password lock client-side convenience lock, server-side authentication নয়; source inspect বা browser storage clear করে bypass করা সম্ভব। Public/private data সুরক্ষার জন্য backend authentication লাগবে।
- Browser-এ রাখা API key local device storage-এ থাকে, কিন্তু website code/runtime থেকে দেখা সম্ভব। Public GitHub repository-তে key বা `.env` commit করবে না। নিরাপদ API ব্যবহারের জন্য private backend proxy দরকার।
- Contact Picker API সব browser-এ নেই; Android Chrome-এ সমর্থন থাকলে user-selected contact দেখাবে। Full contacts database access-এর জন্য native Android permission ও companion দরকার।
- Files picker শুধু user-selected files নেয়। Android-এর সব ফাইল নিজে থেকে scan করে না।
- Bluetooth audio route ব্যবহার করতে headset আগে Android-এর Bluetooth settings থেকে connect করতে হবে। Website সাধারণ Bluetooth headset নিজে pair করে না।
- Voice recognition, wake word ও voice reply page/browser খোলা থাকা অবস্থায় চলে। Browser বন্ধ, screen locked বা background-এ always-on wake-word guarantee করা যায় না।
- Android-wide SMS, flashlight, volume, background GPS/geofence, notification access, Find My Phone, ESP32 control, PC control ও emergency SOS-এর জন্য native Android companion / trusted local agent / hardware integration দরকার। এগুলো শুধুমাত্র GitHub Pages দিয়ে সম্পূর্ণ করা যায় না।
- GitHub Pages-এ `server.js` চালানো হয় না। তাই Node server APIs বা private backend features আলাদা hosting/server ছাড়া চলবে না।

## স্থানীয়ভাবে চালানো
Node.js থাকলে:
```bash
npm install
npm start
```
তারপর `http://localhost:3000` খুলবে। GitHub Pages-এর জন্য `npm start` দরকার নেই।

## ২০টি ফিচারের বর্তমান অবস্থা

| ফিচার | এই ZIP-এ অবস্থা |
|---|---|
| Wake Word | Page খোলা ও live voice session চালু থাকলে JARVIS শব্দ ফিল্টার করে |
| Continuous Voice | Browser-supported live session যোগ করা হয়েছে |
| Bengali Voice | bn-IN speech recognition / speech output যোগ করা হয়েছে; device/browser support নির্ভর |
| AI Vision | Camera preview আছে; object recognition এখনো সম্পূর্ণ যুক্ত নয় |
| Battery & Network | Browser যে status দেয় তা দেখায়; পূর্ণ Android telemetry নয় |
| GPS & Geofence | Existing page-based GPS / danger-zone features আছে; background reliability-এর জন্য Android service দরকার |
| ESP32 Smart Home | Existing UI আছে; verified end-to-end device connection নয় |
| 3D JARVIS Avatar | Existing animated holographic core আছে; talking 3D avatar নয় |
| Smart Memory | Existing browser-local memory features আছে |
| Find My Phone | UI/নেটিভ কাজ বাকি |
| Flashlight & Volume | Android native implementation বাকি |
| Gemini + ChatGPT + Local AI | Gemini browser integration আছে; ChatGPT external link; Local AI connector বাকি |
| Automation Center | Existing planner/tools; Android background automation বাকি |
| Emergency SOS | Native SMS/call workflow বাকি |
| PC Control | Secure PC agent বাকি |
| Secure Login + 2FA | Changeable client-side password lock আছে; server authentication/2FA বাকি |
| Dashboard Editor | Existing settings/design controls সীমিত; full drag-drop editor বাকি |
| AI PDF Reader | File Vault আছে; full AI PDF extraction/Q&A বাকি |
| Calendar & Reminder | Existing planner/alarm features আছে; background notification guarantee নয় |
| Voice Calculator & Study Assistant | Full end-to-end assistant workflow বাকি |

এই টেবিলটি ইচ্ছাকৃতভাবে বর্তমান বাস্তব অবস্থা দেখায়—যে ফিচার শুধু UI বা আংশিক browser feature, সেটিকে সম্পূর্ণ বলে দাবি করা হয়নি।
