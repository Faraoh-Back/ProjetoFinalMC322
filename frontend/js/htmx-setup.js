/**
 * HTMX Configuration and Setup - Chess 4 Fun
 * 
 * Este arquivo configura e estende a biblioteca HTMX para o jogo de xadrez.
 * HTMX é uma biblioteca que permite criar interfaces dinâmicas sem JavaScript complexo.
 * 
 * FUNCIONALIDADES PRINCIPAIS:
 * - Configuração global do HTMX
 * - Event handlers para requisições
 * - Loading states e animações
 * - Tratamento de erros
 * - Sistema de polling automático
 * - Integração com WebSocket (opcional)
 * 
 * ARQUITETURA:
 * - Event listeners para ciclo de vida de requisições
 * - Helpers utilitários para operações comuns
 * - Polling inteligente para atualizações automáticas
 * - Error handling robusto
 * 
 * BENEFÍCIOS DO HTMX:
 * - Menos JavaScript que SPAs tradicionais
 * - Progressivos enhancement
 * - Integração natural com backend
 * - Performance excelente
 */

// ===========================================
// CONFIGURAÇÃO GLOBAL DO HTMX
// ===========================================

// Habilitar transições de visual globais (animações suaves)
htmx.config.globalViewTransitions = true;

// Definir tamanho do cache de histórico (quantas páginas armazenar)
htmx.config.historyCacheSize = 25;

// Não refresh automático quando página não encontrada no cache
htmx.config.refreshOnHistoryMiss = false;

/**
 * ===========================================
 * EVENT HANDLERS PARA CICLO DE VIDA
 * ===========================================
 * 
 * Estes event listeners monitoram todas as requisições HTMX
 * e implementam comportamentos específicos baseados no contexto.
 */

// Event fired após conclusão de uma requisição HTMX
document.addEventListener('htmx:afterRequest', function(event) {
    const target = event.target;  // Elemento que fez a requisição
    
    /**
     * ATUALIZAÇÃO DO TABULEIRO
     * 
     * LÓGICA:
     * - Verificar se requisição foi para o tabuleiro
     * - Aplicar animação de fade-in
     * - Remover animação após 300ms
     */
    if (target.id === 'chess-board' || target.closest('#chess-board')) {
        // Adicionar animação de entrada
        target.classList.add('fade-in');
        
        // Remover animação após trans完成
        setTimeout(() => target.classList.remove('fade-in'), 300);
    }
    
    /**
     * ATUALIZAÇÃO DOS JOGADORES
     * 
     * LÓGICA:
     * - Verificar se requisição foi para status dos jogadores
     * - Aplicar animação de fade-in
     * - Scroll automático para mostrar novidades
     */
    if (target.id === 'players-status' || target.closest('#players-status')) {
        target.classList.add('fade-in');
        setTimeout(() => target.classList.remove('fade-in'), 300);
    }
    
    /**
     * ATUALIZAÇÃO DO HISTÓRICO
     * 
     * LÓGICA:
     * - Verificar se requisição foi para histórico
     * - Scroll automático para mostrar último movimento
     */
    if (target.id === 'game-history' || target.closest('#game-history')) {
        const historyContainer = document.getElementById('game-history');
        if (historyContainer) {
            // Scroll automático para o final (último movimento)
            historyContainer.scrollTop = historyContainer.scrollHeight;
        }
    }
});

// Event fired quando há erro na resposta HTTP
document.addEventListener('htmx:responseError', function(event) {
    // Log do erro para debug
    console.error('HTMX Error:', event.detail.xhr.status, event.detail.xhr.statusText);
    
    // Mostrar mensagem de erro amigável para o usuário
    showHTMXError('Erro de comunicação com o servidor. Verifique se o backend está rodando.');
});

// Event fired quando há erro ao enviar requisição
document.addEventListener('htmx:sendError', function(event) {
    console.error('HTMX Send Error:', event.detail);
    
    showHTMXError('Erro ao enviar requisição. Verifique sua conexão.');
});

// ===========================================
// GERENCIAMENTO DE LOADING STATES
// ===========================================

