package org.smoothbuild.testing.message;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.UserConsole;

import com.google.inject.AbstractModule;

public class FakeUserConsoleModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(FakeUserConsole.class).in(Singleton.class);
    bind(UserConsole.class).to(FakeUserConsole.class);
  }
}
