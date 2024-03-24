package org.smoothbuild.compilerfrontend.compile;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.NList.nlist;
import static org.smoothbuild.compilerfrontend.compile.InitializeScopes.initializeScopes;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.intP;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.itemP;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.lambdaP;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.moduleP;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.namedFuncP;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.namedValueP;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedEvaluableP;

public class InitializeScopesTest {
  @Nested
  class _module {
    @Test
    public void module_scope_has_its_member_function_in_referenceables() {
      var namedFuncP = namedFuncP("myFunc");
      var moduleP = moduleP(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myFunc")).isEqualTo(namedFuncP);
    }

    @Test
    public void module_scope_has_its_member_value_in_referenceables() {
      var namedValueP = namedValueP("myValue");
      var moduleP = moduleP(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    public void module_scope_has_its_member_function_param_default_value_in_referenceables() {
      var defaultValue = namedValueP("myFunc:param");
      var param = itemP("param", defaultValue);
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = moduleP(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(moduleP.scope().referencables().get("myFunc:param")).isEqualTo(defaultValue);
    }
  }

  @Nested
  class _function {
    @Nested
    class _parameter_default_value {
      @Test
      public void parameter_default_value_scope_referenceables_does_not_contain_that_parameter() {
        var defaultValue = namedValueP("myFunc:param");
        var param = itemP("param", defaultValue);
        var namedFuncP = namedFuncP("myFunc", nlist(param));
        var moduleP = moduleP(list(), list(namedFuncP));

        initializeScopes(moduleP, new Logger());

        assertThat(defaultValue.scope().referencables().getMaybe("param")).isEqualTo(none());
      }

      @Test
      public void parameter_default_value_scope_referenceables_does_not_contain_other_parameter() {
        var param1DefaultValue = namedValueP("myFunc:param");
        var param1 = itemP("param1", param1DefaultValue);
        var param2 = itemP("param2");
        var namedFuncP = namedFuncP("myFunc", nlist(param1, param2));
        var moduleP = moduleP(list(), list(namedFuncP));

        initializeScopes(moduleP, new Logger());

        assertThat(param1DefaultValue.scope().referencables().getMaybe("param2"))
            .isEqualTo(none());
      }

      @Test
      public void parameter_default_value_scope_referenceables_contains_function_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(namedFuncP("myFunc"));
      }

      @Test
      public void parameter_default_value_scope_referenceables_contains_value_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(namedValueP("myValue"));
      }

      private void testThatParameterDefaultValueScopeHasModuleMemberInReferenceables(
          NamedEvaluableP member) {
        var defaultValue = namedValueP("myFuncWithParamWithDefaultValue:param");
        var param = itemP("param", defaultValue);
        var namedFuncP = namedFuncP("myFuncWithParamWithDefaultValue", nlist(param));
        var moduleP = moduleP(list(), list(namedFuncP, member));

        initializeScopes(moduleP, new Logger());

        assertThat(defaultValue.scope().referencables().get(member.name())).isEqualTo(member);
      }
    }

    @Test
    public void named_function_scope_has_its_parameter() {
      var param = itemP("param");
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = moduleP(list(), list(namedFuncP));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("param")).isEqualTo(param);
    }

    @Test
    public void named_function_scope_has_its_sibling_named_value() {
      var namedValueP = namedValueP("myValue");
      var param = itemP("param");
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = moduleP(list(), list(namedFuncP, namedValueP));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    public void named_function_scope_has_its_sibling_named_function() {
      var otherFunc = namedFuncP("otherFunc");
      var param = itemP("param");
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = moduleP(list(), list(namedFuncP, otherFunc));

      initializeScopes(moduleP, new Logger());

      assertThat(namedFuncP.scope().referencables().get("otherFunc")).isEqualTo(otherFunc);
    }
  }

  @Nested
  class _lambda {
    @Test
    public void lambda_scope_has_value_that_encloses_it() {
      var param = itemP("param");
      var lambdaP = lambdaP(nlist(param), intP());
      var namedValueP = namedValueP("myValue", lambdaP);
      var moduleP = moduleP(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      var cast = ((LambdaP) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("myValue")).isEqualTo(namedValueP);
    }

    @Test
    public void lambda_scope_has_function_that_encloses_it() {
      var param = itemP("param");
      var lambdaP = lambdaP(nlist(param), intP());
      var namedValueP = namedFuncP("myFunc", lambdaP);
      var moduleP = moduleP(list(), list(namedValueP));

      initializeScopes(moduleP, new Logger());

      var cast = ((LambdaP) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("myFunc")).isEqualTo(namedValueP);
    }

    @Test
    public void lambda_scope_has_its_parameter() {
      var param = itemP("param");
      var lambdaP = lambdaP(nlist(param), intP());
      var moduleP = moduleP(list(), list(namedValueP(lambdaP)));

      initializeScopes(moduleP, new Logger());

      var cast = ((LambdaP) lambdaP.polymorphic());
      assertThat(cast.scope().referencables().get("param")).isEqualTo(param);
    }
  }
}
