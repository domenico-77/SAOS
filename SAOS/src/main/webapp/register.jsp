<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione</title>
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
        .register-container {
            background-color: #ffffff;
            padding: 40px;
            border-radius: 12px;
            box-shadow: 0 8px 16px rgba(0, 0, 0, 0.2);
            width: 380px;
        }
        h2 {
            color: #4A3F9E;
            text-align: center;
            margin-bottom: 25px;
            font-size: 28px;
        }
        form {
            display: flex;
            flex-direction: column;
        }
        label {
            margin-bottom: 5px;
            font-weight: 600;
            color: #333;
        }
        input[type="email"], input[type="password"], input[type="file"], input[type="text"] {
            padding: 12px;
            margin-bottom: 20px;
            border: 1px solid #ddd;
            border-radius: 6px;
            font-size: 16px;
            transition: border 0.3s ease;
        }
        input[type="email"]:focus, input[type="password"]:focus, input[type="file"]:focus, input[type="text"]:focus {
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
            font-size: 18px;
            transition: background-color 0.3s ease;
        }
        input[type="submit"]:hover {
            background-color: #6A57D7;
        }
        .error-message {
            color: red;
            text-align: center;
            margin-top: 15px;
        }
    </style>
    <script>
        function validateForm() {
            var nickname = document.getElementById('nickname').value;
            var email = document.getElementById('email').value;
            var password = document.getElementById('password').value;
            var confirmPassword = document.getElementById('confirmPassword').value;
            var fileInput = document.getElementById('profileImage');
            var file = fileInput.files[0];
            var errorMessage = document.getElementById('error-message');
            
            errorMessage.innerHTML = '';

            // Controllo nickname: solo alfanumerico
            var nicknamePattern = /^[a-zA-Z0-9._-]{3,20}$/;
            if (!nicknamePattern.test(nickname)) {
                errorMessage.innerHTML = 'Il nickname deve contenere solo lettere e numeri.';
                return false;
            }

            var emailPattern = /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/;
            if (!emailPattern.test(email)) {
                errorMessage.innerHTML = 'Per favore, inserisci un indirizzo email valido.';
                return false;
            }

            var passwordPattern = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$/;
            if (!passwordPattern.test(password)) {
                errorMessage.innerHTML = 'La password deve contenere almeno 8 caratteri, una lettera maiuscola, una lettera minuscola, un numero e un carattere speciale.';
                return false;
            }

            if (password !== confirmPassword) {
                errorMessage.innerHTML = 'Le password non corrispondono.';
                return false;
            }

            if (file && !['image/png', 'image/jpeg', 'image/jpg'].includes(file.type)) {
                errorMessage.innerHTML = 'Il formato dell\'immagine non è valido. Usa un formato .png, .jpeg o .jpg.';
                return false;
            }
            
            var maxSize = 16 * 1024 * 1024;
            if (file && file.size > maxSize) {
                errorMessage.innerHTML = 'Il file caricato è troppo grande. La dimensione massima è 16 MB.';
                return false;
            }
            
            return true;
        }
    </script>
</head>
<body>
    <div class="register-container">
        <h2>Registrazione</h2>
        <form method="POST" action="register" enctype="multipart/form-data" onsubmit="return validateForm()">
            <label for="nickname">Nickname</label>
            <input type="text" id="nickname" name="nickname" required>

            <label for="email">Email</label>
            <input type="email" id="email" name="email" required>

            <label for="password">Password</label>
            <input type="password" id="password" name="password" required>

            <label for="confirmPassword">Conferma Password</label>
            <input type="password" id="confirmPassword" name="confirmPassword" required>

            <label for="profileImage">Immagine di Profilo (PNG, JPG, JPEG) MAX 16 MB</label>
            <input type="file" id="profileImage" name="profileImage" accept=".png, .jpg, .jpeg" required>

            <input type="submit" value="Registrati">
        </form>
        
        <div id="error-message" class="error-message"></div>
    </div>
</body>
</html>