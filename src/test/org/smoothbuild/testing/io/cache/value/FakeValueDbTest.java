package org.smoothbuild.testing.io.cache.value;

import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.db.objects.FakeObjectsDb;

public class FakeValueDbTest {
  Path path = Path.path("my/file");
  FakeObjectsDb fakeObjectsDb = new FakeObjectsDb();

  SFile file;

  @Test
  public void created_file_containing_its_path_can_be_read_from_db() {
    given(file = fakeObjectsDb.createFileContainingItsPath(path));
    when(fakeObjectsDb.read(FILE, file.hash()).path());
    thenReturned(path);
  }

  @Test
  public void created_file_containing_its_path_can_be_read_from_db2() throws IOException {
    given(file = fakeObjectsDb.createFileContainingItsPath(path));
    when(inputStreamToString(fakeObjectsDb.read(FILE, file.hash()).content().openInputStream()));
    thenReturned(path.value());
  }
}
