package time;


/**
 * Represents a closed interval with a discrete minimum and maximum.
 *
 * @author Jonathan Uhler
 */
public class Interval {
    
    /** The minimum inclusive value of the interval. */
    private int min;
    /** The maximum inclusive value of the interval. */
    private int max;
    
    
    /**
     * Constructs a new {@code Interval} object.
     *
     * @param min  the minimum inclusive value of the interval.
     * @param max  the maximum inclusive value of the interval.
     *
     * @throws IllegalArgumentException  if {@code min} is greater than {@code max}.
     */
    public Interval(int min, int max) {
        if (min > max) {
            throw new IllegalArgumentException("invalid interval [" + min + ", " + max + "]");
        }
	
        this.min = min;
        this.max = max;
    }
    
    
    /**
     * Returns the inclusive minimum of this interval.
     *
     * @return the inclusive minimum of this interval.
     */
    public int getMin() {
        return this.min;
    }
    
    
    /**
     * Returns the inclusive maximum of this interval.
     *
     * @return the inclusive maximum of this interval.
     */
    public int getMax() {
        return this.max;
    }
    
    
    /**
     * Returns whether this interval is inside of the specified interval
     *
     * @param other  the interval that must contain this interval.
     *
     * @return whether this interval is inside of the specified interval.
     *
     * @throws NullPointerException  if {@code other} is null.
     */
    public boolean isIn(Interval other) {
        if (other == null) {
            throw new NullPointerException("other cannot be null");
        }

        return this.min >= other.min && this.max >= other.min &&
               this.min <= other.max && this.max <= other.max;
    }
    
    
    /**
     * Returns a string representation of this {@code Interval}.
     *
     * @return a string representation of this {@code Interval}.
     */
    @Override
    public String toString() {
        return "[" + this.min + ", " + this.max + "]";
    }
    
}
