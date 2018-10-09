<%@ page language="java" contentType="text/html; charset=utf-8"
    pageEncoding="utf-8"%>
<%@ page import="com.app.*,java.util.*" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
<title>Insert title here</title>
</head>
<body>
<%
Stock stock = new Stock();
Calendar c = Calendar.getInstance();
c.set(2015, Calendar.DECEMBER,31);

Calendar e = Calendar.getInstance();
e.set(2017, Calendar.MARCH,31);

while(true){
	if(c.getTimeInMillis()<=e.getTimeInMillis()){
		String jidu = String.format("%tF", c.getTime());
		c.set(Calendar.DAY_OF_MONTH, 30);
		String savejidu = String.format("%tF", c.getTime());
		
		stock.getQuarterReport(jidu, savejidu);
		stock.getQuarterZhuli(jidu, savejidu);
		
		c.add(Calendar.MONTH, 3);
		c.set(Calendar.DAY_OF_MONTH, c.getActualMaximum(Calendar.DAY_OF_MONTH));
	}else{
		break;
	}
}
%>
</body>
</html>