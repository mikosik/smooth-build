package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJarFiles;
import static org.smoothbuild.testing.common.JarTester.jarByteString;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.TupleH;
import org.smoothbuild.testing.TestingContext;

public class PackagedJavaFileObjectsTest extends TestingContext {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    TupleH file1 = fileH(path("my/package/MyKlass.class"));
    TupleH file2 = fileH(path("my/package/MyKlass2.class"));
    TupleH jar = fileH("myFile.jar", blobH(jarByteString(file1, file2)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    TupleH file1 = fileH(path(name));
    TupleH jar = fileH("myFile.jar", blobH(jarByteString(file1)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar, jar)))
        .isNull();
    assertThat(nativeApi().messages())
        .isEqualTo(arrayH(errorMessage(
            "File " + name + " is contained by two different library jar files.")));
  }
}
