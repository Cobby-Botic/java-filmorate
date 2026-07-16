<img width="1091" height="451" alt="ER-диаграмма" src="https://github.com/user-attachments/assets/91ecaab9-42ba-4020-91c5-2b83ea794193" />

Получение всех пользователей таблицы:

SELECT *
FROM users

Получить список друзей пользователя

SELECT u.NAME
FROM friendships f
JOIN user u ON f.friend_id = u.id
where f.user_id = 'user_id'

Получить топ 10 фильмов по популярности

SELECT m.name,
  COUNT(l.user_id) AS likes_count
FROM movies m
JOIN likes l ON l.film_id = m.id
GROUP BY m.id, m.name
ORDER BY likes_count DESC
LIMIT 10
