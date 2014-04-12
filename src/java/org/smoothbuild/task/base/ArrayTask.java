package org.smoothbuild.task.base;

import java.util.List;

import org.smoothbuild.io.cache.value.build.ArrayBuilder;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SArrayType;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

import com.google.common.collect.ImmutableList;

public class ArrayTask<T extends SValue> extends Task<SArray<T>> {
  private final SArrayType<T> arrayType;
  private final ImmutableList<Result<T>> elements;

  public ArrayTask(SArrayType<T> arrayType, List<? extends Result<T>> elements,
      CodeLocation codeLocation) {
    super(arrayType, arrayType.name(), true, codeLocation);
    this.arrayType = arrayType;
    this.elements = ImmutableList.copyOf(elements);
  }

  @Override
  public SArray<T> execute(NativeApiImpl nativeApi) {
    ArrayBuilder<T> builder = nativeApi.arrayBuilder(arrayType);
    for (Result<T> task : elements) {
      builder.add(task.value());
    }
    return builder.build();
  }
}
