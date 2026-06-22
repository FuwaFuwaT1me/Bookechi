// Bookechi — exploration: блок «было → стало %» (без повторного прогресс-бара)
const PT = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  goalG1: '#D98E63', goalG2: '#9E4A2C', on: '#FFF6EE',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const B = { before: 57, after: 64, total: 320, page: 206, beforePage: 182 };
const GAIN = B.after - B.before;

function Cell({ label, children }) {
  return (
    <div style={{ display: 'flex', flexDirection: 'column', gap: 14 }}>
      <div style={{ fontSize: 12.5, fontWeight: 600, color: PT.text2 }}>{label}</div>
      <div style={{ background: PT.canvas, borderRadius: 22, padding: '30px 24px', display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 150, boxShadow: '0 1px 2px rgba(56,42,32,0.05), 0 10px 30px -14px rgba(56,42,32,0.25)' }}>
        {children}
      </div>
    </div>
  );
}

function Arrow({ c = PT.text2, s = 16 }) {
  return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2.2" strokeLinecap="round" strokeLinejoin="round"><path d="M5 12h13M13 6l6 6-6 6"/></svg>;
}

/* A · две плитки «было / стало» */
function VarTiles() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 14 }}>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontFamily: PT.serif, fontSize: 30, fontWeight: 600, color: PT.text2 }}>{B.before}%</div>
        <div style={{ fontSize: 11, color: PT.text2, marginTop: 2 }}>было</div>
      </div>
      <div style={{ width: 34, height: 34, borderRadius: '50%', background: PT.accentSoft, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Arrow c={PT.accentDeep} /></div>
      <div style={{ textAlign: 'center' }}>
        <div style={{ fontFamily: PT.serif, fontSize: 40, fontWeight: 700, color: PT.accentDeep }}>{B.after}%</div>
        <div style={{ fontSize: 11, color: PT.accentDeep, fontWeight: 600, marginTop: 2 }}>стало</div>
      </div>
    </div>
  );
}

/* B · крупное «стало» + чип прироста */
function VarBigChip() {
  return (
    <div style={{ textAlign: 'center' }}>
      <div style={{ display: 'inline-flex', alignItems: 'baseline', gap: 2 }}>
        <span style={{ fontFamily: PT.serif, fontSize: 64, fontWeight: 700, color: PT.accentDeep, lineHeight: 0.9 }}>{B.after}</span>
        <span style={{ fontFamily: PT.serif, fontSize: 28, fontWeight: 600, color: PT.accentDeep }}>%</span>
      </div>
      <div style={{ marginTop: 8 }}>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, background: PT.sageSoft, color: PT.sage, borderRadius: 999, padding: '4px 11px', fontSize: 12.5, fontWeight: 700 }}>↑ +{GAIN}% сегодня</span>
      </div>
      <div style={{ fontSize: 12, color: PT.text2, marginTop: 8 }}>книги позади</div>
    </div>
  );
}

/* C · мини-кольцо с призраком прошлого */
function VarRing() {
  const R = 40, C = 2 * Math.PI * R, sw = 9;
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 18 }}>
      <div style={{ position: 'relative', width: 96, height: 96 }}>
        <svg width="96" height="96" style={{ transform: 'rotate(-90deg)' }}>
          <circle cx="48" cy="48" r={R} fill="none" stroke={PT.chip} strokeWidth={sw} />
          <circle cx="48" cy="48" r={R} fill="none" stroke={PT.accentSoft} strokeWidth={sw} strokeLinecap="round" strokeDasharray={C} strokeDashoffset={C * (1 - B.before / 100)} />
          <circle cx="48" cy="48" r={R} fill="none" stroke="url(#rg)" strokeWidth={sw} strokeLinecap="round" strokeDasharray={C} strokeDashoffset={C * (1 - B.after / 100)} opacity="0.0" />
          <defs><linearGradient id="rg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={PT.goalG1} /><stop offset="1" stopColor={PT.goalG2} /></linearGradient></defs>
        </svg>
        {/* gain arc drawn separately from before→after */}
        <svg width="96" height="96" style={{ position: 'absolute', inset: 0, transform: 'rotate(-90deg)' }}>
          <circle cx="48" cy="48" r={R} fill="none" stroke="url(#rg2)" strokeWidth={sw} strokeLinecap="round"
            strokeDasharray={`${C * GAIN / 100} ${C}`} strokeDashoffset={-C * B.before / 100} />
          <defs><linearGradient id="rg2" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={PT.goalG1} /><stop offset="1" stopColor={PT.goalG2} /></linearGradient></defs>
        </svg>
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center', fontFamily: PT.serif, fontSize: 24, fontWeight: 700, color: PT.accentDeep }}>{B.after}%</div>
      </div>
      <div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 7, fontSize: 12.5 }}><span style={{ width: 10, height: 10, borderRadius: 3, background: PT.accentSoft }}></span> было {B.before}%</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 7, fontSize: 12.5, marginTop: 7 }}><span style={{ width: 10, height: 10, borderRadius: 3, background: PT.accent }}></span> +{GAIN}% сегодня</div>
      </div>
    </div>
  );
}

