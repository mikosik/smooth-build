package org.smoothbuild.testing.type;

import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.testing.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.testing.type.TestedTS.TestedArrayTS;
import org.smoothbuild.testing.type.TestedTS.TestedFuncTS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class TestedTSF {
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedTS A = new TestedTS(var("A"), null, null);
  public static final TestedTS B = new TestedTS(var("B"), null, null);
  public static final TestedTS ANY = new TestedTS(
      TestingTS.ANY,
      "createAny()",
      null,
      Set.of(),
      Set.of("Any createAny() = \"abc\";"));
  public static final TestedTS BLOB = new TestedTS(
      TestingTS.BLOB,
      "0x" + encodeString("xyz", US_ASCII).hex(),
      "xyz"
  );
  public static final TestedTS BOOL = new TestedTS(
      TestingTS.BOOL,
      "true",
      new String(new byte[] {1})
  );

  /**
   * Polytypes that can be used in any place. Each var in such a polytype occurs exactly once.
   */
  public static final List<TestedTS> TESTED_SINGLE_VARIABLE_POLYTYPES = list(
      A,
      a(A),
      a(a(A)),
      f(A),
      f(BOOL, A),
      f(f(A)),
      f(BOOL, f(A)),
      f(BOOL, f(BOOL, A))
  );

  /**
   * Polytypes that can be used in any place. Each var in such a polytype occurs more than
   * once.
   */
  public static final List<TestedTS> TESTED_VALID_POLYTYPES = list(
      a(f(A, A)),
      a(a(f(A, A))),
      f(A, A),
      f(A, a(A)),
      f(f(A), f(A)),
      f(f(A), a(A)),
      f(f(A, A)),
      f(BOOL, f(A, A))
  );
  public static final TestedTS INT = new TestedTS(
      TestingTS.INT,
      "123",
      new String(new byte[] {123})
  );
  public static final TestedTS NOTHING = new TestedTS(
      TestingTS.NOTHING,
      "reportError(\"e\")",
      null,
      Set.of(),
      Set.of("@Native(\"impl\") Nothing reportError(String message);"));
  public static final TestedTS STRING = new TestedTS(
      TestingTS.STRING,
      "\"abc\"",
      "abc"
  );
  public static final TestedTS STRUCT = new TestedTS(
      TestingTS.struct("Person", nList(itemSigS(TestingTS.STRING, "name"))),
      "person(\"John\")",
      null,
      Set.of("Person{ String name }"),
      Set.of("Person{ String name }")
  );
  public static final List<TestedTS> TESTED_MONOTYPES = list(
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      a(BLOB),
      a(NOTHING),
      a(STRUCT),
      a(a(BLOB)),
      a(a(NOTHING)),
      a(a(STRUCT)),
      f(BLOB),
      f(NOTHING),
      f(f(BLOB)),
      f(f(NOTHING)),
      f(BLOB, BOOL),
      f(BLOB, NOTHING),
      f(BLOB, f(BOOL)),
      f(BLOB, f(NOTHING))
  );
  public static final List<TestedTS> TESTED_TYPES = ImmutableList.<TestedTS>builder()
      .addAll(TESTED_MONOTYPES)
      .addAll(TESTED_VALID_POLYTYPES)
      .addAll(TESTED_SINGLE_VARIABLE_POLYTYPES)
      .build();
  public static final ImmutableList<TestedTS> ELEMENTARY_TYPES = list(
      ANY,
      BLOB,
      BOOL,
      NOTHING,
      STRING,
      STRUCT,
      A);
  public static final TestedTS STRUCT_WITH_BLOB = new TestedTS(
      TestingTS.struct("Data", nList(itemSigS(TestingTS.BLOB, "value"))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"),
      Set.of("Data{ Blob value }")
  );
  public static final TestedTS STRUCT_WITH_BOOL = new TestedTS(
      TestingTS.struct("Flag", nList(itemSigS(TestingTS.BOOL, "value"))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"),
      Set.of("Flag{ Bool value }")
  );

  public TestingTS testingT() {
    return TestingTS.INSTANCE;
  }

  public TestedTS any() {
    return ANY;
  }

  public TestedTS blob() {
    return BLOB;
  }

  public TestedTS bool() {
    return BOOL;
  }

  public TestedTS int_() {
    return INT;
  }

  public TestedTS nothing() {
    return NOTHING;
  }

  public TestedTS string() {
    return STRING;
  }

  public TestedTS struct() {
    return STRUCT;
  }

  public TestedTS tuple() {
    throw new UnsupportedOperationException();
  }

  public TestedTS tuple(ImmutableList<TestedTS> items) {
    throw new UnsupportedOperationException();
  }

  public TestedTS varA() {
    return A;
  }

  public TestedTS varB() {
    return B;
  }

  public TestedTS array(TestedTS type) {
    return a(type);
  }

  public static TestedTS a(TestedTS type) {
    if (type == NOTHING) {
      return new TestedArrayTS(
          type,
          TestingTS.a(TestingTS.NOTHING),
          "[]",
          list(),
          Set.of(),
          Set.of()
      );
    } else {
      Object value = type.value() == null ? null : list(type.value());
      return a(type, value);
    }
  }

  private static TestedTS a(TestedTS type, Object value) {
    return new TestedArrayTS(
        type,
        TestingTS.a(type.type()),
        "[" + type.literal() + "]",
        value,
        type.typeDeclarations(),
        type.allDeclarations()
    );
  }

  public TestedTS array2(TestedTS type) {
    return array(array(type));
  }

  public TestedTS func(TestedTS resT, ImmutableList<TestedTS> paramTs) {
    return f(resT, paramTs);
  }

  public static TestedTS f(TestedTS resT, TestedTS... paramTestedTs) {
    return f(resT, list(paramTestedTs));
  }

  public static TestedFuncTS f(TestedTS resT, ImmutableList<TestedTS> paramTestedTs) {
    var paramSigs = toSigs(paramTestedTs);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);".formatted(
        resT.name(),
        name,
        toParamDeclarationString(paramTestedTs));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resT.allDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.allDeclarations().stream())
            .collect(toList()))
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resT.typeDeclarations())
        .addAll(paramTestedTs.stream()
            .flatMap(t -> t.typeDeclarations().stream())
            .collect(toList()))
        .build();
    return new TestedFuncTS(
        resT,
        paramTestedTs,
        TestingTS.f(resT.type(), map(paramSigs, ItemSigS::type)),
        name,
        null,
        typeDeclarations,
        declarations
    );
  }

  private static String toParamDeclarationString(ImmutableList<TestedTS> paramTestedTs) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.append(paramTestedTs.get(i).name() + " p" + i);
    }
    return builder.toString();
  }

  private static ImmutableList<ItemSigS> toSigs(List<TestedTS> paramTestedTs) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.add(new ItemSigS(paramTestedTs.get(i).type(), "p" + i, Optional.empty()));
    }
    return builder.build();
  }

  public TestedAssignSpecS testedAssignmentSpec(TestedTS target,
      TestedTS source, boolean allowed) {
    return new TestedAssignSpecS(target, source, allowed);
  }
}
