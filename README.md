🎬 MovieFlix Analytics
Curso: Arquitetura de Serviços e Dados

Foi criada uma plataforma simples de cadastro e avaliação de filmes, que, inicialmente, busca dados de uma base bruta e trata esses dados de forma a deixá-los aptos para inserção no banco de dados. Após a inserção nos bancos (movies, ratings, users), o programa faz consultas e análises desses dados, para entender preferências, tendências e apoiar decisões de negócio.

🛠️ Tecnologias Utilizadas
Spring boot + Java 21 - backend da aplicação
Banco de dados H2 para testes e Postgres para desenvolvimento e produção.
Docker e Docker hub - Containerização
Python + Pandas + SQLAlchemy - Processos ETL
Git hub Actions - Integração Contínua e Testes

🏗️ Arquitetura do Sistema

Data Lake
  Um Data Lake é um repositório centralizado que permite armazenar todos os seus dados estruturados e não estruturados em qualquer escala.
  No projeto foram usados 3 arquivos .csv (movies.csv, rating.csv e user.csv), que contém dados não estruturados de filmes, notas de usuários e usuários.

ETL - Extrair, Transformar e Carregar
  O termo ETL, do inglês "Extract, Transform and Load" ou em tradução livre "Extrair, Transformar e Carregar", refere-se ao processo de extração e preparação dos dados para serem persistidos em nosso Data Warehouse.
  Os dados desses arquivos são tratados de forma que somente dados consistentes sejam incluídos no banco de dados, pois dados faltantes podem gerar discrepâncias na futura análise pelo sistema.
  Filmes(movie) sem título, sem gênero, sem ano de lançamento são descartados.
  Notas(rating) sem id do filme, sem id do usuário, com notas negativas ou acima de 5,0, sem data/hora são descartados.
  Usuários(user) sem país, sem ano de nascimento ou com ano de nascimento abaixo de 1910, são descartados.
  Os dados tratados são inseridos no banco de dados (POSTGRES) e os dados descartados são gravados em outros arquivos .csv (movie_invalid.csv, rating_invalid.csv, user_invalid.csv) para posterior possível tratamento, caso seja interessante ao negócio.

Data Warehouse
  Data Warehouse é um banco de informações referentes ao negócio da empresa ou área, onde os dados são ingeridos e processados a fim de responder às dúvidas das áreas de negócio, como, por exemplo: Relatórios de vendas, qualidade, engajamento do cliente, etc.
  No projeto os dados tratados são inseridos no banco de dados Postgres, onde são usados para gerar consultas analíticas, que respondem:
  
  - Filmes mais bem avaliados da última semana
      SELECT m.title, AVG(r.rating) as avgRating
      FROM Rating r JOIN Movie m ON r.movie_id = m.movie_id
      WHERE r.rating_ts >= :sevenDaysAgo
      GROUP BY m.title ORDER BY avgRating DESC"
    
- Top 5 filmes mais bem avaliados de toda a base
      SELECT m.title, AVG(r.rating) as avgRating, COUNT(r) as total
      FROM Rating r JOIN Movie m ON r.movie_id = m.movie_id
      GROUP BY m.title ORDER BY avgRating DESC
  
- Média de avaliação por gênero
      SELECT u.genres, AVG(r.rating)
      FROM Rating r JOIN Movie u ON r.movie_id = u.movie_id
      GROUP BY u.genres ORDER BY AVG(r.rating) DESC

- Média por país
      SELECT u.country, AVG(r.rating)
      FROM Rating r JOIN User u ON r.user_id = u.user_id
      GROUP BY u.country ORDER BY AVG(r.rating) DESC

- Quantidade de avaliações por país
      SELECT u.country, COUNT(r)
      FROM Rating r JOIN User u ON r.user_id = u.user_id
      GROUP BY u.country ORDER BY COUNT(r) DESC

