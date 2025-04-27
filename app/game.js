document.addEventListener('DOMContentLoaded', () => {
    const startBtn = document.getElementById('start-btn');
    const battlefield = document.getElementById('battlefield');

    startBtn.addEventListener('click', initializeGame);

    battlefield.addEventListener('click', () => {
        sendClick();
    });
});

let socket = null;
let gameId = null;
let timerInterval = null;
let canClick = true; // Можно кликать по полю?
let username = "default";


async function initializeGame() {
    try {
        gameId = await createGameSession();
        console.log('Created game with ID:', gameId);

        connectWebSocket(gameId);

        document.getElementById('game-container').style.display = 'block';
        document.getElementById('start-btn').style.display = 'none';
    } catch (error) {
        console.error('Game initialization failed:', error);
        updateStatus('Ошибка: ' + error.message);
    }
}

async function createGameSession() {
    const usernameInput = document.getElementById('username-input');
    const username = usernameInput.value.trim() || 'Player' + Math.floor(Math.random() * 1000);

    const response = await fetch('http://localhost:8080/api/v1/sessions', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ username: username })
    });

    if (!response.ok) {
        throw new Error(`HTTP error! Status: ${response.status}`);
    }

    const data = await response.json();
    if (!data?.gameId) {
        throw new Error('Invalid game ID received');
    }
    return data.gameId;
}

function connectWebSocket(gameId) {
    if (!gameId) {
        throw new Error('Cannot connect - gameId is missing');
    }

    const wsUrl = `ws://localhost:8080/ws/game?gameId=${gameId}`;
    socket = new WebSocket(wsUrl);

    socket.onopen = () => {
        console.log('WebSocket connection established');
        updateStatus('Игра началась!');
        sendInitialClick();
    };

    socket.onmessage = (event) => {
        console.log('Message from server:', event.data);
        handleServerMessage(event.data);
    };

    socket.onerror = (error) => {
        console.error('WebSocket error:', error);
        updateStatus('Ошибка соединения');
    };

    socket.onclose = () => {
        console.log('WebSocket connection closed');
        updateStatus('Соединение закрыто');
    };
}

function sendInitialClick() {
    if (socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ type: 'CLICK' }));
    }
}

function sendClick() {
    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.send(JSON.stringify({ type: 'CLICK' }));
        console.log('Sent CLICK');
    }
}

function handleServerMessage(rawData) {
    try {
        const message = JSON.parse(rawData);

        if (message.myHp !== undefined && message.hrHp !== undefined) {
            updateHP(message.myHp, message.hrHp);
        }

        switch (message.type) {
            case 'QUESTION':
                showQuestion(message);
                break;
            case 'ANSWER_RESULT':
                showAnswerResult(message);
                break;
            case 'RESULT':
                endGame(message);
                break;
            case 'TIME_UP':
                handleTimeUp(message);
                break;
        }
    } catch (error) {
        console.error('Error parsing message:', error);
    }
}

function updateStatus(text) {
    const statusElement = document.getElementById('status');
    if (statusElement) statusElement.textContent = text;
}

function updateHP(myHp, hrHp) {
    const myHpElement = document.getElementById('my-hp');
    const hrHpElement = document.getElementById('hr-hp');

    if (myHpElement) myHpElement.style.width = `${myHp}%`;
    if (hrHpElement) hrHpElement.style.width = `${hrHp}%`;
}

function showQuestion(question) {
    canClick = false; // ❗ Блокируем клики
    const questionText = document.getElementById('question-text');
    const answersContainer = document.getElementById('answers-container');
    const timerElement = document.getElementById('timer');

    answersContainer.innerHTML = '';
    questionText.textContent = question.text;

    question.answers.forEach(answer => {
        const button = document.createElement('button');
        button.className = 'answer-btn';
        button.textContent = answer.text;
        button.dataset.id = answer.id;
        button.addEventListener('click', () => sendAnswer(answer.id));
        answersContainer.appendChild(button);
    });

    if (question.secondsLeft) {
        startTimer(question.secondsLeft);
    }
}


function sendInitialClick() {
    if (socket.readyState === WebSocket.OPEN && canClick) {
        socket.send(JSON.stringify({ type: 'CLICK' }));
        console.log('Sent CLICK');
    }
}

function startTimer(seconds) {
    const timerElement = document.getElementById('timer');
    let timeLeft = seconds;

    if (timerInterval) {
        clearInterval(timerInterval);
    }

    timerElement.textContent = `Осталось времени: ${timeLeft} сек.`;

    timerInterval = setInterval(() => {
        timeLeft--;
        timerElement.textContent = `Осталось времени: ${timeLeft} сек.`;

        if (timeLeft <= 0) {
            clearInterval(timerInterval);
        }
    }, 1000);
}

function sendAnswer(answerId) {
    if (!socket || socket.readyState !== WebSocket.OPEN) return;

    socket.send(JSON.stringify({
        type: 'ANSWER',
        answerId: parseInt(answerId)
    }));

    const buttons = document.querySelectorAll('.answer-btn');
    buttons.forEach(btn => {
        btn.disabled = true;
    });
}

function showAnswerResult(result) {
    const buttons = document.querySelectorAll('.answer-btn');

    buttons.forEach(btn => {
        if (parseInt(btn.dataset.id) === result.yourAnswerId) {
            btn.style.backgroundColor = result.correct ? '#a5d6a7' : '#ef9a9a';
        }
        if (parseInt(btn.dataset.id) === result.correctAnswerId && !result.correct) {
            btn.style.backgroundColor = '#a5d6a7';
        }
    });

    setTimeout(() => {
        const answersContainer = document.getElementById('answers-container');
        answersContainer.innerHTML = '';
        document.getElementById('question-text').textContent = 'Ожидаем следующий вопрос...';

        canClick = true; // ❗ Снова разрешаем клики
    }, 2000);
}


function endGame(result) {
    const resultContainer = document.getElementById('result-container');
    const resultMessage = document.getElementById('result-message');
    const scoreMessage = document.getElementById('score-message');
    const gameContainer = document.getElementById('game-container');

    if (timerInterval) {
        clearInterval(timerInterval);
    }

    resultContainer.style.display = 'block';
    resultMessage.textContent = result.win ? 'Поздравляем! Вы победили!' : 'Вы проиграли!';
    if (result.score) {
        scoreMessage.textContent = `Ваш счет: ${result.score}`;
    }

    if (socket && socket.readyState === WebSocket.OPEN) {
        socket.close();
    }
}

function handleTimeUp(message) {
    console.log('Time up:', message);
}
