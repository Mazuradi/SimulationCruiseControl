//Interface for the car's speed

package cruise;

public interface ICarSpeed
{
	public double getSpeed();

	public void setThrottle( double aPercent );
	
	public void setCruiseController( ICruiseControl aController );
}
