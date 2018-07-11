<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Person"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="app.Phone" %>
<%@ page import="app.Phonebook" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Управление данными о человеке</title>
</head>
<body>

<%
	HashMap<String,String> jsp_parameters = new HashMap<>();
	Phone phone = new Phone();
	String error_message;

	if (request.getAttribute("jsp_parameters") != null)
	{
		jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
	}

	if (request.getAttribute("phone") != null)
	{
		phone = (Phone)request.getAttribute("phone");
	}

    Phonebook phonebook = Phonebook.getInstance();
    Person person = phonebook.getPerson(phone.getOwner());
	
	error_message = jsp_parameters.get("error_message");
%>

<form action="<%=request.getContextPath()%>/" method="post">
<input type="hidden" name="id" value="<%=phone.getId()%>"/>
<table align="center" border="1" width="70%">
    <%
    if ((error_message != null)&&(!error_message.equals("")))
    {
    %>
    <tr>
     	<td colspan="2" align="center"><span style="color:red"><%=error_message%></span></td>
    </tr>
    <%
    }
    %>
    <tr>
        <td colspan="2" align="center">Информация о телефоне владельца: <%=person.getSurname()%> <%=person.getName()%> <%=person.getMiddlename()%></td>
    </tr>
    <tr>
        <td>Телефон:</td>
        <td>
         <%out.write(phone.getNumber() + "\n");%><br />
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">
         <input type="submit" name="<%=jsp_parameters.get("next_action")%>" value="<%=jsp_parameters.get("next_action_label")%>" />
         <br /><a href="/">Вернуться к списку</a>
        </td>
    </tr> 
 </table>
 </form>
</body>
</html>