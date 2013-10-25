package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;
import static org.smoothbuild.testing.task.exec.HashedTasksTester.hashedTasks;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.task.base.FakeTask;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.ImmutableList;

public class StringSetNodeTest {
  FakeSandbox sandbox = new FakeSandbox();

  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  StringNode node1 = mock(StringNode.class);
  StringNode node2 = mock(StringNode.class);

  Task task1 = new FakeTask(string1);
  Task task2 = new FakeTask(string2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);
  ImmutableList<StringNode> elemNodes = ImmutableList.of(node1, node2);
  StringSetNode stringSetNode = new StringSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    assertThat(stringSetNode.type()).isEqualTo(STRING_SET);
  }

  @Test
  public void generateTask() throws Exception {
    TaskGenerator taskGenerator = mock(TaskGenerator.class);
    Mockito.when(taskGenerator.generateTask(node1)).thenReturn(task1.hash());
    Mockito.when(taskGenerator.generateTask(node2)).thenReturn(task2.hash());

    Task task = stringSetNode.generateTask(taskGenerator);
    task.execute(sandbox, hashedTasks(task1, task2));
    assertThat((StringSet) task.result(), containsOnly(string1.value(), string2.value()));
  }
}
