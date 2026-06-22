// Bookechi — exploration: рамка вокруг обложки, заполняемая по %
const FT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  goalG1: '#D98E63', goalG2: '#9E4A2C', chip: '#EBE2D6',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const FTONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const FBOOK = { title: 'Норвежский лес', author: 'Харуки Мураками', cur: 182, pages: 320 };
const FCUR = FBOOK.cur / FBOOK.pages;
const FPCT = Math.round(FCUR * 100);

function FCv({ w = 104, r = 12 }) {
  const h = Math.round(w * 1.45);
  return (
    <div style={{
      width: w, height: h, borderRadius: r, overflow: 'hidden', position: 'relative', flex: 'none',
      background: `linear-gradient(158deg, ${FTONE.bg}, color-mix(in oklab, ${FTONE.bg} 74%, #000))`,
      color: FTONE.fg, padding: '12px 10px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
      boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)',
    }}>
      <div style={{ position: 'absolute', left: w * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
      <div style={{ fontFamily: FT.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 12.5, paddingLeft: w * 0.1 }}>{FBOOK.title}</div>
      <div style={{ fontSize: 8, opacity: 0.85, paddingLeft: w * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{FBOOK.author}</div>
    </div>
  );
}

function FStage({ children, caption }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '28px 0 18px' }}>
      {children}
      <div style={{ fontFamily: FT.serif, fontWeight: 600, fontSize: 17, marginTop: 18, whiteSpace: 'nowrap' }}>{FBOOK.title}</div>
      <div style={{ fontSize: 12.5, color: FT.text2, marginTop: 4, whiteSpace: 'nowrap' }}>{caption || 'стр. ' + FBOOK.cur + ' / ' + FBOOK.pages + ' · ' + FPCT + '%'}</div>
    </div>
  );
}

function grad(id) {
  return <defs><linearGradient id={id} x1="0" y1="1" x2="0.4" y2="0"><stop offset="0" stopColor={FT.goalG1} /><stop offset="1" stopColor={FT.goalG2} /></linearGradient></defs>;
}

/* half perimeter path: bottom-center → up the right side → top-center */
function halfPath(W, H, rr) {
  return `M ${W / 2} ${H} L ${W - rr} ${H} Q ${W} ${H} ${W} ${H - rr} L ${W} ${rr} Q ${W} 0 ${W - rr} 0 L ${W / 2} 0`;
}

/* 1 · СНИЗУ ВВЕРХ — symmetric fill from bottom up both sides */
function FrameBottomUp() {
  const W = 104, H = Math.round(W * 1.45), r = 13, pad = 8;
  const ow = W + pad * 2, oh = H + pad * 2, rr = r + 5;
  const d = halfPath(ow - 6, oh - 6, rr);
  return (
    <FStage>
      <div style={{ position: 'relative', width: ow, height: oh, display: 'flex', alignItems: 'center', justifyContent: 'center', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0 }}>
          <g transform="translate(3,3)">
            <path d={d} fill="none" stroke={FT.chip} strokeWidth="3.5" strokeLinecap="round" />
            <path d={d} fill="none" stroke={FT.chip} strokeWidth="3.5" strokeLinecap="round" transform={`scale(-1,1) translate(${-(ow - 6)},0)`} />
            <path d={d} fill="none" stroke="url(#fbu)" strokeWidth="3.5" strokeLinecap="round" pathLength="100" strokeDasharray="100" strokeDashoffset={100 - FPCT} />
            <path d={d} fill="none" stroke="url(#fbu)" strokeWidth="3.5" strokeLinecap="round" pathLength="100" strokeDasharray="100" strokeDashoffset={100 - FPCT} transform={`scale(-1,1) translate(${-(ow - 6)},0)`} />
          </g>
          {grad('fbu')}
        </svg>
        <FCv w={W} r={r} />
      </div>
    </FStage>
  );
}

/* 2 · ТОНКАЯ ОБВОДКА — single clockwise hairline + % chip */
function FrameHairline() {
  const W = 104, H = Math.round(W * 1.45), r = 13, pad = 7;
  const ow = W + pad * 2, oh = H + pad * 2, rr = r + 4;
  return (
    <FStage caption={'осталось ' + (FBOOK.pages - FBOOK.cur) + ' стр.'}>
      <div style={{ position: 'relative', width: ow, height: oh, display: 'flex', alignItems: 'center', justifyContent: 'center', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0 }}>
          <rect x="2.5" y="2.5" width={ow - 5} height={oh - 5} rx={rr} fill="none" stroke={FT.chip} strokeWidth="3" />
          <rect x="2.5" y="2.5" width={ow - 5} height={oh - 5} rx={rr} fill="none" stroke="url(#fh)" strokeWidth="3" strokeLinecap="round"
            pathLength="100" strokeDasharray="100" strokeDashoffset={100 - FPCT} transform={`rotate(-90 ${ow / 2} ${oh / 2})`} />
          {grad('fh')}
        </svg>
        <FCv w={W} r={r} />
        <div style={{ position: 'absolute', bottom: -11, left: '50%', transform: 'translateX(-50%)', height: 24, padding: '0 11px', borderRadius: 999, background: FT.accent, color: '#FFF6EE', fontSize: 12.5, fontWeight: 700, display: 'flex', alignItems: 'center', boxShadow: '0 3px 10px -2px rgba(132,59,34,0.5)' }}>{FPCT}%</div>
      </div>
    </FStage>
  );
}

