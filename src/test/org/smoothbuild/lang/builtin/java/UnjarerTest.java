package org.smoothbuild.lang.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import org.junit.Test;
import org.smoothbuild.lang.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.lang.type.Array;
import org.smoothbuild.lang.type.File;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class UnjarerTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakeSandbox sandbox = new FakeSandbox();
  Unjarer unjarer = new Unjarer(sandbox);

  @Test
  public void unjaringTwoFiles() throws Exception {
    FakeFile jarFile = jaredFiles(fileName1, fileName2);

    Array<File> fileArray = unjarer.unjarFile(jarFile);

    int fileCount = 0;
    for (File file : fileArray) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unjaringIgnoresDirectories() throws Exception {
    FakeFile jarFile = jaredFiles(fileName1, directoryName);

    Array<File> fileArray = unjarer.unjarFile(jarFile);

    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    assertThat(fileArray.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void unjaringWithFilter() throws Exception {
    FakeFile jarFile = jaredFiles(fileName1, fileName2);

    Array<File> fileArray = unjarer.unjarFile(jarFile, Predicates.equalTo(fileName2));

    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    assertThat(fileArray.iterator().next().path().value()).isEqualTo(fileName2);
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    FakeFile jarFile = jaredFiles(illegalFileName);

    try {
      unjarer.unjarFile(jarFile);
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IllegalPathInJarError.class);
    }
  }
}
