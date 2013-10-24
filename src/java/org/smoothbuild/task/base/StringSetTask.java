package org.smoothbuild.task.base;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import java.util.List;

import org.smoothbuild.hash.HashTask;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.task.exec.HashedTasks;
import org.smoothbuild.type.impl.ImmutableStringSet;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.hash.HashCode;

public class StringSetTask extends AbstractTask {
  private final ImmutableList<HashCode> elements;

  public StringSetTask(List<HashCode> elements, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation), HashTask.stringSet(elements));
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public void execute(Sandbox sandbox, HashedTasks hashedTasks) {
    Builder<String> builder = ImmutableList.builder();
    for (HashCode hash : elements) {
      builder.add((String) hashedTasks.get(hash).result());
    }

    setResult(new ImmutableStringSet(builder.build()));
  }

  @Override
  public ImmutableCollection<HashCode> dependencies() {
    return elements;
  }
}
