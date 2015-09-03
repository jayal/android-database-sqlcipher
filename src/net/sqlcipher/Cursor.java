/*
 * Copyright (C) 2006 The Android Open Source Project
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

package net.sqlcipher;

/**
 * This interface provides random read-write access to the result set returned
 * by a database query.
 * <p>
 * Cursor implementations are not required to be synchronized so code using a Cursor from multiple
 * threads should perform its own synchronization when using the Cursor.
 * </p><p>
 * Implementations should subclass {@link AbstractCursor}.
 * </p>
 */
public interface Cursor extends android.database.Cursor {

}
