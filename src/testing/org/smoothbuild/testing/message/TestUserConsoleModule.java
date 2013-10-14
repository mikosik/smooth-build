package org.smoothbuild.testing.message;

import javax.inject.Singleton;

import org.smoothbuild.message.listen.UserConsole;

import com.google.inject.AbstractModule;

public class TestUserConsoleModule extends AbstractModule {
  @Override
  protected void configure() {
    bind(TestUserConsole.class).in(Singleton.class);
    bind(UserConsole.class).to(TestUserConsole.class);
  }
}
