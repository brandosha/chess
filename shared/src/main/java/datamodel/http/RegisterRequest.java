package datamodel.http;

import datamodel.UserData;

public class RegisterRequest {
  public String username;
  public String password;
  public String email;

  public RegisterRequest() {}

  public RegisterRequest(String username, String password, String email) {
    this.username = username;
    this.password = password;
    this.email = email;
  }

  public UserData userData() {
    return new UserData(username, password, email);
  }
}
