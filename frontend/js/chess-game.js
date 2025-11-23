/**
 * Chess 4 Fun - Lógica JavaScript do Frontend
 * 
 * Este arquivo contém toda a lógica client-side do jogo de xadrez para 4 jogadores.
 * É responsável por:
 * - Gerenciar interações do usuário (cliques, seleção de peças)
 * - Comunicar-se com a API REST do backend
 * - Atualizar a interface visual
 * - Controlar o estado local do jogo
 * - Sincronizar dados entre frontend e backend
 * 
 * ARQUITETURA:
 * - Classe ChessGame: Controller principal da lógica do cliente
 * - Métodos assíncronos: Comunicação com API sem bloquear interface
 * - Event handlers: Resposta a cliques e ações do usuário
 * - Polling: Atualização automática do estado do jogo
 * - Validação local: Feedback imediato antes de comunicar com backend
 * 
 * TECNOLOGIAS UTILIZADAS:
 * - Fetch API: Requisições HTTP para o backend
 * - DOM Manipulation: Atualização da interface
 * - Event Listeners: Detecção de interações do usuário
 * - Async/Await: Programação assíncrona para operações de rede
 */

// Configurações das peças com símbolos visuais (Unicode)
// Usado para exibição no tabuleiro
const PIECE_SYMBOLS = {
    'PAWN': '♟',      // Peão
    'ROOK': '♜',      // Torre
    'KNIGHT': '♞',    // Cavalo
    'BISHOP': '♝',    // Bispo
    'QUEEN': '♛',     // Rainha
    'KING': '♚'       // Rei
};

/**
 * Classe principal do jogo de xadrez no frontend
 * 
 * RESPONSABILIDADES:
 * - Controlar estado local do jogo (peça selecionada, movimentos possíveis)
 * - Gerenciar interação do usuário com o tabuleiro
 * - Sincronizar com backend via API REST
 * - Atualizar interface visual baseada no estado do jogo
 * - Implementar validação de movimentos no cliente
 */
class ChessGame {
    /**
     * Construtor da classe
     * 
     * INICIALIZA:
     * - selectedPiece: Peça atualmente selecionada pelo usuário
     * - possibleMoves: Lista de movimentos possíveis para a peça selecionada
     * - currentPlayer: Jogador ativo (em memória local)
     * - gameState: Estado completo do jogo (em cache)
     */
    constructor() {
        this.selectedPiece = null;           // Peça selecionada pelo usuário
        this.possibleMoves = [];             // Movimentos possíveis da peça
        this.currentPlayer = 'GREEN';        // Jogador atual (heurística local)
        this.gameState = null;               // Estado completo do jogo
        this.init();                         // Inicializar o jogo
    }

    /**
     * Inicialização do jogo
     * 
     * CONFIGURA:
     * - Event listeners para cliques no tabuleiro
     * - Sistema de polling para atualizações automáticas
     * - Estado inicial da interface
     */
    init() {
        // Configurar listeners de eventos
        this.setupEventListeners();
        
        // Iniciar polling automático (atualizações periódicas)
        this.startPolling();
    }

    /**
     * Configura os event listeners da aplicação
     * 
     * EVENT LISTENERS:
     * - 'click': Cliques no tabuleiro para selecionar peças e fazer movimentos
     * - Cliques no background do modal para fechar modais
     * 
     * TÉCNICA:
     * - Event delegation: Um listener global captura todos os cliques
     * - Element filtering: Identifica se o clique foi em uma célula do tabuleiro
     */
    setupEventListeners() {
        // Event delegation para cliques no tabuleiro
        document.addEventListener('click', (e) => {
            // Verificar se o clique foi em uma célula do tabuleiro
            const cell = e.target.closest('.board-cell');
            if (cell) {
                // Processar clique na célula
                this.handleCellClick(cell);
            }
        });

        // Fechar modal de movimento ao clicar no background
        document.getElementById('move-modal').addEventListener('click', (e) => {
            // Se clicou no background (não no modal)
            if (e.target === e.currentTarget) {
                this.closeMoveModal();
            }
        });
    }

