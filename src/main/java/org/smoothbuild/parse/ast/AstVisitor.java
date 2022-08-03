package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.function.BiConsumer;

import org.smoothbuild.parse.ast.expr.BlobP;
import org.smoothbuild.parse.ast.expr.CallP;
import org.smoothbuild.parse.ast.expr.DefaultArgP;
import org.smoothbuild.parse.ast.expr.ExprP;
import org.smoothbuild.parse.ast.expr.IntP;
import org.smoothbuild.parse.ast.expr.NamedArgP;
import org.smoothbuild.parse.ast.expr.OrderP;
import org.smoothbuild.parse.ast.expr.RefP;
import org.smoothbuild.parse.ast.expr.SelectP;
import org.smoothbuild.parse.ast.expr.StringP;
import org.smoothbuild.parse.ast.refable.FuncP;
import org.smoothbuild.parse.ast.refable.ItemP;
import org.smoothbuild.parse.ast.refable.PolyRefableP;
import org.smoothbuild.parse.ast.refable.ValP;
import org.smoothbuild.parse.ast.type.TypeP;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitRefable(ast.refables());
  }

  public void visitStructs(List<StructP> structs) {
    structs.forEach(this::visitStruct);
  }

  public void visitStruct(StructP struct) {
    visitFields(struct.fields());
  }

  public void visitFields(List<ItemP> fields) {
    fields.forEach(this::visitField);
  }

  public void visitField(ItemP field) {
    visitType(field.type());
  }

  public void visitRefable(List<PolyRefableP> refables) {
    refables.forEach(this::visitRefable);
  }

  public void visitRefable(PolyRefableP refable) {
    switch (refable) {
      case FuncP func -> visitFunc(func);
      case ValP value -> visitValue(value);
    }
  }

  public void visitValue(ValP valP) {
    valP.ann().ifPresent(this::visitAnn);
    valP.type().ifPresent(this::visitType);
    valP.body().ifPresent(this::visitObj);
  }

  public void visitFunc(FuncP funcP) {
    funcP.ann().ifPresent(this::visitAnn);
    funcP.resT().ifPresent(this::visitType);
    visitParams(funcP.params());
    funcP.body().ifPresent(this::visitObj);
  }

  public void visitAnn(AnnP annotation) {
    visitString(annotation.path());
  }

  public void visitParams(List<ItemP> params) {
    visitIndexedElements(params, this::visitParam);
  }

  public void visitParam(int index, ItemP param) {
    visitType(param.type());
    param.body().ifPresent(this::visitObj);
  }

  public void visitType(TypeP type) {}

  public void visitObj(ExprP obj) {
    switch (obj) {
      case OrderP orderP -> visitOrder(orderP);
      case BlobP blobP -> visitBlob(blobP);
      case CallP callP -> visitCall(callP);
      case DefaultArgP defaultArgP -> visitDefaultArg(defaultArgP);
      case IntP intP -> visitInt(intP);
      case NamedArgP namedArgP -> visitNamedArg(namedArgP);
      case RefP refP -> visitRef(refP);
      case SelectP selectP -> visitSelect(selectP);
      case StringP stringP -> visitString(stringP);
    }
  }

  public void visitArgs(List<ExprP> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ExprP arg) {
    visitObj(arg);
  }

  public void visitBlob(BlobP blob) {
  }

  public void visitCall(CallP call) {
    visitObj(call.callee());
    visitArgs(call.args());
  }

  public void visitDefaultArg(DefaultArgP defaultArg) {
  }

  public void visitInt(IntP int_) {
  }

  public void visitNamedArg(NamedArgP namedArg) {
    visitObj(namedArg.expr());
  }

  public void visitOrder(OrderP order) {
    order.elems().forEach(this::visitObj);
  }

  public void visitSelect(SelectP select) {
    visitObj(select.selectable());
  }

  public void visitRef(RefP ref) {}

  public void visitString(StringP string) {
  }

  public <E> void visitIndexedElements(List<E> elems, BiConsumer<Integer, ? super E> consumer) {
    for (int i = 0; i < elems.size(); i++) {
      consumer.accept(i, elems.get(i));
    }
  }
}
