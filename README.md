<img width="1091" height="451" alt="ER-диаграмма" src="https://github.com/user-attachments/assets/91ecaab9-42ba-4020-91c5-2b83ea794193" />

Получение всех пользователей таблицы:

SELECT *
FROM user

Получить список друзей пользователя

SELECT u.NAME
FROM friends f
JOIN user u ON f.friend_id = u.id
where f.user_id = 'user_id'

Получить топ 10 фильмов по популярности

SELECT f.name,
  COUNT(l.user_id) AS likes_count
FROM film f
JOIN likes l ON l.film_id = f.id
GROUP BY f.id, f.name
ORDER BY likes_count DESC
LIMIT 10