    /**
     * Manipula cliques em células do tabuleiro
     * 
     * LÓGICA DE SELEÇÃO:
     * 1. Se há uma peça selecionada e o clique é um movimento válido → Fazer movimento
     * 2. Se há uma peça na célula → Selecionar a peça
     * 3. Caso contrário → Limpar seleção
     * 
     * @param {Element} cell - Elemento HTML da célula clicada
     */
    async handleCellClick(cell) {
        // Extrair coordenadas da célula
        const row = parseInt(cell.dataset.row);
        const col = parseInt(cell.dataset.col);
        const piece = cell.dataset.piece;

        // CASO 1: Movimento válido - há uma peça selecionada e destino é válido
        if (this.selectedPiece && this.possibleMoves.some(move => move.row === row && move.col === col)) {
            // Executar o movimento
            await this.makeMove(this.selectedPiece.row, this.selectedPiece.col, row, col);
            this.clearSelection(); // Limpar seleção após o movimento
            return;
        }

        // CASO 2: Seleção de peça - célula contém uma peça
        if (piece && piece !== 'empty') {
            this.selectPiece(row, col, piece);
        } 
        // CASO 3: Limpar seleção - célula vazia ou peça inválida
        else {
            this.clearSelection();
        }
    }

    /**
     * Seleciona uma peça para mostrar seus movimentos possíveis
     * 
     * PROCESSO:
     * 1. Limpar seleção anterior (destacar célula anterior)
     * 2. Armazenar dados da nova peça selecionada
     * 3. Destacar visualmente a célula selecionada
     * 4. Carregar movimentos possíveis da API
     * 5. Destacar visualmente destinos possíveis
     * 
     * @param {number} row - Linha da peça
     * @param {number} col - Coluna da peça
     * @param {string} pieceData - Dados da peça (tipo, cor, etc.)
     */
    selectPiece(row, col, pieceData) {
        // Limpar seleção anterior (remover destaques visuais)
        this.clearSelection();
        
        // Armazenar dados da peça selecionada
        this.selectedPiece = { row, col, piece: pieceData };
        
        // Destacar visualmente a célula selecionada
        const cell = document.querySelector(`[data-row="${row}"][data-col="${col}"]`);
        cell.classList.add('cell-selected');
        
        // Carregar e destacar movimentos possíveis
        this.loadPossibleMoves(row, col);
    }

    /**
     * Limpa a seleção atual e destaques visuais
     * 
     * REMOVE:
     * - Destaque da célula selecionada (cell-selected)
     * - Destaque de movimentos possíveis (cell-move, cell-capture)
     * - Reset da peça selecionada
     * - Reset da lista de movimentos possíveis
     */
    clearSelection() {
        // Remover todas as classes de destaque
        document.querySelectorAll('.cell-selected, .cell-move, .cell-capture').forEach(cell => {
            cell.classList.remove('cell-selected', 'cell-move', 'cell-capture');
        });
        
        // Reset do estado
        this.selectedPiece = null;
        this.possibleMoves = [];
    }

    /**
     * Carrega movimentos possíveis para uma peça específica
     * 
     * PROCESSO:
     * 1. Fazer requisição para API REST: GET /api/moves/{row}/{col}
     * 2. Receber lista de movimentos possíveis
     * 3. Armazenar localmente para validação de cliques
     * 4. Destacar visualmente os destinos possíveis
     * 
     * @param {number} row - Linha da peça
     * @param {number} col - Coluna da peça
     */
    async loadPossibleMoves(row, col) {
        try {
            // Fazer requisição para o backend
            const response = await fetch(`/api/moves/${row}/${col}`);
            const moves = await response.json();
            
            // Armazenar movimentos para validação de cliques
            this.possibleMoves = moves || [];
            
            // Destacar visualmente destinos possíveis
            this.highlightPossibleMoves();
            
        } catch (error) {
            console.error('Erro ao carregar movimentos possíveis:', error);
            // Pode mostrar notificação para o usuário
        }
    }

