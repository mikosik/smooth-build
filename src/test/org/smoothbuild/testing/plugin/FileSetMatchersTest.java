package org.smoothbuild.testing.plugin;

import static org.hamcrest.Matchers.not;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFile;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFileContaining;
import static org.smoothbuild.testing.plugin.FileSetMatchers.containsFileContainingItsPath;
import static org.testory.Testory.given;
import static org.testory.Testory.then;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.testing.plugin.FakeFile;
import org.smoothbuild.testing.plugin.FakeFileSet;

public class FileSetMatchersTest {
  FakeFile file;
  FakeFileSet fileSet = new FakeFileSet();
  Path path = Path.path("my/path1");
  Path path2 = Path.path("my/path2");
  String content = "content";

  // assertContentContainsFilePath

  @Test
  public void asserting_file_path_content_succeeds_when_content_and_path_match() throws IOException {
    given(file = new FakeFile(path));
    when(fileSet).add(file);
    then(fileSet, containsFileContainingItsPath(path));
  }

  @Test
  public void asserting_file_path_content_fails_when_file_matches_but_content_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileSet).add(file);
    then(fileSet, not(containsFileContainingItsPath(path)));
  }

  @Test
  public void asserting_file_path_content_on_empty_file_set_fails() throws IOException {
    when(fileSet);
    thenReturned(not(containsFileContainingItsPath(path)));
  }

  // assertCotentContains

  @Test
  public void asserting_content_succeeds_when_content_and_path_match() throws IOException {
    given(file = new FakeFile(path, content));
    when(fileSet).add(file);
    then(fileSet, containsFileContaining(path, content));
  }

  @Test
  public void asserting_content_fails_when_file_matches_but_content_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileSet).add(file);
    then(fileSet, not(containsFileContaining(path, content + "something")));
  }

  @Test
  public void asserting_content_fails_when_content_matches_but_file_path_does_not_match()
      throws IOException {
    given(file = new FakeFile(path, content));
    when(fileSet).add(file);
    then(fileSet, not(containsFileContaining(path2, content)));
  }

  @Test
  public void asserting_content_on_empty_file_set_fails() throws IOException {
    when(fileSet);
    thenReturned(not(containsFileContaining(path, content)));
  }

  // assertContentContainsFilePath

  @Test
  public void file_set_contains_added_file() throws IOException {
    given(file = new FakeFile(path, content));
    when(fileSet).add(file);
    then(fileSet, containsFile(path));
  }

  @Test
  public void file_set_with_one_file_does_not_contain_other_file() throws IOException {
    given(file = new FakeFile(path2, content));
    when(fileSet).add(file);
    then(fileSet, not(containsFile(path)));
  }

  @Test
  public void empty_file_set_does_not_contain_file() throws IOException {
    when(fileSet);
    thenReturned(not(containsFile(path)));
  }
}
