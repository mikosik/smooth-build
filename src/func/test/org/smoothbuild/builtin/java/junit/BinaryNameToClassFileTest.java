package org.smoothbuild.builtin.java.junit;

import static java.util.Arrays.asList;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.ImmutableMap;

public class BinaryNameToClassFileTest {
  private final FakeNativeApi nativeApi = new FakeNativeApi();
  private Blob blob;
  private SFile file1;
  private SFile file2;

  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    given(file1 = nativeApi.file(path("a/Klass.class")));
    given(file2 = nativeApi.file(path("b/Klass.class")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(nativeApi, asList(blob)));
    thenReturned(ImmutableMap.of("a.Klass", file1, "b.Klass", file2));
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    given(file1 = nativeApi.file(path("a/Klass.txt")));
    given(file2 = nativeApi.file(path("b/Klass.java")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(nativeApi, asList(blob)));
    thenReturned(ImmutableMap.of());
  }
}
