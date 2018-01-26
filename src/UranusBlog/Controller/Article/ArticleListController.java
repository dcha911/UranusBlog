package UranusBlog.Controller.Article;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;


/**
 * possible parameters:
 * all = true / false
 * amount = (int)
 * start = (int)
 */
public class ArticleListController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

//        int userID = Integer.parseInt(req.getParameter("userID"));
//        int start = Integer.parseInt(req.getParameter("start"));
//        int amount = Integer.parseInt(req.getParameter("amount"));
//        boolean usersArticlesOnly = (req.getParameter("own");

        boolean usersArticlesOnly = Boolean.parseBoolean(req.getParameter("own"));

        int userID = 2;
        int start = 0;
        int amount = 5;

        PrintWriter out = resp.getWriter();

        boolean articlesReturned = false;

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }


//        int aid = Integer.parseInt(req.getParameter("aid"));

        Properties dbProps = new Properties();
        dbProps.setProperty("url", "jdbc:mysql://db.sporadic.nz/xliu617");
        dbProps.setProperty("user", "xliu617");
        dbProps.setProperty("password", "123");
        try (Connection conn = DriverManager.getConnection(dbProps.getProperty("url"), dbProps.getProperty("user"), dbProps.getProperty("password"))) {

            if (usersArticlesOnly) {
                try (PreparedStatement stmt = conn.prepareStatement("call GetArticleListOwn (?,?,?)")) {
                    stmt.setInt(1, userID);
                    stmt.setInt(2, start);
                    stmt.setInt(3, amount);
                    System.out.println(stmt);
                    try (ResultSet r = stmt.executeQuery()) {

                        if (r == null) {
                            articlesReturned = false;
                        }
                        JSONArray jsonArray = constructJSON(r);
                        out.print(jsonArray);
                        articlesReturned = true;

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                try (PreparedStatement stmt = conn.prepareStatement("call GetArticleListAll(?,?,?)")) {
                    stmt.setInt(1, userID);
                    stmt.setInt(2, start);
                    stmt.setInt(3, amount);
                    try (ResultSet r = stmt.executeQuery()) {

                        if (r == null) {
                            articlesReturned = false;
                        }
                        JSONArray jsonArray = constructJSON(r);
                        out.print(jsonArray);
                        articlesReturned = true;

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        if (!articlesReturned) {
            out.println("<p> that didn't work </p>");
        }
    }

    private JSONArray constructJSON(ResultSet r) throws SQLException {
        JSONArray jsonArray = new JSONArray();

        while (r.next()) {
            JSONObject jsonSingle = new JSONObject();

            int article_id = r.getInt(1);
            String title = r.getString(3);
            String content = r.getString(4);
            String created_time = r.getString(5);
            String modified_time = r.getString(6);
            String post_time = r.getString(7);
            String isPrivate = r.getString(9);
            String authorName = r.getString(10);

            jsonSingle.put("article_id", article_id);
            jsonSingle.put("title", title);
            jsonSingle.put("content", content);
            jsonSingle.put("created_time", created_time);
            jsonSingle.put("modified_time", modified_time);
            jsonSingle.put("post_time", post_time);
            jsonSingle.put("is_private", isPrivate);
            jsonSingle.put("author_name", authorName);

            jsonArray.add(jsonSingle);
        }
        return jsonArray;
    }
}

