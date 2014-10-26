package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
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

import com.google.common.collect.ImmutableMap;

public class MapBuilderTest {
  MapBuilder mapBuilder = new MapBuilder();

  @Test
  public void addingNullArgThrowsException() throws Exception {
    try {
      mapBuilder.add(parameter(STRING, "name", false), null);
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void addingNullParamThrowsException() throws Exception {
    try {
      mapBuilder.add(null, arg(STRING));
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void addingAssignmentForTheSameParamTwiceThrowsException() throws Exception {
    String name = "name";
    Parameter parameter1 = parameter(STRING, name, false);

    Argument argument1 = arg(STRING);
    Argument argument2 = arg(FILE);

    mapBuilder.add(parameter1, argument1);
    try {
      mapBuilder.add(parameter1, argument2);
      fail("exception should be thrown");
    } catch (IllegalStateException e) {
      // expected
    }
  }

  @Test
  public void build_returns_map_with_all_added_mappings() {
    // given
    String name1 = "name1";
    String name2 = "name2";
    String name3 = "name3";

    Parameter parameter1 = parameter(STRING, name1, false);
    Parameter parameter2 = parameter(STRING, name2, false);
    Parameter parameter3 = parameter(STRING, name3, false);

    Argument argument1 = arg(STRING);
    Argument argument2 = arg(STRING);
    Argument argument3 = arg(STRING);

    mapBuilder.add(parameter1, argument1);
    mapBuilder.add(parameter2, argument2);
    mapBuilder.add(parameter3, argument3);

    assertThat(mapBuilder.build()).isEqualTo(ImmutableMap.of(parameter1, argument1, parameter2,
        argument2, parameter3, argument3));
  }

  private static Argument arg(Type<?> type) {
    return arg(type, "name");
  }

  private static Argument arg(Type<?> type, String name) {
    return arg(1, type, name);
  }

  private static Argument arg(int number, Type<?> type, String name) {
    Expression<?> expression = mock(Expression.class);
    given(willReturn(type), expression).type();

    return namedArgument(number, name, expression, codeLocation(1));
  }
}
