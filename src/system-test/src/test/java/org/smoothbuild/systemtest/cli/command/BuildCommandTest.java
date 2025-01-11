package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;

import java.io.IOException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.build.BuildCommand;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestContext;
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
  class _basic extends SystemTestContext {
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
  class _filter_logs_option extends AbstractLogLevelOptionTestSuite {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(buildCommand(option, "result"));
    }
  }

  @Nested
  class _filter_tasks_option extends SystemTestContext {
    @Test
    void illegal_value_causes_error() throws IOException {
      createUserModule("""
                result = "abc";
                """);
      runSmooth(buildCommand("--filter-tasks=ILLEGAL", "result"));
      assertFinishedWithError();
      assertSystemErrContains(
          """
          Invalid value for option '--filter-tasks': Unknown matcher 'ILLEGAL'.

          Usage:""");
    }

    private static final String NATIVE_FUNCTION_CALL =
        """
        result = concat([["a"], ["b"]]);
        """;
    private static final String NATIVE_CALL_TASK_HEADER =
        """
        :vm:evaluate:invoke
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
      runSmooth(buildCommand("--filter-tasks=:vm:evaluate:invoke", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains(expectedHeaderToBeShown);
    }

    private void testThatTaskHeaderIsNotShownWhenInvokeIsDisabled(
        String callDeclaration, String headerThatShouldNotBeShows) throws IOException {
      createUserModule(callDeclaration);
      runSmooth(buildCommand("--filter-tasks=none", "result"));
      assertFinishedWithSuccess();
      assertSystemOutDoesNotContain(headerThatShouldNotBeShows);
    }

    private static final String ORDER =
        """
        result = [
          123,
          456,
        ];
        """;
    private static final String ORDER_TASK_HEADER = """
          :vm:evaluate:order
          """;

    @Test
    void shows_order_task_when_enabled() throws IOException {
      createUserModule(ORDER);
      runSmooth(buildCommand("--filter-tasks=:vm:evaluate:order", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains(ORDER_TASK_HEADER);
    }

    @Test
    void hides_order_task_when_not_enabled() throws IOException {
      createUserModule(ORDER);
      runSmooth(buildCommand("--filter-tasks=none", "result"));
      assertFinishedWithSuccess();
      assertSystemOutDoesNotContain(ORDER_TASK_HEADER);
    }
  }

  @Nested
  class _when_filter_stack_traces_option extends SystemTestContext {
    @Test
    void matches() throws IOException {
      createNativeJar(ReportError.class);
      createUserModule(format(
          """
              @Native("%s")
              A reportError<A>(String message);
              Int result = reportError("my-error-message");
              """,
          ReportError.class.getCanonicalName()));
      runSmooth(buildCommand("--filter-stack-traces=all", "result"));
      assertFinishedWithError();
      assertSystemOutContains("@ {project}/build.smooth:3 reportError");
    }

    @Test
    void not_matches() throws IOException {
      createNativeJar(ReportError.class);
      createUserModule(format(
          """
              @Native("%s")
              A reportError(String message);
              Int result = reportError("my-error-message");
              """,
          ReportError.class.getCanonicalName()));
      runSmooth(buildCommand("--filter-stack-traces=none", "result"));
      assertFinishedWithError();
      assertSystemOutDoesNotContain("@ {project}/build.smooth:3 reportError");
    }
  }

  @Nested
  class _when_filter_logs_option {
    @Nested
    class is_fatal extends SystemTestContext {
      @Test
      void then_error_log_is_not_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
                @Native("%s")
                A reportError(String message);
                Int result = reportError("my-error-message");
                """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--filter-logs=fatal", "result"));
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
        runSmooth(buildCommand("--filter-logs=fatal", "result"));
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
        runSmooth(buildCommand("--filter-logs=fatal", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("[INFO] my-info-message");
      }
    }

    @Nested
    class is_error extends SystemTestContext {
      @Test
      void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError<A>(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--filter-logs=error", "result"));
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
        runSmooth(buildCommand("--filter-logs=error", "result"));
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
        runSmooth(buildCommand("--filter-logs=error", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_warning extends SystemTestContext {
      @Test
      void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError<A>(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--filter-logs=warning", "result"));
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
        runSmooth(buildCommand("--filter-logs=warning", "result"));
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
        runSmooth(buildCommand("--filter-logs=warning", "result"));
        assertFinishedWithSuccess();
        assertSystemOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_info extends SystemTestContext {
      @Test
      void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format(
            """
            @Native("%s")
            A reportError<A>(String message);
            Int result = reportError("my-error-message");
            """,
            ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--filter-logs=info", "result"));
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
        runSmooth(buildCommand("--filter-logs=info", "result"));
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
        runSmooth(buildCommand("--filter-logs=info", "result"));
        assertFinishedWithSuccess();
        assertSystemOutContains("[INFO] my-info-message");
      }
    }
  }

  @Nested
  class _reported_task_header_for extends SystemTestContext {
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
      runSmooth(buildCommand("--filter-tasks=all", "result"));
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
      runSmooth(buildCommand("--filter-tasks=all", "result"));
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
      runSmooth(buildCommand("--filter-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSystemOutContains("""
          :evaluate:select
          """);
    }
  }
}
