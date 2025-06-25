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

import pro.cyberyon.mojet.types.AbstractTypeHandler;
import java.time.LocalDate;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import lombok.Data;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit test
 *
 * @author Guillaume CHAUVET
 */
class MojetLineAggregatorTest {

    public static final class MyLocalDateTypeHandler extends AbstractTypeHandler<LocalDate> {

        @Override
        protected boolean isAccept(Class<?> type) {
            return LocalDate.class == type;
        }

        @Override
        public LocalDate read(String data, String format) {
            final DateTimeFormatter sfd = DateTimeFormatter.ofPattern("dd" + format, Locale.FRENCH);
            return LocalDate.parse("01" + data, sfd);
        }

        @Override
        public String write(LocalDate data, String format) {
            final DateTimeFormatter sfd = DateTimeFormatter.ofPattern("dd" + format, Locale.FRENCH);
            return data.format(sfd).substring(2);
        }

    }

    @Data
    @Record
    @Filler(length = 3, value = '€')
    public static final class SimplePojo {

        @Fragment(length = 7, padder = '0')
        private long id;
        @Filler(length = 3, value = '#')
        @Filler(length = 2, value = '|')
        @Padding(Padding.PadWay.LEFT)
        @Fragment(length = 10)
        private String name;
        @Padding(Padding.PadWay.RIGHT)
        @Fragment(length = 10, padder = '_')
        private String surname;
        @Converter(MyLocalDateTypeHandler.class)
        @Fragment(length = 4, format = "yyMM")
        private LocalDate date;
        @Fragment(length = 2, padder = '$')
        private byte octet = 5;
        @Fragment(length = 2, padder = '€')
        private char car = 'C';
        @Fragment(length = 5)
        @Occurences(3)
        private long[] values = new long[] {2, 4, 6};
    }

    @Test
    void testSimpleAggregation() {
        var instance = new MojetLineAggregator<>(SimplePojo.class);
        SimplePojo item = new SimplePojo();
        item.setId(777);
        item.setName("CHAUVET");
        item.setSurname("Guillaume");
        item.setDate(LocalDate.of(1999, Month.JULY, 18));
        assertEquals("0000777###||   CHAUVETGuillaume_9907$5€C    2    4    6€€€", instance.aggregate(item));
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
        public Object read(String data, String format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String write(Object data, String format) {
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
        public Object read(String data, String format) {
            throw new UnsupportedOperationException("Not supported yet.");
        }

        @Override
        public String write(Object data, String format) {
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

    @Data
    @Record
    public static final class OverflowPojo {

        @Fragment(length = 2)
        private String good = "OK";
        @Fragment(length = 3)
        private String overflow = "TEST";
    }

    @Data
    @Record
    public static final class NoIterableAllowedPojo {

        @Fragment(length = 5)
        private Iterable<Long> undefined;
    }

    @Data
    @Record
    public static final class NoCollectionAllowedPojo {

        @Fragment(length = 5)
        private List<Object> iterable;
    }

    @Test
    void testErrorAggregation() {
        var undefined = new MojetLineAggregator<>(UndefinedPojo.class);
        var pojo1 = new UndefinedPojo();
        assertThrows(MojetRuntimeException.class, () -> undefined.aggregate(pojo1));
        var bugged = new MojetLineAggregator<>(BuggedConverterPojo.class);
        var pojo2 = new BuggedConverterPojo();
        assertThrows(MojetRuntimeException.class, () -> bugged.aggregate(pojo2));
        var uncoverted = new MojetLineAggregator<>(NoConverterPojo.class);
        var pojo3 = new NoConverterPojo();
        assertThrows(MojetRuntimeException.class, () -> uncoverted.aggregate(pojo3));
        var inacceptable = new MojetLineAggregator<>(InacceptablePojo.class);
        var pojo4 = new InacceptablePojo();
        assertThrows(MojetRuntimeException.class, () -> inacceptable.aggregate(pojo4));
        var overflow = new MojetLineAggregator<>(OverflowPojo.class);
        var pojo5 = new OverflowPojo();
        assertEquals("private java.lang.String pro.cyberyon.mojet.MojetLineAggregatorTest$OverflowPojo.overflow length (4) greater than fragment length definition (3)", assertThrows(MojetRuntimeException.class, () -> overflow.aggregate(pojo5)).getMessage());
        var noIterable = new MojetLineAggregator<>(NoIterableAllowedPojo.class);
        var pojo6 = new NoIterableAllowedPojo();
        assertThrows(MojetRuntimeException.class, () -> noIterable.aggregate(pojo6));
        var noCollection = new MojetLineAggregator<>(NoCollectionAllowedPojo.class);
        var pojo7 = new NoCollectionAllowedPojo();
        assertThrows(MojetRuntimeException.class, () -> noCollection.aggregate(pojo7));
    }

}
