package org.smoothbuild.cli;

import static org.smoothbuild.SmoothConstants.EXIT_CODE_SUCCESS;

import org.smoothbuild.SmoothConstants;

public class Version implements Command {
  @Override
  public int run(String... names) {
    System.out.println("smooth build version " + SmoothConstants.VERSION);
    return EXIT_CODE_SUCCESS;
  }
}
