package org.smoothbuild.parse;

import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import javax.inject.Inject;

import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.BlobS;
import org.smoothbuild.lang.define.CallS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.IntS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.ObjRefS;
import org.smoothbuild.lang.define.OrderS;
import org.smoothbuild.lang.define.ParamRefS;
import org.smoothbuild.lang.define.PolyFuncS;
import org.smoothbuild.lang.define.RefableObjS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.MonoRefableObj;
import org.smoothbuild.lang.like.Obj;
import org.smoothbuild.lang.like.PolyRefableObj;
import org.smoothbuild.lang.like.Refable;
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
import org.smoothbuild.parse.ast.RefableObjN;
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

  public RefableObjS loadTopObj(ModPath path, RefableObjN refableN) {
    return switch (refableN) {
      case FuncN funcN -> loadFunc(path, funcN);
      case ValN valN -> loadVal(path, valN);
    };
  }

  private ValS loadVal(ModPath path, ValN valN) {
    var type = valN.typeO().get();
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

  private PolyFuncS loadFunc(ModPath path, FuncN funcN) {
    var params = loadParams(funcN);
    var resT = funcN.resTS().get();
    var name = funcN.name();
    var loc = funcN.loc();
    var paramTs = map(params, ItemS::type);
    var funcT = typeSF.func(resT, paramTs);
    if (funcN.ann().isPresent()) {
      var ann = loadAnn(funcN.ann().get());
      return PolyFuncS.polyFuncS(new AnnFuncS(ann, funcT, path, name, params, loc));
    } else {
      var body = createObj(funcN.body().get());
      return PolyFuncS.polyFuncS(new DefFuncS(funcT, path, name, params, body, loc));
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
    var type = param.typeN().typeO().get();
    var name = param.name();
    var body = param.body().map(this::createObj);
    return new ItemS(type, name, body, param.loc());
  }

  private MonoObjS createObj(ObjN obj) {
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

  private MonoObjS createArray(OrderN order) {
    var type = (ArrayTS) order.typeO().get();
    ImmutableList<MonoObjS> elems = map(order.elems(), this::createObj);
    return new OrderS(type, elems, order.loc());
  }

  private MonoObjS createCall(CallN call) {
    var callee = createObj(call.callee());
    var argObjs = map(call.assignedArgs(), a -> createArgObj(a.obj()));
    var resT = call.typeO().get();
    return new CallS(resT, callee, argObjs, call.loc());
  }

  private MonoObjS createArgObj(Obj obj) {
    return switch (obj) {
      case ObjN objN -> createObj(objN);
      case MonoObjS objS -> objS;
      default -> throw unexpectedCaseExc(obj);
    };
  }

  private MonoObjS createSelect(SelectN selectN) {
    var structT = (StructTS) selectN.selectable().typeO().get();
    var index = structT.fields().indexMap().get(selectN.field());
    var fieldT = structT.fields().get(index).type();
    var selectable = createObj(selectN.selectable());
    return new SelectS(fieldT, selectable, selectN.field(), selectN.loc());
  }

  private MonoObjS createRef(RefN ref) {
    Refable referenced = ref.referenced();
    return switch (referenced) {
      case ItemN itemN -> new ParamRefS(itemN.typeO().get(), ref.name(), ref.loc());
      case MonoRefableObj mono -> new ObjRefS(mono.typeO().get(), ref.name(), ref.loc());
      case PolyRefableObj poly -> new ObjRefS(poly.typeO().get().type(), ref.name(), ref.loc());
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
