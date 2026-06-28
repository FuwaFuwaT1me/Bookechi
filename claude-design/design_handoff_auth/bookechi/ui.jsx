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
    bell: <><path d="M6 9.5a6 6 0 1 1 12 0c0 4.6 1.8 5.8 1.8 5.8H4.2S6 14.1 6 9.5z"/><path d="M10 19.5a2.2 2.2 0 0 0 4 0"/></>,
    sun: <><circle cx="12" cy="12" r="4.5"/><path d="M12 2.5v2.5M12 19v2.5M2.5 12H5M19 12h2.5M4.9 4.9l1.8 1.8M17.3 17.3l1.8 1.8M19.1 4.9l-1.8 1.8M6.7 17.3l-1.8 1.8"/></>,
    moon: <path d="M19.5 14A8 8 0 0 1 10 4.5 8 8 0 1 0 19.5 14z"/>,
    arrowDown: <><path d="M12 4v14"/><path d="M6 13l6 6 6-6"/></>,
    camera: <><rect x="3" y="7" width="18" height="13" rx="3"/><circle cx="12" cy="13" r="3.5"/><path d="M8.5 7l1.5-2.5h4L15.5 7"/></>,
    star: <path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z"/>,
    pencil: <><path d="M4 20l1-4L16.5 4.5a2.1 2.1 0 0 1 3 3L8 19l-4 1z"/></>,
    clock: <><circle cx="12" cy="12" r="8.5"/><path d="M12 7.5V12l3 2"/></>,
    help: <><circle cx="12" cy="12" r="9.5"/><path d="M9.2 9.3a2.8 2.8 0 0 1 5.4 1c0 1.9-2.8 2.5-2.8 2.5"/><circle cx="12" cy="17.2" r="0.55" fill="currentColor"/></>,
    pin: <path d="M12 2c-3.9 0-7 3-7 6.9 0 4.7 5.7 10.6 6.3 11.2.4.4 1 .4 1.4 0 .6-.6 6.3-6.5 6.3-11.2C19 5 15.9 2 12 2zm0 9.4a2.4 2.4 0 1 1 0-4.8 2.4 2.4 0 0 1 0 4.8z" fill="currentColor" stroke="none"/>,
    close: <><path d="M6 6l12 12M18 6L6 18"/></>,
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
function StreakBlock({ streak, todayMarked, comeback = false, onBell }) {
  const { WEEKDAYS_RU, TODAY } = BK_DATA;
  const todayIdx = (TODAY.getDay() + 6) % 7; // Mon=0 → июнь 9 = Вт = 1
  const days = WEEKDAYS_RU.map((label, i) => {
    const filled = !comeback && streak > 0 && (i < todayIdx || (i === todayIdx && todayMarked));
    return { label, filled, isToday: i === todayIdx };
  });
  const title = comeback
    ? 'С возвращением'
    : streak === 0 ? 'Начните серию сегодня' : streak + ' ' + plural(streak, 'день', 'дня', 'дней') + ' подряд';
  const caption = comeback
    ? 'Серия на паузе. Пара страниц — и она начнётся заново'
    : todayMarked ? 'Сегодня уже отмечено' : 'Отметьте чтение, чтобы продолжить серию';
  const flameScale = 1 + Math.min(streak, 12) * 0.018;
  return (
    <div className="bk-streak" data-comment-anchor="streak-block">
      <div className="bk-row" style={{ gap: 10, marginBottom: 14 }}>
        <div style={{
          width: 44, height: 44, borderRadius: 16, background: 'var(--streak-badge)',
          display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--flame)', flex: 'none',
          opacity: comeback ? 0.65 : 1,
        }}>
          <svg width="24" height="24" viewBox="0 0 24 24" fill="currentColor" style={{ transform: 'scale(' + flameScale + ')' }}><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>
        </div>
        <div className="bk-grow">
          <div style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 19, lineHeight: 1.2 }}>{title}</div>
          <div className="bk-caption">{caption}</div>
        </div>
        {onBell && (
          <button className="bk-iconbtn" aria-label="Напоминание о чтении" onClick={onBell}
            style={{ width: 36, height: 36, alignSelf: 'flex-start', background: 'color-mix(in oklab, var(--surface) 60%, transparent)' }}>
            <BkIcon name="bell" size={17} />
          </button>
        )}
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

/* ── GoalBar ── */
function GoalRemaining({ goal }) {
  const left = Math.max(0, goal.value - goal.done);
  if (left === 0) return <span style={{ color: 'var(--sage)', fontWeight: 600 }}>Цель выполнена — отличная неделя!</span>;
  return <span>Осталось {left.toLocaleString('ru-RU')} стр. — примерно один вечер</span>;
}

