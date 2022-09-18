package util;


public class Interval {

	private int min;
	private int max;
	

	public Interval(int min, int max) {
		if (min > max) {
			int tempMin = min;
			min = max;
			max = tempMin;
		}
		
		this.min = min;
		this.max = max;
	}


	public int getMin() {
		return this.min;
	}


	public int getMax() {
		return this.max;
	}


	public boolean isIn(int minParent, int maxParent) {
		if (minParent > maxParent) {
			int tempMin = minParent;
			minParent = maxParent;
			maxParent = tempMin;
		}

		return (this.min >= minParent && this.max >= minParent &&
				this.min <= maxParent && this.max <= maxParent);
	}


	@Override
	public String toString() {
		return "[" + this.min + ", " + this.max + "]";
	}

}
