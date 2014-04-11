package org.smoothbuild.lang.function.def.args;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.smoothbuild.lang.function.base.Param.param;
import static org.smoothbuild.lang.function.def.args.Arg.namedArg;
import static org.smoothbuild.lang.type.STypes.FILE;
import static org.smoothbuild.lang.type.STypes.STRING;
import static org.testory.Testory.given;
import static org.testory.Testory.mock;
import static org.testory.Testory.willReturn;

import org.junit.Test;
import org.smoothbuild.lang.function.base.Param;
import org.smoothbuild.lang.function.def.Node;
import org.smoothbuild.lang.type.SType;
import org.smoothbuild.testing.message.FakeCodeLocation;

import com.google.common.collect.ImmutableMap;

public class ParamToArgMapBuilderTest {
  ParamToArgMapBuilder paramToArgMapBuilder = new ParamToArgMapBuilder();

  @Test
  public void addingNullArgThrowsException() throws Exception {
    try {
      paramToArgMapBuilder.add(param(STRING, "name"), null);
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void addingNullParamThrowsException() throws Exception {
    try {
      paramToArgMapBuilder.add(null, arg(STRING));
      fail("exception should be thrown");
    } catch (NullPointerException e) {
      // expected
    }
  }

  @Test
  public void addingAssignmentForTheSameParamTwiceThrowsException() throws Exception {
    String name = "name";
    Param param1 = param(STRING, name);

    Arg arg1 = arg(STRING);
    Arg arg2 = arg(FILE);

    paramToArgMapBuilder.add(param1, arg1);
    try {
      paramToArgMapBuilder.add(param1, arg2);
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

    Param param1 = param(STRING, name1);
    Param param2 = param(STRING, name2);
    Param param3 = param(STRING, name3);

    Arg arg1 = arg(STRING);
    Arg arg2 = arg(STRING);
    Arg arg3 = arg(STRING);

    paramToArgMapBuilder.add(param1, arg1);
    paramToArgMapBuilder.add(param2, arg2);
    paramToArgMapBuilder.add(param3, arg3);

    assertThat(paramToArgMapBuilder.build()).isEqualTo(
        ImmutableMap.of(param1, arg1, param2, arg2, param3, arg3));
  }

  private static Arg arg(SType<?> type) {
    return arg(type, "name");
  }

  private static Arg arg(SType<?> type, String name) {
    return arg(1, type, name);
  }

  private static Arg arg(int number, SType<?> type, String name) {
    Node<?> node = mock(Node.class);
    given(willReturn(type), node).type();

    return namedArg(number, name, node, new FakeCodeLocation());
  }
}
