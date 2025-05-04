// Подключение шрифта MinecraftSeven
const minecraftFont = new FontFace('MinecraftSeven', 'url(./res/MinecraftSeven.woff) format("woff")');
minecraftFont.load().then(loaded => {
    document.fonts.add(loaded);
    document.body.style.fontFamily = 'MinecraftSeven, sans-serif';
}).catch(err => console.error('Не удалось загрузить шрифт MinecraftSeven:', err));

let currentPage = 0;
document.addEventListener('DOMContentLoaded', () => {
    if (window.innerWidth < 768) {
        const gameScreen = document.getElementById('game-screen');
        if (gameScreen) gameScreen.classList.remove('py-4');

        const gameScreenRow = document.querySelector('#game-screen .row');
        const gameScreenContent = document.querySelector('#game-screen .h-100.g-3');

        if (gameScreenRow) gameScreenRow.classList.remove('row');
        if (gameScreenContent) gameScreenContent.classList.add('d-flex', 'flex-column');

        const fight = document.getElementById('fight');
        const hpBlock = fight?.querySelector('.w-75.text-center.text-white');

        if (fight && hpBlock && gameScreenContent) {
            hpBlock.classList.remove('w-75', 'mt-4');
            fight.removeChild(hpBlock);

            gameScreenContent.prepend(fight);
            gameScreenContent.prepend(hpBlock);
        }

        // Переносим battlefield
        const battlefield = document.getElementById('battlefield');
        const narrowPanel = document.querySelector('.narrow-panel.left-panel');

        if (battlefield && narrowPanel && narrowPanel.parentNode) {
            narrowPanel.parentNode.insertBefore(battlefield, narrowPanel);
        }

        // Настройка размеров character-animation
        const characterAnimation = document.getElementById('character-animation');
        if (characterAnimation) {
            characterAnimation.style.width = '250px';
            characterAnimation.style.height = '250px';
            characterAnimation.style.margin = '0 auto -15px';
        }

        // Убираем min-height у текста вопроса
        const questionText = document.getElementById('question-text');
        if (questionText) {
            questionText.style.minHeight = '0';
        }

        // Убираем margin-bottom: auto у question-section
        const questionSection = document.getElementById('question-section');
        if (questionSection) {
            questionSection.style.marginBottom = '0';
        }

        // answers-container скрыть
        const answersContainer = document.getElementById('answers-container');
        if (answersContainer) {
            answersContainer.style.minHeight = '0';
            answersContainer.style.display = 'none';
        }

        // battlefield поведение
        const originalShowQuestion = window.showQuestion;
        window.showQuestion = function(q) {
            if (battlefield) battlefield.style.display = 'none';
            if (answersContainer) answersContainer.style.display = 'block';
            if (questionText) questionText.textContent = q.text;
            originalShowQuestion(q);
        };

        const originalResetQuestionUI = window.resetQuestionUI;
        window.resetQuestionUI = function(message) {
            if (battlefield) battlefield.style.display = 'block';
            if (answersContainer) answersContainer.style.display = 'none';
            originalResetQuestionUI(message);
        };
    }

    document.getElementById('show-leaderboard')
    .addEventListener('click', async () => {
        currentPage = 0;
        toggleScreens('menu', 'leaderboard');
        await loadLeaderboard(currentPage);
    });

    document.getElementById('back-to-menu')
        .addEventListener('click', () => toggleScreens('leaderboard', 'menu'));

    document.getElementById('prev-page')
        .addEventListener('click', async () => {
            if (currentPage > 0) {
                currentPage--;
                await loadLeaderboard(currentPage);
            }
        });

    document.getElementById('next-page')
        .addEventListener('click', async () => {
            currentPage++;
            await loadLeaderboard(currentPage);
        });

    document.getElementById('info-button').addEventListener('click', () => {
        document.getElementById('team-popup').classList.remove('d-none');
      });
      
      document.getElementById('close-popup').addEventListener('click', () => {
        document.getElementById('team-popup').classList.add('d-none');
      });  
      
    //   document.getElementById('volume-slider').addEventListener('input', (e) => {
    //     if (currentTrack) currentTrack.volume = e.target.value;
    // });
    
    document.getElementById('mute-button').addEventListener('click', () => {
        if (currentTrack) {
            if (currentTrack.volume > 0) {
                currentTrack.volume = 0;
                document.getElementById('volume-slider').value = 0;
                document.getElementById('volume-slider-game').value = 0;
                document.getElementById('mute-button').innerText = '🔇';
                document.getElementById('mute-button-game').innerText = '🔇';
            } else {
                currentTrack.volume = 0.25;
                document.getElementById('volume-slider').value = parseFloat(localStorage.getItem('volume'));
                document.getElementById('volume-slider-game').value = parseFloat(localStorage.getItem('volume'));
                document.getElementById('mute-button').innerText = '🔊';
                document.getElementById('mute-button-game').innerText = '🔊';
            }
        }
    });

    let isFirstInteraction = false;

    document.addEventListener('click', () => {
        if (!isFirstInteraction) {
            playTrack('menu');
            isFirstInteraction = true;
        }
    }, { once: true }); // <-- чтобы обработчик сработал только 1 раз

});

