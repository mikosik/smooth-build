package org.smoothbuild.bytecode.type;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.type.TestedAssignCasesB.TESTED_ASSIGN_CASES_B;

import java.util.List;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import org.smoothbuild.testing.type.TestedAssignSpecB;

public class IsAssignableTest {

  @ParameterizedTest
  @MethodSource("isAssignable_test_data")
  public void isAssignable(TestedAssignSpecB spec) {
    var target = spec.target().type();
    var source = spec.source().type();
    assertThat(IsAssignable.isAssignable(target, source))
        .isEqualTo(spec.allowed());
  }

  private static List<? extends TestedAssignSpecB> isAssignable_test_data() {
    return TESTED_ASSIGN_CASES_B.assignment_test_specs(true);
  }
}
