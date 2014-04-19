package org.smoothbuild;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskresults.TaskResultsDbModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.builtin.BuiltinModule;

import com.google.inject.AbstractModule;

public class MainModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskResultsDbModule());
    install(new ObjectsDbModule());
    install(new FileSystemModule());
    install(new BuiltinModule());
  }
}
