package org.smoothbuild;

import org.smoothbuild.db.outputs.OutputsDbModule;
import org.smoothbuild.db.values.ValuesDbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.io.util.ReleaseJarModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new OutputsDbModule());
    install(new ValuesDbModule());
    install(new FileSystemModule());
    install(new ReleaseJarModule());
  }
}
