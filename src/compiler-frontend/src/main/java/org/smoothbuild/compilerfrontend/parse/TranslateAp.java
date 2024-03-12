package org.smoothbuild.compilerfrontend.parse;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;
import static org.smoothbuild.compilerfrontend.compile.CompileError.compileError;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicReference;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.smoothbuild.antlr.lang.SmoothAntlrBaseVisitor;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.AnnotationContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ArgContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ArgListContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ArrayTContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ChainContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ChainHeadContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ChainPartContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ExprContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.FuncTContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ItemContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ItemListContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.LambdaContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.ModuleContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.NamedFuncContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.NamedValueContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.PipeContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.SelectContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.StructContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.TypeContext;
import org.smoothbuild.antlr.lang.SmoothAntlrParser.TypeNameContext;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.filesystem.base.FullPath;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.step.TryFunction;
import org.smoothbuild.common.tuple.Tuple2;
import org.smoothbuild.compilerfrontend.compile.ast.define.AnnotationP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ArrayTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.BlobP;
import org.smoothbuild.compilerfrontend.compile.ast.define.CallP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ExprP;
import org.smoothbuild.compilerfrontend.compile.ast.define.FuncTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ImplicitTP;
import org.smoothbuild.compilerfrontend.compile.ast.define.InstantiateP;
import org.smoothbuild.compilerfrontend.compile.ast.define.IntP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ItemP;
import org.smoothbuild.compilerfrontend.compile.ast.define.LambdaP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ModuleP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedArgP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedEvaluableP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedFuncP;
import org.smoothbuild.compilerfrontend.compile.ast.define.NamedValueP;
import org.smoothbuild.compilerfrontend.compile.ast.define.OrderP;
import org.smoothbuild.compilerfrontend.compile.ast.define.ReferenceP;
import org.smoothbuild.compilerfrontend.compile.ast.define.SelectP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StringP;
import org.smoothbuild.compilerfrontend.compile.ast.define.StructP;
import org.smoothbuild.compilerfrontend.compile.ast.define.TypeP;
import org.smoothbuild.compilerfrontend.lang.base.TypeNamesS;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;

public class TranslateAp implements TryFunction<Tuple2<ModuleContext, FullPath>, ModuleP> {
  @Override
  public Try<ModuleP> apply(Tuple2<ModuleContext, FullPath> context) {
    var logger = new Logger();
    var module = context.element1();
    var fullPath = context.element2();
    var structs = new ArrayList<StructP>();
    var evaluables = new ArrayList<NamedEvaluableP>();
    var apTranslatingVisitor = new ApTranslatingVisitor(fullPath, structs, evaluables, logger);
    apTranslatingVisitor.visit(module);
    var name = fullPath.withExtension("").path().lastPart().toString();
    var moduleP = new ModuleP(name, listOfAll(structs), listOfAll(evaluables));
    return Try.of(moduleP, logger);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }

  private static Location fileLocation(FullPath fullPath, ParserRuleContext parserRuleContext) {
    return fileLocation(fullPath, parserRuleContext.getStart());
  }

  private static Location fileLocation(FullPath fullPath, TerminalNode node) {
    return fileLocation(fullPath, node.getSymbol());
  }

  private static Location fileLocation(FullPath fullPath, Token token) {
    return Locations.fileLocation(fullPath, token.getLine());
  }

  private static class ApTranslatingVisitor extends SmoothAntlrBaseVisitor<Void> {
    private final FullPath fullPath;
    private final ArrayList<StructP> structs;
    private final ArrayList<NamedEvaluableP> evaluables;
    private final Logger logger;
    private final String scopeName;
    private int lambdaCount;

    public ApTranslatingVisitor(
        FullPath fullPath,
        ArrayList<StructP> structs,
        ArrayList<NamedEvaluableP> evaluables,
        Logger logger) {
      this(fullPath, structs, evaluables, logger, null);
    }

    public ApTranslatingVisitor(
        FullPath fullPath,
        ArrayList<StructP> structs,
        ArrayList<NamedEvaluableP> evaluables,
        Logger logger,
        String scopeName) {
      this.fullPath = fullPath;
      this.structs = structs;
      this.evaluables = evaluables;
      this.logger = logger;
      this.scopeName = scopeName;
      this.lambdaCount = 0;
    }

    @Override
    public Void visitStruct(StructContext struct) {
      var name = struct.NAME().getText();
      var location = fileLocation(fullPath, struct.NAME().getSymbol());
      var fields = createItems(name, struct.itemList());
      structs.add(new StructP(name, fields, location));
      return null;
    }

