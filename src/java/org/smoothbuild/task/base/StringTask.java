package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.type.STypes.STRING;

import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.PluginApiImpl;

public class StringTask extends Task<SString> {
  private final SString string;

  public StringTask(SString string, CodeLocation codeLocation) {
    super(STRING, STRING.name(), true, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public SString execute(PluginApiImpl pluginApi) {
    return string;
  }
}
