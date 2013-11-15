package org.smoothbuild.testing.io.db;

import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.testing.io.db.value.FakeValueDb;

public class FakeObjectDbTest {
  Path path = Path.path("my/file");
  FakeValueDb fakeValueDb = new FakeValueDb();

  File file;

  @Test
  public void created_file_containing_its_path_can_be_read_from_db() {
    given(file = fakeValueDb.createFileContainingItsPath(path));
    when(fakeValueDb.file(file.hash()).path());
    thenReturned(path);
  }

  @Test
  public void created_file_containing_its_path_can_be_read_from_db2() throws IOException {
    given(file = fakeValueDb.createFileContainingItsPath(path));
    when(inputStreamToString(fakeValueDb.file(file.hash()).openInputStream()));
    thenReturned(path.value());
  }
}
