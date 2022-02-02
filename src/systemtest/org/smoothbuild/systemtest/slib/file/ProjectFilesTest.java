package org.smoothbuild.systemtest.slib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.install.ProjectPaths.PRJ_MODULE_PATH;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ProjectFilesTest extends SystemTestCase {
  @Test
  public void listing_files_from_smooth_dir_causes_error() throws Exception {
    createUserModule("""
            result = projectFiles(".smooth");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void listing_files_from_smooth_dir_subdir_causes_error() throws Exception {
    createUserModule("""
            result = projectFiles(".smooth/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Listing files from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    createUserModule("""
            result = projectFiles("..");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Param `dir` has illegal value. Path cannot contain '..' part.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    createUserModule("""
            result = projectFiles("nonexistent/path.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Dir 'nonexistent/path.txt' doesn't exist.");
  }

  @Test
  public void non_dir_path_causes_error() throws Exception {
    createFile("file.txt", "abc");
    createUserModule("""
            result = projectFiles("file.txt");
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
            result = projectFiles("dir");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc", "subdir/file.txt", "def");
  }

  @Test
  public void files_from_project_root_are_returned_except_content_of_smooth_dir() throws Exception {
    String script = "result = projectFiles(\".\");";
    createUserModule(script);
    createFile("dir/file.txt", "abc");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    String userModule = PRJ_MODULE_PATH.toString();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly(userModule, script, "dir/file.txt", "abc");
  }
}
