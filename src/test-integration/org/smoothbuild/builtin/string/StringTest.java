package org.smoothbuild.builtin.string;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.artifactPath;
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
    script(fileSystem, "result: 'abc' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "abc");
  }

  @Test
  public void string_literal_with_escaped_tab() throws IOException {
    script(fileSystem, "result: '\\t' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\t");
  }

  @Test
  public void string_literal_with_escaped_backspace() throws IOException {
    script(fileSystem, "result: '\\b' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\b");
  }

  @Test
  public void string_literal_with_escaped_new_line() throws IOException {
    script(fileSystem, "result: '\\n' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\n");
  }

  @Test
  public void string_literal_with_escaped_carriage_return() throws IOException {
    script(fileSystem, "result: '\\r' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\r");
  }

  @Test
  public void string_literal_with_escaped_form_feed() throws IOException {
    script(fileSystem, "result: '\\f' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\f");
  }

  @Test
  public void string_literal_with_escaped_double_quotes() throws IOException {
    script(fileSystem, "result: '\\\"' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\"");
  }

  @Test
  public void string_literal_with_escaped_backslash() throws IOException {
    script(fileSystem, "result: '\\\\' ;");
    buildWorker.run(asList("result"));
    userConsole.messages().assertNoProblems();
    fileSystem.assertFileContains(artifactPath("result"), "\\");
  }
}
