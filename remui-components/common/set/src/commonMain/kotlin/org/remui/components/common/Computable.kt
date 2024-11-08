/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

@file:Suppress("RemoveRedundantBackticks", "CONTEXT_RECEIVERS_DEPRECATED")

package org.remui.components.common

import org.remui.ObjectModule
import org.remui.ObjectPoolBuilderSpec
import org.remui.ObjectPoolCategory
import kotlinx.serialization.Contextual
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Polymorphic
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.jvm.JvmName
import kotlin.reflect.KType
import kotlin.reflect.typeOf

@Serializable
data class RV<T: Any>(
    private val direct      :              T?                   = null,
    private val computable  :              Computable.Internal? = null,
    private val directRemote: @Polymorphic RemoteValue<T>?      = null,
)
{

    val value: T get() = when {
        null != direct       -> direct
        null != computable   -> with(object: ComputableContext {}) { computable.value as T }
        null != directRemote -> directRemote.remValue
        else                 -> error("")
    }

    companion object {
        operator fun <T: Any> invoke(direct    : T             ): RV<T> = RV(direct       = direct             )
        operator fun <T: Any> invoke(computable: Computable<T> ): RV<T> = RV(computable   = computable.internal)
                 fun <T: Any> direct(remote    : RemoteValue<T>): RV<T> = RV(directRemote = remote             )
    }

}


interface ComputableContext {

}


object FakeKTypeSerializer: KSerializer<KType> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("KType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: KType) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): KType {
        TODO("Not yet implemented")
    }

}

object FakeAnySerializer: KSerializer<Any?> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("KType", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: Any?) {
        TODO("Not yet implemented")
    }

    override fun deserialize(decoder: Decoder): Any? {
        TODO("Not yet implemented")
    }

}

//@Serializable
//@Polymorphic
data class DynamicValue @PublishedApi internal constructor(
    @Serializable(with = FakeKTypeSerializer::class) val type : KType,
    @Serializable(with = FakeAnySerializer::class  ) val value: Any?
)

inline fun <reified T> dynamic(value: T): DynamicValue = DynamicValue(typeOf<T>(), value)

@Serializable
data class Computable<out T> constructor(@PublishedApi internal val internal: Internal) {

    context(ComputableContext)
    val value: T get() = internal.value as T

    @Serializable
    data class Internal @PublishedApi internal constructor(
        private val isNull: Boolean                           = false,
        private val constS: String?                           = null,
        private val constI: Int?                              = null,
        private val constL: Long?                             = null,
        private val constF: Float?                            = null,
        private val constD: Double?                           = null,
        private val constZ: Boolean?                          = null,
        private val constM: CFunction<Nothing>?               = null,
        private val const : @Contextual DynamicValue?         = null,
        private val remote: @Contextual RemoteValue<Nothing>? = null,
        private val use   : Internal?                         = null,
        private val call  : List<Internal>                    = listOf(),
    ) {

        context(ComputableContext)
        val value: Any? get() {
            return when {
                isNull            -> null
                null != constS    -> constS
                null != constI    -> constI
                null != constL    -> constL
                null != constF    -> constF
                null != constD    -> constD
                null != constZ    -> constZ
                null != constM    -> constM
                null != const     -> const.value
                null != remote    -> (remote as RemoteValue<Any?>).remValue
                null != use       -> (use.value as Computable<*>).internal.value
                call.isNotEmpty() -> (call[0].value as CFunction<Any?>).call(call.drop(1).map { object: Readable<Any?>{
                    override fun invoke(): Any? = it.value
                } })
                else -> error("")
            }
        }
    }

    companion object {
        fun const(value: String ): Computable<String>  = Computable(Internal(constS = value))
        fun const(value: Int    ): Computable<Int>     = Computable(Internal(constI = value))
        fun const(value: Long   ): Computable<Long>    = Computable(Internal(constL = value))
        fun const(value: Float  ): Computable<Float>   = Computable(Internal(constF = value))
        fun const(value: Double ): Computable<Double>  = Computable(Internal(constD = value))
        fun const(value: Boolean): Computable<Boolean> = Computable(Internal(constZ = value))
        fun <M: CFunction<*>> const(value: M): Computable<M> = Computable(Internal(constM = value as CFunction<Nothing>))

        inline fun <reified T> const(value: T): Computable<T> {
            return Computable(Internal(const = dynamic(value)))
        }

        fun <T> remote(value: RemoteValue<T>): Computable<T> {
            return Computable(Internal(remote = value as RemoteValue<Nothing>))
        }

        fun <T> use(value: Computable<T>): Computable<T> {
            return Computable(Internal(use = value.internal))
        }

        fun <R> call(op: Computable<CFunction<R>>, args: List<Computable<*>>): Computable<R> {
            return Computable(Internal(call = listOf(op.internal) + args.map { it.internal }))
        }
    }

//    @Serializable
//    sealed interface Internal {
//        context(ComputableContext)
//        val value: Any?
//    }
//
//    @Serializable
//    @PublishedApi
//    @SerialName("c")
//    internal data class Const(val given: @Contextual DynamicValue): Internal{
//        context(ComputableContext)
//        override val value: Any? get() = given.value
//    }
//
//    @Serializable
//    @SerialName("r")
//    data class Remote(val remote: @Contextual RemoteValue<Nothing>): Internal {
//
//        context(ComputableContext)
//        override val value get() = (remote as RemoteValue<Any?>).remValue
//
//    }
//
//    @Serializable
//    @SerialName("u")
//    data class Use(val ref: Internal): Internal {
//
//        context(ComputableContext)
//        override val value get() = (ref.value as Computable<*>).internal.value
//
//    }
//
//    @Serializable
//    @PublishedApi
//    @SerialName("fc")
//    internal data class FunctionCall(val op: Internal, val args: List<Internal>): Internal{
//
//        context(ComputableContext)
//        override val value: Any? get() = (op.value as CFunction<Any?>).call(args.map { object: Readable<Any?>{
//            override fun invoke(): Any? = it.value
//        } })
//    }
}
val String.c get() = Computable.const(this)
val Int.c get() = Computable.const(this)
val Long.c get() = Computable.const(this)
val Float.c get() = Computable.const(this)
val Double.c get() = Computable.const(this)
val Boolean.c get() = Computable.const(this)
inline val <reified T> T.c: Computable<T> get() = Computable.const(this)

interface Readable<T>{
    operator fun invoke(): T
}

@Polymorphic
interface CFunction<R> {
    fun call(args: List<Readable<*>>): R
}

class CFunction1<R, P0>(
    val func: (Readable<P0>) -> R
): CFunction<R> {

    override fun call(args: List<Readable<*>>): R = func(args[0] as Readable<P0>)

}

inline operator fun <reified C: CFunction1<R, P0>, R, P0> C.invoke(p0: Computable<P0>): Computable<R> = Computable.call(Computable.const(this), listOf(p0))

