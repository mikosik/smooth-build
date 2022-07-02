package org.smoothbuild.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.TestContext.loc;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.testing.type.TestingTS.STRING;

import java.util.Optional;

import org.junit.jupiter.api.Test;

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
    assertCall(() -> new ItemS(STRING, null, Optional.empty(), loc()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    param = new ItemS(STRING, name, Optional.empty(), loc());
    assertThat(param.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    param = new ItemS(STRING, name, Optional.empty(), loc());
    assertThat(param.name())
        .isEqualTo(name);
  }

  @Test
  public void to_string() {
    param = new ItemS(STRING, name, Optional.empty(), loc());
    assertThat(param.toString())
        .isEqualTo("Item(`String name`)");
  }
}
