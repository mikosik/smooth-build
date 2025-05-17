package org.smoothbuild.stdlib.java;

import static java.lang.Thread.currentThread;
import static java.net.http.HttpResponse.BodyHandlers.ofInputStream;
import static okio.Okio.buffer;
import static okio.Okio.source;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import org.smoothbuild.virtualmachine.bytecode.BytecodeException;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BString;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;
import org.smoothbuild.virtualmachine.evaluate.plugin.NativeApi;

public class MavenArtifactFunc {
  public static BValue func(NativeApi nativeApi, BTuple args) throws BytecodeException {
    try (var httpClient = HttpClient.newHttpClient()) {
      return funcImpl(nativeApi, args, httpClient);
    }
  }

  static BValue funcImpl(NativeApi nativeApi, BTuple args, HttpClient httpClient)
      throws BytecodeException {
    var coordinate = mavenCoordinate(args);
    try {
      var response = httpClient.send(httpRequest(coordinate.url()), ofInputStream());
      if (response.statusCode() != 200) {
        var template =
            """
            Failed to download Maven artifact %s
            from %s
            Status code: %d""";
        var message = template.formatted(coordinate, coordinate.url(), response.statusCode());
        nativeApi.log().fatal(message);
        return null;
      }

      try (var blobBuilder = nativeApi.factory().blobBuilder()) {
        try (var sourceBuffer = buffer(source(response.body()))) {
          sourceBuffer.readAll(blobBuilder);
        }
        var content = blobBuilder.build();
        var jarName = nativeApi.factory().string(coordinate.jarName());
        return nativeApi.factory().file(content, jarName);
      }
    } catch (IOException e) {
      nativeApi.log().fatal("Error downloading Maven artifact: " + e.getMessage());
      return null;
    } catch (InterruptedException e) {
      nativeApi.log().fatal("Error downloading Maven artifact: " + e.getMessage());
      currentThread().interrupt();
      return null;
    }
  }

  private static HttpRequest httpRequest(String mavenUrl) {
    return HttpRequest.newBuilder().uri(URI.create(mavenUrl)).GET().build();
  }

  private static MavenCoordinate mavenCoordinate(BTuple args) throws BytecodeException {
    String groupIdPath = ((BString) args.get(0)).toJavaString();
    String artifactIdStr = ((BString) args.get(1)).toJavaString();
    String versionStr = ((BString) args.get(2)).toJavaString();
    return new MavenCoordinate(groupIdPath, artifactIdStr, versionStr);
  }
}
