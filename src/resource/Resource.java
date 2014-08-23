/*
 * Enrico Rotundo - 2014 - http://www.math.unipd.it/~crafa/prog3/
 */
package resource;

import java.util.Random;
import java.util.Vector;
import resource.part.ResourcePart;

/**
 * The Class Resource.
 *
 * @author erotundo
 */
public final class Resource implements ResourceInterface {  // Josh Bloch's: "design for inheritance or prohibit it"
	
	/** The name. */
	private ResourceName name;
	
	/** The resource parts. */
	private Vector<ResourcePart> resourceParts;
	
	/** The random generator. */
	private static Random randomGenerator = new Random();
	
	/** The possible resources names. */
	private static String possibleResourcesNames="ABCDEFGHIJKLMNOPQRSTUVWXYZ";

	/**
	 * Instantiates a new resource.
	 *
	 * @param paramName the param name
	 * @param paramNumberOfParts the param number of parts
	 */
	public Resource(Character paramName, Integer paramNumberOfParts) {
		name = new ResourceName(paramName, paramNumberOfParts);
		resourceParts = new Vector<ResourcePart>();
		for (int i = 1; i <= paramNumberOfParts; i++) {
			resourceParts.add(new ResourcePart(i, this));
		}
	}
	
	/**
	 * Instantiates a new resource starting from @param paramText String.
	 *
	 * @param paramText have to respect the mask: oneletter name, blank space, one digit number.
	 */
	public Resource(String paramText) {
		this(paramText.charAt(0), Integer.parseInt(String.valueOf(paramText.charAt(2))));
	}
	
	/**
	 * Creates the random resource.
	 *
	 * @return the resource
	 */
	public static Resource createRandomResource(){
		return new Resource(getRandomAlphabethChar(), getRandomNonZeroNumber());
	}
	
	/**
	 * Creates the random resource vector.
	 *
	 * @return a Vector<Resource> of length [1...10) filled with non-duplicates random named and parts resources.
	 */
	public static Vector<Resource> createRandomResourceVector(){
		Vector<Resource> randomResouceVector = new Vector<Resource>();
		for (int i = 0; i < getRandomNonZeroNumber(); i++) {
			Resource createdResource = createRandomResource();
			//if true then randomResouceVector contains at least one element, so respects method's contract.
			if (randomResouceVector.contains(createdResource) == false) 
				randomResouceVector.add(createdResource);
		}
		return randomResouceVector;
	}
	
	/**
	 * Gets the random non zero number.
	 *
	 * @return an Integer greater then or equal to 1 and less then 10
	 */
	private static Integer getRandomNonZeroNumber() {
		Integer rndInteger = (int) (Math.random()*10);
		if (rndInteger == 0) 
			rndInteger = 1;
		return rndInteger;
	}
	
	/**
	 * Gets the random alphabeth char.
	 *
	 * @return the random alphabeth char
	 */
	private static Character getRandomAlphabethChar(){
		return possibleResourcesNames.charAt(randomGenerator.nextInt(possibleResourcesNames.length()));
	}
	
	/* (non-Javadoc)
	 * @see resource.ResourceInterface#toString()
	 */
	@Override
	public String toString() {
		return getName().toString();
	}
	
	/* (non-Javadoc)
	 * @see resource.ResourceInterface#getName()
	 */
	@Override
	public ResourceName getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see resource.ResourceInterface#getParts()
	 */
	@Override
	public Vector<ResourcePart> getParts() {
		return resourceParts;
	}

}
