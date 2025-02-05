<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Visualizza Proposta</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
        }
        .container {
            background-color: #ffffff;
            padding: 30px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.1);
            width: 80%;
            max-width: 900px;
            text-align: center;
        }
        h1 {
            color: #4C3BCF;
            font-size: 30px;
            margin-bottom: 20px;
        }
        .proposal-name {
            font-size: 24px;
            font-weight: bold;
            color: #333;
            margin-bottom: 20px;
        }
        pre {
            background-color: #f4f4f4;
            padding: 20px;
            border-radius: 8px;
            border: 1px solid #ddd;
            font-size: 16px;
            max-width: 100%;
            white-space: pre-wrap;
            word-wrap: break-word;
            text-align: left;
            box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
            margin-top: 20px;
            overflow: auto;
            line-height: 1.6;
            max-height: 60vh;
        }
        .content-container {
            margin-top: 40px;
        }
        .error-message {
            color: red;
            margin-top: 20px;
            font-weight: bold;
        }
        footer {
            position: absolute;
            bottom: 20px;
            left: 50%;
            transform: translateX(-50%);
            font-size: 12px;
            color: #4C3BCF;
        }
        footer a {
            color: #4C3BCF;
            text-decoration: none;
        }
        footer a:hover {
            text-decoration: underline;
        }
    </style>
</head>
<body>
    <div class="container">
        <% 
            String proposalName = (String) request.getAttribute("proposalName");
            if (proposalName != null && !proposalName.isEmpty()) {
                proposalName = StringEscapeUtils.escapeHtml4(proposalName);
        %>
            <div class="proposal-name">
                <h1><%= proposalName %></h1>
            </div>
        <% } %>

        <% 
            String content = (String) request.getAttribute("content");
            if (content != null && !content.isEmpty()) {
                content = StringEscapeUtils.escapeHtml4(content); 
        %>
            <div class="content-container">
                <pre><%= content %></pre>
            </div>
        <% } else { %>
            <div class="error-message">
                <p>Contenuto non disponibile o errore nel recupero.</p>
            </div>
        <% } %>
    </div>
</body>
</html>