// Быстрый выбор ответа по 1-4
document.addEventListener('keydown', (event) => {
    if (['INPUT', 'TEXTAREA'].includes(document.activeElement.tagName)) return;

    const key = event.key;

    if (['1', '2', '3', '4'].includes(key)) {
        const index = parseInt(key, 10) - 1;
        const buttons = document.querySelectorAll('#answers-container button');
        if (buttons[index]) {
            buttons[index].click();
        }
    }
});

// Однократный клик при отпускании Enter или Space
document.addEventListener('keyup', (event) => {
    if (['INPUT', 'TEXTAREA'].includes(document.activeElement.tagName)) return;

    if (event.key === 'Enter' || event.key === ' ') {
        const battlefield = document.getElementById('battlefield');
        if (battlefield && battlefield.offsetParent !== null) {
            sendClick();
        }
    }
});

// const tracks = {
//     menu: new Audio('./res/music/Light Club - Blizzard.mp3'),
//     fight: new Audio('./res/music/MEGALOVANIA.flac'),
//     victory: new Audio('./res/music/victory.mp3'),
//     defeat: new Audio('./res/music/defeat.mp3')
// };

// let currentTrack = tracks.menu;

const trackList = {
    menu: [
        'Light Club - Blizzard.mp3', 
        'Scattle - Inner Animal.mp3', 
        'Jasper Byrne - Miami.mp3',
        'Sneaky Driver.mp3',
        'Disturbed Lines.mp3',
        'You Will Never Know.mp3',
        'Overdose.mp3'
    ],
    fight: [
        'MEGALOVANIA.flac',
        'Amireal - Spear Of Justice.mp3',
        'Amireal - Megalovania.mp3'
    ],
    victory: [
        'Jasper Byrne - Hotline (Analogue Mix).mp3', 
        'Jasper Byrne - Miami.mp3',
        'Mike Klubnika - 70K.mp3',
        'You Will Never Know.mp3'
    ],
    defeat: [
        'All For Now.mp3',
        'Blue Room (KZ-version).mp3',
        'Delusive Bunker.mp3'
    ]
};

let currentTrack = null;

// Загружаем настройки из localStorage
const savedVolume = parseFloat(localStorage.getItem('volume'));
const savedMuted = localStorage.getItem('muted') === 'true';

// if (!isNaN(savedVolume)) {
//     for (let key in tracks) tracks[key].volume = savedMuted ? 0 : savedVolume;
// }

let isMuted = savedMuted;

// // Настроим автоповтор треков
// for (let key in tracks) {
//     tracks[key].loop = true;
// }

// Функция воспроизведения определённого трека
// function playTrack(name) {
//     if (currentTrack) currentTrack.pause();
//     currentTrack = tracks[name];
//     currentTrack.volume = document.getElementById('volume-slider').value;
//     currentTrack.play().catch(err => console.error('Ошибка воспроизведения:', err));
// }

