<?xml version="1.0" encoding="UTF-8" ?>
<%@ page import="app.Person"%>
<%@ page import="java.util.HashMap"%>
<%@ page import="app.Phone" %>
<%@ page import="static app.ManagePersonServlet.JSP_PARAMETERS" %>
<%@ page import="static app.ManagePersonServlet.ERROR_MESSAGE" %>
<%@ page import="static app.ManagePersonServlet.NEXT_ACTION" %>
<%@ page import="static app.ManagePersonServlet.*" %>
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
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>Управление данными о человеке</title>
</head>
<body link="black" alink="black" vlink="black">

<%
	HashMap<String,String> jspParameters = new HashMap<>();
	Person person = new Person();
	String errorMessage;

	if (request.getAttribute(JSP_PARAMETERS) != null)
	{
		jspParameters = (HashMap<String,String>)request.getAttribute(JSP_PARAMETERS);
	}

	if (request.getAttribute("person") != null)
	{
		person=(Person)request.getAttribute("person");
	}

	errorMessage = jspParameters.get(ERROR_MESSAGE);
%>

<form action="<%=request.getContextPath()%>/" method="post">
<input type="hidden" name="id" value="<%=person.getId()%>"/>
<table align="center" border="1" width="70%" bgcolor="#bfc9dc" cellpadding="6" class="a">
    <%
    if ((errorMessage != null)&&(!errorMessage.equals("")))
    {
    %>
    <tr>
     	<td colspan="2" align="center"><span style="color:red"><%=errorMessage%></span></td>
    </tr>
    <%
    }
    %>
    <tr>
        <td colspan="2" align="center">Информация о человеке</td>
    </tr>
    <tr>
        <td>Фамилия:</td>
        <td><label>
            <input type="text" name="surname" value="<%=person.getSurname()%>"/>
        </label></td>
    </tr>
    <tr>
        <td>Имя:</td>
		<td><label>
            <input type="text" name="name" value="<%=person.getName()%>"/>
        </label></td>
    </tr>
    <tr>
        <td>Отчество:</td>
        <td><label>
            <input type="text" name="middlename" value="<%=person.getMiddlename()%>"/>
        </label></td>
    </tr>
    <tr>
        <td>Телефоны:</td>
        <td>
         <%
             for(Phone phone : person.getPhones().values())
             {
                 out.write(phone.getNumber() + "\n");%>&nbsp;
            <a href="<%=request.getContextPath()%>/?action=editPhone&id=<%=phone.getId()%>&ownerID=<%=person.getId()%>">Редактировать</a>&nbsp;
            <a href="<%=request.getContextPath()%>/?action=deletePhone&id=<%=phone.getId()%>&ownerID=<%=person.getId()%>">Удалить</a>

            <br /> <%
             }
         %>
            <a href="<%=request.getContextPath()%>/?action=addPhone&ownerID=<%=person.getId()%>">Добавить</a>
        </td>
    </tr>
    <tr>
        <td colspan="2" align="center">
         <input type="submit" name="<%=jspParameters.get(NEXT_ACTION)%>" value="<%=jspParameters.get(NEXT_ACTION_LABEL)%>" />
         <br /><a href="${pageContext.request.contextPath}/">Вернуться к списку</a>
        </td>
    </tr> 
 </table>
 </form>
</body>
</html>