/* 3 · ПАСПАРТУ — thick mat frame, progress on outer edge */
function FramePasse() {
  const W = 92, H = Math.round(W * 1.45), r = 9, mat = 13;
  const ow = W + mat * 2, oh = H + mat * 2, rr = r + mat;
  return (
    <FStage>
      <div style={{ position: 'relative', width: ow, height: oh, filter: 'drop-shadow(0 16px 32px rgba(56,42,32,0.4))' }}>
        <div style={{ position: 'absolute', inset: 0, borderRadius: rr, background: FT.surfacePure, boxShadow: 'inset 0 1px 2px rgba(255,255,255,0.8), inset 0 -2px 6px rgba(56,42,32,0.12)' }}></div>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0 }}>
          <rect x="2" y="2" width={ow - 4} height={oh - 4} rx={rr - 1} fill="none" stroke={FT.chip} strokeWidth="3.5" />
          <rect x="2" y="2" width={ow - 4} height={oh - 4} rx={rr - 1} fill="none" stroke="url(#fp)" strokeWidth="3.5" strokeLinecap="round"
            pathLength="100" strokeDasharray="100" strokeDashoffset={100 - FPCT} transform={`rotate(-90 ${ow / 2} ${oh / 2})`} />
          {grad('fp')}
        </svg>
        <div style={{ position: 'absolute', top: mat, left: mat }}><FCv w={W} r={r} /></div>
      </div>
      <div style={{ marginTop: 14, fontSize: 13, fontWeight: 600, color: FT.accentDeep }}>{FPCT}% пройдено</div>
    </FStage>
  );
}

/* 4 · ЗАСЕЧКИ — segmented tick frame fills by % */
function FrameSegments() {
  const W = 104, H = Math.round(W * 1.45), r = 13, pad = 8;
  const ow = W + pad * 2, oh = H + pad * 2, rr = r + 5;
  return (
    <FStage>
      <div style={{ position: 'relative', width: ow, height: oh, display: 'flex', alignItems: 'center', justifyContent: 'center', filter: 'drop-shadow(0 14px 30px rgba(56,42,32,0.42))' }}>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0 }}>
          <rect x="3" y="3" width={ow - 6} height={oh - 6} rx={rr} fill="none" stroke={FT.chip} strokeWidth="4" strokeDasharray="1.6 2.2" pathLength="100" />
          <rect x="3" y="3" width={ow - 6} height={oh - 6} rx={rr} fill="none" stroke="url(#fs)" strokeWidth="4" strokeDasharray="1.6 2.2" pathLength="100" strokeDashoffset={100 - FPCT}
            style={{ strokeDasharray: '1.6 2.2' }} transform={`rotate(-90 ${ow / 2} ${oh / 2})`} />
          {grad('fs')}
        </svg>
        <FCv w={W} r={r} />
      </div>
      <div style={{ marginTop: 14, fontSize: 13, fontWeight: 600, color: FT.accentDeep }}>{FPCT}%</div>
    </FStage>
  );
}

function FrameWrap({ children }) {
  return <div style={{ width: 300, height: 360, background: FT.canvas, fontFamily: FT.sans, color: FT.text, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>{children}</div>;
}

function App() {
  return (
    <DesignCanvas>
      <DCSection id="frame" title="Рамка вокруг обложки, заполняемая по %" subtitle="Состояние 57% · палитра Terracotta & Linen">
        <DCArtboard id="1" label="1 · Снизу вверх (симметрично)" width={300} height={360}><FrameWrap><FrameBottomUp /></FrameWrap></DCArtboard>
        <DCArtboard id="2" label="2 · Тонкая обводка + чип" width={300} height={360}><FrameWrap><FrameHairline /></FrameWrap></DCArtboard>
        <DCArtboard id="3" label="3 · Паспарту" width={300} height={360}><FrameWrap><FramePasse /></FrameWrap></DCArtboard>
        <DCArtboard id="4" label="4 · Засечки" width={300} height={360}><FrameWrap><FrameSegments /></FrameWrap></DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
