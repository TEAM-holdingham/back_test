<?php
$servername = "localhost"; // 서버 이름
$username = "root"; // MySQL 사용자 이름
$password = ""; // MySQL 비밀번호
$dbname = "test"; // 데이터베이스 이름

// 시간대 설정 (한국 시간대 예시)
date_default_timezone_set('Asia/Seoul');

// 데이터베이스 연결 생성
$conn = new mysqli($servername, $username, $password, $dbname);

// 연결 확인
if ($conn->connect_error) {
    die("Connection failed: " . $conn->connect_error);
}

// 이전 작업의 종료 시간을 가져오기 위한 SQL 쿼리
$sql_previous_stop = "SELECT stop_time FROM timers ORDER BY id DESC LIMIT 1";

// 이전 작업의 종료 시간 초기화
$previous_stop_time = null;

// 이전 작업의 종료 시간을 가져옴
$result = $conn->query($sql_previous_stop);
if ($result && $result->num_rows > 0) {
    $row = $result->fetch_assoc();
    $previous_stop_time = strtotime($row['stop_time']);
}

// POST 데이터 수신 및 변수 초기화
$start_time = isset($_POST['start_time']) ? round($_POST['start_time'] / 1000) : 0;
$stop_time = isset($_POST['stop_time']) ? round($_POST['stop_time'] / 1000) : 0;
$elapsed_time = isset($_POST['elapsed_time']) ? $_POST['elapsed_time'] : 0;

// elapsed_time 계산 (start-stop 시간 차이)
$elapsed_time_seconds = $stop_time - $start_time;

// pause_duration 계산
$pause_duration = 0;
if ($previous_stop_time !== null) {
    $pause_duration = $start_time - $previous_stop_time;
}

// SQL 쿼리 작성
$sql = "INSERT INTO timers (start_time, stop_time, elapsed_time, pause_duration)
VALUES (FROM_UNIXTIME($start_time), FROM_UNIXTIME($stop_time), $elapsed_time_seconds, $pause_duration)";

// 쿼리 실행 및 결과 확인
if ($conn->query($sql) === TRUE) {
    echo "New record created successfully";
} else {
    echo "Error: " . $sql . "<br>" . $conn->error;
}

// 연결 종료
$conn->close();
?>
