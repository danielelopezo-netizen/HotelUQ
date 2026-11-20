$env:JAVA_HOME = "C:\Program Files\Java\jdk-17"
$mvnPath = "C:\Program Files\JetBrains\IntelliJ IDEA 2024.3.4.1\plugins\maven\lib\maven3\bin"
$env:PATH = "$env:JAVA_HOME\bin;$mvnPath;$env:PATH"

Write-Host "Iniciando HotelUQ..." -ForegroundColor Green
& "$mvnPath\mvn.cmd" compile exec:java