function GoalBar({ goal, onClick, variant = 'Полоса' }) {
  if (!goal || goal.type !== 'pages') return null; // цель «дни» живёт в стрик-карточке
  const pct = Math.min(100, (goal.done / goal.value) * 100);
  const Tag = onClick ? 'button' : 'div';
  const base = { display: 'block', width: '100%', textAlign: 'left' };

  if (variant === 'Минимум') {
    return (
      <Tag className="bk-card" data-comment-anchor="goal-bar" onClick={onClick}
        style={{ ...base, padding: '14px 16px', boxShadow: 'none' }}>
        <div className="bk-row" style={{ gap: 10 }}>
          <span className="bk-caption bk-grow" style={{ fontWeight: 600 }}>На этой неделе</span>
          <span style={{ fontSize: 14, fontWeight: 600, whiteSpace: 'nowrap', flex: 'none' }}>
            {goal.done.toLocaleString('ru-RU')} / {goal.value.toLocaleString('ru-RU')} стр.
          </span>
        </div>
        <div style={{ marginTop: 10 }}><ProgressBar pct={pct} /></div>
      </Tag>
    );
  }

  if (variant === 'Кольцо') {
    const R = 24, C = 2 * Math.PI * R;
    return (
      <Tag className="bk-card" data-comment-anchor="goal-bar" onClick={onClick}
        style={{ ...base, padding: '14px 16px' }}>
        <div className="bk-row" style={{ gap: 14 }}>
          <svg width="60" height="60" viewBox="0 0 60 60" style={{ flex: 'none', transform: 'rotate(-90deg)' }}>
            <circle cx="30" cy="30" r={R} fill="none" stroke="var(--chip-bg)" strokeWidth="6"></circle>
            <circle cx="30" cy="30" r={R} fill="none" stroke="url(#bk-goal-grad)" strokeWidth="6" strokeLinecap="round"
              strokeDasharray={C} strokeDashoffset={C * (1 - pct / 100)}
              style={{ transition: 'stroke-dashoffset 0.6s cubic-bezier(.3,.8,.3,1)' }}></circle>
            <defs>
              <linearGradient id="bk-goal-grad" x1="0" y1="0" x2="1" y2="1">
                <stop offset="0" stopColor="var(--goal-g1)"></stop>
                <stop offset="1" stopColor="var(--goal-g2)"></stop>
              </linearGradient>
            </defs>
          </svg>
          <div className="bk-grow">
            <div className="bk-label" style={{ fontSize: 10.5 }}>На этой неделе</div>
            <div style={{ marginTop: 3 }}>
              <span style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 24 }}>{goal.done.toLocaleString('ru-RU')}</span>
              <span className="bk-caption"> / {goal.value.toLocaleString('ru-RU')} стр.</span>
            </div>
            <div className="bk-caption" style={{ marginTop: 3, fontSize: 12 }}><GoalRemaining goal={goal} /></div>
          </div>
          <span className="bk-goal-chip" style={{ flex: 'none' }}>{Math.round(pct)}%</span>
        </div>
      </Tag>
    );
  }

  // «Полоса» — градиентный бар со свечением и точкой-наконечником
  return (
    <Tag className="bk-card" data-comment-anchor="goal-bar" onClick={onClick}
      style={{ ...base, padding: '16px 18px 15px' }}>
      <div className="bk-row" style={{ gap: 10 }}>
        <span className="bk-label bk-grow" style={{ fontSize: 10.5 }}>На этой неделе</span>
        <span className="bk-goal-chip">{Math.round(pct)}%</span>
      </div>
      <div style={{ marginTop: 6 }}>
        <span style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 27, lineHeight: 1 }}>{goal.done.toLocaleString('ru-RU')}</span>
        <span className="bk-caption"> / {goal.value.toLocaleString('ru-RU')} стр.</span>
      </div>
      <div className="bk-goal-track" style={{ marginTop: 12 }}>
        <div className="bk-goal-fill" style={{ width: pct + '%' }}>
          {pct > 4 && pct < 100 && <span className="bk-goal-dot"></span>}
        </div>
      </div>
      <div className="bk-caption" style={{ marginTop: 9, fontSize: 12 }}><GoalRemaining goal={goal} /></div>
    </Tag>
  );
}

