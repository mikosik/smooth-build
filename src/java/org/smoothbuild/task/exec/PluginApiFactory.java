package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.io.cache.value.ValueDb;
import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.task.base.Task;

public class PluginApiFactory {
  private final FileSystem fileSystem;
  private final ValueDb valueDb;

  @Inject
  public PluginApiFactory(@ProjectDir FileSystem fileSystem, ValueDb valueDb) {
    this.fileSystem = fileSystem;
    this.valueDb = valueDb;
  }

  public PluginApiImpl createPluginApi(Task task) {
    return new PluginApiImpl(fileSystem, valueDb, task);
  }
}
