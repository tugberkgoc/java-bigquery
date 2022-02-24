package cloudcode.helloworld.model;

import java.io.Serializable;

public class TestObject implements Serializable {

  private String data;
  private double elapsedTime;

  public String getData() {
    return data;
  }

  public double getElapsedTime() {
    return elapsedTime;
  }

  public void setElapsedTime(double elapsedTime) {
    this.elapsedTime = elapsedTime;
  }

  public void setData(String data) {
    this.data = data;
  }
}
