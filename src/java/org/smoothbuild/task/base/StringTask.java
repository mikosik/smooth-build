package org.smoothbuild.task.base;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.smoothbuild.lang.base.STypes.STRING;

import org.smoothbuild.lang.base.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.exec.NativeApiImpl;

public class StringTask extends Task<SString> {
  private final SString string;

  public StringTask(SString string, CodeLocation codeLocation) {
    super(STRING, STRING.name(), true, codeLocation);
    this.string = checkNotNull(string);
  }

  @Override
  public SString execute(NativeApiImpl nativeApi) {
    return string;
  }
}