function playTrack(type) {
    if (currentTrack) {
        currentTrack.pause();
        currentTrack.currentTime = 0;
    }

    const list = trackList[type];
    if (!list || list.length === 0) return;

    const filename = list[Math.floor(Math.random() * list.length)];
    currentTrack = new Audio(`./res/music/${filename}`);
    currentTrack.loop = true;

    const savedVolume = parseFloat(localStorage.getItem('volume')) || 0.25;
    const savedMuted = localStorage.getItem('muted') === 'true';

    currentTrack.volume = savedMuted ? 0 : savedVolume;

    currentTrack.play().catch(err => console.error('Ошибка воспроизведения:', err));
}

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
    logo.style.maxWidth = '65%';
    logo.style.height = 'auto';
    logo.style.zIndex = '1';
    menu.prepend(logo);

    // Фон в зависимости от ширины экрана
    function updateMenuBackground() {
        const isMobile = window.innerWidth < 768;
        const bgUrl = isMobile ? './res/menu-mobile.avif' : './res/menu-desktop.avif';
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

    const showLeaderBoard = document.getElementById('show-leaderboard');
    showLeaderBoard.style.backgroundColor = 'rgba(0, 0, 0, 0.6)';
    showLeaderBoard.style.color = '#fff';
    showLeaderBoard.style.border = '1px solid rgba(255, 255, 255, 0.3)';
    showLeaderBoard.style.backdropFilter = 'blur(4px)';
    showLeaderBoard.style.zIndex = '1';
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
    document.getElementById('play-again-btn-win')
        .addEventListener('click', () => location.reload());

    document.getElementById('battlefield')
        .addEventListener('click', sendClick);

    const volumeSlider = document.getElementById('volume-slider');
    const muteButton = document.getElementById('mute-button');
    const volumeSliderGame = document.getElementById('volume-slider-game');
    const muteButtonGame = document.getElementById('mute-button-game');
    
    volumeSlider.value = isMuted ? 0 : parseFloat(localStorage.getItem('volume')) || 0.25;
    volumeSliderGame.value = isMuted ? 0 : parseFloat(localStorage.getItem('volume')) || 0.25;
    muteButton.innerText = isMuted ? '🔇' : '🔊';
    muteButtonGame.innerText = isMuted ? '🔇' : '🔊';

    volumeSlider.addEventListener('input', (e) => {
        const vol = parseFloat(e.target.value);
        if (!isMuted) {
            if (currentTrack) currentTrack.volume = vol;
        }
        localStorage.setItem('volume', vol);
        volumeSliderGame.value = vol;
    });
    
    muteButton.addEventListener('click', () => {
        isMuted = !isMuted;
    
        if (isMuted) {
            if (currentTrack) currentTrack.volume = 0;
            muteButton.innerText = '🔇';
            muteButtonGame.innerText = '🔇';
        } else {
            const vol = parseFloat(volumeSlider.value);
            if (currentTrack) currentTrack.volume = vol;
            muteButton.innerText = '🔊';
            muteButtonGame.innerText = '🔊';
        }
    
        localStorage.setItem('muted', isMuted);
    });
    volumeSliderGame.addEventListener('input', (e) => {
        const vol = parseFloat(e.target.value);
        if (!isMuted) {
            if (currentTrack) currentTrack.volume = vol;
        }
        localStorage.setItem('volume', vol);
        volumeSlider.value = vol;
    });
    
    muteButtonGame.addEventListener('click', () => {
        isMuted = !isMuted;
    
        if (isMuted) {
            if (currentTrack) currentTrack.volume = 0;
            muteButton.innerText = '🔇';
            muteButtonGame.innerText = '🔇';
        } else {
            const vol = parseFloat(volumeSliderGame.value);
            if (currentTrack) currentTrack.volume = vol;
            muteButton.innerText = '🔊';
            muteButtonGame.innerText = '🔊';
        }
    
        localStorage.setItem('muted', isMuted);
    }); 
});

