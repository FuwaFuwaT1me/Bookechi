// Bookechi — Auth flows: email (вход/регистрация/восстановление), anonymous sheet, success

const { useState: useFlState, useEffect: useFlEffect } = React;

const EMAIL_RE = /^\S+@\S+\.\S+$/;

/* ── Password field with show/hide ── */
function AuthPassword({ label, value, onChange, error, hint, placeholder }) {
  const [show, setShow] = useFlState(false);
  return (
    <div className="bk-field">
      <label>{label}</label>
      <div style={{ position: 'relative' }}>
        <input
          className={'bk-input' + (error ? ' error' : '')}
          type={show ? 'text' : 'password'}
          value={value} placeholder={placeholder || '••••••••'}
          onChange={(e) => onChange(e.target.value)}
          style={{ paddingRight: 48 }}
        />
        <button type="button" aria-label={show ? 'Скрыть пароль' : 'Показать пароль'}
          onClick={() => setShow((s) => !s)}
          style={{
            position: 'absolute', right: 6, top: '50%', transform: 'translateY(-50%)',
            width: 38, height: 38, borderRadius: 10, display: 'grid', placeItems: 'center',
            color: 'var(--text2)',
          }}>
          <AuthIcon name={show ? 'eyeOff' : 'eye'} size={20} />
        </button>
      </div>
      {error
        ? <span className="bk-err">{error}</span>
        : hint ? <span className="bk-caption" style={{ fontSize: 12 }}>{hint}</span> : null}
    </div>
  );
}

/* ── small inline Google button (used inside email screen) ── */
function GoogleInlineBtn({ onClick, busy, label = 'Продолжить с Google' }) {
  return (
    <button className="auth-pbtn outline" onClick={onClick} disabled={busy}>
      <span className="auth-glyph"><GoogleG size={20} /></span>
      {label}
    </button>
  );
}

