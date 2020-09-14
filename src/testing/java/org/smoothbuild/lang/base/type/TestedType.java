package org.smoothbuild.lang.base.type;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.util.Lists.list;

import java.util.List;

import com.google.common.collect.ImmutableList;

public record TestedType(String name, String literal, Object value, String declarations) {
  public static final TestedType A = new TestedType(
      "A",
      null,
      null
  );
  public static final TestedType B = new TestedType(
      "A",
      null,
      null
  );
  public static final TestedType BLOB = new TestedType(
      blob().name(),
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedType BOOL = new TestedType(
      bool().name(),
      "true",
      new String(new byte[] {1})
  );
  public static final TestedType NOTHING = new TestedType(
      nothing().name(),
      """
          reportError("e")""",
      null,
      "Nothing reportError(String message);");
  public static final TestedType STRING = new TestedType(
      string().name(),
      "\"abc\"",
      "abc"
  );
  public static final TestedType STRUCT_WITH_BLOB = new TestedType(
      "Data",
      "data(0xAB)",
      null,
      "Data{ Blob value }");
  public static final TestedType STRUCT_WITH_BOOL = new TestedType(
      "Flag",
      "flag(true)",
      null,
      "Flag{ Bool value }");
  public static final TestedType STRUCT_WITH_STRING = new TestedType(
      "Person",
      """
          person("John")""",
      null,
      "Person{ String name }");

  public static final TestedType A_ARRAY = array(A);
  public static final TestedType B_ARRAY = array(B);
  public static final TestedType BLOB_ARRAY = array(BLOB);
  public static final TestedType BOOL_ARRAY = array(BOOL);
  public static final TestedType NOTHING_ARRAY = new TestedType(
      "[" + nothing().name() + "]",
      "[]",
      list()
  );
  public static final TestedType STRING_ARRAY = array(STRING);
  public static final TestedType STRUCT_WITH_BLOB_ARRAY = array(STRUCT_WITH_BLOB);
  public static final TestedType STRUCT_WITH_BOOL_ARRAY = array(STRUCT_WITH_BOOL);
  public static final TestedType STRUCT_WITH_STRING_ARRAY = array(STRUCT_WITH_STRING);

  public static final TestedType A_ARRAY2 = array(A_ARRAY);
  public static final TestedType B_ARRAY2 = array(B_ARRAY);
  public static final TestedType BLOB_ARRAY2 = array(BLOB_ARRAY);
  public static final TestedType BOOL_ARRAY2 = array(BOOL_ARRAY);
  public static final TestedType NOTHING_ARRAY2 = array(NOTHING_ARRAY);
  public static final TestedType STRING_ARRAY2 = array(STRING_ARRAY);
  public static final TestedType STRUCT_WITH_BLOB_ARRAY2 = array(STRUCT_WITH_BLOB_ARRAY);
  public static final TestedType STRUCT_WITH_BOOL_ARRAY2 = array(STRUCT_WITH_BOOL_ARRAY);
  public static final TestedType STRUCT_WITH_STRING_ARRAY2 = array(STRUCT_WITH_STRING_ARRAY);

  public static final List<TestedType> CONCRETE_TESTED_TYPES = ImmutableList.of(
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT_WITH_BLOB,
      STRUCT_WITH_BOOL,
      STRUCT_WITH_STRING,
      BLOB_ARRAY,
      BOOL_ARRAY,
      NOTHING_ARRAY,
      STRING_ARRAY,
      STRUCT_WITH_BLOB_ARRAY,
      STRUCT_WITH_BOOL_ARRAY,
      STRUCT_WITH_STRING_ARRAY,
      BLOB_ARRAY2,
      BOOL_ARRAY2,
      NOTHING_ARRAY2,
      STRING_ARRAY2,
      STRUCT_WITH_BLOB_ARRAY2,
      STRUCT_WITH_BOOL_ARRAY2,
      STRUCT_WITH_STRING_ARRAY2
  );

  public static final List<TestedType> TESTED_TYPES = ImmutableList.<TestedType>builder()
      .addAll(CONCRETE_TESTED_TYPES)
      .add(A)
      .add(A_ARRAY)
      .add(A_ARRAY2)
      .build();

  private static TestedType array(TestedType type) {
    Object value = type.value == null ? null : list(type.value);
    return array(type, value);
  }

  private static TestedType array(TestedType type, Object value) {
    return new TestedType(
        "[" + type.name + "]",
        "[" + type.literal + "]",
        value,
        type.declarations
    );
  }

  public TestedType(String name, String literal, Object value) {
    this(name, literal, value, "");
  }

  public String q() {
    return "`" + name + "`";
  }
}
