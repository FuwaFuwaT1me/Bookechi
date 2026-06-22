// Bookechi — exploration: варианты «Обложка-герой» (Success A) с анимацией
const { useState: useA, useEffect: useAE } = React;

const AT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  streakBadge: '#F0C9A8', streakG1: '#FBE9D6', goalG1: '#D98E63', goalG2: '#9E4A2C',
  flame: '#BE5E3B', on: '#FFF6EE', serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const STONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const B = { title: 'Норвежский лес', author: 'Харуки Мураками', before: 57, after: 64, delta: 24, streak: 8, mins: 47, page: 206, total: 320 };

function Flame({ s = 16, c = AT.flame }) { return <svg width={s} height={s} viewBox="0 0 24 24" fill={c}><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>; }
const easeOut = (k) => 1 - Math.pow(1 - k, 3);

function CountUp({ to, dur = 1000, prefix = '' }) {
  const [v, setV] = useA(0);
  useAE(() => { let raf; const t0 = performance.now(); const tick = (t) => { const k = Math.min(1, (t - t0) / dur); setV(Math.round(easeOut(k) * to)); if (k < 1) raf = requestAnimationFrame(tick); }; raf = requestAnimationFrame(tick); return () => cancelAnimationFrame(raf); }, [to]);
  return <>{prefix}{v}</>;
}

function useReplay(ms) {
  const [k, setK] = useA(0);
  useAE(() => { const id = setInterval(() => setK((x) => x + 1), ms); return () => clearInterval(id); }, []);
  return [k, () => setK((x) => x + 1)];
}

