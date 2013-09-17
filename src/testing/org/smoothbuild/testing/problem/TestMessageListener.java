package org.smoothbuild.testing.problem;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.smoothbuild.problem.Message;
import org.smoothbuild.problem.MessageListener;
import org.smoothbuild.problem.MessageType;

import com.google.common.collect.Lists;

public class TestMessageListener implements MessageListener {
  private final List<Message> list = Lists.newArrayList();

  @Override
  public void report(Message message) {
    if (message.type() != MessageType.INFO) {
      list.add(message);
    }
  }

  public void assertProblemsFound() {
    assertThat(list.isEmpty()).isFalse();
  }

  public void assertOnlyProblem(Class<? extends Message> klass) {
    if (list.size() != 1) {
      throw new AssertionError("Expected one problem,\nbut got:\n" + list.toString());
    }
    assertThat(list.get(0)).isInstanceOf(klass);
  }

  public void assertNoProblems() {
    if (!list.isEmpty()) {
      StringBuilder builder = new StringBuilder("Expected zero problems, but got:\n");
      for (Message message : list) {
        builder.append(message.toString());
        builder.append("\n");
      }
      throw new AssertionError(builder.toString());
    }
  }
}
