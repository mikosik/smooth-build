package org.smoothbuild.parse.ast;

import java.util.List;
import java.util.function.BiConsumer;

public class AstVisitor {
  public void visitAst(Ast ast) {
    visitStructs(ast.structs());
    visitRefable(ast.topRefables());
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
    visitType(field.typeP());
  }

  public void visitRefable(List<TopRefableP> refableObjs) {
    refableObjs.forEach(this::visitRefable);
  }

  public void visitRefable(TopRefableP eval) {
    switch (eval) {
      case FuncP func -> visitFunc(func);
      case ValP value -> visitValue(value);
    }
  }

  public void visitValue(ValP valP) {
    valP.ann().ifPresent(this::visitAnn);
    valP.typeP().ifPresent(this::visitType);
    valP.body().ifPresent(this::visitObj);
  }

  public void visitFunc(FuncP funcP) {
    funcP.ann().ifPresent(this::visitAnn);
    funcP.resTP().ifPresent(this::visitType);
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
    visitType(param.typeP());
    param.body().ifPresent(this::visitObj);
  }

  public void visitType(TypeP type) {}

  public void visitObj(ObjP obj) {
    switch (obj) {
      case OrderP orderP -> visitOrder(orderP);
      case BlobP blobP -> visitBlob(blobP);
      case CallP callP -> visitCall(callP);
      case IntP intP -> visitInt(intP);
      case RefP refP -> visitRef(refP);
      case SelectP selectP -> visitSelect(selectP);
      case StringP stringP -> visitString(stringP);
    }
  }

  public void visitOrder(OrderP order) {
    order.elems().forEach(this::visitObj);
  }

  public void visitBlob(BlobP blob) {
  }

  public void visitCall(CallP call) {
    visitObj(call.callee());
    visitArgs(call.args());
  }

  public void visitArgs(List<ArgP> args) {
    args.forEach(this::visitArg);
  }

  public void visitArg(ArgP arg) {
    visitObj(arg.obj());
  }

  public void visitSelect(SelectP select) {
    visitObj(select.selectable());
  }

  public void visitInt(IntP int_) {
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
