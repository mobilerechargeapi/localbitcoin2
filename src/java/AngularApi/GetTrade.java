/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AngularApi;

import com.function.currencyconverter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.login.Util;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import org.json.JSONObject;

/**
 *
 * @author susheel
 */
public class GetTrade extends HttpServlet {

    private String id;
    private String username;
    private String location;
    private String type;
    private String currency;
    private String margin;
    private String track_liquidity;
    private String trusted_person_only;
    private String restrict_amount;
    private String min_transaction;
    private String price_equation;
    private String identified_person_only;
    private String payment_method;
    private String sms_verification;
    private String terms_of_trade;
    private String max_transcation;
    private String price;
    private String trade_type;
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
            throws ServletException, IOException, Exception {
        response.setContentType("application/json;charset=UTF-8");
        
        try (PrintWriter out = response.getWriter();
                Connection con = Util.getConnection();
            Statement st = con.createStatement();) {
            
            String query = "";
            HttpSession session = request.getSession();
            String roll = String.valueOf(session.getAttribute("roll"));
            String user = String.valueOf(session.getAttribute("username"));
            String cs = request.getParameter("cs");
            currencyconverter c = new currencyconverter();
            double brl = Double.parseDouble(c.curex());
            if (Integer.parseInt(roll) == 10) {
                query = "select * from trade_transaction order by trade_type DESC,id ASC";
            } else {
                query = "select * from trade_transaction where username='" + user + "' and type='" + cs + "' order by trade_type DESC,id ASC";
            }
            System.out.println(query);
            ResultSet rs = st.executeQuery(query);
            ArrayList<GetTrade> a = new ArrayList<>();
            while (rs.next()) {

                GetTrade a1 = new GetTrade();

               
                a1.id = rs.getString("id");
                a1.username = rs.getString("username");
                a1.trade_type = rs.getString("trade_type");
                a1.margin = rs.getString("margin");
                if (cs.equals("Buy_bitcoin") || cs.equals("Sell_bitcoin")) {
                    min_transaction = String.valueOf(brl * Double.parseDouble(rs.getString("min_transaction")));
                    a1.min_transaction = min_transaction;
                    max_transcation = String.valueOf(brl * Double.parseDouble(rs.getString("max_transcation")));
                    a1.max_transcation = max_transcation;
                    a1.price = String.valueOf(brl + (Double.parseDouble(rs.getString("margin")) * brl / 100));
                } else {
                    min_transaction = String.valueOf(rs.getString("min_transaction"));
                    a1.min_transaction = min_transaction;
                    max_transcation = String.valueOf(rs.getString("max_transcation"));
                    a1.max_transcation = max_transcation;
                    a1.price = String.valueOf(rs.getString("margin"));
                }
                if (cs.equals("Buy_bitcoin")) {
                    a1.type = "Sell_bitcoin";
                } else if (cs.equals("Buy_imobicash")) {
                    a1.type = "Sell_imobicash";
                } else if (cs.equals("Sell_imobicash")) {
                    a1.type = "Buy_imobicash";
                } else {
                    a1.type = "Buy_bitcoin";
                }
                a.add(a1);

            }
            Gson gson = new GsonBuilder().create();
            String jsonArray = gson.toJson(a);
            out.write(jsonArray);

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
            Logger.getLogger(GetTrade.class.getName()).log(Level.SEVERE, null, ex);
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
            Logger.getLogger(GetTrade.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    protected void doPut(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("application/json;charset=UTF-8");
        PrintWriter out = response.getWriter();
        Connection con = null;
        Statement st = null;

        try {

            BufferedReader br = new BufferedReader(new InputStreamReader(request.getInputStream()));
            String json = "";
            if (br != null) {
                json = br.readLine();
            }
            System.out.println(json);
            JSONObject jsonObj = new JSONObject(json);

            String id = String.valueOf(jsonObj.getString("id"));
            String trade_type = String.valueOf(jsonObj.getString("trade_type"));
            String query = "";
            con = Util.getConnection();
            st = con.createStatement();
            HttpSession session = request.getSession();
            String roll = String.valueOf(session.getAttribute("roll"));

            if (Integer.parseInt(roll) == 10) {
                query = "update trade_transaction set trade_type='" + trade_type + "' where id='" + id + "'";
                int i = st.executeUpdate(query);
                System.out.println(query);
                if (i > 0) {
                    out.println("{\"Error\":false,\"Message\":\"Success\"}");
                }
            }
        } catch (Exception e) {
            out.println("{\"Error\":true,\"Message\":" + e.getMessage() + "}");
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

}