    /**
     * Destaca visualmente os destinos possíveis para a peça selecionada
     * 
     * VISUAL:
     * - Movimentos de captura: Classe 'cell-capture' (células vermelhas)
     * - Movimentos normais: Classe 'cell-move' (células verdes)
     * 
     * O CSS define as cores e estilos visuais para cada tipo
     */
    highlightPossibleMoves() {
        this.possibleMoves.forEach(move => {
            // Encontrar célula de destino
            const cell = document.querySelector(`[data-row="${move.row}"][data-col="${move.col}"]`);
            if (cell) {
                // Aplicar classe baseada no tipo de movimento
                cell.classList.add(move.capture ? 'cell-capture' : 'cell-move');
            }
        });
    }

    /**
     * Executa um movimento no jogo
     * 
     * PROCESSO:
     * 1. Preparar dados do movimento
     * 2. Fazer requisição para API: POST /api/move
     * 3. Processar resposta do backend
     * 4. Atualizar interface se sucesso ou mostrar erro
     * 5. Recarregar estado do jogo
     * 
     * @param {number} fromRow - Linha de origem
     * @param {number} fromCol - Coluna de origem
     * @param {number} toRow - Linha de destino
     * @param {number} toCol - Coluna de destino
     */
    async makeMove(fromRow, fromCol, toRow, toCol) {
        try {
            // Preparar requisição HTTP
            const response = await fetch('/api/move', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                // Dados do movimento em formato JSON
                body: JSON.stringify({
                    fromRow, fromCol, toRow, toCol
                })
            });

            // Processar resposta do backend
            const result = await response.json();
            
            if (result.success) {
                // Movimento executado com sucesso
                this.showMessage('Movimento executado com sucesso!', 'success');
                this.refreshBoard(); // Recarregar estado do tabuleiro
            } else {
                // Movimento inválido
                this.showMessage(result.error || 'Movimento inválido!', 'error');
            }
            
        } catch (error) {
            console.error('Erro ao executar movimento:', error);
            this.showMessage('Erro ao executar movimento!', 'error');
        }
    }

    /**
     * Recarrega o estado completo do tabuleiro
     * 
     * ATUALIZA:
     * - HTML do tabuleiro de xadrez
     * - Estado do turno atual
     * - Status dos jogadores
     * - Histórico de jogos
     * - Aplica animação de fade-in para feedback visual
     */
    async refreshBoard() {
        try {
            // Recarregar tabuleiro principal
            const boardContainer = document.getElementById('chess-board');
            const response = await fetch('/api/board/state');
            const boardHTML = await response.text();
            
            // Atualizar conteúdo com animação
            boardContainer.innerHTML = boardHTML;
            boardContainer.classList.add('fade-in');
            
            // Recarregar dados complementares em paralelo
            await Promise.all([
                this.loadCurrentTurn(),
                this.loadPlayersStatus(),
                this.loadGameHistory()
            ]);
            
        } catch (error) {
            console.error('Erro ao recarregar tabuleiro:', error);
            this.showMessage('Erro ao atualizar interface!', 'error');
        }
    }

    /**
     * Carrega e atualiza informação do turno atual
     * 
     * ENDPOINT: GET /api/game/turn
     * 
     * ATUALIZA:
     * - Texto do elemento 'current-turn'
     * - Classe CSS para colorir baseado na cor do jogador
     */
    async loadCurrentTurn() {
        try {
            const response = await fetch('/api/game/turn');
            const data = await response.json();
            
            // Atualizar elemento visual do turno
            const turnElement = document.getElementById('current-turn');
            if (turnElement) {
                turnElement.textContent = `Vez do: ${this.getPlayerName(data.currentTurn)}`;
                turnElement.className = `px-4 py-2 rounded-lg font-bold turn-${data.currentTurn.toLowerCase()}`;
            }
            
        } catch (error) {
            console.error('Erro ao carregar turno atual:', error);
        }
    }

    /**
     * Carrega e atualiza status dos jogadores
     * 
     * ENDPOINT: GET /api/players/status
     * 
     * ATUALIZA:
     * - HTML da sidebar de jogadores
     * - Informações de eliminação, tempo restante, etc.
     */
    async loadPlayersStatus() {
        try {
            const response = await fetch('/api/players/status');
            const playersHTML = await response.text();
            
            // Atualizar sidebar de jogadores
            document.getElementById('players-status').innerHTML = playersHTML;
            
        } catch (error) {
            console.error('Erro ao carregar status dos jogadores:', error);
        }
    }

    /**
     * Carrega e atualiza histórico de movimentos
     * 
     * ENDPOINT: GET /api/game/history
     * 
     * ATUALIZA:
     * - HTML do container de histórico
     * - Lista de movimentos realizados
     * 
     * FEEDBACK:
     * - Scroll automático para mostrar movimento mais recente
     */
    async loadGameHistory() {
        try {
            const response = await fetch('/api/game/history');
            const historyHTML = await response.text();
            
            const historyContainer = document.getElementById('game-history');
            if (historyContainer) {
                historyContainer.innerHTML = historyHTML;
                // Scroll automático para o último movimento
                historyContainer.scrollTop = historyContainer.scrollHeight;
            }
            
        } catch (error) {
            console.error('Erro ao carregar histórico:', error);
        }
    }

    /**
     * Converte código de cor para nome em português
     * 
     * @param {string} color - Código da cor (RED, BLUE, GREEN, YELLOW)
     * @returns {string} Nome da cor em português
     */
    getPlayerName(color) {
        const names = {
            'RED': 'Vermelho',
            'BLUE': 'Azul',
            'GREEN': 'Verde',
            'YELLOW': 'Amarelo'
        };
        return names[color] || color;
    }

    /**
     * Exibe mensagem temporária para o usuário
     * 
     * TIPOS DE MENSAGEM:
     * - 'success': Operação bem-sucedida (fundo verde)
     * - 'error': Erro ou falha (fundo vermelho)
     * - 'info': Informação geral (fundo amarelo)
     * 
     * @param {string} message - Texto da mensagem
     * @param {string} type - Tipo da mensagem ('success', 'error', 'info')
     */
    showMessage(message, type = 'info') {
        const messageElement = document.getElementById('game-message');
        if (messageElement) {
            messageElement.textContent = message;
            
            // Aplicar estilo baseado no tipo
            messageElement.className = `px-4 py-3 rounded ${
                type === 'success' ? 'bg-green-100 border-green-400 text-green-700' :
                type === 'error' ? 'bg-red-100 border-red-400 text-red-700' :
                'bg-yellow-100 border-yellow-400 text-yellow-700'
            }`;
            
            // Mostrar mensagem
            messageElement.classList.remove('hidden');
            
            // Esconder automaticamente após 3 segundos
            setTimeout(() => {
                messageElement.classList.add('hidden');
            }, 3000);
        }
    }

    /**
     * Fecha o modal de movimento
     * 
     * Esconde o elemento modal adicionando classe 'hidden'
     */
    closeMoveModal() {
        const modal = document.getElementById('move-modal');
        if (modal) {
            modal.classList.add('hidden');
        }
    }

    /**
     * Inicia sistema de polling automático
     * 
     * POLLING:
     * - Atualiza estado do jogo a cada 3 segundos
     * - Garante sincronização com outros jogadores
     * - Atualiza automaticamente turnos e movimentos
     * 
     * INTERVALO:
     * - 3000ms = 3 segundos
     * - Balance entre responsividade e eficiência
     */
    startPolling() {
        // Configurar polling periódico para atualizações automáticas
        setInterval(() => {
            this.refreshBoard();
        }, 3000); // 3 segundos
    }

    /**
     * ===== MÉTODOS UTILITÁRIOS ESTÁTICOS =====
     * 
     * Estes métodos não dependem de estado da instância
     * e podem ser usados independentemente
     */

    /**
     * Extrai coordenadas de um elemento HTML
     * 
     * @param {Element} element - Elemento com atributos data-row e data-col
     * @returns {Object} Objeto com coordenadas {row, col}
     */
    static getBoardCoordinates(element) {
        return {
            row: parseInt(element.dataset.row),
            col: parseInt(element.dataset.col)
        };
    }

    /**
     * Valida se coordenadas estão dentro do tabuleiro
     * 
     * TABULEIRO: 14x14 para xadrez de 4 jogadores
     * 
     * @param {number} row - Linha
     * @param {number} col - Coluna
     * @returns {boolean} true se coordenadas são válidas
     */
    static isValidPosition(row, col) {
        return row >= 0 && row < 14 && col >= 0 && col < 14;
    }

    /**
     * Identifica se uma posição está em um canto do tabuleiro
     * 
     * CANTOS:
     * - São áreas inválidas para xadrez de 4 jogadores
     * - As peças não podem ser movidas para estas posições
     * 
     * @param {number} row - Linha
     * @param {number} col - Coluna
     * @returns {boolean} true se posição é um canto
     */
    static isCornerPosition(row, col) {
        // Coordenadas dos cantos cortados do tabuleiro
        const corners = [
            // Canto superior esquerdo
            [0, 0], [0, 1], [0, 2], [0, 3],
            [1, 0], [2, 0], [3, 0],
            
            // Canto superior direito
            [0, 10], [0, 11], [0, 12], [0, 13],
            [1, 13], [2, 13], [3, 13],
            
            // Canto inferior esquerdo
            [10, 0], [11, 0], [12, 0], [13, 0],
            
            // Canto inferior direito
            [10, 13], [11, 13], [12, 13], [13, 13],
            [13, 0], [13, 1], [13, 2], [13, 3],
            [13, 10], [13, 11], [13, 12], [13, 13]
        ];
        
        // Verificar se coordenadas coincidem com algum canto
        return corners.some(([r, c]) => r === row && c === col);
    }

    /**
     * Determina a cor visual de uma célula no tabuleiro
     * 
     * LÓGICA:
     * - Cantos: cor especial
     * - Outras células: padrão xadrez (xadrezado)
     * 
     * @param {number} row - Linha
     * @param {number} col - Coluna
     * @returns {string} Tipo da cor ('corner', 'light', 'dark')
     */
    static getCellColor(row, col) {
        if (ChessGame.isCornerPosition(row, col)) {
            return 'corner'; // Área cortada
        }
        // Padrão xadrez: células alternadas
        return (row + col) % 2 === 0 ? 'light' : 'dark';
    }

    /**
     * ===== MÉTODOS DE CONTROLE DO JOGO =====
     */

    /**
     * Inicia um novo jogo
     * 
     * PROCESSO:
     * 1. Enviar requisição para API: POST /api/game/new
     * 2. Receber confirmação do backend
     * 3. Recarregar interface para estado inicial
     * 4. Mostrar mensagem de confirmação
     */
    async newGame() {
        try {
            const response = await fetch('/api/game/new', { method: 'POST' });
            const result = await response.json();
            
            if (result.success) {
                this.showMessage('Novo jogo iniciado!', 'success');
                await this.refreshBoard();
            }
        } catch (error) {
            console.error('Erro ao iniciar novo jogo:', error);
            this.showMessage('Erro ao iniciar novo jogo!', 'error');
        }
    }

    /**
     * Reinicia o jogo atual
     * 
     * PROCESSO:
     * 1. Enviar requisição para API: POST /api/game/reset
     * 2. Receber confirmação do backend
     * 3. Recarregar interface para estado inicial
     * 4. Mostrar mensagem de confirmação
     */
    async resetGame() {
        try {
            const response = await fetch('/api/game/reset', { method: 'POST' });
            const result = await response.json();
            
            if (result.success) {
                this.showMessage('Jogo reiniciado!', 'success');
                await this.refreshBoard();
            }
        } catch (error) {
            console.error('Erro ao reiniciar jogo:', error);
            this.showMessage('Erro ao reiniciar jogo!', 'error');
        }
    }
}

/**
 * Inicialização da aplicação
 * 
 * EXECUTADO:
 * - Quando o DOM é completamente carregado
 * - Garante que todos os elementos HTML existem antes de inicializar
 * 
 * AÇÃO:
 * - Cria instância global do jogo
 * - Torna disponível via window.chessGame para debug/testes
 */
document.addEventListener('DOMContentLoaded', () => {
    // Criar instância global do jogo
    window.chessGame = new ChessGame();
    
    // Log para debug (remover em produção)
    console.log('Chess 4 Fun inicializado com sucesso!');
});

/**
 * Export para módulos (se necessário)
 * Permite import em outros scripts se usado como módulo
 */
if (typeof module !== 'undefined' && module.exports) {
    module.exports = ChessGame;
}