package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.builtin.java.javac.LibraryClasses.libraryClasses;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.smoothbuild.builtin.java.javac.err.DuplicateClassFileError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.PluginErrorException;
import org.smoothbuild.testing.common.StreamTester;

import com.google.common.collect.ImmutableList;

public class LibraryClassesTest {

  @Test
  public void test() throws Exception {
    String fileName1 = "my/package/MyKlass.class";
    String fileName2 = "my/package2/MyKlass2.class";
    File file = jaredFiles(fileName1, fileName2);

    LibraryClasses libraryClasses = libraryClasses(ImmutableList.of(file));

    JavaFileObject fileObject = libraryClasses.classesInPackage("my.package").iterator().next();
    StreamTester.assertContent(fileObject.openInputStream(), fileName1);
    assertThat(fileObject.getName()).isEqualTo("/input.jar:my/package/MyKlass.class");
  }

  @Test
  public void duplicateClassFileException() throws Exception {
    String fileName = "my/package/MyKlass.class";
    File file1 = jaredFiles(fileName);
    File file2 = jaredFiles(fileName);

    try {
      libraryClasses(ImmutableList.of(file1, file2));
      fail("exception should be thrown");
    } catch (PluginErrorException e) {
      assertThat(e.error()).isInstanceOf(DuplicateClassFileError.class);
      // expected
    }
  }
}
