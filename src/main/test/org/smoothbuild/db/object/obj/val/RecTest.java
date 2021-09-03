package org.smoothbuild.db.object.obj.val;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.testing.TestingContext;

public class RecTest extends TestingContext {
  @Test
  public void creating_rec_with_less_elements_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> recVal(personSpec(), list(strVal("John"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void creating_rec_with_more_elements_than_specified_in_its_spec_causes_exception() {
    assertCall(() -> recVal(personSpec(), list(strVal("John"), strVal("Doe"), strVal("abc"))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void setting_element_to_null_throws_exception() {
    assertCall(() -> recVal(personSpec(), list(strVal("John"), null)))
        .throwsException(NullPointerException.class);
  }

  @Test
  public void setting_element_to_object_of_wrong_spec_throws_exception() {
    assertCall(() -> recVal(personSpec(), list(strVal("John"), intVal(123))))
        .throwsException(IllegalArgumentException.class);
  }

  @Test
  public void spec_of_person_rec_is_person_spec() {
    Rec person = johnDoePerson();
    assertThat(person.spec())
        .isEqualTo(personSpec());
  }

  @Test
  public void element_contains_object_passed_to_builder() {
    Rec person = johnDoePerson();
    assertThat(person.spec())
        .isEqualTo(personSpec());
    assertThat(person.get(0))
        .isEqualTo(strVal("John"));
  }

  @Test
  public void reading_elements_with_negative_index_throws_exception() {
    Rec person = johnDoePerson();
    assertCall(() -> person.get(-1))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void reading_elements_with_index_greater_than_max_index_throws_exception() {
    Rec person = johnDoePerson();
    assertCall(() -> person.get(2))
        .throwsException(IndexOutOfBoundsException.class);
  }

  @Test
  public void super_object_is_equal_to_first_element() {
    Rec person = johnDoePerson();
    assertThat(person.superObject())
        .isEqualTo(strVal("John"));
  }

  @Test
  public void super_object_is_null_when_rec_spec_has_no_elements() {
    Rec rec = recVal(emptyRecSpec(), list());
    assertThat(rec.superObject())
        .isNull();
  }

  @Test
  public void rec_hash_is_different_of_its_element_hash() {
    Rec person = johnDoePerson();
    assertThat(person.hash())
        .isNotEqualTo(person.get(0).hash());
  }

  @Test
  public void recs_with_equal_elements_are_equal() {
    Rec person1 = johnDoePerson();
    Rec person2 = johnDoePerson();
    assertThat(person1)
        .isEqualTo(person2);
  }

  @Test
  public void recs_with_one_element_different_are_not_equal() {
    Rec person1 = johnDoePerson();
    Rec person2 = recVal(personSpec(), list(strVal("John"), strVal("Doe2")));

    assertThat(person1)
        .isNotEqualTo(person2);
  }

  @Test
  public void reecs_with_equal_elements_have_equal_hashes() {
    Rec person1 = johnDoePerson();
    Rec person2 = johnDoePerson();
    assertThat(person1.hash())
        .isEqualTo(person2.hash());
  }

  @Test
  public void recs_with_different_element_have_different_hashes() {
    Rec person1 = johnDoePerson();
    Rec person2 = recVal(personSpec(), list(strVal("John"), strVal("Doe2")));
    assertThat(person1.hash())
        .isNotEqualTo(person2.hash());
  }

  @Test
  public void recs_with_equal_elements_have_equal_hash_codes() {
    Rec person1 = johnDoePerson();
    Rec person2 = johnDoePerson();
    assertThat(person1.hashCode())
        .isEqualTo(person2.hashCode());
  }

  @Test
  public void recs_with_different_element_have_different_hash_codes() {
    Rec person1 = johnDoePerson();
    Rec person2 = recVal(personSpec(), list(strVal("John"), strVal("Doe2")));
        assertThat(person1.hashCode())
            .isNotEqualTo(person2.hashCode());
  }

  @Test
  public void recs_can_be_read_by_hash() {
    Rec person = johnDoePerson();
    assertThat(objectDbOther().get(person.hash()))
        .isEqualTo(person);
  }

  @Test
  public void recs_read_by_hash_have_equal_elements() {
    Rec person = johnDoePerson();
    Rec personRead = (Rec) objectDbOther().get(person.hash());
    assertThat(personRead.get(0))
        .isEqualTo(person.get(0));
    assertThat(personRead.get(1))
        .isEqualTo(person.get(1));
  }

  @Test
  public void to_string() {
    Rec person = johnDoePerson();
    assertThat(person.toString())
        .isEqualTo("""
            {"John","Doe"}:""" + person.hash());
  }

  private Rec johnDoePerson() {
    return recVal(personSpec(), list(strVal("John"), strVal("Doe")));
  }
}
