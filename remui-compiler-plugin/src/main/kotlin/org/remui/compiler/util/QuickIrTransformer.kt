/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("CONTEXT_RECEIVERS_DEPRECATED")

package org.remui.compiler.util

import org.jetbrains.kotlin.ir.IrElement
import org.jetbrains.kotlin.ir.IrStatement
import org.jetbrains.kotlin.ir.declarations.*
import org.jetbrains.kotlin.ir.expressions.*
import org.jetbrains.kotlin.ir.visitors.IrElementTransformer

class QuickIrTransformer<D>() : IrElementTransformer<D> {


    private var _visitElement: (context(IrTransformerContext<D>) IrElement.(D) -> IrElement?)? = null
    override fun visitElement(element: IrElement, data: D): IrElement =
        _visitElement?.invoke(IrTransformerContext(this), element, data) ?: super.visitElement(element, data)


    private var _visitDeclaration: (context(IrTransformerContext<D>) IrDeclarationBase.(D) -> IrStatement?)? = null
    override fun visitDeclaration(declaration: IrDeclarationBase, data: D): IrStatement =
        _visitDeclaration?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitDeclaration(declaration, data)


    private var _visitValueParameter: (context(IrTransformerContext<D>) IrValueParameter.(D) -> IrStatement?)? = null
    override fun visitValueParameter(declaration: IrValueParameter, data: D): IrStatement =
        _visitValueParameter?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitValueParameter(declaration, data)


    private var _visitClass: (context(IrTransformerContext<D>) IrClass.(D) -> IrStatement?)? = null
    override fun visitClass(declaration: IrClass, data: D): IrStatement =
        _visitClass?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitClass(declaration, data)


    private var _visitAnonymousInitializer: (context(IrTransformerContext<D>) IrAnonymousInitializer.(D) -> IrStatement?)? = null
    override fun visitAnonymousInitializer(declaration: IrAnonymousInitializer, data: D): IrStatement =
        _visitAnonymousInitializer?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitAnonymousInitializer(declaration, data)


    private var _visitTypeParameter: (context(IrTransformerContext<D>) IrTypeParameter.(D) -> IrStatement?)? = null
    override fun visitTypeParameter(declaration: IrTypeParameter, data: D): IrStatement =
        _visitTypeParameter?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitTypeParameter(declaration, data)


    private var _visitFunction: (context(IrTransformerContext<D>) IrFunction.(D) -> IrStatement?)? = null
    override fun visitFunction(declaration: IrFunction, data: D): IrStatement =
        _visitFunction?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitFunction(declaration, data)


    private var _visitConstructor: (context(IrTransformerContext<D>) IrConstructor.(D) -> IrStatement?)? = null
    override fun visitConstructor(declaration: IrConstructor, data: D): IrStatement =
        _visitConstructor?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitConstructor(declaration, data)


    private var _visitEnumEntry: (context(IrTransformerContext<D>) IrEnumEntry.(D) -> IrStatement?)? = null
    override fun visitEnumEntry(declaration: IrEnumEntry, data: D): IrStatement =
        _visitEnumEntry?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitEnumEntry(declaration, data)


    private var _visitErrorDeclaration: (context(IrTransformerContext<D>) IrErrorDeclaration.(D) -> IrStatement?)? = null
    override fun visitErrorDeclaration(declaration: IrErrorDeclaration, data: D): IrStatement =
        _visitErrorDeclaration?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitErrorDeclaration(declaration, data)


    private var _visitField: (context(IrTransformerContext<D>) IrField.(D) -> IrStatement?)? = null
    override fun visitField(declaration: IrField, data: D): IrStatement =
        _visitField?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitField(declaration, data)


    private var _visitLocalDelegatedProperty: (context(IrTransformerContext<D>) IrLocalDelegatedProperty.(D) -> IrStatement?)? = null
    override fun visitLocalDelegatedProperty(declaration: IrLocalDelegatedProperty, data: D): IrStatement =
        _visitLocalDelegatedProperty?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitLocalDelegatedProperty(declaration, data)


    private var _visitModuleFragment: (context(IrTransformerContext<D>) IrModuleFragment.(D) -> IrModuleFragment?)? = null
    override fun visitModuleFragment(declaration: IrModuleFragment, data: D): IrModuleFragment =
        _visitModuleFragment?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitModuleFragment(declaration, data)


    private var _visitProperty: (context(IrTransformerContext<D>) IrProperty.(D) -> IrStatement?)? = null
    override fun visitProperty(declaration: IrProperty, data: D): IrStatement =
        _visitProperty?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitProperty(declaration, data)


    private var _visitScript: (context(IrTransformerContext<D>) IrScript.(D) -> IrStatement?)? = null
    override fun visitScript(declaration: IrScript, data: D): IrStatement =
        _visitScript?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitScript(declaration, data)


    private var _visitSimpleFunction: (context(IrTransformerContext<D>) IrSimpleFunction.(D) -> IrStatement?)? = null
    override fun visitSimpleFunction(declaration: IrSimpleFunction, data: D): IrStatement =
        _visitSimpleFunction?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitSimpleFunction(declaration, data)


    private var _visitTypeAlias: (context(IrTransformerContext<D>) IrTypeAlias.(D) -> IrStatement?)? = null
    override fun visitTypeAlias(declaration: IrTypeAlias, data: D): IrStatement =
        _visitTypeAlias?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitTypeAlias(declaration, data)


    private var _visitVariable: (context(IrTransformerContext<D>) IrVariable.(D) -> IrStatement?)? = null
    override fun visitVariable(declaration: IrVariable, data: D): IrStatement =
        _visitVariable?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitVariable(declaration, data)


    private var _visitPackageFragment: (context(IrTransformerContext<D>) IrPackageFragment.(D) -> IrElement?)? = null
    override fun visitPackageFragment(declaration: IrPackageFragment, data: D): IrElement =
        _visitPackageFragment?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitPackageFragment(declaration, data)


    private var _visitExternalPackageFragment: (context(IrTransformerContext<D>) IrExternalPackageFragment.(D) -> IrExternalPackageFragment?)? = null
    override fun visitExternalPackageFragment(declaration: IrExternalPackageFragment, data: D): IrExternalPackageFragment =
        _visitExternalPackageFragment?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitExternalPackageFragment(declaration, data)


    private var _visitFile: (context(IrTransformerContext<D>) IrFile.(D) -> IrFile?)? = null
    override fun visitFile(declaration: IrFile, data: D): IrFile =
        _visitFile?.invoke(IrTransformerContext(this), declaration, data) ?: super.visitFile(declaration, data)


    private var _visitExpression: (context(IrTransformerContext<D>) IrExpression.(D) -> IrExpression?)? = null
    override fun visitExpression(expression: IrExpression, data: D): IrExpression =
        _visitExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitExpression(expression, data)


    private var _visitBody: (context(IrTransformerContext<D>) IrBody.(D) -> IrBody?)? = null
    override fun visitBody(body: IrBody, data: D): IrBody =
        _visitBody?.invoke(IrTransformerContext(this), body, data) ?: super.visitBody(body, data)


