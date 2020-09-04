package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJars;
import static org.smoothbuild.testing.common.JarTester.jar;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.base.Blob;
import org.smoothbuild.db.object.base.Tuple;
import org.smoothbuild.testing.TestingContext;

public class PackagedJavaFileObjectsTest extends TestingContext {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    Tuple file1 = file(path("my/package/MyKlass.class"));
    Tuple file2 = file(path("my/package/MyKlass2.class"));
    Blob jar = jar(file1, file2);
    assertThat(classesFromJars(nativeApi(), list(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    Tuple file1 = file(path(name));
    Blob jar = jar(file1);
    assertThat(classesFromJars(nativeApi(), list(jar, jar)))
        .isNull();
    assertThat(nativeApi().messages())
        .isEqualTo(array(errorMessage(
            "File " + name + " is contained by two different library jar files.")));
  }
}
