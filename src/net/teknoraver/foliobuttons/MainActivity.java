package net.teknoraver.foliobuttons;

import java.io.IOException;
import java.io.OutputStreamWriter;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import android.widget.SeekBar.OnSeekBarChangeListener;

public class MainActivity extends Activity implements OnSeekBarChangeListener, OnClickListener {
	private ToggleButton lig;
	private CheckBox same;
	private TextView t1, t2, t3, t4;
	private SeekBar b1, b2, b3, b4;
	private Button apply;
	private SharedPreferences sp;

	private final static String light = "light", lock = "lock", v1 = "v1", v2 = "v2", v3 = "v3", v4 = "v4";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		sp = PreferenceManager.getDefaultSharedPreferences(this);

		t1 = (TextView)findViewById(R.id.t1);
		t2 = (TextView)findViewById(R.id.t2);
		t3 = (TextView)findViewById(R.id.t3);
		t4 = (TextView)findViewById(R.id.t4);

		b1 = (SeekBar)findViewById(R.id.b1);
		b1.setProgress(sp.getInt(v1, 16));
		b1.setOnSeekBarChangeListener(this);

		b2 = (SeekBar)findViewById(R.id.b2);
		b2.setProgress(sp.getInt(v2, 16));
		b2.setOnSeekBarChangeListener(this);

		b3 = (SeekBar)findViewById(R.id.b3);
		b3.setProgress(sp.getInt(v3, 16));
		b3.setOnSeekBarChangeListener(this);

		b4 = (SeekBar)findViewById(R.id.b4);
		b4.setProgress(sp.getInt(v4, 16));
		b4.setOnSeekBarChangeListener(this);

		same = (CheckBox)findViewById(R.id.same);
		same.setChecked(sp.getBoolean(lock, true));

		lig = (ToggleButton)findViewById(R.id.light); 
		lig.setChecked(sp.getBoolean(light, true));

		apply = (Button)findViewById(R.id.apply); 
		apply.setOnClickListener(this);

		onProgressChanged(null, 0, false);
	}

	@Override
	public void onClick(View v) {
		try {
			Process su = Runtime.getRuntime().exec("su");
			OutputStreamWriter out = new OutputStreamWriter(su.getOutputStream());
			if(lig.isChecked())
				out.write("echo regctrl=0x8603 >/sys/devices/virtual/input/input7/tegra_capsensor\n");
			else
				out.write("echo regctrl=0x8600 >/sys/devices/virtual/input/input7/tegra_capsensor\n");
			int flag =
				b1.getProgress() + 1 << 24 |
				b2.getProgress() + 1 << 16 |
				b3.getProgress() + 1 <<  8 |
				b4.getProgress() + 1;
			out.write("echo sensitivity=0x" + Integer.toHexString(flag) + " >/sys/devices/virtual/input/input7/tegra_capsensor\n");
			out.close();

			SharedPreferences.Editor editor = sp.edit();
			editor.putBoolean(light, lig.isChecked());
			editor.putInt(v1, b1.getProgress());
			editor.putInt(v2, b2.getProgress());
			editor.putInt(v3, b3.getProgress());
			editor.putInt(v4, b4.getProgress());
			editor.putBoolean(lock, same.isChecked());
			editor.commit();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if(same.isChecked() && fromUser) {
			b1.setProgress(progress);
			b2.setProgress(progress);
			b3.setProgress(progress);
			b4.setProgress(progress);
		}
		t1.setText(b1.getProgress() * 100 / 255 + " %");
		t2.setText(b2.getProgress() * 100 / 255 + " %");
		t3.setText(b3.getProgress() * 100 / 255 + " %");
		t4.setText(b4.getProgress() * 100 / 255 + " %");
	}

	@Override
	public boolean onKeyDown(final int keyCode, final KeyEvent event) {
		String name = null;
		switch(keyCode) {
		case KeyEvent.KEYCODE_SEARCH: name = "search"; break;
		case KeyEvent.KEYCODE_BACK: name = "back"; break;
		case KeyEvent.KEYCODE_MENU: name = "menu"; break;
		}
		Toast.makeText(this, name + " pressed", Toast.LENGTH_SHORT).show();
		return false;
	}

	@Override public void onStartTrackingTouch(SeekBar seekBar) { }
	@Override public void onStopTrackingTouch(SeekBar seekBar) { }
}