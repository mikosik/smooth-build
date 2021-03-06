package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static java.nio.file.Files.exists;
import static org.smoothbuild.acceptance.CommandWithArgs.buildCommand;
import static org.smoothbuild.install.ProjectPaths.ARTIFACTS_PATH;
import static org.smoothbuild.install.ProjectPaths.TEMPORARY_PATH;
import static org.smoothbuild.util.Strings.unlines;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.CommandWithArgs;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.FunctionsArgTestCase;
import org.smoothbuild.acceptance.cli.command.common.LockFileTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.acceptance.testing.ReportError;
import org.smoothbuild.acceptance.testing.ReportInfo;
import org.smoothbuild.acceptance.testing.ReportWarning;
import org.smoothbuild.acceptance.testing.TempFilePath;
import org.smoothbuild.cli.command.BuildCommand;

@SuppressWarnings("ClassCanBeStatic")
public class BuildCommandTest {
  @Nested
  class basic extends AcceptanceTestCase {
    @Test
    public void temp_file_is_deleted_after_build_execution() throws Exception {
      createNativeJar(TempFilePath.class);
      createUserModule(
          "  String tempFilePath();    ",
          "  result = tempFilePath();  ");
      runSmoothBuild("result");
      assertFinishedWithSuccess();
      assertThat(new File(artifactFileContentAsString("result")).exists())
          .isFalse();
    }

    @Test
    public void build_command_clears_artifacts_dir() throws Exception {
      String path = ARTIFACTS_PATH.appendPart("file.txt").toString();
      createFile(path, "content");
      createUserModule(
          "  syntactically incorrect script  ");
      runSmoothBuild("result");
      assertFinishedWithError();
      assertThat(exists(absolutePath(path)))
          .isFalse();
    }

    @Test
    public void build_command_clears_temporary_dir() throws Exception {
      String path = TEMPORARY_PATH.appendPart("file.txt").toString();
      createFile(path, "content");
      createUserModule(
          "  syntactically incorrect script  ");
      runSmoothBuild("result");
      assertFinishedWithError();
      assertThat(exists(absolutePath(path)))
          .isFalse();
    }
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return buildCommand("result");
    }
  }

  @Nested
  class LockFile extends LockFileTestCase {
    @Override
    protected CommandWithArgs commandNameWithArgument() {
      return buildCommand("result");
    }
  }

  @Nested
  class FunctionArgs extends FunctionsArgTestCase {
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
  class LogLevelOption extends LogLevelOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      runSmooth(buildCommand(option, "result"));
    }
  }

  @Nested
  class show_tasks_option {
    @Nested
    class basic extends AcceptanceTestCase {
      @Test
      public void illegal_value_causes_error() throws IOException {
        createUserModule("result = 'abc';");
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
    class literal_matcher extends AcceptanceTestCase {
      private static final String LITERAL_TASK_HEADER =
          "'myLiteral'                              build.smooth:1";

      @Test
      public void shows_literals_when_enabled() throws IOException {
        createUserModule("result = 'myLiteral';");
        runSmooth(buildCommand("--show-tasks=literal", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(quotesX2(LITERAL_TASK_HEADER));
      }

      @Test
      public void hides_literals_when_not_enabled() throws IOException {
        createUserModule("result = 'myLiteral';");
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(quotesX2(LITERAL_TASK_HEADER));
      }
    }

    @Nested
    class call_matcher extends AcceptanceTestCase {
      private static final String CALL_TASK_HEADER =
          "result                                   command line                   group";
      private static final String NATIVE_CALL_TASK_HEADER =
          "concat                                   build.smooth:1";

      @Test
      public void shows_call_when_enabled() throws IOException {
        createUserModule("result = 'myLiteral';");
        runSmooth(buildCommand("--show-tasks=call", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(CALL_TASK_HEADER);
      }

      @Test
      public void hides_calls_when_not_enabled() throws IOException {
        createUserModule("result = 'myLiteral';");
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(CALL_TASK_HEADER);
      }

      @Test
      public void shows_native_call_when_enabled() throws IOException {
        createUserModule("result = concat(['a'], ['b']);");
        runSmooth(buildCommand("--show-tasks=call", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(NATIVE_CALL_TASK_HEADER);
      }

      @Test
      public void hides_native_calls_when_not_enabled() throws IOException {
        createUserModule("result = concat(['a'], ['b']);");
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(NATIVE_CALL_TASK_HEADER);
      }
    }

    @Nested
    class conversion_matcher extends AcceptanceTestCase {
      private static final String CONVERSION_TASK_HEADER =
          "[String]<-[Nothing]                      build.smooth:1";

      @Test
      public void shows_conversion_when_enabled() throws IOException {
        createUserModule("[String] result = [];");
        runSmooth(buildCommand("--show-tasks=conversion", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains(CONVERSION_TASK_HEADER);
      }

      @Test
      public void hides_conversion_when_not_enabled() throws IOException {
        createUserModule("result = 'myLiteral';");
        runSmooth(buildCommand("--show-tasks=none", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain(CONVERSION_TASK_HEADER);
      }
    }
  }

  @Nested
  class when_log_level_option {
    @Nested
    class is_fatal extends AcceptanceTestCase {
      @Test
      public void then_error_log_is_not_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(
            "  Nothing reportError(String message);         ",
            "  result = reportError('my-error-message');    ");
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithError();
        assertSysOutDoesNotContain("my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(
            "  String reportWarning(String message);            ",
            "  result = reportWarning('my-warning-message');    ");
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(
            "  String reportInfo(String message);         ",
            "  result = reportInfo('my-info-message');    ");
        runSmooth(buildCommand("--log-level=fatal", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("INFO: my-info-message");
      }
    }

    @Nested
    class is_error extends AcceptanceTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(
            "  Nothing reportError(String message);         ",
            "  result = reportError('my-error-message');    ");
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_not_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(
            "  String reportWarning(String message);            ",
            "  result = reportWarning('my-warning-message');    ");
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(
            "  String reportInfo(String message);         ",
            "  result = reportInfo('my-info-message');    ");
        runSmooth(buildCommand("--log-level=error", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_warning extends AcceptanceTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(
            "  Nothing reportError(String message);         ",
            "  result = reportError('my-error-message');    ");
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(
            "  String reportWarning(String message);            ",
            "  result = reportWarning('my-warning-message');    ");
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_not_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(
            "  String reportInfo(String message);         ",
            "  result = reportInfo('my-info-message');    ");
        runSmooth(buildCommand("--log-level=warning", "result"));
        assertFinishedWithSuccess();
        assertSysOutDoesNotContain("my-info-message");
      }
    }

    @Nested
    class is_info extends AcceptanceTestCase {
      @Test
      public void then_error_log_is_shown() throws IOException {
        createNativeJar(ReportError.class);
        createUserModule(
            "  Nothing reportError(String message);         ",
            "  result = reportError('my-error-message');    ");
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithError();
        assertSysOutContains("ERROR: my-error-message");
      }

      @Test
      public void then_warning_log_is_shown() throws IOException {
        createNativeJar(ReportWarning.class);
        createUserModule(
            "  String reportWarning(String message);            ",
            "  result = reportWarning('my-warning-message');    ");
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("WARNING: my-warning-message");
      }

      @Test
      public void then_info_log_is_shown() throws IOException {
        createNativeJar(ReportInfo.class);
        createUserModule(
            "  String reportInfo(String message);         ",
            "  result = reportInfo('my-info-message');    ");
        runSmooth(buildCommand("--log-level=info", "result"));
        assertFinishedWithSuccess();
        assertSysOutContains("INFO: my-info-message");
      }
    }
  }
}