class CFunction2<R, P0, P1>(
    val func: (Readable<P0>, Readable<P1>) -> R
): CFunction<R> {

    override fun call(args: List<Readable<*>>): R = func(args[0] as Readable<P0>, args[1] as Readable<P1>)

}

inline operator fun <reified C: CFunction2<R, P0, P1>, R, P0, P1> C.invoke(p0: Computable<P0>, p1: Computable<P1>): Computable<R> = Computable.call(Computable.const(this), listOf(p0, p1))

class CFunction3<R, P0, P1, P2>(
    val func: (Readable<P0>, Readable<P1>, Readable<P2>) -> R
): CFunction<R> {

    override fun call(args: List<Readable<*>>): R = func(args[0] as Readable<P0>, args[1] as Readable<P1>, args[2] as Readable<P2>)

}

inline operator fun <reified C: CFunction3<R, P0, P1, P2>, R, P0, P1, P2> C.invoke(p0: Computable<P0>, p1: Computable<P1>, p2: Computable<P2>): Computable<R> = Computable.call(Computable.const(this), listOf(p0, p1, p2))

//class CFunction4<R, P0, P1, P2, P3>(
//    val func: (Readable<P0>, Readable<P1>, Readable<P2>, Readable<P3>) -> R
//): CFunction<R> {
//
//    override fun call(args: List<Readable<*>>): R = func(args[0] as Readable<P0>, args[1] as Readable<P1>, args[2] as Readable<P2>, args[3] as Readable<P3>)
//
//}
//
//class CFunction5<R, P0, P1, P2, P3, P4>(
//    val func: (Readable<P0>, Readable<P1>, Readable<P2>, Readable<P3>, Readable<P4>) -> R
//): CFunction<R> {
//
//    override fun call(args: List<Readable<*>>): R = func(args[0] as Readable<P0>, args[1] as Readable<P1>, args[2] as Readable<P2>, args[3] as Readable<P3>, args[4] as Readable<P4>)
//
//}

val functionCategory = ObjectPoolCategory<CFunction<*>>()

abstract class CFunctionGroup(): ObjectModule {
    @PublishedApi
    internal val _functions = mutableListOf<CFunction<*>>()

    context(ObjectPoolBuilderSpec)
    override fun construct() {
        include(functionCategory, _functions)
    }

    protected inline fun <R, P0> funcR(
        noinline func: (Readable<P0>) -> R
    ): CFunction1<R, P0> = CFunction1(func).also { _functions += it }

    protected inline fun <R, P0, P1> funcR(
        noinline func: (Readable<P0>, Readable<P1>) -> R
    ): CFunction2<R, P0, P1> = CFunction2(func).also { _functions += it }

    protected inline fun <R, P0, P1, P2> funcR(
        noinline func: (Readable<P0>, Readable<P1>, Readable<P2>) -> R
    ): CFunction3<R, P0, P1, P2> = CFunction3(func).also { _functions += it }

}

@Suppress("ObjectPropertyName")
object CBaseFunctions: CFunctionGroup() {

    val binaryBaB = funcR<_, Byte  , Byte  > { a, b -> a() + b() }
    val binaryBaS = funcR<_, Byte  , Short > { a, b -> a() + b() }
    val binaryBaI = funcR<_, Byte  , Int   > { a, b -> a() + b() }
    val binaryBaL = funcR<_, Byte  , Long  > { a, b -> a() + b() }
    val binaryBaF = funcR<_, Byte  , Float > { a, b -> a() + b() }
    val binaryBaD = funcR<_, Byte  , Double> { a, b -> a() + b() }

    val binarySaB = funcR<_, Short , Byte  > { a, b -> a() + b() }
    val binarySaS = funcR<_, Short , Short > { a, b -> a() + b() }
    val binarySaI = funcR<_, Short , Int   > { a, b -> a() + b() }
    val binarySaL = funcR<_, Short , Long  > { a, b -> a() + b() }
    val binarySaF = funcR<_, Short , Float > { a, b -> a() + b() }
    val binarySaD = funcR<_, Short , Double> { a, b -> a() + b() }

    val binaryIaB = funcR<_, Int   , Byte  > { a, b -> a() + b() }
    val binaryIaS = funcR<_, Int   , Short > { a, b -> a() + b() }
    val binaryIaI = funcR<_, Int   , Int   > { a, b -> a() + b() }
    val binaryIaL = funcR<_, Int   , Long  > { a, b -> a() + b() }
    val binaryIaF = funcR<_, Int   , Float > { a, b -> a() + b() }
    val binaryIaD = funcR<_, Int   , Double> { a, b -> a() + b() }

    val binaryLaB = funcR<_, Long  , Byte  > { a, b -> a() + b() }
    val binaryLaS = funcR<_, Long  , Short > { a, b -> a() + b() }
    val binaryLaI = funcR<_, Long  , Int   > { a, b -> a() + b() }
    val binaryLaL = funcR<_, Long  , Long  > { a, b -> a() + b() }
    val binaryLaF = funcR<_, Long  , Float > { a, b -> a() + b() }
    val binaryLaD = funcR<_, Long  , Double> { a, b -> a() + b() }

    val binaryFaB = funcR<_, Float , Byte  > { a, b -> a() + b() }
    val binaryFaS = funcR<_, Float , Short > { a, b -> a() + b() }
    val binaryFaI = funcR<_, Float , Int   > { a, b -> a() + b() }
    val binaryFaL = funcR<_, Float , Long  > { a, b -> a() + b() }
    val binaryFaF = funcR<_, Float , Float > { a, b -> a() + b() }
    val binaryFaD = funcR<_, Float , Double> { a, b -> a() + b() }

    val binaryDaB = funcR<_, Double, Byte  > { a, b -> a() + b() }
    val binaryDaS = funcR<_, Double, Short > { a, b -> a() + b() }
    val binaryDaI = funcR<_, Double, Int   > { a, b -> a() + b() }
    val binaryDaL = funcR<_, Double, Long  > { a, b -> a() + b() }
    val binaryDaF = funcR<_, Double, Float > { a, b -> a() + b() }
    val binaryDaD = funcR<_, Double, Double> { a, b -> a() + b() }



    val binaryBsB = funcR<_, Byte  , Byte  > { a, b -> a() - b() }
    val binaryBsS = funcR<_, Byte  , Short > { a, b -> a() - b() }
    val binaryBsI = funcR<_, Byte  , Int   > { a, b -> a() - b() }
    val binaryBsL = funcR<_, Byte  , Long  > { a, b -> a() - b() }
    val binaryBsF = funcR<_, Byte  , Float > { a, b -> a() - b() }
    val binaryBsD = funcR<_, Byte  , Double> { a, b -> a() - b() }

    val binarySsB = funcR<_, Short , Byte  > { a, b -> a() - b() }
    val binarySsS = funcR<_, Short , Short > { a, b -> a() - b() }
    val binarySsI = funcR<_, Short , Int   > { a, b -> a() - b() }
    val binarySsL = funcR<_, Short , Long  > { a, b -> a() - b() }
    val binarySsF = funcR<_, Short , Float > { a, b -> a() - b() }
    val binarySsD = funcR<_, Short , Double> { a, b -> a() - b() }

