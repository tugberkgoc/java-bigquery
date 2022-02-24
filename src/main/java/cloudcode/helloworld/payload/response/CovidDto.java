package cloudcode.helloworld.payload.response;

public class CovidDto {

  // @JsonInclude(JsonInclude.Include.NON_NULL)
  private String location;

  private String date;

  private String variant;

  private int numSequences;

  private double percSequences;

  private int numSequencesTotal;

  public CovidDto() {
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getVariant() {
    return variant;
  }

  public void setVariant(String variant) {
    this.variant = variant;
  }

  public int getNum_sequences() {
    return numSequences;
  }

  public void setNum_sequences(int numSequences) {
    this.numSequences = numSequences;
  }

  public double getPerc_sequences() {
    return percSequences;
  }

  public void setPerc_sequences(double percSequences) {
    this.percSequences = percSequences;
  }

  public int getNum_sequences_total() {
    return numSequencesTotal;
  }

  public void setNum_sequences_total(int numSequencesTotal) {
    this.numSequencesTotal = numSequencesTotal;
  }

}
