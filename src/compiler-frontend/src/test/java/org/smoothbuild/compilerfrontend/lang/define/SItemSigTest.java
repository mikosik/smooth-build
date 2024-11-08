package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.testing.FrontendCompilerTestContext;

public class SItemSigTest extends FrontendCompilerTestContext {
  private final String name = "name";
  private SItemSig item;

  @Test
  void null_type_is_forbidden() {
    assertCall(() -> new SItemSig(null, name)).throwsException(NullPointerException.class);
  }

  @Test
  void null_name_is_forbidden() {
    assertCall(() -> new SItemSig(sStringType(), null)).throwsException(NullPointerException.class);
  }

  @Test
  void type_getter() {
    item = new SItemSig(sStringType(), name);
    assertThat(item.type()).isEqualTo(sStringType());
  }

  @Test
  void name_getter() {
    item = new SItemSig(sStringType(), name);
    assertThat(item.name()).isEqualTo(name);
  }

  @Test
  void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new SItemSig(sStringType(), "name"));
    tester.addEqualityGroup(new SItemSig(sStringType(), "name2"));
    tester.addEqualityGroup(new SItemSig(sBlobType(), "name"));
    tester.testEquals();
  }

  @Test
  void to_padded_string() {
    item = new SItemSig(sStringType(), "myName");
    assertThat(item.toPaddedString(10, 13)).isEqualTo("String    : myName       ");
  }

  @Test
  void to_padded_string_for_short_limits() {
    item = new SItemSig(sStringType(), "myName");
    assertThat(item.toPaddedString(1, 1)).isEqualTo("String: myName");
  }

  @Test
  void to_string() {
    item = new SItemSig(sStringType(), "myName");
    assertThat(item.toString()).isEqualTo("String myName");
  }
}
