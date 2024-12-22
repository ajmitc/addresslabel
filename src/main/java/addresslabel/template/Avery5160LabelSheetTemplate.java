package addresslabel.template;

public class Avery5160LabelSheetTemplate extends LabelSheetTemplate {
    /**
     * Set the top margin at .5 inches, side margin to .19 inches, vertical pitch to 1 inch, horizontal pitch to 2.75 inches,
     * paper size to 8.5 by 11 inches, label height at 1 inch and label width at 2.63 inches. The number across is three and the number down is 10."""
     */
    public Avery5160LabelSheetTemplate() {
        super("Avery 5160 (1\" x 2 5/8\")");
        rows = 10;
        columns = 3;
        paperWidth = 8.5;    // inches
        paperHeight = 11.0;    // inches
        labelWidth = 2.625;  // inches
        labelHeight = 1.0;    // inches
        margins[LabelSheetTemplate.TOP] = 0.5;
        margins[LabelSheetTemplate.RIGHT] = 0.19;  // 0.21975
        margins[LabelSheetTemplate.LEFT] = 0.19;  // 0.21975
        margins[LabelSheetTemplate.BOTTOM] = 0.5;
        verticalPitch = 1.0;    // inches
        horizontalPitch = 2.75;   // inches
    }
}


