package org.smoothbuild.slib;

import java.nio.file.Path;
import java.nio.file.Paths;

public class GitRepo {
  /**
   * Returns path to smooth-build git repository on local filesystem.
   */
  public static Path gitRepoRoot() {
    /*
     * ant build script passes git repository root via repository_root env variable. If it's null
     * then we are run from IDE and working dir is repo's root.
     */
    String repositoryDir = System.getenv("repository_dir");
    return repositoryDir == null ? workingDir() : Paths.get(repositoryDir);
  }

  private static Path workingDir() {
    return Paths.get(".").toAbsolutePath();
  }
}
