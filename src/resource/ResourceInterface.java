package resource;

import java.io.Serializable;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.text.MaskFormatter;

import resource.part.ResourcePart;

public interface ResourceInterface extends Serializable {

	/**
	 * Used when a client search a file: textbox string is converted in a
	 * ResourceName
	 */
	public class ResourceName implements Serializable {

		/**
		 * Creates the formatter.
		 *
		 * @param paramStringMask
		 *            is a string like "U #"
		 * @return the mask formatter
		 */
		final protected static MaskFormatter createFormatter(final String paramStringMask) {
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

		private static final long serialVersionUID = 6549872704452284682L;

		private Character resourceName;
		private Integer numberOfParts = 0;

		/**
		 * Instantiates a new resource name.
		 *
		 * @param paramResourceNameCharacter
		 *            the param resource name character
		 * @param paramNumberOfParts
		 *            the param number of parts
		 */
		public ResourceName(final Character paramResourceNameCharacter, final Integer paramNumberOfParts) {
			setResourceName(paramResourceNameCharacter);
			setNumberOfParts(paramNumberOfParts);
		}

		final public Integer getNumberOfParts() {
			return numberOfParts;
		}

		final public Character getResourceName() {
			return resourceName;
		}

		final public void setNumberOfParts(final Integer numberOfParts) {
			this.numberOfParts = numberOfParts;
		}

		final public void setResourceName(final Character resourceName) {
			this.resourceName = resourceName;
		}

		/**
		 * @return a String representation of the ResourceName (e.g "A 3")
		 */
		@Override
		final public String toString() {
			return resourceName + " " + numberOfParts;
		}

	}

	public Vector<ResourcePart> getParts();

	/**
	 * To string.e
	 *
	 * @return the string formatted resource name (eg. "A 3").
	 */
	@Override
	public String toString();
}
