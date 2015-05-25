package com.xhr88.lp.adapter;

import io.rong.imkit.RCloudContext;
import io.rong.imkit.Res;
import io.rong.imkit.adapter.BaseAdapter;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.model.UIGroup;
import io.rong.imkit.model.UIUserInfo;
import io.rong.imkit.utils.HighLightUtils;
import io.rong.imkit.utils.RCDateUtils;
import io.rong.imkit.utils.Util;
import io.rong.imkit.view.AsyncImageView;
import io.rong.imlib.RongIMClient;
import io.rong.message.ImageMessage;
import io.rong.message.InformationNotificationMessage;
import io.rong.message.LocationMessage;
import io.rong.message.RichContentMessage;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

import java.util.Date;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.xhr.framework.util.StringUtil;
import com.xhr88.lp.R;
import com.xhr88.lp.business.dao.UserDao;
import com.xhr88.lp.model.datamodel.ConversationUserInfoModel;

public class ConversationListAdapter extends BaseAdapter<UIConversation> {
	private OnGetDataListener mOnGetDataListener;
	private boolean mIsGroup = false;

	public ConversationListAdapter(Context context, boolean isGroup) {
		super(context);
		this.mIsGroup = isGroup;
	}

	public boolean isIsGroup() {
		return this.mIsGroup;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;

		if ((convertView == null) || (convertView.getTag() == null)) {
			convertView = View.inflate(mContext, R.layout.rc_item_conversationlist, null);
			holder = new ViewHolder();
			holder.mLayout = convertView.findViewById(R.id.ll_item_view);
			holder.mIvUserHead = (AsyncImageView) convertView.findViewById(R.id.iv_user_head);
			holder.mIvNewMsg = (TextView) convertView.findViewById(R.id.rc_new_message);
			holder.mTvUserName = (TextView) convertView.findViewById(R.id.tv_user_name);
			holder.mTvSendTime = (TextView) convertView.findViewById(R.id.tv_send_time);
			holder.mTvMsgContent = (TextView) convertView.findViewById(R.id.tv_msg_content);
			holder.mIvBlock = (ImageView) convertView.findViewById(R.id.message_block);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			holder.mIvUserHead.clean();
			holder.mTvSendTime.setText("");
		}

		holder.mTvUserName.setText("");
		holder.mTvMsgContent.setText("");
		holder.mTvMsgContent.setCompoundDrawables(null, null, null, null);

		UIConversation conversation = (UIConversation) this.dataSet.get(position);

		if (conversation != null) {
			int unReadCont = conversation.getUnreadMessageCount();

			String moreMsgFlag = null;

			if (unReadCont > 0) {
				if (unReadCont > 99)
					moreMsgFlag = "99+";
				else {
					moreMsgFlag = String.valueOf(unReadCont);
				}
				holder.mIvNewMsg.setVisibility(View.VISIBLE);
				holder.mIvNewMsg.setText(moreMsgFlag);
			} else {
				holder.mIvNewMsg.setVisibility(View.GONE);
			}

			if (conversation.isTop())
				holder.mLayout.setBackgroundResource(R.drawable.conversation_list_item_top_bg);
			else {
				holder.mLayout.setBackgroundResource(R.drawable.conversation_list_item_normal_bg);
			}

			if ((conversation.getConversationType() == RongIMClient.ConversationType.PRIVATE)
					|| (conversation.getConversationType() == RongIMClient.ConversationType.SYSTEM)) {
				UIUserInfo userInfo = null;

				if ((conversation.getUserInfo() == null) || (TextUtils.isEmpty(conversation.getUserInfo().getName()))
						|| (TextUtils.isEmpty(conversation.getConversationTitle()))) {
					userInfo = getUserInfoByCache(conversation.getTargetId());
					conversation.setUserInfo(userInfo);
				} else {
					userInfo = conversation.getUserInfo();
				}

				if (userInfo != null) {
					holder.mTvUserName.setText(userInfo.getName());
					conversation.setConversationTitle(userInfo.getName());

					if (userInfo.getPortraitResource() != null)
						holder.mIvUserHead.setResource(userInfo.getPortraitResource());
					else {
						ConversationUserInfoModel model = UserDao.getConversationUserInfoModel(userInfo.getUserId());
						if (null != model && !StringUtil.isNullOrEmpty(model.getUid())) {
							int imageBgRes = R.drawable.default_man_head_bg;
							Integer sex = model.getSex();
							if (1 != sex) {
								imageBgRes = R.drawable.default_woman_head_bg;
							}
							holder.mIvUserHead.setDefaultDrawable(mContext.getResources().getDrawable(imageBgRes));
						}
						holder.mIvUserHead.clean();
					}
				}
			} else if (conversation.getConversationType() == RongIMClient.ConversationType.DISCUSSION) {
				holder.mIvUserHead.setImageDrawable(this.mContext.getResources().getDrawable(
						Res.getInstance(this.mContext).drawable("rc_default_discussion_portrait")));

				if ((TextUtils.isEmpty(conversation.getConversationTitle()))
						|| (conversation.getUiDiscussion() == null)) {
					if (this.mOnGetDataListener != null) {
						this.mOnGetDataListener.getDiscussionInfo(position, conversation.getTargetId());
					}

				} else if ((conversation.getUiDiscussion() != null)
						&& (!TextUtils.isEmpty(conversation.getUiDiscussion().getName()))) {
					conversation.setConversationTitle(conversation.getUiDiscussion().getName());
				}

				if (!TextUtils.isEmpty(conversation.getConversationTitle()))
					holder.mTvUserName.setText(conversation.getConversationTitle());
				else {
					holder.mTvUserName.setText(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("default_discussion_name")));
				}

				if ((conversation.getLatestMessage() instanceof RongIMClient.DiscussionNotificationMessage)) {
					RongIMClient.DiscussionNotificationMessage discussionNotificationMessage = (RongIMClient.DiscussionNotificationMessage) conversation
							.getLatestMessage();
					if (discussionNotificationMessage.getType() == 3) {
						holder.mTvUserName.setText(discussionNotificationMessage.getExtension());
					}

				}

				if ((conversation.getLatestMessage() == null) && (conversation.getSentTime() == 0L)) {
					conversation.setSentTime(System.currentTimeMillis());
				}

			} else if (RongIMClient.ConversationType.GROUP == conversation.getConversationType()) {
				holder.mIvUserHead.setImageDrawable(this.mContext.getResources().getDrawable(
						Res.getInstance(this.mContext).drawable("rc_group_default_portrait")));

				if (this.mIsGroup) {
					UIGroup uiGroup = conversation.getUiGroup();

					if (uiGroup == null) {
						uiGroup = getGroupInfoByCache(conversation.getTargetId());
						conversation.setUiGroup(uiGroup);
					}

					if (uiGroup != null) {
						holder.mTvUserName.setText(uiGroup.getName());

						if (uiGroup.getPortraitResource() != null)
							holder.mIvUserHead.setResource(uiGroup.getPortraitResource());
						else
							holder.mIvUserHead.clean();
					}
				} else {
					holder.mTvUserName.setText(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("rc_group_conversation_list_name")));
				}
			} else {
				holder.mTvUserName.setText(conversation.getTargetId());
			}

