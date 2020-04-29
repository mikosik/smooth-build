package org.smoothbuild.acceptance;

import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.io.Files.createTempDir;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.Assert.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.SmoothPaths.USER_MODULE;
import static org.smoothbuild.acceptance.GitRepo.gitRepoRoot;
import static org.smoothbuild.acceptance.SmoothBinary.smoothBinary;
import static org.smoothbuild.cli.console.Console.prefixMultiline;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.smoothbuild.cli.command.BuildCommand;
import org.smoothbuild.cli.command.CleanCommand;
import org.smoothbuild.cli.command.ListCommand;
import org.smoothbuild.cli.command.TreeCommand;
import org.smoothbuild.cli.command.VersionCommand;
import org.smoothbuild.util.DataReader;

import com.google.common.collect.ImmutableList;

import okio.BufferedSource;
import okio.ByteString;

public abstract class AcceptanceTestCase {
  private static final String DEFAULT_BUILD_SCRIPT_FILE = "build.smooth";
  private static final String DEFAULT_NATIVE_JAR_FILE = "build.jar";
  private static final String ARTIFACTS_DIR_PATH = ".smooth/artifacts/";
  private static final Path GIT_REPO_ROOT = gitRepoRoot();
  private static final Path SMOOTH_BINARY = smoothBinary(GIT_REPO_ROOT);

  private File projectDir;
  private Integer exitCode;
  private String sysOut;
  private String sysErr;

  @BeforeEach
  public void init() {
    projectDir = createTempDir();
  }

  @AfterEach
  public void destroy() throws IOException {
    deleteRecursively(projectDir().toPath());
  }

  public void givenScript(String... lines) throws IOException {
    givenRawScript(quotesX2(join("\n", lines)));
  }

  public static String quotesX2(String string) {
    return string.replace('\'', '"');
  }

  public void givenRawScript(String buildScript) throws IOException {
    givenFile(DEFAULT_BUILD_SCRIPT_FILE, buildScript);
  }

  public void givenFile(String path, String content) throws IOException {
    File fullPath = file(path);
    fullPath.getParentFile().mkdirs();
    try (FileWriter writer = new FileWriter(fullPath.toString(), UTF_8)) {
      writer.write(content);
    }
  }

