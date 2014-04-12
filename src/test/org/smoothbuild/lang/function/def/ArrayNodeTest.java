package org.smoothbuild.lang.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.type.SArray;
import org.smoothbuild.lang.type.SString;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Result;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.smoothbuild.testing.task.base.FakeResult;
import org.smoothbuild.testing.task.exec.FakeNativeApi;

import com.google.common.collect.ImmutableList;

public class ArrayNodeTest {
  TaskGenerator taskGenerator = mock(TaskGenerator.class);
  FakeNativeApi nativeApi = new FakeNativeApi();
  CodeLocation codeLocation = new FakeCodeLocation();
  SString string1 = nativeApi.valueDb().writeString("string1");
  SString string2 = nativeApi.valueDb().writeString("string2");

  @SuppressWarnings("unchecked")
  Node<SString> node1 = mock(Node.class);
  @SuppressWarnings("unchecked")
  Node<SString> node2 = mock(Node.class);

  Result<?> result1 = new FakeResult<>(string1);
  Result<?> result2 = new FakeResult<>(string2);

  ImmutableList<Node<SString>> elemNodes = ImmutableList.of(node1, node2);
  ArrayNode<SString> arrayNode = new ArrayNode<>(STRING_ARRAY, elemNodes, codeLocation);

  @Test
  public void type() {
    when(arrayNode.type());
    thenReturned(STRING_ARRAY);
  }

  @Test
  public void code_location() throws Exception {
    given(arrayNode = new ArrayNode<>(STRING_ARRAY, elemNodes, codeLocation));
    when(arrayNode.codeLocation());
    thenReturned(codeLocation);
  }

  @Test
  public void generateTask() throws Exception {
    given(willReturn(result1), taskGenerator).generateTask(node1);
    given(willReturn(result2), taskGenerator).generateTask(node2);

    Task<?> task = arrayNode.generateTask(taskGenerator);
    @SuppressWarnings("unchecked")
    SArray<SString> result = (SArray<SString>) task.execute(nativeApi);
    assertThat(result).containsOnly(string1, string2);
  }
}
