package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.TestingExpressionS;

public class ItemSTest extends TestingExpressionS {
  private final String name = "name";
  private ItemS param;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemS(null, name, none(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemS(stringTS(), null, none(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    param = new ItemS(stringTS(), name, none(), location());
    assertThat(param.type()).isEqualTo(stringTS());
  }

  @Test
  public void name_getter() {
    param = new ItemS(stringTS(), name, none(), location());
    assertThat(param.name()).isEqualTo(name);
  }

  @Test
  public void to_string() {
    param = new ItemS(stringTS(), name, none(), location());
    assertThat(param.toString())
        .isEqualTo(
            """
            ItemS(
              type = String
              name = name
              defaultValue = None
              location = {prj}/build.smooth:11
            )""");
  }
}
