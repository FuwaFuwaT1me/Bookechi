// Bookechi — Журнал чтения (вариант A), полная спецификация: все кадры, свет + тёмная
const { useState: useJ } = React;

const THEMES = {
  light: {
    canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF', stroke: '#E4D9CC', divider: '#D9CCBC',
    text: '#382A20', text2: '#8C7C6E', accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
    sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2', goalG1: '#D98E63', goalG2: '#9E4A2C',
    on: '#FFF6EE', del: '#C9543A', scrim: 'rgba(56,42,32,0.42)',
  },
  dark: {
    canvas: '#1C1611', surface: '#261D17', surfacePure: '#31261E', stroke: '#3C2F26', divider: '#45362B',
    text: '#F0E7DC', text2: '#AE9D8E', accent: '#CE6E48', accentDeep: '#E08960', accentSoft: '#4A3023',
    sage: '#8FA07E', sageSoft: '#2E3326', chip: '#31261E', cardTint: '#2E231B', goalG1: '#E08960', goalG2: '#B4583A',
    on: '#FFF1E6', del: '#D8694B', scrim: 'rgba(10,7,4,0.55)',
  },
};
const serif = "'Lora', Georgia, serif";

const COVERS = {
  norway: { bg: '#8C6E54', fg: '#F6EFE6', ini: 'НЛ' },
  kahneman: { bg: '#A08B7C', fg: '#F7F1E8', ini: 'ДК' },
  life: { bg: '#BE5E3B', fg: '#FBEEE4', ini: 'МЖ' },
  rose: { bg: '#5C6B5E', fg: '#EDF1EC', ini: 'ИР' },
};
const SESSIONS = [
  { id: 1, book: 'norway', title: 'Норвежский лес', author: 'Харуки Мураками', from: 182, to: 206, mins: 47, pct: 7, date: 'Сегодня', time: '21:30', last: true },
  { id: 2, book: 'kahneman', title: 'Думай медленно… решай быстро', author: 'Даниэль Канеман', from: 96, to: 120, mins: 32, pct: 5, date: 'Сегодня', time: '08:15', last: true },
  { id: 3, book: 'norway', title: 'Норвежский лес', author: 'Харуки Мураками', from: 160, to: 182, mins: 38, pct: 7, date: 'Вчера', time: '22:05', last: false },
  { id: 4, book: 'life', title: 'Маленькая жизнь', author: 'Ханья Янагихара', from: 0, to: 18, mins: 21, pct: 3, date: 'Вчера', time: '13:40', last: true },
  { id: 5, book: 'kahneman', title: 'Думай медленно… решай быстро', author: 'Даниэль Канеман', from: 74, to: 96, mins: 29, pct: 5, date: 'На этой неделе', time: '7 июня', last: false },
  { id: 6, book: 'rose', title: 'Имя розы', author: 'Умберто Эко', from: 40, to: 72, mins: 51, pct: 5, date: 'На этой неделе', time: '5 июня', last: true },
];
const GROUPS = ['Сегодня', 'Вчера', 'На этой неделе'];

/* ── atoms ── */
function Cover({ book, w = 44, r = 8, T }) {
  const c = COVERS[book]; const h = Math.round(w * 1.5);
  return (
    <div style={{ width: w, height: h, borderRadius: r, flex: 'none', position: 'relative', overflow: 'hidden',
      background: `linear-gradient(158deg, ${c.bg}, color-mix(in oklab, ${c.bg} 74%, #000))`, color: c.fg,
      boxShadow: 'inset -2px 0 4px -1px rgba(0,0,0,0.3), 0 2px 6px -2px rgba(56,42,32,0.4)',
      display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
      <div style={{ position: 'absolute', left: w * 0.09, top: 0, bottom: 0, width: 1.2, background: 'rgba(255,255,255,0.22)' }}></div>
      <span style={{ fontFamily: serif, fontWeight: 600, fontSize: w * 0.34, letterSpacing: '0.02em' }}>{c.ini}</span>
    </div>
  );
}
function ClockIc({ s = 13, c, T }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c || T.text2} strokeWidth="2" strokeLinecap="round"><circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/></svg>; }
function GainChip({ pages, T, sm }) {
  return <span style={{ display: 'inline-flex', alignItems: 'center', background: `linear-gradient(135deg, ${T.goalG1}, ${T.goalG2})`, color: T.on, borderRadius: 999, padding: sm ? '2px 9px' : '3px 10px', fontSize: sm ? 11.5 : 12.5, fontWeight: 700, boxShadow: '0 2px 7px -2px rgba(132,59,34,0.5)' }}>+{pages} стр</span>;
}
function Dots({ T }) { return <svg width="18" height="18" viewBox="0 0 24 24" fill={T.text2}><circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/></svg>; }
function LastTag({ T }) {
  return <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3, background: T.accentSoft, color: T.accentDeep, borderRadius: 999, padding: '1px 8px 1px 6px', fontSize: 10, fontWeight: 700, letterSpacing: '0.03em', textTransform: 'uppercase' }}>
    <svg width="11" height="11" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2c-3.9 0-7 3-7 6.9 0 4.7 5.7 10.6 6.3 11.2.4.4 1 .4 1.4 0 .6-.6 6.3-6.5 6.3-11.2C19 5 15.9 2 12 2zm0 9.4a2.4 2.4 0 1 1 0-4.8 2.4 2.4 0 0 1 0 4.8z"/></svg>
    текущая
  </span>;
}

