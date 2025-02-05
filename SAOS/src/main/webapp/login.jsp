<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login</title>
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
        .login-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
            width: 360px;
        }
        h2 {
            color: #4A3F9E;
            text-align: center;
            margin-bottom: 25px;
            font-size: 28px;
        }
        label {
            font-weight: 600;
            color: #333;
        }
        input[type="text"], input[type="password"] {
            padding: 12px;
            margin-bottom: 20px;
            width: 100%;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 16px;
            transition: border 0.3s ease;
        }
        input[type="text"]:focus, input[type="password"]:focus {
            border-color: #4A3F9E;
            outline: none;
        }
        input[type="submit"] {
            background-color: #4A3F9E;
            color: white;
            padding: 12px;
            border: none;
            border-radius: 6px;
            cursor: pointer;
            width: 100%;
            font-size: 18px;
            transition: background-color 0.3s ease;
        }
        input[type="submit"]:hover {
            background-color: #6A57D7;
        }
        .register-link {
            text-align: center;
            margin-top: 15px;
        }
        .register-link a {
            color: #4A3F9E;
            text-decoration: none;
            font-weight: 600;
        }
        .register-link a:hover {
            text-decoration: underline;
        }
        .remember-me {
            font-size: 14px;
            margin-bottom: 30px; /* Aggiunto margine inferiore per separare dal pulsante Accedi */
        }
        .remember-me input {
            margin-right: 5px;
        }
        .error-message {
            color: red;
            text-align: center;
            margin-top: 15px;
        }
        /* Aggiunto margine inferiore per separare username e password */
        input[type="text"] {
            margin-bottom: 10px;
        }
    </style>
    <script>
        function validateLoginForm() {
            var email = document.getElementById('email').value;
            var password = document.getElementById('pass').value;
            var errorMessage = document.getElementById('error-message');

            errorMessage.innerHTML = '';

            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            if (!emailPattern.test(email)) {
                errorMessage.innerHTML = 'Per favore, inserisci un indirizzo email valido.';
                return false;
            }

            if (password.length < 8) {
                errorMessage.innerHTML = 'La password deve contenere almeno 8 caratteri.';
                return false;
            }

            return true;
        }
    </script>
</head>
<body>
    <div class="login-container">
        <h2>Login</h2>
        <form method="POST" action="login" onsubmit="return validateLoginForm()">
            <label for="email">Email</label>
            <input type="text" id="email" name="email" required>

            <label for="pass">Password</label>
            <input type="password" id="pass" name="pass" required>
		
            <div class="remember-me">
                <input type="checkbox" id="rememberme" name="rememberme" value="true">
                <label for="remember">Ricordami</label>
            </div>

            <input type="submit" name="login" value="Accedi">
        </form>

        <div class="error-message" id="error-message"></div>

        <div class="register-link">
            <p>Non hai un account? <p><a href="<%= org.apache.commons.text.StringEscapeUtils.escapeHtml4(request.getContextPath()) %>/register.jsp">
                Registrati qui
            </a></p>
        </div>
    </div>
</body>
</html>
