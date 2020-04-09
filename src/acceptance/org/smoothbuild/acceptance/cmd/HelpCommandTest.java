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
        "Usage:",
        "smooth COMMAND",
        "",
        "Description:",
        "smooth-build is a build tool with strongly and statically typed, purely",
        "functional language. It features fine-grained, aggressive caching that will",
        "make sure no computation happens twice on the same machine, decreasing build",
        "times significantly.",
        "More info at https://github.com/mikosik/smooth-build/blob/master/doc/tutorial.md",
        "",
        "Commands:",
        "  build    Build artifact(s) by running specified function(s)",
        "  clean    Remove all cached objects and artifacts calculated during all",
        "             previous builds",
        "  help     Displays help information about the specified command",
        "  list     Print arg-less user defined functions",
        "  tree     Print execution tree for specified function(s)",
        "  version  Print version information and exit");
  }

  @Test
  public void help_build_command_prints_help_for_build() {
    whenSmoothHelp("build");
    thenFinishedWithSuccess();
    thenOutputContains(expectedBuildCommandHelp());
  }

  private static String expectedBuildCommandHelp() {
    return unlines(
        "Usage:",
        "smooth build <function>...",
        "",
        "Description:",
        "Build artifact(s) by running specified function(s)",
        "",
        "Parameters:",
        "      <function>...   function(s) which results are saved as artifacts",
        "");
  }

  @Test
  public void help_clean_command_prints_help_for_clean() {
    whenSmoothHelp("clean");
    thenFinishedWithSuccess();
    thenOutputContains(expectedCleanCommandHelp());
  }

  private static String expectedCleanCommandHelp() {
    return unlines(
        "Usage:",
        "smooth clean",
        "",
        "Description:",
        "Remove all cached objects and artifacts calculated during all previous builds",
        "");
  }
}
