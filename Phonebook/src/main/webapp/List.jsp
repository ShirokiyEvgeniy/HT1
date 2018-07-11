<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Phonebook"%>
<%@ page import="app.Person"%>
<%@ page import="java.util.ArrayList"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="app.Phone" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml" xmlns:f="http://java.sun.com/jsf/core" xmlns:h="http://java.sun.com/jsf/html">

<style>
    TABLE.a {
        border-collapse: collapse;
        padding: 5px; /* Поля вокруг содержимого таблицы */
        border: 1px solid black; /* Параметры рамки */
    }
</style>
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
    <title>Список людей</title>
    <link rel="stylesheet" type="text/css">
</head>
<body link="black" alink="black" vlink="black" background="resources/background.jpg">

<%
    // Phonebook phonebook = Phonebook.getInstance();
	String user_message;
	HashMap<String,String> jsp_parameters = new HashMap<>();
	Phonebook phonebook = (Phonebook)request.getAttribute("phonebook");
	
	if (request.getAttribute("jsp_parameters") != null)
	{
		jsp_parameters = (HashMap<String,String>)request.getAttribute("jsp_parameters");
	}
	
	user_message = jsp_parameters.get("current_action_result_label");
%>

<table align="center" border="1" width="90%" bgcolor="#d3d3d3" cellpadding="6" class="a">
    
    <%
    if ((user_message != null)&&(!user_message.equals("")))
    {
    %>
    <tr>
     	<td colspan="6" align="center"><%=user_message%></td>
    </tr>
    <%
    }
    %>
    
    <tr>
     	<td colspan="6" align="center"><a href="<%=request.getContextPath()%>/?action=add">Добавить запись</a></td>
    </tr>
    <tr>
        <td align="center"><b>Фамилия</b></td>
        <td align="center"><b>Имя</b></td>
        <td align="center"><b>Отчество</b></td>
        <td align="center"><b>Телефон(ы)</b></td>
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