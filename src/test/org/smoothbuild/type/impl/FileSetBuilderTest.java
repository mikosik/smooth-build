package org.smoothbuild.type.impl;

import static org.hamcrest.Matchers.emptyIterable;
import static org.smoothbuild.testing.common.StreamTester.inputStreamToString;
import static org.smoothbuild.testing.common.StreamTester.writeAndClose;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;
import java.io.OutputStream;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.object.FileSetBuilder;
import org.smoothbuild.testing.fs.base.TestFileSystem;
import org.smoothbuild.type.api.FileSet;

public class FileSetBuilderTest {
  String content = "content";
  Path path = Path.path("my/file.txt");
  FileSet fileSet;
  OutputStream outputStream;
  TestFileSystem fileSystem = new TestFileSystem();
  FileSetBuilder fileSetBuilder = new FileSetBuilder(fileSystem);

  @Test
  public void build_returns_empty_file_set_when_no_file_has_been_added() throws IOException {
    when(fileSetBuilder.build());
    thenReturned(emptyIterable());
  }

  @Test
  public void returned_file_set_contains_streamed_file() throws Exception {
    given(fileSetBuilder.openFileOutputStream(path)).close();
    given(fileSet = fileSetBuilder.build());
    when(fileSet.contains(path));
    thenReturned(true);
  }

  @Test
  public void returned_file_set_contains_added_file() throws Exception {
    given(fileSetBuilder).add(fileSystem.createEmptyFile(path));
    given(fileSet = fileSetBuilder.build());
    when(fileSet.contains(path));
    thenReturned(true);
  }

  @Test
  public void built_file_set_contains_streamed_file_with_created_content() throws Exception {
    given(this).writeFile(fileSetBuilder, path, content);
    given(fileSet = fileSetBuilder.build());
    when(inputStreamToString(fileSet.file(path).openInputStream()));
    thenReturned(content);
  }

  @Test
  public void built_file_set_contains_added_file_with_created_content() throws Exception {
    given(fileSetBuilder).add(fileSystem.createFileContainingItsPath(path));
    given(fileSet = fileSetBuilder.build());
    when(inputStreamToString(fileSet.file(path).openInputStream()));
    thenReturned(path.value());
  }

  @Test
  public void builder_does_not_contain_not_added_file() throws IOException {
    when(fileSetBuilder.contains(path));
    thenReturned(false);
  }

  @Test
  public void builder_contains_streamed_file() throws IOException {
    given(this).writeFile(fileSetBuilder, path, content);
    when(fileSetBuilder.contains(path));
    thenReturned(true);
  }

  private void writeFile(FileSetBuilder fileSetBuilder, Path path, String content)
      throws IOException {
    writeAndClose(fileSetBuilder.openFileOutputStream(path), content);
  }
}
