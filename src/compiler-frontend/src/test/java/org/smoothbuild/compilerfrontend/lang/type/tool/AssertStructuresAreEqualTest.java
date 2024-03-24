package org.smoothbuild.compilerfrontend.lang.type.tool;

import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sBlobType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sIntType;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.sTempVar;

import com.google.common.truth.Truth;
import java.util.function.Function;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.compilerfrontend.lang.type.SType;
import org.smoothbuild.compilerfrontend.testing.TestingSExpression;

public class AssertStructuresAreEqualTest {
  @ParameterizedTest
  @MethodSource("org.smoothbuild.compilerfrontend.testing.TestingSExpression#nonCompositeTypes")
  public void concrete_non_composite_types_have_equal_structure_to_itself(SType sType) {
    structuresAreEqualReturnsTrue(sType, sType);
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
  public void concrete_composite_types_have_equal_structure_to_itself(
      Function<SType, SType> composedFactory) {
    structuresAreEqualReturnsTrue(
        composedFactory.apply(sIntType()), composedFactory.apply(sIntType()));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
  public void types_have_equal_structure_when_only_temp_var_names_differ(
      Function<SType, SType> composedFactory) {
    structuresAreEqualReturnsTrue(
        composedFactory.apply(sTempVar("1")), composedFactory.apply(sTempVar("2")));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
  public void types_have_not_equal_structure_when_temp_var_is_replaced_with_concrete_type(
      Function<SType, SType> composedFactory) {
    structuresAreEqualReturnsFalse(
        composedFactory.apply(sTempVar("1")), composedFactory.apply(sIntType()));
  }

  @ParameterizedTest
  @MethodSource(
      "org.smoothbuild.compilerfrontend.testing.TestingSExpression#compositeTypeSFactories")
  public void
      types_have_not_equal_structure_when_concrete_type_is_replaced_with_other_concrete_type(
          Function<SType, SType> composedFactory) {
    structuresAreEqualReturnsFalse(
        composedFactory.apply(sBlobType()), composedFactory.apply(sIntType()));
  }

  @Test
  public void
      type_have_not_equal_structure_when_two_different_temp_vars_are_replaced_with_single_one() {
    var type1 = TestingSExpression.sTupleType(sTempVar("1"), sTempVar("1"));
    var type2 = TestingSExpression.sTupleType(sTempVar("2"), sTempVar("3"));
    structuresAreEqualReturnsFalse(type1, type2);
  }

  private static void structuresAreEqualReturnsTrue(SType type1, SType type2) {
    Truth.assertThat(AssertStructuresAreEqual.structuresAreEqual(type1, type2)).isTrue();
  }

  private static void structuresAreEqualReturnsFalse(SType type1, SType type2) {
    Truth.assertThat(AssertStructuresAreEqual.structuresAreEqual(type1, type2)).isFalse();
  }
}
