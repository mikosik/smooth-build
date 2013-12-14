package org.smoothbuild.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.parse.err.ForbiddenArrayElemTypeError;
import org.smoothbuild.parse.err.IncompatibleArrayElemsError;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestCase;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class ArraysSmoothTest extends IntegrationTestCase {
  @Test
  public void nested_arrays_are_forbidden() throws IOException {
    // given
    script("run : [ [ 'abc' ] ];");

    // when
    build("run");

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

    script(scriptBuilder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertContainsOnly(ForbiddenArrayElemTypeError.class);
  }

  @Test
  public void array_containing_string_and_file_is_forbidden() throws Exception {
    // given
    Path path = path("some/dir");
    fileSystem.createDir(path);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myFile: file(" + path + ") ;");
    scriptBuilder.addLine("run: [ myString, myFile ] ;");

    script(scriptBuilder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void array_containing_string_and_blob_is_forbidden() throws Exception {
    // given
    Path path = path("some/dir");
    fileSystem.createDir(path);

    ScriptBuilder scriptBuilder = new ScriptBuilder();
    scriptBuilder.addLine("myString: 'abc' ;");
    scriptBuilder.addLine("myBlob: file(" + path + ") | content ;");
    scriptBuilder.addLine("run: [ myString, myBlob ] ;");

    script(scriptBuilder.build());

    // when
    build("run");

    // then
    userConsole.messages().assertContainsOnly(IncompatibleArrayElemsError.class);
  }

  @Test
  public void empty_array_can_be_saved() throws IOException {
    // given
    script("run : [];");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = RESULTS_PATH.append(path("run"));
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.childNames(artifactPath)).isEmpty();
  }

  @Test
  public void empty_file_array_can_be_saved() throws IOException {
    // given
    Path path = path("some/dir");
    fileSystem.createDir(path);
    script("run : files(" + path + ");");

    // when
    build("run");

    // then
    userConsole.messages().assertNoProblems();

    Path artifactPath = RESULTS_PATH.append(path("run"));
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.childNames(artifactPath)).isEmpty();
  }
}
