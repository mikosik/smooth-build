package org.smoothbuild.compilerfrontend.lang.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;

public class STypeSchemeTest extends FrontendCompilerTestContext {
  @Test
  void equals_and_hashcode() {
    new EqualsTester()
        .addEqualityGroup(new STypeScheme(list(), sIntType()))
        .addEqualityGroup(new STypeScheme(list(), sBlobType()))
        .addEqualityGroup(new STypeScheme(list(), sIntArrayT()))
        .addEqualityGroup(new STypeScheme(list(), sIntFuncType()))
        .addEqualityGroup(new STypeScheme(list(), sFuncType(sIntType(), sIntType())))
        .addEqualityGroup(new STypeScheme(list(varA()), sVarAArrayT()))
        .addEqualityGroup(new STypeScheme(list(varA()), sTupleType(varA(), varA())))
        .addEqualityGroup(new STypeScheme(list(varA()), sVarAFuncType()))
        .addEqualityGroup(new STypeScheme(list(varA()), sFuncType(varA(), varA())))
        .testEquals();
  }

  @Test
  void to_string() {
    var aVar = sVar("A");
    var typeScheme = new STypeScheme(list(aVar), sFuncType(sIntType(), aVar));
    assertThat(typeScheme.toString()).isEqualTo("<A>(Int)->A");
  }
}
