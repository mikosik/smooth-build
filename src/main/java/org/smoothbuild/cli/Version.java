package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import javax.inject.Inject;

import org.smoothbuild.SmoothConstants;

public class Version implements Command {
  private final Console console;

  @Inject
  public Version(Console console) {
    this.console = console;
  }

  @Override
  public int run(String... names) {
    console.println("smooth build version " + SmoothConstants.VERSION);
    return EXIT_CODE_SUCCESS;
  }
}
