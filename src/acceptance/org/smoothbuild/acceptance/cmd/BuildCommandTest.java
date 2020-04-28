package org.smoothbuild.acceptance.cmd;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.SmoothConstants.ARTIFACTS_PATH;
import static org.smoothbuild.SmoothConstants.TEMPORARY_PATH;

import java.io.File;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;
import org.smoothbuild.acceptance.testing.TempFilePath;
import org.smoothbuild.cli.BuildCommand;

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
  class ShowTasksOption extends ShowTasksOptionTestCase {
    @Override
    protected void whenSmoothCommandWithOption(String option) {
      whenSmooth("build", option, "result");
    }
  }
}
