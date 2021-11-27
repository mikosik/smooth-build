package org.smoothbuild.acceptance.cli.command;

import static java.lang.String.format;
import static org.smoothbuild.acceptance.CommandWithArgs.planCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.acceptance.cli.command.common.ValuesArgTestCase;
import org.smoothbuild.acceptance.testing.ReturnAbc;
import org.smoothbuild.acceptance.testing.StringIdentity;
import org.smoothbuild.cli.command.PlanCommand;

public class PlanCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void defined_value_reference() throws Exception {
      createUserModule("""
              myValue = "abc";
              result = myValue;
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void defined_function_call_with_argument() throws Exception {
      createUserModule("""
              myFunction(String element) = element;
              result = myFunction("abc");
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void native_function_call_with_argument() throws Exception {
      createNativeJar(StringIdentity.class);
      createUserModule(format("""
            @Native("%s.function")
            String stringIdentity(String value);
            result = stringIdentity("abc");
            """, StringIdentity.class.getCanonicalName()));
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void native_function_call_without_argument() throws Exception {
      createNativeJar(ReturnAbc.class);
      createUserModule(format("""
            @Native("%s.function")
            String returnAbc();
            result = returnAbc();
            """, ReturnAbc.class.getCanonicalName()));
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void constructor_call() throws Exception {
      createUserModule("""
              MyStruct {
                String field
              }
              result = myStruct("abc");
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          {String} building-evaluation
            {String}() result
          """);
    }

    @Test
    public void select() throws Exception {
      createUserModule("""
              MyStruct {
                String field
              }
              result = myStruct("abc").field;
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void array_literal () throws Exception {
      createUserModule("""
              result = [ "abc", "def"];
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          [String] building-evaluation
            [String]() result
          """);
    }

    @Test
    public void string_literal() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void long_string_literal() throws Exception {
      createUserModule("""
              result = "01234567890123456789012345678901234567890123456789";
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String building-evaluation
            String() result
          """);
    }

    @Test
    public void blob_literal() throws Exception {
      createUserModule("""
              result = 0x01;
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          Blob building-evaluation
            Blob() result
          """);
    }

    @Test
    public void long_blob_literal() throws Exception {
      createUserModule("""
              result = 0x01234567890ABCDEF789012345678901234567890123456789;
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          Blob building-evaluation
            Blob() result
          """);
    }

    @Test
    public void int_literal() throws Exception {
      createUserModule("""
              result = -1234;
              """);
      runSmoothPlan("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          Int building-evaluation
            Int() result
          """);
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return planCommand("result");
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return planCommand("result");
    }
  }

  @Nested
  class FunctionArgs extends ValuesArgTestCase {
    @Override
    protected String commandName() {
      return PlanCommand.NAME;
    }

    @Override
    protected String sectionName() {
      return "Creating execution plan";
    }
  }

  @Nested
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(planCommand(option, "result"));
    }
  }
}
