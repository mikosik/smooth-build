package org.smoothbuild.accept;

import static com.google.common.truth.Truth.assertThat;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.accept.AcceptanceTestCase;

public class ValTest extends AcceptanceTestCase {
  @Test
  public void val_body_can_be_reference_to_polymorphic_func_when_value_has_explicit_type()
      throws Exception {
    createUserModule("""
          A myId(A a) = a;
          String(String) myId2 = myId;
          result = myId2("abc");
          """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(stringB("abc"));
  }

  @Test
  public void val_body_can_be_reference_to_polymorphic_func_when_value_has_no_explicit_type()
      throws Exception {
    createUserModule("""
          A myId(A a) = a;
          myId2 = myId;
          result = myId2("abc");
          """);
    evaluate("result");
    assertThat(artifact())
        .isEqualTo(stringB("abc"));
  }
}
