package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ArrayTest {
  @Inject
  @ProjectDir
  private FakeFileSystem fileSystem;
  @Inject
  private FakeUserConsole userConsole;
  @Inject
  private BuildWorker buildWorker;

  private final Path path1 = path("some/path1");
  private final Path path2 = path("some/path2");
  private final String content1 = "content 1";
  private final String content2 = "content 2";

  @Before
  public void before() {
    createInjector(new IntegrationTestModule()).injectMembers(this);
  }

  @Test
  public void nested_arrays_are_forbidden() throws IOException {
    // given
    script(fileSystem, "run : [ [ 'abc' ] ];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(SyntaxError.class);
  }

  /**
   * arrays nested inline ( [[ 'abc' ]] ) are detected as syntax errors while
   * those passed as function results ([ myArray() ]) are detected by parser.
   * This will probably change in future when more complicated expression are
   * allowed.
   */
  @Test
  public void nested_arrays_are_forbidden_regression_test() throws IOException {
    // given
    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("someArray: [ 'abc' ] ;");
    scriptBuilder.addLine("run: [ someArray ] ;");

    script(fileSystem, scriptBuilder.build());

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertContainsOnly(ForbiddenArrayElemError.class);
  }

  @Test
  public void empty_array_can_be_saved() throws IOException {
    // given
    script(fileSystem, "run : [];");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = ARTIFACTS_PATH.append(path("run"));
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.filesFrom(artifactPath)).isEmpty();
  }
}
