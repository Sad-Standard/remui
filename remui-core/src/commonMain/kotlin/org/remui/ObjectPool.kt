/*
 * Copyright Dynamic Animation Systems, Stronghold Robotics, and other original others.
 *
 * All rights reserved. This program and the accompanying materials are
 * made available under the terms of the Eclipse Public License v2.0 which
 * accompanies this distribution and is available at
 * https://www.eclipse.org/legal/epl-v20.html
 */

package org.remui

import kotlin.reflect.KClass

class ObjectPoolCategory<T: Any>

interface ObjectPoolBuilderSpec {
    fun <T: Any> include(category: ObjectPoolCategory<T>, objects: Iterable<T>)
}

interface ObjectModule {

    context(ObjectPoolBuilderSpec)
    fun construct()

    private data class Base(val builder: ObjectPoolBuilderSpec.() -> Unit): ObjectModule {

        context(ObjectPoolBuilderSpec)
        override fun construct() { builder() }

    }

    operator fun plus(right: ObjectModule): ObjectModule = Base {
        this@ObjectModule.construct()
        right.construct()
    }

    companion object {

        operator fun invoke(builder: ObjectPoolBuilderSpec.() -> Unit): ObjectModule = Base(builder)

        fun empty(): ObjectModule = object: ObjectModule {
            context(ObjectPoolBuilderSpec)
            override fun construct() { }
        }
    }
}



class ObjectPool private constructor(
    private val categories: Map<ObjectPoolCategory<*>, Pair<List<Any>, Map<Any, Int>>>
) {

    private operator fun <T: Any> get(category: ObjectPoolCategory<T>) =
        _getOrNull(category) ?: error("Category not present in pool")

    private fun <T: Any> _getOrNull(category: ObjectPoolCategory<T>) =
        categories[category] as Pair<List<T>, Map<T, Int>>?

    operator fun <T: Any> get(category: ObjectPoolCategory<T>, index: Int): T =
        this[category].first.getOrNull(index) ?: error("Index not found in category")

    operator fun <T: Any> get(category: ObjectPoolCategory<T>, obj: T): Int =
        this[category].second[obj] ?: error("Object ($obj) not found in category")

    fun <T: Any> getOrNull(category: ObjectPoolCategory<T>, obj: T): Int? =
        _getOrNull(category)?.second[obj]

    fun <T: Any> getAll(category: ObjectPoolCategory<T>): List<T> =
        this[category].first

    companion object {
        operator fun invoke(vararg modules: ObjectModule): ObjectPool {
            val categories = mutableMapOf<ObjectPoolCategory<*>, Pair<MutableList<Any>, MutableMap<Any, Int>>>()

            val builder = object: ObjectPoolBuilderSpec {
                override fun <T: Any> include(category: ObjectPoolCategory<T>, objects: Iterable<T>) {
                    val pair = categories.getOrPut(category){ Pair(mutableListOf(), mutableMapOf()) }
                    objects.forEach { obj ->
                        if(obj !in pair.second) {
                            pair.second[obj] = pair.first.size
                            pair.first += obj
                        }
                    }
                }
            }

            modules.forEach { module -> with(builder) { module.construct() } }

            return ObjectPool(categories)
        }
    }
}

val classKey = ObjectPoolCategory<KClass<*>>()

class ClassModule(
    private vararg val classes: KClass<*>
): ObjectModule {
    context(ObjectPoolBuilderSpec)
    override fun construct() {
        include(classKey, classes.asIterable())
    }
}