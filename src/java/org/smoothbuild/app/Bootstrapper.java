package org.smoothbuild.app;

import org.smoothbuild.builtin.BuiltinModule;
import org.smoothbuild.db.DbModule;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;

public class Bootstrapper {
  public static <T> T bootstrap(Class<T> klass) {
    Injector injector = Guice.createInjector(new MainModule());
    return injector.getInstance(klass);
  }

  public static class MainModule extends AbstractModule {
    @Override
    protected void configure() {
      install(new DbModule());
      install(new BuiltinModule());
    }
  }
}