    val binaryIsB = funcR<_, Int   , Byte  > { a, b -> a() - b() }
    val binaryIsS = funcR<_, Int   , Short > { a, b -> a() - b() }
    val binaryIsI = funcR<_, Int   , Int   > { a, b -> a() - b() }
    val binaryIsL = funcR<_, Int   , Long  > { a, b -> a() - b() }
    val binaryIsF = funcR<_, Int   , Float > { a, b -> a() - b() }
    val binaryIsD = funcR<_, Int   , Double> { a, b -> a() - b() }

    val binaryLsB = funcR<_, Long  , Byte  > { a, b -> a() - b() }
    val binaryLsS = funcR<_, Long  , Short > { a, b -> a() - b() }
    val binaryLsI = funcR<_, Long  , Int   > { a, b -> a() - b() }
    val binaryLsL = funcR<_, Long  , Long  > { a, b -> a() - b() }
    val binaryLsF = funcR<_, Long  , Float > { a, b -> a() - b() }
    val binaryLsD = funcR<_, Long  , Double> { a, b -> a() - b() }

    val binaryFsB = funcR<_, Float , Byte  > { a, b -> a() - b() }
    val binaryFsS = funcR<_, Float , Short > { a, b -> a() - b() }
    val binaryFsI = funcR<_, Float , Int   > { a, b -> a() - b() }
    val binaryFsL = funcR<_, Float , Long  > { a, b -> a() - b() }
    val binaryFsF = funcR<_, Float , Float > { a, b -> a() - b() }
    val binaryFsD = funcR<_, Float , Double> { a, b -> a() - b() }

    val binaryDsB = funcR<_, Double, Byte  > { a, b -> a() - b() }
    val binaryDsS = funcR<_, Double, Short > { a, b -> a() - b() }
    val binaryDsI = funcR<_, Double, Int   > { a, b -> a() - b() }
    val binaryDsL = funcR<_, Double, Long  > { a, b -> a() - b() }
    val binaryDsF = funcR<_, Double, Float > { a, b -> a() - b() }
    val binaryDsD = funcR<_, Double, Double> { a, b -> a() - b() }



    val binaryBmB = funcR<_, Byte  , Byte  > { a, b -> a() * b() }
    val binaryBmS = funcR<_, Byte  , Short > { a, b -> a() * b() }
    val binaryBmI = funcR<_, Byte  , Int   > { a, b -> a() * b() }
    val binaryBmL = funcR<_, Byte  , Long  > { a, b -> a() * b() }
    val binaryBmF = funcR<_, Byte  , Float > { a, b -> a() * b() }
    val binaryBmD = funcR<_, Byte  , Double> { a, b -> a() * b() }

    val binarySmB = funcR<_, Short , Byte  > { a, b -> a() * b() }
    val binarySmS = funcR<_, Short , Short > { a, b -> a() * b() }
    val binarySmI = funcR<_, Short , Int   > { a, b -> a() * b() }
    val binarySmL = funcR<_, Short , Long  > { a, b -> a() * b() }
    val binarySmF = funcR<_, Short , Float > { a, b -> a() * b() }
    val binarySmD = funcR<_, Short , Double> { a, b -> a() * b() }

    val binaryImB = funcR<_, Int   , Byte  > { a, b -> a() * b() }
    val binaryImS = funcR<_, Int   , Short > { a, b -> a() * b() }
    val binaryImI = funcR<_, Int   , Int   > { a, b -> a() * b() }
    val binaryImL = funcR<_, Int   , Long  > { a, b -> a() * b() }
    val binaryImF = funcR<_, Int   , Float > { a, b -> a() * b() }
    val binaryImD = funcR<_, Int   , Double> { a, b -> a() * b() }

    val binaryLmB = funcR<_, Long  , Byte  > { a, b -> a() * b() }
    val binaryLmS = funcR<_, Long  , Short > { a, b -> a() * b() }
    val binaryLmI = funcR<_, Long  , Int   > { a, b -> a() * b() }
    val binaryLmL = funcR<_, Long  , Long  > { a, b -> a() * b() }
    val binaryLmF = funcR<_, Long  , Float > { a, b -> a() * b() }
    val binaryLmD = funcR<_, Long  , Double> { a, b -> a() * b() }

    val binaryFmB = funcR<_, Float , Byte  > { a, b -> a() * b() }
    val binaryFmS = funcR<_, Float , Short > { a, b -> a() * b() }
    val binaryFmI = funcR<_, Float , Int   > { a, b -> a() * b() }
    val binaryFmL = funcR<_, Float , Long  > { a, b -> a() * b() }
    val binaryFmF = funcR<_, Float , Float > { a, b -> a() * b() }
    val binaryFmD = funcR<_, Float , Double> { a, b -> a() * b() }

    val binaryDmB = funcR<_, Double, Byte  > { a, b -> a() * b() }
    val binaryDmS = funcR<_, Double, Short > { a, b -> a() * b() }
    val binaryDmI = funcR<_, Double, Int   > { a, b -> a() * b() }
    val binaryDmL = funcR<_, Double, Long  > { a, b -> a() * b() }
    val binaryDmF = funcR<_, Double, Float > { a, b -> a() * b() }
    val binaryDmD = funcR<_, Double, Double> { a, b -> a() * b() }



    val binaryBdB = funcR<_, Byte  , Byte  > { a, b -> a() / b() }
    val binaryBdS = funcR<_, Byte  , Short > { a, b -> a() / b() }
    val binaryBdI = funcR<_, Byte  , Int   > { a, b -> a() / b() }
    val binaryBdL = funcR<_, Byte  , Long  > { a, b -> a() / b() }
    val binaryBdF = funcR<_, Byte  , Float > { a, b -> a() / b() }
    val binaryBdD = funcR<_, Byte  , Double> { a, b -> a() / b() }

    val binarySdB = funcR<_, Short , Byte  > { a, b -> a() / b() }
    val binarySdS = funcR<_, Short , Short > { a, b -> a() / b() }
    val binarySdI = funcR<_, Short , Int   > { a, b -> a() / b() }
    val binarySdL = funcR<_, Short , Long  > { a, b -> a() / b() }
    val binarySdF = funcR<_, Short , Float > { a, b -> a() / b() }
    val binarySdD = funcR<_, Short , Double> { a, b -> a() / b() }

    val binaryIdB = funcR<_, Int   , Byte  > { a, b -> a() / b() }
    val binaryIdS = funcR<_, Int   , Short > { a, b -> a() / b() }
    val binaryIdI = funcR<_, Int   , Int   > { a, b -> a() / b() }
    val binaryIdL = funcR<_, Int   , Long  > { a, b -> a() / b() }
    val binaryIdF = funcR<_, Int   , Float > { a, b -> a() / b() }
    val binaryIdD = funcR<_, Int   , Double> { a, b -> a() / b() }

