package org.smoothbuild.compile.fs.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.location;
import static org.smoothbuild.testing.TestContext.stringTS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ItemSTest {
  private final String name = "name";
  private ItemS param;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemS(null, name, Optional.empty(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemS(stringTS(), null, Optional.empty(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    param = new ItemS(stringTS(), name, Optional.empty(), location());
    assertThat(param.type())
        .isEqualTo(stringTS());
  }

  @Test
  public void name_getter() {
    param = new ItemS(stringTS(), name, Optional.empty(), location());
    assertThat(param.name())
        .isEqualTo(name);
  }

  @Test
  public void to_string() {
    param = new ItemS(stringTS(), name, Optional.empty(), location());
    assertThat(param.toString())
        .isEqualTo("""
            ItemS(
              type = String
              name = name
              defaultValue = Optional.empty
              location = build.smooth:11
            )""");
  }
}
