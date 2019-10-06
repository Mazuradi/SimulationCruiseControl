//The main window Frame for the GUI
//Adds in both canvas components for the car's speed
//and for the cruise control controls

package cruise;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class SimulatorFrame extends JFrame 
{
	private static final long serialVersionUID = 1L;
	
	CarSimulatorController fCarSimulator;
	CruiseControlController fCruiseControlSimulator;

    public SimulatorFrame( String aTitle )
    {
    	super( aTitle );
    	
        JFrame.setDefaultLookAndFeelDecorated( true );

        setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE );

        getContentPane().setLayout( new GridLayout( 1, 2, 1, 1 ) );

        fCarSimulator = new CarSimulatorController();
        fCruiseControlSimulator = new CruiseControlController( fCarSimulator );
        
        getContentPane().add( fCarSimulator );
        getContentPane().add( fCruiseControlSimulator );
        pack();
    }
}
