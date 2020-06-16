/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchControls;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Monica
 */
public class Auth extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String user= req.getParameter("username");
        String pass= req.getParameter("password");
        boolean isAuth=false;
        try {
           isAuth =authenticateJndi(user,pass);
        } catch (Exception ex) {
            Logger.getLogger(Auth.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(isAuth){
            System.out.println("Usuario Autenticado");
            req.getRequestDispatcher("success.jsp").forward(req, resp);
        }else{
            System.out.println("Invalido");
            req.getRequestDispatcher("error.jsp").forward(req, resp);
        }
        
        
    }
 public static boolean authenticateJndi(String username, String password) throws Exception{
    Properties props = new Properties();
    props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
    props.put(Context.PROVIDER_URL, "ldap://directorio-una.eastus.cloudapp.azure.com:389");
    props.put(Context.SECURITY_PRINCIPAL, "cn=admin,dc=eastus,dc=cloudapp,dc=azure,dc=com");//adminuser - User with special priviledge, dn user
    props.put(Context.SECURITY_CREDENTIALS, "admin");//dn user password


    InitialDirContext context = new InitialDirContext(props);

    SearchControls ctrls = new SearchControls();
    ctrls.setReturningAttributes(new String[] { "givenName", "sn","memberOf" });
    ctrls.setSearchScope(SearchControls.SUBTREE_SCOPE);

    NamingEnumeration<javax.naming.directory.SearchResult> answers = context.search("dc=eastus,dc=cloudapp,dc=azure,dc=com", "(uid=" + username + ")", ctrls);
    javax.naming.directory.SearchResult result = answers.nextElement();

    String user = result.getNameInNamespace();

    try {
        props = new Properties();
        props.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        props.put(Context.PROVIDER_URL, "ldap://directorio-una.eastus.cloudapp.azure.com:389");
        props.put(Context.SECURITY_PRINCIPAL, user);
        props.put(Context.SECURITY_CREDENTIALS, password);

   context = new InitialDirContext(props);
    } catch (Exception e) {
        return false;
    }
    return true;
}
    
   



}
