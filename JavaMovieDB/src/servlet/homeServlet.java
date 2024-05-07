package servlet;

import dao.MoviesDao;
import entity.Movie;
import exce.homeTypeException;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/home")
public class homeServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        MoviesDao md = new MoviesDao();
        String query = String.valueOf(request.getParameter("type"));
        Integer page = Integer.valueOf(request.getParameter("Page"));
        try {
            if (! (query.equals("popular") || query.equals("latest")) )
                throw new homeTypeException();
        } catch (homeTypeException hte) {
            hte.printStackTrace();
        }
        List<Movie> movies = new ArrayList<Movie>();
        if (query.equals("popular")){
            movies = md.TopPopular(page);
        } else if (query.equals("latest")) {
            movies = md.TopLatest(page);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonMovies = objectMapper.writeValueAsString(movies);
        response.getWriter().write(jsonMovies);
    }
}