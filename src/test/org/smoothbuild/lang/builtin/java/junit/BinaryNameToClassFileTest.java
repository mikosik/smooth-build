package org.smoothbuild.lang.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.type.SFile;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.lang.type.FileTester;
import org.smoothbuild.testing.task.exec.FakePluginApi;

import com.google.common.collect.ImmutableList;

public class BinaryNameToClassFileTest {

  @Test
  public void binaryNamesAreMappedToProperClassFiles() throws IOException {
    String file1 = "a/Klass.class";
    String file2 = "b/Klass.class";
    FakeFile jarFile = jaredFiles(file1, file2);
    Map<String, SFile> x = binaryNameToClassFile(new FakePluginApi(), ImmutableList.<SFile> of(jarFile));

    FileTester.assertContentContains(x.get("a.Klass"), file1);
    FileTester.assertContentContains(x.get("b.Klass"), file2);
    assertThat(x.size()).isEqualTo(2);
  }

  @Test
  public void nonClassFilesAreNotMapped() throws IOException {
    String file1 = "a/Klass.txt";
    String file2 = "b/Klass.java";
    FakeFile jarFile = jaredFiles(file1, file2);
    Map<String, SFile> x = binaryNameToClassFile(new FakePluginApi(), ImmutableList.<SFile> of(jarFile));

    assertThat(x.size()).isEqualTo(0);
  }

}
