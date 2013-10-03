package org.smoothbuild.type.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;
import static org.smoothbuild.type.impl.ImmutableFile.immutableFile;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.File;
import org.smoothbuild.type.api.MutableFile;
import org.smoothbuild.type.impl.StoredFile;

public class ImmutableFileTest {
  TestFileSystem fileSystem = new TestFileSystem();
  Path filePath = path("xyz/test.txt");

  File file = immutableFile(new StoredFile(fileSystem, filePath));

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
    fileSystem.createFileContainingItsPath(filePath);
    assertContentHasFilePath(file);
  }

  public static void assertContentHasFilePath(File file) throws IOException, FileNotFoundException {
    assertContent(file.openInputStream(), file.path().value());
  }
}
