package org.smoothbuild.compilerfrontend.lang.type.tool;

import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.blobTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.intTS;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.tempVar;
import static org.smoothbuild.compilerfrontend.testing.TestingExpressionS.tupleTS;

import com.google.common.truth.Truth;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.compilerfrontend.lang.type.TypeS;

public class AssertStructuresAreEqualTest {
  @ParameterizedTest
  @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingExpressionS#nonCompositeTypes")
  public void concrete_non_composite_types_have_equal_structure_to_itself(TypeS typeS) {
    structuresAreEqualReturnsTrue(typeS, typeS);
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingExpressionS#compositeTypeSFactories")
  public void concrete_composite_types_have_equal_structure_to_itself(
      Function<TypeS, TypeS> composedFactory) {
    structuresAreEqualReturnsTrue(composedFactory.apply(intTS()), composedFactory.apply(intTS()));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingExpressionS#compositeTypeSFactories")
  public void types_have_equal_structure_when_only_temp_var_names_differ(
      Function<TypeS, TypeS> composedFactory) {
    structuresAreEqualReturnsTrue(
        composedFactory.apply(tempVar("1")), composedFactory.apply(tempVar("2")));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingExpressionS#compositeTypeSFactories")
  public void types_have_not_equal_structure_when_temp_var_is_replaced_with_concrete_type(
      Function<TypeS, TypeS> composedFactory) {
    structuresAreEqualReturnsFalse(
        composedFactory.apply(tempVar("1")), composedFactory.apply(intTS()));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingExpressionS#compositeTypeSFactories")
  public void
      types_have_not_equal_structure_when_concrete_type_is_replaced_with_other_concrete_type(
          Function<TypeS, TypeS> composedFactory) {
    structuresAreEqualReturnsFalse(composedFactory.apply(blobTS()), composedFactory.apply(intTS()));
  }

  @Test
  public void
      type_have_not_equal_structure_when_two_different_temp_vars_are_replaced_with_single_one() {
    var type1 = tupleTS(tempVar("1"), tempVar("1"));
    var type2 = tupleTS(tempVar("2"), tempVar("3"));
    structuresAreEqualReturnsFalse(type1, type2);
  }

  private static void structuresAreEqualReturnsTrue(TypeS type1, TypeS type2) {
    Truth.assertThat(AssertStructuresAreEqual.structuresAreEqual(type1, type2)).isTrue();
  }

  private static void structuresAreEqualReturnsFalse(TypeS type1, TypeS type2) {
    Truth.assertThat(AssertStructuresAreEqual.structuresAreEqual(type1, type2)).isFalse();
  }
}