// Event fired antes de enviar uma requisição
document.addEventListener('htmx:beforeRequest', function(event) {
    const target = event.target;
    
    if (target) {
        /**
         * ADICIONAR ESTADO DE LOADING
         * 
         * VISUAL:
         * - Adicionar classe 'loading' ao elemento
         * - Mostrar spinner para feedback visual
         */
        
        // Adicionar classe de loading
        target.classList.add('loading');
        
        // Adicionar spinner para elementos específicos
        if (target.id === 'chess-board' || target.id === 'players-status') {
            const spinner = document.createElement('div');
            spinner.className = 'spinner';
            // Inserir spinner antes do conteúdo existente
            target.insertBefore(spinner, target.firstChild);
        }
    }
});

// Event fired após conclusão de uma requisição (sucesso ou erro)
document.addEventListener('htmx:afterRequest', function(event) {
    const target = event.target;
    
    if (target) {
        /**
         * REMOVER ESTADO DE LOADING
         * 
         * LÓGICA:
         * - Remover classe 'loading'
         * - Remover spinners criados anteriormente
         */
        
        target.classList.remove('loading');
        
        // Remover todos os spinners
        const spinners = target.querySelectorAll('.spinner');
        spinners.forEach(spinner => spinner.remove());
    }
});

// ===========================================
// FUNÇÕES AUXILIARES DE ERRO
// ===========================================

/**
 * Exibe mensagem de erro HTMX para o usuário
 * 
 * CARACTERÍSTICAS:
 * - Remove mensagens anteriores
 * - Aplica estilo visual de erro
 * - Esconde automaticamente após 5 segundos
 * 
 * @param {string} message - Mensagem de erro para exibir
 */
function showHTMXError(message) {
    const messageElement = document.getElementById('game-message');
    if (messageElement) {
        messageElement.textContent = message;
        messageElement.className = 'bg-red-100 border-red-400 text-red-700 px-4 py-3 rounded';
        messageElement.classList.remove('hidden');
        
        // Esconder automaticamente após 5 segundos
        setTimeout(() => {
            messageElement.classList.add('hidden');
        }, 5000);
    }
}

// ===========================================
// HELPERS E EXTENSÕES HTMX
// ===========================================

/**
 * Objeto com funções auxiliares para HTMX
 * 
 * FUNCIONALIDADES:
 * - Debounce para requisições
 * - Habilitar/desabilitar elementos
 * - Refresh forçado de elementos
 */
const HTMXHelpers = {
    
    /**
     * Função debounce para evitar muitas requisições
     * 
     * USO:
     * - Previne spam de requisições
     * - Melhora performance
     * - Reduz carga no servidor
     * 
     * @param {Function} func - Função a ser executada
     * @param {number} wait - Tempo de espera em ms
     * @returns {Function} Função com debounce aplicado
     */
    debounce: function(func, wait) {
        let timeout;
        return function executedFunction(...args) {
            const later = () => {
                clearTimeout(timeout);
                func(...args);
            };
            clearTimeout(timeout);
            timeout = setTimeout(later, wait);
        };
    },
    
    /**
     * Desabilita requisições HTMX em elementos específicos
     * 
     * USO:
     * - Prevenir ações durante processing
     * - Feedback visual de estado bloqueado
     * 
     * @param {string} selector - Seletor CSS dos elementos
     */
    disableRequests: function(selector) {
        document.querySelectorAll(selector).forEach(element => {
            // Adicionar classe que indica requisição em andamento
            htmx.addClass(element, 'htmx-request');
        });
    },
    
    /**
     * Habilita requisições HTMX em elementos específicos
     * 
     * @param {string} selector - Seletor CSS dos elementos
     */
    enableRequests: function(selector) {
        document.querySelectorAll(selector).forEach(element => {
            htmx.removeClass(element, 'htmx-request');
        });
    },
    
    /**
     * Força refresh de elementos via HTMX
     * 
     * USO:
     * - Atualização manual sem polling
     * - Trigger de mudanças de estado
     * 
     * @param {string} selector - Seletor CSS dos elementos
     */
    refreshElement: function(selector) {
        const elements = document.querySelectorAll(selector);
        elements.forEach(element => {
            htmx.trigger(element, 'refresh');
        });
    }
};

