package ru.otus.java.pro.webserver;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/div")
public class DivideServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        double a = Double.parseDouble(req.getParameter("a"));
        double b = Double.parseDouble(req.getParameter("b"));
        if (b == 0) {
            resp.getWriter().write("Error: Division by zero");
        } else {
            double result = a / b;
            resp.getWriter().write("Result: " + result);
        }
    }
}