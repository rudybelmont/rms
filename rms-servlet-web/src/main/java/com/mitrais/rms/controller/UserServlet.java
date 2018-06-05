package com.mitrais.rms.controller;

import com.mitrais.rms.dao.UserDao;
import com.mitrais.rms.dao.impl.UserDaoImpl;
import com.mitrais.rms.model.User;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/users/*")
public class UserServlet extends AbstractController {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String pathInfo = "";
		UserDao userDao = UserDaoImpl.getInstance();
		if ("/list".equalsIgnoreCase(req.getPathInfo())) {
			pathInfo = req.getPathInfo();
			List<User> users = userDao.findAll();
			req.setAttribute("users", users);
		} else if (req.getPathInfo().contains("/edit")) {
			pathInfo = "/form";
			Long userId = Long.parseLong(req.getParameter("userId"));
			User user = userDao.find(userId).get();
			req.setAttribute("user", user);
		} else if (req.getPathInfo().contains("/delete")) {
			Long userId = Long.parseLong(req.getParameter("userId"));
			User user = userDao.find(userId).get();
			boolean isSuccess = userDao.delete(user);
			List<User> users = userDao.findAll();
			req.setAttribute("users", users);
			req.setAttribute("isDeleteSuccess", isSuccess);
			resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/list");
			return;
		} else if ("/form".equalsIgnoreCase(req.getPathInfo())) {
			pathInfo = "/form";
		} else {
			resp.sendRedirect(req.getContextPath() + "/index.jsp");
			return;
		}

		String path = getTemplatePath(req.getServletPath() + pathInfo);
		System.out.println(path);
		RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
		requestDispatcher.forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		UserDao userDao = UserDaoImpl.getInstance();
		User user;
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		String userid = req.getParameter("userId");
		System.out.println("-------- " + userid + " --------");
		if (userid == null || userid.isEmpty()) {
			System.out.println("-------- Add --------");
			user = new User(null, username, password);
			userDao.save(user);
		} else {
			System.out.println("-------- Update --------");
			user = new User(Long.parseLong(userid), username, password);
			userDao.update(user);
		}
		String path = getTemplatePath(req.getServletPath() + "/list");
//		System.out.println("-------- POST " + path);
		// RequestDispatcher requestDispatcher = req.getRequestDispatcher(path);
		List<User> users = userDao.findAll();
		req.setAttribute("users", users);
		// requestDispatcher.forward(req, resp);
		resp.sendRedirect(req.getContextPath() + req.getServletPath() + "/list");		
	}

}