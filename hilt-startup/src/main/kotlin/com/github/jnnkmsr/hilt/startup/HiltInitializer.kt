/*
 * Copyright 2023 Jannik Möser
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.github.jnnkmsr.hilt.startup

import android.annotation.SuppressLint
import android.content.Context
import androidx.startup.Initializer
import dagger.hilt.EntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

/**
 * An [Initializer] implementation that allows to combine the Android
 * [App Startup](https://d.android.com/topic/libraries/app-startup) library
 * with [Hilt](https://dagger.dev/hilt/) dependency injection.
 */
@SuppressLint("EnsureInitializerNoArgConstr")
public abstract class HiltInitializer<
    ComponentT,
    in EntryPointT,
    >
protected constructor(

    /**
     * The type of the Hilt [EntryPoint] for injecting dependencies into this
     * initializer.
     */
    private val entryPointType: Class<out EntryPointT>,

    /**
     * Other [Initializer]s this [HiltInitializer] depends on. Used to determine
     * the initialization order.
     */
    private vararg val dependencies: Class<out Initializer<*>>,
) : Initializer<ComponentT> {

    final override fun create(context: Context): ComponentT & Any {
        return create(context, resolveEntryPoint(context))
    }

    /**
     * Initializes and returns the component given the application [context].
     * The [entryPoint] should define an `inject` function that is to be called
     * within `create` to inject dependencies into `this` initializer.
     */
    protected abstract fun create(
        context: Context,
        entryPoint: EntryPointT,
    ): ComponentT & Any

    /**
     * Returns the [EntryPoint] for the given application [context]. This only
     * works if the [EntryPoint] is installed into the [SingletonComponent]].
     */
    private fun resolveEntryPoint(context: Context): EntryPointT =
        EntryPointAccessors.fromApplication(
            context = checkNotNull(context.applicationContext),
            entryPoint = entryPointType,
        )

    final override fun dependencies(): MutableList<Class<out Initializer<*>>> =
        dependencies.toMutableList()
}
