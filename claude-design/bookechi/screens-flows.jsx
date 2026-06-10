// Bookechi — flow screens: Добавить/Редактировать книгу, Отметить прогресс, Успех
const { useState: useFState, useEffect: useFEffect } = React;

/* ═══ ADD / EDIT BOOK (bottom sheet) ═══ */
function BookFormSheet({ app }) {
  const ov = app.overlay;
  const open = ov && (ov.type === 'add' || ov.type === 'edit');
  const isEdit = ov && ov.type === 'edit';
  const src = isEdit ? ov.book : null;
  const prefill = !isEdit && ov ? ov.prefill : null;

  const [f, setF] = useFState({ title: '', author: '', pages: '', status: 'reading', current: '', fav: false });
  const [errs, setErrs] = useFState({});
  const [confirmDel, setConfirmDel] = useFState(false);

  useFEffect(() => {
    if (open) {
      setErrs({}); setConfirmDel(false);
      setF(src
        ? { title: src.title, author: src.author, pages: String(src.pages), status: src.status, current: String(src.current || ''), fav: src.fav }
        : prefill
          ? { title: prefill.title, author: prefill.author, pages: String(prefill.pages), status: 'reading', current: '', fav: false }
          : { title: '', author: '', pages: '', status: 'reading', current: '', fav: false });
    }
  }, [open, src && src.id, prefill && prefill.title]);

  const set = (k) => (v) => { setF((p) => ({ ...p, [k]: v })); setErrs((p) => ({ ...p, [k]: null })); };

  function submit() {
    const e = {};
    if (!f.title.trim()) e.title = 'Без названия не получится — добавьте его';
    const pages = parseInt(f.pages, 10);
    if (!pages || pages <= 0) e.pages = 'Укажите число страниц больше нуля';
    const current = parseInt(f.current, 10) || 0;
    if (f.status === 'reading' && pages > 0 && current > pages) e.current = 'Не больше, чем страниц в книге';
    setErrs(e);
    if (Object.keys(e).length) return;
    app.saveBook({
      id: src ? src.id : null,
      title: f.title.trim(), author: f.author.trim() || 'Автор не указан',
      pages, status: f.status,
      current: f.status === 'finished' ? pages : (f.status === 'reading' ? current : 0),
      fav: f.fav,
      tone: prefill ? prefill.tone : null,
    });
  }

  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} onClick={() => app.closeOverlay()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} data-screen-label={isEdit ? 'Редактировать книгу' : 'Добавить книгу'}>
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          <div className="bk-row" style={{ padding: '6px 0 18px', gap: 12 }}>
            <h2 className="bk-title bk-grow" style={{ margin: 0 }}>{isEdit ? 'Редактировать книгу' : 'Добавить книгу'}</h2>
            {isEdit && <FavoriteToggle value={f.fav} onChange={set('fav')} size={24} />}
          </div>

          {/* cover block */}
          <button className="bk-row" style={{
            gap: 16, width: '100%', textAlign: 'left', padding: 16,
            borderRadius: 'var(--r-card)', background: 'var(--addblock)',
            border: '1.6px dashed var(--divider)',
          }}>
            {isEdit && src ? <BookCover book={src} width={56} radius={10} />
              : prefill ? <BookCover book={{ title: prefill.title, author: prefill.author, cover: BK_DATA.COVER_TONES[prefill.tone] }} width={56} radius={10} />
              : <BookCover placeholder width={56} radius={10} />}
            <div className="bk-grow">
              <div style={{ fontSize: 14.5, fontWeight: 600 }}>{isEdit ? 'Сменить обложку' : prefill ? 'Обложка найдена' : 'Добавить обложку'}</div>
              <div className="bk-caption" style={{ marginTop: 2 }}>{prefill ? 'Можно заменить своим фото' : 'Фото или из каталога — по желанию'}</div>
            </div>
            <div className="bk-iconbtn" style={{ width: 40, height: 40, background: 'var(--surface)' }}>
              <BkIcon name="camera" size={19} />
            </div>
          </button>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 16, marginTop: 20 }}>
            <WarmTextField label="Название" value={f.title} onChange={set('title')} placeholder="Например, «Норвежский лес»" error={errs.title} />
            <WarmTextField label="Автор" value={f.author} onChange={set('author')} placeholder="Имя автора" />
            <WarmTextField label="Всего страниц" value={f.pages} onChange={set('pages')} placeholder="0" inputMode="numeric" error={errs.pages} />

            <div className="bk-field">
              <label>Статус</label>
              <div className="bk-statuspick">
                {['reading', 'planned', 'finished'].map((s) => (
                  <button key={s} className={f.status === s ? 'active' : ''} onClick={() => set('status')(s)}>{STATUS_LABEL[s]}</button>
                ))}
              </div>
            </div>

            {f.status === 'reading' && (
              <WarmTextField label="Текущая страница" value={f.current} onChange={set('current')} placeholder="0" inputMode="numeric" error={errs.current} />
            )}
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 10, marginTop: 26 }}>
            <button className="bk-btn bk-btn-primary" onClick={submit}>{isEdit ? 'Сохранить' : 'Добавить книгу'}</button>
            {isEdit && (
              <button className="bk-btn bk-btn-danger" onClick={() => setConfirmDel(true)}>
                <BkIcon name="trash" size={18} /> Удалить книгу
              </button>
            )}
          </div>
        </div>
      </div>

      {/* delete confirm */}
      <div className={'bk-scrim' + (confirmDel ? ' open' : '')} style={{ zIndex: 65 }} onClick={() => setConfirmDel(false)}></div>
      <div className={'bk-dialog' + (confirmDel ? ' open' : '')}>
        <div className="bk-title" style={{ fontSize: 19 }}>Удалить книгу?</div>
        <p className="bk-caption" style={{ margin: '8px 0 20px', textWrap: 'pretty' }}>
          «{f.title}» и весь её прогресс исчезнут из библиотеки. Это действие нельзя отменить.
        </p>
        <div style={{ display: 'flex', gap: 10 }}>
          <button className="bk-btn bk-btn-ghost" style={{ minHeight: 48 }} onClick={() => setConfirmDel(false)}>Оставить</button>
          <button className="bk-btn" style={{ minHeight: 48, background: 'var(--error)', color: 'var(--on-accent)' }}
            onClick={() => { setConfirmDel(false); app.deleteBook(src); }}>Удалить</button>
        </div>
      </div>
    </>
  );
}

