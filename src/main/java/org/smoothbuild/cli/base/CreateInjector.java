package org.smoothbuild.cli.base;

import static com.google.inject.Stage.PRODUCTION;

import org.smoothbuild.cli.console.Level;
import org.smoothbuild.cli.taskmatcher.TaskMatcher;
import org.smoothbuild.exec.task.TaskModule;
import org.smoothbuild.install.InstallationPathsModule;
import org.smoothbuild.io.fs.FileSystemModule;
import org.smoothbuild.lang.object.db.ObjectDbModule;

import com.google.inject.Guice;
import com.google.inject.Injector;

public class CreateInjector {
  public static Injector createInjector() {
    return createInjector(new ReportModule());
  }

  public static Injector createInjector(TaskMatcher taskMatcher, Level logLevel) {
    return createInjector(new ReportModule(taskMatcher, logLevel));
  }

  private static Injector createInjector(ReportModule reportModule) {
    return Guice.createInjector(PRODUCTION,
        new TaskModule(),
        new ObjectDbModule(),
        new FileSystemModule(),
        new InstallationPathsModule(),
        reportModule);
  }
}
