package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.util.Iterator;

import org.junit.Test;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.PluginErrorException;
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
    } catch (PluginErrorException e) {
      // expected
      assertThat(e.error()).isInstanceOf(IllegalPathInJarError.class);
    }
  }
}
