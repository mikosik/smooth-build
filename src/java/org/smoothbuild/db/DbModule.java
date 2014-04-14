package org.smoothbuild.db;

import org.smoothbuild.db.objects.ObjectsDbModule;
import org.smoothbuild.db.taskresults.TaskResultsDbModule;

import com.google.inject.AbstractModule;

public class DbModule extends AbstractModule {
  @Override
  protected void configure() {
    install(new TaskResultsDbModule());
    install(new ObjectsDbModule());
  }
}
