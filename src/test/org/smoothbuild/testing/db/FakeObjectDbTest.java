package org.smoothbuild.testing.db;

import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;

public class FakeObjectDbTest {
  Path path = Path.path("my/file");
  FakeObjectDb fakeObjectDb = new FakeObjectDb();

  File file;

  @Test
  public void created_file_containing_its_path_can_be_read_from_db() {
    given(file = fakeObjectDb.createFileContainingItsPath(path));
    when(fakeObjectDb.file(file.hash()).path());
    thenReturned(path);
  }

  @Test
  public void created_file_containing_its_path_can_be_read_from_db2() throws IOException {
    given(file = fakeObjectDb.createFileContainingItsPath(path));
    when(inputStreamToString(fakeObjectDb.file(file.hash()).openInputStream()));
    thenReturned(path.value());
  }
}
