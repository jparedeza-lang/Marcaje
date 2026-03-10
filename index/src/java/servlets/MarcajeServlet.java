package servlets;

import config.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Date;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "MarcajeServlet", urlPatterns = {"/MarcajeServlet"})
public class MarcajeServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        HttpSession session = request.getSession();
        Integer idUsuario = (Integer) session.getAttribute("id_usuario");
        String accion = request.getParameter("accion");
        
        if (idUsuario == null) {
            response.sendRedirect("index.html");
            return;
        }

        java.time.LocalTime ahora = java.time.LocalTime.now();
        Time horaSql = Time.valueOf(ahora);
        Date fechaHoy = new Date(System.currentTimeMillis());

        String mensaje = "";

        try (Connection cn = Conexion.conectar()) {
            // [FA03, FA04, FA07] Verificar si ya existe un registro hoy 
            String sqlCheck = "SELECT * FROM marcajes WHERE id_usuario = ? AND fecha = ?";
            PreparedStatement psCheck = cn.prepareStatement(sqlCheck);
            psCheck.setInt(1, idUsuario);
            psCheck.setDate(2, fechaHoy);
            ResultSet rs = psCheck.executeQuery();
            boolean existeRegistro = rs.next();

            // --- Lógica de Marcaje de Entrada ---
            if ("entrada".equals(accion)) {
                if (existeRegistro) {
                    mensaje = "Ya has realizado el marcaje de entrada hoy.";
                } else {
                    // [RN01] Validar entrada tarde (8:00 AM) [cite: 83]
                    String estado = ahora.isAfter(java.time.LocalTime.of(8, 0)) ? "Tarde" : "Puntual";
                    String sql = "INSERT INTO marcajes (id_usuario, fecha, hora_entrada, estado_entrada) VALUES (?, ?, ?, ?)";
                    PreparedStatement pst = cn.prepareStatement(sql);
                    pst.setInt(1, idUsuario);
                    pst.setDate(2, fechaHoy);
                    pst.setTime(3, horaSql);
                    pst.setString(4, estado);
                    pst.executeUpdate();
                    mensaje = "Marcaje realizado con éxito (" + estado + ").";
                }
            } 
            // --- Lógica de Marcaje de Primer Descanso ---
            else if ("descanso1".equals(accion)) {
                if (!existeRegistro) {
                    // [FA05] Mensaje de alerta Primer descanso [cite: 56]
                    mensaje = "Debe marcar la entrada antes de registrar el descanso."; 
                } else {
                    // [FA03] Marcaje primer Descanso [cite: 46]
                    actualizarCampo(cn, "hora_descanso1", idUsuario, fechaHoy, horaSql);
                    mensaje = "Primer descanso registrado."; 
                }
            }
            // --- Lógica de Marcaje de Segundo Descanso ---
            else if ("descanso2".equals(accion)) {
                // [FA06] Mensaje de alerta segundo descanso [cite: 60]
                if (!existeRegistro || rs.getTime("hora_descanso1") == null) {
                    mensaje = "Debe marcar el primer descanso antes de registrar el segundo descanso."; 
                } else {
                    // [FA04] Marcaje segundo Descanso [cite: 50]
                    actualizarCampo(cn, "hora_descanso2", idUsuario, fechaHoy, horaSql);
                    mensaje = "Segundo descanso registrado."; 
                }
            }
            // --- Lógica de Marcaje de Salida ---
            else if ("salida".equals(accion)) {
                // [FA08, FA09] Alertas de Salida [cite: 68, 72]
                if (!existeRegistro || rs.getTime("hora_descanso1") == null) {
                    mensaje = "Debe marcar el primer descanso antes de registrar la salida."; 
                } else if (rs.getTime("hora_descanso2") == null) {
                    mensaje = "Debe marcar el segundo descanso antes de registrar la salida."; 
                } else {
                    // [FA07] Marcar salida [cite: 62]
                    actualizarCampo(cn, "hora_salida", idUsuario, fechaHoy, horaSql);
                    mensaje = "Marcaje de salida realizado."; 
                }
            }

        } catch (SQLException e) {
            mensaje = "Error: " + e.getMessage();
        }

        request.setAttribute("mensaje", mensaje);
        request.getRequestDispatcher("marcaje.jsp").forward(request, response);
    }

    // Método auxiliar para actualizar campos específicos [cite: 86]
    private void actualizarCampo(Connection cn, String campo, int idU, Date fecha, Time hora) throws SQLException {
        String sql = "UPDATE marcajes SET " + campo + " = ? WHERE id_usuario = ? AND fecha = ?";
        PreparedStatement pst = cn.prepareStatement(sql);
        pst.setTime(1, hora);
        pst.setInt(2, idU);
        pst.setDate(3, fecha);
        pst.executeUpdate();
    }
}