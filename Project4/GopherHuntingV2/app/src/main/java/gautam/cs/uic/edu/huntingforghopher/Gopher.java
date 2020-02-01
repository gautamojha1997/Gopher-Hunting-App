package gautam.cs.uic.edu.huntingforghopher;

import android.graphics.Color;

public class Gopher {

    private boolean guessed;
    private int position;
    private int color;

    // Default constructor
    public Gopher(int position) {
        this.guessed = false;
        this.position = position;
        this.color = Color.YELLOW;
    }

    public void setAsGuessed() { this.guessed = true; }
    public void setColor(int color) { this.color = color; }
    public boolean wasGuessed() { return this.guessed; }
    public int getPosition() { return this.position; }
    public int getColor() { return this.color; }
}
