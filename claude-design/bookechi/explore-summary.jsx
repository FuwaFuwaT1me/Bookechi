// Bookechi — exploration: что показывать в шапке журнала вместо «за всё время»
const { useState: useSv } = React;
const SV = {
  canvas: '#F4ECE1', surface: '#FBF6EF', surfacePure: '#FFFFFF', stroke: '#E4D9CC', divider: '#D9CCBC',
  text: '#382A20', text2: '#8C7C6E', accent: '#BE5E3B', accentDeep: '#9E4A2C', accentSoft: '#E8C9B6',
  sage: '#7C8A6E', sageSoft: '#DDE3D2', chip: '#EBE2D6', cardTint: '#EFE0D2', goalG1: '#D98E63', goalG2: '#9E4A2C', on: '#FFF6EE',
};
const serif = "'Lora', Georgia, serif";

function Wrap({ label, sub, children }) {
  return (
    <div style={{ flex: 'none', width: 360 }}>
      <div style={{ fontSize: 13, fontWeight: 600, color: SV.text, marginBottom: 2 }}>{label}</div>
      <div style={{ fontSize: 12, color: SV.text2, marginBottom: 12, minHeight: 32 }}>{sub}</div>
      <div style={{ background: SV.canvas, borderRadius: 24, padding: '16px 18px 18px', boxShadow: '0 16px 40px -18px rgba(56,42,32,0.4)' }}>
        {/* mini header context */}
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, marginBottom: 14 }}>
          <div style={{ width: 34, height: 34, borderRadius: '50%', background: SV.chip, display: 'flex', alignItems: 'center', justifyContent: 'center' }}>
            <svg width="17" height="17" viewBox="0 0 24 24" fill="none" stroke={SV.text} strokeWidth="2" strokeLinecap="round" strokeLinejoin="round"><path d="M15 5l-7 7 7 7"/></svg>
          </div>
          <div>
            <div style={{ fontSize: 10, fontWeight: 600, letterSpacing: '0.06em', textTransform: 'uppercase', color: SV.text2 }}>Продуктивность</div>
            <div style={{ fontFamily: serif, fontSize: 18, fontWeight: 600 }}>Журнал чтения</div>
          </div>
        </div>
        {children}
        {/* faux list peek */}
        <div style={{ marginTop: 14, opacity: 0.5 }}>
          <div style={{ fontSize: 10.5, fontWeight: 700, letterSpacing: '0.06em', textTransform: 'uppercase', color: SV.text2, marginBottom: 8 }}>Сегодня</div>
          <div style={{ height: 56, borderRadius: 14, background: SV.surface, border: '1px solid ' + SV.stroke }}></div>
        </div>
      </div>
    </div>
  );
}
function Cell({ v, l, accent }) {
  return (
    <div style={{ flex: 1, textAlign: 'center' }}>
      <div style={{ fontFamily: serif, fontSize: 18, fontWeight: 700, color: accent ? SV.accentDeep : SV.text }}>{v}</div>
      <div style={{ fontSize: 10.5, color: SV.text2, fontWeight: 600, marginTop: 1 }}>{l}</div>
    </div>
  );
}
const Div = () => <div style={{ width: 1, background: SV.divider, margin: '3px 0' }}></div>;

/* A · период-сегменты + сводка под выбранный период (с дельтой к прошлому) */
function VarPeriods() {
  const [p, setP] = useSv('Неделя');
  // [сессии, страницы, время, дельта%, подпись дельты]
  const data = {
    'Неделя': ['12', '286', '5 ч 40 м', 18, 'к прошлой неделе'],
    'Месяц': ['41', '1 020', '21 ч', 9, 'к прошлому месяцу'],
    'Всё время': ['148', '8 480', '92 ч', null, null],
  };
  const d = data[p];
  return (
    <>
      <div style={{ display: 'flex', background: SV.chip, borderRadius: 999, padding: 4, marginBottom: 12 }}>
        {Object.keys(data).map(k => (
          <button key={k} onClick={() => setP(k)} style={{ flex: 1, border: 'none', cursor: 'pointer', borderRadius: 999, padding: '7px 0', fontSize: 12.5, fontWeight: 600, fontFamily: 'inherit',
            background: p === k ? SV.surfacePure : 'transparent', color: p === k ? SV.text : SV.text2, boxShadow: p === k ? '0 1px 4px rgba(56,42,32,0.12)' : 'none' }}>{k}</button>
        ))}
      </div>
      <div style={{ background: SV.cardTint, border: '1px solid ' + SV.stroke, borderRadius: 16, padding: '12px 6px 11px' }}>
        <div style={{ display: 'flex' }}>
          <Cell v={d[0]} l="сессии" /><Div /><Cell v={d[1]} l="страниц" accent /><Div /><Cell v={d[2]} l="времени" />
        </div>
        {d[3] != null ? (
          <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'center', gap: 6, marginTop: 10, paddingTop: 10, borderTop: '1px solid ' + SV.divider }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, background: SV.accentSoft, color: SV.accentDeep, borderRadius: 999, padding: '2px 9px', fontSize: 11.5, fontWeight: 700 }}>▲ +{d[3]}%</span>
            <span style={{ fontSize: 11.5, color: SV.text2 }}>{d[4]}</span>
          </div>
        ) : (
          <div style={{ textAlign: 'center', marginTop: 10, paddingTop: 10, borderTop: '1px solid ' + SV.divider, fontSize: 11.5, color: SV.text2 }}>
            148 книг · с марта 2024
          </div>
        )}
      </div>
    </>
  );
}

