// Bookechi — Журнал чтения (push screen + edit sheet + delete dialog)
const { useState: useJState, useEffect: useJEffect } = React;

/* period summary header with segments + delta */
function JournalSummary({ books }) {
  const [period, setPeriod] = useJState('Неделя');
  const ps = BK_DATA.periodSummary(books);
  const d = ps[period];
  const fmtT = (m) => (m >= 60 ? Math.floor(m / 60) + ' ч' + (m % 60 ? ' ' + (m % 60) + ' м' : '') : m + ' мин');
  const cell = (v, l, accent) => (
    <div style={{ flex: 1, textAlign: 'center' }}>
      <div style={{ fontFamily: 'var(--serif)', fontSize: 18, fontWeight: 700, color: accent ? 'var(--accent-deep)' : 'var(--text)' }}>{v}</div>
      <div style={{ fontSize: 10.5, color: 'var(--text2)', fontWeight: 600, marginTop: 1 }}>{l}</div>
    </div>
  );
  const sep = <div style={{ width: 1, background: 'var(--divider)', margin: '3px 0' }}></div>;
  return (
    <div style={{ margin: '0 0 6px' }}>
      <div className="bk-seg" style={{ marginBottom: 10 }}>
        {['Неделя', 'Месяц', 'Всё время'].map((k) => (
          <button key={k} className={period === k ? 'active' : ''} onClick={() => setPeriod(k)}>{k}</button>
        ))}
      </div>
      <div className="bk-card" style={{ padding: '12px 6px 11px', background: 'var(--card-tint)', boxShadow: 'none' }}>
        <div className="bk-row">{cell(d.sessions, 'сессии')}{sep}{cell(d.pages.toLocaleString('ru-RU'), 'страниц', true)}{sep}{cell(fmtT(d.mins), 'времени')}</div>
        {d.delta != null ? (
          <div className="bk-row" style={{ justifyContent: 'center', gap: 6, marginTop: 10, paddingTop: 10, borderTop: '1px solid var(--divider)' }}>
            <span style={{ display: 'inline-flex', alignItems: 'center', gap: 4, background: 'var(--accent-soft)', color: 'var(--accent-deep)', borderRadius: 999, padding: '2px 9px', fontSize: 11.5, fontWeight: 700 }}>▲ +{d.delta}%</span>
            <span style={{ fontSize: 11.5, color: 'var(--text2)' }}>{d.deltaLabel}</span>
          </div>
        ) : (
          <div style={{ textAlign: 'center', marginTop: 10, paddingTop: 10, borderTop: '1px solid var(--divider)', fontSize: 11.5, color: 'var(--text2)' }}>{d.deltaLabel}</div>
        )}
      </div>
    </div>
  );
}

function CurrentTag() {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 3, background: 'var(--accent-soft)', color: 'var(--accent-deep)', borderRadius: 999, padding: '1px 8px 1px 6px', fontSize: 10, fontWeight: 700, letterSpacing: '0.03em', textTransform: 'uppercase', flex: 'none' }}>
      <svg width="11" height="11" viewBox="0 0 24 24" fill="currentColor"><path d="M12 2c-3.9 0-7 3-7 6.9 0 4.7 5.7 10.6 6.3 11.2.4.4 1 .4 1.4 0 .6-.6 6.3-6.5 6.3-11.2C19 5 15.9 2 12 2zm0 9.4a2.4 2.4 0 1 1 0-4.8 2.4 2.4 0 0 1 0 4.8z"/></svg>
      текущая
    </span>
  );
}

function MetaChip({ icon, children }) {
  return (
    <span style={{ display: 'inline-flex', alignItems: 'center', gap: 5, background: 'var(--chip-bg)', borderRadius: 8, padding: '3px 9px', fontSize: 12, fontWeight: 600, color: 'var(--text)' }}>
      <BkIcon name={icon} size={13} stroke={1.9} style={{ color: 'var(--text)' }} />{children}
    </span>
  );
}

