package org.smoothbuild.task.exec.err;

import static org.smoothbuild.message.base.MessageType.ERROR;

import java.util.Collection;
import java.util.List;

import org.smoothbuild.lang.function.base.Name;
import org.smoothbuild.message.base.Message;

import com.google.common.base.Joiner;
import com.google.common.collect.Ordering;

public class UnknownFunctionError extends Message {
  public UnknownFunctionError(Name name, Collection<Name> availableNames) {
    super(ERROR, "Unknown function " + name + " passed in command line.\n"
        + "Only following function(s) are available: " + nameList(availableNames));
  }

  public static String nameList(Collection<Name> availableNames) {
    String prefix = "\n  ";
    List<Name> sortedNames = Ordering.usingToString().sortedCopy(availableNames);
    return prefix + Joiner.on(prefix).join(sortedNames);
  }
}
