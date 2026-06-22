// Bookechi — exploration: обложка на экране обновления прогресса
const { useState: useEp } = React;

const PT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  goalG1: '#D98E63', goalG2: '#9E4A2C', chip: '#EBE2D6',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const TONE = { bg: '#8C6E54', fg: '#F6EFE6' }; // leather «Норвежский лес»
const BOOK = { title: 'Норвежский лес', author: 'Харуки Мураками', cur: 182, pages: 320 };
const CUR = BOOK.cur / BOOK.pages;       // 0.569
const TGT = 246 / BOOK.pages;            // preview target 77%
const PCT = Math.round(CUR * 100);

/* base cover */
function Cv({ w = 108, r = 14, dim = 0, children }) {
  const h = Math.round(w * 1.45);
  return (
    <div style={{ position: 'relative', width: w, height: h, flex: 'none', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
      <div style={{
        width: w, height: h, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${TONE.bg}, color-mix(in oklab, ${TONE.bg} 74%, #000))`,
        color: TONE.fg, padding: '13px 11px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
        boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)',
      }}>
        <div style={{ position: 'absolute', left: w * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: PT.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 13.5, paddingLeft: w * 0.1 }}>{BOOK.title}</div>
        <div style={{ fontSize: 8.5, opacity: 0.85, paddingLeft: w * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{BOOK.author}</div>
      </div>
      {children}
    </div>
  );
}

function Stage({ children, tone = 'soft' }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '30px 0 18px',
      background: tone === 'soft' ? 'radial-gradient(120% 70% at 50% 14%, color-mix(in oklab, #E8C9B6 38%, transparent), transparent 70%)' : 'none' }}>
      {children}
      <div style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 18, marginTop: 18, whiteSpace: 'nowrap' }}>{BOOK.title}</div>
      <div style={{ fontSize: 13, color: PT.text2, marginTop: 4 }}>сейчас — стр. {BOOK.cur} / {BOOK.pages} · {PCT}%</div>
    </div>
  );
}

/* A · ЗАЛИВКА (current) — dim unread + waterline + gain */
function VarFill() {
  const W = 108, H = Math.round(W * 1.45), r = 14;
  return (
    <Stage>
      <Cv w={W} r={r}>
        <div style={{ position: 'absolute', left: 0, right: 0, top: 0, height: (1 - TGT) * H, background: 'color-mix(in oklab, #F4ECE1 70%, transparent)', backdropFilter: 'saturate(0.5) brightness(1.04)', borderRadius: `${r}px ${r}px 0 0` }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: CUR * H, height: (TGT - CUR) * H, background: 'linear-gradient(0deg, color-mix(in oklab, #BE5E3B 36%, transparent), color-mix(in oklab, #BE5E3B 12%, transparent))' }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: CUR * H, height: 2, background: 'rgba(255,255,255,0.55)' }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: TGT * H, height: 2, background: PT.accent, boxShadow: '0 0 10px rgba(190,94,59,0.45)' }}>
          <span style={{ position: 'absolute', right: 6, top: '50%', transform: 'translateY(-50%)', height: 19, lineHeight: '19px', padding: '0 7px', borderRadius: 999, background: PT.accent, color: '#FFF6EE', fontSize: 10.5, fontWeight: 700 }}>{Math.round(TGT * 100)}%</span>
        </div>
      </Cv>
    </Stage>
  );
}

/* B · КОЛЬЦО-ОБВОДКА — perimeter stroke traces progress */
function VarRing() {
  const W = 108, H = Math.round(W * 1.45), r = 14, pad = 7;
  const ow = W + pad * 2, oh = H + pad * 2;
  return (
    <Stage>
      <div style={{ position: 'relative', width: ow, height: oh, display: 'flex', alignItems: 'center', justifyContent: 'center', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0 }}>
          <rect x="3" y="3" width={ow - 6} height={oh - 6} rx={r + 4} fill="none" stroke={PT.chip} strokeWidth="3.5" />
          <rect x="3" y="3" width={ow - 6} height={oh - 6} rx={r + 4} fill="none" stroke="url(#pg)" strokeWidth="3.5" strokeLinecap="round"
            pathLength="100" strokeDasharray="100" strokeDashoffset={100 - PCT} transform={`rotate(-90 ${ow / 2} ${oh / 2})`} />
          <defs><linearGradient id="pg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={PT.goalG1} /><stop offset="1" stopColor={PT.goalG2} /></linearGradient></defs>
        </svg>
        <Cv w={W} r={r} />
      </div>
      <div style={{ marginTop: 14, height: 26, padding: '0 12px', borderRadius: 999, background: PT.accentSoft, color: PT.accentDeep, fontSize: 13, fontWeight: 700, display: 'flex', alignItems: 'center', gap: 6 }}>
        <span style={{ width: 7, height: 7, borderRadius: '50%', background: PT.accent }}></span>{PCT}% прочитано
      </div>
    </Stage>
  );
}

