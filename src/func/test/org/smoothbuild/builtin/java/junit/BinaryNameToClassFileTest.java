package org.smoothbuild.builtin.java.junit;

import static org.smoothbuild.builtin.java.junit.BinaryNameToClassFile.binaryNameToClassFile;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

public class BinaryNameToClassFileTest {
  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private SBlob blob;
  private SFile file1;
  private SFile file2;

  @Test
  public void binary_names_are_mapped_to_proper_class_files() throws IOException {
    given(file1 = objectsDb.file(path("a/Klass.class")));
    given(file2 = objectsDb.file(path("b/Klass.class")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(objectsDb, ImmutableList.of(blob)));
    thenReturned(ImmutableMap.of("a.Klass", file1, "b.Klass", file2));
  }

  @Test
  public void non_class_files_are_not_mapped() throws IOException {
    given(file1 = objectsDb.file(path("a/Klass.txt")));
    given(file2 = objectsDb.file(path("b/Klass.java")));
    given(blob = JarTester.jar(file1, file2));
    when(binaryNameToClassFile(objectsDb, ImmutableList.of(blob)));
    thenReturned(ImmutableMap.of());
  }
}
