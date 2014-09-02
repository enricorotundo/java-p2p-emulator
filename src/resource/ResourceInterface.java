package resource;

import java.io.Serializable;
import java.util.Vector;

import resource.part.ResourcePartInterface;

public interface ResourceInterface extends Serializable {

	public interface ResourceNameInterface extends Serializable {
		/**
		 * return true only if objects has same name and #of parts
		 */
		@Override
		public boolean equals(final Object other);
	}

	public ResourceInterface createResource(final Character paramName, final Integer paramNumberOfParts);

	@Override
	public boolean equals(final Object other);

	public Vector<ResourcePartInterface> getParts();

	public Boolean resourceCompare(final String paramResName);

	/**
	 * @return the string formatted resource name (eg. "A 3").
	 */
	@Override
	public String toString();
}
