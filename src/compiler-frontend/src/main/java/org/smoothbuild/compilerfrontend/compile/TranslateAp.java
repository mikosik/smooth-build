package org.smoothbuild.compilerfrontend.compile;

import static org.smoothbuild.common.base.Throwables.unexpectedCaseExc;
import static org.smoothbuild.common.collect.List.list;
import static org.smoothbuild.common.collect.List.listOfAll;
import static org.smoothbuild.common.collect.Maybe.maybe;
import static org.smoothbuild.common.collect.Maybe.none;
import static org.smoothbuild.common.collect.Maybe.some;
import static org.smoothbuild.common.collect.NList.nlistWithShadowing;
import static org.smoothbuild.compilerfrontend.FrontendCompilerConstants.COMPILE_PREFIX;
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
import org.smoothbuild.common.bucket.base.FullPath;
import org.smoothbuild.common.collect.List;
import org.smoothbuild.common.collect.Maybe;
import org.smoothbuild.common.collect.NList;
import org.smoothbuild.common.log.base.Label;
import org.smoothbuild.common.log.base.Logger;
import org.smoothbuild.common.log.base.Try;
import org.smoothbuild.common.plan.TryFunction2;
import org.smoothbuild.compilerfrontend.compile.ast.define.PAnnotation;
import org.smoothbuild.compilerfrontend.compile.ast.define.PArrayType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PBlob;
import org.smoothbuild.compilerfrontend.compile.ast.define.PCall;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PExpr;
import org.smoothbuild.compilerfrontend.compile.ast.define.PFuncType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PImplicitType;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInstantiate;
import org.smoothbuild.compilerfrontend.compile.ast.define.PInt;
import org.smoothbuild.compilerfrontend.compile.ast.define.PItem;
import org.smoothbuild.compilerfrontend.compile.ast.define.PLambda;
import org.smoothbuild.compilerfrontend.compile.ast.define.PModule;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedArg;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedEvaluable;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedFunc;
import org.smoothbuild.compilerfrontend.compile.ast.define.PNamedValue;
import org.smoothbuild.compilerfrontend.compile.ast.define.POrder;
import org.smoothbuild.compilerfrontend.compile.ast.define.PReference;
import org.smoothbuild.compilerfrontend.compile.ast.define.PSelect;
import org.smoothbuild.compilerfrontend.compile.ast.define.PString;
import org.smoothbuild.compilerfrontend.compile.ast.define.PStruct;
import org.smoothbuild.compilerfrontend.compile.ast.define.PType;
import org.smoothbuild.compilerfrontend.lang.base.STypeNames;
import org.smoothbuild.compilerfrontend.lang.base.location.Location;
import org.smoothbuild.compilerfrontend.lang.base.location.Locations;

public class TranslateAp implements TryFunction2<ModuleContext, FullPath, PModule> {
  @Override
  public Label label() {
    return Label.label(COMPILE_PREFIX, "simplifyParseTree");
  }

