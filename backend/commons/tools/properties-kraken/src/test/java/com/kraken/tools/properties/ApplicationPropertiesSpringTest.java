package com.kraken.tools.properties;

import com.kraken.Application;
import com.kraken.tools.properties.api.ApplicationProperties;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.assertj.core.api.Assertions.assertThat;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
public class ApplicationPropertiesSpringTest {

  @Autowired
  ApplicationProperties properties;

  @Test
  public void shouldCreateProperties() {
    assertThat(properties.getData()).isNotNull();
  }
}
