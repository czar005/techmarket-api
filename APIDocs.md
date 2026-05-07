
# TechMarket API Документация

## Аутентификация

Все защищённые эндпоинты требуют JWT токен в заголовке:
Authorization: Bearer <token>

## Эндпоинты

### Аутентификация (`/api/auth`)

| Метод | URL | Описание | Защищён |
|-------|-----|----------|---------|
| POST | `/api/auth/register` | Регистрация | Нет |
| POST | `/api/auth/login` | Вход | Нет |

**Request body (register):**
```json
{"email": "string", "password": "string", "role": "USER|ADMIN"}
Request body (login):

json
{"email": "string", "password": "string"}
Response:

json
{"token": "jwt-token"}
Объявления (/api/listings)
Метод	URL	Описание	Защищён
POST	/api/listings/add	Создать объявление	Да
GET	/api/listings/{id}	Получить по ID	Нет
GET	/api/listings	Все объявления (пагинация)	Нет
POST	/api/listings/filter	Фильтрация	Нет
GET	/api/listings/user	Мои объявления	Да
PUT	/api/listings/{id}	Обновить	Да
DELETE	/api/listings/{id}	Удалить	Да
GET	/api/listings/brand/{brand}	По бренду	Нет
GET	/api/listings/condition/{condition}	По состоянию	Нет
GET	/api/listings/price-range	По диапазону цен	Нет
Параметры пагинации (GET /api/listings):

page (0) - номер страницы

size (10) - размер страницы

sortBy (createdAt) - поле сортировки

direction (desc) - asc/desc

Request body (создание/обновление):

json
{"title": "string", "brand": "string", "price": 0, "condition": "NEW|LIKE_NEW|USED|REFURBISHED"}
Request body (фильтр):

json
{"brand": "string", "minPrice": 0, "maxPrice": 0, "condition": "NEW|LIKE_NEW|USED|REFURBISHED"}
Response:

json
{"id": 0, "title": "string", "brand": "string", "price": 0, "condition": "string", "status": "ACTIVE|RESERVED|SOLD", "ownerId": 0, "createdAt": "date", "updatedAt": "date"}
Сделки (/api/deals)
Метод	URL	Описание	Защищён
POST	/api/deals/purchase-request	Создать запрос на покупку	Да
PUT	/api/deals/{dealId}/confirm	Подтвердить	Да
PUT	/api/deals/{dealId}/complete	Завершить	Да
PUT	/api/deals/{dealId}/cancel	Отменить	Да
GET	/api/deals/{dealId}	Получить по ID	Да
GET	/api/deals/user	Мои сделки	Да
Request body (создание): {"listingId": 0}

Response:

json
{"id": 0, "listingId": 0, "listingTitle": "string", "buyerId": 0, "buyerEmail": "string", "sellerId": 0, "sellerEmail": "string", "status": "PENDING|CONFIRMED|COMPLETED|CANCELLED", "createdAt": "date", "updatedAt": "date", "completedAt": "date"}
Мониторинг (/actuator)
Метод	URL	Описание
GET	/actuator/health	Проверка здоровья
GET	/actuator/metrics	Метрики
GET	/actuator/info	Информация
Home
Метод	URL	Описание
GET	/	Проверка работы
Коды ответов
Код	Описание
200	Успех
201	Создано
204	Удалено
400	Ошибка запроса
401	Не авторизован
403	Доступ запрещён
404	Не найдено
500	Ошибка сервера