/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package resource;

import java.text.ParseException;
import java.util.Vector;

import javax.swing.text.MaskFormatter;

import resource.part.ResourcePart;

/**
 * The Interface ResourceInterface.
 *
 * @author erotundo
 */
public interface ResourceInterface {

	/**
	 * Used when a client search a file: textbox string is converted in a
	 * ResourceName object and then the search asks the system if there are an
	 * equal object.
	 *
	 * @author erotundo
	 */
	public class ResourceName {

		/**
		 * Creates the formatter.
		 *
		 * @param paramStringMask
		 *            is a string like "U #"
		 * @return the mask formatter
		 */
		final protected static MaskFormatter createFormatter(
				final String paramStringMask) {
			final MaskFormatter formatter = new MaskFormatter();
			try {
				formatter.setMask(paramStringMask);
			} catch (final ParseException exc) {
				System.out.println("formatter is bad: " + exc.getMessage());
			}
			return formatter;
		}

		/**
		 * Gets the mask. "U #" means one A-Z lettere, a blank space, a
		 * one-digit number.
		 *
		 * @return the mask
		 * @throws ParseException
		 *             the parse exception
		 */
		public final static MaskFormatter getMask() throws ParseException {
			return createFormatter("U #");
		}

		/** The resource name. */
		private Character resourceName;

		/** The number of parts. */
		private Integer numberOfParts = 0;

		/**
		 * Instantiates a new resource name.
		 *
		 * @param paramResourceNameCharacter
		 *            the param resource name character
		 * @param paramNumberOfParts
		 *            the param number of parts
		 */
		public ResourceName(final Character paramResourceNameCharacter,
				final Integer paramNumberOfParts) {
			setResourceName(paramResourceNameCharacter);
			setNumberOfParts(paramNumberOfParts);
		}

		/**
		 * Gets the number of parts.
		 *
		 * @return the numberOfParts
		 */
		final public Integer getNumberOfParts() {
			return numberOfParts;
		}

		/**
		 * Gets the resource name.
		 *
		 * @return the resourceName
		 */
		final public Character getResourceName() {
			return resourceName;
		}

		/**
		 * Sets the number of parts.
		 *
		 * @param numberOfParts
		 *            the numberOfParts to set
		 */
		final public void setNumberOfParts(final Integer numberOfParts) {
			this.numberOfParts = numberOfParts;
		}

		/**
		 * Sets the resource name.
		 *
		 * @param resourceName
		 *            the resourceName to set
		 */
		final public void setResourceName(final Character resourceName) {
			this.resourceName = resourceName;
		}

		/**
		 * To string.
		 *
		 * @return a String representation of the ResourceName (e.g "A 3")
		 */
		@Override
		final public String toString() {
			return resourceName + " " + numberOfParts;
		}

	}

	/**
	 * Gets the resource ResourceName object.
	 *
	 * @return the name
	 */
	public ResourceName getName();

	/**
	 * Gets the resource's parts.
	 *
	 * @return the parts Vector<Part>.
	 */
	public Vector<ResourcePart> getParts();

	/**
	 * To string.e
	 *
	 * @return the string formatted resource name (eg. "A 3").
	 */
	@Override
	public String toString();
}
