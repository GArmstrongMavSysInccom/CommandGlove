package mikroe.com.myapplication;

import android.app.Activity;
import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import java.io.IOException;

public class RelaysControl extends Activity {

    private BtInterface BT_RN41 = null;
    public MediaPlayer mediaPlayer;
    private long lastTime = 0;
    private ImageButton mLED4;
    private ImageButton mLED5;
    private ImageButton mLED6;
    private ImageButton mLED7;
    private ImageButton mLED8;

    private boolean led4_state = false;     // play music
    private boolean led5_state = false;
    private boolean led6_state = false;
    private boolean led7_state = false;
    private boolean led8_state = false;

    private boolean mSound = false;
    private double leftVolume = 0.5;
    private double rightVolume = 0.5;
    private int mSong = 0;
    private int mIndex = 0;
    private int[] mSongs;
    final private String TAG = "CommandGlove";
    private int index = 0;
//    private char[] last = { '0', '0', '0', '0', '0', '0', '0','0', '0', '0' };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_relays_control);
        mSongs = new int[]{R.raw.kalimba, R.raw.maid, R.raw.sleep};
        mediaPlayer = MediaPlayer.create(this, mSongs[0]);
        mSong = 0;
        mediaPlayer.setVolume((float) leftVolume, (float) rightVolume);

        mLED4 = (ImageButton) findViewById(R.id.btLED4);
        mLED4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                    String wr_data;
                    if (led4_state) {
                        wr_data = "LED4=0" + (char) 0x0A;
                        mLED4.setImageResource(R.drawable.on_switch_btn);
                        mSound = true;
                        mediaPlayer.start();

                    } else {
                        wr_data = "LED4=1" + (char) 0x0A;
                        mLED4.setImageResource(R.drawable.off_switch_btn);
                        mSound = false;
                        mediaPlayer.pause();
                    }

                    BT_RN41.SendData(wr_data);

                }
            }
        });

        mLED5 = (ImageButton) findViewById(R.id.btLED5);
        mLED5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                    String wr_data;
                    if (led5_state) {
                        wr_data = "LED5=0" + (char) 0x0A;
                    } else {
                        wr_data = "LED5=1" + (char) 0x0A;
                    }

                    BT_RN41.SendData(wr_data);

                }
            }
        });

        mLED6 = (ImageButton) findViewById(R.id.btLED6);
        mLED6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                    String wr_data;
                    if (led6_state) {
                        wr_data = "LED6=0" + (char) 0x0A;
                    } else {
                        wr_data = "LED6=1" + (char) 0x0A;
                    }

                    BT_RN41.SendData(wr_data);

                }
            }
        });

        mLED7 = (ImageButton) findViewById(R.id.btLED7);
        mLED7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                    String wr_data;
                    if (led7_state) {
                        wr_data = "LED7=0" + (char) 0x0A;
                        mLED7.setImageResource(R.drawable.on_switch_btn);
                    } else {
                        wr_data = "LED7=1" + (char) 0x0A;
                        mLED7.setImageResource(R.drawable.off_switch_btn);
                    }
                    led7_state = !led7_state;

                    BT_RN41.SendData(wr_data);

//                    wr_data = "GET"  + (char)0x0A;
//                    BT_RN41.SendData(wr_data);

                }

            }
        });

        mLED8 = (ImageButton) findViewById(R.id.btLED8);
        mLED8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
                    String wr_data;
                    if (led8_state) {
                        wr_data = "LED8=0" + (char) 0x0A;
                        mLED8.setImageResource(R.drawable.on_switch_btn);
                    } else {
                        wr_data = "LED8=1" + (char) 0x0A;
                        mLED8.setImageResource(R.drawable.off_switch_btn);
                    }
                    led8_state = !led8_state;
                    BT_RN41.SendData(wr_data);

//                    wr_data = "GET"  + (char)0x0A;
//                    BT_RN41.SendData(wr_data);

                }

            }
        });
        BT_RN41 = new BtInterface(handlerStatus, handler);
    }


    @Override
    protected void onResume() {
        super.onResume();
        if (BT_RN41 == null) {
            BT_RN41 = new BtInterface(handlerStatus, handler);
        }

    }


    @Override
    protected void onPause() {
        super.onPause();
        if (BT_RN41 != null) {
            BT_RN41.close();
            BT_RN41 = null;
        }

    }


    private void SyncData() {
        if (RN41.SOCKET == RN41.SOCKET_STATE.CONNECTED) {
            String wr_data = "GET" + (char) 0x0A;
            BT_RN41.SendData(wr_data);
        }
    }

    private void changeVolume(boolean dir) {
        if (dir) {                                  // up one octave
            leftVolume += .25;       //GAA 0.25 18Feb206
            rightVolume += .25;
        } else {                                    // down one octave
            leftVolume -= .25;
            rightVolume -= .25;
        }
        if (leftVolume > 1.0) leftVolume = 1.0;
        if (rightVolume > 1.0) rightVolume = 1.0;
        if (leftVolume < 0) leftVolume = 0;
        if (rightVolume < 0) rightVolume = 0;
        mediaPlayer.setVolume((float) leftVolume, (float) rightVolume);
        Toast.makeText(getApplicationContext(), "Volume" + leftVolume + rightVolume, Toast.LENGTH_SHORT).show();

    }  //  private void changeVolume ( )


    private int changesong(int index, int dir) {
        if (dir > 0) index++;
        else index--;
        if (index < 0) index = 2;
        if (index > 2) index = 0;
        return index;
    }  //      private int changesong (int index, int dir)

    private void play(int index) {
        index = (index) % 3;
        AssetFileDescriptor afd = this.getResources().openRawResourceFd(mSongs[index]);
        Log.i(TAG, "play() created afd");

        try {
            if (mediaPlayer == null) {
                mediaPlayer = new MediaPlayer();
            } else {
                mediaPlayer.reset();   // so can change data source etc.
            }
//            mediaPlayer.setOnErrorListener(this);
            mediaPlayer.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getDeclaredLength());
            mediaPlayer.prepare();
//            mediaPlayer.start();
            afd.close();
            Log.i(TAG, "play() completed try");
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        } catch (IllegalStateException e) {
            Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, "Unable to play audio queue do to exception: " + e.getMessage(), e);
        }
    }  //      private void play( int index )


    ////////////////////////////////////////////////////////////////////////////////////////////////
    //                                   BT RN41
    ////////////////////////////////////////////////////////////////////////////////////////////////

    final Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            String data = msg.getData().getString("receivedData");
            Boolean changed = Boolean.FALSE;
            int i = 0;
