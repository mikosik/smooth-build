package org.smoothbuild.lang.function.def.args.err;

import static com.google.common.collect.Sets.newHashSet;
import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.STypes.FILE;
import static org.smoothbuild.lang.base.STypes.FILE_ARRAY;
import static org.smoothbuild.lang.base.STypes.NIL;
import static org.smoothbuild.lang.base.STypes.STRING;
import static org.smoothbuild.lang.base.STypes.STRING_ARRAY;
import static org.smoothbuild.lang.function.base.Name.name;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.function.def.args.Arg.namelessArg;
import static org.smoothbuild.lang.function.def.args.Arg.pipedArg;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import java.util.Map;
import java.util.Set;

import org.junit.Test;
import org.smoothbuild.lang.base.SType;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.args.Arg;
import org.smoothbuild.lang.function.def.args.TypedParamsPool;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;

public class AmbiguousNamelessArgsErrorTest {

  @Test
  public void test() {

    Param p1 = param(STRING, "param1");
    Arg a1 = namedArg(12, "arg1", expr(STRING), codeLocation(2));

    Param p2 = param(STRING_ARRAY, "param2");
    Arg a2 = namelessArg(7, expr(STRING_ARRAY), codeLocation(12));

    Param p3 = param(FILE, "param3");
    Arg a3 = pipedArg(expr(FILE), codeLocation(14));

    Arg a4 = namedArg(3, "arg4", expr(NIL), codeLocation(7));
    Set<Arg> availableArgs = newHashSet();
    availableArgs.add(a4);

    Map<Param, Arg> paramToArgMap = ImmutableMap.of(p1, a1, p2, a2, p3, a3);

    Param p4 = param(FILE_ARRAY, "param4");
    Param p5 = param(STRING_ARRAY, "param5");
    TypedParamsPool availableParams =
        new TypedParamsPool(newHashSet(p4, p5), Sets.<Param> newHashSet());

    AmbiguousNamelessArgsError error =
        new AmbiguousNamelessArgsError(name("func"), paramToArgMap, availableArgs, availableParams);

    LineBuilder builder = new LineBuilder();
    builder
        .addLine("ERROR [ line 7 ]: Can't decide unambiguously to which parameters in 'func' function some nameless arguments should be assigned:");
    builder.addLine("List of assignments that were successfully detected is following:");
    builder.addLine("  String  : param1 <- String  : arg1       #12 " + a1.codeLocation());
    builder.addLine("  String[]: param2 <- String[]: <nameless> #7  " + a2.codeLocation());
    builder.addLine("  File    : param3 <- File    : <nameless> #|  " + a3.codeLocation());
    builder.addLine("List of nameless arguments that caused problems:");
    builder.addLine("  Nothing[]: arg4 #3 " + a4.codeLocation());
    builder.addLine("List of unassigned parameters of desired type is following:");
    builder.addLine("  String[]: param5");
    builder.addLine("  File[]  : param4");

    assertThat(error.toString()).isEqualTo(builder.build());
  }

  private Expr<?> expr(SType<?> type) {
    Expr<?> result = mock(Expr.class);
    given(willReturn(type), result).type();
    return result;
  }
}
