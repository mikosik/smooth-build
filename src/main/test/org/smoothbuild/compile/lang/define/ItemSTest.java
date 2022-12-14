package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.loc;
import static org.smoothbuild.testing.TestContext.stringTS;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import com.google.common.truth.Truth;

public class ItemSTest {
  private final String name = "name";
  private ItemS param;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemS(null, name, Optional.empty(), loc()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemS(stringTS(), null, Optional.empty(), loc()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    param = new ItemS(stringTS(), name, Optional.empty(), loc());
    Truth.assertThat(param.type())
        .isEqualTo(stringTS());
  }

  @Test
  public void name_getter() {
    param = new ItemS(stringTS(), name, Optional.empty(), loc());
    Truth.assertThat(param.name())
        .isEqualTo(name);
  }

  @Test
  public void to_string() {
    param = new ItemS(stringTS(), name, Optional.empty(), loc());
    assertThat(param.toString())
        .isEqualTo("""
            ItemS(
              type = String
              name = name
              defaultValue = Optional.empty
              location = myBuild.smooth:11
            )""");
  }
}
