package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.compile.GenerateScopes.initializeScopes;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class GenerateScopesTest extends FrontendCompilerTestContext {
  @Nested
  class _module {
    @Test
    void module_scope_has_its_member_function_in_referenceables() {
      var pNamedFunc = pNamedFunc("myFunc");
      var pModule = pModule(list(), list(pNamedFunc));

      initializeScopes(pModule, new Logger());

      assertThat(pModule.scope().referencables().get("myFunc")).isEqualTo(pNamedFunc);
    }

    @Test
    void module_scope_has_its_member_value_in_referenceables() {
      var pNamedValue = pNamedValue("myValue");
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(pModule, new Logger());

      assertThat(pModule.scope().referencables().get("myValue")).isEqualTo(pNamedValue);
    }
  }

  @Nested
  class _function {
    @Test
    void named_function_scope_has_its_parameter() {
      var param = pItem("param");
      var pNamedFunc = pNamedFunc("myFunc", nlist(param));
      var pModule = pModule(list(), list(pNamedFunc));

      initializeScopes(pModule, new Logger());

      assertThat(pNamedFunc.scope().referencables().get("param")).isEqualTo(param);
    }

    @Test
    void named_function_scope_has_its_sibling_named_value() {
      var pNamedValue = pNamedValue("myValue");
      var param = pItem("param");
      var pNamedFunc = pNamedFunc("myFunc", nlist(param));
      var pModule = pModule(list(), list(pNamedFunc, pNamedValue));

      initializeScopes(pModule, new Logger());

      assertThat(pNamedFunc.scope().referencables().get("myValue")).isEqualTo(pNamedValue);
    }

    @Test
    void named_function_scope_has_its_sibling_named_function() {
      var otherFunc = pNamedFunc("otherFunc");
      var param = pItem("param");
      var pNamedFunc = pNamedFunc("myFunc", nlist(param));
      var pModule = pModule(list(), list(pNamedFunc, otherFunc));

      initializeScopes(pModule, new Logger());

      assertThat(pNamedFunc.scope().referencables().get("otherFunc")).isEqualTo(otherFunc);
    }
  }

  @Nested
  class _lambda {
    @Test
    void lambda_scope_has_value_that_encloses_it() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var pNamedValue = pNamedValue("myValue", pLambda);
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(pModule, new Logger());

      var cast = ((PLambda) pLambda.polymorphic());
      assertThat(cast.scope().referencables().get("myValue")).isEqualTo(pNamedValue);
    }

    @Test
    void lambda_scope_has_function_that_encloses_it() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var pNamedValue = pNamedFunc("myFunc", pLambda);
      var pModule = pModule(list(), list(pNamedValue));

      initializeScopes(pModule, new Logger());

      var cast = ((PLambda) pLambda.polymorphic());
      assertThat(cast.scope().referencables().get("myFunc")).isEqualTo(pNamedValue);
    }

    @Test
    void lambda_scope_has_its_parameter() {
      var param = pItem("param");
      var pLambda = pLambda(nlist(param), pInt());
      var pModule = pModule(list(), list(pNamedValue(pLambda)));

      initializeScopes(pModule, new Logger());

      var cast = ((PLambda) pLambda.polymorphic());
      assertThat(cast.scope().referencables().get("param")).isEqualTo(param);
    }
  }
}
