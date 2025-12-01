# 🏆 Chess4Fun - Xadrez para 4 Jogadores

Um jogo de xadrez para 4 jogadores implementado em Java, com funcionalidade completa de salvar/carregar partidas e interface web moderna.

## 🎮 Sobre o Projeto

O Chess4Fun é uma variação do xadrez tradicional que permite até 4 jogadores simultâneos. Cada jogador controla um conjunto de peças com cores distintas (Vermelho, Amarelo, Verde, Azul) e o objetivo é eliminar todos os oponentes ou dar xeque-mate no rei de cada cor.

## ✨ Principais Funcionalidades

### 🎯 Core do Jogo
- **Jogo para 4 Jogadores**: Sistema completo de 4 cores (Vermelho, Amarelo, Verde, Azul)
- **Todas as Peças Implementadas**: Rei, Rainha, Torre, Bispo, Cavalo e Peão com movimentos corretos
- **Sistema de Captura**: Mecânica completa de captura de peças
- **Xeque e Xeque-mate**: Detecção automática de condições de xeque e finalização do jogo

### 💾 Sistema de Persistência
- **Salvar Partidas**: Grave o estado completo do jogo a qualquer momento
- **Carregar Partidas**: Continue jogos salvos com todos os dados preservados
- **Gerenciar Saves**: Liste, carregue ou exclua partidas salvas
- **Serialização Java**: Utiliza ObjectOutputStream/ObjectInputStream para persistência

### 🎨 Interface Web
- **Interface Moderna**: Interface web responsiva usando Spark Framework
- **Rotas RESTful**: API completa para controle do jogo via HTTP
- **Servidor Embutido**: Jetty server integrado na porta 8080
- **Recursos Estáticos**: Serve arquivos estáticos de /public

### 🔧 Arquitetura Técnica
- **Padrão Factory**: Criação de peças usando PieceType enum
- **Herança Bem Definida**: Hierarquia de classes para cada tipo de peça
- **Enum Color**: Sistema de cores robusto para os 4 jogadores
- **Serializable**: Todas as classes principais implementam Serializable

## 🚀 Como Executar

### Pré-requisitos
- Java 17 ou superior
- Gradle (incluído no projeto)

### 1. Clonar o Repositório
```bash
git clone <URL_DO_REPOSITORIO>
cd chess4fun-project
```

### 2. Verificar Versão do Gradle
```bash
./gradlew --version
```
Se não funcionar, você pode usar o gradle instalado no sistema:
```bash
gradle --version
```

### 3. Limpar Build Anterior (Opcional)
```bash
./gradlew app:clean
# ou se usar gradle do sistema:
gradle app:clean
```

### 4. Compilar o Projeto
```bash
./gradlew app:build
# ou se usar gradle do sistema:
gradle app:build
```

### 5. Executar o Jogo
```bash
./gradlew app:run
# ou se usar gradle do sistema:
gradle app:run
```

### 6. Acessar o Jogo
Abra seu navegador em: **http://localhost:8080**

## 🛠️ Resolução de Problemas

### ❌ Erro: "Address already in use" na porta 8080

**Problema**: Outro processo está usando a porta 8080.

**Soluções**:

#### Opção 1: Encontrar e Parar o Processo
```bash
# Encontre qual processo está usando a porta 8080
lsof -i :8080
# ou
netstat -tulpn | grep 8080
# ou
sudo ss -lptn 'sport = :8080'

# Mate o processo (substitua [PID] pelo número encontrado)
kill -9 [PID]
```

#### Opção 2: Usar Sistema Operacional

**No Linux/Mac**:
```bash
# Mate todos os processos Java rodando
pkill -f java

# Ou mate especificamente o processo na porta 8080
fuser -k 8080/tcp
```

**No Windows**:
```cmd
# Encontre o PID usando a porta 8080
netstat -ano | findstr :8080

# Mate o processo (substitua [PID] pelo número encontrado)
taskkill /PID [PID] /F
```

#### Opção 3: Usar Porta Diferente
Edite o arquivo `src/main/java/org/chess/web/Main.java` e adicione:
```java
// Antes das definições de rota, configure a porta:
Spark.port(8081); // ou qualquer outra porta disponível
```

### ❌ Erro: "Permission denied" no gradlew

**Solução**:
```bash
# Dê permissão de execução ao gradlew
chmod +x ./gradlew
```

### ❌ Erro: "Java not found"

**Solução**:
```bash
# Instale Java 17 (Ubuntu/Debian)
sudo apt install openjdk-17-jdk

# Instale Java 17 (CentOS/RHEL)
sudo yum install java-17-openjdk-devel

# Instale Java 17 (macOS)
brew install openjdk@17
```

### ✅ Build com Sucesso
Se você ver `BUILD SUCCESSFUL` ou `BUILD SUCCESSFUL in 2s`, a compilação funcionou!

### ✅ Servidor Rodando
Se você ver:
```
INFO spark.staticfiles.StaticFilesConfiguration - StaticResourceHandler configured with folder = /public
INFO org.eclipse.jetty.server.AbstractConnector - Started ServerConnector@743be17e{HTTP/1.1, (http/1.1)}{0.0.0.0:8080}
INFO org.eclipse.jetty.server.Server - Started @165ms
```

O servidor está rodando perfeitamente! 🎉

## 📁 Estrutura do Projeto

```
chess4fun-project/
├── src/main/java/org/chess/
│   ├── App.java              # Lógica principal do jogo
│   ├── web/Main.java         # Servidor Spark e rotas HTTP
│   ├── Player.java           # Classe Jogador
│   ├── Pos.java              # Coordenadas do tabuleiro
│   ├── Move.java             # Movimentação das peças
│   ├── board/Board.java      # Tabuleiro principal
│   ├── pieces/
│   │   ├── Piece.java        # Classe base das peças
│   │   ├── King.java         # Rei
│   │   ├── Queen.java        # Rainha
│   │   ├── Rook.java         # Torre
│   │   ├── Bishop.java       # Bispo
│   │   ├── Knight.java       # Cavalo
│   │   └── Pawn.java         # Peão
│   └── exception/InvalidPosition.java  # Exceção para posições inválidas
├── build.gradle.kts          # Configuração Gradle
├── settings.gradle.kts       # Configurações do projeto
└── public/                   # Arquivos estáticos web
```

## 🎯 Funcionalidades Implementadas

### Sistema de Jogo
- ✅ Tabuleiro 14x14 com áreas específicas para cada jogador
- ✅ 4 jogadores com cores distintas
- ✅ Movimentos válidos para todas as peças
- ✅ Sistema de captura de peças
- ✅ Detecção de xeque e xeque-mate
- ✅ Eliminação de jogadores

### Persistência
- ✅ Salvar estado completo do jogo
- ✅ Carregar jogos salvos
- ✅ Listar partidas disponíveis
- ✅ Excluir saves antigos

### Interface
- ✅ API RESTful completa
- ✅ Interface web responsiva
- ✅ Rotas para todas as operações
- ✅ Servidor integrado

## 🏁 Pronto para Jogar!

Agora que você sabe como executar o projeto, é só:

1. **Resolver o problema da porta 8080** (se necessário)
2. **Executar os comandos** na ordem correta
3. **Abrir http://localhost:8080** no navegador
4. **Começar a jogar** com até 3 amigos!

**Boa sorte nas suas partidas de Chess4Fun!** ♟️🎉