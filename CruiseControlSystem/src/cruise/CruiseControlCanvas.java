//Car Simulator Controller involving GUI & thread cycling

package cruise;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;

public class CruiseControlCanvas extends Canvas
{
	private static final long serialVersionUID = 1L;

    private Font fBigFont;
    private Font fSmallFont;
    
    private int fSpeed;
    private boolean fIsEnabled;

    public void setSpeed( int aSpeed )
    {
    	if ( aSpeed != fSpeed )
    	{
    		fSpeed = aSpeed;

    		repaint();
    	}
    }

    public boolean get()
    {
    	return fIsEnabled;
    }

    public void set( boolean aIsEnabled )
    {
    	fIsEnabled = aIsEnabled;
    	
    	repaint();
    }

	public CruiseControlCanvas()
	{
		super();
		
		setPreferredSize( new Dimension( 180, 160 ) );
		
	    fBigFont = new Font( "Chalkboard", Font.BOLD, 18 );
	    fSmallFont = new Font( "Chalkboard", Font.BOLD, 14 );
	    
	    fSpeed = 0;
	    fIsEnabled = false;
	}

	public void update( Graphics g )
	{
        g.setColor( Color.DARK_GRAY );
        g.fillRect( 0, 0, getWidth(), getHeight() );

        g.setColor(Color.white);
        g.drawRect( 5, 5, getWidth() - 10, getHeight() - 10 );

        g.setFont( fBigFont );
        g.drawString( "Cruise Control", 30, 35 );

        g.setFont( fSmallFont );

        g.drawString( "Cruise Speed", 45, 75 );
        
        g.drawRect( 36, 55, 108, 60 );

        g.setFont( fBigFont );

        String lSpeed = String.valueOf( fSpeed ); 
        g.drawString( lSpeed, 75 + 5*(3-lSpeed.length()), 106 );

        g.setFont( fSmallFont );

        if ( fIsEnabled )
        {
        	g.drawString( "Enabeled", 45, 140 );
            g.setColor( Color.GREEN );
        }
        else
        {
            g.drawString( "Disabled", 45, 140 );
            g.setColor( Color.RED );
        }
        
        g.fillArc( 115, 125, 20, 20, 0, 360 );
	}

	public void paint( Graphics g )
	{
		update( g );
	}
}
