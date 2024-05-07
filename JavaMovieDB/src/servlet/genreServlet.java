package servlet;

import dao.MoviesDao;
import entity.Movie;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/genre")
public class genreServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");
        int genre_id = Integer.valueOf(request.getParameter("genre_id"));
        Integer page = Integer.valueOf(request.getParameter("Page"));

        MoviesDao md = new MoviesDao();
        List<Movie> movies = md.getGenre(genre_id,page);

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovies = objectMapper.writeValueAsString(movies);
        response.getWriter().write(jsonMovies);
    }
}