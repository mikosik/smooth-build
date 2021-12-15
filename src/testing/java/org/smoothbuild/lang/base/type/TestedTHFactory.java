package org.smoothbuild.lang.base.type;

import static org.smoothbuild.util.collect.Lists.map;

import org.smoothbuild.db.object.type.base.TypeH;
import org.smoothbuild.lang.base.type.TestedTH.TestedArrayTH;
import org.smoothbuild.lang.base.type.TestedTH.TestedFuncTH;

import com.google.common.collect.ImmutableList;

public class TestedTHFactory implements TestedTFactory<TypeH, TestedTH, TestedAssignSpecH> {
  @Override
  public TestingT<TypeH> testingT() {
    return TestingTH.INSTANCE;
  }

  @Override
  public TestedTH any() {
    return new TestedTH(TestingTH.ANY);
  }

  @Override
  public TestedTH blob() {
    return new TestedTH(TestingTH.BLOB);
  }

  @Override
  public TestedTH bool() {
    return new TestedTH(TestingTH.BOOL);
  }

  @Override
  public TestedTH int_() {
    return new TestedTH(TestingTH.INT);
  }

  @Override
  public TestedTH nothing() {
    return new TestedTH(TestingTH.NOTHING);
  }

  @Override
  public TestedTH string() {
    return new TestedTH(TestingTH.STRING);
  }

  @Override
  public TestedTH struct() {
    return new TestedTH(TestingTH.TUPLE);
  }

  @Override
  public TestedTH varA() {
    return new TestedTH(TestingTH.A);
  }

  @Override
  public TestedTH varB() {
    return new TestedTH(TestingTH.B);
  }

  @Override
  public TestedTH array(TestedTH elem) {
    return new TestedArrayTH(elem, TestingTH.INSTANCE.array(elem.type()));
  }

  @Override
  public TestedTH array2(TestedTH type) {
    return array(array(type));
  }

  @Override
  public TestedTH func(TestedTH resT, ImmutableList<TestedTH> paramTestedTs) {
    TypeH funcTH = TestingTH.INSTANCE.func(resT.type(), map(paramTestedTs, TestedTH::type));
    return new TestedFuncTH(funcTH, resT, paramTestedTs);
  }

  @Override
  public TestedAssignSpecH testedAssignmentSpec(TestedTH target, TestedTH source, boolean allowed) {
    return new TestedAssignSpecH(target, source, allowed);
  }
}
