package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class FileFuncTest extends StandardLibraryTestCase {
  @Test
  public void file_from_smooth_dir_causes_error() throws Exception {
    var userModule = """
        result = file(".smooth/file.txt");
        """;
    createUserModule(userModule);
    createProjectFile(".smooth/file.txt", "abc");
    evaluate("result");
    assertThat(logs()).contains(error("Reading file from '.smooth' dir is not allowed."));
  }

  @Test
  public void file_from_smooth_subdir_causes_error() throws Exception {
    var userModule = """
        result = file(".smooth/subdir/file.txt");
        """;
    createUserModule(userModule);
    createProjectFile(".smooth/subdir/file.txt", "abc");
    evaluate("result");
    assertThat(logs()).contains(error("Reading file from '.smooth' dir is not allowed."));
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    var userModule = """
        result = file("..");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Param `path` has illegal value. Path cannot contain '..' part."));
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    var userModule = """
        result = file("nonexistent/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("File 'nonexistent/file.txt' doesn't exist."));
  }

  @Test
  public void dir_path_causes_error() throws Exception {
    var userModule = """
        result = file("some/dir");
        """;
    createUserModule(userModule);
    createProjectFile("some/dir/file", "");
    evaluate("result");
    assertThat(logs()).contains(error("File 'some/dir' doesn't exist. It is a dir."));
  }

  @Test
  public void file_is_returned() throws Exception {
    var userModule = """
        result = file("dir/file.txt");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bFile("dir/file.txt", "abc"));
  }

  @Test
  public void result_is_not_cached() throws Exception {
    var userModule = """
        result = file("dir/file.txt");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bFile("dir/file.txt", "abc"));

    createProjectFile("dir/file.txt", "def");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bFile("dir/file.txt", "def"));
  }
}
