package ru.study.crud.servlet;

import ru.study.crud.dao.Dao;
import ru.study.crud.dao.impl.UserDaoImpl;
import ru.study.crud.model.Users;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet("/")
public class UsersServlet extends HttpServlet {

    private static final Dao<Users, String> USER_DAO = new UserDaoImpl();

    @Override
    public void init(ServletConfig config) throws ServletException {
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();
        switch (action) {
            case "/new":
                showNewForm(req, resp);
                break;
            case "/insert":
                insert(req, resp);
                break;
            case "/edit":
                editUser(req, resp);
                break;
            case "/update":
                showEditForm(req, resp);
                break;
            case "/delete":
                deleteUser(req, resp);
                break;
            default:
                listUsers(req, resp);
                break;
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doGet(req, resp);
    }


    private void listUsers(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/users.jsp");
        List<Users> listUsers = USER_DAO.findAll();
        req.setAttribute("listUsers", listUsers);
        rd.forward(req, resp);
    }

    private void showNewForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/insertPage.jsp");
        rd.forward(req, resp);
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        Optional<Users> usersOptional = USER_DAO.findById(id);
        RequestDispatcher rd = req.getRequestDispatcher("/WEB-INF/view/insertPage.jsp");
        usersOptional.ifPresent(users -> req.setAttribute("user", users));
        rd.forward(req, resp);
    }

    private void insert(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        int age = Integer.parseInt(req.getParameter("age"));
        Users createdUser = new Users();
        createdUser.setName(name);
        createdUser.setSurname(surname);
        createdUser.setAge(age);
        USER_DAO.save(createdUser);
        resp.sendRedirect("/users");
    }

    private void editUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        String name = req.getParameter("name");
        String surname = req.getParameter("surname");
        int age = Integer.parseInt(req.getParameter("age"));
        Users editUser = new Users(id, name, surname, age);
        USER_DAO.update(editUser);
        resp.sendRedirect("/users");
    }

    private void deleteUser(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        int id = Integer.parseInt(req.getParameter("id"));
        USER_DAO.deleteById(id);
        resp.sendRedirect("/users");
    }
}
