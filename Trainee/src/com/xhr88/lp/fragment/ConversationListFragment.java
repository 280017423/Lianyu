package com.xhr88.lp.fragment;

import io.rong.imkit.RCloudContext;
import io.rong.imkit.Res;
import io.rong.imkit.RongIM;
import io.rong.imkit.common.RongConst;
import io.rong.imkit.data.DBHelper;
import io.rong.imkit.fragment.ActionBaseFragment;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.model.UIDiscussion;
import io.rong.imkit.model.UIGroup;
import io.rong.imkit.model.UIMessage;
import io.rong.imkit.utils.RongToast;
import io.rong.imkit.view.ActionBar;
import io.rong.imkit.view.LoadingDialog;
import io.rong.imkit.view.SelectDialog;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.RongIMClient.UserInfo;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xhr88.lp.R;
import com.xhr88.lp.adapter.ConversationListAdapter;

import de.greenrobot.event.EventBus;

public class ConversationListFragment extends ActionBaseFragment implements AdapterView.OnItemClickListener,
		AdapterView.OnItemLongClickListener, AbsListView.OnScrollListener, ConversationListAdapter.OnGetDataListener {
	public static final String TAG = "ConversationListFragment";
	protected static final int HANDLE_NOTIFY_ADAPTER = 1100;
	protected static final int HANDLE_NOTIFY_LOAD_DATA = 1101;
	private static final int GET_DISCUSSION_INFO = 1102;
	protected static final int START_CONVERSATION = 1103;
	protected static final int HANDLE_ONCLICK_ITEM = 1104;
	protected static final int START_CONVERSATION_CREATE_SUCCESS = 1105;
	protected static final int HANDLE_SETTING_FUNCTION_SEND_BROADCAST = 1106;
	protected static final int HANDLE_HAS_MESSAGE = 1107;
	protected static final int HANDLE_RE_LOAD_DATA = 1108;
	public static final String INTENT_PRIVATE_SELECT_PEOPLE = "intent_private_select_people";
	private ListView mLvConversation;
	protected ConversationListAdapter mConversationListAdapter;
	protected RongIMClient.ConversationType mConversationType;
	private LoadingDialog mDialog;
	private TextView mConnectStateTextView;
	protected UIConversation mGroupUIConversation;
	private int mGroupUnreadMessageCount = 0;

	public void onResume() {
		super.onResume();

		if (RCloudContext.getInstance() != null) {
			RCloudContext.getInstance().getRongIMClient();
			RongIMClient.clearNotifications();
			RCloudContext.getInstance().setNotificationNewMessageCount(0);
			RCloudContext.getInstance().clearNotificationUserIdList();

			RCloudContext.getInstance().setConnectionStatusListener(new RCloudContext.ConnectionStatusListener() {
				public void onChanged(int code) {
					ConversationListFragment.this.setNetStatus(code);
				}
			});
			setGroupUnReadMessageCount();
		}

		setCurrentConversationTargetId(null);
	}

	protected void setGroupUnReadMessageCount() {
		if ((RCloudContext.getInstance() == null) || (RCloudContext.getInstance().getRongIMClient() == null)) {
			return;
		}

		getHandler().post(new Runnable() {
			public void run() {
				RongIMClient.ConversationType[] conversationTypes = { RongIMClient.ConversationType.GROUP };
				ConversationListFragment.this.mGroupUnreadMessageCount = RCloudContext.getInstance().getRongIMClient()
						.getUnreadCount(conversationTypes);

				if (ConversationListFragment.this.mConversationListAdapter != null) {
					int count = ConversationListFragment.this.mConversationListAdapter.getCount();

					for (int i = 0; i < count; i++) {
						UIConversation uiConversation = (UIConversation) ConversationListFragment.this.mConversationListAdapter
								.getItem(i);

						if (RongIMClient.ConversationType.GROUP == uiConversation.getConversationType()) {
							uiConversation
									.setUnreadMessageCount(ConversationListFragment.this.mGroupUnreadMessageCount);
							ConversationListFragment.this.mConversationListAdapter.notifyDataSetChanged();
							break;
						}
					}
				}
			}
		});
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		EventBus.getDefault().register(this);
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = View.inflate(getActivity(), R.layout.rc_fragment_conversation_list, null);
		mLvConversation = (ListView) view.findViewById(R.id.lv_conversation_list);
		initAdapter();
		mLvConversation.setEmptyView(view.findViewById(R.id.tv_empty));
		mConnectStateTextView = (TextView) view.findViewById(R.id.tv_warning);
		mConversationListAdapter.setOnGetDataListener(this);
		if (getActionBar() != null) {
			setActionBar(getActionBar());
		}
		return view;
	}

	protected void initAdapter() {
		mConversationListAdapter = new ConversationListAdapter(getActivity(), false);
		mLvConversation.setAdapter(mConversationListAdapter);
		mLvConversation.setOnItemClickListener(this);
		mLvConversation.setOnItemLongClickListener(this);
	}

	protected void setActionBar(ActionBar actionBar) {
		if (actionBar == null) {
			return;
		}
		actionBar.getTitleTextView().setText(
				Res.getInstance(getActivity()).string("conversation_list_action_bar_title"));

		View view = LayoutInflater.from(getActivity()).inflate(
				Res.getInstance(getActivity()).layout("rc_action_bar_conversation_list_select"), getActionBar(), false);
		ImageView peopleView = (ImageView) getViewById(view, "rc_conversation_list_select_image");

		peopleView.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				RongIM.getInstance().startFriendSelect(ConversationListFragment.this.getActivity());
			}
		});
		actionBar.addView(peopleView);

		actionBar.setOnBackClick(new View.OnClickListener() {
			public void onClick(View v) {
				ConversationListFragment.this.getActivity().finish();
			}
		});
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		this.mLvConversation.setOnScrollListener(this);
		this.mConversationListAdapter.setListView(this.mLvConversation);

		getHandler().post(new Runnable() {
			public void run() {
				ConversationListFragment.this.resetData();
			}
		});
		super.onViewCreated(view, savedInstanceState);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		UIConversation conversation = (UIConversation) this.mConversationListAdapter.getItem(position);
		this.mConversationType = conversation.getConversationType();
		Uri uri;
		if (this.mConversationType == RongIMClient.ConversationType.GROUP) {
			setCurrentConversationTargetId(null);

			uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
					.appendPath("conversationgrouplist").build();
		} else {
			setCurrentConversationTargetId(conversation.getTargetId());

			String title = "";

			if (!TextUtils.isEmpty(conversation.getConversationTitle()))
				title = conversation.getConversationTitle();
			else if (conversation.getConversationType() == RongIMClient.ConversationType.DISCUSSION) {
				if (conversation.getUiDiscussion() != null)
					title = conversation.getUiDiscussion().getName();
			} else if ((conversation.getConversationType() == RongIMClient.ConversationType.PRIVATE)
					&& (conversation.getUserInfo() != null)) {
				title = conversation.getUserInfo().getName();
			}

			uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
					.appendPath("conversation").appendPath(conversation.getConversationType().getName().toLowerCase())
					.appendQueryParameter("targetId", conversation.getTargetId()).appendQueryParameter("title", title)
					.build();
		}

		getActivity().startActivity(new Intent("android.intent.action.VIEW", uri));
	}

	private final void removeConversation(String conversationId) {
		int count = this.mConversationListAdapter.getCount();

		for (int i = 0; i < count; i++) {
			UIConversation conversation = (UIConversation) this.mConversationListAdapter.getItem(i);

			if ((conversation != null) && (conversation.getTargetId().equals(conversationId))) {
				this.mConversationListAdapter.remove(i);
				this.mConversationListAdapter.notifyDataSetChanged();
				break;
			}
		}
	}

	protected void resetData() {
		this.mWorkHandler.post(new Runnable() {
			public void run() {
				List<UIConversation> uiConversations = DBHelper.getInstance().getConversationList();

				if ((uiConversations == null) || (uiConversations.size() == 0)) {
					return;
				}

				boolean isHaveGroup = false;

				final ArrayList<UIConversation> conversations = new ArrayList<UIConversation>();

				if (ConversationListFragment.this.mGroupUIConversation != null) {
					ConversationListFragment.this.mGroupUIConversation.setUnreadMessageCount(0);
				}

				for (UIConversation uiConversation : uiConversations) {
					if (RongIMClient.ConversationType.GROUP == uiConversation.getConversationType()) {
						if (isHaveGroup) {
							ConversationListFragment.this.mGroupUIConversation
									.setUnreadMessageCount(ConversationListFragment.this.mGroupUIConversation
											.getUnreadMessageCount() + uiConversation.getUnreadMessageCount());
						} else {
							ConversationListFragment.this.mGroupUIConversation = uiConversation;
							conversations.add(uiConversation);
							isHaveGroup = true;
						}
					} else {
						conversations.add(uiConversation);
					}
				}

				ConversationListFragment.this.getHandler().post(new Runnable() {
					public void run() {
						ConversationListFragment.this.mConversationListAdapter.removeAll();
						ConversationListFragment.this.clearListCache();
						ConversationListFragment.this.mConversationListAdapter.addData(conversations);
						ConversationListFragment.this.mConversationListAdapter.notifyDataSetChanged();
					}
				});
			}
		});
	}

	public void receiveData(Intent intent) {
		String action = intent.getAction();
		int leftMessageCount = intent.getIntExtra("left_message_count", 0);
		int messageCount = intent.getIntExtra("message_count", 0);

		if (messageCount > 20) {
			if (leftMessageCount == 0) {
				getHandler().obtainMessage(HANDLE_RE_LOAD_DATA).sendToTarget();
				return;
			}
		}

		if ((RongConst.BROADCAST.ACTION_P2P_MESSAGE_RECEIVE.equals(action))
				|| (RongConst.BROADCAST.ACTION_DISCUSSION_MESSAGE_RECEIVE.equals(action))
				|| (RongConst.BROADCAST.ACTION_GROUP_MESSAGE_RECEIVE.equals(action))
				|| (RongConst.BROADCAST.ACTION_CHATROOM_MESSAGE_RECEIVE.equals(action))
				|| (RongConst.BROADCAST.ACTION_SYSTEM_MESSAGE_RECEIVE.equals(action))) {
			UIMessage message = (UIMessage) intent.getParcelableExtra("message_obj");

			String targetId = message.getTargetId();
			if ((RCloudContext.getInstance() != null) && (RCloudContext.getInstance().getCurrentTargetId() != null)
					&& (RCloudContext.getInstance().getCurrentTargetId().equals(targetId))) {
				return;
			}

			getHandler().obtainMessage(HANDLE_HAS_MESSAGE, message).sendToTarget();
		}
	}

	protected void setNetStatus(final int status) {
		if (getActivity() == null) {
			return;
		}
		Runnable runnable = new Runnable() {
			public void run() {
				switch (status) {
					case -9:
						ConversationListFragment.this.mConnectStateTextView
								.setText(Res.getInstance(ConversationListFragment.this.getActivity()).string(
										"conntect_state_prompt_disconnect"));
						ConversationListFragment.this.mConnectStateTextView.setVisibility(0);
						break;
					case 0:
						ConversationListFragment.this.mConnectStateTextView.setVisibility(8);
						break;
					case 6:
						ConversationListFragment.this.mConnectStateTextView.setText(Res.getInstance(
								ConversationListFragment.this.getActivity()).string(
								"conntect_state_prompt_other_device_login"));
						ConversationListFragment.this.mConnectStateTextView.setVisibility(0);
						break;
					case -1:
						ConversationListFragment.this.mConnectStateTextView.setText(Res.getInstance(
								ConversationListFragment.this.getActivity()).string(
								"conntect_state_prompt_unknow_error"));
						ConversationListFragment.this.mConnectStateTextView.setVisibility(0);
						break;
					case 1:
						ConversationListFragment.this.mConnectStateTextView.setText(Res.getInstance(
								ConversationListFragment.this.getActivity()).string(
								"conntect_state_prompt_network_unavailable"));
						ConversationListFragment.this.mConnectStateTextView.setVisibility(0);
				}
			}
		};
		if (Looper.myLooper() == Looper.getMainLooper())
			runnable.run();
		else
			getHandler().post(runnable);
	}

	public void receivePageIntent(Intent intent) {
		String action = intent.getAction();

		if ((RongConst.BROADCAST.ACTION_REMOVE_CONVERSATION_FOR_DELETE_FRIEND.equals(action))
				|| (RongConst.BROADCAST.ACTION_RESET_DATA_FOR_CONVERSION_LIST.equals(action))) {
			resetData();
			return;
		}

		if (RongConst.BROADCAST.ACTION_BUNDLE_IO_RONG_IMKIT_CONVERSATION.equals(action)) {
			resetData();
			this.mConversationType = null;
			setCurrentConversationTargetId(null);
		} else if (RongConst.BROADCAST.ACTION_BUNDLE_IO_RONG_IMKIT_CONVERSATION_SETTING.equals(action)) {
			getHandler().obtainMessage(HANDLE_SETTING_FUNCTION_SEND_BROADCAST, intent).sendToTarget();
		}
	}

	public void registerActions(List<String> actions) {
		actions.add(RongConst.BROADCAST.ACTION_P2P_MESSAGE_RECEIVE);
		actions.add(RongConst.BROADCAST.ACTION_GROUP_MESSAGE_RECEIVE);
		actions.add(RongConst.BROADCAST.ACTION_DISCUSSION_MESSAGE_RECEIVE);
		actions.add(RongConst.BROADCAST.ACTION_SYSTEM_MESSAGE_RECEIVE);

		super.registerActions(actions);
	}

	public void registerBundleActions(List<String> actions) {
		actions.add(RongConst.BROADCAST.ACTION_BUNDLE_IO_RONG_IMKIT_CONVERSATION);
		actions.add(RongConst.BROADCAST.ACTION_BUNDLE_IO_RONG_IMKIT_CONVERSATION_SETTING);
		actions.add(RongConst.BROADCAST.ACTION_REMOVE_CONVERSATION_FOR_DELETE_FRIEND);
		actions.add(RongConst.BROADCAST.ACTION_RESET_DATA_FOR_CONVERSION_LIST);

		super.registerBundleActions(actions);
	}

	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
	}

	public boolean onItemLongClick(AdapterView<?> parent, View view, final int positionArg, long id) {
		final SelectDialog mSelectDialog = new SelectDialog(getActivity());
		final UIConversation conversation = (UIConversation) this.mConversationListAdapter.getItem(positionArg);

		if (conversation != null) {
			mSelectDialog.setTitle(conversation.getConversationTitle());
		}

		if (!conversation.isTop())
			mSelectDialog.setFristLineContent("dialog_converastion_istop");
		else {
			mSelectDialog.setFristLineContent("dialog_converastion_istop_cancel");
		}

		mSelectDialog.setSecondLineContent("dialog_converastion_remove");

		mSelectDialog.setOnDialogItemViewListener(new SelectDialog.OnDialogItemViewListener() {
			public void OnDialogItemViewClick(View view, int position) {
				if (position == 0) {
					DBHelper.getInstance().setTop(conversation.getConversationType(), conversation.getTargetId(),
							!conversation.isTop());

					if (conversation.isTop())
						Toast.makeText(
								ConversationListFragment.this.getActivity(),
								ConversationListFragment.this.getResources().getString(
										Res.getInstance(ConversationListFragment.this.getActivity()).string(
												"conversation_list_set_top_cancel")), 1000).show();
					else {
						Toast.makeText(
								ConversationListFragment.this.getActivity(),
								ConversationListFragment.this.getResources().getString(
										Res.getInstance(ConversationListFragment.this.getActivity()).string(
												"conversation_list_set_top")), 1000).show();
					}

					ConversationListFragment.this.resetData();
				} else if (position == 1) {
					String targetId = null;

					if (conversation != null) {
						targetId = conversation.getTargetId();

						if (!TextUtils.isEmpty(targetId)) {
							DBHelper.getInstance().removeConversation(conversation.getConversationType(), targetId);
							ConversationListFragment.this.mConversationListAdapter.remove(positionArg);
							ConversationListFragment.this.mConversationListAdapter.notifyDataSetChanged();
						}
					}
				}

				mSelectDialog.dismiss();
			}
		});
		mSelectDialog.show();

		return true;
	}

	protected void rongHandleMessage(Message msg) {
		if (msg.what == HANDLE_NOTIFY_ADAPTER) {
			this.mConversationListAdapter.notifyDataSetChanged();
		} else if (msg.what == HANDLE_NOTIFY_LOAD_DATA) {
			resetData();

			if (this.mDialog != null)
				this.mDialog.dismiss();
		} else if (msg.what == GET_DISCUSSION_INFO) {
			UIDiscussion uiDiscussion = (UIDiscussion) msg.obj;
			UIConversation uiconversation = getUIConversation(uiDiscussion.getId());
			uiconversation.setUiDiscussion(uiDiscussion);
			uiconversation.setConversationTitle(uiDiscussion.getName());

			this.mConversationListAdapter.notifyDataSetChanged();
		} else if (msg.what == START_CONVERSATION) {
			Intent data = (Intent) msg.obj;

			List<UserInfo> userInfos = data.getParcelableArrayListExtra("extra_users");
			int conversationTypeValue = data.getIntExtra("intent_private_select_people", 0);

			RongIMClient.ConversationType conversationType = null;

			if (conversationTypeValue > 0) {
				conversationType = RongIMClient.ConversationType.setValue(conversationTypeValue);
			}

			if ((userInfos.size() > 1)
					|| ((conversationType != null) && (conversationType == RongIMClient.ConversationType.DISCUSSION))) {
				ArrayList<String> userIds = new ArrayList<String>();
				ArrayList<String> userNames = new ArrayList<String>();

				for (RongIMClient.UserInfo info : userInfos) {
					userIds.add(info.getUserId());

					if (userNames.size() < 10) {
						userNames.add(info.getName());
					}
				}
				RongIMClient.UserInfo userInfo = getCurrentUserInfo();

				if ((userInfo != null) && (!TextUtils.isEmpty(userInfo.getName()))) {
					userNames.add(userInfo.getName());
				}

				Intent intent = new Intent(RongConst.BROADCAST.ACTION_DISCUSSION_CREATE);
				intent.putStringArrayListExtra("multi_talk_id_array", userIds);
				intent.putExtra("multi_talk_name", TextUtils.join(",", userNames));

				this.mDialog = new LoadingDialog(getActivity());
				this.mDialog.setText(Res.getInstance(getActivity()).string("discussion_create_loading_title"));
				this.mDialog.show();

				sendAction(intent, new ActionBaseFragment.ActionCallback() {
					public void callback(Intent intentArg) {
						ConversationListFragment.this.mDialog.dismiss();
						boolean isSuccess = intentArg.getBooleanExtra("intent_api_operation_status", false);

						if (isSuccess) {
							String targetId = intentArg.getStringExtra("multi_talk_id");
							String discussionName = intentArg.getStringExtra("multi_talk_name");
							ConversationListFragment.this.setCurrentConversationTargetId(targetId);

							UIConversation conversation = new UIConversation();
							conversation.setTargetId(targetId);
							conversation.setConversationType(RongIMClient.ConversationType.DISCUSSION);
							conversation.setConversationTitle(discussionName);

							ConversationListFragment.this.newMessageSetTop(conversation);

							ConversationListFragment.this.getHandler()
									.obtainMessage(START_CONVERSATION_CREATE_SUCCESS, conversation).sendToTarget();
						} else {
							if ((ConversationListFragment.this.mDialog != null)
									&& (ConversationListFragment.this.mDialog.isShowing())) {
								ConversationListFragment.this.mDialog.dismiss();
							}
							RongToast.toast(
									ConversationListFragment.this.getActivity(),
									Res.getInstance(ConversationListFragment.this.getActivity()).string(
											"discussion_create_failure"));
						}
					}

				});
			} else if (userInfos.size() == 1) {
				UIConversation conversation = new UIConversation();
				conversation.setConversationTitle(((RongIMClient.UserInfo) userInfos.get(0)).getName());
				conversation.setTargetId(((RongIMClient.UserInfo) userInfos.get(0)).getUserId());
				conversation.setConversationType(RongIMClient.ConversationType.PRIVATE);
				startConversation(conversation);
				setCurrentConversationTargetId(((RongIMClient.UserInfo) userInfos.get(0)).getUserId());
			}
		} else if (msg.what == HANDLE_ONCLICK_ITEM) {
			Intent intent = (Intent) msg.obj;
			final UIConversation conversation = (UIConversation) intent.getParcelableExtra("extra_conversation");

			this.mWorkHandler.post(new Runnable() {
				public void run() {
					UIConversation conversationTemp = DBHelper.getInstance().getConversation(
							conversation.getConversationType(), conversation.getTargetId());

					if ((conversationTemp == null) || (TextUtils.isEmpty(conversationTemp.getTargetId()))) {
						return;
					}

					final UIMessage uiMessage = new UIMessage();
					uiMessage.setContent(conversationTemp.getLatestMessage());
					uiMessage.setTargetId(conversationTemp.getTargetId());
					uiMessage.setSentTime(conversationTemp.getSentTime());
					uiMessage.setConversationType(conversationTemp.getConversationType());
					uiMessage.setDraft(conversationTemp.getDraft());
					uiMessage.setSenderUserId(conversationTemp.getSenderUserId());
					uiMessage.setSentStatus(conversationTemp.getSentStatus());
					ConversationListFragment.this.getHandler().post(new Runnable() {
						public void run() {
							ConversationListFragment.this.hasNewMessage(uiMessage, false, false);
						}
					});
				}
			});
		} else if (msg.what == START_CONVERSATION_CREATE_SUCCESS) {
			UIConversation conversation = (UIConversation) msg.obj;
			startConversation(conversation);
		} else if (msg.what == HANDLE_SETTING_FUNCTION_SEND_BROADCAST) {
			Intent intent = (Intent) msg.obj;

			String targetId = intent.getStringExtra("target_id");
			boolean isQuitDiscussion = intent.getBooleanExtra("intent_quit_discussion_close_page", false);
			int isSetTopConversation = intent.getIntExtra("intent_set_top_conversation_success", -1);
			String discussionName = intent.getStringExtra("intent_update_name_discussion");
			String createDiscussion = intent.getStringExtra("intent_create_discussion_success");
			boolean isClearMessages = intent.getBooleanExtra("intent_clear_message_success", false);

			UIConversation conversation = getUIConversation(targetId);

			if (conversation != null) {
				if (!TextUtils.isEmpty(createDiscussion)) {
					newMessageSetTop(conversation);
					return;
				}

				if (isQuitDiscussion) {
					this.mConversationListAdapter.remove(conversation);
					this.mConversationListAdapter.notifyDataSetChanged();
					return;
				}

				UIConversation conversationTemp = DBHelper.getInstance().getConversation(
						conversation.getConversationType(), conversation.getTargetId());

				if (conversationTemp != null) {
					conversation.setLatestMessage(conversationTemp.getLatestMessage());
					conversation.setSentTime(conversationTemp.getSentTime());
				}

				if (!TextUtils.isEmpty(discussionName)) {
					if ((conversation != null) && (conversation.getUiDiscussion() != null)) {
						UIDiscussion uiDiscussion = conversation.getUiDiscussion();
						uiDiscussion.setName(discussionName);
					}

					conversation.setConversationTitle(discussionName);
					this.mConversationListAdapter.notifyDataSetChanged();
				}

				if (isClearMessages) {
					UIConversation uiconversation = getUIConversation(targetId);

					if (uiconversation != null) {
						uiconversation.setLatestMessage(null);
						this.mConversationListAdapter.notifyDataSetChanged();
					}
				}

				if (isSetTopConversation != -1) {
					if (isSetTopConversation == 1) {
						UIConversation uiConversation = getUIConversation(conversation.getTargetId());
						uiConversation.setTop(true);
						int count = this.mConversationListAdapter.getCount();

						if (count > 1) {
							removeConversation(conversation.getTargetId());
							this.mConversationListAdapter.addItem(0, uiConversation);
						}
						this.mConversationListAdapter.notifyDataSetChanged();
					} else {
						resetData();
					}
				}
			}
		} else if (msg.what == 990001) {
			this.mConversationListAdapter.notifyDataSetChanged();
		} else if (msg.what == 990003) {
			if ((msg.obj instanceof RongIMClient.Group)) {
				RongIMClient.Group group = (RongIMClient.Group) msg.obj;
				UIConversation conversation = getUIConversation(group.getId());

				if (conversation != null) {
					conversation.setUiGroup(new UIGroup(group));
					conversation.setConversationTitle(group.getName());
					this.mConversationListAdapter.notifyDataSetChanged();
				}
			}
		} else if (msg.what == HANDLE_HAS_MESSAGE) {
			if ((msg.obj instanceof UIMessage)) {
				UIMessage message = (UIMessage) msg.obj;
				hasNewMessage(message, true, false);
			}
		} else if (msg.what == HANDLE_RE_LOAD_DATA) {
			resetData();
		}
	}

	private void startConversation(UIConversation conversation) {
		String title = "";

		if (!TextUtils.isEmpty(conversation.getConversationTitle())) {
			title = conversation.getConversationTitle();
		}

		Uri uri = Uri.parse("rong://" + getActivity().getApplicationInfo().packageName).buildUpon()
				.appendPath("conversation").appendPath(conversation.getConversationType().getName().toLowerCase())
				.appendQueryParameter("targetId", conversation.getTargetId()).appendQueryParameter("title", title)
				.build();

		getActivity().startActivity(new Intent("android.intent.action.VIEW", uri));
	}

	protected boolean wrapGroupCoversation(UIMessage message) {
		if (RongIMClient.ConversationType.GROUP == message.getConversationType()) {
			if (this.mGroupUIConversation != null) {
				if ((this.mGroupUIConversation.getUiGroup() != null)
						&& (!this.mGroupUIConversation.getUiGroup().getId().equals(message.getTargetId()))) {
					this.mGroupUIConversation.setUiGroup(null);
				}

				this.mGroupUIConversation.setSenderUserName(null);
				this.mGroupUIConversation.setUserInfo(message.getUserInfo());

				this.mGroupUIConversation.setTextMessageContent(null);
				this.mGroupUIConversation.setTargetId(message.getTargetId());
				this.mGroupUIConversation.setLatestMessage(message.getContent());
				this.mGroupUIConversation.setSentTime(message.getSentTime());
				this.mGroupUIConversation.setConversationType(RongIMClient.ConversationType.GROUP);
				this.mGroupUIConversation.setUnreadMessageCount(this.mGroupUIConversation.getUnreadMessageCount() + 1);
				this.mGroupUIConversation.setSenderUserId(message.getSenderUserId());
				this.mConversationListAdapter.notifyDataSetChanged();
			} else {
				this.mGroupUIConversation = new UIConversation();
				this.mGroupUIConversation.setLatestMessage(message.getContent());
				this.mGroupUIConversation.setConversationTitle(getString(Res.getInstance(getActivity()).string(
						"rc_group_conversation_list_name")));
				this.mGroupUIConversation.setSentTime(message.getSentTime());
				this.mGroupUIConversation.setUnreadMessageCount(1);
				this.mGroupUIConversation.setTargetId(message.getTargetId());
				this.mGroupUIConversation.setConversationType(RongIMClient.ConversationType.GROUP);
				this.mConversationListAdapter.addItem(0, this.mGroupUIConversation);
				this.mConversationListAdapter.notifyDataSetChanged();
			}

			return true;
		}
		return false;
	}

	protected void hasNewMessage(UIMessage message, boolean isNewMessage, boolean isGroup) {
		if (!isGroup) {
			boolean isWrapGroup = wrapGroupCoversation(message);
			if (isWrapGroup) {
				return;
			}
		} else if ((message != null) && (message.getConversationType() != RongIMClient.ConversationType.GROUP)) {
			return;
		}

		String targetId = message.getTargetId();

		int count = this.mConversationListAdapter.getCount();
		boolean isExit = false;

		for (int i = 0; i < count; i++) {
			UIConversation conversation = (UIConversation) this.mConversationListAdapter.getItem(i);

			if (conversation.getTargetId().equals(targetId)) {
				conversation.setDraft(message.getDraft());
				conversation.setSentTime(message.getSentTime());
				conversation.setLatestMessage(message.getContent());
				conversation.setTextMessageContent(null);
				conversation.setLatestMessage(message.getContent());
				conversation.setSentStatus(message.getSentStatus());
				conversation.setConversationType(message.getConversationType());
				conversation.setSenderUserName(null);
				conversation.setUserInfo(null);
				conversation.setSenderUserId(message.getSenderUserId());
				conversation.setOperator(null);
				conversation.setOperatored(null);

				if ((conversation.getReceivedTime() > message.getReceivedTime())
						|| (conversation.getSentTime() > message.getSentTime())) {
					resetData();
					return;
				}

				if (((conversation.getReceivedTime() == message.getReceivedTime()) || (conversation.getSentTime() == message
						.getSentTime())) && (!isNewMessage)) {
					conversation.setUnreadMessageCount(0);
					this.mConversationListAdapter.notifyDataSetChanged();
					return;
				}

				if (isNewMessage) {
					if (!(conversation.getLatestMessage() instanceof RongIMClient.DiscussionNotificationMessage))
						conversation.setUnreadMessageCount(conversation.getUnreadMessageCount() + 1);
				} else {
					conversation.setUnreadMessageCount(0);
				}

				if (conversation.isTop()) {
					if (i != 0) {
						this.mConversationListAdapter.remove(i);
						this.mConversationListAdapter.addItem(0, conversation);
					}
				} else {
					for (int j = 0; j < count; j++) {
						UIConversation uiConversation = (UIConversation) this.mConversationListAdapter.getItem(j);

						if (!uiConversation.isTop()) {
							this.mConversationListAdapter.remove(i);
							this.mConversationListAdapter.addItem(j, conversation);

							break;
						}
					}
				}

				getHandler().obtainMessage(HANDLE_NOTIFY_ADAPTER).sendToTarget();

				isExit = true;

				break;
			}

		}

		if (!isExit) {
			int isNotExitTopPosition = -1;

			for (int j = 0; j < count; j++) {
				UIConversation uiConversation = (UIConversation) this.mConversationListAdapter.getItem(j);

				if (!uiConversation.isTop()) {
					isNotExitTopPosition = j;
					break;
				}
			}

			UIConversation conversation = message.toConversation();

			if (!isNewMessage) {
				conversation.setSentStatus(message.getSentStatus());
				conversation.setUnreadMessageCount(0);
			} else if (!(conversation.getLatestMessage() instanceof RongIMClient.DiscussionNotificationMessage)) {
				conversation.setUnreadMessageCount(1);
			}

			if (isNotExitTopPosition >= 0) {
				this.mConversationListAdapter.addItem(isNotExitTopPosition, conversation);
			} else {
				isNotExitTopPosition = count;
				this.mConversationListAdapter.addItem(isNotExitTopPosition, conversation);
			}
			this.mConversationListAdapter.notifyDataSetChanged();
		}
	}

	private final void newMessageSetTop(UIConversation conversation) {
		if ((this.mConversationListAdapter != null) && (conversation != null)) {
			int count = this.mConversationListAdapter.getCount();

			if (count <= 0) {
				this.mConversationListAdapter.addData(conversation);
			} else {
				for (int i = 0; i < count; i++) {
					UIConversation uiConversation = (UIConversation) this.mConversationListAdapter.getItem(i);

					if (!uiConversation.isTop()) {
						this.mConversationListAdapter.addItem(i, conversation);
						break;
					}
				}
			}
			this.mConversationListAdapter.notifyDataSetChanged();
		}
	}

	public void getDiscussionInfo(int position, String discusstionId) {
		getDiscussionInfo(discusstionId, position);
	}

	private void getDiscussionInfo(final String discusstionId, final int position) {
		this.mWorkHandler.post(new Runnable() {
			public void run() {
				if (Looper.myLooper() == Looper.getMainLooper()) {
					Log.d("ConversationListFragment",
							"*************Looper.myLooper() ==Looper.getMainLooper()*******getDiscussionInfo***");
				}

				ConversationListFragment.this.getDiscussionInfo(discusstionId,
						new ActionBaseFragment.GetDiscussionInfoCallback() {
							public void onSuccess(UIDiscussion discussion) {
								if ((discussion != null) && (!TextUtils.isEmpty(discussion.getName())))
									ConversationListFragment.this.getHandler()
											.obtainMessage(GET_DISCUSSION_INFO, position, 0, discussion).sendToTarget();
							}

							public void onError() {
								UIDiscussion discussion = new UIDiscussion();
								discussion.setId(discusstionId);
								discussion.setName(ConversationListFragment.this.getString(Res.getInstance(
										ConversationListFragment.this.getActivity()).string("default_discussion_name")));
								ConversationListFragment.this.getHandler()
										.obtainMessage(GET_DISCUSSION_INFO, position, 0, discussion).sendToTarget();
							}
						});
			}
		});
	}

	protected UIConversation getUIConversation(String targetId) {
		int count = this.mConversationListAdapter.getCount();

		for (int i = 0; i < count; i++) {
			UIConversation conversation = (UIConversation) this.mConversationListAdapter.getItem(i);

			if ((conversation != null) && (conversation.getTargetId().equals(targetId))) {
				return conversation;
			}
		}

		return null;
	}

	public void onEventMainThread(RongIMClient.UserInfo userInfo) {
		if (this.mConversationListAdapter != null)
			this.mConversationListAdapter.notifyDataSetChanged();
	}

	public void onEventMainThread(RongIMClient.Group group) {
		if (this.mConversationListAdapter != null)
			this.mConversationListAdapter.notifyDataSetChanged();
	}

	@Override
	public void onDestroy() {
		EventBus.getDefault().unregister(this);
		super.onDestroy();
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}
}