// Bookechi — tab screens: Активность, Продуктивность, Библиотека

/* ═══ HOME / Активность ═══ */
function GreetingHeader({ dark, onToggleTheme }) {
  return (
    <header className="bk-row" style={{ gap: 12, padding: '14px 0 18px' }}>
      <div className="bk-grow">
        <div className="bk-caption" style={{ marginBottom: 3 }}>Добрый вечер, Иван</div>
        <h1 className="bk-display" style={{ margin: 0 }}>Что читаем сегодня?</h1>
      </div>
      <button className="bk-iconbtn" aria-label="Сменить тему" onClick={onToggleTheme}>
        <BkIcon name={dark ? 'sun' : 'moon'} size={20} />
      </button>
    </header>
  );
}

function BookHeroCard({ book, onMark, onEdit, nudge }) {
  const pct = (book.current / book.pages) * 100;
  const left = book.pages - book.current;
  const days = Math.max(1, Math.round(left / 24));
  return (
    <section className="bk-hero" data-comment-anchor="hero-card" style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
      <div className="bk-row" style={{ gap: 16, alignItems: 'flex-start' }}>
        <BookCover book={book} width={92} radius={14} />
        <div className="bk-grow" style={{ paddingTop: 2 }}>
          <div className="bk-row" style={{ gap: 8, alignItems: 'flex-start' }}>
            <div className="bk-title bk-grow" style={{ textWrap: 'pretty' }}>{book.title}</div>
            <button className="bk-iconbtn" style={{ width: 36, height: 36, margin: '-4px -4px 0 0' }} aria-label="Редактировать" onClick={() => onEdit(book)}>
              <BkIcon name="dots" size={18} />
            </button>
          </div>
          <div className="bk-caption" style={{ marginTop: 3 }}>{book.author}</div>
          <div style={{ marginTop: 14, fontSize: 13.5, fontWeight: 600 }}>
            стр. {book.current} <span style={{ color: 'var(--text2)', fontWeight: 500 }}>/ {book.pages}</span>
          </div>
          <div style={{ marginTop: 8 }}><ProgressBar pct={pct} showPct={true} /></div>
        </div>
      </div>
      <div className="bk-caption bk-row" style={{ gap: 6 }}>
        <BkIcon name="flame" size={15} style={{ color: 'var(--accent)' }} />
        <span>При твоём темпе — около {days} {plural(days, 'дня', 'дней', 'дней')} до конца</span>
      </div>
      {nudge && (
        <div className="bk-nudge">
          <BkIcon name="pencil" size={18} style={{ color: 'var(--accent-deep)', flex: 'none' }} />
          <span>Сегодняшние страницы ещё не отмечены — это займёт минуту.</span>
        </div>
      )}
      <button className="bk-btn bk-btn-primary" onClick={() => onMark(book)}>Отметить прогресс</button>
    </section>
  );
}

