package resource;

import java.io.Serializable;
import java.util.Vector;

import resource.part.ResourcePart;

public interface ResourceInterface extends Serializable {

	public interface ResourceNameInterface extends Serializable {
		/**
		 * return true only if objects has same name and #of parts
		 */
		@Override
		public boolean equals(final Object other);
	}

	@Override
	public boolean equals(final Object other);

	public Vector<ResourcePart> getParts();

	/**
	 * @return the string formatted resource name (eg. "A 3").
	 */
	@Override
	public String toString();
}