/* ── one session row ── */
function Row({ s, T, swipe = 0 }) {
  return (
    <div style={{ position: 'relative' }}>
      {swipe > 0 && (
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
          <button style={{ width: 60, height: 60, borderRadius: 16, border: 'none', background: T.del, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 2, color: '#fff', cursor: 'pointer', boxShadow: '0 6px 16px -4px rgba(132,59,34,0.5)' }}>
            <svg width="19" height="19" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 7h14M9 7V5h6v2M7 7l1 13h8l1-13"/></svg>
            <span style={{ fontSize: 9.5, fontWeight: 700 }}>Удалить</span>
          </button>
        </div>
      )}
      <div style={{ position: 'relative', display: 'flex', gap: 13, alignItems: 'center', background: T.surface, border: '1px solid ' + T.stroke, borderRadius: 18, padding: 12, transform: swipe ? `translateX(-72px)` : 'none', transition: 'transform .3s cubic-bezier(.32,.72,.28,1)', boxShadow: swipe ? '0 8px 22px -10px rgba(56,42,32,0.4)' : 'none' }}>
        <Cover book={s.book} T={T} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
            <span style={{ fontSize: 14, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', flex: '0 1 auto' }}>{s.title}</span>
            {s.last && <LastTag T={T} />}
          </div>
          <div style={{ fontSize: 10.5, fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', color: T.text2, marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.author}</div>
          <div style={{ display: 'flex', alignItems: 'center', gap: 7, marginTop: 9 }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: T.chip, borderRadius: 8, padding: '3px 9px', fontSize: 12, fontWeight: 600 }}>
              <PagesIc T={T} /> {s.from}–{s.to}
            </span>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: T.chip, borderRadius: 8, padding: '3px 9px', fontSize: 12, fontWeight: 600 }}>
              <ClockIc T={T} c={T.text} /> {s.mins} мин
            </span>
          </div>
        </div>
        {/* hero metric + time */}
        <div style={{ flex: 'none', textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', alignSelf: 'stretch', justifyContent: 'space-between' }}>
          <span style={{ fontSize: 11, color: T.text2 }}>{s.time}</span>
          <div style={{ display: 'flex', alignItems: 'baseline', gap: 3 }}>
            <span style={{ fontFamily: serif, fontSize: 23, fontWeight: 700, color: T.accentDeep, lineHeight: 1 }}>+{s.to - s.from}</span>
            <span style={{ fontSize: 10.5, fontWeight: 600, color: T.text2 }}>стр</span>
          </div>
        </div>
      </div>
    </div>
  );
}
function PagesIc({ T }) { return <svg width="13" height="13" viewBox="0 0 24 24" fill="none" stroke={T.text} strokeWidth="1.9" strokeLinecap="round" strokeLinejoin="round"><path d="M12 6c-1.5-1.6-4-2.5-8-2.5v14c4 0 6.5.9 8 2.5 1.5-1.6 4-2.5 8-2.5v-14c-4 0-6.5.9-8 2.5z"/><path d="M12 6v14"/></svg>; }

/* ── phone shell ── */
function Phone({ T, children, scrim }) {
  return (
    <div style={{ position: 'relative', width: 384, height: 740, background: T.canvas, color: T.text, fontFamily: "'Inter', system-ui, sans-serif", overflow: 'hidden', display: 'flex', flexDirection: 'column' }}>
      {children}
      {scrim}
    </div>
  );
}
function Header({ T, title, sub, big, help }) {
  return (
    <header style={{ padding: '20px 20px 12px', flex: 'none' }}>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12 }}>
        <button style={{ width: 40, height: 40, borderRadius: '50%', border: 'none', background: T.chip, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', flex: 'none' }}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={T.text} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M15 5l-7 7 7 7"/></svg>
        </button>
        <div style={{ flex: 1 }}>
          {sub && <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: '0.06em', textTransform: 'uppercase', color: T.text2 }}>{sub}</div>}
          <h1 style={{ fontFamily: serif, fontWeight: 600, fontSize: big ? 24 : 22, margin: '2px 0 0' }}>{title}</h1>
        </div>
        {help && (
          <button style={{ width: 40, height: 40, borderRadius: '50%', border: 'none', background: help === 'pulse' ? T.accentSoft : T.chip, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', flex: 'none', boxShadow: help === 'pulse' ? '0 0 0 4px ' + T.accentSoft : 'none' }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={help === 'pulse' ? T.accentDeep : T.text2} strokeWidth="2.1" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="9.5"/><path d="M9.2 9.3a2.8 2.8 0 0 1 5.4 1c0 1.9-2.8 2.5-2.8 2.5"/><circle cx="12" cy="17.2" r="0.6" fill="currentColor"/></svg>
          </button>
        )}
      </div>
    </header>
  );
}
/* dismissable hint banner */
function HintBanner({ T, onClose }) {
  return (
    <div style={{ margin: '6px 20px 4px', display: 'flex', gap: 11, alignItems: 'flex-start', background: T.cardTint, border: '1px solid ' + T.stroke, borderRadius: 16, padding: '13px 13px 13px 15px' }}>
      <span style={{ flex: 'none', marginTop: 1, color: T.accentDeep }}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2c-3.9 0-7 3-7 6.9 0 4.7 5.7 10.6 6.3 11.2.4.4 1 .4 1.4 0 .6-.6 6.3-6.5 6.3-11.2C19 5 15.9 2 12 2zm0 9.4a2.4 2.4 0 1 1 0-4.8 2.4 2.4 0 0 1 0 4.8z"/></svg>
      </span>
      <div style={{ flex: 1, fontSize: 12.5, lineHeight: 1.45, color: T.text }}>
        Удалять можно записи с меткой <b style={{ color: T.accentDeep }}>текущая</b> — это ваша позиция в книге. Прошлые сессии заморожены, чтобы прогресс книги оставался цельным.
      </div>
      <button onClick={onClose} style={{ flex: 'none', width: 24, height: 24, borderRadius: '50%', border: 'none', background: 'transparent', color: T.text2, cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
        <svg width="15" height="15" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.2" strokeLinecap="round"><path d="M6 6l12 12M18 6L6 18"/></svg>
      </button>
    </div>
  );
}
/* toast */
function Toast({ T, show }) {
  return (
    <div style={{ position: 'absolute', left: 20, right: 20, bottom: 22, zIndex: 8, display: 'flex', alignItems: 'center', gap: 10,
      background: T.text, color: T.canvas, borderRadius: 14, padding: '13px 16px', boxShadow: '0 12px 30px -8px rgba(0,0,0,0.4)',
      opacity: show ? 1 : 0, transform: show ? 'translateY(0)' : 'translateY(12px)', transition: 'opacity .3s, transform .3s', pointerEvents: 'none' }}>
      <span style={{ flex: 'none' }}>
        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.1" strokeLinecap="round" strokeLinejoin="round"><circle cx="12" cy="12" r="9.5"/><path d="M9.2 9.3a2.8 2.8 0 0 1 5.4 1c0 1.9-2.8 2.5-2.8 2.5"/><circle cx="12" cy="17.2" r="0.6" fill="currentColor"/></svg>
      </span>
      <span style={{ fontSize: 13, fontWeight: 500, lineHeight: 1.35 }}>Подсказка свернута — открыть снова можно по «?» сверху</span>
    </div>
  );
}
function SummaryBar({ T }) {
  const [p, setP] = useJ('Неделя');
  const data = {
    'Неделя': ['12', '286', '5 ч 40 м', 18, 'к прошлой неделе'],
    'Месяц': ['41', '1 020', '21 ч', 9, 'к прошлому месяцу'],
    'Всё время': ['148', '8 480', '92 ч', null, 'с марта 2024'],
  };
  const d = data[p];
  const cell = (v, l, accent) => (
    <div style={{ flex: 1, textAlign: 'center' }}>
      <div style={{ fontFamily: serif, fontSize: 18, fontWeight: 700, color: accent ? T.accentDeep : T.text }}>{v}</div>
      <div style={{ fontSize: 10.5, color: T.text2, fontWeight: 600, marginTop: 1 }}>{l}</div>
    </div>
  );
  const sep = <div style={{ width: 1, background: T.divider, margin: '3px 0' }}></div>;
  return (
    <div style={{ margin: '0 20px 6px' }}>
      <div style={{ display: 'flex', background: T.chip, borderRadius: 999, padding: 4, marginBottom: 10 }}>
        {Object.keys(data).map(k => (
          <button key={k} onClick={() => setP(k)} style={{ flex: 1, border: 'none', cursor: 'pointer', borderRadius: 999, padding: '7px 0', fontSize: 12.5, fontWeight: 600, fontFamily: 'inherit',
            background: p === k ? T.surfacePure : 'transparent', color: p === k ? T.text : T.text2, boxShadow: p === k ? '0 1px 4px rgba(56,42,32,0.12)' : 'none' }}>{k}</button>
        ))}
      </div>
      <div style={{ background: T.cardTint, border: '1px solid ' + T.stroke, borderRadius: 16, padding: '12px 6px 11px' }}>
        <div style={{ display: 'flex' }}>{cell(d[0], 'сессии')}{sep}{cell(d[1], 'страниц', true)}{sep}{cell(d[2], 'времени')}</div>
        {d[3] != null ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, marginTop: 10, paddingTop: 10, borderTop: '1px solid ' + T.divider }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, background: T.accentSoft, color: T.accentDeep, borderRadius: 999, padding: '2px 9px', fontSize: 11.5, fontWeight: 700 }}>▲ +{d[3]}%</span>
            <span style={{ fontSize: 11.5, color: T.text2 }}>{d[4]}</span>
          </div>
        ) : (
          <div style={{ textAlign: 'center', marginTop: 10, paddingTop: 10, borderTop: '1px solid ' + T.divider, fontSize: 11.5, color: T.text2 }}>148 книг · {d[4]}</div>
        )}
      </div>
    </div>
  );
}
function GroupHead({ T, children, sum }) {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, margin: '20px 2px 12px' }}>
      <span style={{ fontSize: 11, fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: T.text2, flex: 'none' }}>{children}</span>
      <span style={{ flex: 1, height: 1, background: T.divider }}></span>
      {sum && <span style={{ fontSize: 11, color: T.text2, flex: 'none' }}>{sum}</span>}
    </div>
  );
}

