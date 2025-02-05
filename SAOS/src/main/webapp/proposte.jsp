<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="secureWebApp.Proposte" %>
<%@ page import="org.apache.commons.text.StringEscapeUtils" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta name="author" content="Roberto Capolongo">
    <meta charset="UTF-8">
    <title>Proposte-Caricate</title>
    <style>
        body {
            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            color: #333;
        }

        header {
            background-color: #402E7A;
            color: white;
            padding: 20px;
            text-align: center;
            border-bottom: 5px solid #3DC2EC;
            position: relative;
        }

        h2 {
            font-size: 28px;
            margin-bottom: 10px;
        }

        h3, h1 {
            font-size: 22px;
            color: #4C3BCF;
        }

        .logout-form {
            position: absolute;
            top: 20px;
            right: 20px;
        }

        .file-form {
            display: flex;
            flex-direction: column;
            align-items: flex-start;
            margin: 20px;
        }

        label {
            font-weight: bold;
            color: #4C3BCF;
        }

        input[type="file"] {
            padding: 12px;
            margin: 10px 0;
            border: 1px solid #4C3BCF;
            border-radius: 5px;
            width: 100%;
            background-color: #f1f1f1;
        }

        button {
            background-color: #4B70F5;
            color: white;
            border: none;
            padding: 12px 25px;
            border-radius: 5px;
            cursor: pointer;
            font-size: 16px;
            width: 100%;
            margin-top: 10px;
        }

        button:hover {
            background-color: #3DC2EC;
        }

        .table-container {
            display: flex;
            justify-content: center;
            margin-top: 30px;
            padding: 0 20px; /* Aggiunto spazio sui lati */
        }

        table {
            width: 80%;
            border-radius: 10px; /* Bordi arrotondati */
            border-collapse: collapse;
            box-shadow: 0px 4px 10px rgba(0, 0, 0, 0.1); /* Ombra leggera */
            overflow: hidden; /* Per evitare che i bordi arrotondati vengano coperti */
        }

        th, td {
            padding: 15px;
            text-align: left;
            border-bottom: 1px solid #ddd;
        }

        th {
            background-color: #4C3BCF;
            color: white;
        }

        tr:nth-child(even) {
            background-color: #f9f9f9; /* Colore alternato per le righe */
        }

        tr:hover {
            background-color: #f1f1f1;
        }

        a {
            color: #4B70F5;
            text-decoration: none;
            font-weight: bold;
        }

        a:hover {
            text-decoration: underline;
        }

        footer {
            text-align: center;
            margin-top: 40px;
            font-size: 14px;
            color: #402E7A;
        }

        .centered-title {
            display: flex;
            justify-content: center;
            width: 100%;
            margin-top: 20px;
        }
    </style>
</head>
<body>
    <header>
        <h2>Login effettuato con successo!</h2>
        <p>Benvenuto, <strong><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4((String) session.getAttribute("user")) %></strong>!</p>
        
        <!-- Pulsante per fare logout -->
        <form class="logout-form" action="logout" method="post">
            <button type="submit">Logout</button>
        </form>
    </header>

    <!-- Form di caricamento proposta -->
    <div class="file-form">
        <h3>Carica una nuova proposta</h3>
        <form action="proposte" method="post" enctype="multipart/form-data">
        	<input type="hidden" name="csrfToken" value="${csrfToken}">
            <label for="proposalFile">Seleziona un file .txt:</label>
            <input type="file" id="proposalFile" name="proposalFile" accept=".txt" required>
            <button type="submit">Carica</button>
        </form>
    </div>
    
    <!-- Titolo "Proposte Caricate" centrato -->
    <div class="centered-title">
        <h1>Proposte Caricate</h1>
    </div>

    <%
        // Recupera la lista di proposte dalla request (passata dal servlet)
        List<Proposte> proposals = (List<Proposte>) request.getAttribute("otherProposals");
        if (proposals != null && !proposals.isEmpty()) {
    %>
        <div class="table-container">
            <table>
                <thead>
                    <tr>
                        <th>Utente</th>
                        <th>Nome del file</th>
                        <th>Data di caricamento</th>
                        <th>Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <%
                        // Itera attraverso tutte le proposte
                        for (Proposte proposta : proposals) {
                            String username = proposta.getUsername();
                            String fileName = proposta.getFileName(); // Genera un nome di file generico
                            java.sql.Timestamp uploadDate = proposta.getUploadDate();
                            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    %>
                            <tr>
                                <td><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(username) %></td>
                                <td><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(fileName) %></td>
                                <td><%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(sdf.format(uploadDate)) %></td>
                                <td>
                                    <a href="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4("downloadProposal?id=" + proposta.getId()) %>" target="_blank">Scarica</a>
                                    &nbsp;|&nbsp;
                                    <a href="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4("viewProposal?id=" + proposta.getId()) %>" target="_blank">Visualizza</a>
                                </td>
                            </tr>
                    <%
                        }
                    %>
                </tbody>
            </table>
        </div>
    <%
        } else {
    %>
        <p>Non ci sono proposte caricate al momento.</p>
    <%
        }
    %>

    <footer>
        <p>© 2025 Domenico Vicenti - Tutti i diritti riservati.</p>
    </footer>
</body>
</html>