package com.xhr88.lp.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;

import com.xhr.framework.app.XhrApplicationBase;

/**
 * Description the class 用户头像上传工具类
 * 
 * @author zou.sq
 * @version 2012-11-9 上午10:12:20 zou.sq 用户头像上传工具类
 */
public class ImageUtil {

	public static final int BUFFER_SIZE = 1024;
	public static final String TAG = "ImageUtil";
	private static final int INSAMPLE_SIZE = 8;
	private static final int QUALITY = 50;

	/**
	 * 判断是否有SD卡
	 * 
	 * @return true为有SDcard，false则表示没有
	 */
	public static boolean hasSdcard() {
		boolean hasCard = false;
		String state = Environment.getExternalStorageState();
		if (state.equals(Environment.MEDIA_MOUNTED)) {
			hasCard = true;
		}
		return hasCard;
	}

	/**
	 * 
	 * 使用系统当前日期加以调整作为照片的名称
	 * 
	 * @return String 文件拍照名字
	 * @throws
	 */
	public static String getPhotoFileName() {
		Date date = new Date(System.currentTimeMillis());
		SimpleDateFormat dateFormat = new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss", Locale.CHINA);
		return dateFormat.format(date) + ".jpg";
	}

	/**
	 * 检查是否有相机
	 * 
	 * @return boolean true为有相机，false则表示没有
	 */
	public static boolean hasCamera() {
		boolean hasCamera = false;
		PackageManager pm = XhrApplicationBase.CONTEXT.getPackageManager();
		if (pm.hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
			hasCamera = true;
		}
		return hasCamera;
	}

	/**
	 * 处理图片
	 * 
	 * @param bm
	 *            所要转换的bitmap
	 * @param newWidth
	 *            新的宽
	 * @param newHeight
	 *            新的高
	 * @return 指定宽高的bitmap
	 */
	public static Bitmap zoomImg(Bitmap bm, int newWidth, int newHeight) {
		// 获得图片的宽高
		int width = bm.getWidth();
		int height = bm.getHeight();
		// 计算缩放比例
		float scaleWidth = ((float) newWidth) / width;
		float scaleHeight = ((float) newHeight) / height;
		// 取得想要缩放的matrix参数
		Matrix matrix = new Matrix();
		matrix.postScale(scaleWidth, scaleHeight);
		// 得到新的图片
		Bitmap newbm = Bitmap.createBitmap(bm, 0, 0, width, height, matrix, true);
		return newbm;
	}

	/**
	 * 
	 * Uri转bitmap
	 * 
	 * @param uri
	 *            图像的uri对象
	 * @return Bitmap 对象
	 * @throws
	 */
	public static Bitmap getBitmapFromUri(Uri uri) {
		Bitmap bitmap = null;
		try {
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 4;
			InputStream inputStream = XhrApplicationBase.CONTEXT.getContentResolver().openInputStream(uri);
			bitmap = BitmapFactory.decodeStream(inputStream, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return bitmap;
	}

	/**
	 * 加载本地图片 http://bbs.3gstdy.com
	 * 
	 * @param url
	 *            本地图片路径
	 * @return Bitmap Bitmap对象
	 */
	public static Bitmap getLoacalBitmap(String url) {
		try {
			FileInputStream fis = new FileInputStream(url);
			BitmapFactory.Options options = new Options();
			// 降低图片质量，降低OOM几率
			options.inSampleSize = INSAMPLE_SIZE;
			options.inPreferredConfig = Config.ARGB_4444;
			return BitmapFactory.decodeStream(fis, null, options);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 根据指定的图像路径和大小来获取缩略图 此方法有两点好处： 1.
	 * 使用较小的内存空间，第一次获取的bitmap实际上为null，只是为了读取宽度和高度，
	 * 第二次读取的bitmap是根据比例压缩过的图像，第三次读取的bitmap是所要的缩略图。 2.
	 * 缩略图对于原图像来讲没有拉伸，这里使用了2.2版本的新工具ThumbnailUtils，使 用这个工具生成的图像不会被拉伸。
	 * 
	 * @param imagePath
	 *            图像的路径
	 * @param width
	 *            指定输出图像的宽度
	 * @param height
	 *            指定输出图像的高度
	 * @return 生成的缩略图
	 */
	public static Bitmap getImageThumbnail(String filePath, int width, int height) {
		final BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(filePath, options);
		options.inSampleSize = calculateInSampleSize(options, width, height);
		options.inJustDecodeBounds = false;
		Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
		int degree = readPictureDegree(filePath);
		if (degree != 0) {// 旋转照片角度
			bitmap = rotateBitmap(bitmap, degree);
		}
		return bitmap;
	}

	/**
	 * 
	 * @Name scanMedia
	 * @Description 及时扫描拍照后的照片，在相册就能看到
	 * @param context
	 *            上下文对象
	 * @param path
	 *            照片的路径
	 * 
	 */
	public static void scanMedia(Context context, String path) {
		File file = new File(path);
		Uri uri = Uri.fromFile(file);
		Intent scanFileIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, uri);
		context.sendBroadcast(scanFileIntent);
	}

	/*
	 * 压缩图片，处理某些手机拍照角度旋转的问题
	 */
	public static String compressImage(String filePath, File imageDir, String fileName) throws FileNotFoundException {

		Bitmap bm = getImageThumbnail(filePath, 480, 800);
		File outputFile = new File(imageDir, fileName);
		if (outputFile.exists()) {
			outputFile.delete();
		}
		FileOutputStream out = new FileOutputStream(outputFile);
		bm.compress(Bitmap.CompressFormat.JPEG, QUALITY, out);
		return outputFile.getPath();
	}

	// 计算图片的缩放值
	public static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
		final int height = options.outHeight;
		final int width = options.outWidth;
		int inSampleSize = 1;
		if (height > reqHeight || width > reqWidth) {
			final int heightRatio = Math.round((float) height / (float) reqHeight);
			final int widthRatio = Math.round((float) width / (float) reqWidth);
			inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
		}
		return inSampleSize;
	}

	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
				case ExifInterface.ORIENTATION_ROTATE_90:
					degree = 90;
					break;
				case ExifInterface.ORIENTATION_ROTATE_180:
					degree = 180;
					break;
				case ExifInterface.ORIENTATION_ROTATE_270:
					degree = 270;
					break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	public static Bitmap rotateBitmap(Bitmap bitmap, int degress) {
		if (bitmap != null) {
			Matrix m = new Matrix();
			m.postRotate(degress);
			bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
			return bitmap;
		}
		return bitmap;
	}

}