    val binaryLdB = funcR<_, Long  , Byte  > { a, b -> a() / b() }
    val binaryLdS = funcR<_, Long  , Short > { a, b -> a() / b() }
    val binaryLdI = funcR<_, Long  , Int   > { a, b -> a() / b() }
    val binaryLdL = funcR<_, Long  , Long  > { a, b -> a() / b() }
    val binaryLdF = funcR<_, Long  , Float > { a, b -> a() / b() }
    val binaryLdD = funcR<_, Long  , Double> { a, b -> a() / b() }

    val binaryFdB = funcR<_, Float , Byte  > { a, b -> a() / b() }
    val binaryFdS = funcR<_, Float , Short > { a, b -> a() / b() }
    val binaryFdI = funcR<_, Float , Int   > { a, b -> a() / b() }
    val binaryFdL = funcR<_, Float , Long  > { a, b -> a() / b() }
    val binaryFdF = funcR<_, Float , Float > { a, b -> a() / b() }
    val binaryFdD = funcR<_, Float , Double> { a, b -> a() / b() }

    val binaryDdB = funcR<_, Double, Byte  > { a, b -> a() / b() }
    val binaryDdS = funcR<_, Double, Short > { a, b -> a() / b() }
    val binaryDdI = funcR<_, Double, Int   > { a, b -> a() / b() }
    val binaryDdL = funcR<_, Double, Long  > { a, b -> a() / b() }
    val binaryDdF = funcR<_, Double, Float > { a, b -> a() / b() }
    val binaryDdD = funcR<_, Double, Double> { a, b -> a() / b() }



    val binaryBrB = funcR<_, Byte  , Byte  > { a, b -> a() % b() }
    val binaryBrS = funcR<_, Byte  , Short > { a, b -> a() % b() }
    val binaryBrI = funcR<_, Byte  , Int   > { a, b -> a() % b() }
    val binaryBrL = funcR<_, Byte  , Long  > { a, b -> a() % b() }
    val binaryBrF = funcR<_, Byte  , Float > { a, b -> a() % b() }
    val binaryBrD = funcR<_, Byte  , Double> { a, b -> a() % b() }

    val binarySrB = funcR<_, Short , Byte  > { a, b -> a() % b() }
    val binarySrS = funcR<_, Short , Short > { a, b -> a() % b() }
    val binarySrI = funcR<_, Short , Int   > { a, b -> a() % b() }
    val binarySrL = funcR<_, Short , Long  > { a, b -> a() % b() }
    val binarySrF = funcR<_, Short , Float > { a, b -> a() % b() }
    val binarySrD = funcR<_, Short , Double> { a, b -> a() % b() }

    val binaryIrB = funcR<_, Int   , Byte  > { a, b -> a() % b() }
    val binaryIrS = funcR<_, Int   , Short > { a, b -> a() % b() }
    val binaryIrI = funcR<_, Int   , Int   > { a, b -> a() % b() }
    val binaryIrL = funcR<_, Int   , Long  > { a, b -> a() % b() }
    val binaryIrF = funcR<_, Int   , Float > { a, b -> a() % b() }
    val binaryIrD = funcR<_, Int   , Double> { a, b -> a() % b() }

    val binaryLrB = funcR<_, Long  , Byte  > { a, b -> a() % b() }
    val binaryLrS = funcR<_, Long  , Short > { a, b -> a() % b() }
    val binaryLrI = funcR<_, Long  , Int   > { a, b -> a() % b() }
    val binaryLrL = funcR<_, Long  , Long  > { a, b -> a() % b() }
    val binaryLrF = funcR<_, Long  , Float > { a, b -> a() % b() }
    val binaryLrD = funcR<_, Long  , Double> { a, b -> a() % b() }

    val binaryFrB = funcR<_, Float , Byte  > { a, b -> a() % b() }
    val binaryFrS = funcR<_, Float , Short > { a, b -> a() % b() }
    val binaryFrI = funcR<_, Float , Int   > { a, b -> a() % b() }
    val binaryFrL = funcR<_, Float , Long  > { a, b -> a() % b() }
    val binaryFrF = funcR<_, Float , Float > { a, b -> a() % b() }
    val binaryFrD = funcR<_, Float , Double> { a, b -> a() % b() }

    val binaryDrB = funcR<_, Double, Byte  > { a, b -> a() % b() }
    val binaryDrS = funcR<_, Double, Short > { a, b -> a() % b() }
    val binaryDrI = funcR<_, Double, Int   > { a, b -> a() % b() }
    val binaryDrL = funcR<_, Double, Long  > { a, b -> a() % b() }
    val binaryDrF = funcR<_, Double, Float > { a, b -> a() % b() }
    val binaryDrD = funcR<_, Double, Double> { a, b -> a() % b() }

    val negateB = funcR<_, Byte  > { a -> -a() }
    val negateS = funcR<_, Short > { a -> -a() }
    val negateI = funcR<_, Int   > { a -> -a() }
    val negateL = funcR<_, Long  > { a -> -a() }
    val negateF = funcR<_, Float > { a -> -a() }
    val negateD = funcR<_, Double> { a -> -a() }


    val `toB` = funcR<_, Number>{ it().toByte()   }
    val `toS` = funcR<_, Number>{ it().toShort()  }
    val `toI` = funcR<_, Number>{ it().toInt()    }
    val `toL` = funcR<_, Number>{ it().toLong()   }
    val `toF` = funcR<_, Number>{ it().toFloat()  }
    val `toD` = funcR<_, Number>{ it().toDouble() }


    val `asString` = funcR<_, Any?> { it().toString() }

    val `lAsString` = funcR<_, Long, Int> { l, r -> l().toString(radix = r()) }
    val `iAsString` = funcR<_, Int , Int> { i, r -> i().toString(radix = r()) }
}

@JvmName("binaryBaB") operator fun Computable<Byte  >.plus(right: Computable<Byte  >) = CBaseFunctions.binaryBaB(this, right)
//@JvmName("binaryBaS") operator fun Computable<Byte  >.plus(right: Computable<Short >) = CBaseFunctions.binaryBaS(this, right)
//@JvmName("binaryBaI") operator fun Computable<Byte  >.plus(right: Computable<Int   >) = CBaseFunctions.binaryBaI(this, right)
//@JvmName("binaryBaL") operator fun Computable<Byte  >.plus(right: Computable<Long  >) = CBaseFunctions.binaryBaL(this, right)
//@JvmName("binaryBaF") operator fun Computable<Byte  >.plus(right: Computable<Float >) = CBaseFunctions.binaryBaF(this, right)
//@JvmName("binaryBaD") operator fun Computable<Byte  >.plus(right: Computable<Double>) = CBaseFunctions.binaryBaD(this, right)

