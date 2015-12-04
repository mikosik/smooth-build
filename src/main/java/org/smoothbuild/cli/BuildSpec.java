package org.smoothbuild.cli;

public class BuildSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Build artifact(s) by running specified function(s)";
  }

  @Override
  public String longDescription() {
    return "usage: smooth build <function>...\n"
        + "\n"
        + shortDescription() + "\n"
        + "\n"
        + "  <function>  function which execution result is saved as artifact";
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Build.class;
  }
}
