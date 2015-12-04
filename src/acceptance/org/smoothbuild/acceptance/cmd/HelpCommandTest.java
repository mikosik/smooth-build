package org.smoothbuild.acceptance.cmd;

import static org.hamcrest.Matchers.containsString;
import static org.testory.Testory.then;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class HelpCommandTest extends AcceptanceTestCase {
  @Test
  public void help_command_prints_general_help() throws Exception {
    whenSmoothHelp();
    thenFinishedWithSuccess();
    then(output(), containsString(generalHelp()));
  }

  private static String generalHelp() {
    return "usage: smooth <command> <arg>...\n"
        + "\n"
        + "All available commands are:\n"
        + "  build   Build artifact(s) by running specified function(s)\n"
        + "  clean   Remove all cached values and artifacts calculated during previous builds\n"
        + "  help    Print help about given command\n";
  }

  @Test
  public void help_build_command_prints_help_for_build() throws Exception {
    whenSmoothHelp("build");
    thenFinishedWithSuccess();
    then(output(), containsString(buildHelp()));
  }

  private static String buildHelp() {
    return "usage: smooth build <function>...\n"
        + "\n"
        + "Build artifact(s) by running specified function(s)\n"
        + "\n"
        + "  <function>  function which execution result is saved as artifact\n";
  }

  @Test
  public void help_clean_command_prints_help_for_clean() throws Exception {
    whenSmoothHelp("clean");
    thenFinishedWithSuccess();
    then(output(), containsString(buildClean()));
  }

  private static String buildClean() {
    return "usage: smooth clean\n"
        + "\n"
        + "Remove all cached values and artifacts calculated during previous builds\n";
  }
}
