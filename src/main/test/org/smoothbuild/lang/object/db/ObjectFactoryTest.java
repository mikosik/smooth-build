package org.smoothbuild.lang.object.db;

import static org.smoothbuild.lang.base.Location.unknownLocation;
import static org.smoothbuild.lang.object.base.Messages.ERROR;
import static org.smoothbuild.lang.object.base.Messages.INFO;
import static org.smoothbuild.lang.object.base.Messages.WARNING;
import static org.smoothbuild.lang.object.base.Messages.severity;
import static org.smoothbuild.lang.object.base.Messages.text;
import static org.smoothbuild.util.Lists.list;
import static org.testory.Testory.given;
import static org.testory.Testory.thenReturned;
import static org.testory.Testory.thenThrown;
import static org.testory.Testory.when;

import org.junit.Test;
import org.smoothbuild.lang.base.Field;
import org.smoothbuild.lang.object.base.Struct;
import org.smoothbuild.lang.object.type.ArrayType;
import org.smoothbuild.lang.object.type.GenericArrayType;
import org.smoothbuild.lang.object.type.GenericType;
import org.smoothbuild.lang.object.type.StructType;
import org.smoothbuild.lang.object.type.Type;
import org.smoothbuild.testing.TestingContext;

import okio.ByteString;

public class ObjectFactoryTest extends TestingContext {
  private final ByteString bytes = ByteString.encodeUtf8("aaa");
  private Struct message;
  private StructType type;
  private ArrayType arrayType;
  private Type a;

  @Test
  public void blob_data_can_be_read_back() throws Exception {
    when(objectFactory().blob(sink -> sink.write(bytes)).source().readByteString());
    thenReturned(bytes);
  }

  @Test
  public void error_severity_is_error() {
    given(message = objectFactory().errorMessage("text"));
    when(() -> severity(message));
    thenReturned(ERROR);
  }

  @Test
  public void warning_severity_is_warning() {
    given(message = objectFactory().warningMessage("text"));
    when(() -> severity(message));
    thenReturned(WARNING);
  }

  @Test
  public void info_severity_is_info() {
    given(message = objectFactory().infoMessage("text"));
    when(() -> severity(message));
    thenReturned(INFO);
  }

  @Test
  public void text_returns_text() {
    given(message = objectFactory().errorMessage("text"));
    when(() -> text(message));
    thenReturned("text");
  }

  @Test
  public void contains_all_basic_types_initially() {
    when(() -> emptyCacheObjectFactory().containsType(blobType().name()));
    thenReturned(true);
    when(() -> emptyCacheObjectFactory().containsType(boolType().name()));
    thenReturned(true);
    when(() -> emptyCacheObjectFactory().containsType(nothingType().name()));
    thenReturned(true);
    when(() -> emptyCacheObjectFactory().containsType(stringType().name()));
    thenReturned(true);
  }

  @Test
  public void contains_not_array_type_that_was_queried_before() {
    given(arrayType = emptyCacheObjectFactory().arrayType(emptyCacheObjectFactory().stringType()));
    when(() -> emptyCacheObjectFactory().containsType(arrayType.name()));
    thenReturned(false);
  }

  @Test
  public void contains_struct_that_was_added_before() {
    given(emptyCacheObjectFactory()).structType("MyStruct", list());
    when(() -> emptyCacheObjectFactory().containsType("MyStruct"));
    thenReturned(true);
  }

  @Test
  public void bool_type_can_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("Bool"));
    thenReturned(boolType());
  }

  @Test
  public void string_type_can_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("String"));
    thenReturned(stringType());
  }

  @Test
  public void blob_type_can_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("Blob"));
    thenReturned(blobType());
  }

  @Test
  public void nothing_type_can_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("Nothing"));
    thenReturned(nothingType());
  }

  @Test
  public void generic_type_can_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("A"));
    thenReturned(new GenericType("A"));
  }

  @Test
  public void custom_struct_type_can_be_retrieved_by_name() {
    given(type = emptyCacheObjectFactory().structType(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation()))));
    when(() -> emptyCacheObjectFactory().getType("MyStruct"));
    thenReturned(type);
  }

  @Test
  public void generic_array_type_can_be_created() {
    given(a = emptyCacheObjectFactory().getType("A"));
    when(() -> emptyCacheObjectFactory().arrayType(a));
    thenReturned(new GenericArrayType(new GenericType("A")));
  }

  @Test
  public void reusing_struct_name_causes_exception() {
    given(type = emptyCacheObjectFactory().structType(
        "MyStruct", list(new Field(stringType(), "field", unknownLocation()))));
    when(() -> emptyCacheObjectFactory().structType("MyStruct", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void reusing_basic_type_name_as_struct_name_causes_exception() {
    when(() -> emptyCacheObjectFactory().structType("String", list()));
    thenThrown(IllegalStateException.class);
  }

  @Test
  public void type_type_can_not_be_retrieved_by_name() {
    when(() -> emptyCacheObjectFactory().getType("Type"));
    thenThrown(IllegalStateException.class);
  }
}
