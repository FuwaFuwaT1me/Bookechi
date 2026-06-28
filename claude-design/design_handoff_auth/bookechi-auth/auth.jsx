// Bookechi — Auth shell: state machine, scaling, device frame, tweaks

const { useState: useAuState, useEffect: useAuEffect } = React;

const TWEAK_DEFAULTS = /*EDITMODE-BEGIN*/{
  "variant": "Обложки",
  "primary": "Google",
  "dark": false,
  "serif": "Lora",
  "radius": 24
}/*EDITMODE-END*/;

const AUTH_SERIF = {
  'Lora': "'Lora', Georgia, serif",
  'Source Serif 4': "'Source Serif 4', Georgia, serif",
  'Literata': "'Literata', Georgia, serif",
};

function useAuthScale() {
  const [scale, setScale] = useAuState(1);
  useAuEffect(() => {
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

function AuthApp() {
  const [t, setTweak] = useTweaks(TWEAK_DEFAULTS);
  const [flow, setFlow] = useAuState(null);        // null | email | anon | connecting | done
  const [via, setVia] = useAuState(null);          // google | email | anon
  const [emailMode, setEmailMode] = useAuState('signin');
  const scale = useAuthScale();

  // Google connecting → done
  useAuEffect(() => {
    if (flow !== 'connecting') return;
    const id = setTimeout(() => setFlow('done'), 1150);
    return () => clearTimeout(id);
  }, [flow]);

  const app = {
    flow, via, emailMode,
    primary: t.primary,
    startGoogle: () => { setVia('google'); setFlow('connecting'); },
    openEmail: (mode = 'signin') => { setEmailMode(mode); setFlow('email'); },
    openAnon: () => setFlow('anon'),
    closeFlow: () => setFlow(null),
    finish: (v) => { setVia(v); setFlow('done'); },
    reset: () => { setFlow(null); setVia(null); },
  };

  // dev hook for verification / screenshots
  window.__authApp = app;
  window.__authSetTweak = setTweak;

  const radius = t.radius || 24;
  const rootVars = {
    height: '100%',
    '--serif': AUTH_SERIF[t.serif] || AUTH_SERIF['Lora'],
    '--r-card': radius + 'px',
    '--r-hero': (radius + 6) + 'px',
    '--r-btn': Math.max(14, radius - 6) + 'px',
  };

  const Welcome = WELCOME_VARIANTS[t.variant] || WelcomeMinimal;
  const actions = {
    onGoogle: app.startGoogle,
    onEmail: () => app.openEmail('signin'),
    onAnon: app.openAnon,
    busy: false,
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
              <Welcome key={t.variant} primary={t.primary} actions={actions} />
              <EmailScreen app={app} />
              <AnonSheet app={app} />
              <ConnectingOverlay app={app} />
              <SuccessScreen app={app} />
            </div>
          </div>
        </AndroidDevice>
      </div>

      <TweaksPanel title="Tweaks">
        <TweakSection label="Экран приветствия" />
        <TweakSelect label="Вариант" value={t.variant}
          options={['Тёплый минимал', 'Обложки', 'Редакторский', 'Дуга']}
          onChange={(v) => setTweak('variant', v)} />
        <TweakRadio label="Главная кнопка" value={t.primary}
          options={['Google', 'Почта']}
          onChange={(v) => setTweak('primary', v)} />
        <TweakSection label="Оформление" />
        <TweakToggle label="Тёмная тема" value={t.dark} onChange={(v) => setTweak('dark', v)} />
        <TweakSelect label="Шрифт заголовков" value={t.serif}
          options={['Lora', 'Source Serif 4', 'Literata']}
          onChange={(v) => setTweak('serif', v)} />
        <TweakSlider label="Радиус" value={radius} min={16} max={32} step={2} unit="px"
          onChange={(v) => setTweak('radius', v)} />
      </TweaksPanel>
    </div>
  );
}

ReactDOM.createRoot(document.getElementById('root')).render(<AuthApp />);
