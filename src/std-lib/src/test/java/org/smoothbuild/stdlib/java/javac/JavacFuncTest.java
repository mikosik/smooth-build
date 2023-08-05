package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.filesystem.base.PathS.path;
import static org.smoothbuild.stdlib.java.javac.JavacFunc.classesFromJarFiles;
import static org.smoothbuild.testing.common.JarTester.jarByteString;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.vm.bytecode.expr.value.TupleB;

public class JavacFuncTest extends TestContext {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    TupleB file1 = fileB(path("my/package/MyKlass.class"));
    TupleB file2 = fileB(path("my/package/MyKlass2.class"));
    TupleB jar = fileB("myFile.jar", blobB(jarByteString(file1, file2)));
    assertThat(classesFromJarFiles(nativeApi(), arrayB(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    TupleB file1 = fileB(path(name));
    TupleB jar = fileB("myFile.jar", blobB(jarByteString(file1)));
    var nativeApi = nativeApi();
    assertThat(classesFromJarFiles(nativeApi, arrayB(jar, jar)))
        .isNull();
    assertThat(nativeApi.messages())
        .isEqualTo(arrayB(errorMessage(
            "File " + name + " is contained by two different library jar files.")));
  }
}
