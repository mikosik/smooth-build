package org.smoothbuild.lang.builtin.java;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.common.StreamTester.assertContent;

import org.junit.Test;
import org.smoothbuild.lang.builtin.java.err.IllegalPathInJarError;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SBlob;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.message.listen.ErrorMessageException;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.task.exec.FakePluginApi;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

public class UnjarerTest {
  String fileName1 = "file/path/file1.txt";
  String fileName2 = "file/path/file2.txt";
  String directoryName = "my/directory/";

  FakePluginApi pluginApi = new FakePluginApi();
  Unjarer unjarer = new Unjarer(pluginApi);

  @Test
  public void unjaringTwoFiles() throws Exception {
    SBlob blob = JarTester.jar(fileName1, fileName2);

    SArray<SFile> fileArray = unjarer.unjar(blob);

    int fileCount = 0;
    for (SFile file : fileArray) {
      fileCount++;
      assertContent(file.openInputStream(), file.path().value());
    }
    assertThat(fileCount).isEqualTo(2);
  }

  @Test
  public void unjaringIgnoresDirectories() throws Exception {
    SBlob blob = JarTester.jar(fileName1, directoryName);

    SArray<SFile> fileArray = unjarer.unjar(blob);

    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    assertThat(fileArray.iterator().next().path()).isEqualTo(path(fileName1));
  }

  @Test
  public void unjaringWithFilter() throws Exception {
    SBlob blob = JarTester.jar(fileName1, fileName2);

    SArray<SFile> fileArray = unjarer.unjar(blob, Predicates.equalTo(fileName2));

    assertThat(Iterables.size(fileArray)).isEqualTo(1);
    assertThat(fileArray.iterator().next().path().value()).isEqualTo(fileName2);
  }

  @Test
  public void entryWithIllegalName() throws Exception {
    String illegalFileName = "/leading/slash/is/forbidden";
    SBlob blob = JarTester.jar(illegalFileName);

    try {
      unjarer.unjar(blob);
      fail("exception should be thrown");
    } catch (ErrorMessageException e) {
      // expected
      assertThat(e.errorMessage()).isInstanceOf(IllegalPathInJarError.class);
    }
  }
}
