package servlet;

import dao.GenreDao;
import entity.Genre;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/header")
public class headerServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        request.setCharacterEncoding("utf-8");
        response.setContentType("text/html;charset=utf-8");

        ObjectMapper objectMapper = new ObjectMapper();
        String jsonGenres = "";

        List<Genre> genreList = new ArrayList<Genre>();
        GenreDao gd = new GenreDao();
        genreList = gd.SelectAll();

        jsonGenres = objectMapper.writeValueAsString(genreList);

        response.getWriter().write(jsonGenres);
    }
}