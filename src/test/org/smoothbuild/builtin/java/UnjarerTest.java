package org.smoothbuild.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.fs.base.Path.path;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import java.util.Iterator;

import org.junit.Test;
import org.smoothbuild.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.type.impl.TestFile;
import org.smoothbuild.testing.type.impl.TestFileSet;
import org.smoothbuild.type.api.File;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class UnjarerTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  TestFileSet resultFileSet = new TestFileSet();
  Unjarer unjarer = new Unjarer();

  @Test
  public void unjaringTwoFiles() throws Exception {
    TestFile jarFile = jaredFiles(fileName1, fileName2);

    unjarer.unjarFile(jarFile, new FileSetBuilder(resultFileSet));

    int fileCount = 0;
    for (File file : resultFileSet) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unjaringIgnoresDirectories() throws Exception {
    TestFile jarFile = jaredFiles(fileName1, directoryName);

    unjarer.unjarFile(jarFile, new FileSetBuilder(resultFileSet));

    assertThat(Iterables.size(resultFileSet)).isEqualTo(1);
    assertThat(resultFileSet.iterator().next().path()).isEqualTo(path(fileName1));
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
      unjarer.unjarFile(jarFile, new FileSetBuilder(resultFileSet));
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IllegalPathInJarError.class);
    }
  }
}
