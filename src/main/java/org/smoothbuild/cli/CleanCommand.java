package org.smoothbuild.cli;

import org.smoothbuild.MainModule;

import com.google.inject.Guice;

public class CleanCommand implements Command {
  @Override
  public String shortDescription() {
    return "Remove all cached values and artifacts calculated during previous builds";
  }

  @Override
  public String longDescription() {
    StringBuilder builder = new StringBuilder();
    builder.append("usage: smooth clean\n");
    builder.append("\n");
    builder.append(shortDescription());
    return builder.toString();
  }

  @Override
  public int execute(String[] args) {
    return Guice.createInjector(new MainModule()).getInstance(Argparse4j.class).parse(args);
  }
}
