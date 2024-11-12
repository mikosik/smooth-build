package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static com.google.common.truth.Truth.assertWithMessage;
import static java.nio.file.Files.createDirectories;
import static java.util.Locale.ROOT;
import static okio.Okio.buffer;
import static okio.Okio.source;
import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.cli.Main.EXIT_CODE_ERROR;
import static org.smoothbuild.cli.Main.EXIT_CODE_SUCCESS;
import static org.smoothbuild.cli.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.ExecuteOsProcess.executeOsProcess;
import static org.smoothbuild.common.base.Strings.convertOsLineSeparatorsToNewLine;
import static org.smoothbuild.common.base.Strings.unlines;
import static org.smoothbuild.common.bucket.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.helpCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.listCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.stream.Stream;
import okio.BufferedSource;
import okio.ByteString;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;
import org.smoothbuild.common.function.Function1;

public abstract class SystemTestContext {
  public static final Path SMOOTH_DIR = Paths.get(".smooth");
  public static final Path COMPUTATION_DB_PATH = SMOOTH_DIR.resolve("computations");
  public static final Path BYTECODE_DB_PATH = SMOOTH_DIR.resolve("bytecode");
  public static final Path ARTIFACTS_PATH = SMOOTH_DIR.resolve("artifacts");
  public static final Path SMOOTH_BINARY = findSmoothBinary();

  private static Path findSmoothBinary() {
    var osName = System.getProperty("os.name").toUpperCase(ROOT);
    if (osName.startsWith("LINUX") || osName.startsWith("MAC OS")) {
      return Paths.get("./build/installation/smooth/bin/smooth").toAbsolutePath();
    } else {
      return Paths.get("./build/installation/smooth/bin/smooth.bat").toAbsolutePath();
    }
  }

  private Path projectDir;
  private Integer exitCode;
  private String systemOut;
  private String systemErr;

  @BeforeEach
  public void init(@TempDir Path projectDir) {
    this.projectDir = projectDir;
  }

  @AfterEach
  public void destroy() throws IOException {
    deleteRecursively(projectDirAbsolutePath());
  }

  public void createUserModule(String code) throws IOException {
    createFile(DEFAULT_MODULE_PATH.toString(), code);
  }

  public void createFile(String path, String content) throws IOException {
    Path fullPath = absolutePath(path);
    createDirectories(fullPath.getParent());
    try (FileWriter writer = new FileWriter(fullPath.toString(), CHARSET)) {
      writer.write(content);
    }
  }

  public void createNativeJar(Class<?>... classes) throws IOException {
    saveBytecodeInJar(absolutePath("build.jar"), list(classes));
  }

  public void createDir(String path) throws IOException {
    createDirectories(absolutePath(path));
  }

  public void runSmoothBuild(String... args) {
    runSmooth(buildCommand(args));
  }

  public void runSmoothClean(String... args) {
    runSmooth(cleanCommand(args));
  }

  public void runSmoothHelp(String... args) {
    runSmoothWithoutProjectAndInstallationDir(helpCommand(args));
  }

  public void runSmoothList(String... args) {
    runSmooth(listCommand(args));
  }

  public void runSmoothVersion(String... args) {
    runSmoothWithoutProjectDir(versionCommand(args));
  }

  public void runSmoothWithoutProjectAndInstallationDir(CommandWithArgs command) {
    runSmoothInForkedJvm(command);
  }

  public void runSmoothWithoutProjectDir(CommandWithArgs command) {
    runSmoothInForkedJvm(command);
  }

  public void runSmooth(CommandWithArgs command) {
    runSmoothInForkedJvm(command);
  }

