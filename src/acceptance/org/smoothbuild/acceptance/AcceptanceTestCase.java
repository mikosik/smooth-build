package org.smoothbuild.acceptance;

import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.io.ByteStreams.toByteArray;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.UTF_8;
import static java.nio.file.Files.createDirectories;
import static java.util.stream.Collectors.toList;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.SmoothConstants.CHARSET;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_ERROR;
import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;
import static org.smoothbuild.acceptance.AcceptanceUtils.GIT_REPO_ROOT;
import static org.smoothbuild.acceptance.AcceptanceUtils.SMOOTH_BINARY;
import static org.smoothbuild.acceptance.CommandWithArgs.buildCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.cleanCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.helpCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.listCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.treeCommand;
import static org.smoothbuild.acceptance.CommandWithArgs.versionCommand;
import static org.smoothbuild.cli.console.Console.prefixMultiline;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.SMOOTH_DIR;
import static org.smoothbuild.install.ProjectPaths.USER_MODULE_PATH;
import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.util.Okios.readAndClose;
import static org.smoothbuild.util.Strings.unlines;
import static org.smoothbuild.util.reflect.Classes.saveBytecodeInJar;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Stream;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.cli.Main;
import org.smoothbuild.util.DataReader;

import okio.BufferedSource;
import okio.ByteString;

public abstract class AcceptanceTestCase {
  private Path projectDir;
  private Integer exitCode;
  private String sysOut;
  private String sysErr;

  @BeforeEach
  public void init(@TempDir Path projectDir) {
    this.projectDir = projectDir;
  }

  @AfterEach
  public void destroy() throws IOException {
    deleteRecursively(projectDirAbsolutePath());
  }

  public void givenScript(String... lines) throws IOException {
    givenRawScript(quotesX2(join("\n", lines)));
  }

  public static String quotesX2(String string) {
    return string.replace('\'', '"');
  }

  public void givenRawScript(String buildScript) throws IOException {
    givenFile(USER_MODULE_PATH.toString(), buildScript);
  }

  public void givenFile(String path, String content) throws IOException {
    Path fullPath = absolutePath(path);
    createDirectories(fullPath.getParent());
    try (FileWriter writer = new FileWriter(fullPath.toString(), UTF_8)) {
      writer.write(content);
    }
  }

