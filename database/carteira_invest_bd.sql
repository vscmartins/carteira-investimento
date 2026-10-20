-- COMO AS TABELAS SE RELACIONAM:
--
--   usuarios      (como "clientes")
--   corretoras    (só o nome da corretora)
--   contas        (a conta que o usuário abre em uma corretora)
--   ativos        (como "produtos": cartela com ações e criptomoedas)
--   movimentacoes (como "pedidos + itens_pedidos": o que o usuário faz)
--
--   usuarios 1 ---- N contas N ---- 1 corretoras
--                      |
--                      1
--                      |
--                      N
--                movimentacoes N ---- 1 ativos
--
-- Regra da tabela movimentacoes (uma só tabela para dinheiro e ativos):
--   deposito / saque  -> mexe só no DINHEIRO (id_ativo, quantidade e
--                        preco_unitario ficam NULL)
--   compra / venda    -> mexe em um ATIVO (id_ativo, quantidade e
--                        preco_unitario são preenchidos)
-- O SALDO não é guardado: é calculado com SUM (veja a consulta 08).
-- =====================================================================

create database if not exists carteira_invest_bd
character set utf8mb4
collate utf8mb4_unicode_ci;

use carteira_invest_bd;

-- ---------------------------------------------------------------------
-- 1) usuarios
-- ---------------------------------------------------------------------
create table if not exists usuarios (
                                        id_usuario int auto_increment primary key,
                                        nome_usuario varchar(100) not null,
    cpf char(11) not null unique,
    email varchar(100) not null unique,
    data_nascimento date not null,
    data_cadastro datetime not null default current_timestamp
    ) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 2) corretoras
-- ---------------------------------------------------------------------
create table if not exists corretoras (
                                          id_corretora int auto_increment primary key,
                                          nome_corretora varchar(80) not null unique
    ) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 3) contas  (usuário + corretora)
-- ---------------------------------------------------------------------
create table if not exists contas (
                                      id_conta int auto_increment primary key,
                                      id_usuario int not null,
                                      id_corretora int not null,
                                      numero_conta varchar(20) not null unique,
    data_abertura datetime not null default current_timestamp,

    unique (id_usuario, id_corretora),
    foreign key (id_usuario) references usuarios(id_usuario) on delete restrict,
    foreign key (id_corretora) references corretoras(id_corretora) on delete restrict
    ) engine=InnoDB;

-- ---------------------------------------------------------------------
-- 4) ativos
-- ---------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS ativos (
                                      id_ativo INT AUTO_INCREMENT PRIMARY KEY,
                                      usuario_id INT NOT NULL,
                                      ticker VARCHAR(10) NOT NULL,
                                      nome_empresa VARCHAR(100) NOT NULL,
                                      tipo_ativo ENUM('ACAO', 'CRIPTO', 'CRIPTOMOEDA') NOT NULL,
                                      quantidade DECIMAL(18, 8) NOT NULL,
                                      preco_medio DECIMAL(15, 2) NOT NULL,
                                      data_operacao TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      CONSTRAINT fk_usuario_ativo FOREIGN KEY (usuario_id) REFERENCES usuarios(id_usuario) ON DELETE CASCADE
) engine=InnoDB;
-- ---------------------------------------------------------------------
-- 5) movimentacoes
-- ---------------------------------------------------------------------
create table if not exists movimentacoes (
                                             id_movimentacao int auto_increment primary key,
                                             id_conta int not null,
                                             id_ativo int null,
                                             tipo_movimentacao enum('DEPOSITO', 'SAQUE', 'COMPRA', 'VENDA') not null,
    quantidade decimal(18, 8),
    preco_unitario decimal(15, 2),
    valor_total decimal(15, 2) not null,
    data_movimentacao datetime not null default current_timestamp,

    check (valor_total > 0),
    check (
    (tipo_movimentacao in ('DEPOSITO', 'SAQUE')
    and quantidade is null and preco_unitario is null)
    or
(tipo_movimentacao in ('COMPRA', 'VENDA')
    and quantidade is not null and preco_unitario is not null
    and quantidade > 0 and preco_unitario > 0
    and valor_total = round(quantidade * preco_unitario, 2))
    ),
    foreign key (id_conta) references contas(id_conta) on delete restrict,
    foreign key (id_ativo) references ativos(id_ativo) on delete restrict
    ) engine=InnoDB;



create index idx_movimentacoes_data on movimentacoes(data_movimentacao);

-- --------------------------------------------------------------------------------------------

DROP USER IF EXISTS 'carteira_app'@'localhost';
CREATE USER 'carteira_app'@'localhost' IDENTIFIED BY 'Senha-carteira@123';


GRANT SELECT, INSERT, UPDATE ON carteira_invest_bd.* TO 'carteira_app'@'localhost';

FLUSH PRIVILEGES;