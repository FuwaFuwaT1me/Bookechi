// Bookechi — Auth: brand mark, Google glyph, welcome variants, action buttons

/* ── Extra line icons (24px grid) not in the base set ── */
function AuthIcon({ name, size = 22, stroke = 1.8, style }) {
  const P = {
    mail: <><rect x="3" y="5" width="18" height="14" rx="3" /><path d="M4 7.5l8 5.2 8-5.2" /></>,
    arrowRight: <><path d="M5 12h13" /><path d="M13 6l6 6-6 6" /></>,
    lock: <><rect x="4.5" y="10.5" width="15" height="10" rx="3" /><path d="M8 10.5V8a4 4 0 0 1 8 0v2.5" /></>,
    eye: <><path d="M2.5 12S6 5.5 12 5.5 21.5 12 21.5 12 18 18.5 12 18.5 2.5 12 2.5 12z" /><circle cx="12" cy="12" r="3" /></>,
    eyeOff: <><path d="M4 4l16 16" /><path d="M9.6 5.9A9.2 9.2 0 0 1 12 5.5c6 0 9.5 6.5 9.5 6.5a16 16 0 0 1-3 3.6" /><path d="M6.4 7.7A15.7 15.7 0 0 0 2.5 12S6 18.5 12 18.5c1.2 0 2.3-.2 3.3-.6" /><path d="M9.9 9.9a3 3 0 0 0 4.2 4.2" /></>,
    cloudOff: <><path d="M3 3l18 18" /><path d="M7 8a5 5 0 0 1 9.5 2 3.5 3.5 0 0 1 1.4 6.4" /><path d="M14.5 18.5H7a4 4 0 0 1-1.2-7.8" /></>,
    device: <><rect x="6.5" y="3" width="11" height="18" rx="2.6" /><path d="M11 18h2" /></>,
    sparkle: <><path d="M12 3l1.7 4.8L18.5 9l-4.8 1.7L12 15.5l-1.7-4.8L5.5 9l4.8-1.7z" /><path d="M18.5 14.5l.7 2 2 .7-2 .7-.7 2-.7-2-2-.7 2-.7z" /></>,
    refresh: <><path d="M20 11a8 8 0 0 0-14.3-4.5M4 5v3.5h3.5" /><path d="M4 13a8 8 0 0 0 14.3 4.5M20 19v-3.5h-3.5" /></>,
    check: <path d="M5 13l4.5 4.5L19 7" />,
  };
  return (
    <svg width={size} height={size} viewBox="0 0 24 24" fill="none" stroke="currentColor"
      strokeWidth={stroke} strokeLinecap="round" strokeLinejoin="round" style={style}>
      {P[name] || null}
    </svg>
  );
}

/* ── Google «G» (official 4-color) ── */
function GoogleG({ size = 18 }) {
  return (
    <svg width={size} height={size} viewBox="0 0 48 48" aria-hidden="true">
      <path fill="#FFC107" d="M43.6 20.5H42V20H24v8h11.3c-1.6 4.7-6.1 8-11.3 8-6.6 0-12-5.4-12-12s5.4-12 12-12c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4 12.9 4 4 12.9 4 24s8.9 20 20 20 20-8.9 20-20c0-1.3-.1-2.6-.4-3.5z" />
      <path fill="#FF3D00" d="M6.3 14.7l6.6 4.8C14.7 16 19 12 24 12c3.1 0 5.9 1.2 8 3.1l5.7-5.7C34.6 6.1 29.6 4 24 4 16.3 4 9.7 8.3 6.3 14.7z" />
      <path fill="#4CAF50" d="M24 44c5.5 0 10.5-2.1 14.3-5.5l-6.6-5.6C29.7 34.6 27 36 24 36c-5.2 0-9.6-3.3-11.3-7.9l-6.5 5C9.6 39.6 16.2 44 24 44z" />
      <path fill="#1976D2" d="M43.6 20.5H42V20H24v8h11.3c-.8 2.3-2.3 4.3-4.3 5.7l6.6 5.6C39.9 38.5 44 32 44 24c0-1.3-.1-2.6-.4-3.5z" />
    </svg>
  );
}

/* ── Stacked-books brand glyph ── */
function StackGlyph({ size = 40, color = 'var(--on-accent)' }) {
  return (
    <svg width={size} height={size} viewBox="0 0 40 40" fill="none">
      {/* three stacked book spines, gently fanned */}
      <g stroke={color} strokeWidth="2.4" strokeLinejoin="round">
        <rect x="8.5" y="24.5" width="23" height="7" rx="2.4" fill="none" />
        <rect x="10" y="16.6" width="21" height="7" rx="2.4" fill="none"
          transform="rotate(-5 20 20)" />
        <rect x="9.5" y="8.8" width="22" height="7" rx="2.4" fill="none"
          transform="rotate(4 20 12)" />
      </g>
      {/* page edge ticks */}
      <g stroke={color} strokeWidth="1.6" strokeLinecap="round" opacity="0.6">
        <path d="M13 28h4" />
        <path d="M14.3 20.2l3.8-.3" transform="rotate(-5 20 20)" />
      </g>
    </svg>
  );
}

