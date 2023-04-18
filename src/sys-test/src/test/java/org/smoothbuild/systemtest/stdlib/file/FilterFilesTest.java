package org.smoothbuild.systemtest.stdlib.file;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.systemtest.SystemTestCase;

public class FilterFilesTest extends SystemTestCase {
  @Test
  public void illegal_path_in_pattern() throws IOException {
    createUserModule("""
            result = [] > filterFiles("/");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Parameter 'pattern' has illegal value. Pattern can't start with slash character '/'.");
  }

  @Test
  public void double_star_matches_file_without_extension() throws Exception {
    createUserModule("""
            result = [File(0x41, "file")] > filterFiles("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file", "A");
  }

  @Test
  public void double_star_matches_file_with_extension() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "A");
  }

  @Test
  public void double_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void double_star_matches_file_inside_dir_tree() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "A");
  }

  @Test
  public void double_star_after_dir_matches_path_with_dir_prefix() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir_subdir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/def/file.txt")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/def/file.txt", "A");
  }

  @Test
  public void double_star_after_dir_doesnt_match_file_inside_different_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "different/file.txt")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void double_star_after_dir_doesnt_match_file_with_the_same_name_as_that_dir()
      throws Exception {
    createUserModule("""
            result = [File(0x41, "dir")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void leading_double_star_with_file_matches_that_file() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("**file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "A");
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("**/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir_tree()
      throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("**/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "A");
  }

  @Test
  public void leading_double_star_with_file_doesnt_match_different_file() throws Exception {
    createUserModule("""
            result = [File(0x41, "file2.txt")] > filterFiles("**/file1.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void leading_double_star_with_file_inside_dir_matches_such_file_inside_dir()
      throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("**dir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void leading_double_star_with_file_inside_dir_matches_such_file_inside_dir_tree()
      throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/subdir/file.txt")]
              > filterFiles("**/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "A");
  }

  @Test
  public void single_star_matches_file() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "A");
  }

  @Test
  public void single_star_doesnt_match_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_file_matches_that_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("*/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void star_slash_file_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("*/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_dir_file_matches_that_file_inside_dir_tree() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/subdir/file.txt")]
              > filterFiles("*/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "A");
  }

  @Test
  public void dir_slash_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void dir_slash_star_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("dir/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/file.txt")] > filterFiles("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "A");
  }

  @Test
  public void star_slash_star_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "file.txt")] > filterFiles("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_star_doesnt_match_file_inside_two_dirs() throws Exception {
    createUserModule("""
            result = [File(0x41, "dir/subdir/file.txt")] > filterFiles("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void all_java_files_in_src_dir() throws Exception {
    createUserModule("""
            result = [File(0x41, "src/com/comp/Main.java")]
              > filterFiles("src/**/*.java");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("src/com/comp/Main.java", "A");
  }
}
