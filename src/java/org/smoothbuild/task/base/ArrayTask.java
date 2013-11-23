package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.lang.plugin.ArrayBuilder;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.lang.type.Value;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.SandboxImpl;

import com.google.common.collect.ImmutableList;

public class ArrayTask extends Task {
  private final Type<?> arrayType;
  private final ImmutableList<Result> elements;

  public ArrayTask(Type<?> arrayType, List<Result> elements, CodeLocation codeLocation) {
    super(arrayType.name(), true, codeLocation);
    this.arrayType = arrayType;
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public Value execute(SandboxImpl sandbox) {
    /*
     * TODO Remove unchecked cast once Sandbox.arrayBuilder() has Class<?> as
     * its parameter.
     */
    @SuppressWarnings("unchecked")
    ArrayBuilder<Value> builder = (ArrayBuilder<Value>) sandbox.arrayBuilder(arrayType);

    for (Result task : elements) {
      builder.add(task.result());
    }
    return builder.build();
  }
}
