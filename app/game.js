// Подключение шрифта MinecraftSeven
const minecraftFont = new FontFace('MinecraftSeven', 'url(./res/MinecraftSeven.woff) format("woff")');
minecraftFont.load().then(loaded => {
  document.fonts.add(loaded);
  document.body.style.fontFamily = 'MinecraftSeven, sans-serif';
}).catch(err => console.error('Не удалось загрузить шрифт MinecraftSeven:', err));

// Темная тема и кастомизация меню
function applyDarkTheme() {
  document.body.style.backgroundColor = '#000';
  document.body.style.color = '#fff';
}

function customizeMenu() {
  const menu = document.getElementById('menu-screen');

  // Черный фон под картинкой
  menu.style.backgroundColor = '#000';
  menu.style.color = '#fff';
  menu.style.position = 'relative';

  // Лого
  const logo = document.createElement('img');
  logo.src = './res/LOGO.png';
  logo.alt = 'Собеседование в Контур';
  logo.className = 'menu-logo mb-4';
  logo.style.maxWidth = '80%';
  logo.style.height = 'auto';
  logo.style.zIndex = '1';
  menu.prepend(logo);

  // Фон в зависимости от ширины экрана
  function updateMenuBackground() {
    const isMobile = window.innerWidth < 768;
    const bgUrl = isMobile ? './res/menu-mobile.png' : './res/menu-desktop.png';
    menu.style.backgroundImage = `url(${bgUrl})`;
    menu.style.backgroundSize = 'cover';
    menu.style.backgroundPosition = 'center';
  }
  updateMenuBackground();
  window.addEventListener('resize', updateMenuBackground);

  // Кнопка затемненная прозрачная
  const startBtn = document.getElementById('go-to-nickname');
  startBtn.style.backgroundColor = 'rgba(0, 0, 0, 0.6)';
  startBtn.style.color = '#fff';
  startBtn.style.border = '1px solid rgba(255, 255, 255, 0.3)';
  startBtn.style.backdropFilter = 'blur(4px)';
  startBtn.style.zIndex = '1';
}

// Инициализация UI при загрузке
function initializeUI() {
  applyDarkTheme();
  customizeMenu();
}

// Основной JS для игры
let socket, gameId, timerInterval;

document.addEventListener('DOMContentLoaded', () => {
  initializeUI();
  document.getElementById('go-to-nickname')
    .addEventListener('click', () => toggleScreens('menu', 'nickname'));
  document.getElementById('start-btn')
    .addEventListener('click', initializeGame);
  document.getElementById('play-again-btn')
    .addEventListener('click', () => location.reload());

  document.getElementById('battlefield')
    .addEventListener('click', sendClick);
});

function toggleScreens(hide, show) {
  document.getElementById(`${hide}-screen`).classList.add('d-none');
  document.getElementById(`${show}-screen`).classList.remove('d-none');
}

async function initializeGame() {
  const usernameInput = document.getElementById('username-input');
  const username = usernameInput.value.trim() || `Player${Math.floor(Math.random() * 1000)}`;
  toggleScreens('nickname', 'game');

  try {
    gameId = await createGameSession(username);
    connectWebSocket(gameId);
  } catch (e) {
    alert(`Ошибка старта: ${e.message}`);
  }
}

const host = 'kontur.yakovlev05.ru';

async function createGameSession(username) {
  const res = await fetch(`https://${host}/api/v1/sessions`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify({ username })
  });
  if (!res.ok) throw new Error(res.statusText);
  const data = await res.json();
  return data.gameId;
}

function connectWebSocket(id) {
  socket = new WebSocket(`wss://${host}/ws/game?gameId=${id}`);

  socket.onopen = () => console.log('WebSocket connected');
  socket.onmessage = e => handleServerMessage(JSON.parse(e.data));
  socket.onerror = () => alert('Ошибка соединения');
  socket.onclose = () => console.log('WebSocket closed');
}

// Get both GIF elements
const playerGif = document.getElementById('player-gif'); // Nerd
const hrGif = document.getElementById('hr-gif'); // Karen
let animationTimeouts = { player: null, hr: null };
const ANIMATIONS = {
  nerd: {
    static: { file: 'nerd_static.gif', duration: 0 },
    attack: { file: 'nerd_attack.gif', duration: 800 },
    think: { file: 'nerd_think.gif', duration: 5000 },
    think_correct: { file: 'nerd_think_correct.gif', duration: 1500 },
    think_wrong: { file: 'nerd_think_wrong.gif', duration: 1500 },
  },
  karen: {
    static: { file: 'karen_static.gif', duration: 0 },
    attack: { file: 'karen_attack.gif', duration: 800 },
    think: { file: 'karen_think.gif', duration: 5000 }
  }
};