    private var _visitExpressionBody: (context(IrTransformerContext<D>) IrExpressionBody.(D) -> IrBody?)? = null
    override fun visitExpressionBody(body: IrExpressionBody, data: D): IrBody =
        _visitExpressionBody?.invoke(IrTransformerContext(this), body, data) ?: super.visitExpressionBody(body, data)


    private var _visitBlockBody: (context(IrTransformerContext<D>) IrBlockBody.(D) -> IrBody?)? = null
    override fun visitBlockBody(body: IrBlockBody, data: D): IrBody =
        _visitBlockBody?.invoke(IrTransformerContext(this), body, data) ?: super.visitBlockBody(body, data)


    private var _visitDeclarationReference: (context(IrTransformerContext<D>) IrDeclarationReference.(D) -> IrExpression?)? = null
    override fun visitDeclarationReference(expression: IrDeclarationReference, data: D): IrExpression =
        _visitDeclarationReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitDeclarationReference(expression, data)


    private var _visitMemberAccess: (context(IrTransformerContext<D>) IrMemberAccessExpression<*>.(D) -> IrElement?)? = null
    override fun visitMemberAccess(expression: IrMemberAccessExpression<*>, data: D): IrElement =
        _visitMemberAccess?.invoke(IrTransformerContext(this), expression, data) ?: super.visitMemberAccess(expression, data)


    private var _visitFunctionAccess: (context(IrTransformerContext<D>) IrFunctionAccessExpression.(D) -> IrElement?)? = null
    override fun visitFunctionAccess(expression: IrFunctionAccessExpression, data: D): IrElement =
        _visitFunctionAccess?.invoke(IrTransformerContext(this), expression, data) ?: super.visitFunctionAccess(expression, data)


    private var _visitConstructorCall: (context(IrTransformerContext<D>) IrConstructorCall.(D) -> IrElement?)? = null
    override fun visitConstructorCall(expression: IrConstructorCall, data: D): IrElement =
        _visitConstructorCall?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConstructorCall(expression, data)


    private var _visitSingletonReference: (context(IrTransformerContext<D>) IrGetSingletonValue.(D) -> IrExpression?)? = null
    override fun visitSingletonReference(expression: IrGetSingletonValue, data: D): IrExpression =
        _visitSingletonReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitSingletonReference(expression, data)


    private var _visitGetObjectValue: (context(IrTransformerContext<D>) IrGetObjectValue.(D) -> IrExpression?)? = null
    override fun visitGetObjectValue(expression: IrGetObjectValue, data: D): IrExpression =
        _visitGetObjectValue?.invoke(IrTransformerContext(this), expression, data) ?: super.visitGetObjectValue(expression, data)


    private var _visitGetEnumValue: (context(IrTransformerContext<D>) IrGetEnumValue.(D) -> IrExpression?)? = null
    override fun visitGetEnumValue(expression: IrGetEnumValue, data: D): IrExpression =
        _visitGetEnumValue?.invoke(IrTransformerContext(this), expression, data) ?: super.visitGetEnumValue(expression, data)


    private var _visitRawFunctionReference: (context(IrTransformerContext<D>) IrRawFunctionReference.(D) -> IrExpression?)? = null
    override fun visitRawFunctionReference(expression: IrRawFunctionReference, data: D): IrExpression =
        _visitRawFunctionReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitRawFunctionReference(expression, data)


    private var _visitContainerExpression: (context(IrTransformerContext<D>) IrContainerExpression.(D) -> IrExpression?)? = null
    override fun visitContainerExpression(expression: IrContainerExpression, data: D): IrExpression =
        _visitContainerExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitContainerExpression(expression, data)


    private var _visitBlock: (context(IrTransformerContext<D>) IrBlock.(D) -> IrExpression?)? = null
    override fun visitBlock(expression: IrBlock, data: D): IrExpression =
        _visitBlock?.invoke(IrTransformerContext(this), expression, data) ?: super.visitBlock(expression, data)


    private var _visitComposite: (context(IrTransformerContext<D>) IrComposite.(D) -> IrExpression?)? = null
    override fun visitComposite(expression: IrComposite, data: D): IrExpression =
        _visitComposite?.invoke(IrTransformerContext(this), expression, data) ?: super.visitComposite(expression, data)


    private var _visitReturnableBlock: (context(IrTransformerContext<D>) IrReturnableBlock.(D) -> IrExpression?)? = null
    override fun visitReturnableBlock(expression: IrReturnableBlock, data: D): IrExpression =
        _visitReturnableBlock?.invoke(IrTransformerContext(this), expression, data) ?: super.visitReturnableBlock(expression, data)


    private var _visitInlinedFunctionBlock: (context(IrTransformerContext<D>) IrInlinedFunctionBlock.(D) -> IrExpression?)? = null
    override fun visitInlinedFunctionBlock(inlinedBlock: IrInlinedFunctionBlock, data: D): IrExpression =
        _visitInlinedFunctionBlock?.invoke(IrTransformerContext(this), inlinedBlock, data) ?: super.visitInlinedFunctionBlock(inlinedBlock, data)


    private var _visitSyntheticBody: (context(IrTransformerContext<D>) IrSyntheticBody.(D) -> IrBody?)? = null
    override fun visitSyntheticBody(body: IrSyntheticBody, data: D): IrBody =
        _visitSyntheticBody?.invoke(IrTransformerContext(this), body, data) ?: super.visitSyntheticBody(body, data)


    private var _visitBreakContinue: (context(IrTransformerContext<D>) IrBreakContinue.(D) -> IrExpression?)? = null
    override fun visitBreakContinue(jump: IrBreakContinue, data: D): IrExpression =
        _visitBreakContinue?.invoke(IrTransformerContext(this), jump, data) ?: super.visitBreakContinue(jump, data)


    private var _visitBreak: (context(IrTransformerContext<D>) IrBreak.(D) -> IrExpression?)? = null
    override fun visitBreak(jump: IrBreak, data: D): IrExpression =
        _visitBreak?.invoke(IrTransformerContext(this), jump, data) ?: super.visitBreak(jump, data)


    private var _visitContinue: (context(IrTransformerContext<D>) IrContinue.(D) -> IrExpression?)? = null
    override fun visitContinue(jump: IrContinue, data: D): IrExpression =
        _visitContinue?.invoke(IrTransformerContext(this), jump, data) ?: super.visitContinue(jump, data)


    private var _visitCall: (context(IrTransformerContext<D>) IrCall.(D) -> IrElement?)? = null
    override fun visitCall(expression: IrCall, data: D): IrElement =
        _visitCall?.invoke(IrTransformerContext(this), expression, data) ?: super.visitCall(expression, data)


    private var _visitCallableReference: (context(IrTransformerContext<D>) IrCallableReference<*>.(D) -> IrElement?)? = null
    override fun visitCallableReference(expression: IrCallableReference<*>, data: D): IrElement =
        _visitCallableReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitCallableReference(expression, data)


    private var _visitFunctionReference: (context(IrTransformerContext<D>) IrFunctionReference.(D) -> IrElement?)? = null
    override fun visitFunctionReference(expression: IrFunctionReference, data: D): IrElement =
        _visitFunctionReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitFunctionReference(expression, data)


