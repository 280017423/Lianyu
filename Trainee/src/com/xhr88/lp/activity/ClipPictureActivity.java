package com.xhr88.lp.activity;

import java.io.File;
import java.io.FileOutputStream;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.FileUtil;
import com.xhr.framework.util.UIUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.util.ImageUtil;
import com.xhr88.lp.widget.ClipView;

/**
 * 图片浏览、缩放、拖动、自动居中
 */
public class ClipPictureActivity extends TraineeBaseActivity implements OnTouchListener, OnClickListener {

	private static final String PHOTO_PATH = Environment.getExternalStorageDirectory() + "/DCIM/";
	private static final String TAG = "ClipPictureActivity";
	private static final int FRAME_WIDTH = 640;
	private static final int FRAME_HIGTN = 640;
	private static final int NONE = 0; // 初始状态
	private static final int DRAG = 1; // 拖动
	private static final int ZOOM = 2; // 缩放
	private static final int ARRAY_SIZE = 9;
	private static final float MAX_SCALE = 20f; // 最大缩放比例

	private final Matrix mMatrix = new Matrix();
	private final Matrix mSavedMatrix = new Matrix();
	private ImageView mSrcPicIv;
	private Button mOkBtn;
	private ClipView mClipview;
	private Bitmap mBitmap;

	private int mMode = NONE;
	private int mStatusBarHeight;
	private int mTitleBarHeight;
	private final int mOutputX = FRAME_WIDTH;
	private final int mOutputY = FRAME_HIGTN;

