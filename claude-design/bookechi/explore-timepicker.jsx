// Bookechi — exploration: кастомные скролл-пикеры времени чтения
const { useState: useTp, useRef: useTpRef, useEffect: useTpEffect, useCallback: useTpCb } = React;

const TPT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  goalG1: '#D98E63', goalG2: '#9E4A2C', chip: '#EBE2D6', on: '#FFF6EE',
  serif: "'Lora', Georgia, serif",
};

function fmt(min) {
  if (!min || min <= 0) return '0 мин';
  const h = Math.floor(min / 60), m = min % 60;
  if (h && m) return h + ' ч ' + m + ' мин';
  return h ? h + ' ч' : m + ' мин';
}

function Header({ value }) {
  return (
    <div style={{ textAlign: 'center', marginBottom: 18 }}>
      <div style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', color: TPT.text2 }}>Сколько читали сегодня?</div>
      <div style={{ fontFamily: TPT.serif, fontWeight: 600, fontSize: 30, color: TPT.accentDeep, marginTop: 6 }}>{fmt(value)}</div>
    </div>
  );
}

/* ════ A · ВЕРТИКАЛЬНОЕ КОЛЕСО ════ */
function WheelPicker() {
  const ITEMH = 42, PAD = 2; // 2 items padding each side
  const vals = [];
  for (let m = 5; m <= 180; m += 5) vals.push(m);
  const ref = useTpRef(null);
  const [idx, setIdx] = useTp(5); // default 30 min → index 5
  const onScroll = useTpCb(() => {
    const el = ref.current; if (!el) return;
    const i = Math.round(el.scrollTop / ITEMH);
    setIdx(Math.max(0, Math.min(vals.length - 1, i)));
  }, []);
  useTpEffect(() => {
    requestAnimationFrame(() => { if (ref.current) ref.current.scrollTop = 5 * ITEMH; });
  }, []);
  return (
    <div>
      <Header value={vals[idx]} />
      <div style={{ position: 'relative', height: ITEMH * (PAD * 2 + 1), margin: '0 auto', maxWidth: 200 }}>
        {/* center band */}
        <div style={{ position: 'absolute', left: 0, right: 0, top: ITEMH * PAD, height: ITEMH, borderRadius: 14, background: 'color-mix(in oklab, var(--ac-soft) 50%, transparent)', pointerEvents: 'none', border: '1.5px solid ' + TPT.accentSoft, '--ac-soft': TPT.accentSoft }}></div>
        <div ref={ref} onScroll={onScroll} className="tp-wheel" style={{ height: '100%', overflowY: 'scroll', scrollSnapType: 'y mandatory', scrollbarWidth: 'none', maskImage: 'linear-gradient(180deg, transparent, #000 30%, #000 70%, transparent)', WebkitMaskImage: 'linear-gradient(180deg, transparent, #000 30%, #000 70%, transparent)' }}>
          <div style={{ height: ITEMH * PAD }}></div>
          {vals.map((m, i) => (
            <div key={m} style={{ height: ITEMH, scrollSnapAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center',
              fontFamily: TPT.serif, fontWeight: 600, fontSize: i === idx ? 22 : 17,
              color: i === idx ? TPT.accentDeep : TPT.text2, opacity: i === idx ? 1 : 0.5, transition: 'font-size .12s, opacity .12s' }}>
              {fmt(m)}
            </div>
          ))}
          <div style={{ height: ITEMH * PAD }}></div>
        </div>
      </div>
      <div style={{ textAlign: 'center', fontSize: 12, color: TPT.text2, marginTop: 14 }}>Крутите колесо вверх-вниз</div>
    </div>
  );
}

