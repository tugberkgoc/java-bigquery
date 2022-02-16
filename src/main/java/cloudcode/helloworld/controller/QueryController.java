package cloudcode.helloworld.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.FieldValueList;
import com.google.cloud.bigquery.Job;
import com.google.cloud.bigquery.JobException;
import com.google.cloud.bigquery.JobId;
import com.google.cloud.bigquery.JobInfo;
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
import cloudcode.helloworld.api.BigQueryApi;
import cloudcode.helloworld.payload.response.CovidDto;
import cloudcode.helloworld.util.StopWatch;

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

        @GetMapping("/all-job")
        public ResponseEntity<List<CovidDto>> getAllWithUsingJob(
                        @RequestParam(required = true, value = "limit") String limit,
                        @RequestParam(required = true, value = "offset") String offset)
                        throws InterruptedException {

                StopWatch stopWatch = new StopWatch();

                String query = "SELECT * FROM `" + projectId + "." + datasetName + "." + tableName
                                + "` LIMIT " + limit + " OFFSET " + offset;

                // See: https://cloud.google.com/bigquery/sql-reference/
                QueryJobConfiguration queryConfig = QueryJobConfiguration.newBuilder(query)
                                .setUseLegacySql(false).build();

                Job queryJob = bigQuery.create(JobInfo.newBuilder(queryConfig)
                                .setJobId(JobId.of(UUID.randomUUID().toString())).build());

                // Wait for the query to complete.
                queryJob = queryJob.waitFor();

                if (queryJob == null) {
                        throw new RuntimeException("Job no longer exists");
                } else if (queryJob.getStatus().getError() != null) {
                        throw new RuntimeException(queryJob.getStatus().getError().toString());
                }

                TableResult result = queryJob.getQueryResults();

                List<CovidDto> covids = new ArrayList<CovidDto>();

                for (FieldValueList row : result.iterateAll()) {
                        String location = row.get("location").getStringValue();
                        String date = row.get("date").getStringValue();
                        String variant = row.get("variant").getStringValue();
                        int numSequences = row.get("num_sequences").getNumericValue().intValue();
                        double percSequences = row.get("perc_sequences").getDoubleValue();
                        int numSequencesTotal =
                                        row.get("num_sequences_total").getNumericValue().intValue();

                        covids.add(new CovidDto(location, date, variant, numSequences,
                                        percSequences, numSequencesTotal));
                }

                LOGGER.info("[JOB METHOD] Execution took in seconds: "
                                + stopWatch.getElapsedTime());

                return ResponseEntity.ok(covids);
        }

        @GetMapping("/all-table")
        public ResponseEntity<?> getAllWithUsingTable(
                        @RequestParam(required = true, value = "limit") String limit,
                        @RequestParam(required = true, value = "offset") String offset) {

                StopWatch stopWatch = new StopWatch();

                // TableId tableId = TableId.of(datasetName, tableName);
                TableResult results = bigQuery.listTableData(datasetName, tableName,
                                BigQuery.TableDataListOption.pageSize(Integer.parseInt(limit)),
                                BigQuery.TableDataListOption.startIndex(Integer.parseInt(offset)));

                List<CovidDto> covids = new ArrayList<CovidDto>();

                for (FieldValueList row : results.getValues()) {
                        String location = row.get(0).getStringValue();
                        String date = row.get(1).getStringValue();
                        String variant = row.get(2).getStringValue();
                        int numSequences = row.get(3).getNumericValue().intValue();
                        double percSequences = row.get(4).getDoubleValue();
                        int numSequencesTotal = row.get(5).getNumericValue().intValue();

                        covids.add(new CovidDto(location, date, variant, numSequences,
                                        percSequences, numSequencesTotal));
                }

                LOGGER.info("[TABLE METHOD] Execution took in seconds: "
                                + stopWatch.getElapsedTime());

                return ResponseEntity.ok(covids);
        }

        @GetMapping("/query-table")
        public ResponseEntity<?> queryTableReturnJson(
                        @RequestParam(required = true, value = "limit") String limit,
                        @RequestParam(required = true, value = "offset") String offset)
                        throws JobException, InterruptedException {

                StopWatch stopWatch = new StopWatch();

                String query = "SELECT TO_JSON_STRING(t) FROM `" + projectId + "." + datasetName
                                + "." + tableName + "` AS t LIMIT " + limit + " OFFSET " + offset;

                QueryJobConfiguration config = QueryJobConfiguration.newBuilder(query)
                                .setUseLegacySql(false).build();

                TableResult results = bigQuery.query(config);

                results.iterateAll().forEach(row -> row
                                .forEach(val -> System.out.printf("%s,", val.toString())));

                LOGGER.info("[TABLE METHOD] Execution took in seconds: "
                                + stopWatch.getElapsedTime());

                return ResponseEntity.ok(results.getValues().toString());
        }

        @GetMapping("/all-api")
        public ResponseEntity<?> getAllWithUsingApi(
                        @RequestParam(required = true, value = "limit") String limit,
                        @RequestParam(required = true, value = "offset") String offset) {

                StopWatch stopWatch = new StopWatch();

                BigQueryApi bigQueryApi = new BigQueryApi();

                String response = bigQueryApi.makeRequest();


                LOGGER.info("[API METHOD] Execution took in seconds: "
                                + stopWatch.getElapsedTime());

                return ResponseEntity.ok(response);
        }
}
