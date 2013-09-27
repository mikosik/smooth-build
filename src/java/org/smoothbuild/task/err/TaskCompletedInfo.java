package org.smoothbuild.task.err;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.function.base.Name;
import org.smoothbuild.message.message.Info;

@SuppressWarnings("serial")
public class TaskCompletedInfo extends Info {
  public TaskCompletedInfo(Name name) {
    super(createMessage(name));
  }

  private static String createMessage(Name name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(Name name, String status) {
    return padEnd("[" + name.simple() + "]", 10, ' ') + " " + status;
  }
}
