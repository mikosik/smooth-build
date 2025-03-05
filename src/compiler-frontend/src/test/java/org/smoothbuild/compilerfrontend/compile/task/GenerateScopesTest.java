package org.smoothbuild.compilerfrontend.compile.task;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Result.ok;
import static org.smoothbuild.compilerfrontend.compile.task.GenerateScopes.initializeScopes;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.lang.define.SScope;
import org.smoothbuild.compilerfrontend.lang.name.Bindings;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class GenerateScopesTest extends FrontendCompilerTestContext {
  @Nested
  class _module {
    @Test
    void module_scope_has_its_member_function_in_referenceables() {
      var pNamedFunc = pPoly(pNamedFunc("myFunc"));
      var pModule = pModule(list(), list(pNamedFunc));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pModule.scope().referenceables().find(fqn("myFunc"))).isEqualTo(ok(pNamedFunc));
    }

    @Test
    void module_scope_has_its_member_value_in_referenceables() {
      var pNamedValue = pPoly(pNamedValue("myValue"));
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pModule.scope().referenceables().find(fqn("myValue"))).isEqualTo(ok(pNamedValue));
    }
  }

  @Nested
  class _function {
    @Test
    void named_function_scope_has_its_parameter() {
      var param = pItem("param");
      var pNamedFunc = pPoly(pNamedFunc("myFunc", nlist(param)));
      var pModule = pModule(list(), list(pNamedFunc));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pNamedFunc.evaluable().scope().referenceables().find(fqn("param")))
          .isEqualTo(ok(param));
    }

    @Test
    void named_function_scope_has_its_sibling_named_value() {
      var pNamedValue = pPoly(pNamedValue("myValue"));
      var param = pItem("param");
      var pNamedFunc = pPoly(pNamedFunc("myFunc", nlist(param)));
      var pModule = pModule(list(), list(pNamedFunc, pNamedValue));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pNamedFunc.evaluable().scope().referenceables().find(fqn("myValue")))
          .isEqualTo(ok(pNamedValue));
    }

    @Test
    void named_function_scope_has_its_sibling_named_function() {
      var otherFunc = pPoly(pNamedFunc("otherFunc"));
      var param = pItem("param");
      var pNamedFunc = pPoly(pNamedFunc("myFunc", nlist(param)));
      var pModule = pModule(list(), list(pNamedFunc, otherFunc));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pNamedFunc.evaluable().scope().referenceables().find(fqn("otherFunc")))
          .isEqualTo(ok(otherFunc));
    }
  }

  @Nested
  class _lambda {
    @Test
    void lambda_scope_has_value_that_encloses_it() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var pNamedValue = pPoly(pNamedValue("myValue", pLambda));
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat((pLambda).scope().referenceables().find(fqn("myValue")))
          .isEqualTo(ok(pNamedValue));
    }

    @Test
    void lambda_scope_has_function_that_encloses_it() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var pNamedValue = pPoly(pNamedFunc("myFunc", pLambda));
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pLambda.scope().referenceables().find(fqn("myFunc"))).isEqualTo(ok(pNamedValue));
    }

    @Test
    void lambda_scope_has_its_parameter() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var value = pPoly(pNamedValue(pLambda));
      var pModule = pModule(list(), list(value));

      initializeScopes(emptyImportedScope(), pModule, new Logger());

      assertThat(pLambda.scope().referenceables().find(fqn("param"))).isEqualTo(ok(param));
    }
  }

  private static SScope emptyImportedScope() {
    return new SScope(Bindings.bindings(), Bindings.bindings());
  }
}