/* ── SessionBars (last 10 days) ── */
function SessionBars({ book }) {
  const data = BK_DATA.sessionsFor(book);
  const max = Math.max(...data, 1);
  const total = data.reduce((s, v) => s + v, 0);
  return (
    <div>
      <div className="bk-bars">
        {data.map((v, i) => (
          <div key={i}
            className={(v === 0 ? 'zero' : '') + (i === data.length - 1 && v > 0 ? ' last' : '')}
            style={{ height: Math.max(7, (v / max) * 100) + '%' }}
            title={v + ' стр.'}></div>
        ))}
      </div>
      <div className="bk-row" style={{ justifyContent: 'space-between', marginTop: 8 }}>
        <span className="bk-caption" style={{ fontSize: 11.5 }}>Последние 10 дней</span>
        <span className="bk-caption" style={{ fontSize: 11.5 }}>{total} стр. · {fmtDuration(BK_DATA.minutesFor(total), { short: true })}</span>
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
  const ICONS = {
    home: <path d="M12 3.1c-.4 0-.8.15-1.1.4l-7.5 6.4c-.5.4-.7 1-.5 1.6.2.6.7 1 1.4 1h.5v5.9c0 1 .8 1.8 1.8 1.8h2.5v-4.6c0-.8.6-1.4 1.4-1.4h3c.8 0 1.4.6 1.4 1.4v4.6h2.5c1 0 1.8-.8 1.8-1.8v-5.9h.5c.7 0 1.2-.4 1.4-1 .2-.6 0-1.2-.5-1.6l-7.5-6.4c-.3-.25-.7-.4-1.1-.4Z" />,
    stats: <><rect x="3.8" y="12.6" width="4.6" height="7.6" rx="2.3" /><rect x="9.7" y="3.8" width="4.6" height="16.4" rx="2.3" /><rect x="15.6" y="8.8" width="4.6" height="11.4" rx="2.3" /></>,
    library: <><path d="M11.2 6.6C9.4 5.1 6.9 4.3 4.3 4.3c-.5 0-1 .2-1.35.55-.35.35-.55.8-.55 1.3v9.9c0 1 .8 1.75 1.8 1.75 2.4 0 4.55.5 6.3 1.7.25.18.55-.02.55-.32V7.4c0-.3-.13-.58-.35-.78Z" /><path d="M12.8 6.6c1.8-1.5 4.3-2.3 6.9-2.3.5 0 1 .2 1.35.55.35.35.55.8.55 1.3v9.9c0 1-.8 1.75-1.8 1.75-2.4 0-4.55.5-6.3 1.7-.25.18-.55-.02-.55-.32V7.4c0-.3.13-.58.35-.78Z" /></>,
  };
  const items = [
    { id: 'home', label: 'Сегодня' },
    { id: 'stats', label: 'Статистика' },
    { id: 'library', label: 'Библиотека' },
  ];
  return (
    <nav className="bk-nav" data-comment-anchor="bottom-nav">
      {items.map((it) => {
        const active = tab === it.id;
        return (
          <button key={it.id} className={'bk-nav-item' + (active ? ' active' : '')} onClick={() => onTab(it.id)} aria-label={it.label}>
            <span className="bk-nav-ic">
              <svg width="26" height="26" viewBox="0 0 24 24" fill="currentColor" stroke="none" style={{ opacity: active ? 1 : 0.88 }}>
                {ICONS[it.id]}
              </svg>
            </span>
          </button>
        );
      })}
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

/* ── duration formatting ── */
function fmtDuration(min, opts = {}) {
  if (!min || min <= 0) return '0 мин';
  const h = Math.floor(min / 60), m = min % 60;
  if (opts.short) {
    if (h && m) return h + ' ч ' + m + ' м';
    return h ? h + ' ч' : m + ' мин';
  }
  if (h && m) return h + ' ч ' + m + ' мин';
  return h ? h + ' ' + plural(h, 'час', 'часа', 'часов') : m + ' мин';
}

/* ── DurationChips — tasteful optional time picker for the progress screen ── */
const BK_DURATIONS = [15, 30, 45, 60, 90];
function DurationChips({ value, onChange }) {
  return (
    <div>
      <div className="bk-row" style={{ gap: 7, marginBottom: 11 }}>
        <BkIcon name="clock" size={16} style={{ color: 'var(--accent-deep)', flex: 'none' }} />
        <span style={{ fontSize: 13, fontWeight: 600, whiteSpace: 'nowrap', flex: 'none' }}>Сколько читали?</span>
        <span className="bk-caption" style={{ fontSize: 12, whiteSpace: 'nowrap' }}>— необязательно</span>
      </div>
      <div style={{ display: 'flex', gap: 8, flexWrap: 'wrap' }}>
        {BK_DURATIONS.map((m) => {
          const active = value === m;
          return (
            <button key={m} className={'bk-timechip' + (active ? ' active' : '')}
              aria-pressed={active} onClick={() => onChange(active ? null : m)}>
              {fmtDuration(m, { short: true })}
            </button>
          );
        })}
      </div>
    </div>
  );
}

/* ── TimeStatCard — surfaces reading-time on the stats screen ── */
function TimeStatCard({ period }) {
  const D = BK_DATA;
  const isMonth = period === 'Месяц';
  const ms = D.monthSummary();
  const ys = D.yearSummary();
  const totalMin = isMonth ? ms.totalMin : ys.totalMin;
  const week = D.weekMinutes();
  const maxW = Math.max(...week, 1);
  return (
    <section className="bk-card" data-comment-anchor="time-card" style={{ padding: 18 }}>
      <div className="bk-row" style={{ gap: 12, marginBottom: 16 }}>
        <div style={{
          width: 42, height: 42, borderRadius: 14, flex: 'none',
          background: 'var(--accent-soft)', color: 'var(--accent-deep)',
          display: 'flex', alignItems: 'center', justifyContent: 'center',
        }}>
          <BkIcon name="clock" size={22} />
        </div>
        <div className="bk-grow">
          <div className="bk-label" style={{ fontSize: 10.5 }}>Время за чтением{isMonth ? ' · месяц' : ' · год'}</div>
          <div style={{ marginTop: 2 }}>
            <span style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 26, lineHeight: 1 }}>{fmtDuration(totalMin)}</span>
          </div>
        </div>
      </div>
      <div className="bk-row" style={{ alignItems: 'flex-end', gap: 6, height: 44, marginBottom: 8 }}>
        {week.map((m, i) => (
          <div key={i} className="bk-grow" style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'flex-end', height: '100%' }}>
            <div style={{
              width: '100%', maxWidth: 22, borderRadius: '4px 4px 2px 2px',
              height: Math.max(4, (m / maxW) * 100) + '%',
              background: i === week.length - 1 ? 'var(--accent-deep)' : 'var(--heat2)',
            }} title={fmtDuration(m, { short: true })}></div>
          </div>
        ))}
      </div>
      <div className="bk-row" style={{ justifyContent: 'space-between' }}>
        <span className="bk-caption" style={{ fontSize: 11.5 }}>Последние 7 дней</span>
        {isMonth && ms.sessions > 0 && (
          <span className="bk-caption" style={{ fontSize: 11.5 }}>~{fmtDuration(ms.avgSession, { short: true })} за вечер</span>
        )}
      </div>
    </section>
  );
}

