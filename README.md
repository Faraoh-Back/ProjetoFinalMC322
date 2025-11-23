### ProjetoFinalMC322

## Início Rápido

bash

# 1. Clonar e entrar no diretório
git clone <repo-url>
cd ProjetoFinalMC322/app

# 2. Compilar e executar
./setup_chess_4_fun.sh

# 3. Testar em outro terminal
curl "http://localhost:8080/api/state"

## Testando a integração

Cenários de Teste Detalhados

Cenário 1: Teste Completo de Partida

bash

# 1. Criar jogo
curl -X POST "http://localhost:8080/api/games" \
  -H "Content-Type: application/json" \
  -d '{"playerNames": ["Alice", "Bob", "Charlie", "Diana"]}'

# 2. Copiar o gameId da resposta

# 3. Sequência de movimentos de teste
# Turno 1 - Alice (Vermelho)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e2","to":"e4"}'

# Turno 2 - Bob (Azul)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"e9","to":"e7"}'

# Turno 3 - Charlie (Verde)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player3","from":"e2","to":"e4"}'

# Turno 4 - Diana (Amarelo)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player4","from":"e9","to":"e7"}'

# 4. Verificar estado
curl -X GET "http://localhost:8080/api/games/{gameId}/state"


Cenário 2: Teste de Roque (Castling)

bash

# Preparar posição para roque
# Mover peças para posições adequadas
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"g1","to":"f3"}'

curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"b8","to":"c6"}'

# Tentar roque
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e1","to":"g1"}'


Cenário 3: Teste de En Passant

bash

# 1. Alice.move e2 para e4 (peão anda 2 casas)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e2","to":"e4"}'

# 2. Bob.move e9 para e7
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"e9","to":"e7"}'

# 3. Charlie.move e2 para e4 (para en passant)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player3","from":"e2","to":"e4"}'

# 4. Diana.move e9 para e7
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player4","from":"e9","to":"e7"}'

# 5. Bob captura en passant
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"e7","to":"e5"}'

# 6. Alice captura en passant
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e4","to":"e5"}'


Cenário 4: Teste de Movimentos Inválidos

bash

# Movimento inválido - tentar mover peça do opponent
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e7","to":"e5"}'

# Movimento inválido - fora do turno
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"g1","to":"f3"}'

# Movimento inválido - posição inexistente
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"z1","to":"z3"}'


Cenário 5: Teste de Check e Checkmate

bash

# Sequência que leva ao check (exemplo simplificado)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"e2","to":"e4"}'

curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"f9","to":"h6"}'

# Verificar se está em check
curl -X GET "http://localhost:8080/api/games/{gameId}/state" | grep -i "check"

# Movimento que continua o ataque
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"d1","to":"h5"}'


🧪 Testes de Regras Específicas do Xadrez 4

Teste: Turnos em Ordem

bash

# Verificar ordem dos turnos (Alice -> Bob -> Charlie -> Diana)
for i in {1..4}; do
  echo "Turno $i:"
  curl -X GET "http://localhost:8080/api/games/{gameId}/state" | grep "currentPlayer"
  sleep 1
done


Teste: Validação de Posições do Tabuleiro 4 Jogadores

bash

# Verificar se o tabuleiro tem 4 lados corretos
curl -X GET "http://localhost:8080/api/games/{gameId}/state" | jq '.data.board.sides'

# Verificar posições iniciais das peças
curl -X GET "http://localhost:8080/api/games/{gameId}/state" | jq '.data.board.pieces'


Teste: Movimentos por Tipo de Peça

bash

# Movimento de Torre (horizontal/vertical)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player1","from":"a1","to":"a5"}'

# Movimento de Bispo (diagonal)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player2","from":"c8","to":"f5"}'

# Movimento de Cavalo (L)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player3","from":"g1","to":"f3"}'

# Movimento de Rainha (livre)
curl -X POST "http://localhost:8080/api/games/{gameId}/move" \
  -H "Content-Type: application/json" \
  -d '{"playerId":"player4","from":"d8","to":"d5"}'


📊 Scripts de Teste Automatizado

Script Completo de Validação

bash

#!/bin/bash
echo "🧪 Teste Automatizado Completo - Xadrez 4 Jogadores"

# Criar jogo
echo "Criando jogo..."
GAME_RESPONSE=$(curl -s -X POST "http://localhost:8080/api/games" \
  -H "Content-Type: application/json" \
  -d '{"playerNames": ["Alice", "Bob", "Charlie", "Diana"]}')

GAME_ID=$(echo $GAME_RESPONSE | grep -o '"id":"[^"]*"' | cut -d'"' -f4)
echo "Jogo criado: $GAME_ID"

# Teste de 10 movimentos
MOVES=(
  '{"playerId":"player1","from":"e2","to":"e4"}'
  '{"playerId":"player2","from":"e9","to":"e7"}'
  '{"playerId":"player3","from":"e2","to":"e4"}'
  '{"playerId":"player4","from":"e9","to":"e7"}'
  '{"playerId":"player1","from":"g1","to":"f3"}'
  '{"playerId":"player2","from":"b8","to":"c6"}'
  '{"playerId":"player3","from":"f1","to":"c4"}'
  '{"playerId":"player4","from":"f9","to":"c6"}'
  '{"playerId":"player1","from":"d1","to":"f3"}'
  '{"playerId":"player2","from":"c6","to":"d4"}'
)

for i in "${!MOVES[@]}"; do
  echo "Movimento $((i+1)): ${MOVES[$i]}"
  curl -s -X POST "http://localhost:8080/api/games/$GAME_ID/move" \
    -H "Content-Type: application/json" \
    -d "${MOVES[$i]}"
  echo ""
done

# Verificar estado final
echo "Estado final do jogo:"
curl -s -X GET "http://localhost:8080/api/games/$GAME_ID/state"