/* ════ B · ГОРИЗОНТАЛЬНАЯ ЛИНЕЙКА ════ */
function RulerPicker() {
  const STEP = 5, GAP = 13; // px between ticks
  const vals = [];
  for (let m = 0; m <= 180; m += STEP) vals.push(m);
  const ref = useTpRef(null);
  const [val, setVal] = useTp(30);
  const onScroll = useTpCb(() => {
    const el = ref.current; if (!el) return;
    const i = Math.round(el.scrollLeft / GAP);
    setVal(Math.max(0, Math.min(180, i * STEP)));
  }, []);
  useTpEffect(() => { requestAnimationFrame(() => { if (ref.current) ref.current.scrollLeft = (30 / STEP) * GAP; }); }, []);
  return (
    <div>
      <Header value={val} />
      <div style={{ position: 'relative' }}>
        {/* center caret */}
        <div style={{ position: 'absolute', left: '50%', top: -4, transform: 'translateX(-50%)', zIndex: 3, width: 0, height: 0, borderLeft: '6px solid transparent', borderRight: '6px solid transparent', borderTop: '8px solid ' + TPT.accent }}></div>
        <div style={{ position: 'absolute', left: '50%', top: 0, bottom: 14, width: 2, transform: 'translateX(-50%)', background: TPT.accent, zIndex: 3, borderRadius: 2 }}></div>
        <div ref={ref} onScroll={onScroll} className="tp-wheel" style={{ overflowX: 'scroll', scrollSnapType: 'x mandatory', scrollbarWidth: 'none', padding: '14px 0 8px', maskImage: 'linear-gradient(90deg, transparent, #000 18%, #000 82%, transparent)', WebkitMaskImage: 'linear-gradient(90deg, transparent, #000 18%, #000 82%, transparent)' }}>
          <div style={{ display: 'flex', alignItems: 'flex-end', height: 44, width: 'max-content', padding: '0 calc(50% - 1px)' }}>
            {vals.map((m) => {
              const major = m % 15 === 0;
              return (
                <div key={m} style={{ width: GAP, flex: 'none', display: 'flex', flexDirection: 'column', alignItems: 'center', scrollSnapAlign: 'center' }}>
                  <div style={{ width: 2, height: major ? 28 : 16, borderRadius: 2, background: major ? TPT.accentDeep : TPT.divider }}></div>
                  {major && <div style={{ fontSize: 9.5, color: TPT.text2, marginTop: 4 }}>{m}</div>}
                </div>
              );
            })}
          </div>
        </div>
      </div>
      <div style={{ textAlign: 'center', fontSize: 12, color: TPT.text2, marginTop: 10 }}>Тяните линейку вбок · минуты</div>
    </div>
  );
}

/* ════ C · ДУГА-ЦИФЕРБЛАТ ════ */
function ArcDial() {
  const R = 92, CX = 110, CY = 110, MAX = 180;
  const ref = useTpRef(null);
  const [val, setVal] = useTp(30);
  const dragging = useTpRef(false);
  // angle sweep from -220deg to 40deg (260deg span) bottom-left → bottom-right
  const A0 = -220, ASPAN = 260;
  const toXY = (frac) => {
    const a = (A0 + frac * ASPAN) * Math.PI / 180;
    return [CX + R * Math.cos(a), CY + R * Math.sin(a)];
  };
  const frac = val / MAX;
  const [kx, ky] = toXY(frac);
  const arcPath = (() => {
    const [sx, sy] = toXY(0), [ex, ey] = toXY(frac);
    const large = ASPAN * frac > 180 ? 1 : 0;
    return `M ${sx} ${sy} A ${R} ${R} 0 ${large} 1 ${ex} ${ey}`;
  })();
  const trackPath = (() => {
    const [sx, sy] = toXY(0), [ex, ey] = toXY(1);
    return `M ${sx} ${sy} A ${R} ${R} 0 1 1 ${ex} ${ey}`;
  })();
  const setFromEvent = (e) => {
    const rect = ref.current.getBoundingClientRect();
    const px = (e.touches ? e.touches[0].clientX : e.clientX) - rect.left - CX;
    const py = (e.touches ? e.touches[0].clientY : e.clientY) - rect.top - CY;
    let deg = Math.atan2(py, px) * 180 / Math.PI;
    let f = (deg - A0) / ASPAN;
    if (deg > 60) f = (deg - 360 - A0) / ASPAN;
    f = Math.max(0, Math.min(1, f));
    setVal(Math.round((f * MAX) / 5) * 5);
  };
  return (
    <div>
      <div style={{ position: 'relative', width: 220, height: 200, margin: '0 auto' }}>
        <svg ref={ref} width="220" height="200" viewBox="0 0 220 200"
          onPointerDown={(e) => { dragging.current = true; setFromEvent(e); }}
          onPointerMove={(e) => dragging.current && setFromEvent(e)}
          onPointerUp={() => dragging.current = false}
          onPointerLeave={() => dragging.current = false}
          style={{ touchAction: 'none', cursor: 'pointer' }}>
          <path d={trackPath} fill="none" stroke={TPT.chip} strokeWidth="10" strokeLinecap="round" />
          <path d={arcPath} fill="none" stroke="url(#tparc)" strokeWidth="10" strokeLinecap="round" />
          <circle cx={kx} cy={ky} r="13" fill={TPT.surfacePure} stroke={TPT.accent} strokeWidth="3.5" />
          <defs><linearGradient id="tparc" x1="0" y1="1" x2="1" y2="0"><stop offset="0" stopColor={TPT.goalG1} /><stop offset="1" stopColor={TPT.goalG2} /></linearGradient></defs>
        </svg>
        <div style={{ position: 'absolute', left: 0, right: 0, top: 78, textAlign: 'center', pointerEvents: 'none' }}>
          <div style={{ fontFamily: TPT.serif, fontWeight: 600, fontSize: 28, color: TPT.accentDeep }}>{fmt(val)}</div>
          <div style={{ fontSize: 11, color: TPT.text2, marginTop: 2 }}>сегодня</div>
        </div>
      </div>
      <div style={{ textAlign: 'center', fontSize: 12, color: TPT.text2, marginTop: 6 }}>Ведите бегунок по дуге</div>
    </div>
  );
}

