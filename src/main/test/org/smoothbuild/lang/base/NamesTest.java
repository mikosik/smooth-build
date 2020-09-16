package org.smoothbuild.lang.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Names.isLegalName;

import org.junit.jupiter.api.Test;

public class NamesTest {
  @Test
  public void name_with_letters_only_is_legal() {
    assertThat(isLegalName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"))
        .isTrue();
  }

  @Test
  public void name_with_underscore_is_legal() {
    assertThat(isLegalName("some_name"))
        .isTrue();
  }

  @Test
  public void name_with_underscore_at_beginning_is_legal() {
    assertThat(isLegalName("_somename"))
        .isTrue();
  }

  @Test
  public void name_with_underscore_at_end_is_legal() {
    assertThat(isLegalName("somename_"))
        .isTrue();
  }

  @Test
  public void name_with_dash_is_legal() {
    assertThat(isLegalName("some-name"))
        .isTrue();
  }

  @Test
  public void name_with_dash_at_beginning_is_illegal() {
    assertThat(isLegalName("-somename"))
        .isFalse();
  }

  @Test
  public void name_with_dash_at_end_is_legal() {
    assertThat(isLegalName("somename-"))
        .isTrue();
  }

  @Test
  public void name_with_dot_is_legal() {
    assertThat(isLegalName("some.name"))
        .isTrue();
  }

  @Test
  public void name_with_dot_at_beginning_is_illegal() {
    assertThat(isLegalName(".somename"))
        .isFalse();
  }

  @Test
  public void name_with_dot_at_end_is_legal() {
    assertThat(isLegalName("somename."))
        .isTrue();
  }

  @Test
  public void empty_name_is_illegal() {
    assertThat(isLegalName(""))
        .isFalse();
  }
}
