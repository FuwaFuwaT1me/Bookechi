// Bookechi — app shell: state, router, overlays, theme, tweaks, device frame
const { useState: useAState, useEffect: useAEffect } = React;

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "dark": false,
  "serif": "Lora",
  "scenario": "Наполненный",
  "radius": 24,
  "goalStyle": "Полоса"
}/*EDITMODE-END*/;

const SERIF_STACKS = {
  'Lora': "'Lora', Georgia, serif",
  'Source Serif 4': "'Source Serif 4', Georgia, serif",
  'Literata': "'Literata', Georgia, serif",
};

function useScale() {
  const [scale, setScale] = useAState(1);
  useAEffect(() => {
    const fit = () => {
      const s = Math.min(1, (window.innerHeight - 40) / 892, (window.innerWidth - 32) / 412);
      setScale(Math.max(0.4, s));
    };
    fit();
    window.addEventListener('resize', fit);
    return () => window.removeEventListener('resize', fit);
  }, []);
  return scale;
}

function App() {
  const [t, setTweak] = useTweaks(TWEAK_DEFAULTS);
  const [state, setState] = useAState(() => BK_DATA.SCENARIOS['Наполненный']());
  const [tab, setTab] = useAState('home');
  const [overlay, setOverlay] = useAState(null);
  const [bookCardId, setBookCardId] = useAState(null);
  const [reminder, setReminder] = useAState({ on: true, time: '21:00' });
  const scale = useScale();

  // scenario switch resets demo data
  useAEffect(() => {
    const make = BK_DATA.SCENARIOS[t.scenario];
    if (make) { setState(make()); setOverlay(null); setBookCardId(null); }
  }, [t.scenario]);

  const patchBooks = (fn) => setState((s) => ({ ...s, books: fn(s.books) }));

  const makeActive = (book) => {
    patchBooks((books) => books.map((b) =>
      b.id === book.id
        ? { ...b, status: 'reading', active: true, current: b.current || 0 }
        : { ...b, active: false }
    ));
  };

  const app = {
    state, tab, overlay, bookCardId, reminder,
    dark: t.dark,
    goalStyle: t.goalStyle || 'Полоса',
    toggleTheme: () => setTweak('dark', !t.dark),
    setTab, setReminder,
    closeOverlay: () => setOverlay(null),

    openSearch: () => setOverlay({ type: 'search' }),
    openAdd: () => setOverlay({ type: 'add', prefill: null }),
    pickSearchResult: (r) => setOverlay({ type: 'add', prefill: r }),
    openEdit: (book) => setOverlay({ type: 'edit', book }),
    openProgress: (book) => setOverlay({ type: 'progress', book }),
    openReminder: () => setOverlay({ type: 'reminder' }),
    openGoal: () => setOverlay({ type: 'goal' }),

    openBook: (book) => setBookCardId(book.id),
    closeBook: () => setBookCardId(null),

    makeActive,
    startReading: (book) => {
      makeActive(book);
      setOverlay({ type: 'progress', book: { ...book, status: 'reading', current: book.current || 0 } });
    },

    setFav: (book, v) => patchBooks((books) => books.map((b) => (b.id === book.id ? { ...b, fav: v } : b))),

    saveGoal: (g) => {
      setState((s) => ({
        ...s,
        goal: { type: g.type, value: g.value, done: g.type === s.goal.type ? Math.min(s.goal.done, g.value) : 0 },
      }));
      setOverlay(null);
    },

    saveBook: (data) => {
      if (data.id) {
        patchBooks((books) => books.map((b) => (b.id === data.id
          ? { ...b, ...data, tone: undefined, active: data.status === 'reading' ? b.active : false }
          : b)));
      } else {
        const tone = data.tone != null
          ? BK_DATA.COVER_TONES[data.tone]
          : BK_DATA.COVER_TONES[(state.books.length * 5 + 2) % BK_DATA.COVER_TONES.length];
        const hasActive = state.books.some((b) => b.active && b.status === 'reading');
        const nb = {
          ...data, tone: undefined, id: 'u' + Date.now(), cover: tone, rating: null, note: '', quotes: [],
          active: data.status === 'reading' && !hasActive,
        };
        patchBooks((books) => [nb, ...books]);
      }
      setOverlay(null);
    },

    deleteBook: (book) => {
      patchBooks((books) => books.filter((b) => b.id !== book.id));
      setOverlay(null);
      if (bookCardId === book.id) setBookCardId(null);
    },

    quickLog: (book, n) => {
      const end = Math.min(book.current + n, book.pages);
      app.saveProgress(book, end, null);
    },

    saveProgress: (book, end, mins) => {
      const before = Math.round((book.current / book.pages) * 100);
      const after = Math.round((end / book.pages) * 100);
      const delta = end - book.current;
      const finished = end >= book.pages;
      const newStreak = state.todayMarked ? state.streak : (state.comeback ? 1 : state.streak + 1);
      patchBooks((books) => books.map((b) => (b.id === book.id
        ? { ...b, current: end, status: finished ? 'finished' : b.status, active: finished ? false : b.active }
        : b)));
      setState((s) => {
        const goal = { ...s.goal };
        if (goal.type === 'days') { if (!s.todayMarked) goal.done = Math.min(goal.value, goal.done + 1); }
        else goal.done = Math.min(goal.value, goal.done + delta);
        return {
          ...s, streak: newStreak, todayMarked: true, nudge: false, hasHistory: true,
          comeback: false, todayDelta: (s.todayDelta || 0) + delta, goal,
        };
      });
      setOverlay({ type: 'success', data: { book, delta, before, after, streak: newStreak, finished, mins } });
    },

    finishSuccess: (d, rating, note) => {
      if (d.finished) {
        patchBooks((books) => {
          let next = books.map((b) => (b.id === d.book.id ? { ...b, rating: rating || null, note } : b));
          if (!next.some((b) => b.active && b.status === 'reading')) {
            const candidate = next.find((b) => b.status === 'reading');
            if (candidate) next = next.map((b) => ({ ...b, active: b.id === candidate.id }));
          }
          return next;
        });
      }
      setOverlay(null);
    },
  };

  // dev hooks for programmatic state-driving (screenshots, verification)
  window.__bkApp = app;
  window.__bkSetTweak = setTweak;
  window.__bkReset = (name) => {
    const make = BK_DATA.SCENARIOS[name];
    if (make) { setState(make()); setOverlay(null); setBookCardId(null); }
  };

  const radius = t.radius || 24;
  const rootVars = {
    height: '100%',
    '--serif': SERIF_STACKS[t.serif] || SERIF_STACKS['Lora'],
    '--r-card': radius + 'px',
    '--r-hero': (radius + 6) + 'px',
    '--r-btn': Math.max(14, radius - 6) + 'px',
  };

  return (
    <div style={{
      minHeight: '100vh', display: 'flex', alignItems: 'center', justifyContent: 'center',
      background: t.dark ? '#120D09' : '#E7DCCC',
    }}>
      <div style={{ transform: 'scale(' + scale + ')', flex: 'none' }}>
        <AndroidDevice width={412} height={892} dark={t.dark} bg={t.dark ? '#1C1611' : '#F4ECE1'}>
          <div className="bk-root" data-theme={t.dark ? 'dark' : 'light'} style={rootVars}>
            <div className="bk-phone">
              {tab === 'home' && <HomeScreen app={app} />}
              {tab === 'stats' && <StatsScreen app={app} />}
              {tab === 'library' && <LibraryScreen app={app} />}
              <BottomNav tab={tab} onTab={setTab} />
              <BookCardScreen app={app} />
              <BookFormSheet app={app} />
              <SearchSheet app={app} />
              <ReminderSheet app={app} />
              <GoalSheet app={app} />
              <ProgressScreen app={app} />
              <SuccessScreen app={app} />
            </div>
          </div>
        </AndroidDevice>
      </div>

      <TweaksPanel>
        <TweakSection label="Демо-сценарий" />
        <TweakSelect label="Данные" value={t.scenario}
          options={['Наполненный', 'Без отметки сегодня', 'Возвращение', 'Нет активной книги', 'Пустая библиотека']}
          onChange={(v) => setTweak('scenario', v)} />
        <TweakSection label="Блок цели" />
        <TweakRadio label="Стиль" value={t.goalStyle || 'Полоса'}
          options={['Полоса', 'Кольцо', 'Минимум']}
          onChange={(v) => setTweak('goalStyle', v)} />
        <TweakSection label="Тема" />
        <TweakToggle label="Тёмная тема" value={t.dark} onChange={(v) => setTweak('dark', v)} />
        <TweakSelect label="Шрифт заголовков" value={t.serif}
          options={['Lora', 'Source Serif 4', 'Literata']}
          onChange={(v) => setTweak('serif', v)} />
        <TweakSlider label="Радиус карточек" value={radius} min={16} max={32} step={2} unit="px"
          onChange={(v) => setTweak('radius', v)} />
      </TweaksPanel>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<App />);
