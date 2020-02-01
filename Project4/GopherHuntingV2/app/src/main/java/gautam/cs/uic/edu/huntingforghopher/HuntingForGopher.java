package gautam.cs.uic.edu.huntingforghopher;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Random;

public class HuntingForGopher extends AppCompatActivity {
    int newMode;
    ArrayAdapter<String> adapter;
    ArrayList<Integer> player1_moves = new ArrayList<Integer>();
    ArrayList<Integer> player2_moves = new ArrayList<Integer>();
    ArrayList<String> temp = new ArrayList<>();
    protected int winningHole;
    protected TextView currMode;
    protected Button playButton;
    protected GridView gopherGrid;
    protected HuntingAdapter ghAdapter;
    protected Random random = new Random();
    protected PlayerHandlerThread p1;
    protected PlayerHandlerThread p2;

    protected int CURRENT_MODE;
    protected int currentTurn;
    protected final int GUESS_BY_GUESS_MODE = 1;
    protected final int CONTINUOUS_MODE = 2;

    // UI Handler cases
    protected final int FIRST_GUESS_MADE = 3;
    protected final int NEXT_MOVE = 4;
    protected final int UPDATE_GOPHER_HOLES = 7;

    // Player Handler cases
    protected final int MAKE_GUESS = 6;
    protected final int GAME_IS_OVER = 8;
    protected final int TEST = 10;

    private ArrayList<Integer> heuristic(int guess, int id)
    {
    /*
    0 : left (-1)
    1 : right (+1)
    2 : down (-10)
    3 : up (+10)
    4 : diagonal up right (-9)
    5 : diagonal up left (-11)
    6 : diagonal down left (+9)
    7 : diagonal down right (+11)
     */
        ArrayList<Integer> nearmiss = new ArrayList<Integer>();

        ArrayList<Integer> closemiss = new ArrayList<Integer>();
        ArrayList<Integer> empty = new ArrayList<Integer>();
        for(int i = 0;i<8;i++)
        {
            empty.add(0);
        }
        int  row = 0;
        int col = 0;
        int temporary = 0;
        int flag_nearmiss = 0;
        int flag_closemiss = 0;
        int[][] multD = new int[10][10];
        for(int i = 0; i<10;i++)
        {
            for(int j = 0; j<10; j++)
            {

                multD[i][j] = temporary;
                temporary ++;
            }
        }
        col = guess % 10;
        row = guess / 10;
        try
        {
            nearmiss.add(multD[row][col-1]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row][col+1]);
        }
        catch (Exception e) {}

