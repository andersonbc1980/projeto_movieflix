import pandas as pd
from sqlalchemy import create_engine, text
import time
import os


PG_USER = os.getenv("PG_USER", "user")
PG_PASS = os.getenv("PG_PASS", "secret")
PG_DB   = os.getenv("PG_DB", "movieflix-db")
PG_HOST = os.getenv("PG_HOST", "pg-dados")  # hostname do container Postgres na rede docker
PG_PORT = os.getenv("PG_PORT", "5432")

# string de conexão 
conn_str = f"postgresql+psycopg2://{PG_USER}:{PG_PASS}@{PG_HOST}:{PG_PORT}/{PG_DB}"

print("Conexão:", conn_str)

time.sleep(3)


engine = create_engine(conn_str, echo=False)

#MOVIES

import unicodedata
csv_path = "data/movies.csv"
print("Lendo CSV:", csv_path)
df = pd.read_csv(csv_path)

# Padronizar nomes das colunas: remover espaços e acentos
def normalize_col(col):
    col = col.strip()
    col = unicodedata.normalize('NFKD', col).encode('ASCII', 'ignore').decode('ASCII')
    col = col.replace(' ', '_').replace('-', '_')
    return col.lower()

df.columns = [normalize_col(c) for c in df.columns]

# 2) TRANSFORM: renomear colunas simples 
df = df.rename(columns={
    "titulo": "title",
    "anolancamento": "movie_year",
    "ano_lancamento": "movie_year",
    "genero": "genres"
})


# Garantir colunas esperadas (se não existir, cria com NaN)
for col in ["title", "movie_year", "genres"]:
    if col not in df.columns:
        df[col] = None

# Remover colunas extras que não são esperadas pelo banco
df = df[["title", "movie_year", "genres"]]

# Tratar dados simples
df["title"] = df["title"].astype(pd.StringDtype()).str.strip()
df["genres"] = df["genres"].astype(pd.StringDtype()).str.strip()
# converter ano com valores default quando faltam
df["movie_year"] = pd.to_numeric(df["movie_year"], errors="coerce").fillna(0).astype(int)

# Descarta linhas essenciais faltando
df_clean = df.dropna(subset=["title", "genres"])
df_clean = df_clean[df_clean["title"].str.strip() != ""]
df_clean = df_clean[df_clean["movie_year"] > 1910]
df_clean = df_clean[df_clean["genres"].str.strip() != ""]

print("Preview após transformação:")
print(df_clean.head())

# Gravar linhas excluídas em um arquivo csv
df_invalid = df[~df.index.isin(df_clean.index)]
df_invalid.to_csv("data/movie_invalid.csv", index=False)

# 3) LOAD: gravar no Postgres - Data Warehouse (tabela 'movies')
with engine.begin() as conn:
    # criar schema/tabela caso não exista (cria via SQL simples)
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS movies (
        movie_id SERIAL PRIMARY KEY,
        title TEXT,
        movie_year INTEGER,
        genres TEXT
        
    );
    """
    conn.execute(text(create_table_sql))

    conn.execute(text("TRUNCATE TABLE movies;"))


df_clean.to_sql("movies", engine, if_exists="append", index=False)

print("✅ Dados carregados na tabela 'movies' (Data Warehouse).")

with engine.connect() as conn:
    total = conn.execute(text("SELECT COUNT(*) FROM movies;")).scalar()
    print(f"Registros no Warehouse (movies): {total}")

####### RATING #######
import unicodedata
csv_path = "data/rating.csv"
print("Lendo CSV:", csv_path)
df = pd.read_csv(csv_path)

# Padronizar nomes das colunas: remover espaços e acentos
def normalize_col(col):
    col = col.strip()
    col = unicodedata.normalize('NFKD', col).encode('ASCII', 'ignore').decode('ASCII')
    col = col.replace(' ', '_').replace('-', '_')
    return col.lower()

df.columns = [normalize_col(c) for c in df.columns]

# 2) TRANSFORM: renomear colunas simples 
df = df.rename(columns={
    "idusuario": "user_id",
    "id_usuario": "user_id",
    "userid": "user_id",
    "idfilme": "movie_id",
    "id_filme": "movie_id",
    "movieid": "movie_id",
    "nota": "rating",
    "rating": "rating",
    "datahora": "rating_ts",
    "data_hora": "rating_ts",
    "timestamp": "rating_ts"
})


# Selecionar apenas colunas conhecidas (se existirem)
expected_cols = ["user_id", "movie_id", "rating", "rating_ts"]
df = df[[c for c in expected_cols if c in df.columns]]

print("Colunas finais no DF:", df.columns.tolist())
print(df.head())

# converter tipos
df["rating"] = pd.to_numeric(df["rating"], errors="coerce").fillna(0).astype(float)

# Descarta linhas essenciais faltando
df_clean = df.dropna(subset=["user_id", "movie_id", "rating", "rating_ts"])
# filtrar notas válidas
df_clean = df_clean[(df_clean["rating"] >= 0.5) & (df_clean["rating"] <= 5.0)]

print("Preview após transformação:")
print(df_clean.head())

# Gravar linhas excluídas em um arquivo csv
df_invalid = df[~df.index.isin(df_clean.index)]
df_invalid.to_csv("data/rating_invalid.csv", index=False)

# 3) LOAD: gravar no Postgres - Data Warehouse (tabela 'ratings')
with engine.begin() as conn:
    # criar schema/tabela caso não exista (cria via SQL simples)
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS ratings (
        id SERIAL PRIMARY KEY,
        user_id INTEGER,
        movie_id INTEGER,
        rating REAL,
        rating_ts TIMESTAMP
        
    );
    """
    conn.execute(text(create_table_sql))

    conn.execute(text("TRUNCATE TABLE ratings;"))


