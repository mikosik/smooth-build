package org.smoothbuild.cli;

import org.smoothbuild.MainModule;

import com.google.inject.Guice;

public class BuildCommand implements Command {
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
  public int execute(String[] args) {
    return Guice.createInjector(new MainModule()).getInstance(Argparse4j.class).parse(args);
  }
}
