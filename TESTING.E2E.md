## Testes de ponta-a-ponta

#### Este documento cobre como rodar os testes end-to-end (e2e) localmente. Deve-se ter configurado todos os requisitos abaixo:

- Docker;
- Ambos os repositórios [api](https://github.com/notehubbr/notehub-api) e [client](https://github.com/notehubbr/notehub-client) devidamente instalados;
- Variáveis de ambiente configuradas (`.env.example` -> `.env`).

#### Abaixo estão configurações específicas para cada projeto, é necessário segui-las:

<details>

<summary>Back-end</summary>

> A rota `/api/v1/test/**` estar pública nas configurações não é uma brecha de segurança em produção, ela só existe sob o perfil `e2e`.

#### 1. Perfil

Ative o perfil para testes end-to-end no `.env`.

```bash
SPRING_PROFILES_ACTIVE=e2e
```

#### 2. Ambiente

Suba o banco de testes e a aplicação.

> Caso já tenha levantado o container "app" previamente, é necessário refazer a reconstrução para aplicar as alterações, ex.: `... -d --build app`.

```bash
docker compose --profile e2e up -d
```

> Roda na porta `5433` do host por padrão (mapeada da porta `5432` do container), permitindo rodar em paralelo com o container `postgres` normal. Os dados ficam em `tmpfs` — tudo é apagado quando o container reinicia. Não depender dele para persistir estado entre execuções.

</details>

<details>

<summary>Front-end</summary>

#### O arquivo `global-setup.ts` é o responsável por popular o banco de dados antes de serem realizados todos os testes, não é necessário executá-lo previamente.

> Considere necessário ter o back-end executando sob o perfil `e2e`.

#### 1. Build

```bash
npm run build
```

#### 2.1 Testando globalmente

```bash
npm run test:e2e
```

#### 2.2 Testando o arquivo em si

```bash
npx playwright test tests/.../file.ts
```

#### 2.3 Testando especificamente

```bash
npx playwright test -g "test description"
```

</details>

#### Fechando os serviços docker

```
docker compose --profile e2e down
```