/* B · «эта неделя» с дельтой к прошлой */
function VarWeekDelta() {
  return (
    <div style={{ background: SV.cardTint, border: '1px solid ' + SV.stroke, borderRadius: 18, padding: '15px 16px' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 12 }}>
        <span style={{ fontSize: 12, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', color: SV.text2 }}>Эта неделя</span>
        <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3, fontSize: 11.5, fontWeight: 700, color: SV.sage }}>▲ +18% к прошлой</span>
      </div>
      <div style={{ display: 'flex' }}>
        <Cell v="286" l="страниц" accent /><Div /><Cell v="5 ч 40 м" l="времени" /><Div /><Cell v="6" l="дней подряд" />
      </div>
    </div>
  );
}

/* C · контекстная строка (без больших чисел) */
function VarContext() {
  return (
    <div style={{ display: 'flex', alignItems: 'center', gap: 12, background: SV.cardTint, border: '1px solid ' + SV.stroke, borderRadius: 16, padding: '13px 16px' }}>
      <div style={{ width: 40, height: 40, borderRadius: 12, background: SV.accentSoft, color: SV.accentDeep, display: 'flex', alignItems: 'center', justifyContent: 'center', flex: 'none' }}>
        <svg width="20" height="20" viewBox="0 0 24 24" fill="currentColor"><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>
      </div>
      <div style={{ flex: 1, fontSize: 13.5, lineHeight: 1.4 }}>
        <b>На этой неделе</b> вы прочли <b style={{ color: SV.accentDeep }}>286 страниц</b> за 12 сессий — лучший темп за месяц.
      </div>
    </div>
  );
}

/* D · мини-график недели + итог */
function VarChart() {
  const days = [[ 'Пн', 0.5 ], ['Вт', 0.9], ['Ср', 0.3], ['Чт', 0.7], ['Пт', 1.0], ['Сб', 0.6], ['Вс', 0.15]];
  return (
    <div style={{ background: SV.cardTint, border: '1px solid ' + SV.stroke, borderRadius: 18, padding: '14px 16px' }}>
      <div style={{ display: 'flex', alignItems: 'baseline', justifyContent: 'space-between', marginBottom: 10 }}>
        <span style={{ fontSize: 12, fontWeight: 700, letterSpacing: '0.04em', textTransform: 'uppercase', color: SV.text2 }}>Эта неделя</span>
        <span style={{ fontSize: 13 }}><b style={{ fontFamily: serif, fontSize: 16 }}>286</b> <span style={{ color: SV.text2 }}>стр · 5 ч 40 м</span></span>
      </div>
      <div style={{ display: 'flex', alignItems: 'flex-end', gap: 7, height: 46 }}>
        {days.map(([d, h], i) => (
          <div key={d} style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 5 }}>
            <div style={{ width: '100%', maxWidth: 20, height: Math.max(5, h * 38), borderRadius: '4px 4px 2px 2px', background: i === 4 ? SV.accentDeep : SV.goalG1, opacity: i === 6 ? 0.4 : 1 }}></div>
            <span style={{ fontSize: 9, color: SV.text2 }}>{d}</span>
          </div>
        ))}
      </div>
    </div>
  );
}

/* E · совсем убрать сводку — только фильтр-чипы */
function VarFilterOnly() {
  const [p, setP] = useSv('Все книги');
  const opts = ['Все книги', 'Эта неделя', 'Этот месяц'];
  return (
    <div>
      <div style={{ display: 'flex', gap: 8, overflowX: 'auto' }}>
        {opts.map(k => (
          <button key={k} onClick={() => setP(k)} style={{ flex: 'none', border: '1.5px solid ' + (p === k ? SV.accent : SV.stroke), cursor: 'pointer', borderRadius: 999, padding: '8px 15px', fontSize: 13, fontWeight: 600, fontFamily: 'inherit',
            background: p === k ? 'color-mix(in oklab,' + SV.accentSoft + ' 45%, ' + SV.surface + ')' : SV.surface, color: p === k ? SV.accentDeep : SV.text2 }}>{k}</button>
        ))}
      </div>
      <div style={{ fontSize: 11.5, color: SV.text2, marginTop: 10 }}>Сводка — на экране «Продуктивность», здесь только история и фильтр.</div>
    </div>
  );
}

function App() {
  return (
    <div style={{ minHeight: '100vh', padding: '34px 36px', fontFamily: "'Inter', system-ui, sans-serif", background: '#E7DCCC' }}>
      <h1 style={{ fontFamily: serif, fontWeight: 600, fontSize: 26, margin: '0 0 4px', color: SV.text }}>Шапка журнала — чем заменить «за всё время»</h1>
      <p style={{ color: SV.text2, fontSize: 14, margin: '0 0 26px' }}>Проблема: общая сводка за всё время оторвана от ленты последних сессий. Палитра Terracotta &amp; Linen</p>
      <div style={{ display: 'flex', gap: 26, flexWrap: 'wrap' }}>
        <Wrap label="A · Период-сегменты + сводка" sub="переключатель Неделя / Месяц / Всё; цифры под выбранное (твоя идея фильтра)"><VarPeriods /></Wrap>
        <Wrap label="B · «Эта неделя» + дельта" sub="фокус на текущей неделе, прогресс к прошлой">{<VarWeekDelta />}</Wrap>
        <Wrap label="C · Контекстная фраза" sub="живой текст вместо сухих чисел">{<VarContext />}</Wrap>
        <Wrap label="D · Мини-график недели" sub="ритм по дням + итог недели">{<VarChart />}</Wrap>
        <Wrap label="E · Только фильтр (без сводки)" sub="сводка остаётся в «Продуктивности»">{<VarFilterOnly />}</Wrap>
      </div>
    </div>
  );
}
ReactDOM.createRoot(document.getElementById('root')).render(<App />);
