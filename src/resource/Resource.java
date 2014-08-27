package resource;

import java.util.Random;
import java.util.Vector;

import resource.part.ResourcePart;

public final class Resource implements ResourceInterface {
	// Josh Bloch's: "design for inheritance or prohibit it"

	public static Resource createRandomResource() {
		return new Resource(getRandomAlphabethChar(), getRandomNonZeroNumber());
	}

	/**
	 * @return a Vector<Resource> of length [1...10) filled with non-duplicates
	 *         random named and parts resources.
	 */
	public static Vector<Resource> createRandomResourceVector() {
		final Vector<Resource> randomResouceVector = new Vector<Resource>();
		for (int i = 0; i < getRandomNonZeroNumber(); i++) {
			final Resource createdResource = createRandomResource();
			// if true then randomResouceVector contains at least one element,
			// so respects method's contract.
			if (randomResouceVector.contains(createdResource) == false)
				randomResouceVector.add(createdResource);
		}
		return randomResouceVector;
	}

	/**
	 * @return a random alphabeth char
	 */
	private static Character getRandomAlphabethChar() {
		return possibleResourcesNames.charAt(randomGenerator
				.nextInt(possibleResourcesNames.length()));
	}

	/**
	 * Gets the random non zero number.
	 *
	 * @return an Integer greater then or equal to 1 and less then 10
	 */
	private static Integer getRandomNonZeroNumber() {
		Integer rndInteger = (int) (Math.random() * 10);
		if (rndInteger == 0)
			rndInteger = 1;
		return rndInteger;
	}

	private final ResourceName name;
	private final Vector<ResourcePart> resourceParts;
	private static Random randomGenerator = new Random();
	private static String possibleResourcesNames = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	public Resource(final Character paramName, final Integer paramNumberOfParts) {
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
	 */
	public Resource(final String paramText) {
		this(paramText.charAt(0), Integer.parseInt(String.valueOf(paramText
				.charAt(2))));
	}

	@Override
	public ResourceName getName() {
		return name;
	}

	@Override
	public Vector<ResourcePart> getParts() {
		return resourceParts;
	}

	@Override
	public String toString() {
		return getName().toString();
	}

}
