package org.smoothbuild.task.base;

import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;

import org.smoothbuild.lang.type.SValue;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.PluginApiImpl;

public class EmptyArrayTask extends Task {

  public EmptyArrayTask(CodeLocation codeLocation) {
    super(EMPTY_ARRAY.name(), true, codeLocation);
  }

  @Override
  public SValue execute(PluginApiImpl pluginApi) {
    return pluginApi.emptyArray();
  }
}
