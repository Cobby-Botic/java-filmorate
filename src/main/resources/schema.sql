CREATE TABLE IF NOT EXISTS users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    login VARCHAR(40),
    email VARCHAR(40) UNIQUE,
    name VARCHAR(40),
    birthdate DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS MPA (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(5)
);

CREATE TABLE IF NOT EXISTS movies (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40),
    releaseDate DATE NOT NULL,
    duration INT,
    mpa_id INT,
    description VARCHAR(200),
    FOREIGN KEY (mpa_id) REFERENCES MPA(id)
);

CREATE TABLE IF NOT EXISTS genres (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(40)
);

CREATE TABLE IF NOT EXISTS movie_genres (
  movie_id INT,
  genre_id INT,
  PRIMARY KEY (movie_id, genre_id),
  FOREIGN KEY (movie_id) REFERENCES movies(id),
  FOREIGN KEY (genre_id) REFERENCES genres(id)
);

CREATE TABLE IF NOT EXISTS friendships (
    user_id INT NOT NULL,
    friend_id INT NOT NULL,
    status ENUM('pending', 'accepted', 'declined') NOT NULL,
    PRIMARY KEY (user_id, friend_id),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (friend_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS likes (
    movie_id INT NOT NULL,
    user_id INT NOT NULL,
    PRIMARY KEY (movie_id, user_id),
    FOREIGN KEY (movie_id) REFERENCES movies(id),
    FOREIGN KEY (user_id) REFERENCES users(id)
);
