package org.smoothbuild.stdlib.file;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.log.base.Log.error;

import java.io.IOException;
import org.junit.jupiter.api.Test;
import org.smoothbuild.stdlib.StandardLibraryTestCase;

public class FilterFilesTest extends StandardLibraryTestCase {
  @Test
  public void illegal_path_in_pattern() throws IOException {
    var userModule = """
        result = [] > filterFiles("/");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(logs())
        .contains(
            error(
                "Parameter 'pattern' has illegal value. Pattern can't start with slash character '/'."));
  }

  @Test
  public void double_star_matches_file_without_extension() throws Exception {
    var userModule = """
        result = [File(0x41, "file")] > filterFiles("**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file", "A")));
  }

  @Test
  public void double_star_matches_file_with_extension() throws Exception {
    var userModule = """
        result = [File(0x41, "file.txt")] > filterFiles("**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file.txt", "A")));
  }

  @Test
  public void double_star_matches_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void double_star_matches_file_inside_dir_tree() throws Exception {
    createUserModule(
        """
            result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("**");
            """);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/subdir/file.txt", "A")));
  }

  @Test
  public void double_star_after_dir_matches_path_with_dir_prefix() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir_subdir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/def/file.txt")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/def/file.txt", "A")));
  }

  @Test
  public void double_star_after_dir_not_matches_file_inside_different_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "different/file.txt")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void double_star_after_dir_not_matches_file_with_the_same_name_as_that_dir()
      throws Exception {
    var userModule = """
        result = [File(0x41, "dir")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void leading_double_star_with_file_matches_that_file() throws Exception {
    var userModule =
        """
        result = [File(0x41, "file.txt")] > filterFiles("**file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file.txt", "A")));
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("**/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir_tree() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("**/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/subdir/file.txt", "A")));
  }

  @Test
  public void leading_double_star_with_file_not_matches_different_file() throws Exception {
    var userModule =
        """
        result = [File(0x41, "file2.txt")] > filterFiles("**/file1.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void leading_double_star_with_file_inside_dir_matches_such_file_inside_dir()
      throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("**dir/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void leading_double_star_with_file_inside_dir_matches_such_file_inside_dir_tree()
      throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/subdir/file.txt")]
          > filterFiles("**/subdir/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/subdir/file.txt", "A")));
  }

  @Test
  public void single_star_matches_file() throws Exception {
    var userModule = """
        result = [File(0x41, "file.txt")] > filterFiles("*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("file.txt", "A")));
  }

  @Test
  public void single_star_not_matches_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void star_slash_file_matches_that_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("*/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void star_slash_file_not_matches_file_without_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "file.txt")] > filterFiles("*/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void star_slash_dir_file_matches_that_file_inside_dir_tree() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/subdir/file.txt")]
          > filterFiles("*/subdir/file.txt");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/subdir/file.txt", "A")));
  }

  @Test
  public void dir_slash_star_matches_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void dir_slash_star_not_matches_file_without_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "file.txt")] > filterFiles("dir/*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void star_slash_star_matches_file_inside_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/file.txt")] > filterFiles("*/*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("dir/file.txt", "A")));
  }

  @Test
  public void star_slash_star_not_matches_file_without_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "file.txt")] > filterFiles("*/*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void star_slash_star_not_matches_file_inside_two_dirs() throws Exception {
    var userModule =
        """
        result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("*/*");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFileType()));
  }

  @Test
  public void all_java_files_in_src_dir() throws Exception {
    var userModule =
        """
        result = [File(0x41, "src/com/comp/Main.java")]
          > filterFiles("src/**/*.java");
        """;
    createUserModule(userModule);
    evaluate("result");
    assertThat(artifact()).isEqualTo(bArray(bFile("src/com/comp/Main.java", "A")));
  }
}