/* ── Brand mark tile ── */
function BrandMark({ size = 64, tone = 'accent' }) {
  const light = tone === 'light';
  return (
    <div className={'auth-mark' + (light ? ' light' : '')} style={{ width: size, height: size }}>
      <StackGlyph size={size * 0.6} color={light ? 'var(--accent-deep)' : 'var(--on-accent)'} />
    </div>
  );
}

/* ── Cover-fan hero (variant «Обложки») ──
   Real public-domain classics — safe to display, and a warm, familiar shelf. */
function CoverFan() {
  const covers = [
    { book: { title: 'Анна Каренина', author: 'Л. Толстой', cover: { bg: '#7C8A6E', fg: '#FBF6EF' } }, rot: -15, x: -58, y: 6 },
    { book: { title: 'Преступление и наказание', author: 'Ф. Достоевский', cover: { bg: '#9E4A2C', fg: '#FFF1E6' } }, rot: 15, x: 58, y: 6 },
    { book: { title: 'Евгений Онегин', author: 'А. Пушкин', cover: { bg: '#BE5E3B', fg: '#FFF6EE' } }, rot: 0, x: 0, y: 0 },
  ];
  return (
    <div className="auth-fan" aria-hidden="true">
      {covers.map((c, i) => (
        <div key={i} style={{ transform: `translateX(-50%) translate(${c.x}px, ${c.y}px) rotate(${c.rot}deg)`, zIndex: i === 2 ? 3 : 1 }}>
          <BookCover book={c.book} width={92} radius={11} />
        </div>
      ))}
    </div>
  );
}

/* ═══ Action buttons (3 methods, hierarchy driven by `primary`) ═══ */
function AuthActions({ primary, onGoogle, onEmail, onAnon, busy, copy }) {
  const googlePrimary = primary === 'Google';

  const Google = (
    <button className={'auth-pbtn ' + (googlePrimary ? 'fill' : 'outline')}
      onClick={onGoogle} disabled={busy} data-screen-label="Кнопка Google">
      <span className="auth-glyph">
        {googlePrimary
          ? <span className="auth-gchip"><GoogleG size={18} /></span>
          : <GoogleG size={20} />}
      </span>
      {copy.google}
    </button>
  );

  const Email = (
    <button className={'auth-pbtn ' + (googlePrimary ? 'outline' : 'fill')}
      onClick={onEmail} disabled={busy}>
      <span className="auth-glyph">
        <AuthIcon name="mail" size={20} stroke={1.9} />
      </span>
      {copy.email}
    </button>
  );

  const Anon = (
    <button className="auth-textbtn" onClick={onAnon} disabled={busy}>
      {copy.anon}
      <span className="auth-chev"><AuthIcon name="arrowRight" size={17} stroke={2.1} /></span>
    </button>
  );

  return (
    <div className="auth-stack">
      {googlePrimary ? <>{Google}{Email}</> : <>{Email}{Google}</>}
      {Anon}
    </div>
  );
}

/* ═══════════ WELCOME VARIANTS ═══════════ */

/* A — «Тёплый минимал»: centered mark, calm copy */
function WelcomeMinimal({ primary, actions }) {
  return (
    <div className="auth-screen auth-pad" data-screen-label="Приветствие · Тёплый минимал">
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', paddingTop: 32 }}>
        <div className="auth-rise"><BrandMark size={72} /></div>
        <h1 className="auth-word auth-rise" style={{ fontSize: 34, margin: '22px 0 0' }}>Bookechi</h1>
        <p className="bk-body auth-rise-2" style={{ color: 'var(--text2)', margin: '10px 0 0', maxWidth: 268, textWrap: 'pretty' }}>
          Тёплый дневник чтения. Отмечайте страницы, держите серию, любите книги.
        </p>
      </div>
      <div className="auth-rise-3" style={{ paddingBottom: 22 }}>
        <AuthActions primary={primary} {...actions}
          copy={{ google: 'Продолжить с Google', email: 'Войти по почте', anon: 'Зайти без аккаунта' }} />
        <div style={{ height: 18 }}></div>
        <Legal />
      </div>
    </div>
  );
}

