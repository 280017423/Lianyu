package com.xhr88.lp.listener;

import io.rong.imkit.RCloudContext;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.UserInfo;
import io.rong.message.CommandNotificationMessage;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.ProfileNotificationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.xhr.framework.util.EvtLog;
import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.activity.MainActivity;
import com.xhr88.lp.activity.OnLineImageDetailActivity;
import com.xhr88.lp.activity.OtherHomeActivity;
import com.xhr88.lp.authentication.ActionResult;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.business.manager.UserMgr;
import com.xhr88.lp.business.request.MsgReq;
import com.xhr88.lp.common.ConstantSet;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;
import com.xhr88.lp.model.datamodel.UserInfoModel;
import com.xhr88.lp.util.LpMessage;

/**
 * Created by zhjchen on 1/29/15.
 */

/**
 * 融云SDK事件监听处理。 把事件统一处理，开发者可直接复制到自己的项目中去使用。
 * <p/>
 * 该类包含的监听事件有： 1、消息接收器：OnReceiveMessageListener。
 * 2、发出消息接收器：OnSendMessageListener。 3、用户信息提供者：GetUserInfoProvider。
 * 4、好友信息提供者：GetFriendsProvider。 5、群组信息提供者：GetGroupInfoProvider。
 * 6、会话界面操作的监听器：ConversationBehaviorListener。
 * 7、连接状态监听器，以获取连接相关状态：ConnectionStatusListener。 8、地理位置提供者：LocationProvider。
 */