    private var _visitPropertyReference: (context(IrTransformerContext<D>) IrPropertyReference.(D) -> IrElement?)? = null
    override fun visitPropertyReference(expression: IrPropertyReference, data: D): IrElement =
        _visitPropertyReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitPropertyReference(expression, data)


    private var _visitLocalDelegatedPropertyReference: (context(IrTransformerContext<D>) IrLocalDelegatedPropertyReference.(D) -> IrElement?)? = null
    override fun visitLocalDelegatedPropertyReference(expression: IrLocalDelegatedPropertyReference, data: D): IrElement =
        _visitLocalDelegatedPropertyReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitLocalDelegatedPropertyReference(expression, data)


    private var _visitClassReference: (context(IrTransformerContext<D>) IrClassReference.(D) -> IrExpression?)? = null
    override fun visitClassReference(expression: IrClassReference, data: D): IrExpression =
        _visitClassReference?.invoke(IrTransformerContext(this), expression, data) ?: super.visitClassReference(expression, data)


    private var _visitConst: (context(IrTransformerContext<D>) IrConst<*>.(D) -> IrExpression?)? = null
    override fun visitConst(expression: IrConst<*>, data: D): IrExpression =
        _visitConst?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConst(expression, data)


    private var _visitConstantValue: (context(IrTransformerContext<D>) IrConstantValue.(D) -> IrConstantValue?)? = null
    override fun visitConstantValue(expression: IrConstantValue, data: D): IrConstantValue =
        _visitConstantValue?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConstantValue(expression, data)


    private var _visitConstantPrimitive: (context(IrTransformerContext<D>) IrConstantPrimitive.(D) -> IrConstantValue?)? = null
    override fun visitConstantPrimitive(expression: IrConstantPrimitive, data: D): IrConstantValue =
        _visitConstantPrimitive?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConstantPrimitive(expression, data)


    private var _visitConstantObject: (context(IrTransformerContext<D>) IrConstantObject.(D) -> IrConstantValue?)? = null
    override fun visitConstantObject(expression: IrConstantObject, data: D): IrConstantValue =
        _visitConstantObject?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConstantObject(expression, data)


    private var _visitConstantArray: (context(IrTransformerContext<D>) IrConstantArray.(D) -> IrConstantValue?)? = null
    override fun visitConstantArray(expression: IrConstantArray, data: D): IrConstantValue =
        _visitConstantArray?.invoke(IrTransformerContext(this), expression, data) ?: super.visitConstantArray(expression, data)


    private var _visitDelegatingConstructorCall: (context(IrTransformerContext<D>) IrDelegatingConstructorCall.(D) -> IrElement?)? = null
    override fun visitDelegatingConstructorCall(expression: IrDelegatingConstructorCall, data: D): IrElement =
        _visitDelegatingConstructorCall?.invoke(IrTransformerContext(this), expression, data) ?: super.visitDelegatingConstructorCall(expression, data)


    private var _visitDynamicExpression: (context(IrTransformerContext<D>) IrDynamicExpression.(D) -> IrExpression?)? = null
    override fun visitDynamicExpression(expression: IrDynamicExpression, data: D): IrExpression =
        _visitDynamicExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitDynamicExpression(expression, data)


    private var _visitDynamicOperatorExpression: (context(IrTransformerContext<D>) IrDynamicOperatorExpression.(D) -> IrExpression?)? = null
    override fun visitDynamicOperatorExpression(expression: IrDynamicOperatorExpression, data: D): IrExpression =
        _visitDynamicOperatorExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitDynamicOperatorExpression(expression, data)


    private var _visitDynamicMemberExpression: (context(IrTransformerContext<D>) IrDynamicMemberExpression.(D) -> IrExpression?)? = null
    override fun visitDynamicMemberExpression(expression: IrDynamicMemberExpression, data: D): IrExpression =
        _visitDynamicMemberExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitDynamicMemberExpression(expression, data)


    private var _visitEnumConstructorCall: (context(IrTransformerContext<D>) IrEnumConstructorCall.(D) -> IrElement?)? = null
    override fun visitEnumConstructorCall(expression: IrEnumConstructorCall, data: D): IrElement =
        _visitEnumConstructorCall?.invoke(IrTransformerContext(this), expression, data) ?: super.visitEnumConstructorCall(expression, data)


    private var _visitErrorExpression: (context(IrTransformerContext<D>) IrErrorExpression.(D) -> IrExpression?)? = null
    override fun visitErrorExpression(expression: IrErrorExpression, data: D): IrExpression =
        _visitErrorExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitErrorExpression(expression, data)


    private var _visitErrorCallExpression: (context(IrTransformerContext<D>) IrErrorCallExpression.(D) -> IrExpression?)? = null
    override fun visitErrorCallExpression(expression: IrErrorCallExpression, data: D): IrExpression =
        _visitErrorCallExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitErrorCallExpression(expression, data)


    private var _visitFieldAccess: (context(IrTransformerContext<D>) IrFieldAccessExpression.(D) -> IrExpression?)? = null
    override fun visitFieldAccess(expression: IrFieldAccessExpression, data: D): IrExpression =
        _visitFieldAccess?.invoke(IrTransformerContext(this), expression, data) ?: super.visitFieldAccess(expression, data)


    private var _visitGetField: (context(IrTransformerContext<D>) IrGetField.(D) -> IrExpression?)? = null
    override fun visitGetField(expression: IrGetField, data: D): IrExpression =
        _visitGetField?.invoke(IrTransformerContext(this), expression, data) ?: super.visitGetField(expression, data)


    private var _visitSetField: (context(IrTransformerContext<D>) IrSetField.(D) -> IrExpression?)? = null
    override fun visitSetField(expression: IrSetField, data: D): IrExpression =
        _visitSetField?.invoke(IrTransformerContext(this), expression, data) ?: super.visitSetField(expression, data)


    private var _visitFunctionExpression: (context(IrTransformerContext<D>) IrFunctionExpression.(D) -> IrElement?)? = null
    override fun visitFunctionExpression(expression: IrFunctionExpression, data: D): IrElement =
        _visitFunctionExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitFunctionExpression(expression, data)


    private var _visitGetClass: (context(IrTransformerContext<D>) IrGetClass.(D) -> IrExpression?)? = null
    override fun visitGetClass(expression: IrGetClass, data: D): IrExpression =
        _visitGetClass?.invoke(IrTransformerContext(this), expression, data) ?: super.visitGetClass(expression, data)


    private var _visitInstanceInitializerCall: (context(IrTransformerContext<D>) IrInstanceInitializerCall.(D) -> IrExpression?)? = null
    override fun visitInstanceInitializerCall(expression: IrInstanceInitializerCall, data: D): IrExpression =
        _visitInstanceInitializerCall?.invoke(IrTransformerContext(this), expression, data) ?: super.visitInstanceInitializerCall(expression, data)


