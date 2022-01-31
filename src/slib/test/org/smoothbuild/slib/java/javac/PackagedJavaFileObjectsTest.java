package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.PathS.path;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJarFiles;
import static org.smoothbuild.testing.common.JarTester.jarByteString;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.bytecode.obj.val.TupleB;
import org.smoothbuild.testing.TestingContext;

public class PackagedJavaFileObjectsTest extends TestingContext {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    TupleB file1 = fileB(path("my/package/MyKlass.class"));
    TupleB file2 = fileB(path("my/package/MyKlass2.class"));
    TupleB jar = fileB("myFile.jar", blobB(jarByteString(file1, file2)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    TupleB file1 = fileB(path(name));
    TupleB jar = fileB("myFile.jar", blobB(jarByteString(file1)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar, jar)))
        .isNull();
    assertThat(nativeApi().messages())
        .isEqualTo(arrayB(errorMessage(
            "File " + name + " is contained by two different library jar files.")));
  }
}
