package datamodel.http;

public class JoinGameRequest implements Request {
  public String playerColor;
  public Integer gameID;

  public JoinGameRequest(String playerColor, int gameID) {
    this.playerColor = playerColor;
    this.gameID = gameID;
  }

  @Override
  public void validate() throws InvalidRequestException {
    if (playerColor == null) {
      throw new InvalidRequestException("playerColor field is required");
    } else if (gameID == null) {
      throw new InvalidRequestException("gameID field is required");
    } else if (!playerColor.equals("BLACK") && !playerColor.equals("WHITE")) {
      throw new InvalidRequestException("playerColor must be \"BLACK\" or \"WHITE\"");
    }
  }
}
