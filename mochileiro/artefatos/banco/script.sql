	/*Tabela Empresa*/
	CREATE TABLE TB_EMPRESA(
	cod_empresa      SERIAL,
	razao_social     varchar(70) NOT NULL,
	logradouro       varchar(70) NOT NULL,
	numero           varchar(6)  NOT NULL,  
	complemento      varchar(20) , 
	bairro           varchar(70) NOT NULL,
	cidade           varchar(70) NOT NULL,
	estado           int         NOT NULL,
	cep              varchar(8)  NOT NULL,
	telefone         varchar(10) NOT NULL,
	cnpj             varchar(14) UNIQUE NOT NULL,
	e_mail           varchar(40) NOT NULL,
	home_page        varchar(70),
	data_cadastro    timestamp   NOT NULL DEFAULT now(),
	CONSTRAINT pk_empresa PRIMARY KEY (cod_empresa)
	);

	/*Tabela Viajante*/

	-- criaÁ„o de sequence necess·ria pois ao cadastrar viajante
	-- e empresa os mesmos retornam seus campos seriais comeÁando
	-- sempre em 1
	create sequence tb_viajante_cod_viajante_seq
	increment 1
	start 10;
	
	CREATE TABLE TB_VIAJANTE(
	cod_viajante  	   integer DEFAULT nextval('tb_viajante_cod_viajante_seq') NOT NULL,
	nome    	   varchar(70) NOT NULL,
	logradouro     	   varchar(70) NOT NULL,
	numero             varchar(6)  NOT NULL,   
	complemento        varchar(20) ,  
	bairro       	   varchar(70) NOT NULL,
	cidade       	   varchar(70) NOT NULL,
	estado       	   int         NOT NULL,
	cep                varchar(8)  NOT NULL, 
	telefone     	   varchar(10) NOT NULL,
	cpf	           varchar(11) UNIQUE NOT NULL,
	e_mail             varchar(40) NOT NULL,
	data_cadastro      timestamp   NOT NULL DEFAULT now(),
	CONSTRAINT pk_viajante PRIMARY KEY (cod_viajante)
	);


		
	/*Tabela Usuario*/
	CREATE TABLE TB_USUARIO(
	cod_usuario  	    int,
	usuario    	    varchar(25)        NOT NULL,
	senha     	    varchar(25)        NOT NULL,
	tipo_usuario  	    varchar(10)        NOT NULL
	);

	
	/*Tabela Categoria*/
	CREATE TABLE TB_CATEGORIA(
	cod_categoria     Serial,
	nome_categoria    varchar(70),
	descricao_categoria text,
	tipo_categoria varchar(7),
	CONSTRAINT pk_categoria PRIMARY KEY (cod_categoria)
	);  


	/*Tabela Atividade*/
	CREATE TABLE TB_ATIVIDADE(
	cod_atividade     Serial      NOT NULL,
	cod_categoria     int         NOT NULL,
	nome_atividade    varchar(70) UNIQUE NOT NULL,
	desc_atv       	  text        NOT NULL,
	custo             VARCHAR(11) NOT NULL,
	per_disp_ini      date	      NULL,
	per_disp_fim   	  date        NULL,
	logradouro     	  varchar(70) NOT NULL,
	numero            varchar(9)  NOT NULL,   
	complemento       varchar(20) NULL,  
	bairro       	  varchar(70) NOT NULL,
	cep               varchar(8)  NOT NULL, 
	hora_func_ini	  time        NULL,
	hora_func_fim	  time        NULL,
	dia_sem_ini	  varchar(10) NULL,
	dia_sem_fim       varchar(10) NULL,
	imagem	          varchar(20) NULL,
	data_cadastro     timestamp   NOT NULL DEFAULT now(),
	CONSTRAINT pk_atividade   PRIMARY KEY (cod_atividade),
	CONSTRAINT fk_categ_cod   FOREIGN KEY (cod_categoria) REFERENCES TB_CATEGORIA  (cod_categoria)	
	);

	/*Tabela parceria*/
	CREATE TABLE TB_PARCERIA(	
	cod_parceria  		SERIAL,
	cod_atv_solicitada	int not null, -- cÛdigo da atividade que recebe a solicitaÁ„o
	cod_atv_solicitante	int not null, -- cÛdigo da atividade que envia  a solicitaÁ„o
	ATIVO			boolean null, -- status da parceria
	CONSTRAINT pk_parceiro	     	   PRIMARY KEY (cod_parceria),
	CONSTRAINT fk_cod_atv_solicitada   FOREIGN KEY (cod_atv_solicitada)  REFERENCES TB_ATIVIDADE (cod_atividade),
	CONSTRAINT fk_cod_atv_solicitante  FOREIGN KEY (cod_atv_solicitante) REFERENCES TB_ATIVIDADE (cod_atividade)
	);  

	
	/*Tabela classificacao*/
	CREATE TABLE TB_CLASSIFICA(
	cod_classifica     Serial,
	cod_atv_fk         int not null,    
	cod_viajante       int not null,      
	nota		   boolean not null,
	comentario         varchar(500) not null,
	data_comentario	   timestamp   NOT NULL DEFAULT now(),	
	CONSTRAINT pk_classifica     PRIMARY KEY (cod_classifica),
	CONSTRAINT fk_cod_atividade  FOREIGN KEY (cod_atv_fk) REFERENCES TB_ATIVIDADE (cod_atividade)
	);

	
	/*Tabela Lista*/
	CREATE TABLE TB_LISTA(
	cod_lista  	  Serial,
	cod_atividade_fk  int NOT NULL,
	cod_empresa_fk    int NOT NULL,
	PRIMARY KEY (cod_lista),
	CONSTRAINT fk_list_atv_cod     FOREIGN KEY (cod_atividade_fk) REFERENCES TB_ATIVIDADE (cod_atividade),
	CONSTRAINT fk_list_empresa_cod FOREIGN KEY (cod_empresa_fk)   REFERENCES TB_EMPRESA (cod_empresa)
	);

	/*Tabela Favoritos*/
	CREATE TABLE TB_FAVORITOS(
	cod_favoritos  	  Serial,
	cod_atividade_fk  int NOT NULL,
	cod_viajante_fk    int NOT NULL,
	CONSTRAINT pk_favoritos PRIMARY KEY (cod_favoritos),
	CONSTRAINT fk_FAV_atv_cod  FOREIGN KEY (cod_atividade_fk) REFERENCES TB_ATIVIDADE (cod_atividade),
	CONSTRAINT fk_list_atv_cod FOREIGN KEY (cod_viajante_fk)  REFERENCES TB_VIAJANTE  (cod_viajante)
	);

	/*Tabela Estado*/
	CREATE TABLE TB_ESTADO(
	cod_estado  	  Serial,
	nome_estado  	  varchar(50),
	CONSTRAINT pk_estado PRIMARY KEY (cod_estado)
	);

	--\Procedure Inserir Empresa
	CREATE OR REPLACE FUNCTION INSERE_EMPRESA(
	cRazao_social char,cLogradouro char,cNumero char,cComplemento char,cBairro char, cCidade char,cEstado int,cCep char,
	cTelefone char,cCnpj char,cE_mail char,cHome_page char,cUsuario char,cSenha char) RETURNS void
	
	AS $$
	BEGIN 

	   INSERT INTO TB_EMPRESA(razao_social,logradouro,numero,complemento,bairro,cidade,estado,cep,telefone,cnpj,e_mail,home_page)
	   VALUES (cRazao_social,cLogradouro,cNumero,cComplemento,cBairro,cCidade,cEstado,cCep,cTelefone,cCnpj,cE_mail,cHome_page);

	   INSERT INTO TB_USUARIO(cod_usuario,usuario,senha,tipo_usuario) 
	   VALUES (currval('tb_empresa_cod_empresa_seq'),cUsuario,cSenha,'Empresa');
	  
	END;

	$$ LANGUAGE plpgsql;

	--\Procedure ATUALIZA Empresa
	CREATE OR REPLACE FUNCTION ATUALIZA_EMPRESA(cRazao_social char,cLogradouro char,cNumero char,cComplemento char,cBairro char, 
	cCidade char,cEstado int,cCep char, cTelefone char,cCnpj char,cE_mail char,cHome_page char,cUsuario char,cSenha char, cCod_empresa int) 
	
	RETURNS void
	AS $$
	BEGIN 
		   update TB_EMPRESA SET 
			  razao_social =	cRazao_social ,
			  logradouro   =	cLogradouro ,
			  numero       =	cNumero ,   
			  complemento  =	cComplemento ,  
			  bairro       =	cBairro ,
			  cidade       =	cCidade ,
			  estado       = 	cEstado ,
			  cep	       =	cCep , 
			  telefone     =	cTelefone ,
			  cnpj         =   	cCnpj , 
			  e_mail       =	cE_mail ,
			  home_page    =	cHome_page

			 where cod_empresa = cCod_empresa;

			update TB_USUARIO SET
			
			       usuario  = cUsuario,
			       
			       senha = cSenha
			       
			where cod_usuario = cCod_empresa;

		END;

	$$ LANGUAGE plpgsql;



	--\Procedure Inserir Viajante
	CREATE OR REPLACE FUNCTION INSERE_VIAJANTE(cNome char,cLogradouro char,cNumero char,cComplemento char,cBairro char,cCidade char,
						   iEstado int,cCep char,cTelefone char,cCpf char,cE_mail char, cUsuario char,cSenha char) 
	RETURNS void
	
	AS $$
	BEGIN 

	   INSERT INTO TB_VIAJANTE(nome,Logradouro,numero,complemento,bairro,cidade,estado,cep,telefone,cpf,e_mail)
	   VALUES (cNome,cLogradouro,cNumero,cComplemento,cBairro,cCidade,iEstado,cCep,cTelefone,cCpf,cE_mail);

	   INSERT INTO TB_USUARIO(cod_usuario,usuario,senha,tipo_usuario) 
	   VALUES (currval('tb_viajante_cod_viajante_seq'),cUsuario,cSenha,'Viajante');
	   
	END;

	$$ LANGUAGE plpgsql;

	
	--\Procedure Atualizar viajante
	CREATE OR REPLACE FUNCTION ATUALIZA_VIAJANTE(
	cNome char,cLogradouro char,cNumero char,cComplemento char,cBairro char, cCidade char,iEstado int,cCep char,
	cTelefone char,cCpf char,cE_mail char,cUsuario char, cSenha char, cCod_viaj int) 

	RETURNS void
	
	AS $$

	BEGIN 

	update  TB_VIAJANTE SET 
		nome    	= cNome,
		Logradouro     	= cLogradouro,
		numero          = cNumero,   
		complemento     = cComplemento,  
		bairro       	= cBairro,
		cidade       	= cCidade,
		estado       	= iEstado,
		cep             = cCep, 
		telefone     	= cTelefone,
		cpf	        = cCpf,
		e_mail          = cE_mail
		where cod_viajante = cCod_viaj;

	update TB_USUARIO SET
	       usuario  = cUsuario,
	       senha = cSenha
	       where cod_usuario = cCod_viaj;
	   
	END;

	$$ LANGUAGE plpgsql;


	--\Procedure Inserir Atividade
	CREATE OR REPLACE FUNCTION INSERE_ATIVIDADE(
		iCod_Categoria int, cNome_atv char, cDesc_atv char, cCusto char, dPer_disp_ini char, dPer_disp_fim char, cLogradouro char,
		cNumero char,cComplemento char, cBairro char, cCep char, tHora_func_ini char,tHora_func_fim char, cDia_sem_ini char, 
		cDia_sem_fim char, cImagem char, iCod_empresa int)

		RETURNS void

		AS $$
		
		DECLARE
		-- convers„o de horas necess·ria, caso contr·rio lanÁa mensagem de erro ao tentar cadastro pela aplicaÁ„o
		dData_ini Date = dPer_disp_ini;
		dData_fim Date = dPer_disp_fim;
		tHora_ini time = tHora_func_ini;
		tHora_fim time = tHora_func_fim;

		
		BEGIN 

		-- verificaÁ„o necess·ria, pois como a procedure n„o aceita valores em branco nem nulos, ocorre uma exceÁ„o
		IF tHora_func_ini = '23:59:59' then

			tHora_ini = null;
			tHora_fim = null;			
			
		end IF;

		INSERT INTO tb_atividade(
				cod_categoria, nome_atividade, desc_atv, custo, per_disp_ini, per_disp_fim, logradouro, numero, complemento, 
				bairro, cep, hora_func_ini, hora_func_fim, dia_sem_ini, dia_sem_fim, imagem)
		VALUES (
			       iCod_categoria, cNome_atv, cDesc_atv, cCusto, dData_ini, dData_fim, cLogradouro, cNumero, cComplemento, 
			       cBairro, cCep, tHora_ini, tHora_fim, cDia_sem_ini, cDia_sem_fim, cImagem);

		INSERT INTO TB_LISTA(cod_atividade_fk, cod_empresa_fk) VALUES (currval('tb_atividade_cod_atividade_seq'), iCod_empresa); 
		 
	END;

	$$ LANGUAGE plpgsql;


	--\Procedure Atualizar viajante
	CREATE OR REPLACE FUNCTION ATUALIZA_ATIVIDADE(
		iCod_atividade int, iCod_Categoria int, cNome_atv char, cDesc_atv char, cCusto char, dPer_disp_ini char, dPer_disp_fim char,
		cLogradouro char,cNumero char,cComplemento char, cBairro char, cCep char, tHora_func_ini char,tHora_func_fim char, 
		cDia_sem_ini char, cDia_sem_fim char, cImagem char)

	RETURNS void
	
	AS $$

	DECLARE
	-- convers„o de horas necess·ria, caso contr·rio lanÁa mensagem de erro ao tentar cadastro pela aplicaÁ„o
	dData_ini Date = dPer_disp_ini;
	dData_fim Date = dPer_disp_fim;
	tHora_ini time = tHora_func_ini;
	tHora_fim time = tHora_func_fim;
	

	BEGIN 

	update  TB_ATIVIDADE SET 
		cod_categoria   = iCod_Categoria,
		nome_atividade  = cNome_atv,
		desc_atv 	= cDesc_atv,
		custo 		= cCusto,
		per_disp_ini 	= dData_ini,
		per_disp_fim 	= dData_fim,
		logradouro     	= cLogradouro,
		numero          = cNumero,   
		complemento     = cComplemento,  
		bairro       	= cBairro,
		cep		= cCep,
		hora_func_ini	= tHora_ini,
		hora_func_fim	= tHora_fim,
		dia_sem_ini	= cDia_sem_ini,
		dia_sem_fim	= cDia_sem_fim,
		imagem		= cImagem
		
		WHERE tb_atividade.cod_atividade = iCod_atividade;		
	END;

	$$ LANGUAGE plpgsql;


	--\Procedure Inserir classificacao
	CREATE OR REPLACE FUNCTION INSERE_CLASSIFICACAO(iCod_atividade int, iCod_viajante int, bNota boolean, cComentario char)
	RETURNS void
	
	AS $$
	BEGIN 

	   INSERT INTO TB_CLASSIFICA(cod_atv_fk, cod_viajante, nota, comentario)
	   VALUES (iCod_atividade, iCod_viajante, bNota, cComentario);
	   
	END;

	$$ LANGUAGE plpgsql;


	CREATE OR REPLACE FUNCTION INSERE_PARCEIRO(iCod_atv_solicitada int, iCod_atv_solicitante int)
	RETURNS void
	
	AS $$
	BEGIN 

	   INSERT INTO TB_PARCERIA(cod_atv_solicitada, cod_atv_solicitante)
	   VALUES (iCod_atv_solicitada, iCod_atv_solicitante);
	   
	END;

	$$ LANGUAGE plpgsql;	

	-- TODO: CRIAR PROCEDURE DE ATUALIZA«√O DE PARCERIA
	
	
	-- INSER«’ES OBRIGAT”RIAS
	insert into tb_categoria (nome_categoria, descricao_categoria, tipo_categoria) values ('Hospedagem', 'HotÈis, pousadas, pensıes ou qualquer serviÁo relacionado ‡ estadia fornecido pela empresa', 'serviÁo');

	insert into tb_categoria (nome_categoria, descricao_categoria, tipo_categoria) values ('Cultura e Lazer', 'PeÁas de teatro, show musicais, oficinas e festas', 'evento');

	insert into tb_categoria (nome_categoria, descricao_categoria, tipo_categoria) values ('Gastronomia', 'Restaurantes, bares e lanchonetes', 'evento');

	insert into tb_categoria (nome_categoria, descricao_categoria, tipo_categoria) values ('NegÛcios', ' Feiras e eventos, workshops e palestras', 'serviÁo');


	-- TESTES 
	
	-- INSER«√O DE EMPRESA COM PROCEDURE INSERE_EMPRESA

	select INSERE_EMPRESA('Mochileiro Company','Rua sem nome','12','Complemento','jd sem nada','terra do nunca',4,'12345678','1187654321','11111111111111',
	'mochileiro@mochileiro.com.br','www.mochileiro.com.br','mochileiro','mochileiro');

	-- ATUALIZA«√O DE EMPRESA COM PROCEDURE ATUALIZA_EMPRESA