/* D · «осталось» — фрейминг от обратного */
function VarRemaining() {
  const left = 100 - B.after;
  const pagesLeft = B.total - B.page;
  return (
    <div style={{ textAlign: 'center' }}>
      <div style={{ fontFamily: PT.serif, fontSize: 17, fontWeight: 500, color: PT.text2 }}>осталось</div>
      <div style={{ fontFamily: PT.serif, fontSize: 44, fontWeight: 700, color: PT.text, marginTop: 2 }}>{left}%</div>
      <div style={{ fontSize: 13, color: PT.text2, marginTop: 4 }}>примерно {pagesLeft} страниц · 4–5 вечеров</div>
      <div style={{ display: 'inline-flex', marginTop: 12, gap: 8, alignItems: 'center', fontSize: 12.5, color: PT.accentDeep, fontWeight: 600 }}>
        <span>{B.before}%</span><Arrow c={PT.accentDeep} s={14} /><span style={{ fontSize: 14, fontWeight: 700 }}>{B.after}%</span>
      </div>
    </div>
  );
}

/* E · издательская строка */
function VarEditorial() {
  return (
    <div style={{ textAlign: 'center' }}>
      <div style={{ fontFamily: PT.serif, fontSize: 22, fontWeight: 600, color: PT.text, lineHeight: 1.3 }}>
        <span style={{ color: PT.accentDeep }}>{B.after}%</span> книги позади
      </div>
      <div style={{ fontSize: 13, color: PT.text2, marginTop: 8, display: 'inline-flex', alignItems: 'center', gap: 7 }}>
        <span style={{ textDecoration: 'line-through', opacity: 0.7 }}>{B.before}%</span>
        <span style={{ background: PT.sageSoft, color: PT.sage, borderRadius: 999, padding: '3px 9px', fontWeight: 700, fontSize: 12 }}>+{GAIN}% за вечер</span>
      </div>
    </div>
  );
}

/* F · сегменты-таблетки */
function VarSegments() {
  const seg = (val, fill) => (
    <div style={{ flex: 1, textAlign: 'center', padding: '12px 8px', borderRadius: 16, background: fill ? PT.accentDeep : PT.surface, color: fill ? PT.on : PT.text, border: fill ? 'none' : '1.5px solid ' + PT.stroke }}>
      <div style={{ fontFamily: PT.serif, fontSize: fill ? 28 : 24, fontWeight: 700 }}>{val}%</div>
      <div style={{ fontSize: 11, marginTop: 2, opacity: fill ? 0.9 : 0.7 }}>{fill ? 'сейчас' : 'было'}</div>
    </div>
  );
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 10, width: 230 }}>
      {seg(B.before, false)}
      <span style={{ fontSize: 12, fontWeight: 700, color: PT.sage, flex: 'none' }}>+{GAIN}</span>
      {seg(B.after, true)}
    </div>
  );
}

function Frame({ children }) {
  return <div style={{ background: PT.cardTint, padding: 30, borderRadius: 28 }}>{children}</div>;
}

function App() {
  return (
    <div style={{ minHeight: '100vh', padding: 40, fontFamily: PT.sans, color: PT.text }}>
      <h1 style={{ fontFamily: PT.serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px' }}>Блок «было → стало %» — варианты</h1>
      <p style={{ color: PT.text2, fontSize: 14, margin: '0 0 28px' }}>Прогресс-бар уже на обложке — здесь альтернативные трактовки. Палитра Terracotta &amp; Linen</p>
      <Frame>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(3, 1fr)', gap: 22 }}>
          <Cell label="A · Две плитки «было / стало»"><VarTiles /></Cell>
          <Cell label="B · Крупное «стало» + чип"><VarBigChip /></Cell>
          <Cell label="C · Мини-кольцо с призраком"><VarRing /></Cell>
          <Cell label="D · Фрейминг «осталось»"><VarRemaining /></Cell>
          <Cell label="E · Издательская строка"><VarEditorial /></Cell>
          <Cell label="F · Сегменты-таблетки"><VarSegments /></Cell>
        </div>
      </Frame>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