    private var _visitLoop: (context(IrTransformerContext<D>) IrLoop.(D) -> IrExpression?)? = null
    override fun visitLoop(loop: IrLoop, data: D): IrExpression =
        _visitLoop?.invoke(IrTransformerContext(this), loop, data) ?: super.visitLoop(loop, data)


    private var _visitWhileLoop: (context(IrTransformerContext<D>) IrWhileLoop.(D) -> IrExpression?)? = null
    override fun visitWhileLoop(loop: IrWhileLoop, data: D): IrExpression =
        _visitWhileLoop?.invoke(IrTransformerContext(this), loop, data) ?: super.visitWhileLoop(loop, data)


    private var _visitDoWhileLoop: (context(IrTransformerContext<D>) IrDoWhileLoop.(D) -> IrExpression?)? = null
    override fun visitDoWhileLoop(loop: IrDoWhileLoop, data: D): IrExpression =
        _visitDoWhileLoop?.invoke(IrTransformerContext(this), loop, data) ?: super.visitDoWhileLoop(loop, data)


    private var _visitReturn: (context(IrTransformerContext<D>) IrReturn.(D) -> IrExpression?)? = null
    override fun visitReturn(expression: IrReturn, data: D): IrExpression =
        _visitReturn?.invoke(IrTransformerContext(this), expression, data) ?: super.visitReturn(expression, data)


    private var _visitStringConcatenation: (context(IrTransformerContext<D>) IrStringConcatenation.(D) -> IrExpression?)? = null
    override fun visitStringConcatenation(expression: IrStringConcatenation, data: D): IrExpression =
        _visitStringConcatenation?.invoke(IrTransformerContext(this), expression, data) ?: super.visitStringConcatenation(expression, data)


    private var _visitSuspensionPoint: (context(IrTransformerContext<D>) IrSuspensionPoint.(D) -> IrExpression?)? = null
    override fun visitSuspensionPoint(expression: IrSuspensionPoint, data: D): IrExpression =
        _visitSuspensionPoint?.invoke(IrTransformerContext(this), expression, data) ?: super.visitSuspensionPoint(expression, data)


    private var _visitSuspendableExpression: (context(IrTransformerContext<D>) IrSuspendableExpression.(D) -> IrExpression?)? = null
    override fun visitSuspendableExpression(expression: IrSuspendableExpression, data: D): IrExpression =
        _visitSuspendableExpression?.invoke(IrTransformerContext(this), expression, data) ?: super.visitSuspendableExpression(expression, data)


    private var _visitThrow: (context(IrTransformerContext<D>) IrThrow.(D) -> IrExpression?)? = null
    override fun visitThrow(expression: IrThrow, data: D): IrExpression =
        _visitThrow?.invoke(IrTransformerContext(this), expression, data) ?: super.visitThrow(expression, data)


    private var _visitTry: (context(IrTransformerContext<D>) IrTry.(D) -> IrExpression?)? = null
    override fun visitTry(aTry: IrTry, data: D): IrExpression =
        _visitTry?.invoke(IrTransformerContext(this), aTry, data) ?: super.visitTry(aTry, data)


    private var _visitCatch: (context(IrTransformerContext<D>) IrCatch.(D) -> IrCatch?)? = null
    override fun visitCatch(aCatch: IrCatch, data: D): IrCatch =
        _visitCatch?.invoke(IrTransformerContext(this), aCatch, data) ?: super.visitCatch(aCatch, data)


    private var _visitTypeOperator: (context(IrTransformerContext<D>) IrTypeOperatorCall.(D) -> IrExpression?)? = null
    override fun visitTypeOperator(expression: IrTypeOperatorCall, data: D): IrExpression =
        _visitTypeOperator?.invoke(IrTransformerContext(this), expression, data) ?: super.visitTypeOperator(expression, data)


    private var _visitValueAccess: (context(IrTransformerContext<D>) IrValueAccessExpression.(D) -> IrExpression?)? = null
    override fun visitValueAccess(expression: IrValueAccessExpression, data: D): IrExpression =
        _visitValueAccess?.invoke(IrTransformerContext(this), expression, data) ?: super.visitValueAccess(expression, data)


    private var _visitGetValue: (context(IrTransformerContext<D>) IrGetValue.(D) -> IrExpression?)? = null
    override fun visitGetValue(expression: IrGetValue, data: D): IrExpression =
        _visitGetValue?.invoke(IrTransformerContext(this), expression, data) ?: super.visitGetValue(expression, data)


    private var _visitSetValue: (context(IrTransformerContext<D>) IrSetValue.(D) -> IrExpression?)? = null
    override fun visitSetValue(expression: IrSetValue, data: D): IrExpression =
        _visitSetValue?.invoke(IrTransformerContext(this), expression, data) ?: super.visitSetValue(expression, data)


    private var _visitVararg: (context(IrTransformerContext<D>) IrVararg.(D) -> IrExpression?)? = null
    override fun visitVararg(expression: IrVararg, data: D): IrExpression =
        _visitVararg?.invoke(IrTransformerContext(this), expression, data) ?: super.visitVararg(expression, data)


    private var _visitSpreadElement: (context(IrTransformerContext<D>) IrSpreadElement.(D) -> IrSpreadElement?)? = null
    override fun visitSpreadElement(spread: IrSpreadElement, data: D): IrSpreadElement =
        _visitSpreadElement?.invoke(IrTransformerContext(this), spread, data) ?: super.visitSpreadElement(spread, data)


    private var _visitWhen: (context(IrTransformerContext<D>) IrWhen.(D) -> IrExpression?)? = null
    override fun visitWhen(expression: IrWhen, data: D): IrExpression =
        _visitWhen?.invoke(IrTransformerContext(this), expression, data) ?: super.visitWhen(expression, data)


    private var _visitBranch: (context(IrTransformerContext<D>) IrBranch.(D) -> IrBranch?)? = null
    override fun visitBranch(branch: IrBranch, data: D): IrBranch =
        _visitBranch?.invoke(IrTransformerContext(this), branch, data) ?: super.visitBranch(branch, data)


    private var _visitElseBranch: (context(IrTransformerContext<D>) IrElseBranch.(D) -> IrElseBranch?)? = null
    override fun visitElseBranch(branch: IrElseBranch, data: D): IrElseBranch =
        _visitElseBranch?.invoke(IrTransformerContext(this), branch, data) ?: super.visitElseBranch(branch, data)


