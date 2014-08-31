package resource;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.text.ParseException;
import java.util.Vector;

import javax.swing.text.MaskFormatter;

import resource.part.ResourcePart;

// Josh Bloch's: "design for inheritance or prohibit it"
public final class Resource extends UnicastRemoteObject implements ResourceInterface {

	/**
	 * Used when a client search a file: textbox string is converted in a
	 * ResourceName
	 */
	public static class ResourceName implements ResourceNameInterface {

		/**
		 * Gets the mask. "U #" means one A-Z letter, a blank space, a one-digit
		 * number.
		 *
		 * @return the mask formatter
		 * @throws ParseException
		 *             the parse exception
		 */
		public static MaskFormatter getMask() throws ParseException {
			final MaskFormatter formatter = new MaskFormatter();
			try {
				formatter.setMask("U #");
			} catch (final ParseException exc) {
				System.out.println("formatter is bad: " + exc.getMessage());
			}
			return formatter;
		}
		private static final long serialVersionUID = 7377971239594224445L;
		private final Character resourceName;

		private final Integer numberOfParts;

		/**
		 * Instantiates a new resource name.
		 *
		 * @param paramResourceNameCharacter
		 *            the param resource name character
		 * @param paramNumberOfParts
		 *            the param number of parts
		 */
		public ResourceName(final Character paramResourceNameCharacter, final Integer paramNumberOfParts) {
			resourceName = paramResourceNameCharacter;
			numberOfParts = paramNumberOfParts;
		}

		/**
		 * return true only if objects has same name and #of parts
		 */
		@Override
		public boolean equals(final Object other){
			if (other == null)
				return false;
			if (other == this)
				return true;
			if (!(other instanceof ResourceName))
				return false;
			final ResourceName otherMyClass = (ResourceName) other;
			if (otherMyClass.resourceName.equals(this.resourceName) && otherMyClass.numberOfParts.equals(this.numberOfParts)) {
				return true;
			}
			return false;
		}

		/**
		 * @return a String representation of the ResourceName (e.g "A 3")
		 */
		@Override
		final public String toString() {
			return resourceName + " " + numberOfParts;
		}
	}

	private static final long serialVersionUID = -8240063610816799938L;
	private final ResourceName name;
	private final Vector<ResourcePart> resourceParts;

	public Resource(final Character paramName, final Integer paramNumberOfParts) throws RemoteException {
		name = new ResourceName(paramName, paramNumberOfParts);
		resourceParts = new Vector<ResourcePart>();
		for (int i = 1; i <= paramNumberOfParts; i++) {
			resourceParts.add(new ResourcePart(i, this));
		}
	}

	/**
	 * Instantiates a new resource starting from @param paramText String.
	 *
	 * @param paramText
	 *            have to respect the mask: oneletter name, blank space, one
	 *            digit number.
	 * @throws RemoteException
	 * @throws NumberFormatException
	 */
	public Resource(final String paramText) throws NumberFormatException, RemoteException {
		this(paramText.charAt(0), Integer.parseInt(String.valueOf(paramText.charAt(2))));
	}

	@Override
	public boolean equals(final Object other){
		if (other == null)
			return false;
		if (other == this)
			return true;
		if (!(other instanceof Resource))
			return false;
		final Resource otherMyClass = (Resource) other;
		if (otherMyClass.name.equals(this.name)) {
			return true;
		}
		return false;
	}

	@Override
	public Vector<ResourcePart> getParts() {
		return resourceParts;
	}

	@Override
	public String toString() {
		return name.toString();
	}
}
