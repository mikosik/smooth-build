package org.smoothbuild.lang.object.db;

import static com.google.common.truth.Truth.assertThat;
import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;
import static org.smoothbuild.testing.common.AssertCall.assertCall;
import static org.smoothbuild.util.Lists.list;

import org.junit.jupiter.api.Test;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.type.GenericArrayType;
import org.smoothbuild.lang.object.type.GenericType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    assertThat(objectFactory().blob(sink -> sink.write(bytes)).source().readByteString())
        .isEqualTo(bytes);
  }

  @Test
  public void error_severity_is_error() {
    assertThat(severity(objectFactory().errorMessage("text")))
        .isEqualTo(ERROR);
  }

  @Test
  public void warning_severity_is_warning() {
    assertThat(severity(objectFactory().warningMessage("text")))
        .isEqualTo(WARNING);
  }

  @Test
  public void info_severity_is_info() {
    assertThat(severity(objectFactory().infoMessage("text")))
        .isEqualTo(INFO);
  }

  @Test
  public void text_returns_text() {
    assertThat(text(objectFactory().errorMessage("text")))
        .isEqualTo("text");
  }

  @Test
  public void contains_all_basic_types_initially() {
    assertThat(emptyCacheObjectFactory().containsType(blobType().name()))
        .isTrue();
    assertThat(emptyCacheObjectFactory().containsType(boolType().name()))
        .isTrue();
    assertThat(emptyCacheObjectFactory().containsType(nothingType().name()))
        .isTrue();
    assertThat(emptyCacheObjectFactory().containsType(stringType().name()))
        .isTrue();
  }

  @Test
  public void contains_not_array_type_that_was_queried_before() {
    Type arrayType = emptyCacheObjectFactory().arrayType(emptyCacheObjectFactory().stringType());
    assertThat(emptyCacheObjectFactory().containsType(arrayType.name()))
        .isFalse();
  }

  @Test
  public void contains_struct_that_was_added_before() {
    emptyCacheObjectFactory().structType("MyStruct", list());
    assertThat(emptyCacheObjectFactory().containsType("MyStruct"))
        .isTrue();
  }

  @Test
  public void bool_type_can_be_retrieved_by_name() {
    assertThat(emptyCacheObjectFactory().getType("Bool"))
        .isEqualTo(boolType());
  }

  @Test
  public void string_type_can_be_retrieved_by_name() {
    assertThat(emptyCacheObjectFactory().getType("String"))
        .isEqualTo(stringType());
  }

  @Test
  public void blob_type_can_be_retrieved_by_name() {
    assertThat(emptyCacheObjectFactory().getType("Blob"))
        .isEqualTo(blobType());
  }

  @Test
  public void nothing_type_can_be_retrieved_by_name() {
    assertThat(emptyCacheObjectFactory().getType("Nothing"))
        .isEqualTo(nothingType());
  }

  @Test
  public void generic_type_can_be_retrieved_by_name() {
    assertThat(emptyCacheObjectFactory().getType("A"))
        .isEqualTo(new GenericType("A"));
  }

  @Test
  public void custom_struct_type_can_be_retrieved_by_name() {
    Type type = emptyCacheObjectFactory().structType(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation())));
    assertThat(emptyCacheObjectFactory().getType("MyStruct"))
        .isEqualTo(type);
  }

  @Test
  public void generic_array_type_can_be_created() {
    Type a = emptyCacheObjectFactory().getType("A");
    assertThat(emptyCacheObjectFactory().arrayType(a))
        .isEqualTo(new GenericArrayType(new GenericType("A")));
  }

  @Test
  public void reusing_struct_name_causes_exception() {
    emptyCacheObjectFactory().structType(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation())));
    assertCall(() -> emptyCacheObjectFactory().structType("MyStruct", list()))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void reusing_basic_type_name_as_struct_name_causes_exception() {
    assertCall(() -> emptyCacheObjectFactory().structType("String", list()))
        .throwsException(IllegalStateException.class);
  }

  @Test
  public void type_type_can_not_be_retrieved_by_name() {
    assertCall(() -> emptyCacheObjectFactory().getType("Type"))
        .throwsException(IllegalStateException.class);
  }
}
