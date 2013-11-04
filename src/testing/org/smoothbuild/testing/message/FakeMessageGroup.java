package org.smoothbuild.testing.message;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.message.message.MessageType.ERROR;
import static org.smoothbuild.message.message.MessageType.FATAL;

import java.util.List;

import org.smoothbuild.message.listen.MessageGroup;
import org.smoothbuild.message.message.Message;

import com.google.common.collect.Lists;

public class FakeMessageGroup extends MessageGroup {
  private final List<Message> problems = Lists.newArrayList();
  private final List<Message> nonProblems = Lists.newArrayList();

  public FakeMessageGroup() {
    super("TestMessageGroup");
  }

  @Override
  public void report(Message message) {
    if (message.type() == FATAL || message.type() == ERROR) {
      problems.add(message);
    } else {
      nonProblems.add(message);
    }
    super.report(message);
  }

  public void assertProblemsFound() {
    assertThat(problems.isEmpty()).isFalse();
  }

  public void assertOnlyInfo(Class<? extends Message> klass) {
    if (nonProblems.size() != 1) {
      throw new AssertionError("Expected one info ,\nbut got:\n" + nonProblems.toString());
    }
    assertThat(nonProblems.get(0)).isInstanceOf(klass);
  }

  public void assertOnlyProblem(Class<? extends Message> klass) {
    if (problems.size() != 1) {
      throw new AssertionError("Expected one problem,\nbut got:\n" + problems.toString());
    }
    Message onlyProblem = problems.get(0);
    assertThat(onlyProblem).isInstanceOf(klass);
  }

  public void assertNoProblems() {
    if (!problems.isEmpty()) {
      StringBuilder builder = new StringBuilder("Expected zero problems, but got:\n");
      for (Message message : problems) {
        builder.append(message.toString());
        builder.append("\n");
      }
      throw new AssertionError(builder.toString());
    }
  }
}
