package org.smoothbuild.function.def.args.err;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.smoothbuild.function.base.Name.name;
import static org.smoothbuild.function.base.Param.param;
import static org.smoothbuild.function.base.Type.EMPTY_SET;
import static org.smoothbuild.function.base.Type.FILE;
import static org.smoothbuild.function.base.Type.FILE_SET;
import static org.smoothbuild.function.base.Type.STRING;
import static org.smoothbuild.function.base.Type.STRING_SET;
import static org.smoothbuild.function.base.Type.VOID;
import static org.smoothbuild.function.def.args.Argument.namedArg;
import static org.smoothbuild.function.def.args.Argument.namelessArg;
import static org.smoothbuild.function.def.args.Argument.pipedArg;
import static org.smoothbuild.function.def.args.Assignment.assignment;
import static org.smoothbuild.message.message.CodeLocation.codeLocation;

import java.util.Set;

import org.junit.Test;
import org.smoothbuild.function.base.Param;
import org.smoothbuild.function.base.Type;
import org.smoothbuild.function.def.Node;
import org.smoothbuild.function.def.args.Argument;
import org.smoothbuild.function.def.args.AssignmentList;
import org.smoothbuild.function.def.args.TypedParamsPool;

public class AmbiguousNamelessArgsErrorTest {

  @Test
  public void test() {
    AssignmentList assignmentList = new AssignmentList();

    Param p1 = param(STRING, "param1");
    Argument a1 = namedArg(12, "arg1", node(STRING), codeLocation(2));
    assignmentList.add(assignment(p1, a1));

    Param p2 = param(STRING_SET, "param2");
    Argument a2 = namelessArg(7, node(STRING_SET), codeLocation(12));
    assignmentList.add(assignment(p2, a2));

    Param p3 = param(FILE, "param3");
    Argument a3 = pipedArg(node(FILE), codeLocation(14));
    assignmentList.add(assignment(p3, a3));

    Set<Argument> availableArgs = newHashSet();
    availableArgs.add(namedArg(3, "arg4", node(EMPTY_SET), codeLocation(7)));
    availableArgs.add(namedArg(7, "arg5", node(VOID), codeLocation(17)));

    TypedParamsPool availableParams = new TypedParamsPool();
    availableParams.add(param(FILE_SET, "param4"));
    availableParams.add(param(STRING_SET, "param6"));

    AmbiguousNamelessArgsError error = new AmbiguousNamelessArgsError(name("func"),
        assignmentList, availableArgs, availableParams);

    StringBuilder builder = new StringBuilder();
    builder
        .append("ERROR [7]: Can't decide unambiguously to which parameters in 'func' function some nameless arguments should be assigned:\n");
    builder.append("List of assignments that were successfully detected is following:\n");
    builder.append("  String : param1 <- String : arg1       #12 [2]\n");
    builder.append("  String*: param2 <- String*: <nameless> #7  [12]\n");
    builder.append("  File   : param3 <- File   : <nameless> #|  [14]\n");
    builder.append("List of nameless arguments that caused problems:\n");
    builder.append("  Any*: arg4 #3 [7]\n");
    builder.append("  Void: arg5 #7 [17]\n");
    builder.append("List of unassigned parameters of desired type is following:\n");
    builder.append("  File*  : param4\n");
    builder.append("  String*: param6\n");

    assertThat(error.toString()).isEqualTo(builder.toString());
  }

  private Node node(Type type) {
    Node result = mock(Node.class);
    when(result.type()).thenReturn(type);
    return result;
  }

}
