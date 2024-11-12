package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FileFuncTest extends StandardLibraryTestContext {
  @Test
  void illegal_path_causes_error() throws Exception {
    var userModule = """
        result = file("..");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Param `path` has illegal value. Path cannot contain '..' part."));
  }

  @Test
  void nonexistent_path_causes_error() throws Exception {
    var userModule = """
        result = file("nonexistent/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(
            error(
                "Error reading file '{t-project}/nonexistent/file.txt'. File 'nonexistent/file.txt' doesn't exist."));
  }

  @Test
  void dir_path_causes_error() throws Exception {
    var userModule = """
        result = file("some/dir");
        """;
    createUserModule(userModule);
    createProjectFile("some/dir/file", "");
    evaluate("result");
    assertThat(logs())
        .contains(
            error(
                "Error reading file '{t-project}/some/dir'. File 'some/dir' doesn't exist. It is a dir."));
  }

  @Test
  void file_is_returned() throws Exception {
    var userModule = """
        result = file("dir/file.txt");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bFile("dir/file.txt", "abc"));
  }
}