function BookListItem({ book, onClick, trailing }) {
  const pct = book.pages ? (book.current / book.pages) * 100 : 0;
  return (
    <button className="bk-listitem" style={{ width: '100%', textAlign: 'left' }} onClick={onClick}>
      <BookCover book={book} width={44} radius={9} />
      <div className="bk-grow">
        <div style={{ fontSize: 14.5, fontWeight: 600, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{book.title}</div>
        <div className="bk-caption" style={{ marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{book.author}</div>
        {book.status === 'reading'
          ? <div style={{ marginTop: 8, maxWidth: 170 }}><ProgressBar pct={pct} /></div>
          : <div style={{ marginTop: 6 }}><StatusChip status={book.status} /></div>}
      </div>
      {trailing}
    </button>
  );
}

function HomeScreen({ app }) {
  const { books, streak, todayMarked, nudge } = app.state;
  const active = books.find((b) => b.active && b.status === 'reading');
  const others = books.filter((b) => b.status === 'reading' && !b.active);
  const planned = books.filter((b) => b.status === 'planned');

  return (
    <div className="bk-screen" data-screen-label="Активность">
      <GreetingHeader dark={app.dark} onToggleTheme={app.toggleTheme} />
      <StreakBlock streak={streak} todayMarked={todayMarked} />
      <div style={{ height: 16 }}></div>

      {books.length === 0 && (
        /* empty (a): нет книг вообще */
        <div className="bk-card" style={{ marginTop: 8 }}>
          <EmptyState
            icon="book"
            title="Добавьте первую книгу"
            text="Она появится здесь, и каждый прочитанный вечер будет в копилке."
            cta="Добавить книгу"
            onCta={() => app.openAdd()}
          />
        </div>
      )}

      {books.length > 0 && !active && (
        /* empty (b): книги есть, активной нет */
        <section>
          <h2 className="bk-title" style={{ margin: '6px 0 4px' }}>Что читаете сейчас?</h2>
          <p className="bk-caption" style={{ margin: '0 0 14px' }}>Выберите книгу из планов — она станет текущей.</p>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
            {planned.slice(0, 4).map((b) => (
              <BookListItem key={b.id} book={b} onClick={() => app.makeActive(b)}
                trailing={<span style={{ color: 'var(--accent-deep)', fontSize: 13, fontWeight: 600, flex: 'none' }}>Начать</span>} />
            ))}
          </div>
        </section>
      )}

      {active && (
        <>
          <BookHeroCard book={active} nudge={nudge && !todayMarked} onMark={app.openProgress} onEdit={app.openEdit} />
          {(others.length > 0 || planned.length > 0) && (
            <section style={{ marginTop: 24 }}>
              <div className="bk-label" style={{ marginBottom: 12 }}>Ещё в чтении и планах</div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 10 }}>
                {others.map((b) => <BookListItem key={b.id} book={b} onClick={() => app.openProgress(b)} />)}
                {planned.slice(0, 2).map((b) => <BookListItem key={b.id} book={b} onClick={() => app.openEdit(b)} />)}
              </div>
            </section>
          )}
        </>
      )}
    </div>
  );
}

/* ═══ STATS / Продуктивность ═══ */
function Heatmap({ period }) {
  const D = BK_DATA;
  if (period === 'Год') {
    const months = D.yearGrid();
    return (
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(4, 1fr)', gap: 8 }}>
        {months.map((m) => (
          <div key={m.label} style={{ textAlign: 'center' }}>
            <div className="bk-heat-cell" data-l={m.level > 0 ? m.level : undefined}
              style={{ aspectRatio: 'auto', height: 40, borderRadius: 10, opacity: m.level < 0 ? 0.45 : 1 }}></div>
            <div className="bk-caption" style={{ fontSize: 11, marginTop: 5 }}>{m.label}</div>
          </div>
        ))}
      </div>
    );
  }
  const cells = D.monthGrid();
  return (
    <div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 5, marginBottom: 6 }}>
        {D.WEEKDAYS_RU.map((w) => (
          <div key={w} className="bk-caption" style={{ fontSize: 10.5, textAlign: 'center' }}>{w}</div>
        ))}
      </div>
      <div style={{ display: 'grid', gridTemplateColumns: 'repeat(7, 1fr)', gap: 5 }}>
        {cells.map((c, i) =>
          c === null
            ? <div key={i}></div>
            : <div key={i} className={'bk-heat-cell' + (c.isToday ? ' today' : '')}
                data-l={c.level > 0 ? c.level : undefined}
                style={c.level < 0 ? { opacity: 0.45, background: 'transparent' } : null}
                title={c.day + ' — ' + Math.max(0, c.pages) + ' стр.'}></div>
        )}
      </div>
      <div className="bk-row" style={{ gap: 5, marginTop: 14, justifyContent: 'flex-end' }}>
        <span className="bk-caption" style={{ fontSize: 11, marginRight: 3 }}>Меньше</span>
        {[0, 1, 2, 3, 4, 5].map((l) => (
          <div key={l} className="bk-heat-cell" data-l={l > 0 ? l : undefined} style={{ width: 13, height: 13, aspectRatio: 'auto' }}></div>
        ))}
        <span className="bk-caption" style={{ fontSize: 11, marginLeft: 3 }}>Больше</span>
      </div>
    </div>
  );
}

