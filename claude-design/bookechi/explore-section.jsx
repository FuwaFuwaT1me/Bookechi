// Bookechi — exploration: «Ещё в чтении и планах» redesign directions
const { useState: useEx } = React;

/* ── tokens (light Terracotta & Linen) ── */
const T = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  goalG1: '#D98E63', goalG2: '#9E4A2C',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};

const TONES = {
  stone:    { bg: '#6E6353', fg: '#F2EEE6' },
  terra:    { bg: '#BE5E3B', fg: '#FBEEE4' },
  walnut:   { bg: '#A08B7C', fg: '#F7F1E8' },
  sage:     { bg: '#5C6B5E', fg: '#EDF1EC' },
  clay:     { bg: '#9E4A2C', fg: '#FAEBE0' },
  ochre:    { bg: '#B08968', fg: '#FAF3E8' },
};

/* demo books for the section */
const READING = { title: 'Думай медленно… решай быстро', author: 'Даниэль Канеман', cur: 120, pages: 456, tone: 'stone' };
const PLANNED = [
  { title: 'Маленькая жизнь', author: 'Ханья Янагихара', pages: 720, tone: 'terra' },
  { title: 'Имя розы', author: 'Умберто Эко', pages: 672, tone: 'walnut' },
  { title: 'Пикник на обочине', author: 'Стругацкие', pages: 224, tone: 'sage' },
  { title: 'Дом, в котором…', author: 'Мариам Петросян', pages: 960, tone: 'ochre' },
];

