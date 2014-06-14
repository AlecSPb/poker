package com.porker;

import java.util.*;//ArrayList;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.graphics.*;//Bitmap;
//import android.graphics.Bitmap.Config;
//import android.graphics.BitmapFactory;
//import android.graphics.Canvas;
//import android.graphics.Color;
//import android.graphics.Rect;
//import android.graphics.drawable.Drawable;
import android.util.*;//DisplayMetrics;
//import android.util.Log;
//import android.util.SparseLongArray;
import android.view.*;//Gravity;
import android.view.ViewGroup.LayoutParams;
import android.view.View.*;//OnClickListener;
//import android.view.View.OnTouchListener;
//import android.view.Menu;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.ViewGroup.LayoutParams;
import android.widget.*;
//import static android.text.InputType.*;
//import static android.widget.GridLayout.*;

public class BlackJackActivity extends Activity implements OnTouchListener {

	private static final String TAG = BlackJackActivity.class
			.getCanonicalName();
	// private static int rows_in_view;
	private int col_in_row[] = { 1, 1, 1 }; // { 1, 1, 2 }; // { 1, 3, 1 }; //
	// private int neighborId[] = {-1, -1, -1};//{ -1, 0, 1};
	private ArrayList<View> mView;
	private int color[] = { android.graphics.Color.BLUE,
			android.graphics.Color.GREEN, android.graphics.Color.GRAY,
			android.graphics.Color.MAGENTA, android.graphics.Color.RED };

	// private coord mDimCard = new coord(73, 98);
	// private Bitmap mCardmap;
	private BJCard mCard;
	private int mPlayer;

	private List<playerInfo> plst;
	private int mComputer;
	private int mRid;
	private List<RuleType> playerlst;
	private Rule mRule;
	private Handler mHandler;

	private List<Integer> genListID() {

		Random rgen = new Random();
		List<Integer> lstID = null;
		int total = 0;
		int i;

		for (i = 0; i < col_in_row.length; i++) {
			total += col_in_row[i];
		}
		if (total > color.length) {
			Log.d(TAG, "Not enough color code!");
			return lstID;
		}

		lstID = new ArrayList<Integer>();
		for (i = 0; i < total; i++) {
			lstID.add(rgen.nextInt(100) + 1);
		}
		return lstID;
	}