/* C · ЗАКЛАДКА — ribbon bookmark at progress position */
function VarBookmark() {
  const W = 108, H = Math.round(W * 1.45), r = 14;
  const top = (1 - CUR) * H; // ribbon tail at read boundary from top
  return (
    <Stage>
      <Cv w={W} r={r}>
        {/* ribbon */}
        <div style={{ position: 'absolute', top: -6, right: 22, width: 26, height: top + 6, background: `linear-gradient(180deg, ${PT.goalG1}, ${PT.goalG2})`, borderRadius: '3px 3px 0 0', boxShadow: '2px 2px 8px rgba(0,0,0,0.28)' }}>
          {/* notch */}
          <div style={{ position: 'absolute', bottom: -9, left: 0, width: 0, height: 0, borderLeft: '13px solid transparent', borderRight: '13px solid transparent', borderTop: `9px solid ${PT.goalG2}` }}></div>
          <span style={{ position: 'absolute', bottom: 8, left: '50%', transform: 'translateX(-50%) rotate(90deg)', transformOrigin: 'center', color: '#FFF6EE', fontSize: 10, fontWeight: 700, whiteSpace: 'nowrap' }}>{PCT}%</span>
        </div>
      </Cv>
    </Stage>
  );
}

/* D · КОРЕШОК-МЕТР — page-stack meter beside cover */
function VarPages() {
  const W = 100, H = Math.round(W * 1.45), r = 13;
  const N = 22, filled = Math.round(CUR * N);
  return (
    <Stage>
      <div style={{ display: 'flex', gap: 12, alignItems: 'stretch', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
        <Cv w={W} r={r} />
        <div style={{ display: 'flex', flexDirection: 'column-reverse', gap: 2.5, justifyContent: 'flex-start', paddingTop: 2 }}>
          {Array.from({ length: N }).map((_, i) => (
            <div key={i} style={{ width: 9, flex: 1, borderRadius: 2, background: i < filled ? `color-mix(in oklab, ${PT.accent} ${60 + (i / N) * 40}%, ${PT.goalG2})` : PT.chip }}></div>
          ))}
        </div>
        <div style={{ display: 'flex', flexDirection: 'column', justifyContent: 'flex-end', paddingBottom: 2 }}>
          <span style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 22, color: PT.accentDeep }}>{PCT}%</span>
          <span style={{ fontSize: 11, color: PT.text2 }}>{BOOK.pages - BOOK.cur} стр.<br/>осталось</span>
        </div>
      </div>
    </Stage>
  );
}

/* E · ТЁПЛОЕ СВЕЧЕНИЕ — warm glow fills from bottom, no dimming */
function VarGlow() {
  const W = 108, H = Math.round(W * 1.45), r = 14;
  return (
    <Stage>
      <Cv w={W} r={r}>
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: 0, height: CUR * H, background: `linear-gradient(0deg, color-mix(in oklab, ${PT.accent} 52%, transparent), color-mix(in oklab, ${PT.accent} 8%, transparent))`, borderRadius: `0 0 ${r}px ${r}px`, mixBlendMode: 'multiply' }}></div>
        <div style={{ position: 'absolute', left: 0, right: 0, bottom: CUR * H, height: 3, background: 'linear-gradient(90deg, transparent, #FBEEE4, transparent)', boxShadow: '0 0 12px rgba(255,220,190,0.9)' }}></div>
      </Cv>
      <div style={{ marginTop: 14, fontSize: 13, color: PT.text2 }}>осталось {BOOK.pages - BOOK.cur} стр. — примерно один вечер</div>
    </Stage>
  );
}

function App() {
  return (
    <DesignCanvas>
      <DCSection id="pcov" title="Обложка на экране прогресса" subtitle="5 идей визуализации · показано состояние 57% (у A — превью до 77%)">
        <DCArtboard id="a" label="A · Заливка (текущая)" width={300} height={360}><Frame><VarFill /></Frame></DCArtboard>
        <DCArtboard id="b" label="B · Кольцо-обводка" width={300} height={360}><Frame><VarRing /></Frame></DCArtboard>
        <DCArtboard id="e" label="E · Тёплое свечение" width={300} height={360}><Frame><VarGlow /></Frame></DCArtboard>
        <DCArtboard id="c" label="C · Закладка" width={300} height={360}><Frame><VarBookmark /></Frame></DCArtboard>
        <DCArtboard id="d" label="D · Корешок-метр" width={300} height={360}><Frame><VarPages /></Frame></DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}
function Frame({ children }) {
  return <div style={{ width: 300, height: 360, background: PT.canvas, fontFamily: PT.sans, color: PT.text, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>{children}</div>;
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
