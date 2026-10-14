# Carteira Investimento - Sistema de Gestao de Ativos Financeiros

## Sobre o Projeto e Motivacao
O Carteira Investimento nasceu da necessidade de criar uma base sólida, em nível de código, para o estudo prático escolhendo o tema mercado financeiro e suas regras operacionais.
O escopo atual do projeto foca nos fundamentos da gestão de carteira: controle rigoroso de entrada de dados de investidores, segregação de ativos em categorias (Ações e Criptomoedas), recálculo automático de preço médio a cada novo aporte, e bloqueio de operações inválidas (como venda a descoberto ou venda de ativos inexistentes na carteira). O sistema não permite inconsistências matemáticas ou de cadastro, refletindo a seriedade exigida por aplicações financeiras reais.

## Arquitetura e Decisoes Tecnicas (DDD)
O mercado financeiro é um ambiente dominado por regras de negócio complexas. Para refletir isso no código, a arquitetura foi desenhada com inspiração no Domain-Driven Design (DDD), para treinar mais a arquitetura de software.

A escolha do DDD justifica-se pelos seguintes motivos:
1. **Isolamento do Domínio:** As regras matemáticas (como o cálculo de preço médio) pertencem exclusivamente ao modelo (`AtivoFinanceiro`). O ativo "sabe" como se comportar, evitando que a lógica fique espalhada pelo sistema.
2. **Separação de Responsabilidades:** A classe `Main` cuida apenas da interação com o usuário (I/O). A classe `CarteiraService` atua como orquestradora, e o pacote `validador` blinda a entrada de dados.
3. **Escalabilidade e Manutenção:** O uso de Polimorfismo e Interfaces permite que, no futuro, novos tipos de investimentos (como Fundos Imobiliários) sejam adicionados sem alterar a lógica principal do serviço.

## Tecnologias e Recursos Utilizados
- **Java 25:** Uso de *Implicitly Declared Classes* (`void main` sem classe pública) e métodos de I/O simplificados (`java.lang.IO`).
- **DTO (Data Transfer Object):** Uso da estrutura `record` para transporte imutável de dados.
- **Tratamento de Exceções Customizado:** Classes próprias para violações de regras de negócio.
- **Regex e Algoritmos:** Validação de CPF sem dependência de bibliotecas externas e sanitização de inputs.

## Destaques do Codigo

### 1. Transporte Seguro de Dados (DTO)
Para garantir que a camada de visualização não altere os valores processados pelo serviço, utilizamos um `record` imutável como DTO:

```java
package domain.dto;

public record ResumoCarteiraDTO(
    int quantidadeTotalAtivos, 
    double valorTotalAlocado
) {}
```
## 2. Experiência do Usuário (UX) e Fail-Fast
Na rotina de venda, o sistema não faz perguntas desnecessárias se a premissa básica for falsa. A existência do ativo é validada imediatamente, economizando tempo do usuário:

``` java
void venderAtivo(CarteiraService carteira) {
println("\n-- VENDER ATIVO --");
String ticket = Entrada.lerEntrada("Ticket para vender: ").toUpperCase();

    // Verificação antecipada (Fail-fast): Aborta caso o ativo não exista.
    carteira.buscarAtivo(ticket);

    // O fluxo só continua se o ativo for encontrado.
    int qtd = (int) Entrada.lerDouble("Quantidade a vender: ");
    // ...
}
```

### Como Compilar e Executar

O projeto requer o JDK 25 instalado e configurado nas variáveis de ambiente.
Abra o Main.java na pasta raiz do projeto (onde está a pasta src) e execute.

### Roteiro de Testes

Para homologar a resiliência do sistema, siga os cenários de teste abaixo no terminal durante a execução:

#### Cenário 1: Barreira de Validação de Cadastro

- Tente informar um nome vazio (apenas aperte Enter). O sistema deve exigir um texto válido.

- Informe o CPF: 11111111111 ou 123. O algoritmo deve recusar CPFs matematicamente inválidos ou com tamanho incorreto.

- Insira um CPF válido para testes (ex: 12345678909) para acessar o menu.

#### Cenário 2: Tratamento de Tipos e Fluxo de Compra

- Escolha a opção 1 (Comprar).

- Ao ser questionado sobre a Quantidade, digite -50. O validador deve impedir números negativos.

- Informe os dados corretos (ex: Ticket PETR4, Quantidade 100, Preço 35.50). A compra será efetivada.

#### Cenário 3: Exceções de Regra de Negócio (Alarme)

- Escolha a opção 2 (Vender).

- Tente vender um Ticket que você não comprou (ex: VALE3).A UX Fail-fast deve barrar a ação informando que o ativo não existe na carteira.

- Tente vender novamente, agora selecionando o Ticket PETR4, mas informe uma quantidade de 500. 
A SaldoInsuficienteException deve ser disparada, interrompendo a transação de forma amigável e retornando ao menu.

#### Cenário 4: Resumo e Polimorfismo

- Registre novas compras de ações e criptomoedas.

- Acesse a opção 4 (Resumo).
O sistema iterará sobre todos os objetos da lista genérica de ativos e exibirá a soma calculada via DTO.


###  Demonstração de Execução (Log Completo)
Abaixo está o registro real de um teste contemplando todas as validações de segurança e regras de negócio operando no terminal:
````
==============================================
   BEM-VINDO AO CARTEIRA DE INVESTIMENTO     
==============================================
Digite seu nome:
Erro: O texto não pode ser vazio.
Digite seu nome: Victor
Digite seu CPF (com ou sem pontos): 111111111111
Erro: CPF inválido. Digite um CPF válido com 11 dígitos.
Digite seu CPF (com ou sem pontos): 123
Erro: CPF inválido. Digite um CPF válido com 11 dígitos.
Digite seu CPF (com ou sem pontos): 12345678909

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 9
   Erro: Digite um número de 1 a 5.
   Escolha uma opção (1-5): A
   Erro: Digite um número de 1 a 5.
   Escolha uma opção (1-5): 1

-- COMPRAR ATIVO --
Tipo (1 - Ação | 2 - Cripto): 1
Ticket (ex: PETR4, BTC): PETR4
Nome da empresa/moeda: Petrobras
Quantidade: -50
Erro: O valor deve ser estritamente positivo.
Quantidade: 100
Preço médio (R$): 35.50
✅ Compra registrada com sucesso!

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 3

-- SUA CARTEIRA --
[PETR4] Petrobras - Qtd: 100 | Preco Medio: R$ 35,50

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 4

-- RESUMO GERAL --
Ativos diferentes na carteira: 1
Valor total investido: R$ 3550,00

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 2

-- VENDER ATIVO --
Ticket para vender: VALE3

[!] Erro: Ativo VALE3 não encontrado na carteira.

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 2

-- VENDER ATIVO --
Ticket para vender: PETR4
Quantidade a vender: 500
Preço de venda (R$): 40.00

[!] Erro: Você tem apenas 100 cotas de PETR4.

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 2

-- VENDER ATIVO --
Ticket para vender: PETR4
Quantidade a vender: 100
Preço de venda (R$): 40.00
✅ Venda realizada com sucesso!

--- MENU DE VICTOR ---
1. Comprar Ativo
2. Vender Ativo
3. Listar Carteira
4. Ver Resumo
5. Sair
   Escolha uma opção (1-5): 5
   Saindo do Carteira de Investimento. Até logo!
````

Desenvolvido por: Victor Suander  e Alexandre Toro 