// Bookechi — reusable UI components (Terracotta & Linen DS)
const { useState } = React;

/* ── Icons (simple line set, 24px grid) ── */
function BkIcon({ name, size = 22, stroke = 1.8, style }) {
  const P = {
    feather: <><path d="M20 4c-6 0-12 4-14 11l-2 5 5-2c7-2 11-8 11-14z"/><path d="M16 8L6 18"/></>,
    chart: <><path d="M5 20V12"/><path d="M12 20V5"/><path d="M19 20v-5"/></>,
    book: <><path d="M12 6c-1.5-1.6-4-2.5-8-2.5v14c4 0 6.5.9 8 2.5 1.5-1.6 4-2.5 8-2.5v-14c-4 0-6.5.9-8 2.5z"/><path d="M12 6v14"/></>,
    plus: <><path d="M12 5v14"/><path d="M5 12h14"/></>,
    heart: <path d="M12 20s-7-4.3-9-9c-1.2-3 .8-6.5 4-6.5 2.2 0 4 1.5 5 3 1-1.5 2.8-3 5-3 3.2 0 5.2 3.5 4 6.5-2 4.7-9 9-9 9z"/>,
    flame: <path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/>,
    back: <><path d="M15 5l-7 7 7 7"/></>,
    check: <path d="M5 13l4.5 4.5L19 7"/>,
    trash: <><path d="M5 7h14"/><path d="M9 7V5h6v2"/><path d="M7 7l1 13h8l1-13"/></>,
    dots: <><circle cx="12" cy="5.5" r="1" fill="currentColor"/><circle cx="12" cy="12" r="1" fill="currentColor"/><circle cx="12" cy="18.5" r="1" fill="currentColor"/></>,
    sun: <><circle cx="12" cy="12" r="4.5"/><path d="M12 2.5v2.5M12 19v2.5M2.5 12H5M19 12h2.5M4.9 4.9l1.8 1.8M17.3 17.3l1.8 1.8M19.1 4.9l-1.8 1.8M6.7 17.3l-1.8 1.8"/></>,
    moon: <path d="M19.5 14A8 8 0 0 1 10 4.5 8 8 0 1 0 19.5 14z"/>,
    arrowDown: <><path d="M12 4v14"/><path d="M6 13l6 6 6-6"/></>,
    camera: <><rect x="3" y="7" width="18" height="13" rx="3"/><circle cx="12" cy="13" r="3.5"/><path d="M8.5 7l1.5-2.5h4L15.5 7"/></>,
    star: <path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z"/>,
    pencil: <><path d="M4 20l1-4L16.5 4.5a2.1 2.1 0 0 1 3 3L8 19l-4 1z"/></>,
  };
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor"
      strokeWidth={stroke} strokeLinecap="round" strokeLinejoin="round" style={style}>
      {P[name] || null}
    </svg>
  );
}

