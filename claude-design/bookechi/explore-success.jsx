// Bookechi — exploration: экран результатов обновления прогресса (Success)
const ST = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF',
  stroke: '#E4D9CC', divider: '#D9CCBC', text: '#382A20', text2: '#8C7C6E',
  accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2',
  streakBadge: '#F0C9A8', streakG1: '#FBE9D6', streakG2: '#F3D8BF',
  goalG1: '#D98E63', goalG2: '#9E4A2C', flame: '#BE5E3B', on: '#FFF6EE',
  serif: "'Lora', Georgia, serif", sans: "'Inter', system-ui, sans-serif",
};
const STONE = { bg: '#8C6E54', fg: '#F6EFE6' };
const BOOK = { title: 'Норвежский лес', author: 'Харуки Мураками', before: 57, after: 64, delta: 24, streak: 8, mins: 47, page: 206, total: 320 };

function Flame({ size = 24, c = ST.flame }) {
  return <svg width={size} height={size} viewBox="0 0 24 24" fill={c}><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>;
}
function Clock({ size = 15, c = ST.accentDeep }) {
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="1.9" strokeLinecap="round"><circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/></svg>;
}
function Check({ size = 16, c = '#fff' }) {
  return <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke={c} strokeWidth="2.6" strokeLinecap="round" strokeLinejoin="round"><path d="M5 13l4.5 4.5L19 7"/></svg>;
}

function Cover({ w = 96, r = 13, fill = 0 }) {
  const h = Math.round(w * 1.45);
  return (
    <div style={{ position: 'relative', width: w, height: h, flex: 'none', filter: 'drop-shadow(0 14px 28px rgba(56,42,32,0.4))' }}>
      <div style={{ width: w, height: h, borderRadius: r, overflow: 'hidden', position: 'relative',
        background: `linear-gradient(158deg, ${STONE.bg}, color-mix(in oklab, ${STONE.bg} 74%, #000))`,
        color: STONE.fg, padding: '11px 9px', display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
        boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.28)' }}>
        <div style={{ position: 'absolute', left: w * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
        <div style={{ fontFamily: ST.serif, fontWeight: 600, lineHeight: 1.16, fontSize: 12.5, paddingLeft: w * 0.1 }}>{BOOK.title}</div>
        <div style={{ fontSize: 8, opacity: 0.85, paddingLeft: w * 0.1, textTransform: 'uppercase', letterSpacing: '0.04em' }}>{BOOK.author}</div>
        {fill > 0 && <div style={{ position: 'absolute', left: 0, right: 0, top: 0, height: (1 - fill) * 100 + '%', background: 'color-mix(in oklab,' + ST.canvas + ' 66%, transparent)' }}></div>}
      </div>
    </div>
  );
}

function StreakDots({ n = 8, active = 8 }) {
  const days = ['Пн','Вт','Ср','Чт','Пт','Сб','Вс'];
  return (
    <div style={{ display: 'flex', justifyContent: 'space-between' }}>
      {days.map((d, i) => {
        const on = i <= 1; // Пн, Вт filled (today = Вт)
        return (
          <div key={d} style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 6, fontSize: 10.5, fontWeight: 600, color: ST.text2 }}>
            <span>{d}</span>
            <div style={{ width: 24, height: 24, borderRadius: '50%', display: 'flex', alignItems: 'center', justifyContent: 'center',
              background: on ? ST.accent : 'transparent', border: on ? 'none' : '1.6px dashed ' + ST.divider,
              boxShadow: i === 1 ? '0 0 0 2.5px ' + ST.streakG1 + ', 0 0 0 4.5px ' + ST.accent : 'none' }}>
              {on && <Check size={12} c={ST.on} />}
            </div>
          </div>
        );
      })}
    </div>
  );
}

function Btn({ children }) {
  return <button style={{ height: 52, borderRadius: 18, border: 'none', background: ST.accent, color: ST.on, fontSize: 16, fontWeight: 600, fontFamily: ST.sans, width: '100%', cursor: 'pointer' }}>{children}</button>;
}

function Screen({ children, bg }) {
  return <div style={{ width: 390, minHeight: 680, background: bg || ST.canvas, fontFamily: ST.sans, color: ST.text, display: 'flex', flexDirection: 'column', padding: 28 }}>{children}</div>;
}

/* ════ A · ОБЛОЖКА-ГЕРОЙ ════ */
function VarCover() {
  return (
    <Screen>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center', gap: 0 }}>
        <div style={{ position: 'relative', marginBottom: 22 }}>
          <Cover w={108} fill={BOOK.after / 100} />
          <div style={{ position: 'absolute', right: -14, bottom: -10, background: ST.surface, borderRadius: 999, padding: '6px 12px', boxShadow: '0 6px 16px -4px rgba(56,42,32,0.35)', display: 'flex', alignItems: 'center', gap: 5 }}>
            <Flame size={16} /><span style={{ fontWeight: 700, fontSize: 14, color: ST.accentDeep }}>{BOOK.streak}</span>
          </div>
        </div>
        <div style={{ fontFamily: ST.serif, fontWeight: 600, fontSize: 27, lineHeight: 1.15 }}>+{BOOK.delta} страницы</div>
        <div style={{ color: ST.text2, fontSize: 14.5, marginTop: 8 }}>сегодня за {BOOK.mins} мин · стр. {BOOK.page}</div>
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginTop: 24, fontFamily: ST.serif, fontWeight: 600, fontSize: 18 }}>
          <span style={{ color: ST.text2 }}>{BOOK.before}%</span>
          <span style={{ width: 60, height: 6, borderRadius: 999, background: ST.chip, overflow: 'hidden' }}><span style={{ display: 'block', width: BOOK.after + '%', height: '100%', background: `linear-gradient(90deg,${ST.goalG1},${ST.goalG2})` }}></span></span>
          <span style={{ color: ST.accentDeep }}>{BOOK.after}%</span>
        </div>
      </div>
      <Btn>Готово</Btn>
    </Screen>
  );
}

