package org.smoothbuild.builtin.file;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.io.fs.base.PathState.NOTHING;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;
import static org.testory.Testory.thenEqual;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.builtin.file.err.IllegalPathPatternError;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class FilterTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  @Test
  public void illegal_path_is_detected() throws IOException {
    script(fileSystem, "result: [] | filter('///');");
    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(IllegalPathPatternError.class);
  }

  @Test
  public void double_star_alone() throws Exception {
    String pattern = "**";
    List<String> included = asList("file", "file.txt", "a/b/c/d/file.txt");
    List<String> excluded = asList();

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_end() throws Exception {
    String pattern = "abc/**";
    List<String> included = asList("abc/file", "abc/def/file.txt");
    List<String> excluded = asList("xxx/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_end_does_not_include_file_named_as_prefix_dir() throws Exception {
    String pattern = "abc/**";
    List<String> included = asList();
    List<String> excluded = asList("abc");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void double_star_at_the_beginning() throws Exception {
    String pattern = "**/file";
    List<String> included = asList("file", "abc/file", "abc/def/file");
    List<String> excluded = asList("file2", "abc/file2", "abc/def/file2");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_alone() throws Exception {
    String pattern = "*";
    List<String> included = asList("file", "file.txt");
    List<String> excluded = asList("abc/file", "a/b/c/d/file.txt");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_at_the_beginning() throws Exception {
    String pattern = "*/abc/file";
    List<String> included = asList("xxx/abc/file", "yyy/abc/file");
    List<String> excluded = asList("abc/file", "xxx/yyy/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void single_star_at_the_end() throws Exception {
    String pattern = "*/abc/file";
    List<String> included = asList("xxx/abc/file", "yyy/abc/file");
    List<String> excluded = asList("abc/file", "xxx/yyy/abc/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void only_single_star_twice() throws Exception {
    String pattern = "*/*";
    List<String> included = asList("abc/file", "def/file");
    List<String> excluded = asList("file", "abc/xxx/file");

    doTestFiltering(pattern, included, excluded);
  }

  @Test
  public void filter_all_java_files_from_given_dir() throws Exception {
    String pattern = "src/**/*.java";
    List<String> included = asList("src/Klass.java", "src/com/example/Main.java");
    List<String> excluded = asList("dir/Main.java", "src/com/example/Main.class");

    doTestFiltering(pattern, included, excluded);
  }

  private void doTestFiltering(String pattern, List<String> included, List<String> excluded)
      throws IOException {
    Path pathA = path("arrayA");
    for (String path : included) {
      fileSystem.createFileContainingItsPath(pathA, path(path));
    }
    for (String path : excluded) {
      fileSystem.createFileContainingItsPath(pathA, path(path));
    }
    script(fileSystem, "result: files(" + pathA + ") | filter('" + pattern + "') ;");
    buildWorker.run(asList("result"));

    userConsole.messages().assertNoProblems();
    for (String path : excluded) {
      thenEqual(fileSystem.pathState(artifactPath("result").append(path(path))), NOTHING);
    }
    for (String path : included) {
      fileSystem.assertFileContainsItsPath(artifactPath("result"), path(path));
    }
  }
}
