<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Phonebook"%>
<%@ page import="app.Person"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="app.Phone" %>
<%@ page import="static app.ManagePersonServlet.JSP_PARAMETERS" %>
<%@ page import="static app.ManagePersonServlet.CURRENT_ACTION_RESULT_LABEL" %>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<style>
    TABLE.a {
        border-collapse: collapse;
        padding: 5px; /* Поля вокруг содержимого таблицы */
        border: 1px solid black; /* Параметры рамки */
    }
    body {
        background-color: lightgray ;
    }
</style>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title>Список людей</title>
</head>
<body link="black" alink="black" vlink="black">

<%
	String userMessage;
	HashMap<String,String> jspParameters = new HashMap<>();
	Phonebook phonebook = (Phonebook)request.getAttribute("phonebook");
	
	if (request.getAttribute(JSP_PARAMETERS) != null)
	{
		jspParameters = (HashMap<String,String>)request.getAttribute(JSP_PARAMETERS);
	}
	
	userMessage = jspParameters.get(CURRENT_ACTION_RESULT_LABEL);
%>

<table align="center" border="1" width="90%" bgcolor="#bfc9dc" cellpadding="6" class="a">
    
    <%
    if ((userMessage != null)&&(!userMessage.equals("")))
    {
    %>
    <tr>
     	<td colspan="6" align="center"><%=userMessage%></td>
    </tr>
    <%
    }
    %>
    
    <tr>
     	<td colspan="6" align="center"><a href="<%=request.getContextPath()%>/?action=add">Добавить запись</a></td>
    </tr>
    <tr>
        <td align="center" style="font-weight: bold;">Фамилия</td>
        <td align="center" style="font-weight: bold;">Имя</td>
        <td align="center" style="font-weight: bold;">Отчество</td>
        <td align="center" style="font-weight: bold;">Телефон(ы)</td>
        <td align="center">&nbsp;</td>
        <td align="center">&nbsp;</td>
    </tr>
    
        <%
        for (Person person : phonebook.getContents().values()) {
            
        %>
          <tr>
           <td><%=person.getSurname()%></td>
           <td><%=person.getName()%></td>
           <td><%=person.getMiddlename()%></td>
           <td>
            <%
             for(Phone phone : person.getPhones().values())
              {
            %>
             <%=phone.getNumber()%><br />
            <%
              }
            %>
           </td>
           <td><a href="<%=request.getContextPath()%>/?action=edit&id=<%=person.getId()%>">Редактировать</a></td>
           <td><a href="<%=request.getContextPath()%>/?action=delete&id=<%=person.getId()%>">Удалить</a></td>
          </tr>
        <%
          }
        %>
    
 </table>

</body>
</html>