/* ═══ MARK PROGRESS (push screen) ═══ */
function ProgressScreen({ app }) {
  const ov = app.overlay;
  const open = ov && ov.type === 'progress';
  const book = open ? ov.book : null;

  const [val, setVal] = useFState('');
  const [mins, setMins] = useFState('');
  useFEffect(() => { if (open) { setVal(''); setMins(''); } }, [open, book && book.id]);

  const start = book ? book.current : 0;
  const total = book ? book.pages : 1;
  const curPct = Math.round((start / total) * 100);
  const n = parseInt(val, 10);
  const has = val !== '' && !isNaN(n);

  let endN = null, err = null;
  if (has) {
    endN = n;
    if (n <= start) err = 'Страница должна быть больше ' + start + ' — там вы уже были.';
    else if (n > total) err = 'В книге всего ' + total + ' страниц.';
  }
  const valid = has && !err && endN != null;
  const delta = valid ? endN - start : 0;
  const sliderVal = valid ? endN : start;
  const sliderPct = total > start ? ((sliderVal - start) / (total - start)) * 100 : 0;

  function fromSlider(v) {
    const p = Math.max(start + 1, Math.min(total, Math.round(v)));
    setVal(String(p));
  }

  const placeholder = String(Math.min(total, start + 24));

  return (
    <div className={'bk-push top' + (open ? ' open' : '')} data-screen-label="Отметить прогресс">
      {book && (
        <>
          <div className="bk-row" style={{ justifyContent: 'space-between', marginBottom: 18 }}>
            <button className="bk-iconbtn" aria-label="Назад" onClick={() => app.closeOverlay()}>
              <BkIcon name="back" size={20} />
            </button>
          </div>

          <div className="bk-row" style={{ gap: 14, marginBottom: 22 }}>
            <BookCover book={book} width={56} radius={10} />
            <div className="bk-grow">
              <div className="bk-title" style={{ fontSize: 18 }}>{book.title}</div>
              <div className="bk-caption" style={{ marginTop: 3 }}>сейчас — стр. {start} / {total} · {curPct}%</div>
            </div>
          </div>

          <h1 className="bk-display" style={{ margin: '0 0 24px', fontSize: 26 }}>Где остановились сегодня?</h1>

          <div className="bk-caption" style={{ marginBottom: 10 }}>Дочитал до страницы</div>
          <div className="bk-row" style={{ gap: 12, alignItems: 'flex-end' }}>
            <input
              className={'bk-pageinput' + (err ? ' error' : '')}
              inputMode="numeric" placeholder={placeholder}
              value={val} onChange={(e) => setVal(e.target.value.replace(/\D/g, ''))}
              style={{ flex: 1 }}
            />
            <div style={{ fontFamily: 'var(--serif)', fontSize: 28, fontWeight: 600, color: 'var(--text2)', paddingBottom: 12, flex: 'none' }}>/ {total}</div>
          </div>

          <div style={{ margin: '24px 0 0' }}>
            <input type="range" className="bk-slider" aria-label="Страница"
              min={start} max={total} value={sliderVal}
              onChange={(e) => fromSlider(+e.target.value)}
              style={{ background: 'linear-gradient(to right, var(--accent) ' + sliderPct + '%, var(--chip-bg) ' + sliderPct + '%)' }} />
            <div className="bk-row" style={{ justifyContent: 'space-between', marginTop: 10 }}>
              <span className="bk-caption" style={{ fontSize: 11.5, whiteSpace: 'nowrap' }}>стр. {start}</span>
              <span className="bk-caption" style={{ fontSize: 11.5 }}>{total}</span>
            </div>
          </div>

          <div style={{ minHeight: 52, marginTop: 24 }}>
            {err && <div className="bk-err">{err}</div>}
            {valid && (
              <div style={{ fontSize: 17 }}>
                Прочитано сегодня: <span style={{ fontFamily: 'var(--serif)', fontWeight: 700, color: 'var(--accent-deep)', fontSize: 20 }}>+{delta} {plural(delta, 'страница', 'страницы', 'страниц')}</span>
                {endN === total && <div className="bk-caption" style={{ marginTop: 4 }}>…и это последняя страница книги 🎉</div>}
              </div>
            )}
          </div>

          <div className="bk-field" style={{ marginTop: 10, maxWidth: 220 }}>
            <label>Время чтения, мин — необязательно</label>
            <input className="bk-input" inputMode="numeric" placeholder="30" value={mins}
              onChange={(e) => setMins(e.target.value.replace(/\D/g, ''))} />
          </div>

          <button className="bk-btn bk-btn-primary" disabled={!valid}
            style={{ marginTop: 24, opacity: valid ? 1 : 0.45 }}
            onClick={() => valid && app.saveProgress(book, endN, parseInt(mins, 10) || null)}>
            Сохранить прогресс
          </button>
        </>
      )}
    </div>
  );
}

