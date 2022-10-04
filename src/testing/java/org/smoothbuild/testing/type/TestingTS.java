package org.smoothbuild.testing.type;

import static org.smoothbuild.compile.lang.define.ItemSigS.itemSigS;
import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.NList.nlist;

import org.smoothbuild.compile.lang.define.ItemSigS;
import org.smoothbuild.compile.lang.type.ArrayTS;
import org.smoothbuild.compile.lang.type.BlobTS;
import org.smoothbuild.compile.lang.type.BoolTS;
import org.smoothbuild.compile.lang.type.FuncTS;
import org.smoothbuild.compile.lang.type.IntTS;
import org.smoothbuild.compile.lang.type.StringTS;
import org.smoothbuild.compile.lang.type.StructTS;
import org.smoothbuild.compile.lang.type.TupleTS;
import org.smoothbuild.compile.lang.type.TypeFS;
import org.smoothbuild.compile.lang.type.TypeS;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TestingTS {
  public static final BlobTS BLOB = TypeFS.BLOB;
  public static final BoolTS BOOL = TypeFS.BOOL;
  public static final IntTS INT = TypeFS.INT;
  public static final StringTS STRING = TypeFS.STRING;
  public static final StructTS PERSON = struct("Person",
      nlist(itemSigS(STRING, "firstName"), itemSigS(STRING, "lastName")));

  public static ArrayTS a(TypeS elemT) {
    return new ArrayTS(elemT);
  }

  public static FuncTS f(TypeS resT, TypeS... paramTs) {
    return f(resT, list(paramTs));
  }

  public static FuncTS f(TypeS resT, ImmutableList<TypeS> paramTs) {
    return new FuncTS(resT, paramTs);
  }

  public static TupleTS tuple(TypeS... items) {
    return new TupleTS(ImmutableList.copyOf(items));
  }

  public static StructTS struct(String name, NList<ItemSigS> fields) {
    return new StructTS(name, fields);
  }

  public TypeS array(TypeS elemT) {
    return a(elemT);
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
}
