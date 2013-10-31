package org.smoothbuild.function.def;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.message.message.CodeLocation;
import org.smoothbuild.task.base.LocatedTask;
import org.smoothbuild.task.exec.TaskGenerator;
import org.smoothbuild.testing.message.FakeCodeLocation;

public class AbstractNodeTest {
  CodeLocation codeLocation;
  MyNode node;

  @Test(expected = NullPointerException.class)
  public void null_code_location_is_forbidden() {
    new MyNode(null);
  }

  @Test
  public void code_location_passed_to_constructor_is_returned() throws Exception {
    given(codeLocation = new FakeCodeLocation());
    given(node = new MyNode(codeLocation));
    when(node.codeLocation());
    thenReturned(codeLocation);
  }

  public class MyNode extends AbstractNode {
    public MyNode(CodeLocation codeLocation) {
      super(codeLocation);
    }

    @Override
    public Type type() {
      return null;
    }

    @Override
    public LocatedTask generateTask(TaskGenerator taskGenerator) {
      return null;
    }
  }
}
