package controller;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import model.Match;

public class UserFinder extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		sendResponse(req, resp, "POST requests only, please.", true);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		executeQueryAndRedirect(req, resp);
	}

	private void handleUpdate(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		int uesrId = (int) session.getAttribute("userId");
		try (Connection conn = getConnection()) {
			List<Match> matches = (List<Match>) session.getAttribute("records");
			// Use batch mode to insert data.
			conn.setAutoCommit(false);
			PreparedStatement stat = conn.prepareStatement(
					"INSERT INTO records(user_pick, agent_pick, result, user_id) VALUES(?, ?, ?, ?);");
			for (Match m : matches) {
				if (m.isNewMatch()) {
					stat.setInt(1, m.getUserInput());
					stat.setInt(2, m.getComputerInput());
					stat.setInt(3, m.getResult());
					stat.setInt(4, uesrId);
					stat.addBatch();
				}
			}
			stat.executeBatch();
			conn.commit();
			sendResponse(req, resp, "Records are stored.", true);
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void handleDelete(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		List<Match> matches = (List<Match>) session.getAttribute("records");
		if (matches != null && !matches.isEmpty()) {
			int userId = matches.get(0)
					.getUserId();
			String sql = "DELETE FROM records WHERE user_id = ?;";
			try (Connection conn = getConnection()) {
				PreparedStatement stat = conn.prepareStatement(sql);
				stat.setInt(1, userId);
				stat.executeUpdate();
			}
			catch (SQLException e) {
				e.printStackTrace();
				sendResponse(req, resp, "Records had not been deleted", false);
			}
		}
		session.setAttribute("records", Collections.emptyList());
		sendResponse(req, resp, "Records had been deleted", true);
	}

	private void handleLogin(HttpServletRequest req, HttpServletResponse resp) {
		HttpSession session = req.getSession();
		try (Connection conn = getConnection()) {
			String username = req.getParameter("username");
			if (username == null) {
				sendResponse(req, resp, "username can not be null.", false);
			}
			// check user
			if (userExist(req, username)) {
				String sql =
						"SELECT records.id, records.user_pick, records.agent_pick, records.result, records.user_id FROM records, users WHERE users.name = ? "
								+ " and users.id = records.user_id";
				PreparedStatement stat = conn.prepareStatement(sql);
				stat.setString(1, username);
				ResultSet ret = stat.executeQuery();
				List<Match> matches = new ArrayList<>();
				session.setAttribute("records", matches);
				if (ret.isBeforeFirst()) { // Find records for the user.
					while (ret.next()) {
						int id = ret.getInt(1);
						int userInput = ret.getInt(2);
						int computerInput = ret.getInt(3);
						int result = ret.getInt(4);
						int userId = ret.getInt(5);
						Match match = new Match(id, userInput, computerInput, result, userId);
						matches.add(match);
					}
				}
			}
			// create a user.
			else {
				PreparedStatement stat = conn.prepareStatement("INSERT INTO Users(name) VALUES(?);");
				stat.setString(1, username);
				stat.execute(); 
				// Get the generate key from db.
				try (ResultSet ret = stat.getGeneratedKeys();) {
					if (ret.next()) {
						int userId = ret.getInt(1);
						session.setAttribute("userId", userId);
					}
				}
				catch (SQLException e) {
					e.printStackTrace();
				}
			}
			resp.sendRedirect("match.html");
		}
		catch (SQLException | IOException e) {
			e.printStackTrace();
		}
	}

	private boolean userExist(HttpServletRequest req, String username) {
		boolean flag = false;
		String sql = "SELECT id FROM users WHERE name ='" + username + "'";
		try (Connection conn = getConnection();
				PreparedStatement stat = conn.prepareStatement(sql);
				ResultSet ret = stat.executeQuery();) {
			flag = ret.isBeforeFirst();
			if (ret.next()) {
				req.getSession().setAttribute("userId", ret.getInt(1));
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
		return flag;
	}

	private void executeQueryAndRedirect(HttpServletRequest req, HttpServletResponse resp) {
		String type = req.getParameter("type");
		if ("login".equals(type)) { // Match login user or create a new user.
			handleLogin(req, resp);
		}
		else if ("insert".equals(type)) { // Insert new records into db.
			handleUpdate(req, resp);
		}
		else if ("delete".equals(type)) { // Delete records from db.
			handleDelete(req, resp);
		}
	}

	/**
	 * Depending on flag, redirects to badResult.jsp or goodResult.jsp.
	 * 
	 * @param req
	 * @param resp
	 * @param msg
	 * @param flag
	 */
	public void sendResponse(HttpServletRequest req, HttpServletResponse resp, String msg, boolean flag) {
		req.getSession()
				.setAttribute("result", msg);
		String url = "";
		if (flag) {
			url = "goodResult.jsp";
		}
		else {
			url = "badResult.jsp";
		}
		try {
			resp.sendRedirect(url);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	public Connection getConnection() {
		String uri = "jdbc:postgresql://localhost/match";
		Properties props = setLoginForDB("wyb", "secret");
		Connection conn = null;
		try {
			Class.forName("org.postgresql.Driver");
			conn = DriverManager.getConnection(uri, props);
		}
		catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
		}
		return conn;
	}

	private Properties setLoginForDB(String username, String password) {
		Properties props = new Properties();
		props.setProperty("user", username);
		props.setProperty("password", password);
		return props;
	}
}
