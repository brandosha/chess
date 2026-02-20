package datamodel.http;

public class LoginRequest implements Request {
  public String username;
  public String password;

  public LoginRequest() {}

  public LoginRequest(String username, String password) {
    this.username = username;
    this.password = password;
  }

  @Override
  public void validate() throws InvalidRequestException {
    if (username == null) {
      throw new InvalidRequestException("username is required");
    } else if (password == null) {
      throw new InvalidRequestException("password is required");
    }
  }
}
