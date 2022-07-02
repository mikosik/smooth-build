package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.list;
import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.CatDb;
import org.smoothbuild.bytecode.type.cnst.BlobTB;
import org.smoothbuild.bytecode.type.cnst.BoolTB;
import org.smoothbuild.bytecode.type.cnst.IntTB;
import org.smoothbuild.bytecode.type.cnst.NothingTB;
import org.smoothbuild.bytecode.type.cnst.StringTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.testing.TestContext;
import org.smoothbuild.testing.type.TestedTB.TestedArrayTB;
import org.smoothbuild.testing.type.TestedTB.TestedFuncTB;
import org.smoothbuild.testing.type.TestedTB.TestedTupleTB;

import com.google.common.collect.ImmutableList;

public class TestedTBF {
  public static final CatDb CAT_DB = new TestContext().catDb();

  public static final BlobTB BLOB = CAT_DB.blob();
  public static final BoolTB BOOL = CAT_DB.bool();
  public static final IntTB INT = CAT_DB.int_();
  public static final NothingTB NOTHING = CAT_DB.nothing();
  public static final StringTB STRING = CAT_DB.string();
  public static final TupleTB TUPLE = CAT_DB.tuple(list(STRING, INT));

  public TestedTB blob() {
    return new TestedTB(BLOB);
  }

  public TestedTB bool() {
    return new TestedTB(BOOL);
  }

  public TestedTB int_() {
    return new TestedTB(INT);
  }

  public TestedTB nothing() {
    return new TestedTB(NOTHING);
  }

  public TestedTB string() {
    return new TestedTB(STRING);
  }

  public TestedTB struct() {
    throw new UnsupportedOperationException();
  }

  public TestedTB tuple() {
    return new TestedTB(TUPLE);
  }

  public TestedTB tuple(ImmutableList<TestedTB> items) {
    var typeH = CAT_DB.tuple(map(items, TestedTB::type));
    return new TestedTupleTB(typeH, items);
  }

  public TestedTB array(TestedTB elem) {
    return new TestedArrayTB(elem, CAT_DB.array(elem.type()));
  }

  public TestedTB array2(TestedTB type) {
    return array(array(type));
  }

  public TestedTB func(TestedTB resT, ImmutableList<TestedTB> paramTestedTs) {
    TypeB funcTH = CAT_DB.func(resT.type(), map(paramTestedTs, TestedTB::type));
    return new TestedFuncTB(funcTH, resT, paramTestedTs);
  }

  public TestedAssignSpecB testedAssignmentSpec(TestedTB target, TestedTB source, boolean allowed) {
    return new TestedAssignSpecB(target, source, allowed);
  }
}
