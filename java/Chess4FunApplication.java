package org.chess;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Aplicação principal do Chess 4 Fun
 * 
 * Esta classe é o ponto de entrada da aplicação Spring Boot e integra:
 * 
 * COMPONENTES:
 * - Backend Java: Lógica completa de xadrez para 4 jogadores
 * - Frontend HTMX: Interface web responsiva e dinâmica
 * - Spring Boot: Framework web que une frontend e backend
 * - API REST: Comunicação entre frontend e backend
 * 
 * FUNCIONALIDADES:
 * - Servidor web para servir páginas HTML
 * - APIs REST para operações de xadrez
 * - Integração com lógica de negócios Java
 * - Controle de ciclo de vida da aplicação
 * 
 * ARQUITETURA:
 * - Página principal: Landing page com links
 * - Página do jogo: Interface completa do xadrez
 * - Auto-configuração Spring Boot
 * - Injeção de dependências automática
 */
@SpringBootApplication  // Configuração automática do Spring Boot
@RestController         // Habilita endpoints REST nesta classe
public class Chess4FunApplication {
    
    /**
     * Método principal da aplicação
     * 
     * RESPONSABILIDADES:
     * - Inicializar o container Spring
     * - Configurar todas as beans automaticamente
     * - Iniciar servidor web embarcado
     * - Carregar configurações da aplicação
     * 
     * EXECUÇÃO:
     * - Invocado pelo JVM ao iniciar a aplicação
     * - Configurado como main class no build
     * - Pode receber argumentos de linha de comando
     * 
     * @param args Argumentos da linha de comando (opcional)
     */
    public static void main(String[] args) {
        // Inicializar e executar aplicação Spring Boot
        // SpringApplication.run() faz:
        // 1. Criar ApplicationContext
        // 2. Configurar beans automaticamente
        // 3. Iniciar servidor web
        // 4. Monitorar configurações
        SpringApplication.run(Chess4FunApplication.class, args);
    }
    
