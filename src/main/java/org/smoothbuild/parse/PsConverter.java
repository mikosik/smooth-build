package org.smoothbuild.parse;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.lang.define.PolyFuncS.polyFuncS;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.bindings.OptionalBindings.newOptionalBindings;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Optionals.pullUp;

import java.util.Optional;
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
import org.smoothbuild.lang.define.RefableS;
import org.smoothbuild.lang.define.SelectS;
import org.smoothbuild.lang.define.StringS;
import org.smoothbuild.lang.define.TopRefableS;
import org.smoothbuild.lang.type.ArrayTS;
import org.smoothbuild.lang.type.MonoTS;
import org.smoothbuild.lang.type.PolyTS;
import org.smoothbuild.lang.type.StructTS;
import org.smoothbuild.lang.type.TypeFS;
import org.smoothbuild.parse.ast.AnnP;
import org.smoothbuild.parse.ast.ArgP;
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
import org.smoothbuild.parse.ast.ValP;
import org.smoothbuild.util.bindings.OptionalBindings;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class PsConverter {
  private final OptionalBindings<? extends RefableS> bindings;

  public PsConverter(OptionalBindings<? extends RefableS> bindings) {
    this.bindings = bindings;
  }

  public Optional<TopRefableS> convertVal(ModPath path, ValP valP) {
    var type = valP.typeS().get();
    var name = valP.name();
    var loc = valP.loc();
    if (valP.ann().isPresent()) {
      var ann = convertAnn(valP.ann().get());
      return Optional.of(new AnnValS(ann, type, path, name, loc));
    } else {
      var body = convertObj(valP.body().get());
      return body.map(b -> new DefValS(type, path, name, b, loc));
    }
  }

  public Optional<TopRefableS> convertFunc(ModPath modPath, FuncP funcP) {
    return convertParams(funcP)
        .flatMap(params -> convertFunc(modPath, funcP, params));
  }

  private Optional<NList<ItemS>> convertParams(FuncP funcP) {
    return pullUp(map(funcP.params().list(), this::convertParam)).map(NList::nlist);
  }

  private Optional<TopRefableS> convertFunc(ModPath modPath, FuncP funcP, NList<ItemS> params) {
    var resT = funcP.typeS().get().res();
    var name = funcP.name();
    var loc = funcP.loc();
    var paramTs = map(params, ItemS::type);
    var funcT = TypeFS.func(resT, paramTs);
    if (funcP.ann().isPresent()) {
      var ann = convertAnn(funcP.ann().get());
      return Optional.of(
          polimorphizeIfNeeded(new AnnFuncS(ann, funcT, modPath, name, params, loc)));
    } else {
      OptionalBindings<RefableS> bindingsInBody = newOptionalBindings(bindings);
      params.forEach(p -> bindingsInBody.add(p.name(), Optional.of(p)));
      var body = new PsConverter(bindingsInBody).convertObj(funcP.body().get());
      return body.map(
          b -> polimorphizeIfNeeded(new DefFuncS(funcT, modPath, name, params, b, loc)));
    }
  }

  private TopRefableS polimorphizeIfNeeded(MonoFuncS funcS) {
    return funcS.type().vars().isEmpty() ? funcS : polyFuncS(funcS);
  }

  private AnnS convertAnn(AnnP annP) {
    var path = convertString(annP.path());
    return new AnnS(annP.name(), path, annP.loc());
  }

  public Optional<ItemS> convertParam(ItemP param) {
    var type = param.typeP().typeS();
    var name = param.name();
    var body = param.body().flatMap(this::convertObj);
    return type.map(t -> new ItemS(t, name, body, param.loc()));
  }

  private Optional<MonoObjS> convertObj(ObjP obj) {
    return switch (obj) {
      case OrderP orderP -> convertOrder(orderP);
      case BlobP blobP -> Optional.of(convertBlob(blobP));
      case CallP callP -> convertCall(callP);
      case IntP intP -> Optional.of(convertInt(intP));
      case RefP refP -> convertRef(refP);
      case SelectP selectP -> convertSelect(selectP);
      case StringP stringP -> Optional.of(convertString(stringP));
    };
  }

  private Optional<MonoObjS> convertOrder(OrderP order) {
    var type = (ArrayTS) order.typeS().get();
    var elems = pullUp(map(order.elems(), this::convertObj));
    return elems.map(es -> new OrderS(type, es, order.loc()));
  }

  private Optional<MonoObjS> convertCall(CallP call) {
    var callee = convertObj(call.callee());
    var argObjs = callee.flatMap(c -> convertArgs(call, c));
    var resT = call.typeS().get();
    if (callee.isPresent() && argObjs.isPresent()) {
      return Optional.of(new CallS(resT, callee.get(), argObjs.get(), call.loc()));
    } else {
      return Optional.empty();
    }
  }

  private Optional<ImmutableList<MonoObjS>> convertArgs(CallP call, MonoObjS callee) {
    var explicitArgs = call.explicitArgs();
    var args = IntStream.range(0, explicitArgs.size())
        .mapToObj(i -> convertArg(callee, explicitArgs, i))
        .collect(toImmutableList());
    return pullUp(args);
  }

  private Optional<MonoObjS> convertArg(MonoObjS callee,
      ImmutableList<Optional<ArgP>> explicitArgs, int i) {
    return explicitArgs.get(i)
        .flatMap(a -> convertObj(a.obj()))
        .or(() -> defaultArgumentFor(callee.name(), i));
  }

  private Optional<MonoObjS> defaultArgumentFor(String funcName, int parameterIndex) {
    return bindings.get(funcName).value()
        .map(f -> ((FuncS) f).params().get(parameterIndex).body().get());
  }

  private Optional<MonoObjS> convertSelect(SelectP selectP) {
    if (selectP.selectable().typeS().get() instanceof StructTS structT) {
      var fieldName = selectP.field();
      var fieldT = structT.fields().get(fieldName).type();
      var selectable = convertObj(selectP.selectable());
      return selectable.map(s -> new SelectS(fieldT, s, fieldName, selectP.loc()));
    } else {
      return Optional.empty();
    }
  }

  private Optional<MonoObjS> convertRef(RefP ref) {
    return bindings.get(ref.name())
        .toOptional()
        .map(r -> handleRef(ref, r));
  }

  private MonoObjS handleRef(RefP ref, RefableS refable) {
    return switch (refable) {
      case ItemS itemP -> new ParamRefS(itemP.type(), ref.name(), ref.loc());
      case TopRefableS topRefableS -> switch (topRefableS.type()) {
        case MonoTS monoTS -> new MonoRefS(monoTS, ref.name(), ref.loc());
        case PolyTS polyTS -> {
          var funcRefS = new PolyRefS(polyTS, ref.name(), ref.loc());
          yield new MonoizeS(ref.inferredMonoT(), funcRefS, ref.loc());
        }
        default -> throw unexpectedCaseExc(topRefableS.type());
      };
    };
  }

  private BlobS convertBlob(BlobP blob) {
    return new BlobS(TypeFS.blob(), blob.byteString(), blob.loc());
  }

  private IntS convertInt(IntP intP) {
    return new IntS(TypeFS.int_(), intP.bigInteger(), intP.loc());
  }

  private StringS convertString(StringP string) {
    return new StringS(TypeFS.string(), string.unescapedValue(), string.loc());
  }
}
