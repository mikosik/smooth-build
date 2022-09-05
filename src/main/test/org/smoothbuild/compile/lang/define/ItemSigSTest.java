package org.smoothbuild.compile.lang.define;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.type.TestingTS;

import com.google.common.testing.EqualsTester;
import com.google.common.truth.Truth;

public class ItemSigSTest extends TestingTS {
  private final String name = "name";
  private ItemSigS item;

  @Test
  public void null_type_is_forbidden() {
    assertCall(() -> new ItemSigS(null, name))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void null_name_is_forbidden() {
    assertCall(() -> new ItemSigS(string(), (String) null))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void type_getter() {
    item = new ItemSigS(string(), name);
    Truth.assertThat(item.type())
        .isEqualTo(string());
  }

  @Test
  public void name_getter() {
    item = new ItemSigS(string(), name);
    assertThat(item.nameO())
        .isEqualTo(Optional.of(name));
  }

  @Test
  public void equals_and_hash_code() {
    EqualsTester tester = new EqualsTester();
    tester.addEqualityGroup(new ItemSigS(string(), "name"));
    tester.addEqualityGroup(new ItemSigS(string(), "name2"));
    tester.addEqualityGroup(new ItemSigS(blob(), "name"));
    tester.testEquals();
  }

  @Test
  public void to_padded_string() {
    item = new ItemSigS(string(), "myName");
    assertThat(item.toPaddedString(10, 13))
        .isEqualTo("String    : myName       ");
  }

  @Test
  public void to_padded_string_for_short_limits() {
    item = new ItemSigS(string(), "myName");
    assertThat(item.toPaddedString(1, 1))
        .isEqualTo("String: myName");
  }

  @Test
  public void to_string() {
    item = new ItemSigS(string(), "myName");
    assertThat(item.toString())
        .isEqualTo("String myName");
  }

  @Test
  public void to_string_without_name() {
    item = new ItemSigS(string(), Optional.empty());
    assertThat(item.toString())
        .isEqualTo("String");
  }
}