//@JvmName("binarySaB") operator fun Computable<Short >.plus(right: Computable<Byte  >) = CBaseFunctions.binarySaB(this, right)
@JvmName("binarySaS") operator fun Computable<Short >.plus(right: Computable<Short >) = CBaseFunctions.binarySaS(this, right)
//@JvmName("binarySaI") operator fun Computable<Short >.plus(right: Computable<Int   >) = CBaseFunctions.binarySaI(this, right)
//@JvmName("binarySaL") operator fun Computable<Short >.plus(right: Computable<Long  >) = CBaseFunctions.binarySaL(this, right)
//@JvmName("binarySaF") operator fun Computable<Short >.plus(right: Computable<Float >) = CBaseFunctions.binarySaF(this, right)
//@JvmName("binarySaD") operator fun Computable<Short >.plus(right: Computable<Double>) = CBaseFunctions.binarySaD(this, right)

//@JvmName("binaryIaB") operator fun Computable<Int   >.plus(right: Computable<Byte  >) = CBaseFunctions.binaryIaB(this, right)
//@JvmName("binaryIaS") operator fun Computable<Int   >.plus(right: Computable<Short >) = CBaseFunctions.binaryIaS(this, right)
@JvmName("binaryIaI") operator fun Computable<Int   >.plus(right: Computable<Int   >) = CBaseFunctions.binaryIaI(this, right)
//@JvmName("binaryIaL") operator fun Computable<Int   >.plus(right: Computable<Long  >) = CBaseFunctions.binaryIaL(this, right)
//@JvmName("binaryIaF") operator fun Computable<Int   >.plus(right: Computable<Float >) = CBaseFunctions.binaryIaF(this, right)
//@JvmName("binaryIaD") operator fun Computable<Int   >.plus(right: Computable<Double>) = CBaseFunctions.binaryIaD(this, right)

//@JvmName("binaryLaB") operator fun Computable<Long  >.plus(right: Computable<Byte  >) = CBaseFunctions.binaryLaB(this, right)
//@JvmName("binaryLaS") operator fun Computable<Long  >.plus(right: Computable<Short >) = CBaseFunctions.binaryLaS(this, right)
//@JvmName("binaryLaI") operator fun Computable<Long  >.plus(right: Computable<Int   >) = CBaseFunctions.binaryLaI(this, right)
@JvmName("binaryLaL") operator fun Computable<Long  >.plus(right: Computable<Long  >) = CBaseFunctions.binaryLaL(this, right)
//@JvmName("binaryLaF") operator fun Computable<Long  >.plus(right: Computable<Float >) = CBaseFunctions.binaryLaF(this, right)
//@JvmName("binaryLaD") operator fun Computable<Long  >.plus(right: Computable<Double>) = CBaseFunctions.binaryLaD(this, right)

//@JvmName("binaryFaB") operator fun Computable<Float >.plus(right: Computable<Byte  >) = CBaseFunctions.binaryFaB(this, right)
//@JvmName("binaryFaS") operator fun Computable<Float >.plus(right: Computable<Short >) = CBaseFunctions.binaryFaS(this, right)
//@JvmName("binaryFaI") operator fun Computable<Float >.plus(right: Computable<Int   >) = CBaseFunctions.binaryFaI(this, right)
//@JvmName("binaryFaL") operator fun Computable<Float >.plus(right: Computable<Long  >) = CBaseFunctions.binaryFaL(this, right)
@JvmName("binaryFaF") operator fun Computable<Float >.plus(right: Computable<Float >) = CBaseFunctions.binaryFaF(this, right)
//@JvmName("binaryFaD") operator fun Computable<Float >.plus(right: Computable<Double>) = CBaseFunctions.binaryFaD(this, right)

//@JvmName("binaryDaB") operator fun Computable<Double>.plus(right: Computable<Byte  >) = CBaseFunctions.binaryDaB(this, right)
//@JvmName("binaryDaS") operator fun Computable<Double>.plus(right: Computable<Short >) = CBaseFunctions.binaryDaS(this, right)
//@JvmName("binaryDaI") operator fun Computable<Double>.plus(right: Computable<Int   >) = CBaseFunctions.binaryDaI(this, right)
//@JvmName("binaryDaL") operator fun Computable<Double>.plus(right: Computable<Long  >) = CBaseFunctions.binaryDaL(this, right)
//@JvmName("binaryDaF") operator fun Computable<Double>.plus(right: Computable<Float >) = CBaseFunctions.binaryDaF(this, right)
@JvmName("binaryDaD") operator fun Computable<Double>.plus(right: Computable<Double>) = CBaseFunctions.binaryDaD(this, right)



@JvmName("binaryBsB") operator fun Computable<Byte  >.minus(right: Computable<Byte  >) = CBaseFunctions.binaryBsB(this, right)
//@JvmName("binaryBsS") operator fun Computable<Byte  >.minus(right: Computable<Short >) = CBaseFunctions.binaryBsS(this, right)
//@JvmName("binaryBsI") operator fun Computable<Byte  >.minus(right: Computable<Int   >) = CBaseFunctions.binaryBsI(this, right)
//@JvmName("binaryBsL") operator fun Computable<Byte  >.minus(right: Computable<Long  >) = CBaseFunctions.binaryBsL(this, right)
//@JvmName("binaryBsF") operator fun Computable<Byte  >.minus(right: Computable<Float >) = CBaseFunctions.binaryBsF(this, right)
//@JvmName("binaryBsD") operator fun Computable<Byte  >.minus(right: Computable<Double>) = CBaseFunctions.binaryBsD(this, right)

//@JvmName("binarySsB") operator fun Computable<Short >.minus(right: Computable<Byte  >) = CBaseFunctions.binarySsB(this, right)
@JvmName("binarySsS") operator fun Computable<Short >.minus(right: Computable<Short >) = CBaseFunctions.binarySsS(this, right)
//@JvmName("binarySsI") operator fun Computable<Short >.minus(right: Computable<Int   >) = CBaseFunctions.binarySsI(this, right)
//@JvmName("binarySsL") operator fun Computable<Short >.minus(right: Computable<Long  >) = CBaseFunctions.binarySsL(this, right)
//@JvmName("binarySsF") operator fun Computable<Short >.minus(right: Computable<Float >) = CBaseFunctions.binarySsF(this, right)
//@JvmName("binarySsD") operator fun Computable<Short >.minus(right: Computable<Double>) = CBaseFunctions.binarySsD(this, right)

//@JvmName("binaryIsB") operator fun Computable<Int   >.minus(right: Computable<Byte  >) = CBaseFunctions.binaryIsB(this, right)
//@JvmName("binaryIsS") operator fun Computable<Int   >.minus(right: Computable<Short >) = CBaseFunctions.binaryIsS(this, right)
@JvmName("binaryIsI") operator fun Computable<Int   >.minus(right: Computable<Int   >) = CBaseFunctions.binaryIsI(this, right)
//@JvmName("binaryIsL") operator fun Computable<Int   >.minus(right: Computable<Long  >) = CBaseFunctions.binaryIsL(this, right)
//@JvmName("binaryIsF") operator fun Computable<Int   >.minus(right: Computable<Float >) = CBaseFunctions.binaryIsF(this, right)
//@JvmName("binaryIsD") operator fun Computable<Int   >.minus(right: Computable<Double>) = CBaseFunctions.binaryIsD(this, right)

