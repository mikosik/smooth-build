package org.smoothbuild.parse.def.err;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.message.CodeLocation.codeLocation;
import static org.smoothbuild.parse.def.Argument.namedArg;
import static org.smoothbuild.parse.def.Argument.namelessArg;
import static org.smoothbuild.parse.def.Argument.pipedArg;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.DefinitionNode;
import org.smoothbuild.parse.def.Argument;
import org.smoothbuild.parse.def.AssignmentList;
import org.smoothbuild.parse.def.TypedParamsPool;

public class AmbiguousNamelessArgsErrorTest {

  @Test
  public void test() {
    AssignmentList assignmentList = new AssignmentList();

    Param p1 = param(STRING, "param1");
    Argument a1 = namedArg(12, "arg1", node(STRING), codeLocation(1, 2, 4));
    assignmentList.add(p1, a1);

    Param p2 = param(STRING_SET, "param2");
    Argument a2 = namelessArg(7, node(STRING_SET), codeLocation(11, 2, 9));
    assignmentList.add(p2, a2);

    Param p3 = param(FILE, "param3");
    Argument a3 = pipedArg(node(FILE), codeLocation(13, 4, 8));
    assignmentList.add(p3, a3);

    Set<Argument> availableArgs = newHashSet();
    availableArgs.add(namedArg(3, "arg4", node(EMPTY_SET), codeLocation(6, 9, 15)));
    availableArgs.add(namedArg(7, "arg5", node(VOID), codeLocation(16, 19, 22)));

    TypedParamsPool availableParams = new TypedParamsPool();
    availableParams.add(param(FILE_SET, "param4"));
    availableParams.add(param(STRING_SET, "param6"));

    AmbiguousNamelessArgsError error = new AmbiguousNamelessArgsError(assignmentList,
        availableArgs, availableParams);

    StringBuilder builder = new StringBuilder();
    builder
        .append("ERROR[7:10-15]: Couldn't decide unambiguously to which parameters some nameless arguments should be assigned:\n");
    builder.append("List of assignments that were successfully detected is following:\n");
    builder.append("  String : param1 <- String : arg1       #12 [2:3-4]\n");
    builder.append("  String*: param2 <- String*: <nameless> #7  [12:3-9]\n");
    builder.append("  File   : param3 <- File   : <nameless> #|  [14:5-8]\n");
    builder.append("List of nameless arguments that caused problems:\n");
    builder.append("  Any*: arg4 #3 [7:10-15]\n");
    builder.append("  Void: arg5 #7 [17:20-22]\n");
    builder.append("List of unassigned parameters of desired type is following:\n");
    builder.append("  File*  : param4\n");
    builder.append("  String*: param6\n");

    assertThat(error.toString()).isEqualTo(builder.toString());
  }

  private DefinitionNode node(Type type) {
    DefinitionNode result = mock(DefinitionNode.class);
    when(result.type()).thenReturn(type);
    return result;
  }

}
