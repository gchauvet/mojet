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

import pro.cyberyon.mojet.MojetRuntimeException;
import pro.cyberyon.mojet.Zap;

/**
 * A filler node
 *
 * @author Guillaume CHAUVET
 */
public class ZapNode extends AbstractNode<Zap> {

	/**
	 * Construct a filler node
	 *
	 * @param annotation the filler annotation
	 */
	public ZapNode(Zap annotation) {
		super(annotation, "");
		if (getLength() < 1) {
			throw new MojetRuntimeException("Lenght must be a positive value");
		}
	}

	/**
	 * Get the filler length area
	 *
	 * @return the length
	 */
	public int getLength() {
		return annotation.length();
	}

	/**
	 * Get the padder charactere
	 *
	 * @return the padding value
	 */
	public char getPadding() {
		return annotation.value();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void accept(NodeVisitor visitor) {
		visitor.visit(this);
	}
}
