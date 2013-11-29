package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.PluginApiImpl;

import com.google.common.collect.ImmutableList;

public class ArrayTask extends Task {
  private final SArrayType<?> arrayType;
  private final ImmutableList<Result> elements;

  public ArrayTask(SArrayType<?> arrayType, List<Result> elements, CodeLocation codeLocation) {
    super(arrayType.name(), true, codeLocation);
    this.arrayType = arrayType;
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public SValue execute(PluginApiImpl pluginApi) {
    /*
     * TODO Remove unchecked cast once Sandbox.arrayBuilder() has Class<?> as
     * its parameter.
     */
    @SuppressWarnings("unchecked")
    ArrayBuilder<SValue> builder = (ArrayBuilder<SValue>) pluginApi.arrayBuilder(arrayType);

    for (Result task : elements) {
      builder.add(task.value());
    }
    return builder.build();
  }
}
