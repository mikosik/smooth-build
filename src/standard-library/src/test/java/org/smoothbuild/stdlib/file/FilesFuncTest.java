package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BArray;
import org.smoothbuild.virtualmachine.bytecode.expr.base.BValue;

public class FilesFuncTest extends StandardLibraryTestCase {
  @Test
  public void listing_files_from_smooth_dir_causes_error() throws Exception {
    var userModule = """
        result = files(".smooth");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("Listing files from '.smooth' dir is not allowed."));
  }

  @Test
  public void listing_files_from_smooth_dir_subdir_causes_error() throws Exception {
    var userModule = """
        result = files(".smooth/subdir/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("Listing files from '.smooth' dir is not allowed."));
  }

  @Test
  public void illegal_path_causes_error() throws Exception {
    var userModule = """
        result = files("..");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(error("Param `dir` has illegal value. Path cannot contain '..' part."));
  }

  @Test
  public void nonexistent_path_causes_error() throws Exception {
    var userModule = """
        result = files("nonexistent/path.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs()).contains(error("Dir 'nonexistent/path.txt' doesn't exist."));
  }

  @Test
  public void non_dir_path_causes_error() throws Exception {
    var userModule = """
        result = files("file.txt");
        """;
    createUserModule(userModule);
    createProjectFile("file.txt", "abc");
    evaluate("result");
    assertThat(logs()).contains(error("Dir 'file.txt' doesn't exist. It is a file."));
  }

  @Test
  public void files_from_dir_are_returned() throws Exception {
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

  @Test
  public void files_from_project_root_are_returned_except_content_of_smooth_dir() throws Exception {
    var userModule = """
        result = files(".");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    createProjectFile(".smooth/hidden.txt", "abc");
    evaluate("result");

    var files = ((BArray) artifact()).elements(BValue.class);
    assertThat(files).containsExactly(bFile("dir/file.txt", "abc"));
  }

  @Test
  public void result_is_not_cached() throws Exception {
    var userModule = """
        result = files("dir");
        """;
    createUserModule(userModule);
    createProjectFile("dir/file.txt", "abc");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file.txt", "abc")));

    createProjectFile("dir/file.txt", "def");
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file.txt", "def")));
  }
}
