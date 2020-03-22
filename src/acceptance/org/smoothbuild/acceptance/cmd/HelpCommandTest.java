package org.smoothbuild.acceptance.cmd;

import static org.smoothbuild.util.Strings.unlines;

import org.junit.jupiter.api.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class HelpCommandTest extends AcceptanceTestCase {
  @Test
  public void help_command_prints_general_help() {
    whenSmoothHelp();
    thenFinishedWithSuccess();
    thenOutputContains(expectedGeneralHelp());
  }

  private static String expectedGeneralHelp() {
    return unlines(
        "usage: smooth <command> <arg>...",
        "",
        "All available commands are:",
        "  build   Build artifact(s) by running specified function(s)",
        "  clean   Remove all cached objects and artifacts calculated during previous builds",
        "  help    Print help about given command",
        "  list    Print arg-less user defined functions",
        "  tree    Prints execution tree for specified function(s)",
        "  version Print smooth build version number");
  }

  @Test
  public void help_build_command_prints_help_for_build() {
    whenSmoothHelp("build");
    thenFinishedWithSuccess();
    thenOutputContains(expectedBuildCommandHelp());
  }

  private static String expectedBuildCommandHelp() {
    return unlines(
        "usage: smooth build <function>...",
        "",
        "Build artifact(s) by running specified function(s)",
        "",
        "  <function>  function which execution result is saved as artifact");
  }

  @Test
  public void help_clean_command_prints_help_for_clean() {
    whenSmoothHelp("clean");
    thenFinishedWithSuccess();
    thenOutputContains(expectedCleanCommandHelp());
  }

  private static String expectedCleanCommandHelp() {
    return unlines(
        "usage: smooth clean",
        "",
        "Remove all cached objects and artifacts calculated during previous builds");
  }
}