  public void givenNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(absolutePath("build.jar"), classes);
  }

  public void givenDir(String path) throws IOException {
    createDirectories(absolutePath(path));
  }

  public void givenJunitCopied() {
    copyLib("junit-4.13.jar", "junit/");
    copyLib("hamcrest-core-1.3.jar", "junit/");
  }

  private Path copyLib(String jar, String dirInsideProject) {
    try {
      Path destinationDir = absolutePath(dirInsideProject);
      createDirectories(destinationDir);
      return Files.copy(GIT_REPO_ROOT.resolve("lib/ivy").resolve(jar), destinationDir.resolve(jar));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public void whenSmoothBuild(String... args) {
    whenSmooth(buildCommand(args));
  }

  public void whenSmoothClean(String... args) {
    whenSmooth(cleanCommand(args));
  }

  public void whenSmoothHelp(String... args) {
    whenSmoothWithoutProjectAndInstallationDir(helpCommand(args));
  }

  public void whenSmoothList(String... args) {
    whenSmooth(listCommand(args));
  }

  public void whenSmoothTree(String... args) {
    whenSmooth(treeCommand(args));
  }

  public void whenSmoothVersion(String... args) {
    whenSmoothWithoutProjectDir(versionCommand(args));
  }

  public void whenSmoothWithoutProjectAndInstallationDir(CommandWithArgs command) {
    runSmooth(command);
  }

  public void whenSmoothWithoutProjectDir(CommandWithArgs command) {
    runSmooth(command,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  public void whenSmooth(CommandWithArgs command) {
    runSmooth(command,
        "--project-dir=" + projectDir,
        "--INTERNAL-installation-dir=" + SMOOTH_BINARY.getParent().toAbsolutePath());
  }

  private void runSmooth(CommandWithArgs command, String... additionalArguments) {
    switch (AcceptanceUtils.TEST_MODE) {
      case SINGLE_JVM:
        runSmoothInCurrentJvm(command, additionalArguments);
        break;
        case FULL_BINARY:
          runSmoothInForkedJvm(command);
          break;
      default:
        fail("Unknown mode: " + AcceptanceUtils.TEST_MODE);
    }
  }

  private void runSmoothInCurrentJvm(CommandWithArgs command, String... additionalArgs) {
    ByteArrayOutputStream outBytes = new ByteArrayOutputStream();
    ByteArrayOutputStream errBytes = new ByteArrayOutputStream();
    try (PrintWriter outWriter = printWriter(outBytes);
        PrintWriter errWriter = printWriter(errBytes)) {
      String[] commandAndAllArgs = command.commandPlusArgsPlus(additionalArgs);
      exitCode = Main.runSmooth(commandAndAllArgs, outWriter, errWriter);
      outWriter.flush();
      errWriter.flush();
      this.sysOut = outBytes.toString(UTF_8);
      this.sysErr = errBytes.toString(UTF_8);
    }
  }

  private static PrintWriter printWriter(ByteArrayOutputStream outBytes) {
    return new PrintWriter(outBytes, true, UTF_8);
  }

  private void runSmoothInForkedJvm(CommandWithArgs command) {
    try {
      ProcessBuilder processBuilder = new ProcessBuilder(processArgs(command.commandPlusArgs()));
      processBuilder.directory(projectDirAbsolutePath().toFile());
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

  public static String[] processArgs(String... params) {
    return concat(SMOOTH_BINARY.toString(), params);
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

  public int exitCode() {
    return exitCode;
  }

  public void thenSysOutContainsParseError(int lineNumber, String... errorLines) {
    errorLines[0] = USER_MODULE_PATH.toString() + ":" + lineNumber + ": " + errorLines[0];
    thenSysOutContainsParseError(errorLines);
  }

  public void thenSysOutContainsParseError(String... errorLines) {
    errorLines[0] = "ERROR: " + errorLines[0];
    thenSysOutContains(
        "  " + USER_MODULE_PATH.toString(),
        prefixMultiline(errorLines));
  }

  public void thenSysOutContains(String... lines) {
    String text = unlines(lines);
    if (!sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut doesn't contain expected substring");
    }
  }

  public void thenSysOutDoesNotContain(String... lines) {
    String text = unlines(lines);
    if (sysOut.contains(text)) {
      failWithFullOutputs(text, "SysOut contains forbidden substring");
    }
  }

  public void thenSysErrContains(String... lines) {
    String text = unlines(lines);
    if (!sysErr.contains(text)) {
      failWithFullOutputs(text, "SysErr doesn't contain expected substring");
    }
  }

  private void failWithFullOutputs(String text, String message) {
    // We use isEqualTo() instead of contains() so generated failure text will contain
    // as much data as possible and intellij is able to display it via visual diff.
    assertWithMessage(message)
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

  /**
   * @return given artifact as ByteString, or List<ByteString> if it is array,
   * or List<List<ByteString>> if it is array of depth=2, and so on.
   */
  public Object artifactAsByteStrings(String name) {
    try {
      return actual(artifactAbsolutePath(name), BufferedSource::readByteString);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public boolean artifactAsBoolean(String name) {
    try {
      return readAndClose(buffer(source(artifactAbsolutePath(name))), s -> {
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

  /**
   * Returns content of artifact as string
   * or (when artifact is an array) returns list containing stringified elements.
   * Works with array artifacts of any depth.
   */
  public Object stringifiedArtifact(String name) {
    try {
      return actual(artifactAbsolutePath(name), s -> s.readString(CHARSET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(Path path, DataReader<?> dataReader) throws IOException {
    if (!Files.exists(path)) {
      return null;
    }
    if (Files.isDirectory(path)) {
      return actualArray(path, dataReader);
    }
    return readAndClose(buffer(source(path)), dataReader);
  }

  private static Object actualArray(Path path, DataReader<?> dataReader) throws IOException {
    long count = Files.list(path).count();

    List<Object> result = new ArrayList<>((int) count);
    for (int i = 0; i < count; i++) {
      result.add(actual(path.resolve(Integer.toString(i)), dataReader));
    }
    return result;
  }

  /**
   * Project dir that has been passed via --project-dir option.
   * It may be "." when current dir is equal to {@link #projectDirAbsolutePath()}.
   */
  public Path projectDirOption() {
    switch (AcceptanceUtils.TEST_MODE) {
      case SINGLE_JVM:
        return projectDir;
      case FULL_BINARY:
        return Paths.get(".");
      default:
        fail("Unknown mode: " + AcceptanceUtils.TEST_MODE);
        return null;
    }
  }

  public String artifactFileContent(String artifact) throws IOException {
    return fileContent(artifactAbsolutePath(artifact));
  }

  public Map<String, String> artifactTreeContent(String artifact) throws IOException {
    Path dir = artifactAbsolutePath(artifact);
    if (!Files.exists(dir)) {
      fail("No such artifact: " + artifact);
    }
    HashMap<String, String> result = new HashMap<>();
    addFilesToMap(dir, Paths.get(""), result);
    return result;
  }

  private static void addFilesToMap(Path rootDir, Path relativePath, HashMap<String, String> result)
      throws IOException {
    try (Stream<Path> stream = Files.list(rootDir.resolve(relativePath))) {
      for (Path path : stream.collect(toList())) {
        Path relative = relativePath.resolve(path.getFileName());
        if (Files.isDirectory(path)) {
          addFilesToMap(rootDir, relative, result);
        } else {
          result.put(relative.toString(), fileContent(path));
        }
      }
    }
  }

  private static String fileContent(Path path) throws IOException {
    return readAndClose(buffer(source(path)), s -> s.readString(CHARSET));
  }

  public Path smoothDirAbsolutePath() {
    return absolutePath(SMOOTH_DIR.toString());
  }

  public Path absolutePath(String path) {
    return projectDirAbsolutePath().resolve(path);
  }

  public Path artifactAbsolutePath(String name) {
    return absolutePath(ARTIFACTS_PATH.appendPart(name).toString());
  }

  /**
   * Absolute path to project dir.
   */
  public Path projectDirAbsolutePath() {
    return projectDir;
  }
}
