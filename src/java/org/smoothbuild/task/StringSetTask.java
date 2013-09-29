package org.smoothbuild.task;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.Constants.SET_TASK_NAME;

import java.util.Set;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.Sandbox;
import org.smoothbuild.plugin.internal.ImmutableStringSet;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class StringSetTask extends AbstractTask {
  private final ImmutableSet<Task> dependencies;

  public StringSetTask(Set<Task> dependencies, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation));
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