/* ── RatingStars — interactive (editable) or read-only star rating ── */
function RatingStars({ value = 0, onChange, size = 22, gap = 6, readOnly = false }) {
  const [hover, setHover] = React.useState(0);
  const shown = hover || value;
  return (
    <div className="bk-row" style={{ gap }} role={readOnly ? undefined : 'radiogroup'} aria-label="Оценка">
      {[1, 2, 3, 4, 5].map((s) => {
        const filled = s <= shown;
        const star = (
          <svg width={size} height={size} viewBox="0 0 24 24"
            fill={filled ? 'var(--accent)' : 'none'}
            stroke={filled ? 'var(--accent)' : 'var(--divider)'}
            strokeWidth="1.6" strokeLinejoin="round"
            style={{ filter: filled ? 'drop-shadow(0 1px 1.5px rgba(190,94,59,0.28))' : 'none' }}>
            <path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z" />
          </svg>
        );
        if (readOnly) return <span key={s} style={{ display: 'flex' }}>{star}</span>;
        return (
          <button key={s} type="button" role="radio" aria-checked={value === s} aria-label={s + ' из 5'}
            onClick={(e) => { e.stopPropagation(); onChange && onChange(value === s ? 0 : s); }}
            onMouseEnter={() => setHover(s)} onMouseLeave={() => setHover(0)}
            style={{ background: 'none', border: 'none', padding: 2, cursor: 'pointer', display: 'flex', transition: 'transform 0.12s ease', transform: hover === s ? 'scale(1.16)' : 'none' }}>
            {star}
          </button>
        );
      })}
    </div>
  );
}

Object.assign(window, {
  BkIcon, BookCover, StatusChip, FavoriteToggle, ProgressBar, StreakBlock,
  MetricCard, PeriodSwitcher, EmptyState, BottomNav, WarmTextField, plural, STATUS_LABEL,
  GoalBar, SessionBars, fmtDuration, DurationChips, TimeStatCard, RatingStars,
});
