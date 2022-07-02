package org.smoothbuild.testing.type;

import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;
import static org.smoothbuild.util.collect.Sets.set;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.Bounds;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

public class TestingTS {
  public static final TestingTS INSTANCE = new TestingTS();

  private static final TestContext CONTEXT = new TestContext();

  public static final ImmutableList<MonoTS> BASE_TYPES = ImmutableList.copyOf(TypeFS.baseTs());
  public static final ImmutableList<MonoTS> INFERABLE_BASE_TYPES = TypeFS.inferableBaseTs();

  public static final AnyTS ANY = TypeFS.any();
  public static final BlobTS BLOB = TypeFS.blob();
  public static final BoolTS BOOL = TypeFS.bool();
  public static final IntTS INT = TypeFS.int_();
  public static final NothingTS NOTHING = TypeFS.nothing();
  public static final StringTS STRING = TypeFS.string();
  public static final StructTS PERSON = struct("Person",
      nlist(itemSigS(STRING, "firstName"), itemSigS(STRING, "lastName")));
  public static final StructTS FLAG = struct("Flag", nlist(itemSigS(BOOL, "flab")));
  public static final StructTS DATA = struct("Data", nlist(itemSigS(BLOB, "data")));
  public static final VarS A = var("A");
  public static final VarS B = var("B");
  public static final VarS C = var("C");
  public static final VarS X = var("X");
  public static final VarS Y = var("Y");

  public static final ImmutableList<MonoTS> ELEMENTARY_TYPES = ImmutableList.<MonoTS>builder()
      .addAll(BASE_TYPES)
      .add(PERSON)
      .build();

  public static final MonoFuncTS STRING_GETTER_FUNCTION = f(STRING);
  public static final MonoFuncTS PERSON_GETTER_FUNCTION = f(PERSON);
  public static final MonoFuncTS STRING_MAP_FUNCTION = f(STRING, STRING);
  public static final MonoFuncTS PERSON_MAP_FUNCTION = f(PERSON, PERSON);
  public static final MonoFuncTS IDENTITY_FUNCTION = f(A, A);
  public static final MonoFuncTS ARRAY_HEAD_FUNCTION = f(A, a(A));
  public static final MonoFuncTS ARRAY_LENGTH_FUNCTION = f(STRING, a(A));

  public static final ImmutableList<MonoTS> FUNCTION_TYPES =
      list(
          STRING_GETTER_FUNCTION,
          PERSON_GETTER_FUNCTION,
          STRING_MAP_FUNCTION,
          PERSON_MAP_FUNCTION,
          IDENTITY_FUNCTION,
          ARRAY_HEAD_FUNCTION,
          ARRAY_LENGTH_FUNCTION);

  public static final ImmutableList<MonoTS> ALL_TESTED_TYPES =
      ImmutableList.<MonoTS>builder()
          .addAll(ELEMENTARY_TYPES)
          .addAll(FUNCTION_TYPES)
          .add(X)
          .build();

  public static ArrayTS a(MonoTS elemT) {
    return TypeFS.array(elemT);
  }

  public static MonoFuncTS f(MonoTS resT) {
    return f(resT, list());
  }

  public static MonoFuncTS f(MonoTS resT, MonoTS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static MonoFuncTS f(MonoTS resT, ImmutableList<MonoTS> paramTs) {
    return TypeFS.func(resT, paramTs);
  }

  public static VarS var(String a) {
    return TypeFS.var(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return TypeFS.struct(name, fields);
  }

  public Bounds<MonoTS> bounds() {
    return bounds(nothing(), any());
  }

  public Bounds<MonoTS> bounds(MonoTS lower, MonoTS upper) {
    return new Bounds<>(lower, upper);
  }

  public ImmutableList<MonoTS> typesForBuildWideGraph() {
    return list(varA(), varB(), blob(), bool(), int_(), struct(), string());
  }

  public ImmutableList<MonoTS> baseTypes() {
    return BASE_TYPES;
  }

  public ImmutableList<MonoTS> elementaryTypes() {
    return ELEMENTARY_TYPES;
  }

  public ImmutableList<MonoTS> allTestedTypes() {
    return ALL_TESTED_TYPES;
  }

  public MonoTS array(MonoTS elemT) {
    return a(elemT);
  }

  public static MonoTS join(ImmutableSet<MonoTS> elems) {
    return JoinTS.join(elems);
  }

  public static MonoTS join(MonoTS... types) {
    return JoinTS.join(set(types));
  }

  public static MonoTS meet(ImmutableSet<MonoTS> elems) {
    return MeetTS.meet(elems);
  }

  public static MonoTS meet(MonoTS... types) {
    return MeetTS.meet(set(types));
  }

  public MonoTS func(MonoTS resT, ImmutableList<MonoTS> params) {
    return TestingTS.f(resT, params);
  }

  public MonoTS any() {
    return ANY;
  }

  public MonoTS blob() {
    return BLOB;
  }

  public MonoTS bool() {
    return BOOL;
  }

  public MonoTS int_() {
    return INT;
  }

  public MonoTS nothing() {
    return NOTHING;
  }

  public MonoTS string() {
    return STRING;
  }

  public boolean isStructSupported() {
    return true;
  }

  public MonoTS struct() {
    return PERSON;
  }

  public boolean isTupleSupported() {
    return false;
  }

  public MonoTS tuple() {
    throw new UnsupportedOperationException();
  }

  public MonoTS tuple(ImmutableList<MonoTS> items) {
    throw new UnsupportedOperationException();
  }

  public static VarS v0() {
    return var("0").prefixed("_");
  }

  public static VarS v1() {
    return var("1").prefixed("_");
  }

  public static VarS v2() {
    return var("2").prefixed("_");
  }

  public static VarS v3() {
    return var("3").prefixed("_");
  }

  public VarS varA() {
    return A;
  }

  public VarS varB() {
    return B;
  }

  public VarS varC() {
    return C;
  }
}
