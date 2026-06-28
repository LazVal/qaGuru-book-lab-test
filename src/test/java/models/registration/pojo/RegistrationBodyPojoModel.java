package models.registration.pojo;

public class RegistrationBodyPojoModel {
    String username;
    String password;


    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = this.username;
    }
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "{\"username\": \"" + this.username + "\",\"password\": \"" + this.password + "\"}";
    }

}
