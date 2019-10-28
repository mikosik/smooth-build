package org.smoothbuild.lang.object.base;

import static org.hamcrest.Matchers.not;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.db.hashed.Hash;
import org.smoothbuild.lang.object.db.ObjectsDbException;
import org.smoothbuild.testing.TestingContext;

public class BoolTest extends TestingContext {
  private Bool bool;
  private Hash hash;

  @Test
  public void type_of_bool_is_bool() throws Exception {
    given(bool = bool(true));
    when(bool).type();
    thenReturned(boolType());
  }

  @Test
  public void data_returns_java_true_from_true_bool() throws Exception {
    given(bool = bool(true));
    when(bool).data();
    thenReturned(true);
  }

  @Test
  public void data_returns_java_false_from_false_bool() throws Exception {
    given(bool = bool(false));
    when(bool).data();
    thenReturned(false);
  }

  @Test
  public void bools_with_equal_values_are_equal() throws Exception {
    when(bool(true));
    thenReturned(bool(true));
  }

  @Test
  public void bools_with_different_values_are_not_equal() throws Exception {
    when(bool(true));
    thenReturned(not(bool(false)));
  }

  @Test
  public void hash_of_true_bools_are_the_same() throws Exception {
    given(bool = bool(true));
    when(bool).hash();
    thenReturned(bool(true).hash());
  }

  @Test
  public void hash_of_false_bools_are_the_same() throws Exception {
    given(bool = bool(false));
    when(bool).hash();
    thenReturned(bool(false).hash());
  }

  @Test
  public void hash_of_bools_with_different_values_is_not_the_same() throws Exception {
    given(bool = bool(true));
    when(bool).hash();
    thenReturned(not(bool(false).hash()));
  }

  @Test
  public void hash_code_of_true_bools_is_the_same() throws Exception {
    given(bool = bool(true));
    when(bool).hashCode();
    thenReturned(bool(true).hashCode());
  }

  @Test
  public void hash_code_of_false_bools_is_the_same() throws Exception {
    given(bool = bool(false));
    when(bool).hashCode();
    thenReturned(bool(false).hashCode());
  }

  @Test
  public void hash_code_of_bools_with_different_values_is_not_the_same() throws Exception {
    given(bool = bool(true));
    when(bool).hashCode();
    thenReturned(not(bool(false).hashCode()));
  }

  @Test
  public void bool_can_be_read_back_by_hash() throws Exception {
    given(bool = bool(true));
    given(hash = bool.hash());
    when(() -> objectsDbOther().get(hash));
    thenReturned(bool);
  }

  @Test
  public void bool_read_back_by_hash_has_same_data() throws Exception {
    given(bool = bool(true));
    given(hash = bool.hash());
    when(() -> ((Bool) objectsDbOther().get(hash)).data());
    thenReturned(true);
  }

  @Test
  public void to_string_contains_value() throws Exception {
    given(bool = bool(true));
    when(() -> bool.toString());
    thenReturned("Bool(true):" + bool.hash());
  }

  @Test
  public void reading_not_stored_bool_fails() throws Exception {
    given(hash = Hash.of(33));
    given(bool = boolType().newInstance(hash));
    when(bool).data();
    thenThrown(ObjectsDbException.class);
  }
}