    @Override
    public Void visitNamedFunc(NamedFuncContext namedFunc) {
      var nameNode = namedFunc.NAME();
      var location = fileLocation(fullPath, nameNode);
      visitChildren(namedFunc);
      var type = createTypeSane(namedFunc.type(), location);
      var name = nameNode.getText();
      var fullName = createFullName(name);
      var body = createPipeSane(namedFunc.pipe());
      var annotation = createNativeSane(namedFunc.annotation());
      var params = createItems(name, namedFunc.itemList());
      evaluables.add(new NamedFuncP(type, fullName, name, params, body, annotation, location));
      return null;
    }

    @Override
    public Void visitNamedValue(NamedValueContext namedValue) {
      var nameNode = namedValue.NAME();
      var location = fileLocation(fullPath, nameNode);
      visitChildren(namedValue);
      var type = createTypeSane(namedValue.type(), location);
      var name = nameNode.getText();
      var expr = createPipeSane(namedValue.pipe());
      var annotation = createNativeSane(namedValue.annotation());
      evaluables.add(new NamedValueP(type, createFullName(name), name, expr, annotation, location));
      return null;
    }

    private Maybe<AnnotationP> createNativeSane(AnnotationContext annotation) {
      if (annotation == null) {
        return none();
      } else {
        var name = annotation.NAME().getText();
        return some(new AnnotationP(
            name,
            createStringNode(annotation, annotation.STRING()),
            fileLocation(fullPath, annotation)));
      }
    }

    private NList<ItemP> createItems(String ownerName, ItemListContext itemList) {
      return nlistWithShadowing(createItemsList(ownerName, itemList));
    }

    private List<ItemP> createItemsList(String ownerName, ItemListContext itemList) {
      if (itemList != null) {
        var items = itemList.item();
        List<ItemContext> saneItems = items == null ? list() : listOfAll(items);
        return saneItems.map(i -> createItem(ownerName, i));
      }
      return list();
    }

    private ItemP createItem(String ownerName, ItemContext item) {
      var type = createT(item.type());
      var nameNode = item.NAME();
      var itemName = nameNode.getText();
      var location = fileLocation(fullPath, nameNode);
      var defaultValue = createDefaultValue(ownerName, itemName, item, location);
      return new ItemP(type, itemName, defaultValue, location);
    }

    private Maybe<NamedValueP> createDefaultValue(
        String ownerName, String itemName, ItemContext item, Location location) {
      return createExprSane(item.expr())
          .map(e -> namedValueForDefaultArgument(ownerName, itemName, e, location));
    }

    private NamedValueP namedValueForDefaultArgument(
        String ownerName, String itemName, ExprP body, Location location) {
      var name = ownerName + ":" + itemName;
      var type = new ImplicitTP(location);
      return new NamedValueP(type, name, itemName, some(body), none(), location);
    }

    private Maybe<ExprP> createPipeSane(PipeContext pipe) {
      return maybe(pipe).map(this::createPipe);
    }

    private ExprP createPipe(PipeContext pipe) {
      return createPipe(new AtomicReference<>(), pipe);
    }

    private ExprP createPipe(AtomicReference<ExprP> outerPiped, PipeContext pipe) {
      var exprs = pipe.expr();
      var firstExpr = createExpr(outerPiped, exprs.get(0));
      var innerPiped = new AtomicReference<>(firstExpr);
      for (int i = 1; i < exprs.size(); i++) {
        var exprContext = exprs.get(i);
        var expr = createExpr(innerPiped, exprContext);
        if (innerPiped.get() != null) {
          logPipedValueNotConsumedError(exprContext);
        }
        innerPiped.set(expr);
      }
      return innerPiped.get();
    }

    private void logPipedValueNotConsumedError(ExprContext parserRuleContext) {
      var location = fileLocation(fullPath, parserRuleContext);
      logger.log(compileError(location, "Piped value is not consumed."));
    }

    private Maybe<ExprP> createExprSane(ExprContext expr) {
      return maybe(expr).map(this::createExpr);
    }

    private ExprP createExpr(ExprContext expr) {
      return createExpr(new AtomicReference<>(), expr);
    }

    private ExprP createExpr(AtomicReference<ExprP> piped, ExprContext expr) {
      if (expr.chain() != null) {
        return createChain(piped, expr.chain());
      } else if (expr.lambda() != null) {
        return createLambda(expr.lambda());
      } else {
        throw new RuntimeException("shouldn't happen");
      }
    }

    private ExprP createChain(AtomicReference<ExprP> piped, ChainContext chain) {
      var chainHead = createChainHead(piped, chain.chainHead());
      return createChain(piped, chainHead, listOfAll(chain.chainPart()));
    }

