package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.builtin.java.javac.PackagedJavaFileObjects.packagedJavaFileObjects;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.lang.function.value.File;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjectsTest {

  @Test
  public void test() throws Exception {
    String fileName1 = "my/package/MyKlass.class";
    String fileName2 = "my/package2/MyKlass2.class";
    File file = jaredFiles(fileName1, fileName2);

    Multimap<String, JavaFileObject> packageToJavaFileObjects = packagedJavaFileObjects(
        new FakeSandbox(), ImmutableList.of(file));

    JavaFileObject fileObject = packageToJavaFileObjects.get("my.package").iterator().next();
    StreamTester.assertContent(fileObject.openInputStream(), fileName1);
    assertThat(fileObject.getName()).isEqualTo("/input.jar:my/package/MyKlass.class");
  }

  @Test
  public void duplicateClassFileException() throws Exception {
    String fileName = "my/package/MyKlass.class";
    File file1 = jaredFiles(fileName);
    File file2 = jaredFiles(fileName);

    try {
      packagedJavaFileObjects(new FakeSandbox(), ImmutableList.of(file1, file2));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(DuplicateClassFileError.class);
    }
  }
}
