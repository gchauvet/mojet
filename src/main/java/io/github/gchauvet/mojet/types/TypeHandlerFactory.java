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
package io.github.gchauvet.mojet.types;

import io.github.gchauvet.mojet.MojetRuntimeException;
import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;

/**
 * Default data type manager factory
 *
 * @author Guillaume CHAUVET
 */
public final class TypeHandlerFactory {

    private final Set<TypeHandler<?>> handlers;

    private static TypeHandlerFactory instance = null;

    private TypeHandlerFactory() {
        final var types = new HashSet<TypeHandler<?>>();
        types.add(new StringTypeHandler());
        types.add(new LongTypeHandler());
        types.add(new ByteTypeHandler());
        types.add(new CharacterTypeHandler());
        types.add(new LocalDateTypeHandler());
        handlers = Collections.unmodifiableSet(types);
    }

    /**
     * Retrive the first type handler for a give type.
     *
     * @param <T> type to handle
     * @param type the class type to handle
     * @return the type handler for the type
     * @throws MojetRuntimeException
     */
    public <T> TypeHandler<T> get(final Class<T> type) {
        try {
            return (TypeHandler<T>) handlers.stream().filter(t -> t.accept(type)).findFirst().get();
        } catch (NoSuchElementException ex) {
            throw new MojetRuntimeException(ex);
        }
    }

    /**
     *
     * @return
     */
    public static TypeHandlerFactory getInstance() {
        if (instance == null) {
            instance = new TypeHandlerFactory();
        }
        return instance;
    }

}
