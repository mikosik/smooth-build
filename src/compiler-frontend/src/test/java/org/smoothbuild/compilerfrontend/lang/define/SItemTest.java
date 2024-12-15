package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.base.Name.referenceableName;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.lang.base.Name;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SItemTest extends FrontendCompilerTestContext {
  private final Name name = referenceableName("name");
  private SItem param;

  @Test
  void null_type_is_forbidden() {
    assertCall(() -> new SItem(null, name, none(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  void null_name_is_forbidden() {
    assertCall(() -> new SItem(sStringType(), null, none(), location()))
        .throwsException(NullPointerException.class);
  }

  @Test
  void type_getter() {
    param = new SItem(sStringType(), name, none(), location());
    assertThat(param.type()).isEqualTo(sStringType());
  }

  @Test
  void name_getter() {
    param = new SItem(sStringType(), name, none(), location());
    assertThat(param.id()).isEqualTo(name);
  }

  @Test
  void to_string() {
    param = new SItem(sStringType(), name, none(), location());
    assertThat(param.toString())
        .isEqualTo(
            """
            SItem(
              type = String
              name = name
              defaultValueId = None
              location = {t-project}/module.smooth:11
            )""");
  }
}
