DELIMITER $$

CREATE PROCEDURE add_movie (IN in_title VARCHAR(100), IN in_year INT(11), IN in_director VARCHAR(100), IN in_star VARCHAR(100), IN in_genre VARCHAR(32), OUT out_message VARCHAR(255))
BEGIN
    SET @movieInDB = (SELECT EXISTS( SELECT * FROM movies WHERE movies.title = in_title AND movies.year = in_year AND movies.director = in_director) );

    IF (@movieInDB=1) THEN
        SET out_message = "Movie already exists.";
    ELSE
        SET @maxMovieId = ( SELECT max( id ) FROM movies );
        SET @maxMovieIdNumber = ( SELECT SUBSTR( @maxMovieId , 3 ) );
        SET @newMovieIdNumber = ( SELECT @maxMovieIdNumber + 1);
        SET @newMovieId = ( SELECT CONCAT( "tt", LPAD( @newMovieIdNumber, 7, 0 ) ) );

        INSERT INTO movies (id, title, year, director) VALUES (@newMovieId, in_title, in_year, in_director);


        SET @genreExists = (SELECT EXISTS(SELECT * FROM genres WHERE name = in_genre));
        IF (@genreExists=1) THEN
            SET @genreId = (SELECT id from genres WHERE name = in_genre);

            INSERT INTO genres_in_movies VALUES (@genreId, @newMovieId);
        ELSE
            SET @maxGenreId = ( SELECT max(id) FROM genres );
            SET @newGenreId = ( SELECT @maxGenreId + 1);

            INSERT INTO genres (id, name) VALUES (@newGenreId, in_genre);
            INSERT INTO genres_in_movies (genreId, movieId) VALUES (@newGenreId, @newMovieId);
        END IF;

        SET @starExists = (SELECT EXISTS(SELECT * FROM stars WHERE name = in_star LIMIT 1));
        IF (@starExists=1) THEN
            SET @starId = (SELECT id FROM stars WHERE name = in_star LIMIT 1);

            INSERT INTO stars_in_movies (starId, movieId) VALUES (@starId, @newMovieId);
        ELSE
            
        END IF;

        SET out_message = CONCAT("\"", in_title, "\" added to database.");
    END IF;

END
$$

DELIMITER ;