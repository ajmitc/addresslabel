package addresslabel.util;

public class Avery5160Template extends SheetTemplate
{
    /**
     * Set the top margin at .5 inches, side margin to .19 inches, vertical pitch to 1 inch, horizontal pitch to 2.75 inches, 
     * paper size to 8.5 by 11 inches, label height at 1 inch and label width at 2.63 inches. The number across is three and the number down is 10.
     */
    public Avery5160Template()
    {
        super( "Avery 5160" );
        _rows = 10;
        _columns = 3;
        _paperWidth  =  8.5;    // inches
        _paperHeight = 11.0;    // inches
        _labelWidth  =  2.625;  // inches
        _labelHeight =  1.0;    // inches
        _margins[ SheetTemplate.TOP    ] = 0.5;  
        _margins[ SheetTemplate.RIGHT  ] = 0.19;  // 0.21975
        _margins[ SheetTemplate.LEFT   ] = 0.19;  // 0.21975
        _margins[ SheetTemplate.BOTTOM ] = 0.5;
        _verticalPitch   = 1.0;    // inches
        _horizontalPitch = 2.75;   // inches
    }
}

