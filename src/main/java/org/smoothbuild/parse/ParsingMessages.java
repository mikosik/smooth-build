package org.smoothbuild.parse;

import java.io.PrintStream;

import javax.inject.Inject;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.message.base.Console;

public class ParsingMessages {
  private final PrintStream console;
  private boolean hasErrors;

  @Inject
  public ParsingMessages(@Console PrintStream console) {
    this.console = console;
  }

  public void error(CodeLocation location, String message) {
    hasErrors = true;
    console.println("build.smooth:" + location.line() + ": error: " + message);
  }

  public boolean hasErrors() {
    return hasErrors;
  }
}