//            for(i=0;i<10;i++) { This crashes the app
//                if (data.charAt(i) != last[i]) changed = Boolean.TRUE;
//                last[i] = data.charAt(i);
//            }
//           if (changed) {
//               Toast.makeText(getApplicationContext(), data, Toast.LENGTH_SHORT).show();
//               changed =Boolean.FALSE;
//           }

            //parse received data
            index++;

            if (data.contains("SET")) {
//LED4
                if (data.charAt(4) == 49) {

                    if (!led4_state) {
                        led4_state = true;

                        if (!mSound) {
                            mLED4.setImageResource(R.drawable.on_switch_btn);
                            mSound = true;
                            mediaPlayer.start();
                        }
                    }  //   if (!led4_state)
                } else {

                    if (led4_state) {
                        led4_state = false;

                        if (mSound) {
                            mLED4.setImageResource(R.drawable.off_switch_btn);
                            mSound = false;
                            mediaPlayer.pause();
                        }
                    }  //  if (led4_state)
                } // else from led4
//LED5
                if (data.charAt(5) == 49) {

                    if (!led5_state) {
                        led5_state = true;
                        mLED5.setImageResource(R.drawable.on_switch_btn);
                        mLED6.setImageResource(R.drawable.off_switch_btn);
                        changeVolume(false);      // down 25%
                    }
                } else {

                    if (led5_state) {
                        led5_state = false;
                        mLED5.setImageResource(R.drawable.off_switch_btn);
                    }
                } // else from led5
//LED6
                if (data.charAt(6) == 49) {

                    if (!led6_state) {
                        led6_state = true;
                        mLED6.setImageResource(R.drawable.on_switch_btn);
                        mLED5.setImageResource(R.drawable.off_switch_btn);
                        changeVolume(true);      // up 25%
                    }
                } else {

                    if (led6_state) {
                        led6_state = false;
                        mLED6.setImageResource(R.drawable.off_switch_btn);
                    }
                } // else from led6
//LED7
                if (data.charAt(7) == 49) {

                    if (!led7_state) {
                        mLED7.setImageResource(R.drawable.on_switch_btn);
                        mLED8.setImageResource(R.drawable.off_switch_btn);
                        mLED4.setImageResource(R.drawable.on_switch_btn);  //PLAY
                        mSong = changesong(mSong, 1);  // next song
                        play(mSong);
                        mediaPlayer.start();
                        led7_state = true;
                        //                       Toast.makeText(getApplicationContext(), "LED7 ChngSong" +mSong, Toast.LENGTH_SHORT).show();

                    }       //    if (!led7_state)
                } else {          //  if(data.charAt(7) == 49){

                    if (led7_state) {
                        led7_state = false;
                        mLED7.setImageResource(R.drawable.off_switch_btn);
                    }       //  if ( led7_state)

                } // else from led7

//LED8
                if (data.charAt(8) == 49) {

                    if (!led8_state) {
                        led8_state = true;
                        mLED8.setImageResource(R.drawable.on_switch_btn);
                        mLED7.setImageResource(R.drawable.off_switch_btn);
                        mLED4.setImageResource(R.drawable.on_switch_btn);       //PLAY
                        mSong = changesong(mSong, -1);
                        play(mSong);
                        mediaPlayer.start();

                    }       //    if (!led8_state)
                } else { // if(data.charAt(8) == 48){

                    if (led8_state) {
                        led8_state = false;
                        mLED8.setImageResource(R.drawable.off_switch_btn);
                    }       //  if ( led8_state)

                }// else from led8

                long t = System.currentTimeMillis();
                if (t - lastTime > 100) {
                    lastTime = System.currentTimeMillis();
                }

            } //  if (data.contains("SET")) {
        }

    }; // end of Handler

        final Handler handlerStatus = new Handler() {
            public void handleMessage(Message msg) {
                int co = msg.arg1;
                if (co == 1) { //Connection success

                    Toast.makeText(getApplicationContext(), "RELAYS CONNECTED", Toast.LENGTH_LONG).show();

                    SyncData();

                } else if (co == 2) { //Connection error
                    Toast.makeText(getApplicationContext(), "ERROR!", Toast.LENGTH_LONG).show();

                }


            }
        };

    }

