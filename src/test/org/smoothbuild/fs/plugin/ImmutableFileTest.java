package org.smoothbuild.fs.plugin;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.plugin.ImmutableFile.immutableFile;
import static org.smoothbuild.plugin.Path.path;
import static org.smoothbuild.testing.TestingStream.assertContent;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.SubFileSystem;
import org.smoothbuild.plugin.File;
import org.smoothbuild.plugin.MutableFile;
import org.smoothbuild.plugin.Path;
import org.smoothbuild.testing.TestingFileSystem;

public class ImmutableFileTest {
  TestingFileSystem fileSystem = new TestingFileSystem();
  Path root = path("abc/efg");
  Path filePath = path("xyz/test.txt");

  File file = immutableFile(new StoredFile(new SubFileSystem(fileSystem, root), filePath));

  @Test
  public void immutableFileIsNotMutable() throws Exception {
    assertThat(file).isNotInstanceOf(MutableFile.class);
  }

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

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertContent(file.createInputStream(), file.path().value());
  }
}
