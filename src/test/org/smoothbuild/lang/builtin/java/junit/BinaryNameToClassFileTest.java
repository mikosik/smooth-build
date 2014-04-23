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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BinaryNameToClassFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();

  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    Path path1 = path("a/Klass.class");
    Path path2 = path("b/Klass.class");
    SBlob blob = JarTester.jar(objectsDb.file(path1), objectsDb.file(path2));

    when(binaryNameToClassFile(objectsDb, ImmutableList.of(blob)));
    thenReturned(ImmutableMap
        .of("a.Klass", objectsDb.file(path1), "b.Klass", objectsDb.file(path2)));
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    Path path1 = path("a/Klass.txt");
    Path path2 = path("b/Klass.java");
    SBlob blob = JarTester.jar(objectsDb.file(path1), objectsDb.file(path2));
    Map<String, SFile> map = binaryNameToClassFile(objectsDb, ImmutableList.<SBlob> of(blob));

    assertThat(map.size()).isEqualTo(0);
  }

}
