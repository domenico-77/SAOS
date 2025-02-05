<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Email già utilizzata</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #4A3F9E;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .message-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
            width: 380px;
            text-align: center;
        }
        h2 {
            color: #4A3F9E;
            margin-bottom: 25px;
            font-size: 28px;
        }
        p {
            color: #333;
            font-size: 16px;
            margin-bottom: 20px;
        }
        a {
            background-color: #4A3F9E;
            color: white;
            padding: 12px 25px;
            border-radius: 6px;
            text-decoration: none;
            font-size: 18px;
            display: inline-block;
            transition: background-color 0.3s ease;
        }
        a:hover {
            background-color: #6A57D7;
        }
    </style>
</head>
<body>

    <div class="message-container">
        <h2>Errore Registrazione</h2>
        <p>Alcuni campi non sono stati compilati correttamente</p>
         <a href="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(request.getContextPath()) %>/register.jsp">
                Clicca qui per tornare alla registrazione
            </a>
    </div>

</body>
</html>