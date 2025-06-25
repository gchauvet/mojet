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
package pro.cyberyon.mojet;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import lombok.NonNull;

/**
 * Skeleton class providing fields extraction.
 *
 * @author Guillaume CHAUVET
 */
abstract class AbstractMojetLine<T> {

    protected final Map<String, Field> mappedFields;
    protected final Class<T> type;

    protected AbstractMojetLine(@NonNull Class<T> targetType) {
        this.type = targetType;
        mappedFields = Collections.unmodifiableMap(mapAnnotatedFields(type, ""));
    }

    /**
     * Browse the class fields and create a map of fields annotated with
     *
     * @Fragment. If a field is a custom class annotated with @Registration, it
     * is explored recursively.
     *
     * @param clazz la classe to wrap
     * @param prefix prefix for field names
     * @return a map of field names and their corresponding ranges
     */
    private Map<String, Field> mapAnnotatedFields(Class<?> clazz, String prefix) {
        final Map<String, Field> fieldMap = new LinkedHashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            if (field.isAnnotationPresent(Fragment.class) || field.isAnnotationPresent(Filler.class) || field.isAnnotationPresent(Fillers.class)) {
                final String key = prefix + field.getName();
                if (field.getType().isArray()) {
                    if (!field.isAnnotationPresent(Occurences.class)) {
                        throw new MojetRuntimeException("Array required a number of occurences. See corresponding annotation");
                    }
                    final int max = field.getAnnotation(Occurences.class).value();
                    if (max < 1)
                        throw new MojetRuntimeException("Natual number of occurences expected on field " + field);
                    for (int i = 0; i < max; i++) {
                        fieldMap.put(key + "[" + i + "]", field);
                    }
                } else {
                    fieldMap.put(key, field);
                }
            }
            if (field.getType().isAnnotationPresent(Record.class)) {
                // If the field is a custom class, we recursively explore
                fieldMap.putAll(mapAnnotatedFields(field.getType(), prefix + field.getName() + "."));
            }
        }
        return fieldMap;
    }

}
