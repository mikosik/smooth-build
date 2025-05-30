package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.name.Name;

public class SItemSigTest extends FrontendCompilerTestContext {
  private final Name name = referenceableName("name");

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
    var item = new SItemSig(sStringType(), name);
    assertThat(item.type()).isEqualTo(sStringType());
  }

  @Test
  void name_getter() {
    var item = new SItemSig(sStringType(), name);
    assertThat(item.name()).isEqualTo(name);
  }

  @Test
  void to_source_code() {
    var item = new SItemSig(sStringType(), referenceableName("myName"));
    assertThat(item.toSourceCode()).isEqualTo("String myName");
  }

  @Test
  void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new SItemSig(sStringType(), referenceableName("name")));
    tester.addEqualityGroup(new SItemSig(sStringType(), referenceableName("name2")));
    tester.addEqualityGroup(new SItemSig(sBlobType(), referenceableName("name")));
    tester.testEquals();
  }

  @Test
  void to_padded_string() {
    var item = new SItemSig(sStringType(), referenceableName("myName"));
    assertThat(item.toPaddedString(10, 13)).isEqualTo("String    : myName       ");
  }

  @Test
  void to_padded_string_for_short_limits() {
    var item = new SItemSig(sStringType(), referenceableName("myName"));
    assertThat(item.toPaddedString(1, 1)).isEqualTo("String: myName");
  }

  @Test
  void to_string() {
    var item = new SItemSig(sStringType(), referenceableName("myName"));
    assertThat(item.toString()).isEqualTo("String myName");
  }
}
