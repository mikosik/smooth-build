package org.smoothbuild.lang.function.def.args;

import static org.junit.Assert.assertEquals;
import static org.smoothbuild.lang.base.Types.FILE;
import static org.smoothbuild.lang.base.Types.STRING;
import static org.smoothbuild.lang.function.base.Parameter.parameter;
import static org.smoothbuild.lang.function.def.args.Argument.namedArgument;
import static org.smoothbuild.message.base.CodeLocation.codeLocation;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.base.Type;
import org.smoothbuild.lang.expr.Expression;
import org.smoothbuild.lang.function.base.Parameter;
import org.smoothbuild.util.LineBuilder;

import com.google.common.collect.ImmutableMap;

public class MapToStringTest {
  @Test
  public void testToString() throws Exception {
    // given
    Parameter parameter1 = parameter(STRING, "name1-that-is-long", false);
    Parameter parameter2 = parameter(STRING, "name2", false);
    Parameter parameter3 = parameter(FILE, "name3", false);

    Argument argument1 = arg(1, STRING, "name4");
    Argument argument2 = arg(1234, STRING, "name5");
    Argument argument3 = arg(7, FILE, "name6-that-is-long");

    // when
    String actual =
        MapToString.toString(ImmutableMap.of(parameter1, argument1, parameter2, argument2,
            parameter3, argument3));

    // then
    String l = codeLocation(2).toString();
    LineBuilder expected = new LineBuilder();
    expected.addLine("  String: name1-that-is-long <- String: name4              #1    " + l);
    expected.addLine("  String: name2              <- String: name5              #1234 " + l);
    expected.addLine("  File  : name3              <- File  : name6-that-is-long #7    " + l);

    assertEquals(expected.build(), actual);
  }

  private static Argument arg(int number, Type<?> type, String name) {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(type), expression).type();

    return namedArgument(number, name, expression, codeLocation(2));
  }
}
