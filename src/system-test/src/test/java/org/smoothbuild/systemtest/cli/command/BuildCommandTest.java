package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.smoothbuild.app.layout.Layout.ARTIFACTS_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.app.cli.command.BuildCommand;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.AbstractDefaultModuleTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLockFileTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractLogLevelOptionTestSuite;
import org.smoothbuild.systemtest.cli.command.common.AbstractValuesArgTestSuite;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportError;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportInfo;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReportWarning;
import org.smoothbuild.virtualmachine.testing.func.nativ.ReturnAbc;

public class BuildCommandTest {
  @Nested
  class _basic extends SystemTestCase {
    @Test
    public void build_command_clears_artifacts_dir() throws Exception {
      String path = ARTIFACTS_PATH.appendPart("file.txt").toString();
      createFile(path, "content");
      createUserModule("""
              syntactically incorrect script
              """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertThat(exists(absolutePath(path))).isFalse();
    }
  }

  @Nested
  class _default_module extends AbstractDefaultModuleTestSuite {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return buildCommand("result");
    }
  }

  @Nested
  class _lock_file extends AbstractLockFileTestSuite {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return buildCommand("result");
    }
  }

  @Nested
  class _value_args extends AbstractValuesArgTestSuite {
    @Override
    protected String commandName() {
      return BuildCommand.NAME;
    }
  }

  @Nested
  class _log_level_option extends AbstractLogLevelOptionTestSuite {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(buildCommand(option, "result"));
    }
  }

  @Nested
  class _show_tasks_option {
    @Nested
    class _basic extends SystemTestCase {
      @Test
      public void illegal_value_causes_error() throws IOException {
        createUserModule("""
                result = "abc";
                """);
        runSmooth(buildCommand("--show-tasks=ILLEGAL", "result"));
        assertFinishedWithError();
        assertSystemErrContains(
            """
            Invalid value for option '--show-tasks': Unknown matcher 'ILLEGAL'.

            Usage:""");
      }
    }

    @Nested
    class _call_matcher extends SystemTestCase {
      private static final String NATIVE_FUNCTION_CALL =
          """
          result = concat([["a"], ["b"]]);
          """;
      private static final String NATIVE_CALL_TASK_HEADER =
          """
          ::Evaluating::call::concat                                                 exec
          """;

      @Test
      public void shows_call_to_native_func_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenCallIsEnabled(NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      @Test
      public void hides_call_to_native_func_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenCallIsDisabled(
            NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      private void testThatTaskHeaderShownWhenCallIsEnabled(
          String callDeclaration, String expectedHeaderToBeShown) throws IOException {
        createUserModule(callDeclaration);
        runSmooth(buildCommand("--show-tasks=call", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(expectedHeaderToBeShown);
      }

      private void testThatTaskHeaderIsNotShownWhenCallIsDisabled(
          String callDeclaration, String headerThatShouldNotBeShows) throws IOException {
        createUserModule(callDeclaration);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(headerThatShouldNotBeShows);
      }
    }

    @Nested
    class _combine_matcher extends SystemTestCase {
      private static final String COMBINE =
          """
          MyStruct(
            String myField
          )
          result = MyStruct("abc");
          """;
      private static final String COMBINE_TASK_HEADER =
          """
          ::Evaluating::combine                                                      exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=tuple", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(COMBINE_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(COMBINE_TASK_HEADER);
      }
    }

    @Nested
    class _const_matcher extends SystemTestCase {
      private static final String BLOB_CONST = """
          result = 0xABCD;
          """;
      private static final String BLOB_CONST_TASK_HEADER =
          """
          ::Evaluating::const::Blob""";
      private static final String INT_CONST = """
          result = 123;
          """;
      private static final String INT_CONST_TASK_HEADER = """
          ::Evaluating::const::Int""";
      private static final String STRING_CONST = """
          result = "myLiteral";
          """;
      private static final String STRING_CONST_TASK_HEADER =
          """
          ::Evaluating::const::String""";

      @Test
      public void shows_blob_when_consts_enabled() throws IOException {
        testThatTaskHeaderShownWhenConstAreEnabled(BLOB_CONST, BLOB_CONST_TASK_HEADER);
      }

      @Test
      public void hides_blob_when_const_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenConstAreDisabled(BLOB_CONST, BLOB_CONST_TASK_HEADER);
      }

      @Test
      public void shows_int_when_consts_enabled() throws IOException {
        testThatTaskHeaderShownWhenConstAreEnabled(INT_CONST, INT_CONST_TASK_HEADER);
      }

      @Test
      public void hides_int_when_const_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenConstAreDisabled(INT_CONST, INT_CONST_TASK_HEADER);
      }

      @Test
      public void shows_string_when_consts_enabled() throws IOException {
        testThatTaskHeaderShownWhenConstAreEnabled(STRING_CONST, STRING_CONST_TASK_HEADER);
      }

      @Test
      public void hides_string_when_consts_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenConstAreDisabled(STRING_CONST, STRING_CONST_TASK_HEADER);
      }

      private void testThatTaskHeaderShownWhenConstAreEnabled(
          String constDeclaration, String expectedTaskHeader) throws IOException {
        createUserModule(constDeclaration);
        runSmooth(buildCommand("--show-tasks=const", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(expectedTaskHeader);
      }

      private void testThatTaskHeaderIsNotShownWhenConstAreDisabled(
          String constDeclaration, String headerThatShouldNotBeShown) throws IOException {
        createUserModule(constDeclaration);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(headerThatShouldNotBeShown);
      }
    }

    @Nested
    class _pick_matcher extends SystemTestCase {
      private static final String PICK =
          """
            result = elem([1, 2, 3], 0);
            """;
      private static final String PICK_TASK_HEADER =
          """
          ::Evaluating::pick                                                         exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(PICK);
        runSmooth(buildCommand("--show-tasks=pick", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(PICK_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(PICK);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(PICK_TASK_HEADER);
      }
    }

    @Nested
    class _order_matcher extends SystemTestCase {
      private static final String ORDER =
          """
          result = [
            123,
            456,
          ];
          """;
      private static final String ORDER_TASK_HEADER =
          """
          ::Evaluating::order                                                        exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=array", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(ORDER_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(ORDER_TASK_HEADER);
      }
    }

    @Nested
    class _select_matcher extends SystemTestCase {
      private static final String SELECT =
          """
            MyStruct(
              String myField
            )
            aStruct = MyStruct("abc");
            result = aStruct.myField;
            """;
      private static final String SELECT_TASK_HEADER =
          """
          ::Evaluating::select                                                       exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(SELECT);
        runSmooth(buildCommand("--show-tasks=select", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(SELECT_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(SELECT);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(SELECT_TASK_HEADER);
      }
    }
  }

  @Nested
  class _when_log_level_option {
    @Nested
    class is_fatal extends SystemTestCase {
      @Test
      public void then_error_log_is_not_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(
            """
                Nothing reportError(String message);
                result = reportError("my-error-message");
                """);
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithError();
        assertSystemOutDoesNotContain("my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """,
            ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("[WARNING] my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """,
            ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("[INFO] my-info-message");
      }
    }

    @Nested
    class is_error extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithError();
        assertSystemOutContains("[ERROR] my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """,
            ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """,
            ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_warning extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithError();
        assertSystemOutContains("[ERROR] my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """,
            ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains("[WARNING] my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """,
            ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_info extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithError();
        assertSystemOutContains("[ERROR] my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """,
            ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains("[WARNING] my-warning-message");
      }

      @Test
      public void then_info_log_is_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format(
            """
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """,
            ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains("[INFO] my-info-message");
      }
    }
  }

  @Nested
  class _reported_task_header_for extends SystemTestCase {
    @Test
    public void native_call() throws IOException {
      createNativeJar(ReturnAbc.class);
      createUserModule(format(
          """
          @Native("%s")
          String myFunc();
          result = myFunc();
          """,
          ReturnAbc.class.getCanonicalName()));
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains(
          """
          ::Evaluating::call::myFunc                                                 exec
          """);
    }

    @Test
    public void select() throws IOException {
      createUserModule(
          """
          MyStruct(
            String myField
          )
          result = MyStruct("abc").myField;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains(
          """
          ::Evaluating::select                                                       exec
          """);
    }

    @Test
    public void func_reference() throws IOException {
      createUserModule("""
          myFunc() = 7;
          result = myFunc;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          ::Evaluating::const::()->Int""");
    }

    @Test
    public void value_reference() throws IOException {
      createUserModule("""
          myValue = "abc";
          result = myValue;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          ::Evaluating::const::()->String""");
    }

    @Test
    public void literal_array() throws IOException {
      createUserModule("""
          result = ["abc"];
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains(
          """
          ::Evaluating::order                                                        exec
          """);
    }

    @Test
    public void literal_blob() throws IOException {
      createUserModule("""
          result = 0x0102;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          ::Evaluating::const::Blob""");
    }

    @Test
    public void literal_string() throws IOException {
      createUserModule("""
          result = "abc";
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          ::Evaluating::const::String""");
    }

    @Test
    public void literal_int() throws IOException {
      createUserModule("""
          result = 17;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          ::Evaluating::const::Int""");
    }
  }
}
