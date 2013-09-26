package org.smoothbuild.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.testing.common.JarTester.jaredFiles;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.plugin.api.File;
import org.smoothbuild.testing.plugin.internal.FileTester;
import org.smoothbuild.testing.plugin.internal.TestFile;

import com.google.common.collect.ImmutableList;

public class BinaryNameToClassFileTest {

  @Test
  public void binaryNamesAreMappedToProperClassFiles() throws IOException {
    String file1 = "a/Klass.class";
    String file2 = "b/Klass.class";
    TestFile jarFile = jaredFiles(file1, file2);
    Map<String, File> x = binaryNameToClassFile(ImmutableList.<File> of(jarFile));

    FileTester.assertContentContains(x.get("a.Klass"), file1);
    FileTester.assertContentContains(x.get("b.Klass"), file2);
    assertThat(x.size()).isEqualTo(2);
  }

  @Test
  public void nonClassFilesAreNotMapped() throws IOException {
    String file1 = "a/Klass.txt";
    String file2 = "b/Klass.java";
    TestFile jarFile = jaredFiles(file1, file2);
    Map<String, File> x = binaryNameToClassFile(ImmutableList.<File> of(jarFile));

    assertThat(x.size()).isEqualTo(0);
  }

}
