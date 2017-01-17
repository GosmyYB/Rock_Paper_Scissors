package controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Match;


public class MatchServlet extends HttpServlet{

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException, IOException {
		int userInput = Integer.valueOf(req.getParameter("pick"));
		Match match = new Match(userInput, Match.NEW_ID);
		// store to session
		HttpSession session = req.getSession();
		session.setAttribute("currentMatch", match);
		Object o = session.getAttribute("records");
		List<Match> list = null;
		if (o != null) {
			list = (List<Match>) o;
		} else {
			list = new ArrayList<Match>();
			session.setAttribute("records", list);
		}
		list.add(match);
		//req.getRequestDispatcher("result.jsp").forward(req, res);
		res.sendRedirect("result.jsp");
	}
}
