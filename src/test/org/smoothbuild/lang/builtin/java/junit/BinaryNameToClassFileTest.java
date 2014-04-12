package org.smoothbuild.lang.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.lang.type.FileTester;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.ImmutableList;

public class BinaryNameToClassFileTest {

  @Test
  public void binaryNamesAreMappedToProperClassFiles() throws IOException {
    String file1 = "a/Klass.class";
    String file2 = "b/Klass.class";
    SBlob blob = JarTester.jar(file1, file2);
    Map<String, SFile> x =
        binaryNameToClassFile(new FakeNativeApi(), ImmutableList.<SBlob> of(blob));

    FileTester.assertContentContains(x.get("a.Klass"), file1);
    FileTester.assertContentContains(x.get("b.Klass"), file2);
    assertThat(x.size()).isEqualTo(2);
  }

  @Test
  public void nonClassFilesAreNotMapped() throws IOException {
    String file1 = "a/Klass.txt";
    String file2 = "b/Klass.java";
    SBlob blob = JarTester.jar(file1, file2);
    Map<String, SFile> x =
        binaryNameToClassFile(new FakeNativeApi(), ImmutableList.<SBlob> of(blob));

    assertThat(x.size()).isEqualTo(0);
  }

}
