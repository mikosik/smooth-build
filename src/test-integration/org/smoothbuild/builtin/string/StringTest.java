package org.smoothbuild.builtin.string;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.io.fs.base.Path.path;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.ARTIFACTS_PATH;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class StringTest {
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
  public void string_literal() throws IOException {
    // given
    script(fileSystem, "run : 'abc' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "abc");
  }

  @Test
  public void string_literal_with_escaped_tab() throws IOException {
    // given
    script(fileSystem, "run : '\\t' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\t");
  }

  @Test
  public void string_literal_with_escaped_backspace() throws IOException {
    // given
    script(fileSystem, "run : '\\b' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\b");
  }

  @Test
  public void string_literal_with_escaped_new_line() throws IOException {
    // given
    script(fileSystem, "run : '\\n' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\n");
  }

  @Test
  public void string_literal_with_escaped_carriage_return() throws IOException {
    // given
    script(fileSystem, "run : '\\r' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\r");
  }

  @Test
  public void string_literal_with_escaped_form_feed() throws IOException {
    // given
    script(fileSystem, "run : '\\f' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\f");
  }

  @Test
  public void string_literal_with_escaped_double_quotes() throws IOException {
    // given
    script(fileSystem, "run : '\\\"' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\"");
  }

  @Test
  public void string_literal_with_escaped_backslash() throws IOException {
    // given
    script(fileSystem, "run : '\\\\' ;");

    // when
    buildWorker.run(asList("run"));

    // then
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(ARTIFACTS_PATH.append(path("run")), "\\");
  }
}
