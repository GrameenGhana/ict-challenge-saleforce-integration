/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grameenfoundation.ictchallenge.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URISyntaxException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Joseph George Davis
 */
@WebServlet(name = "ConnectedAppREST", urlPatterns = {"/ConnectedAppREST"})
public class ConnectedAppREST extends HttpServlet {
   
    Logger log =  Logger.getLogger(ConnectedAppREST.class.getName());
    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    private static final long serialVersionUID = 1L;
    private static final String ACCESS_TOKEN = "ACCESS_TOKEN";
    private static final String INSTANCE_URL = "INSTANCE_URL";
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            
             JSONObject farmers;
            String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);

		String instanceUrl = (String) request.getSession().getAttribute(
				INSTANCE_URL);

		if (accessToken == null) {
			out.write("Error - no access token");
			return;
		}

                log.log(Level.INFO,"We have an access token: {0}" + "\n" + "Using instance {1}\n\n", new Object[]{accessToken, instanceUrl});
		//out.write("We have an access token: " + accessToken + "\n"
				//+ "Using instance " + instanceUrl + "\n\n");
                
                
                if(request.getParameter("action").equals("search"))
                {
                    farmers = getFarmerById(instanceUrl, accessToken,request.getParameter("farmer_id"));
                }
                else
                   farmers = showFarmers(instanceUrl, accessToken);
                
                
                
                if(null!=farmers)
                {
                 request.getSession().setAttribute("All_Farmer", farmers);
                }
                else
                {
                    log.info("Data could not be retrieved from salesforce");
                }
                
               
                
                response.sendRedirect(request.getContextPath() + "/farmer_details.jsp");

		
            
            
           
        }
        
        
        
        
    }
    
    	

    
    private JSONObject showFarmers(String instanceUrl, String accessToken) throws ServletException, IOException {
        CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet();

        //add key and value
        httpGet.addHeader("Authorization", "OAuth " + accessToken);

        try {

            URIBuilder builder = new URIBuilder(instanceUrl + "/services/data/v30.0/query");
            //builder.setParameter("q", "SELECT Name, Id from Account LIMIT 100");
            builder.setParameter("q", "SELECT Name__c,Date_Of_Birth__c,Land_size__c,Farmer_I_D__c,Picture__c from Farmer__c LIMIT 100");

            httpGet.setURI(builder.build());
            
            log.log(Level.INFO, "URl to salesforce {0}", builder.build().getPath());

            CloseableHttpResponse closeableresponse = httpclient.execute(httpGet);
            System.out.println("Response Status line :" + closeableresponse.getStatusLine());

            if (closeableresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the
                // results
                try {

                    // Do the needful with entity.  
                    HttpEntity entity = closeableresponse.getEntity();
                    InputStream rstream = entity.getContent();
                    JSONObject authResponse = new JSONObject(
                            new JSONTokener(rstream));
                    
                   

                  log.log(Level.INFO, "Query response: {0}", authResponse.toString(2));
                  
                  log.log(Level.INFO, "{0} record(s) returned\n\n", authResponse.getInt("totalSize"));
                  
                 
                    
                  return authResponse;
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                    throw new ServletException(e);
                }
            }
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } finally {
            httpclient.close();
        }
        
        return null;
    }
    
    private JSONObject getFarmerById(String instanceUrl, String accessToken,String id)throws ServletException, IOException
    {
         CloseableHttpClient httpclient = HttpClients.createDefault();

        HttpGet httpGet = new HttpGet();

        //add key and value
        httpGet.addHeader("Authorization", "OAuth " + accessToken);

        try {

            URIBuilder builder = new URIBuilder(instanceUrl + "/services/data/v30.0/query");
            //builder.setParameter("q", "SELECT Name, Id from Account LIMIT 100");
            builder.setParameter("q", "SELECT Name__c,Date_Of_Birth__c,Land_size__c,Farmer_I_D__c,Picture__c from Farmer__c "
                    +" WHERE Farmer_I_D__c=" + "'" + id + "'");

            httpGet.setURI(builder.build());

            CloseableHttpResponse closeableresponse = httpclient.execute(httpGet);
            System.out.println("Response Status line :" + closeableresponse.getStatusLine());

            if (closeableresponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				// Now lets use the standard java json classes to work with the
                // results
                try {

                    // Do the needful with entity.  
                    HttpEntity entity = closeableresponse.getEntity();
                    InputStream rstream = entity.getContent();
                    JSONObject authResponse = new JSONObject(
                            new JSONTokener(rstream));
                    
                   

                  log.log(Level.INFO, "Query response: {0}", authResponse.toString(2));
                  
                  log.log(Level.INFO, "{0} record(s) returned\n\n", authResponse.getInt("totalSize"));
                  
                 
                    
                  return authResponse;
                   
                } catch (JSONException e) {
                    e.printStackTrace();
                    
                }
            }
        } catch (URISyntaxException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (IOException ex) {
            Logger.getLogger(ConnectedAppREST.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            httpclient.close();
        }
        
        
        
        return null;
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
        processRequest(request, response);
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
        processRequest(request, response);
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
