package org.smoothbuild.task.err;

import static com.google.common.base.Strings.padEnd;

import org.smoothbuild.message.message.Info;

@SuppressWarnings("serial")
public class TaskCompletedInfo extends Info {
  public TaskCompletedInfo(String name) {
    super(createMessage(name));
  }

  private static String createMessage(String name) {
    return createMessage(name, "DONE");
  }

  public static String createMessage(String name, String status) {
    return padEnd("[" + name + "]", 10, ' ') + " " + status;
  }
}
