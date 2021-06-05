package org.smoothbuild.acceptance.cli.command;

import static java.lang.String.format;
import static org.smoothbuild.acceptance.CommandWithArgs.treeCommand;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.acceptance.cli.command.common.ValuesArgTestCase;
import org.smoothbuild.acceptance.testing.OneStringParameter;
import org.smoothbuild.acceptance.testing.ReturnAbc;
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
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String result
            String myValue
              String "abc"
          """);
    }

    @Test
    public void native_value_reference() throws Exception {
      this.createNativeJar(ReturnAbc.class);
      createUserModule(format("""
            @Native("%s.function")
            String returnAbc;
            result = returnAbc;
            """, ReturnAbc.class.getCanonicalName()));
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String result
            String returnAbc
          """);
    }

    @Test
    public void defined_function_call_with_argument() throws Exception {
      createUserModule("""
              myFunction(String element) = element;
              result = myFunction("abc");
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String result
            String myFunction()
              String "abc"
          """);
    }

    @Test
    public void native_function_call_with_argument() throws Exception {
      createNativeJar(OneStringParameter.class);
      createUserModule(format("""
            @Native("%s.function")
            String oneStringParameter(String value);
            result = oneStringParameter("abc");
            """, OneStringParameter.class.getCanonicalName()));
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String result
            String oneStringParameter()
              String(String value) _native_function
                String "org.smoothbuild.acceptance.testing."...
                Blob _native_module('{prj}/build.jar')
              String "abc"
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
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          MyStruct result
            MyStruct myStruct()
              String "abc"
          """);
    }

    @Test
    public void field_read() throws Exception {
      createUserModule("""
              MyStruct {
                String field
              }
              result = myStruct("abc").field;
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
          String result
            String .field
              MyStruct myStruct()
                String "abc"
          """);
    }

    @Test
    public void array_literal () throws Exception {
      createUserModule("""
              result = [ "abc", "def"];
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              [String] result
                [String] [String]
                  String "abc"
                  String "def"
              """);
    }

    @Test
    public void string_literal() throws Exception {
      createUserModule("""
              result = "abc";
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              String result
                String "abc"
              """);
    }

    @Test
    public void long_string_literal() throws Exception {
      createUserModule("""
              result = "01234567890123456789012345678901234567890123456789";
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              String result
                String "01234567890123456789012345678901234"...
              """);
    }

    @Test
    public void blob_literal() throws Exception {
      createUserModule("""
              result = 0x01;
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              Blob result
                Blob 0x01
              """);
    }

    @Test
    public void long_blob_literal() throws Exception {
      createUserModule("""
              result = 0x01234567890ABCDEF789012345678901234567890123456789;
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              Blob result
                Blob 0x01234567890abcdef789012345678901234...
              """);
    }

    @Test
    public void convert_computation() throws Exception {
      createUserModule("""
              [String] result = [];
              """);
      runSmoothTree("result");
      assertFinishedWithSuccess();
      assertSysOutContains("""
              [String] result
                [String] [String]<-[Nothing]
                  [Nothing] [Nothing]
              """);
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return treeCommand("result");
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return treeCommand("result");
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
      runSmooth(treeCommand(option, "result"));
    }
  }
}
