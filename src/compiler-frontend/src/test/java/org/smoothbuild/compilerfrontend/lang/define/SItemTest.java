package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.name.Fqn;

public class SItemTest extends FrontendCompilerTestContext {
  private final Fqn name = fqn("myFunc:name");

  @Test
  void type_getter() {
    var param = new SItem(sStringType(), name, none(), location());
    assertThat(param.type()).isEqualTo(sStringType());
  }

  @Test
  void fqn_getter() {
    var param = new SItem(sStringType(), name, none(), location());
    assertThat(param.fqn()).isEqualTo(name);
  }

  @Test
  void to_source_code_with_default_value() {
    var defaultValue = new SDefaultValue(fqn("some:name"));
    var param = new SItem(sStringType(), name, some(defaultValue), location());
    assertThat(param.toSourceCode()).isEqualTo("String name = some:name");
  }

  @Test
  void to_source_code_without_default_value() {
    var param = new SItem(sStringType(), name, none(), location());
    assertThat(param.toSourceCode()).isEqualTo("String name");
  }

  @Test
  void to_string() {
    var param = new SItem(sStringType(), name, none(), location());
    assertThat(param.toString())
        .isEqualTo(
            """
            SItem(
              type = String
              fqn = myFunc:name
              defaultValue = None
              location = {t-project}/module.smooth:11
            )""");
  }
}
