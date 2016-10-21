package bsr.bank.dao.message;

import bsr.bank.service.message.LoginRequest;
import bsr.bank.service.message.RegisterRequest;

public class UserMsg {

    private Integer id;
    private String name;
    private String surname;
    private String login;
    private String password;

    public UserMsg(){}

    public UserMsg(RegisterRequest request){
        this.name = request.getName();
        this.surname = request.getSurname();
        this.login = request.getLogin();
        this.password = request.getPassword();
    }

    public UserMsg(LoginRequest request){
        this.login = request.getLogin();
        this.password = request.getPassword();
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
