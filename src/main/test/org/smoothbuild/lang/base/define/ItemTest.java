package org.smoothbuild.lang.base.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.type.TestingTypes.STRING;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;

public class ItemTest {
  private final String name = "name";
  private Item parameter;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new Item(null, name, Optional.empty()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new Item(STRING, null, Optional.empty()))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    parameter = new Item(STRING, name, Optional.empty());
    assertThat(parameter.type())
        .isEqualTo(STRING);
  }

  @Test
  public void name_getter() {
    parameter = new Item(STRING, name, Optional.empty());
    assertThat(parameter.name())
        .isEqualTo(name);
  }

  @Test
  public void to_string() {
    parameter = new Item(STRING, name, Optional.empty());
    assertThat(parameter.toString())
        .isEqualTo("Item(`String name`)");
  }
}
