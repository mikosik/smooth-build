package org.smoothbuild.testing;

import static com.google.common.base.Preconditions.checkArgument;

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

  public static void deleteRecursively(File file) throws IOException {
    if (file.isDirectory()) {
      deleteDirectoryContents(file);
    }
    if (!file.delete()) {
      throw new IOException("Failed to delete " + file);
    }
  }

  public static void deleteDirectoryContents(File directory) throws IOException {
    checkArgument(directory.isDirectory(), "Not a directory: %s", directory);
    // Symbolic links will have different canonical and absolute paths
    if (!directory.getCanonicalPath().equals(directory.getAbsolutePath())) {
      return;
    }
    File[] files = directory.listFiles();
    if (files == null) {
      throw new IOException("Error listing files for " + directory);
    }
    for (File file : files) {
      deleteRecursively(file);
    }
  }
}