function toggleScreens(hide, show) {
    const hideScreen = document.getElementById(`${hide}-screen`);
    const showScreen = document.getElementById(`${show}-screen`);

    // Плавное исчезновение текущего экрана
    hideScreen.classList.add('fade');
    hideScreen.classList.remove('show');

    // Плавное появление нового экрана
    setTimeout(() => {
        hideScreen.classList.add('d-none'); // Скрываем экран
        showScreen.classList.remove('d-none'); // Показываем новый экран
        showScreen.classList.add('fade', 'show'); // Делаем плавное появление
    }, 500); // Задержка, чтобы эффект исчезновения успел сработать
}


async function initializeGame() {
    const usernameInput = document.getElementById('username-input');
    const username = usernameInput.value.trim() || `Player${Math.floor(Math.random() * 1000)}`;
    toggleScreens('nickname', 'game');
    playTrack('fight'); // <-- переключаем музыку на бой

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
        headers: {'Content-Type': 'application/json'},
        body: JSON.stringify({username})
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

async function loadLeaderboard(page = 0) {
    const body = document.getElementById('leaderboard-body');
    const indicator = document.getElementById('page-indicator');
    body.innerHTML = '<tr><td colspan="4">Загрузка...</td></tr>';
    indicator.textContent = `Страница ${page + 1}`;

    try {
        const res = await fetch(`https://${host}/api/v1/statistics?page=${page}&size=10`);
        const data = await res.json();
        body.innerHTML = '';
        if (data.length === 0) {
            body.innerHTML = '<tr><td colspan="4">Нет данных на этой странице</td></tr>';
            return;
        }

        data.sort((a, b) => b.score - a.score).forEach((item, index) => {
            const date = new Date(item.completedAt * 1000).toLocaleString('ru-RU');
            body.innerHTML += `
              <tr>
                <td>${page * 10 + index + 1}</td>
                <td>${item.username}</td>
                <td>${item.score}</td>
                <td>${date}</td>
              </tr>
            `;
        });
    } catch (e) {
        body.innerHTML = '<tr><td colspan="4">Ошибка загрузки данных</td></tr>';
    }
}

// Get both GIF elements
const idleGif = document.getElementById('idle-gif');
let animationTimeout = null;
let canClick = true;

function playAnimation(type) {
    const animations = {
        static: {file: 'idle.gif', duration: 0},
        attack: {file: 'fight.gif', duration: 800},
        think: {file: 'question_1.gif', duration: 5000},
        think_correct: {file: 'question_correct.gif', duration: 1500},
        think_wrong: {file: 'question_wrong.gif', duration: 1500},
    };

    const anim = animations[type];
    if (!anim) return;

    clearTimeout(animationTimeout);

    idleGif.style.opacity = '0.3'; // <-- ставим не в ноль, а в 0.3
    // будет не исчезновение, а лёгкое затухание

    setTimeout(() => {
        idleGif.src = `./res/${anim.file}`;
        idleGif.onload = () => {
            idleGif.style.opacity = '1'; // обратно на полную яркость
        };
    }, 75); // Было 150мс, стало 75мс - быстрее реагирует


    if (anim.duration > 0) {
        animationTimeout = setTimeout(() => {
            playAnimation('static');
        }, anim.duration);
    }
}

function createAttackParticles(x, y) {
    const particle = document.createElement('div');
    particle.classList.add('attack-particle');
    particle.style.left = `${x}px`;
    particle.style.top = `${y}px`;
    document.body.appendChild(particle);

    setTimeout(() => particle.remove(), 300); // Удаляем частицу после анимации
}

function sendClick() {
    const battlefieldImg = document.getElementById('battlefield-img');

    if (socket && socket.readyState === WebSocket.OPEN && canClick) {
        // Заменяем картинку на "нажата"
        battlefieldImg.src = './res/Button_pressed.avif';

        // Простая анимация (плавное изменение)
        battlefieldImg.style.transition = 'transform 0.1s ease-in-out';
        battlefieldImg.style.transform = 'scale(0.95)';

        // Возвращаем в исходное состояние через 0.1 секунды
        setTimeout(() => {
            battlefieldImg.style.transform = 'scale(1)';
        }, 100);

        // showButton();

        // Делаем анимацию удара
        playAnimation('attack');
        socket.send(JSON.stringify({type: 'CLICK'}));
    }
}

function handleServerMessage(msg) {
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
        case 'EXPIRE_ANSWER':
            handleExpireAnswer(msg);
            break;
        case 'TIME_UP':
        case 'RESULT':
            endGame(msg);
            break;
    }
}