function StatsScreen({ app }) {
  const [period, setPeriod] = React.useState('Месяц');
  const { hasHistory, streak } = app.state;
  const D = BK_DATA;
  const finished = app.state.books.filter((b) => b.status === 'finished').length;
  const ms = D.monthSummary();
  const ys = D.yearSummary();
  const isMonth = period === 'Месяц';

  return (
    <div className="bk-screen" data-screen-label="Продуктивность">
      <header style={{ padding: '14px 0 18px' }}>
        <div className="bk-caption" style={{ marginBottom: 3 }}>Статистика чтения</div>
        <h1 className="bk-display" style={{ margin: 0 }}>Продуктивность</h1>
      </header>

      <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: 10 }}>
        <MetricCard empty={!hasHistory} value={finished} label="книг прочитано" />
        <MetricCard empty={!hasHistory} value="3 480" label="страниц прочитано" />
        <MetricCard empty={!hasHistory} value={streak} label="дней без перерывов" />
        <MetricCard empty={!hasHistory} value="48" label="стр. в день в среднем" />
      </div>

      <div style={{ margin: '20px 0 14px' }}>
        <PeriodSwitcher value={period} onChange={setPeriod} />
      </div>

      <section className="bk-card" style={{ padding: 18 }}>
        <div className="bk-row" style={{ marginBottom: 16 }}>
          <div className="bk-title bk-grow" style={{ fontSize: 18 }}>
            {isMonth ? D.MONTHS_RU[D.TODAY.getMonth()] + ' ' + D.TODAY.getFullYear() : D.TODAY.getFullYear() + ' год'}
          </div>
        </div>
        {hasHistory
          ? <Heatmap period={period} />
          : <div style={{ opacity: 0.5, pointerEvents: 'none' }}><Heatmap period={period} /></div>}
      </section>

      {hasHistory ? (
        <section className="bk-card" style={{ padding: 18, marginTop: 12, background: 'var(--card-tint)' }}>
          <div className="bk-label" style={{ marginBottom: 12 }}>{isMonth ? 'Итоги месяца' : 'Итоги года'}</div>
          {isMonth ? (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 9 }}>
              <SummaryRow k="Всего страниц" v={ms.total.toLocaleString('ru-RU')} />
              <SummaryRow k="Лучший день" v={ms.bestDay + ' июня · ' + ms.best + ' стр.'} />
              <SummaryRow k="Лучшая серия" v={ms.bestStreak + ' ' + plural(ms.bestStreak, 'день', 'дня', 'дней')} />
              <SummaryRow k="Скорость" v="~38 стр/час" />
            </div>
          ) : (
            <div style={{ display: 'flex', flexDirection: 'column', gap: 9 }}>
              <SummaryRow k="Всего страниц" v={ys.total.toLocaleString('ru-RU')} />
              <SummaryRow k="Книг закончено" v={finished} />
              <SummaryRow k="Лучший месяц" v={ys.bestMonth} />
            </div>
          )}
        </section>
      ) : (
        <div className="bk-caption" style={{ textAlign: 'center', padding: '22px 30px', textWrap: 'pretty' }}>
          Здесь появится ваша статистика, как только отметите первый прогресс.
        </div>
      )}
    </div>
  );
}

function SummaryRow({ k, v }) {
  return (
    <div className="bk-row" style={{ justifyContent: 'space-between', gap: 12 }}>
      <span className="bk-caption">{k}</span>
      <span style={{ fontSize: 14.5, fontWeight: 600 }}>{v}</span>
    </div>
  );
}

/* ═══ LIBRARY / Библиотека ═══ */
const LIB_FILTERS = ['Все', 'В планах', 'Читаю', 'Прочитано', 'Любимое'];
const FILTER_FN = {
  'Все': () => true,
  'В планах': (b) => b.status === 'planned',
  'Читаю': (b) => b.status === 'reading',
  'Прочитано': (b) => b.status === 'finished',
  'Любимое': (b) => b.fav,
};

