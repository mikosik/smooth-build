package org.smoothbuild.testing.message;

import static com.google.common.io.ByteStreams.nullOutputStream;
import static org.assertj.core.api.Assertions.assertThat;

import java.io.PrintStream;
import java.util.List;

import org.smoothbuild.message.listen.MessageListener;
import org.smoothbuild.message.listen.UserConsole;
import org.smoothbuild.message.message.Message;
import org.smoothbuild.message.message.MessageType;
import org.smoothbuild.message.message.WrappedCodeMessage;

import com.google.common.collect.Lists;

public class TestUserConsole extends UserConsole implements MessageListener {
  private final List<Message> problems = Lists.newArrayList();
  private final List<Message> infos = Lists.newArrayList();

  public TestUserConsole() {
    super(new PrintStream(nullOutputStream()));
  }

  @Override
  public void report(Message message) {
    if (message.type() == MessageType.INFO) {
      infos.add(message);
    } else {
      problems.add(message);
    }
  }

  public void assertProblemsFound() {
    assertThat(problems.isEmpty()).isFalse();
  }

  public void assertOnlyInfo(Class<? extends Message> klass) {
    if (infos.size() != 1) {
      throw new AssertionError("Expected one info ,\nbut got:\n" + infos.toString());
    }
    assertThat(infos.get(0)).isInstanceOf(klass);
  }

  public void assertOnlyProblem(Class<? extends Message> klass) {
    if (problems.size() != 1) {
      throw new AssertionError("Expected one problem,\nbut got:\n" + problems.toString());
    }
    Message onlyProblem = problems.get(0);
    if (onlyProblem instanceof WrappedCodeMessage) {
      Message wrapped = ((WrappedCodeMessage) onlyProblem).wrappedMessage();
      assertThat(wrapped).isInstanceOf(klass);
    } else {
      assertThat(onlyProblem).isInstanceOf(klass);
    }
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
