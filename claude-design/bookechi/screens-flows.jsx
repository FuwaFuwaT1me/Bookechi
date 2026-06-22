// Bookechi — flow screens: Добавить/Редактировать книгу, Отметить прогресс, Успех
const { useState: useFState, useEffect: useFEffect, useRef: useFRef } = React;

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

  // live cover preview reflecting typed title/author
  const previewTone = isEdit && src ? src.cover
    : prefill ? BK_DATA.COVER_TONES[prefill.tone]
    : BK_DATA.COVER_TONES[(f.title.trim().length * 3) % BK_DATA.COVER_TONES.length];
  const hasPreview = !!(isEdit || prefill || f.title.trim());
  const previewBook = { title: f.title.trim() || 'Без названия', author: f.author.trim() || 'Автор', cover: previewTone };

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

          {/* cover hero — large, centered, live preview */}
          <div className="bk-cover-stage">
            <button className="bk-cover-hero" aria-label={isEdit ? 'Сменить обложку' : 'Добавить обложку'}>
              {hasPreview
                ? <BookCover book={previewBook} width={116} radius={14} />
                : <BookCover placeholder width={116} radius={14} />}
              <span className="bk-cover-cam"><BkIcon name="camera" size={18} /></span>
            </button>
            <div className="bk-caption" style={{ textAlign: 'center', marginTop: 12, textWrap: 'pretty' }}>
              {isEdit ? 'Нажмите, чтобы сменить обложку'
                : prefill ? 'Обложка из каталога — можно заменить своим фото'
                : hasPreview ? 'Превью обложки — добавьте своё фото по желанию'
                : 'Добавьте обложку: фото или из каталога'}
            </div>
          </div>

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
/* TimeRuler — horizontal scroll-ruler, 1-minute resolution, optional (0 = не указано) */
function TimeRuler({ value, onChange, variant }) {
  const STEP = 1, GAP = 9, MAX = 180;
  const ref = useFRef(null);
  const ticks = [];
  for (let m = 0; m <= MAX; m += STEP) ticks.push(m);

  const onScroll = React.useCallback(() => {
    const el = ref.current; if (!el) return;
    const i = Math.round(el.scrollLeft / GAP);
    const v = Math.max(0, Math.min(MAX, i * STEP));
    onChange(v === 0 ? null : v);
  }, [onChange]);

  // external resets (value→null) re-sync scroll; user scrolling won't (diff < GAP)
  useFEffect(() => {
    const el = ref.current; if (!el) return;
    const target = (value || 0) / STEP * GAP;
    if (Math.abs(el.scrollLeft - target) > GAP * 1.5) el.scrollLeft = target;
  }, [value]);

  return (
    <div>
      {variant !== 'sheet' && (
        <>
          <div className="bk-row" style={{ gap: 7, marginBottom: 8 }}>
            <BkIcon name="clock" size={16} style={{ color: 'var(--accent-deep)', flex: 'none' }} />
            <span style={{ fontSize: 13, fontWeight: 600, whiteSpace: 'nowrap', flex: 'none' }}>Сколько читали?</span>
            <span className="bk-caption" style={{ fontSize: 12, whiteSpace: 'nowrap' }}>— необязательно</span>
          </div>
          <div style={{
            fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 26, lineHeight: 1.1,
            color: value ? 'var(--accent-deep)' : 'var(--text2)', marginBottom: 6,
          }}>
            {value ? fmtDuration(value) : 'Не указано'}
          </div>
        </>
      )}
      <div style={{ position: 'relative' }}>
        {/* center caret */}
        <div style={{ position: 'absolute', left: '50%', top: 0, transform: 'translateX(-50%)', zIndex: 3, width: 0, height: 0, borderLeft: '6px solid transparent', borderRight: '6px solid transparent', borderTop: '8px solid var(--accent)' }}></div>
        <div style={{ position: 'absolute', left: '50%', top: 6, height: 30, width: 2, transform: 'translateX(-50%)', background: 'var(--accent)', zIndex: 3, borderRadius: 2 }}></div>
        <div ref={ref} onScroll={onScroll} className="bk-ruler" style={{
          overflowX: 'scroll', scrollSnapType: 'x mandatory',
          maskImage: 'linear-gradient(90deg, transparent, #000 16%, #000 84%, transparent)',
          WebkitMaskImage: 'linear-gradient(90deg, transparent, #000 16%, #000 84%, transparent)',
          padding: '10px 0 6px',
        }}>
          <div style={{ display: 'flex', alignItems: 'flex-end', height: 46, width: 'max-content', padding: '0 calc(50% - 1px)' }}>
            {ticks.map((m) => {
              const major = m % 15 === 0, mid = m % 5 === 0;
              const label = m % 60 === 0 ? (m / 60) + ' ч' : (major ? String(m) : null);
              return (
                <div key={m} style={{ width: GAP, flex: 'none', display: 'flex', flexDirection: 'column', alignItems: 'center', scrollSnapAlign: 'center' }}>
                  <div style={{
                    width: 2, borderRadius: 2,
                    height: major ? 30 : mid ? 18 : 11,
                    background: major ? 'var(--accent-deep)' : mid ? 'var(--text2)' : 'var(--divider)',
                  }}></div>
                  {label && <div style={{ fontSize: 9.5, color: 'var(--text2)', marginTop: 4, whiteSpace: 'nowrap' }}>{label}</div>}
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </div>
  );
}

/* half perimeter path: bottom-center → up the right side → top-center */
function bkHalfPath(W, H, rr) {
  return `M ${W / 2} ${H} L ${W - rr} ${H} Q ${W} ${H} ${W} ${H - rr} L ${W} ${rr} Q ${W} 0 ${W - rr} 0 L ${W / 2} 0`;
}

/* cover progress visual — two styles:
   «Заливка» read part full colour, unread dimmed, waterline rises;
   «Рамка»   bright cover, gradient frame fills bottom-up both sides. */
function ProgressCover({ book, curFrac, targetFrac, variant = 'Заливка' }) {
  const W = 100, H = Math.round(W * 1.45), r = 14;
  const cur = Math.max(0, Math.min(1, curFrac));
  const tgt = Math.max(cur, Math.min(1, targetFrac));

  if (variant === 'Рамка') {
    const pad = 8, ow = W + pad * 2, oh = H + pad * 2, rr = r + 5;
    const d = bkHalfPath(ow - 8, oh - 8, rr);
    const mirror = `scale(-1,1) translate(${-(ow - 8)},0)`;
    const off = (1 - tgt) * 100;
    return (
      <div className="bk-pframe" style={{ width: ow, height: oh }}>
        <svg width={ow} height={oh} style={{ position: 'absolute', inset: 0, overflow: 'visible' }}>
          <g transform="translate(4,4)">
            <path d={d} fill="none" stroke="var(--chip)" strokeWidth="4" strokeLinecap="round"></path>
            <path d={d} transform={mirror} fill="none" stroke="var(--chip)" strokeWidth="4" strokeLinecap="round"></path>
            <path d={d} fill="none" stroke="url(#bk-pframe-g)" strokeWidth="4" strokeLinecap="round" pathLength="100" strokeDasharray="100" strokeDashoffset={off}></path>
            <path d={d} transform={mirror} fill="none" stroke="url(#bk-pframe-g)" strokeWidth="4" strokeLinecap="round" pathLength="100" strokeDasharray="100" strokeDashoffset={off}></path>
          </g>
          <defs><linearGradient id="bk-pframe-g" x1="0" y1="1" x2="0.4" y2="0"><stop offset="0" stopColor="var(--goal-g1)"></stop><stop offset="1" stopColor="var(--goal-g2)"></stop></linearGradient></defs>
        </svg>
        <div style={{ position: 'absolute', left: pad, top: pad }}><BookCover book={book} width={W} radius={r} /></div>
        <span className="bk-pframe-flag">{Math.round(tgt * 100)}%</span>
      </div>
    );
  }

  const gain = tgt > cur + 0.001;
  return (
    <div className="bk-pcover" style={{ width: W, height: H }}>
      <BookCover book={book} width={W} radius={r} />
      {/* unread dim — full-height layer scaled from the top (transform animates reliably) */}
      <div className="bk-pcover-scrim" style={{ transform: 'scaleY(' + (1 - tgt) + ')', borderRadius: r + 'px ' + r + 'px 0 0' }}></div>
      {/* today's gain band (keyed to remount so its px box always resolves) */}
      {gain && (
        <div key={Math.round(cur * 1000) + '-' + Math.round(tgt * 1000)} className="bk-pcover-gain"
          style={{ bottom: cur * H, height: (tgt - cur) * H }}></div>
      )}
      {/* target waterline (where you are now) */}
      {tgt > 0.01 && tgt < 0.995 && (
        <div className="bk-pcover-line tgt" style={{ transform: 'translateY(' + (-tgt * H) + 'px)' }}>
          <span className="bk-pcover-flag">{Math.round(tgt * 100)}%</span>
        </div>
      )}
    </div>
  );
}

function ProgressScreen({ app }) {
  const ov = app.overlay;
  const open = ov && ov.type === 'progress';
  const book = open ? ov.book : null;

  const [val, setVal] = useFState('');
  const [mins, setMins] = useFState(null);
  const [timeOpen, setTimeOpen] = useFState(false);
  useFEffect(() => { if (open) { setVal(''); setMins(null); setTimeOpen(false); } }, [open, book && book.id]);

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
          <div className="bk-row" style={{ justifyContent: 'space-between', marginBottom: 6 }}>
            <button className="bk-iconbtn" aria-label="Назад" onClick={() => app.closeOverlay()}>
              <BkIcon name="back" size={20} />
            </button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginBottom: 22 }}>
            <ProgressCover book={book} curFrac={start / total} targetFrac={(valid ? endN : start) / total} variant={app.coverStyle} />
            <div className="bk-title" style={{ fontSize: 18, textAlign: 'center', marginTop: 16, textWrap: 'pretty' }}>{book.title}</div>
            <div className="bk-caption" style={{ marginTop: 4 }}>сейчас — стр. {start} / {total} · {curPct}%</div>
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

          <button type="button" className={'bk-timebtn' + (mins ? ' set' : '')} style={{ marginTop: 14 }}
            onClick={() => setTimeOpen(true)}>
            <span className="bk-timebtn-ic"><BkIcon name="clock" size={19} /></span>
            <span className="bk-grow" style={{ textAlign: 'left' }}>
              {mins ? (
                <>
                  <span style={{ display: 'block', fontSize: 11.5, color: 'var(--text2)', fontWeight: 500 }}>Время чтения</span>
                  <span style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 18, color: 'var(--accent-deep)' }}>{fmtDuration(mins)}</span>
                </>
              ) : (
                <>
                  <span style={{ fontSize: 15, fontWeight: 600 }}>Выбрать время</span>
                  <span style={{ display: 'block', fontSize: 12, color: 'var(--text2)' }}>необязательно</span>
                </>
              )}
            </span>
            <span className="bk-timebtn-act">{mins ? 'Изменить' : <BkIcon name="plus" size={18} stroke={2.2} />}</span>
          </button>

          <button className="bk-btn bk-btn-primary" disabled={!valid}
            style={{ marginTop: 24, opacity: valid ? 1 : 0.45 }}
            onClick={() => valid && app.saveProgress(book, endN, mins || null)}>
            Сохранить прогресс
          </button>
        </>
      )}

      {/* time picker sheet (nested above the progress push) */}
      <TimeSheet open={timeOpen} initial={mins}
        onClose={() => setTimeOpen(false)}
        onSave={(v) => { setMins(v); setTimeOpen(false); }} />
    </div>
  );
}