	private final PointF mPrev = new PointF();
	private final PointF mMid = new PointF();
	private float mDist = 1f;
	private Uri mUri;
	private boolean mIsBackground;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE); // 去掉标题栏
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.activity_image_cut);
		initVariables();
		initViews();
	}

	/**
	 * 初始化控件
	 * 
	 * @param
	 * @return void
	 * @throws
	 */
	private void initViews() {
		mSrcPicIv = (ImageView) findViewById(R.id.src_pic); // 获取控件
		mOkBtn = (Button) this.findViewById(R.id.btn_ok);
		mOkBtn.setOnClickListener(this);
		mBitmap = ImageUtil.getBitmapFromUri(mUri);
		if (null == mBitmap) {
			toast("图片加载失败");
			finish();
		}
		mBitmap = scale(mBitmap);// 宽度默认是屏幕宽度
		mSrcPicIv.setImageBitmap(mBitmap);
		mSrcPicIv.setOnTouchListener(this);
		center();
		mSrcPicIv.setImageMatrix(mMatrix);
		mClipview = (ClipView) this.findViewById(R.id.clipview);
	}

	private Bitmap scale(Bitmap bitmap) {
		float width = bitmap.getWidth();
		float scale = UIUtil.getScreenWidth(this) / width;
		Matrix matrix = new Matrix();
		matrix.postScale(scale, scale); // 长和宽放大缩小的比例
		Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
		return resizeBmp;
	}

	private void initVariables() {
		mUri = getIntent().getData();
		mIsBackground = getIntent().getBooleanExtra("isBackground", false);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {

		switch (event.getAction() & MotionEvent.ACTION_MASK) {
		// 主点按下
			case MotionEvent.ACTION_DOWN:
				mSavedMatrix.set(mMatrix);
				mPrev.set(event.getX(), event.getY());
				mMode = DRAG;
				break;
			// 副点按下
			case MotionEvent.ACTION_POINTER_DOWN:
				mDist = spacing(event);
				// 如果连续两点距离大于10，则判定为多点模式
				if (spacing(event) > 10f) {
					mSavedMatrix.set(mMatrix);
					midPoint(mMid, event);
					mMode = ZOOM;
				}
				break;
			case MotionEvent.ACTION_UP:
			case MotionEvent.ACTION_POINTER_UP:
				mMode = NONE;
				break;
			case MotionEvent.ACTION_MOVE:
				if (mMode == DRAG) {
					mMatrix.set(mSavedMatrix);
					mMatrix.postTranslate(event.getX() - mPrev.x, event.getY() - mPrev.y);
				} else if (mMode == ZOOM) {
					float newDist = spacing(event);
					if (newDist > 10f) {
						mMatrix.set(mSavedMatrix);
						float tScale = newDist / mDist;
						mMatrix.postScale(tScale, tScale, mMid.x, mMid.y);
					}
				}
				break;
			default:
				break;
		}
		mSrcPicIv.setImageMatrix(mMatrix);
		checkView();
		return true;
	}

	/**
	 * 限制最大最小缩放比例，自动居中
	 */
	private void checkView() {
		float[] p = new float[ARRAY_SIZE];
		mMatrix.getValues(p);
		if (mMode == ZOOM) {
			// if (p[0] < 1) {
			// mMatrix.setScale(mMinScaleR, mMinScaleR);
			// }
			if (p[0] > MAX_SCALE) {
				mMatrix.set(mSavedMatrix);
			}
		}
		// center();
	}

	private void center() {
		center(true, true);
	}

	/**
	 * 横向、纵向居中
	 */
	protected void center(boolean horizontal, boolean vertical) {
		RectF rect = null;
		Matrix m = new Matrix();
		m.set(mMatrix);
		try {
			rect = new RectF(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
			m.mapRect(rect);

			float height = rect.height();
			float width = rect.width();

			float deltaX = 0;
			float deltaY = 0;

			if (vertical) {
				// 图片小于屏幕大小，则居中显示。大于屏幕，上方留空则往上移，下放留空则往下移
				int screenHeight = UIUtil.getScreenHeight(this);
				if (height < screenHeight) {
					deltaY = (screenHeight - height) / 2 - rect.top;
				} else if (rect.top > 0) {
					deltaY = -rect.top;
				} else if (rect.bottom < screenHeight) {
					deltaY = mSrcPicIv.getHeight() - rect.bottom;
				}
			}

			if (horizontal) {
				int screenWidth = UIUtil.getScreenWidth(this);
				if (width < screenWidth) {
					deltaX = (screenWidth - width) / 2 - rect.left;
				} else if (rect.left > 0) {
					deltaX = -rect.left;
				} else if (rect.right < screenWidth) {
					deltaX = screenWidth - rect.right;
				}
			}
			mMatrix.postTranslate(deltaX, deltaY);
		} catch (Exception e) {
			finish();
		}
	}

	/**
	 * 两点的距离
	 */
	private float spacing(MotionEvent event) {
		float x = event.getX(0) - event.getX(1);
		float y = event.getY(0) - event.getY(1);
		return (float) Math.sqrt(x * x + y * y);
	}

	/**
	 * 两点的中点
	 */
	private void midPoint(PointF point, MotionEvent event) {
		float x = event.getX(0) + event.getX(1);
		float y = event.getY(0) + event.getY(1);
		point.set(x / 2, y / 2);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btn_ok:
				doActionAgain(R.id.btn_ok + "", new ActionListener() {
					@Override
					public void doAction() {
						Uri uri = getBitmap();
						if (null != uri) {
							Intent intent = new Intent();
							intent.putExtra("isBackground", mIsBackground);
							intent.setData(uri);
							setResult(Activity.RESULT_OK, intent);
						}
						finish();
					}
				});
				break;
			default:
				break;
		}
	}

	private Uri getBitmap() {
		Bitmap finalBitmap = null;
		getBarHeight();
		Bitmap screenShoot = takeScreenShot();
		if (null == screenShoot) {
			EvtLog.d(TAG, "获取缓存图片失败，再次调用方法获取图片");
			screenShoot = takeScreenShot();
		}
		if (null == screenShoot) {
			return null;
		}

		try {
			int width = mClipview.getWidth();
			int height = mClipview.getHeight();
			finalBitmap = Bitmap.createBitmap(screenShoot, 2, (height - width) / 2 + mTitleBarHeight + mStatusBarHeight
					+ 2, width - 4, width - 4);
			if (ImageUtil.hasSdcard()) {
				FileUtil.prepareFile(PHOTO_PATH); // 准备文件夹
				String fileName = ImageUtil.getPhotoFileName();
				File file = new File(PHOTO_PATH, fileName);
				FileOutputStream out = new FileOutputStream(file);
				finalBitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
				return Uri.fromFile(file);
			} else {
				toast(getString(R.string.not_have_sd));
			}
		} catch (Exception e) {
			finish();
		}
		return null;
	}

	private void getBarHeight() {
		Rect frame = new Rect();
		this.getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);
		mStatusBarHeight = frame.top;
		int contenttop = this.getWindow().findViewById(Window.ID_ANDROID_CONTENT).getTop();
		mTitleBarHeight = contenttop - mStatusBarHeight;
	}

	private Bitmap takeScreenShot() {
		View view = this.getWindow().getDecorView();
		if (view != null) {
			view.setDrawingCacheEnabled(true);
			view.buildDrawingCache();
			return view.getDrawingCache();
		}
		return null;
	}
}
