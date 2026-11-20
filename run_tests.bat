@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-17"
set "JAVAC=%JAVA_HOME%\bin\javac.exe"
set "JAVA=%JAVA_HOME%\bin\java.exe"

if not exist target\classes mkdir target\classes

echo [1/3] Listando clases del dominio y patrones...
dir /s /b src\main\java\co\edu\uniquindio\hotel\model\*.java > sources_core.txt
dir /s /b src\main\java\co\edu\uniquindio\hotel\exceptions\*.java >> sources_core.txt
dir /s /b src\main\java\co\edu\uniquindio\hotel\patterns\*.java >> sources_core.txt
dir /s /b src\main\java\co\edu\uniquindio\hotel\service\*.java >> sources_core.txt

echo [2/3] Compilando logica de negocio y patrones GoF...
"%JAVAC%" -encoding UTF-8 -d target\classes @sources_core.txt
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Fallo en la compilacion de la logica central.
    exit /b 1
)

echo [3/3] Compilando y ejecutando suite de pruebas automatizadas...
"%JAVAC%" -encoding UTF-8 -cp target\classes -d target\classes src\test\java\co\edu\uniquindio\hotel\TestRunner.java
if %ERRORLEVEL% neq 0 (
    echo [ERROR] Fallo en la compilacion de pruebas.
    exit /b 1
)

"%JAVA%" -cp target\classes co.edu.uniquindio.hotel.TestRunner
