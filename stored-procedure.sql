DELIMITER $$

CREATE PROCEDURE add_movie (IN title VARCHAR(100), IN year INT(11), IN director VARCHAR(100), IN star VARCHAR(100), IN genre VARCHAR(32))
BEGIN


    SET @movieInDB = (SELECT EXISTS( SELECT * FROM movies WHERE movies.title = @title AND movies.year = @year AND movies.director = @director) );

    SET @maxMovieId = (SELECT max( id ) FROM movies);

    SELECT CONCAT( "tt", LPAD( SUBSTR( @maxMovieId , 3 ) + 1, 7, 0 ) ) AS newMovieId;


END
$$

DELIMITER ;