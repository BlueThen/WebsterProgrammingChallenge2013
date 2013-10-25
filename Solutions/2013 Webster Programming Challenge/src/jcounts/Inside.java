package jcounts;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

/**
 * Problem C: Inside
 * @author Jared Counts
 *
 */

// post-note: this one was a bit long.
// If you can foresee this, I definitely recommend putting it off 'til you get some of the easier problems out of the way.

// There's a million and one ways we can solve this problem
// Problem H: Railroad Intersections asks for us to find intersections between pairs of line segments
// this is awesome, because after solving this one, then solving H would be really easy and fast

// We didn't have time to solve this particular problem last year,
// but my logic would have been:
// write float[] intersect(x1,y1, x2,y2,  x3,y3, x4,y4) where segments are (x1,y1)->(x2,y2) and (x3,y3)->(x4,y4) and [0]=x-intersect and [1]=y-intersect
// create a line segment between the ball and each vertex
// if any of those segments intersects a polygon edge, then it's outside.
// (this only works if the polygon is convex, which the problem allows us to assume)

// Wikipedia has a solution that works for both convex and concave polygons, and even in three dimensions!
// If one were to ray cast from any point outside the polygon towards the ball, then the number of edges it intersects will be even if and only if the ball is inside the polygon!
// see: http://en.wikipedia.org/wiki/Point_in_polygon