function SessionRow({ s, app, swiped, onSwipe, showCover = true }) {
  return (
    <div style={{ position: 'relative' }}>
      {swiped && (
        <div style={{ position: 'absolute', inset: 0, display: 'flex', alignItems: 'center', justifyContent: 'flex-end' }}>
          <button onClick={() => app.askDeleteSession(s)} aria-label="Удалить"
            style={{ width: 60, height: 60, borderRadius: 16, border: 'none', background: 'var(--error)', display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 2, color: '#fff', cursor: 'pointer', boxShadow: '0 6px 16px -4px rgba(132,59,34,0.5)' }}>
            <BkIcon name="trash" size={19} stroke={2} style={{ color: '#fff' }} />
            <span style={{ fontSize: 9.5, fontWeight: 700 }}>Удалить</span>
          </button>
        </div>
      )}
      <button className="bk-listitem" style={{ width: '100%', textAlign: 'left', transform: swiped ? 'translateX(-72px)' : 'none', transition: 'transform .3s cubic-bezier(.32,.72,.28,1)' }}
        onClick={() => (swiped ? onSwipe(null) : (s.isCurrent ? app.openEditSession(s) : onSwipe(s.id)))}>
        {showCover && <BookCover book={{ title: s.title, author: s.author, cover: s.cover }} width={44} radius={8} />}
        <div className="bk-grow">
          <div className="bk-row" style={{ gap: 7 }}>
            <span style={{ fontSize: 14, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', flex: '0 1 auto' }}>{showCover ? s.title : s.time}</span>
            {s.isCurrent && <CurrentTag />}
          </div>
          {showCover && <div className="bk-label" style={{ fontSize: 10.5, marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{s.author}</div>}
          <div className="bk-row" style={{ gap: 7, marginTop: 9 }}>
            <MetaChip icon="book">{s.from}–{s.to}</MetaChip>
            <MetaChip icon="clock">{s.mins} мин</MetaChip>
          </div>
        </div>
        <div style={{ flex: 'none', textAlign: 'right', display: 'flex', flexDirection: 'column', alignItems: 'flex-end', alignSelf: 'stretch', justifyContent: showCover ? 'space-between' : 'center', minWidth: 46 }}>
          {showCover && <span style={{ fontSize: 11, color: 'var(--text2)' }}>{s.time}</span>}
          <div style={{ lineHeight: 1 }}>
            <span style={{ fontFamily: 'var(--serif)', fontSize: 22, fontWeight: 700, color: 'var(--accent-deep)' }}>+{s.to - s.from}</span>
            <span style={{ display: 'block', fontSize: 10, fontWeight: 600, color: 'var(--text2)', marginTop: 1 }}>стр</span>
          </div>
        </div>
      </button>
    </div>
  );
}

function JournalScreen({ app }) {
  const open = app.journal != null;
  const scope = app.journal; // null | {bookId, title}
  const [swipeId, setSwipeId] = useJState(null);
  const [hint, setHint] = useJState('open'); // open | rest
  const [toast, setToast] = useJState(false);
  useJEffect(() => { if (open) { setSwipeId(null); } }, [open, scope && scope.bookId]);

  const allLog = BK_DATA.sessionLog(app.state.books);
  const log = scope && scope.bookId ? BK_DATA.sessionLog(app.state.books, scope.bookId) : allLog;
  const byBook = !!(scope && scope.bookId);

  // group by day label
  const groups = {};
  log.forEach((s) => { const g = BK_DATA.dayLabel(s.dayOffset); (groups[g] = groups[g] || []).push(s); });
  const order = ['Сегодня', 'Вчера', 'На этой неделе'];

  const closeHint = () => {
    setHint('rest'); setToast(true);
    setTimeout(() => setToast(false), 2600);
  };

  return (
    <div className={'bk-push top' + (open ? ' open' : '')} data-screen-label="Журнал чтения" style={{ padding: 0, display: 'flex', flexDirection: 'column' }}>
      {open && (
        <>
          <header style={{ padding: '16px 20px 12px', flex: 'none' }}>
            <div className="bk-row" style={{ gap: 12 }}>
              <button className="bk-iconbtn" aria-label="Назад" onClick={() => app.closeJournal()}>
                <BkIcon name="back" size={20} />
              </button>
              <div className="bk-grow">
                <div className="bk-label" style={{ fontSize: 10.5 }}>{byBook ? 'История книги' : 'Продуктивность'}</div>
                <h1 className="bk-title" style={{ margin: '2px 0 0', fontSize: 21, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{byBook ? scope.title : 'Журнал чтения'}</h1>
              </div>
              {!byBook && hint === 'rest' && (
                <button className="bk-iconbtn" aria-label="Подсказка" onClick={() => setHint('open')}>
                  <BkIcon name="help" size={20} />
                </button>
              )}
            </div>
          </header>

          <div style={{ flex: 1, overflowY: 'auto', padding: '0 20px 24px', scrollbarWidth: 'none' }}>
            {!byBook && <JournalSummary books={app.state.books} />}

            {!byBook && hint === 'open' && (
              <div className="bk-nudge" style={{ alignItems: 'flex-start', marginTop: 8 }}>
                <BkIcon name="pin" size={18} style={{ color: 'var(--accent-deep)', flex: 'none', marginTop: 1 }} />
                <span style={{ flex: 1 }}>Удалять можно записи с меткой <b style={{ color: 'var(--accent-deep)' }}>текущая</b> — это ваша позиция в книге. Прошлые сессии заморожены, чтобы прогресс оставался цельным.</span>
                <button aria-label="Скрыть" onClick={closeHint} style={{ flex: 'none', width: 22, height: 22, borderRadius: '50%', border: 'none', background: 'transparent', color: 'var(--text2)', cursor: 'pointer', display: 'flex', alignItems: 'center', justifyContent: 'center', margin: '-2px -4px 0 0' }}>
                  <BkIcon name="close" size={15} stroke={2.2} />
                </button>
              </div>
            )}

            {log.length === 0 ? (
              <div style={{ textAlign: 'center', padding: '60px 30px' }}>
                <div className="bk-caption" style={{ textWrap: 'pretty' }}>Здесь появятся сессии чтения этой книги.</div>
              </div>
            ) : order.filter((g) => groups[g]).map((g) => {
              const items = groups[g];
              const sp = items.reduce((a, s) => a + (s.to - s.from), 0);
              const mn = items.reduce((a, s) => a + s.mins, 0);
              return (
                <section key={g}>
                  <div className="bk-row" style={{ gap: 12, margin: '20px 2px 12px' }}>
                    <span className="bk-label" style={{ flex: 'none' }}>{g}</span>
                    <span style={{ flex: 1, height: 1, background: 'var(--divider)' }}></span>
                    <span className="bk-caption" style={{ fontSize: 11, flex: 'none' }}>{sp} стр · {mn >= 60 ? Math.floor(mn / 60) + ' ч ' + (mn % 60) + ' м' : mn + ' мин'}</span>
                  </div>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                    {items.map((s) => (
                      <SessionRow key={s.id} s={s} app={app} showCover={!byBook}
                        swiped={swipeId === s.id} onSwipe={setSwipeId} />
                    ))}
                  </div>
                </section>
              );
            })}
          </div>

          {/* toast */}
          <div style={{ position: 'absolute', left: 20, right: 20, bottom: 22, zIndex: 8, display: 'flex', alignItems: 'center', gap: 10,
            background: 'var(--text)', color: 'var(--canvas)', borderRadius: 14, padding: '13px 16px', boxShadow: '0 12px 30px -8px rgba(0,0,0,0.4)',
            opacity: toast ? 1 : 0, transform: toast ? 'translateY(0)' : 'translateY(12px)', transition: 'opacity .3s, transform .3s', pointerEvents: 'none' }}>
            <BkIcon name="help" size={18} style={{ color: 'var(--canvas)', flex: 'none' }} />
            <span style={{ fontSize: 13, fontWeight: 500, lineHeight: 1.35 }}>Подсказку можно открыть по «?» сверху</span>
          </div>
        </>
      )}
    </div>
  );
}

/* edit session sheet */
function SessionEditSheet({ app }) {
  const ov = app.overlay;
  const open = ov && ov.type === 'editSession';
  const s = open ? ov.session : null;
  const [mins, setMins] = useJState(null);
  useJEffect(() => { if (open && s) setMins(s.mins); }, [open, s && s.id]);
  const book = s ? app.state.books.find((b) => b.id === s.bookId) : null;
  const total = book ? book.pages : (s ? s.to + 100 : 320);

  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} style={{ zIndex: 82 }} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} style={{ zIndex: 83 }} data-screen-label="Изменить запись">
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          {s && (
            <>
              <div className="bk-row" style={{ gap: 13, marginBottom: 20 }}>
                <BookCover book={{ title: s.title, author: s.author, cover: s.cover }} width={46} radius={10} />
                <div className="bk-grow">
                  <div className="bk-title" style={{ fontSize: 18 }}>Изменить запись</div>
                  <div className="bk-caption" style={{ marginTop: 2 }}>{s.title} · Сегодня</div>
                </div>
              </div>

              <div className="bk-label" style={{ marginBottom: 8 }}>Страницы за сессию</div>
              <div className="bk-row" style={{ gap: 10, alignItems: 'stretch' }}>
                <div style={{ flex: 1, background: 'var(--chip-bg)', borderRadius: 16, padding: '11px 14px' }}>
                  <div className="bk-caption" style={{ fontSize: 11.5, fontWeight: 600 }}>с</div>
                  <span style={{ fontFamily: 'var(--serif)', fontSize: 24, fontWeight: 700, color: 'var(--text2)' }}>{s.from}</span>
                </div>
                <div className="bk-row" style={{ color: 'var(--accent-deep)', flex: 'none' }}><BkIcon name="arrowDown" size={20} style={{ transform: 'rotate(-90deg)' }} /></div>
                <div style={{ flex: 1, background: 'var(--surface-pure)', border: '1.5px solid var(--accent)', borderRadius: 16, padding: '11px 14px' }}>
                  <div style={{ fontSize: 11.5, fontWeight: 600, color: 'var(--accent-deep)' }}>до страницы</div>
                  <div style={{ display: 'flex', alignItems: 'baseline', gap: 4, marginTop: 2 }}>
                    <span style={{ fontFamily: 'var(--serif)', fontSize: 24, fontWeight: 700 }}>{s.to}</span>
                    <span className="bk-caption" style={{ fontSize: 12 }}>/ {total}</span>
                  </div>
                </div>
              </div>
              <div style={{ fontSize: 12, color: 'var(--accent-deep)', fontWeight: 600, marginTop: 8 }}>+{s.to - s.from} страницы · {Math.round((s.to - s.from) / total * 100)}% книги</div>

              <div style={{ margin: '18px 0 6px' }}>
                <div className="bk-row" style={{ justifyContent: 'space-between', marginBottom: 2 }}>
                  <span className="bk-label" style={{ whiteSpace: 'nowrap' }}>Время чтения</span>
                  <span style={{ fontFamily: 'var(--serif)', fontSize: 20, fontWeight: 700, color: mins ? 'var(--accent-deep)' : 'var(--text2)', whiteSpace: 'nowrap', flex: 'none' }}>{mins ? mins + ' мин' : 'Не указано'}</span>
                </div>
                <TimeRuler value={mins} onChange={setMins} variant="sheet" />
              </div>

              <button className="bk-btn bk-btn-primary" style={{ marginTop: 10 }} onClick={() => app.closeOverlay()}>Сохранить</button>
              <button className="bk-btn bk-btn-danger" style={{ marginTop: 8 }} onClick={() => app.askDeleteSession(s)}>Удалить запись</button>
            </>
          )}
        </div>
      </div>
    </>
  );
}

/* delete-session confirm dialog */
function SessionDeleteDialog({ app }) {
  const ov = app.overlay;
  const open = ov && ov.type === 'delSession';
  const s = open ? ov.session : null;
  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} style={{ zIndex: 84 }} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-dialog' + (open ? ' open' : '')} style={{ zIndex: 85 }}>
        {s && (
          <>
            <div style={{ width: 52, height: 52, borderRadius: 16, margin: '0 auto 14px', background: 'color-mix(in oklab, var(--error) 16%, var(--surface))', display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--error)' }}>
              <BkIcon name="trash" size={24} style={{ color: 'var(--error)' }} />
            </div>
            <div className="bk-title" style={{ fontSize: 19 }}>Удалить запись?</div>
            <p className="bk-caption" style={{ margin: '8px 0 20px', textWrap: 'pretty' }}>
              Это ваша текущая позиция в книге «{s.title}». Прогресс откатится со стр. {s.to} до {s.from}.
            </p>
            <div style={{ display: 'flex', gap: 10 }}>
              <button className="bk-btn bk-btn-ghost" style={{ minHeight: 48 }} onClick={() => app.closeOverlay()}>Оставить</button>
              <button className="bk-btn" style={{ minHeight: 48, background: 'var(--error)', color: 'var(--on-accent)' }} onClick={() => app.deleteSession(s)}>Удалить</button>
            </div>
          </>
        )}
      </div>
    </>
  );
}

Object.assign(window, { JournalScreen, SessionEditSheet, SessionDeleteDialog });
