package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class HelpCommandTest extends AcceptanceTestCase {
  @Test
  public void help_command_prints_general_help() throws Exception {
    whenSmoothHelp();
    thenReturnedCode(0);
    then(output(), containsString(generalHelp()));
  }

  private static String generalHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth <command> <arg>...\n");
    builder.append("\n");
    builder.append("All available commands are:\n");
    builder.append("  build   Build artifact(s) by running specified function(s)\n");
    builder.append(
        "  clean   Remove all cached values and artifacts calculated during previous builds\n");
    builder.append("  help    Print help about given command\n");
    return builder.toString();
  }

  @Test
  public void help_build_command_prints_help_for_build() throws Exception {
    whenSmoothHelp("build");
    thenReturnedCode(0);
    then(output(), containsString(buildHelp()));
  }

  private static String buildHelp() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth build <function>...\n");
    builder.append("\n");
    builder.append("Build artifact(s) by running specified function(s)\n");
    builder.append("\n");
    builder.append("  <function>  function which execution result is saved as artifact\n");
    return builder.toString();
  }

  @Test
  public void help_clean_command_prints_help_for_clean() throws Exception {
    whenSmoothHelp("clean");
    thenReturnedCode(0);
    then(output(), containsString(buildClean()));
  }

  private static String buildClean() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth clean\n");
    builder.append("\n");
    builder.append("Remove all cached values and artifacts calculated during previous builds\n");
    return builder.toString();
  }
}
