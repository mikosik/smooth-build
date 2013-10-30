package org.smoothbuild.plugin;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.testing.message.ErrorMessageMatchers.containsInstanceOf;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFileContaining;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.plugin.err.CannotAddDuplicatePathError;
import org.smoothbuild.testing.db.FakeObjectDb;

import com.google.common.base.Charsets;

public class FileSetBuilderTest {
  String content = "content";
  Path path = Path.path("my/file.txt");
  FakeObjectDb objectDb = new FakeObjectDb();
  File file;

  FileSetBuilder fileSetBuilder = new FileSetBuilder(objectDb);

  @Test
  public void build_returns_empty_file_set_when_no_file_has_been_added() throws IOException {
    when(fileSetBuilder.build());
    thenReturned(emptyIterable());
  }

  @Test
  public void returned_file_set_contains_added_file() throws Exception {
    given(file = objectDb.file(path, content.getBytes(Charsets.UTF_8)));
    given(fileSetBuilder).add(file);
    when(fileSetBuilder).build();
    thenReturned(containsFileContaining(path, content));
  }

  @Test
  public void builder_does_not_contain_not_added_file() throws IOException {
    when(fileSetBuilder.contains(path));
    thenReturned(false);
  }

  @Test
  public void adding_two_files_with_the_same_path_fails() throws Exception {
    given(file = objectDb.file(path, content.getBytes(Charsets.UTF_8)));
    given(fileSetBuilder).add(file);
    when(fileSetBuilder).add(file);
    thenThrown(containsInstanceOf(CannotAddDuplicatePathError.class));
  }
}