//@JvmName("binaryLsB") operator fun Computable<Long  >.minus(right: Computable<Byte  >) = CBaseFunctions.binaryLsB(this, right)
//@JvmName("binaryLsS") operator fun Computable<Long  >.minus(right: Computable<Short >) = CBaseFunctions.binaryLsS(this, right)
//@JvmName("binaryLsI") operator fun Computable<Long  >.minus(right: Computable<Int   >) = CBaseFunctions.binaryLsI(this, right)
@JvmName("binaryLsL") operator fun Computable<Long  >.minus(right: Computable<Long  >) = CBaseFunctions.binaryLsL(this, right)
//@JvmName("binaryLsF") operator fun Computable<Long  >.minus(right: Computable<Float >) = CBaseFunctions.binaryLsF(this, right)
//@JvmName("binaryLsD") operator fun Computable<Long  >.minus(right: Computable<Double>) = CBaseFunctions.binaryLsD(this, right)

//@JvmName("binaryFsB") operator fun Computable<Float >.minus(right: Computable<Byte  >) = CBaseFunctions.binaryFsB(this, right)
//@JvmName("binaryFsS") operator fun Computable<Float >.minus(right: Computable<Short >) = CBaseFunctions.binaryFsS(this, right)
//@JvmName("binaryFsI") operator fun Computable<Float >.minus(right: Computable<Int   >) = CBaseFunctions.binaryFsI(this, right)
//@JvmName("binaryFsL") operator fun Computable<Float >.minus(right: Computable<Long  >) = CBaseFunctions.binaryFsL(this, right)
@JvmName("binaryFsF") operator fun Computable<Float >.minus(right: Computable<Float >) = CBaseFunctions.binaryFsF(this, right)
//@JvmName("binaryFsD") operator fun Computable<Float >.minus(right: Computable<Double>) = CBaseFunctions.binaryFsD(this, right)

//@JvmName("binaryDsB") operator fun Computable<Double>.minus(right: Computable<Byte  >) = CBaseFunctions.binaryDsB(this, right)
//@JvmName("binaryDsS") operator fun Computable<Double>.minus(right: Computable<Short >) = CBaseFunctions.binaryDsS(this, right)
//@JvmName("binaryDsI") operator fun Computable<Double>.minus(right: Computable<Int   >) = CBaseFunctions.binaryDsI(this, right)
//@JvmName("binaryDsL") operator fun Computable<Double>.minus(right: Computable<Long  >) = CBaseFunctions.binaryDsL(this, right)
//@JvmName("binaryDsF") operator fun Computable<Double>.minus(right: Computable<Float >) = CBaseFunctions.binaryDsF(this, right)
@JvmName("binaryDsD") operator fun Computable<Double>.minus(right: Computable<Double>) = CBaseFunctions.binaryDsD(this, right)



@JvmName("binaryBmB") operator fun Computable<Byte  >.times(right: Computable<Byte  >) = CBaseFunctions.binaryBmB(this, right)
//@JvmName("binaryBmS") operator fun Computable<Byte  >.times(right: Computable<Short >) = CBaseFunctions.binaryBmS(this, right)
//@JvmName("binaryBmI") operator fun Computable<Byte  >.times(right: Computable<Int   >) = CBaseFunctions.binaryBmI(this, right)
//@JvmName("binaryBmL") operator fun Computable<Byte  >.times(right: Computable<Long  >) = CBaseFunctions.binaryBmL(this, right)
//@JvmName("binaryBmF") operator fun Computable<Byte  >.times(right: Computable<Float >) = CBaseFunctions.binaryBmF(this, right)
//@JvmName("binaryBmD") operator fun Computable<Byte  >.times(right: Computable<Double>) = CBaseFunctions.binaryBmD(this, right)

//@JvmName("binarySmB") operator fun Computable<Short >.times(right: Computable<Byte  >) = CBaseFunctions.binarySmB(this, right)
@JvmName("binarySmS") operator fun Computable<Short >.times(right: Computable<Short >) = CBaseFunctions.binarySmS(this, right)
//@JvmName("binarySmI") operator fun Computable<Short >.times(right: Computable<Int   >) = CBaseFunctions.binarySmI(this, right)
//@JvmName("binarySmL") operator fun Computable<Short >.times(right: Computable<Long  >) = CBaseFunctions.binarySmL(this, right)
//@JvmName("binarySmF") operator fun Computable<Short >.times(right: Computable<Float >) = CBaseFunctions.binarySmF(this, right)
//@JvmName("binarySmD") operator fun Computable<Short >.times(right: Computable<Double>) = CBaseFunctions.binarySmD(this, right)

//@JvmName("binaryImB") operator fun Computable<Int   >.times(right: Computable<Byte  >) = CBaseFunctions.binaryImB(this, right)
//@JvmName("binaryImS") operator fun Computable<Int   >.times(right: Computable<Short >) = CBaseFunctions.binaryImS(this, right)
@JvmName("binaryImI") operator fun Computable<Int   >.times(right: Computable<Int   >) = CBaseFunctions.binaryImI(this, right)
//@JvmName("binaryImL") operator fun Computable<Int   >.times(right: Computable<Long  >) = CBaseFunctions.binaryImL(this, right)
//@JvmName("binaryImF") operator fun Computable<Int   >.times(right: Computable<Float >) = CBaseFunctions.binaryImF(this, right)
//@JvmName("binaryImD") operator fun Computable<Int   >.times(right: Computable<Double>) = CBaseFunctions.binaryImD(this, right)

//@JvmName("binaryLmB") operator fun Computable<Long  >.times(right: Computable<Byte  >) = CBaseFunctions.binaryLmB(this, right)
//@JvmName("binaryLmS") operator fun Computable<Long  >.times(right: Computable<Short >) = CBaseFunctions.binaryLmS(this, right)
//@JvmName("binaryLmI") operator fun Computable<Long  >.times(right: Computable<Int   >) = CBaseFunctions.binaryLmI(this, right)
@JvmName("binaryLmL") operator fun Computable<Long  >.times(right: Computable<Long  >) = CBaseFunctions.binaryLmL(this, right)
//@JvmName("binaryLmF") operator fun Computable<Long  >.times(right: Computable<Float >) = CBaseFunctions.binaryLmF(this, right)
//@JvmName("binaryLmD") operator fun Computable<Long  >.times(right: Computable<Double>) = CBaseFunctions.binaryLmD(this, right)

