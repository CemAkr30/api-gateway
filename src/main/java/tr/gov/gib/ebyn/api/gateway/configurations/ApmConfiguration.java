package tr.gov.gib.ebyn.api.gateway.configurations;

import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.APPLICATION_PACKAGES_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.ENABLE_EXPERIMENTAL_INSTRUMENTATIONS_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.ENVIRONMENT_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.LOG_LEVEL_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.METRICS_INTERVAL_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.SERVER_URL_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.SERVICE_NAME_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.SPAN_FRAMES_MIN_DURATION_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.TRANSACTION_SAMPLE_RATE_KEY;
import static tr.gov.gib.ebyn.api.gateway.constants.ApmConstant.USE_PATH_AS_TRANSACTION_NAME_KEY;

import co.elastic.apm.attach.ElasticApmAttacher;
import jakarta.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.annotation.Configuration;
import tr.gov.gib.ebyn.api.gateway.configurations.properties.ApmConnectionProperty;

@Setter
@Configuration
@RequiredArgsConstructor
public class ApmConfiguration {

  private final ApmConnectionProperty apmConnectionProperty;

  @PostConstruct
  public void init() {
    Map<String, String> apmProps = new HashMap<>();
    addApmProperties(apmProps);
    attachRunner(apmProps);
  }

  private void addApmProperties(Map<String, String> apmProps) {
    apmProps.put(SERVER_URL_KEY, apmConnectionProperty.getServerUrl());
    apmProps.put(SERVICE_NAME_KEY, apmConnectionProperty.getServiceName());
    apmProps.put(ENVIRONMENT_KEY, apmConnectionProperty.getEnvironment());
    apmProps.put(APPLICATION_PACKAGES_KEY, apmConnectionProperty.getApplicationPackages());
    apmProps.put(LOG_LEVEL_KEY, apmConnectionProperty.getLogLevel());
    apmProps.put(ENABLE_EXPERIMENTAL_INSTRUMENTATIONS_KEY,
        String.valueOf(apmConnectionProperty.isEnableExperimentalInstrumentations()));
    apmProps.put(TRANSACTION_SAMPLE_RATE_KEY,
        String.valueOf(apmConnectionProperty.getTransactionSampleRate()));
    apmProps.put(SPAN_FRAMES_MIN_DURATION_KEY, apmConnectionProperty.getSpanFramesMinDuration());
    apmProps.put(METRICS_INTERVAL_KEY, apmConnectionProperty.getMetricsInterval());
    apmProps.put(USE_PATH_AS_TRANSACTION_NAME_KEY,
        String.valueOf(apmConnectionProperty.isUsePathAsTransactionName()));
  }

  private void attachRunner(Map<String, String> apmProps) {
    ElasticApmAttacher.attach(apmProps);
  }
}
