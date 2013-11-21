package org.smoothbuild.lang.function.def;

import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.type.Type;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.Task;
import org.smoothbuild.task.exec.TaskGenerator;
import org.testory.common.Closure;

public class NodeTest {
  Type type;
  CodeLocation codeLocation;

  MyNode node;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_type_is_forbidden() {
    when($myNode(null, codeLocation));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_code_location_is_forbidden() {
    when($myNode(type, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void type() throws Exception {
    given(node = new MyNode(type, codeLocation));
    when(node.type());
    thenReturned(type);
  }

  @Test
  public void code_location() throws Exception {
    given(node = new MyNode(type, codeLocation));
    when(node.codeLocation());
    thenReturned(codeLocation);
  }

  private static Closure $myNode(final Type type, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyNode(type, codeLocation);
      }
    };
  }

  public static class MyNode extends Node {
    public MyNode(Type type, CodeLocation codeLocation) {
      super(type, codeLocation);
    }

    @Override
    public Task generateTask(TaskGenerator taskGenerator) {
      return null;
    }
  }
}
