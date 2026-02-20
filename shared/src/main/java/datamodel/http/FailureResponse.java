package datamodel.http;

public class FailureResponse {
  public final String message;

  public FailureResponse(String message) {
    this.message = message;
  }

  public FailureResponse(Exception err) {
    this.message = "Error: " + err.getMessage();
  }
}