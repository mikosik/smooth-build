package org.smoothbuild.acceptance.slib.file;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class FilterTest extends AcceptanceTestCase {
  @Test
  public void illegal_path_in_pattern() throws IOException {
    createUserModule("""
            result = [] | filter("/");
            """);
    runSmoothBuild("result");
    assertFinishedWithError();
    assertSysOutContains(
        "Parameter 'pattern' has illegal value. Pattern can't start with slash character '/'.");
  }

  @Test
  public void double_star_matches_file_without_extension() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file") ] | filter("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file", "abc");
  }

  @Test
  public void double_star_matches_file_with_extension() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc");
  }

  @Test
  public void double_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void double_star_matches_file_inside_dir_tree() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/subdir/file.txt") ] | filter("**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "abc");
  }

  @Test
  public void double_star_after_dir_matches_path_with_dir_prefix() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void double_star_after_dir_matches_file_inside_this_dir_subdir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/def/file.txt") ] | filter("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/def/file.txt", "abc");
  }

  @Test
  public void double_star_after_dir_doesnt_match_file_inside_different_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "different/file.txt") ] | filter("dir/**");
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
            result = [ file(toBlob("abc"), "dir") ] | filter("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void leading_double_star_with_file_matches_that_file() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("**file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc");
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("**/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void leading_double_star_with_file_matches_that_file_inside_dir_tree()
      throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/subdir/file.txt") ] | filter("**/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "abc");
  }

  @Test
  public void leading_double_star_with_file_doesnt_match_different_file() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file2.txt") ] | filter("**/file1.txt");
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
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("**dir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void leading_double_star_with_file_inside_dir_matches_such_file_inside_dir_tree()
      throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/subdir/file.txt") ]
              | filter("**/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "abc");
  }

  @Test
  public void single_star_matches_file() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("file.txt", "abc");
  }

  @Test
  public void single_star_doesnt_match_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_file_matches_that_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("*/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void star_slash_file_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("*/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_dir_file_matches_that_file_inside_dir_tree() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/subdir/file.txt") ]
              | filter("*/subdir/file.txt");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/subdir/file.txt", "abc");
  }

  @Test
  public void dir_slash_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("dir/**");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void dir_slash_star_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("dir/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_star_matches_file_inside_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/file.txt") ] | filter("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("dir/file.txt", "abc");
  }

  @Test
  public void star_slash_star_doesnt_match_file_without_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "file.txt") ] | filter("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void star_slash_star_doesnt_match_file_inside_two_dirs() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "dir/subdir/file.txt") ] | filter("*/*");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .isEmpty();
  }

  @Test
  public void all_java_files_in_src_dir() throws Exception {
    createUserModule("""
            result = [ file(toBlob("abc"), "src/com/comp/Main.java") ]
              | filter("src/**/*.java");
            """);
    runSmoothBuild("result");
    assertFinishedWithSuccess();
    assertThat(artifactTreeContentAsStrings("result"))
        .containsExactly("src/com/comp/Main.java", "abc");
  }
}
