package org.smoothbuild;

import org.smoothbuild.db.DbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.builtin.BuiltinModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new DbModule());
    install(new FileSystemModule());
    install(new BuiltinModule());
  }
}
