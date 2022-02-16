package cloudcode.helloworld.payload.response;

public class CovidDto {

  private String location;

  private String date;

  private String variant;

  private int numSequences;

  private double percSequences;

  private int numSequencesTotal;

  public CovidDto(String location, String date, String variant, int numSequences, double percSequences,
      int numSequencesTotal) {
    this.location = location;
    this.date = date;
    this.variant = variant;
    this.numSequences = numSequences;
    this.percSequences = percSequences;
    this.numSequencesTotal = numSequencesTotal;
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
