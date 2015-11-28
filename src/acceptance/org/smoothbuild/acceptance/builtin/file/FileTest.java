package org.smoothbuild.acceptance.builtin.file;

import static org.hamcrest.Matchers.containsString;
import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FileTest extends AcceptanceTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    givenFile(".smooth/file.txt", "abc");
    givenScript("result: file('//.smooth/file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Reading file from '.smooth' dir is not allowed."));
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    givenFile(".smooth/subdir/file.txt", "abc");
    givenScript("result: file('//.smooth/subdir/file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Reading file from '.smooth' dir is not allowed."));
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    givenScript("result: file('//..');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString(
        "Param 'path' has illegal value. Path cannot contain '..' element."));
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    givenScript("result: file('//nonexistent/file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("File 'nonexistent/file.txt' doesn't exist."));
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    givenDir("some/dir");
    givenScript("result: file('//some/dir');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("File 'some/dir' doesn't exist. It is a dir."));
  }

  @Test
  public void path_not_prefixed_with_double_slash_causes_error() throws Exception {
    givenFile("file.txt", "abc");
    givenScript("result: file('file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithError();
    then(output(), containsString("Param 'path' has illegal value. "
        + "It should start with \"//\" which represents project's root dir."));
  }

  @Test
  public void file_is_returned() throws Exception {
    givenFile("dir/file.txt", "abc");
    givenScript("result: file('//dir/file.txt');");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
