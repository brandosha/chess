package datamodel.http;

public interface Request {
  public void validate() throws InvalidRequestException;
}