  @Override
  public Try<PModule> apply(ModuleContext moduleContext, FullPath fullPath) {
    var logger = new Logger();
    var structs = new ArrayList<PStruct>();
    var evaluables = new ArrayList<PNamedEvaluable>();
    var apTranslatingVisitor = new ApTranslatingVisitor(fullPath, structs, evaluables, logger);
    apTranslatingVisitor.visit(moduleContext);
    var name = fullPath.withExtension("").path().lastPart().toString();
    var moduleP = new PModule(name, listOfAll(structs), listOfAll(evaluables));
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
    private final ArrayList<PStruct> structs;
    private final ArrayList<PNamedEvaluable> evaluables;
    private final Logger logger;
    private final String scopeName;
    private int lambdaCount;

    public ApTranslatingVisitor(
        FullPath fullPath,
        ArrayList<PStruct> structs,
        ArrayList<PNamedEvaluable> evaluables,
        Logger logger) {
      this(fullPath, structs, evaluables, logger, null);
    }

    public ApTranslatingVisitor(
        FullPath fullPath,
        ArrayList<PStruct> structs,
        ArrayList<PNamedEvaluable> evaluables,
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
      structs.add(new PStruct(name, fields, location));
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
      evaluables.add(new PNamedFunc(type, fullName, name, params, body, annotation, location));
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
      evaluables.add(new PNamedValue(type, createFullName(name), name, expr, annotation, location));
      return null;
    }

    private Maybe<PAnnotation> createNativeSane(AnnotationContext annotation) {
      if (annotation == null) {
        return none();
      } else {
        var name = annotation.NAME().getText();
        return some(new PAnnotation(
            name,
            createStringNode(annotation, annotation.STRING()),
            fileLocation(fullPath, annotation)));
      }
    }

    private NList<PItem> createItems(String ownerName, ItemListContext itemList) {
      return nlistWithShadowing(createItemsList(ownerName, itemList));
    }

    private List<PItem> createItemsList(String ownerName, ItemListContext itemList) {
      if (itemList != null) {
        var items = itemList.item();
        List<ItemContext> saneItems = items == null ? list() : listOfAll(items);
        return saneItems.map(i -> createItem(ownerName, i));
      }
      return list();
    }

    private PItem createItem(String ownerName, ItemContext item) {
      var type = createT(item.type());
      var nameNode = item.NAME();
      var itemName = nameNode.getText();
      var location = fileLocation(fullPath, nameNode);
      var defaultValue = createDefaultValue(ownerName, itemName, item, location);
      return new PItem(type, itemName, defaultValue, location);
    }

    private Maybe<PNamedValue> createDefaultValue(
        String ownerName, String itemName, ItemContext item, Location location) {
      return createExprSane(item.expr())
          .map(e -> namedValueForDefaultArgument(ownerName, itemName, e, location));
    }

    private PNamedValue namedValueForDefaultArgument(
        String ownerName, String itemName, PExpr body, Location location) {
      var name = ownerName + ":" + itemName;
      var type = new PImplicitType(location);
      return new PNamedValue(type, name, itemName, some(body), none(), location);
    }

    private Maybe<PExpr> createPipeSane(PipeContext pipe) {
      return maybe(pipe).map(this::createPipe);
    }

    private PExpr createPipe(PipeContext pipe) {
      return createPipe(new AtomicReference<>(), pipe);
    }

    private PExpr createPipe(AtomicReference<PExpr> outerPiped, PipeContext pipe) {
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

    private Maybe<PExpr> createExprSane(ExprContext expr) {
      return maybe(expr).map(this::createExpr);
    }

    private PExpr createExpr(ExprContext expr) {
      return createExpr(new AtomicReference<>(), expr);
    }

    private PExpr createExpr(AtomicReference<PExpr> piped, ExprContext expr) {
      if (expr.chain() != null) {
        return createChain(piped, expr.chain());
      } else if (expr.lambda() != null) {
        return createLambda(expr.lambda());
      } else {
        throw new RuntimeException("shouldn't happen");
      }
    }

    private PExpr createChain(AtomicReference<PExpr> piped, ChainContext chain) {
      var chainHead = createChainHead(piped, chain.chainHead());
      return createChain(piped, chainHead, listOfAll(chain.chainPart()));
    }

    private PExpr createChainHead(AtomicReference<PExpr> pipedArg, ChainHeadContext chainHead) {
      var location = fileLocation(fullPath, chainHead);
      if (chainHead.NAME() != null) {
        var referenceP = new PReference(chainHead.NAME().getText(), location);
        return new PInstantiate(referenceP, location);
      }
      if (chainHead.array() != null) {
        var elems = listOfAll(chainHead.array().expr()).map(this::createExpr);
        if (pipedArg.get() != null) {
          elems = list(pipedArg.get()).appendAll(elems);
          pipedArg.set(null);
        }
        return new POrder(elems, location);
      }
      if (chainHead.parens() != null) {
        return createPipe(pipedArg, chainHead.parens().pipe());
      }
      if (chainHead.BLOB() != null) {
        return new PBlob(chainHead.BLOB().getText().substring(2), location);
      }
      if (chainHead.INT() != null) {
        return new PInt(chainHead.INT().getText(), location);
      }
      if (chainHead.STRING() != null) {
        return createStringNode(chainHead, chainHead.STRING());
      }
      throw newRuntimeException(ChainHeadContext.class);
    }

    private PExpr createChain(
        AtomicReference<PExpr> pipedArg, PExpr chainHead, List<ChainPartContext> chainParts) {
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

    private PInstantiate createLambda(LambdaContext lambdaFunc) {
      var fullName = createFullName("^" + (++lambdaCount));
      var params = createItems(fullName, lambdaFunc.itemList());
      var body = createExpr(lambdaFunc.expr());
      var location = fileLocation(fullPath, lambdaFunc);
      var lambdaFuncP = new PLambda(fullName, params, body, location);
      return new PInstantiate(lambdaFuncP, location);
    }

    private PString createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
      var unquoted = unquote(quotedString.getText());
      var location = fileLocation(fullPath, expr);
      return new PString(unquoted, location);
    }

    private PSelect createSelect(PExpr selectable, SelectContext fieldRead) {
      var name = fieldRead.NAME().getText();
      var location = fileLocation(fullPath, fieldRead);
      return new PSelect(selectable, name, location);
    }

    private List<PExpr> createArgList(ArgListContext argList) {
      ArrayList<PExpr> result = new ArrayList<>();
      for (ArgContext arg : argList.arg()) {
        ExprContext expr = arg.expr();
        TerminalNode nameNode = arg.NAME();
        PExpr pExpr = createExpr(expr);
        if (nameNode == null) {
          result.add(pExpr);
        } else {
          result.add(new PNamedArg(nameNode.getText(), pExpr, fileLocation(fullPath, arg)));
        }
      }
      return listOfAll(result);
    }

    private PExpr createCall(PExpr callable, List<PExpr> args, ArgListContext argListContext) {
      var location = fileLocation(fullPath, argListContext);
      return new PCall(callable, args, location);
    }

    private PType createTypeSane(TypeContext type, Location location) {
      return type == null ? new PImplicitType(location) : createT(type);
    }

    private PType createT(TypeContext type) {
      return switch (type) {
        case TypeNameContext name -> createT(name);
        case ArrayTContext arrayT -> createArrayT(arrayT);
        case FuncTContext funcT -> createFuncT(funcT);
        default -> throw unexpectedCaseExc(type);
      };
    }

    private PType createT(TypeNameContext type) {
      return new PExplicitType(type.getText(), fileLocation(fullPath, type.NAME()));
    }

    private PType createArrayT(ArrayTContext arrayT) {
      var elemType = createT(arrayT.type());
      return new PArrayType(elemType, fileLocation(fullPath, arrayT));
    }

    private PType createFuncT(FuncTContext funcT) {
      var types = listOfAll(funcT.type()).map(this::createT);
      var resultType = types.get(types.size() - 1);
      var paramTypesS = types.subList(0, types.size() - 1);
      return new PFuncType(resultType, paramTypesS, fileLocation(fullPath, funcT));
    }

    private String createFullName(String shortName) {
      return STypeNames.fullName(scopeName, shortName);
    }

    private RuntimeException newRuntimeException(Class<?> clazz) {
      return new RuntimeException(
          "Illegal parse tree: " + clazz.getSimpleName() + " without children.");
    }
  }
}
