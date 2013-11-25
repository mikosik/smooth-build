package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Argument.pipedArg;
import static org.smoothbuild.lang.function.def.args.Assignment.assignment;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.BDDMockito;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.testing.message.FakeCodeLocation;
import org.testory.common.Closure;

public class AssignmentTest {
  Param param;
  Argument argument;

  @Before
  public void before() {
    givenTest(this);
  }

  @Test
  public void null_param_is_forbidden() {
    when($assignment(null, argument));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void null_argument_is_forbidden() {
    when($assignment(param, null));
    thenThrown(NullPointerException.class);
  }

  @Test
  public void incompatible_param_and_argument_throws_exception() throws Exception {
    Node argNode = mock(Node.class);
    BDDMockito.willReturn(FILE).given(argNode).type();
    argument = pipedArg(argNode, new FakeCodeLocation());
    try {
      assignment(param(STRING, "name"), argument);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void param_and_argument_are_returned() throws Exception {
    Node argNode = mock(Node.class);
    BDDMockito.willReturn(STRING).given(argNode).type();
    String name = "name";
    param = param(STRING, name);
    argument = pipedArg(argNode, new FakeCodeLocation());

    Assignment assignment = assignment(param, argument);
    assertThat(assignment.argument()).isSameAs(argument);
    assertThat(assignment.param()).isSameAs(param);
    assertThat(assignment.assignedName()).isEqualTo(name);
  }

  private static Closure $assignment(final Param param, final Argument argument) {
    return new Closure() {
      @Override
      public Object invoke() throws Throwable {
        return Assignment.assignment(param, argument);
      }
    };
  }

}
