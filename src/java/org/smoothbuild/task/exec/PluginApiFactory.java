package org.smoothbuild.task.exec;

import javax.inject.Inject;

import org.smoothbuild.io.fs.ProjectDir;
import org.smoothbuild.io.fs.base.FileSystem;
import org.smoothbuild.lang.type.SValueBuilders;
import org.smoothbuild.task.base.Task;

public class PluginApiFactory {
  private final FileSystem fileSystem;
  private final SValueBuilders valueBuilders;

  @Inject
  public PluginApiFactory(@ProjectDir FileSystem fileSystem, SValueBuilders valueBuilders) {
    this.fileSystem = fileSystem;
    this.valueBuilders = valueBuilders;
  }

  public PluginApiImpl createPluginApi(Task task) {
    return new PluginApiImpl(fileSystem, valueBuilders, task);
  }
}
