package org.smoothbuild.acceptance;

import static org.junit.jupiter.api.Assertions.fail;
import static org.smoothbuild.acceptance.AcceptanceUtils.TestMode.FULL_BINARY;
import static org.smoothbuild.acceptance.AcceptanceUtils.TestMode.SINGLE_JVM;
import static org.smoothbuild.acceptance.SmoothBinary.smoothBinary;

import java.nio.file.Path;

public class AcceptanceUtils {
  public static final TestMode TEST_MODE = getAcceptanceTestMode();
  public static final Path GIT_REPO_ROOT = gitRepoRoot();
  public static final Path SMOOTH_BINARY = smoothBinary(GIT_REPO_ROOT);

  public enum TestMode {
    SINGLE_JVM,
    FULL_BINARY,
  }

  public static TestMode getAcceptanceTestMode() {
    String mode = System.getenv("acceptance-test-mode");
    if (mode == null || mode.equals("single-jvm")) {
      return SINGLE_JVM;
    } else if (mode.equals("full-binary")) {
      return FULL_BINARY;
    } else {
      fail("Unknown mode: " + mode);
      return null;
    }
  }

  /**
   * Returns path to smooth-build git repository on local filesystem.
   */
  public static Path gitRepoRoot() {
    /*
     * ant build script passes git repository root via repository_root env variable. If it's null
     * then we are run from IDE and working dir is repo's root.
     */
    String repositoryDir = System.getenv("repository_dir");
    return repositoryDir == null ? workingDir() : Path.of(repositoryDir);
  }

  private static Path workingDir() {
    return Path.of(".").toAbsolutePath();
  }
}
