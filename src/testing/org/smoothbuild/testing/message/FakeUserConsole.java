package org.smoothbuild.testing.message;

import static com.google.common.io.ByteStreams.nullOutputStream;

import java.io.PrintStream;

import org.smoothbuild.message.base.Message;
import org.smoothbuild.message.listen.UserConsole;

public class FakeUserConsole extends UserConsole {
  private final FakeLoggedMessages messages;

  public FakeUserConsole() {
    this(new FakeLoggedMessages());
  }

  public FakeUserConsole(FakeLoggedMessages messageGroup) {
    super(new PrintStream(nullOutputStream()));
    this.messages = messageGroup;
  }

  @Override
  public void print(Message message) {
    messages.log(message);
  }

  public FakeLoggedMessages messages() {
    return messages;
  }
}
