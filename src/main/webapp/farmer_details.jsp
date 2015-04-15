<%-- 
    Document   : farmer_details
    Created on : Apr 7, 2015, 11:29:52 AM
    Author     : grameen
--%>

<%@page import="org.json.JSONArray"%>
<%@page import="org.json.JSONObject"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<% 
     JSONObject farmerJSON = (JSONObject)request.getSession().getAttribute("All_Farmer");
     
     //out.print(farmerJSON.toString(2));
      JSONArray records = farmerJSON.getJSONArray("records");
      
      

%>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Farmer Details</title>
        <link href="<%=request.getContextPath()%>/css/bootstrap.min.css" rel="stylesheet">
    </head>
    <body>
        
        <div class="row">
           
            <div class="col-md-8" style="margin: 100px" >
                <div class="panel panel-primary">
                    <!-- Default panel contents -->
                    <div class="panel-heading"><p>Farmer Details</p>
                        <form class="form-horizontal" role="search" action="<%=request.getContextPath()%>/ConnectedAppREST" method="POST">
                            <div class="col-md-4 form-group">
                                <input type="text" name="farmer_id"   class="form-control" placeholder="Enter Farmer Id">  
                            </div>
                            <button type="submit" class="btn btn-info">Search</button>
                            <input type="hidden" name="action" value="search">
                        </form>
                    </div>
                    <div class="panel-body">
                       
                    </div>

                      <!-- Table -->
                    <table class="table table-striped">
                        <thead>
                            <tr>
                                <th>Farmer ID</th>
                                <th>Farmer Name</th>
                                <th>Date of Birth</th>
                                <th>Land Size</th>
                            </tr>
                        </thead>
                        <tbody>
                            <%
                            if(records.length() ==0)
                            {
                                out.print("<tr>records not available for this Farmer id</tr>");
                            }
                            else
                               for (int i = 0; i < records.length(); i++) {
                            %>
                            <tr>
                                <td><%= records.getJSONObject(i).getString("Farmer_I_D__c") %></td>
                                <td><%= records.getJSONObject(i).getString("Name__c") %></td>
                                <td><%= records.getJSONObject(i).getString("Date_Of_Birth__c") %></td>
                                <td><%= records.getJSONObject(i).getString("Land_size__c") %></td>
                            </tr>
                            <% }%>
                        </tbody>
                    </table>  
                </div>      

            </div>
        </div>

        <script src="//code.jquery.com/jquery-1.11.2.min.js"></script>
        <script src="<%=request.getContextPath()%>/js/bootstrap.min.js"></script>
       
    </body> 
</html>
