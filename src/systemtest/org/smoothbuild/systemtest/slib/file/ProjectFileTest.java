package org.smoothbuild.systemtest.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class ProjectFileTest extends SystemTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    createFile(".smooth/file.txt", "abc");
    createUserModule("""
            result = projectFile(".smooth/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    createFile(".smooth/subdir/file.txt", "abc");
    createUserModule("""
            result = projectFile(".smooth/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    createUserModule("""
            result = projectFile("..");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Param `path` has illegal value. Path cannot contain '..' part.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    createUserModule("""
            result = projectFile("nonexistent/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("File 'nonexistent/file.txt' doesn't exist.");
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    createDir("some/dir");
    createUserModule("""
            result = projectFile("some/dir");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("File 'some/dir' doesn't exist. It is a dir.");
  }

  @Test
  public void file_is_returned() throws Exception {
    createFile("dir/file.txt", "abc");
    createUserModule("""
            result = projectFile("dir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void result_is_not_cached() throws Exception {
    createFile("dir/file.txt", "abc");
    createUserModule("""
            result = projectFile("dir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();

    createFile("dir/file.txt", "def");
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "def");
  }
}