//@JvmName("binaryFmB") operator fun Computable<Float >.times(right: Computable<Byte  >) = CBaseFunctions.binaryFmB(this, right)
//@JvmName("binaryFmS") operator fun Computable<Float >.times(right: Computable<Short >) = CBaseFunctions.binaryFmS(this, right)
//@JvmName("binaryFmI") operator fun Computable<Float >.times(right: Computable<Int   >) = CBaseFunctions.binaryFmI(this, right)
//@JvmName("binaryFmL") operator fun Computable<Float >.times(right: Computable<Long  >) = CBaseFunctions.binaryFmL(this, right)
@JvmName("binaryFmF") operator fun Computable<Float >.times(right: Computable<Float >) = CBaseFunctions.binaryFmF(this, right)
//@JvmName("binaryFmD") operator fun Computable<Float >.times(right: Computable<Double>) = CBaseFunctions.binaryFmD(this, right)

//@JvmName("binaryDmB") operator fun Computable<Double>.times(right: Computable<Byte  >) = CBaseFunctions.binaryDmB(this, right)
//@JvmName("binaryDmS") operator fun Computable<Double>.times(right: Computable<Short >) = CBaseFunctions.binaryDmS(this, right)
//@JvmName("binaryDmI") operator fun Computable<Double>.times(right: Computable<Int   >) = CBaseFunctions.binaryDmI(this, right)
//@JvmName("binaryDmL") operator fun Computable<Double>.times(right: Computable<Long  >) = CBaseFunctions.binaryDmL(this, right)
//@JvmName("binaryDmF") operator fun Computable<Double>.times(right: Computable<Float >) = CBaseFunctions.binaryDmF(this, right)
@JvmName("binaryDmD") operator fun Computable<Double>.times(right: Computable<Double>) = CBaseFunctions.binaryDmD(this, right)



@JvmName("binaryBdB") operator fun Computable<Byte  >.div(right: Computable<Byte  >) = CBaseFunctions.binaryBdB(this, right)
//@JvmName("binaryBdS") operator fun Computable<Byte  >.div(right: Computable<Short >) = CBaseFunctions.binaryBdS(this, right)
//@JvmName("binaryBdI") operator fun Computable<Byte  >.div(right: Computable<Int   >) = CBaseFunctions.binaryBdI(this, right)
//@JvmName("binaryBdL") operator fun Computable<Byte  >.div(right: Computable<Long  >) = CBaseFunctions.binaryBdL(this, right)
//@JvmName("binaryBdF") operator fun Computable<Byte  >.div(right: Computable<Float >) = CBaseFunctions.binaryBdF(this, right)
//@JvmName("binaryBdD") operator fun Computable<Byte  >.div(right: Computable<Double>) = CBaseFunctions.binaryBdD(this, right)

//@JvmName("binarySdB") operator fun Computable<Short >.div(right: Computable<Byte  >) = CBaseFunctions.binarySdB(this, right)
@JvmName("binarySdS") operator fun Computable<Short >.div(right: Computable<Short >) = CBaseFunctions.binarySdS(this, right)
//@JvmName("binarySdI") operator fun Computable<Short >.div(right: Computable<Int   >) = CBaseFunctions.binarySdI(this, right)
//@JvmName("binarySdL") operator fun Computable<Short >.div(right: Computable<Long  >) = CBaseFunctions.binarySdL(this, right)
//@JvmName("binarySdF") operator fun Computable<Short >.div(right: Computable<Float >) = CBaseFunctions.binarySdF(this, right)
//@JvmName("binarySdD") operator fun Computable<Short >.div(right: Computable<Double>) = CBaseFunctions.binarySdD(this, right)

//@JvmName("binaryIdB") operator fun Computable<Int   >.div(right: Computable<Byte  >) = CBaseFunctions.binaryIdB(this, right)
//@JvmName("binaryIdS") operator fun Computable<Int   >.div(right: Computable<Short >) = CBaseFunctions.binaryIdS(this, right)
@JvmName("binaryIdI") operator fun Computable<Int   >.div(right: Computable<Int   >) = CBaseFunctions.binaryIdI(this, right)
//@JvmName("binaryIdL") operator fun Computable<Int   >.div(right: Computable<Long  >) = CBaseFunctions.binaryIdL(this, right)
//@JvmName("binaryIdF") operator fun Computable<Int   >.div(right: Computable<Float >) = CBaseFunctions.binaryIdF(this, right)
//@JvmName("binaryIdD") operator fun Computable<Int   >.div(right: Computable<Double>) = CBaseFunctions.binaryIdD(this, right)

//@JvmName("binaryLdB") operator fun Computable<Long  >.div(right: Computable<Byte  >) = CBaseFunctions.binaryLdB(this, right)
//@JvmName("binaryLdS") operator fun Computable<Long  >.div(right: Computable<Short >) = CBaseFunctions.binaryLdS(this, right)
//@JvmName("binaryLdI") operator fun Computable<Long  >.div(right: Computable<Int   >) = CBaseFunctions.binaryLdI(this, right)
@JvmName("binaryLdL") operator fun Computable<Long  >.div(right: Computable<Long  >) = CBaseFunctions.binaryLdL(this, right)
//@JvmName("binaryLdF") operator fun Computable<Long  >.div(right: Computable<Float >) = CBaseFunctions.binaryLdF(this, right)
//@JvmName("binaryLdD") operator fun Computable<Long  >.div(right: Computable<Double>) = CBaseFunctions.binaryLdD(this, right)

//@JvmName("binaryFdB") operator fun Computable<Float >.div(right: Computable<Byte  >) = CBaseFunctions.binaryFdB(this, right)
//@JvmName("binaryFdS") operator fun Computable<Float >.div(right: Computable<Short >) = CBaseFunctions.binaryFdS(this, right)
//@JvmName("binaryFdI") operator fun Computable<Float >.div(right: Computable<Int   >) = CBaseFunctions.binaryFdI(this, right)
//@JvmName("binaryFdL") operator fun Computable<Float >.div(right: Computable<Long  >) = CBaseFunctions.binaryFdL(this, right)
@JvmName("binaryFdF") operator fun Computable<Float >.div(right: Computable<Float >) = CBaseFunctions.binaryFdF(this, right)
//@JvmName("binaryFdD") operator fun Computable<Float >.div(right: Computable<Double>) = CBaseFunctions.binaryFdD(this, right)

//@JvmName("binaryDdB") operator fun Computable<Double>.div(right: Computable<Byte  >) = CBaseFunctions.binaryDdB(this, right)
//@JvmName("binaryDdS") operator fun Computable<Double>.div(right: Computable<Short >) = CBaseFunctions.binaryDdS(this, right)
//@JvmName("binaryDdI") operator fun Computable<Double>.div(right: Computable<Int   >) = CBaseFunctions.binaryDdI(this, right)
//@JvmName("binaryDdL") operator fun Computable<Double>.div(right: Computable<Long  >) = CBaseFunctions.binaryDdL(this, right)
//@JvmName("binaryDdF") operator fun Computable<Double>.div(right: Computable<Float >) = CBaseFunctions.binaryDdF(this, right)
@JvmName("binaryDdD") operator fun Computable<Double>.div(right: Computable<Double>) = CBaseFunctions.binaryDdD(this, right)