/* ── Cover ── */
function Cover({ tone, title, author, w = 46, r = 9, tilt = false }) {
  const c = TONES[tone];
  const h = Math.round(w * 1.45);
  const big = w >= 80;
  return (
    <div style={{
      width: w, height: h, borderRadius: r, flex: 'none', position: 'relative', overflow: 'hidden',
      background: `linear-gradient(158deg, ${c.bg}, color-mix(in oklab, ${c.bg} 76%, #000))`,
      color: c.fg, padding: big ? '11px 9px' : '7px 6px',
      display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
      boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28), 0 6px 18px -6px rgba(56,42,32,0.4)',
      transform: tilt ? 'perspective(600px) rotateY(-11deg)' : 'none',
    }}>
      <div style={{ position: 'absolute', left: w * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
      <div style={{ fontFamily: T.serif, fontWeight: 600, lineHeight: 1.16, fontSize: big ? 12.5 : 8.5, paddingLeft: w * 0.1, overflow: 'hidden', display: '-webkit-box', WebkitLineClamp: big ? 4 : 3, WebkitBoxOrient: 'vertical' }}>{title}</div>
      <div style={{ fontSize: big ? 8.5 : 6, opacity: 0.85, paddingLeft: w * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{author}</div>
    </div>
  );
}

function Label({ children }) {
  return <div style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', color: T.text2, marginBottom: 13 }}>{children}</div>;
}

/* progress ring */
function Ring({ pct, size = 30, sw = 3, children }) {
  const R = (size - sw) / 2, C = 2 * Math.PI * R;
  return (
    <div style={{ position: 'relative', width: size, height: size, flex: 'none' }}>
      <svg width={size} height={size} style={{ transform: 'rotate(-90deg)' }}>
        <circle cx={size / 2} cy={size / 2} r={R} fill="none" stroke={T.chip} strokeWidth={sw} />
        <circle cx={size / 2} cy={size / 2} r={R} fill="none" stroke="url(#exg)" strokeWidth={sw} strokeLinecap="round"
          strokeDasharray={C} strokeDashoffset={C * (1 - pct / 100)} />
        <defs><linearGradient id="exg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={T.goalG1} /><stop offset="1" stopColor={T.goalG2} /></linearGradient></defs>
      </svg>
      {children && <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>{children}</div>}
    </div>
  );
}

function Chevron({ s = 18 }) {
  return <svg width={s} height={s} viewBox="0 0 24 24" fill="none" stroke={T.text2} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M9 6l6 6-6 6"/></svg>;
}

/* ════════════ A · КНИЖНАЯ ПОЛКА (carousel) ════════════ */
function VariantShelf() {
  const pct = Math.round((READING.cur / READING.pages) * 100);
  return (
    <div className="ex-stagger">
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 13 }}>
        <span style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', color: T.text2 }}>Ещё на полке</span>
        <span style={{ fontSize: 12.5, color: T.accentDeep, fontWeight: 600 }}>Все →</span>
      </div>
      <div style={{ display: 'flex', gap: 16, overflowX: 'auto', paddingBottom: 8, margin: '0 -4px', paddingLeft: 4 }}>
        {/* reading one with ring */}
        <div className="ex-shelf-item" style={{ width: 92, flex: 'none' }}>
          <div style={{ position: 'relative' }}>
            <Cover tone={READING.tone} title={READING.title} author={READING.author} w={92} r={11} />
            <div style={{ position: 'absolute', top: -7, right: -7, background: T.surface, borderRadius: '50%', padding: 3, boxShadow: '0 3px 10px rgba(56,42,32,0.25)' }}>
              <Ring pct={pct} size={30} sw={3}><span style={{ fontSize: 8.5, fontWeight: 700, color: T.accentDeep }}>{pct}</span></Ring>
            </div>
          </div>
          <div style={{ fontSize: 11.5, fontWeight: 600, marginTop: 9, lineHeight: 1.25, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{READING.title}</div>
          <div style={{ fontSize: 10.5, color: T.accentDeep, fontWeight: 600, marginTop: 3 }}>Продолжить</div>
        </div>
        {PLANNED.map((b) => (
          <div key={b.title} className="ex-shelf-item" style={{ width: 92, flex: 'none' }}>
            <div style={{ position: 'relative' }}>
              <Cover tone={b.tone} title={b.title} author={b.author} w={92} r={11} />
              <div style={{ position: 'absolute', inset: 0, borderRadius: 11, background: 'linear-gradient(180deg, transparent 55%, rgba(0,0,0,0.32))' }}></div>
              <button className="ex-play" aria-label="Начать">
                <svg width="15" height="15" viewBox="0 0 24 24" fill={T.surfacePure}><path d="M8 5v14l11-7z"/></svg>
              </button>
            </div>
            <div style={{ fontSize: 11.5, fontWeight: 600, marginTop: 9, lineHeight: 1.25, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{b.title}</div>
            <div style={{ fontSize: 10.5, color: T.text2, marginTop: 3 }}>{b.pages} стр.</div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ════════════ B · ВИТРИНА (rich tinted cards) ════════════ */
function RichCard({ b, reading, i }) {
  const c = TONES[b.tone];
  const pct = reading ? Math.round((b.cur / b.pages) * 100) : 0;
  return (
    <div className="ex-rich" style={{ animationDelay: (i * 0.08) + 's',
      background: `linear-gradient(110deg, color-mix(in oklab, ${c.bg} 13%, ${T.surface}), ${T.surface} 62%)`,
      border: `1px solid ${T.stroke}`, borderRadius: 20, padding: 12, display: 'flex', gap: 14, alignItems: 'center' }}>
      <Cover tone={b.tone} title={b.title} author={b.author} w={52} r={10} />
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14.5, fontWeight: 600, lineHeight: 1.25, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{b.title}</div>
        <div style={{ fontSize: 12, color: T.text2, marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{b.author}</div>
        {reading ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 9, marginTop: 9 }}>
            <div style={{ flex: 1, height: 6, borderRadius: 999, background: T.chip, overflow: 'hidden' }}>
              <div className="ex-fill" style={{ width: pct + '%', height: '100%', borderRadius: 999, background: `linear-gradient(90deg, ${T.goalG1}, ${T.goalG2})` }}></div>
            </div>
            <span style={{ fontSize: 12, fontWeight: 700, color: T.accentDeep, flex: 'none' }}>{pct}%</span>
          </div>
        ) : (
          <div style={{ marginTop: 8 }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', height: 22, padding: '0 10px', borderRadius: 999, background: T.chip, color: T.text2, fontSize: 11, fontWeight: 600 }}>В планах · {b.pages} стр.</span>
          </div>
        )}
      </div>
      <button className="ex-pill" style={{ flex: 'none' }}>{reading ? 'Дальше' : 'Начать'}</button>
    </div>
  );
}
function VariantShowcase() {
  return (
    <div>
      <Label>Ещё в чтении и планах</Label>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
        <RichCard b={READING} reading i={0} />
        {PLANNED.slice(0, 2).map((b, i) => <RichCard key={b.title} b={b} i={i + 1} />)}
      </div>
    </div>
  );
}

/* ════════════ C · ДВЕ ДОРОЖКИ (split rails) ════════════ */
function VariantRails() {
  const pct = Math.round((READING.cur / READING.pages) * 100);
  return (
    <div className="ex-stagger">
      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
        <span style={{ width: 7, height: 7, borderRadius: '50%', background: T.accent }}></span>
        <span style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', color: T.text2 }}>Продолжить чтение</span>
      </div>
      <div className="ex-mini" style={{ background: T.surface, border: `1px solid ${T.stroke}`, borderRadius: 18, padding: 12, display: 'flex', gap: 12, alignItems: 'center', marginBottom: 22 }}>
        <Cover tone={READING.tone} title={READING.title} author={READING.author} w={44} r={9} />
        <div style={{ flex: 1, minWidth: 0 }}>
          <div style={{ fontSize: 13.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{READING.title}</div>
          <div style={{ fontSize: 11.5, color: T.text2, marginTop: 3 }}>стр. {READING.cur} из {READING.pages}</div>
        </div>
        <Ring pct={pct} size={38} sw={3.5}><span style={{ fontSize: 10, fontWeight: 700, color: T.accentDeep }}>{pct}%</span></Ring>
      </div>

      <div style={{ display: 'flex', alignItems: 'center', gap: 8, marginBottom: 12 }}>
        <span style={{ width: 7, height: 7, borderRadius: '50%', background: T.sage }}></span>
        <span style={{ fontSize: 11.5, fontWeight: 600, letterSpacing: '0.08em', textTransform: 'uppercase', color: T.text2 }}>В планах · {PLANNED.length}</span>
      </div>
      <div style={{ display: 'flex', gap: 12, overflowX: 'auto', paddingBottom: 6 }}>
        {PLANNED.map((b) => (
          <div key={b.title} className="ex-shelf-item" style={{ width: 70, flex: 'none' }}>
            <Cover tone={b.tone} title={b.title} author={b.author} w={70} r={10} />
            <div style={{ fontSize: 10.5, fontWeight: 600, marginTop: 7, lineHeight: 1.2, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden' }}>{b.title}</div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ════════════ D · УТОНЧЁННЫЙ СПИСОК (elevated list) ════════════ */
function ListRow({ b, reading, i }) {
  const pct = reading ? Math.round((b.cur / b.pages) * 100) : 0;
  return (
    <div className="ex-row" style={{ animationDelay: (i * 0.07) + 's', display: 'flex', gap: 14, alignItems: 'center', padding: '11px 14px 11px 11px', background: T.surface, border: `1px solid ${T.stroke}`, borderRadius: 18 }}>
      <div style={{ position: 'relative', flex: 'none' }}>
        <Cover tone={b.tone} title={b.title} author={b.author} w={46} r={9} />
        {reading && (
          <div style={{ position: 'absolute', bottom: -5, right: -5, background: T.surface, borderRadius: '50%', padding: 2.5, boxShadow: '0 2px 7px rgba(56,42,32,0.25)' }}>
            <Ring pct={pct} size={24} sw={2.6}><span style={{ fontSize: 7.5, fontWeight: 700, color: T.accentDeep }}>{pct}</span></Ring>
          </div>
        )}
      </div>
      <div style={{ flex: 1, minWidth: 0 }}>
        <div style={{ fontSize: 14, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{b.title}</div>
        <div style={{ fontSize: 12, color: T.text2, marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{b.author}</div>
        {reading
          ? <div style={{ fontSize: 11.5, color: T.accentDeep, fontWeight: 600, marginTop: 5 }}>Продолжить · стр. {b.cur}</div>
          : <div style={{ fontSize: 11.5, color: T.text2, marginTop: 5 }}>В планах · {b.pages} стр.</div>}
      </div>
      <span className="ex-chev" style={{ flex: 'none' }}><Chevron /></span>
    </div>
  );
}
function VariantList() {
  return (
    <div>
      <Label>Ещё в чтении и планах</Label>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 9 }}>
        <ListRow b={READING} reading i={0} />
        {PLANNED.slice(0, 2).map((b, i) => <ListRow key={b.title} b={b} i={i + 1} />)}
      </div>
    </div>
  );
}

/* phone frame wrapper for an artboard */
function Phone({ children, h = 560 }) {
  return (
    <div style={{ width: 412, height: h, background: T.canvas, padding: '24px 20px', fontFamily: T.sans, color: T.text, overflow: 'hidden' }}>
      {/* context: a hint of the hero above */}
      <div style={{ height: 1, background: T.divider, opacity: 0.5, marginBottom: 22 }}></div>
      {children}
    </div>
  );
}

function App() {
  return (
    <DesignCanvas>
      <DCSection id="dirs" title="«Ещё в чтении и планах»" subtitle="4 направления переработки · светлая тема, палитра Terracotta & Linen">
        <DCArtboard id="a" label="A · Книжная полка" width={412} height={420}>
          <Phone h={420}><VariantShelf /></Phone>
        </DCArtboard>
        <DCArtboard id="b" label="B · Витрина" width={412} height={460}>
          <Phone h={460}><VariantShowcase /></Phone>
        </DCArtboard>
        <DCArtboard id="c" label="C · Две дорожки" width={412} height={500}>
          <Phone h={500}><VariantRails /></Phone>
        </DCArtboard>
        <DCArtboard id="d" label="D · Утончённый список" width={412} height={420}>
          <Phone h={420}><VariantList /></Phone>
        </DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