			if (conversation.getNotificationStatus() == RongIMClient.ConversationNotificationStatus.DO_NOT_DISTURB)
				holder.mIvBlock.setVisibility(0);
			else {
				holder.mIvBlock.setVisibility(8);
			}

			if (!TextUtils.isEmpty(conversation.getDraft())) {
				SpannableStringBuilder spannable = new SpannableStringBuilder(this.mContext.getResources().getString(
						Res.getInstance(this.mContext).string("message_type_draft_content")));

				holder.mTvMsgContent.append(spannable.append(Util.highLightLink(HighLightUtils
						.loadHighLight(HighLightUtils.replaceEmoji(conversation.getDraft())))));
			} else if (conversation.getLatestMessage() != null) {
				if ((TextUtils.isEmpty(conversation.getSenderUserName()))
						&& ((conversation.getConversationType() == RongIMClient.ConversationType.GROUP) || (conversation
								.getConversationType() == RongIMClient.ConversationType.DISCUSSION))) {
					UIUserInfo uiUserInfo = conversation.getUserInfo();

					if (uiUserInfo == null) {
						UIUserInfo uiUserInfo1 = getUserInfoByCache(conversation.getSenderUserId());

						if (uiUserInfo1 != null) {
							conversation.setUserInfo(uiUserInfo1);
							conversation.setSenderUserName(uiUserInfo1.getName());
						}
					}
				}

				if ((!this.mIsGroup)
						&& (conversation.getConversationType() == RongIMClient.ConversationType.GROUP)
						&& ((conversation.getUiGroup() == null) || (TextUtils.isEmpty(conversation.getUiGroup()
								.getName())))) {
					UIGroup uiGroup = conversation.getUiGroup();

					if (uiGroup == null) {
						uiGroup = getGroupInfoByCache(conversation.getTargetId());
						conversation.setUiGroup(uiGroup);
					}

				}

				setContent(position, this.mIsGroup, holder, conversation);

				String userId = getCurrentUserInfo() != null ? getCurrentUserInfo().getUserId() : "";

				if ((conversation.getSenderUserId() != null) && (conversation.getSenderUserId().equals(userId))) {
					if ((conversation.getSentStatus() != null)
							&& (conversation.getSentStatus() == RongIMClient.SentStatus.FAILED)) {
						int width = (int) this.mContext.getResources().getDimension(
								Res.getInstance(this.mContext).dimen("px_to_dip_26"));
						Drawable drawable = this.mContext.getResources().getDrawable(
								Res.getInstance(this.mContext).drawable("rc_conversation_list_msg_send_failure"));
						drawable.setBounds(0, 0, width, width);

						holder.mTvMsgContent.setCompoundDrawables(drawable, null, null, null);
					} else if (conversation.getSentStatus() == RongIMClient.SentStatus.SENDING) {
						int width = (int) this.mContext.getResources().getDimension(
								Res.getInstance(this.mContext).dimen("px_to_dip_26"));
						Drawable drawable = this.mContext.getResources().getDrawable(
								Res.getInstance(this.mContext).drawable("conversation_list_msg_sending"));
						drawable.setBounds(0, 0, width, width);

						holder.mTvMsgContent.setCompoundDrawables(drawable, null, null, null);
					}
				}
			} else {
				holder.mTvMsgContent.setCompoundDrawables(null, null, null, null);
				holder.mTvMsgContent.setText("");
			}
			if (conversation.getSentTime() > 0L) {
				String time = RCDateUtils.getConvastionListFromatDate(new Date(conversation.getSentTime()));
				holder.mTvSendTime.setText(time);
			} else {
				holder.mTvSendTime.setText("");
			}
		}
		return convertView;
	}

	private final void setContent(int position, boolean isGroup, ViewHolder hodler, UIConversation conversation) {
		if (isGroup) {
			if (conversation.getConversationType() == RongIMClient.ConversationType.GROUP) {
				if (!TextUtils.isEmpty(conversation.getSenderUserName()))
					hodler.mTvMsgContent.setText(getContent(position, conversation, false, isGroup));
				else {
					hodler.mTvMsgContent.setText(getContent(position, conversation, true, isGroup));
				}
			} else {
				hodler.mTvMsgContent.setText(getContent(position, conversation, true, isGroup));
			}

		} else if (conversation.getConversationType() == RongIMClient.ConversationType.DISCUSSION) {
			if (!TextUtils.isEmpty(conversation.getSenderUserName()))
				hodler.mTvMsgContent.setText(getContent(position, conversation, false, isGroup));
			else {
				hodler.mTvMsgContent.setText(getContent(position, conversation, true, isGroup));
			}
		} else if (conversation.getConversationType() == RongIMClient.ConversationType.GROUP) {
			if ((conversation.getUiGroup() != null) && (!TextUtils.isEmpty(conversation.getUiGroup().getName())))
				hodler.mTvMsgContent.setText(getContent(position, conversation, false, isGroup));
			else {
				hodler.mTvMsgContent.setText(getContent(position, conversation, true, isGroup));
			}
		} else
			hodler.mTvMsgContent.setText(getContent(position, conversation, true, isGroup));
	}

	private final SpannableStringBuilder getContent(int position, UIConversation conversation, boolean isDirect,
			boolean isGroup) {
		SpannableStringBuilder textContent = new SpannableStringBuilder();

		if (!isDirect) {
			if (!isGroup) {
				if (conversation.getConversationType() == RongIMClient.ConversationType.GROUP) {
					if ((conversation.getLatestMessage() instanceof TextMessage)) {
						if ((conversation.getUiGroup() != null)
								&& (!TextUtils.isEmpty(conversation.getUiGroup().getName())))
							textContent.append(conversation.getUiGroup().getName()).append("：")
									.append(conversation.getTextMessageContent());
						else {
							textContent.append(conversation.getTextMessageContent());
						}
					} else if ((conversation.getLatestMessage() instanceof ImageMessage)) {
						if ((conversation.getUiGroup() != null)
								&& (!TextUtils.isEmpty(conversation.getUiGroup().getName()))) {
							textContent.append(conversation.getUiGroup().getName()).append("：");
							textContent.append(this.mContext.getResources().getString(
									Res.getInstance(this.mContext).string("message_type_image_content")));
						}
					} else if ((conversation.getLatestMessage() instanceof VoiceMessage)) {
						if ((conversation.getUiGroup() != null)
								&& (!TextUtils.isEmpty(conversation.getUiGroup().getName()))) {
							textContent.append(conversation.getUiGroup().getName()).append("：");
							textContent.append(this.mContext.getResources().getString(
									Res.getInstance(this.mContext).string("message_type_voice_content")));
						}
					} else if ((conversation.getLatestMessage() instanceof RichContentMessage)) {
						if ((conversation.getUiGroup() != null)
								&& (!TextUtils.isEmpty(conversation.getUiGroup().getName()))) {
							textContent.append(conversation.getUiGroup().getName()).append("：");
							textContent.append(this.mContext.getResources().getString(
									Res.getInstance(this.mContext).string("message_type_image_text_content")));
						}
					} else if ((conversation.getLatestMessage() instanceof LocationMessage)) {
						if ((conversation.getUiGroup() != null)
								&& (!TextUtils.isEmpty(conversation.getUiGroup().getName()))) {
							textContent.append(conversation.getUiGroup().getName()).append("：");
							textContent.append(this.mContext.getResources().getString(
									Res.getInstance(this.mContext).string("message_type_location_content")));
						}
					} else if ((conversation.getLatestMessage() instanceof InformationNotificationMessage)) {
						InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) conversation
								.getLatestMessage();
						if ((informationNotificationMessage != null)
								&& (!TextUtils.isEmpty(informationNotificationMessage.getMessage()))) {
							textContent.append(Html.fromHtml(informationNotificationMessage.getMessage()).toString());
						}
					}
				} else if (conversation.getConversationType() == RongIMClient.ConversationType.DISCUSSION) {
					if ((conversation.getLatestMessage() instanceof TextMessage)) {
						if (TextUtils.isEmpty(conversation.getSenderUserName()))
							textContent.append(conversation.getTextMessageContent());
						else
							textContent.append(conversation.getSenderUserName()).append("：")
									.append(conversation.getTextMessageContent());
					} else if ((conversation.getLatestMessage() instanceof ImageMessage)) {
						if (conversation.getSenderUserName() != null)
							textContent.append(conversation.getSenderUserName()).append("：");
						textContent.append(this.mContext.getResources().getString(
								Res.getInstance(this.mContext).string("message_type_image_content")));
					} else if ((conversation.getLatestMessage() instanceof VoiceMessage)) {
						if (conversation.getSenderUserName() != null)
							textContent.append(conversation.getSenderUserName()).append("：");
						textContent.append(this.mContext.getResources().getString(
								Res.getInstance(this.mContext).string("message_type_voice_content")));
					} else if ((conversation.getLatestMessage() instanceof RichContentMessage)) {
						if (conversation.getSenderUserName() != null)
							textContent.append(conversation.getSenderUserName()).append("：");
						textContent.append(this.mContext.getResources().getString(
								Res.getInstance(this.mContext).string("message_type_image_text_content")));
					} else if ((conversation.getLatestMessage() instanceof LocationMessage)) {
						if (conversation.getSenderUserName() != null)
							textContent.append(conversation.getSenderUserName()).append("：");
						textContent.append(this.mContext.getResources().getString(
								Res.getInstance(this.mContext).string("message_type_location_content")));
					} else if ((conversation.getLatestMessage() instanceof RongIMClient.DiscussionNotificationMessage)) {
						RongIMClient.DiscussionNotificationMessage discussionNotificationMessage = (RongIMClient.DiscussionNotificationMessage) conversation
								.getLatestMessage();

						String selfUserId = getCurrentUserInfo() != null ? getCurrentUserInfo().getUserId() : "";
						String[] userIds = null;
						String operator = discussionNotificationMessage.getOperator();

						if (!TextUtils.isEmpty(discussionNotificationMessage.getExtension())) {
							if (discussionNotificationMessage.getExtension().indexOf(",") != -1)
								userIds = discussionNotificationMessage.getExtension().split(",");
							else {
								userIds = new String[] { discussionNotificationMessage.getExtension() };
							}

						}
						String formatString;

						switch (discussionNotificationMessage.getType()) {
							case 1:
								if (operator.equals(selfUserId)) {
									if (userIds != null) {
										if (userIds.length > 1) {
											formatString = this.mContext.getResources().getString(
													Res.getInstance(this.mContext).string(
															"notification_message_discussion_add"));
											textContent.append(String.format(formatString,
													new Object[] { "你", Integer.valueOf(userIds.length) }));
										} else if (userIds.length == 1) {
											if ((conversation.getOperatored() == null)
													|| (TextUtils.isEmpty(conversation.getOperatored().getName()))) {
												conversation.setOperatored(getUserInfoByCache(userIds[0]));
											}

											if ((conversation.getOperatored() != null)
													&& (!TextUtils.isEmpty(conversation.getOperatored().getName()))) {
												formatString = this.mContext.getResources().getString(
														Res.getInstance(this.mContext).string(
																"notification_message_discussion_added"));
												textContent.append(String.format(formatString, new Object[] {
														"你",
														conversation.getOperatored().getName() }));
											}

										}

									}

								} else if (userIds != null) {
									if (!operator.equals(selfUserId)) {
										if ((conversation.getOperator() == null)
												|| (TextUtils.isEmpty(conversation.getOperator().getName()))) {
											conversation.setOperator(getUserInfoByCache(userIds[0]));
										}
									}

									if (userIds.length == 1) {
										if ((!userIds[0].equals(selfUserId))
												&& ((conversation.getOperatored() == null) || (TextUtils
														.isEmpty(conversation.getOperatored().getName())))) {
											conversation.setOperatored(getUserInfoByCache(userIds[0]));
										}

										if ((conversation.getOperator() != null)
												&& (!TextUtils.isEmpty(conversation.getOperator().getName()))) {
											formatString = this.mContext.getResources().getString(
													Res.getInstance(this.mContext).string(
															"notification_message_discussion_added"));

											if (userIds[0].equals(selfUserId)) {
												textContent.append(String.format(formatString, new Object[] {
														conversation.getOperator().getName(),
														"你" }));
											} else if ((conversation.getOperatored() != null)
													&& (!TextUtils.isEmpty(conversation.getOperatored().getName()))) {
												textContent.append(String.format(formatString, new Object[] {
														conversation.getOperator().getName(),
														conversation.getOperatored().getName() }));
											}

										}

									} else if (operator.equals(selfUserId)) {
										formatString = this.mContext.getResources().getString(
												Res.getInstance(this.mContext).string(
														"notification_message_discussion_add"));
										textContent.append(String.format(formatString,
												new Object[] { "你", Integer.valueOf(userIds.length) }));
									} else if ((conversation.getOperator() != null)
											&& (!TextUtils.isEmpty(conversation.getOperator().getName()))) {
										formatString = this.mContext.getResources().getString(
												Res.getInstance(this.mContext).string(
														"notification_message_discussion_add"));
										textContent.append(String.format(formatString, new Object[] {
												conversation.getOperator().getName(),
												Integer.valueOf(userIds.length) }));
									}
								}
								break;
							case 2:
								if ((conversation.getOperator() == null)
										|| (TextUtils.isEmpty(conversation.getOperator().getName()))) {
									conversation.setOperator(getUserInfoByCache(userIds[0]));
								}

								if ((userIds != null) && (conversation.getOperator() != null)
										&& (!TextUtils.isEmpty(conversation.getOperator().getName()))) {
									formatString = this.mContext.getResources().getString(
											Res.getInstance(this.mContext).string(
													"notification_message_discussion_exit"));
									textContent.append(String.format(formatString, new Object[] { conversation
											.getOperator().getName() }));
								}
								break;
							case 3:
								formatString = this.mContext.getResources()
										.getString(
												Res.getInstance(this.mContext).string(
														"notification_message_discussion_rename"));

								if (!TextUtils.isEmpty(discussionNotificationMessage.getExtension())) {
									if (operator.equals(selfUserId)) {
										textContent.append(String.format(formatString, new Object[] {
												"你",
												discussionNotificationMessage.getExtension() }));
									} else {
										if ((conversation.getOperator() == null)
												|| (TextUtils.isEmpty(conversation.getOperator().getName()))) {
											conversation.setOperator(getUserInfoByCache(operator));
										}

										if ((conversation.getOperator() != null)
												&& (!TextUtils.isEmpty(conversation.getOperator().getName()))) {
											textContent.append(String.format(formatString, new Object[] {
													conversation.getOperator().getName(),
													discussionNotificationMessage.getExtension() }));
										}
									}
									conversation.setConversationTitle(discussionNotificationMessage.getExtension());
								}
								break;
							case 4:
								if (userIds != null) {
									formatString = this.mContext.getResources().getString(
											Res.getInstance(this.mContext).string(
													"notification_message_discussion_who_removed"));

									if ((!operator.equals(selfUserId))
											&& ((conversation.getOperator() == null) || (TextUtils.isEmpty(conversation
													.getOperator().getName())))) {
										conversation.setOperator(getUserInfoByCache(operator));
									}

									if ((!userIds[0].equals(selfUserId))
											&& ((conversation.getOperatored() == null) || (TextUtils
													.isEmpty(conversation.getOperatored().getName())))) {
										conversation.setOperatored(getUserInfoByCache(userIds[0]));
									}

									if (operator.equals(selfUserId)) {
										if ((conversation.getOperatored() != null)
												&& (!TextUtils.isEmpty(conversation.getOperatored().getName()))) {
											textContent.append(String.format(formatString, new Object[] {
													conversation.getOperatored().getName(),
													"你" }));
										}
									} else if ((conversation.getOperator() != null)
											&& (!TextUtils.isEmpty(conversation.getOperator().getName()))) {
										if (userIds[0].equals(selfUserId)) {
											textContent.append(String.format(formatString, new Object[] {
													"你",
													conversation.getOperator().getName() }));
										} else if ((conversation.getOperatored() != null)
												&& (!TextUtils.isEmpty(conversation.getOperatored().getName()))) {
											textContent.append(String.format(formatString, new Object[] {
													conversation.getOperatored().getName(),
													conversation.getOperator().getName() }));
										}
									}
								}
								break;
							case 5:
								formatString = this.mContext.getResources().getString(
										Res.getInstance(this.mContext).string(
												"notification_message_discussion_is_open_invite"));

								if (operator.equals(selfUserId)) {
									if (!TextUtils.isEmpty(discussionNotificationMessage.getExtension())) {
										if (discussionNotificationMessage.getExtension().equals("1"))
											textContent.append(String.format(formatString, new Object[] { "你", "关闭" }));
										else if (discussionNotificationMessage.getExtension().equals("0"))
											textContent.append(String.format(formatString, new Object[] { "你", "开放" }));
									}
								} else {
									if ((conversation.getOperator() == null)
											|| (TextUtils.isEmpty(conversation.getOperator().getName()))) {
										conversation.setOperator(getUserInfoByCache(operator));
									}

									if (!TextUtils.isEmpty(discussionNotificationMessage.getExtension())) {
										if (discussionNotificationMessage.getExtension().equals("1"))
											textContent.append(String.format(formatString, new Object[] {
													conversation.getOperator().getName(),
													"关闭" }));
										else if (discussionNotificationMessage.getExtension().equals("0"))
											textContent.append(String.format(formatString, new Object[] {
													conversation.getOperator().getName(),
													"开放" }));
									}
								}
								break;
						}

					} else if ((conversation.getLatestMessage() instanceof InformationNotificationMessage)) {
						InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) conversation
								.getLatestMessage();
						if ((informationNotificationMessage != null)
								&& (!TextUtils.isEmpty(informationNotificationMessage.getMessage()))) {
							textContent.append(Html.fromHtml(informationNotificationMessage.getMessage()).toString());
						}
					}
				} else if ((conversation.getLatestMessage() instanceof TextMessage)) {
					textContent.append(conversation.getTextMessageContent());
				} else if ((conversation.getLatestMessage() instanceof ImageMessage)) {
					textContent.append(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("message_type_image_content")));
				} else if ((conversation.getLatestMessage() instanceof VoiceMessage)) {
					textContent.append(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("message_type_voice_content")));
				} else if ((conversation.getLatestMessage() instanceof RichContentMessage)) {
					textContent.append(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("message_type_image_text_content")));
				} else if ((conversation.getLatestMessage() instanceof LocationMessage)) {
					textContent.append(this.mContext.getResources().getString(
							Res.getInstance(this.mContext).string("message_type_location_content")));
				} else if ((conversation.getLatestMessage() instanceof InformationNotificationMessage)) {
					InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) conversation
							.getLatestMessage();
					if ((informationNotificationMessage != null)
							&& (!TextUtils.isEmpty(informationNotificationMessage.getMessage()))) {
						textContent.append(Html.fromHtml(informationNotificationMessage.getMessage()).toString());
					}

				}

			} else if ((conversation.getLatestMessage() instanceof TextMessage)) {
				if (TextUtils.isEmpty(conversation.getSenderUserName()))
					textContent.append(conversation.getTextMessageContent());
				else
					textContent.append(conversation.getSenderUserName()).append("：")
							.append(conversation.getTextMessageContent());
			} else if ((conversation.getLatestMessage() instanceof ImageMessage)) {
				if (conversation.getSenderUserName() != null)
					textContent.append(conversation.getSenderUserName()).append("：");
				textContent.append(this.mContext.getResources().getString(
						Res.getInstance(this.mContext).string("message_type_image_content")));
			} else if ((conversation.getLatestMessage() instanceof VoiceMessage)) {
				if (conversation.getSenderUserName() != null)
					textContent.append(conversation.getSenderUserName()).append("：");
				textContent.append(this.mContext.getResources().getString(
						Res.getInstance(this.mContext).string("message_type_voice_content")));
			} else if ((conversation.getLatestMessage() instanceof RichContentMessage)) {
				if (conversation.getSenderUserName() != null)
					textContent.append(conversation.getSenderUserName()).append("：");
				textContent.append(this.mContext.getResources().getString(
						Res.getInstance(this.mContext).string("message_type_image_text_content")));
			} else if ((conversation.getLatestMessage() instanceof LocationMessage)) {
				if (conversation.getSenderUserName() != null)
					textContent.append(conversation.getSenderUserName()).append("：");
				textContent.append(this.mContext.getResources().getString(
						Res.getInstance(this.mContext).string("message_type_location_content")));
			} else if ((conversation.getLatestMessage() instanceof InformationNotificationMessage)) {
				InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) conversation
						.getLatestMessage();
				if ((informationNotificationMessage != null)
						&& (!TextUtils.isEmpty(informationNotificationMessage.getMessage()))) {
					textContent.append(Html.fromHtml(informationNotificationMessage.getMessage()).toString());
				}

			}

		} else if ((conversation.getLatestMessage() instanceof TextMessage)) {
			textContent.append(conversation.getTextMessageContent());
		} else if ((conversation.getLatestMessage() instanceof ImageMessage)) {
			textContent.append(this.mContext.getResources().getString(
					Res.getInstance(this.mContext).string("message_type_image_content")));
		} else if ((conversation.getLatestMessage() instanceof VoiceMessage)) {
			textContent.append(this.mContext.getResources().getString(
					Res.getInstance(this.mContext).string("message_type_voice_content")));
		} else if ((conversation.getLatestMessage() instanceof RichContentMessage)) {
			textContent.append(this.mContext.getResources().getString(
					Res.getInstance(this.mContext).string("message_type_image_text_content")));
		} else if ((conversation.getLatestMessage() instanceof LocationMessage)) {
			textContent.append(this.mContext.getResources().getString(
					Res.getInstance(this.mContext).string("message_type_location_content")));
		} else if ((conversation.getLatestMessage() instanceof InformationNotificationMessage)) {
			InformationNotificationMessage informationNotificationMessage = (InformationNotificationMessage) conversation
					.getLatestMessage();
			if ((informationNotificationMessage != null)
					&& (!TextUtils.isEmpty(informationNotificationMessage.getMessage()))) {
				textContent.append(Html.fromHtml(informationNotificationMessage.getMessage()).toString());
			}
		}
		return textContent;
	}

	public void setOnGetDataListener(OnGetDataListener mOnGetDataListener) {
		this.mOnGetDataListener = mOnGetDataListener;
	}

	private final UIUserInfo getUserInfoByCache(String userId) {
		if ((RCloudContext.getInstance() != null) && (RCloudContext.getInstance().getUserInfoCache() != null)) {
			RongIMClient.UserInfo userInfo = (RongIMClient.UserInfo) RCloudContext.getInstance().getUserInfoCache()
					.get(userId);

			if (userInfo != null) {
				return new UIUserInfo(userInfo.getUserId(), userInfo.getName(), userInfo.getPortraitUri());
			}
		}

		return null;
	}

	private final UIGroup getGroupInfoByCache(String groupId) {
		if ((RCloudContext.getInstance() != null) && (RCloudContext.getInstance().getGroupCache() != null)) {
			RongIMClient.Group group = (RongIMClient.Group) RCloudContext.getInstance().getGroupCache().get(groupId);

			if (group != null) {
				return new UIGroup(group.getId(), group.getName(), group.getPortraitUri());
			}
		}

		return null;
	}

	public static abstract interface OnGetDataListener {
		public abstract void getDiscussionInfo(int paramInt, String paramString);
	}

	static class ViewHolder {
		View mLayout;
		AsyncImageView mIvUserHead;
		TextView mIvNewMsg;
		TextView mTvUserName;
		TextView mTvSendTime;
		TextView mTvMsgContent;
		ImageView mIvBlock;
	}
}