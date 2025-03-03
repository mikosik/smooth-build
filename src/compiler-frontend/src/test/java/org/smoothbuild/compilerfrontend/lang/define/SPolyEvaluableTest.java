package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.compilerfrontend.lang.name.Fqn.fqn;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SPolyEvaluableTest extends FrontendCompilerTestContext {
  @Nested
  class _toSourcecode {
    @Test
    void annotated_func() {
      var params = nlist(sItem(varA(), "p1"), sItem(sIntType(), "p2", "default:value"));
      var resultType = sStringType();
      var typeParams = list(varA());
      var annotation = sNativeAnnotation("path");
      var func = new SAnnotatedFunc(annotation, resultType, fqn("myFunc"), params, location(1));
      var poly = sPoly(typeParams, func);
      assertThat(poly.toSourceCode())
          .isEqualTo(
              """
          @Native("path")
          String myFunc<A>(A p1, Int p2 = default:value);""");
    }

    @Test
    void annotated_value() {
      var sAnnotation = new SAnnotation("MyAnnotation", sString(7, "myPath"), location(17));
      var sAnnotatedValue =
          new SAnnotatedValue(sAnnotation, varA(), fqn("module:myValue"), location(7));
      var poly = sPoly(list(varA()), sAnnotatedValue);
      assertThat(poly.toSourceCode())
          .isEqualTo("""
          @MyAnnotation("myPath")
          A myValue<A>;""");
    }

    @Test
    void named_expr_func() {
      var params = nlist(sItem(varA(), "p1"), sItem(sIntType(), "p2", "default:value"));
      var resultType = sStringType();
      var func =
          new SNamedExprFunc(resultType, fqn("module:myFunc"), params, sInt(17), location(1));
      var poly = sPoly(list(varA()), func);
      assertThat(poly.toSourceCode())
          .isEqualTo("""
        String myFunc<A>(A p1, Int p2 = default:value)
          = 17;""");
    }

    @Test
    void named_expr_value() {
      var value = new SNamedExprValue(varA(), fqn("module:myValue"), sInt(9), location(7));
      var poly = sPoly(list(varA()), value);
      assertThat(poly.toSourceCode()).isEqualTo("""
        A myValue<A>
          = 9;""");
    }
  }
}
