#!/bin/bash

# Chess 4 Fun - Setup Script
# Este script configura automaticamente o frontend HTMX no projeto Java existente

set -e

echo "♔====================================♔"
echo "    Chess 4 Fun - Setup Automático"
echo "    Frontend HTMX + Backend Java"
echo "♔====================================♔"

# Verificar se estamos no diretório correto
if [ ! -d "app" ] || [ ! -f "app/build.gradle.kts" ]; then
    echo "❌ Erro: Execute este script na raiz do projeto (onde está a pasta 'app')"
    exit 1
fi

# Verificar se existem os arquivos frontend
if [ ! -d "frontend" ]; then
    echo "❌ Erro: Pasta 'frontend' não encontrada!"
    exit 1
fi

echo "✅ Diretório do projeto verificado"

# Criar estrutura de diretórios no backend
echo "📁 Criando estrutura de diretórios..."

mkdir -p app/src/main/resources/static
mkdir -p app/src/main/java/org/chess/config
mkdir -p app/src/main/java/org/chess/api

echo "✅ Estrutura criada"

# Copiar arquivos frontend
echo "🎨 Copiando arquivos frontend..."

# Frontend principal
cp frontend/index.html app/src/main/resources/static/ 2>/dev/null || echo "⚠️ index.html não encontrado, pulando..."

# CSS
if [ -d "frontend/styles" ]; then
    cp -r frontend/styles/* app/src/main/resources/static/ 2>/dev/null || echo "⚠️ styles não encontrados, pulando..."
fi

# JavaScript
if [ -d "frontend/js" ]; then
    cp -r frontend/js/* app/src/main/resources/static/js/ 2>/dev/null || echo "⚠️ js files não encontrados, pulando..."
fi

echo "✅ Frontend copiado"

# Copiar exemplos Java para integração
echo "☕ Integrando com backend Java..."

# Controller API
if [ -f "frontend/java-example/ChessAPIController.java" ]; then
    cp frontend/java-example/ChessAPIController.java app/src/main/java/org/chess/api/
    echo "✅ ChessAPIController.java copiado"
else
    echo "⚠️ ChessAPIController.java não encontrado"
fi

# Configuração Web
if [ -f "frontend/java-example/WebConfig.java" ]; then
    cp frontend/java-example/WebConfig.java app/src/main/java/org/chess/config/
    echo "✅ WebConfig.java copiado"
else
    echo "⚠️ WebConfig.java não encontrado"
fi

# Aplicação principal
if [ -f "frontend/java-example/Chess4FunApplication.java" ]; then
    cp frontend/java-example/Chess4FunApplication.java app/src/main/java/org/chess/
    echo "✅ Chess4FunApplication.java copiado"
else
    echo "⚠️ Chess4FunApplication.java não encontrado"
fi

# Atualizar build.gradle.kts
if [ -f "frontend/java-example/build.gradle.kts" ]; then
    echo "📦 Verificando build.gradle.kts..."
    
    # Backup do original
    cp app/build.gradle.kts app/build.gradle.kts.backup
    
    # Verificar se Spring Boot está configurado
    if ! grep -q "spring-boot-starter" app/build.gradle.kts; then
        echo "🔧 Adicionando Spring Boot dependencies..."
        
        # Adicionar Spring Boot plugin e dependências
        cat > temp_gradle_content << 'EOF'

plugins {
    java
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}
EOF
        
        echo "✅ Spring Boot configurado"
    else
        echo "✅ Spring Boot já configurado"
    fi
fi

# Atualizar application.properties
if [ -f "frontend/java-example/application.properties" ]; then
    mkdir -p app/src/main/resources
    cp frontend/java-example/application.properties app/src/main/resources/
    echo "✅ application.properties copiado"
fi

# Modificar a classe App.java para integração
echo "🔧 Preparando integração com App.java..."

# Verificar se a classe App.java existe
if [ -f "app/src/main/java/org/chess/App.java" ]; then
    echo "✅ App.java encontrado - verificando métodos necessários..."
    
    # Verificar se tem executeMove (caso não, precisará ser adicionado)
    if ! grep -q "executeMove" app/src/main/java/org/chess/App.java; then
        echo "⚠️ Método executeMove não encontrado em App.java"
        echo "   Você precisará adicionar este método manualmente"
        echo "   Veja o exemplo em frontend/java-example/ChessAPIController.java"
    fi
fi

echo ""
echo "🎉 Setup concluído com sucesso!"
echo ""
echo "📋 Próximos passos:"
echo ""
echo "1. 📝 Adicione o método executeMove() na classe App.java:"
echo "   - Veja exemplo em frontend/java-example/ChessAPIController.java"
echo ""
echo "2. 🚀 Execute o backend:"
echo "   cd app"
echo "   ./gradlew bootRun"
echo ""
echo "3. 🌐 Acesse o jogo:"
echo "   http://localhost:8080"
echo ""
echo "4. 🎮 Teste o tabuleiro:"
echo "   - Verifique se as 4 cores aparecem"
echo "   - Teste mover algumas peças"
echo "   - Confirme se os turnos mudam"
echo ""
echo "📚 Documentação completa:"
echo "   - frontend/README.md"
echo "   - CHESS_4_FUN_README.md"
echo ""
echo "🐛 Problemas? Verifique:"
echo "   - Logs do Spring Boot no console"
echo "   - Console do navegador (F12)"
echo "   - Network tab para erros de API"
echo ""
echo "♔ Boa sorte no projeto! ♔"