    companion object {

        fun <D> create(body: IrTransformerSpec<D>.() -> Unit): IrElementTransformer<D> {

            val quick = QuickIrTransformer<D>()

            val spec = object: IrTransformerSpec<D> {

                override fun visitElement                        (transformer: (context(IrTransformerContext<D>) IrElement                        .(D) -> IrElement?                 )) { quick._visitElement                         = transformer }
                override fun visitDeclaration                    (transformer: (context(IrTransformerContext<D>) IrDeclarationBase                .(D) -> IrStatement?               )) { quick._visitDeclaration                     = transformer }
                override fun visitValueParameter                 (transformer: (context(IrTransformerContext<D>) IrValueParameter                 .(D) -> IrStatement?               )) { quick._visitValueParameter                  = transformer }
                override fun visitClass                          (transformer: (context(IrTransformerContext<D>) IrClass                          .(D) -> IrStatement?               )) { quick._visitClass                           = transformer }
                override fun visitAnonymousInitializer           (transformer: (context(IrTransformerContext<D>) IrAnonymousInitializer           .(D) -> IrStatement?               )) { quick._visitAnonymousInitializer            = transformer }
                override fun visitTypeParameter                  (transformer: (context(IrTransformerContext<D>) IrTypeParameter                  .(D) -> IrStatement?               )) { quick._visitTypeParameter                   = transformer }
                override fun visitFunction                       (transformer: (context(IrTransformerContext<D>) IrFunction                       .(D) -> IrStatement?               )) { quick._visitFunction                        = transformer }
                override fun visitConstructor                    (transformer: (context(IrTransformerContext<D>) IrConstructor                    .(D) -> IrStatement?               )) { quick._visitConstructor                     = transformer }
                override fun visitEnumEntry                      (transformer: (context(IrTransformerContext<D>) IrEnumEntry                      .(D) -> IrStatement?               )) { quick._visitEnumEntry                       = transformer }
                override fun visitErrorDeclaration               (transformer: (context(IrTransformerContext<D>) IrErrorDeclaration               .(D) -> IrStatement?               )) { quick._visitErrorDeclaration                = transformer }
                override fun visitField                          (transformer: (context(IrTransformerContext<D>) IrField                          .(D) -> IrStatement?               )) { quick._visitField                           = transformer }
                override fun visitLocalDelegatedProperty         (transformer: (context(IrTransformerContext<D>) IrLocalDelegatedProperty         .(D) -> IrStatement?               )) { quick._visitLocalDelegatedProperty          = transformer }
                override fun visitModuleFragment                 (transformer: (context(IrTransformerContext<D>) IrModuleFragment                 .(D) -> IrModuleFragment?          )) { quick._visitModuleFragment                  = transformer }
                override fun visitProperty                       (transformer: (context(IrTransformerContext<D>) IrProperty                       .(D) -> IrStatement?               )) { quick._visitProperty                        = transformer }
                override fun visitScript                         (transformer: (context(IrTransformerContext<D>) IrScript                         .(D) -> IrStatement?               )) { quick._visitScript                          = transformer }
                override fun visitSimpleFunction                 (transformer: (context(IrTransformerContext<D>) IrSimpleFunction                 .(D) -> IrStatement?               )) { quick._visitSimpleFunction                  = transformer }
                override fun visitTypeAlias                      (transformer: (context(IrTransformerContext<D>) IrTypeAlias                      .(D) -> IrStatement?               )) { quick._visitTypeAlias                       = transformer }
                override fun visitVariable                       (transformer: (context(IrTransformerContext<D>) IrVariable                       .(D) -> IrStatement?               )) { quick._visitVariable                        = transformer }
                override fun visitPackageFragment                (transformer: (context(IrTransformerContext<D>) IrPackageFragment                .(D) -> IrElement?                 )) { quick._visitPackageFragment                 = transformer }
                override fun visitExternalPackageFragment        (transformer: (context(IrTransformerContext<D>) IrExternalPackageFragment        .(D) -> IrExternalPackageFragment? )) { quick._visitExternalPackageFragment         = transformer }
                override fun visitFile                           (transformer: (context(IrTransformerContext<D>) IrFile                           .(D) -> IrFile?                    )) { quick._visitFile                            = transformer }
                override fun visitExpression                     (transformer: (context(IrTransformerContext<D>) IrExpression                     .(D) -> IrExpression?              )) { quick._visitExpression                      = transformer }
                override fun visitBody                           (transformer: (context(IrTransformerContext<D>) IrBody                           .(D) -> IrBody?                    )) { quick._visitBody                            = transformer }
                override fun visitExpressionBody                 (transformer: (context(IrTransformerContext<D>) IrExpressionBody                 .(D) -> IrBody?                    )) { quick._visitExpressionBody                  = transformer }
                override fun visitBlockBody                      (transformer: (context(IrTransformerContext<D>) IrBlockBody                      .(D) -> IrBody?                    )) { quick._visitBlockBody                       = transformer }
                override fun visitDeclarationReference           (transformer: (context(IrTransformerContext<D>) IrDeclarationReference           .(D) -> IrExpression?              )) { quick._visitDeclarationReference            = transformer }
                override fun visitMemberAccess                   (transformer: (context(IrTransformerContext<D>) IrMemberAccessExpression<*>      .(D) -> IrElement?                 )) { quick._visitMemberAccess                    = transformer }
                override fun visitFunctionAccess                 (transformer: (context(IrTransformerContext<D>) IrFunctionAccessExpression       .(D) -> IrElement?                 )) { quick._visitFunctionAccess                  = transformer }
                override fun visitConstructorCall                (transformer: (context(IrTransformerContext<D>) IrConstructorCall                .(D) -> IrElement?                 )) { quick._visitConstructorCall                 = transformer }
                override fun visitSingletonReference             (transformer: (context(IrTransformerContext<D>) IrGetSingletonValue              .(D) -> IrExpression?              )) { quick._visitSingletonReference              = transformer }
                override fun visitGetObjectValue                 (transformer: (context(IrTransformerContext<D>) IrGetObjectValue                 .(D) -> IrExpression?              )) { quick._visitGetObjectValue                  = transformer }
                override fun visitGetEnumValue                   (transformer: (context(IrTransformerContext<D>) IrGetEnumValue                   .(D) -> IrExpression?              )) { quick._visitGetEnumValue                    = transformer }
                override fun visitRawFunctionReference           (transformer: (context(IrTransformerContext<D>) IrRawFunctionReference           .(D) -> IrExpression?              )) { quick._visitRawFunctionReference            = transformer }
                override fun visitContainerExpression            (transformer: (context(IrTransformerContext<D>) IrContainerExpression            .(D) -> IrExpression?              )) { quick._visitContainerExpression             = transformer }
                override fun visitBlock                          (transformer: (context(IrTransformerContext<D>) IrBlock                          .(D) -> IrExpression?              )) { quick._visitBlock                           = transformer }
                override fun visitComposite                      (transformer: (context(IrTransformerContext<D>) IrComposite                      .(D) -> IrExpression?              )) { quick._visitComposite                       = transformer }
                override fun visitReturnableBlock                (transformer: (context(IrTransformerContext<D>) IrReturnableBlock                .(D) -> IrExpression?              )) { quick._visitReturnableBlock                 = transformer }
                override fun visitInlinedFunctionBlock           (transformer: (context(IrTransformerContext<D>) IrInlinedFunctionBlock           .(D) -> IrExpression?              )) { quick._visitInlinedFunctionBlock            = transformer }
                override fun visitSyntheticBody                  (transformer: (context(IrTransformerContext<D>) IrSyntheticBody                  .(D) -> IrBody?                    )) { quick._visitSyntheticBody                   = transformer }
                override fun visitBreakContinue                  (transformer: (context(IrTransformerContext<D>) IrBreakContinue                  .(D) -> IrExpression?              )) { quick._visitBreakContinue                   = transformer }
                override fun visitBreak                          (transformer: (context(IrTransformerContext<D>) IrBreak                          .(D) -> IrExpression?              )) { quick._visitBreak                           = transformer }
                override fun visitContinue                       (transformer: (context(IrTransformerContext<D>) IrContinue                       .(D) -> IrExpression?              )) { quick._visitContinue                        = transformer }
                override fun visitCall                           (transformer: (context(IrTransformerContext<D>) IrCall                           .(D) -> IrElement?                 )) { quick._visitCall                            = transformer }
                override fun visitCallableReference              (transformer: (context(IrTransformerContext<D>) IrCallableReference<*>           .(D) -> IrElement?                 )) { quick._visitCallableReference               = transformer }
                override fun visitFunctionReference              (transformer: (context(IrTransformerContext<D>) IrFunctionReference              .(D) -> IrElement?                 )) { quick._visitFunctionReference               = transformer }
                override fun visitPropertyReference              (transformer: (context(IrTransformerContext<D>) IrPropertyReference              .(D) -> IrElement?                 )) { quick._visitPropertyReference               = transformer }
                override fun visitLocalDelegatedPropertyReference(transformer: (context(IrTransformerContext<D>) IrLocalDelegatedPropertyReference.(D) -> IrElement?                 )) { quick._visitLocalDelegatedPropertyReference = transformer }
                override fun visitClassReference                 (transformer: (context(IrTransformerContext<D>) IrClassReference                 .(D) -> IrExpression?              )) { quick._visitClassReference                  = transformer }
                override fun visitConst                          (transformer: (context(IrTransformerContext<D>) IrConst<*>                       .(D) -> IrExpression?              )) { quick._visitConst                           = transformer }
                override fun visitConstantValue                  (transformer: (context(IrTransformerContext<D>) IrConstantValue                  .(D) -> IrConstantValue?           )) { quick._visitConstantValue                   = transformer }
                override fun visitConstantPrimitive              (transformer: (context(IrTransformerContext<D>) IrConstantPrimitive              .(D) -> IrConstantValue?           )) { quick._visitConstantPrimitive               = transformer }
                override fun visitConstantObject                 (transformer: (context(IrTransformerContext<D>) IrConstantObject                 .(D) -> IrConstantValue?           )) { quick._visitConstantObject                  = transformer }
                override fun visitConstantArray                  (transformer: (context(IrTransformerContext<D>) IrConstantArray                  .(D) -> IrConstantValue?           )) { quick._visitConstantArray                   = transformer }
                override fun visitDelegatingConstructorCall      (transformer: (context(IrTransformerContext<D>) IrDelegatingConstructorCall      .(D) -> IrElement?                 )) { quick._visitDelegatingConstructorCall       = transformer }
                override fun visitDynamicExpression              (transformer: (context(IrTransformerContext<D>) IrDynamicExpression              .(D) -> IrExpression?              )) { quick._visitDynamicExpression               = transformer }
                override fun visitDynamicOperatorExpression      (transformer: (context(IrTransformerContext<D>) IrDynamicOperatorExpression      .(D) -> IrExpression?              )) { quick._visitDynamicOperatorExpression       = transformer }
                override fun visitDynamicMemberExpression        (transformer: (context(IrTransformerContext<D>) IrDynamicMemberExpression        .(D) -> IrExpression?              )) { quick._visitDynamicMemberExpression         = transformer }
                override fun visitEnumConstructorCall            (transformer: (context(IrTransformerContext<D>) IrEnumConstructorCall            .(D) -> IrElement?                 )) { quick._visitEnumConstructorCall             = transformer }
                override fun visitErrorExpression                (transformer: (context(IrTransformerContext<D>) IrErrorExpression                .(D) -> IrExpression?              )) { quick._visitErrorExpression                 = transformer }
                override fun visitErrorCallExpression            (transformer: (context(IrTransformerContext<D>) IrErrorCallExpression            .(D) -> IrExpression?              )) { quick._visitErrorCallExpression             = transformer }
                override fun visitFieldAccess                    (transformer: (context(IrTransformerContext<D>) IrFieldAccessExpression          .(D) -> IrExpression?              )) { quick._visitFieldAccess                     = transformer }
                override fun visitGetField                       (transformer: (context(IrTransformerContext<D>) IrGetField                       .(D) -> IrExpression?              )) { quick._visitGetField                        = transformer }
                override fun visitSetField                       (transformer: (context(IrTransformerContext<D>) IrSetField                       .(D) -> IrExpression?              )) { quick._visitSetField                        = transformer }
                override fun visitFunctionExpression             (transformer: (context(IrTransformerContext<D>) IrFunctionExpression             .(D) -> IrElement?                 )) { quick._visitFunctionExpression              = transformer }
                override fun visitGetClass                       (transformer: (context(IrTransformerContext<D>) IrGetClass                       .(D) -> IrExpression?              )) { quick._visitGetClass                        = transformer }
                override fun visitInstanceInitializerCall        (transformer: (context(IrTransformerContext<D>) IrInstanceInitializerCall        .(D) -> IrExpression?              )) { quick._visitInstanceInitializerCall         = transformer }
                override fun visitLoop                           (transformer: (context(IrTransformerContext<D>) IrLoop                           .(D) -> IrExpression?              )) { quick._visitLoop                            = transformer }
                override fun visitWhileLoop                      (transformer: (context(IrTransformerContext<D>) IrWhileLoop                      .(D) -> IrExpression?              )) { quick._visitWhileLoop                       = transformer }
                override fun visitDoWhileLoop                    (transformer: (context(IrTransformerContext<D>) IrDoWhileLoop                    .(D) -> IrExpression?              )) { quick._visitDoWhileLoop                     = transformer }
                override fun visitReturn                         (transformer: (context(IrTransformerContext<D>) IrReturn                         .(D) -> IrExpression?              )) { quick._visitReturn                          = transformer }
                override fun visitStringConcatenation            (transformer: (context(IrTransformerContext<D>) IrStringConcatenation            .(D) -> IrExpression?              )) { quick._visitStringConcatenation             = transformer }
                override fun visitSuspensionPoint                (transformer: (context(IrTransformerContext<D>) IrSuspensionPoint                .(D) -> IrExpression?              )) { quick._visitSuspensionPoint                 = transformer }
                override fun visitSuspendableExpression          (transformer: (context(IrTransformerContext<D>) IrSuspendableExpression          .(D) -> IrExpression?              )) { quick._visitSuspendableExpression           = transformer }
                override fun visitThrow                          (transformer: (context(IrTransformerContext<D>) IrThrow                          .(D) -> IrExpression?              )) { quick._visitThrow                           = transformer }
                override fun visitTry                            (transformer: (context(IrTransformerContext<D>) IrTry                            .(D) -> IrExpression?              )) { quick._visitTry                             = transformer }
                override fun visitCatch                          (transformer: (context(IrTransformerContext<D>) IrCatch                          .(D) -> IrCatch?                   )) { quick._visitCatch                           = transformer }
                override fun visitTypeOperator                   (transformer: (context(IrTransformerContext<D>) IrTypeOperatorCall               .(D) -> IrExpression?              )) { quick._visitTypeOperator                    = transformer }
                override fun visitValueAccess                    (transformer: (context(IrTransformerContext<D>) IrValueAccessExpression          .(D) -> IrExpression?              )) { quick._visitValueAccess                     = transformer }
                override fun visitGetValue                       (transformer: (context(IrTransformerContext<D>) IrGetValue                       .(D) -> IrExpression?              )) { quick._visitGetValue                        = transformer }
                override fun visitSetValue                       (transformer: (context(IrTransformerContext<D>) IrSetValue                       .(D) -> IrExpression?              )) { quick._visitSetValue                        = transformer }
                override fun visitVararg                         (transformer: (context(IrTransformerContext<D>) IrVararg                         .(D) -> IrExpression?              )) { quick._visitVararg                          = transformer }
                override fun visitSpreadElement                  (transformer: (context(IrTransformerContext<D>) IrSpreadElement                  .(D) -> IrSpreadElement?           )) { quick._visitSpreadElement                   = transformer }
                override fun visitWhen                           (transformer: (context(IrTransformerContext<D>) IrWhen                           .(D) -> IrExpression?              )) { quick._visitWhen                            = transformer }
                override fun visitBranch                         (transformer: (context(IrTransformerContext<D>) IrBranch                         .(D) -> IrBranch?                  )) { quick._visitBranch                          = transformer }
                override fun visitElseBranch                     (transformer: (context(IrTransformerContext<D>) IrElseBranch                     .(D) -> IrElseBranch?              )) { quick._visitElseBranch                      = transformer }

            }

            spec.apply(body)

            return quick
        }

    }
}

