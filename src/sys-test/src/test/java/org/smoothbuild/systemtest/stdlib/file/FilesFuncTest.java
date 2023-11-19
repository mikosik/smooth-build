package org.smoothbuild.systemtest.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.filesystem.project.ProjectSpaceLayout.DEFAULT_MODULE_PATH;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class FilesFuncTest extends SystemTestCase {
  @Test
  public void listing_files_from_smooth_dir_causes_error() throws Exception {
    createUserModule("""
            result = files(".smooth");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void listing_files_from_smooth_dir_subdir_causes_error() throws Exception {
    createUserModule("""
            result = files(".smooth/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    createUserModule("""
            result = files("..");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Param `dir` has illegal value. Path cannot contain '..' part.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    createUserModule("""
            result = files("nonexistent/path.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Dir 'nonexistent/path.txt' doesn't exist.");
  }

  @Test
  public void non_dir_path_causes_error() throws Exception {
    createFile("file.txt", "abc");
    createUserModule("""
            result = files("file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Dir 'file.txt' doesn't exist. It is a file.");
  }

  @Test
  public void files_from_dir_are_returned() throws Exception {
    createFile("dir/file.txt", "abc");
    createFile("dir/subdir/file.txt", "def");
    createUserModule("""
            result = files("dir");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc", "subdir/file.txt", "def");
  }

  @Test
  public void files_from_project_root_are_returned_except_content_of_smooth_dir() throws Exception {
    String script = "result = files(\".\");";
    createUserModule(script);
    createFile("dir/file.txt", "abc");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String userModule = DEFAULT_MODULE_PATH.toString();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly(userModule, script, "dir/file.txt", "abc");
  }

  @Test
  public void result_is_not_cached() throws Exception {
    createFile("dir/file.txt", "abc");
    createUserModule("""
            result = files("dir");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();

    createFile("dir/file.txt", "def");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result")).containsExactly("file.txt", "def");
  }
}
