package org.smoothbuild.compile.fs.fp;

import static com.google.common.collect.ImmutableList.toImmutableList;
import static org.smoothbuild.compile.fs.lang.base.TypeNamesS.fullName;
import static org.smoothbuild.out.log.Maybe.maybe;
import static org.smoothbuild.util.Throwables.unexpectedCaseExc;
import static org.smoothbuild.util.collect.Lists.concat;
import static org.smoothbuild.util.collect.Lists.map;
import static org.smoothbuild.util.collect.Lists.sane;
import static org.smoothbuild.util.collect.NList.nlistWithShadowing;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
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
import org.smoothbuild.compile.fs.lang.base.location.Location;
import org.smoothbuild.compile.fs.lang.base.location.Locations;
import org.smoothbuild.compile.fs.ps.CompileError;
import org.smoothbuild.compile.fs.ps.ast.define.AnnotationP;
import org.smoothbuild.compile.fs.ps.ast.define.ArrayTP;
import org.smoothbuild.compile.fs.ps.ast.define.BlobP;
import org.smoothbuild.compile.fs.ps.ast.define.CallP;
import org.smoothbuild.compile.fs.ps.ast.define.ExplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.ExprP;
import org.smoothbuild.compile.fs.ps.ast.define.FuncTP;
import org.smoothbuild.compile.fs.ps.ast.define.ImplicitTP;
import org.smoothbuild.compile.fs.ps.ast.define.InstantiateP;
import org.smoothbuild.compile.fs.ps.ast.define.IntP;
import org.smoothbuild.compile.fs.ps.ast.define.ItemP;
import org.smoothbuild.compile.fs.ps.ast.define.LambdaP;
import org.smoothbuild.compile.fs.ps.ast.define.ModuleP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedArgP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedEvaluableP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedFuncP;
import org.smoothbuild.compile.fs.ps.ast.define.NamedValueP;
import org.smoothbuild.compile.fs.ps.ast.define.OrderP;
import org.smoothbuild.compile.fs.ps.ast.define.ReferenceP;
import org.smoothbuild.compile.fs.ps.ast.define.SelectP;
import org.smoothbuild.compile.fs.ps.ast.define.StringP;
import org.smoothbuild.compile.fs.ps.ast.define.StructP;
import org.smoothbuild.compile.fs.ps.ast.define.TypeP;
import org.smoothbuild.fs.space.FilePath;
import org.smoothbuild.out.log.Level;
import org.smoothbuild.out.log.LogBuffer;
import org.smoothbuild.out.log.Maybe;
import org.smoothbuild.util.collect.NList;

import com.google.common.collect.ImmutableList;

public class ApTranslator {
  public static Maybe<ModuleP> translateAp(FilePath filePath, ModuleContext module) {
    var logs = new LogBuffer();
    var structs = new ArrayList<StructP>();
    var evaluables = new ArrayList<NamedEvaluableP>();
    new ApTranslatingVisitor(filePath, structs, evaluables, logs).visit(module);
    var name = filePath.withExtension("").path().lastPart().toString();
    var ast = new ModuleP(name, structs, evaluables);
    return maybe(logs.containsAtLeast(Level.ERROR) ? null : ast, logs);
  }

  private static String unquote(String quotedString) {
    return quotedString.substring(1, quotedString.length() - 1);
  }

  private static Location fileLocation(FilePath filePath, ParserRuleContext parserRuleContext) {
    return fileLocation(filePath, parserRuleContext.getStart());
  }

  private static Location fileLocation(FilePath filePath, TerminalNode node) {
    return fileLocation(filePath, node.getSymbol());
  }

  private static Location fileLocation(FilePath filePath, Token token) {
    return Locations.fileLocation(filePath, token.getLine());
  }

  private static class ApTranslatingVisitor extends SmoothAntlrBaseVisitor<Void> {
    private final FilePath filePath;
    private final ArrayList<StructP> structs;
    private final ArrayList<NamedEvaluableP> evaluables;
    private final LogBuffer logs;
    private final String scopeName;
    private int lambdaCount;

    public ApTranslatingVisitor(FilePath filePath, ArrayList<StructP> structs,
        ArrayList<NamedEvaluableP> evaluables, LogBuffer logs) {
      this(filePath, structs, evaluables, logs, null);
    }

    public ApTranslatingVisitor(
        FilePath filePath,
        ArrayList<StructP> structs,
        ArrayList<NamedEvaluableP> evaluables,
        LogBuffer logs,
        String scopeName) {
      this.filePath = filePath;
      this.structs = structs;
      this.evaluables = evaluables;
      this.logs = logs;
      this.scopeName = scopeName;
      this.lambdaCount = 0;
    }

    @Override
    public Void visitStruct(StructContext struct) {
      var name = struct.NAME().getText();
      var location = fileLocation(filePath, struct.NAME().getSymbol());
      var fields = createItems(name, struct.itemList());
      structs.add(new StructP(name, fields, location));
      return null;
    }

    @Override
    public Void visitNamedFunc(NamedFuncContext namedFunc) {
      var nameNode = namedFunc.NAME();
      var location = fileLocation(filePath, nameNode);
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
      var location = fileLocation(filePath, nameNode);
      visitChildren(namedValue);
      var type = createTypeSane(namedValue.type(), location);
      var name = nameNode.getText();
      var expr = createPipeSane(namedValue.pipe());
      var annotation = createNativeSane(namedValue.annotation());
      evaluables.add(new NamedValueP(type, createFullName(name), name, expr, annotation, location));
      return null;
    }

