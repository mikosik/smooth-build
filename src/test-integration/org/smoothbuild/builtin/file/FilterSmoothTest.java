package org.smoothbuild.builtin.file;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.fs.base.Path;
import org.smoothbuild.integration.IntegrationTestCase;
import org.smoothbuild.testing.type.impl.FakeFileSet;

import com.google.common.collect.ImmutableList;

public class FilterSmoothTest extends IntegrationTestCase {
  @Test
  public void illegal_path_is_detected() throws IOException {
    // given
    script("run : [] | filter('///');");

    // when
    smoothApp.run("run");

    // then
    messages.assertOnlyProblem(IllegalPathPatternError.class);
  }

  @Test
  public void double_star_alone() throws Exception {
    String pattern = "**";
    ImmutableList<String> included = ImmutableList.of("file", "file.txt", "a/b/c/d/file.txt");
    ImmutableList<String> excluded = ImmutableList.of();

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_end() throws Exception {
    String pattern = "abc/**";
    ImmutableList<String> included = ImmutableList.of("abc/file", "abc/def/file.txt");
    ImmutableList<String> excluded = ImmutableList.of("xxx/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_end_does_not_include_file_named_as_prefix_dir() throws Exception {
    String pattern = "abc/**";
    ImmutableList<String> included = ImmutableList.of();
    ImmutableList<String> excluded = ImmutableList.of("abc");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_beginning() throws Exception {
    String pattern = "**/file";
    ImmutableList<String> included = ImmutableList.of("file", "abc/file", "abc/def/file");
    ImmutableList<String> excluded = ImmutableList.of("file2", "abc/file2", "abc/def/file2");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_alone() throws Exception {
    String pattern = "*";
    ImmutableList<String> included = ImmutableList.of("file", "file.txt");
    ImmutableList<String> excluded = ImmutableList.of("abc/file", "a/b/c/d/file.txt");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_at_the_beginning() throws Exception {
    String pattern = "*/abc/file";
    ImmutableList<String> included = ImmutableList.of("xxx/abc/file", "yyy/abc/file");
    ImmutableList<String> excluded = ImmutableList.of("abc/file", "xxx/yyy/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_at_the_end() throws Exception {
    String pattern = "*/abc/file";
    ImmutableList<String> included = ImmutableList.of("xxx/abc/file", "yyy/abc/file");
    ImmutableList<String> excluded = ImmutableList.of("abc/file", "xxx/yyy/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void only_single_star_twice() throws Exception {
    String pattern = "*/*";
    ImmutableList<String> included = ImmutableList.of("abc/file", "def/file");
    ImmutableList<String> excluded = ImmutableList.of("file", "abc/xxx/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void filter_all_java_files_from_given_dir() throws Exception {
    String pattern = "src/**/*.java";
    ImmutableList<String> included = ImmutableList
        .of("src/Klass.java", "src/com/example/Main.java");
    ImmutableList<String> excluded = ImmutableList
        .of("dir/Main.java", "src/com/example/Main.class");

    doTestFiltering(pattern, included, excluded);
  }

  private void doTestFiltering(String pattern, ImmutableList<String> included,
      ImmutableList<String> excluded) throws IOException {
    // given
    Path outputPath = path("output");
    FakeFileSet files = fileSet(path("setA"));
    for (String path : included) {
      createFileInFiles(files, path);
    }
    for (String path : excluded) {
      createFileInFiles(files, path);
    }
    script("run : files('setA') | filter('" + pattern + "')  | save(" + outputPath + ");");

    // when
    smoothApp.run("run");

    // then
    messages.assertNoProblems();
    assertThat(fileSet(outputPath)).hasSize(included.size());
    for (String path : included) {
      fileSet(outputPath).file(path(path)).assertContentContainsFilePath();
    }
  }

  private void createFileInFiles(FakeFileSet files, String path) throws IOException {
    files.file(path(path)).createContentWithFilePath();
  }
}
