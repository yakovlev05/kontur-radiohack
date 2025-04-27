// Улучшенный game.js с исправлениями по документации

document.addEventListener('DOMContentLoaded', () => {
    document.getElementById('go-to-nickname')
      .addEventListener('click', () => toggleScreens('menu', 'nickname'));
    document.getElementById('start-btn')
      .addEventListener('click', initializeGame);
    document.getElementById('play-again-btn')
      .addEventListener('click', () => location.reload());
  
    // Кликом по battlefield тоже запускаем
    document.getElementById('battlefield')
      .addEventListener('click', sendClick);
  });
  
  let socket, gameId, timerInterval;
  let canClick = true;
  
  function toggleScreens(hide, show) {
    document.getElementById(`${hide}-screen`).classList.add('d-none');
    document.getElementById(`${show}-screen`).classList.remove('d-none');
  }
  
  async function initializeGame() {
    const usernameInput = document.getElementById('username-input');
    const username = usernameInput.value.trim() || `Player${Math.floor(Math.random()*1000)}`;
    toggleScreens('nickname', 'game');
  
    try {
      gameId = await createGameSession(username);
      connectWebSocket(gameId);
    } catch (e) {
      alert(`Ошибка старта: ${e.message}`);
    }
  }
  
  const host = 'kontur.yakovlev05.ru'; // Ваш API-хост
  
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
  
    socket.onopen = () => sendClick();
    socket.onmessage = e => handleServerMessage(JSON.parse(e.data));
    socket.onerror = () => alert('Ошибка соединения');
  }
  
  function sendClick() {
    if (canClick && socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({ type: 'CLICK' }));
    }
  }
  
  function handleServerMessage(msg) {
    // Всегда обновляем HP, если есть
    if (typeof msg.myHp === 'number' && typeof msg.hrHp === 'number') {
      updateHP(msg.myHp, msg.hrHp);
    }
  
    switch (msg.type) {
      case 'QUESTION':
        showQuestion(msg);
        break;
      case 'ANSWER_RESULT':
        showAnswerResult(msg);
        break;
      case 'TIME_UP':
        // При истечении времени — конец игры
        endGame(msg);
        break;
      case 'RESULT':
        endGame(msg);
        break;
    }
  }
  
  function updateHP(my, hr) {
    document.getElementById('my-hp').style.width = my + '%';
    document.getElementById('hr-hp').style.width = hr + '%';
  }
  
  function showQuestion(q) {
    canClick = false;
    clearInterval(timerInterval);
  
    // Отрисовываем текст и ответы
    document.getElementById('question-text').textContent = q.text;
    const cont = document.getElementById('answers-container');
    cont.innerHTML = '';
  
    q.answers.forEach(a => {
      const btn = document.createElement('button');
      btn.className = 'btn btn-outline-secondary m-1';
      btn.textContent = a.text;
      btn.dataset.id = a.id;           // сохраняем id
      btn.onclick = () => sendAnswer(a.id);
      cont.appendChild(btn);
    });
  
    startTimer(q.secondsLeft);
  }
  
  function sendAnswer(id) {
    if (socket && socket.readyState === WebSocket.OPEN) {
      socket.send(JSON.stringify({ type: 'ANSWER', answerId: id }));
      // Блокируем кнопки после клика
      document.querySelectorAll('#answers-container button')
        .forEach(b => b.disabled = true);
    }
  }
  
  function showAnswerResult(r) {
    // Выделяем правильный и неправильный ответы
    document.querySelectorAll('#answers-container button').forEach(b => {
      const bid = parseInt(b.dataset.id, 10);
      if (bid === r.correctAnswerId) {
        b.classList.replace('btn-outline-secondary', 'btn-success');
      }
      if (bid === r.yourAnswerId && !r.correct) {
        b.classList.replace('btn-outline-secondary', 'btn-danger');
      }
    });
  
    // Через полсекунды готовимся к следующему клику
    setTimeout(() => {
      canClick = true;
      document.getElementById('answers-container').innerHTML = '';
      document.getElementById('question-text').textContent = 'Ждём вопрос...';
      document.getElementById('timer').textContent = '';
    }, 1500);
  }
  
  function startTimer(seconds) {
    clearInterval(timerInterval);
    let t = seconds;
    const el = document.getElementById('timer');
    el.textContent = `Осталось: ${t}s`;
  
    timerInterval = setInterval(() => {
      t -= 1;
      if (t >= 0) {
        el.textContent = `Осталось: ${t}s`;
      } else {
        clearInterval(timerInterval);
      }
    }, 1000);
  }
  
  function endGame(res) {
    clearInterval(timerInterval);
    toggleScreens('game', 'victory');
  
    // Подпись результата
    const msgEl = document.getElementById('result-message');
    msgEl.textContent = res.win ? 'Поздравляем, вы победили!' : 'Время вышло. Вы проиграли.';
  
    const scoreEl = document.getElementById('score-message');
    scoreEl.textContent = typeof res.score === 'number' ? `Ваш счёт: ${res.score}` : '';
  
    socket.close();
  }
  