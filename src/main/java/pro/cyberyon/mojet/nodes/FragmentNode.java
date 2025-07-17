/*
 * Copyright 2025 Guillaume CHAUVET.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
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
 * Node represening a fragment
 *
 * @author Guillaume CHAUVET
 */
public class FragmentNode extends AbstractNode<Fragment> {

	@Getter
	private final TypeHandler<?> handler;

	/**
	 * Construct a fragment node instance
	 *
	 * @param accessor   the field accessor name
	 * @param annotation the fragment annotation
	 * @param handler    the concrete handle to use
	 */
	public FragmentNode(final String accessor, final Fragment annotation, final TypeHandler<?> handler) {
		super(annotation, accessor);
		this.handler = handler;
		if (getLenght() < 1) {
			throw new MojetRuntimeException("Lenght must be a positive value");
		}
	}

	/**
	 * The length of data area
	 *
	 * @return a strict positive number
	 */
	public int getLenght() {
		return annotation.length();
	}

	/**
	 * The padding charactere
	 *
	 * @return the padding value
	 */
	public char getPadder() {
		return annotation.padder();
	}

	/**
	 * the padding way
	 *
	 * @return the alignement xay
	 */
	public PadWay getAlignement() {
		return annotation.alignement();
	}

	/**
	 * The format for handling data
	 *
	 * @return a string, or null
	 */
	public String getFormat() {
		return annotation.format();
	}

	/**
	 * Flag for optional content
	 *
	 * @return <code>true</code> if optional
	 */
	public boolean isOptional() {
		return annotation.optional();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
