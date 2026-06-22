// Bookechi — exploration: экран лога сессий чтения (4 направления)
const LT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  goalG1: '#D98E63', goalG2: '#9E4A2C', on: '#FFF6EE',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const TONES = {
  stone: { bg: '#8C6E54', fg: '#F6EFE6' }, terra: { bg: '#BE5E3B', fg: '#FBEEE4' },
  walnut: { bg: '#A08B7C', fg: '#F7F1E8' }, sage: { bg: '#5C6B5E', fg: '#EDF1EC' },
};
// mock sessions, newest first
const S = [
  { id: 1, title: 'Норвежский лес', author: 'Харуки Мураками', tone: 'stone', from: 182, to: 206, mins: 47, day: 'Сегодня', time: '21:30' },
  { id: 2, title: 'Думай медленно… решай быстро', author: 'Канеман', tone: 'walnut', from: 96, to: 120, mins: 32, day: 'Сегодня', time: '08:15' },
  { id: 3, title: 'Норвежский лес', author: 'Харуки Мураками', tone: 'stone', from: 160, to: 182, mins: 38, day: 'Вчера', time: '22:05' },
  { id: 4, title: 'Маленькая жизнь', author: 'Янагихара', tone: 'terra', from: 0, to: 18, mins: 21, day: 'Вчера', time: '13:40' },
  { id: 5, title: 'Думай медленно… решай быстро', author: 'Канеман', tone: 'walnut', from: 74, to: 96, mins: 29, day: '7 июня', time: '20:50' },
];

function Cover({ tone, w = 38, r = 7 }) {
  const c = TONES[tone]; const h = Math.round(w * 1.45);
  return <div style={{ width: w, height: h, borderRadius: r, flex: 'none', background: `linear-gradient(158deg, ${c.bg}, color-mix(in oklab, ${c.bg} 74%, #000))`, boxShadow: 'inset -2px 0 4px -1px rgba(0,0,0,0.28)' }}></div>;
}
function Clock({ s = 13, c = LT.accentDeep }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2" strokeLinecap="round"><circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/></svg>; }
function Pages({ s = 13, c = LT.accentDeep }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.9" strokeLinecap="round" strokeLinejoin="round"><path d="M12 6c-1.5-1.6-4-2.5-8-2.5v14c4 0 6.5.9 8 2.5 1.5-1.6 4-2.5 8-2.5v-14c-4 0-6.5.9-8 2.5z"/><path d="M12 6v14"/></svg>; }
function Dots() { return <svg width="18" height="18" viewBox="0 0 24 24" fill={LT.text2}><circle cx="5" cy="12" r="1.6"/><circle cx="12" cy="12" r="1.6"/><circle cx="19" cy="12" r="1.6"/></svg>; }

function Screen({ children, label }) {
  return (
    <div style={{ width: 390, height: 720, background: LT.canvas, fontFamily: LT.sans, color: LT.text, display: 'flex', flexDirection: 'column', overflow: 'hidden' }}>
      <header style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '18px 20px 14px' }}>
        <button style={{ width: 40, height: 40, borderRadius: '50%', border: 'none', background: LT.chip, display: 'flex', alignItems: 'center', justifyContent: 'center', cursor: 'pointer', flex: 'none' }}>
          <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={LT.text} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M15 5l-7 7 7 7"/></svg>
        </button>
        <div>
          <div style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.06em', textTransform: 'uppercase', color: LT.text2 }}>{label}</div>
          <h1 style={{ fontFamily: LT.serif, fontWeight: 600, fontSize: 22, margin: '2px 0 0' }}>Журнал чтения</h1>
        </div>
      </header>
      <div style={{ flex: 1, overflowY: 'auto', padding: '4px 20px 24px' }}>{children}</div>
    </div>
  );
}

function GroupLabel({ children, sub }) {
  return <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', margin: '18px 2px 12px' }}>
    <span style={{ fontSize: 13, fontWeight: 700, color: LT.text }}>{children}</span>
    {sub && <span style={{ fontSize: 12, color: LT.text2 }}>{sub}</span>}
  </div>;
}

