package gautam.cs.uic.edu.huntingforghopher;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class HuntingAdapter extends BaseAdapter {

    private Context mContext;
    private Gopher[] gophers;
    //private String [] marray;

    // constructor
    public HuntingAdapter(Context context) {
        this.mContext = context;
        this.gophers = new Gopher[100];
        //this.marray = marray;
        createGopherHoles();
    }

    private void createGopherHoles() {
        for(int i = 0;  i < 100; ++i){
            gophers[i] = new Gopher(i);
        }
    }

    @Override
    public int getCount() {
        return gophers.length;
    }

    @Override  // TODO: ???
    public long getItemId(int position) {
        return 0;
    }

    @Override  // Return Gopher at position
    public Object getItem(int position) {
        return this.gophers[position];
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Gopher gopher = this.gophers[position];
        TextView textView = new TextView(mContext);
        //ListView listView = new ListView(mContext);
        textView.setText(String.valueOf(position));
        textView.setBackgroundColor(gopher.getColor());

        return textView;
    }
}