@QuickPluginDsl
interface IrTransformerSpec<D> {

    fun visitElement                        (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrElement                        .(D) -> IrElement?                 ))
    fun visitDeclaration                    (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDeclarationBase                .(D) -> IrStatement?               ))
    fun visitValueParameter                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrValueParameter                 .(D) -> IrStatement?               ))
    fun visitClass                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrClass                          .(D) -> IrStatement?               ))
    fun visitAnonymousInitializer           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrAnonymousInitializer           .(D) -> IrStatement?               ))
    fun visitTypeParameter                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrTypeParameter                  .(D) -> IrStatement?               ))
    fun visitFunction                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFunction                       .(D) -> IrStatement?               ))
    fun visitConstructor                    (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstructor                    .(D) -> IrStatement?               ))
    fun visitEnumEntry                      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrEnumEntry                      .(D) -> IrStatement?               ))
    fun visitErrorDeclaration               (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrErrorDeclaration               .(D) -> IrStatement?               ))
    fun visitField                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrField                          .(D) -> IrStatement?               ))
    fun visitLocalDelegatedProperty         (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrLocalDelegatedProperty         .(D) -> IrStatement?               ))
    fun visitModuleFragment                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrModuleFragment                 .(D) -> IrModuleFragment?          ))
    fun visitProperty                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrProperty                       .(D) -> IrStatement?               ))
    fun visitScript                         (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrScript                         .(D) -> IrStatement?               ))
    fun visitSimpleFunction                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSimpleFunction                 .(D) -> IrStatement?               ))
    fun visitTypeAlias                      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrTypeAlias                      .(D) -> IrStatement?               ))
    fun visitVariable                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrVariable                       .(D) -> IrStatement?               ))
    fun visitPackageFragment                (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrPackageFragment                .(D) -> IrElement?                 ))
    fun visitExternalPackageFragment        (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrExternalPackageFragment        .(D) -> IrExternalPackageFragment? ))
    fun visitFile                           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFile                           .(D) -> IrFile?                    ))
    fun visitExpression                     (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrExpression                     .(D) -> IrExpression?              ))
    fun visitBody                           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBody                           .(D) -> IrBody?                    ))
    fun visitExpressionBody                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrExpressionBody                 .(D) -> IrBody?                    ))
    fun visitBlockBody                      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBlockBody                      .(D) -> IrBody?                    ))
    fun visitDeclarationReference           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDeclarationReference           .(D) -> IrExpression?              ))
    fun visitMemberAccess                   (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrMemberAccessExpression<*>      .(D) -> IrElement?                 ))
    fun visitFunctionAccess                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFunctionAccessExpression       .(D) -> IrElement?                 ))
    fun visitConstructorCall                (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstructorCall                .(D) -> IrElement?                 ))
    fun visitSingletonReference             (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetSingletonValue              .(D) -> IrExpression?              ))
    fun visitGetObjectValue                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetObjectValue                 .(D) -> IrExpression?              ))
    fun visitGetEnumValue                   (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetEnumValue                   .(D) -> IrExpression?              ))
    fun visitRawFunctionReference           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrRawFunctionReference           .(D) -> IrExpression?              ))
    fun visitContainerExpression            (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrContainerExpression            .(D) -> IrExpression?              ))
    fun visitBlock                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBlock                          .(D) -> IrExpression?              ))
    fun visitComposite                      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrComposite                      .(D) -> IrExpression?              ))
    fun visitReturnableBlock                (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrReturnableBlock                .(D) -> IrExpression?              ))
    fun visitInlinedFunctionBlock           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrInlinedFunctionBlock           .(D) -> IrExpression?              ))
    fun visitSyntheticBody                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSyntheticBody                  .(D) -> IrBody?                    ))
    fun visitBreakContinue                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBreakContinue                  .(D) -> IrExpression?              ))
    fun visitBreak                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBreak                          .(D) -> IrExpression?              ))
    fun visitContinue                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrContinue                       .(D) -> IrExpression?              ))
    fun visitCall                           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrCall                           .(D) -> IrElement?                 ))
    fun visitCallableReference              (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrCallableReference<*>           .(D) -> IrElement?                 ))
    fun visitFunctionReference              (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFunctionReference              .(D) -> IrElement?                 ))
    fun visitPropertyReference              (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrPropertyReference              .(D) -> IrElement?                 ))
    fun visitLocalDelegatedPropertyReference(transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrLocalDelegatedPropertyReference.(D) -> IrElement?                 ))
    fun visitClassReference                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrClassReference                 .(D) -> IrExpression?              ))
    fun visitConst                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConst<*>                       .(D) -> IrExpression?              ))
    fun visitConstantValue                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstantValue                  .(D) -> IrConstantValue?           ))
    fun visitConstantPrimitive              (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstantPrimitive              .(D) -> IrConstantValue?           ))
    fun visitConstantObject                 (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstantObject                 .(D) -> IrConstantValue?           ))
    fun visitConstantArray                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrConstantArray                  .(D) -> IrConstantValue?           ))
    fun visitDelegatingConstructorCall      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDelegatingConstructorCall      .(D) -> IrElement?                 ))
    fun visitDynamicExpression              (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDynamicExpression              .(D) -> IrExpression?              ))
    fun visitDynamicOperatorExpression      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDynamicOperatorExpression      .(D) -> IrExpression?              ))
    fun visitDynamicMemberExpression        (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDynamicMemberExpression        .(D) -> IrExpression?              ))
    fun visitEnumConstructorCall            (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrEnumConstructorCall            .(D) -> IrElement?                 ))
    fun visitErrorExpression                (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrErrorExpression                .(D) -> IrExpression?              ))
    fun visitErrorCallExpression            (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrErrorCallExpression            .(D) -> IrExpression?              ))
    fun visitFieldAccess                    (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFieldAccessExpression          .(D) -> IrExpression?              ))
    fun visitGetField                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetField                       .(D) -> IrExpression?              ))
    fun visitSetField                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSetField                       .(D) -> IrExpression?              ))
    fun visitFunctionExpression             (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrFunctionExpression             .(D) -> IrElement?                 ))
    fun visitGetClass                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetClass                       .(D) -> IrExpression?              ))
    fun visitInstanceInitializerCall        (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrInstanceInitializerCall        .(D) -> IrExpression?              ))
    fun visitLoop                           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrLoop                           .(D) -> IrExpression?              ))
    fun visitWhileLoop                      (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrWhileLoop                      .(D) -> IrExpression?              ))
    fun visitDoWhileLoop                    (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrDoWhileLoop                    .(D) -> IrExpression?              ))
    fun visitReturn                         (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrReturn                         .(D) -> IrExpression?              ))
    fun visitStringConcatenation            (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrStringConcatenation            .(D) -> IrExpression?              ))
    fun visitSuspensionPoint                (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSuspensionPoint                .(D) -> IrExpression?              ))
    fun visitSuspendableExpression          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSuspendableExpression          .(D) -> IrExpression?              ))
    fun visitThrow                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrThrow                          .(D) -> IrExpression?              ))
    fun visitTry                            (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrTry                            .(D) -> IrExpression?              ))
    fun visitCatch                          (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrCatch                          .(D) -> IrCatch?                   ))
    fun visitTypeOperator                   (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrTypeOperatorCall               .(D) -> IrExpression?              ))
    fun visitValueAccess                    (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrValueAccessExpression          .(D) -> IrExpression?              ))
    fun visitGetValue                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrGetValue                       .(D) -> IrExpression?              ))
    fun visitSetValue                       (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSetValue                       .(D) -> IrExpression?              ))
    fun visitVararg                         (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrVararg                         .(D) -> IrExpression?              ))
    fun visitSpreadElement                  (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrSpreadElement                  .(D) -> IrSpreadElement?           ))
    fun visitWhen                           (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrWhen                           .(D) -> IrExpression?              ))
    fun visitBranch                         (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrBranch                         .(D) -> IrBranch?                  ))
    fun visitElseBranch                     (transformer: (@QuickPluginDsl context(IrTransformerContext<D>) IrElseBranch                     .(D) -> IrElseBranch?              ))

}


