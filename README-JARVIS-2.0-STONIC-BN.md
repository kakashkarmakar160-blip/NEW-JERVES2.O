# JARVIS 2.0 — Stonic Workspace Update

এই আপডেটে মূল JARVIS প্রজেক্টের ফাইল ও পুরোনো মডিউল রেখে নতুন responsive Stonic-inspired Agent Workspace যোগ করা হয়েছে।

## নতুন ইন্টারফেস
- বাম পাশে animated globe, browser permission-ভিত্তিক live location, coordinates ও map preview।
- মাঝখানে video/core panel, Visual Hub, Agent Workspace এবং Memory/Skills/Character/Settings shortcuts।
- ডান পাশে Voice / Agent / Notes tabs-সহ chat board।
- Landscape/fullscreen control; মোবাইলে portrait ও landscape layout আলাদাভাবে সাজানো।

## AI ও Visual Hub
- Chat backend URL ডিফল্টভাবে এই Worker-এ সেট করা: `https://little-forest-7b75.kakashkarmakar159.workers.dev`
- Chat response Cloudflare Worker-এর `/api/chat` streaming endpoint ব্যবহার করে।
- Mermaid fenced output পেলে diagram render হয়।
- HTML code block পেলে sandboxed iframe-এ preview হয়।
- Text/code files attach করে prompt-এর context হিসেবে পাঠানো যায়। PDF/image attachment-এর পূর্ণ OCR/vision extraction এখনো যুক্ত নয়।
- Agent cards বর্তমান request-এর workflow status দেখায়; এগুলো আলাদা autonomous AI agents নয়।

## Custom command + link
Settings → COMMAND & LINK-এ command এবং HTTPS/HTTP URL যোগ করো। Chat board-এ command-এর নাম হুবহু লিখলে লিংক খুলবে।

## Core video control
Settings → CORE VIDEO CONTROL-এ video URL সেট, volume/mute, play/pause করা যায়। স্থানীয় video file নির্বাচন করলে তা বর্তমান browser session-এ চলে; reload-এর পর আবার নির্বাচন করতে হতে পারে।

## Location & route
- `my life location` বা `আমার লাইভ লোকেশন` বললে browser geolocation permission নিয়ে বর্তমান অবস্থান দেখায়।
- Chat-এ coordinates দিয়ে route/দূরত্ব চাইলে আগের JARVIS Live GPS Navigation module খোলার চেষ্টা করে এবং destination coordinates বসায়। GPS permission ও internet প্রয়োজন।

## GitHub Pages আপলোড
1. ZIP extract করো।
2. ZIP-এর ভেতরের `NEW-JERVES2.O-main` ফোল্ডারের সব ফাইল GitHub repository-র root-এ আপলোড করো (ফোল্ডারের ভিতরে আরেকটি nested folder যেন না থাকে)।
3. Commit changes করো।
4. GitHub Pages reload হলে নতুন interface দেখা যাবে। Browser cache থাকলে hard refresh/clear site data করো।

## গুরুত্বপূর্ণ সীমাবদ্ধতা
- Browser location শুধু user permission দিলে এবং page চালু থাকলে কাজ করে; background geofence-এর জন্য Android companion প্রয়োজন।
- Voice recognition browser support-এর ওপর নির্ভর করে।
- Native Android notifications, contacts, phone file browsing, app control, flashlight, volume, wake word এবং full background automation-এর জন্য Android companion app ও permissions দরকার।
- PC Control এই build-এ ইচ্ছাকৃতভাবে যোগ করা হয়নি (আগের সিদ্ধান্ত অনুযায়ী later)।
- Gemini API Key কখনো GitHub বা browser-এ রাখবে না; Cloudflare Worker Secret-এ রাখবে। আগের screenshot-এ প্রকাশিত key revoke করে নতুন key ব্যবহার করো।
