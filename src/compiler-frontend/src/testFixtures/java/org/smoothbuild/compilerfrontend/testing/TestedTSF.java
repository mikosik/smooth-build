package org.smoothbuild.compilerfrontend.testing;

import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.compilerfrontend.lang.name.NList.nlist;
import static org.smoothbuild.compilerfrontend.lang.name.Name.referenceableName;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.compilerfrontend.dagger.FrontendCompilerTestContext;
import org.smoothbuild.compilerfrontend.lang.define.SItemSig;
import org.smoothbuild.compilerfrontend.testing.TestedTS.TestedArrayTS;
import org.smoothbuild.compilerfrontend.testing.TestedTS.TestedFuncTS;

public class TestedTSF {
  private static final FrontendCompilerTestContext CONTEXT = new FrontendCompilerTestContext();
  private static final AtomicLong UNIQUE_IDENTIFIER = new AtomicLong();

  public static final TestedTS A = new TestedTS(CONTEXT.sVar("A"));
  public static final TestedTS B = new TestedTS(CONTEXT.sVar("B"));
  public static final TestedTS BLOB = new TestedTS(CONTEXT.sBlobType());
  public static final TestedTS BOOL = new TestedTS(CONTEXT.sBoolType());

  public static final TestedTS INT = new TestedTS(CONTEXT.sIntType());
  public static final TestedTS STRING = new TestedTS(CONTEXT.sStringType());
  public static final TestedTS STRUCT = new TestedTS(
      CONTEXT.sStructType("Person", nlist(CONTEXT.sSig(CONTEXT.sStringType(), "name"))),
      Set.of("Person{String name}"),
      Set.of("Person{String name}"));
  public static final List<TestedTS> TESTED_TYPES = list(
      BLOB,
      BOOL,
      STRING,
      STRUCT,
      a(BLOB),
      a(STRUCT),
      a(a(BLOB)),
      a(a(STRUCT)),
      f(BLOB),
      f(f(BLOB)),
      f(BLOB, BOOL),
      f(BLOB, f(BOOL)));

  public TestedTS blob() {
    return BLOB;
  }

  public TestedTS bool() {
    return BOOL;
  }

  public TestedTS int_() {
    return INT;
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

  public TestedTS tuple(List<TestedTS> items) {
    throw new UnsupportedOperationException();
  }

  public TestedTS array(TestedTS type) {
    return a(type);
  }

  private static TestedTS a(TestedTS type) {
    return new TestedArrayTS(
        type, CONTEXT.sArrayType(type.type()), type.typeDeclarations(), type.allDeclarations());
  }

  public TestedTS func(TestedTS resultT, List<TestedTS> paramTs) {
    return f(resultT, paramTs);
  }

  public static TestedTS f(TestedTS resultT, TestedTS... paramTestedTs) {
    return f(resultT, list(paramTestedTs));
  }

  public static TestedFuncTS f(TestedTS resultT, List<TestedTS> paramTestedTs) {
    var paramSigs = toSigs(paramTestedTs);
    String name = "f" + UNIQUE_IDENTIFIER.getAndIncrement();
    String declaration = "@Native(\"impl\") %s %s(%s);"
        .formatted(resultT.specifier(), name, toParamDeclarationString(paramTestedTs));
    Set<String> declarations = ImmutableSet.<String>builder()
        .add(declaration)
        .addAll(resultT.allDeclarations())
        .addAll(
            paramTestedTs.stream().flatMap(t -> t.allDeclarations().stream()).toList())
        .build();
    Set<String> typeDeclarations = ImmutableSet.<String>builder()
        .addAll(resultT.typeDeclarations())
        .addAll(
            paramTestedTs.stream().flatMap(t -> t.typeDeclarations().stream()).toList())
        .build();
    return new TestedFuncTS(
        resultT,
        paramTestedTs,
        CONTEXT.sFuncType(paramSigs.map(SItemSig::type), resultT.type()),
        typeDeclarations,
        declarations);
  }

  private static String toParamDeclarationString(List<TestedTS> paramTestedTs) {
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.append(paramTestedTs.get(i).specifier() + " p" + i);
    }
    return builder.toString();
  }

  private static List<SItemSig> toSigs(List<TestedTS> paramTestedTs) {
    var builder = new ArrayList<SItemSig>();
    for (int i = 0; i < paramTestedTs.size(); i++) {
      builder.add(new SItemSig(paramTestedTs.get(i).type(), referenceableName("p" + i)));
    }
    return listOfAll(builder);
  }
}
