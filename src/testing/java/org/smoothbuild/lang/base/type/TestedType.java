package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.type.Type.toItemSignatures;
import static org.smoothbuild.lang.base.type.Types.any;
import static org.smoothbuild.lang.base.type.Types.blob;
import static org.smoothbuild.lang.base.type.Types.bool;
import static org.smoothbuild.lang.base.type.Types.nothing;
import static org.smoothbuild.lang.base.type.Types.string;
import static org.smoothbuild.lang.base.type.Types.struct;
import static org.smoothbuild.lang.base.type.Types.variable;
import static org.smoothbuild.util.Lists.list;
import static org.smoothbuild.util.Lists.map;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TestedType {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedType A = new TestedType(
      variable("A"),
      null,
      null
  );
  public static final TestedType B = new TestedType(
      variable("B"),
      null,
      null
  );
  public static final TestedType ANY = new TestedType(
      any(),
      "createAny()",
      null,
      Set.of("Any createAny() = \"abc\";")
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
  public static final TestedType STRUCT = new TestedType(
      struct("Person", list(new ItemSignature(string(), "name", Optional.empty()))),
      """
          person("John")""",
      null,
      Set.of("Person{ String name }"));
  public static final TestedType STRUCT_WITH_BLOB = new TestedType(
      struct("Data", list(new ItemSignature(blob(), "value", Optional.empty()))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"));
  public static final TestedType STRUCT_WITH_BOOL = new TestedType(
      struct("Flag", list(new ItemSignature(bool(), "value", Optional.empty()))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"));

  public static final ImmutableList<TestedType> ELEMENTARY_TYPES = ImmutableList.of(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      A);

  public static final List<TestedType> TESTED_MONOTYPES = ImmutableList.of(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT_WITH_BLOB,
      STRUCT_WITH_BOOL,
      STRUCT,
      a(ANY),
      a(BLOB),
      a(BOOL),
      a(NOTHING),
      a(STRING),
      a(STRUCT_WITH_BLOB),
      a(STRUCT_WITH_BOOL),
      a(STRUCT),
      a(a(ANY)),
      a(a(BLOB)),
      a(a(BOOL)),
      a(a(NOTHING)),
      a(a(STRING)),
      a(a(STRUCT_WITH_BLOB)),
      a(a(STRUCT_WITH_BOOL)),
      a(a(STRUCT))
  );

  public static final List<TestedType> TESTED_TYPES = ImmutableList.<TestedType>builder()
      .addAll(TESTED_MONOTYPES)
      .add(A)
      .add(a(A))
      .add(a(a(A)))
      .build();

  public static TestedType a2(TestedType type) {
    return a(a(type));
  }

  public static TestedType a(TestedType type) {
    if (type == NOTHING) {
      return new TestedArrayType(
          type,
          Types.array(nothing()),
          "[]",
          list(),
          Set.of()
      );
    } else {
      Object value = type.value == null ? null : list(type.value);
      return a(type, value);
    }
  }

  private static TestedType a(TestedType type, Object value) {
    return new TestedArrayType(
        type,
        Types.array(type.type),
        "[" + type.literal + "]",
        value,
        type.declarations
    );
  }

  private final Type type;
  private final String literal;
  private final Object value;
  private final Set<String> declarations;

  public TestedType(Type type, String literal, Object value) {
    this(type, literal, value, Set.of());
  }

  public TestedType(Type type, String literal, Object value, Set<String> declarations) {
    this.type = type;
    this.literal = literal;
    this.value = value;
    this.declarations = declarations;
  }

  public Type type() {
    return type;
  }

  public String literal() {
    return literal;
  }

  public Object value() {
    return value;
  }

  public Set<String> declarations() {
    return declarations;
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

  public boolean isArrayOf(TestedType elem) {
    return false;
  }

  public boolean isArray() {
    return false;
  }

  public boolean isArrayOfArrays() {
    return false;
  }

  public boolean isNothing() {
    return type instanceof NothingType;
  }

  public boolean isFunction(Predicate<TestedType> result, Predicate<TestedType>... parameters) {
    return false;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == this) return true;
    if (obj == null || obj.getClass() != this.getClass()) return false;
    var that = (TestedType) obj;
    return Objects.equals(this.type, that.type) &&
        Objects.equals(this.literal, that.literal) &&
        Objects.equals(this.value, that.value) &&
        Objects.equals(this.declarations, that.declarations);
  }

  @Override
  public int hashCode() {
    return Objects.hash(type, literal, value, declarations);
  }

  @Override
  public String toString() {
    return "TestedType[" +
        "type=" + type + ", " +
        "literal=" + literal + ", " +
        "value=" + value + ", " +
        "declarations=" + declarations + ']';
  }

  public static TestedType function(TestedType resultType, TestedType... paramTestedTypes) {
    var parameters = toItemSignatures(map(list(paramTestedTypes), TestedType::type));
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "%s %s(%s);".formatted(
        resultType.type.name(),
        name,
        join(",", map(parameters, ItemSignature::toString)));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(stream(paramTestedTypes)
            .flatMap(t -> t.declarations().stream())
            .collect(toList()))
        .build();
    return new TestedFunctionType(
        resultType,
        ImmutableList.copyOf(paramTestedTypes),
        Types.function(resultType.type, parameters),
        name,
        null,
        declarations
    );
  }

  public static class TestedFunctionType extends TestedType {
    public final TestedType resultType;
    public final ImmutableList<TestedType> parameters;

    public TestedFunctionType(TestedType resultType, ImmutableList<TestedType> parameters,
        Type type, String literal, Object value, Set<String> declarations) {
      super(type, literal, value, declarations);
      this.resultType = resultType;
      this.parameters = parameters;
    }

    @Override
    public boolean isFunction(Predicate<TestedType> result, Predicate<TestedType>... parameters) {
      if (result.test(resultType) && this.parameters.size() == parameters.length) {
        for (int i = 0; i < this.parameters.size(); i++) {
          if (!parameters[i].test(this.parameters.get(i))) {
            return false;
          }
        }
        return true;
      } else {
        return false;
      }
    }
  }

  public static class TestedArrayType extends TestedType {
    public final TestedType elemType;

    public TestedArrayType(TestedType elemType, Type type, String literal, Object value,
        Set<String> declarations) {
      super(type, literal, value, declarations);
      this.elemType = elemType;
    }

    @Override
    public boolean isArrayOf(TestedType elemType) {
      return this.elemType.equals(elemType);
    }

    @Override
    public boolean isArray() {
      return true;
    }

    @Override
    public boolean isArrayOfArrays() {
      return elemType.isArray();
    }
  }
}