    private Optional<AnnotationP> createNativeSane(AnnotationContext annotation) {
      if (annotation == null) {
        return Optional.empty();
      } else {
        var name = annotation.NAME().getText();
        return Optional.of(new AnnotationP(
            name,
            createStringNode(annotation, annotation.STRING()),
            fileLocation(filePath, annotation)));
      }
    }

    private NList<ItemP> createItems(String ownerName, ItemListContext itemList) {
      return nlistWithShadowing(createItemsList(ownerName, itemList));
    }

    private ImmutableList<ItemP> createItemsList(String ownerName, ItemListContext itemList) {
      if (itemList != null) {
        return sane(itemList.item())
            .stream()
            .map(item -> createItem(ownerName, item))
            .collect(toImmutableList());
      }
      return ImmutableList.of();
    }

    private ItemP createItem(String ownerName, ItemContext item) {
      var type = createT(item.type());
      var nameNode = item.NAME();
      var itemName = nameNode.getText();
      var location = fileLocation(filePath, nameNode);
      var defaultValue = createDefaultValue(ownerName, itemName, item, location);
      return new ItemP(type, itemName, defaultValue, location);
    }

    private Optional<NamedValueP> createDefaultValue(
        String ownerName, String itemName, ItemContext item, Location location) {
      return createExprSane(item.expr())
          .map(e -> namedValueForDefaultArgument(ownerName, itemName, e, location));
    }

    private NamedValueP namedValueForDefaultArgument(
        String ownerName, String itemName, ExprP body, Location location) {
      var name = ownerName + ":" + itemName;
      var type = new ImplicitTP(location);
      return new NamedValueP(
          type, name, itemName, Optional.of(body), Optional.empty(), location);
    }

    private Optional<ExprP> createPipeSane(PipeContext pipe) {
      return Optional.ofNullable(pipe).map(this::createPipe);
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
      var location = fileLocation(filePath, parserRuleContext);
      logs.log(CompileError.compileError(location, "Piped value is not consumed."));
    }

    private Optional<ExprP> createExprSane(ExprContext expr) {
      return Optional.ofNullable(expr).map(this::createExpr);
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
      return createChain(piped, chainHead, chain.chainPart());
    }

    private ExprP createChainHead(AtomicReference<ExprP> pipedArg, ChainHeadContext chainHead) {
      var location = fileLocation(filePath, chainHead);
      if (chainHead.NAME() != null) {
        var referenceP = new ReferenceP(chainHead.NAME().getText(), location);
        return new InstantiateP(referenceP, location);
      }
      if (chainHead.array() != null) {
        var elems = map(chainHead.array().expr(), this::createExpr);
        if (pipedArg.get() != null) {
          elems = concat(pipedArg.get(), elems);
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

    private ExprP createChain(AtomicReference<ExprP> pipedArg, ExprP chainHead,
        List<ChainPartContext> chainParts) {
      var result = chainHead;
      for (var chainPart : chainParts) {
        var argList = chainPart.argList();
        if (argList != null) {
          var args = createArgList(argList);
          if (pipedArg.get() != null) {
            args = concat(pipedArg.get(), args);
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
      var location = fileLocation(filePath, lambdaFunc);
      var lambdaFuncP = new LambdaP(fullName, params, body, location);
      return new InstantiateP(lambdaFuncP, location);
    }

    private StringP createStringNode(ParserRuleContext expr, TerminalNode quotedString) {
      var unquoted = unquote(quotedString.getText());
      var location = fileLocation(filePath, expr);
      return new StringP(unquoted, location);
    }

    private SelectP createSelect(ExprP selectable, SelectContext fieldRead) {
      var name = fieldRead.NAME().getText();
      var location = fileLocation(filePath, fieldRead);
      return new SelectP(selectable, name, location);
    }

    private List<ExprP> createArgList(ArgListContext argList) {
      List<ExprP> result = new ArrayList<>();
      for (ArgContext arg : argList.arg()) {
        ExprContext expr = arg.expr();
        TerminalNode nameNode = arg.NAME();
        ExprP exprP = createExpr(expr);
        if (nameNode == null) {
          result.add(exprP);
        } else {
          result.add(new NamedArgP(nameNode.getText(), exprP, fileLocation(filePath, arg)));
        }
      }
      return result;
    }

    private ExprP createCall(ExprP callable, List<ExprP> args, ArgListContext argListContext) {
      var location = fileLocation(filePath, argListContext);
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
      return new ExplicitTP(type.getText(), fileLocation(filePath, type.NAME()));
    }

    private TypeP createArrayT(ArrayTContext arrayT) {
      var elemType = createT(arrayT.type());
      return new ArrayTP(elemType, fileLocation(filePath, arrayT));
    }

    private TypeP createFuncT(FuncTContext funcT) {
      var types = map(funcT.type(), this::createT);
      var resultType = types.get(types.size() - 1);
      var paramTypesS = types.subList(0, types.size() - 1);
      return new FuncTP(resultType, paramTypesS, fileLocation(filePath, funcT));
    }

    private String createFullName(String shortName) {
      return fullName(scopeName, shortName);
    }

    private RuntimeException newRuntimeException(Class<?> clazz) {
      return new RuntimeException("Illegal parse tree: " + clazz.getSimpleName()
          + " without children.");
    }
  }
}
