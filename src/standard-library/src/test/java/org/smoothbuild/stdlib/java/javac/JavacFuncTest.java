package org.smoothbuild.stdlib.java.javac;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.bucket.base.Path.path;
import static org.smoothbuild.stdlib.java.javac.JavacFunc.filesToInputClassFiles;

import org.junit.jupiter.api.Test;
import org.smoothbuild.virtualmachine.testing.VmTestContext;

public class JavacFuncTest extends VmTestContext {
  @Test
  void files_from_library_jars_are_accessible_as_java_objects() throws Exception {
    var file1 = bFile(path("my/package/MyKlass.class"));
    var file2 = bFile(path("my/package/MyKlass2.class"));
    var fileArrayArray = bArray(bArray(file1, file2));
    assertThat(filesToInputClassFiles(nativeApi(), fileArrayArray))
        .containsExactly(new InputClassFile(file1), new InputClassFile(file2));
  }

  @Test
  void duplicate_class_file_exception() throws Exception {
    var name = "my/package/MyKlass.class";
    var file1 = bFile(path(name));
    var fileArrayArray = bArray(bArray(file1), bArray(file1));
    var nativeApi = nativeApi();
    assertThat(filesToInputClassFiles(nativeApi, fileArrayArray)).isNull();
    assertThat(nativeApi.messages())
        .isEqualTo(bArray(
            bErrorLog("File " + name + " is contained by two different library jar files.")));
  }
}
