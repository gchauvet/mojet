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

import org.springframework.batch.item.file.transform.LineAggregator;

/**
 * @param <T> POJO type
 * @author Guillaume CHAUVET
 */
 class MojetLineAggregator<T> implements LineAggregator<T> {
    
    private final Class<T> type;

    public MojetLineAggregator(Class<T> type) {
        this.type = type;
    }

    @Override
    public String aggregate(T item) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    
}
