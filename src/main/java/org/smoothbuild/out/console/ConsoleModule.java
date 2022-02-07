package org.smoothbuild.out.console;

import java.io.PrintWriter;

import com.google.inject.AbstractModule;

public class ConsoleModule extends AbstractModule {
  private final PrintWriter out;

  public ConsoleModule(PrintWriter out) {
    this.out = out;
  }

  @Override
  protected void configure() {
    bind(PrintWriter.class).toInstance(out);
  }
}