/* ════ D · КОЛЕСО + ПРЕСЕТЫ ════ */
function WheelPresets() {
  const ITEMH = 40, PAD = 2;
  const vals = [];
  for (let m = 5; m <= 180; m += 5) vals.push(m);
  const ref = useTpRef(null);
  const [idx, setIdx] = useTp(5);
  const onScroll = useTpCb(() => {
    const el = ref.current; if (!el) return;
    setIdx(Math.max(0, Math.min(vals.length - 1, Math.round(el.scrollTop / ITEMH))));
  }, []);
  useTpEffect(() => { requestAnimationFrame(() => { if (ref.current) ref.current.scrollTop = 5 * ITEMH; }); }, []);
  const jump = (m) => { const i = vals.indexOf(m); if (i >= 0 && ref.current) ref.current.scrollTo({ top: i * ITEMH, behavior: 'smooth' }); };
  return (
    <div>
      <Header value={vals[idx]} />
      <div style={{ position: 'relative', height: ITEMH * (PAD * 2 + 1), maxWidth: 180, margin: '0 auto' }}>
        <div style={{ position: 'absolute', left: 0, right: 0, top: ITEMH * PAD, height: ITEMH, borderRadius: 12, background: TPT.chip, pointerEvents: 'none' }}></div>
        <div ref={ref} onScroll={onScroll} className="tp-wheel" style={{ height: '100%', overflowY: 'scroll', scrollSnapType: 'y mandatory', scrollbarWidth: 'none', maskImage: 'linear-gradient(180deg, transparent, #000 35%, #000 65%, transparent)', WebkitMaskImage: 'linear-gradient(180deg, transparent, #000 35%, #000 65%, transparent)' }}>
          <div style={{ height: ITEMH * PAD }}></div>
          {vals.map((m, i) => (
            <div key={m} style={{ height: ITEMH, scrollSnapAlign: 'center', display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: TPT.serif, fontWeight: 600, fontSize: i === idx ? 20 : 16, color: i === idx ? TPT.accentDeep : TPT.text2, opacity: i === idx ? 1 : 0.45 }}>{fmt(m)}</div>
          ))}
          <div style={{ height: ITEMH * PAD }}></div>
        </div>
      </div>
      <div style={{ display: 'flex', gap: 8, justifyContent: 'center', marginTop: 16, flexWrap: 'wrap' }}>
        {[15, 30, 45, 60].map((m) => (
          <button key={m} onClick={() => jump(m)} style={{ height: 34, padding: '0 14px', borderRadius: 999, border: '1.5px solid ' + (vals[idx] === m ? TPT.accent : TPT.stroke), background: vals[idx] === m ? 'color-mix(in oklab,' + TPT.accentSoft + ' 45%, ' + TPT.surface + ')' : TPT.surface, color: vals[idx] === m ? TPT.accentDeep : TPT.text2, fontSize: 13, fontWeight: 600, cursor: 'pointer' }}>{fmt(m)}</button>
        ))}
      </div>
    </div>
  );
}

function Frame({ children }) {
  return <div style={{ width: 320, height: 360, background: TPT.canvas, display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: 24, fontFamily: "'Inter', system-ui, sans-serif", color: TPT.text }}>{children}</div>;
}

function App() {
  return (
    <DesignCanvas>
      <DCSection id="time" title="Кастомный скролл-пикер времени" subtitle="Интерактивно — крутите/тяните. Палитра Terracotta & Linen">
        <DCArtboard id="a" label="A · Вертикальное колесо" width={320} height={360}><Frame><WheelPicker /></Frame></DCArtboard>
        <DCArtboard id="b" label="B · Горизонтальная линейка" width={320} height={360}><Frame><RulerPicker /></Frame></DCArtboard>
        <DCArtboard id="c" label="C · Дуга-циферблат" width={320} height={360}><Frame><ArcDial /></Frame></DCArtboard>
        <DCArtboard id="d" label="D · Колесо + пресеты" width={320} height={360}><Frame><WheelPresets /></Frame></DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
