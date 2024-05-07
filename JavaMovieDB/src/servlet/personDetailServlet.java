package servlet;

import dao.CastDao;
import dao.MoviesDao;
import entity.Cast;
import entity.Movie;
import dao.PersonDao;
import entity.Person;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/personDetail")
public class personDetailServlet extends HttpServlet {

    static class PersonWithMovies{
        public Person person;
        public List<Movie> movieList;

        public PersonWithMovies(Person person, List<Movie> movieList) {
            this.person = person;
            this.movieList = movieList;
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
        Integer person_id = Integer.valueOf(request.getParameter("person_id"));

        PersonDao pd = new PersonDao();
        Person person = pd.SelectById(person_id);
        MoviesDao md = new MoviesDao();
        List<Movie> movieList = md.selectByPersonID(person_id);
        PersonWithMovies pwm = new PersonWithMovies(person, movieList);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovies = objectMapper.writeValueAsString(pwm);
        response.getWriter().write(jsonMovies);
    }
}