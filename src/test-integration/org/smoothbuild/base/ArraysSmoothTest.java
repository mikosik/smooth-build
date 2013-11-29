package org.smoothbuild.base;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.io.fs.base.Path.path;

import java.io.IOException;

import org.junit.Test;
import org.smoothbuild.io.fs.base.Path;
import org.smoothbuild.io.fs.base.PathState;
import org.smoothbuild.parse.err.SyntaxError;
import org.smoothbuild.testing.integration.IntegrationTestCase;

public class ArraysSmoothTest extends IntegrationTestCase {
  @Test
  public void nested_arrays_are_forbidden() throws IOException {
    // given
    script("run : [ [ 'abc' ] ];");

    // when
    build("run");

    // then
    userConsole.assertOnlyProblem(SyntaxError.class);
  }

  @Test
  public void empty_array_can_be_saved() throws IOException {
    // given
    script("run : [];");

    // when
    build("run");

    // then
    userConsole.assertNoProblems();

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
    userConsole.assertNoProblems();

    Path artifactPath = RESULTS_PATH.append(path("run"));
    assertThat(fileSystem.pathState(artifactPath)).isEqualTo(PathState.DIR);
    assertThat(fileSystem.childNames(artifactPath)).isEmpty();
  }
}
