// Bookechi — app shell: state, router, overlays, theme, tweaks, device frame
const { useState: useAState, useEffect: useAEffect } = React;

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "dark": false,
  "serif": "Lora",
  "scenario": "Наполненный",
  "radius": 24
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
  const scale = useScale();

  // scenario switch resets demo data
  useAEffect(() => {
    const make = BK_DATA.SCENARIOS[t.scenario];
    if (make) { setState(make()); setOverlay(null); }
  }, [t.scenario]);

  const patchBooks = (fn) => setState((s) => ({ ...s, books: fn(s.books) }));

  const app = {
    state, tab, overlay,
    dark: t.dark,
    toggleTheme: () => setTweak('dark', !t.dark),
    setTab,
    closeOverlay: () => setOverlay(null),
    openAdd: () => setOverlay({ type: 'add' }),
    openEdit: (book) => setOverlay({ type: 'edit', book }),
    openProgress: (book) => setOverlay({ type: 'progress', book }),

    makeActive: (book) => {
      patchBooks((books) => books.map((b) =>
        b.id === book.id
          ? { ...b, status: 'reading', active: true, current: b.current || 0 }
          : { ...b, active: false }
      ));
    },

    setFav: (book, v) => patchBooks((books) => books.map((b) => (b.id === book.id ? { ...b, fav: v } : b))),

    saveBook: (data) => {
      if (data.id) {
        patchBooks((books) => books.map((b) => (b.id === data.id
          ? { ...b, ...data, active: data.status === 'reading' ? b.active : false }
          : b)));
      } else {
        const tone = BK_DATA.COVER_TONES[(state.books.length * 5 + 2) % BK_DATA.COVER_TONES.length];
        const hasActive = state.books.some((b) => b.active && b.status === 'reading');
        const nb = {
          ...data, id: 'u' + Date.now(), cover: tone, rating: null, note: '',
          active: data.status === 'reading' && !hasActive,
        };
        patchBooks((books) => [nb, ...books]);
        setState((s) => ({ ...s, hasHistory: s.hasHistory }));
      }
      setOverlay(null);
    },

    deleteBook: (book) => {
      patchBooks((books) => books.filter((b) => b.id !== book.id));
      setOverlay(null);
    },

    saveProgress: (book, end, mins) => {
      const before = Math.round((book.current / book.pages) * 100);
      const after = Math.round((end / book.pages) * 100);
      const delta = end - book.current;
      const finished = end >= book.pages;
      const newStreak = state.todayMarked ? state.streak : state.streak + 1;
      patchBooks((books) => books.map((b) => (b.id === book.id
        ? { ...b, current: end, status: finished ? 'finished' : b.status, active: finished ? false : b.active }
        : b)));
      setState((s) => ({ ...s, streak: newStreak, todayMarked: true, nudge: false, hasHistory: true }));
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
  window.__bkReset = (name) => {
    const make = BK_DATA.SCENARIOS[name];
    if (make) { setState(make()); setOverlay(null); }
  };

  const radius = t.radius || 24;

  // dev hook for programmatic state driving (screenshots/tests)
  React.useEffect(() => {
    window.__bk = {
      app, setTweak, setTab,
      reset: (sc) => { const m = BK_DATA.SCENARIOS[sc]; if (m) setState(m()); setOverlay(null); },
    };
  });
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
              <BookFormSheet app={app} />
              <ProgressScreen app={app} />
              <SuccessScreen app={app} />
            </div>
          </div>
        </AndroidDevice>
      </div>

      <TweaksPanel>
        <TweakSection label="Демо-сценарий" />
        <TweakSelect label="Данные" value={t.scenario}
          options={['Наполненный', 'Без отметки сегодня', 'Нет активной книги', 'Пустая библиотека']}
          onChange={(v) => setTweak('scenario', v)} />
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
