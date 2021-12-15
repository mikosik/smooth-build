package org.smoothbuild.lang.base.type;

import static java.lang.String.join;
import static java.nio.charset.StandardCharsets.US_ASCII;
import static java.util.stream.Collectors.toList;
import static okio.ByteString.encodeString;
import static org.smoothbuild.lang.base.define.ItemSigS.itemSigS;
import static org.smoothbuild.lang.base.type.TestingTS.var;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.NList.nList;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import org.smoothbuild.lang.base.define.ItemSigS;
import org.smoothbuild.lang.base.type.TestedTS.TestedArrayTS;
import org.smoothbuild.lang.base.type.TestedTS.TestedFuncTS;
import org.smoothbuild.lang.base.type.impl.TypeS;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;
import com.google.common.collect.ImmutableSet;

public class TestedTSFactory implements TestedTFactory<TypeS, TestedTS, TestedAssignSpecS> {
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
      TestingTS.struct("Person", nList(itemSigS("name", TestingTS.STRING))),
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
      TestingTS.struct("Data", nList(itemSigS("value", TestingTS.BLOB))),
      "data(0xAB)",
      null,
      Set.of("Data{ Blob value }"),
      Set.of("Data{ Blob value }")
  );
  public static final TestedTS STRUCT_WITH_BOOL = new TestedTS(
      TestingTS.struct("Flag", nList(itemSigS("value", TestingTS.BOOL))),
      "flag(true)",
      null,
      Set.of("Flag{ Bool value }"),
      Set.of("Flag{ Bool value }")
  );

  @Override
  public TestingTS testingT() {
    return TestingTS.INSTANCE;
  }

  @Override
  public TestedTS any() {
    return ANY;
  }

  @Override
  public TestedTS blob() {
    return BLOB;
  }

  @Override
  public TestedTS bool() {
    return BOOL;
  }

  @Override
  public TestedTS int_() {
    return INT;
  }

  @Override
  public TestedTS nothing() {
    return NOTHING;
  }

  @Override
  public TestedTS string() {
    return STRING;
  }

  @Override
  public TestedTS struct() {
    return STRUCT;
  }

  @Override
  public TestedTS varA() {
    return A;
  }

  @Override
  public TestedTS varB() {
    return B;
  }

  @Override
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

  @Override
  public TestedTS array2(TestedTS type) {
    return array(array(type));
  }

  @Override
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
        join(",", map(paramSigs, ItemSigS::toString)));
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

  private static ImmutableList<ItemSigS> toSigs(List<TestedTS> paramTestedTs) {
    Builder<ItemSigS> builder = ImmutableList.builder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.add(new ItemSigS(paramTestedTs.get(i).type(), "p" + i, Optional.empty()));
    }
    return builder.build();
  }

  @Override
  public TestedAssignSpecS testedAssignmentSpec(TestedTS target,
      TestedTS source, boolean allowed) {
    return new TestedAssignSpecS(target, source, allowed);
  }
}
