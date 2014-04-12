package org.smoothbuild.task.base;

import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

public class ConvertTask<S extends SValue, T extends SValue> extends Task<T> {
  private final Result<S> toConvert;
  private final Converter<S, T> converter;

  public ConvertTask(Result<S> toConvert, Converter<S, T> converter, CodeLocation codeLocation) {
    super(converter.targetType(), converter.name(), true, codeLocation);
    this.toConvert = toConvert;
    this.converter = converter;
  }

  @Override
  public T execute(NativeApiImpl nativeApi) {
    return converter.convert(nativeApi, toConvert.value());
  }
}
