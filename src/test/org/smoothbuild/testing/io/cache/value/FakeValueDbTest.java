package org.smoothbuild.testing.io.cache.value;

import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.util.Streams.inputStreamToString;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SFile;

public class FakeValueDbTest {
  Path path = Path.path("my/file");
  FakeValueDb fakeValueDb = new FakeValueDb();

  SFile file;

  @Test
  public void created_file_containing_its_path_can_be_read_from_db() {
    given(file = fakeValueDb.createFileContainingItsPath(path));
    when(fakeValueDb.read(FILE, file.hash()).path());
    thenReturned(path);
  }

  @Test
  public void created_file_containing_its_path_can_be_read_from_db2() throws IOException {
    given(file = fakeValueDb.createFileContainingItsPath(path));
    when(inputStreamToString(fakeValueDb.read(FILE, file.hash()).content().openInputStream()));
    thenReturned(path.value());
  }
}
