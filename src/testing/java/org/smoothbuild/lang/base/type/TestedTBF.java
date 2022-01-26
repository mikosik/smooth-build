package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.bytecode.type.base.TypeB;
import org.smoothbuild.lang.base.type.TestedTB.TestedArrayTB;
import org.smoothbuild.lang.base.type.TestedTB.TestedFuncTB;
import org.smoothbuild.lang.base.type.TestedTB.TestedTupleTB;

import com.google.common.collect.ImmutableList;

public class TestedTBF implements TestedTF<TypeB, TestedTB, TestedAssignSpecB> {
  @Override
  public TestingT<TypeB> testingT() {
    return TestingTB.INSTANCE;
  }

  @Override
  public TestedTB any() {
    return new TestedTB(TestingTB.ANY);
  }

  @Override
  public TestedTB blob() {
    return new TestedTB(TestingTB.BLOB);
  }

  @Override
  public TestedTB bool() {
    return new TestedTB(TestingTB.BOOL);
  }

  @Override
  public TestedTB int_() {
    return new TestedTB(TestingTB.INT);
  }

  @Override
  public TestedTB nothing() {
    return new TestedTB(TestingTB.NOTHING);
  }

  @Override
  public TestedTB string() {
    return new TestedTB(TestingTB.STRING);
  }

  @Override
  public TestedTB struct() {
    throw new UnsupportedOperationException();
  }

  @Override
  public TestedTB tuple() {
    return new TestedTB(TestingTB.TUPLE);
  }

  @Override
  public TestedTB tuple(ImmutableList<TestedTB> items) {
    var typeH = TestingTB.INSTANCE.tuple(map(items, TestedTB::type));
    return new TestedTupleTB(typeH, items);
  }

  @Override
  public TestedTB varA() {
    return new TestedTB(TestingTB.OPEN_A);
  }

  @Override
  public TestedTB varB() {
    return new TestedTB(TestingTB.OPEN_B);
  }

  @Override
  public TestedTB array(TestedTB elem) {
    return new TestedArrayTB(elem, TestingTB.INSTANCE.array(elem.type()));
  }

  @Override
  public TestedTB array2(TestedTB type) {
    return array(array(type));
  }

  @Override
  public TestedTB func(TestedTB resT, ImmutableList<TestedTB> paramTestedTs) {
    TypeB funcTH = TestingTB.INSTANCE.func(resT.type(), map(paramTestedTs, TestedTB::type));
    return new TestedFuncTB(funcTH, resT, paramTestedTs);
  }

  @Override
  public TestedAssignSpecB testedAssignmentSpec(TestedTB target, TestedTB source, boolean allowed) {
    return new TestedAssignSpecB(target, source, allowed);
  }
}
