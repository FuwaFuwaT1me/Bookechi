// Bookechi — extra screens: карточка книги, поиск, напоминание, цель недели
const { useState: useXState, useEffect: useXEffect } = React;

/* ═══ BOOK CARD (push) ═══ */
function BookCardScreen({ app }) {
  const open = !!app.bookCardId;
  const book = app.state.books.find((b) => b.id === app.bookCardId) || null;
  const pct = book && book.pages ? (book.current / book.pages) * 100 : 0;
  const left = book ? book.pages - book.current : 0;
  const days = Math.max(1, Math.round(left / 24));
  const notes = book ? [
    ...book.quotes,
    ...(book.note ? [{ text: book.note, page: null }] : []),
  ] : [];

  return (
    <div className={'bk-push' + (open ? ' open' : '')} data-screen-label="Карточка книги">
      {book && (
        <>
          <div className="bk-row" style={{ justifyContent: 'space-between', marginBottom: 16 }}>
            <button className="bk-iconbtn" aria-label="Назад" onClick={() => app.closeBook()}>
              <BkIcon name="back" size={20} />
            </button>
            <div className="bk-row" style={{ gap: 8 }}>
              <button className="bk-iconbtn" aria-label="Редактировать" onClick={() => app.openEdit(book)}>
                <BkIcon name="pencil" size={18} />
              </button>
              <div className="bk-iconbtn" style={{ background: 'transparent' }}>
                <FavoriteToggle value={book.fav} onChange={(v) => app.setFav(book, v)} size={22} />
              </div>
            </div>
          </div>

          <div className="bk-row" style={{ gap: 18, alignItems: 'flex-start', marginBottom: 20 }}>
            <BookCover book={book} width={104} radius={14} />
            <div className="bk-grow" style={{ paddingTop: 6 }}>
              <h1 className="bk-title" style={{ margin: 0, fontSize: 23, textWrap: 'pretty' }}>{book.title}</h1>
              <div className="bk-caption" style={{ marginTop: 5 }}>{book.author}</div>
              <div style={{ marginTop: 12 }}><StatusChip status={book.status} /></div>
              {book.status === 'finished' && book.rating && (
                <div className="bk-row" style={{ gap: 4, marginTop: 12 }}>
                  {[1, 2, 3, 4, 5].map((s) => (
                    <svg key={s} width="18" height="18" viewBox="0 0 24 24"
                      fill={s <= book.rating ? 'var(--accent)' : 'none'}
                      stroke={s <= book.rating ? 'var(--accent)' : 'var(--divider)'}
                      strokeWidth="1.6" strokeLinejoin="round">
                      <path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z"/>
                    </svg>
                  ))}
                </div>
              )}
            </div>
          </div>

          {book.status !== 'planned' && (
            <section className="bk-card" style={{ padding: 18, marginBottom: 12 }}>
              <div className="bk-row" style={{ gap: 10, marginBottom: 10 }}>
                <span style={{ fontSize: 14, fontWeight: 600 }} className="bk-grow">
                  {book.status === 'finished' ? 'Прочитано целиком' : 'стр. ' + book.current + ' / ' + book.pages}
                </span>
              </div>
              <ProgressBar pct={pct} showPct={true} />
              {book.status === 'reading' && (
                <div className="bk-caption bk-row" style={{ gap: 6, marginTop: 12 }}>
                  <BkIcon name="flame" size={14} style={{ color: 'var(--accent)' }} />
                  <span>При твоём темпе — около {days} {plural(days, 'дня', 'дней', 'дней')} до конца</span>
                </div>
              )}
            </section>
          )}

          {book.status === 'reading' && (
            <button className="bk-btn bk-btn-primary" onClick={() => app.openProgress(book)}>Отметить прогресс</button>
          )}
          {book.status === 'planned' && (
            <button className="bk-btn bk-btn-primary" onClick={() => app.startReading(book)}>Начать читать</button>
          )}

          {app.state.hasHistory && book.status !== 'planned' && (
            <section style={{ marginTop: 24 }}>
              <div className="bk-row" style={{ justifyContent: 'space-between', marginBottom: 12 }}>
                <span className="bk-label">История чтения</span>
                <button onClick={() => app.openBookJournal(book)} style={{ background: 'none', border: 'none', cursor: 'pointer', fontSize: 12.5, fontWeight: 600, color: 'var(--accent-deep)', padding: 0 }}>Все сессии →</button>
              </div>
              <div className="bk-card" style={{ padding: '16px 18px 14px', boxShadow: 'none' }}>
                <SessionBars book={book} />
              </div>
            </section>
          )}

          <section style={{ marginTop: 24, paddingBottom: 12 }}>
            <div className="bk-label" style={{ marginBottom: 12 }}>Цитаты и заметки</div>
            {notes.length > 0 ? (
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                {notes.map((q, i) => (
                  <figure key={i} className="bk-quote" style={{ margin: 0 }}>
                    <p>«{q.text}»</p>
                    {q.page && <span>стр. {q.page}</span>}
                  </figure>
                ))}
              </div>
            ) : (
              <div style={{
                border: '1.6px dashed var(--divider)', borderRadius: 18,
                padding: '20px 18px', textAlign: 'center',
              }}>
                <div className="bk-caption" style={{ textWrap: 'pretty' }}>
                  Здесь будут ваши цитаты — сохраняйте любимые строки после сессий чтения.
                </div>
              </div>
            )}
          </section>
        </>
      )}
    </div>
  );
}

