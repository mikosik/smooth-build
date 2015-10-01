package org.smoothbuild.cli;

public class BuildSpec implements CommandSpec {
  @Override
  public String shortDescription() {
    return "Build artifact(s) by running specified function(s)";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth build <function>...\n");
    builder.append("\n");
    builder.append(shortDescription() + "\n");
    builder.append("\n");
    builder.append("  <function>  function which execution result is saved as artifact");
    return builder.toString();
  }

  @Override
  public Class<? extends Command> commandClass() {
    return Build.class;
  }
}