function CoverBase({ w = 116, r = 16 }) {
  const h = Math.round(w * 1.45);
  return (
    <div style={{ width: w, height: h, borderRadius: r, overflow: 'hidden', position: 'relative',
      background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`,
      color: STONE.fg, padding: '13px 11px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
      boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
      <div style={{ position: 'absolute', left: w * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
      <div style={{ fontFamily: AT.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 13.5, paddingLeft: w * 0.1 }}>{B.title}</div>
      <div style={{ fontSize: 8.5, opacity: 0.85, paddingLeft: w * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{B.author}</div>
    </div>
  );
}

function Btn() { return <button style={{ height: 52, borderRadius: 18, border: 'none', background: AT.accent, color: AT.on, fontSize: 16, fontWeight: 600, fontFamily: AT.sans, width: '100%', cursor: 'pointer' }}>Готово</button>; }
function Screen({ children, onClick }) { return <div onClick={onClick} style={{ width: 390, height: 700, background: AT.canvas, fontFamily: AT.sans, color: AT.text, display: 'flex', flexDirection: 'column', padding: 28, cursor: 'pointer' }}>{children}</div>; }
function Chip() { return <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, background: AT.streakBadge, color: AT.accentDeep, borderRadius: 999, padding: '6px 13px', fontSize: 13, fontWeight: 700 }}><Flame s={15} /> {B.streak} дней подряд</div>; }
function BeforeAfter() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, fontFamily: AT.serif, fontWeight: 600, fontSize: 18 }}>
      <span style={{ color: AT.text2 }}>{B.before}%</span>
      <span style={{ width: 64, height: 6, borderRadius: 999, background: AT.chip, overflow: 'hidden' }}><span className="a-bar" style={{ display: 'block', height: '100%', background: `linear-gradient(90deg,${AT.goalG1},${AT.goalG2})`, '--w': B.after + '%' }}></span></span>
      <span style={{ color: AT.accentDeep }}>{B.after}%</span>
    </div>
  );
}

/* ════ A1 · ЗАЛИВКА ════ */
function FillCover() {
  const W = 124, H = Math.round(W * 1.45), r = 16;
  const f0 = 1 - B.before / 100, f1 = 1 - B.after / 100, y1 = -B.after / 100 * H;
  return (
    <div style={{ position: 'relative', width: W, height: H, filter: 'drop-shadow(0 16px 30px rgba(56,42,32,0.42))' }}>
      <CoverBase w={W} r={r} />
      <div className="a-fill" style={{ position: 'absolute', left: 0, right: 0, top: 0, height: '100%', transformOrigin: 'top', borderRadius: `${r}px ${r}px 0 0`, background: 'color-mix(in oklab,' + AT.canvas + ' 66%, transparent)', '--f0': f0, '--f1': f1 }}></div>
      <div className="a-line" style={{ position: 'absolute', left: 0, right: 0, bottom: 0, height: 3, background: AT.accent, boxShadow: '0 0 0 1px ' + AT.surfacePure, '--y1': y1 + 'px' }}>
        <span style={{ position: 'absolute', right: 6, top: '50%', transform: 'translateY(-50%)', height: 20, lineHeight: '20px', padding: '0 8px', borderRadius: 999, background: AT.accent, color: AT.on, fontSize: 10.5, fontWeight: 700, border: '1.5px solid ' + AT.surfacePure }}>{B.after}%</span>
      </div>
      <div className="a-badge" style={{ position: 'absolute', right: -14, top: -12, background: AT.surface, borderRadius: 999, padding: '6px 11px', boxShadow: '0 6px 16px -4px rgba(56,42,32,0.4)', display: 'flex', alignItems: 'center', gap: 5 }}>
        <Flame s={15} /><span style={{ fontWeight: 700, fontSize: 14, color: AT.accentDeep }}>{B.streak}</span>
      </div>
    </div>
  );
}
function VarFill({ replayKey }) {
  return (
    <div key={replayKey} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
      <div className="a-pop" style={{ marginBottom: 26 }}><FillCover /></div>
      <div className="a-rise" style={{ animationDelay: '.5s', fontFamily: AT.serif, fontWeight: 600, fontSize: 30 }}>+<CountUp to={B.delta} /> страницы</div>
      <div className="a-rise" style={{ animationDelay: '.62s', color: AT.text2, fontSize: 14.5, marginTop: 8 }}>сегодня за {B.mins} мин · стр. {B.page}</div>
      <div className="a-rise" style={{ animationDelay: '.74s', marginTop: 22 }}><BeforeAfter /></div>
    </div>
  );
}

/* ════ A2 · РАМКА ════ */
function FrameCover() {
  const W = 116, H = Math.round(W * 1.45), r = 14, pad = 8;
  const ow = W + pad * 2, oh = H + pad * 2, rr = r + 5;
  const off = 100 - B.after;
  return (
    <div style={{ position: 'relative', width: ow, height: oh, filter: 'drop-shadow(0 16px 30px rgba(56,42,32,0.42))' }}>
      <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0, overflow: 'visible' }}>
        <rect x="2.5" y="2.5" width={ow - 5} height={oh - 5} rx={rr} fill="none" stroke={AT.chip} strokeWidth="4" />
        <rect className="a-frame" x="2.5" y="2.5" width={ow - 5} height={oh - 5} rx={rr} fill="none" stroke="url(#ag)" strokeWidth="4" strokeLinecap="round" pathLength="100" strokeDasharray="100" style={{ '--off': off }} transform={`rotate(-90 ${ow / 2} ${oh / 2})`} />
        <defs><linearGradient id="ag" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={AT.goalG1} /><stop offset="1" stopColor={AT.goalG2} /></linearGradient></defs>
      </svg>
      <div style={{ position: 'absolute', left: pad, top: pad }}><CoverBase w={W} r={r} /></div>
      <span className="a-flag" style={{ position: 'absolute', left: '50%', bottom: -3, transform: 'translateX(-50%)', height: 24, lineHeight: '24px', padding: '0 11px', borderRadius: 999, background: AT.accent, color: AT.on, fontSize: 12.5, fontWeight: 700, border: '2px solid ' + AT.canvas }}>{B.after}%</span>
    </div>
  );
}
function VarFrame({ replayKey }) {
  return (
    <div key={replayKey} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
      <div className="a-rise" style={{ animationDelay: '.05s', marginBottom: 22 }}><Chip /></div>
      <div className="a-pop" style={{ marginBottom: 26 }}><FrameCover /></div>
      <div className="a-rise" style={{ animationDelay: '.55s', fontFamily: AT.serif, fontWeight: 600, fontSize: 30 }}>+<CountUp to={B.delta} /> страницы</div>
      <div className="a-rise" style={{ animationDelay: '.67s', color: AT.text2, fontSize: 14.5, marginTop: 8 }}>сегодня за {B.mins} мин · стр. {B.page}</div>
    </div>
  );
}

/* ════ A3 · ПОСТЕР (горизонтальная композиция) ════ */
function VarPoster({ replayKey }) {
  const W = 96, H = Math.round(W * 1.45), r = 12;
  const f1 = 1 - B.after / 100;
  return (
    <div key={replayKey} style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
      <div className="a-rise" style={{ background: AT.cardTint, borderRadius: 26, padding: 22, display: 'flex', gap: 18, alignItems: 'center', boxShadow: '0 16px 40px -18px rgba(56,42,32,0.4)' }}>
        <div className="a-pop" style={{ position: 'relative', width: W, height: H, flex: 'none', filter: 'drop-shadow(0 10px 22px rgba(56,42,32,0.4))' }}>
          <CoverBase w={W} r={r} />
          <div className="a-fill" style={{ position: 'absolute', left: 0, right: 0, top: 0, height: '100%', transformOrigin: 'top', borderRadius: `${r}px ${r}px 0 0`, background: 'color-mix(in oklab,' + AT.cardTint + ' 60%, transparent)', '--f0': 1 - B.before / 100, '--f1': f1 }}></div>
        </div>
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: AT.streakBadge, color: AT.accentDeep, borderRadius: 999, padding: '4px 10px', fontSize: 12, fontWeight: 700 }}><Flame s={13} /> {B.streak} дней</div>
          <div style={{ fontFamily: AT.serif, fontWeight: 600, fontSize: 34, marginTop: 12, color: AT.accentDeep, lineHeight: 1 }}>+<CountUp to={B.delta} /></div>
          <div style={{ fontFamily: AT.serif, fontSize: 16, marginTop: 2 }}>страницы сегодня</div>
          <div style={{ fontSize: 12.5, color: AT.text2, marginTop: 10 }}>{B.mins} мин · стр. {B.page} из {B.total}</div>
        </div>
      </div>
      <div className="a-rise" style={{ animationDelay: '.3s', marginTop: 18 }}>
        <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, color: AT.text2, marginBottom: 7 }}><span>{B.before}%</span><span>{B.after}%</span></div>
        <div style={{ height: 8, borderRadius: 999, background: AT.chip, overflow: 'hidden' }}><div className="a-bar" style={{ height: '100%', borderRadius: 999, background: `linear-gradient(90deg,${AT.goalG1},${AT.goalG2})`, '--w': B.after + '%' }}></div></div>
      </div>
    </div>
  );
}

/* ════ A4 · МИНИМАЛ-ГЛУБИНА ════ */
function VarMinimal({ replayKey }) {
  const W = 132, H = Math.round(W * 1.45), r = 18;
  return (
    <div key={replayKey} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', gap: 0 }}>
      <div className="a-float" style={{ position: 'relative', width: W, height: H, marginBottom: 34 }}>
        <CoverBase w={W} r={r} />
        <div className="a-fill" style={{ position: 'absolute', left: 0, right: 0, top: 0, height: '100%', transformOrigin: 'top', borderRadius: `${r}px ${r}px 0 0`, background: 'color-mix(in oklab,' + AT.canvas + ' 66%, transparent)', '--f0': 1 - B.before / 100, '--f1': 1 - B.after / 100 }}></div>
      </div>
      <div className="a-rise" style={{ animationDelay: '.55s', display: 'inline-flex', alignItems: 'center', gap: 6, color: AT.accentDeep, fontWeight: 700, fontSize: 13 }}><Flame s={14} /> {B.streak} дней подряд</div>
      <div className="a-rise" style={{ animationDelay: '.66s', fontFamily: AT.serif, fontWeight: 600, fontSize: 32, marginTop: 14 }}>+<CountUp to={B.delta} /> страницы</div>
      <div className="a-rise" style={{ animationDelay: '.78s', color: AT.text2, fontSize: 14.5, marginTop: 8 }}>{B.before}% → {B.after}% · {B.mins} мин</div>
    </div>
  );
}

function Wrap({ Comp }) {
  const [k, replay] = useReplay(4200);
  return <Screen onClick={replay}><Comp replayKey={k} /><Btn /></Screen>;
}

function Labeled({ label, children }) {
  return (
    <div style={{ flex: 'none' }}>
      <div style={{ fontSize: 13, fontWeight: 600, color: AT.text2, marginBottom: 10 }}>{label}</div>
      <div style={{ borderRadius: 28, overflow: 'hidden', boxShadow: '0 20px 50px -20px rgba(56,42,32,0.45)' }}>{children}</div>
    </div>
  );
}

function App() {
  return (
    <div style={{ minHeight: '100vh', padding: '32px 36px', boxSizing: 'border-box' }}>
      <h1 style={{ fontFamily: AT.serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px', color: AT.text }}>«Обложка-герой» — арт-направления + анимация</h1>
      <p style={{ color: AT.text2, fontSize: 14, margin: '0 0 26px' }}>Живые · кликните по экрану, чтобы повторить анимацию · авто-повтор каждые 4 с</p>
      <div style={{ display: 'flex', gap: 32, overflowX: 'auto', paddingBottom: 24 }}>
        <Labeled label="A1 · Заливка"><Wrap Comp={VarFill} /></Labeled>
        <Labeled label="A2 · Рамка"><Wrap Comp={VarFrame} /></Labeled>
        <Labeled label="A3 · Постер"><Wrap Comp={VarPoster} /></Labeled>
        <Labeled label="A4 · Минимал-глубина"><Wrap Comp={VarMinimal} /></Labeled>
      </div>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
