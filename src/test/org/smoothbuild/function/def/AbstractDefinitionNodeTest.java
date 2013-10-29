package org.smoothbuild.function.def;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.Task;

public class AbstractDefinitionNodeTest {
  CodeLocation codeLocation;
  MyDefinitionNode definitionNode;

  @Test(expected = NullPointerException.class)
  public void null_code_location_is_forbidden() {
    new MyDefinitionNode(null);
  }

  @Test
  public void code_location_passed_to_constructor_is_returned() throws Exception {
    given(codeLocation = CodeLocation.codeLocation(1, 3, 7));
    given(definitionNode = new MyDefinitionNode(codeLocation));
    when(definitionNode.codeLocation());
    thenReturned(codeLocation);
  }

  public class MyDefinitionNode extends AbstractDefinitionNode {
    public MyDefinitionNode(CodeLocation codeLocation) {
      super(codeLocation);
    }

    @Override
    public Type type() {
      return null;
    }

    @Override
    public Task generateTask() {
      return null;
    }
  }
}
