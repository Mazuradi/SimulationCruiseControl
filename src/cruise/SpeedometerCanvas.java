//Speedometer canvas widget

package cruise;

import java.awt.BasicStroke;
import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;

public class SpeedometerCanvas extends Canvas
{
	private static final long serialVersionUID = 1L;

    private Font fBigFont;
    private Font fSmallFont;

    private double fSpeedValue;
    private double fDistanceValue;
    private double fThrottleValue;
    private double fBrakeValue;
    private boolean fIgnitionOn;

    public void setSpeed( double aSpeedValue )
    {
    	if ( aSpeedValue != fSpeedValue )
    	{
    		fSpeedValue = aSpeedValue;
    		
    		repaint();
    	}
    }

    private int fDistanceDigits[];
    
    private void mapDistance()
    {
    	// distance measured in meters, display in meters/100
        int lDistance = (int)fDistanceValue / 10;
        
        // map 5 digits of distance
        for ( int i = 4; i >= 0 ; i-- )
        {            
            lDistance /= 10;
            fDistanceDigits[i] = lDistance % 10;
        }
    }
    
    public void setDistance( double aDistanceValue )
    {
    	if ( aDistanceValue != fDistanceValue )
    	{
    		fDistanceValue = aDistanceValue;
    		
    		mapDistance();
    		
    		repaint();
    	}
    }

    public void setThrottle( double aThrottleValue )
    {
    	if ( aThrottleValue != fThrottleValue )
    	{
    		fThrottleValue = aThrottleValue;
    		fBrakeValue = 0.0;
    		
    		repaint();
    	}
    }

    public void setBrake( double aBrakeValue )
    {
    	if ( aBrakeValue != fBrakeValue )
    	{
    		fBrakeValue = aBrakeValue;
    		fThrottleValue = 0.0;
    		
    		repaint();
    	}
    }
    public void setIgnitionOn()
    {
    	fIgnitionOn = true;
    	
    	repaint();
    }

    public void setIgnitionOff()
    {
    	fIgnitionOn = false;
    	
		repaint();
    }

    public void clear()
    {
    	fIgnitionOn = false;
    	fSpeedValue = 0.0;
    	fDistanceValue = 0.0;
    	fBrakeValue = 0.0;
    	fThrottleValue = 0.0;
    	mapDistance();
    	
		repaint();
    }
    
	public SpeedometerCanvas()
	{
		super();
		
		setPreferredSize( new Dimension( 300, 300 ) );

		fBigFont = new Font( "Chalkboard", Font.BOLD, 18 );
		fSmallFont = new Font( "Chalkboard", Font.BOLD, 14 );
		
 		fSpeedValue = 0;
		fDistanceValue = 0;
		fThrottleValue = 0.0;
		fBrakeValue = 0.0;
		fIgnitionOn = false;
		
		fDistanceDigits = new int[5];
	}
	
