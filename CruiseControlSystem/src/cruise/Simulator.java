//The car simulator module, main function for executing the program

package cruise;

public class Simulator
{
    private static void createAndShowGUI() 
    {
    	SimulatorFrame lFrame = new SimulatorFrame( "Cruise Control Simulator" );
    	
    	lFrame.setVisible( true );
    }

    public static void main(String[] args) 
    {
        //Schedule event-dispatching thread job.
        //creating and showing this application's GUI.
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}
