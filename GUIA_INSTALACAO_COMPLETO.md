# Guia Completo de Instalação e Teste - Jogo de Xadrez 4 Jogadores

## 📋 Pré-requisitos

- Java 17 ou superior
- Gradle (incluído no projeto)
- Git
- Terminal/PowerShell

## 🚀 Passo a Passo de Instalação

### 1. Clonar o Repositório
```bash
git clone <url-do-repositorio>
cd ProjetoFinalMC322
```

### 2. Verificar Estrutura do Projeto
```bash
ls -la
# Deve mostrar: app/ build.gradle.kts settings.gradle.kts
```

### 3. Entrar no Diretório da Aplicação
```bash
cd app
```

### 4. Compilar o Projeto
```bash
./gradlew clean build
```
**Resultado esperado:** `BUILD SUCCESSFUL`

### 5. Executar o Servidor
```bash
./gradlew bootRun
```
**Resultado esperado:** 
```
Started App in X.XXX seconds
```

### 6. Verificar se o Servidor está Rodando
Em outro terminal:
```bash
curl "http://localhost:8080/api/state"
```
**Resultado esperado:** `{"meta":{"rc":"error","msg":"api.err.LoginRequired"},"data":[]}`

## 🧪 Testes da API

### Teste 1: Verificar Estado do Servidor
```bash
curl -X GET "http://localhost:8080/api/state"
```
**Esperado:** Erro de login (significa que servidor está funcionando)

### Teste 2: Listar Jogadores
```bash
curl -X GET "http://localhost:8080/api/players"
```

### Teste 3: Criar Novo Jogo
```bash
curl -X POST "http://localhost:8080/api/games" \
  -H "Content-Type: application/json" \
  -d '{
    "playerNames": ["Jogador1", "Jogador2", "Jogador3", "Jogador4"]
  }'
```

### Teste 4: Fazer uma Jogada
```bash
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{
    "playerId": "player1",
    "from": "e2",
    "to": "e4"
  }'
```

### Teste 5: Obter Estado do Jogo
```bash
curl -X GET "http://localhost:8080/api/games/{gameId}/state"
```

## 🎮 Testes de Funcionalidade

### Teste de Movimento de Peças
1. **Movimento Básico de Peão:**
   ```bash
   # Mover peão de e2 para e4
   curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
     -H "Content-Type: application/json" \
     -d '{"playerId":"player1","from":"e2","to":"e4"}'
   ```

2. **Movimento de Torre:**
   ```bash
   # Mover torre de a1 para a3
   curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
     -H "Content-Type: application/json" \
     -d '{"playerId":"player1","from":"a1","to":"a3"}'
   ```

3. **Movimento de Cavalo:**
   ```bash
   # Mover cavalo de g1 para f3
   curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
     -H "Content-Type: application/json" \
     -d '{"playerId":"player1","from":"g1","to":"f3"}'
   ```

### Teste de Regras Especiais

#### Roque (Castling)
```bash
# Roque curto (rei e torre)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e1","to":"g1"}'
```

#### En Passant
```bash
# Captura en passant
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e5","to":"d6"}'
```

## 🔧 Solução de Problemas

### Problema: "BUILD FAILED"
**Solução:**
```bash
cd app
./gradlew clean build --refresh-dependencies
```

### Problema: "Port 8080 already in use"
**Solução:**
```bash
# Encontrar processo usando a porta
lsof -i :8080
# Matar o processo
kill -9 <PID>
```

### Problema: Nenhum jogo encontrado
**Solução:**
1. Crie um jogo primeiro com `/api/games`
2. Use o ID retornado nas chamadas subsequentes

### Problema: "Invalid move"
**Solução:**
1. Verifique se a notação da posição está correta (ex: e2, e4)
2. Confirme se é a vez do jogador
3. Verifique se a peça pode se mover para a posição desejada

## 📊 Status Codes da API

- **200:** Sucesso
- **400:** Dados inválidos na requisição
- **404:** Recurso não encontrado
- **500:** Erro interno do servidor

## 🎯 Checklist de Funcionalidades

- [ ] Servidor inicia sem NullPointerException
- [ ] API responde com erro de login (significa que está funcionando)
- [ ] Criação de jogos funciona
- [ ] Movimentos básicos de peças funcionam
- [ ] Validação de turnos funciona
- [ ] Roque funciona
- [ ] En Passant funciona
- [ ] Check/Checkmate funciona

## 🚨 Erros Conhecidos Corrigidos

✅ **NullPointerException em Pawn.java (linha 57)** - CORRIGIDO
✅ **NullPointerException em History.java (linha 59)** - CORRIGIDO  
✅ **NullPointerException em King.java (linha 96, 110, 113)** - CORRIGIDO
✅ **NullPointerException em King.java (linha 43)** - CORRIGIDO

## 📞 Suporte

Se encontrar algum problema não coberto neste guia:
1. Verifique se todos os pré-requisitos estão instalados
2. Execute `./gradlew clean build` para recompilar
3. Verifique os logs do servidor para erros específicos
4. Confirme se a porta 8080 está disponível