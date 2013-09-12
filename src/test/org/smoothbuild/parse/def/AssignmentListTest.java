package org.smoothbuild.parse.def;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.parse.def.Argument.explicitArg;
import static org.smoothbuild.problem.CodeLocation.codeLocation;

import java.util.Map;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;

import com.google.common.collect.ImmutableMap;

public class AssignmentListTest {
  AssignmentList assignmentList = new AssignmentList();

  @Test
  public void nullParamThrowsException() throws Exception {
    Argument arg1 = arg(STRING);
    try {
      assignmentList.add(null, arg1);
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void nullArgumentThrowsException() throws Exception {
    Param param1 = param(STRING, "name1");
    try {
      assignmentList.add(param1, null);
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

    assignmentList.add(param1, arg1);
    try {
      assignmentList.add(param2, arg2);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void assigningIncorrectTypeThrowsException() throws Exception {
    doTestAssigningIncorrectTypeThrowsException(STRING, FILE);
  }

  private void doTestAssigningIncorrectTypeThrowsException(Type paramType, Type argType) {
    Param param = param(paramType, "name");
    Argument arg = arg(argType);

    try {
      assignmentList.add(param, arg);
      fail("exception should be thrown");
    } catch (IllegalArgumentException e) {
      // expected
    }
  }

  @Test
  public void createNodesMap() {
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

    assignmentList.add(param1, arg1);
    assignmentList.add(param2, arg2);
    assignmentList.add(param3, arg3);

    // when
    Map<String, DefinitionNode> actual = assignmentList.createNodesMap();

    // then
    Object expected = ImmutableMap.of(name1, arg1.definitionNode(), name2, arg2.definitionNode(),
        name3, arg3.definitionNode());
    assertThat(actual).isEqualTo(expected);
  }

  @Test
  public void testToString() throws Exception {
    // given
    Param param1 = param(STRING, "name1-that-is-long");
    Param param2 = param(STRING, "name2");
    Param param3 = param(FILE, "name3");

    Argument arg1 = arg(STRING, "name4");
    Argument arg2 = arg(STRING, "name5");
    Argument arg3 = arg(FILE, "name6-that-is-long");

    assignmentList.add(param1, arg1);
    assignmentList.add(param2, arg2);
    assignmentList.add(param3, arg3);

    // when
    String actual = assignmentList.toString();

    // then
    StringBuilder expected = new StringBuilder();
    expected.append("String: name1-that-is-long <- String: name4              [1:2-3]\n");
    expected.append("String: name2              <- String: name5              [1:2-3]\n");
    expected.append("File  : name3              <- File  : name6-that-is-long [1:2-3]\n");

    assertThat(actual).isEqualTo(expected.toString());
  }

  private static Argument arg(Type type) {
    return arg(type, "name");
  }

  private static Argument arg(Type type, String name) {
    DefinitionNode node = mock(DefinitionNode.class);
    when(node.type()).thenReturn(type);

    return explicitArg(name, node, codeLocation(1, 2, 3));
  }
}
