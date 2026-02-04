$LogDir = "test-logs/phase1"
New-Item -ItemType Directory -Force -Path $LogDir | Out-Null
$Timestamp = Get-Date -Format "yyyyMMdd-HHmmss"
$LogFile = "$LogDir/test_run_$Timestamp.log"

Write-Host "Running Phase 1 Verification Suite..." -ForegroundColor Cyan
Write-Host "Logs will be saved to: $LogFile"

$env:JAVA_HOME = "C:\Program Files\Microsoft\jdk-17.0.16.8-hotspot"

# Execute Clean Test
& "C:\Program Files\Apache\Maven\apache-maven-3.9.12\bin\mvn.cmd" clean test > $LogFile 2>&1

if ($LASTEXITCODE -eq 0) {
    Write-Host "✅ ALL TESTS PASSED" -ForegroundColor Green
    
    # Check Offline (Simulation)
    Write-Host "Verifying Offline Capability..." -NoNewline
    # Simple check: Does code contain "http" calls? (Naive but effective for Phase 1)
    $NetworkCalls = Get-ChildItem -Path "src/main/java" -Include "*.java" -Recurse | Select-String -Pattern "http:", "https:", "java.net.URL", "Socket"
    if ($NetworkCalls) {
        Write-Host " [WARNING]" -ForegroundColor Yellow
        Write-Host "Potential network calls found:"
        $NetworkCalls | ForEach-Object { Write-Host $_ }
    }
    else {
        Write-Host " [PASS]" -ForegroundColor Green
    }
    
    Get-Content $LogFile | Select-String "Tests run:" | Select-Object -Last 1
}
else {
    Write-Host "❌ TESTS FAILED" -ForegroundColor Red
    Write-Host "Check log for details: $LogFile"
    exit 1
}
