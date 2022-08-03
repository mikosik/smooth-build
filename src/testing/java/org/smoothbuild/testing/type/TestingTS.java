package org.smoothbuild.testing.type;

import static org.smoothbuild.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import org.smoothbuild.lang.define.ItemSigS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.FuncTS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.lang.type.TypeS;
import org.smoothbuild.lang.type.VarS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTS {
  public static final BlobTS BLOB = TypeFS.BLOB;
  public static final BoolTS BOOL = TypeFS.BOOL;
  public static final IntTS INT = TypeFS.INT;
  public static final StringTS STRING = TypeFS.STRING;
  public static final StructTS PERSON = struct("Person",
      nlist(itemSigS(STRING, "firstName"), itemSigS(STRING, "lastName")));
  public static final VarS A = var("A");
  public static final VarS B = var("B");
  public static final VarS C = var("C");

  public static ArrayTS a(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public static FuncTS f(TypeS resT) {
    return f(resT, list());
  }

  public static FuncTS f(TypeS resT, TypeS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static FuncTS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    return new FuncTS(resT, paramTs);
  }

  public static VarS var(String a) {
    return new VarS(a);
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public TypeS array(TypeS elemT) {
    return a(elemT);
  }

  public TypeS func(TypeS resT, ImmutableList<TypeS> params) {
    return TestingTS.f(resT, params);
  }

  public TypeS blob() {
    return BLOB;
  }

  public TypeS bool() {
    return BOOL;
  }

  public TypeS int_() {
    return INT;
  }

  public TypeS string() {
    return STRING;
  }

  public TypeS struct() {
    return PERSON;
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
}
