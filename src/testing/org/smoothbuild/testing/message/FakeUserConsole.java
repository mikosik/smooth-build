package org.smoothbuild.testing.message;

import static com.google.common.io.ByteStreams.nullOutputStream;

import java.io.PrintStream;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.UserConsole;

public class FakeUserConsole extends UserConsole {
  private final FakeMessageGroup messageGroup;

  public FakeUserConsole() {
    this(new FakeMessageGroup());
  }

  public FakeUserConsole(FakeMessageGroup messageGroup) {
    super(new PrintStream(nullOutputStream()));
    this.messageGroup = messageGroup;
  }

  @Override
  public void report(Message message) {
    messageGroup.report(message);
  }

  public FakeMessageGroup messageGroup() {
    return messageGroup;
  }
}
