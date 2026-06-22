// Bookechi — mock data, scenarios, heatmap generator
(function () {
  const COVER_TONES = [
    { bg: '#A08B7C', fg: '#F7F1E8' }, // walnut
    { bg: '#BE5E3B', fg: '#FBEEE4' }, // terracotta
    { bg: '#7C8A6E', fg: '#F0F3EA' }, // sage
    { bg: '#8C6E54', fg: '#F6EFE6' }, // leather
    { bg: '#9E4A2C', fg: '#FAEBE0' }, // deep clay
    { bg: '#6E6353', fg: '#F2EEE6' }, // stone
    { bg: '#B08968', fg: '#FAF3E8' }, // ochre
    { bg: '#5C6B5E', fg: '#EDF1EC' }, // forest sage
  ];

  let _id = 0;
  function book(title, author, pages, status, opts = {}) {
    _id += 1;
    return {
      id: 'b' + _id,
      title, author, pages,
      status, // 'reading' | 'planned' | 'finished'
      current: opts.current ?? (status === 'finished' ? pages : 0),
      fav: !!opts.fav,
      rating: opts.rating ?? null,
      note: opts.note ?? '',
      quotes: opts.quotes ?? [],
      cover: COVER_TONES[(_id * 3) % COVER_TONES.length],
      active: !!opts.active,
    };
  }

  function makeBooks() {
    _id = 0;
    return [
      book('Норвежский лес', 'Харуки Мураками', 320, 'reading', { current: 182, fav: true, active: true, quotes: [
        { text: 'Иногда целая осень помещается в одном вечере с книгой.', page: 96 },
        { text: 'Тишина — тоже разговор, просто очень честный.', page: 211 },
      ] }),
      book('Думай медленно… решай быстро', 'Даниэль Канеман', 456, 'reading', { current: 120, quotes: [
        { text: 'Быстрый ответ — не всегда мой ответ. (заметка себе)', page: 88 },
      ] }),
      book('Маленькая жизнь', 'Ханья Янагихара', 720, 'planned'),
      book('Имя розы', 'Умберто Эко', 672, 'planned'),
      book('Дом, в котором…', 'Мариам Петросян', 960, 'planned'),
      book('Пикник на обочине', 'Аркадий и Борис Стругацкие', 224, 'planned'),
      book('Над пропастью во ржи', 'Дж. Д. Сэлинджер', 272, 'finished', { fav: true, rating: 5 }),
      book('1984', 'Джордж Оруэлл', 328, 'finished', { rating: 4 }),
      book('Мастер и Маргарита', 'Михаил Булгаков', 480, 'finished', { fav: true, rating: 5 }),
      book('Сто лет одиночества', 'Габриэль Гарсиа Маркес', 416, 'finished', { rating: 4 }),
      book('Великий Гэтсби', 'Фрэнсис С. Фицджеральд', 240, 'finished', { rating: 4 }),
      book('Старик и море', 'Эрнест Хемингуэй', 128, 'finished', { rating: 5 }),
      book('О дивный новый мир', 'Олдос Хаксли', 350, 'finished', { rating: 4 }),
      book('Цветы для Элджернона', 'Дэниел Киз', 320, 'finished', { fav: true, rating: 5 }),
      book('Портрет Дориана Грея', 'Оскар Уайльд', 320, 'finished', { rating: 3 }),
      book('Убить пересмешника', 'Харпер Ли', 416, 'finished', { rating: 4 }),
      book('Три товарища', 'Эрих Мария Ремарк', 480, 'finished', { rating: 5 }),
      book('Преступление и наказание', 'Фёдор Достоевский', 672, 'finished', { rating: 4 }),
    ];
  }

  // ── Scenarios ──────────────────────────────────
  // key → { books, streak, todayMarked, hasHistory, nudge }
  const SCENARIOS = {
    'Наполненный':         () => ({ books: makeBooks(), streak: 7, todayMarked: false, hasHistory: true, nudge: false, comeback: false, todayDelta: 0, record: 8, goal: { type: 'pages', value: 400, done: 340 } }),
    'Без отметки сегодня': () => ({ books: makeBooks(), streak: 7, todayMarked: false, hasHistory: true, nudge: true, comeback: false, todayDelta: 0, record: 8, goal: { type: 'pages', value: 400, done: 340 } }),
    'Возвращение':         () => ({ books: makeBooks(), streak: 0, todayMarked: false, hasHistory: true, nudge: false, comeback: true, todayDelta: 0, record: 8, goal: { type: 'pages', value: 400, done: 60 } }),
    'Нет активной книги':  () => {
      const books = makeBooks().map(b => b.status === 'reading' ? { ...b, status: 'planned', current: 0, active: false } : b);
      return { books, streak: 3, todayMarked: false, hasHistory: true, nudge: false, comeback: false, todayDelta: 0, record: 8, goal: { type: 'pages', value: 400, done: 180 } };
    },
    'Пустая библиотека':   () => ({ books: [], streak: 0, todayMarked: false, hasHistory: false, nudge: false, comeback: false, todayDelta: 0, record: 0, goal: { type: 'days', value: 5, done: 0 } }),
  };

  // ── Mock search catalogue ────────────────────
  const SEARCH_BOOKS = [
    { title: 'Кафка на пляже', author: 'Харуки Мураками', pages: 640, tone: 1 },
    { title: 'Шум. Несовершенство человеческих суждений', author: 'Даниэль Канеман', pages: 544, tone: 5 },
    { title: 'Тень горы', author: 'Грегори Дэвид Робертс', pages: 864, tone: 3 },
    { title: 'Зулейха открывает глаза', author: 'Гузель Яхина', pages: 512, tone: 2 },
    { title: 'Завтрак у Тиффани', author: 'Трумен Капоте', pages: 160, tone: 6 },
  ];

  // ── Reading sessions (per book, last 10 days) ────
  function sessionsFor(book) {
    let h = 0;
    for (const ch of String(book.id)) h += ch.charCodeAt(0);
    const out = [];
    for (let i = 9; i >= 0; i--) {
      const r = seeded(h * 31 + i * 7);
      out.push(r < 0.32 ? 0 : Math.round(6 + r * 52));
    }
    return out;
  }

  // ── Dates / heatmap ────────────────────────────
  const TODAY = new Date(2026, 5, 9); // 9 июня 2026, вторник
  const MONTHS_RU = ['Январь','Февраль','Март','Апрель','Май','Июнь','Июль','Август','Сентябрь','Октябрь','Ноябрь','Декабрь'];
  const MONTHS_RU_SHORT = ['Янв','Фев','Мар','Апр','Май','Июн','Июл','Авг','Сен','Окт','Ноя','Дек'];
  const WEEKDAYS_RU = ['Пн','Вт','Ср','Чт','Пт','Сб','Вс'];

  function seeded(n) { // deterministic 0..1
    const x = Math.sin(n * 127.1 + 311.7) * 43758.5453;
    return x - Math.floor(x);
  }

  // pages read on a given date (deterministic), 0 if future
  function pagesOn(date) {
    if (date > TODAY) return -1; // future
    const key = date.getFullYear() * 372 + date.getMonth() * 31 + date.getDate();
    const r = seeded(key);
    // last 7 days = streak, always > 0
    const diff = Math.round((TODAY - date) / 86400000);
    if (diff >= 1 && diff <= 7) return 18 + Math.round(r * 50);
    if (diff === 0) return 0; // today not yet marked
    if (r < 0.3) return 0;
    return Math.round(r * 75);
  }

  function levelFor(pages) {
    if (pages <= 0) return 0;
    if (pages < 12) return 1;
    if (pages < 25) return 2;
    if (pages < 42) return 3;
    if (pages < 60) return 4;
    return 5;
  }

  // month grid: weeks (Mon-start) for TODAY's month
  function monthGrid() {
    const y = TODAY.getFullYear(), m = TODAY.getMonth();
    const first = new Date(y, m, 1);
    const daysIn = new Date(y, m + 1, 0).getDate();
    const lead = (first.getDay() + 6) % 7; // Mon=0
    const cells = [];
    for (let i = 0; i < lead; i++) cells.push(null);
    for (let d = 1; d <= daysIn; d++) {
      const date = new Date(y, m, d);
      const p = pagesOn(date);
      cells.push({ day: d, pages: p, level: p < 0 ? -1 : levelFor(p), isToday: d === TODAY.getDate() });
    }
    while (cells.length % 7 !== 0) cells.push(null);
    return cells;
  }

  const SPEED = 38; // pages / hour — keeps time derived from pages consistently
  function minutesFor(pages) { return Math.max(0, Math.round((pages / SPEED) * 60)); }

  function monthSummary() {
    const y = TODAY.getFullYear(), m = TODAY.getMonth();
    let total = 0, best = 0, bestDay = 0, streak = 0, bestStreak = 0, sessions = 0;
    for (let d = 1; d <= TODAY.getDate(); d++) {
      const p = pagesOn(new Date(y, m, d));
      if (p > 0) { total += p; streak += 1; bestStreak = Math.max(bestStreak, streak); sessions += 1; }
      else streak = 0;
      if (p > best) { best = p; bestDay = d; }
    }
    const totalMin = minutesFor(total);
    const avgSession = sessions ? Math.round(totalMin / sessions) : 0;
    return { total, best, bestDay, bestStreak, sessions, totalMin, avgSession };
  }

  // minutes read for each of the last 7 days (oldest → newest)
  function weekMinutes() {
    const out = [];
    for (let i = 6; i >= 0; i--) {
      const d = new Date(TODAY); d.setDate(TODAY.getDate() - i);
      out.push(minutesFor(Math.max(0, pagesOn(d))));
    }
    return out;
  }

  // year: 12 aggregated months
  function yearGrid() {
    const y = TODAY.getFullYear();
    return MONTHS_RU_SHORT.map((label, m) => {
      if (m > TODAY.getMonth()) return { label, total: -1, level: -1 };
      const daysIn = m === TODAY.getMonth() ? TODAY.getDate() : new Date(y, m + 1, 0).getDate();
      let total = 0;
      for (let d = 1; d <= daysIn; d++) total += Math.max(0, pagesOn(new Date(y, m, d)));
      const avg = total / daysIn;
      return { label, total, level: levelFor(avg) };
    });
  }

  function yearSummary() {
    const months = yearGrid().filter(m => m.total >= 0);
    const total = months.reduce((s, m) => s + m.total, 0);
    const best = months.reduce((a, b) => (b.total > a.total ? b : a), months[0]);
    return { total, bestMonth: best.label, months: months.length, totalMin: minutesFor(total) };
  }

  function greeting() {
    return 'Добрый вечер, Иван';
  }

  // Reading sessions log (deterministic, derived from books)
  function sessionLog(books, onlyBookId) {
    if (onlyBookId) {
      const b = books.find(x => x.id === onlyBookId);
      return b ? buildSessions([b]) : [];
    }
    const reading = books.filter(b => b.status === 'reading');
    const finished = books.filter(b => b.status === 'finished').slice(0, 2);
    return buildSessions(reading.concat(finished));
  }
  function buildSessions(src) {
    const out = [];
    src.forEach((b, bi) => {
      let pos = b.current;
      const nDays = 2 + Math.round(seeded(bi * 7 + 3) * 4);
      for (let i = 0; i < nDays && pos > 0; i++) {
        const chunk = 14 + Math.round(seeded(bi * 13 + i * 5) * 40);
        const from = Math.max(0, pos - chunk);
        if (from === pos) break;
        out.push({
          id: 'sess' + b.id + '_' + i,
          bookId: b.id, title: b.title, author: b.author, cover: b.cover,
          from, to: pos, mins: minutesFor(pos - from),
          pct: Math.round((pos - from) / b.pages * 100),
          dayOffset: i,
          time: ['21:30', '08:15', '22:05', '13:40', '20:50', '19:10'][(bi + i) % 6],
          isCurrent: i === 0 && b.status === 'reading',
        });
        pos = from;
      }
    });
    out.sort((a, b) => a.dayOffset - b.dayOffset);
    return out;
  }
  function dayLabel(off) {
    if (off === 0) return 'Сегодня';
    if (off === 1) return 'Вчера';
    return 'На этой неделе';
  }
  function periodSummary(books) {
    const log = sessionLog(books);
    const sumW = log.reduce((a, s) => ({ n: a.n + 1, p: a.p + (s.to - s.from), m: a.m + s.mins }), { n: 0, p: 0, m: 0 });
    const fin = books.filter(b => b.status === 'finished').length;
    return {
      'Неделя': { sessions: sumW.n, pages: sumW.p, mins: sumW.m, delta: 18, deltaLabel: 'к прошлой неделе' },
      'Месяц': { sessions: sumW.n * 3 + 5, pages: sumW.p * 3 + 120, mins: sumW.m * 3 + 400, delta: 9, deltaLabel: 'к прошлому месяцу' },
      'Всё время': { sessions: 148, pages: 8480, mins: 5520, delta: null, deltaLabel: fin + ' книг · с марта 2024' },
    };
  }

  window.BK_DATA = {
    SCENARIOS, TODAY, MONTHS_RU, MONTHS_RU_SHORT, WEEKDAYS_RU, COVER_TONES, SEARCH_BOOKS,
    monthGrid, monthSummary, yearGrid, yearSummary, pagesOn, levelFor, greeting, sessionsFor,
    minutesFor, weekMinutes, sessionLog, dayLabel, periodSummary,
  };
})();
