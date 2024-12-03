package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.compile.InitializeScopes.initializeScopes;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class InitializeScopesTest extends FrontendCompilerTestContext {
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

    @Test
    void module_scope_has_its_member_function_param_default_value_in_referenceables() {
      var defaultValue = pNamedValue("myFunc:param");
      var param = pItem("param", defaultValue);
      var pNamedFunc = pNamedFunc("myFunc", nlist(param));
      var pModule = pModule(list(), list(pNamedFunc));

      initializeScopes(pModule, new Logger());

      assertThat(pModule.scope().referencables().get("myFunc:param")).isEqualTo(defaultValue);
    }
  }

  @Nested
  class _function {
    @Nested
    class _parameter_default_value {
      @Test
      void parameter_default_value_scope_referenceables_does_not_contain_that_parameter() {
        var defaultValue = pNamedValue("myFunc:param");
        var param = pItem("param", defaultValue);
        var pNamedFunc = pNamedFunc("myFunc", nlist(param));
        var pModule = pModule(list(), list(pNamedFunc));

        initializeScopes(pModule, new Logger());

        assertThat(defaultValue.scope().referencables().getMaybe("param")).isEqualTo(none());
      }

      @Test
      void parameter_default_value_scope_referenceables_does_not_contain_other_parameter() {
        var param1DefaultValue = pNamedValue("myFunc:param");
        var param1 = pItem("param1", param1DefaultValue);
        var param2 = pItem("param2");
        var pNamedFunc = pNamedFunc("myFunc", nlist(param1, param2));
        var pModule = pModule(list(), list(pNamedFunc));

        initializeScopes(pModule, new Logger());

        assertThat(param1DefaultValue.scope().referencables().getMaybe("param2"))
            .isEqualTo(none());
      }

      @Test
      void parameter_default_value_scope_referenceables_contains_function_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(pNamedFunc("myFunc"));
      }

      @Test
      void parameter_default_value_scope_referenceables_contains_value_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(pNamedValue("myValue"));
      }

      private void testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(
          PNamedEvaluable member) {
        var defaultValue = pNamedValue("myFuncWithParamWithDefaultValue:param");
        var param = pItem("param", defaultValue);
        var pNamedFunc = pNamedFunc("myFuncWithParamWithDefaultValue", nlist(param));
        var pModule = pModule(list(), list(pNamedFunc, member));

        initializeScopes(pModule, new Logger());

        assertThat(defaultValue.scope().referencables().get(member.name())).isEqualTo(member);
      }
    }

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
