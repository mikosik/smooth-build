package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.plugin.StringSetBuilder;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class StringSetTask extends Task {
  private final ImmutableList<Result> elements;

  public StringSetTask(List<Result> elements, CodeLocation codeLocation) {
    super("String*", true, codeLocation);
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    StringSetBuilder stringSetBuilder = sandbox.stringSetBuilder();

    for (Result task : elements) {
      stringSetBuilder.add((StringValue) task.result());
    }

    return stringSetBuilder.build();
  }
}
