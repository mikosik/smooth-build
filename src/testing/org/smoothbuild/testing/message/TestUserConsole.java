package org.smoothbuild.testing.message;

import static com.google.common.io.ByteStreams.nullOutputStream;

import java.io.PrintStream;

import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.Message;

public class TestUserConsole extends UserConsole {
  private final TestMessageGroup messageGroup;

  public TestUserConsole() {
    this(new TestMessageGroup());
  }

  public TestUserConsole(TestMessageGroup messageGroup) {
    super(new PrintStream(nullOutputStream()));
    this.messageGroup = messageGroup;
  }

  @Override
  public void report(Message message) {
    messageGroup.report(message);
  }

  public void assertProblemsFound() {
    messageGroup.assertProblemsFound();
  }

  public void assertOnlyInfo(Class<? extends Message> klass) {
    messageGroup.assertOnlyInfo(klass);
  }

  public void assertOnlyProblem(Class<? extends Message> klass) {
    messageGroup.assertOnlyProblem(klass);
  }

  public void assertNoProblems() {
    messageGroup.assertNoProblems();
  }
}
