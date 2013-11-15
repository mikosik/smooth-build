package org.smoothbuild.app;

import org.smoothbuild.io.cache.CacheModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.builtin.BuiltinModule;

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
      install(new CacheModule());
      install(new FileSystemModule());
      install(new BuiltinModule());
    }
  }
}