/* ═══ SEARCH (sheet) ═══ */
function SearchSheet({ app }) {
  const open = app.overlay && app.overlay.type === 'search';
  const [q, setQ] = useXState('');
  useXEffect(() => { if (open) setQ(''); }, [open]);

  const ql = q.trim().toLowerCase();
  const results = BK_DATA.SEARCH_BOOKS.filter((b) =>
    !ql || b.title.toLowerCase().includes(ql) || b.author.toLowerCase().includes(ql));

  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} data-screen-label="Поиск книги">
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          <h2 className="bk-title" style={{ margin: '6px 0 16px' }}>Добавить книгу</h2>
          <input className="bk-input" placeholder="Найти по названию или автору"
            value={q} onChange={(e) => setQ(e.target.value)} />

          <div style={{ display: 'flex', flexDirection: 'column', gap: 8, marginTop: 16, minHeight: 200 }}>
            {results.length === 0 && (
              <div className="bk-caption" style={{ textAlign: 'center', padding: '28px 20px' }}>
                Ничего не нашлось — попробуйте иначе или введите вручную.
              </div>
            )}
            {results.map((r) => (
              <button key={r.title} className="bk-listitem" style={{ width: '100%', textAlign: 'left' }}
                onClick={() => app.pickSearchResult(r)}>
                <BookCover book={{ title: r.title, author: r.author, cover: BK_DATA.COVER_TONES[r.tone] }} width={40} radius={8} />
                <div className="bk-grow">
                  <div style={{ fontSize: 14.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{r.title}</div>
                  <div className="bk-caption" style={{ marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{r.author} · {r.pages} стр.</div>
                </div>
                <BkIcon name="plus" size={18} style={{ color: 'var(--accent-deep)', flex: 'none' }} />
              </button>
            ))}
          </div>

          <button className="bk-btn bk-btn-secondary" style={{ marginTop: 16 }} onClick={() => app.openAdd()}>
            Ввести вручную
          </button>
        </div>
      </div>
    </>
  );
}

/* ═══ REMINDER (sheet) ═══ */
const REMINDER_TIMES = ['20:00', '20:30', '21:00', '21:30', '22:00'];
function ReminderSheet({ app }) {
  const open = app.overlay && app.overlay.type === 'reminder';
  const r = app.reminder;

  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} data-screen-label="Напоминание о чтении">
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          <h2 className="bk-title" style={{ margin: '6px 0 8px' }}>Напоминание о чтении</h2>
          <p className="bk-caption" style={{ margin: '0 0 20px', textWrap: 'pretty' }}>
            Вечером, когда дела стихают, — самое время для пары глав. Напомним мягко, один раз в день.
          </p>

          <button className="bk-row bk-card" style={{ width: '100%', padding: '14px 16px', gap: 12, boxShadow: 'none', textAlign: 'left' }}
            onClick={() => app.setReminder({ ...r, on: !r.on })}>
            <span style={{ fontSize: 15, fontWeight: 600 }} className="bk-grow">Напоминать каждый день</span>
            <span className={'bk-switch' + (r.on ? ' on' : '')}></span>
          </button>

          {r.on && (
            <div style={{ marginTop: 18 }}>
              <div className="bk-label" style={{ marginBottom: 12 }}>Время</div>
              <div style={{ display: 'flex', gap: 7, flexWrap: 'wrap' }}>
                {REMINDER_TIMES.map((tm) => (
                  <button key={tm} className={'bk-chip' + (r.time === tm ? ' active' : '')}
                    onClick={() => app.setReminder({ ...r, time: tm })}>{tm}</button>
                ))}
              </div>
              <p className="bk-caption" style={{ margin: '16px 0 0' }}>
                Хорошо: в {r.time} напомним, что «{(app.state.books.find((b) => b.active) || { title: 'ваша книга' }).title}» ждёт.
              </p>
            </div>
          )}

          <button className="bk-btn bk-btn-primary" style={{ marginTop: 24 }} onClick={() => app.closeOverlay()}>Готово</button>
        </div>
      </div>
    </>
  );
}

