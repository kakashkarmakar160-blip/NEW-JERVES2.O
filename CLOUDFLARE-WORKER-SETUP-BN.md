# JARVIS দ্রুত AI Backend — Cloudflare Workers (ফ্রি প্ল্যান)

এই Worker Gemini API-কে সার্ভারের ভিতরে রাখে এবং উত্তর streaming করে পাঠায়। ফলে Gemini API key browser/localStorage/GitHub-এ রাখতে হয় না।

## ১) Cloudflare Worker তৈরি
1. https://dash.cloudflare.com/ খুলে Free account-এ sign in করো।
2. **Workers & Pages** → **Create** → **Worker** নির্বাচন করো।
3. Worker-এর নাম দাও `jarvis-ai-backend` এবং Deploy করো।
4. Worker-এর **Edit code** খুলে এই ZIP-এর `worker.js` ফাইলের সম্পূর্ণ কোড paste করে Save and Deploy করো।

## ২) Gemini API Key নিরাপদে যোগ
1. Google AI Studio থেকে নিজের Gemini API key তৈরি করো: https://aistudio.google.com/apikey
2. Cloudflare Worker → **Settings** → **Variables and Secrets** খোলো।
3. **Add** → Type **Secret** নির্বাচন করো। Name: `GEMINI_API_KEY`; Value: তোমার API key। Save করো।
4. চাইলে plain-text Variable হিসেবে `GEMINI_MODEL` = `gemini-3.8-flash` যোগ করতে পারো। না দিলে Worker এই default model ব্যবহার করবে।
5. Save and Deploy করো। API key কখনো GitHub-এ বা JARVIS Settings-এ paste করবে না।

## ৩) JARVIS ওয়েবসাইটে Backend URL বসাও
1. Worker deploy হওয়ার পর `https://jarvis-ai-backend.<তোমার-account-subdomain>.workers.dev`-এর মতো URL পাবে।
2. JARVIS GitHub Pages ওয়েবসাইট খোলো → unlock করো → Settings → Secure AI Backend।
3. Worker URL paste করে **SAVE** চাপো। `Connected ✓ • Streaming enabled` দেখালে সংযোগ হয়েছে।
4. Chat-এ একটি সাধারণ প্রশ্ন করে পরীক্ষা করো। উত্তর তৈরি হওয়ার সঙ্গে সঙ্গে লেখা দেখা শুরু হবে।

## ৪) পরীক্ষা
- Worker health URL: `https://তোমার-worker-url/health` খুললে `ok: true` আসবে।
- AI endpoint: ওয়েবসাইট থেকেই POST request পাঠানো হবে; সরাসরি browser address bar-এ খুললে কাজ করবে না।

## খরচ ও সীমা
- Cloudflare Workers Free plan-এর বর্তমান সীমা: দৈনিক 100,000 Worker requests; সীমা পেরোলে request ব্যর্থ হতে পারে। Cloudflare-এর plan/limits ভবিষ্যতে বদলাতে পারে।
- Gemini API-র free-tier model, RPM/TPM/RPD quota এবং দেশ/অ্যাকাউন্টভেদে availability প্রযোজ্য। Free quota শেষ হলে উত্তর বন্ধ বা rate-limit হতে পারে; paid billing চালু করা বাধ্যতামূলক নয়।
- এই Worker শুধু AI proxy। এটি SMS, Android app-control বা native phone permissions দেয় না।

## নিরাপত্তা
- Worker-এ কেবল নির্দিষ্ট GitHub Pages origin অনুমোদিত। নিজের GitHub Pages origin বদলালে `worker.js`-এর `ALLOWED_ORIGIN`-ও বদলাতে হবে।
- API key Worker Secret-এ থাকবে, public source code-এ নয়।
- Public website হলে ভবিষ্যতে login/session token, per-user rate limiting ও abuse protection যোগ করা উচিত।
