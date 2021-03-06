/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AngularApi;

import com.function.Balance;
import com.function.currencyconverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.login.Util;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author susheel
 */
public class AllUser extends HttpServlet {

    private String bitcoinaddress;
    private String balance;
    private String coin;
    private String domain;
    private String BTC;
    private double USD;
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException, SQLException, Exception {
       response.setContentType("application/json;charset=UTF-8");
        try (PrintWriter out = response.getWriter();
                Connection con = Util.getConnection();
            Statement st = con.createStatement();) {
            ResultSet rs;
        String query = "";
          HttpSession session = request.getSession();
            String username = String.valueOf(session.getAttribute("username")).trim();
            if (username.equals("admin")) {
                // query = "SELECT * from register r , ( select  sum(cr) scr ,sum(dr) sdr,coin ,username from   transactions   GROUP by coin ,username ) t where t.username=r.username order by id desc";

                query = "SELECT r.id,r.username,r.name,r.email,r.mobile,r.status,r.email_verified,r.domain,t.balance ,t.coin,d.bitcoin from register r , ( select sum(cr)-sum(dr) as balance ,coin ,username from transactions GROUP by coin ,username ) t ,depositaddress d where t.username=r.username and d.username=r.username ";

                System.out.println(query);
                rs = st.executeQuery(query);
                String us;
                try {
                    us = request.getParameter("username");
                } catch (Exception e) {
                    us = "";
                }
                if (!us.equals("")) {
                    Profilebyusername(con, st, out, us);
                } else {
                    ArrayList<AllUser> a = new ArrayList<>();

                    while (rs.next()) {
                        AllUser a1 = new AllUser();
                        a1.id = rs.getString("id");
                        a1.username = rs.getString("username");
                        a1.email = rs.getString("email");
                        a1.name = rs.getString("name");
                        a1.mobile = rs.getString("mobile");
                        a1.password = "";// rs.getString("password");
                        a1.ac_number = rs.getString("name");
                        a1.status = rs.getString("status");
                        //a1.sms_verified = rs.getString("sms_verified");
                        a1.email_verified = rs.getString("email_verified");
                        a1.domain = rs.getString("domain");

                        a1.bitcoinaddress = rs.getString("bitcoin");
                        a1.coin = rs.getString("coin");
                        Balance b = new Balance();
                        String bitcoinbalance = b.getBalancecoin(a1.username, a1.coin, a1.domain);
                        Double btc = Double.parseDouble(bitcoinbalance);
                        a1.BTC = bitcoinbalance;
                        currencyconverter c = new currencyconverter();
                        String aa = c.cur();

                        a1.USD = Double.parseDouble(aa) * btc;
                        //a1.balance = rs.getString("balance");

                        a.add(a1);

                    }
                    Gson gson = new GsonBuilder().create();
                    String jsonArray = gson.toJson(a);
                    out.write(jsonArray);

                    con.close();
                }
        }
    }
    }
    
     public void Profilebyusername(Connection con, Statement st, PrintWriter out, String username) {
        try {
            String query = "select * from register where username='" + username + "'";
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);

            AllUser a1 = new AllUser();
            while (rs.next()) {

                a1.id = rs.getString("id");
                a1.username = rs.getString("username");
                a1.email = rs.getString("email");
                a1.name = rs.getString("name");
                a1.mobile = rs.getString("mobile");
                a1.password = "";// rs.getString("password");
                a1.ac_number = rs.getString("name");
                a1.status = rs.getString("status");
                a1.trusted = rs.getString("trusted");
                a1.sms_verified = rs.getString("sms_verified");
                a1.email_verified = rs.getString("email_verified");
                a1.date = rs.getString("date");

                a1.identified = rs.getString("identified");
                a1.identification_document = rs.getString("identification_document");

            }
            Gson gson = new GsonBuilder().create();
            String jsonArray = gson.toJson(a1);

            out.write(jsonArray);

        } catch (SQLException ex) {
            Logger.getLogger(AllUser.class.getName()).log(Level.SEVERE, null, ex);
        }
     }
    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(AllUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            processRequest(request, response);
        } catch (Exception ex) {
            Logger.getLogger(AllUser.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>
    public String id, username, name, mobile, password, ac_number, admin_status, email, status, trusted, sms_verified, identified, identification_document, email_verified, date;

}
