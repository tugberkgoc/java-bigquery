package cloudcode.helloworld.connector;

import java.io.FileInputStream;
import java.io.InputStream;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.google.cloud.bigquery.BigQuery;
import com.google.cloud.bigquery.BigQueryOptions;
import com.google.cloud.bigquery.FormatOptions;
import com.google.cloud.bigquery.Job;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gcp.bigquery.core.BigQueryTemplate;
import org.springframework.cloud.gcp.bigquery.integration.BigQuerySpringMessageHeaders;
import org.springframework.cloud.gcp.bigquery.integration.outbound.BigQueryFileMessageHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.gateway.GatewayProxyFactoryBean;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.util.concurrent.ListenableFuture;

/** Sample configuration for using BigQuery with Spring Integration. */
// https://github.com/GoogleCloudPlatform/spring-cloud-gcp/tree/main/spring-cloud-gcp-samples/spring-cloud-gcp-bigquery-sample
@Configuration
public class BigQuerySampleConfiguration {

  private static final Logger LOGGER = LoggerFactory.getLogger(BigQuerySampleConfiguration.class);

  @Value("${GOOGLE_APPLICATION_CREDENTIALS}")
  private String credentialJsonPath;

  @Value("${spring.cloud.gcp.project-id}")
  private String projectId;

  @Bean
  public BigQuery createBigQueryBean() {
    GoogleCredentials credentials;
    try {
      InputStream in = new FileInputStream(credentialJsonPath);
      credentials = ServiceAccountCredentials.fromStream(in);

      BigQuery bigQuery = BigQueryOptions.newBuilder().setCredentials(credentials)
          .setProjectId(projectId).build().getService();

      LOGGER.info("BigQuery Service is ready !!");

      return bigQuery;
    } catch (Exception io) {
      LOGGER.warn("Credentials/ProjectId is wrong!!");
      io.printStackTrace();
    }

    return BigQueryOptions.getDefaultInstance().getService();
  }

  @Bean
  public DirectChannel bigQueryWriteDataChannel() {
    return new DirectChannel();
  }

  @Bean
  public DirectChannel bigQueryJobReplyChannel() {
    return new DirectChannel();
  }

  @Bean
  @ServiceActivator(inputChannel = "bigQueryWriteDataChannel")
  public MessageHandler messageSender(BigQueryTemplate bigQueryTemplate) {
    BigQueryFileMessageHandler messageHandler = new BigQueryFileMessageHandler(bigQueryTemplate);
    messageHandler.setFormatOptions(FormatOptions.csv());
    messageHandler.setOutputChannel(bigQueryJobReplyChannel());
    return messageHandler;
  }

  @Bean
  public GatewayProxyFactoryBean gatewayProxyFactoryBean() {
    GatewayProxyFactoryBean factoryBean = new GatewayProxyFactoryBean(BigQueryFileGateway.class);
    factoryBean.setDefaultRequestChannel(bigQueryWriteDataChannel());
    factoryBean.setDefaultReplyChannel(bigQueryJobReplyChannel());
    // Ensures that BigQueryFileGateway does not return double-wrapped
    // ListenableFutures
    factoryBean.setAsyncExecutor(null);
    return factoryBean;
  }

  /**
   * Spring Integration gateway which allows sending data to load to BigQuery
   * through a channel.
   */
  @MessagingGateway
  public interface BigQueryFileGateway {
    ListenableFuture<Job> writeToBigQueryTable(byte[] csvData,
        @Header(BigQuerySpringMessageHeaders.TABLE_NAME) String tableName);
  }
}