/* chips scroller with edge fades */
function FilterChips({ value, onChange }) {
  const ref = React.useRef(null);
  const [fade, setFade] = React.useState({ left: false, right: false });

  const update = React.useCallback(() => {
    const el = ref.current;
    if (!el) return;
    const maxScroll = el.scrollWidth - el.clientWidth;
    setFade({
      left: el.scrollLeft > 4,
      right: maxScroll > 4 && el.scrollLeft < maxScroll - 4,
    });
  }, []);

  React.useEffect(() => {
    update();
    window.addEventListener('resize', update);
    const ro = new ResizeObserver(update);
    if (ref.current) {
      ro.observe(ref.current);
      [...ref.current.children].forEach((c) => ro.observe(c));
    }
    return () => { window.removeEventListener('resize', update); ro.disconnect(); };
  }, [update]);

  const fadeStyle = (side, on) => ({
    position: 'absolute', top: 0, bottom: 16, width: 36, [side]: 0,
    pointerEvents: 'none', zIndex: 2,
    background: 'linear-gradient(to ' + (side === 'left' ? 'right' : 'left') + ', var(--canvas), transparent)',
    opacity: on ? 1 : 0, transition: 'opacity 0.2s ease',
  });

  return (
    <div style={{ position: 'relative' }}>
      <div ref={ref} onScroll={update}
        style={{ display: 'flex', gap: 7, overflowX: 'auto', padding: '2px 20px 16px', scrollbarWidth: 'none' }}>
        {LIB_FILTERS.map((f) => (
          <button key={f} aria-label={f} className={'bk-chip' + (value === f ? ' active' : '')} onClick={() => onChange(f)}>
            {f === 'Любимое' ? <BkIcon name="heart" size={16} stroke={2} /> : f}
          </button>
        ))}
      </div>
      <div style={fadeStyle('left', fade.left)}></div>
      <div style={fadeStyle('right', fade.right)}></div>
    </div>
  );
}

function LibraryScreen({ app }) {
  const [filter, setFilter] = React.useState('Все');
  const { books } = app.state;
  const shown = books.filter(FILTER_FN[filter]);

  return (
    <div className="bk-screen" data-screen-label="Библиотека" style={{ paddingLeft: 0, paddingRight: 0 }}>
      <header style={{ padding: '14px 20px 14px' }}>
        <div className="bk-caption" style={{ marginBottom: 3 }}>
          {books.length === 0 ? 'Пока пусто' : books.length + ' ' + plural(books.length, 'книга', 'книги', 'книг')}
        </div>
        <h1 className="bk-display" style={{ margin: 0 }}>Библиотека</h1>
      </header>

      {books.length === 0 ? (
        <div className="bk-card" style={{ margin: '8px 20px' }}>
          <EmptyState
            icon="book"
            title="Библиотека пуста"
            text="Добавьте книгу — бумажную, электронную, какую угодно."
            cta="Добавить книгу"
            onCta={() => app.openAdd()}
          />
        </div>
      ) : (
        <>
          <FilterChips value={filter} onChange={setFilter} />
          {shown.length === 0 ? (
            <div className="bk-caption" style={{ textAlign: 'center', padding: '40px 30px' }}>В этом фильтре пока ничего нет.</div>
          ) : (
            <div style={{ display: 'grid', gridTemplateColumns: 'minmax(0,1fr) minmax(0,1fr)', gap: 12, padding: '0 20px' }}>
              {shown.map((b) => <LibraryCard key={b.id} book={b} app={app} />)}
            </div>
          )}
        </>
      )}

      <button className="bk-fab" aria-label="Добавить книгу" onClick={() => app.openAdd()}>
        <BkIcon name="plus" size={26} stroke={2.2} />
      </button>
    </div>
  );
}

function LibraryCard({ book, app }) {
  const pct = book.pages ? (book.current / book.pages) * 100 : 0;
  return (
    <div className="bk-gridcard" onClick={() => app.openEdit(book)}>
      <div style={{ position: 'relative' }}>
        <div style={{ display: 'flex', justifyContent: 'center', padding: '8px 0 0' }}>
          <BookCover book={book} width={108} radius={12} />
        </div>
        <div style={{ position: 'absolute', top: 2, right: 2 }}>
          <FavoriteToggle value={book.fav} onChange={(v) => app.setFav(book, v)} size={20} />
        </div>
      </div>
      <div style={{ padding: '0 4px' }}>
        <div style={{ fontSize: 13.5, fontWeight: 600, lineHeight: 1.3, display: '-webkit-box', WebkitLineClamp: 2, WebkitBoxOrient: 'vertical', overflow: 'hidden', minHeight: 35 }}>{book.title}</div>
        <div className="bk-caption" style={{ fontSize: 12, marginTop: 2, overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap' }}>{book.author}</div>
        <div style={{ marginTop: 9 }}>
          {book.status === 'reading' ? <ProgressBar pct={pct} showPct={true} /> : <StatusChip status={book.status} />}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, { HomeScreen, StatsScreen, LibraryScreen, BookListItem, GreetingHeader });
