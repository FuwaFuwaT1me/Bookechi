// Bookechi — concept: flow «продление стрика → результаты» с цепочечной анимацией
const { useState: useF, useEffect: useFE, useRef: useFR } = React;

const T = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentPressed: '#843B22', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  streakBadge: '#F0C9A8', streakG1: '#FBE9D6', goalG1: '#D98E63', goalG2: '#9E4A2C',
  flame: '#BE5E3B', on: '#FFF6EE', serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const STONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const B = { title: 'Норвежский лес', author: 'Харуки Мураками', before: 57, after: 64, beforePage: 182, page: 206, total: 320, delta: 24, streak: 8, prevStreak: 7, mins: 47 };

/* ── timing helpers ── */
const clamp = (v, a, b) => Math.max(a, Math.min(b, v));
const easeOut = (k) => 1 - Math.pow(1 - k, 3);
const easeBack = (k) => { const c = 1.7; return 1 + (c + 1) * Math.pow(k - 1, 3) + c * Math.pow(k - 1, 2); };
const seg = (t, start, dur, ease = easeOut) => ease(clamp((t - start) / dur, 0, 1));

function useTimeline(total) {
  const [t, setT] = useF(0);
  const raf = useFR(0); const start = useFR(null);
  const run = React.useCallback(() => {
    cancelAnimationFrame(raf.current); start.current = null;
    const tick = (now) => {
      if (start.current == null) start.current = now;
      const e = now - start.current; setT(Math.min(total, e));
      if (e < total) raf.current = requestAnimationFrame(tick);
    };
    raf.current = requestAnimationFrame(tick);
  }, [total]);
  useFE(() => { run(); return () => cancelAnimationFrame(raf.current); }, [run]);
  return [t, run];
}

