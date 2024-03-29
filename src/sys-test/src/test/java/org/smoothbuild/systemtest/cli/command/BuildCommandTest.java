package org.smoothbuild.systemtest.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.lang.String.format;
import static java.nio.file.Files.exists;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
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
import org.smoothbuild.testing.func.nativ.ReportError;
import org.smoothbuild.testing.func.nativ.ReportInfo;
import org.smoothbuild.testing.func.nativ.ReportWarning;
import org.smoothbuild.testing.func.nativ.ReturnAbc;

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

    @Override
    protected String sectionName() {
      return "Building";
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
        assertSysErrContains("""
            Invalid value for option '--show-tasks': Unknown matcher 'ILLEGAL'.
            
            Usage:"""
        );
      }
    }

    @Nested
    class _call_matcher extends SystemTestCase {
      private static final String NATIVE_FUNCTION_CALL = """
          result = concat([["a"], ["b"]]);
          """;
      private static final String NATIVE_CALL_TASK_HEADER = """
          concat()                                    build.smooth:1                 exec
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
          MyStruct(
            String myField
          )
          result = MyStruct("abc");
          """;
      private static final String COMBINE_TASK_HEADER = """
          (,)                                         build.smooth:1                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(COMBINE);
        runSmooth(buildCommand("--show-tasks=tuple", "result"));
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
          Blob                                        build.smooth:1
          """;
      private static final String INT_CONST = """
          result = 123;
          """;
      private static final String INT_CONST_TASK_HEADER = """
          Int                                         build.smooth:1
          """;
      private static final String STRING_CONST = """
          result = "myLiteral";
          """;
      private static final String STRING_CONST_TASK_HEADER = """
          String                                      build.smooth:1
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
    class _pick_matcher extends SystemTestCase {
      private static final String PICK = """
            result = elem([1, 2, 3], 0);
            """;
      private static final String PICK_TASK_HEADER = """
          [].                                         unknown                        exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(PICK);
        runSmooth(buildCommand("--show-tasks=pick", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(PICK_TASK_HEADER);
      }

      @Test
      public void hides_when_not_enabled() throws IOException {
        createUserModule(PICK);
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(PICK_TASK_HEADER);
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
          [,]                                         build.smooth:1                 exec
          """;

      @Test
      public void shows_when_enabled() throws IOException {
        createUserModule(ORDER);
        runSmooth(buildCommand("--show-tasks=array", "result"));
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
            MyStruct(
              String myField
            )
            aStruct = MyStruct("abc");
            result = aStruct.myField;
            """;
      private static final String SELECT_TASK_HEADER = """
          {}.                                         build.smooth:5                 exec
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
            A reportError(String message);
            Int result = reportError("my-error-message");
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
            A reportError(String message);
            Int result = reportError("my-error-message");
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
            A reportError(String message);
            Int result = reportError("my-error-message");
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
    public void native_call() throws IOException {
      createNativeJar(ReturnAbc.class);
      createUserModule(format("""
          @Native("%s")
          String myFunc();
          result = myFunc();
          """, ReturnAbc.class.getCanonicalName()));
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          myFunc()                                    build.smooth:3                 exec
          """);
    }

    @Test
    public void select() throws IOException {
      createUserModule("""
          MyStruct(
            String myField
          )
          result = MyStruct("abc").myField;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          {}.                                         build.smooth:4                 exec
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
      assertSysOutContains("""
          myFunc                                      build.smooth:1
          """);
    }

    @Test
    public void value_reference() throws IOException {
      createUserModule("""
          myValue = "abc";
          result = myValue;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          myValue                                     build.smooth:1
          """);
    }

    @Test
    public void literal_array() throws IOException {
      createUserModule("""
          result = ["abc"];
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          [,]                                         build.smooth:1                 exec
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
          Blob                                        build.smooth:1
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
          String                                      build.smooth:1
          """);
    }

    @Test
    public void literal_int() throws IOException {
      createUserModule("""
          result = 17;
          """);
      runSmooth(buildCommand("--show-tasks=all", "result"));
      assertFinishedWithSuccess();
      assertSysOutContains("""
          Int                                         build.smooth:1
          """);
    }
  }
}
