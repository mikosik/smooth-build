package org.smoothbuild.acceptance.builtin.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothPaths.USER_MODULE;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FilesTest extends AcceptanceTestCase {
  @Test
  public void listing_files_from_smooth_dir_causes_error() throws Exception {
    givenScript(
        "  result = files('//.smooth');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void listing_files_from_smooth_dir_subdir_causes_error() throws Exception {
    givenScript(
        "  result = files('//.smooth/subdir/file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    givenScript(
        "  result = files('//..');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Param 'dir' has illegal value. Path cannot contain '..' element.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    givenScript(
        "  result = files('//nonexistent/path.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Dir 'nonexistent/path.txt' doesn't exist.");
  }

  @Test
  public void non_dir_path_causes_error() throws Exception {
    givenFile("file.txt", "abc");
    givenScript(
        "  result = files('//file.txt');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Dir 'file.txt' doesn't exist. It is a file.");
  }

  @Test
  public void path_not_prefixed_with_double_slash_causes_error() throws Exception {
    givenScript(
        "  result = files('dir');  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    thenSysOutContains("Param 'dir' has illegal value. "
        + "It should start with \"//\" which represents project's root dir.");
  }

  @Test
  public void files_from_dir_are_returned() throws Exception {
    givenFile("dir/file.txt", "abc");
    givenFile("dir/subdir/file.txt", "def");
    givenScript(
        "  result = files('//dir');  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(artifactDir("result"))
        .containsExactly("file.txt", "abc", "subdir/file.txt", "def");
  }

  @Test
  public void files_from_project_root_are_returned_except_content_of_smooth_dir() throws Exception {
    String script = "result = files(\"//\");";
    givenScript(script);
    givenFile("dir/file.txt", "abc");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    String userModule = USER_MODULE.fullPath().toString();
    assertThat(artifactDir("result"))
        .containsExactly(userModule, script, "dir/file.txt", "abc");
  }
}
