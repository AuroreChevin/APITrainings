package fr.fms;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

public class JwtAuthorizationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String token = request.getHeader((SecurityConstants.HEADER_STRING));
        System.out.println(token);
        if(token != null && token.startsWith(SecurityConstants.TOKEN_PREFIX)){
            try{
                String jwtToken = token.substring(7);
                System.out.println(jwtToken);
                JWTVerifier jwtVerifier = JWT.require(Algorithm.HMAC256(SecurityConstants.SECRET)).build();
                System.out.println(SecurityConstants.SECRET);
                System.out.println(jwtVerifier);
                DecodedJWT decodedJWT = jwtVerifier.verify(jwtToken);
                System.out.println(decodedJWT);
                String username = decodedJWT.getSubject();
                System.out.println(username);
                String[]roles = decodedJWT.getClaim("roles").asArray(String.class);
                Collection<GrantedAuthority> authorities = new ArrayList<>();
                for (String role : roles) {authorities.add(new SimpleGrantedAuthority(role));System.out.println(role);}
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, null, authorities);
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                //response.addHeader("Access-Control-Allow-Origin","*");
            }
            catch(Exception e) {
                response.setHeader(SecurityConstants.ERROR_MSG, e.getMessage());
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                System.out.println("tu me saoules");
            }
        }
        filterChain.doFilter(request,response);
    }
}
