package cloudcode.helloworld.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.QueryJobConfiguration;
import com.google.cloud.bigquery.TableResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import cloudcode.helloworld.model.TestObject;
import cloudcode.helloworld.payload.response.CovidDto;
import cloudcode.helloworld.util.StopWatch;
import flexjson.JSONSerializer;

@RestController
public class QueryController {

  private static final Logger LOGGER = LoggerFactory.getLogger(QueryController.class);

  @Value("${spring.cloud.gcp.project-id}")
  private String projectId;

  @Value("${spring.cloud.gcp.bigquery.datasetName}")
  private String datasetName;

  @Value("${spring.cloud.gcp.bigquery.tableName}")
  private String tableName;

  @Autowired
  private BigQuery bigQuery;

  @GetMapping("/all-table")
  public ResponseEntity<?> getAllWithUsingTable(
      @RequestParam(required = true, value = "limit") String limit,
      @RequestParam(required = true, value = "offset") String offset,
      @RequestParam(required = true, value = "fields") String fields) {

    StopWatch stopWatch = new StopWatch();

    // TableId tableId = TableId.of(datasetName, tableName);
    TableResult results = bigQuery.listTableData(
        datasetName,
        tableName,
        BigQuery.TableDataListOption.pageSize(Integer.parseInt(limit)),
        BigQuery.TableDataListOption.startIndex(Integer.parseInt(offset)));

    List<Object> objs = new ArrayList<Object>();

    for (FieldValueList row : results.getValues()) {
      CovidDto covidDto = new CovidDto();
      covidDto.setLocation(row.get(0).getStringValue());
      covidDto.setDate(row.get(1).getStringValue());
      covidDto.setVariant(row.get(2).getStringValue());
      covidDto.setNum_sequences(row.get(3).getNumericValue().intValue());
      covidDto.setPerc_sequences(row.get(4).getDoubleValue());
      covidDto.setNum_sequences_total(row.get(5).getNumericValue().intValue());
      objs.add(covidDto);
    }

    JSONSerializer jsonSerializer = new JSONSerializer();
    List<String> includedfields = Arrays.asList(fields.split(",", -1));
    includedfields.forEach(x -> jsonSerializer.include(x));

    TestObject testObject = new TestObject();
    testObject.setData(jsonSerializer.exclude("*").serialize(objs));
    testObject.setElapsedTime(stopWatch.getElapsedTime());

    LOGGER.info("[TABLE METHOD] Execution took in seconds: " + stopWatch.getElapsedTime());

    return ResponseEntity.ok(testObject);
  }

  @GetMapping("/query-table")
  public ResponseEntity<?> queryTableReturnJson(
      @RequestParam(required = true, value = "limit") String limit,
      @RequestParam(required = true, value = "offset") String offset)
      throws JobException, InterruptedException {

    StopWatch stopWatch = new StopWatch();

    String query = "SELECT * FROM `" + projectId + "." + datasetName + "."
        + tableName + "`LIMIT " + limit + " OFFSET " + offset;

    QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query).setUseLegacySql(false).build();

    TableResult results = bigQuery.query(config);

    List<Object> objs = new ArrayList<Object>();

    for (FieldValueList row : results.getValues()) {
      CovidDto covidDto = new CovidDto();
      covidDto.setLocation(row.get(0).getStringValue());
      covidDto.setDate(row.get(1).getStringValue());
      covidDto.setVariant(row.get(2).getStringValue());
      covidDto.setNum_sequences(row.get(3).getNumericValue().intValue());
      covidDto.setPerc_sequences(row.get(4).getDoubleValue());
      covidDto.setNum_sequences_total(row.get(5).getNumericValue().intValue());
      objs.add(covidDto);
    }

    LOGGER.info("[TABLE METHOD] Execution took in seconds: " + stopWatch.getElapsedTime());

    return ResponseEntity.ok(objs);
  }

}
