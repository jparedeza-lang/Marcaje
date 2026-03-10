<%@page import="java.sql.*"%>
<%@page import="config.Conexion"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Información del Marcaje - UMG</title>
    <style>
        table { width: 80%; margin: 20px auto; border-collapse: collapse; font-family: Arial; }
        th, td { border: 1px solid #ddd; padding: 12px; text-align: center; }
        th { background-color: #2c3e50; color: white; }
        .container { text-align: center; margin-top: 30px; }
        .btn { padding: 10px 20px; cursor: pointer; }
    </style>
</head>
<body>
    <div class="container">
        <h2>Historial de Marcajes</h2>
        <table>
            <thead>
                <tr>
                    <th>ID </th>
                    <th>Fecha </th>
                    <th>Entrada </th>
                    <th>Descanso 1 </th>
                    <th>Descanso 2 </th>
                    <th>Salida </th>
                    <th>Estado</th>
                </tr>
            </thead>
            <tbody>
                <%
                    Integer idUsuario = (Integer) session.getAttribute("id_usuario");
                    try (Connection cn = Conexion.conectar()) {
                        String sql = "SELECT * FROM marcajes WHERE id_usuario = ? ORDER BY fecha DESC";
                        PreparedStatement pst = cn.prepareStatement(sql);
                        pst.setInt(1, idUsuario);
                        ResultSet rs = pst.executeQuery();
                        
                        while(rs.next()) {
                %>
                <tr>
                    <td><%= rs.getInt("id_marcaje") %></td>
                    <td><%= rs.getDate("fecha") %></td>
                    <td><%= rs.getTime("hora_entrada") %></td>
                    <td><%= (rs.getTime("hora_descanso1") != null) ? rs.getTime("hora_descanso1") : "--:--" %></td>
                    <td><%= (rs.getTime("hora_descanso2") != null) ? rs.getTime("hora_descanso2") : "--:--" %></td>
                    <td><%= (rs.getTime("hora_salida") != null) ? rs.getTime("hora_salida") : "--:--" %></td>
                    <td><%= rs.getString("estado_entrada") %></td>
                </tr>
                <%
                        }
                    } catch (Exception e) {
                        out.print("Error al cargar datos: " + e.getMessage());
                    }
                %>
            </tbody>
        </table>
        <br>
        <button onclick="location.href='marcaje.jsp'" class="btn">Regresar</button>
    </div>
</body>
</html>