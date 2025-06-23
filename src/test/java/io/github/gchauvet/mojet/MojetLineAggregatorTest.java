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

import io.github.gchauvet.mojet.types.AbstractTypeHandler;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import lombok.Data;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test
 * @author Guillaume CHAUVET
 */
class MojetLineAggregatorTest {
    
    public static final class MyLocalDateTypeHandler extends AbstractTypeHandler<LocalDate> {
        
        private final DateTimeFormatter sfd = DateTimeFormatter.ofPattern("ddyyMM", Locale.FRENCH);

        @Override
        protected boolean isAccept(Class<?> type) {
            return LocalDate.class == type;
        }

        @Override
        public LocalDate read(String data) {
            return LocalDate.parse("01" + data, sfd);
        }

        @Override
        public String write(LocalDate data) {
            return data.format(sfd).substring(2);
        }

    }
    
    @Data
    @Record
    public static final class SimplePojo {
        @Fragment(length = 7, padder = '0')
        private long id;
        @Filler(length = 3, value = '#')
        @Filler(length = 2, value = '|')
        @Padding(Padding.PadWay.LEFT)
        @Fragment(length = 10)
        private String name;
        @Padding(Padding.PadWay.RIGHT)
        @Fragment(length = 10)
        private String surname;
        @Converter(MyLocalDateTypeHandler.class)
        @Fragment(length = 4)
        private LocalDate date;
    }
    
    @Test
    void testSimpleAggregation() {
        var instance = new MojetLineAggregator<>(SimplePojo.class);
        SimplePojo item = new SimplePojo();
        item.setId(777);
        item.setName("CHAUVET");
        item.setSurname("Guillaume");
        item.setDate(LocalDate.of(1999, Month.JULY, 18));
        assertEquals("0000777###||   CHAUVETGuillaume 9907", instance.aggregate(item));
    }
    
    @Data
    @Record
    public static final class UndefinedPojo {
        @Fragment(length = 10)
        private Object undefined = "";
    }
    
    public static class BuggerConverterType extends AbstractTypeHandler<Object> {

        public BuggerConverterType() {
            throw new IllegalStateException("bug");
        }

        @Override
        protected boolean isAccept(Class<?> type) {
            return true;
        }

        @Override
        public Object read(String data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String write(Object data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    @Data
    @Record
    public static final class BuggedConverterPojo {
        @Fragment(length = 10)
        @Converter(BuggerConverterType.class)
        private Object undefined = "";
    }
    
    @Data
    @Record
    public static final class NoConverterPojo {
        @Fragment(length = 10)
        private Object undefined = "";
    }
    
    
    public static class InacceptableConverterType extends AbstractTypeHandler<Object> {

        @Override
        protected boolean isAccept(Class<?> type) {
            return false;
        }

        @Override
        public Object read(String data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String write(Object data) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

    }
    
    @Data
    @Record
    public static final class InacceptablePojo {
        @Fragment(length = 10)
        @Converter(InacceptableConverterType.class)
        private Object undefined = "";
    }
    
    @Test
    void testErrorAggregation() {
        assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(UndefinedPojo.class).aggregate(new UndefinedPojo()));
        assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(BuggedConverterPojo.class).aggregate(new BuggedConverterPojo()));
        assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(NoConverterPojo.class).aggregate(new NoConverterPojo()));
        assertThrows(MojetRuntimeException.class, () -> new MojetLineAggregator<>(InacceptablePojo.class).aggregate(new InacceptablePojo()));
    }
}
