package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.systemtest.CommandWithArgs.buildCommand;
import static org.smoothbuild.util.Strings.unlines;

import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.cli.command.BuildCommand;
import org.smoothbuild.systemtest.CommandWithArgs;
import org.smoothbuild.systemtest.SystemTestCase;
import org.smoothbuild.systemtest.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.systemtest.cli.command.common.LockFileTestCase;
import org.smoothbuild.systemtest.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.systemtest.cli.command.common.ValuesArgTestCase;
import org.smoothbuild.testing.nativefunc.ReportError;
import org.smoothbuild.testing.nativefunc.ReportInfo;
import org.smoothbuild.testing.nativefunc.ReportWarning;

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
      assertThat(exists(absolutePath(path)))
          .isFalse();
    }

    @Test
    public void build_command_clears_temporary_dir() throws Exception {
      String path = TEMPORARY_PATH.appendPart("file.txt").toString();
      createFile(path, "content");
      createUserModule("""
              syntactically incorrect script
              """);
      runSmoothBuild("result");
      assertFinishedWithError();
      assertThat(exists(absolutePath(path)))
          .isFalse();
    }
  }

  @Nested
  class _default_module extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return buildCommand("result");
    }
  }

  @Nested
  class _lock_file extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArg() {
      return buildCommand("result");
    }
  }

  @Nested
  class _value_args extends ValuesArgTestCase {
    @Override
    protected String commandName() {
      return BuildCommand.NAME;
    }

    @Override
    protected String sectionName() {
      return "Building";
    }
  }

  @Nested
  class _log_level_option extends LogLevelOptionTestCase {
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
        assertSysErrContains(unlines(
            "Invalid value for option '--show-tasks': Unknown matcher 'ILLEGAL'.",
            "",
            "Usage:"
        ));
      }
    }

    @Nested
    class _call_matcher extends SystemTestCase {
      private static final String DEFINED_FUNCTION_CALL = """
          myFunc() = "myLiteral";
          result = myFunc();
          """;
      private static final String DEFINED_CALL_TASK_HEADER = """
          myFunc()                                    build.smooth:2
          """;
      private static final String NATIVE_FUNCTION_CALL = """
          result = concat(["a"], ["b"]);
          """;
      private static final String NATIVE_CALL_TASK_HEADER = """
          concat()                                    build.smooth:1
          """;
      private static final String IF_FUNCTION_CALL = """
          result = if(true, "true", "false");
          """;
      private static final String IF_CALL_TASK_HEADER = """
          if()                                        build.smooth:1
          """;
      private static final String MAP_FUNCTION_CALL = """
          result = map([false, true], not);
          """;
      private static final String MAP_CALL_TASK_HEADER = """
          map()                                       build.smooth:1
          """;

      @Test
      public void shows_call_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenCallIsEnabled(DEFINED_FUNCTION_CALL, DEFINED_CALL_TASK_HEADER);
      }

      @Test
      public void hides_calls_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenCallIsDisabled(
            DEFINED_FUNCTION_CALL, DEFINED_CALL_TASK_HEADER);
      }

      @Test
      public void shows_call_to_nat_func_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenCallIsEnabled(NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      @Test
      public void hides_call_to_nat_func_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenCallIsDisabled(
            NATIVE_FUNCTION_CALL, NATIVE_CALL_TASK_HEADER);
      }

      @Test
      public void shows_call_to_internal_if_func_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenCallIsEnabled(IF_FUNCTION_CALL, IF_CALL_TASK_HEADER);
      }

      @Test
      public void hides_call_to_internal_if_func_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenCallIsDisabled(IF_FUNCTION_CALL, IF_CALL_TASK_HEADER);
      }

      @Test
      public void shows_call_to_internal_map_func_when_enabled() throws IOException {
        testThatTaskHeaderShownWhenCallIsEnabled(MAP_FUNCTION_CALL, MAP_CALL_TASK_HEADER);
      }

      @Test
      public void hides_call_to_internal_map_func_when_not_enabled() throws IOException {
        testThatTaskHeaderIsNotShownWhenCallIsDisabled(MAP_FUNCTION_CALL, MAP_CALL_TASK_HEADER);
      }

      private void testThatTaskHeaderShownWhenCallIsEnabled(String callDeclaration,
          String expectedHeaderToBeShown) throws IOException {
        createUserModule(callDeclaration);
        runSmooth(buildCommand("--show-tasks=call", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(expectedHeaderToBeShown);
      }

      private void testThatTaskHeaderIsNotShownWhenCallIsDisabled(String callDeclaration,
          String headerThatShouldNotBeShows) throws IOException {
        createUserModule(callDeclaration);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(headerThatShouldNotBeShows);
      }
    }

    @Nested
    class _combine_matcher extends SystemTestCase {
      private static final String COMBINE = """
          MyStruct {
            String myField
          }
          result = myStruct("abc");
          """;
      private static final String COMBINE_TASK_HEADER = """
          {}                                          build.smooth:1                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=combine", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(COMBINE_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(COMBINE_TASK_HEADER);
      }
    }

    @Nested
    class _const_matcher extends SystemTestCase {
      private static final String BLOB_CONST = """
          result = 0xABCD;
          """;
      private static final String BLOB_CONST_TASK_HEADER = """
          0xabcd                                      build.smooth:1
          """;
      private static final String INT_CONST = """
          result = 123;
          """;
      private static final String INT_CONST_TASK_HEADER = """
          123                                         build.smooth:1
          """;
      private static final String STRING_CONST = """
          result = "myLiteral";
          """;
      private static final String STRING_CONST_TASK_HEADER = """
          "myLiteral"                                 build.smooth:1
          """;

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

      private void testThatTaskHeaderShownWhenConstAreEnabled(String constDeclaration,
          String expectedTaskHeader) throws IOException {
        createUserModule(constDeclaration);
        runSmooth(buildCommand("--show-tasks=const", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(expectedTaskHeader);
      }

      private void testThatTaskHeaderIsNotShownWhenConstAreDisabled(String constDeclaration,
          String headerThatShouldNotBeShown) throws IOException {
        createUserModule(constDeclaration);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(headerThatShouldNotBeShown);
      }
    }

    @Nested
    class _convert_matcher extends SystemTestCase {
      private static final String CONVERT = """
          result = [
            [ 123 ],
            [],
          ];
          """;
      private static final String CONVERT_TASK_HEADER = """
          [Int] <- [Nothing]                          build.smooth:3                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(CONVERT);
        runSmooth(buildCommand("--show-tasks=convert", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(CONVERT_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(CONVERT);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(CONVERT_TASK_HEADER);
      }
    }

    @Nested
    class _invoke_matcher extends SystemTestCase {
      private static final String INVOKE = """
            result = not(true);
            """;
      private static final String INVOKE_TASK_HEADER = """
          not()~                                      slib.smooth:""";

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(INVOKE);
        runSmooth(buildCommand("--show-tasks=invoke", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(INVOKE_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(INVOKE);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(INVOKE_TASK_HEADER);
      }
    }

    @Nested
    class _order_matcher extends SystemTestCase {
      private static final String ORDER = """
          result = [
            123,
            456,
          ];
          """;
      private static final String ORDER_TASK_HEADER = """
          []                                          build.smooth:1                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=order", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(ORDER_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(ORDER_TASK_HEADER);
      }
    }

    @Nested
    class _select_matcher extends SystemTestCase {
      private static final String SELECT = """
            MyStruct {
              String myField
            }
            aStruct = myStruct("abc");
            result = aStruct.myField;
            """;
      private static final String SELECT_TASK_HEADER = """
          .myField                                    build.smooth:5                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(SELECT);
        runSmooth(buildCommand("--show-tasks=select", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(SELECT_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(SELECT);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(SELECT_TASK_HEADER);
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
        createUserModule("""
                Nothing reportError(String message);
                result = reportError("my-error-message");
                """);
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithError();
        assertSysOutDoesNotContain("my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format("""
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """, ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format("""
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """, ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("INFO: my-info-message");
      }
    }

    @Nested
    class is_error extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format("""
            @Native("%s")
            Nothing reportError(String message);
            result = reportError("my-error-message");
            """, ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format("""
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """, ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format("""
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """, ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_warning extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format("""
            @Native("%s")
            Nothing reportError(String message);
            result = reportError("my-error-message");
            """, ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format("""
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """, ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format("""
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """, ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_info extends SystemTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(format("""
            @Native("%s")
            Nothing reportError(String message);
            result = reportError("my-error-message");
            """, ReportError.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(format("""
            @Native("%s")
            String reportWarning(String message);
            result = reportWarning("my-warning-message");
            """, ReportWarning.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(format("""
            @Native("%s")
            String reportInfo(String message);
            result = reportInfo("my-info-message");
            """, ReportInfo.class.getCanonicalName()));
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("INFO: my-info-message");
      }
    }
  }

  @Nested
  class _reported_task_header_for extends SystemTestCase {
    @Test
    public void call() throws IOException {
      createUserModule("""
          myFunc() = "abc";
          result = myFunc();
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          myFunc()                                    build.smooth:2
          """);
    }

    @Test
    public void select() throws IOException {
      createUserModule("""
          MyStruct {
            String myField
          }
          result = myStruct("abc").myField;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          .myField                                    build.smooth:4                 exec
          """);
    }

    @Test
    public void func_reference() throws IOException {
      createUserModule("""
          result = if;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          if                                          internal
          """);
    }

    @Test
    public void literal_array() throws IOException {
      createUserModule("""
          result = [ "abc" ];
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          []                                          build.smooth:1                 exec
          """);
    }

    @Test
    public void literal_blob() throws IOException {
      createUserModule("""
          result = 0x0102;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          0x0102                                      build.smooth:1
          """);
    }

    @Test
    public void literal_string() throws IOException {
      createUserModule("""
          result = "abc";
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          "abc"                                       build.smooth:1
          """);
    }

    @Test
    public void value() throws IOException {
      createUserModule("""
          myValue = "abc";
          result = myValue;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
            "abc"                                       build.smooth:1
          """);
    }
  }
}