function Flame({ s = 24, c = T.flame }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill={c}><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>; }
function Check({ s = 14, c = '#fff' }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round"><path d="M5 13l4.5 4.5L19 7"/></svg>; }

/* ══════════ PHASE 1 · STREAK CELEBRATION (in-your-face) ══════════ */
function StreakScreen({ onDone }) {
  const [t, replay] = useTimeline(4000);
  useFE(() => { const id = setTimeout(onDone, 3850); return () => clearTimeout(id); }, []);

  const easeInOut = (k) => (k < 0.5 ? 4 * k * k * k : 1 - Math.pow(-2 * k + 2, 3) / 2);
  const flameK = seg(t, 150, 650, easeBack);
  const groupK = seg(t, 550, 400);            // number group fades/scales in (shows prevStreak)
  const rollRaw = seg(t, 1250, 950, easeInOut); // old rolls up, new rolls in
  const badgeK = seg(t, 1500, 550, easeBack);   // «+1» pop
  const settleK = seg(t, 2050, 350, easeBack);  // tiny bounce when new lands
  const num = rollRaw < 0.5 ? B.prevStreak : B.streak;
  const titleK = seg(t, 2150, 500);
  const dotsBase = 2350;
  const days = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс'];
  const rays = 12;

  return (
    <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center',
      background: `radial-gradient(120% 80% at 50% 38%, ${T.accent}, ${T.accentPressed})`, color: T.on, textAlign: 'center', padding: 32, overflow: 'hidden' }}>
      {/* rays */}
      <div style={{ position: 'absolute', top: '34%', left: '50%', width: 0, height: 0 }}>
        {Array.from({ length: rays }).map((_, i) => {
          const rk = seg(t, 200 + i * 18, 600);
          return <div key={i} style={{ position: 'absolute', width: 3, height: 56, borderRadius: 3, background: 'rgba(255,246,238,0.5)',
            transform: `rotate(${i * (360 / rays)}deg) translateY(${-66 - rk * 26}px) scaleY(${rk})`, transformOrigin: 'center top', opacity: (1 - seg(t, 1100, 700)) * 0.9 }}></div>;
        })}
      </div>
      {/* flame */}
      <div style={{ transform: `scale(${flameK})`, marginBottom: 26, filter: 'drop-shadow(0 12px 30px rgba(0,0,0,0.3))' }}>
        <div style={{ width: 130, height: 130, borderRadius: 44, background: 'rgba(255,246,238,0.16)', display: 'flex', alignItems: 'center', justifyContent: 'center', border: '1.5px solid rgba(255,246,238,0.3)' }}>
          <Flame s={74} c={T.on} />
        </div>
      </div>
      {/* streak number — vertical roll prevStreak → streak */}
      {(() => {
        const NH = 104; // roller window height
        const landBounce = 1 + 0.06 * Math.sin(settleK * Math.PI);
        const numStyle = { fontFamily: T.serif, fontWeight: 600, fontSize: 96, lineHeight: NH + 'px', height: NH, display: 'flex', alignItems: 'center', justifyContent: 'center' };
        return (
          <div style={{ position: 'relative', display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 14,
            opacity: groupK, transform: `scale(${(0.7 + groupK * 0.3) * landBounce})` }}>
            <div style={{ height: NH, overflow: 'hidden', position: 'relative' }}>
              <div style={{ transform: `translateY(${-rollRaw * NH}px)` }}>
                <div style={numStyle}>{B.prevStreak}</div>
                <div style={numStyle}>{B.streak}</div>
              </div>
            </div>
            {/* «+1» badge */}
            <div style={{ position: 'absolute', left: '50%', top: 6, transform: `translateX(46px) scale(${badgeK})`, opacity: badgeK * (1 - seg(t, 2700, 500)),
              display: 'flex', alignItems: 'center', gap: 3, background: T.on, color: T.accentDeep, borderRadius: 999, padding: '4px 11px', fontFamily: T.sans, fontSize: 16, fontWeight: 800, boxShadow: '0 6px 18px rgba(0,0,0,0.22)' }}>+1</div>
          </div>
        );
      })()}
      <div style={{ fontFamily: T.serif, fontWeight: 600, fontSize: 30, marginTop: 6, opacity: titleK, transform: `translateY(${(1 - titleK) * 12}px)` }}>{plural(num, 'день', 'дня', 'дней')} подряд!</div>
      <div style={{ fontSize: 15.5, opacity: titleK * 0.92, marginTop: 8, transform: `translateY(${(1 - titleK) * 12}px)` }}>Серия продолжается. Так держать.</div>
      {/* week dots — appear after the counter settles, lit one-by-one */}
      <div style={{ display: 'flex', gap: 12, marginTop: 30 }}>
        {days.map((d, i) => {
          const on = i <= 1;            // Пн, Вт отмечены
          const today = i === 1;        // сегодня — Вт
          const dk = seg(t, dotsBase + i * 110, 420, easeBack);
          const appear = seg(t, dotsBase + i * 110, 420);
          return (
            <div key={d} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6, fontSize: 10.5, fontWeight: today ? 800 : 600, opacity: 0.5 + 0.5 * appear, transform: `translateY(${(1 - appear) * 8}px)` }}>
              <span>{d}</span>
              <div style={{ position: 'relative', width: 28, height: 28, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
                background: on ? T.on : 'transparent',
                border: on ? 'none' : '1.6px solid rgba(255,246,238,0.5)',
                boxShadow: today ? '0 0 0 2.5px ' + T.accentPressed + ', 0 0 0 4.5px ' + T.on + ', 0 0 16px rgba(255,246,238,0.6)' : 'none',
                transform: `scale(${on ? dk : appear})` }}>
                {on && <Check s={13} c={T.accentDeep} />}
              </div>
              <span style={{ height: 12, fontSize: 9, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', opacity: today ? appear : 0 }}>{today ? 'сегодня' : ''}</span>
            </div>
          );
        })}
      </div>
      <div style={{ position: 'absolute', bottom: 26, fontSize: 12.5, opacity: seg(t, 3300, 500) * 0.8 }}>Нажмите, чтобы продолжить</div>
      <div onClick={(e) => { e.stopPropagation(); onDone(); }} style={{ position: 'absolute', inset: 0 }}></div>
    </div>
  );
}

