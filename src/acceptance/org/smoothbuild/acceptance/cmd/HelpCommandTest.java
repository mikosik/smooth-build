package org.smoothbuild.acceptance.cmd;

import org.junit.Test;
import org.smoothbuild.acceptance.AcceptanceTestCase;

public class HelpCommandTest extends AcceptanceTestCase {
  @Test
  public void help_command_prints_general_help() {
    whenSmoothHelp();
    thenFinishedWithSuccess();
    thenOutputContains(generalHelp());
  }

  private static String generalHelp() {
    return "usage: smooth <command> <arg>...\n"
        + "\n"
        + "All available commands are:\n"
        + "  build   Build artifact(s) by running specified function(s)\n"
        + "  clean   Remove all cached objects and artifacts calculated during previous builds\n"
        + "  dag     Prints execution DAG (directed acyclic graph) of for given function(s)\n"
        + "  help    Print help about given command\n"
        + "  list    Print arg-less user defined functions\n"
        + "  version Print smooth build version number\n";
  }

  @Test
  public void help_build_command_prints_help_for_build() {
    whenSmoothHelp("build");
    thenFinishedWithSuccess();
    thenOutputContains(buildHelp());
  }

  private static String buildHelp() {
    return "usage: smooth build <function>...\n"
        + "\n"
        + "Build artifact(s) by running specified function(s)\n"
        + "\n"
        + "  <function>  function which execution result is saved as artifact\n";
  }

  @Test
  public void help_clean_command_prints_help_for_clean() {
    whenSmoothHelp("clean");
    thenFinishedWithSuccess();
    thenOutputContains(cleanHelp());
  }

  private static String cleanHelp() {
    return "usage: smooth clean\n"
        + "\n"
        + "Remove all cached objects and artifacts calculated during previous builds\n";
  }
}
