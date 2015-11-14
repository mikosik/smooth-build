package org.smoothbuild.testing.common;

import static org.smoothbuild.io.fs.disk.RecursiveDeleter.deleteRecursively;

import java.io.File;
import java.io.IOException;

import org.junit.After;

import com.google.common.io.Files;

/**
 * TestCaseWithTempDir provides easy way for creating temporary files in junit
 * tests. Just make your test class extend TestCaseWithTempDir - It will
 * automatically create temporary dir for you when you call getTempDir() and
 * delete its content at the end of test.
 */
public class TestCaseWithTempDir {
  private File tempDir;

  public File getTempDir() {
    if (tempDir == null) {
      tempDir = Files.createTempDir();
    }
    return tempDir;
  }

  @After
  public void after() throws IOException {
    deleteRecursively(tempDir.toPath());
  }
}
