//Speedometer Widget created using Java Canvas

package cruise;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Panel;

import javax.swing.JButton;

public class CruiseControlController extends Panel implements Runnable, ICruiseControl
{
	private static final long serialVersionUID = 1L;

	private ICarSpeed fCarSpeedController;

	private CruiseControlCanvas fCruiseControlView;

	private JButton fEnable;
	private JButton fSet;
	private JButton fResume;
	private JButton fIncSpeed;
	private JButton fDecSpeed;

	private enum States
	{
		OFF,
		ENABLED,
		DISABLED
	}
	
	private double fTargetSpeed;
    private States fState;

    private Thread fCCThread;

    private synchronized void on_off()
    {
    	switch( fState )
    	{
    	case OFF:
    		fState = States.ENABLED;
    		fCruiseControlView.set( true );

			fEnable.setText( "Off" );
			fResume.setEnabled( false );

    		if ( fCCThread == null )
    		{
    			fCCThread = new Thread( this );
    			fCCThread.start();
    		}
    		
    		break;
    	default:
    		fCruiseControlView.set( false );
			fResume.setEnabled( false );
    		fCCThread = null;
			fEnable.setText( "On" );
			fState = States.OFF;
    	}
    }

    private synchronized void setSpeed()
    {
		fTargetSpeed = fCarSpeedController.getSpeed();
		fCruiseControlView.setSpeed( (int)fTargetSpeed );
    }
    
    private synchronized void increaseCruise()
    {
    	fTargetSpeed++;
    	fCruiseControlView.setSpeed( (int)fTargetSpeed );
    }
    
    private synchronized void decreaseCruise()
    {
    	fTargetSpeed--;
    	fCruiseControlView.setSpeed( (int)fTargetSpeed );
    }

    private synchronized void resume()
    {
    	fState = States.ENABLED;
		fResume.setEnabled( false );
		fCruiseControlView.set( true );
    }
    
    private void setupCruiseControlView()
	{
		setLayout( new GridBagLayout() );
		GridBagConstraints lConstraints = new GridBagConstraints();

		lConstraints.fill = GridBagConstraints.HORIZONTAL;

		fCruiseControlView = new CruiseControlCanvas();
		lConstraints.gridx = 0;
		lConstraints.gridy = 0;
		lConstraints.gridheight = 2;
		add( fCruiseControlView, lConstraints );
        
		fEnable = new JButton( "On" );
		fEnable.addActionListener( e -> on_off() );
		fEnable.setEnabled( false );
		lConstraints.gridy = 2;
		lConstraints.gridheight = 1;
		lConstraints.insets = new Insets(10,0,0,0);
		add( fEnable, lConstraints );

		fSet = new JButton( "Set" );
		fSet.addActionListener( e -> setSpeed() );
		fSet.setEnabled( false );
		lConstraints.gridy = 3;
		add( fSet, lConstraints );

		fResume = new JButton( "Resume" );
		fResume.addActionListener( e -> resume() );
		fResume.setEnabled( false );
		lConstraints.gridy = 4;
		add( fResume, lConstraints );
		
		fIncSpeed = new JButton("Increase");
		fIncSpeed.addActionListener(e -> increaseCruise());
		lConstraints.gridy = 5;
		add(fIncSpeed, lConstraints);
		
		fDecSpeed = new JButton("Decrease");
		fDecSpeed.addActionListener(e -> decreaseCruise());
		lConstraints.gridy = 6;
		add(fDecSpeed, lConstraints);
		
	}

	public CruiseControlController( ICarSpeed aCarSpeedController )
	{
		super();
		
		fCarSpeedController = aCarSpeedController;
		fTargetSpeed = 0.0;
		fState = States.OFF;
	
		setupCruiseControlView();

		fCarSpeedController.setCruiseController( this );
	}
	
	public synchronized void engineOn()
	{
		fEnable.setEnabled( true );
		fSet.setEnabled( true );
	}

	public synchronized void brake()
	{
		if ( fState == States.ENABLED )
		{
			fState = States.DISABLED;
			fResume.setEnabled( true );
			fCruiseControlView.set( false );
		}
	}
	
	public synchronized void engineOff()
	{
		fCruiseControlView.set( false );
		fCruiseControlView.setSpeed( 0 );
		fEnable.setEnabled( false );
		fSet.setEnabled( false );
		fResume.setEnabled( false );
		fCCThread = null;
		fEnable.setText( "On" );
		fState = States.OFF;
	}
    
	public void run()
	{
		try
		{
			synchronized(this)
			{
				while ( fCCThread != null )
				{
					if ( fState == States.ENABLED )
					{
						//The feedback control of the system						
						double lError = (fTargetSpeed - fCarSpeedController.getSpeed()) * 100.0 / fTargetSpeed;

						fCarSpeedController.setThrottle( lError ); 
					}
					
					wait( 050 ); // Cycling every 50 milliseconds
				}
			}
		}
		catch ( InterruptedException e )
		{
			// empty on purpose
		}
		
		fCCThread = null;
	}
}
