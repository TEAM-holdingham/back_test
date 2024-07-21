// 타이머 관련 변수들
let timerInterval; // setInterval을 저장할 변수
let timerRunning = false; // 타이머가 실행 중인지 여부를 나타내는 변수
let startTime, stopTime; // 시작 시간과 정지 시간을 저장할 변수
let startStopInterval, stopInterval; // 시작-정지 간격과 정지 시점의 간격을 저장할 변수
let hours = 0, minutes = 0, seconds = 0; // 타이머의 시간 변수 초기화

// 타이머 표시 업데이트 함수
function updateTimerDisplay() {
    document.getElementById('hours').innerText = formatTime(hours);
    document.getElementById('minutes').innerText = formatTime(minutes);
    document.getElementById('seconds').innerText = formatTime(seconds);
}

// 시간 형식을 맞추기 위한 함수 (2자리 숫자로 표시)
function formatTime(time) {
    return time < 10 ? `0${time}` : time;
}

// 타이머 시작 함수
function startTimer() {
    if (!timerRunning) {
        startTime = new Date().getTime(); // 현재 시간을 밀리초로 저장
        timerInterval = setInterval(updateTime, 1000); // 1초마다 updateTime 함수 호출
        timerRunning = true;
    }
}

// 타이머 멈춤 함수
function stopTimer() {
    clearInterval(timerInterval);
    stopTime = new Date().getTime(); // 현재 시간을 밀리초로 저장
    startStopInterval = calculateTimeDifference(startTime, stopTime);
    stopInterval = calculateTimeDifference(startTime, new Date().getTime());
    timerRunning = false;

    // 데이터를 PHP 파일에 전송
    sendDataToPHP();
}

// 타이머 재시작 함수
function resetTimer() {
    clearInterval(timerInterval);
    timerRunning = false;
    hours = 0;
    minutes = 0;
    seconds = 0;
    updateTimerDisplay();
}

// 시간을 증가시키고 화면에 업데이트하는 함수
function updateTime() {
    seconds++;
    if (seconds === 60) {
        seconds = 0;
        minutes++;
    }
    if (minutes === 60) {
        minutes = 0;
        hours++;
    }
    updateTimerDisplay();
}

// 두 시간 사이의 차이를 계산하는 함수 (밀리초 단위로 반환)
function calculateTimeDifference(start, end) {
    return end - start;
}

// 데이터를 PHP 파일에 전송하는 함수
function sendDataToPHP() {
    // AJAX를 사용하여 데이터를 전송
    const xhr = new XMLHttpRequest();
    const url = "timer.php";
    const params = `start_time=${startTime}&stop_time=${stopTime}&elapsed_time=${startStopInterval}&pause_duration=${stopInterval}`;
    xhr.open("POST", url, true);
    xhr.setRequestHeader("Content-type", "application/x-www-form-urlencoded");

    xhr.onreadystatechange = function () {
        if (xhr.readyState == 4 && xhr.status == 200) {
            console.log(xhr.responseText); // 성공 시 서버에서 반환한 메시지 출력
        }
    };

    xhr.send(params);
}

// 시작 버튼 클릭 시
document.getElementById('button-start').addEventListener('click', startTimer);

// 정지 버튼 클릭 시
document.getElementById('button-stop').addEventListener('click', stopTimer);

// 재시작 버튼 클릭 시
document.getElementById('button-reset').addEventListener('click', resetTimer);
