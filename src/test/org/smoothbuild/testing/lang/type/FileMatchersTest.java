package org.smoothbuild.testing.lang.type;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.lang.base.SFile;
import org.smoothbuild.testing.lang.type.FakeFile;
import org.smoothbuild.testing.lang.type.FileMatchers;

public class FileMatchersTest {
  byte[] bytes1 = new byte[] { 1, 2, 3 };
  byte[] bytes2 = new byte[] { 1, 2, 3, 4 };
  Path path1 = Path.path("my/file1");
  Path path2 = Path.path("my/file2");
  SFile file1;
  SFile file2;

  @Test
  public void file_is_equal_to_itself() {
    given(file1 = new FakeFile(path1, bytes1));
    when(FileMatchers.equalTo(file1).matches(file1));
    thenReturned(true);
  }

  @Test
  public void files_with_different_bytes_but_equal_path_does_not_match() {
    given(file1 = new FakeFile(path1, bytes1));
    given(file2 = new FakeFile(path1, bytes2));
    when(FileMatchers.equalTo(file1).matches(file2));
    thenReturned(false);
  }

  @Test
  public void files_with_different_path_but_equal_bytes_does_not_match() {
    given(file1 = new FakeFile(path1, bytes1));
    given(file2 = new FakeFile(path2, bytes1));
    when(FileMatchers.equalTo(file1).matches(file2));
    thenReturned(false);
  }

  @Test
  public void files_with_different_path_and_bytes_does_not_match() {
    given(file1 = new FakeFile(path1, bytes1));
    given(file2 = new FakeFile(path2, bytes2));
    when(FileMatchers.equalTo(file1).matches(file2));
    thenReturned(false);
  }
}