// ===========================================
// TRATAMENTO DE CSRF (Cross-Site Request Forgery)
// ===========================================

/**
 * Extrai token CSRF do meta tag
 * 
 * @returns {string|null} Token CSRF ou null se não encontrado
 */
function getCSRFToken() {
    const meta = document.querySelector('meta[name="csrf-token"]');
    return meta ? meta.getAttribute('content') : null;
}

/**
 * Adiciona token CSRF a todas as requisições HTMX
 * 
 * SEGURANÇA:
 * - Prevém ataques CSRF
 * - Autenticação automática
 * 
 * EVENTO:
 * - Executa antes de cada requisição
 */
document.addEventListener('htmx:beforeRequest', function(event) {
    const csrfToken = getCSRFToken();
    if (csrfToken) {
        // Adicionar token ao header da requisição
        event.detail.headers['X-CSRF-Token'] = csrfToken;
    }
});

// ===========================================
// CONFIGURAÇÃO DE POLLING AUTOMÁTICO
// ===========================================

/**
 * Intervalos de polling para diferentes elementos
 * 
 * ESTRATÉGIA:
 * - Elementos críticos: mais frequente
 * - Elementos informativos: menos frequente
 * - Balance entre responsividade e performance
 */
const POLLING_INTERVALS = {
    board: 3000,      // 3 segundos - tabuleiro (crítico)
    players: 3000,    // 3 segundos - jogadores (importante)
    history: 5000,    // 5 segundos - histórico (informativo)
    turn: 2000        // 2 segundos - turno atual (crítico)
};

/**
 * Configura polling automático para elementos do jogo
 * 
 * POLLING:
 * - Atualizações automáticas sem intervenção do usuário
 * - Sincronização em tempo real
 * - Diferentes intervalos baseados na importância
 */
function setupPolling() {
    /**
     * POLLING DO TABULEIRO
     * 
     * LÓGICA:
     * - Atualiza estado do tabuleiro a cada 3s
     * - Movimentos dos jogadores
     * - Mudanças no estado das peças
     */
    const board = document.getElementById('chess-board');
    if (board) {
        // Configurar trigger automático
        board.setAttribute('hx-trigger', `every ${POLLING_INTERVALS.board}ms`);
    }
    
    /**
     * POLLING DOS JOGADORES
     * 
     * LÓGICA:
     * - Status dos jogadores (eliminado, ativo)
     * - Tempo restante dos relógios
     */
    const players = document.getElementById('players-status');
    if (players) {
        players.setAttribute('hx-trigger', `every ${POLLING_INTERVALS.players}ms`);
    }
    
    /**
     * POLLING DO HISTÓRICO
     * 
     * LÓGICA:
     * - Novos movimentos realizados
     * - Histórico de jogadas
     * - Intervalo maior (menos crítico)
     */
    const history = document.getElementById('game-history');
    if (history) {
        history.setAttribute('hx-trigger', `every ${POLLING_INTERVALS.history}ms`);
    }
    
    /**
     * POLLING DO TURNO ATUAL
     * 
     * LÓGICA:
     * - Qual jogador está jogando agora
     * - Intervalo menor (mais crítico)
     * - Load inicial + polling
     */
    const turn = document.getElementById('current-turn');
    if (turn) {
        // Trigger imediato + polling contínuo
        turn.setAttribute('hx-trigger', `load, every ${POLLING_INTERVALS.turn}ms`);
    }
}

// Inicializar polling quando DOM estiver carregado
document.addEventListener('DOMContentLoaded', setupPolling);

// ===========================================
// INTEGRAÇÃO WEBSOCKET (OPCIONAL)
// ===========================================

/**
 * Bridge entre WebSocket e HTMX
 * 
 * FUNÇÃO:
 * - Conectar WebSocket para updates em tempo real
 * - Atualizar elementos HTMX baseado em mensagens WebSocket
 * - Reconexão automática em caso de falha
 * 
 * ALTERNATIVA AO POLLING:
 * - Mais eficiente para updates frequentes
 * - Conexão persistente
 * - Latência menor
 */