/* ════ B · ИЗДАТЕЛЬСКИЙ (big number) ════ */
function VarEditorial() {
  return (
    <Screen bg={ST.canvas}>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, alignSelf: 'flex-start', background: ST.streakBadge, color: ST.accentDeep, borderRadius: 999, padding: '6px 13px', fontSize: 13, fontWeight: 700 }}>
          <Flame size={15} /> {BOOK.streak} дней подряд
        </div>
        <div style={{ fontFamily: ST.serif, fontWeight: 600, fontSize: 92, lineHeight: 0.95, marginTop: 26, color: ST.accentDeep }}>+{BOOK.delta}</div>
        <div style={{ fontFamily: ST.serif, fontSize: 26, fontWeight: 500, marginTop: 4 }}>страницы сегодня</div>
        <div style={{ color: ST.text2, fontSize: 15, marginTop: 14, lineHeight: 1.5 }}>«{BOOK.title}» — теперь стр. {BOOK.page} из {BOOK.total}.<br/>За {BOOK.mins} минут вечера.</div>
        <div style={{ marginTop: 26 }}>
          <div style={{ display: 'flex', justifyContent: 'space-between', fontSize: 12.5, color: ST.text2, marginBottom: 7 }}><span>{BOOK.before}%</span><span>{BOOK.after}%</span></div>
          <div style={{ height: 8, borderRadius: 999, background: ST.chip, overflow: 'hidden' }}><div style={{ width: BOOK.after + '%', height: '100%', borderRadius: 999, background: `linear-gradient(90deg,${ST.goalG1},${ST.goalG2})` }}></div></div>
        </div>
      </div>
      <Btn>Готово</Btn>
    </Screen>
  );
}

