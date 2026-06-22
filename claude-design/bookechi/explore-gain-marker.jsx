// Bookechi — adaptive gain marker: скобка / выноска / флажок по высоте прироста
const PT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const STONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const BK = { title: 'Норвежский лес', author: 'Харуки Мураками', total: 320 };

function bracePath(bw, h) {
  const m = bw / 2;
  return `M ${bw},0 Q ${m},0 ${m},${h * 0.16} L ${m},${h * 0.40} Q ${m},${h / 2} 0,${h / 2}`
    + ` Q ${m},${h / 2} ${m},${h * 0.60} L ${m},${h * 0.84} Q ${m},${h} ${bw},${h}`;
}

/* one cover at a given before→after, choosing the marker automatically */
function Cover({ before, after }) {
  const W = 132, H = Math.round(W * 1.45), r = 16;
  const beforeY = H * (1 - before / 100);
  const curY = H * (1 - after / 100);
  const gainH = beforeY - curY;
  const dimFrac = 1 - after / 100;
  const delta = Math.round((after - before) / 100 * BK.total);
  const bw = 10, gap = 11;

  // pick marker mode
  const mode = gainH >= 26 ? 'brace' : gainH >= 8 ? 'leader' : 'flag';

  const chip = (extra) => (
    <div style={{ whiteSpace: 'nowrap', background: PT.accent, color: PT.surfacePure, borderRadius: 999, padding: '3px 10px', fontSize: 12, fontWeight: 700, boxShadow: '0 3px 9px -2px rgba(132,59,34,0.5)', ...extra }}>+{delta} стр</div>
  );

  return (
    <div style={{ position: 'relative', width: W, height: H, filter: 'drop-shadow(0 14px 28px rgba(56,42,32,0.4))' }}>
      <div style={{ width: W, height: H, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`, color: STONE.fg,
        padding: '13px 11px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between', boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
        <div style={{ position: 'absolute', left: W * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: PT.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 13.5, paddingLeft: W * 0.1 }}>{BK.title}</div>
        <div style={{ fontSize: 8.5, opacity: 0.85, paddingLeft: W * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{BK.author}</div>
        {/* dim unread */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: 0, height: dimFrac * 100 + '%', background: 'color-mix(in oklab,' + PT.canvas + ' 64%, transparent)', borderRadius: `${r}px ${r}px 0 0` }}></div>
        {/* gain band */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: gainH, background: `linear-gradient(0deg, color-mix(in oklab,${PT.accent} 42%, transparent), color-mix(in oklab,${PT.accent} 10%, transparent))` }}></div>
        {/* waterline */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: curY, height: 3, background: PT.accent, boxShadow: '0 0 0 1px ' + PT.surfacePure }}></div>
      </div>

      {/* ── marker ── */}
      {mode === 'brace' && (
        <div style={{ position: 'absolute', left: -(bw + gap), top: curY, width: bw, height: gainH }}>
          <svg width={bw} height={gainH} viewBox={`0 0 ${bw} ${gainH}`} style={{ overflow: 'visible' }}>
            <path d={bracePath(bw, gainH)} fill="none" stroke={PT.accentDeep} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round" />
          </svg>
          <div style={{ position: 'absolute', right: bw + 7, top: '50%', transform: 'translateY(-50%)' }}>{chip()}</div>
        </div>
      )}

      {mode === 'leader' && (
        <div style={{ position: 'absolute', left: 0, top: curY }}>
          {/* dot on the waterline */}
          <div style={{ position: 'absolute', left: -5, top: -4, width: 9, height: 9, borderRadius: '50%', background: PT.accent, border: '1.5px solid ' + PT.surfacePure, boxShadow: '0 1px 4px rgba(56,42,32,0.3)' }}></div>
          {/* leader line */}
          <div style={{ position: 'absolute', left: -34, top: 0, width: 30, height: 1.5, background: PT.accentDeep }}></div>
          {/* chip to the left of the line */}
          <div style={{ position: 'absolute', left: -42, top: 0, transform: 'translate(-100%,-50%)' }}>{chip()}</div>
        </div>
      )}

      {mode === 'flag' && (
        <div style={{ position: 'absolute', left: -gap, top: curY }}>
          {/* little connector notch */}
          <div style={{ position: 'absolute', right: 0, top: 0, transform: 'translateY(-50%)', width: 7, height: 2, background: PT.accent }}></div>
          <div style={{ position: 'absolute', right: 8, top: 0, transform: 'translate(0,-50%)', whiteSpace: 'nowrap' }}>{chip()}</div>
        </div>
      )}

      {/* caption under cover */}
      <div style={{ position: 'absolute', left: 0, right: 0, bottom: -46, textAlign: 'center' }}>
        <div style={{ fontSize: 12.5, fontWeight: 700, color: PT.accentDeep }}>+{delta} стр · {after - before}%</div>
        <div style={{ fontSize: 11, color: PT.text2, marginTop: 2 }}>режим: {mode === 'brace' ? 'скобка' : mode === 'leader' ? 'выноска' : 'флажок'}</div>
      </div>
    </div>
  );
}

function Case({ label, before, after }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 16 }}>
      <div style={{ fontSize: 13, fontWeight: 600, color: PT.text2 }}>{label}</div>
      <div style={{ paddingLeft: 70, paddingBottom: 50 }}><Cover before={before} after={after} /></div>
    </div>
  );
}

function App() {
  return (
    <div style={{ minHeight: '100vh', padding: '40px 44px', fontFamily: PT.sans, color: PT.text }}>
      <h1 style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px' }}>Маркер прироста адаптируется к высоте</h1>
      <p style={{ color: PT.text2, fontSize: 14, margin: '0 0 14px' }}>Чип «+N стр» остаётся всегда — меняется только привязка. Палитра Terracotta &amp; Linen</p>
      <div style={{ background: PT.surface, border: '1px solid ' + PT.stroke, borderRadius: 28, padding: '44px 30px', display: 'flex', gap: 48, justifyContent: 'center', flexWrap: 'wrap' }}>
        <Case label="Большой прирост · скобка" before={48} after={72} />
        <Case label="Средний прирост · выноска" before={57} after={64} />
        <Case label="Малый прирост · выноска" before={60} after={63} />
        <Case label="Крошечный (1–2 стр) · флажок" before={62} after={62.6} />
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
