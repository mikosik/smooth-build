package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;
import static org.smoothbuild.parse.def.Argument.pipedArg;
import static org.smoothbuild.parse.def.Assignment.assignment;
import static org.smoothbuild.testing.function.base.ParamTester.param;
import static org.testory.Testory.givenTest;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.def.DefinitionNode;
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
    DefinitionNode argNode = mock(DefinitionNode.class);
    Mockito.when(argNode.type()).thenReturn(FILE);
    argument = pipedArg(argNode, codeLocation(1, 2, 4));
    try {
      assignment(param(STRING, "name"), argument);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void param_and_argument_are_returned() throws Exception {
    DefinitionNode argNode = mock(DefinitionNode.class);
    Mockito.when(argNode.type()).thenReturn(STRING);
    String name = "name";
    param = param(STRING, name);
    argument = pipedArg(argNode, codeLocation(1, 2, 4));

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
