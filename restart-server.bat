@echo off
title Servidor Minecraft - Auto Restart
echo ========================================
echo   SERVIDOR MINECRAFT COM AUTO-RESTART
echo ========================================
echo.
echo Este script reinicia automaticamente o servidor
echo quando ele desliga (por comando ou crash).
echo.
echo Pressione Ctrl+C para PARAR o servidor permanentemente
echo ========================================
echo.

:start
echo [%date% %time%] Iniciando servidor...

REM Configurações de memória
set MIN_RAM=2G
set MAX_RAM=4G

REM Nome do arquivo JAR do servidor
set SERVER_JAR=server.jar

REM Executar o servidor
java -Xms%MIN_RAM% -Xmx%MAX_RAM% -jar %SERVER_JAR% nogui

echo.
echo ========================================
echo [%date% %time%] Servidor desligado!
echo Aguardando 5 segundos antes de reiniciar...
echo ========================================
echo.

timeout /t 5 /nobreak

goto start
