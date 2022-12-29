package org.smoothbuild.compile.fs.ps;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.compile.fs.ps.ScopesInitializer.initializeScopes;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import java.util.Optional;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compile.fs.ps.ast.expr.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.expr.NamedEvaluableP;
import org.smoothbuild.testing.TestContext;

public class ScopesInitializerTest extends TestContext {
  @Nested
  class _module {
    @Test
    public void module_scope_has_its_member_function_in_refables() {
      var namedFuncP = namedFuncP("myFunc");
      var moduleP = new ModuleP(list(), list(namedFuncP));

      initializeScopes(moduleP);

      assertThat(moduleP.scope().refables().get("myFunc"))
          .isEqualTo(namedFuncP);
    }

    @Test
    public void module_scope_has_its_member_value_in_refables() {
      var namedValueP = namedValueP("myValue");
      var moduleP = new ModuleP(list(), list(namedValueP));

      initializeScopes(moduleP);

      assertThat(moduleP.scope().refables().get("myValue"))
          .isEqualTo(namedValueP);
    }

    @Test
    public void module_scope_has_its_member_function_param_default_value_in_refables() {
      var defaultValue = namedValueP("myFunc:param");
      var param = itemP("param", defaultValue);
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = new ModuleP(list(), list(namedFuncP));

      initializeScopes(moduleP);

      assertThat(moduleP.scope().refables().get("myFunc:param"))
          .isEqualTo(defaultValue);
    }
  }

  @Nested
  class _function {
    @Nested
    class _parameter_default_value {
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
      public void parameter_default_value_scope_refables_does_not_contain_other_parameter() {
        var param1DefaultValue = namedValueP("myFunc:param");
        var param1 = itemP("param1", param1DefaultValue);
        var param2 = itemP("param2");
        var namedFuncP = namedFuncP("myFunc", nlist(param1, param2));
        var moduleP = new ModuleP(list(), list(namedFuncP));

        initializeScopes(moduleP);

        assertThat(param1DefaultValue.scope().refables().getOptional("param2"))
            .isEqualTo(Optional.empty());
      }

      @Test
      public void parameter_default_value_scope_refables_contains_function_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInRefables(namedFuncP("myFunc"));
      }

      @Test
      public void parameter_default_value_scope_refables_contains_value_from_module() {
        testThatParameterDefaultValueScopeHasModuleMemberInRefables(namedValueP("myValue"));
      }

      private void testThatParameterDefaultValueScopeHasModuleMemberInRefables(
          NamedEvaluableP member) {
        var defaultValue = namedValueP("myFuncWithParamWithDefaultValue:param");
        var param = itemP("param", defaultValue);
        var namedFuncP = namedFuncP("myFuncWithParamWithDefaultValue", nlist(param));
        var moduleP = new ModuleP(list(), list(namedFuncP, member));

        initializeScopes(moduleP);

        assertThat(defaultValue.scope().refables().get(member.name()))
            .isEqualTo(member);
      }
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
    public void named_function_scope_has_its_sibling_named_value() {
      var namedValueP = namedValueP("myValue");
      var param = itemP("param");
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = new ModuleP(list(), list(namedFuncP, namedValueP));

      initializeScopes(moduleP);

      assertThat(namedFuncP.scope().refables().get("myValue"))
          .isEqualTo(namedValueP);
    }

    @Test
    public void named_function_scope_has_its_sibling_named_function() {
      var otherFunc = namedFuncP("otherFunc");
      var param = itemP("param");
      var namedFuncP = namedFuncP("myFunc", nlist(param));
      var moduleP = new ModuleP(list(), list(namedFuncP, otherFunc));

      initializeScopes(moduleP);

      assertThat(namedFuncP.scope().refables().get("otherFunc"))
          .isEqualTo(otherFunc);
    }
  }

  @Nested
  class _anonymous_function {
    @Test
    public void anonymous_function_scope_has_value_that_encloses_it() {
      var param = itemP("param");
      var anonymousFuncP = anonymousFuncP(nlist(param), intP());
      var namedValueP = namedValueP("myValue", anonymousFuncP);
      var moduleP = new ModuleP(list(), list(namedValueP));

      initializeScopes(moduleP);

      assertThat(anonymousFuncP.scope().refables().get("myValue"))
          .isEqualTo(namedValueP);
    }

    @Test
    public void anonymous_function_scope_has_function_that_encloses_it() {
      var param = itemP("param");
      var anonymousFuncP = anonymousFuncP(nlist(param), intP());
      var namedValueP = namedFuncP("myFunc", anonymousFuncP);
      var moduleP = new ModuleP(list(), list(namedValueP));

      initializeScopes(moduleP);

      assertThat(anonymousFuncP.scope().refables().get("myFunc"))
          .isEqualTo(namedValueP);
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
}
