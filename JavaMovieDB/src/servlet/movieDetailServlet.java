package servlet;

import dao.*;
import entity.*;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/movieDetail")
public class movieDetailServlet extends HttpServlet {

    static class CastDetail {
        public Cast cast;
        public Person detail;
        public CastDetail(Cast c) {
            this.cast = c;
            this.detail = new PersonDao().SelectById(c.actor_id);
        }
    }
    static class CrewDetail {
        public Crew crew;
        public Person detail;
        public CrewDetail(Crew c) {
            this.crew = c;
            this.detail = new PersonDao().SelectById(c.crew_member_id);
        }
    }
    static class MovieWithPerson {
        public Movie movie;
        public List<Genre> genres = new ArrayList<>();
        public List<CastDetail> castDetailList = new ArrayList<>();
        public List<CrewDetail> crewDetailList = new ArrayList<>();

        public MovieWithPerson(Movie movie, List<Genre> genres, List<Cast> castList, List<Crew> crewList) {
            this.movie = movie;
            this.genres = genres;
            castList.forEach( cast -> castDetailList.add(new CastDetail(cast)) );
            crewList.forEach( crew -> crewDetailList.add(new CrewDetail(crew)) );
        }
    }
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        Integer movie_id = Integer.valueOf(request.getParameter("movie_id"));

        MovieWithPerson mwp = new MovieWithPerson(
                new MoviesDao().selectID(movie_id),
                new GenreDao().SelectByMovieId(movie_id),
                new CastDao().SelectByMovieID(movie_id),
                new CrewDao().SelectByMovieID(movie_id)
        );

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovie = objectMapper.writeValueAsString(mwp);
        response.getWriter().write(jsonMovie);
    }
}