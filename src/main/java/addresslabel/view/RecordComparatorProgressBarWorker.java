package addresslabel.view;

import addresslabel.Record;
import addresslabel.compare.RecordComparator;
import addresslabel.compare.RecordDiff;

import javax.swing.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class RecordComparatorProgressBarWorker extends SwingWorker<List<RecordDiff>, Integer> {
    private List<Record> records0;
    private List<Record> records1;
    private RecordComparatorProgressBarDialog dialog;

    public RecordComparatorProgressBarWorker(List<Record> records0, List<Record> records1, RecordComparatorProgressBarDialog dialog){
        super();
        this.records0 = records0;
        this.records1 = records1;
        this.dialog   = dialog;

        addPropertyChangeListener(
                new PropertyChangeListener() {
                    public  void propertyChange(PropertyChangeEvent evt) {
                        if ("progress".equals(evt.getPropertyName())) {
                            dialog.incProgress((Integer)evt.getNewValue());
                        }
                    }
                });
    }

    @Override
    protected List<RecordDiff> doInBackground() throws Exception {
        dialog.prepare(Math.max(records0.size(), records1.size()));
        List<RecordDiff> diffs = RecordComparator.getDiffs(this.records0, this.records1, this);
        return diffs;
    }

    @Override
    protected void process(List<Integer> chunks) {
        super.process(chunks);
    }

    @Override
    protected void done() {
        super.done();
        dialog.dispose();
    }

    public void publish(int number, String text){
        super.publish(number);
        //if (text != null){
            //dialog.setText(text);
        //}
    }
}
