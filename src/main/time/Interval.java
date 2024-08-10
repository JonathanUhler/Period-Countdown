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
     * Constructs a new {@code Interval} object. If the minimum is less than the maximum, the two
     * values are swapped.
     *
     * @param min  the minimum inclusive value of the interval.
     * @param max  the maximum inclusive value of the interval.
     */
    public Interval(int min, int max) {
        if (min > max) {
            int tempMin = min;
            min = max;
            max = tempMin;
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
     * Returns whether a given sub-range is within the range of this interval. If the minimum
     * of the sub-range is less than the maximum of the sub-range, the two values are swapped.
     *
     * @param minParent  the inclusive minimum for the sub-range.
     * @param maxParent  the inclusive maximum for the sub-range.
     *
     * @return whether a given sub-range is within the range of this interval.
     */
    public boolean isIn(int minParent, int maxParent) {
        if (minParent > maxParent) {
            int tempMin = minParent;
            minParent = maxParent;
            maxParent = tempMin;
        }
        
        return (this.min >= minParent && this.max >= minParent &&
                this.min <= maxParent && this.max <= maxParent);
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
