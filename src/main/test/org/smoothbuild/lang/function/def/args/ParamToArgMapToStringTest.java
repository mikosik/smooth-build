package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expr;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.ImmutableMap;

public class ParamToArgMapToStringTest {
  @Test
  public void testToString() throws Exception {
    // given
    Param param1 = param(STRING, "name1-that-is-long", false);
    Param param2 = param(STRING, "name2", false);
    Param param3 = param(FILE, "name3", false);

    Arg arg1 = arg(1, STRING, "name4");
    Arg arg2 = arg(1234, STRING, "name5");
    Arg arg3 = arg(7, FILE, "name6-that-is-long");

    // when
    String actual = ParamToArgMapToString.toString(ImmutableMap.of(param1, arg1, param2, arg2,
        param3, arg3));

    // then
    String l = codeLocation(2).toString();
    LineBuilder expected = new LineBuilder();
    expected.addLine("  String: name1-that-is-long <- String: name4              #1    " + l);
    expected.addLine("  String: name2              <- String: name5              #1234 " + l);
    expected.addLine("  File  : name3              <- File  : name6-that-is-long #7    " + l);

    assertThat(actual).isEqualTo(expected.build());
  }

  private static Arg arg(int number, Type<?> type, String name) {
    Expr<?> expr = mock(Expr.class);
    given(willReturn(type), expr).type();

    return namedArg(number, name, expr, codeLocation(2));
  }
}