class HTMXWebSocketBridge {
    constructor(url) {
        this.url = url;
        this.ws = new WebSocket(url);
        this.setupHandlers();
    }
    
    /**
     * Configura event handlers do WebSocket
     */
    setupHandlers() {
        /**
         * MENSAGEM RECEBIDA
         * 
         * PROCESSO:
         * 1. Parse do JSON recebido
         * 2. Identificar tipo de mensagem
         * 3. Atualizar elemento correspondente via HTMX
         */
        this.ws.onmessage = (event) => {
            const data = JSON.parse(event.data);
            this.handleWebSocketMessage(data);
        };
        
        /**
         * CONEXÃO FECHADA
         * 
         * RECONEXÃO:
         * - Aguarda 5 segundos
         * - Reconecta automaticamente
         * - Previne loop de reconexão
         */
        this.ws.onclose = () => {
            setTimeout(() => {
                this.ws = new WebSocket(this.url);
            }, 5000);
        };
    }
    
    /**
     * Processa mensagens WebSocket baseadas no tipo
     * 
     * TIPOS DE MENSAGEM:
     * - 'move': Movimento realizado
     * - 'turn': Mudança de turno
     * - 'player_update': Atualização de jogador
     * 
     * @param {Object} data - Dados da mensagem WebSocket
     */
    handleWebSocketMessage(data) {
        switch (data.type) {
            case 'move':
                this.refreshBoard();
                break;
            case 'turn':
                this.updateTurn(data.currentTurn);
                break;
            case 'player_update':
                this.refreshPlayers();
                break;
        }
    }
    
    /**
     * Atualiza tabuleiro via HTMX
     */
    refreshBoard() {
        const board = document.getElementById('chess-board');
        if (board) {
            // Trigger refresh HTMX manual
            htmx.trigger(board, 'refresh');
        }
    }
    
    /**
     * Atualiza informação do turno atual
     * 
     * @param {string} currentTurn - Cor do jogador atual
     */
    updateTurn(currentTurn) {
        const turnElement = document.getElementById('current-turn');
        if (turnElement && window.chessGame) {
            turnElement.textContent = `Vez do: ${window.chessGame.getPlayerName(currentTurn)}`;
            turnElement.className = `px-4 py-2 rounded-lg font-bold turn-${currentTurn.toLowerCase()}`;
        }
    }
    
    /**
     * Atualiza status dos jogadores
     */
    refreshPlayers() {
        const players = document.getElementById('players-status');
        if (players) {
            htmx.trigger(players, 'refresh');
        }
    }
}

// ===========================================
// FUNÇÕES UTILITÁRIAS GLOBAIS
// ===========================================

/**
 * Objeto com funções utilitárias para HTMX
 * 
 * ACESSO:
 * - window.HTMXUtils
 * - Usado para operações comuns HTMX
 */
window.HTMXUtils = {
    
    /**
     * Mostra estado de loading em elemento
     * 
     * @param {string} elementId - ID do elemento
     */
    showLoading: function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.classList.add('loading');
            
            // Criar spinner visual
            const spinner = document.createElement('div');
            spinner.className = 'spinner';
            spinner.id = `${elementId}-spinner`;
            element.insertBefore(spinner, element.firstChild);
        }
    },
    
    /**
     * Esconde estado de loading
     * 
     * @param {string} elementId - ID do elemento
     */
    hideLoading: function(elementId) {
        const element = document.getElementById(elementId);
        if (element) {
            element.classList.remove('loading');
            
            // Remover spinner específico
            const spinner = document.getElementById(`${elementId}-spinner`);
            if (spinner) {
                spinner.remove();
            }
        }
    },
    
    /**
     * Refresh de todos os elementos principais
     * 
     * USO:
     * - Atualização global após evento importante
     * - Sincronização completa do estado
     */
    refreshAll: function() {
        ['chess-board', 'players-status', 'game-history', 'current-turn'].forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                htmx.trigger(element, 'refresh');
            }
        });
    }
};

// ===========================================
// EXPORT PARA ACESSO GLOBAL
// ===========================================

// Disponibilizar helpers globalmente
window.HTMXHelpers = HTMXHelpers;
window.HTMXWebSocketBridge = HTMXWebSocketBridge;