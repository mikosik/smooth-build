package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.acceptance.AcceptanceTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.lang.function.def.err.DuplicateArgNameError;
import org.smoothbuild.lang.function.def.err.UnknownParamNameError;
import org.smoothbuild.testing.acceptance.AcceptanceTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;

public class ArgumentSyntaxTest {
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
  public void trailing_comma_is_allowed_in_argument_list() throws IOException {
    script(fileSystem, "run : toBlob(string='abc',) ;");
    buildWorker.run(asList("run"));
    userConsole.messages().assertNoProblems();
  }

  @Test
  public void two_arguments_with_the_same_name_are_not_allowed() throws IOException {
    script(fileSystem, "illegal : file(path='abc',path='cde') ;");
    buildWorker.run(asList("run"));
    userConsole.messages().assertContainsOnly(DuplicateArgNameError.class);
  }

  @Test
  public void argument_with_unknown_name_is_not_allowed() throws IOException {
    script(fileSystem, "illegal : file(unknown='abc') ;");
    buildWorker.run(asList("run"));
    userConsole.messages().assertContainsOnly(UnknownParamNameError.class);
  }
}
