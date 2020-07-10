package phenotype;

import java.util.ArrayList;
import genotype.Genome;

public class Decoder {
	
	private Genome genome;
	
	public Decoder(Genome g) {
		this.genome = g;
	}
	
	public Individual decode() {
		
		int n = genome.getN();
		ArrayList<Circle> phenotypeCircles = new ArrayList<Circle>(n);
		phenotypeCircles.add(new Circle(0,0,genome.getRadius(0)));
		ArrayList<AbstractCircle> collissionCircles = new ArrayList<AbstractCircle>(n);
		collissionCircles.add(new AbstractCircle(0,0,(double)genome.getRadius(0)));
		
		for (int i = 1; i < n; i++) {
			double angle = Math.toRadians((double)genome.getAngle(i));
			float radius = genome.getRadius(i);
			Vector attackVector = new Vector(Math.cos(angle), Math.sin(angle));
			double maxAttackVectorMultiple = genome.getRadius(0) + radius;
			
			for (int j = i-1; j >= 0; j--) {
				AbstractCircle collider = collissionCircles.get(j);
				
				//check for collision at all
				Vector colliderCenter = collider.getCenter(); //center of the collider-Circle.
				Vector projection = colliderCenter.projectOnto(attackVector); //collider-center-vector projected onto attack-vector.
				
				//bounding conditions?
				boolean positiveDotProduct = projection.getDotproduct(attackVector) >= 0;
				boolean possibleCollissionDespiteNegativeDotProduct = projection.getLengthSquared() <= (radius * radius);
				
				if (positiveDotProduct || possibleCollissionDespiteNegativeDotProduct) { //projection points in the same direction as the attack-vector?
					Vector minDistanceVector = projection.getSubtracted(colliderCenter); //collider-center to nearest point on attack-vector
					double minDistanceSquared = minDistanceVector.getLengthSquared(); //distance squared between collider-center and attack-vector
					double addedRadius = radius + collider.getRadius(); //added radius of collider-circle and new circle
					
					if (minDistanceSquared < addedRadius * addedRadius) { //do collide?
						double lengthToAddToProjection = Math.sqrt(addedRadius*addedRadius - minDistanceSquared); //Pythagoras to get to actual collission point
						double lengthOfProjection = projection.getLength(); //current length of projection vector
						if (!positiveDotProduct && possibleCollissionDespiteNegativeDotProduct) {//invert main projection-length?
							lengthOfProjection = -lengthOfProjection;
						}
						Vector collissionVector = attackVector.getMultiple(lengthOfProjection + lengthToAddToProjection); //calculate actual out-most collission point
						
						if ((maxAttackVectorMultiple * maxAttackVectorMultiple) < collissionVector.getLengthSquared()) { //NEW out-most collission?
							maxAttackVectorMultiple = collissionVector.getLength(); //override maxAttackVectorMultiple to new collission value
						}
					}
				}
			}
			
			//target position of new circle center
			Vector newTargetCenter = attackVector.getMultiple(maxAttackVectorMultiple);
			float centerX = (float)newTargetCenter.getX();
			float centerY = (float)newTargetCenter.getY();
			
			//add new circle to both collections
			phenotypeCircles.add(new Circle(centerX, centerY, radius));
			collissionCircles.add(new AbstractCircle(centerX, centerY, radius));
		}
		
		Individual phenotype = new Individual(phenotypeCircles);
		
		return phenotype;
	}
	
	
	private class Vector {
		
		private double x,y;
		
		public Vector(double x, double y) {
			this.x = x;
			this.y = y;
		}
		
		public double getX() {
			return x;
		}
		
		public double getY() {
			return y;
		}
		
		public double getLengthSquared() {
			return this.x*this.x + this.y*this.y;
		}
		
		public double getLength() {
			return Math.sqrt(getLengthSquared());
		}
		
		public Vector getMultiple(double factor) {
			return new Vector (this.x * factor, this.y * factor);
		}
		
		public Vector getSubtracted(Vector subtract) {
			return new Vector (this.x - subtract.x, this.y - subtract.y);
		}
		
		public double getDotproduct(Vector v2) {
			return this.x * v2.x + this.y * v2.y;
		}
		
		public Vector projectOnto(Vector v2) {
			double d = v2.getDotproduct(v2);
			if (d > 0) {
				double dotproduct = this.getDotproduct(v2);
				return v2.getMultiple(dotproduct / d);
			}
			else {
				return v2;
			}
		}
	}
	
	private class AbstractCircle {
		
		public double x,y,r;
		
		public AbstractCircle(double x, double y, double r) {
			this.x = x;
			this.y = y;
			this.r = r;
		}
		
		public Vector getCenter() {
			return new Vector(x,y);
		}
		
		public double getRadius() {
			return r;
		}
	}
}
