package org.smoothbuild.type.impl;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.testing.type.impl.FileSetMatchers.containsFileContaining;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.FileObject;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.object.FakeObjectsDb;

import com.google.common.base.Charsets;

public class FileSetBuilderTest {
  String content = "content";
  Path path = Path.path("my/file.txt");
  FakeObjectsDb objectsDb = new FakeObjectsDb();

  FileSetBuilder fileSetBuilder = new FileSetBuilder(objectsDb);

  @Test
  public void build_returns_empty_file_set_when_no_file_has_been_added() throws IOException {
    when(fileSetBuilder.build());
    thenReturned(emptyIterable());
  }

  @Test
  public void returned_file_set_contains_added_file() throws Exception {
    given(this).addFile(fileSetBuilder, path, content);
    when(fileSetBuilder).build();
    thenReturned(containsFileContaining(path, content));
  }

  @Test
  public void builder_does_not_contain_not_added_file() throws IOException {
    when(fileSetBuilder.contains(path));
    thenReturned(false);
  }

  private void addFile(FileSetBuilder fileSetBuilder, Path path, String content) throws IOException {
    FileObject file = objectsDb.file(path, content.getBytes(Charsets.UTF_8));
    fileSetBuilder.add(file);
  }
}
