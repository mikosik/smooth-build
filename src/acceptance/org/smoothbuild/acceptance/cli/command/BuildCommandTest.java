package org.smoothbuild.acceptance.cli.command;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;
import static org.smoothbuild.util.Strings.unlines;

import java.io.File;
import java.io.IOException;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.cli.command.common.DefaultModuleTestCase;
import org.smoothbuild.acceptance.cli.command.common.FunctionsArgTestCase;
import org.smoothbuild.acceptance.cli.command.common.LogLevelOptionTestCase;
import org.smoothbuild.acceptance.testing.TempFilePath;
import org.smoothbuild.cli.command.BuildCommand;

@SuppressWarnings("ClassCanBeStatic")
public class BuildCommandTest extends AcceptanceTestCase {
  @Test
  public void temp_file_is_deleted_after_build_execution() throws Exception {
    givenNativeJar(TempFilePath.class);
    givenScript(
        "  String tempFilePath();    ",
        "  result = tempFilePath();  ");
    whenSmoothBuild("result");
    thenFinishedWithSuccess();
    assertThat(new File(artifactContent("result")).exists())
        .isFalse();
  }

  @Test
  public void build_command_clears_artifacts_dir() throws Exception {
    String path = ARTIFACTS_PATH.value() + "/file.txt";
    givenFile(path, "content");
    givenScript(
        "  syntactically incorrect script  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    assertThat(file(path).exists())
        .isFalse();
  }

  @Test
  public void build_command_clears_temporary_dir() throws Exception {
    String path = TEMPORARY_PATH.value() + "/file.txt";
    givenFile(path, "content");
    givenScript(
        "  syntactically incorrect script  ");
    whenSmoothBuild("result");
    thenFinishedWithError();
    assertThat(file(path).exists())
        .isFalse();
  }

  @Nested
  class DefaultModule extends DefaultModuleTestCase {
    @Override
    protected String[] commandNameWithArgument() {
      return new String[] { BuildCommand.NAME, "result" };
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
      whenSmooth("build", option, "result");
    }
  }

  @Nested
  class show_tasks_option extends AcceptanceTestCase {
    @Test
    public void illegal_value_causes_error() throws IOException {
      givenScript("result = 'abc';");
      whenSmooth("build", "--show-tasks=ILLEGAL", "result");
      thenFinishedWithError();
      thenSysErrContains(unlines(
          "Invalid value for option '--show-tasks': Unknown matcher 'ILLEGAL'.",
          "",
          "Usage:"
      ));
    }

    @Nested
    class literal_matcher extends AcceptanceTestCase {
      @Test
      public void shows_literals_when_enabled() throws IOException {
        givenScript("result = 'myLiteral';");
        whenSmooth("build", "--show-tasks=literal", "result");
        thenFinishedWithSuccess();
        thenSysOutContains(quotesX2(unlines(
            "'myLiteral'                                                build.smooth:1",
            ""
        )));
      }

      @Test
      public void hides_literals_when_not_enabled() throws IOException {
        givenScript("result = 'myLiteral';");
        whenSmooth("build", "--show-tasks=none", "result");
        thenFinishedWithSuccess();
        thenSysOutDoesNotContain(quotesX2(unlines(
            "'myLiteral'                                                build.smooth:1",
            ""
        )));
      }
    }
  }
}
