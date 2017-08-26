package org.smoothbuild.lang.function.base;

import static org.smoothbuild.lang.function.base.Name.isLegalName;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.when;

import org.junit.Test;

import com.google.common.testing.EqualsTester;

public class NameTest {
  private Name name;

  @Test
  public void name_with_letters_only_is_legal() throws Exception {
    when(isLegalName("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ"));
    thenReturned(true);
  }

  @Test
  public void name_with_underscore_is_legal() throws Exception {
    when(isLegalName("some_name"));
    thenReturned(true);
  }

  @Test
  public void name_with_underscore_at_beginning_is_legal() throws Exception {
    when(isLegalName("_somename"));
    thenReturned(true);
  }

  @Test
  public void name_with_underscore_at_end_is_legal() throws Exception {
    when(isLegalName("somename_"));
    thenReturned(true);
  }

  @Test
  public void name_with_dash_is_legal() throws Exception {
    when(isLegalName("some-name"));
    thenReturned(true);
  }

  @Test
  public void name_with_dash_at_beginning_is_illegal() throws Exception {
    when(isLegalName("-somename"));
    thenReturned(false);
  }

  @Test
  public void name_with_dash_at_end_is_legal() throws Exception {
    when(isLegalName("somename-"));
    thenReturned(true);
  }

  @Test
  public void name_with_dot_is_legal() throws Exception {
    when(isLegalName("some.name"));
    thenReturned(true);
  }

  @Test
  public void name_with_dot_at_beginning_is_illegal() throws Exception {
    when(isLegalName(".somename"));
    thenReturned(false);
  }

  @Test
  public void name_with_dot_at_end_is_legal() throws Exception {
    when(isLegalName("somename."));
    thenReturned(true);
  }

  @Test
  public void empty_name_is_illegal() throws Exception {
    when(isLegalName(""));
    thenReturned(false);
  }

  @Test
  public void test_to_string() throws Exception {
    given(name = new Name("abc"));
    when(name).toString();
    thenReturned("abc");
  }

  @Test
  public void testEquals() throws Exception {
    EqualsTester tester = new EqualsTester();

    tester.addEqualityGroup(new Name("a"), new Name("a"));
    tester.addEqualityGroup(new Name("b"));
    tester.addEqualityGroup(new Name("c"));
    tester.addEqualityGroup(new Name("ab"));
    tester.addEqualityGroup(new Name("abc"));

    tester.testEquals();
  }
}
