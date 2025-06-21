/*
 * Copyright 2025 Guillaume CHAUVET.
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
package io.github.gchauvet.mojet;

import lombok.Data;

/**
 * Main test pojo class
 * @author Guillaume CHAUVET
 */
@Data
@Record
public class RootPojo {
    
    @Fragment(length = 5)
    private long id;
    @Filler(length = 3, value = '0')
    @Filler(length = 2, value = '#')
    private ChildPojo child;
    @Fragment(length = 3)
    int counter;

}
