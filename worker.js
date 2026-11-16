/**
 * JARVIS Gemini streaming proxy for Cloudflare Workers.
 * Store GEMINI_API_KEY as a Worker secret; never put it in GitHub or browser storage.
 */
const ALLOWED_ORIGIN = 'https://kakashkarmakar160-blip.github.io';

function cors(origin) {
  const allowed = origin === ALLOWED_ORIGIN ? origin : ALLOWED_ORIGIN;
  return {
    'Access-Control-Allow-Origin': allowed,
    'Access-Control-Allow-Methods': 'GET, POST, OPTIONS',
    'Access-Control-Allow-Headers': 'Content-Type',
    'Access-Control-Max-Age': '86400',
    'Vary': 'Origin'
  };
}
function json(data, status, origin) {
  return new Response(JSON.stringify(data), {
    status,
    headers: { ...cors(origin), 'Content-Type': 'application/json; charset=utf-8', 'Cache-Control': 'no-store' }
  });
}

export default {
  async fetch(request, env) {
    const origin = request.headers.get('Origin') || '';
    if (request.method === 'OPTIONS') return new Response(null, { status: 204, headers: cors(origin) });
    const url = new URL(request.url);
    if (url.pathname === '/health' && request.method === 'GET') {
      return json({ ok: true, service: 'JARVIS AI backend', streaming: true }, 200, origin);
    }
    if (url.pathname !== '/api/chat' || request.method !== 'POST') {
      return json({ error: 'Not found' }, 404, origin);
    }
    if (origin && origin !== ALLOWED_ORIGIN) return json({ error: 'Origin not allowed' }, 403, origin);
    if (!env.GEMINI_API_KEY) return json({ error: 'Backend is missing GEMINI_API_KEY secret.' }, 503, origin);

    let body;
    try { body = await request.json(); } catch { return json({ error: 'Invalid JSON request.' }, 400, origin); }
    const prompt = typeof body.prompt === 'string' ? body.prompt.trim() : '';
    if (!prompt) return json({ error: 'Message is empty.' }, 400, origin);
    if (prompt.length > 24000) return json({ error: 'Message is too long. Please shorten it.' }, 413, origin);

    const model = (env.GEMINI_MODEL || 'gemini-3.8-flash').trim();
    const endpoint = `https://generativelanguage.googleapis.com/v1beta/models/${encodeURIComponent(model)}:streamGenerateContent?alt=sse`;
    let upstream;
    try {
      upstream = await fetch(endpoint, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json', 'x-goog-api-key': env.GEMINI_API_KEY },
        body: JSON.stringify({ contents: [{ role: 'user', parts: [{ text: prompt }] }], generationConfig: { temperature: 0.55, maxOutputTokens: 1024 } },),
        signal: request.signal
      });
    } catch {
      return json({ error: 'Gemini could not be reached. Check your connection and try again.' }, 502, origin);
    }
    if (!upstream.ok) {
      const detail = await upstream.text();
      let message = `Gemini request failed (${upstream.status}).`;
      try { message = JSON.parse(detail)?.error?.message || message; } catch {}
      return json({ error: message }, upstream.status === 429 ? 429 : 502, origin);
    }
    const headers = {
      ...cors(origin),
      'Content-Type': 'text/event-stream; charset=utf-8',
      'Cache-Control': 'no-cache, no-transform',
      'X-Accel-Buffering': 'no'
    };
    return new Response(upstream.body, { status: 200, headers });
  }
};