@JvmInline
value class IrTransformerContext<D>(private val quick: QuickIrTransformer<D>) {

    fun visit(element: IrElement                        , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrDeclarationBase                , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrValueParameter                 , data: D): IrStatement                = element.transform(quick, data)
    fun visit(element: IrClass                          , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrAnonymousInitializer           , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrTypeParameter                  , data: D): IrStatement                = element.transform(quick, data)
    fun visit(element: IrFunction                       , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrConstructor                    , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrEnumEntry                      , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrErrorDeclaration               , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrField                          , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrLocalDelegatedProperty         , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrModuleFragment                 , data: D): IrModuleFragment           = element.transform(quick, data)
    fun visit(element: IrProperty                       , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrScript                         , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrSimpleFunction                 , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrTypeAlias                      , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrVariable                       , data: D): IrStatement                = element.transform(quick, data) as IrStatement
    fun visit(element: IrPackageFragment                , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrExternalPackageFragment        , data: D): IrExternalPackageFragment  = element.transform(quick, data) as IrExternalPackageFragment
    fun visit(element: IrFile                           , data: D): IrFile                     = element.transform(quick, data)
    fun visit(element: IrExpression                     , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrBody                           , data: D): IrBody                     = element.transform(quick, data)
    fun visit(element: IrExpressionBody                 , data: D): IrBody                     = element.transform(quick, data)
    fun visit(element: IrBlockBody                      , data: D): IrBody                     = element.transform(quick, data)
    fun visit(element: IrDeclarationReference           , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrMemberAccessExpression<*>      , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrFunctionAccessExpression       , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrConstructorCall                , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrGetSingletonValue              , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrGetObjectValue                 , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrGetEnumValue                   , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrRawFunctionReference           , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrContainerExpression            , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrBlock                          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrComposite                      , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrReturnableBlock                , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrInlinedFunctionBlock           , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSyntheticBody                  , data: D): IrBody                     = element.transform(quick, data)
    fun visit(element: IrBreakContinue                  , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrBreak                          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrContinue                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrCall                           , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrCallableReference<*>           , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrFunctionReference              , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrPropertyReference              , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrLocalDelegatedPropertyReference, data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrClassReference                 , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrConst<*>                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrConstantValue                  , data: D): IrConstantValue            = element.transform(quick, data) as IrConstantValue
    fun visit(element: IrConstantPrimitive              , data: D): IrConstantValue            = element.transform(quick, data) as IrConstantValue
    fun visit(element: IrConstantObject                 , data: D): IrConstantValue            = element.transform(quick, data) as IrConstantValue
    fun visit(element: IrConstantArray                  , data: D): IrConstantValue            = element.transform(quick, data) as IrConstantValue
    fun visit(element: IrDelegatingConstructorCall      , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrDynamicExpression              , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrDynamicOperatorExpression      , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrDynamicMemberExpression        , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrEnumConstructorCall            , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrErrorExpression                , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrErrorCallExpression            , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrFieldAccessExpression          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrGetField                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSetField                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrFunctionExpression             , data: D): IrElement                  = element.transform(quick, data)
    fun visit(element: IrGetClass                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrInstanceInitializerCall        , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrLoop                           , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrWhileLoop                      , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrDoWhileLoop                    , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrReturn                         , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrStringConcatenation            , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSuspensionPoint                , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSuspendableExpression          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrThrow                          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrTry                            , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrCatch                          , data: D): IrCatch                    = element.transform(quick, data)
    fun visit(element: IrTypeOperatorCall               , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrValueAccessExpression          , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrGetValue                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSetValue                       , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrVararg                         , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrSpreadElement                  , data: D): IrSpreadElement            = element.transform(quick, data)
    fun visit(element: IrWhen                           , data: D): IrExpression               = element.transform(quick, data)
    fun visit(element: IrBranch                         , data: D): IrBranch                   = element.transform(quick, data)
    fun visit(element: IrElseBranch                     , data: D): IrElseBranch               = element.transform(quick, data)

    fun visitChildren(element: IrElement, data: D): Unit = element.transformChildren(quick, data)

}

fun <D> irTransformer(body: IrTransformerSpec<D>.() -> Unit): IrElementTransformer<D> = QuickIrTransformer.create(body)

@QuickPluginDsl
fun <D> IrModuleFragment.transform(data: D, body: IrTransformerSpec<D>.() -> Unit): IrModuleFragment {
    return irTransformer<D>(body).let { this.transform(it, data) }
}


