package org.smoothbuild.stdlib.java;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.dagger.VmTestContext;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class MavenArtifactFuncTest extends VmTestContext {
  @Test
  void downloads_artifact_via_http_client() throws Exception {
    var jarContent = "jar content";
    HttpClient httpClient = mock();
    HttpResponse<InputStream> response = mock();

    when(httpClient.send(any(HttpRequest.class), eq(BodyHandlers.ofInputStream())))
        .thenReturn(response);
    when(response.statusCode()).thenReturn(200);
    when(response.body()).thenReturn(new ByteArrayInputStream(jarContent.getBytes()));

    var result = MavenArtifactFunc.funcImpl(
        provide().container(),
        bTuple(bString("com.example"), bString("library"), bString("1.0.0")),
        httpClient);

    assertThat(result).isEqualTo(bFile("library-1.0.0.jar", jarContent));
  }

  @Test
  void reports_fatal_when_downloading_fails() throws Exception {
    HttpClient httpClient = mock();
    HttpResponse<InputStream> response = mock();
    when(httpClient.send(any(HttpRequest.class), eq(BodyHandlers.ofInputStream())))
        .thenReturn(response);
    when(response.statusCode()).thenReturn(404);

    var nativeApi = (NativeApi) provide().container();
    var result = MavenArtifactFunc.funcImpl(
        nativeApi,
        bTuple(bString("com.example"), bString("library"), bString("1.0.0")),
        httpClient);

    assertThat(result).isNull();
    assertThat(nativeApi.messages())
        .isEqualTo(
            bArray(
                bFatalLog(
                    """
                Failed to download Maven artifact com.example:library:1.0.0
                from https://repo1.maven.org/maven2/com/example/library/1.0.0/library-1.0.0.jar
                Status code: 404""")));
  }
}