// Animation control function for both characters
function playAnimation(character, type) {
  const anim = ANIMATIONS[character][type];
  if (!anim) return;

  const gifElement = character === 'nerd' ? playerGif : hrGif;
  const timeoutKey = character === 'nerd' ? 'player' : 'hr';

  // Clear previous timeout
  clearTimeout(animationTimeouts[timeoutKey]);

  // Set new animation
  gifElement.src = `./res/${anim.file}`;

  // Set timeout to return to static
  if (anim.duration > 0) {
    animationTimeouts[timeoutKey] = setTimeout(() => {
      gifElement.src = `./res/${character}_static.gif`;
    }, anim.duration);
  }
}

function sendClick() {
  if (socket && socket.readyState === WebSocket.OPEN) {
    playAnimation('nerd', 'attack'); // Player attacks
    playAnimation('karen', 'attack'); // Player attacks
    socket.send(JSON.stringify({ type: 'CLICK' }));
  }
}

function handleServerMessage(msg) {
  if (typeof msg.myHp === 'number' && typeof msg.hrHp === 'number') {
    updateHP(msg.myHp, msg.hrHp);
  }
  switch (msg.type) {
    case 'QUESTION': showQuestion(msg); break;
    case 'ANSWER_RESULT': showAnswerResult(msg); break;
    case 'EXPIRE_ANSWER': handleExpireAnswer(msg); break;
    case 'TIME_UP':
    case 'RESULT': endGame(msg); break;
  }
}

function updateHP(my, hr) {
  document.getElementById('my-hp').style.width = `${my}%`;
  document.getElementById('hr-hp').style.width = `${hr}%`;
}

function showQuestion(q) {
  clearInterval(timerInterval);
  playAnimation('nerd', 'think'); // Player thinks
  playAnimation('karen', 'think'); // HR also thinks
  document.getElementById('question-text').textContent = q.text;
  const cont = document.getElementById('answers-container');
  cont.innerHTML = '';
  q.answers.forEach(a => {
    const btn = document.createElement('button');
    btn.className = 'btn btn-outline-light m-1';
    btn.textContent = a.text;
    btn.dataset.id = a.id;
    btn.disabled = false;
    btn.onclick = () => sendAnswer(a.id);
    cont.appendChild(btn);
  });
  startTimer(q.secondsLeft);
}

function sendAnswer(id) {
  if (socket && socket.readyState === WebSocket.OPEN) {
    socket.send(JSON.stringify({ type: 'ANSWER', answerId: id }));
    document.querySelectorAll('#answers-container button').forEach(b => b.disabled = true);
  }
}

function showAnswerResult(r) {
  document.querySelectorAll('#answers-container button').forEach(b => {
    const bid = parseInt(b.dataset.id, 10);
    if (bid === r.correctAnswerId) b.classList.replace('btn-outline-light', 'btn-success');
    if (bid === r.yourAnswerId && !r.correct) b.classList.replace('btn-outline-light', 'btn-danger');
  });

  playAnimation('nerd', r.correct ? 'think_correct' : 'think_wrong');
  playAnimation('karen', r.correct ? 'attack' : 'think');

  setTimeout(() => resetQuestionUI('Ждём вопрос...'), 1500);
}

function handleExpireAnswer(msg) {
  clearInterval(timerInterval);
  resetQuestionUI('Время на ответ истекло');
}

function startTimer(seconds) {
  clearInterval(timerInterval);
  let t = seconds;
  const el = document.getElementById('timer');
  el.textContent = `Осталось: ${t}s`;
  timerInterval = setInterval(() => {
    t--;
    if (t >= 0) el.textContent = `Осталось: ${t}s`;
    else clearInterval(timerInterval);
  }, 1000);
}

function resetQuestionUI(message) {
  document.getElementById('answers-container').innerHTML = '';
  document.getElementById('question-text').textContent = message;
  document.getElementById('timer').textContent = '';
}

function endGame(res) {
  clearInterval(timerInterval);
  toggleScreens('game', 'victory');
  document.getElementById('result-message').textContent = res.win ? 'Поздравляем, вы победили!' : 'Вы проиграли.';
  document.getElementById('score-message').textContent = typeof res.score === 'number' ? `Ваш счёт: ${res.score}` : '';
  socket.close();
}