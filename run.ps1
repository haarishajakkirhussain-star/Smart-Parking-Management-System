<#
.SYNOPSIS
    ParkSmart Application Launcher
.DESCRIPTION
    Checks for Maven, downloads a portable copy if missing, and launches Spring Boot.
#>

$ErrorActionPreference = "Stop"
Write-Host "=================================================" -ForegroundColor Cyan
Write-Host "  ParkSmart - Smart Parking Management System    " -ForegroundColor Green
Write-Host "=================================================" -ForegroundColor Cyan

# 1. Check Java
try {
    $javaVer = & java -version 2>&1 | Select-Object -First 1
    Write-Host "[✓] Java runtime detected: $javaVer" -ForegroundColor Green
} catch {
    Write-Host "[!] Java not found. Please ensure Java 17+ or 26 is in PATH." -ForegroundColor Red
    exit 1
}

# 2. Check Maven
$mvnCmd = Get-Command mvn -ErrorAction SilentlyContinue

if (-not $mvnCmd) {
    $localMavenDir = Join-Path $PSScriptRoot ".maven"
    $localMvnBin = Join-Path $localMavenDir "apache-maven-3.9.9\bin"
    $localMvnExe = Join-Path $localMvnBin "mvn.cmd"

    if (-not (Test-Path $localMvnExe)) {
        Write-Host "[*] Maven not found on PATH. Downloading portable Apache Maven 3.9.9..." -ForegroundColor Yellow
        New-Item -ItemType Directory -Force -Path $localMavenDir | Out-Null
        $zipPath = Join-Path $localMavenDir "maven.zip"
        
        $mavenUrl = "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip"
        Invoke-WebRequest -Uri $mavenUrl -OutFile $zipPath -UseBasicParsing
        
        Write-Host "[*] Extracting portable Maven..." -ForegroundColor Yellow
        Expand-Archive -Path $zipPath -DestinationPath $localMavenDir -Force
        Remove-Item $zipPath -Force
        Write-Host "[✓] Portable Maven configured successfully." -ForegroundColor Green
    }

    $env:PATH = "$localMvnBin;" + $env:PATH
    $mvnExecutable = $localMvnExe
} else {
    $mvnExecutable = "mvn"
    Write-Host "[✓] System Maven detected." -ForegroundColor Green
}

# 3. Launch Spring Boot
Write-Host "`n[*] Starting Spring Boot Server..." -ForegroundColor Cyan
Write-Host "[*] Web Dashboard will be available at: http://localhost:8080" -ForegroundColor Yellow
Write-Host "[*] Press Ctrl+C at any time to stop the server.`n" -ForegroundColor Gray

& $mvnExecutable spring-boot:run
