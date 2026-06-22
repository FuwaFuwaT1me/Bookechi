// Bookechi — exploration: герой экрана результатов с акцентом НА ПРОГРЕССЕ
const PT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  goalG1: '#D98E63', goalG2: '#9E4A2C', on: '#FFF6EE',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const STONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const B = { title: 'Норвежский лес', author: 'Харуки Мураками', before: 57, after: 64, beforePage: 182, page: 206, total: 320, delta: 24, mins: 47 };

function bracePath(bw, h) {
  const m = bw / 2;
  return `M ${bw},0 Q ${m},0 ${m},${h * 0.16} L ${m},${h * 0.40} Q ${m},${h / 2} 0,${h / 2}`
    + ` Q ${m},${h / 2} ${m},${h * 0.60} L ${m},${h * 0.84} Q ${m},${h} ${bw},${h}`;
}

/* cover with gain band + brace; label customizable */
function Cover({ w = 116, braceLabel, showBrace = true }) {
  const W = w, H = Math.round(W * 1.45), r = 15;
  const beforeY = H * (1 - B.before / 100);
  const curY = H * (1 - B.after / 100);
  const braceH = beforeY - curY;
  const dimFrac = 1 - B.after / 100;
  const bw = 10, gap = 11;
  return (
    <div style={{ position: 'relative', width: W, height: H, filter: 'drop-shadow(0 16px 30px rgba(56,42,32,0.42))' }}>
      <div style={{ width: W, height: H, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`, color: STONE.fg,
        padding: '12px 10px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
        <div style={{ position: 'absolute', left: W * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: PT.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 13, paddingLeft: W * 0.1 }}>{B.title}</div>
        <div style={{ fontSize: 8, opacity: 0.85, paddingLeft: W * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{B.author}</div>
        <div style={{ position: 'absolute', left: 0, right: 0, top: 0, height: dimFrac * 100 + '%', background: 'color-mix(in oklab,' + PT.canvas + ' 64%, transparent)', borderRadius: `${r}px ${r}px 0 0` }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: braceH, background: `linear-gradient(0deg, color-mix(in oklab,${PT.accent} 42%, transparent), color-mix(in oklab,${PT.accent} 10%, transparent))` }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: 3, background: PT.accent, boxShadow: '0 0 0 1px ' + PT.surfacePure }}></div>
      </div>
      {showBrace && (
        <div style={{ position: 'absolute', left: -(bw + gap), top: curY, width: bw, height: braceH }}>
          <svg width={bw} height={braceH} viewBox={`0 0 ${bw} ${braceH}`} style={{ overflow: 'visible' }}>
            <path d={bracePath(bw, braceH)} fill="none" stroke={PT.accentDeep} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          {braceLabel && (
            <div style={{ position: 'absolute', right: bw + 7, top: '50%', transform: 'translateY(-50%)', whiteSpace: 'nowrap', background: PT.accent, color: PT.on, borderRadius: 999, padding: '3px 10px', fontSize: 12, fontWeight: 700, boxShadow: '0 3px 9px -2px rgba(132,59,34,0.55)' }}>{braceLabel}</div>
          )}
        </div>
      )}
    </div>
  );
}

function Btn() { return <button style={{ height: 52, borderRadius: 18, border: 'none', background: PT.accent, color: PT.on, fontSize: 16, fontWeight: 600, fontFamily: PT.sans, width: '100%', cursor: 'pointer' }}>Готово</button>; }
function StatsRow() {
  return (
    <div style={{ display: 'flex', alignItems: 'stretch', background: PT.cardTint, border: '1px solid ' + PT.stroke, borderRadius: 18, overflow: 'hidden' }}>
      <div style={{ padding: '11px 18px', textAlign: 'center' }}>
        <div style={{ fontFamily: PT.serif, fontSize: 17, fontWeight: 700 }}>{B.mins} мин</div>
        <div style={{ fontSize: 11, color: PT.text2, fontWeight: 600 }}>сессия</div>
      </div>
      <div style={{ width: 1, background: PT.divider, margin: '10px 0' }}></div>
      <div style={{ padding: '11px 18px', textAlign: 'center' }}>
        <div style={{ fontFamily: PT.serif, fontSize: 17, fontWeight: 700 }}>{B.page}/{B.total}</div>
        <div style={{ fontSize: 11, color: PT.text2, fontWeight: 600 }}>страница</div>
      </div>
    </div>
  );
}
function Screen({ children }) {
  return <div style={{ width: 380, height: 700, background: PT.canvas, fontFamily: PT.sans, color: PT.text, display: 'flex', flexDirection: 'column', padding: 28 }}>
    <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>{children}</div>
    <Btn />
  </div>;
}

/* ══ V1 · ГЕРОЙ = ПРИРОСТ «+24 страницы» ══ */
function V1() {
  return (
    <Screen>
      <div style={{ paddingLeft: 28, marginBottom: 30 }}><Cover w={112} braceLabel={B.after + '%'} /></div>
      <div style={{ display: 'flex', alignItems: 'baseline', gap: 4 }}>
        <span style={{ fontFamily: PT.serif, fontWeight: 700, fontSize: 76, lineHeight: 0.82, color: PT.accentDeep }}>+{B.delta}</span>
      </div>
      <div style={{ fontFamily: PT.serif, fontSize: 20, fontWeight: 600, marginTop: 2 }}>страницы прочитано</div>
      <div style={{ marginTop: 18, width: 230 }}><StatsRow /></div>
    </Screen>
  );
}

/* ══ V2 · ГЕРОЙ = ДВИЖЕНИЕ 57→64 крупной дугой ══ */
function V2() {
  const R = 120, sw = 14, cx = 140, cy = 140;
  const a0 = 135, span = 270; // gauge arc
  const pt = (frac) => { const a = (a0 + frac * span) * Math.PI / 180; return [cx + R * Math.cos(a), cy + R * Math.sin(a)]; };
  const arc = (f0, f1) => { const [sx, sy] = pt(f0), [ex, ey] = pt(f1); const large = (f1 - f0) * span > 180 ? 1 : 0; return `M ${sx} ${sy} A ${R} ${R} 0 ${large} 1 ${ex} ${ey}`; };
  return (
    <Screen>
      <div style={{ position: 'relative', width: 280, height: 230 }}>
        <svg width="280" height="230" viewBox="0 0 280 250">
          <path d={arc(0, 1)} fill="none" stroke={PT.chip} strokeWidth={sw} strokeLinecap="round" />
          <path d={arc(0, B.before / 100)} fill="none" stroke={PT.accentSoft} strokeWidth={sw} strokeLinecap="round" />
          <path d={arc(B.before / 100, B.after / 100)} fill="none" stroke="url(#gg)" strokeWidth={sw} strokeLinecap="round" />
          <defs><linearGradient id="gg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={PT.goalG1} /><stop offset="1" stopColor={PT.goalG2} /></linearGradient></defs>
        </svg>
        <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', paddingTop: 16 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 9, fontFamily: PT.serif, fontWeight: 600, fontSize: 22, color: PT.text2 }}>
            <span>{B.before}%</span>
            <svg width="22" height="22" viewBox="0 0 24 24" fill="none" stroke={PT.accentDeep} strokeWidth="2.4" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h13M13 6l6 6-6 6"/></svg>
            <span style={{ fontSize: 40, fontWeight: 700, color: PT.accentDeep }}>{B.after}%</span>
          </div>
          <span style={{ marginTop: 8, display: 'inline-flex', alignItems: 'center', gap: 6, background: `linear-gradient(135deg, ${PT.goalG1}, ${PT.goalG2})`, color: PT.on, borderRadius: 999, padding: '5px 13px', fontSize: 13, fontWeight: 700 }}>+{B.delta} страницы за сессию</span>
        </div>
      </div>
      <div style={{ marginTop: 14, width: 230 }}><StatsRow /></div>
    </Screen>
  );
}

/* ══ V3 · ГЕРОЙ = «движение по треку» обложка + летящий прирост ══ */
function V3() {
  return (
    <Screen>
      <div style={{ paddingLeft: 30, marginBottom: 26 }}><Cover w={120} braceLabel={'+' + B.delta + ' стр'} /></div>
      <div style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 17, color: PT.text2 }}>сегодня вы продвинулись</div>
      <div style={{ display: 'flex', alignItems: 'center', gap: 12, marginTop: 8 }}>
        <span style={{ fontFamily: PT.serif, fontSize: 34, fontWeight: 600, color: PT.text2 }}>{B.before}%</span>
        <div style={{ width: 56, height: 8, borderRadius: 999, background: PT.chip, position: 'relative', overflow: 'hidden' }}>
          <div style={{ position: 'absolute', inset: 0, background: `linear-gradient(90deg, ${PT.goalG1}, ${PT.goalG2})` }}></div>
        </div>
        <span style={{ fontFamily: PT.serif, fontSize: 48, fontWeight: 700, color: PT.accentDeep }}>{B.after}%</span>
      </div>
      <div style={{ marginTop: 20, width: 230 }}><StatsRow /></div>
    </Screen>
  );
}

/* ══ V4 · ГЕРОЙ = прирост-стрелка, % как опора ══ */
function V4() {
  return (
    <Screen>
      <div style={{ paddingLeft: 28, marginBottom: 28 }}><Cover w={108} braceLabel={'+' + B.delta} /></div>
      <span style={{ display: 'inline-flex', alignItems: 'center', gap: 8, background: `linear-gradient(135deg, ${PT.goalG1}, ${PT.goalG2})`, color: PT.on, borderRadius: 999, padding: '8px 10px 8px 18px', fontSize: 26, fontWeight: 800, fontFamily: PT.serif, boxShadow: '0 8px 20px -6px rgba(132,59,34,0.55)' }}>
        +{B.after - B.before}%
        <span style={{ display: 'inline-flex', alignItems: 'center', justifyContent: 'center', width: 30, height: 30, borderRadius: '50%', background: 'rgba(255,246,238,0.22)' }}>
          <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke={PT.on} strokeWidth="3" strokeLinecap="round" strokeLinejoin="round"><path d="M12 19V5M6 11l6-6 6 6"/></svg>
        </span>
      </span>
      <div style={{ fontSize: 14, color: PT.text2, marginTop: 12 }}>прогресс за сессию · теперь <b style={{ color: PT.text }}>{B.after}%</b> книги</div>
      <div style={{ marginTop: 20, width: 230 }}><StatsRow /></div>
    </Screen>
  );
}

function Labeled({ label, children }) {
  return <div style={{ flex: 'none' }}>
    <div style={{ fontSize: 13, fontWeight: 600, color: PT.text2, marginBottom: 10 }}>{label}</div>
    <div style={{ borderRadius: 28, overflow: 'hidden', boxShadow: '0 20px 50px -20px rgba(56,42,32,0.45)' }}>{children}</div>
  </div>;
}
function App() {
  return (
    <div style={{ minHeight: '100vh', padding: '32px 36px' }}>
      <h1 style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px', color: PT.text }}>Экран результатов — акцент на прогрессе</h1>
      <p style={{ color: PT.text2, fontSize: 14, margin: '0 0 26px' }}>4 переосмысления героя · скобка сохранена · Terracotta &amp; Linen</p>
      <div style={{ display: 'flex', gap: 32, overflowX: 'auto', paddingBottom: 24 }}>
        <Labeled label="V1 · Герой = «+24 страницы»"><V1 /></Labeled>
        <Labeled label="V2 · Дуга-движение 57→64"><V2 /></Labeled>
        <Labeled label="V3 · Движение по треку"><V3 /></Labeled>
        <Labeled label="V4 · Прирост-стрелка героем"><V4 /></Labeled>
      </div>
    </div>
  );
}
ReactDOM.createRoot(document.getElementById('root')).render(<App />);
