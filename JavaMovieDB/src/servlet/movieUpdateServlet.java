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
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebServlet("/movieUpdate")
public class movieUpdateServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        // 从请求中获取 JSON 字符串
        String jsonFromFrontend = request.getReader().lines().collect(Collectors.joining(System.lineSeparator()));

        // 将 JSON 字符串转换为 MovieWithCastDTO 对象
        ObjectMapper objectMapper = new ObjectMapper();
        Movie updatedMovie = objectMapper.readValue(jsonFromFrontend, Movie.class);

        MoviesDao md = new MoviesDao();
        md.Update(updatedMovie);

    }
}