// instead of choosing a random point outside the polygon, we'll try to keep it somewhat basic:
// find the vertex farthest to the left, use its x coordinate minus some arbitrary number
// then use the ball's y val,
// and you can make a segment from that (x,y) to the ball's (x,y). Test against each vertex, count the number of confirmed intersects.
// note: if the segment intersects a vertex of the polygon, it should only count as one (even though it intersects the vertex of two edges).
public class Inside {
	static Scanner scanner;
	// EPSILON 
	// a really small number.
	// sometimes, with floats, doing a bunch of multiplying and dividing can mess up the given result by a very very small amount.
	// when doing comparisons, it can be a big issue
	// we use EPSILON to see if our ray casting goes through the vertices of the polygon
	static float EPSILON = 0.00001f;
	public static void main(String args[]) {
		try {
			scanner = new Scanner(new File("data/inside.in"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		// given 1st: number of data sets
		// 2nd: number of points (each set) + 1 (I don't know why they included + 1 for the ball...)
		// x then y coordinate of ball
		// then each vertex of the polygon (all integers)
		float sets = scanner.nextInt();
		for (int d = 0; d < sets; d++) {
			// we're going to declare each coordinate as a float, because I don't trust ints in this case.
			// vertices will be stored as an array
			// vertices[n] -> returns each "coordinate array"
			// vertices[n][0] -> x val of vertex n
			// vertices[n][1] -> y val of vertex n
			float[][] vertices = new float[scanner.nextInt()-1][2];
			
			float ballX = scanner.nextInt();
			float ballY = scanner.nextInt();
			
			float minX = 0;
			for (int v = 0; v < vertices.length; v++) {
				vertices[v][0] = scanner.nextInt();
				vertices[v][1] = scanner.nextInt();
				if (v != 0)
					minX = Math.min(minX, vertices[v][0]);
				else
					minX = vertices[v][0];
			}
			minX -= 1; // don't test right at the edge to avoid ambiguities.
			// now we "cast a ray" from (minX, ballY) to (ballX, ballY) by testing intersections between every edge and this ray.
			float intersects = 0;
			for (int v = 0; v < vertices.length; v++) {
				// intersect() is declared below.
				float[] intersection = intersect(minX, ballY, ballX, ballY,  vertices[v][0], vertices[v][1], vertices[(v+1)%vertices.length][0], vertices[(v+1)%vertices.length][1]);
				if (intersection[0] == 1 || intersection[0] == 2) {
					intersects++;
					// does it intersect a vertex?
					// change it to 0.5 if it does, since both edges will trigger for it.
					if ((Math.abs(vertices[v][0] - intersection[1]) < EPSILON && Math.abs(vertices[v][1] - intersection[2]) < EPSILON) || 
							(Math.abs(vertices[(v+1)%vertices.length][0] - intersection[1]) < EPSILON && Math.abs(vertices[(v+1)%vertices.length][1] - intersection[2]) < EPSILON)) {
						intersects -= 0.5f;
					}
				}
			}
			if (intersects % 2 == 1) // odd number! This intersects!!
				System.out.println("Yes");
			else
				System.out.println("No");
		}
		
	}
	// does segment (x1,y1) (x2,y2) intersect (x3,y3) (x4,y4)?
	// [0] -> 0 for no, 1 for yes, 2 for it intersects on all points on the overlapped segments
	// [1] -> x intersect
	// [2] -> y intersect
	// we actually don't really need the x and y intersects, but it will be helpful later in problem H.
	public static float[] intersect(float x1, float y1, float x2, float y2,   float x3, float y3, float x4, float y4) {
		// there's a a bunch of ways of doing this, mostly involving linear algebra
		// lets stick with what we know
		// straight forward approach:
		// let m1 = (y2 - y1) / (x2 - x1) and m2 = (y4 - y3) / (x4 - x3)
		// b1 = y1 - (m1 * x1)
		// b2 = y3 - (m2 * x3)
		// m1 x + b1 = m2 x + b2
		// m1 x - m2 x = b2 - b1
		// x = (b2 - b1) / (m1 - m2)
		// then we can plug x into either line equation
		// y = m1 * x + b1
		float x,y;
		// special case: what if the lines are vertical? what if they're parallel? what if they're vertical AND parallel!?
		if (x1 == x2 && x3 == x4) {
			// first of all, are they on the same x column?
			if (x2 != x3)
				return new float[]{0};
			// a simple test: if maxY - minY < combinedLengths, they intersect at all points
			float maxY = max(y1,y2,y3,y4);
			float minY = min(y1,y2,y3,y4);
			float d1 = Math.abs(y2-y1);
			float d2 = Math.abs(y4-y3);
			if (maxY - minY <= d2 + d1)
				return new float[]{2}; // intersects over an interval
			// if they don't intersect, then they're parallel, on the same line, but don't overlap
			return new float[]{0};
		}
		else if (x1 == x2) {
			// we only need to create one line equation
			float m2 = (y4 - y3) / (x4 - x3);
			float b2 = y3 / (m2 * x3);
			x = x1;
			y = m2 * x1 + b2;
			
		}
		else if (x3 == x4) {
			float m1 = (y2 - y1) / (x2 - x1);
			float b1 = y1 / (m1 * x1);
			x = x3;
			y = m1 * x3 + b1;
			
		}
		else { // no vertical lines, carry on!
			float m1 = (y2 - y1) / (x2 - x1);
			float m2 = (y4 - y3) / (x4 - x3);
			
			float b1 = y1 - (m1 * x1);
			float b2 = y3 - (m2 * x3);
			
			// are they parallel?
			if (m1 == m2) { 
				// they definitely don't intersect if they have different y-intercepts
				if (b1 != b2) 
					return new float[]{0}; 
				// we can use the same test as in the x1 == x2 && x3 == x4 case
				// except we'll need to use the distance formula for angled lines.
				float d1 = dist(x1,y1, x2,y2);
				float d2 = dist(x3,y3, x4,y4);
				
				// d1 + d2 must be smaller than the distance between the two farthest points for their to be an intersection
				float d3 = dist(x1,y1, x3,y3);
				float d4 = dist(x1,y1, x4,y4);
				float d5 = dist(x2,y2, x3,y3);
				float d6 = dist(x2,y2, x4,y4);
				float maxD = max(d3,d4,d5,d6);
				if (d1 + d2 < maxD)
					return new float[]{2};
				// if they don't intersect, then they're parallel, on the same line, but don't overlap
				return new float[]{0};
			}
			x = (b2 - b1) / (m1 - m2);
		    y = m1 * x + b1;
		}
		// now we need to see if the intersect is within our bounds
		if (within(x, x1,x2) && within(x, x3,x4) && within(y, y1,y2) && within(y, y3,y4)) {
			return new float[]{1, x,y};
		}
		else return new float[]{0};
		
		
	}
	// cool feature of Java:
	// Object... name as a parameter
	// lets you input as many parameters as you'd like
	// eg. min(1,2,3,4,5,6,7,8,9,...) = 1
	public static float min(float... p) {
		float min = p[0];
		for (float v : p)
			min = Math.min(min, v);
		return min;
	}
	public static float max(float... p) {
		float max = p[0];
		for (float v : p)
			max = Math.max(max, v);
		return max;
	}
	
	// finds out if val is within [a,b]
	// b can be > a, so we need to find the minimum and maximum of the two
	public static boolean within(float val, float a, float b) {
		return val >= min(a,b) && val <= max(a,b);
	}
	public static float dist(float x1, float y1, float x2, float y2) {
		float deltaX = x2 - x1;
		float deltaY = y2 - y1;
		return (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
	}
}
