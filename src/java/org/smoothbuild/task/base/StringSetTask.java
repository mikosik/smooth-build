package org.smoothbuild.task.base;

import static org.smoothbuild.function.base.Name.simpleName;
import static org.smoothbuild.message.message.CallLocation.callLocation;
import static org.smoothbuild.task.base.Constants.SET_TASK_NAME;

import java.util.List;

import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringSetBuilder;
import org.smoothbuild.plugin.StringValue;

import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;

public class StringSetTask extends AbstractTask {
  private final ImmutableList<Task> elements;

  public StringSetTask(List<Task> elements, CodeLocation codeLocation) {
    super(callLocation(simpleName(SET_TASK_NAME), codeLocation));
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public void execute(Sandbox sandbox) {
    StringSetBuilder stringSetBuilder = sandbox.stringSetBuilder();

    for (Task task : elements) {
      stringSetBuilder.add((StringValue) task.result());
    }

    setResult(stringSetBuilder.build());
  }

  @Override
  public ImmutableCollection<Task> dependencies() {
    return elements;
  }
}