/* ── TimeSheet — bottom sheet with the minute ruler ── */
function TimeSheet({ open, initial, onClose, onSave }) {
  const [val, setVal] = useFState(initial || null);
  useFEffect(() => { if (open) setVal(initial || null); }, [open]);
  return (
    <>
      <div onClick={onClose} style={{
        position: 'absolute', inset: 0, zIndex: 80, background: 'var(--scrim)',
        opacity: open ? 1 : 0, pointerEvents: open ? 'auto' : 'none', transition: 'opacity 0.3s ease',
      }}></div>
      <div style={{
        position: 'absolute', left: 0, right: 0, bottom: 0, zIndex: 81,
        background: 'var(--canvas)', borderRadius: '32px 32px 0 0',
        transform: open ? 'translateY(0)' : 'translateY(108%)',
        transition: 'transform 0.4s cubic-bezier(.32,.72,.28,1)',
        boxShadow: '0 -20px 60px rgba(0,0,0,0.28)', padding: '10px 20px 26px',
      }}>
        <div className="bk-sheet-grab"></div>
        <h2 className="bk-title" style={{ margin: '6px 0 4px', textAlign: 'center' }}>Сколько читали?</h2>
        <p className="bk-caption" style={{ margin: '0 0 8px', textAlign: 'center' }}>Перетащите линейку — поминутно</p>
        <div style={{ textAlign: 'center', margin: '8px 0 2px' }}>
          <span style={{ fontFamily: 'var(--serif)', fontWeight: 600, fontSize: 38, color: val ? 'var(--accent-deep)' : 'var(--text2)' }}>
            {val ? fmtDuration(val) : 'Не указано'}
          </span>
        </div>
        <TimeRuler value={val} onChange={setVal} variant="sheet" />
        <button className="bk-btn bk-btn-primary" style={{ marginTop: 22 }} onClick={() => onSave(val)}>Сохранить</button>
      </div>
    </>
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

Object.assign(window, { BookFormSheet, ProgressScreen, SuccessScreen, TimeRuler });
