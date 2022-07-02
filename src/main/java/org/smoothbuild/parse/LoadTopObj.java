package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.map;

import java.util.function.Supplier;
import java.util.stream.IntStream;

import org.smoothbuild.lang.define.AnnFuncS;
import org.smoothbuild.lang.define.AnnS;
import org.smoothbuild.lang.define.AnnValS;
import org.smoothbuild.lang.define.BlobS;
import org.smoothbuild.lang.define.CallS;
import org.smoothbuild.lang.define.DefFuncS;
import org.smoothbuild.lang.define.DefValS;
import org.smoothbuild.lang.define.FuncS;
import org.smoothbuild.lang.define.IntS;
import org.smoothbuild.lang.define.ItemS;
import org.smoothbuild.lang.define.ModPath;
import org.smoothbuild.lang.define.MonoFuncS;
import org.smoothbuild.lang.define.MonoObjS;
import org.smoothbuild.lang.define.MonoRefS;
import org.smoothbuild.lang.define.MonoizeS;
import org.smoothbuild.lang.define.OrderS;
import org.smoothbuild.lang.define.ParamRefS;
import org.smoothbuild.lang.define.PolyRefS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.define.ValS;
import org.smoothbuild.lang.like.common.RefableC;
import org.smoothbuild.lang.like.common.TopRefableC;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.BlobP;
import org.smoothbuild.parse.ast.CallP;
import org.smoothbuild.parse.ast.FuncP;
import org.smoothbuild.parse.ast.IntP;
import org.smoothbuild.parse.ast.ItemP;
import org.smoothbuild.parse.ast.ObjP;
import org.smoothbuild.parse.ast.OrderP;
import org.smoothbuild.parse.ast.RefP;
import org.smoothbuild.parse.ast.SelectP;
import org.smoothbuild.parse.ast.StringP;
import org.smoothbuild.parse.ast.TopRefableP;
import org.smoothbuild.parse.ast.ValP;
import org.smoothbuild.util.bindings.Bindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class LoadTopObj {
  public static TopRefableS loadTopObj(ModPath path, TopRefableP refableP,
      Bindings<TopRefableS> bindings) {
    return new Supplier<>(){
      @Override
      public TopRefableS get() {
        return switch (refableP) {
          case FuncP funcP -> loadFunc(path, funcP);
          case ValP valP -> loadVal(path, valP);
        };
      }

      private ValS loadVal(ModPath path, ValP valP) {
        var type = valP.typeS().get();
        var name = valP.name();
        var loc = valP.loc();
        if (valP.ann().isPresent()) {
          var ann = loadAnn(valP.ann().get());
          return new AnnValS(ann, type, path, name, loc);
        } else {
          var body = loadObj(valP.body().get());
          return new DefValS(type, path, name, body, loc);
        }
      }

      private TopRefableS loadFunc(ModPath path, FuncP funcP) {
        var params = loadParams(funcP);
        var resT = funcP.typeS().get().res();
        var name = funcP.name();
        var loc = funcP.loc();
        var paramTs = map(params, ItemS::type);
        var funcT = TypeFS.func(resT, paramTs);
        if (funcP.ann().isPresent()) {
          var ann = loadAnn(funcP.ann().get());
          return polimorphizeIfNeeded(new AnnFuncS(ann, funcT, path, name, params, loc));
        } else {
          var body = loadObj(funcP.body().get());
          return polimorphizeIfNeeded(new DefFuncS(funcT, path, name, params, body, loc));
        }
      }

      private TopRefableS polimorphizeIfNeeded(MonoFuncS funcS) {
        return funcS.type().vars().isEmpty() ? funcS : polyFuncS(funcS);
      }

      private AnnS loadAnn(AnnP annP) {
        var path = loadString(annP.path());
        return new AnnS(annP.name(), path, annP.loc());
      }

      private NList<ItemS> loadParams(FuncP funcP) {
        return funcP.params().map(this::createParam);
      }

      private ItemS createParam(ItemP param) {
        var type = param.typeP().typeS().get();
        var name = param.name();
        var body = param.body().map(this::loadObj);
        return new ItemS(type, name, body, param.loc());
      }

      private MonoObjS loadObj(ObjP obj) {
        return switch (obj) {
          case OrderP orderP -> loadOrder(orderP);
          case BlobP blobP -> loadBlob(blobP);
          case CallP callP -> loadCall(callP);
          case IntP intP -> loadInt(intP);
          case RefP refP -> loadRef(refP);
          case SelectP selectP -> loadSelect(selectP);
          case StringP stringP -> loadString(stringP);
        };
      }

      private MonoObjS loadOrder(OrderP order) {
        var type = (ArrayTS) order.typeS().get();
        var elems = map(order.elems(), this::loadObj);
        return new OrderS(type, elems, order.loc());
      }

      private MonoObjS loadCall(CallP call) {
        var callee = loadObj(call.callee());
        var argObjs = loadArgs(call, callee);
        var resT = call.typeS().get();
        return new CallS(resT, callee, argObjs, call.loc());
      }

      private ImmutableList<MonoObjS> loadArgs(CallP call, MonoObjS callee) {
        var explicitArgs = call.explicitArgs();
        return IntStream.range(0, explicitArgs.size())
            .mapToObj(i -> explicitArgs.get(i)
                .map(a -> loadObj(a.obj()))
                .orElseGet(() -> defaultArgumentFor(callee.name(), i)))
        .collect(toImmutableList());
      }

      private MonoObjS defaultArgumentFor(String funcName, int parameterIndex) {
        var funcS = (FuncS) bindings.get(funcName);
        return funcS.params().get(parameterIndex).body().get();
      }

      private MonoObjS loadSelect(SelectP selectP) {
        var structT = (StructTS) selectP.selectable().typeS().get();
        var fieldName = selectP.field();
        var fieldT = structT.fields().get(fieldName).type();
        var selectable = loadObj(selectP.selectable());
        return new SelectS(fieldT, selectable, fieldName, selectP.loc());
      }

      private MonoObjS loadRef(RefP ref) {
        RefableC referenced = ref.referenced();
        return switch (referenced) {
          case ItemP itemP -> new ParamRefS(itemP.typeS().get(), ref.name(), ref.loc());
          case TopRefableC topRefableC -> switch (topRefableC.typeS().get()) {
            case MonoTS monoTS -> new MonoRefS(monoTS, ref.name(), ref.loc());
            case PolyTS polyTS -> {
              var funcRefS = new PolyRefS(polyTS, ref.name(), ref.loc());
              yield new MonoizeS(ref.inferredMonoT(), funcRefS, ref.loc());
            }
            default -> throw unexpectedCaseExc(topRefableC.typeS().get());
          };
          default -> throw unexpectedCaseExc(referenced);
        };
      }

      private BlobS loadBlob(BlobP blob) {
        return new BlobS(
            TypeFS.blob(),
            blob.byteString(),
            blob.loc());
      }

      private IntS loadInt(IntP intP) {
        return new IntS(
            TypeFS.int_(),
            intP.bigInteger(),
            intP.loc());
      }

      private StringS loadString(StringP string) {
        return new StringS(
            TypeFS.string(),
            string.unescapedValue(),
            string.loc());
      }
    }.get();
  }
}
