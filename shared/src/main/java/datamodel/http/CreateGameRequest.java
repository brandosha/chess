package datamodel.http;

public class CreateGameRequest implements Request {
  public String gameName;

  public CreateGameRequest(String gameName) {
    this.gameName = gameName;
  }
  
  @Override
  public void validate() throws InvalidRequestException {
    if (gameName == null) {
      throw new InvalidRequestException("gameName field is required");
    }
  }
}
