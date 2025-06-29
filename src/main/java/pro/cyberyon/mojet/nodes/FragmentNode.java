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
package pro.cyberyon.mojet.nodes;

import lombok.Getter;
import pro.cyberyon.mojet.Fragment;
import pro.cyberyon.mojet.Fragment.PadWay;
import pro.cyberyon.mojet.MojetRuntimeException;
import pro.cyberyon.mojet.types.TypeHandler;

/**
 *
 * @author Guillaume CHAUVET
 */
public class FragmentNode extends AbstractNode<Fragment> {

    @Getter
    private final TypeHandler<?> handler;

    public FragmentNode(String accessor, Fragment annotation, TypeHandler<?> handler) {
        super(annotation, accessor);
        this.handler = handler;
	if (getLenght() < 1)
	    throw new MojetRuntimeException("Lenght must be a positive value");
    }

    public int getLenght() {
        return annotation.length();
    }

    public char getPadder() {
        return annotation.padder();
    }

    public PadWay getAlignement() {
        return annotation.alignement();
    }

    public String getFormat() {
        return annotation.format();
    }

    @Override
    public void accept(NodeVisitor visitor) {
        visitor.visit(this);
    }
}
