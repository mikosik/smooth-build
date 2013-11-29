package org.smoothbuild.lang.function.def.args.err;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.willReturn;
import static org.mockito.Mockito.mock;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Argument.namedArg;
import static org.smoothbuild.lang.function.def.args.Argument.namelessArg;
import static org.smoothbuild.lang.function.def.args.Argument.pipedArg;
import static org.smoothbuild.lang.type.STypes.EMPTY_ARRAY;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.smoothbuild.lang.type.STypes.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.function.def.args.Argument;
import org.smoothbuild.lang.function.def.args.TypedParamsPool;
import org.smoothbuild.lang.type.SType;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class AmbiguousNamelessArgsErrorTest {

  @Test
  public void test() {

    Param p1 = param(STRING, "param1");
    Argument a1 = namedArg(12, "arg1", node(STRING), codeLocation(2));

    Param p2 = param(STRING_ARRAY, "param2");
    Argument a2 = namelessArg(7, node(STRING_ARRAY), codeLocation(12));

    Param p3 = param(FILE, "param3");
    Argument a3 = pipedArg(node(FILE), codeLocation(14));

    Argument a4 = namedArg(3, "arg4", node(EMPTY_ARRAY), codeLocation(7));
    Set<Argument> availableArgs = newHashSet();
    availableArgs.add(a4);

    Map<Param, Argument> paramToArgMap = ImmutableMap.of(p1, a1, p2, a2, p3, a3);

    Param p4 = param(FILE_ARRAY, "param4");
    Param p5 = param(STRING_ARRAY, "param5");
    TypedParamsPool availableParams =
        new TypedParamsPool(newHashSet(p4, p5), Sets.<Param> newHashSet());

    AmbiguousNamelessArgsError error =
        new AmbiguousNamelessArgsError(name("func"), paramToArgMap, availableArgs, availableParams);

    StringBuilder builder = new StringBuilder();
    builder
        .append("ERROR [ line 7 ]: Can't decide unambiguously to which parameters in 'func' function some nameless arguments should be assigned:\n");
    builder.append("List of assignments that were successfully detected is following:\n");
    builder.append("  String  : param1 <- String  : arg1       #12 " + a1.codeLocation() + "\n");
    builder.append("  String[]: param2 <- String[]: <nameless> #7  " + a2.codeLocation() + "\n");
    builder.append("  File    : param3 <- File    : <nameless> #|  " + a3.codeLocation() + "\n");
    builder.append("List of nameless arguments that caused problems:\n");
    builder.append("  Nothing[]: arg4 #3 " + a4.codeLocation() + "\n");
    builder.append("List of unassigned parameters of desired type is following:\n");
    builder.append("  String[]: param5\n");
    builder.append("  File[]  : param4\n");

    assertThat(error.toString()).isEqualTo(builder.toString());
  }

  private Node node(SType<?> type) {
    Node result = mock(Node.class);
    willReturn(type).given(result).type();
    return result;
  }
}