function updateHP(my, hr) {
    const hrPercent = Math.max(0, Math.min(100, (hr / 1000) * 100));
    
    document.getElementById('my-hp').style.width = `${my}%`;
    document.getElementById('hr-hp').style.width = `${hrPercent}%`;
}

function blockButton() {
    const battlefieldImg = document.getElementById('battlefield-img');
    battlefieldImg.src = './res/Button_inactive.avif'; // Ставим картинку для заблокированной кнопки
    if (window.innerWidth < 768) {
        if (battlefieldImg) {
            battlefieldImg.style.display = 'none';
        }
    }
}

function showButton() {
    const battlefieldImg = document.getElementById('battlefield-img');
    battlefieldImg.src = './res/Button_pressed.avif';
    if (window.innerWidth < 768) {
        if (battlefieldImg) {
            battlefieldImg.style.display = '';
        }
    }
}

function showQuestion(q) {
    canClick = false; // ❌ Блокируем клик

    blockButton(); // Блокируем кнопку

    clearInterval(timerInterval);
    playAnimation('think');
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
        socket.send(JSON.stringify({type: 'ANSWER', answerId: id}));
        document.querySelectorAll('#answers-container button').forEach(b => b.disabled = true);
    }
}

function showAnswerResult(r) {
    document.querySelectorAll('#answers-container button').forEach(b => {
        const bid = parseInt(b.dataset.id, 10);
        if (bid === r.correctAnswerId) b.classList.replace('btn-outline-light', 'btn-success');
        if (bid === r.yourAnswerId && !r.correct) b.classList.replace('btn-outline-light', 'btn-danger');
    });

    playAnimation(r.correct ? 'think_correct' : 'think_wrong');

    setTimeout(() => resetQuestionUI('Быстро нажимайте!'), 1500);
    clearInterval(timerInterval);
}

function handleExpireAnswer(msg) {
    clearInterval(timerInterval);
    resetQuestionUI('Время на ответ истекло, продолжайте быстро нажимать');
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
    canClick = true; // ✅ Разрешаем клик снова
    showButton();

    document.getElementById('answers-container').innerHTML = '';
    document.getElementById('question-text').textContent = message;
    document.getElementById('timer').textContent = '';
}


function endGame(res) {
    clearInterval(timerInterval);

    if (!res.win) {
        playTrack('defeat'); // <-- музыка проигрыша
        showDefeatScreen();
    } else {
        playTrack('victory'); // <-- музыка победы
        setTimeout(() => {
            toggleScreens('game', 'victory');
        }, 1000);
        document.getElementById('victory-message').textContent = 'Поздравляем, вы победили!';
        document.getElementById('player-statistics').textContent = typeof res.score === 'number' ? `Ваш счёт: ${res.score}` : '';
    }

    socket.close();
}

function showDefeatScreen() {
    // Воспроизводим анимацию проигрыша
    const loseGif = document.getElementById('lose-animation');
    loseGif.style.display = 'block'; // Показываем гифку проигрыша

    // Плавная смена экрана
    setTimeout(() => {
        toggleScreens('game', 'defeat'); // Переход на экран поражения
    }, 1000); // 1 секунда задержки для проигрыша

    document.getElementById('defeat-message').textContent = 'Вы проиграли!';
}
