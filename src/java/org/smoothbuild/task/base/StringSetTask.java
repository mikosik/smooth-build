package org.smoothbuild.task.base;

import static org.smoothbuild.lang.type.Type.STRING_SET;

import java.util.List;

import org.smoothbuild.lang.plugin.StringSetBuilder;
import org.smoothbuild.lang.type.StringValue;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class StringSetTask extends Task {
  private final ImmutableList<Result> elements;

  public StringSetTask(List<Result> elements, CodeLocation codeLocation) {
    super(STRING_SET.name(), true, codeLocation);
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
