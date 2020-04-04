package com.kraken.tools.sse.server;

import com.google.common.base.Charsets;
import com.google.common.collect.ImmutableList;
import com.kraken.runtime.client.api.RuntimeClient;
import com.kraken.runtime.entity.log.LogTest;
import com.kraken.runtime.entity.task.TaskTest;
import com.kraken.storage.client.api.StorageClient;
import com.kraken.storage.entity.StorageWatcherEventTest;
import com.kraken.tests.security.AuthControllerTest;
import com.kraken.tests.utils.TestUtils;
import com.kraken.tools.sse.SSEService;
import com.kraken.tools.sse.SSEWrapper;
import com.kraken.tools.sse.SSEWrapperTest;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerSentEvent;
import reactor.core.publisher.Flux;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.BDDMockito.given;

public class SSEControllerTest extends AuthControllerTest {

  @MockBean
  SSEService sse;

  @MockBean
  RuntimeClient runtimeClient;

  @MockBean
  StorageClient storageClient;

  @Test
  public void shouldPassTestUtils() {
    TestUtils.shouldPassNPE(SSEController.class);
  }

  @Test
  public void shouldWatch() {
    final var applicationId = "app";
    given(sse.merge(anyMap())).willReturn(Flux.just(SSEWrapperTest.WRAPPER_STRING, SSEWrapperTest.WRAPPER_INT));
    given(sse.keepAlive(Mockito.<Flux<SSEWrapper>>any())).willReturn(Flux.just(ServerSentEvent.builder(SSEWrapperTest.WRAPPER_STRING).build()));
    given(runtimeClient.watchLogs(applicationId)).willReturn(Flux.just(LogTest.LOG));
    given(runtimeClient.watchTasks(applicationId)).willReturn(Flux.just(ImmutableList.of(TaskTest.TASK)));
    given(storageClient.watch()).willReturn(Flux.just(StorageWatcherEventTest.STORAGE_WATCHER_EVENT));

    final var result = webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/watch").build())
        .header("ApplicationId", applicationId)
        .header("Authorization", "Bearer user-token")
        .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
        .exchange()
        .expectStatus().isOk()
        .expectHeader().contentType(MediaType.TEXT_EVENT_STREAM_VALUE + ";charset=UTF-8")
        .expectBody()
        .returnResult();
    final var body = new String(Optional.ofNullable(result.getResponseBody()).orElse(new byte[0]), Charsets.UTF_8);
    Assertions.assertThat(body).isEqualTo("data:{\"type\":\"String\",\"value\":\"value\"}\n" +
        "\n");
  }

  @Test
  public void shouldFailToWatchHeader() {
    final var applicationId = "applicationId"; // Should match [a-z0-9]*
    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/watch").build())
        .header("Authorization", "Bearer user-token")
        .header("ApplicationId", applicationId)
        .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
        .exchange()
        .expectStatus().is5xxServerError();
  }

  @Test
  public void shouldFailToWatchForbidden() {
    final var applicationId = "app";
    webTestClient.get()
        .uri(uriBuilder -> uriBuilder.path("/watch").build())
        .header("Authorization", "Bearer no-role-token")
        .header("ApplicationId", applicationId)
        .accept(MediaType.valueOf(MediaType.TEXT_EVENT_STREAM_VALUE))
        .exchange()
        .expectStatus().is4xxClientError();
  }
}