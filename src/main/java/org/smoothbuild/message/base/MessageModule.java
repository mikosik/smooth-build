package org.smoothbuild.message.base;

import java.io.PrintStream;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;

public class MessageModule extends AbstractModule {
  @Override
  protected void configure() {}

  @Provides
  @Console
  public PrintStream provideConsolePrintStream() {
    return System.out;
  }
}
