package io.helidon.examples.quickstart.se;

import java.util.List;

import io.helidon.security.providers.httpauth.UserStore.User;

public class MyUser implements User {
    private int tenant;
    private String login;
    private char[] password;
    private List<String> roles;

    public MyUser(int tenant, String login, char[] password, List<String> roles) {
        this.tenant = tenant;
        this.login = login;
        this.password = password;
        this.roles = roles;
    }

    public int tenant() {
        return tenant;
    }

    @Override
    public String login() {
        return login;
    }

    @Override
    public char[] password() {
        return password;
    }

    @Override
    public List<String> roles() {
        return roles;
    }

}

// public class MyUser {
//     String login;
//     char[] password;
//     List<String> roles;

//     public MyUser(String login, char[] password, List<String> roles) {
//         this.login = login;
//         this.password = password;
//         this.roles = roles;
//     }

//     /**
//      * @return the login
//      */
//     public String getLogin() {
//         return login;
//     }

//     /**
//      * @param login the login to set
//      */
//     public void setLogin(String login) {
//         this.login = login;
//     }

//     /**
//      * @return the password
//      */
//     public char[] getPassword() {
//         return password;
//     }

//     /**
//      * @param password the password to set
//      */
//     public void setPassword(char[] password) {
//         this.password = password;
//     }

//     /**
//      * @return the roles
//      */
//     public List<String> getRoles() {
//         return roles;
//     }

//     /**
//      * @param roles the roles to set
//      */
//     public void setRoles(List<String> roles) {
//         this.roles = roles;
//     }

    
// }