/* ═══ EMAIL SCREEN (push) — вход / регистрация / восстановление ═══ */
function EmailScreen({ app }) {
  const open = app.flow === 'email';
  const [mode, setMode] = useFlState('signin'); // signin | register | recover | sent
  const [f, setF] = useFlState({ name: '', email: '', pass: '' });
  const [errs, setErrs] = useFlState({});
  const [busy, setBusy] = useFlState(false);

  // reset when the screen opens (honors the mode the caller requested)
  useFlEffect(() => {
    if (open) {
      setMode(app.emailMode || 'signin');
      setF({ name: '', email: '', pass: '' });
      setErrs({}); setBusy(false);
    }
  }, [open, app.emailMode]);

  const set = (k) => (v) => { setF((p) => ({ ...p, [k]: v })); setErrs((p) => ({ ...p, [k]: null })); };

  function submitAuth() {
    const e = {};
    if (!EMAIL_RE.test(f.email.trim())) e.email = 'Проверьте адрес — кажется, есть опечатка';
    if (f.pass.length < 6) e.pass = 'Минимум 6 символов';
    setErrs(e);
    if (Object.keys(e).length) return;
    setBusy(true);
    setTimeout(() => app.finish('email'), 720);
  }

  function submitRecover() {
    if (!EMAIL_RE.test(f.email.trim())) { setErrs({ email: 'Укажите почту, на которую отправить ссылку' }); return; }
    setBusy(true);
    setTimeout(() => { setBusy(false); setMode('sent'); }, 640);
  }

  const isRegister = mode === 'register';
  const titles = { signin: 'Вход', register: 'Создать аккаунт', recover: 'Сброс пароля', sent: 'Письмо отправлено' };

  return (
    <div className={'bk-push top' + (open ? ' open' : '')} data-screen-label={'Почта · ' + titles[mode]}>
      {/* header */}
      <div className="auth-head">
        <button className="bk-iconbtn" aria-label="Назад"
          onClick={() => { if (mode === 'recover' || mode === 'sent') setMode('signin'); else app.closeFlow(); }}>
          <BkIcon name="back" size={22} />
        </button>
        <h2 className="bk-title bk-grow" style={{ margin: 0 }}>{titles[mode]}</h2>
        <BrandMark size={36} />
      </div>

      {/* ── вход / регистрация ── */}
      {(mode === 'signin' || mode === 'register') && (
        <>
          <div className="bk-seg" style={{ marginBottom: 22 }}>
            <button className={mode === 'signin' ? 'active' : ''} onClick={() => { setMode('signin'); setErrs({}); }}>Вход</button>
            <button className={isRegister ? 'active' : ''} onClick={() => { setMode('register'); setErrs({}); }}>Регистрация</button>
          </div>

          <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
            {isRegister && (
              <WarmTextField label="Имя" value={f.name} onChange={set('name')} placeholder="Как к вам обращаться" />
            )}
            <WarmTextField label="Электронная почта" type="email" inputMode="email"
              value={f.email} onChange={set('email')} placeholder="you@example.com" error={errs.email} />
            <AuthPassword label="Пароль" value={f.pass} onChange={set('pass')} error={errs.pass}
              hint={isRegister ? 'Минимум 6 символов' : null} />

            {mode === 'signin' && (
              <div style={{ display: 'flex', justifyContent: 'flex-end', marginTop: -4 }}>
                <button className="auth-link" onClick={() => { setMode('recover'); setErrs({}); }}>Забыли пароль?</button>
              </div>
            )}

            <button className="auth-pbtn fill" onClick={submitAuth} disabled={busy} style={{ marginTop: 2 }}>
              {busy ? <span className="auth-spin"></span> : (isRegister ? 'Создать аккаунт' : 'Войти')}
            </button>
          </div>

          {isRegister && (
            <p className="auth-legal" style={{ marginTop: 16 }}>
              Регистрируясь, вы принимаете <a href="#" onClick={(e) => e.preventDefault()}>Условия</a> и{' '}
              <a href="#" onClick={(e) => e.preventDefault()}>Политику конфиденциальности</a>.
            </p>
          )}

          <div className="auth-or" style={{ margin: '22px 0' }}>или</div>
          <GoogleInlineBtn onClick={() => app.startGoogle()} busy={busy} />

          <p className="bk-caption" style={{ textAlign: 'center', marginTop: 22 }}>
            {isRegister ? 'Уже есть аккаунт? ' : 'Ещё нет аккаунта? '}
            <button className="auth-link" onClick={() => { setMode(isRegister ? 'signin' : 'register'); setErrs({}); }}>
              {isRegister ? 'Войти' : 'Создать'}
            </button>
          </p>
        </>
      )}

      {/* ── восстановление пароля ── */}
      {mode === 'recover' && (
        <div style={{ display: 'flex', flexDirection: 'column', gap: 16 }}>
          <p className="bk-body" style={{ color: 'var(--text2)', margin: '0 0 4px', textWrap: 'pretty' }}>
            Укажите почту от аккаунта — пришлём ссылку, чтобы задать новый пароль.
          </p>
          <WarmTextField label="Электронная почта" type="email" inputMode="email"
            value={f.email} onChange={set('email')} placeholder="you@example.com" error={errs.email} />
          <button className="auth-pbtn fill" onClick={submitRecover} disabled={busy}>
            {busy ? <span className="auth-spin"></span> : 'Отправить ссылку'}
          </button>
          <button className="auth-textbtn" onClick={() => { setMode('signin'); setErrs({}); }} style={{ color: 'var(--accent-deep)' }}>
            Вернуться ко входу
          </button>
        </div>
      )}

      {/* ── письмо отправлено ── */}
      {mode === 'sent' && (
        <div style={{ textAlign: 'center', padding: '26px 8px 0', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
          <div style={{
            width: 76, height: 76, borderRadius: 24, display: 'grid', placeItems: 'center',
            background: 'var(--accent-soft)', color: 'var(--accent-deep)', marginBottom: 18,
          }}>
            <AuthIcon name="mail" size={34} stroke={1.7} />
          </div>
          <h3 className="bk-title" style={{ margin: 0 }}>Проверьте почту</h3>
          <p className="bk-body" style={{ color: 'var(--text2)', margin: '10px 0 24px', maxWidth: 280, textWrap: 'pretty' }}>
            Отправили ссылку для сброса на <b style={{ color: 'var(--text)' }}>{f.email.trim()}</b>. Перейдите по ней, чтобы задать новый пароль.
          </p>
          <button className="auth-pbtn outline" onClick={() => app.closeFlow()} style={{ maxWidth: 260 }}>
            Открыть почтовое приложение
          </button>
          <button className="auth-textbtn" onClick={() => { setMode('signin'); setErrs({}); }} style={{ color: 'var(--accent-deep)', marginTop: 6 }}>
            Вернуться ко входу
          </button>
        </div>
      )}
    </div>
  );
}

/* ═══ ANONYMOUS SHEET — limitations before entering ═══ */
function AnonSheet({ app }) {
  const open = app.flow === 'anon';
  return (
    <>
      <div className={'bk-scrim' + (open ? ' open' : '')} onClick={() => app.closeFlow()}></div>
      <div className={'bk-sheet' + (open ? ' open' : '')} data-screen-label="Анонимный вход · ограничения">
        <div className="bk-sheet-grab"></div>
        <div className="bk-sheet-body">
          <div style={{ display: 'flex', alignItems: 'center', gap: 13, padding: '6px 0 14px' }}>
            <div className="auth-limit-ic" style={{ width: 46, height: 46, borderRadius: 15, background: 'var(--accent-soft)', color: 'var(--accent-deep)' }}>
              <AuthIcon name="device" size={24} />
            </div>
            <div className="bk-grow">
              <h2 className="bk-title" style={{ margin: 0 }}>Зайти без аккаунта</h2>
              <p className="bk-caption" style={{ marginTop: 2 }}>Можно начать прямо сейчас — вот что важно знать</p>
            </div>
          </div>

          <div style={{ margin: '6px 0 22px' }}>
            <div className="auth-limit">
              <div className="auth-limit-ic warn"><AuthIcon name="cloudOff" size={20} /></div>
              <div>
                <div style={{ fontSize: 14.5, fontWeight: 600 }}>Нет синхронизации и бэкапа</div>
                <p className="bk-caption" style={{ marginTop: 2, textWrap: 'pretty' }}>Книги и серия чтения хранятся только на этом телефоне.</p>
              </div>
            </div>
            <div className="auth-limit">
              <div className="auth-limit-ic warn"><AuthIcon name="refresh" size={20} /></div>
              <div>
                <div style={{ fontSize: 14.5, fontWeight: 600 }}>Прогресс не перенесётся</div>
                <p className="bk-caption" style={{ marginTop: 2, textWrap: 'pretty' }}>При удалении приложения или смене устройства данные потеряются.</p>
              </div>
            </div>
            <div className="auth-limit">
              <div className="auth-limit-ic ok"><AuthIcon name="sparkle" size={20} /></div>
              <div>
                <div style={{ fontSize: 14.5, fontWeight: 600 }}>Аккаунт можно создать позже</div>
                <p className="bk-caption" style={{ marginTop: 2, textWrap: 'pretty' }}>Всё, что уже прочитано, сохранится и переедет в облако.</p>
              </div>
            </div>
          </div>

          <div className="auth-stack">
            <button className="auth-pbtn fill" onClick={() => app.finish('anon')}>Продолжить без аккаунта</button>
            <button className="bk-btn bk-btn-secondary" onClick={() => app.openEmail('register')}>Лучше создать аккаунт</button>
          </div>
          <div style={{ height: 6 }}></div>
        </div>
      </div>
    </>
  );
}

/* ═══ CONNECTING (Google) overlay ═══ */
function ConnectingOverlay({ app }) {
  const open = app.flow === 'connecting';
  return (
    <div className={'bk-fade' + (open ? ' open' : '')} style={{ background: 'var(--canvas)' }}
      data-screen-label="Подключение Google">
      <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', gap: 22, padding: 24 }}>
        <div style={{ position: 'relative', display: 'grid', placeItems: 'center' }}>
          <div className="auth-spin accent" style={{ width: 64, height: 64, borderWidth: 3 }}></div>
          <span style={{ position: 'absolute' }}><GoogleG size={26} /></span>
        </div>
        <div style={{ textAlign: 'center' }}>
          <div className="bk-title" style={{ fontSize: 19 }}>Подключаем Google…</div>
          <p className="bk-caption" style={{ marginTop: 6 }}>Секунду — открываем безопасный вход</p>
        </div>
      </div>
    </div>
  );
}

/* ═══ SUCCESS / boot ═══ */
const DONE_COPY = {
  google: { title: 'Вы вошли', sub: 'Аккаунт Google подключён. Рады видеть вас в Bookechi.' },
  email: { title: 'Добро пожаловать', sub: 'Аккаунт готов — ваша полка ждёт первую отметку.' },
  anon: { title: 'Готово', sub: 'Вы зашли без аккаунта. Прогресс хранится на этом устройстве.' },
};

function SuccessScreen({ app }) {
  const open = app.flow === 'done';
  const via = app.via || 'google';
  const c = DONE_COPY[via];
  return (
    <div className={'bk-fade' + (open ? ' open' : '')} style={{ background: 'var(--canvas)' }}
      data-screen-label="Готово · вход выполнен">
      <div style={{ position: 'absolute', inset: 0, display: 'flex', flexDirection: 'column', padding: '0 24px 28px' }}>
        <div style={{ flex: 1, display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center', textAlign: 'center' }}>
          <div className={open ? 'auth-seal bk-pop' : 'auth-seal'}>
            <AuthIcon name="check" size={42} stroke={2.6} />
          </div>
          <h1 className="auth-word" style={{ fontSize: 30, margin: '24px 0 0' }}>{c.title}</h1>
          <p className="bk-body" style={{ color: 'var(--text2)', margin: '11px 0 0', maxWidth: 280, textWrap: 'pretty' }}>{c.sub}</p>

          {via === 'anon' && (
            <div className="bk-row" style={{ gap: 9, marginTop: 18, padding: '9px 14px', borderRadius: 14, background: 'var(--chip-bg)', color: 'var(--text2)' }}>
              <AuthIcon name="device" size={17} style={{ flex: 'none' }} />
              <span style={{ fontSize: 12.5, fontWeight: 600 }}>Данные на этом устройстве</span>
            </div>
          )}
        </div>

        <div className="auth-stack">
          <a className="auth-pbtn fill" href="Bookechi Prototype.html" style={{ textDecoration: 'none' }}>
            Открыть библиотеку
          </a>
          {via === 'anon'
            ? <button className="auth-textbtn" onClick={() => app.openEmail('register')} style={{ color: 'var(--accent-deep)' }}>Создать аккаунт</button>
            : <button className="auth-textbtn" onClick={() => app.reset()}>Сменить способ входа</button>}
        </div>
      </div>
    </div>
  );
}

Object.assign(window, {
  AuthPassword, GoogleInlineBtn, EmailScreen, AnonSheet, ConnectingOverlay, SuccessScreen, DONE_COPY,
});
