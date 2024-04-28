package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.compile.InitializeScopes.initializeScopes;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pInt;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pItem;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pLambda;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pModule;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pNamedFunc;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.pNamedValue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;

public class InitializeScopesTest {
  @Nested
  class _module {
    @Test
    void module_scope_has_its_member_function_in_referenceables() {
      var namedFuncP = pNamedFunc("myFunc");
      var moduleP = pModule(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myFunc")).isEqualTo(namedFuncP);
    }

    @Test
    void module_scope_has_its_member_value_in_referenceables() {
      var namedValueP = pNamedValue("myValue");
      var moduleP = pModule(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    void module_scope_has_its_member_function_param_default_value_in_referenceables() {
      var defaultValue = pNamedValue("myFunc:param");
      var param = pItem("param", defaultValue);
      var namedFuncP = pNamedFunc("myFunc", nlist(param));
      var moduleP = pModule(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myFunc:param")).isEqualTo(defaultValue);
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
        var namedFuncP = pNamedFunc("myFunc", nlist(param));
        var moduleP = pModule(list(), list(namedFuncP));

        initializeScopes(moduleP, new Logger());

        assertThat(defaultValue.scope().referencables().getMaybe("param")).isEqualTo(none());
      }

      @Test
      void parameter_default_value_scope_referenceables_does_not_contain_other_parameter() {
        var param1DefaultValue = pNamedValue("myFunc:param");
        var param1 = pItem("param1", param1DefaultValue);
        var param2 = pItem("param2");
        var namedFuncP = pNamedFunc("myFunc", nlist(param1, param2));
        var moduleP = pModule(list(), list(namedFuncP));

        initializeScopes(moduleP, new Logger());

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
        var namedFuncP = pNamedFunc("myFuncWithParamWithDefaultValue", nlist(param));
        var moduleP = pModule(list(), list(namedFuncP, member));

        initializeScopes(moduleP, new Logger());

        assertThat(defaultValue.scope().referencables().get(member.name())).isEqualTo(member);
      }
    }

    @Test
    void named_function_scope_has_its_parameter() {
      var param = pItem("param");
      var namedFuncP = pNamedFunc("myFunc", nlist(param));
      var moduleP = pModule(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("param")).isEqualTo(param);
    }

    @Test
    void named_function_scope_has_its_sibling_named_value() {
      var namedValueP = pNamedValue("myValue");
      var param = pItem("param");
      var namedFuncP = pNamedFunc("myFunc", nlist(param));
      var moduleP = pModule(list(), list(namedFuncP, namedValueP));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    void named_function_scope_has_its_sibling_named_function() {
      var otherFunc = pNamedFunc("otherFunc");
      var param = pItem("param");
      var namedFuncP = pNamedFunc("myFunc", nlist(param));
      var moduleP = pModule(list(), list(namedFuncP, otherFunc));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("otherFunc")).isEqualTo(otherFunc);
    }
  }

  @Nested
  class _lambda {
    @Test
    void lambda_scope_has_value_that_encloses_it() {
      var param = pItem("param");
      var lambdaP = pLambda(nlist(param), pInt());
      var namedValueP = pNamedValue("myValue", lambdaP);
      var moduleP = pModule(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      var cast = ((PLambda) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    void lambda_scope_has_function_that_encloses_it() {
      var param = pItem("param");
      var lambdaP = pLambda(nlist(param), pInt());
      var namedValueP = pNamedFunc("myFunc", lambdaP);
      var moduleP = pModule(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      var cast = ((PLambda) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("myFunc")).isEqualTo(namedValueP);
    }

    @Test
    void lambda_scope_has_its_parameter() {
      var param = pItem("param");
      var lambdaP = pLambda(nlist(param), pInt());
      var moduleP = pModule(list(), list(pNamedValue(lambdaP)));

      initializeScopes(moduleP, new Logger());

      var cast = ((PLambda) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("param")).isEqualTo(param);
    }
  }
}
