package org.smoothbuild.compile;

import static java.util.Objects.requireNonNull;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;

import javax.inject.Inject;

import org.smoothbuild.bytecode.BytecodeF;
import org.smoothbuild.bytecode.type.cnst.ArrayTB;
import org.smoothbuild.bytecode.type.cnst.FuncTB;
import org.smoothbuild.bytecode.type.cnst.TupleTB;
import org.smoothbuild.bytecode.type.cnst.TypeB;
import org.smoothbuild.lang.type.AnyTS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.BlobTS;
import org.smoothbuild.lang.type.BoolTS;
import org.smoothbuild.lang.type.IntTS;
import org.smoothbuild.lang.type.JoinTS;
import org.smoothbuild.lang.type.MeetTS;
import org.smoothbuild.lang.type.MonoFuncTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.NothingTS;
import org.smoothbuild.lang.type.StringTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.VarS;

public class TypeSbConverter {
  private final BytecodeF bytecodeF;
  private final Deque<Map<String, TypeB>> varMaps;

  @Inject
  public TypeSbConverter(BytecodeF bytecodeF) {
    this.bytecodeF = bytecodeF;
    this.varMaps = new ArrayDeque<>();
  }

  public void addLastVarMap(Map<String, TypeB> varMap) {
    varMaps.addLast(varMap);
  }

  public void removeLastVarMap() {
    varMaps.removeLast();
  }

  public Map<String, TypeB> getLastVarMap() {
    return varMaps.getLast();
  }

  public TypeB convert(MonoTS type) {
    return switch (type) {
      case AnyTS any -> throw new RuntimeException(
          AnyTS.class.getName() + " cannot be converted to " + TypeB.class.getName() + ".");
      case ArrayTS a -> convert(a);
      case BlobTS blob -> bytecodeF.blobT();
      case BoolTS bool -> bytecodeF.boolT();
      case IntTS i -> bytecodeF.intT();
      case NothingTS n -> bytecodeF.nothingT();
      case VarS v ->  convert(v);
      case StringTS s -> bytecodeF.stringT();
      case StructTS st -> convert(st);
      case MonoFuncTS f -> convert(f);
      case MeetTS meet -> throw unexpectedCaseExc(meet);
      case JoinTS join -> throw unexpectedCaseExc(join);
    };
  }

  public TypeB convert(VarS var) {
    var result = varMaps.getLast().get(var.name());
    return requireNonNull(result);
  }

  public TupleTB convert(StructTS struct) {
    return bytecodeF.tupleT(map(struct.fields(), isig -> convert(isig.type())));
  }

  public ArrayTB convert(ArrayTS array) {
    return bytecodeF.arrayT(convert(array.elem()));
  }

  public FuncTB convert(MonoFuncTS func) {
    var params = map(func.params(), this::convert);
    var res = convert(func.res());
    return bytecodeF.funcT(res, params);
  }
}