/* ── BookCover ── */
function BookCover({ book, width = 64, radius = 12, placeholder = false }) {
  const h = Math.round(width * 1.45);
  if (placeholder || !book) {
    return (
      <div style={{
        width, height: h, borderRadius: radius, flex: 'none',
        border: '1.6px dashed var(--divider)', background: 'var(--chip-bg)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        color: 'var(--text2)',
      }}>
        <BkIcon name="book" size={Math.min(26, width * 0.36)} />
      </div>
    );
  }
  const big = width >= 80;
  return (
    <div style={{
      width, height: h, borderRadius: radius, flex: 'none',
      background: `linear-gradient(160deg, ${book.cover.bg}, color-mix(in oklab, ${book.cover.bg} 78%, black))`,
      color: book.cover.fg, overflow: 'hidden', position: 'relative',
      padding: big ? '12px 10px' : '8px 7px',
      boxShadow: 'inset -3px 0 6px -2px rgba(0,0,0,0.25), 0 4px 14px -4px rgba(56,42,32,0.35)',
      display: 'flex', flexDirection: 'column', justifyContent: 'space-between',
    }}>
      <div style={{ position: 'absolute', left: width * 0.085, top: 0, bottom: 0, width: 1.5, background: 'rgba(255,255,255,0.22)' }}></div>
      <div style={{
        fontFamily: 'var(--serif)', fontWeight: 600, lineHeight: 1.18,
        fontSize: big ? 12.5 : 9.5, paddingLeft: width * 0.1,
        display: '-webkit-box', WebkitLineClamp: 4, WebkitBoxOrient: 'vertical', overflow: 'hidden',
        hyphens: 'auto', overflowWrap: 'anywhere',
      }}>{book.title}</div>
      <div style={{ fontSize: big ? 9 : 7, opacity: 0.85, paddingLeft: width * 0.1, letterSpacing: '0.04em', textTransform: 'uppercase', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{book.author}</div>
    </div>
  );
}

/* ── StatusChip ── */
const STATUS_LABEL = { reading: 'Читаю', planned: 'В планах', finished: 'Прочитано' };
function StatusChip({ status }) {
  return <span className={'bk-status ' + status}>{STATUS_LABEL[status]}</span>;
}

/* ── FavoriteToggle ── */
function FavoriteToggle({ value, onChange, size = 22 }) {
  return (
    <button aria-label="Любимое" onClick={(e) => { e.stopPropagation(); onChange && onChange(!value); }}
      style={{ color: value ? 'var(--accent)' : 'var(--text2)', display: 'flex', padding: 6, margin: -6 }}>
      <svg width={size} height={size} viewBox="0 0 24 24"
        fill={value ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="1.8" strokeLinejoin="round">
        <path d="M12 20s-7-4.3-9-9c-1.2-3 .8-6.5 4-6.5 2.2 0 4 1.5 5 3 1-1.5 2.8-3 5-3 3.2 0 5.2 3.5 4 6.5-2 4.7-9 9-9 9z"/>
      </svg>
    </button>
  );
}

/* ── ProgressBar ── */
function ProgressBar({ pct, showPct = false }) {
  return (
    <div className="bk-row" style={{ gap: 10, width: '100%' }}>
      <div className="bk-progress bk-grow"><div style={{ width: Math.min(100, pct) + '%' }}></div></div>
      {showPct && <span style={{ fontSize: 13, fontWeight: 600, color: 'var(--accent-deep)', flex: 'none' }}>{Math.round(pct)}%</span>}
    </div>
  );
}

/* ── StreakStrip (7-day strip + flame headline) ── */
function StreakBlock({ streak, todayMarked }) {
  const { WEEKDAYS_RU, TODAY } = BK_DATA;
  const todayIdx = (TODAY.getDay() + 6) % 7; // Mon=0 → июнь 9 = Вт = 1
  const days = WEEKDAYS_RU.map((label, i) => {
    const filled = streak > 0 && (i < todayIdx || (i === todayIdx && todayMarked));
    return { label, filled, isToday: i === todayIdx };
  });
  return (
    <div className="bk-streak" data-comment-anchor="streak-block">
      <div className="bk-row" style={{ gap: 10, marginBottom: 14 }}>
        <div style={{
          width: 44, height: 44, borderRadius: 16, background: 'var(--streak-badge)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--flame)', flex: 'none',
        }}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor"><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>
        </div>
        <div className="bk-grow">
          <div style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 19, lineHeight: 1.2 }}>
            {streak === 0 ? 'Начните серию сегодня' : streak + ' ' + plural(streak, 'день', 'дня', 'дней') + ' подряд'}
          </div>
          <div className="bk-caption">{todayMarked ? 'Сегодня уже отмечено' : 'Отметьте чтение, чтобы продолжить серию'}</div>
        </div>
      </div>
      <div className="bk-row" style={{ justifyContent: 'space-between' }}>
        {days.map((d) => (
          <div key={d.label} className="bk-streak-day" style={d.isToday ? { color: 'var(--accent-deep)' } : null}>
            <span>{d.label}</span>
            <div className={'bk-streak-dot' + (d.filled ? ' filled' : '') + (d.isToday ? ' today' : '')}>
              {d.filled && <BkIcon name="check" size={13} stroke={2.6} style={{ color: 'var(--on-accent)' }} />}
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

/* ── MetricCard ── */
function MetricCard({ value, label, empty = false }) {
  return (
    <div className="bk-card" style={{ padding: '16px 16px 14px', boxShadow: 'none' }}>
      <div style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 27, lineHeight: 1.1, color: empty ? 'var(--text2)' : 'var(--text)' }}>
        {empty ? '—' : value}
      </div>
      <div className="bk-caption" style={{ marginTop: 4 }}>{label}</div>
    </div>
  );
}

/* ── PeriodSwitcher ── */
function PeriodSwitcher({ value, onChange }) {
  return (
    <div className="bk-seg" role="tablist">
      {['Месяц', 'Год'].map((p) => (
        <button key={p} className={value === p ? 'active' : ''} onClick={() => onChange(p)}>{p}</button>
      ))}
    </div>
  );
}

/* ── EmptyState ── */
function EmptyState({ title, text, cta, onCta, icon = 'book' }) {
  return (
    <div style={{ textAlign: 'center', padding: '36px 24px', display: 'flex', flexDirection: 'column', alignItems: 'center', gap: 8 }}>
      <div style={{
        width: 92, height: 92, borderRadius: 32, marginBottom: 8,
        background: 'var(--addblock)', color: 'var(--accent-deep)',
        display: 'flex', alignItems: 'center', justifyContent: 'center',
        border: '1.6px dashed var(--divider)',
      }}>
        <BkIcon name={icon} size={40} stroke={1.5} />
      </div>
      <div className="bk-title">{title}</div>
      <div className="bk-caption" style={{ maxWidth: 250, textWrap: 'pretty' }}>{text}</div>
      {cta && (
        <button className="bk-btn bk-btn-primary" style={{ width: 'auto', minWidth: 200, marginTop: 14 }} onClick={onCta}>{cta}</button>
      )}
    </div>
  );
}

/* ── BottomNav ── */
function BottomNav({ tab, onTab }) {
  const items = [
    { id: 'home', label: 'Активность', icon: 'feather' },
    { id: 'stats', label: 'Продуктивность', icon: 'chart' },
    { id: 'library', label: 'Библиотека', icon: 'book' },
  ];
  return (
    <nav className="bk-nav" data-comment-anchor="bottom-nav">
      {items.map((it) => (
        <button key={it.id} className={'bk-nav-item' + (tab === it.id ? ' active' : '')} onClick={() => onTab(it.id)}>
          <BkIcon name={it.icon} size={23} stroke={tab === it.id ? 2 : 1.7} />
          <span>{it.label}</span>
        </button>
      ))}
    </nav>
  );
}

/* ── WarmTextField ── */
function WarmTextField({ label, value, onChange, placeholder, type = 'text', error, inputMode }) {
  return (
    <div className="bk-field">
      <label>{label}</label>
      <input
        className={'bk-input' + (error ? ' error' : '')}
        type={type} inputMode={inputMode}
        value={value} placeholder={placeholder}
        onChange={(e) => onChange(e.target.value)}
      />
      {error && <span className="bk-err">{error}</span>}
    </div>
  );
}

/* ── plural helper ── */
function plural(n, one, few, many) {
  const m10 = n % 10, m100 = n % 100;
  if (m10 === 1 && m100 !== 11) return one;
  if (m10 >= 2 && m10 <= 4 && (m100 < 12 || m100 > 14)) return few;
  return many;
}

Object.assign(window, {
  BkIcon, BookCover, StatusChip, FavoriteToggle, ProgressBar, StreakBlock,
  MetricCard, PeriodSwitcher, EmptyState, BottomNav, WarmTextField, plural, STATUS_LABEL,
});
