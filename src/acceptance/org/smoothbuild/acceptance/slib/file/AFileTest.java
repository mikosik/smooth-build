package org.smoothbuild.acceptance.slib.file;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class AFileTest extends AcceptanceTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    createFile(".smooth/file.txt", "abc");
    createUserModule("""
            result = aFile(".smooth/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    createFile(".smooth/subdir/file.txt", "abc");
    createUserModule("""
            result = aFile(".smooth/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Reading file from '.smooth' dir is not allowed.");
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    createUserModule("""
            result = aFile('..');
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("Param 'path' has illegal value. Path cannot contain '..'.");
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    createUserModule("""
            result = aFile("nonexistent/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("File 'nonexistent/file.txt' doesn't exist.");
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    createDir("some/dir");
    createUserModule("""
            result = aFile('some/dir');
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains("File 'some/dir' doesn't exist. It is a dir.");
  }

  @Test
  public void file_is_returned() throws Exception {
    createFile("dir/file.txt", "abc");
    createUserModule("""
            result = aFile("dir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }
}