/*	
	select ATUALIZA_EMPRESA('mudei a procedure','Rua sem nome','12','Complemento','jd sem nada','terra do nunca',4,'12345678','1187654321','12345678901235',
	'mochileiro@mochileiro.com.br','www.mochileiro.com.br','mudei','123456', 1);
*/

	-- INSER«√O COM PROCEDURE INSERE_ATIVIDADE

	SELECT INSERE_ATIVIDADE ( 1, 'Testes«sss«sss ysys', 'cDesc_atv', '100.000,00', '2009-12-05', '2009-12-05', 'cLogradouro', 'Numero', 
				'cComplemento', 'cBairro', '12345678', '11:25:35', '11:29:35', 'Segunda', 'Sexta', 'noimage.jpg', 1);

	SELECT INSERE_CLASSIFICACAO( 1,1, true, 'Lorem ipsum dolor sit amet, rutrum sapien pellentesque placerat voluptatibus mus arcu. Est elit quam a enim,    fermentum mauris.Lorem ipsum dolor sit amet, rutrum sapien');
	SELECT INSERE_CLASSIFICACAO( 1,1, false, 'Lorem ipsum dolor sit amet, rutrum sapien pellentesque placerat voluptatibus mus arcu. Est elit quam a enim,    fermentum mauris.Lorem ipsum dolor sit amet, rutrum sapien');

	
	

	SELECT INSERE_VIAJANTE('cNomE','cLogradouro' ,'Numero' ,'cComplemento' ,'cBairro' ,'cCidade' ,1 ,'12345678' ,'1234567890' ,'12345678901' ,
				'cE_mail@email.com', 'mochila', 'mochila');
	

	SELECT INSERE_PARCEIRO( 1,1);

select * from tb_atividade inner join tb_lista on tb_atividade.cod_atividade = tb_lista.cod_atividade_fk
					       inner join tb_classifica on tb_atividade.cod_atividade = tb_classifica.cod_atv_fk


					       

select * from tb_classifica
