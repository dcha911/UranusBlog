package UranusBlog.Controller.Account;

import UranusBlog.DAO.AccountDAO;
import UranusBlog.DB.MySQLDatabase;
import UranusBlog.Model.Account;
import org.json.simple.JSONObject;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.*;
import java.util.Properties;

public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
//        PrintWriter out = resp.getWriter();
//        out.println("hello login");
        //   super.doGet(req,resp);
        // todo: use super.doGet if we don't need to use get method to test
        doPost(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        //super.doPost(req, resp);
        //Testing-> switch back
        String userName = req.getParameter("Username");
        //String userName= "user7";
        String password = req.getParameter("Password");
        //String password= "password7";

        PrintWriter out = resp.getWriter();
        boolean authorized = false;

        HttpSession session = req.getSession();

        try(AccountDAO dao = new AccountDAO(new MySQLDatabase(getServletContext()))){
            Account account = dao.login(userName, password);
            if(account == null){
                // login failed
                req.getSession().setAttribute("is_logged", false);
                out.print("{\"result\":\"fail\"}");
                System.out.println("login failed");

            } else {
                //login success
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("result", "success");
                jsonObject.put("username", userName);
                session.setAttribute("is_logged", true);
                session.setAttribute("username", userName);
                session.setAttribute("firstname", account.getFirstname());
                session.setAttribute("lastname", account.getLastname());
                session.setAttribute("middlename", account.getMiddlename());
                session.setAttribute("email", account.getEmail());
                session.setAttribute("userID", account.getUserId());
                session.setAttribute("avatarPath", account.getAvatarPath());
                session.setAttribute("avatarThumbnailPath", account.getAvatarThumbnailPath());
                session.setAttribute("roleID", account.getRoleId());
                session.setAttribute("roleDetail", account.getRoleDetail());
                session.setAttribute("nation", account.getNation());
                session.setAttribute("nationDetail", account.getNationFullname());

                System.out.println(jsonObject);
                out.println(jsonObject);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}