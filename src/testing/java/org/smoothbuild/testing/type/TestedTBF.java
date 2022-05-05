package org.smoothbuild.testing.type;

import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.testing.type.TestedTB.TestedArrayTB;
import org.smoothbuild.testing.type.TestedTB.TestedFuncTB;
import org.smoothbuild.testing.type.TestedTB.TestedTupleTB;

import com.google.common.collect.ImmutableList;

public class TestedTBF {
  public TestingTB testingT() {
    return TestingTB.INSTANCE;
  }

  public TestedTB any() {
    return new TestedTB(TestingTB.ANY);
  }

  public TestedTB blob() {
    return new TestedTB(TestingTB.BLOB);
  }

  public TestedTB bool() {
    return new TestedTB(TestingTB.BOOL);
  }

  public TestedTB int_() {
    return new TestedTB(TestingTB.INT);
  }

  public TestedTB nothing() {
    return new TestedTB(TestingTB.NOTHING);
  }

  public TestedTB string() {
    return new TestedTB(TestingTB.STRING);
  }

  public TestedTB struct() {
    throw new UnsupportedOperationException();
  }

  public TestedTB tuple() {
    return new TestedTB(TestingTB.TUPLE);
  }

  public TestedTB tuple(ImmutableList<TestedTB> items) {
    var typeH = TestingTB.INSTANCE.tuple(map(items, TestedTB::type));
    return new TestedTupleTB(typeH, items);
  }

  public TestedTB varA() {
    return new TestedTB(TestingTB.VAR_A);
  }

  public TestedTB varB() {
    return new TestedTB(TestingTB.VAR_B);
  }

  public TestedTB array(TestedTB elem) {
    return new TestedArrayTB(elem, TestingTB.INSTANCE.array(elem.type()));
  }

  public TestedTB array2(TestedTB type) {
    return array(array(type));
  }

  public TestedTB func(TestedTB resT, ImmutableList<TestedTB> paramTestedTs) {
    TypeB funcTH = TestingTB.INSTANCE.func(resT.type(), map(paramTestedTs, TestedTB::type));
    return new TestedFuncTB(funcTH, resT, paramTestedTs);
  }

  public TestedAssignSpecB testedAssignmentSpec(TestedTB target, TestedTB source, boolean allowed) {
    return new TestedAssignSpecB(target, source, allowed);
  }
}