/* ════ FRAME 1 · LIST (filled, interactive hint/toast) ════ */
function FrameList({ T, swipeId, hintState }) {
  // hintState: 'open' (banner shown) | 'dismissed' (help pulses + toast) | 'rest' (help quiet)
  const [hint, setHint] = useJ(hintState || 'open');
  const [toast, setToast] = useJ(false);
  const closeHint = () => {
    setHint('dismissed'); setToast(true);
    setTimeout(() => setToast(false), 2600);
    setTimeout(() => setHint('rest'), 2600);
  };
  return (
    <Phone T={T} scrim={<Toast T={T} show={toast} />}>
      <Header T={T} sub="Продуктивность" title="Журнал чтения" help={hint === 'open' ? null : (hint === 'dismissed' ? 'pulse' : 'quiet')} />
      <SummaryBar T={T} />
      {hint === 'open' && <HintBanner T={T} onClose={closeHint} />}
      <div style={{ flex: 1, overflowY: 'auto', padding: '2px 20px 24px' }}>
        {GROUPS.map(g => {
          const items = SESSIONS.filter(s => s.date === g);
          const sp = items.reduce((a, s) => a + (s.to - s.from), 0);
          const mn = items.reduce((a, s) => a + s.mins, 0);
          return (
            <div key={g}>
              <GroupHead T={T} sum={sp + ' стр · ' + (mn >= 60 ? Math.floor(mn / 60) + ' ч ' + (mn % 60) + ' м' : mn + ' мин')}>{g}</GroupHead>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                {items.map(s => <Row key={s.id} s={s} T={T} swipe={swipeId === s.id ? 1 : 0} />)}
              </div>
            </div>
          );
        })}
      </div>
    </Phone>
  );
}

