package org.smoothbuild.slib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.slib.java.javac.PackagedJavaFileObjects.classesFromJarFiles;
import static org.smoothbuild.testing.common.JarTester.jarByteString;
import static org.smoothbuild.util.collect.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.db.object.obj.val.Struc_;
import org.smoothbuild.testing.TestingContext;

public class PackagedJavaFileObjectsTest extends TestingContext {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    Struc_ file1 = file(path("my/package/MyKlass.class"));
    Struc_ file2 = file(path("my/package/MyKlass2.class"));
    Struc_ jar = file("myFile.jar", blob(jarByteString(file1, file2)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    Struc_ file1 = file(path(name));
    Struc_ jar = file("myFile.jar", blob(jarByteString(file1)));
    assertThat(classesFromJarFiles(nativeApi(), list(jar, jar)))
        .isNull();
    assertThat(nativeApi().messages())
        .isEqualTo(array(errorMessage(
            "File " + name + " is contained by two different library jar files.")));
  }
}
