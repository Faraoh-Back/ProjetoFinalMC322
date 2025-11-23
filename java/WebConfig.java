package org.chess.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import java.util.Arrays;

/**
 * Configuração Web do Spring Boot para Chess 4 Fun
 * 
 * Esta classe define toda a configuração do servidor web Spring Boot:
 * 
 * FUNCIONALIDADES:
 * - Servir arquivos estáticos (HTML, CSS, JS)
 * - Configurar rotas web (páginas HTML)
 * - Habilitar CORS para comunicação com frontend
 * - Configurar conversores de dados (JSON)
 * - Gerenciar cache de recursos estáticos
 * 
 * RESPONSABILIDADE:
 * - Conectar frontend HTMX com backend Java
 * - Fornecer APIs REST e páginas web
 * - Otimizar performance de carregamento
 * - Garantir segurança CORS
 */
@Configuration
@EnableWebMvc  // Habilitar configurações MVC automáticas do Spring
public class WebConfig implements WebMvcConfigurer {
    
    /**
     * Configura handlers para arquivos estáticos
     * 
     * RESPONSABILIDADES:
     * - Servir HTML, CSS, JavaScript para o navegador
     * - Configurar cache para performance
     * - Mapear URLs para locais físicos dos arquivos
     * 
     * ESTRUTURA DE ARQUIVOS:
     * - resources/static/ → raiz do site
     * - resources/static/js/ → arquivos JavaScript
     * - resources/static/styles/ → arquivos CSS
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        
        /**
         * Handler principal para todos os arquivos estáticos
         * 
         * MAPEAMENTO:
         * - URL: /* (qualquer caminho)
         * - LOCAL: classpath:/static/ (pasta resources/static/)
         * - CACHE: 0 (sem cache para desenvolvimento)
         * 
         * ARQUIVOS SERVIDOS:
         * - index.html (página principal)
         * - Imagens, ícones, etc.
         */
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/")
                .setCachePeriod(0); // Sem cache em desenvolvimento
        
        /**
         * Handler específico para arquivos JavaScript
         * 
         * MAPEAMENTO:
         * - URL: /js/**
         * - LOCAL: classpath:/static/js/
         * - CACHE: 3600 segundos (1 hora)
         * 
         * ARQUIVOS SERVIDOS:
         * - chess-game.js (lógica do cliente)
         * - htmx-setup.js (configuração HTMX)
         */
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/")
                .setCachePeriod(3600); // Cache por 1 hora
        
        /**
         * Handler específico para arquivos CSS
         * 
         * MAPEAMENTO:
         * - URL: /styles/**
         * - LOCAL: classpath:/static/styles/
         * - CACHE: 3600 segundos (1 hora)
         * 
         * ARQUIVOS SERVIDOS:
         * - main.css (estilos principais)
         * - Outros arquivos de estilo
         */
        registry.addResourceHandler("/styles/**")
                .addResourceLocations("classpath:/static/styles/")
                .setCachePeriod(3600); // Cache por 1 hora
    }
    
    /**
     * Configura controllers de visualização (páginas HTML)
     * 
     * RESPONSABILIDADES:
     * - Mapear URLs para templates/views
     * - Definir rotas de páginas web
     * - Configurar redirecionamentos
     * 
     * RENDERIZAÇÃO:
     * - Usa ViewResolver para encontrar templates
     * - Para esta implementação, retorna HTML diretamente
     */
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        
        /**
         * Página principal da aplicação
         * 
         * ROTA: /
         * 
         * RETORNA:
         * - String com conteúdo HTML completo
         * - Ou nome do template se usar Thymeleaf/Freemarker
         * 
         * USO:
         * - Landing page do jogo
         * - Links para acessar o jogo
         * - Informações básicas do projeto
         */
        registry.addViewController("/").setViewName("index");
        
        /**
         * Página do jogo
         * 
         * ROTA: /game
         * 
         * RETORNA:
         * - Interface completa do jogo
         * - Tabuleiro interativo
         * - Controles de jogadores
         * 
         * USO:
         * - Rota principal para jogar
         * - Integração com HTMX
         */
        registry.addViewController("/game").setViewName("index");
    }
    
    /**
     * Configura CORS (Cross-Origin Resource Sharing)
     * 
     * SEGURANÇA:
     * - Permite requisições de outros domínios
     * - Controla quais origens podem acessar a API
     * - Define métodos HTTP permitidos
     * - Configura headers e credenciais
     * 
     * CASOS DE USO:
     * - Desenvolvimento local (diferentes portas)
     * - Integração com frontend separado
     * - APIs públicas ou semi-públicas
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        
        /**
         * Configuração CORS para API de xadrez
         * 
         * PADRÃO:
         * - Endpoints: /api/**
         * - Origens permitidas: localhost:3000 e localhost:8080
         * - Métodos: GET, POST, PUT, DELETE, OPTIONS
         * - Headers: qualquer (*)
         * - Credenciais: permitido (true)
         * - Cache preflight: 1 hora
         */
        registry.addMapping("/api/**")
                // Origens permitidas (desenvolvimento local)
                .allowedOrigins("http://localhost:3000", "http://localhost:8080")
                // Métodos HTTP permitidos
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                // Headers permitidos (todos)
                .allowedHeaders("*")
                // Permitir cookies/credenciais
                .allowCredentials(true)
                // Cache de requisições preflight (1 hora)
                .maxAge(3600);
    }
    
    /**
     * Configura conversores de mensagem HTTP
     * 
     * RESPONSABILIDADES:
     * - Converter objetos Java para JSON e vice-versa
     * - Configurar serialização/desserialização
     * - Gerenciar Content-Type e Accept headers
     * 
     * TECNOLOGIA:
     * - Jackson: Biblioteca padrão do Spring para JSON
     * - MappingJackson2HttpMessageConverter: Conversor específico
     */
    @Override
    public void configureMessageConverters(HttpMessageConverter<?>[] converters) {
        
        /**
         * Adicionar conversor JSON para toda a aplicação
         * 
         * FUNCIONALIDADES:
         * - Serializar objetos Java → JSON para resposta HTTP
         * - Desserializar JSON → objetos Java para requisições
         * - Suporte a anotações Jackson (@JsonProperty, etc.)
         * - Tratamento de datas, coleções, mapas
         * 
         * CONFIGURAÇÃO:
         * - Criar nova instância do conversor
         * - Adicionar ao array de conversores existentes
         * - Manter conversores padrão do Spring
         */
        
        // Criar conversor JSON usando Jackson
        MappingJackson2HttpMessageConverter jsonConverter = new MappingJackson2HttpMessageConverter();
        
        // Redimensionar array para adicionar novo conversor
        converters = Arrays.copyOf(converters, converters.length + 1);
        
        // Adicionar conversor JSON na última posição
        converters[converters.length - 1] = jsonConverter;
    }
}