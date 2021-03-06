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
	Phone phone = new Phone();
	String errorMessage;

	if (request.getAttribute(JSP_PARAMETERS) != null)
	{
		jspParameters = (HashMap<String,String>)request.getAttribute(JSP_PARAMETERS);
	}

	if (request.getAttribute("phone") != null)
	{
		phone = (Phone)request.getAttribute("phone");
	}

    Person person = (Person)request.getAttribute("person");
	
	errorMessage = jspParameters.get(ERROR_MESSAGE);
%>

<form action="<%=request.getContextPath()%>/" method="post">
<input type="hidden" name="id" value="<%=phone.getId()%>"/>
<input type="hidden" name="ownerID" value="<%=person.getId()%>"/>
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
        <td colspan="2" align="center">Информация о телефоне владельца: <%=person.getSurname()%> <%=person.getName()%> <%=person.getMiddlename()%></td>
    </tr>
    <tr>
        <td>Телефон:</td>
        <td><label>
            <input type="text" name="number" value="<%
                     out.write(phone.getNumber());
             %>"/>
        </label>
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