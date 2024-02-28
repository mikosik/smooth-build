package org.smoothbuild.common;

import static com.google.common.io.ByteStreams.toByteArray;
import static org.smoothbuild.common.Constants.CHARSET;

import java.io.IOException;
import java.nio.file.Path;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class CommandExecutor {
  public static CommandResult execute(Path workingDir, String[] allArgs)
      throws IOException, InterruptedException, ExecutionException {
    ProcessBuilder processBuilder = new ProcessBuilder(allArgs);
    processBuilder.directory(workingDir.toFile());
    Process process = processBuilder.start();
    ExecutorService executor = Executors.newFixedThreadPool(2);
    Future<byte[]> inputStream = executor.submit(() -> toByteArray(process.getInputStream()));
    Future<byte[]> errorStream = executor.submit(() -> toByteArray(process.getErrorStream()));
    int exitCode = process.waitFor();
    String systemOut = new String(inputStream.get(), CHARSET);
    String systemErr = new String(errorStream.get(), CHARSET);
    return new CommandResult(exitCode, systemOut, systemErr);
  }

  public static record CommandResult(int exitCode, String systemOut, String systemErr) {}
}
