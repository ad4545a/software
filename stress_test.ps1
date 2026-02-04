$Count = 20
$Failed = 0
$LogDir = "test-logs/stress"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null

Write-Host "Starting Stress Test: $Count Runs..." -ForegroundColor Cyan

for ($i = 1; $i -le $Count; $i++) {
    Write-Host "Run #$i..." -NoNewline
    $LogFile = "$LogDir/run_$i.log"
    $env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"
    
    # Run tests without re-compiling every time to save time, if possible, but safely verify logic
    # Using 'surefire:test' instead of 'clean test' for speed after first run
    if ($i -eq 1) {
        & "C:\Program Files\Apache\Maven\apache-maven-3.9.12\bin\mvn.cmd" clean test > $LogFile 2>&1
    }
    else {
        & "C:\Program Files\Apache\Maven\apache-maven-3.9.12\bin\mvn.cmd" surefire:test > $LogFile 2>&1
    }

    if ($LASTEXITCODE -eq 0) {
        Write-Host " [PASS]" -ForegroundColor Green
    }
    else {
        Write-Host " [FAIL]" -ForegroundColor Red
        $Failed++
        Write-Host "Failure Log: $LogFile"
        break # Stop on first failure as per "Robot Rules"
    }
}

Write-Host "----------------------------------------"
Write-Host "Total Runs: $Count"
Write-Host "Failed: $Failed"
if ($Failed -eq 0) {
    Write-Host "STRESS TEST PASSED" -ForegroundColor Green
}
else {
    Write-Host "STRESS TEST FAILED" -ForegroundColor Red
    exit 1
}
