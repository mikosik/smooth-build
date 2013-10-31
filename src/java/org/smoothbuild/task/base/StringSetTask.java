package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.plugin.Sandbox;
import org.smoothbuild.plugin.StringSetBuilder;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.plugin.Value;

import com.google.common.collect.ImmutableList;

public class StringSetTask implements Task {
  private final ImmutableList<Result> elements;

  public StringSetTask(List<Result> elements) {
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public String name() {
    return "String*";
  }

  @Override
  public boolean isInternal() {
    return true;
  }

  @Override
  public Value execute(Sandbox sandbox) {
    StringSetBuilder stringSetBuilder = sandbox.stringSetBuilder();

    for (Result task : elements) {
      stringSetBuilder.add((StringValue) task.result());
    }

    return stringSetBuilder.build();
  }
}
