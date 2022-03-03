package cloudcode.helloworld.util;

import java.util.Iterator;

import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.TableResult;

import org.apache.commons.lang3.StringUtils;

public class DynamicQuery {

  public DynamicQuery() {
  }

  public String QueryBuilder(String tableName, String[] fields, String limit, String offset) {
    String selectClause = "SELECT ";

    if (fields != null) {
      for (int i = 0; i < fields.length; i++) {
        if (i != 0) {
          selectClause += ", ";
        }
        selectClause += fields[i];
      }
    } else {
      selectClause += "*";
    }

    selectClause += " FROM " + tableName;

    if (StringUtils.isNotBlank(limit)) {
      selectClause += " LIMIT " + limit;
    }
    if (StringUtils.isNotBlank(offset)) {
      selectClause += " OFFSET " + offset;
    }
    return selectClause;
  }

  public String resultToJson(TableResult results, String[] includedfields) {
    String json = "{\"data\": [";

    Iterator<FieldValueList> iterator = results.iterateAll().iterator();

    while (iterator.hasNext()) {
      FieldValueList row = iterator.next();

      json += "{";

      for (int i = 0; i < includedfields.length; i++) {
        if (i != 0) {
          json += ", ";
        }
        json += "\"" + includedfields[i] + "\"" + ":" + "\"" + row.get(includedfields[i]).getStringValue() + "\"";
      }

      if (iterator.hasNext()) {
        json += "}, ";
      } else {
        json += "}";
      }
    }

    json += "]}";

    return json;
  }
}
