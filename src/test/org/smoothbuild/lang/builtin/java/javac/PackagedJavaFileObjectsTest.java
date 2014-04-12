package org.smoothbuild.lang.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.builtin.java.javac.PackagedJavaFileObjects.packagedJavaFileObjects;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.lang.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;

public class PackagedJavaFileObjectsTest {

  @Test
  public void test() throws Exception {
    String fileName1 = "my/package/MyKlass.class";
    String fileName2 = "my/package2/MyKlass2.class";
    SFile file = jaredFiles(fileName1, fileName2);

    Multimap<String, JavaFileObject> packageToJavaFileObjects =
        packagedJavaFileObjects(new FakeNativeApi(), ImmutableList.of(file.content()));

    JavaFileObject fileObject = packageToJavaFileObjects.get("my.package").iterator().next();
    StreamTester.assertContent(fileObject.openInputStream(), fileName1);
    assertThat(fileObject.getName()).isEqualTo("/:my/package/MyKlass.class");
  }

  @Test
  public void duplicateClassFileException() throws Exception {
    String fileName = "my/package/MyKlass.class";
    SFile file1 = jaredFiles(fileName);
    SFile file2 = jaredFiles(fileName);

    try {
      packagedJavaFileObjects(new FakeNativeApi(), ImmutableList.of(file1.content(), file2
          .content()));
      fail("exception should be thrown");
    } catch (DuplicateClassFileError e) {
      // expected
    }
  }
}