/* ── animated curly brace (left-facing «{»), height-driven ── */
function bracePath(bw, h) {
  const m = bw / 2;
  return `M ${bw},0 Q ${m},0 ${m},${h * 0.16} L ${m},${h * 0.40} Q ${m},${h / 2} 0,${h / 2}`
    + ` Q ${m},${h / 2} ${m},${h * 0.60} L ${m},${h * 0.84} Q ${m},${h} ${bw},${h}`;
}

/* ── small ticking clock ── */
function MiniClock({ size = 17, color = T.accentDeep }) {
  const [a, setA] = useF(0);
  useFE(() => {
    let raf; const t0 = performance.now();
    const tick = (now) => { setA(((now - t0) / 1000 * 150) % 360); raf = requestAnimationFrame(tick); };
    raf = requestAnimationFrame(tick); return () => cancelAnimationFrame(raf);
  }, []);
  const r = size / 2;
  const rad = (a - 90) * Math.PI / 180;
  const hx = r + Math.cos(rad) * r * 0.52, hy = r + Math.sin(rad) * r * 0.52;
  const radM = (a / 12 - 90) * Math.PI / 180;
  const mx = r + Math.cos(radM) * r * 0.34, my = r + Math.sin(radM) * r * 0.34;
  return (
    <svg width={size} height={size} viewBox={`0 0 ${size} ${size}`} style={{ flex: 'none' }}>
      <circle cx={r} cy={r} r={r - 1.1} fill="none" stroke={color} strokeWidth="1.7" />
      <line x1={r} y1={r} x2={mx} y2={my} stroke={color} strokeWidth="1.7" strokeLinecap="round" opacity="0.55" />
      <line x1={r} y1={r} x2={hx} y2={hy} stroke={color} strokeWidth="1.7" strokeLinecap="round" />
      <circle cx={r} cy={r} r="1.1" fill={color} />
    </svg>
  );
}

