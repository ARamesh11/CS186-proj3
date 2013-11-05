package simpledb;

/** A class to represent a fixed-width histogram over a single integer-based field.
 */
public class IntHistogram {

    /**
     * Create a new IntHistogram.
     * 
     * This IntHistogram should maintain a histogram of integer values that it receives.
     * It should split the histogram into "buckets" buckets.
     * 
     * The values that are being histogrammed will be provided one-at-a-time through the "addValue()" function.
     * 
     * Your implementation should use space and have execution time that are both
     * constant with respect to the number of values being histogrammed.  For example, you shouldn't 
     * simply store every value that you see in a sorted list.
     * 
     * @param buckets The number of buckets to split the input value into.
     * @param min The minimum integer value that will ever be passed to this class for histogramming
     * @param max The maximum integer value that will ever be passed to this class for histogramming
     */
	public double width; //Width of a bucket
	public int buckets;
	public int min;
	public int max;
	public int[] bucketarray;
	public int total;
	public int adjustment;
	
	
    public IntHistogram(int buckets, int min, int max) {
    	double b  = buckets * 1.0;
    	this.width = (max-min+1)/b;
    	this.buckets = buckets;
    	this.min = min;
    	this.max = max;
    	this.bucketarray = new int[buckets];
    	this.total = 0;
    	this.adjustment = 0;
    	if (min<0){
    		this.adjustment = 0-min;
    		this.min = 0;
    		this.max = max+this.adjustment;
    	}
    }

    /**
     * Add a value to the set of values that you are keeping a histogram of.
     * @param v Value to add to the histogram
     */
    public void addValue(int v) {
    	v = v+adjustment;
    	if (v > max || v < min) return;
    	int index = (int) (v/width);
    	if (index == 0) {
    		this.bucketarray[0] +=1;
    	}
    	else {
    		this.bucketarray[index-1] += 1;
    	}
    	total ++;
    	
    }

    /**
     * Estimate the selectivity of a particular predicate and operand on this table.
     * 
     * For example, if "op" is "GREATER_THAN" and "v" is 5, 
     * return your estimate of the fraction of elements that are greater than 5.
     * 
     * @param op Operator
     * @param v Value
     * @return Predicted selectivity of this particular operator and value
     */
    public double estimateSelectivity(Predicate.Op op, int v) {
    	//TODO: Implement a more accurate selectivity. Possible bug
    	//if (v < min) return 1.0;
    	//if (v > max) return 0.0;
    	v = v+adjustment;
    	int currentBucket = (int) (v/width)-1;
   
    	
    	if (currentBucket < 0){
    		currentBucket = 0;
    	}
    	
    	double counter = 0;
    	double minBucketValue = width * currentBucket + 1;
    	double maxBucketValue = width * (currentBucket+1);
    	
    	if (currentBucket == 0 || currentBucket == -1) {
    		counter = bucketarray[0];
    	} 
    	
    	if (op == Predicate.Op.GREATER_THAN){
    		if (v < min) return 1.0;
        	if (v > max) return 0.0;
    		counter = ((maxBucketValue - (v*1.0))/width) * bucketarray[currentBucket];
	    	for (int i = currentBucket; i<buckets;i++) {
	    		counter += bucketarray[i];
	    	}
    	}else if (op == Predicate.Op.GREATER_THAN_OR_EQ){
    		if (v < min) return 1.0;
        	if (v > max) return 0.0;
    		counter = ((maxBucketValue - (v*1.0))/width +1) * bucketarray[currentBucket];
    		for (int i = currentBucket; i<buckets;i++) {
	    		counter += bucketarray[i];
	    	}
    	}
    	
    	else if (op == Predicate.Op.LESS_THAN){
    		if (v < min) return 0.0;
        	if (v > max) return 1.0;
    		counter = (((v*1.0) - minBucketValue)/width) * bucketarray[currentBucket];
    		for (int i = currentBucket-1; i>-1;i--) {
	    		counter += bucketarray[i];
	    	}
    	}
    	else if (op == Predicate.Op.LESS_THAN_OR_EQ){
    		if (v < min) return 0.0;
        	if (v > max) return 1.0;
    		counter = (((v*1.0) - minBucketValue + 1)/width) * bucketarray[currentBucket];
    		for (int i = currentBucket-1; i>-1;i--) {
	    		counter += bucketarray[i];
	    	}
    	}
    	else if (op == Predicate.Op.EQUALS){
    		counter = bucketarray[currentBucket];    		
    	}
    	else if (op == Predicate.Op.NOT_EQUALS){
    		counter = total - bucketarray[currentBucket];
    	}
    	
    	
    	double t = total *1.0;
    	double selectivity = counter/t;
    	
    	System.out.println("Selectivity: " + selectivity);
    	return selectivity;
    	
   	
    	// some code goes here
    }
    
    /**
     * @return
     *     the average selectivity of this histogram.
     *     
     *     This is not an indispensable method to implement the basic
     *     join optimization. It may be needed if you want to
     *     implement a more efficient optimization
     * */
    public double avgSelectivity()
    {
        // some code goes here
        return 1.0;
    }
    
    /**
     * @return A string describing this histogram, for debugging purposes
     */
    public String toString() {
    	//Returning min,max,buckets,width,total
    	return String.format("Min: %i, Max: %i, Number of Buckets: %i, Width: %i, Total Elements: %i",min,max,buckets,width,total);
    }
}
