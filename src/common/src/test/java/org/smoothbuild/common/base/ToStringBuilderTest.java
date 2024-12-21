package org.smoothbuild.common.base;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.common.collect.List.list;

import org.junit.jupiter.api.Test;

public class ToStringBuilderTest {
  @Test
  public void testAddField() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addField("field1", "value1");
    var expected = """
        Test(
          field1 = value1
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testAddNullField() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addField("field1", null);
    var expected = """
        Test(
          field1 = null
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testAddListField() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addListField("field1", list("value1", "value2"));
    var expected =
        """
        Test(
          field1 = [
            value1
            value2
          ]
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testAddListFieldWithEmptyList() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addListField("field1", list());
    var expected = """
        Test(
          field1 = [
          ]
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testEmptyBuilder() {
    var toStringBuilder = new ToStringBuilder("Test");
    var expected = """
        Test(
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testMultipleFields() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addField("field1", "value1");
    toStringBuilder.addField("field2", "value2");
    var expected =
        """
        Test(
          field1 = value1
          field2 = value2
        )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }

  @Test
  public void testMultipleListFields() {
    var toStringBuilder = new ToStringBuilder("Test");
    toStringBuilder.addListField("field1", list("value1", "value2"));
    toStringBuilder.addListField("field2", list("value3", "value4"));
    var expected =
        """
            Test(
              field1 = [
                value1
                value2
              ]
              field2 = [
                value3
                value4
              ]
            )""";
    assertThat(toStringBuilder.toString()).isEqualTo(expected);
  }
}