/* ════ FRAME · EDIT bottom-sheet ════ */
function Field({ T, label, value, suffix }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 7 }}>
      <label style={{ fontSize: 12.5, fontWeight: 600, color: T.text2 }}>{label}</label>
      <div style={{ position: 'relative' }}>
        <input readOnly value={value} style={{ width: '100%', height: 52, borderRadius: 16, padding: '0 16px', background: T.surfacePure, border: '1.5px solid ' + T.stroke, fontSize: 16, color: T.text, fontFamily: 'inherit' }} />
        {suffix && <span style={{ position: 'absolute', right: 16, top: '50%', transform: 'translateY(-50%)', fontSize: 14, color: T.text2 }}>{suffix}</span>}
      </div>
    </div>
  );
}
/* segmented number stepper-ish field */
function NumField({ T, label, value, hint }) {
  return (
    <div style={{ flex: 1, background: T.surfacePure, border: '1.5px solid ' + T.stroke, borderRadius: 16, padding: '11px 14px' }}>
      <div style={{ fontSize: 11.5, fontWeight: 600, color: T.text2 }}>{label}</div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 5, marginTop: 4 }}>
        <span style={{ fontFamily: serif, fontSize: 26, fontWeight: 700, color: T.text }}>{value}</span>
        {hint && <span style={{ fontSize: 12, color: T.text2 }}>{hint}</span>}
      </div>
    </div>
  );
}
function FrameEdit({ T }) {
  const ruler = []; for (let m = 30; m <= 64; m++) ruler.push(m); // window around 47
  return (
    <Phone T={T} scrim={<div style={{ position: 'absolute', inset: 0, background: T.scrim }}></div>}>
      <Header T={T} sub="Продуктивность" title="Журнал чтения" />
      <SummaryBar T={T} />
      <div style={{ flex: 1, overflow: 'hidden', padding: '2px 20px', opacity: 0.5 }}>
        <GroupHead T={T} sum="56 стр · 1 ч 19 м">Сегодня</GroupHead>
        <Row s={SESSIONS[0]} T={T} />
      </div>
      {/* sheet */}
      <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, background: T.canvas, borderRadius: '28px 28px 0 0', boxShadow: '0 -20px 60px rgba(0,0,0,0.3)', padding: '10px 20px 24px', zIndex: 5 }}>
        <div style={{ width: 40, height: 4, borderRadius: 2, background: T.divider, margin: '0 auto 14px' }}></div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 13, marginBottom: 18 }}>
          <Cover book="norway" w={46} T={T} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <div style={{ fontFamily: serif, fontSize: 18, fontWeight: 600 }}>Изменить запись</div>
            <div style={{ fontSize: 12.5, color: T.text2, marginTop: 2 }}>Норвежский лес · Сегодня</div>
          </div>
        </div>

        {/* pages: from (locked) → to (editable) */}
        <div style={{ fontSize: 12.5, fontWeight: 600, color: T.text2, marginBottom: 8 }}>Страницы за сессию</div>
        <div style={{ display: 'flex', alignItems: 'stretch', gap: 10 }}>
          <div style={{ flex: 1, background: T.chip, borderRadius: 16, padding: '11px 14px', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
            <div style={{ fontSize: 11.5, fontWeight: 600, color: T.text2 }}>с</div>
            <span style={{ fontFamily: serif, fontSize: 24, fontWeight: 700, color: T.text2, marginTop: 2 }}>182</span>
          </div>
          <div style={{ display: 'flex', alignItems: 'center', color: T.text2 }}>
            <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={T.accentDeep} strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h13M13 6l6 6-6 6"/></svg>
          </div>
          <div style={{ flex: 1, background: T.surfacePure, border: '1.5px solid ' + T.accent, borderRadius: 16, padding: '11px 14px' }}>
            <div style={{ fontSize: 11.5, fontWeight: 600, color: T.accentDeep }}>до страницы</div>
            <div style={{ display: 'flex', alignItems: 'baseline', gap: 4, marginTop: 2 }}>
              <span style={{ fontFamily: serif, fontSize: 24, fontWeight: 700, color: T.text }}>206</span>
              <span style={{ fontSize: 12, color: T.text2 }}>/ 320</span>
            </div>
          </div>
        </div>
        <div style={{ fontSize: 12, color: T.accentDeep, fontWeight: 600, marginTop: 8 }}>+24 страницы · вклад в прогресс +7%</div>

        {/* time — ruler picker */}
        <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', margin: '20px 0 4px' }}>
          <span style={{ fontSize: 12.5, fontWeight: 600, color: T.text2 }}>Время чтения</span>
          <span style={{ fontFamily: serif, fontSize: 22, fontWeight: 700, color: T.accentDeep }}>47 мин</span>
        </div>
        <div style={{ position: 'relative' }}>
          {/* center caret */}
          <div style={{ position: 'absolute', left: '50%', top: 0, transform: 'translateX(-50%)', zIndex: 3, width: 0, height: 0, borderLeft: '6px solid transparent', borderRight: '6px solid transparent', borderTop: '8px solid ' + T.accent }}></div>
          <div style={{ position: 'absolute', left: '50%', top: 6, height: 30, width: 2, transform: 'translateX(-50%)', background: T.accent, zIndex: 3, borderRadius: 2 }}></div>
          <div style={{ overflow: 'hidden', padding: '10px 0 4px', maskImage: 'linear-gradient(90deg, transparent, #000 16%, #000 84%, transparent)', WebkitMaskImage: 'linear-gradient(90deg, transparent, #000 16%, #000 84%, transparent)' }}>
            <div style={{ display: 'flex', alignItems: 'flex-end', height: 44, justifyContent: 'center', transform: 'translateX(0)' }}>
              {ruler.map(m => {
                const major = m % 15 === 0, mid = m % 5 === 0;
                return (
                  <div key={m} style={{ width: 9, flex: 'none', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                    <div style={{ width: 2, borderRadius: 2, height: major ? 30 : mid ? 18 : 11, background: major ? T.accentDeep : mid ? T.text2 : T.divider }}></div>
                    {major && <div style={{ fontSize: 9, color: T.text2, marginTop: 3 }}>{m}</div>}
                  </div>
                );
              })}
            </div>
          </div>
        </div>

        <button style={{ width: '100%', height: 52, borderRadius: 16, border: 'none', background: T.accent, color: T.on, fontSize: 16, fontWeight: 600, marginTop: 18, cursor: 'pointer' }}>Сохранить</button>
        <button style={{ width: '100%', height: 46, borderRadius: 16, border: 'none', background: 'none', color: T.del, fontSize: 15, fontWeight: 600, marginTop: 4, cursor: 'pointer' }}>Удалить запись</button>
      </div>
    </Phone>
  );
}

/* ════ FRAME · DELETE confirm ════ */
function FrameConfirm({ T }) {
  return (
    <Phone T={T} scrim={<div style={{ position: 'absolute', inset: 0, background: T.scrim }}></div>}>
      <Header T={T} sub="Продуктивность" title="Журнал чтения" />
      <SummaryBar T={T} />
      <div style={{ flex: 1, overflow: 'hidden', padding: '2px 20px', opacity: 0.5 }}>
        <GroupHead T={T}>Сегодня</GroupHead>
        <Row s={SESSIONS[0]} T={T} />
      </div>
      <div style={{ position: 'absolute', left: 26, right: 26, top: '50%', transform: 'translateY(-50%)', background: T.surface, border: '1px solid ' + T.stroke, borderRadius: 24, padding: '24px 22px', textAlign: 'center', boxShadow: '0 24px 70px rgba(0,0,0,0.32)', zIndex: 6 }}>
        <div style={{ width: 52, height: 52, borderRadius: 16, margin: '0 auto 14px', background: 'color-mix(in oklab,' + T.del + ' 18%, ' + T.surface + ')', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
          <svg width="26" height="26" viewBox="0 0 24 24" fill="none" stroke={T.del} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 7h14M9 7V5h6v2M7 7l1 13h8l1-13"/></svg>
        </div>
        <div style={{ fontFamily: serif, fontSize: 19, fontWeight: 600 }}>Удалить запись?</div>
        <p style={{ fontSize: 13.5, color: T.text2, lineHeight: 1.5, margin: '8px 0 20px' }}>Это ваша текущая позиция в книге «Норвежский лес». Прогресс откатится со стр. 206 до 182.</p>
        <div style={{ display: 'flex', gap: 10 }}>
          <button style={{ flex: 1, height: 48, borderRadius: 14, border: 'none', background: T.chip, color: T.text, fontSize: 15, fontWeight: 600, cursor: 'pointer' }}>Оставить</button>
          <button style={{ flex: 1, height: 48, borderRadius: 14, border: 'none', background: T.del, color: '#fff', fontSize: 15, fontWeight: 600, cursor: 'pointer' }}>Удалить</button>
        </div>
      </div>
    </Phone>
  );
}

/* ════ FRAME · EMPTY ════ */
function FrameEmpty({ T }) {
  return (
    <Phone T={T}>
      <Header T={T} sub="Продуктивность" title="Журнал чтения" />
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', padding: '0 40px' }}>
        <div style={{ width: 92, height: 92, borderRadius: 30, background: T.cardTint, border: '1.6px dashed ' + T.divider, display: 'flex', alignItems: 'center', justifyContent: 'center', color: T.accentDeep, marginBottom: 18 }}>
          <svg width="42" height="42" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round"><path d="M12 6c-1.5-1.6-4-2.5-8-2.5v14c4 0 6.5.9 8 2.5 1.5-1.6 4-2.5 8-2.5v-14c-4 0-6.5.9-8 2.5z"/><path d="M12 6v14"/></svg>
        </div>
        <div style={{ fontFamily: serif, fontSize: 21, fontWeight: 600 }}>Здесь появятся ваши сессии</div>
        <p style={{ fontSize: 14, color: T.text2, lineHeight: 1.5, margin: '8px 0 22px', maxWidth: 250 }}>Отмечайте прогресс после чтения — каждая запись попадёт в журнал.</p>
        <button style={{ height: 50, padding: '0 24px', borderRadius: 16, border: 'none', background: T.accent, color: T.on, fontSize: 15.5, fontWeight: 600, cursor: 'pointer' }}>Отметить прогресс</button>
      </div>
    </Phone>
  );
}

/* ════ FRAME · BY BOOK ════ */
function FrameByBook({ T }) {
  const items = SESSIONS.filter(s => s.book === 'norway').concat([
    { id: 11, from: 140, to: 160, mins: 34, time: '4 июня', last: false },
    { id: 12, from: 120, to: 140, mins: 27, time: '2 июня', last: false },
  ]);
  return (
    <Phone T={T}>
      <Header T={T} sub="Библиотека · история" title="" />
      <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 24px', marginTop: -8 }}>
        {/* book header */}
        <div style={{ display: 'flex', gap: 16, alignItems: 'center', marginBottom: 18 }}>
          <Cover book="norway" w={72} r={11} T={T} />
          <div style={{ flex: 1, minWidth: 0 }}>
            <h1 style={{ fontFamily: serif, fontWeight: 600, fontSize: 20, margin: 0 }}>Норвежский лес</h1>
            <div style={{ fontSize: 11, fontWeight: 600, letterSpacing: '0.05em', textTransform: 'uppercase', color: T.text2, marginTop: 3 }}>Харуки Мураками</div>
            <div style={{ display: 'flex', alignItems: 'center', gap: 9, marginTop: 10 }}>
              <div style={{ flex: 1, height: 6, borderRadius: 999, background: T.chip, overflow: 'hidden' }}><div style={{ width: '64%', height: '100%', background: `linear-gradient(90deg, ${T.goalG1}, ${T.goalG2})` }}></div></div>
              <span style={{ fontSize: 12.5, fontWeight: 700, color: T.accentDeep }}>64%</span>
            </div>
          </div>
        </div>
        <div style={{ display: 'flex', background: T.cardTint, border: '1px solid ' + T.stroke, borderRadius: 16, padding: '11px 6px', marginBottom: 6 }}>
          {[['5', 'сессий'], ['206', 'страниц'], ['3 ч 18 м', 'времени']].map(([v, l], i) => (
            <React.Fragment key={l}>
              {i > 0 && <div style={{ width: 1, background: T.divider, margin: '3px 0' }}></div>}
              <div style={{ flex: 1, textAlign: 'center' }}>
                <div style={{ fontFamily: serif, fontSize: 17, fontWeight: 700 }}>{v}</div>
                <div style={{ fontSize: 10.5, color: T.text2, fontWeight: 600 }}>{l}</div>
              </div>
            </React.Fragment>
          ))}
        </div>
        <GroupHead T={T}>Все сессии</GroupHead>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
          {items.map(s => (
            <div key={s.id} style={{ display: 'flex', alignItems: 'center', gap: 13, background: T.surface, border: '1px solid ' + T.stroke, borderRadius: 16, padding: '12px 14px' }}>
              <div style={{ width: 4, alignSelf: 'stretch', borderRadius: 2, background: COVERS.norway.bg, flex: 'none', margin: '2px 0' }}></div>
              <div style={{ flex: 1, minWidth: 0 }}>
                <div style={{ display: 'flex', alignItems: 'center', gap: 7 }}>
                  <span style={{ fontSize: 13.5, fontWeight: 600 }}>{s.time}</span>
                  {s.last && <LastTag T={T} />}
                </div>
                <div style={{ display: 'flex', alignItems: 'center', gap: 7, marginTop: 8 }}>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: T.chip, borderRadius: 8, padding: '3px 9px', fontSize: 12, fontWeight: 600 }}><PagesIc T={T} /> {s.from}–{s.to}</span>
                  <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: T.chip, borderRadius: 8, padding: '3px 9px', fontSize: 12, fontWeight: 600 }}><ClockIc T={T} c={T.text} /> {s.mins} мин</span>
                </div>
              </div>
              <div style={{ flex: 'none', display: 'flex', alignItems: 'baseline', gap: 3 }}>
                <span style={{ fontFamily: serif, fontSize: 22, fontWeight: 700, color: T.accentDeep, lineHeight: 1 }}>+{s.to - s.from}</span>
                <span style={{ fontSize: 10.5, fontWeight: 600, color: T.text2 }}>стр</span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </Phone>
  );
}

/* ── layout ── */
function Card({ label, children }) {
  return <div style={{ flex: 'none' }}>
    <div style={{ fontSize: 13, fontWeight: 600, color: '#382A20', marginBottom: 10 }}>{label}</div>
    <div style={{ borderRadius: 30, overflow: 'hidden', boxShadow: '0 24px 56px -22px rgba(56,42,32,0.5)', border: '1px solid rgba(56,42,32,0.06)' }}>{children}</div>
  </div>;
}
function Band({ title, theme, children }) {
  const dark = theme === 'dark';
  return (
    <div style={{ background: dark ? '#0F0B07' : 'transparent', borderRadius: 28, padding: dark ? '24px 28px' : '0', margin: dark ? '0 0 8px' : 0 }}>
      <div style={{ fontSize: 13, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', color: dark ? '#AE9D8E' : '#8C7C6E', margin: '0 0 16px' }}>{title}</div>
      <div style={{ display: 'flex', gap: 26, overflowX: 'auto', paddingBottom: 16 }}>{children}</div>
    </div>
  );
}
function App() {
  const L = THEMES.light, D = THEMES.dark;
  return (
    <div style={{ minHeight: '100vh', padding: '34px 36px', fontFamily: "'Inter', system-ui, sans-serif", background: '#E7DCCC' }}>
      <h1 style={{ fontFamily: serif, fontWeight: 600, fontSize: 27, margin: '0 0 4px', color: '#382A20' }}>Журнал чтения — вариант A</h1>
      <p style={{ color: '#8C7C6E', fontSize: 14, margin: '0 0 26px' }}>Вход из «Продуктивности». Подсказка про «текущую» запись (крестик → «?» сверху + тост). Кадры: список · подсказка свёрнута · свайп · шит · подтверждение · пустое · по книге</p>
      <Band title="Светлая тема">
        <Card label="Список + подсказка (закрой ×)"><FrameList T={L} hintState="open" /></Card>
        <Card label="Подсказка свёрнута → «?» + тост"><FrameList T={L} hintState="dismissed" /></Card>
        <Card label="Свайп → удалить"><FrameList T={L} hintState="rest" swipeId={1} /></Card>
        <Card label="Шит «Изменить запись»"><FrameEdit T={L} /></Card>
        <Card label="Подтверждение удаления"><FrameConfirm T={L} /></Card>
        <Card label="Пустое состояние"><FrameEmpty T={L} /></Card>
        <Card label="Вариант «по книге»"><FrameByBook T={L} /></Card>
      </Band>
      <Band title="Тёмная тема" theme="dark">
        <Card label="Список + подсказка"><FrameList T={D} hintState="open" /></Card>
        <Card label="Свёрнута → «?» + тост"><FrameList T={D} hintState="dismissed" /></Card>
        <Card label="Шит «Изменить запись»"><FrameEdit T={D} /></Card>
        <Card label="Подтверждение удаления"><FrameConfirm T={D} /></Card>
        <Card label="Вариант «по книге»"><FrameByBook T={D} /></Card>
      </Band>
    </div>
  );
}
ReactDOM.createRoot(document.getElementById('root')).render(<App />);
