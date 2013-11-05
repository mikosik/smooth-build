package org.smoothbuild.function.def;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.testing.plugin.StringSetMatchers.containsOnly;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.StringSet;
import org.smoothbuild.plugin.StringValue;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeSandbox;

import com.google.common.collect.ImmutableList;

public class StringSetNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeSandbox sandbox = new FakeSandbox();
  CodeLocation codeLocation = new FakeCodeLocation();
  StringValue string1 = sandbox.objectDb().string("string1");
  StringValue string2 = sandbox.objectDb().string("string2");

  Node node1 = mock(Node.class);
  Node node2 = mock(Node.class);

  Result result1 = new FakeResult(string1);
  Result result2 = new FakeResult(string2);

  ImmutableList<Node> elemNodes = ImmutableList.of(node1, node2);
  StringSetNode stringSetNode = new StringSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    when(stringSetNode.type());
    thenReturned(STRING_SET);
  }

  @Test
  public void code_location() throws Exception {
    given(stringSetNode = new StringSetNode(elemNodes, codeLocation));
    when(stringSetNode.codeLocation());
    thenReturned(codeLocation);
  }

  @Test
  public void generateTask() throws Exception {
    Mockito.when(taskGenerator.generateTask(node1)).thenReturn(result1);
    Mockito.when(taskGenerator.generateTask(node2)).thenReturn(result2);

    Task task = stringSetNode.generateTask(taskGenerator);
    StringSet result = (StringSet) task.execute(sandbox);
    assertThat(result, containsOnly(string1.value(), string2.value()));
  }
}