public final class RongCloudEvent implements RongIM.OnReceiveMessageListener, RongIM.OnSendMessageListener,
		RongIM.GetUserInfoProvider, RongIM.GetFriendsProvider, RongIM.GetGroupInfoProvider,
		RongIM.ConversationBehaviorListener, RongIM.ConnectionStatusListener {

	private static final String TAG = "RongIM";

	private static RongCloudEvent mRongCloudInstance;

	private Context mContext;

	/**
	 * 初始化 RongCloud.
	 * 
	 * @param context
	 *            上下文。
	 */
	public static void init(Context context) {

		if (mRongCloudInstance == null) {

			synchronized (RongCloudEvent.class) {

				if (mRongCloudInstance == null) {
					mRongCloudInstance = new RongCloudEvent(context);
				}
			}
		}
	}

	/**
	 * 构造方法。
	 * 
	 * @param context
	 *            上下文。
	 */
	private RongCloudEvent(Context context) {
		mContext = context;
		initDefaultListener();
	}

	/**
	 * RongIM.init(this) 后直接可注册的Listener。
	 */
	private void initDefaultListener() {
		RongIM.setGetUserInfoProvider(this, true);// 设置用户信息提供者。
		RongIM.setGetFriendsProvider(this);// 设置好友信息提供者.
		RongIM.setGetGroupInfoProvider(this);// 设置群组信息提供者。
		RongIM.setConversationBehaviorListener(this);// 设置会话界面操作的监听器。
	}

	/*
	 * 连接成功注册。 <p/> 在RongIM-connect-onSuccess后调用。
	 */
	public void setOtherListener() {
		RongIM.getInstance().setReceiveMessageListener(this);// 设置消息接收监听器。
		RongIM.getInstance().setSendMessageListener(this);// 设置发出消息接收监听器.
		RongIM.getInstance().setConnectionStatusListener(this);// 设置连接状态监听器。
	}

	/**
	 * 获取RongCloud 实例。
	 * 
	 * @return RongCloud。
	 */
	public static RongCloudEvent getInstance() {
		return mRongCloudInstance;
	}

	/**
	 * 接收消息的监听器：OnReceiveMessageListener 的回调方法，接收到消息后执行。
	 * 
	 * @param message
	 *            接收到的消息的实体信息。
	 * @param left
	 *            剩余未拉取消息数目。
	 */
	@Override
	public void onReceived(RongIMClient.Message message, int left) {

		RongIMClient.MessageContent messageContent = message.getContent();

		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			Log.d(TAG, "收到文本信息:" + textMessage.getContent());
			Log.d(TAG, "收到文本信息:" + textMessage.getExtra());

		} else if (messageContent instanceof ImageMessage) {// 图片消息
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onReceived-ImageMessage:" + imageMessage.getRemoteUri());
		} else if (messageContent instanceof VoiceMessage) {// 语音消息
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onReceived-voiceMessage:" + voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// 图文消息
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG, "onReceived-RichContentMessage:" + richContentMessage.getContent());
		} else if (messageContent instanceof ContactNotificationMessage) {// 联系人（好友）操作通知消息
			ContactNotificationMessage contactMessage = (ContactNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-ContactNotificationMessage:" + contactMessage.getMessage());
		} else if (messageContent instanceof ProfileNotificationMessage) {// 资料变更通知消息
			ProfileNotificationMessage profileMessage = (ProfileNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-ProfileNotificationMessage:" + profileMessage.getExtra());
		} else if (messageContent instanceof CommandNotificationMessage) {// 命令通知消息
			CommandNotificationMessage commantMessage = (CommandNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-CommandNotificationMessage:" + commantMessage.getName());
		} else if (messageContent instanceof InformationNotificationMessage) {// 小灰条消息
			InformationNotificationMessage infoMessage = (InformationNotificationMessage) messageContent;
			Log.d(TAG, "onReceived-GroupInvitationNotification:" + infoMessage.getMessage());
		} else if (messageContent instanceof LpMessage) {// 自定义群组消息
			LpMessage lpMessage = (LpMessage) messageContent;
			if (null != lpMessage && "1".equals(lpMessage.getExtra())) {
				Intent pushIntent = new Intent("com.xhr88.lp.FORCE_OFFLINE");
				pushIntent.putExtra("content", lpMessage.getContent());
				mContext.sendBroadcast(pushIntent);
				mContext.sendBroadcast(new Intent(MainActivity.ACTION_FINISH_ALL));
			}
			Log.d(TAG, "自定义推送信息:" + lpMessage.getContent());
			Log.d(TAG, "自定义推送信息:" + lpMessage.getExtra());
		} else {
			Log.d(TAG, "onReceived-其他消息，自己来判断处理" + messageContent);
		}

		/**
		 * 通知详细数量。
		 */
		Intent in = new Intent();
		in.setAction(MainActivity.ACTION_DMEO_RECEIVE_MESSAGE);
		in.putExtra("rongCloud", RongIM.getInstance().getTotalUnreadCount());
		mContext.sendBroadcast(in);

	}

	/**
	 * 消息在UI展示后执行/自己的消息发出后执行,无论成功或失败。
	 * 
	 * @param message
	 *            消息。
	 */
	@Override
	public RongIMClient.Message onSent(RongIMClient.Message message) {
		RongIMClient.MessageContent messageContent = message.getContent();
		if (messageContent instanceof TextMessage) {// 文本消息
			TextMessage textMessage = (TextMessage) messageContent;
			Log.d(TAG, "onSent-TextMessage:" + textMessage.getContent());
		} else if (messageContent instanceof ImageMessage) {// 图片消息
			ImageMessage imageMessage = (ImageMessage) messageContent;
			Log.d(TAG, "onSent-ImageMessage:" + imageMessage.getRemoteUri());
		} else if (messageContent instanceof VoiceMessage) {// 语音消息
			VoiceMessage voiceMessage = (VoiceMessage) messageContent;
			Log.d(TAG, "onSent-voiceMessage:" + voiceMessage.getUri().toString());
		} else if (messageContent instanceof RichContentMessage) {// 图文消息
			RichContentMessage richContentMessage = (RichContentMessage) messageContent;
			Log.d(TAG, "onSent-RichContentMessage:" + richContentMessage.getContent());
		} else {
			Log.d(TAG, "onSent-其他消息，自己来判断处理");
		}
		return message;
	}

	/**
	 * 用户信息的提供者：GetUserInfoProvider 的回调方法，获取用户信息。
	 * 
	 * @param userId
	 *            用户 Id。
	 * @return 用户信息，（注：由开发者提供用户信息）。
	 */
	@Override
	public RongIMClient.UserInfo getUserInfo(final String userId) {
		if (UserMgr.isMineUid(userId)) {
			UserInfoModel userInfoModel = UserDao.getLocalUserInfo().UserInfo;
			UserInfo userInfo = new RongIMClient.UserInfo(userId, userInfoModel.getNickname(), userInfoModel.getHeadimg());
			RCloudContext.getInstance().getUserInfoCache().put(userId, userInfo);
			return userInfo;
		}
		ConversationUserInfoModel model = UserDao.getConversationUserInfoModel(userId);
		if (null != model && !StringUtil.isNullOrEmpty(model.getUid())) {
			EvtLog.d("aaa", "本地已找到用户信息：" + model.getNickname());
			UserInfo userInfo = new RongIMClient.UserInfo(userId, model.getNickname(), model.getHeadimg());
			RCloudContext.getInstance().getUserInfoCache().put(userId, userInfo);
			return userInfo;
		}
		EvtLog.d("aaa", "本地没有找到用户信息：" + userId);
		new Thread(new Runnable() {

			@Override
			public void run() {
				ActionResult result = MsgReq.getUserInfo(userId);
				if (result.ResultCode.equals(ActionResult.RESULT_CODE_SUCCESS) && null != result) {
					ConversationUserInfoModel model = (ConversationUserInfoModel) result.ResultObject;
					if (null != model) {
						EvtLog.d("aaa", "从服务器获取头像成功：" + model.getNickname());
						UserInfo userInfo = new RongIMClient.UserInfo(userId, model.getNickname(), model.getHeadimg());
						RCloudContext.getInstance().getUserInfoCache().put(userId, userInfo);
					}
				}
			}
		}).start();
		return null;
	}

	/**
	 * 好友列表的提供者：GetFriendsProvider 的回调方法，获取好友信息列表。
	 * 
	 * @return 获取好友信息列表，（注：由开发者提供好友列表信息）。
	 */
	@Override
	public List<RongIMClient.UserInfo> getFriends() {
		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		return null;
		// return DemoContext.getInstance().getUserInfos();
	}

	/**
	 * 群组信息的提供者：GetGroupInfoProvider 的回调方法， 获取群组信息。
	 * 
	 * @param groupId
	 *            群组 Id.
	 * @return 群组信息，（注：由开发者提供群组信息）。
	 */
	@Override
	public RongIMClient.Group getGroupInfo(String groupId) {
		/**
		 * demo 代码 开发者需替换成自己的代码。
		 */
		return null;
		// return DemoContext.getInstance().getGroupMap().get(groupId);
	}

	/**
	 * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击用户头像后执行。
	 * 
	 * @param context
	 *            应用当前上下文。
	 * @param conversationType
	 *            会话类型。
	 * @param user
	 *            被点击的用户的信息。
	 * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
	 */
	@Override
	public boolean onClickUserPortrait(Context context, RongIMClient.ConversationType conversationType,
			RongIMClient.UserInfo user) {
		String uid = user.getUserId();
		if (StringUtil.isNullOrEmpty(uid) || UserMgr.isMineUid(uid)) {
			return false;
		}
		Intent intent1 = new Intent(context, OtherHomeActivity.class);
		intent1.putExtra("touid", user.getUserId());
		context.startActivity(intent1);
		return false;
	}

	/**
	 * 会话界面操作的监听器：ConversationBehaviorListener 的回调方法，当点击消息时执行。
	 * 
	 * @param context
	 *            应用当前上下文。
	 * @param message
	 *            被点击的消息的实体信息。
	 * @return 返回True不执行后续SDK操作，返回False继续执行SDK操作。
	 */
	@Override
	public boolean onClickMessage(Context context, RongIMClient.Message message) {
		Log.d(TAG, "onClickMessage");
		if (message.getContent() instanceof ImageMessage) {
			ImageMessage mImageMessage = (ImageMessage) message.getContent();
			Log.d("Begavior", "getRemoteUri():" + mImageMessage.getRemoteUri());
			ArrayList<String> imageList = new ArrayList<String>();
			if (null != mImageMessage && null != mImageMessage.getRemoteUri()) {
				imageList.add(mImageMessage.getRemoteUri().toString());
				Intent intent = new Intent(context, OnLineImageDetailActivity.class);
				intent.putExtra(ConstantSet.ONLINE_IMAGE_DETAIL, imageList);
				context.startActivity(intent);
			}
			return true;
		} else if (message.getContent() instanceof RichContentMessage) {
			RichContentMessage mRichContentMessage = (RichContentMessage) message.getContent();
			Log.d("Begavior", "extra:" + mRichContentMessage.getExtra());
		}
		Log.d("Begavior", message.getObjectName() + ":" + message.getMessageId() + "====" + message.toString() + "==="
				+ message.getExtra());
		return false;
	}

	/**
	 * 连接状态监听器，以获取连接相关状态:ConnectionStatusListener 的回调方法，网络状态变化时执行。
	 * 
	 * @param status
	 *            网络状态。
	 */
	@Override
	public void onChanged(ConnectionStatus status) {
		Log.d(TAG, "onChanged:" + status);
	}

}
