package br.com.pedromartins.todolist.filter;

import at.favre.lib.crypto.bcrypt.BCrypt;
import br.com.pedromartins.todolist.user.IUserRepository;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Base64;

@Component
public class FilterTaskAuth extends OncePerRequestFilter {
    @Autowired
    private IUserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        var servletPath = request.getServletPath();

        if(servletPath.startsWith("/tasks/")){
            var authorization = request.getHeader("Authorization");

            var authEncoded = authorization.substring("Basic".length()).trim();
            byte [] authDecode = Base64.getDecoder().decode(authEncoded);
            var authString = new String(authDecode);

            System.out.println("Authorization: " + authString);

            String[] credentials = authString.split(":");
            String username = credentials[0];
            String password = credentials[1];

            var user = this.userRepository.findByUserName(username);
            if (user == null) {
                response.sendError(401);
            } else {
                // Validar senha
                var passwordVerify = BCrypt.verifyer().verify(password.toCharArray(), user.getPassword());
                if (passwordVerify.verified) {
                    // Segue viagem
                    request.setAttribute("idUser", user.getId());
                    filterChain.doFilter(request, response);
                } else {
                    response.sendError(401);
                }

            }
        } else {
            filterChain.doFilter(request, response);
        }

    }
}
