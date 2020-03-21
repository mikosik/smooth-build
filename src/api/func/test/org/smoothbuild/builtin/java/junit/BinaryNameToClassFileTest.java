package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.smoothbuild.lang.object.base.Blob;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.testing.TestingContext;
import org.smoothbuild.testing.common.JarTester;

public class BinaryNameToClassFileTest extends TestingContext {
  private Blob blob;
  private Struct file1;
  private Struct file2;

  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    given(file1 = file(path("a/Klass.class")));
    given(file2 = file(path("b/Klass.class")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(nativeApi(), list(blob)));
    thenReturned(mapOf("a.Klass", file1, "b.Klass", file2));
  }

  private static Map<String, Struct> mapOf(String name1, Struct file1, String name2, Struct file2) {
    HashMap<String, Struct> result = new HashMap<>();
    result.put(name1, file1);
    result.put(name2, file2);
    return result;
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    given(file1 = file(path("a/Klass.txt")));
    given(file2 = file(path("b/Klass.java")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(nativeApi(), list(blob)).entrySet());
    thenReturned(Matchers.empty());
  }
}