/* B — «Обложки»: fanned cover stack hero */
function WelcomeCovers({ primary, actions }) {
  return (
    <div className="auth-screen auth-pad" data-screen-label="Приветствие · Обложки">
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center', paddingTop: 28 }}>
        <div className="auth-rise" style={{
          padding: '20px 0 6px', width: '100%',
          background: 'radial-gradient(115% 78% at 50% 30%, var(--accent-soft), transparent 70%)',
          borderRadius: 'var(--r-hero)',
        }}>
          <CoverFan />
        </div>
        <h1 className="auth-word auth-rise-2" style={{ fontSize: 29, margin: '24px 0 0' }}>Ваша полка ждёт</h1>
        <p className="bk-body auth-rise-2" style={{ color: 'var(--text2)', margin: '9px 0 0', maxWidth: 272, textWrap: 'pretty' }}>
          Соберите книги, которые читаете и любите — Bookechi сохранит каждую страницу.
        </p>
      </div>
      <div className="auth-rise-3" style={{ paddingBottom: 22 }}>
        <AuthActions primary={primary} {...actions}
          copy={{ google: 'Продолжить с Google', email: 'Войти по почте', anon: 'Сначала осмотреться' }} />
        <div style={{ height: 18 }}></div>
        <Legal />
      </div>
    </div>
  );
}

/* C — «Редакторский»: top-aligned big serif headline, text-forward */
function WelcomeEditorial({ primary, actions }) {
  return (
    <div className="auth-screen auth-pad" data-screen-label="Приветствие · Редакторский">
      <div style={{ flex: 1, display: 'flex', flexDirection: 'column', justifyContent: 'center', paddingTop: 40 }}>
        <div className="bk-row auth-rise" style={{ gap: 12 }}>
          <BrandMark size={42} />
          <span className="auth-word" style={{ fontSize: 19 }}>Bookechi</span>
        </div>
        <h1 className="auth-word auth-rise" style={{ fontSize: 40, lineHeight: 1.08, margin: '30px 0 0', maxWidth: 320 }}>
          Читайте больше.<br />Без давления.
        </h1>
        <p className="bk-body auth-rise-2" style={{ color: 'var(--text2)', margin: '16px 0 0', maxWidth: 300, textWrap: 'pretty' }}>
          Спокойный трекер чтения, который радуется каждой странице, а не отчитывает вас за пропуск.
        </p>
      </div>
      <div className="auth-rise-3" style={{ paddingBottom: 22 }}>
        <AuthActions primary={primary} {...actions}
          copy={{ google: 'Начать с Google', email: 'Войти по почте', anon: 'Попробовать без аккаунта' }} />
        <div style={{ height: 18 }}></div>
        <Legal />
      </div>
    </div>
  );
}

/* D — «Дуга»: branded terracotta dome top, actions on card below */
function WelcomeDome({ primary, actions }) {
  return (
    <div className="auth-screen" data-screen-label="Приветствие · Дуга">
      <div className="auth-dome auth-rise" style={{ textAlign: 'center' }}>
        <div style={{ display: 'flex', justifyContent: 'center' }}><BrandMark size={66} tone="light" /></div>
        <h1 className="auth-word" style={{ fontSize: 32, margin: '20px 0 0' }}>Bookechi</h1>
        <p className="bk-body" style={{ margin: '10px auto 0', maxWidth: 250, color: 'var(--on-accent)', opacity: 0.92, textWrap: 'pretty' }}>
          Добро пожаловать домой, к своим книгам.
        </p>
        <div className="auth-dome-deco"><StackGlyph size={150} color="#fff" /></div>
      </div>
      <div style={{ flex: 1 }}></div>
      <div className="auth-pad auth-rise-3" style={{ paddingBottom: 24, paddingTop: 28 }}>
        <p className="bk-caption" style={{ textAlign: 'center', margin: '0 0 16px', fontWeight: 600, letterSpacing: '0.02em' }}>
          Выберите, как продолжить
        </p>
        <AuthActions primary={primary} {...actions}
          copy={{ google: 'Продолжить с Google', email: 'Войти по почте', anon: 'Зайти без аккаунта' }} />
        <div style={{ height: 18 }}></div>
        <Legal />
      </div>
    </div>
  );
}

function Legal() {
  return (
    <p className="auth-legal">
      Продолжая, вы соглашаетесь с <a href="#" onClick={(e) => e.preventDefault()}>Условиями</a> и{' '}
      <a href="#" onClick={(e) => e.preventDefault()}>Политикой конфиденциальности</a>.
    </p>
  );
}

const WELCOME_VARIANTS = {
  'Тёплый минимал': WelcomeMinimal,
  'Обложки': WelcomeCovers,
  'Редакторский': WelcomeEditorial,
  'Дуга': WelcomeDome,
};

Object.assign(window, {
  AuthIcon, GoogleG, StackGlyph, BrandMark, CoverFan, AuthActions, Legal, WELCOME_VARIANTS,
});
