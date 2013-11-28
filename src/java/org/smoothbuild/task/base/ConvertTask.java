package org.smoothbuild.task.base;

import org.smoothbuild.lang.convert.Converter;
import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.PluginApiImpl;

public class ConvertTask extends Task {
  private final Result toConvert;
  private final Converter<?> converter;

  public ConvertTask(Result toConvert, Converter<?> converter, CodeLocation codeLocation) {
    super(converter.name(), true, codeLocation);
    this.toConvert = toConvert;
    this.converter = converter;
  }

  @Override
  public SValue execute(PluginApiImpl pluginApi) {
    return converter.convert(pluginApi, toConvert.result());
  }

}
