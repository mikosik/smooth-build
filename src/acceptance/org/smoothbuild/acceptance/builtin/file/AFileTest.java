package org.smoothbuild.acceptance.builtin.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AFileTest extends AcceptanceTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    givenFile(".smooth/file.txt", "abc");
    givenScript(
        "  result = aFile('.smooth/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    givenFile(".smooth/subdir/file.txt", "abc");
    givenScript(
        "  result = aFile('.smooth/subdir/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    givenScript(
        "  result = aFile('..');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Param 'path' has illegal value. Path cannot contain '..' element.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    givenScript(
        "  result = aFile('nonexistent/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("File 'nonexistent/file.txt' doesn't exist.");
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    givenDir("some/dir");
    givenScript(
        "  result = aFile('some/dir');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("File 'some/dir' doesn't exist. It is a dir.");
  }

  @Test
  public void file_is_returned() throws Exception {
    givenFile("dir/file.txt", "abc");
    givenScript(
        "  result = aFile('dir/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("dir/file.txt", "abc");
  }
}
