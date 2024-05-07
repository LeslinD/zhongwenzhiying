# DAO 

## Brief Description

*DAO* is the layer communicating between the *database* and the *servlet*.

*DAO* is operating on *Entity*, but not the one-to-one with *Entity*.

## Class Explanation

### Commonality

First of all, all the DAOs share the same name of a static method called `QueryAndResolve`. This method is used to send SQL statements to database and get the `ResultSet rs`, then resolve it to entity(or entity List) in a uniform way inside the same DAO.In other words, different DAO has different implementation of `QueryAndResolve`.
Only methods generating *Query Statements*(i.e. `SELECT` Statements) call `QueryAndResolve`.

Besides, adhering to the principle "data be separated from program", we place all the table names in [TableName.java](TableName.java), which is quite frequently used by DAOs to generate SQL statements.

Lastly, every DAO typically has methods to `INSERT`,`DELETE`,`UPDATE` on top of specific `SELECT`, which will call another resolve method in [`uitl.SQLUtil.Update`](../util/SQLUtil.java).

### Difference

This part we only expand on *Query Statements* methods of different DAOs.

- [CastDao](CastDao.java):
  - `public List<Cast> SelectByMovieID(Integer movie_id)`: Given movie select its all cast.
- [CrewDao](CrewDao.java):
  - `public List<Crew> SelectByMovieID(Integer movie_id)`: Given movie select its all crew.
- [GenreDao](GenreDao.java):
  - `public List<Genre> SelectAll()`: Select all the genres.
  - `public List<Genre> SelectByMovieId(Integer movie_id)`: Given movie select its all genres.
- [MovieDao](MoviesDao.java):
  - `public List<Movie> TopPopular(Integer page)`: Select the top popular movies and return the `page`-th page.
  - `public List<Movie> TopLatest(Integer page)`: Select the top popular movies and return the `page`-th page.
  - `public List<Movie> getGenre(Integer genre_id, Integer page)`: Select all movies of the given genre and return the `page`-th page.
  - `public List<Movie> selectName(String name, Integer page)`: Select all movies which title containing the given name and return the `page`-th page.
  - `public List<Movie> selectByPersonID(Integer personId)`: Select all movies which involve the given person.
  - `public Movie selectID(Integer movieId)`: Given movie id select the movie.
- [PersonDao](PersonDao.java):
  - `public List<Person> selectName(String name, Integer page)`: Select all person whose name containing the given name and return the `page`-th page.
  - `public Person SelectById(Integer id)`: Given person id select the person.
