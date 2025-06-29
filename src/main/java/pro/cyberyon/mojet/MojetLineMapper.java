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

import org.springframework.batch.item.file.LineMapper;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import pro.cyberyon.mojet.nodes.FillerNode;
import pro.cyberyon.mojet.nodes.FragmentNode;
import pro.cyberyon.mojet.nodes.NodeVisitor;
import pro.cyberyon.mojet.nodes.OccurencesNode;
import pro.cyberyon.mojet.nodes.RecordNode;

/**
 * MojetLineMapper is an implementation of LineMapper that uses a POJO type to
 * map data that are annotated.
 *
 * @param <T> POJO type
 *
 * @author Guillaume CHAUVET
 */
public class MojetLineMapper<T> extends AbstractMojetLine<T> implements LineMapper<T> {

    /**
     * Construct a new pojo {@link LineMapper} instance
     *
     * @param targetType the bean type to manage
     */
    public MojetLineMapper(final Class<T> targetType) {
        super(targetType);
    }

    @Override
    public T mapLine(String line, int lineNumber) throws Exception {
        final BeanWrapper wrapper = new BeanWrapperImpl(root.getType());
        root.accept(new NodeVisitor() {
            @Override
            public void visit(RecordNode node) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void visit(FillerNode node) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void visit(OccurencesNode node) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }

            @Override
            public void visit(FragmentNode node) {
                throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
            }
        });
        return (T) wrapper.getWrappedInstance();
    }

}
