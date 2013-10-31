package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.db.ValueDb;
import org.smoothbuild.fs.base.FileSystem;
import org.smoothbuild.task.base.LocatedTask;

public class SandboxFactory {
  private final FileSystem fileSystem;
  private final ValueDb valueDb;

  @Inject
  public SandboxFactory(FileSystem fileSystem, ValueDb valueDb) {
    this.fileSystem = fileSystem;
    this.valueDb = valueDb;
  }

  public SandboxImpl createSandbox(LocatedTask task) {
    return new SandboxImpl(fileSystem, valueDb, task);
  }
}
