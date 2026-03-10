package servlets;

import config.Conexion;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@WebServlet(name = "LoginServlet", urlPatterns = {"/LoginServlet"})
public class LoginServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        // 1. Recibir datos del formulario HTML [cite: 22]
        String user = request.getParameter("txtUsuario");
        String pass = request.getParameter("txtPassword");
        
        try {
            // 2. Intentar conectar a MySQL 
            Connection cn = Conexion.conectar();
            
            // VALIDACIÓN ADICIONAL: Si el driver no carga o WampServer está apagado
            if (cn == null) {
                request.setAttribute("error", "Error de conexión: Verifique el Driver MySQL en la carpeta lib de Tomcat.");
                request.getRequestDispatcher("index.html").forward(request, response);
                return; // Detiene la ejecución para evitar el error 'null'
            }

            // 3. Validar credenciales contra la tabla de usuarios 
            String sql = "SELECT * FROM usuarios WHERE username = ? AND password = ?";
            PreparedStatement pst = cn.prepareStatement(sql);
            pst.setString(1, user);
            pst.setString(2, pass);
            
            ResultSet rs = pst.executeQuery();
            
            System.out.println("Intentando login con: " + user + " y " + pass);
            if (rs.next()) {
                // [EXITO] El sistema valida que las credenciales sean correctas 
                HttpSession session = request.getSession();
                session.setAttribute("id_usuario", rs.getInt("id_usuario"));
                session.setAttribute("nombre", rs.getString("nombre_completo"));
                session.setAttribute("rol", rs.getString("rol")); // Administrador o Empleado [cite: 15, 16]
                
                // El usuario ingresa a la opción marcaje [cite: 24]
                response.sendRedirect("marcaje.jsp");
            } else {
                // [FA01] El sistema muestra mensaje de error "Credenciales incorrectas" [cite: 40, 41]
                request.setAttribute("error", "Credenciales incorrectas");
                request.getRequestDispatcher("index.html").forward(request, response);
            }
        } catch (Exception e) {
            System.out.println("Error en LoginServlet: " + e.getMessage());
            e.printStackTrace();
        }
    }
}