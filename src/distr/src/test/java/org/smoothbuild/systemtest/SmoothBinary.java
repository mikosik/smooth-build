package org.smoothbuild.systemtest;

import static com.google.common.io.ByteStreams.toByteArray;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class SmoothBinary {
  public static Path smoothBinary(Path gitRepoRoot) {
    String smoothHomeEnv = System.getenv("smooth_home_dir");
    Path smoothHome = smoothHomeEnv == null
        ? assembleSmoothBinary(gitRepoRoot)
        : Path.of(smoothHomeEnv);
    return smoothHome.resolve("smooth");
  }

  private static Path assembleSmoothBinary(Path gitRepoRoot) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder("ant", "install-smooth");
      processBuilder.directory(gitRepoRoot.toFile());
      Process process = processBuilder.start();
      ExecutorService executor = Executors.newFixedThreadPool(2);
      Future<byte[]> inputStream = executor.submit(() -> toByteArray(process.getInputStream()));
      Future<byte[]> errorStream = executor.submit(() -> toByteArray(process.getErrorStream()));
      int exitCode = process.waitFor();
      String outputData = new String(inputStream.get(), UTF_8);
      String errorData = new String(errorStream.get(), UTF_8);
      if (exitCode != 0) {
        throw new RuntimeException(
            "Running 'ant install-smooth' failed with following output\n"
                + "STANDARD OUTPUT\n" + outputData + "\n"
                + "STANDARD ERROR\n" + errorData + "\n");
      }
      return gitRepoRoot.resolve("build/systemtest/smooth");
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (IOException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }
}
