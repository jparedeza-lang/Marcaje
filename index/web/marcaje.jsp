<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <title>Control de Marcaje - UMG</title>
        <style>
            .container { text-align: center; margin-top: 50px; font-family: Arial; }
            .reloj { font-size: 40px; color: #2c3e50; font-weight: bold; margin-bottom: 20px; }
            .btn { padding: 10px 20px; font-size: 16px; margin: 5px; cursor: pointer; }
        </style>
    </head>
    <body>
        <div class="container">
            <h2>Bienvenido: <%= session.getAttribute("nombre") %></h2>
            
            <div id="reloj" class="reloj">00:00:00</div>
            
            <div class="opciones">
                <form action="MarcajeServlet" method="POST">
                    <button type="submit" name="accion" value="entrada" class="btn">Marcar Entrada</button>
                    <button type="submit" name="accion" value="descanso1" class="btn">Marcar Descanso 1</button>
                    <button type="submit" name="accion" value="descanso2" class="btn">Marcar Descanso 2</button>
                    <button type="submit" name="accion" value="salida" class="btn">Marcar Salida</button>
                </form>
                
                <br>
                <button onclick="location.href='info_marcaje.jsp'" class="btn">Información del Marcaje</button>
                <button onclick="location.href='index.html'" class="btn">Regresar</button>
            </div>
        </div>

        <script>
            function actualizarReloj() {
                const ahora = new Date();
                const h = String(ahora.getHours()).padStart(2, '0');
                const m = String(ahora.getMinutes()).padStart(2, '0');
                const s = String(ahora.getSeconds()).padStart(2, '0');
                document.getElementById('reloj').textContent = `${h}:${m}:${s}`;
            }
            setInterval(actualizarReloj, 1000);
            actualizarReloj();
        </script>
    </body>
</html>x