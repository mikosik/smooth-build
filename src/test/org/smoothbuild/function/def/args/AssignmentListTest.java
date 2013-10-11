package org.smoothbuild.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.def.args.Argument.namedArg;
import static org.smoothbuild.function.def.args.Assignment.assignment;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;

public class AssignmentListTest {
  AssignmentList assignmentList = new AssignmentList();

  @Test
  public void addingNullAssignmentThrowsException() throws Exception {
    try {
      assignmentList.add(null);
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void addingAssignmentForTheSameParamTwiceThrowsException() throws Exception {
    String name = "name";
    Param param1 = param(STRING, name);
    Param param2 = param(FILE, name);

    Argument arg1 = arg(STRING);
    Argument arg2 = arg(FILE);

    assignmentList.add(assignment(param1, arg1));
    try {
      assignmentList.add(assignment(param2, arg2));
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void iteratorReturnsAllAddedAssignments() {
    // given
    String name1 = "name1";
    String name2 = "name2";
    String name3 = "name3";

    Param param1 = param(STRING, name1);
    Param param2 = param(STRING, name2);
    Param param3 = param(STRING, name3);

    Argument arg1 = arg(STRING);
    Argument arg2 = arg(STRING);
    Argument arg3 = arg(STRING);

    Assignment assignment1 = assignment(param1, arg1);
    Assignment assignment2 = assignment(param2, arg2);
    Assignment assignment3 = assignment(param3, arg3);

    assignmentList.add(assignment1);
    assignmentList.add(assignment2);
    assignmentList.add(assignment3);

    assertThat(assignmentList).containsOnly(assignment1, assignment2, assignment3);
  }

  @Test
  public void testToString() throws Exception {
    // given
    Param param1 = param(STRING, "name1-that-is-long");
    Param param2 = param(STRING, "name2");
    Param param3 = param(FILE, "name3");

    Argument arg1 = arg(1, STRING, "name4");
    Argument arg2 = arg(1234, STRING, "name5");
    Argument arg3 = arg(7, FILE, "name6-that-is-long");

    assignmentList.add(assignment(param1, arg1));
    assignmentList.add(assignment(param2, arg2));
    assignmentList.add(assignment(param3, arg3));

    // when
    String actual = assignmentList.toString();

    // then
    StringBuilder expected = new StringBuilder();
    expected.append("  String: name1-that-is-long <- String: name4              #1    [2:3-4]\n");
    expected.append("  String: name2              <- String: name5              #1234 [2:3-4]\n");
    expected.append("  File  : name3              <- File  : name6-that-is-long #7    [2:3-4]\n");

    assertThat(actual).isEqualTo(expected.toString());
  }

  private static Argument arg(Type type) {
    return arg(type, "name");
  }

  private static Argument arg(Type type, String name) {
    return arg(1, type, name);
  }

  private static Argument arg(int number, Type type, String name) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);

    return namedArg(number, name, node, codeLocation(1, 2, 4));
  }
}
