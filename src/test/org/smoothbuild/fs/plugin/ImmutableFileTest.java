package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.ImmutableFile.immutableFile;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.assertContent;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class ImmutableFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = path("abc/efg");
  Path filePath = path("xyz/test.txt");

  File file = immutableFile(new FileImpl(fileSystem, root, filePath));

  @Test
  public void convertingImmutableToImmutableReturnsTheSameObject() throws Exception {
    assertThat(immutableFile(file)).isSameAs(file);
  }

  @Test
  public void testPath() throws Exception {
    assertThat(file.path()).isEqualTo(filePath);
  }

  @Test
  public void createInputStream() throws Exception {
    fileSystem.createFileContainingItsPath(root, filePath);
    assertContentHasFilePath(file);
  }

  @Test
  public void createOutputStream() throws Exception {
    try {
      file.createOutputStream();
      Assert.fail("exception should be thrown");
    } catch (UnsupportedOperationException e) {
      // expected
    }
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertContent(file.createInputStream(), file.path().value());
  }
}
