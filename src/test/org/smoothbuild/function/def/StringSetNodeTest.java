package org.smoothbuild.function.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.plugin.api.StringSet;
import org.smoothbuild.task.Task;

import com.google.common.collect.ImmutableList;

public class StringSetNodeTest {
  String string1 = "string1";
  String string2 = "string1";

  StringNode node1 = new StringNode(string1);
  StringNode node2 = new StringNode(string2);

  CodeLocation codeLocation = codeLocation(1, 2, 4);

  ImmutableList<StringNode> elemNodes = ImmutableList.of(node1, node2);

  StringSetNode stringSetNode = new StringSetNode(elemNodes, codeLocation);

  @Test
  public void type() {
    assertThat(stringSetNode.type()).isEqualTo(STRING_SET);
  }

  @Test
  public void generateTask() throws Exception {
    Task task = stringSetNode.generateTask();
    task.execute(null);
    assertThat((StringSet) task.result()).containsOnly(string1, string2);
  }

}
