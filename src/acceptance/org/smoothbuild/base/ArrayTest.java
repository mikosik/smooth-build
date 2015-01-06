package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.empty;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.artifactPath;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;
import static org.testory.Testory.then;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.parse.err.ForbiddenArrayElemError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
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

  @Before
  public void before() {
    createInjector(new AcceptanceTestModule()).injectMembers(this);
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
    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("someArray: [ 'abc' ] ;");
    scriptBuilder.addLine("result: [ someArray ] ;");

    script(fileSystem, scriptBuilder.build());

    buildWorker.run(asList("result"));
    userConsole.messages().assertContainsOnly(ForbiddenArrayElemError.class);
  }

  @Test
  public void empty_array_can_be_saved() throws IOException {
    script(fileSystem, "result : [];");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    then(fileSystem.filesFrom(artifactPath("result")), empty());
  }
}
