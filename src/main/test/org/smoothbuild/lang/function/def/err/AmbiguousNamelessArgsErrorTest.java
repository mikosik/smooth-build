package org.smoothbuild.lang.function.def.err;

import static com.google.common.collect.Sets.newHashSet;
import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Parameter.optionalParameter;
import static org.smoothbuild.lang.function.def.Argument.namedArgument;
import static org.smoothbuild.lang.function.def.Argument.namelessArgument;
import static org.smoothbuild.lang.function.def.Argument.pipedArgument;
import static org.smoothbuild.lang.type.Types.FILE;
import static org.smoothbuild.lang.type.Types.FILE_ARRAY;
import static org.smoothbuild.lang.type.Types.NIL;
import static org.smoothbuild.lang.type.Types.STRING;
import static org.smoothbuild.lang.type.Types.STRING_ARRAY;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.lang.function.def.Argument;
import org.smoothbuild.lang.function.def.TypedParametersPool;
import org.smoothbuild.lang.type.Type;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class AmbiguousNamelessArgsErrorTest {

  @Test
  public void test() {

    Parameter p1 = optionalParameter(STRING, "param1");
    Argument a1 = namedArgument(12, "arg1", expression(STRING), codeLocation(2));

    Parameter p2 = optionalParameter(STRING_ARRAY, "param2");
    Argument a2 = namelessArgument(7, expression(STRING_ARRAY), codeLocation(12));

    Parameter p3 = optionalParameter(FILE, "param3");
    Argument a3 = pipedArgument(expression(FILE), codeLocation(14));

    Argument a4 = namedArgument(3, "arg4", expression(NIL), codeLocation(7));
    Set<Argument> availableArguments = newHashSet();
    availableArguments.add(a4);

    Map<Parameter, Argument> paramToArgMap = ImmutableMap.of(p1, a1, p2, a2, p3, a3);

    Parameter p4 = optionalParameter(FILE_ARRAY, "param4");
    Parameter p5 = optionalParameter(STRING_ARRAY, "param5");
    TypedParametersPool availableParams = new TypedParametersPool(newHashSet(p4, p5), Sets
        .<Parameter> newHashSet());

    AmbiguousNamelessArgsError error = new AmbiguousNamelessArgsError(name("func"), paramToArgMap,
        availableArguments, availableParams);

    StringBuilder builder = new StringBuilder();
    builder.append("ERROR [ line 7 ]: Can't decide unambiguously to which parameters in "
        + "'func' function some nameless arguments should be assigned:\n");
    builder.append("List of assignments that were successfully detected is following:\n");
    builder.append("  String  : param1 <- String  : arg1       #12 " + a1.codeLocation() + "\n");
    builder.append("  String[]: param2 <- String[]: <nameless> #7  " + a2.codeLocation() + "\n");
    builder.append("  File    : param3 <- File    : <nameless> #|  " + a3.codeLocation() + "\n");
    builder.append("List of nameless arguments that caused problems:\n");
    builder.append("  Nothing[]: arg4 #3 " + a4.codeLocation() + "\n");
    builder.append("List of unassigned parameters of desired type is following:\n");
    builder.append("  String[]: param5\n");
    builder.append("  File[]  : param4\n");

    assertEquals(builder.toString(), error.toString());
  }

  private Expression expression(Type type) {
    Expression result = mock(Expression.class);
    given(willReturn(type), result).type();
    return result;
  }
}
