package org.smoothbuild.task;

import java.util.Set;

import org.smoothbuild.fs.plugin.ImmutableStringSet;
import org.smoothbuild.plugin.Sandbox;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class StringSetTask extends AbstractTask {
  private final ImmutableSet<Task> dependencies;

  public StringSetTask(Set<Task> dependencies) {
    this.dependencies = ImmutableSet.copyOf(dependencies);
  }

  @Override
  public void execute(Sandbox sandbox) {
    Builder<String> builder = ImmutableList.builder();
    for (Task entry : dependencies) {
      builder.add((String) entry.result());
    }

    setResult(new ImmutableStringSet(builder.build()));
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return dependencies;
  }
}
