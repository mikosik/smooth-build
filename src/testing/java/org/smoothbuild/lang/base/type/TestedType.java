package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.generic;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.testing.common.TestingLocation.loc;
import static org.smoothbuild.util.Lists.list;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.smoothbuild.lang.base.Item;

import com.google.common.collect.ImmutableList;

public record TestedType(Type type, String literal, Object value, Set<String> declarations) {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedType A = new TestedType(
      generic("A"),
      null,
      null
  );
  public static final TestedType B = new TestedType(
      generic("B"),
      null,
      null
  );
  public static final TestedType BLOB = new TestedType(
      blob(),
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedType BOOL = new TestedType(
      bool(),
      "true",
      new String(new byte[] {1})
  );
  public static final TestedType NOTHING = new TestedType(
      nothing(),
      """
          reportError("e")""",
      null,
      Set.of("Nothing reportError(String message);"));
  public static final TestedType STRING = new TestedType(
      string(),
      "\"abc\"",
      "abc"
  );
  public static final TestedType STRUCT_WITH_BLOB = new TestedType(
      struct("Data", loc(7), list(new Item(blob(), "value", Optional.empty(), loc(7)))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"));
  public static final TestedType STRUCT_WITH_BOOL = new TestedType(
      struct("Flag", loc(7), list(new Item(bool(), "value", Optional.empty(), loc(7)))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"));
  public static final TestedType STRUCT_WITH_STRING = new TestedType(
      struct("Person", loc(7), list(new Item(string(), "name", Optional.empty(), loc(7)))),
      """
          person("John")""",
      null,
      Set.of("Person{ String name }"));

  public static final ImmutableList<TestedType> ELEMENTARY_TYPES =
      ImmutableList.<TestedType>builder()
      .add(BLOB)
      .add(BOOL)
      .add(NOTHING)
      .add(STRING)
      .add(STRUCT_WITH_BLOB)
      .add(STRUCT_WITH_BOOL)
      .add(STRUCT_WITH_STRING)
      .add(A)
      .build();

  public static final List<TestedType> CONCRETE_TESTED_TYPES = ImmutableList.of(
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT_WITH_BLOB,
      STRUCT_WITH_BOOL,
      STRUCT_WITH_STRING,
      a(BLOB),
      a(BOOL),
      a(NOTHING),
      a(STRING),
      a(STRUCT_WITH_BLOB),
      a(STRUCT_WITH_BOOL),
      a(STRUCT_WITH_STRING),
      a(a(BLOB)),
      a(a(BOOL)),
      a(a(NOTHING)),
      a(a(STRING)),
      a(a(STRUCT_WITH_BLOB)),
      a(a(STRUCT_WITH_BOOL)),
      a(a(STRUCT_WITH_STRING))
  );

  public static final List<TestedType> TESTED_TYPES = ImmutableList.<TestedType>builder()
      .addAll(CONCRETE_TESTED_TYPES)
      .add(A)
      .add(a(A))
      .add(a(a(A)))
      .build();

  public static TestedType a(TestedType type) {
    if (type == NOTHING) {
      return new TestedType(
          Types.array(nothing()),
          "[]",
          list()
      );
    } else {
      Object value = type.value == null ? null : list(type.value);
      return a(type, value);
    }
  }

  private static TestedType a(TestedType type, Object value) {
    return new TestedType(
        Types.array(type.type),
        "[" + type.literal + "]",
        value,
        type.declarations
    );
  }

  public TestedType(Type type, String literal, Object value) {
    this(type, literal, value, Set.of());
  }

  public String name() {
    return type.name();
  }

  public String q() {
    return "`" + name() + "`";
  }

  public String declarationsAsString() {
    return join("\n", declarations);
  }
}
