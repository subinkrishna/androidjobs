/**
 * Copyright (C) 2019 Subinkrishna Gopi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.subinkrishna.androidjobs.model

/**
 * LCE
 *
 * Inspired by https://github.com/kaushikgopal/movies-usf
 */
sealed class Lce<T> {
    class Loading<T> : Lce<T>()
    data class Content<T>(val payload: T) : Lce<T>()
    data class Error<T>(val payload: T) : Lce<T>()
}