/* ═══ SUCCESS ═══ */
const BK_CONFETTI_COLORS = ['#BE5E3B', '#7C8A6E', '#E8C9B6', '#9E4A2C', '#DDE3D2'];
function ConfettiBurst() {
  return (
    <div className="bk-confetti-wrap">
      {Array.from({ length: 16 }).map((_, i) => (
        <span key={i} className="bk-confetti" style={{
          left: ((i * 61) % 97) + 1.5 + '%',
          background: BK_CONFETTI_COLORS[i % BK_CONFETTI_COLORS.length],
          height: 10 + (i % 3) * 4,
          animationDelay: (i % 8) * 0.09 + 's',
          transform: 'rotate(' + (i * 47) % 360 + 'deg)',
        }}></span>
      ))}
    </div>
  );
}

function SuccessScreen({ app }) {
  const ov = app.overlay;
  const open = ov && ov.type === 'success';
  const d = open ? ov.data : null;

  const [rating, setRating] = useFState(0);
  const [note, setNote] = useFState('');
  const [animPct, setAnimPct] = useFState(0);
  useFEffect(() => {
    if (open) {
      setRating(0); setNote('');
      setAnimPct(d.before);
      const id = setTimeout(() => setAnimPct(d.after), 250);
      return () => clearTimeout(id);
    }
  }, [open]);

  return (
    <div className={'bk-fade' + (open ? ' open' : '')} data-screen-label="Прогресс сохранён"
      style={{ background: 'var(--canvas)' }}>
      {d && d.finished && open && <ConfettiBurst key={'c' + d.book.id} />}
      {d && (
        <div style={{ minHeight: '100%', display: 'flex', flexDirection: 'column', justifyContent: 'center', padding: '40px 28px', textAlign: 'center' }}>
          <div className={open ? 'bk-pop' : ''} style={{
            width: 88, height: 88, borderRadius: 30, margin: '0 auto 20px',
            background: 'linear-gradient(135deg, var(--streak-g1), var(--streak-badge))',
            display: 'flex', alignItems: 'center', justifyContent: 'center', color: 'var(--flame)',
            border: '1px solid var(--stroke)',
          }}>
            <svg width="42" height="42" viewBox="0 0 24 24" fill="currentColor"><path d="M12 21c3.9 0 6.5-2.5 6.5-6 0-2.6-1.4-4.6-2.9-6.4-.5 1-1.1 1.7-2.1 2.4.2-2.9-1-6-3.5-8 .2 2.4-.6 4.2-2 5.8-1.3 1.6-2.5 3.4-2.5 6.2 0 3.5 2.6 6 6.5 6z"/></svg>
          </div>

          <h1 className="bk-display" style={{ margin: 0 }}>
            {d.finished ? 'Книга прочитана 🎉' : d.streak + ' ' + plural(d.streak, 'день', 'дня', 'дней') + ' подряд 🔥'}
          </h1>
          <p className="bk-body" style={{ margin: '10px 0 0', color: 'var(--text2)', textWrap: 'pretty' }}>
            {d.finished
              ? <>«{d.book.title}» переехала на полку «Прочитано».</>
              : <>Сегодня прочитано: <b style={{ color: 'var(--text)' }}>{d.delta} {plural(d.delta, 'страница', 'страницы', 'страниц')}</b></>}
          </p>

          <div className="bk-card" style={{ padding: 18, margin: '26px 0 0' }}>
            <div className="bk-row" style={{ justifyContent: 'center', gap: 10, fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 22 }}>
              <span style={{ color: 'var(--text2)' }}>{d.before}%</span>
              <span style={{ color: 'var(--text2)', fontFamily: 'var(--sans)', fontSize: 16 }}>→</span>
              <span style={{ color: 'var(--accent-deep)' }}>{d.after}%</span>
            </div>
            <div style={{ marginTop: 12 }}><ProgressBar pct={animPct} /></div>
            {d.finished && (
              <p className="bk-caption" style={{ margin: '12px 0 0' }}>Сегодня прочитано: {d.delta} {plural(d.delta, 'страница', 'страницы', 'страниц')}</p>
            )}
          </div>

          {d.finished ? (
            <div className="bk-card" style={{ padding: 20, marginTop: 12 }}>
              <div className="bk-label" style={{ marginBottom: 14 }}>Как вам книга?</div>
              <div className="bk-stars">
                {[1, 2, 3, 4, 5].map((s) => (
                  <button key={s} aria-label={s + ' из 5'} onClick={() => setRating(s)}
                    style={{ color: s <= rating ? 'var(--accent)' : 'var(--divider)', display: 'flex' }}>
                    <svg width="30" height="30" viewBox="0 0 24 24" fill={s <= rating ? 'currentColor' : 'none'} stroke="currentColor" strokeWidth="1.6" strokeLinejoin="round">
                      <path d="M12 3l2.7 5.8 6.3.7-4.7 4.3 1.3 6.2L12 16.8 6.4 20l1.3-6.2L3 9.5l6.3-.7z"/>
                    </svg>
                  </button>
                ))}
              </div>
              <textarea
                className="bk-input" placeholder="Заметка или любимая цитата — по желанию"
                value={note} onChange={(e) => setNote(e.target.value)}
                style={{ height: 84, padding: '13px 16px', resize: 'none', marginTop: 16, fontSize: 14.5, lineHeight: 1.4 }}
              ></textarea>
            </div>
          ) : (
            <p className="bk-body" style={{ margin: '24px 0 0', fontFamily: 'var(--serif)', fontStyle: 'italic', color: 'var(--text2)' }}>
              Хороший вечер для книги.
            </p>
          )}

          <button className="bk-btn bk-btn-primary" style={{ marginTop: 28 }}
            onClick={() => app.finishSuccess(d, rating, note)}>Готово</button>
        </div>
      )}
    </div>
  );
}

Object.assign(window, { BookFormSheet, ProgressScreen, SuccessScreen });
