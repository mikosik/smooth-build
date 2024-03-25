package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.stdlib.java.javac.JavacFunc.classesFromJarFiles;
import static org.smoothbuild.virtualmachine.testing.JarTester.jarByteString;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BTuple;
import org.smoothbuild.virtualmachine.testing.TestingVirtualMachine;

public class JavacFuncTest extends TestingVirtualMachine {
  @Test
  public void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    BTuple file1 = bFile(path("my/package/MyKlass.class"));
    BTuple file2 = bFile(path("my/package/MyKlass2.class"));
    BTuple jar = bFile("myFile.jar", bBlob(jarByteString(file1, file2)));
    assertThat(classesFromJarFiles(nativeApi(), bArray(jar)))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  public void duplicate_class_file_exception() throws Exception {
    String name = "my/package/MyKlass.class";
    BTuple file1 = bFile(path(name));
    BTuple jar = bFile("myFile.jar", bBlob(jarByteString(file1)));
    var nativeApi = nativeApi();
    assertThat(classesFromJarFiles(nativeApi, bArray(jar, jar))).isNull();
    assertThat(nativeApi.messages())
        .isEqualTo(bArray(
            bErrorLog("File " + name + " is contained by two different library jar files.")));
  }
}
