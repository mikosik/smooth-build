package org.smoothbuild.builtin.java.junit;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.ContainerImpl.containerImpl;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.plugin.Container;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.SFile;
import org.smoothbuild.testing.common.JarTester;

public class BinaryNameToClassFileTest {
  private final Container container = containerImpl();
  private Blob blob;
  private SFile file1;
  private SFile file2;

  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    given(file1 = file(memoryValuesDb(), path("a/Klass.class")));
    given(file2 = file(memoryValuesDb(), path("b/Klass.class")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(container, asList(blob)));
    thenReturned(mapOf("a.Klass", file1, "b.Klass", file2));
  }

  private static Map<String, SFile> mapOf(String name1, SFile file1, String name2, SFile file2) {
    HashMap<String, SFile> result = new HashMap<>();
    result.put(name1, file1);
    result.put(name2, file2);
    return result;
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    given(file1 = file(memoryValuesDb(), path("a/Klass.txt")));
    given(file2 = file(memoryValuesDb(), path("b/Klass.java")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(container, asList(blob)).entrySet());
    thenReturned(empty());
  }
}
