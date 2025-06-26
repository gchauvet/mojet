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
package pro.cyberyon.mojet.types;

/**
 * Integer data type handler
 *
 * @author Guillaume CHAUVET
 */
final class IntegerTypeHandler extends AbstractTypeHandler<Integer> {

    @Override
    protected boolean isAccept(Class<?> type) {
        return Integer.class == type;
    }

    @Override
    public Integer read(String data, String format) {
        return Integer.valueOf(data);
    }

    @Override
    public String write(Integer data, String format) {
        return Integer.toString(data);
    }

}
