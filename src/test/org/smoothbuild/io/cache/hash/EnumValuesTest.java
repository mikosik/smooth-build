package org.smoothbuild.io.cache.hash;

import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;

public class EnumValuesTest {
  String value1 = "value 1";
  String value2 = "value 2";
  String value3 = "value 3";

  EnumValues<String> enumValues;

  // isValidByte()

  @Test
  public void negative_byte_value_is_not_valid_byte() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.isValidByte((byte) -1));
    thenReturned(false);
  }

  @Test
  public void zero_is_valid_byte() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.isValidByte((byte) 0));
    thenReturned(true);
  }

  @Test
  public void values_count_is_not_valid_byte() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.isValidByte((byte) 3));
    thenReturned(false);
  }

  // byteToValue()

  @Test
  public void negative_byte_value_cannot_be_translated_to_value() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues).byteToValue((byte) -1);
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void values_count_cannot_be_translated_to_value() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues).byteToValue((byte) 3);
    thenThrown(IllegalArgumentException.class);
  }

  @Test
  public void byte_to_value_returns_first_value_for_zero_argument() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.byteToValue((byte) 0));
    thenReturned(value1);
  }

  @Test
  public void byte_to_value_returns_last_value_for_count_minus_one_argument() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.byteToValue((byte) 2));
    thenReturned(value3);
  }

  // valueToByte()

  @Test
  public void value_to_byte_returns_value_index() {
    given(enumValues = new EnumValues<String>(value1, value2, value3));
    when(enumValues.valueToByte(value2));
    thenReturned((byte) 1);
  }

  @Test
  public void value_to_byte_throws_exception_for_value_that_was_not_passed_to_constructor() {
    given(enumValues = new EnumValues<String>(value1, value2));
    when(enumValues).valueToByte(value3);
    thenThrown(IllegalArgumentException.class);
  }
}
