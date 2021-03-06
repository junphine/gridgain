/*
 * Copyright 2019 GridGain Systems, Inc. and Contributors.
 *
 * Licensed under the GridGain Community Edition License (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.gridgain.com/products/software/community-edition/gridgain-community-edition-license
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

namespace Apache.Ignite.Core.Binary
{
    /// <summary>
    /// Maps class name and class field names to integer identifiers.
    /// </summary>
    public interface IBinaryIdMapper
    {
        /// <summary>
        /// Gets type ID for the given type.
        /// </summary>
        /// <param name="typeName">Full type name.</param>
        /// <returns>ID of the class or 0 in case hash code is to be used.</returns>
        int GetTypeId(string typeName);

        /// <summary>
        /// Gets field ID for the given field of the given class.
        /// </summary>
        /// <param name="typeId">Type ID.</param>
        /// <param name="fieldName">Field name.</param>
        /// <returns>ID of the field or null in case hash code is to be used.</returns>
        int GetFieldId(int typeId, string fieldName);
    }
}
