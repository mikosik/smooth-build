package org.smoothbuild.builtin.java.junit;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Lists.list;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.JarTester;

public class BinaryNameToClassFileTest extends TestingContext {
  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    Struct file1 = file(path("a/Klass.class"));
    Struct file2 = file(path("b/Klass.class"));
    Blob blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)))
        .containsExactly("a.Klass", file1, "b.Klass", file2);
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    Struct file1 = file(path("a/Klass.txt"));
    Struct file2 = file(path("b/Klass.java"));
    Blob blob = JarTester.jar(file1, file2);
    assertThat(binaryNameToClassFile(nativeApi(), list(blob)).entrySet())
        .isEmpty();
  }
}