	private void drawSpeedometer( Graphics2D g, int aXCenter, int aYCenter, int aRadius )
	{
        g.setFont( fSmallFont );

		g.setStroke( new BasicStroke( 2.0f ) );
        g.setColor( Color.WHITE );

        g.drawArc( aXCenter-aRadius, aYCenter-aRadius, aRadius*2, aRadius*2, 0, 360 );

        for ( int i = 0; i <= 12; i++ )
        {
        	// create markers from 210 to 330 degrees via 90 degrees (top) 
        	int lAngleInDeg = 210 - i * 20;
        	
        	if ( lAngleInDeg < 0 )
        	{
        		lAngleInDeg += 360;
        	}
        	
        	double lAngleInRad = (lAngleInDeg * Math.PI) / 180.0;
        	
        	// compute position of X and Y offsets
            int lXOffset = aXCenter + (int)(aRadius * Math.cos( lAngleInRad ));
            int lYOffset = aYCenter - (int)(aRadius * Math.sin( lAngleInRad ));

            // draw maker line
            g.drawLine( aXCenter, aYCenter, lXOffset, lYOffset );
            
            // place number
            lXOffset = aXCenter + (int)((aRadius+10) * Math.cos( lAngleInRad ));
            lYOffset = aYCenter - (int)((aRadius+10) * Math.sin( lAngleInRad ));

            // a bit of adjustment (optional)
            if ( i <= 5 )
            {
            	lXOffset -=15;
            }

            if ( i == 6  )
            {
            	lXOffset -=10;
            }

            if ( i == 7 )
            {
            	lXOffset -=5;
            }

            g.drawString( String.valueOf( i * 20 ), lXOffset, lYOffset );
        }

        // draw current speed
        g.setColor( Color.GREEN );
        g.fillArc( aXCenter-aRadius+2, 
        		   aYCenter-aRadius+2, 
        		   aRadius*2-2, 
        		   aRadius*2-4,
        		   210, 
        		   (int)fSpeedValue != 0 ? -((int)fSpeedValue) : -1 );

        g.setColor( Color.BLACK );
        // move by (10,10)
        g.fillArc( aXCenter-aRadius+10, 
        		   aYCenter-aRadius+10, 
        		   aRadius*2-20, 
        		   aRadius*2-20, 
        		   0, 
        		   360 );

        // draw ignition status
        g.setColor( Color.WHITE );
    	g.drawString( "Ignition", aXCenter-40, aYCenter+50 );
        
        if ( fIgnitionOn )
        {
            g.setColor( Color.GREEN );
        }
        else
        {
            g.setColor( Color.RED );
        }
        
        g.fillArc( aXCenter+30, aYCenter+40, 12, 12, 0, 360 );
	}
	
	private void drawOdometer( Graphics2D g, int aXCenter, int aYCenter, int aCWidth, int aCHeight  )
	{
        for ( int i = 0; i < 5; i++ )
        {
            g.setColor( Color.WHITE );
            g.drawRect( aXCenter + (aCWidth + 4)*i, aYCenter, aCWidth + 4, aCHeight + 2 );
            
            if (i == 4 )
            {
            	g.setColor( Color.RED );
            }
            else
            {
            	g.setColor( Color.YELLOW );
            }
            
            g.drawString( String.valueOf( fDistanceDigits[i] ),
            		      aXCenter+(aCWidth+4)*i+3,
            		      aYCenter+aCHeight-4 );
        }
	}
	
    private void drawControl( Graphics2D g, String aName, int aPos, int aSetting, Color aColor ) 
    {
    	int lXPos = getWidth()/2 + (aPos == 0 ? -80 : 30);
    	int lYPos = getHeight() - 60;
    	
        g.setFont( fSmallFont );
        g.setColor( Color.WHITE );
        g.drawString( aName, lXPos, lYPos );

		g.setStroke( new BasicStroke( 1.0f ) );
        g.drawRect( lXPos, lYPos + 15, 51, 16 );
        g.setColor( aColor );
        g.fillRect( lXPos + 1, lYPos + 16, aSetting, 15 );
    }

	public void update( Graphics g )
	{
		Image lOff = createImage(getWidth(), getHeight());
		
		Graphics gg = lOff.getGraphics();
		
        gg.setColor( Color.BLACK );
        gg.fillRect( 0, 0, getWidth(), getHeight() );
        
        drawSpeedometer( (Graphics2D)gg, getWidth()/2, getHeight()/2-20, 80 );
        
        // calculate font size
        String lZero = "0";

        gg.setFont( fBigFont );
        FontMetrics lFM = g.getFontMetrics();
        
        int lCWidth = lFM.stringWidth( lZero );
        int lCHeight = lFM.getHeight();
        int lXCenter = getWidth()/2 - (lCWidth+4)*5/2;
        int lYCenter = getHeight()/2-40;

        drawOdometer( (Graphics2D)gg, lXCenter, lYCenter, lCWidth, lCHeight );
        drawControl(  (Graphics2D)gg, "Throttle", 0, (int)(fThrottleValue * 5.0), Color.GREEN );
        drawControl(  (Graphics2D)gg, "Brake", 1, (int)(fBrakeValue * 5.0), Color.RED );
        
        g.drawImage(lOff, 0, 0, getWidth(), getHeight(), null);
	}

	public void paint( Graphics g )
	{
		update( g );
	}
}
