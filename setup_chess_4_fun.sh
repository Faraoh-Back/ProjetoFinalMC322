#!/bin/bash

#  Script de Setup Automático - Chess 4 Fun
# Este script automatiza toda a configuração e execução

set -e  # Parar em qualquer erro

echo "  Chess 4 Fun - Setup Automático"
echo "=================================="

PROJECT_PATH="/home/dev/GitPessoal/ProjetoFinalMC322"

# Cores para output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

log_info() {
    echo -e "${GREEN}✅ $1${NC}"
}

log_warn() {
    echo -e "${YELLOW}⚠️  $1${NC}"
}

log_error() {
    echo -e "${RED}❌ $1${NC}"
}

# Função para verificar se comando existe
command_exists() {
    command -v "$1" >/dev/null 2>&1
}

echo ""
echo "🔍 Verificando pré-requisitos..."

# Verificar Java
if command_exists java; then
    JAVA_VERSION=$(java -version 2>&1 | head -n 1 | cut -d'"' -f2)
    log_info "Java encontrado: $JAVA_VERSION"
else
    log_error "Java não encontrado! Instalando..."
    sudo apt update
    sudo apt install -y openjdk-17-jdk
fi

# Verificar Gradle wrapper
if [ -f "$PROJECT_PATH/gradlew" ]; then
    log_info "Gradle wrapper encontrado"
    chmod +x "$PROJECT_PATH/gradlew"
else
    log_error "Gradle wrapper não encontrado em $PROJECT_PATH"
    log_info "Por favor, execute este script dentro da pasta do projeto"
    exit 1
fi

# Verificar estrutura do projeto
echo ""
echo "📁 Verificando estrutura do projeto..."

cd "$PROJECT_PATH"

# Criar pastas se não existirem
mkdir -p frontend/js
mkdir -p frontend/styles
mkdir -p java

# Copiar arquivos se existirem no workspace
if [ -d "/workspace/frontend" ]; then
    log_info "Copiando arquivos frontend..."
    cp -f /workspace/frontend/index.html frontend/ 2>/dev/null || true
    cp -f /workspace/frontend/js/*.js frontend/js/ 2>/dev/null || true
    cp -f /workspace/frontend/styles/*.css frontend/styles/ 2>/dev/null || true
fi

if [ -f "/workspace/CorrectedChessAPIController.java" ]; then
    log_info "Copiando controller Java..."
    cp -f /workspace/CorrectedChessAPIController.java java/
fi

if [ -f "/workspace/build.gradle.kts" ]; then
    log_info "Configurando build.gradle.kts..."
    cp -f /workspace/build.gradle.kts app/
fi

if [ -f "/workspace/user_input_files/Chess4FunApplication.java" ]; then
    log_info "Copiando aplicação Spring..."
    cp -f /workspace/user_input_files/Chess4FunApplication.java java/
fi

if [ -f "/workspace/user_input_files/WebConfig.java" ]; then
    log_info "Copiando configuração web..."
    cp -f /workspace/user_input_files/WebConfig.java java/
fi

# Verificar estrutura final
echo ""
echo "📋 Estrutura do projeto:"
ls -la app/ 2>/dev/null | grep -E "(build\.gradle|src)" || true
ls -la frontend/ 2>/dev/null | head -5 || true
ls -la java/ 2>/dev/null | head -5 || true

echo ""
echo "🛠️  Preparando dependências..."

# Parar processos na porta 8080
if lsof -ti:8080 >/dev/null 2>&1; then
    log_warn "Porta 8080 em uso. Liberando..."
    sudo lsof -ti:8080 | xargs kill -9 2>/dev/null || true
    sleep 2
fi

# Build do projeto
echo ""
echo "🔨 Compilando projeto..."
if ./gradlew clean build --quiet; then
    log_info "Build concluído com sucesso!"
else
    log_error "Erro no build. Tentando com mais detalhes..."
    ./gradlew clean build --info
    exit 1
fi

echo ""
echo "🚀 Iniciando aplicação..."
log_info "Aplicação será iniciada em http://localhost:8080"
log_info "Pressione Ctrl+C para parar"

# Função para limpar ao sair
cleanup() {
    echo ""
    log_info "Parando aplicação..."
    kill $APP_PID 2>/dev/null || true
    exit 0
}
trap cleanup SIGINT

# Iniciar aplicação em background
./gradlew bootRun &
APP_PID=$!

# Aguardar aplicativo inicializar
echo ""
echo "⏳ Aguardando inicialização..."
sleep 10

# Testar se aplicação está funcionando
if curl -s "http://localhost:8080/api/state" >/dev/null; then
    log_info " Aplicação funcionando!"
    echo ""
    echo " URLs para testar:"
    echo "   • http://localhost:8080"
    echo "   • http://localhost:8080/game"
    echo "   • http://localhost:8080/api/state"
    echo ""
    echo " Testando endpoints..."
    
    # Testes básicos
    echo ""
    echo " Estado do jogo:"
    curl -s "http://localhost:8080/api/state" | head -c 100 || echo "Erro na API"
    
    echo ""
    echo " Turno atual:"
    curl -s "http://localhost:8080/api/turn" | head -c 100 || echo "Erro na API"
    
    echo ""
    echo ""
    log_info " Setup concluído! Acesse http://localhost:8080 no navegador"
    
    # Manter script rodando
    wait $APP_PID
else
    log_error " Aplicação não respondeu. Verifique os logs:"
    echo "   ./gradlew bootRun --info"
    kill $APP_PID 2>/dev/null || true
    exit 1
fi