▶️ Como Executar o Projeto
  
  A aplicação roda no docker. Para isso foi criada uma rede para que os contêiner se comuniquem no Docker.
      docker network create movieflix-net
      
  Após a criação da rede é executado o contêiner do postgres, contendo as informações essenciais do banco de dados e criando um volume, para guardar as informações mesmo se o conteineir cair.
      docker run -d --name postgres-dados --network movieflix-net -e POSTGRES_USER=user -e POSTGRES_PASSWORD=secret -e POSTGRES_DB=movieflix-db -p 5432:5432 -v C:\Users\ander\OneDrive\Documentos\ProjetoMovieFlix\movieflix\Postgres\pgdata/:/var/lib/postgresql/data postgres:15
      
  A partir daí é criada a imagem que contém o ETL que busca os dados brutos contidos nos arquivos .csv, faz o tratamento e grava os dados corretos no banco de dados.
      docker build -t etl-postgres .
      docker run --rm --name etl-run --network movieflix-net -v C:\Users\ander\OneDrive\Documentos\ProjetoMovieFlix\movieflix\Postgres\data/:/app/data -e PG_USER=user -e PG_PASS=secret -e PG_DB=movieflix-db -e PG_HOST=postgres-dados etl-postgres

  Após isso é criado o contêiner da aplicação
      docker build -t movieflix-app .
      docker run -d --name movieflix --network movieflix-net movieflix-app

E finalmente a imagem do Ngnix que servirá como proxy reverso da aplicação. Aplicação roda na porta 8081 e o nginx na porta 8080. Quando acionamos  o nginx distribui para a aplicação.
      docker build -t movieflix-nginx .
      docker run -d --name proxy --network movieflix-net -p 8080:80 movieflix-nginx


Docker desktop mostrando os conteiners ativos.

<img width="886" height="93" alt="image" src="https://github.com/user-attachments/assets/57dd4752-f234-4bf9-8830-611a3ac926f8" />


Aplicação Web rodando no navegador
A página principal mostra opções de cadastramento de filmes, notas e usuários.
Mostra também a listagem de todos os filmes, notas e usuários até o momento cadastrados.
Ao clicar no link: VER ANALYTICS, uma nova página é aberta.
<img width="879" height="1138" alt="image" src="https://github.com/user-attachments/assets/3136f7d4-4199-41c2-be12-9d873cd1de64" />
<img width="886" height="1169" alt="image" src="https://github.com/user-attachments/assets/000ca437-ed8c-4cc1-a932-fea2fe32fe19" />
<img width="797" height="1334" alt="image" src="https://github.com/user-attachments/assets/aaf9a39f-9470-41cc-b9e9-6988e62024a8" />
<img width="786" height="1313" alt="image" src="https://github.com/user-attachments/assets/a1cfe6b3-33f9-4786-a337-fc8c00c49c7b" />
<img width="569" height="878" alt="image" src="https://github.com/user-attachments/assets/24a305a9-8528-4289-a1e1-10a392ae84b3" />

A página Analytics contém as análises feitos dos dados para auxiliar na tomada de decisões.
<img width="845" height="1459" alt="image" src="https://github.com/user-attachments/assets/95ba6ecd-71af-4457-81cc-a8c2d4439496" />
<img width="886" height="721" alt="image" src="https://github.com/user-attachments/assets/0d7a434d-73cf-44a8-8619-f3259502cd82" />
<img width="886" height="895" alt="image" src="https://github.com/user-attachments/assets/454a57fa-eeda-4acd-8f05-78b72a5bcb1c" />
<img width="886" height="867" alt="image" src="https://github.com/user-attachments/assets/0b675442-687f-48bb-8163-d319cb7833da" />

⚙️ Testes Automáticos (CI/CD)

No GitHub Actions, o fluxo de teste:
Cria a rede Docker.
Sobe o PostgreSQL.
Roda a aplicação Java.
Sobe o proxy reverso (Nginx).
Executa um teste simples com curl para verificar a resposta da aplicação.

👨‍💻 Autor
Anderson Barbosa Chaves