/* ════ A · КАРТОЧКИ С ОБЛОЖКОЙ ════ */
function VarCards() {
  const grouped = [['Сегодня', S.filter(s => s.day === 'Сегодня')], ['Вчера', S.filter(s => s.day === 'Вчера')], ['Ранее', S.filter(s => s.day === '7 июня')]];
  return (
    <Screen label="Продуктивность">
      {grouped.map(([g, items]) => (
        <div key={g}>
          <GroupLabel sub={items.reduce((a, s) => a + (s.to - s.from), 0) + ' стр · ' + items.reduce((a, s) => a + s.mins, 0) + ' мин'}>{g}</GroupLabel>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {items.map(s => (
              <div key={s.id} style={{ display: 'flex', gap: 13, alignItems: 'center', background: LT.surface, border: '1px solid ' + LT.stroke, borderRadius: 18, padding: 12 }}>
                <Cover tone={s.tone} />
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 14, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.title}</div>
                  <div style={{ display: 'flex', gap: 14, marginTop: 7 }}>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12.5, fontWeight: 600 }}><Pages /> +{s.to - s.from} стр</span>
                    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12.5, color: LT.text2 }}><Clock c={LT.text2} /> {s.mins} мин</span>
                  </div>
                </div>
                <div style={{ textAlign: 'right', flex: 'none' }}>
                  <div style={{ fontSize: 11.5, color: LT.text2 }}>{s.time}</div>
                  <button style={{ marginTop: 6, border: 'none', background: 'none', cursor: 'pointer', padding: 2 }}><Dots /></button>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </Screen>
  );
}

/* ════ B · ТАЙМЛАЙН ════ */
function VarTimeline() {
  const grouped = [['Сегодня', S.filter(s => s.day === 'Сегодня')], ['Вчера', S.filter(s => s.day === 'Вчера')], ['7 июня', S.filter(s => s.day === '7 июня')]];
  return (
    <Screen label="Продуктивность">
      {grouped.map(([g, items]) => (
        <div key={g}>
          <GroupLabel>{g}</GroupLabel>
          <div style={{ position: 'relative', paddingLeft: 26 }}>
            <div style={{ position: 'absolute', left: 5, top: 6, bottom: 6, width: 2, background: LT.divider }}></div>
            <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
              {items.map(s => (
                <div key={s.id} style={{ position: 'relative' }}>
                  <div style={{ position: 'absolute', left: -24, top: 6, width: 12, height: 12, borderRadius: '50%', background: LT.accent, border: '2.5px solid ' + LT.canvas, boxShadow: '0 0 0 1.5px ' + LT.accentSoft }}></div>
                  <div style={{ display: 'flex', alignItems: 'flex-start', gap: 10 }}>
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 13.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.title}</div>
                      <div style={{ fontSize: 12.5, color: LT.text2, marginTop: 3 }}>стр. {s.from}–{s.to} · {s.mins} мин · {s.time}</div>
                    </div>
                    <span style={{ flex: 'none', fontFamily: LT.serif, fontSize: 16, fontWeight: 700, color: LT.accentDeep }}>+{s.to - s.from}</span>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      ))}
    </Screen>
  );
}

/* ════ C · СПИСОК-ВЫПИСКА (компактные строки) ════ */
function VarStatement() {
  const grouped = [['Сегодня', '56 стр · 1 ч 19 мин', S.filter(s => s.day === 'Сегодня')], ['Вчера', '40 стр · 59 мин', S.filter(s => s.day === 'Вчера')], ['7 июня', '22 стр · 29 мин', S.filter(s => s.day === '7 июня')]];
  return (
    <Screen label="Продуктивность">
      {grouped.map(([g, sum, items]) => (
        <div key={g}>
          <GroupLabel sub={sum}>{g}</GroupLabel>
          <div style={{ background: LT.surface, border: '1px solid ' + LT.stroke, borderRadius: 18, overflow: 'hidden' }}>
            {items.map((s, i) => (
              <div key={s.id} style={{ display: 'flex', alignItems: 'center', gap: 12, padding: '13px 15px', borderTop: i ? '1px solid ' + LT.divider : 'none' }}>
                <div style={{ width: 4, alignSelf: 'stretch', borderRadius: 2, background: TONES[s.tone].bg, flex: 'none', margin: '1px 0' }}></div>
                <div style={{ flex: 1, minWidth: 0 }}>
                  <div style={{ fontSize: 13.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.title}</div>
                  <div style={{ fontSize: 12, color: LT.text2, marginTop: 2 }}>{s.time} · стр. {s.from}–{s.to}</div>
                </div>
                <div style={{ textAlign: 'right', flex: 'none' }}>
                  <div style={{ fontFamily: LT.serif, fontSize: 15, fontWeight: 700, color: LT.accentDeep }}>+{s.to - s.from}</div>
                  <div style={{ fontSize: 11, color: LT.text2 }}>{s.mins} мин</div>
                </div>
              </div>
            ))}
          </div>
        </div>
      ))}
    </Screen>
  );
}

/* ════ D · СВАЙП-КАРТОЧКИ (с раскрытыми действиями) ════ */
function VarSwipe() {
  const grouped = [['Сегодня', S.filter(s => s.day === 'Сегодня')], ['Вчера', S.filter(s => s.day === 'Вчера')]];
  return (
    <Screen label="Продуктивность">
      {grouped.map(([g, items], gi) => (
        <div key={g}>
          <GroupLabel>{g}</GroupLabel>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {items.map((s, i) => {
              const open = gi === 0 && i === 0; // demo: first row swiped open
              return (
                <div key={s.id} style={{ position: 'relative', borderRadius: 18, overflow: 'hidden' }}>
                  {/* action layer */}
                  <div style={{ position: 'absolute', inset: 0, display: 'flex', justifyContent: 'flex-end' }}>
                    <div style={{ width: 64, background: LT.accentSoft, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke={LT.accentDeep} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M4 20l1-4L16.5 4.5a2.1 2.1 0 0 1 3 3L8 19l-4 1z"/></svg>
                    </div>
                    <div style={{ width: 64, background: '#C9543A', display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
                      <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="#fff" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 7h14M9 7V5h6v2M7 7l1 13h8l1-13"/></svg>
                    </div>
                  </div>
                  {/* card */}
                  <div style={{ position: 'relative', display: 'flex', gap: 13, alignItems: 'center', background: LT.surface, border: '1px solid ' + LT.stroke, borderRadius: 18, padding: 12, transform: open ? 'translateX(-128px)' : 'none', transition: 'transform .3s' }}>
                    <Cover tone={s.tone} />
                    <div style={{ flex: 1, minWidth: 0 }}>
                      <div style={{ fontSize: 14, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.title}</div>
                      <div style={{ display: 'flex', gap: 14, marginTop: 7 }}>
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12.5, fontWeight: 600 }}><Pages /> +{s.to - s.from} стр</span>
                        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, fontSize: 12.5, color: LT.text2 }}><Clock c={LT.text2} /> {s.mins} мин</span>
                      </div>
                    </div>
                    <span style={{ fontSize: 11.5, color: LT.text2, flex: 'none' }}>{s.time}</span>
                  </div>
                </div>
              );
            })}
          </div>
          {gi === 0 && <div style={{ textAlign: 'center', fontSize: 11.5, color: LT.text2, marginTop: 12 }}>← смахните запись для действий</div>}
        </div>
      ))}
    </Screen>
  );
}

function Labeled({ label, sub, children }) {
  return <div style={{ flex: 'none' }}>
    <div style={{ fontSize: 13, fontWeight: 600, color: LT.text, marginBottom: 2 }}>{label}</div>
    <div style={{ fontSize: 12, color: LT.text2, marginBottom: 10 }}>{sub}</div>
    <div style={{ borderRadius: 28, overflow: 'hidden', boxShadow: '0 20px 50px -20px rgba(56,42,32,0.45)' }}>{children}</div>
  </div>;
}
function App() {
  return (
    <div style={{ minHeight: '100vh', padding: '32px 36px', fontFamily: LT.sans }}>
      <h1 style={{ fontFamily: LT.serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px', color: LT.text }}>Журнал чтения — направления</h1>
      <p style={{ color: LT.text2, fontSize: 14, margin: '0 0 26px' }}>Вход — из «Продуктивности» (кнопка «Все сессии» / тап по дню heatmap). Не отдельная вкладка. Группировка по дням, действия: изменить / удалить</p>
      <div style={{ display: 'flex', gap: 30, overflowX: 'auto', paddingBottom: 24 }}>
        <Labeled label="A · Карточки с обложкой" sub="дружелюбно, обложко-центрично"><VarCards /></Labeled>
        <Labeled label="B · Таймлайн" sub="лента событий по времени"><VarTimeline /></Labeled>
        <Labeled label="C · Список-выписка" sub="плотно, как банк-выписка"><VarStatement /></Labeled>
        <Labeled label="D · Свайп-карточки" sub="смахнуть → изменить/удалить"><VarSwipe /></Labeled>
      </div>
    </div>
  );
}
ReactDOM.createRoot(document.getElementById('root')).render(<App />);