/* ═══ WEEKLY GOAL (sheet) ═══ */
function GoalSheet({ app }) {
  const open = app.overlay && app.overlay.type === 'goal';
  const [type, setType] = useXState('pages');
  const [value, setValue] = useXState(400);

  useXEffect(() => {
    if (open) { setType(app.state.goal.type); setValue(app.state.goal.value); }
  }, [open]);

  const isDays = type === 'days';
  const min = isDays ? 1 : 50, max = isDays ? 7 : 500, step = isDays ? 1 : 25;
  const unit = isDays ? plural(value, 'день', 'дня', 'дней') : 'страниц';

  function switchType(t) {
    setType(t);
    setValue(t === 'days' ? 5 : 400);
  }

  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} data-screen-label="Цель недели">
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          <h2 className="bk-title" style={{ margin: '6px 0 8px' }}>Цель недели</h2>
          <p className="bk-caption" style={{ margin: '0 0 20px', textWrap: 'pretty' }}>
            Небольшая, но регулярная цель работает лучше героических планов.
          </p>

          <div className="bk-seg">
            <button className={!isDays ? 'active' : ''} onClick={() => switchType('pages')}>Страниц в неделю</button>
            <button className={isDays ? 'active' : ''} onClick={() => switchType('days')}>Дней в неделю</button>
          </div>

          {isDays && (
            <p className="bk-caption" style={{ margin: '14px 0 0', textWrap: 'pretty' }}>
              Цель по дням отражается в карточке серии на главном экране — отдельный бар не понадобится.
            </p>
          )}

          <div className="bk-row" style={{ justifyContent: 'center', gap: 22, margin: '26px 0' }}>
            <button className="bk-iconbtn" aria-label="Меньше" style={{ width: 52, height: 52 }}
              onClick={() => setValue(Math.max(min, value - step))}>
              <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round"><path d="M5 12h14"/></svg>
            </button>
            <div style={{ textAlign: 'center', minWidth: 110 }}>
              <div style={{ fontFamily: 'var(--serif)', fontSize: 56, fontWeight: 600, lineHeight: 1 }}>{value}</div>
              <div className="bk-caption" style={{ marginTop: 6 }}>{unit}</div>
            </div>
            <button className="bk-iconbtn" aria-label="Больше" style={{ width: 52, height: 52 }}
              onClick={() => setValue(Math.min(max, value + step))}>
              <BkIcon name="plus" size={20} stroke={2} />
            </button>
          </div>

          <button className="bk-btn bk-btn-primary" onClick={() => app.saveGoal({ type, value })}>Сохранить цель</button>
        </div>
      </div>
    </>
  );
}

Object.assign(window, { BookCardScreen, SearchSheet, ReminderSheet, GoalSheet });
