package org.smoothbuild.parse;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.RefableObjS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.like.Refable;
import org.smoothbuild.lang.like.RefableObj;
import org.smoothbuild.lang.obj.BlobS;
import org.smoothbuild.lang.obj.CallS;
import org.smoothbuild.lang.obj.IntS;
import org.smoothbuild.lang.obj.ObjRefS;
import org.smoothbuild.lang.obj.ObjS;
import org.smoothbuild.lang.obj.OrderS;
import org.smoothbuild.lang.obj.ParamRefS;
import org.smoothbuild.lang.obj.SelectS;
import org.smoothbuild.lang.obj.StringS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeSF;
import org.smoothbuild.parse.ast.AnnN;
import org.smoothbuild.parse.ast.BlobN;
import org.smoothbuild.parse.ast.CallN;
import org.smoothbuild.parse.ast.FuncN;
import org.smoothbuild.parse.ast.IntN;
import org.smoothbuild.parse.ast.ItemN;
import org.smoothbuild.parse.ast.ObjN;
import org.smoothbuild.parse.ast.OrderN;
import org.smoothbuild.parse.ast.RefN;
import org.smoothbuild.parse.ast.RefableN;
import org.smoothbuild.parse.ast.SelectN;
import org.smoothbuild.parse.ast.StringN;
import org.smoothbuild.parse.ast.ValN;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class TopObjLoader {
  private final TypeSF typeSF;

  @Inject
  public TopObjLoader(TypeSF typeSF) {
    this.typeSF = typeSF;
  }

  public RefableObjS loadTopObj(ModPath path, RefableN refableN) {
    return switch (refableN) {
      case FuncN funcN -> loadFunc(path, funcN);
      case ValN valN -> loadVal(path, valN);
      default -> throw unexpectedCaseExc(refableN);
    };
  }

  private ValS loadVal(ModPath path, ValN valN) {
    var type = valN.type().get();
    var name = valN.name();
    var loc = valN.loc();
    if (valN.ann().isPresent()) {
      var ann = loadAnn(valN.ann().get());
      return new AnnValS(ann, type, path, name, loc);
    } else {
      var body = createObj(valN.body().get());
      return new DefValS(type, path, name, body, loc);
    }
  }

  private FuncS loadFunc(ModPath path, FuncN funcN) {
    var params = loadParams(funcN);
    var resT = funcN.resT().get();
    var name = funcN.name();
    var loc = funcN.loc();
    var paramTs = map(params, ItemS::type);
    var funcT = typeSF.func(resT, paramTs);
    if (funcN.ann().isPresent()) {
      var ann = loadAnn(funcN.ann().get());
      return new AnnFuncS(ann, funcT, path, name, params, loc);
    } else {
      var body = createObj(funcN.body().get());
      return new DefFuncS(funcT, path, name, params, body, loc);
    }
  }

  private AnnS loadAnn(AnnN annN) {
    var path = createString(annN.path());
    return new AnnS(annN.name(), path, annN.loc());
  }

  private NList<ItemS> loadParams(FuncN funcN) {
    return funcN.params().map(this::createParam);
  }

  private ItemS createParam(ItemN param) {
    var type = param.evalT().get().type().get();
    var name = param.name();
    var body = param.body().map(this::createObj);
    return new ItemS(type, name, body, param.loc());
  }

  private ObjS createObj(ObjN obj) {
    return switch (obj) {
      case OrderN orderN -> createArray(orderN);
      case BlobN blobN -> createBlob(blobN);
      case CallN callN -> createCall(callN);
      case IntN intN -> createInt(intN);
      case RefN refN -> createRef(refN);
      case SelectN selectN -> createSelect(selectN);
      case StringN stringN -> createString(stringN);
    };
  }

  private ObjS createArray(OrderN order) {
    var type = (ArrayTS) order.type().get();
    ImmutableList<ObjS> elems = map(order.elems(), this::createObj);
    return new OrderS(type, elems, order.loc());
  }

  private ObjS createCall(CallN call) {
    var callable = createObj(call.callable());
    var argObjs = map(call.assignedArgs(), a -> createArgObj(a.obj()));
    var resT = call.type().get();
    return new CallS(resT, callable, argObjs, call.loc());
  }

  private ObjS createArgObj(Obj obj) {
    return switch (obj) {
      case ObjN objN -> createObj(objN);
      case ObjS objS -> objS;
      default -> throw unexpectedCaseExc(obj);
    };
  }

  private ObjS createSelect(SelectN selectN) {
    var structT = (StructTS) selectN.selectable().type().get();
    var index = structT.fields().indexMap().get(selectN.field());
    var fieldT = structT.fields().get(index).type();
    var selectable = createObj(selectN.selectable());
    return new SelectS(fieldT, selectable, selectN.field(), selectN.loc());
  }

  private ObjS createRef(RefN ref) {
    Refable referenced = ref.referenced();
    return switch (referenced) {
      case ItemN itemN -> new ParamRefS(ref.type().get(), ref.name(), ref.loc());
      case RefableObj refableObj -> new ObjRefS(ref.type().get(), ref.name(), ref.loc());
      default -> throw unexpectedCaseExc(referenced);
    };
  }

  public BlobS createBlob(BlobN blob) {
    return new BlobS(
        typeSF.blob(),
        blob.byteString(),
        blob.loc());
  }

  public IntS createInt(IntN intN) {
    return new IntS(
        typeSF.int_(),
        intN.bigInteger(),
        intN.loc());
  }

  public StringS createString(StringN string) {
    return new StringS(
        typeSF.string(),
        string.unescapedValue(),
        string.loc());
  }
}
