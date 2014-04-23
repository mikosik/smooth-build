package org.smoothbuild.lang.builtin.java;

import static org.hamcrest.Matchers.contains;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SBlob;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.common.JarTester;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

import com.google.common.base.Predicates;

public class UnjarerTest {
  private final Path path1 = path("file/path/file1.txt");
  private final Path path2 = path("file/path/file2.txt");

  private final FakeObjectsDb objectsDb = new FakeObjectsDb();
  private final Unjarer unjarer = new Unjarer(objectsDb);
  private SBlob blob;
  private SFile file1;
  private SFile file2;

  @Test
  public void unjars_two_files() throws Exception {
    given(file1 = objectsDb.file(path1));
    given(file2 = objectsDb.file(path2));
    given(blob = JarTester.jar(file1, file2));
    when(unjarer.unjar(blob));
    thenReturned(contains(file1, file2));
  }

  @Test
  public void unjars_files_that_match_filter() throws Exception {
    given(file1 = objectsDb.file(path1));
    given(file2 = objectsDb.file(path2));
    given(blob = JarTester.jar(file1, file2));
    when(unjarer.unjar(blob, Predicates.equalTo(path2.value())));
    thenReturned(contains(file2));
  }
}