/* ════ C · ЧЕК ЧТЕНИЯ (receipt) ════ */
function VarReceipt() {
  const rows = [['Книга', BOOK.title], ['Прочитано', '+' + BOOK.delta + ' стр.'], ['Время', BOOK.mins + ' мин'], ['Страница', BOOK.page + ' / ' + BOOK.total], ['Прогресс', BOOK.before + '% → ' + BOOK.after + '%'], ['Серия', BOOK.streak + ' дней 🔥']];
  return (
    <Screen bg={ST.cardTint}>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
        <div style={{ background: ST.surfacePure, borderRadius: 20, padding: '26px 22px 22px', boxShadow: '0 16px 40px -16px rgba(56,42,32,0.4)', position: 'relative' }}>
          <div style={{ textAlign: 'center', marginBottom: 18 }}>
            <div style={{ width: 52, height: 52, borderRadius: 16, margin: '0 auto 12px', background: ST.sageSoft, color: ST.sage, display: 'flex', alignItems: 'center', justifyContent: 'center' }}><Check size={24} c={ST.sage} /></div>
            <div style={{ fontFamily: ST.serif, fontWeight: 600, fontSize: 20 }}>Прогресс записан</div>
            <div style={{ fontSize: 12.5, color: ST.text2, marginTop: 3 }}>вторник, 20 июня · 21:30</div>
          </div>
          <div style={{ borderTop: '1.5px dashed ' + ST.divider, paddingTop: 16, display: 'flex', flexDirection: 'column', gap: 11 }}>
            {rows.map(([k, v]) => (
              <div key={k} style={{ display: 'flex', justifyContent: 'space-between', gap: 12, fontSize: 14 }}>
                <span style={{ color: ST.text2 }}>{k}</span>
                <span style={{ fontWeight: 600, textAlign: 'right', maxWidth: 180, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{v}</span>
              </div>
            ))}
          </div>
          <div style={{ borderTop: '1.5px dashed ' + ST.divider, marginTop: 16, paddingTop: 14, textAlign: 'center', fontFamily: ST.serif, fontStyle: 'italic', fontSize: 14, color: ST.text2 }}>Хороший вечер для книги</div>
        </div>
      </div>
      <Btn>Готово</Btn>
    </Screen>
  );
}

/* ════ D · КОЛЬЦО ЗАВЕРШЕНИЯ ════ */
function VarRing() {
  const R = 78, C = 2 * Math.PI * R;
  return (
    <Screen>
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', alignItems: 'center', textAlign: 'center' }}>
        <div style={{ display: 'inline-flex', alignItems: 'center', gap: 6, background: ST.streakBadge, color: ST.accentDeep, borderRadius: 999, padding: '6px 13px', fontSize: 13, fontWeight: 700, marginBottom: 28 }}>
          <Flame size={15} /> {BOOK.streak} дней подряд
        </div>
        <div style={{ position: 'relative', width: 190, height: 190 }}>
          <svg width="190" height="190" style={{ transform: 'rotate(-90deg)' }}>
            <circle cx="95" cy="95" r={R} fill="none" stroke={ST.chip} strokeWidth="13" />
            <circle cx="95" cy="95" r={R} fill="none" stroke={ST.divider} strokeWidth="13" strokeLinecap="round" strokeDasharray={C} strokeDashoffset={C * (1 - BOOK.before / 100)} opacity="0.45" />
            <circle cx="95" cy="95" r={R} fill="none" stroke="url(#sg)" strokeWidth="13" strokeLinecap="round" strokeDasharray={C} strokeDashoffset={C * (1 - BOOK.after / 100)} />
            <defs><linearGradient id="sg" x1="0" y1="0" x2="1" y2="1"><stop offset="0" stopColor={ST.goalG1} /><stop offset="1" stopColor={ST.goalG2} /></linearGradient></defs>
          </svg>
          <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <div style={{ fontFamily: ST.serif, fontWeight: 600, fontSize: 40, color: ST.accentDeep }}>{BOOK.after}%</div>
            <div style={{ fontSize: 13, color: ST.sage, fontWeight: 600, marginTop: 2 }}>+{BOOK.after - BOOK.before}% сегодня</div>
          </div>
        </div>
        <div style={{ fontFamily: ST.serif, fontWeight: 600, fontSize: 20, marginTop: 28 }}>+{BOOK.delta} страницы за {BOOK.mins} мин</div>
        <div style={{ color: ST.text2, fontSize: 14, marginTop: 6 }}>«{BOOK.title}» · стр. {BOOK.page} из {BOOK.total}</div>
      </div>
      <Btn>Готово</Btn>
    </Screen>
  );
}

function App() {
  return (
    <DesignCanvas>
      <DCSection id="succ" title="Экран результатов обновления прогресса" subtitle="4 направления · состояние «серия продолжена» · Terracotta & Linen">
        <DCArtboard id="a" label="A · Обложка-герой" width={390} height={680}><VarCover /></DCArtboard>
        <DCArtboard id="b" label="B · Издательский" width={390} height={680}><VarEditorial /></DCArtboard>
        <DCArtboard id="c" label="C · Чек чтения" width={390} height={680}><VarReceipt /></DCArtboard>
        <DCArtboard id="d" label="D · Кольцо завершения" width={390} height={680}><VarRing /></DCArtboard>
      </DCSection>
    </DesignCanvas>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
