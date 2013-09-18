package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.plugin.api.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Iterator;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;

import org.junit.Test;
import org.smoothbuild.builtin.java.err.IllegalPathInJarException;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.testing.plugin.internal.TestFile;
import org.smoothbuild.testing.plugin.internal.TestFileSet;

import com.google.common.base.Predicates;

public class UnjarerTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";

  TestFileSet resultFileSet = new TestFileSet();
  Unjarer unjarer = new Unjarer();

  @Test
  public void unjaring() throws Exception {
    TestFile jarFile = jaredFiles(fileName1, fileName2);

    unjarer.unjarFile(jarFile, resultFileSet);

    int fileCount = 0;
    for (File file : resultFileSet) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unjaringWithFilter() throws Exception {
    TestFile jarFile = jaredFiles(fileName1, fileName2);

    unjarer.unjarFile(jarFile, Predicates.equalTo(fileName2), resultFileSet);

    Iterator<File> it = resultFileSet.iterator();
    File file = it.next();
    assertThat(file.path().value()).isEqualTo(fileName2);
    assertThat(it.hasNext()).isFalse();
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    TestFile jarFile = jaredFiles(illegalFileName);

    try {
      unjarer.unjarFile(jarFile, resultFileSet);
      fail("exception should be thrown");
    } catch (IllegalPathInJarException e) {
      // expected
      assertThat(e.fileName()).isEqualTo(illegalFileName);
    }
  }

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