  public void givenNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(file(DEFAULT_NATIVE_JAR_FILE), classes);
  }

  public void givenDir(String path) throws IOException {
    file(path).mkdirs();
  }

  public void givenJunitCopied() {
    copyLib("junit-4.13.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = projectDir.toPath().resolve(dirInsideProject);
      destinationDir.toFile().mkdirs();
      return Files.copy(
          GIT_REPO_ROOT.resolve("lib/ivy").resolve(jar),
          destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void whenSmoothBuild(String... args) {
    whenSmooth(concat(BuildCommand.NAME, args));
  }

  public void whenSmoothClean(String... args) {
    whenSmooth(concat(CleanCommand.NAME, args));
  }

  public void whenSmoothTree(String... args) {
    whenSmooth(concat(TreeCommand.NAME, args));
  }

  public void whenSmoothHelp(String... args) {
    whenSmooth(concat("help", args));
  }

  public void whenSmoothList(String... args) {
    whenSmooth(concat(ListCommand.NAME, args));
  }

  public void whenSmoothVersion(String... args) {
    whenSmooth(concat(VersionCommand.NAME, args));
  }

  public void whenSmooth(String... smoothCommandArgs) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(smoothCommandArgs));
      processBuilder.directory(projectDir());
      Process process = processBuilder.start();
      ExecutorService executor = Executors.newFixedThreadPool(2);
      Future<byte[]> inputStream = executor.submit(() -> toByteArray(process.getInputStream()));
      Future<byte[]> errorStream = executor.submit(() -> toByteArray(process.getErrorStream()));
      exitCode = process.waitFor();
      sysOut = new String(inputStream.get(), UTF_8);
      sysErr = new String(errorStream.get(), UTF_8);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new RuntimeException(e);
    } catch (IOException | ExecutionException e) {
      throw new RuntimeException(e);
    }
  }

  public static ImmutableList<String> processArgs(String... params) {
    ImmutableList.Builder<String> builder = ImmutableList.builder();
    builder.add(SMOOTH_BINARY.toString());
    builder.addAll(list(params));
    return builder.build();
  }

  public void thenFinishedWithSuccess() {
    thenReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void thenFinishedWithError() {
    thenReturnedCode(EXIT_CODE_ERROR);
  }

  private void thenReturnedCode(int expected) {
    if (expected != exitCode) {
      fail("Expected return code " + expected + " but was " + exitCode + ".\n"
          + "standard out:\n" + sysOut + "\n"
          + "standard err:\n" + sysErr + "\n");
    }
  }

  public void thenSysOutContainsParseError(int lineNumber, String... errorLines) {
    errorLines[0] = USER_MODULE.fullPath() + ":" + lineNumber + ": " + errorLines[0];
    thenSysOutContainsParseError(errorLines);
  }

  public void thenSysOutContainsParseError(String... errorLines) {
    thenSysOutContainsError(USER_MODULE.fullPath().toString(), errorLines);
  }

  public void thenSysOutContainsError(String header, String... errorLines) {
    errorLines[0] = "ERROR: " + errorLines[0];
    thenSysOutContains(
        "  " + header,
        prefixMultiline(errorLines));
  }

  public void thenSysOutContains(String... lines) {
    String text = unlines(lines);
    if (!sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut");
    }
  }

  public void thenSysErrContains(String... lines) {
    String text = unlines(lines);
    if (!sysErr.contains(text)) {
      failWithFullOutputs(text, "SysErr");
    }
  }

  private void failWithFullOutputs(String text, String streamName) {
    // We use isEqualTo() instead of contains() so generated failure text will contain
    // as much data as possible and intellij is able to display it via visual diff.
    assertWithMessage(streamName + " doesn't contain expected substring")
        .that(unlines(
            "================= SYS-OUT ====================",
            sysOut,
            "================= SYS-ERR ====================",
            sysErr
        ))
        .isEqualTo(text);
  }

  public String sysOut() {
    return sysOut;
  }

  public String sysErr() {
    return sysErr;
  }

  public File artifact(String name) {
    return file(ARTIFACTS_DIR_PATH + name);
  }

  /**
   * @return given artifact as ByteString, or List<ByteString> if it is array,
   * or List<List<ByteString>> if it is array of depth=2, and so on.
   */
  public Object artifactAsByteStrings(String name) {
    try {
      return actual(artifact(name), BufferedSource::readByteString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean artifactAsBoolean(String name) {
    try {
      return readAndClose(buffer(source(artifact(name))), s -> {
        ByteString value = s.readByteString();
        if (value.size() != 1) {
          throw new RuntimeException("Expected boolean artifact but got " + value.toString());
        }
        switch (value.getByte(0)) {
          case 0:
            return false;
          case 1:
            return true;
          default:
            throw new RuntimeException("Expected boolean artifact but got " + value.toString());
        }
      });
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Object artifactArray(String name) {
    try {
      return actual(artifact(name), s -> s.readString(CHARSET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(File file, DataReader<?> dataReader) throws IOException {
    if (!file.exists()) {
      return null;
    }
    if (file.isDirectory()) {
      return actualArray(file, dataReader);
    }
    return readAndClose(buffer(source(file)), dataReader);
  }

  private static Object actualArray(File file, DataReader<?> dataReader) throws IOException {
    int count = file.list().length;

    List<Object> result = new ArrayList<>(count);
    for (int i = 0; i < count; i++) {
      result.add(actual(new File(file, Integer.toString(i)), dataReader));
    }
    return result;
  }

  public File file(String path) {
    return new File(projectDir(), path);
  }

  public File projectDir() {
    return projectDir;
  }

  public String artifactContent(String artifact) throws IOException {
    File file = artifact(artifact);
    return fileContent(file);
  }

  public Map<String, String> artifactDir(String artifact) throws IOException {
    File dir = artifact(artifact);
    if (!dir.exists()) {
      Assertions.fail("No such artifact: " + artifact);
    }
    HashMap<String, String> result = new HashMap<>();
    addFilesToMap(dir, "", result);
    return result;
  }

  private void addFilesToMap(File dir, String prefix, HashMap<String, String> result)
      throws IOException {
    for (String fileName : dir.list()) {
      File file = new File(dir, fileName);
      if (file.isDirectory()) {
        addFilesToMap(file, prefix + file.getName() + "/", result);
      } else {
        result.put(prefix + file.getName(), fileContent(file));
      }
    }
  }

  private static String fileContent(File file) throws IOException {
    return readAndClose(buffer(source(file)), s -> s.readString(CHARSET));
  }
}