        try
        {
            nearmiss.add(multD[row-1][col]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row+1][col]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row-1][col+1]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row-1][col-1]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row+1][col-1]);
        }
        catch (Exception e) {}
        try
        {
            nearmiss.add(multD[row+1][col+1]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row][col-2]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row][col+2]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row-2][col]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row+2][col]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row-2][col+2]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row-2][col-2]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row+2][col-2]);
        }
        catch (Exception e) {}
        try
        {
            closemiss.add(multD[row+2][col+2]);
        }
        catch (Exception e) {}

        for(int i = 0; i<nearmiss.size();i++)
        {
            if(nearmiss.get(i) == winningHole)
            {
                flag_nearmiss = 1;
            }
        }

        for(int i = 0; i<closemiss.size();i++)
        {
            if(closemiss.get(i) == winningHole)
            {
                flag_closemiss = 1;
            }
        }

        if(flag_nearmiss == 1)
        {
            if(id == 1)
            {
                temp.add("Nearmiss");
            }
            else
            {
                temp.add("Nearmiss");
            }
            return nearmiss;
        }
        else if(flag_closemiss == 1)
        {
            if(id == 1)
            {
                temp.add("Closemiss");
            }
            else
            {
                temp.add("Closemiss");
            }
            return closemiss;
        }
        else {
            return empty;

        }
    }

    // UI Handler
    protected Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            Gopher gh;
            Message newMSG;
            Message toThread = uiHandler.obtainMessage(7);
            int guess;
            int what = msg.what;
            switch (what) {
                case FIRST_GUESS_MADE:
                    guess = msg.arg2;
                    gh = (Gopher) ghAdapter.getItem(guess);
                    gh.setAsGuessed();
                    gh.setColor(Color.BLUE);
                    ghAdapter.notifyDataSetChanged();
                    Log.i("FIRST_GUESS_MADE","turn = " + currentTurn);
                    Log.i("FIRST_GUESS_MADE", "first guess = " + guess);

                    if(winningHole == guess) {
                        // Check turn for winner, stop threads, end game
                        Log.i("WINNER","Turn = " + currentTurn);
                        //Toast.makeText(HuntingForGopher.this, "Winning Player" + msg.arg1 , Toast.LENGTH_LONG).show();
                        temp.add("Winner is Player: " + winningHole);
                        adapter.notifyDataSetChanged();
                        break;
                    }
                    currentTurn++;
                    break;
                case NEXT_MOVE:
                    if(newMode == CONTINUOUS_MODE)
                    {
                        p1.addMessage(MAKE_GUESS, 0);

                        p2.addMessage(MAKE_GUESS, 1);}
                    else {
                        if(currentTurn%2 == 1) {

                            p1.addMessage(MAKE_GUESS, 0);
                        } else {

                            p2.addMessage(MAKE_GUESS, 1);
                        }
                    }

                    break;
                case UPDATE_GOPHER_HOLES:
                    // Check if guess ws already guessed before
                    guess = msg.arg2;
                    gh = (Gopher) ghAdapter.getItem(guess);

                    if(gh.wasGuessed()) {
                        String response = "DISASTER: Hole already guessed!";
                        Toast.makeText(getApplicationContext(), response, Toast.LENGTH_SHORT).show();
                    } else {// update ui
                        gh.setAsGuessed();
                        gh.setColor(Color.BLUE);
                        ghAdapter.notifyDataSetChanged();

                    }

                    if(winningHole == guess) {
                        // Check turn for winner, stop threads, end game
                        Log.i("WINNER","Turn = " + currentTurn);
                        temp.add("Winner is Player: " + winningHole);

                        break;
                    } else {
                        newMSG = uiHandler.obtainMessage(NEXT_MOVE);
                        uiHandler.sendMessage(newMSG);
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gopher_hunting);
        ListView lv = findViewById( R.id.listview);

        // Create texts button and grid
        currMode = findViewById(R.id.gameCurrModeTxt);
        playButton = findViewById(R.id.playGameButton);
        gopherGrid = findViewById(R.id.gameGrid);
        ghAdapter = new HuntingAdapter(this);
        gopherGrid.setAdapter(ghAdapter);
        adapter = new ArrayAdapter<>(this, R.layout.list_row, R.id.name, temp);
        lv.setAdapter(adapter);

        // Randomly select a hole which contains gopher
        winningHole = random.nextInt(100);
        Log.i("Winning Hole: ", "" + winningHole);

        // Create and start threads
        p1 = new PlayerHandlerThread("Player1", uiHandler);
        p2 = new PlayerHandlerThread("Player2", uiHandler);
        p1.start();
        p2.start();

        // Get selected initial mode
        Intent intent = getIntent();
        newMode = intent.getIntExtra("Mode", GUESS_BY_GUESS_MODE);
        changeModeText(newMode);
        currentTurn = 1;

        // Set listener for button, switched current mode

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playButton.setVisibility(View.GONE);
                startGame();
            }
        });
    }


    // FIXME: Currently only plays in Continuous mode
    public void startGame() {
        makeFirstGuesses();
        // Afterwards, p1 goes again (turn 3)
        Message msg = uiHandler.obtainMessage(NEXT_MOVE);
        uiHandler.sendMessage(msg);
    }

    // Create player threads and have them make their first guess
    private void makeFirstGuesses() {
        // Tell players to make first guess through a runnable
        p1.addRunnable(new Runnable() {
            @Override
            public void run() {
                // Make first guess randomly, change underlying data
                int guess = random.nextInt(50);
                Log.i("NEXT_MOVE: ", "Player 1's Turn = " + currentTurn + "guess" + guess);
                player1_moves.add(guess);
                temp.add("Player 1 "+player1_moves.get(player1_moves.size()-1));
                // Tell UI handler we're done making our guess
                Message msg = uiHandler.obtainMessage(FIRST_GUESS_MADE);
                msg.arg2 = guess;
                msg.arg1 = 1;
                uiHandler.sendMessage(msg);
            }
        });

        p2.addRunnable(new Runnable() {
            @Override
            public void run() {
                // Make first guess randomly, change underlying data
                int guess = random.nextInt(50) + 50;
                player2_moves.add(guess);
                temp.add("Player 2 "+player2_moves.get(player2_moves.size()-1));
                Log.i("NEXT_MOVE: ", "Player 2's Turn = " + currentTurn + "guess" + guess);
                // Tell UI handler we're done making our guess
                Message msg = uiHandler.obtainMessage(FIRST_GUESS_MADE);
                msg.arg2 = guess;
                msg.arg1 = 2;
                uiHandler.sendMessage(msg);
            }
        });
    }




    // Change the mode text
    public void changeModeText(int newMode) {
        if (newMode == CONTINUOUS_MODE) {
            CURRENT_MODE = CONTINUOUS_MODE;
            currMode.setText(R.string.gameTextModeCNT);
        } else {
            CURRENT_MODE = GUESS_BY_GUESS_MODE;
            currMode.setText(R.string.gameTextModeGBG);
        }
    }

    // Called at end of activity lifecycle
    // Destroy threads!
    @Override
    protected void onDestroy() {
        super.onDestroy();
        p1.quit();
        p2.quit();
        p1.interrupt();
        p2.interrupt();
    }


    /******** THREAD CLASS *******/
    public class PlayerHandlerThread extends HandlerThread {

        private Player1Handler p1Handler;
        private Player2Handler p2Handler;
        private Handler uiHandler;
        private Random random;


        // Constructor
        public PlayerHandlerThread(String name, Handler uiHandler) {
            super(name);
            this.random = new Random();
            this.uiHandler = uiHandler;
            Log.i("Constructor: ", "initialized");
        }


        // Required, handles messages and runnables
        @Override
        protected void onLooperPrepared() {
            super.onLooperPrepared();
            p1Handler = new Player1Handler(getLooper());
            p2Handler = new Player2Handler(getLooper());
        }


        // Used by UI thread to send message to worker thread
        public void addMessage(int message, int id){
            if (id == 0) {

                if (p1Handler != null) {
                    Message msg = p1Handler.obtainMessage(message);
                    p1Handler.sendMessageDelayed(msg, 1000);
                }
            }
            else {

                if (p2Handler != null) {
                    Message msg = p2Handler.obtainMessage(message);
                    p2Handler.sendMessageDelayed(msg, 1000);
                }
            }

        }


        // Used by UI thread to send runnable to worker thread
        public void addRunnable(Runnable runnable) {
            if(p2Handler != null) {
                p2Handler.post(runnable);
            }
            else {
                p1Handler.post(runnable);
            }
        }


        private class Player1Handler extends Handler {
            ArrayList<Integer> intarray = new ArrayList<Integer>();
            int isempty;

            public Player1Handler(Looper looper) {
                super(looper);
            }


            @Override
            public void    handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case MAKE_GUESS:
                        Log.i("Test", String.valueOf(msg.arg1));
                        intarray = heuristic(player1_moves.get(player1_moves.size() - 1),1);
                        int count = 0;
                        for(int i = 0;i<intarray.size();i++)
                        {

                            if(intarray.get(i) == 0)
                            {
                                count ++;
                            }
                        }
                        if(count == intarray.size())
                        {
                            isempty = 1;
                        }



                        Log.i("MAKE_GUESS: ", "Making move");
                        int g_temp = makeGuess();
                        Log.i("NEXT_MOVE: ", "Player 1's Turn = " + currentTurn + "guess" + g_temp);
                        currentTurn++;

                        Message uiMSG = uiHandler.obtainMessage(UPDATE_GOPHER_HOLES);
                        uiMSG.arg2 = g_temp;
                        uiMSG.arg1 = 1;
                        uiHandler.sendMessage(uiMSG);
                        break;
                    case GAME_IS_OVER:
                        Log.i("GAME_IS_OVER: ", "game is over");
                        break;
                    case TEST:





                        break;
                }
            }


            // Strategically guess a hole. Use adapter data to check
            // But don't change the data, let UI thread do that
            private int makeGuess() {
                Log.i("makeGuess: ", "Making Move");
                player1_moves.get(player1_moves.size()-1);

                int guess;


                if(isempty == 1)
                {
                    guess = random.nextInt(100-1) + 1;
                    Log.i("inif", String.valueOf(guess));
                }
                else
                {

                    guess = random.nextInt(intarray.size());
                    guess = intarray.get(guess);
                    Log.i("inelse", String.valueOf(guess));
                }
                Log.i("guess", String.valueOf(guess));
                player1_moves.add(guess);
                temp.add("Player 1 "+player1_moves.get(player1_moves.size()-1));
                isempty = 0;

                if(guess == winningHole)
                {
                    temp.add("Winner is Player: " + winningHole);
                    p1.quit();
                    p2.quit();
                    p1.interrupt();
                    p2.interrupt();
                }
                return guess;
            }
        }


        private class Player2Handler extends Handler {
            ArrayList<Integer> intarray = new ArrayList<Integer>();

            public Player2Handler(Looper looper) {
                super(looper);
            }


            @Override
            public void    handleMessage(Message msg) {
                super.handleMessage(msg);
                int what = msg.what;
                switch (what) {
                    case MAKE_GUESS:
                        Log.i("MAKE_GUESS: ", "Making move");
                        intarray = heuristic(player1_moves.get(player1_moves.size() - 1),2);
                        int g_temp = makeGuess();
                        Log.i("NEXT_MOVE: ", "Player 2's Turn = " + currentTurn + "guess" + g_temp);
                        currentTurn++;
                        Message uiMSG = uiHandler.obtainMessage(UPDATE_GOPHER_HOLES);
                        uiMSG.arg2 = g_temp;
                        uiMSG.arg1 = 2;
                        uiHandler.sendMessage(uiMSG);
                        break;
                    case GAME_IS_OVER:
                        Log.i("GAME_IS_OVER: ", "game is over");
                        break;
                    case TEST:
                        Log.i("Test", String.valueOf(msg.arg1));
                        break;
                }
            }


            // Strategically guess a hole. Use adapter data to check
            // But don't change the data, let UI thread do that
            private int makeGuess() {
                Log.i("makeGuess: ", "Making Move");
                int flag;
                int guess;
                do {
                    guess = random.nextInt(100 - 1) + 1;
                    if(player2_moves.contains(guess))
                    {
                        flag = 0;
                    }
                    else
                    {
                        flag = 1;
                    }
                }while(flag == 0);
                player2_moves.add(guess);
                temp.add("Player 2 "+player2_moves.get(player2_moves.size()-1));
                if(guess == winningHole)
                {
                    temp.add("Winner is Player: " +  winningHole);
                    p1.quit();
                    p2.quit();
                    p1.interrupt();
                    p2.interrupt();
                }
                return guess;
            }
        }
    }
}