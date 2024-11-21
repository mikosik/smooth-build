package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestContext;

public class FilesFuncTest extends StandardLibraryTestContext {
  @Test
  void illegal_path_causes_error() throws Exception {
    var userModule = """
        result = files("..");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Param `dir` has illegal value. Path cannot contain '..' part."));
  }

  @Test
  void nonexistent_path_causes_error() throws Exception {
    var userModule = """
        result = files("nonexistent/path.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Cannot list files in '{t-project}/nonexistent/path.txt'. "
            + "Dir '{t-project}/nonexistent/path.txt' doesn't exist."));
  }

  @Test
  void non_dir_path_causes_error() throws Exception {
    var userModule = """
        result = files("file.txt");
        """;
    createUserModule(userModule);
    createProjectFile("file.txt", "abc");
    evaluate("result");
    assertThat(logs())
        .contains(error("Cannot list files in '{t-project}/file.txt'. "
            + "Dir '{t-project}/file.txt' doesn't exist. It is a file."));
  }

  @Test
  void files_from_dir_are_returned() throws Exception {
    var userModule = """
        result = files("dir");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    createProjectFile("dir/subdir/file.txt", "def");
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(bArray(bFile("file.txt", "abc"), bFile("subdir/file.txt", "def")));
  }
}
