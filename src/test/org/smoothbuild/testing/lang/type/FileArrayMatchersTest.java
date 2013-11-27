package org.smoothbuild.testing.lang.type;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.testing.lang.type.FileArrayMatchers.containsFile;
import static org.smoothbuild.testing.lang.type.FileArrayMatchers.containsFileContaining;
import static org.smoothbuild.testing.lang.type.FileArrayMatchers.containsFileContainingItsPath;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.type.SFile;

public class FileArrayMatchersTest {
  FakeFile file;
  FakeArray<SFile> fileArray = new FakeArray<SFile>(FILE_ARRAY);
  Path path = Path.path("my/path1");
  Path path2 = Path.path("my/path2");
  String content = "content";

  // assertContentContainsFilePath

  @Test
  public void asserting_file_path_content_succeeds_when_content_and_path_match() throws IOException {
    given(file = new FakeFile(path));
    when(fileArray).add(file);
    then(fileArray, containsFileContainingItsPath(path));
  }

  @Test
  public void asserting_file_path_content_fails_when_file_matches_but_content_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileArray).add(file);
    then(fileArray, not(containsFileContainingItsPath(path)));
  }

  @Test
  public void asserting_file_path_content_on_empty_file_array_fails() throws IOException {
    when(fileArray);
    thenReturned(not(containsFileContainingItsPath(path)));
  }

  // assertCotentContains

  @Test
  public void asserting_content_succeeds_when_content_and_path_match() throws IOException {
    given(file = new FakeFile(path, content));
    when(fileArray).add(file);
    then(fileArray, containsFileContaining(path, content));
  }

  @Test
  public void asserting_content_fails_when_file_matches_but_content_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileArray).add(file);
    then(fileArray, not(containsFileContaining(path, content + "something")));
  }

  @Test
  public void asserting_content_fails_when_content_matches_but_file_path_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileArray).add(file);
    then(fileArray, not(containsFileContaining(path2, content)));
  }

  @Test
  public void asserting_content_on_empty_file_array_fails() throws IOException {
    when(fileArray);
    thenReturned(not(containsFileContaining(path, content)));
  }

  // assertContentContainsFilePath

  @Test
  public void file_array_contains_added_file() throws IOException {
    given(file = new FakeFile(path, content));
    when(fileArray).add(file);
    then(fileArray, containsFile(path));
  }

  @Test
  public void file_array_with_one_file_does_not_contain_other_file() throws IOException {
    given(file = new FakeFile(path2, content));
    when(fileArray).add(file);
    then(fileArray, not(containsFile(path)));
  }

  @Test
  public void empty_file_array_does_not_contain_file() throws IOException {
    when(fileArray);
    thenReturned(not(containsFile(path)));
  }
}
