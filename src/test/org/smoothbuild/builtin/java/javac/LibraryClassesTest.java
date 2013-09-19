package org.smoothbuild.builtin.java.javac;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.builtin.java.javac.LibraryClasses.libraryClasses;
import static org.smoothbuild.plugin.api.Path.path;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import javax.tools.JavaFileObject;

import org.junit.Test;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

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
  public void duplicatedClassFileException() throws Exception {
    String fileName = "my/package/MyKlass.class";
    File file1 = jaredFiles(fileName);
    File file2 = jaredFiles(fileName);

    try {
      libraryClasses(ImmutableList.of(file1, file2));
      fail("exception should be thrown");
    } catch (DuplicatedClassFileException e) {
      // expected
    }
  }

  // TODO methods copy pasted from UnjarerTest
  private static TestFile jaredFiles(String... fileNames) throws IOException {
    TestFile inputFile = new TestFileSet().createFile(path("input.jar"));

    try (JarOutputStream jarOutputStream = new JarOutputStream(inputFile.openOutputStream());) {
      for (String fileName : fileNames) {
        addEntry(jarOutputStream, fileName);
      }
    }
    return inputFile;
  }

  private static void addEntry(JarOutputStream jarOutputStream, String fileName) throws IOException {
    JarEntry entry = new JarEntry(fileName);
    jarOutputStream.putNextEntry(entry);

    OutputStreamWriter writer = new OutputStreamWriter(jarOutputStream);
    writer.write(fileName);
    writer.flush();

    jarOutputStream.closeEntry();
  }
}