    /**
     * Endpoint principal da aplicação (Landing Page)
     * 
     * ROTA: GET /
     * 
     * RESPONSABILIDADE:
     * - Página inicial do jogo
     * - Apresentação do projeto
     * - Links para acessar funcionalidades
     * - Interface simples de entrada
     * 
     * RETORNA:
     * - HTML string com página de boas-vindas
     * - Link para a página do jogo
     * 
     * USO:
     * - Primeira página que usuários veem
     * - Navegação inicial
     * - Informações sobre o projeto
     */
    @GetMapping("/")
    public String home() {
        // Retornar página HTML como string
        // Em uma implementação completa, seria um template HTML
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chess 4 Fun - Xadrez para 4 Jogadores</title>
                <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
                <style>
                    .chess-bg { background: linear-gradient(135deg, #1e3a8a 0%, #1e40af 100%); }
                    .piece { font-size: 2rem; }
                    .btn-hover:hover { transform: translateY(-2px); transition: all 0.3s; }
                </style>
            </head>
            <body class="bg-gray-100 min-h-screen">
                <!-- Container principal -->
                <div class="container mx-auto px-4 py-8">
                    
                    <!-- Header -->
                    <header class="text-center mb-12">
                        <h1 class="text-6xl font-bold text-blue-800 mb-4">
                            ♔ Chess 4 Fun ♔
                        </h1>
                        <p class="text-xl text-gray-600 mb-6">
                            O primeiro xadrez para 4 jogadores em Java!
                        </p>
                        <div class="chess-bg text-white p-6 rounded-lg inline-block">
                            <span class="piece mr-4">♜</span>
                            <span class="piece mr-4">♞</span>
                            <span class="piece mr-4">♝</span>
                            <span class="piece mr-4">♛</span>
                            <span class="piece mr-4">♚</span>
                            <span class="piece">♟</span>
                        </div>
                    </header>
                    
                    <!-- Informações do projeto -->
                    <div class="grid md:grid-cols-2 gap-8 mb-12">
                        
                        <!-- Sobre o jogo -->
                        <div class="bg-white p-6 rounded-lg shadow-lg">
                            <h2 class="text-2xl font-bold text-blue-800 mb-4">🎮 Sobre o Jogo</h2>
                            <ul class="space-y-2 text-gray-700">
                                <li>• <strong>4 Jogadores:</strong> Vermelho, Azul, Verde e Amarelo</li>
                                <li>• <strong>Tabuleiro 14x14:</strong> Área expandida para 4 lados</li>
                                <li>• <strong>Regras Clássicas:</strong> Todos os movimentos oficiais</li>
                                <li>• <strong>Tempo por Jogador:</strong> Sistema de relógio integrado</li>
                                <li>• <strong>Interface Moderna:</strong> HTMX + JavaScript</li>
                            </ul>
                        </div>
                        
                        <!-- Tecnologias -->
                        <div class="bg-white p-6 rounded-lg shadow-lg">
                            <h2 class="text-2xl font-bold text-blue-800 mb-4">🛠️ Tecnologias</h2>
                            <ul class="space-y-2 text-gray-700">
                                <li>• <strong>Backend:</strong> Java 17 + Spring Boot</li>
                                <li>• <strong>Frontend:</strong> HTML5 + HTMX</li>
                                <li>• <strong>Styling:</strong> Tailwind CSS</li>
                                <li>• <strong>JavaScript:</strong> ES6+ vanilla</li>
                                <li>• <strong>Build:</strong> Gradle</li>
                            </ul>
                        </div>
                    </div>
                    
                    <!-- Status da aplicação -->
                    <div class="bg-green-50 border border-green-200 p-6 rounded-lg mb-8">
                        <h3 class="text-lg font-semibold text-green-800 mb-2">✅ Status da Aplicação</h3>
                        <div class="grid md:grid-cols-3 gap-4 text-sm">
                            <div class="flex items-center">
                                <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                                <span>Backend funcionando</span>
                            </div>
                            <div class="flex items-center">
                                <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                                <span>APIs REST ativas</span>
                            </div>
                            <div class="flex items-center">
                                <span class="w-2 h-2 bg-green-500 rounded-full mr-2"></span>
                                <span>Bugs corrigidos</span>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Botão principal -->
                    <div class="text-center">
                        <a href="/game" 
                           class="inline-flex items-center px-8 py-4 bg-blue-600 text-white font-bold text-lg rounded-lg shadow-lg btn-hover">
                            <span class="mr-2">🎯</span>
                            Começar a Jogar
                            <span class="ml-2">🎯</span>
                        </a>
                    </div>
                    
                    <!-- Instruções rápidas -->
                    <div class="mt-12 bg-blue-50 border border-blue-200 p-6 rounded-lg">
                        <h3 class="text-lg font-semibold text-blue-800 mb-3">🎓 Como Jogar</h3>
                        <ol class="list-decimal list-inside space-y-1 text-blue-700">
                            <li>Clique em <strong>Começar a Jogar</strong></li>
                            <li>Selecione uma peça clicando nela</li>
                            <li>Destinos possíveis ficam destacados</li>
                            <li>Clique no destino para fazer o movimento</li>
                            <li>Turnos alternam entre os 4 jogadores</li>
                            <li>Use o relógio para acompanhar o tempo</li>
                        </ol>
                    </div>
                    
                    <!-- Footer -->
                    <footer class="text-center mt-12 text-gray-500">
                        <p>Chess 4 Fun - Projeto MC322</p>
                        <p class="text-sm">Desenvolvido com ❤️ usando Java + Spring Boot + HTMX</p>
                    </footer>
                </div>
                
                <!-- Scripts (se necessário para esta página) -->
                <script src="/js/chess-game.js"></script>
                <script src="/js/htmx-setup.js"></script>
            </body>
            </html>
            """;
    }
    
    /**
     * Endpoint da página do jogo
     * 
     * ROTA: GET /game
     * 
     * RESPONSABILIDADE:
     * - Interface completa do jogo de xadrez
     * - Tabuleiro interativo
     * - Controles dos jogadores
     * - Histórico de movimentos
     * - Integração com backend via HTMX
     * 
     * CARACTERÍSTICAS:
     * - HTMX para updates dinâmicos
     * - JavaScript para interações
     * - CSS responsivo
     * - Polling automático para sincronização
     * 
     * ELEMENTOS PRINCIPAIS:
     * - Tabuleiro de xadrez 14x14
     * - Sidebar com status dos jogadores
     * - Histórico de movimentos
     * - Controles de jogo (novo, reiniciar)
     */
    @GetMapping("/game")
    public String game() {
        return """
            <!DOCTYPE html>
            <html lang="pt-BR">
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Chess 4 Fun - Jogo</title>
                
                <!-- CSS Frameworks -->
                <link href="https://cdn.jsdelivr.net/npm/tailwindcss@2.2.19/dist/tailwind.min.css" rel="stylesheet">
                <link rel="stylesheet" href="/styles/main.css">
                
                <!-- HTMX -->
                <script src="https://unpkg.com/htmx.org@1.9.10"></script>
                
                <!-- Meta tags -->
                <meta name="csrf-token" content="">
                <meta name="description" content="Jogo de xadrez para 4 jogadores - Chess 4 Fun">
                <meta name="author" content="MC322 Project">
            </head>
            <body class="bg-gray-100 font-sans">
                
                <!-- Container principal -->
                <div class="container mx-auto p-4">
                    
                    <!-- Header do jogo -->
                    <header class="text-center mb-6">
                        <h1 class="text-4xl font-bold text-blue-800 mb-2">♔ Chess 4 Fun ♔</h1>
                        <p class="text-gray-600">Xadrez para 4 Jogadores</p>
                        <a href="/" class="text-blue-600 hover:text-blue-800 text-sm">
                            ← Voltar ao menu principal
                        </a>
                    </header>
                    
                    <!-- Indicador de turno atual -->
                    <div id="current-turn" 
                         class="text-center mb-4 htmx-get"
                         hx-get="/api/turn"
                         hx-trigger="load, every 2s">
                        Carregando turno...
                    </div>
                    
                    <!-- Layout principal -->
                    <div class="grid lg:grid-cols-4 gap-6">
                        
                        <!-- Tabuleiro de xadrez (ocupa 3 colunas) -->
                        <div class="lg:col-span-3">
                            <div id="chess-board" 
                                 class="bg-white rounded-lg shadow-lg p-4 htmx-get"
                                 hx-get="/api/board"
                                 hx-trigger="load, every 3s">
                                
                                <!-- Tabuleiro será carregado via HTMX -->
                                <div class="flex items-center justify-center h-96">
                                    <div class="text-gray-500">
                                        Carregando tabuleiro...
                                    </div>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Sidebar com controles (1 coluna) -->
                        <div class="lg:col-span-1 space-y-6">
                            
                            <!-- Status dos jogadores -->
                            <div class="bg-white rounded-lg shadow-lg p-4">
                                <h3 class="font-bold text-lg mb-3 text-center">👥 Jogadores</h3>
                                <div id="players-status" 
                                     class="htmx-get"
                                     hx-get="/api/players"
                                     hx-trigger="load, every 3s">
                                    Carregando jogadores...
                                </div>
                            </div>
                            
                            <!-- Controles do jogo -->
                            <div class="bg-white rounded-lg shadow-lg p-4">
                                <h3 class="font-bold text-lg mb-3 text-center">🎮 Controles</h3>
                                <div class="space-y-3">
                                    <button onclick="window.chessGame.newGame()" 
                                            class="w-full bg-blue-500 hover:bg-blue-600 text-white font-bold py-2 px-4 rounded">
                                        Novo Jogo
                                    </button>
                                    <button onclick="window.chessGame.resetGame()" 
                                            class="w-full bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded">
                                        Reiniciar
                                    </button>
                                </div>
                            </div>
                            
                            <!-- Histórico de movimentos -->
                            <div class="bg-white rounded-lg shadow-lg p-4">
                                <h3 class="font-bold text-lg mb-3 text-center">📜 Histórico</h3>
                                <div id="game-history" 
                                     class="max-h-64 overflow-y-auto htmx-get"
                                     hx-get="/api/history"
                                     hx-trigger="load, every 5s">
                                    <div class="text-gray-500 text-sm">
                                        Nenhum movimento ainda...
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Mensagens de feedback -->
                    <div id="game-message" 
                         class="hidden fixed top-4 right-4 z-50 max-w-sm"></div>
                    
                    <!-- Modal para movimentos especiais (se necessário) -->
                    <div id="move-modal" class="hidden fixed inset-0 bg-black bg-opacity-50 z-40">
                        <div class="flex items-center justify-center h-full">
                            <div class="bg-white rounded-lg p-6 max-w-sm w-full mx-4">
                                <h3 class="text-lg font-bold mb-4">Movimento Especial</h3>
                                <div id="modal-content">
                                    <!-- Conteúdo do modal será carregado aqui -->
                                </div>
                                <div class="mt-4 text-right">
                                    <button onclick="window.chessGame.closeMoveModal()" 
                                            class="bg-gray-500 hover:bg-gray-600 text-white font-bold py-2 px-4 rounded">
                                        Cancelar
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
                
                <!-- Scripts do jogo -->
                <script src="/js/htmx-setup.js"></script>
                <script src="/js/chess-game.js"></script>
                
                <!-- Inicialização -->
                <script>
                    // HTMX será configurado automaticamente pelo htmx-setup.js
                    document.addEventListener('DOMContentLoaded', function() {
                        console.log('Chess 4 Fun - Interface do jogo carregada!');
                    });
                </script>
            </body>
            </html>
            """;
    }
}