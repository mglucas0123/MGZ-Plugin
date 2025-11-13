#!/bin/bash

echo "========================================"
echo "  SERVIDOR MINECRAFT COM AUTO-RESTART"
echo "========================================"
echo ""
echo "Este script reinicia automaticamente o servidor"
echo "quando ele desliga (por comando ou crash)."
echo ""
echo "Pressione Ctrl+C para PARAR o servidor permanentemente"
echo "========================================"
echo ""

# Configurações de memória
MIN_RAM="2G"
MAX_RAM="4G"

# Nome do arquivo JAR do servidor
SERVER_JAR="server.jar"

while true
do
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Iniciando servidor..."
    
    # Executar o servidor
    java -Xms$MIN_RAM -Xmx$MAX_RAM -jar $SERVER_JAR nogui
    
    echo ""
    echo "========================================"
    echo "[$(date '+%Y-%m-%d %H:%M:%S')] Servidor desligado!"
    echo "Aguardando 5 segundos antes de reiniciar..."
    echo "========================================"
    echo ""
    
    sleep 5
done
