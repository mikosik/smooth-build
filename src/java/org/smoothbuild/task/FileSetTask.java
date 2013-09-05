package org.smoothbuild.task;

import java.util.Set;

import org.smoothbuild.plugin.api.File;
import org.smoothbuild.plugin.api.MutableFileSet;
import org.smoothbuild.plugin.api.Sandbox;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableSet;

public class FileSetTask extends AbstractTask {
  private final ImmutableSet<Task> dependencies;

  public FileSetTask(Set<Task> dependencies) {
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  @Override
  public void execute(Sandbox sandbox) {
    MutableFileSet fileSet = new MutableFileSet();
    for (Task entry : dependencies) {
      fileSet.add((File) entry.result());
    }

    setResult(fileSet);
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies;
  }
}