@JvmName("binaryBrB") operator fun Computable<Byte  >.rem(right: Computable<Byte  >) = CBaseFunctions.binaryBrB(this, right)
//@JvmName("binaryBrS") operator fun Computable<Byte  >.rem(right: Computable<Short >) = CBaseFunctions.binaryBrS(this, right)
//@JvmName("binaryBrI") operator fun Computable<Byte  >.rem(right: Computable<Int   >) = CBaseFunctions.binaryBrI(this, right)
//@JvmName("binaryBrL") operator fun Computable<Byte  >.rem(right: Computable<Long  >) = CBaseFunctions.binaryBrL(this, right)
//@JvmName("binaryBrF") operator fun Computable<Byte  >.rem(right: Computable<Float >) = CBaseFunctions.binaryBrF(this, right)
//@JvmName("binaryBrD") operator fun Computable<Byte  >.rem(right: Computable<Double>) = CBaseFunctions.binaryBrD(this, right)

//@JvmName("binarySrB") operator fun Computable<Short >.rem(right: Computable<Byte  >) = CBaseFunctions.binarySrB(this, right)
@JvmName("binarySrS") operator fun Computable<Short >.rem(right: Computable<Short >) = CBaseFunctions.binarySrS(this, right)
//@JvmName("binarySrI") operator fun Computable<Short >.rem(right: Computable<Int   >) = CBaseFunctions.binarySrI(this, right)
//@JvmName("binarySrL") operator fun Computable<Short >.rem(right: Computable<Long  >) = CBaseFunctions.binarySrL(this, right)
//@JvmName("binarySrF") operator fun Computable<Short >.rem(right: Computable<Float >) = CBaseFunctions.binarySrF(this, right)
//@JvmName("binarySrD") operator fun Computable<Short >.rem(right: Computable<Double>) = CBaseFunctions.binarySrD(this, right)

//@JvmName("binaryIrB") operator fun Computable<Int   >.rem(right: Computable<Byte  >) = CBaseFunctions.binaryIrB(this, right)
//@JvmName("binaryIrS") operator fun Computable<Int   >.rem(right: Computable<Short >) = CBaseFunctions.binaryIrS(this, right)
@JvmName("binaryIrI") operator fun Computable<Int   >.rem(right: Computable<Int   >) = CBaseFunctions.binaryIrI(this, right)
//@JvmName("binaryIrL") operator fun Computable<Int   >.rem(right: Computable<Long  >) = CBaseFunctions.binaryIrL(this, right)
//@JvmName("binaryIrF") operator fun Computable<Int   >.rem(right: Computable<Float >) = CBaseFunctions.binaryIrF(this, right)
//@JvmName("binaryIrD") operator fun Computable<Int   >.rem(right: Computable<Double>) = CBaseFunctions.binaryIrD(this, right)

//@JvmName("binaryLrB") operator fun Computable<Long  >.rem(right: Computable<Byte  >) = CBaseFunctions.binaryLrB(this, right)
//@JvmName("binaryLrS") operator fun Computable<Long  >.rem(right: Computable<Short >) = CBaseFunctions.binaryLrS(this, right)
//@JvmName("binaryLrI") operator fun Computable<Long  >.rem(right: Computable<Int   >) = CBaseFunctions.binaryLrI(this, right)
@JvmName("binaryLrL") operator fun Computable<Long  >.rem(right: Computable<Long  >) = CBaseFunctions.binaryLrL(this, right)
//@JvmName("binaryLrF") operator fun Computable<Long  >.rem(right: Computable<Float >) = CBaseFunctions.binaryLrF(this, right)
//@JvmName("binaryLrD") operator fun Computable<Long  >.rem(right: Computable<Double>) = CBaseFunctions.binaryLrD(this, right)

//@JvmName("binaryFrB") operator fun Computable<Float >.rem(right: Computable<Byte  >) = CBaseFunctions.binaryFrB(this, right)
//@JvmName("binaryFrS") operator fun Computable<Float >.rem(right: Computable<Short >) = CBaseFunctions.binaryFrS(this, right)
//@JvmName("binaryFrI") operator fun Computable<Float >.rem(right: Computable<Int   >) = CBaseFunctions.binaryFrI(this, right)
//@JvmName("binaryFrL") operator fun Computable<Float >.rem(right: Computable<Long  >) = CBaseFunctions.binaryFrL(this, right)
@JvmName("binaryFrF") operator fun Computable<Float >.rem(right: Computable<Float >) = CBaseFunctions.binaryFrF(this, right)
//@JvmName("binaryFrD") operator fun Computable<Float >.rem(right: Computable<Double>) = CBaseFunctions.binaryFrD(this, right)

//@JvmName("binaryDrB") operator fun Computable<Double>.rem(right: Computable<Byte  >) = CBaseFunctions.binaryDrB(this, right)
//@JvmName("binaryDrS") operator fun Computable<Double>.rem(right: Computable<Short >) = CBaseFunctions.binaryDrS(this, right)
//@JvmName("binaryDrI") operator fun Computable<Double>.rem(right: Computable<Int   >) = CBaseFunctions.binaryDrI(this, right)
//@JvmName("binaryDrL") operator fun Computable<Double>.rem(right: Computable<Long  >) = CBaseFunctions.binaryDrL(this, right)
//@JvmName("binaryDrF") operator fun Computable<Double>.rem(right: Computable<Float >) = CBaseFunctions.binaryDrF(this, right)
@JvmName("binaryDrD") operator fun Computable<Double>.rem(right: Computable<Double>) = CBaseFunctions.binaryDrD(this, right)

@JvmName("negateB") operator fun Computable<Byte  >.unaryMinus() = CBaseFunctions.negateB(this)
@JvmName("negateS") operator fun Computable<Short >.unaryMinus() = CBaseFunctions.negateS(this)
@JvmName("negateI") operator fun Computable<Int   >.unaryMinus() = CBaseFunctions.negateI(this)
@JvmName("negateL") operator fun Computable<Long  >.unaryMinus() = CBaseFunctions.negateL(this)
@JvmName("negateF") operator fun Computable<Float >.unaryMinus() = CBaseFunctions.negateF(this)
@JvmName("negateD") operator fun Computable<Double>.unaryMinus() = CBaseFunctions.negateD(this)

fun Computable<Number>.toByte  () = CBaseFunctions.`toB`(this)
fun Computable<Number>.toShort () = CBaseFunctions.`toS`(this)
fun Computable<Number>.toInt   () = CBaseFunctions.`toI`(this)
fun Computable<Number>.toLong  () = CBaseFunctions.`toL`(this)
fun Computable<Number>.toFloat () = CBaseFunctions.`toF`(this)
fun Computable<Number>.toDouble() = CBaseFunctions.`toD`(this)

fun Computable<Any?>  .asString() = CBaseFunctions.asString(this)

fun Computable<Long>  .asString(radix: Computable<Int>) = CBaseFunctions.lAsString(this, radix)

//fun Computable<Any?>  .asString() = CBaseFunctions.asString(this)

fun <T> Computable<Computable<T>>.unwrap() = Computable.use(this)


