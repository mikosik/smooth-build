package org.smoothbuild.compile.ps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.ps.ast.expr.ModuleP;
import org.smoothbuild.testing.TestContext;

public class ScopesInitializerTest extends TestContext {
  @Test
  public void parameter_default_value_scope_refables_does_not_contain_that_parameter() {
    var defaultValue = namedValueP("myFunc:param");
    var param = itemP("param", defaultValue);
    var namedFuncP = namedFuncP("myFunc", nlist(param));
    var moduleP = new ModuleP(list(), list(namedFuncP));

    initializeScopes(moduleP);

    assertThat(defaultValue.scope().refables().getOptional("param"))
        .isEqualTo(Optional.empty());
  }

  @Test
  public void named_function_scope_has_its_parameter() {
    var param = itemP("param");
    var namedFuncP = namedFuncP("myFunc", nlist(param));
    var moduleP = new ModuleP(list(), list(namedFuncP));

    initializeScopes(moduleP);

    assertThat(namedFuncP.scope().refables().get("param"))
        .isEqualTo(param);
  }

  @Test
  public void anonymous_function_scope_has_its_parameter() {
    var param = itemP("param");
    var anonymousFuncP = anonymousFuncP(nlist(param), intP());
    var moduleP = new ModuleP(list(), list(namedValueP(anonymousFuncP)));

    initializeScopes(moduleP);

    assertThat(anonymousFuncP.scope().refables().get("param"))
        .isEqualTo(param);
  }
}
