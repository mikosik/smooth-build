package org.smoothbuild.testing.common;

import static org.smoothbuild.fs.base.RecursiveDirectoryDeleter.deleteRecursively;

import java.io.File;
import java.io.IOException;

import org.junit.After;

import com.google.common.io.Files;

/**
 * TestCaseWithTempDir provides easy way for creating temporary files in junit
 * tests. Just make your test class extend TestCaseWithTempDir - It will
 * automatically create temporary directory for you when you call
 * getTempDirectory() and delete its content at the end of test.
 */
public class TestCaseWithTempDir {
  private File tempDirectory;

  public File getTempDirectory() {
    if (tempDirectory == null) {
      tempDirectory = Files.createTempDir();
    }
    return tempDirectory;
  }

  @After
  public void after() throws IOException {
    deleteRecursively(tempDirectory);
  }
}