  private void runSmoothInForkedJvm(CommandWithArgs command) {
    try {
      String[] allArgs = processArgs(command.commandPlusArgs());
      Path workingDir = projectDirAbsolutePath();
      var processResult = executeOsProcess(workingDir, allArgs);
      exitCode = processResult.exitCode();
      systemOut = processResult.systemOut();
      systemErr = processResult.systemErr();
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

  public void assertFinishedWithSuccess() {
    assertReturnedCode(EXIT_CODE_SUCCESS);
  }

  public void assertFinishedWithError() {
    assertReturnedCode(EXIT_CODE_ERROR);
  }

  private void assertReturnedCode(int expected) {
    if (expected != exitCode) {
      fail("Expected return code " + expected + " but was " + exitCode + ".\n"
          + "standard out:\n" + systemOut + "\n"
          + "standard err:\n" + systemErr + "\n");
    }
  }

  public int exitCode() {
    return exitCode;
  }

  public void assertSystemOutContains(String text) {
    assertWithFullOutputs(systemOut, text, "SystemOut");
  }

  public void assertSystemErrContains(String text) {
    assertWithFullOutputs(systemErr, text, "SystemErr");
  }

  private void assertWithFullOutputs(String out, String text, String outName) {
    var convertedOut = convertOsLineSeparatorsToNewLine(out);
    if (!convertedOut.contains(text)) {
      assertWithMessage(unlines(
              outName + " doesn't contain expected substring.",
              "================= SYSTEM-OUT START ====================",
              systemOut,
              "================= SYSTEM-OUT END   ====================",
              "================= SYSTEM-ERR START ====================",
              systemErr,
              "================= SYSTEM-ERR END   ===================="))
          .that(convertedOut)
          .isEqualTo(text);
    }
  }

  public void assertSystemOutDoesNotContain(String text) {
    assertWithMessage(unlines(
            "SystemOut contains forbidden substring",
            "================= SYSTEM-OUT START ====================",
            systemOut,
            "================= SYSTEM-OUT END   ====================",
            "================= SYSTEM-ERR START ====================",
            systemErr,
            "================= SYSTEM-ERR END   ===================="))
        .that(convertOsLineSeparatorsToNewLine(systemOut))
        .doesNotContain(text);
  }

  public String systemOut() {
    return convertOsLineSeparatorsToNewLine(systemOut);
  }

  public String systemErr() {
    return convertOsLineSeparatorsToNewLine(systemErr);
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
      try (var source = buffer(source(artifactAbsolutePath(name)))) {
        var byteString = source.readByteString();
        if (byteString.size() != 1) {
          throw new RuntimeException("Expected boolean artifact but got " + byteString);
        }
        return switch (byteString.getByte(0)) {
          case 0 -> false;
          case 1 -> true;
          default -> throw new RuntimeException("Expected boolean artifact but got " + byteString);
        };
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  public Integer artifactAsInt(String name) {
    try {
      try (var bufferedSource = buffer(source(artifactAbsolutePath(name)))) {
        var byteString = bufferedSource.readByteString();
        if (4 < byteString.size()) {
          throw new RuntimeException(
              "Expected int artifact but got too many bytes: " + byteString.size() + " .");
        }
        return new BigInteger(byteString.toByteArray()).intValue();
      }
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * Returns content of artifact as string
   * or (when artifact is an array) returns list containing stringified elems.
   * Works with array artifacts of any depth.
   */
  public Object artifactStringified(String name) {
    try {
      return actual(artifactAbsolutePath(name), s -> s.readString(CHARSET));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static Object actual(Path path, Function1<BufferedSource, Object, IOException> dataReader)
      throws IOException {
    if (!Files.exists(path)) {
      return null;
    }
    if (Files.isDirectory(path)) {
      return actualArray(path, dataReader);
    }
    try (var bufferedSource = buffer(source(path))) {
      return dataReader.apply(bufferedSource);
    }
  }

  private static Object actualArray(
      Path path, Function1<BufferedSource, Object, IOException> dataReader) throws IOException {
    try (Stream<Path> pathStream = Files.list(path)) {
      long count = pathStream.count();
      List<Object> result = new ArrayList<>((int) count);
      for (int i = 0; i < count; i++) {
        result.add(actual(path.resolve(Integer.toString(i)), dataReader));
      }
      return result;
    }
  }

  public String artifactAsString(String artifact) throws IOException {
    return fileContentAsString(artifactAbsolutePath(artifact));
  }

  public Map<String, String> artifactTreeContentAsStrings(String artifact) throws IOException {
    Path dir = artifactAbsolutePath(artifact);
    if (!Files.exists(dir)) {
      fail("No such artifact: " + artifact);
    }
    HashMap<String, String> result = new HashMap<>();
    addFilesToMap(dir, Path.of(""), result);
    return result;
  }

  private static void addFilesToMap(Path rootDir, Path relativePath, HashMap<String, String> result)
      throws IOException {
    try (Stream<Path> stream = Files.list(rootDir.resolve(relativePath))) {
      for (Path path : stream.toList()) {
        Path relative = relativePath.resolve(path.getFileName());
        if (Files.isDirectory(path)) {
          addFilesToMap(rootDir, relative, result);
        } else {
          result.put(relative.toString().replace('\\', '/'), fileContentAsString(path));
        }
      }
    }
  }

  private static String fileContentAsString(Path path) throws IOException {
    try (var bufferedSource = buffer(source(path))) {
      return bufferedSource.readString(CHARSET);
    }
  }

  public ByteString artifactAsByteString(String artifact) throws IOException {
    return fileContentAsByteString(artifactAbsolutePath(artifact));
  }

  public static ByteString fileContentAsByteString(Path path) throws IOException {
    try (var bufferedSource = buffer(source(path))) {
      return bufferedSource.readByteString();
    }
  }

  public Path smoothDirAbsolutePath() {
    return absolutePath(SMOOTH_DIR.toString());
  }

  public Path absolutePath(String path) {
    return projectDirAbsolutePath().resolve(path);
  }

  public Path artifactAbsolutePath(String name) {
    return absolutePath(ARTIFACTS_PATH.resolve(name).toString());
  }

  /**
   * Absolute path to project dir.
   */
  public Path projectDirAbsolutePath() {
    return projectDir;
  }
}
