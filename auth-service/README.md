# API IoT Cloud

## Содержание

- [Общая информация](#общая-информация)
- [API пользователей](#api-пользователей)
  - [Регистрация пользователя](#регистрация-пользователя)
  - [Подтверждение email](#подтверждение-email)
  - [Запрос кода для входа](#запрос-кода-для-входа)
  - [Вход в систему](#вход-в-систему)
  - [Проверка устройства](#проверка-устройства)
- [Общие эндпоинты API](#общие-эндпоинты-api)
  - [Получение информации о текущем пользователе](#получение-информации-о-текущем-пользователе)
  - [Тестовый эндпоинт для админа](#тестовый-эндпоинт-для-админа)
  - [Запрещенный эндпоинт](#запрещенный-эндпоинт)
  - [Публичный эндпоинт](#публичный-эндпоинт)
  - [Статус пользователя](#статус-пользователя)
- [Модели данных](#модели-данных)
- [Роли пользователей](#роли-пользователей)
- [Примечания](#примечания)

## Общая информация

- Базовый URL для API пользователей: /users
- Базовый URL для общих эндпоинтов: /api
- Формат данных: JSON

## Эндпоинты

### API пользователя

Регистрирует нового пользователя в системе.

- URL: /register
- Метод: POST
- Тело запроса: Объект User в JSON
- Успешный ответ:
    - Код: 201 CREATED
    - Содержание: { "message": "Пользователь зарегистрирован. Проверьте почту для подтверждения." }
- Ошибка:
    - Код: 409 CONFLICT
    - Содержание: { "message": "Пользователь с таким email уже существует" }

### Подтверждение email

Подтверждает email пользователя.

- URL: /confirm-email
- Метод: POST
- Параметры запроса:
    - email=[string]
    - code=[string]
- Успешный ответ:
    - Код: 200 OK
    - Содержание: { "message": "Email подтвержден. Теперь вы можете войти в систему." }
- Ошибки:
    - Код: 400 BAD REQUEST
    - Содержание: { "message": "Неверный код подтверждения" }
    - Код: 404 NOT FOUND
    - Содержание: { "message": "Пользователь не найден" }

### Запрос кода для входа

Генерирует и отправляет код для входа.

- URL: /request-code
- Метод: POST
- Параметры запроса:
    - email=[string]
- Успешный ответ:
    - Код: 200 OK
    - Содержание: { "message": "Код отправлен на вашу почту" }
- Ошибка:
    - Код: 404 NOT FOUND
    - Содержание: { "message": "Пользователь не найден" }

### Вход в систему

Выполняет вход пользователя в систему.

- URL: /login
- Метод: POST
- Параметры запроса:
    - email=[string]
    - code=[string]
    - deviceId=[string]
- Успешный ответ:
    - Код: 200 OK
    - Содержание: { "token": "ваш_токен", "deviceHash": "хеш_устройства" }
- Ошибки:
    - Код: 401 UNAUTHORIZED
    - Содержание: { "message": "Код недействителен или просрочен" }
    - Код: 403 FORBIDDEN
    - Содержание: { "message": "Аккаунт не активирован. Подтвердите email." }
    - Код: 404 NOT FOUND
    - Содержание: { "message": "Пользователь не найден" }

### Проверка устройства

Проверяет и обновляет хеш устройства пользователя.

- URL: /verify-device
- Метод: POST
- Параметры запроса:
    - userId=[long]
    - deviceId=[string]
    - hash=[string]
- Успешный ответ:
    - Код: 200 OK
    - Содержание: { "newDeviceHash": "новый_хеш_устройства" }
- Ошибка:
    - Код: 401 UNAUTHORIZED
    - Содержание: { "message": "Неверные данные устройства" }

## Общие эндпоинты API

### Получение информации о текущем пользователе

Возвращает информацию о текущем авторизованном пользователе.

- URL: /api/
- Метод: GET
- Требуемые роли: USER, SERVICE
- Успешный ответ:
  - Код: 200 OK
  - Содержание: Объект User в JSON
- Ошибки:
  - Код: 403 FORBIDDEN
  - Содержание: { "message": "Аккаунт не активирован. Пожалуйста, подтвердите ваш email." }
  - Код: 404 NOT FOUND
  - Содержание: { "message": "Пользователь не найден" }

### Тестовый эндпоинт для админа

Проверяет доступ пользователя с ролью админа.

- URL: /api/admin
- Метод: GET
- Требуемая роль: ADMIN
- Успешный ответ:
  - Код: 200 OK
  - Содержание: { "message": "Доступ к админ-панели предоставлен для [email]" }

### Запрещенный эндпоинт

Этот эндпоинт всегда запрещен для всех пользователей.

- URL: /api/void
- Метод: GET
- Ответ:
  - Код: 403 FORBIDDEN
  - Содержание: { "message": "Доступ запрещен" }

### Публичный эндпоинт

Доступен для всех пользователей, включая неавторизованных.

- URL: /api/public
- Метод: GET
- Успешный ответ:
  - Код: 200 OK
  - Содержание: { "message": "Это публичный эндпоинт" }

### Статус пользователя

Возвращает информацию о текущем статусе пользователя.

- URL: /api/status
- Метод: GET
- Успешный ответ для авторизованного пользователя:
  - Код: 200 OK
  - Содержание: { "message": "Вы вошли как [email] с ролью: [роль]" }
- Ответ для неавторизованного пользователя:
  - Код: 200 OK
  - Содержание: { "message": "Вы не авторизованы" }

## Модели данных

### User
- email: String
- isActive: Boolean
- confirmationCode: String (временный)

### OneTimeCode
- userId: Long
- code: String
- expirationTime: LocalDateTime

### UserDeviceHash
- userId: Long
- deviceId: String
- hash: String

## Примечания

- Все запросы и ответы используют формат JSON.
- Для операций входа и подтверждения используются одноразовые коды вместо паролей.
- Система использует механизм подтверждения устройств для дополнительной безопасности.
- Убедитесь, что вы храните deviceId и deviceHash на стороне клиента для последующих запросов.