    private ExprP createChainHead(AtomicReference<ExprP> pipedArg, ChainHeadContext chainHead) {
      var location = fileLocation(fullPath, chainHead);
      if (chainHead.NAME() != null) {
        var referenceP = new ReferenceP(chainHead.NAME().getText(), location);
        return new InstantiateP(referenceP, location);
      }
      if (chainHead.array() != null) {
        var elems = listOfAll(chainHead.array().expr()).map(this::createExpr);
        if (pipedArg.get() != null) {
          elems = list(pipedArg.get()).appendAll(elems);
          pipedArg.set(null);
        }
        return new OrderP(elems, location);
      }
      if (chainHead.parens() != null) {
        return createPipe(pipedArg, chainHead.parens().pipe());
      }
      if (chainHead.BLOB() != null) {
        return new BlobP(chainHead.BLOB().getText().substring(2), location);
      }
      if (chainHead.INT() != null) {
        return new IntP(chainHead.INT().getText(), location);
      }
      if (chainHead.STRING() != null) {
        return createStringNode(chainHead, chainHead.STRING());
      }
      throw newRuntimeException(ChainHeadContext.class);
    }

    private ExprP createChain(
        AtomicReference<ExprP> pipedArg, ExprP chainHead, List<ChainPartContext> chainParts) {
      var result = chainHead;
      for (var chainPart : chainParts) {
        var argList = chainPart.argList();
        if (argList != null) {
          var args = createArgList(argList);
          if (pipedArg.get() != null) {
            args = list(pipedArg.get()).appendAll(args);
            pipedArg.set(null);
          }
          result = createCall(result, args, argList);
        } else if (chainPart.select() != null) {
          result = createSelect(result, chainPart.select());
        } else {
          throw newRuntimeException(ChainPartContext.class);
        }
      }
      return result;
    }

    private InstantiateP createLambda(LambdaContext lambdaFunc) {
      var fullName = createFullName("^" + (++lambdaCount));
      var params = createItems(fullName, lambdaFunc.itemList());
      var body = createExpr(lambdaFunc.expr());
      var location = fileLocation(fullPath, lambdaFunc);
      var lambdaFuncP = new LambdaP(fullName, params, body, location);
      return new InstantiateP(lambdaFuncP, location);
    }

    private StringP createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
      var unquoted = unquote(quotedString.getText());
      var location = fileLocation(fullPath, expr);
      return new StringP(unquoted, location);
    }

    private SelectP createSelect(ExprP selectable, SelectContext fieldRead) {
      var name = fieldRead.NAME().getText();
      var location = fileLocation(fullPath, fieldRead);
      return new SelectP(selectable, name, location);
    }

    private List<ExprP> createArgList(ArgListContext argList) {
      ArrayList<ExprP> result = new ArrayList<>();
      for (ArgContext arg : argList.arg()) {
        ExprContext expr = arg.expr();
        TerminalNode nameNode = arg.NAME();
        ExprP exprP = createExpr(expr);
        if (nameNode == null) {
          result.add(exprP);
        } else {
          result.add(new NamedArgP(nameNode.getText(), exprP, fileLocation(fullPath, arg)));
        }
      }
      return listOfAll(result);
    }

    private ExprP createCall(ExprP callable, List<ExprP> args, ArgListContext argListContext) {
      var location = fileLocation(fullPath, argListContext);
      return new CallP(callable, args, location);
    }

    private TypeP createTypeSane(TypeContext type, Location location) {
      return type == null ? new ImplicitTP(location) : createT(type);
    }

    private TypeP createT(TypeContext type) {
      return switch (type) {
        case TypeNameContext name -> createT(name);
        case ArrayTContext arrayT -> createArrayT(arrayT);
        case FuncTContext funcT -> createFuncT(funcT);
        default -> throw unexpectedCaseExc(type);
      };
    }

    private TypeP createT(TypeNameContext type) {
      return new ExplicitTP(type.getText(), fileLocation(fullPath, type.NAME()));
    }

    private TypeP createArrayT(ArrayTContext arrayT) {
      var elemType = createT(arrayT.type());
      return new ArrayTP(elemType, fileLocation(fullPath, arrayT));
    }

    private TypeP createFuncT(FuncTContext funcT) {
      var types = listOfAll(funcT.type()).map(this::createT);
      var resultType = types.get(types.size() - 1);
      var paramTypesS = types.subList(0, types.size() - 1);
      return new FuncTP(resultType, paramTypesS, fileLocation(fullPath, funcT));
    }

    private String createFullName(String shortName) {
      return TypeNamesS.fullName(scopeName, shortName);
    }

    private RuntimeException newRuntimeException(Class<?> clazz) {
      return new RuntimeException(
          "Illegal parse tree: " + clazz.getSimpleName() + " without children.");
    }
  }
}
