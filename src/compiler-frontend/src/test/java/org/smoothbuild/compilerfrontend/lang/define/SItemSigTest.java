package org.smoothbuild.compilerfrontend.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.commontesting.AssertCall.assertCall;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.blobTS;
import static org.smoothbuild.compilerfrontend.testing.TestingSExpression.stringTS;

import com.google.common.testing.EqualsTester;
import org.junit.jupiter.api.Test;

public class SItemSigTest {
  private final String name = "name";
  private SItemSig item;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new SItemSig(null, name)).throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new SItemSig(stringTS(), null)).throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new SItemSig(stringTS(), name);
    assertThat(item.type()).isEqualTo(stringTS());
  }

  @Test
  public void name_getter() {
    item = new SItemSig(stringTS(), name);
    assertThat(item.name()).isEqualTo(name);
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new SItemSig(stringTS(), "name"));
    tester.addEqualityGroup(new SItemSig(stringTS(), "name2"));
    tester.addEqualityGroup(new SItemSig(blobTS(), "name"));
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new SItemSig(stringTS(), "myName");
    assertThat(item.toPaddedString(10, 13)).isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new SItemSig(stringTS(), "myName");
    assertThat(item.toPaddedString(1, 1)).isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new SItemSig(stringTS(), "myName");
    assertThat(item.toString()).isEqualTo("String myName");
  }
}
