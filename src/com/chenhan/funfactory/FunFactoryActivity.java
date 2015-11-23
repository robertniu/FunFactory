package com.chenhan.funfactory;
import java.util.HashMap;

import android.app.Activity;
import android.app.Service;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class FunFactoryActivity extends Activity implements SensorEventListener
{
    private static final String TAG = "FunFactoryActivity";
    private static final boolean D = true;
	Vibrator vibrator;
    long lastUpdate,lastShakeTime = 0;
    float x,y,last_x = 0,last_y = 0;
    int shake_threshold = 400;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    TextView tips;
    EditText shake_value;
    SoundPool soundPool;
	HashMap<Integer , Integer> soundMap 
	= new HashMap<Integer , Integer>();

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
        if(D) Log.e(TAG, "+++ ON CREATE +++");
		setContentView(R.layout.main);
		vibrator = (Vibrator)getSystemService(Service.VIBRATOR_SERVICE);
		mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		mAccelerometer= mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);	
		
		Button stop_playing = (Button)findViewById(R.id.stop_playing);
        stop_playing.setOnClickListener(stop_playing_listener);
        Button start_playing = (Button)findViewById(R.id.start_playing);
        start_playing.setOnClickListener(start_playing_listener);
        tips = (TextView)findViewById(R.id.tips);

        shake_value = (EditText)findViewById(R.id.shake_value);
        
        Button reset_shake = (Button)findViewById(R.id.reset_shake);
        reset_shake.setOnClickListener(reset_shake_listener);
        
		// ������������10����Ƶ������Ƶ��Ʒ��Ϊ5
		soundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);
		// load��������ָ����Ƶ�ļ��������������ص���ƵID���˴�ʹ��HashMap��������Щ��Ƶ��
		soundMap.put(1 , soundPool.load(this, R.raw.bomb , 1));
		soundMap.put(2 , soundPool.load(this, R.raw.shot , 1));
		soundMap.put(3 , soundPool.load(this, R.raw.arrow , 1));

        
	}
	
	  
    private OnClickListener start_playing_listener = new OnClickListener(){
		public void onClick(View v) {
			StartEngin();		   
		}
    };
    
    
    private OnClickListener stop_playing_listener = new OnClickListener(){
		public void onClick(View v) {
			StopEngin();
		
		}
    };
    
    private OnClickListener reset_shake_listener = new OnClickListener(){
		public void onClick(View v) {
			ResetShake();		
		}
    };
    
    @Override
    public void onPause(){
    	mSensorManager.unregisterListener(this);
    	super.onPause();	
    }
    
	public void onStop()
	{
		if(D) Log.e(TAG, "+ onStop +");
		mSensorManager.unregisterListener(this);
		super.onStop();
		
	}
	
	public void StopEngin()
	{
		if(D) Log.e(TAG, "+ StopEngin +");
		mSensorManager.unregisterListener(this);
	}
	public void StartEngin()
	{
		if(D) Log.e(TAG, "+ StartEngin +");
		mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_GAME);
	}
	public void ResetShake()
	{
		if(D) Log.e(TAG, "+ ResetShake +");
		shake_threshold=Integer.valueOf(shake_value.getText().toString());
		Toast.makeText(getApplicationContext(),shake_value.getText().toString() , Toast.LENGTH_LONG).show();
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(D) Log.e(TAG, "+ onTouchEvent +");
		makevibrator();
		return super.onTouchEvent(event);
	}
	
	public void makevibrator()
	{
		if(D) Log.e(TAG, "+ makevibrator +");
		Toast.makeText(this, "���ٶȷ�ֵ="+shake_threshold , 5000).show();
		// �����ֻ���2��
		vibrator.vibrate(2000);
		
		//��������
		soundPool.play(soundMap.get(1), 1, 1, 0, 0, 1);

	}
	


        public void onAccuracyChanged(Sensor sensor, int accuracy) {
        }
        
        public void onSensorChanged(SensorEvent e) {
        	if(D) Log.e(TAG, "+ onSensorChanged +");
            long curTime = System.currentTimeMillis();
            // detect per 100 Millis
            if ((curTime - lastUpdate) > 100) 
            {
                long diffTime = (curTime - lastUpdate);
                lastUpdate = curTime;
                //�������˼򻯣�û����z������
                x = e.values[SensorManager.DATA_X];
                y = e.values[SensorManager.DATA_Y];
                //z = Math.abs(values[SensorManager.DATA_Z]);
                float acceChangeRate = 0;
                // = Math.abs(x+y - last_x - last_y) / diffTime * 1000;  
                if(last_x != 0) acceChangeRate = Math.abs(x+y - last_x - last_y) / diffTime * 10000;
                 //�����趨2����ֵ��һ���Ǽ��ٶȵģ�һ����shake�ļ��ʱ���
                if (acceChangeRate > shake_threshold && curTime - lastShakeTime > 2000) 
                {

                    lastShakeTime = curTime;
                    makevibrator();//����shakeʱ�Ĵ�����, �Ե�̨�������ǻ���һ�׸�
                    
       
                }
                last_x = x;
                last_y = y;
            }
        }//onSensorChanged




}


	