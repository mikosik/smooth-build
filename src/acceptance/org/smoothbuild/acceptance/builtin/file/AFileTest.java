package org.smoothbuild.acceptance.builtin.file;

import static org.smoothbuild.acceptance.FileContentMatcher.hasContent;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AFileTest extends AcceptanceTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    givenFile(".smooth/file.txt", "abc");
    givenScript(
        "  result = aFile('//.smooth/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    givenFile(".smooth/subdir/file.txt", "abc");
    givenScript(
        "  result = aFile('//.smooth/subdir/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    givenScript(
        "  result = aFile('//..');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Param 'path' has illegal value. Path cannot contain '..' element.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    givenScript(
        "  result = aFile('//nonexistent/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("File 'nonexistent/file.txt' doesn't exist.");
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    givenDir("some/dir");
    givenScript(
        "  result = aFile('//some/dir');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("File 'some/dir' doesn't exist. It is a dir.");
  }

  @Test
  public void path_not_prefixed_with_double_slash_causes_error() throws Exception {
    givenFile("file.txt", "abc");
    givenScript(
        "  result = aFile('file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenOutputContains("Param 'path' has illegal value. "
        + "It should start with \"//\" which represents project's root dir.");
  }

  @Test
  public void file_is_returned() throws Exception {
    givenFile("dir/file.txt", "abc");
    givenScript(
        "  result = aFile('//dir/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    then(artifact("result"), hasContent("abc"));
  }
}
