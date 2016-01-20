/*
 * Copyright 2016 Niek Haarman
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

package com.nhaarman.triad

import java.util.*

/**
 * Describes the history of a [Triad] instance at a specific point in time.
 */
class Backstack private constructor(private val backstack: Deque<Entry<*>>) : Iterable<Screen<*>> {

    override fun iterator(): Iterator<Screen<*>> {
        return ReadIterator(backstack.iterator())
    }

    fun reverseIterator(): Iterator<Screen<*>> {
        return ReadIterator(backstack.descendingIterator())
    }

    internal fun reverseEntryIterator(): Iterator<Entry<*>> {
        return EntryReadIterator(backstack.descendingIterator())
    }

    fun size(): Int {
        return backstack.size
    }

    internal fun <T : Any> current(): Entry<T> {
        return backstack.peek() as Entry<T>
    }

    /**
     * Get a builder to modify a copy of this backstack.
     */
    fun buildUpon(): Builder {
        return Builder(backstack)
    }

    override fun toString(): String {
        return backstack.toString()
    }

    internal data class Entry<T : Any>(val screen: Screen<T>, val animator: TransitionAnimator?)

    class Builder internal constructor(backstack: Collection<Entry<*>>) {

        private val backstack: Deque<Entry<*>> = ArrayDeque(backstack)

        @JvmOverloads fun push(screen: Screen<*>, animator: TransitionAnimator? = null): Builder {
            return push(Entry(screen, animator))
        }

        internal fun push(entry: Entry<*>): Builder {
            backstack.push(entry)
            return this
        }

        internal fun pop(): Entry<*>? {
            return backstack.pop()
        }

        fun clear(): Builder {
            backstack.clear()

            return this
        }

        fun build(): Backstack {
            return Backstack(backstack)
        }
    }

    private class ReadIterator internal constructor(private val mIterator: Iterator<Entry<*>>) : Iterator<Screen<*>> {

        override fun hasNext(): Boolean {
            return mIterator.hasNext()
        }

        override fun next(): Screen<*> {
            return mIterator.next().screen
        }
    }

    private class EntryReadIterator internal constructor(private val mIterator: Iterator<Entry<*>>) : Iterator<Entry<*>> {

        override fun hasNext(): Boolean {
            return mIterator.hasNext()
        }

        override fun next(): Entry<*> {
            return mIterator.next()
        }
    }

    companion object {

        fun emptyBuilder(): Builder {
            return Builder(emptyList<Entry<*>>())
        }

        /**
         * Create a backstack that contains a single screen.
         */
        fun single(screen: Screen<*>): Backstack {
            return emptyBuilder().push(screen).build()
        }

        /**
         * Creates a backstack that contains given screens.
         */
        fun of(vararg screens: Screen<*>): Backstack {
            val builder = emptyBuilder()

            for (screen in screens) {
                builder.push(screen)
            }

            return builder.build()
        }
    }
}
