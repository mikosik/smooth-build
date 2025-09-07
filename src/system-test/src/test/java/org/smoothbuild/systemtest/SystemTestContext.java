package org.smoothbuild.systemtest;

import static com.google.common.collect.ObjectArrays.concat;
import static java.nio.file.Files.createDirectories;
import static java.util.Locale.ROOT;
import static org.smoothbuild.cli.layout.Layout.DEFAULT_MODULE_PATH;
import static org.smoothbuild.common.Constants.CHARSET;
import static org.smoothbuild.common.ExecuteOsProcess.executeOsProcess;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.filesystem.disk.RecursiveDeleter.deleteRecursively;
import static org.smoothbuild.common.reflect.Classes.saveBytecodeInJar;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.cleanCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.helpCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.listCommand;
import static org.smoothbuild.systemtest.CommandWithArgs.versionCommand;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.ExecutionException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.io.TempDir;

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

  public SystemTestOutput runSmoothBuild(String... args) {
    return runSmooth(buildCommand(args));
  }

  public SystemTestOutput runSmoothClean(String... args) {
    return runSmooth(cleanCommand(args));
  }

  public SystemTestOutput runSmoothHelp(String... args) {
    return runSmoothWithoutProjectAndInstallationDir(helpCommand(args));
  }

  public SystemTestOutput runSmoothList(String... args) {
    return runSmooth(listCommand(args));
  }

  public SystemTestOutput runSmoothVersion(String... args) {
    return runSmoothWithoutProjectDir(versionCommand(args));
  }

  public SystemTestOutput runSmoothWithoutProjectAndInstallationDir(CommandWithArgs command) {
    return runSmoothInForkedJvm(command);
  }

  public SystemTestOutput runSmoothWithoutProjectDir(CommandWithArgs command) {
    return runSmoothInForkedJvm(command);
  }

  public SystemTestOutput runSmooth(CommandWithArgs command) {
    return runSmoothInForkedJvm(command);
  }

  private SystemTestOutput runSmoothInForkedJvm(CommandWithArgs command) {
    try {
      String[] allArgs = processArgs(command.commandPlusArgs());
      Path workingDir = projectDirAbsolutePath();
      var processResult = executeOsProcess(workingDir, allArgs);
      return new SystemTestOutput(
          processResult.exitCode(), processResult.systemOut(), processResult.systemErr());
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

  public Path smoothDirAbsolutePath() {
    return absolutePath(SMOOTH_DIR.toString());
  }

  public Path absolutePath(String path) {
    return projectDirAbsolutePath().resolve(path);
  }

  /**
   * Absolute path to project dir.
   */
  public Path projectDirAbsolutePath() {
    return projectDir;
  }
}
