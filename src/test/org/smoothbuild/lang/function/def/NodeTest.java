package org.smoothbuild.lang.function.def;

import static org.smoothbuild.lang.base.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.smoothbuild.lang.base.SString;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.message.base.CodeLocation;
import org.smoothbuild.task.base.TaskWorker;
import org.smoothbuild.util.Empty;
import org.testory.Closure;

public class NodeTest {
  SType<SString> type = STRING;
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

  private static Closure $myNode(final SType<SString> type, final CodeLocation codeLocation) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return new MyNode(type, codeLocation);
      }
    };
  }

  public static class MyNode extends Node<SString> {
    public MyNode(SType<SString> type, CodeLocation codeLocation) {
      super(type, Empty.nodeList(), codeLocation);
    }

    @Override
    public TaskWorker<SString> createWorker() {
      return null;
    }
  }
}