/* ── cover with start-point + gain segment + growing brace ── */
function CoverHero({ p, braceO, before = B.before, after = B.after, delta = B.delta }) {
  const W = 132, H = Math.round(W * 1.45), r = 16;
  const cur = before + (after - before) * p;                // animated waterline %
  const dimFrac = 1 - cur / 100;                             // unread above
  const beforeY = H * (1 - before / 100);                    // px from top: start line
  const curY = H * (1 - cur / 100);                          // px from top: current line
  const braceH = Math.max(2, beforeY - curY);                // gain span (grows with p)
  const bw = 11, gap = 12;
  return (
    <div style={{ position: 'relative', width: W, height: H, filter: 'drop-shadow(0 18px 34px rgba(56,42,32,0.45))' }}>
      <div style={{ width: W, height: H, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`, color: STONE.fg,
        padding: '14px 11px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
        <div style={{ position: 'absolute', left: W * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: T.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 14, paddingLeft: W * 0.1 }}>{B.title}</div>
        <div style={{ fontSize: 9, opacity: 0.85, paddingLeft: W * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{B.author}</div>
        {/* unread dim */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: 0, height: dimFrac * 100 + '%', background: 'color-mix(in oklab,' + T.canvas + ' 64%, transparent)', borderRadius: `${r}px ${r}px 0 0` }}></div>
        {/* gain band (before → cur) */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: braceH, background: `linear-gradient(0deg, color-mix(in oklab,${T.accent} 42%, transparent), color-mix(in oklab,${T.accent} 10%, transparent))` }}></div>
      </div>

      {/* growing brace embracing today's gain */}
      <div style={{ position: 'absolute', left: -(bw + gap), top: curY, width: bw, height: braceH, opacity: braceO }}>
        <svg width={bw} height={braceH} viewBox={`0 0 ${bw} ${braceH}`} style={{ overflow: 'visible' }}>
          <path d={bracePath(bw, braceH)} fill="none" stroke={T.accentDeep} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
        </svg>
        {/* gain label on the brace — clean chip */}
        <div style={{ position: 'absolute', right: bw + 7, top: '50%', transform: 'translateY(-50%)', whiteSpace: 'nowrap',
          background: T.accent, color: T.on, borderRadius: 999, padding: '3px 10px', fontSize: 12, fontWeight: 700,
          boxShadow: '0 3px 9px -2px rgba(132,59,34,0.5)' }}>+{delta} стр</div>
      </div>

      {/* current waterline + flag */}
      <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: 3, background: T.accent, boxShadow: '0 0 0 1px ' + T.surfacePure, opacity: p > 0.02 ? 1 : 0 }}></div>
    </div>
  );
}

/* ── fire-blazing chip — the chip's own top edge morphs into flame ── */
/* ══════════ PHASE 2 · RESULTS (chained reveal) ══════════ */
function ResultsScreen({ over, onDone }) {
  const D = { ...B, ...over };
  const [t, replay] = useTimeline(3900);
  useFE(() => { if (!onDone) return; const id = setTimeout(onDone, 3950); return () => clearTimeout(id); }, []);
  // chained timings: each starts when previous finishes
  const coverK = seg(t, 0, 550, easeBack);
  const braceO = seg(t, 1000, 400);
  const fillP = seg(t, 1000, 850);
  const pctK = seg(t, 1100, 900);                 // 57 → 64 count-up, synced with fill
  const pctNow = Math.round(D.before + (D.after - D.before) * pctK);
  const pctShowK = seg(t, 1100, 500, easeBack);   // «N% книги позади» springs in with fill
  const heroK = seg(t, 2150, 650, easeBack);      // «+7%» accent springs in
  const shineK = seg(t, 2600, 700);               // light sweep across the badge
  const minK = seg(t, 3000, 500);
  const btnK = seg(t, 3400, 450);

  return (
    <div onClick={replay} style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', padding: 28, cursor: 'pointer', background: T.canvas }}>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
        <div style={{ transform: `scale(${coverK})`, opacity: clamp(coverK, 0, 1), marginBottom: 32, paddingLeft: 30 }}>
          <CoverHero p={fillP} braceO={braceO} before={D.before} after={D.after} delta={D.delta} />
        </div>

        {/* hero accent: big «+7%» springs in + light sweep */}
        <div style={{ opacity: clamp(heroK, 0, 1), transform: `translateY(${(1 - heroK) * 40}px) scale(${0.6 + heroK * 0.4})` }}>
          <span style={{ position: 'relative', overflow: 'hidden', display: 'inline-flex', alignItems: 'center', gap: 9, background: `linear-gradient(135deg, ${T.goalG1}, ${T.goalG2})`, color: T.on, borderRadius: 999, padding: '9px 11px 9px 20px', fontFamily: T.serif, fontWeight: 700, fontSize: 40, lineHeight: 1, boxShadow: '0 12px 26px -8px rgba(132,59,34,0.6)' }}>
            +{D.after - D.before}%
            <span style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 34, height: 34, borderRadius: '50%', background: 'rgba(255,246,238,0.22)' }}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={T.on} strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><path d="M12 19V5M6 11l6-6 6 6"/></svg>
            </span>
            {/* light sweep */}
            <span style={{ position: 'absolute', top: 0, bottom: 0, width: '45%', left: (-60 + shineK * 200) + '%',
              background: 'linear-gradient(100deg, transparent, rgba(255,246,238,0.5), transparent)',
              opacity: shineK > 0 && shineK < 1 ? 1 : 0, transform: 'skewX(-18deg)', pointerEvents: 'none' }}></span>
          </span>
          <div style={{ fontSize: 13.5, color: T.text2, fontWeight: 600, marginTop: 8 }}>прогресс за сессию</div>
        </div>

        {/* «N% книги позади» — counts up with the fill, springs in just below */}
        <div style={{ opacity: clamp(pctShowK, 0, 1), transform: `translateY(${(1 - clamp(pctShowK, 0, 1)) * 10}px) scale(${0.85 + clamp(pctShowK, 0, 1) * 0.15})`, marginTop: 16 }}>
          <span style={{ fontFamily: T.serif, fontWeight: 700, fontSize: 30, color: T.accentDeep }}>{pctNow}%</span>
          <span style={{ fontSize: 14, color: T.text2, marginLeft: 7 }}>книги позади</span>
        </div>

        {/* one unified stats row: minutes | page — replaces two stacked pills */}
        <div style={{ opacity: minK, transform: `translateY(${(1 - minK) * 12}px)`, marginTop: 22, display: 'flex', alignItems: 'stretch', background: T.cardTint, border: '1px solid ' + T.stroke, borderRadius: 18, overflow: 'hidden' }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 9, padding: '12px 18px' }}>
            <MiniClock size={18} color={T.accentDeep} />
            <div style={{ textAlign: 'left', lineHeight: 1.15 }}>
              <div style={{ fontFamily: T.serif, fontSize: 18, fontWeight: 700 }}>{D.mins} мин</div>
              <div style={{ fontSize: 11, color: T.text2, fontWeight: 600 }}>сессия</div>
            </div>
          </div>
          <div style={{ width: 1, background: T.divider, margin: '10px 0' }}></div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 9, padding: '12px 18px' }}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke={T.accentDeep} strokeWidth="1.9" strokeLinecap="round" strokeLinejoin="round" style={{ flex: 'none' }}><path d="M12 6c-1.5-1.6-4-2.5-8-2.5v14c4 0 6.5.9 8 2.5 1.5-1.6 4-2.5 8-2.5v-14c-4 0-6.5.9-8 2.5z"/><path d="M12 6v14"/></svg>
            <div style={{ textAlign: 'left', lineHeight: 1.15 }}>
              <div style={{ fontFamily: T.serif, fontSize: 18, fontWeight: 700 }}>{D.page}/{D.total}</div>
              <div style={{ fontSize: 11, color: T.text2, fontWeight: 600 }}>страница</div>
            </div>
          </div>
        </div>
      </div>
      <button style={{ opacity: btnK, transform: `translateY(${(1 - btnK) * 12}px)`, height: 52, borderRadius: 18, border: 'none', background: T.accent, color: T.on, fontSize: 16, fontWeight: 600, fontFamily: T.sans, width: '100%', cursor: 'pointer' }}>{onDone ? 'Дальше' : 'Готово'}</button>
    </div>
  );
}

function plural(n, one, few, many) { const a = n % 10, b = n % 100; if (a === 1 && b !== 11) return one; if (a >= 2 && a <= 4 && (b < 12 || b > 14)) return few; return many; }

/* ── full-colour cover for the finished state (no dim) ── */
function CoverDone({ w = 138, pop = 1 }) {
  const W = w, H = Math.round(W * 1.45), r = 16;
  return (
    <div style={{ position: 'relative', width: W, height: H, transform: `scale(${pop})`, filter: 'drop-shadow(0 20px 38px rgba(56,42,32,0.5))' }}>
      <div style={{ width: W, height: H, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`, color: STONE.fg,
        padding: '15px 12px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
        <div style={{ position: 'absolute', left: W * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: T.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 15, paddingLeft: W * 0.1 }}>{B.title}</div>
        <div style={{ fontSize: 9, opacity: 0.85, paddingLeft: W * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{B.author}</div>
      </div>
      {/* «прочитано» sash */}
      <div style={{ position: 'absolute', right: -8, top: 16, background: `linear-gradient(135deg, ${T.goalG1}, ${T.goalG2})`, color: T.on, fontSize: 11, fontWeight: 800, letterSpacing: '0.04em', textTransform: 'uppercase', padding: '5px 12px', borderRadius: 999, boxShadow: '0 6px 16px -4px rgba(132,59,34,0.6)' }}>прочитано</div>
    </div>
  );
}

/* ══════════ PHASE 2b · BOOK FINISHED ══════════ */
function FinishScreen() {
  const [t, replay] = useTimeline(4200);
  const coverK = seg(t, 100, 650, easeBack);
  const titleK = seg(t, 700, 500);
  const subK = seg(t, 1000, 500);
  const starsBase = 1400;
  const statK = seg(t, 2300, 500);
  const btnK = seg(t, 2650, 450);
  const [rating, setRating] = useF(0);

  // confetti pieces
  const conf = React.useMemo(() => Array.from({ length: 26 }).map((_, i) => ({
    x: (i * 53) % 100, dur: 2400 + (i % 5) * 360, delay: (i % 9) * 70,
    rot: (i * 47) % 360, col: [T.accent, T.goalG1, T.goalG2, T.sage, T.accentSoft][i % 5], w: 6 + (i % 3) * 2,
  })), []);

  return (
    <div onClick={replay} style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', padding: 28, cursor: 'pointer', background: `radial-gradient(125% 80% at 50% 16%, color-mix(in oklab, ${T.accentSoft} 45%, ${T.canvas}), ${T.canvas} 62%)` }}>
      {/* confetti */}
      <div style={{ position: 'absolute', inset: 0, overflow: 'hidden', pointerEvents: 'none' }}>
        {conf.map((c, i) => {
          const lt = Math.max(0, t - c.delay);
          const prog = (lt % c.dur) / c.dur;
          const y = -20 + prog * 820;
          const op = t < c.delay ? 0 : (prog > 0.85 ? (1 - prog) / 0.15 : 1);
          return <div key={i} style={{ position: 'absolute', left: c.x + '%', top: y, width: c.w, height: c.w * 1.6, borderRadius: 2, background: c.col, opacity: op * 0.9, transform: `rotate(${c.rot + prog * 540}deg)` }}></div>;
        })}
      </div>

      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', position: 'relative' }}>
        <div style={{ opacity: clamp(coverK, 0, 1), marginBottom: 26 }}><CoverDone pop={clamp(coverK, 0, 1)} /></div>
        <div style={{ opacity: titleK, transform: `translateY(${(1 - titleK) * 14}px)`, display: 'inline-flex', alignItems: 'center', gap: 8, fontFamily: T.serif, fontWeight: 700, fontSize: 30, color: T.accentDeep }}>
          Книга прочитана
        </div>
        <div style={{ opacity: subK, transform: `translateY(${(1 - subK) * 12}px)`, color: T.text2, fontSize: 14.5, marginTop: 8 }}>«{B.title}» · {B.total} страниц позади</div>

        {/* rating */}
        <div style={{ marginTop: 22, display: 'flex', gap: 10 }}>
          {[1, 2, 3, 4, 5].map((s) => {
            const sk = seg(t, starsBase + (s - 1) * 110, 380, easeBack);
            const filled = s <= rating;
            return (
              <button key={s} onClick={(e) => { e.stopPropagation(); setRating(s); }} style={{ background: 'none', border: 'none', padding: 0, cursor: 'pointer', transform: `scale(${sk})` }}>
                <svg width="34" height="34" viewBox="0 0 24 24" fill={filled ? T.accent : 'none'} stroke={filled ? T.accent : T.divider} strokeWidth="1.6" strokeLinejoin="round"><path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z"/></svg>
              </button>
            );
          })}
        </div>
        <div style={{ opacity: seg(t, starsBase, 500), fontSize: 12.5, color: T.text2, marginTop: 8 }}>{rating ? 'Ваша оценка: ' + rating + '/5' : 'Оцените книгу'}</div>

        {/* finish stat */}
        <div style={{ opacity: statK, transform: `translateY(${(1 - statK) * 12}px)`, marginTop: 20, display: 'inline-flex', alignItems: 'center', gap: 8, background: T.cardTint, border: '1px solid ' + T.stroke, borderRadius: 999, padding: '8px 16px', fontSize: 13.5, fontWeight: 600 }}>
          <BkFlame /> 12-я книга в этом году
        </div>
      </div>
      <button style={{ opacity: btnK, transform: `translateY(${(1 - btnK) * 12}px)`, height: 52, borderRadius: 18, border: 'none', background: T.accent, color: T.on, fontSize: 16, fontWeight: 600, fontFamily: T.sans, width: '100%', cursor: 'pointer' }}>На полку «Прочитано»</button>
    </div>
  );
}
function BkFlame() { return <svg width="16" height="16" viewBox="0 0 24 24" fill={T.flame} style={{ flex: 'none' }}><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>; }

function Flow({ mode }) {
  const [phase, setPhase] = useF('streak');
  const [k, setK] = useF(0);
  useFE(() => { setPhase('streak'); setK((x) => x + 1); }, [mode]);
  const replay = () => { setPhase('streak'); setK((x) => x + 1); };
  const finishedData = { before: 84, after: 100, delta: 51, page: B.total, total: B.total, mins: 52 };
  let screen;
  if (phase === 'streak') screen = <StreakScreen key={k} onDone={() => setPhase('results')} />;
  else if (mode === 'finished') {
    screen = phase === 'results'
      ? <ResultsScreen key={'rf' + k} over={finishedData} onDone={() => setPhase('finish')} />
      : <FinishScreen key={'f' + k} />;
  } else screen = <ResultsScreen key={'r' + k} />;
  return (
    <div style={{ position: 'relative', width: 390, height: 760, fontFamily: T.sans, color: T.text, background: T.canvas, overflow: 'hidden' }}>
      {screen}
      <button onClick={replay} style={{ position: 'absolute', top: 12, right: 12, zIndex: 9, height: 30, padding: '0 12px', borderRadius: 999, border: 'none', background: 'rgba(56,42,32,0.28)', color: '#fff', fontSize: 12, fontWeight: 600, cursor: 'pointer' }}>↻ Заново</button>
    </div>
  );
}

function App() {
  const [mode, setMode] = useF('progress');
  return (
    <div style={{ minHeight: '100vh', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', padding: 32, gap: 16 }}>
      <div style={{ textAlign: 'center' }}>
        <h1 style={{ fontFamily: T.serif, fontWeight: 600, fontSize: 24, margin: 0, color: T.text }}>Стрик → результаты</h1>
        <p style={{ color: T.text2, fontSize: 14, margin: '6px 0 12px' }}>Переключите финал · ↻ чтобы повторить анимацию</p>
        <div style={{ display: 'inline-flex', background: T.chip, borderRadius: 999, padding: 4, gap: 2 }}>
          {[['progress', 'Прогресс'], ['finished', 'Книга прочитана']].map(([m, label]) => (
            <button key={m} onClick={() => setMode(m)} style={{ border: 'none', cursor: 'pointer', borderRadius: 999, padding: '8px 16px', fontSize: 13.5, fontWeight: 600, fontFamily: T.sans,
              background: mode === m ? T.surfacePure : 'transparent', color: mode === m ? T.text : T.text2, boxShadow: mode === m ? '0 1px 4px rgba(56,42,32,0.12)' : 'none' }}>{label}</button>
          ))}
        </div>
      </div>
      <div style={{ borderRadius: 32, overflow: 'hidden', boxShadow: '0 30px 70px -28px rgba(56,42,32,0.5)' }}><Flow mode={mode} /></div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