df_clean.to_sql("ratings", engine, if_exists="append", index=False)

print("✅ Dados carregados na tabela 'ratings' (Data Warehouse).")

with engine.connect() as conn:
    total = conn.execute(text("SELECT COUNT(*) FROM ratings;")).scalar()
    print(f"Registros no Warehouse (ratings): {total}")

########### USERS #############
import unicodedata
csv_path = "data/users.csv"
print("Lendo CSV:", csv_path)
df = pd.read_csv(csv_path)

# Padronizar nomes das colunas: remover espaços e acentos
def normalize_col(col):
    col = col.strip()
    col = unicodedata.normalize('NFKD', col).encode('ASCII', 'ignore').decode('ASCII')
    col = col.replace(' ', '_').replace('-', '_')
    return col.lower()

df.columns = [normalize_col(c) for c in df.columns]

# 2) TRANSFORM: renomear colunas simples 
df = df.rename(columns={
    "pais": "country",
    "anoaniversario": "birth_year",
    "ano_aniversario": "birth_year"
})


# Garantir colunas esperadas (se não existir, cria com NaN)
for col in ["country", "birth_year"]:
    if col not in df.columns:
        df[col] = None

# Remover colunas extras que não são esperadas pelo banco
df = df[["country", "birth_year"]]

# Tratar dados simples
df["country"] = df["country"].astype(pd.StringDtype()).str.strip()
# converter ano com valores default quando faltam
df["birth_year"] = pd.to_numeric(df["birth_year"], errors="coerce").fillna(0).astype(int)

# Descarta linhas essenciais faltando
df_clean = df.dropna(subset=["country"])
df_clean = df_clean[df_clean["country"].str.strip() != ""]
df_clean = df_clean[df_clean["birth_year"] > 1910]

print("Preview após transformação:")
print(df_clean.head())

# Gravar linhas excluídas em um arquivo csv
df_invalid = df[~df.index.isin(df_clean.index)]
df_invalid.to_csv("data/user_invalid.csv", index=False)

# 3) LOAD: gravar no Postgres - Data Warehouse (tabela 'users')
with engine.begin() as conn:
    # criar schema/tabela caso não exista (cria via SQL simples)
    create_table_sql = """
    CREATE TABLE IF NOT EXISTS users (
        user_id SERIAL PRIMARY KEY,
        country TEXT,
        birth_year INTEGER
        
    );
    """
    conn.execute(text(create_table_sql))

    conn.execute(text("TRUNCATE TABLE users;"))


df_clean.to_sql("users", engine, if_exists="append", index=False)

print("✅ Dados carregados na tabela 'users' (Data Warehouse).")

with engine.connect() as conn:
    total = conn.execute(text("SELECT COUNT(*) FROM users;")).scalar()
    print(f"Registros no Warehouse (users): {total}")


print("Fim do ETL.")