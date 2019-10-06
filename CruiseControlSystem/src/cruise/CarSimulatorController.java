//Car Simulator Controller involving GUI & thread cycling

package cruise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Panel;

import javax.swing.JButton;

public class CarSimulatorController extends Panel implements Runnable, ICarSpeed
{
	private static final long serialVersionUID = 1L;

	private ICruiseControl fCruiseController;
	private SpeedometerCanvas fSpeedometerView;

	private JButton fEngine;
	private JButton fAccelerate;
	private JButton fBrake;

	private double fCurrentSpeed;
    private double fDistanceTraveled;
    private double fThrottleValue;
    private double fBrakeValue;
    private boolean fIgnitionOn;

    private Thread fEngineThread;
        
    public static double MaxSpeed = 240.0;			// 240 km/h
    public static double MaxThrottle = 5.0;		// 100% => 5 m/s^2 
    public static double MaxBrake = 8.0;			// 100% => 8 m/s^2
    
    public static double DragMultiplier = 0.00026288; 
    
    public final static int TicksPerSecond = 5;

    private synchronized void engineOn_Off()
    {
		if ( fIgnitionOn )
		{
	    	fEngineThread = null;
			fIgnitionOn = false;
			fEngine.setText( "Engine On" );
			fSpeedometerView.clear();
			fAccelerate.setEnabled( false );
			fBrake.setEnabled( false );
			fSpeedometerView.setIgnitionOff();
			if ( fCruiseController != null )
			{
				fCruiseController.engineOff();
			}
		}
		else
		{
			fIgnitionOn = true;
			fEngine.setText( "Engine Off" );
			fAccelerate.setEnabled( true );
			fBrake.setEnabled( true );
			fSpeedometerView.setIgnitionOn();

			if ( fEngineThread == null )
		    {
		    	fEngineThread = new Thread( this );
		       	fEngineThread.start();
		    }
			
			if ( fCruiseController != null )
			{
				fCruiseController.engineOn();
			}
		}
    }

    private synchronized void accelerate()
    {
    	if ( fThrottleValue < MaxThrottle )
    	{
    		fBrakeValue = 0;
        	fThrottleValue++;
        	
        	if ( fThrottleValue > MaxThrottle )
        	{
        		fThrottleValue = MaxThrottle;
        	}
        	        	
        	fSpeedometerView.setThrottle( fThrottleValue );
    	}
    }

    private synchronized void brake()
    {
    	if ( fBrakeValue < MaxBrake )
    	{
        	fThrottleValue = 0;
        	fBrakeValue++;
        	
        	if ( fCruiseController != null )
        	{
        		fCruiseController.brake();
        	}
        	
        	if ( fBrakeValue > MaxBrake )
        	{
        		fBrakeValue = MaxBrake;
        	}
        	
        	fSpeedometerView.setBrake( fBrakeValue );
    	}
    }
    
	private void setupSpeedometerView()
	{
		setLayout( new GridBagLayout() );
		
		GridBagConstraints lConstraints = new GridBagConstraints();

		lConstraints.fill = GridBagConstraints.HORIZONTAL;

		fSpeedometerView = new SpeedometerCanvas();
		lConstraints.gridx = 0;
		lConstraints.gridy = 0;
		lConstraints.gridheight = 5;
		lConstraints.gridwidth = 3;
		add( fSpeedometerView, lConstraints );
		
		fEngine = new JButton( "Engine On" );
		fEngine.addActionListener( e -> engineOn_Off() );
		lConstraints.gridx = 0;
		lConstraints.gridy = 6;
		lConstraints.gridheight = 1;
		lConstraints.gridwidth = 1;
		add( fEngine, lConstraints );

		fAccelerate = new JButton( "Accelerate" );
		fAccelerate.addActionListener( e -> accelerate() );
		lConstraints.gridx = 1;
		add( fAccelerate, lConstraints );

		fBrake = new JButton( "Brake" );
		fBrake.addActionListener( e -> brake() );
		fBrake.setEnabled( false );
		lConstraints.gridx = 2;
		add( fBrake, lConstraints );
	}

	public CarSimulatorController()
	{
		super();
		
		fCurrentSpeed = 0.0;
	    fDistanceTraveled = 0.0;
	    fThrottleValue = 0.0;
	    fBrakeValue = 0.0;
	    fIgnitionOn = false;

	    setupSpeedometerView();
	}
	
	public void run() 
	{
		try
		{
			synchronized(this)
			{
				// controller cycle, runs every 0.2 seconds
	            while ( fEngineThread!=null )
	            {
	                wait( 1000 / TicksPerSecond );
	                
	                // update speed
	                double lSpeedSquare = fCurrentSpeed*fCurrentSpeed / 3.6;	// in m^2/s^2
	                double lRelativeAcceleration = 
	                		(fThrottleValue/2.0) - (DragMultiplier * lSpeedSquare) - fBrakeValue;

	                // add gain in km/h
	                fCurrentSpeed += lRelativeAcceleration * 3.6/ TicksPerSecond;

	                if ( fCurrentSpeed > MaxSpeed )
	                {
	                	fCurrentSpeed = MaxSpeed;
	                }

	                if ( fCurrentSpeed < 0.0 )
	                {
	                	fCurrentSpeed = 0.0;
	                }

	                fEngine.setEnabled( (int)fCurrentSpeed == 0 ); // engine mustn't turn off while driving
	                
	                fSpeedometerView.setSpeed( fCurrentSpeed );
	                
	                // make sure distance travelled is updated
	                fDistanceTraveled += fCurrentSpeed / TicksPerSecond;
	                
	                fSpeedometerView.setDistance( fDistanceTraveled );

	                // throttle decay
	                if ( fThrottleValue > 0.0 )
	                {
	                	fThrottleValue -= 0.5 / TicksPerSecond;
	                }

	                fSpeedometerView.setThrottle( fThrottleValue );
	            }	
			}
		}
		catch ( InterruptedException e )
		{
			// empty on purpose
		}
		
		fCurrentSpeed = 0.0;
		fDistanceTraveled = 0.0;
    	fThrottleValue = 0.0;
		fBrakeValue = 0.0;
		fSpeedometerView.clear();
	}
	
	public synchronized double getSpeed()
	{
		return fCurrentSpeed;
	}

	public synchronized void setThrottle( double aPercent )
	{
		fBrakeValue = 0;
    	fThrottleValue += aPercent / 10.0;	// map to 0..10

		if ( fThrottleValue < 0.0 )
    	{
	    	fThrottleValue = 0.0;
    	}
        	
        if ( fThrottleValue > MaxThrottle )
        {
        	fThrottleValue = MaxThrottle;
        }
        	
        fSpeedometerView.setThrottle( fThrottleValue );
	}
	
	public void setCruiseController( ICruiseControl aController )
	{
		// run at setup
		fCruiseController = aController;
	}
}
