package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.BuildCommand;
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
    void build_command_clears_artifacts_dir() throws Exception {
      String path = ARTIFACTS_PATH.resolve("file.txt").toString();
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
      void illegal_value_causes_error() throws IOException {
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
    class _invoke_matcher extends SystemTestCase {
      private static final String NATIVE_FUNCTION_CALL =
          """
          result = concat([["a"], ["b"]]);
          """;
      private static final String NATIVE_CALL_TASK_HEADER =
          """
          :evaluate:invoke
          """;

      @Test
      void shows_call_to_native_func_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenInvokeIsEnabled(NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      @Test
      void hides_call_to_native_func_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenInvokeIsDisabled(
            NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      private void testThatTaskHeaderShownWhenInvokeIsEnabled(
          String callDeclaration, String expectedHeaderToBeShown) throws IOException {
        createUserModule(callDeclaration);
        runSmooth(buildCommand("--show-tasks=invoke", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(expectedHeaderToBeShown);
      }

      private void testThatTaskHeaderIsNotShownWhenInvokeIsDisabled(
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
          MyStruct {
            String myField
          }
          result = MyStruct("abc");
          """;
      private static final String COMBINE_TASK_HEADER =
          """
          :evaluate:combine
          """;

      @Test
      void shows_when_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=combine", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(COMBINE_TASK_HEADER);
      }

      @Test
      void hides_when_not_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain(COMBINE_TASK_HEADER);
      }
    }

    @Nested
    class _pick_matcher extends SystemTestCase {
      private static final String PICK =
          """
            result = elem([1, 2, 3], 0);
            """;
      private static final String PICK_TASK_HEADER = """
          :evaluate:pick
          """;

      @Test
      void shows_when_enabled() throws IOException {
        createUserModule(PICK);
        runSmooth(buildCommand("--show-tasks=pick", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(PICK_TASK_HEADER);
      }

      @Test
      void hides_when_not_enabled() throws IOException {
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
      private static final String ORDER_TASK_HEADER = """
          :evaluate:order
          """;

      @Test
      void shows_when_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=order", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(ORDER_TASK_HEADER);
      }

      @Test
      void hides_when_not_enabled() throws IOException {
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
            MyStruct {
              String myField
            }
            aStruct = MyStruct("abc");
            result = aStruct.myField;
            """;
      private static final String SELECT_TASK_HEADER = """
          :evaluate:select
          """;

      @Test
      void shows_when_enabled() throws IOException {
        createUserModule(SELECT);
        runSmooth(buildCommand("--show-tasks=select", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains(SELECT_TASK_HEADER);
      }

      @Test
      void hides_when_not_enabled() throws IOException {
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
      void then_error_log_is_not_shown() throws IOException {
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
      void then_warning_log_is_not_shown() throws IOException {
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
      void then_info_log_is_not_shown() throws IOException {
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
      void then_error_log_is_shown() throws IOException {
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
      void then_warning_log_is_not_shown() throws IOException {
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
      void then_info_log_is_not_shown() throws IOException {
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
      void then_error_log_is_shown() throws IOException {
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
      void then_warning_log_is_shown() throws IOException {
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
      void then_info_log_is_not_shown() throws IOException {
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
      void then_error_log_is_shown() throws IOException {
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
      void then_warning_log_is_shown() throws IOException {
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
      void then_info_log_is_shown() throws IOException {
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
    void native_call() throws IOException {
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
      assertSystemOutContains("""
          :evaluate:invoke
          """);
    }

    @Test
    void func_call() throws IOException {
      createUserModule("""
          myFunc() = 7;
          result = myFunc();
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          :vm:inline""");
    }

    @Test
    void select() throws IOException {
      createUserModule(
          """
          MyStruct {
            String myField
          }
          result = MyStruct("abc").myField;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          :evaluate:select
          """);
    }
  }
}
