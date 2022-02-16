package cloudcode.helloworld.model;

import java.util.HashMap;
import java.util.Map;

public class Dataset {
  private Map<String, Object> labels;

  public Dataset() {
    labels = new HashMap<>();
  }

  public Dataset addLabel(String labelKey, String labelValue) {
    labels.put(labelKey, labelValue);
    return this;
  }

  public Dataset removeLabel(String labelKey) {
    labels.remove(labelKey);
    return this;
  }

  public Map<String, Object> getLabels() {
    return labels;
  }
}
