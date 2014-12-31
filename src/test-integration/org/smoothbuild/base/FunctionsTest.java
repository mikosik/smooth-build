package org.smoothbuild.base;

import static com.google.inject.Guice.createInjector;
import static java.util.Arrays.asList;
import static org.smoothbuild.testing.integration.IntegrationTestUtils.script;

import java.io.IOException;

import javax.inject.Inject;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.cli.work.BuildWorker;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.parse.err.CycleInCallGraphError;
import org.smoothbuild.parse.err.OverridenBuiltinFunctionError;
import org.smoothbuild.testing.integration.IntegrationTestModule;
import org.smoothbuild.testing.io.fs.base.FakeFileSystem;
import org.smoothbuild.testing.message.FakeUserConsole;
import org.smoothbuild.testing.parse.ScriptBuilder;

public class FunctionsTest {
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
  public void overriding_core_function_is_forbidden() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("file: 'abc';");
    script(fileSystem, builder.build());

    buildWorker.run(asList("file"));

    userConsole.messages().assertContainsOnly(OverridenBuiltinFunctionError.class);
  }

  @Test
  public void direct_function_recursion_is_forbidden() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("function1: function1;");
    script(fileSystem, builder.build());

    buildWorker.run(asList("function1"));

    userConsole.messages().assertContainsOnly(CycleInCallGraphError.class);
  }

  @Test
  public void indirect_function_recursion_with_two_steps_is_forbidden() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("function1: function2;");
    builder.addLine("function2: function1;");
    script(fileSystem, builder.build());

    buildWorker.run(asList("function1"));

    userConsole.messages().assertContainsOnly(CycleInCallGraphError.class);
  }

  @Test
  public void indirect_recursion_with_three_steps_is_forbidden() throws IOException {
    ScriptBuilder builder = new ScriptBuilder();
    builder.addLine("function1: function2;");
    builder.addLine("function2: function3;");
    builder.addLine("function3: function1;");
    script(fileSystem, builder.build());

    buildWorker.run(asList("function1"));

    userConsole.messages().assertContainsOnly(CycleInCallGraphError.class);
  }
}
