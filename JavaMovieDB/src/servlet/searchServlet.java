package servlet;

import dao.MoviesDao;
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

@WebServlet("/search")
public class searchServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        String query = String.valueOf(request.getParameter("type"));
        String name = String.valueOf(request.getParameter("name"));
        Integer page = Integer.valueOf(request.getParameter("Page"));

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovies = "";

        if (query.equals("movie")){
            MoviesDao md = new MoviesDao();
            List<Movie> movies = md.selectName(name, page);
            jsonMovies = objectMapper.writeValueAsString(movies);
        } else if (query.equals("person")){
            PersonDao pd = new PersonDao();
            List<Person> personList = pd.selectName(name, page);
            jsonMovies = objectMapper.writeValueAsString(personList);
        }

        response.getWriter().write(jsonMovies);
    }
}