	private void create(Context context) {

		Log.i(TAG, "Create");
		int rows_in_view = col_in_row.length;

		RelativeLayout v = (RelativeLayout) findViewById(R.id.layoutMaster);
		DisplayMetrics metrics = context.getResources().getDisplayMetrics();
		int elem = 0;
		GameView view = null;
		int row_size = metrics.heightPixels / rows_in_view;
		rows_in_view = col_in_row.length;

		int row = 0;
		
		mHandler = new Handler() {
			public void handleMessage(Message msg)
			{
				Log.i(TAG, "handleMessage begin");
				Log.i(TAG, "msg: " + msg.what);
				Bundle bundle = msg.getData();
				Boolean bTotal = bundle.getBoolean("getTotal");
				Log.i(TAG, "getResult " + bTotal);
				int playerId = bundle.getInt("playerId");

				if (bTotal)
				{
					playerInfo pinfo = plst.get(playerId);
					Log.i(TAG, "total: " + pinfo.getCardTotal());
				}
				
			}
		};
		//List<Integer> lstID = genListID();

		//int id = -1;
		//int oid = -1;
		//int pId = -1;
		Log.i(TAG, "Metrics: (" + metrics.widthPixels + ", "
				+ metrics.heightPixels + ")");

		Rect dim;

		for (row = 0; row < rows_in_view; row++) {

			int row_start = row * row_size;
			int col_size = metrics.widthPixels / col_in_row[row];
			//int first_row_elem_id = -1;

			for (int col = 0; col < col_in_row[row]; col++) {
				int col_start = col * col_size;

				dim = new Rect(col_start, row_start, col_start + col_size,
						row_start + row_size);

				playerInfo player = plst.get(elem);
				player.setDim(dim);
				player.setSPOS(new coord(col_start, row_start));
				/*
				 * Rect dim = new Rect(col_start, row_start, col_start +
				 * col_size, row_start + row_size);
				 * 
				 * view = createView(context, dim, color[elem], elem); id =
				 * lstID.get(elem); params = new
				 * RelativeLayout.LayoutParams(col_size, row_size); Log.d(TAG,
				 * "id: " + id + " oid:" + oid); if (row > 0) { // Log.d(TAG,
				 * "Element below: " + pId);
				 * params.addRule(RelativeLayout.BELOW, pId); }
				 * 
				 * if (col > 0) { // Log.d(TAG, "Element right: " + oid);
				 * params.addRule(RelativeLayout.RIGHT_OF, oid); }
				 * 
				 * view.setLayoutParams(params); view.setId(id);
				 * 
				 * mView.add(view); v.addView(view);
				 * view.setOnTouchListener(this); oid = id;
				 * 
				 * plst.get(elem).setVid(id); if (col == 0) { first_row_elem_id
				 * = id; }
				 */
				// Log.d(TAG, "get Pos at " + elem + "(" + view.getId() + " ): "
				// + view.getX()
				// + ", " + view.getY());
				elem++;
			}
			//pId = first_row_elem_id;
			//oid = -1;
		}
		RelativeLayout.LayoutParams params;

		dim = new Rect(0, 0, metrics.widthPixels, metrics.heightPixels);
		view = createView(context, dim, color[0], 0);
		params = new RelativeLayout.LayoutParams(metrics.widthPixels,
				metrics.heightPixels);
		view.setLayoutParams(params);
		v.addView(view);
		Button drawButton = new Button(this);
		drawButton.setText("PASS");
		params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
		params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		drawButton.setLayoutParams(params);
		v.addView(drawButton);
		view.setOnTouchListener(this);
		drawButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// switch to dealer
				for (int i = 0; i < plst.size(); i++) {
					playerInfo pinfo = plst.get(i);
					if (pinfo.getRuleType() == RuleType.PLAYER) {
						if (pinfo.isActive() == true)
							pinfo.setActive(false);
					} else if (pinfo.getRuleType() == RuleType.DEALER) {
						pinfo.setActive(true);
					}
				}
			}
		});
	}

	protected GameView createView(Context c, Rect dim, int colorId, int viewId) {

		RuleType rtype = playerlst.get(viewId);
		GameView view = new GameView(c, mHandler, plst, rtype);

		Log.d(TAG, "Rect: (" + dim.top + ", " + dim.left + ", " + dim.right
				+ ", " + dim.bottom + ")");

		view.setTop(dim.top);
		view.setLeft(dim.left);
		view.setRight(dim.right);
		view.setBottom(dim.bottom);

		view.setX((float) dim.left);
		view.setY((float) dim.top);

		view.setBackgroundColor(colorId);
		return view;

	}

	protected void cardSetup() {
		mCard = new BJCard(52);
		mCard.shuffle();
	}

	public BJCard getCard() {
		return mCard;
	}

	private void init() {
		int i;

		playerlst.add(RuleType.DEALER);
		playerlst.add(RuleType.NONE);
		for (i = 0; i < mComputer; i++) {
			playerlst.add(RuleType.COMPUTER);
		}
		for (i = 0; i < mPlayer; i++) {
			playerlst.add(RuleType.PLAYER);
		}

	}

	private void init_player_list() {
		int i;
		RuleType rtype;

		for (i = 0; i < playerlst.size(); i++) {
			rtype = playerlst.get(i);
			plst.add(new playerInfo(rtype));
		}
	}

	private void init_playing_card() {
		playerInfo pinfo = null;
		int i, j;
		Log.i(TAG, "Start # card in deck:" + mCard.count());
		for (i = 0; i < plst.size(); i++) {
			pinfo = plst.get(i);
			if (pinfo.getRuleType() != RuleType.NONE) {
				//Log.i(TAG, "MCard deck: " + mCard.getcard(0).getCardValue().getType()
				//		+ " value: " + mCard.getcard(0).getCardValue().getValue());
				pinfo.addcard(mCard.getcard(0));
				mCard.removecardAt(0);
				
			}
		}
		for (i = 0; i < plst.size(); i++) {
			pinfo = plst.get(i);
			Log.i(TAG, "player #" + i);
			for (j = 0; j < pinfo.getNumCard(); j++) {
				cardinfo cinfo = pinfo.getCardAt(j);
				Log.i(TAG, "card deck: " + cinfo.getCardValue().getType()
						+ " value: " + cinfo.getCardValue().getValue());
			}
		}
		Log.i(TAG, "End # card in deck:" + mCard.count());
	}

	protected void onCreate(Bundle savedInstanceState) {
		Log.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_black_jack);

		Log.i(TAG, "thread id: " + Thread.currentThread().getId());

		mComputer = getIntent().getIntExtra("computer", 0);
		mPlayer = getIntent().getIntExtra("user", 1);

		playerlst = new ArrayList<RuleType>();
		Log.i(TAG, "Player: " + mPlayer + " Computer: " + mComputer);
		plst = new ArrayList<playerInfo>();
		mView = new ArrayList<View>();
		mRid = -1;
		mRule = new Rule();

		cardSetup();

		init();
		init_player_list();
		init_playing_card();

		create(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.black_jack, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.i(TAG, "onResume");
	}

	private void update_player_list() {
		Log.i(TAG, "update_player_list");
		if (mView.size() != plst.size()) {
			Log.i(TAG, "Size mismatch");
			return;
		}

		for (int i = 0; i < mView.size(); i++) {
			playerInfo pinfo = plst.get(i);
			pinfo.setVid(mView.get(i).getId());
		}

	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		Log.i(TAG, "onConfigurationChanged");
		// Checks the orientation of the screen

		mView.clear();
		create(this);
		update_player_list();
		/*
		 * for (int i = 0; i < mView.size(); i++) { mRid = i; Log.i(TAG,
		 * "Start invalidate"); mView.get(i).invalidate(); Log.i(TAG,
		 * "End invalidate"); }
		 */
		if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
			Toast.makeText(this, "landscape", Toast.LENGTH_SHORT).show();
		} else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
			Toast.makeText(this, "portrait", Toast.LENGTH_SHORT).show();
		}

	}

	public int getPlayerId() {
		return mRid;
	}

	public void resetPlayerId() {
		mRid = -1;
	}

	private int getRegionId(int vid, int x, int y) {

		/*
		 * View v = null;
		 * 
		 * if (vid == -1) { for (int i = 0; i < mView.size(); i++) { v =
		 * mView.get(i); int xstart = (int) v.getX(); int xend = xstart +
		 * v.getWidth(); int ystart = (int) v.getY(); int yend = ystart +
		 * v.getHeight(); if (x > xstart && x < xend && y > ystart && y < yend)
		 * { vid = v.getId(); break; } } }
		 * 
		 * Log.i(TAG, "View id " + vid); if (vid != -1) { for (int i = 0; i <
		 * plst.size(); i++) { playerInfo player = plst.get(i); if (vid ==
		 * player.getVid() && player.getRuleType() == RuleType.PLAYER) { return
		 * i; } } }
		 */

		for (int i = 0; i < plst.size(); i++) {
			playerInfo player = plst.get(i);
			if (player.getRuleType() != RuleType.NONE) {
				Rect dim = player.getDim();
				if (x > dim.left && x < dim.right && y > dim.top
						&& y < dim.bottom) {
					return i;
				}
			}
		}

		Log.i(TAG, "Not a player ");
		return -1;
	}

	@Override
	public boolean onTouch(View vv, MotionEvent event) {
		// TODO Auto-generated method stub
		//Log.i(TAG, "ViewGroup Onclick event: " + event.getAction() + " pos: "
		//		+ event.getX() + ", " + event.getY());
		// Log.i(TAG, "thread id: " + android.os.Process.myTid());

		int rid = -1;
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			Log.i(TAG, "ACTION_DOWN");
			if ((mRid = getRegionId(vv.getId(), (int) event.getX(),
					(int) event.getY())) != -1) {
				playerInfo player = plst.get(mRid);
				if (player.isActive() == true) {
					player.addcard(mCard.getcard(0));
					mCard.removecardAt(0);
					int total = player.getCardTotal();
					boolean bIsBlow = player.test(total);
					Log.i(TAG, "Player BJ total=" + total);
					
				}
			}
			break;
		case MotionEvent.ACTION_UP:
			Log.i(TAG, "ACTION_UP");
			rid = getRegionId(vv.getId(), (int) event.getX(),
					(int) event.getY());
			if (rid != -1) {
				playerInfo player = plst.get(rid);
				Rect dim = player.getDim();
				vv.invalidate(dim.left, dim.top, dim.right, dim.bottom);
			}
			break;
		case MotionEvent.ACTION_MOVE:
			Log.i(TAG, "ACTION_MOVE");
			break;
		default:
			break;
		}

		return true;
	}

}