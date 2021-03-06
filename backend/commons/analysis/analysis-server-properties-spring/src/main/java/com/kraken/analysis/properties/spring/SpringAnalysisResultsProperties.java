package com.kraken.analysis.properties.spring;

import com.kraken.analysis.properties.api.AnalysisResultsProperties;
import lombok.Builder;
import lombok.NonNull;
import lombok.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

import java.nio.file.Path;
import java.nio.file.Paths;

@Value
@Builder
@ConstructorBinding
@ConfigurationProperties("kraken.analysis.results")
public class SpringAnalysisResultsProperties implements AnalysisResultsProperties {
  @NonNull
  String root;

  @Override
  public Path getResultPath(final String resultId) {
    return Paths.get(root, resultId);
  }
}
