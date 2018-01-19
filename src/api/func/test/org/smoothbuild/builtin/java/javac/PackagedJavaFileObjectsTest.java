package org.smoothbuild.builtin.java.javac;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.smoothbuild.builtin.java.javac.PackagedJavaFileObjects.classesFromJars;
import static org.smoothbuild.db.values.ValuesDb.memoryValuesDb;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.task.exec.Container.container;
import static org.smoothbuild.testing.common.JarTester.jar;
import static org.smoothbuild.testing.db.values.ValueCreators.file;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.plugin.AbortException;
import org.smoothbuild.lang.plugin.NativeApi;
import org.smoothbuild.lang.value.Blob;
import org.smoothbuild.lang.value.Struct;

public class PackagedJavaFileObjectsTest {
  private final NativeApi nativeApi = container();
  private Struct file1;
  private Struct file2;
  private Blob jar;

  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    given(file1 = file(memoryValuesDb(), path("my/package/MyKlass.class")));
    given(file2 = file(memoryValuesDb(), path("my/package/MyKlass2.class")));
    given(jar = jar(file1, file2));
    when(classesFromJars(nativeApi, asList(jar)));
    thenReturned(containsInAnyOrder(new InputClassFile(file1), new InputClassFile(file2)));
  }

  @Test
  public void duplicateClassFileException() throws Exception {
    given(file1 = file(memoryValuesDb(), path("my/package/MyKlass.class")));
    given(jar = jar(file1));
    when(() -> classesFromJars(nativeApi, asList(jar, jar)));
    thenThrown(AbortException.class);
  }
}
