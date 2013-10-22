package org.smoothbuild.testing.object;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.common.StreamTester;
import org.smoothbuild.type.api.File;

public class FakeObjectsDbTest {
  Path path = Path.path("my/file");
  FakeObjectsDb fakeObjectsDb = new FakeObjectsDb();

  File file;

  @Test
  public void created_file_containing_its_path_can_be_read_from_db() {
    given(file = fakeObjectsDb.createFileContainingItsPath(path));
    when(fakeObjectsDb.file(file.hash()).path());
    thenReturned(path);
  }

  @Test
  public void created_file_containing_its_path_can_be_read_from_db2() throws IOException {
    given(file = fakeObjectsDb.createFileContainingItsPath(path));
    when(StreamTester.inputStreamToString(fakeObjectsDb.file(file.hash()).openInputStream()));
    thenReturned(path.value());
  }
}
