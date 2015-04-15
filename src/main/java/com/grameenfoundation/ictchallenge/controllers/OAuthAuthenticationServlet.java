/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.grameenfoundation.ictchallenge.controllers;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

/**
 *
 * @author Joseph George Davis
 * @date 
 * @description-this Servlet handles OAuth 2.0 authentication of application with Salesforce.com
 */
@WebServlet(name = "oauth", urlPatterns = {"/oauth/*", "/oauth"}, initParams = {
    //Localhost Parameters
    @WebInitParam(name = "clientId", value = "3MVG9Rd3qC6oMalUF3kBJSM3.4hraZY6r8WQbPL2CW3.YnCOfi9YEh5FgfycUCjTuhlAdjfSjBdr5SeNKyOvq"),
    @WebInitParam(name = "clientSecret", value = "1213817300959953455"),
    @WebInitParam(name = "redirectUri", value = "http://localhost:8084/ICTChallenge/oauth/_callback"),
    
    //ICTChallenge Server Parameters
//    @WebInitParam(name = "clientId", value = "3MVG9Rd3qC6oMalUF3kBJSM3.4lH4ggGtDcGAhOTFAs0517EqQDMknYaES43XrSnYIdpC79hd6FtW8HbBe_A6"),
//    @WebInitParam(name = "clientSecret", value = "386878405410026822"),
//    @WebInitParam(name = "redirectUri", value = "https://104.236.220.225:8443/ICTChallenge/oauth/_callback"),
    @WebInitParam(name = "environment", value = "https://login.salesforce.com")
})


public class OAuthAuthenticationServlet extends HttpServlet {

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

    //Initialize WebInit Parameters
    private String clientId = null;
    private String clientSecret = null;
    private String redirectUri = null;
    private String environment = null;
    private String authUrl = null;
    private String tokenUrl = null;
    
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            
            //assign the necessary initparameters to variables
                clientId = this.getInitParameter("clientId");
		clientSecret = this.getInitParameter("clientSecret");
		redirectUri = this.getInitParameter("redirectUri");
		environment = this.getInitParameter("environment");

		try {
                    //build authentication url
			authUrl = environment
					+ "/services/oauth2/authorize?response_type=code&client_id="
					+ clientId + "&redirect_uri="
					+ URLEncoder.encode(redirectUri, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			throw new ServletException(e);
		}

                
                //build url to get access token
		tokenUrl = environment + "/services/oauth2/token";
                
                
                String accessToken = (String) request.getSession().getAttribute(
				ACCESS_TOKEN);
                
                //test if access token is null 
                if (accessToken == null) {
			String instanceUrl = null;

                        //get authorization for the first time
			if (request.getRequestURI().endsWith("oauth")) {

				// we need to send the user to authorize
				response.sendRedirect(authUrl);
				return;
			}//else call back url is activated
                        else {
				System.out.println("Auth successful - got callback");

				String code = request.getParameter("code");


				// Create an instance of HttpClient.
				CloseableHttpClient httpclient = HttpClients.createDefault();  

				try{
					// Create an instance of HttpPost.  
					HttpPost httpost = new HttpPost(tokenUrl);  

					// Adding all form parameters in a List of type NameValuePair  

					List<NameValuePair> nvps = new ArrayList<NameValuePair>();  
					nvps.add(new BasicNameValuePair("code", code));  
					nvps.add(new BasicNameValuePair("grant_type","authorization_code")); 
					nvps.add(new BasicNameValuePair("client_id", clientId)); 
					nvps.add(new BasicNameValuePair("client_secret", clientSecret)); 
					nvps.add(new BasicNameValuePair("redirect_uri", redirectUri)); 

					httpost.setEntity(new UrlEncodedFormEntity(nvps, Consts.UTF_8));


					// Execute the request.  
					CloseableHttpResponse closeableresponse = httpclient.execute(httpost);  
					System.out.println("Response Status line :" + closeableresponse.getStatusLine());  
					try {  
						// Do the needful with entity.  
						HttpEntity entity = closeableresponse.getEntity(); 
						InputStream rstream = entity.getContent();
                                                
                                                //get authentication response in JSON format
						JSONObject authResponse = new JSONObject(
								new JSONTokener(rstream));
						
                                                //get access token and instance url to push subsequent requests
						accessToken = authResponse.getString("access_token");
						instanceUrl = authResponse.getString("instance_url");
						
						
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} finally {  
						// Closing the response  
						closeableresponse.close();  
					}  
				} finally {  
					httpclient.close();  
				}  


			}
                        
                       // Set a session attribute so that other servlets can get the access token
			request.getSession().setAttribute(ACCESS_TOKEN, accessToken);

			// get the instance URL from the OAuth response into session
			request.getSession().setAttribute(INSTANCE_URL, instanceUrl);
                        
                        
                }
                
                response.sendRedirect(request.getContextPath() + "/ConnectedAppREST");
          
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
        return "this Servlet handles OAuth 2.0 authentication of application with Salesforce.com";
    }// </editor-fold>

}
