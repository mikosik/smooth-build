package org.smoothbuild.testing.object;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.File;
import org.smoothbuild.testing.common.StreamTester;

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
    when(StreamTester.inputStreamToString(fakeObjectDb.file(file.hash()).openInputStream()));
    thenReturned(path.value());
  }
}
