package org.smoothbuild.task.exec.err;

import static org.smoothbuild.message.base.MessageType.ERROR;
import static org.smoothbuild.task.exec.err.UnknownFunctionError.nameList;

import java.util.Collection;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

@SuppressWarnings("serial")
public class NoFunctionSpecifiedError extends Message {
  public NoFunctionSpecifiedError(Collection<Name> availableNames) {
    super(ERROR, "No function passed to build command.\n"
        + "Pass at least one from following available functions: " + nameList(availableNames));
  }
}
