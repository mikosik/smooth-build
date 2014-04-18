package org.smoothbuild.lang.builtin.java.junit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.lang.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;
import java.util.Map;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BinaryNameToClassFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void binaryNamesAreMappedToProperClassFiles() throws IOException {
    Path path1 = path("a/Klass.class");
    Path path2 = path("b/Klass.class");
    SBlob blob = JarTester.jar(path1.value(), path2.value());

    when(binaryNameToClassFile(new FakeNativeApi(), ImmutableList.of(blob)));
    thenReturned(ImmutableMap
        .of("a.Klass", objectsDb.file(path1), "b.Klass", objectsDb.file(path2)));
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
