package com.panaceasoft.psbuyandsell.ui.chat.adapter;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.panaceasoft.psbuyandsell.Config;
import com.panaceasoft.psbuyandsell.R;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListImageReceiverAdapterBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListImageSenderAdapterBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListMakeOfferReceiverBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListMakeOfferSenderBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListOfferAcceptedReceiverBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListOfferAcceptedSenderBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListOfferRejectedReceiverBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListOfferRejectedSenderBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListReceiverAdapterBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListSenderAdapterBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemChatListTimeBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemItemHasBeenBoughtBinding;
import com.panaceasoft.psbuyandsell.databinding.ItemItemHasBeenSoldBinding;
import com.panaceasoft.psbuyandsell.ui.chat.chat.ChatFragment;
import com.panaceasoft.psbuyandsell.ui.common.DataBoundListAdapter;
import com.panaceasoft.psbuyandsell.utils.Constants;
import com.panaceasoft.psbuyandsell.utils.Objects;
import com.panaceasoft.psbuyandsell.viewobject.Item;
import com.panaceasoft.psbuyandsell.viewobject.messageHolder.Message;

import java.util.List;

import static java.lang.Integer.parseInt;

public class ChatListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final androidx.databinding.DataBindingComponent dataBindingComponent;
    private DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface;
    private final clickCallBack callback;
    private List<Message> messageList;
    private int dataVersion = 0;
    private String userId;
    private String itemId;
    private String otherUserProfileUrl;

//    Context context;

    public ChatListAdapter(androidx.databinding.DataBindingComponent dataBindingComponent, DataBoundListAdapter.DiffUtilDispatchedInterface diffUtilDispatchedInterface, String userId,
                           clickCallBack callback, String itemId, String otherUserProfileUrl) {
        this.dataBindingComponent = dataBindingComponent;
        this.diffUtilDispatchedInterface = diffUtilDispatchedInterface;
        this.userId = userId;
        this.callback = callback;
        this.itemId = itemId;
        this.otherUserProfileUrl = otherUserProfileUrl;

    }

    public class ImageSenderViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListImageSenderAdapterBinding imageAdapterBinding;

        ImageSenderViewHolder(ItemChatListImageSenderAdapterBinding itemChatListImageAdapterBinding) {
            super(itemChatListImageAdapterBinding.getRoot());
            this.imageAdapterBinding = itemChatListImageAdapterBinding;
        }

        public void bind(Message message) {
            imageAdapterBinding.setMessage(message);
            imageAdapterBinding.executePendingBindings();

            imageAdapterBinding.chatImageView.setOnClickListener(v -> {
                Message chatMessage = imageAdapterBinding.getMessage();

                callback.onImageClicked(chatMessage);
            });
        }
    }

    public class TextSenderViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListSenderAdapterBinding textAdapterBinding;

        TextSenderViewHolder(ItemChatListSenderAdapterBinding itemChatListAdapterBinding) {
            super(itemChatListAdapterBinding.getRoot());
            this.textAdapterBinding = itemChatListAdapterBinding;
        }

        public void bind(Message message) {
            textAdapterBinding.setMessage(message);
            textAdapterBinding.executePendingBindings();

            textAdapterBinding.messageTextView.setOnLongClickListener(view -> {
                Message chatMessage = textAdapterBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onSenderChatMessageClicked(message, textAdapterBinding);

                }
                return true;
            });
        }
    }

    public class ImageReceiverViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListImageReceiverAdapterBinding imageAdapterBinding;

        ImageReceiverViewHolder(ItemChatListImageReceiverAdapterBinding itemChatListImageAdapterBinding) {
            super(itemChatListImageAdapterBinding.getRoot());
            this.imageAdapterBinding = itemChatListImageAdapterBinding;
        }

        public void bind(Message message) {
            imageAdapterBinding.setMessage(message);

            imageAdapterBinding.profileImageView.setOnClickListener(v -> {
                Message chatMessage = imageAdapterBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onProfileClicked();
                }
            });

            imageAdapterBinding.chatImageView.setOnClickListener(v -> {
                Message chatMessage = imageAdapterBinding.getMessage();
                callback.onImageClicked(chatMessage);
            });

            dataBindingComponent.getFragmentBindingAdapters().bindCircleImage(imageAdapterBinding.profileImageView, otherUserProfileUrl);


            imageAdapterBinding.executePendingBindings();
        }
    }

    public class TextReceiverViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListReceiverAdapterBinding textAdapterBinding;

        TextReceiverViewHolder(ItemChatListReceiverAdapterBinding itemChatListAdapterBinding) {
            super(itemChatListAdapterBinding.getRoot());
            this.textAdapterBinding = itemChatListAdapterBinding;
        }

        public void bind(Message message) {
            textAdapterBinding.setMessage(message);

            textAdapterBinding.profileImageView.setOnClickListener(v -> {
                Message chatMessage = textAdapterBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onProfileClicked();
                }
            });

            textAdapterBinding.messageTextView.setOnLongClickListener(view -> {
                Message chatMessage = textAdapterBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onReceiverChatMessageClicked(message, textAdapterBinding);

                }
                return true;
            });

            dataBindingComponent.getFragmentBindingAdapters().bindCircleImage(textAdapterBinding.profileImageView, otherUserProfileUrl);


            textAdapterBinding.executePendingBindings();
        }
    }

    public class TimeViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListTimeBinding itemChatListTimeBinding;

        TimeViewHolder(ItemChatListTimeBinding itemChatListTimeBinding) {
            super(itemChatListTimeBinding.getRoot());
            this.itemChatListTimeBinding = itemChatListTimeBinding;
        }

        public void bind(Message message) {
            itemChatListTimeBinding.setMessage(message);
            itemChatListTimeBinding.executePendingBindings();
        }
    }

    public class MakeOfferSenderViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListMakeOfferSenderBinding itemChatListMakeOfferSenderBinding;

        MakeOfferSenderViewHolder(ItemChatListMakeOfferSenderBinding itemChatListMakeOfferSenderBinding) {
            super(itemChatListMakeOfferSenderBinding.getRoot());
            this.itemChatListMakeOfferSenderBinding = itemChatListMakeOfferSenderBinding;
        }

        public void bind(Message message) {
            itemChatListMakeOfferSenderBinding.setMessage(message);
            itemChatListMakeOfferSenderBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListMakeOfferSenderBinding.priceTextView.setText(message.message);
            } else {
                itemChatListMakeOfferSenderBinding.priceTextView.setText(R.string.item_price_free);
            }
        }
    }

    public class MakeOfferReceiverViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListMakeOfferReceiverBinding itemChatListMakeOfferReceiverBinding;

        MakeOfferReceiverViewHolder(ItemChatListMakeOfferReceiverBinding itemChatListMakeOfferReceiverBinding) {
            super(itemChatListMakeOfferReceiverBinding.getRoot());
            this.itemChatListMakeOfferReceiverBinding = itemChatListMakeOfferReceiverBinding;
        }

        @SuppressLint("StringFormatInvalid")
        public void bind(Message message) {
            itemChatListMakeOfferReceiverBinding.setMessage(message);

            if (message.offerStatus == Constants.CHAT_STATUS_ACCEPT
                    || message.offerStatus == Constants.CHAT_STATUS_REJECT) {
                itemChatListMakeOfferReceiverBinding.acceptButton.setVisibility(View.GONE);
                itemChatListMakeOfferReceiverBinding.rejectButton.setVisibility(View.GONE);
                itemChatListMakeOfferReceiverBinding.spaceView.setVisibility(View.GONE);
            } else {
                itemChatListMakeOfferReceiverBinding.acceptButton.setVisibility(View.VISIBLE);
                itemChatListMakeOfferReceiverBinding.rejectButton.setVisibility(View.VISIBLE);
                itemChatListMakeOfferReceiverBinding.spaceView.setVisibility(View.VISIBLE);
            }

            itemChatListMakeOfferReceiverBinding.profileImageView.setOnClickListener(v -> {
                Message chatMessage = itemChatListMakeOfferReceiverBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onProfileClicked();
                }
            });

            itemChatListMakeOfferReceiverBinding.acceptButton.setOnClickListener(v -> {
                Message chatMessage = itemChatListMakeOfferReceiverBinding.getMessage();

                callback.onAcceptButtonClicked(chatMessage);
            });

            itemChatListMakeOfferReceiverBinding.rejectButton.setOnClickListener(v -> {
                Message chatMessage = itemChatListMakeOfferReceiverBinding.getMessage();

                callback.onRejectButtonClicked(chatMessage);
            });

            dataBindingComponent.getFragmentBindingAdapters().bindCircleImage(itemChatListMakeOfferReceiverBinding.profileImageView, otherUserProfileUrl);

            itemChatListMakeOfferReceiverBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListMakeOfferReceiverBinding.priceTextView.setText(message.message);
            } else {
                itemChatListMakeOfferReceiverBinding.priceTextView.setText(R.string.item_price_free);
            }
        }
    }

    public class OfferSenderAcceptedViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListOfferAcceptedSenderBinding itemChatListOfferAcceptedSenderBinding;

        OfferSenderAcceptedViewHolder(ItemChatListOfferAcceptedSenderBinding itemChatListOfferAcceptedSenderBinding) {
            super(itemChatListOfferAcceptedSenderBinding.getRoot());
            this.itemChatListOfferAcceptedSenderBinding = itemChatListOfferAcceptedSenderBinding;
        }

        public void bind(Message message) {
            itemChatListOfferAcceptedSenderBinding.setMessage(message);
            itemChatListOfferAcceptedSenderBinding.executePendingBindings();

            if (message.isUserBought) {
                itemChatListOfferAcceptedSenderBinding.userBoughtButton.setVisibility(View.GONE);
            } else {
                itemChatListOfferAcceptedSenderBinding.userBoughtButton.setVisibility(View.VISIBLE);
            }

            itemChatListOfferAcceptedSenderBinding.userBoughtButton.setOnClickListener(v -> {
                Message chatMessage = itemChatListOfferAcceptedSenderBinding.getMessage();

                callback.onUserBoughtButtonClicked(chatMessage);
            });

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListOfferAcceptedSenderBinding.priceTextView.setText(message.message);
            } else {
                itemChatListOfferAcceptedSenderBinding.priceTextView.setText(R.string.item_price_free);
            }

        }
    }

    public class OfferReceiverAcceptedViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListOfferAcceptedReceiverBinding itemChatListOfferAcceptedReceiverBinding;

        OfferReceiverAcceptedViewHolder(ItemChatListOfferAcceptedReceiverBinding OfferReceiverAcceptedViewHolder) {
            super(OfferReceiverAcceptedViewHolder.getRoot());
            this.itemChatListOfferAcceptedReceiverBinding = OfferReceiverAcceptedViewHolder;
        }

        public void bind(Message message) {
            itemChatListOfferAcceptedReceiverBinding.setMessage(message);

            itemChatListOfferAcceptedReceiverBinding.profileImageView.setOnClickListener(v -> {
                Message chatMessage = itemChatListOfferAcceptedReceiverBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onProfileClicked();
                }
            });

            dataBindingComponent.getFragmentBindingAdapters().bindCircleImage(itemChatListOfferAcceptedReceiverBinding.profileImageView, otherUserProfileUrl);

            itemChatListOfferAcceptedReceiverBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListOfferAcceptedReceiverBinding.priceTextView.setText(message.message);
            } else {
                itemChatListOfferAcceptedReceiverBinding.priceTextView.setText(R.string.item_price_free);
            }
        }

    }

    public class OfferSenderRejectedViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListOfferRejectedSenderBinding itemChatListOfferRejectedSenderBinding;

        OfferSenderRejectedViewHolder(ItemChatListOfferRejectedSenderBinding itemChatListOfferRejectedSenderBinding) {
            super(itemChatListOfferRejectedSenderBinding.getRoot());
            this.itemChatListOfferRejectedSenderBinding = itemChatListOfferRejectedSenderBinding;
        }

        public void bind(Message message) {
            itemChatListOfferRejectedSenderBinding.setMessage(message);
            itemChatListOfferRejectedSenderBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListOfferRejectedSenderBinding.priceTextView.setText(message.message);
            } else {
                itemChatListOfferRejectedSenderBinding.priceTextView.setText(R.string.item_price_free);
            }
        }

    }

    public class OfferReceiverRejectedViewHolder extends RecyclerView.ViewHolder {

        private final ItemChatListOfferRejectedReceiverBinding itemChatListOfferRejectedReceiverBinding;

        OfferReceiverRejectedViewHolder(ItemChatListOfferRejectedReceiverBinding OfferReceiverAcceptedViewHolder) {
            super(OfferReceiverAcceptedViewHolder.getRoot());
            this.itemChatListOfferRejectedReceiverBinding = OfferReceiverAcceptedViewHolder;
        }

        public void bind(Message message) {
            itemChatListOfferRejectedReceiverBinding.setMessage(message);
            itemChatListOfferRejectedReceiverBinding.profileImageView.setOnClickListener(v -> {
                Message chatMessage = itemChatListOfferRejectedReceiverBinding.getMessage();
                if (chatMessage != null && callback != null) {
                    callback.onProfileClicked();
                }
            });

            dataBindingComponent.getFragmentBindingAdapters().bindCircleImage(itemChatListOfferRejectedReceiverBinding.profileImageView, otherUserProfileUrl);

            itemChatListOfferRejectedReceiverBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemChatListOfferRejectedReceiverBinding.priceTextView.setText(message.message);
            } else {
                itemChatListOfferRejectedReceiverBinding.priceTextView.setText(R.string.item_price_free);
            }
        }
    }

    public class ItemHasBeenSoldViewHolder extends RecyclerView.ViewHolder {

        private final ItemItemHasBeenSoldBinding itemItemHasBeenSoldBinding;

        ItemHasBeenSoldViewHolder(ItemItemHasBeenSoldBinding itemItemHasBeenSoldBinding) {
            super(itemItemHasBeenSoldBinding.getRoot());
            this.itemItemHasBeenSoldBinding = itemItemHasBeenSoldBinding;
        }

        public void bind(Message message) {
            itemItemHasBeenSoldBinding.setMessage(message);
            itemItemHasBeenSoldBinding.executePendingBindings();

            if(!(message.message.equals(Config.freePrice))) {
                itemItemHasBeenSoldBinding.priceTextView.setText(message.message);
            } else {
                itemItemHasBeenSoldBinding.priceTextView.setText(R.string.item_price_free);
            }
        }
    }

    public class ItemHasBeenBoughtViewHolder extends RecyclerView.ViewHolder {

        private final ItemItemHasBeenBoughtBinding itemItemHasBeenBoughtBinding;

        ItemHasBeenBoughtViewHolder(ItemItemHasBeenBoughtBinding itemItemHasBeenBoughtBinding) {
            super(itemItemHasBeenBoughtBinding.getRoot());
            this.itemItemHasBeenBoughtBinding = itemItemHasBeenBoughtBinding;
        }

        public void bind(Message message) {
            itemItemHasBeenBoughtBinding.setMessage(message);
            itemItemHasBeenBoughtBinding.executePendingBindings();

            itemItemHasBeenBoughtBinding.giveReviewButton.setOnClickListener(v -> callback.onGiveReviewClicked());

            if (message.isSold || !message.sendByUserId.equals(userId)) {
                itemItemHasBeenBoughtBinding.markAsSoldButton.setVisibility(View.GONE);
            } else {
                itemItemHasBeenBoughtBinding.markAsSoldButton.setVisibility(View.VISIBLE);
            }

            itemItemHasBeenBoughtBinding.markAsSoldButton.setOnClickListener(v -> {
                Message chatMessage = itemItemHasBeenBoughtBinding.getMessage();

                callback.onMarkAsSoldButtonClicked(chatMessage);
            });

            if(!(message.message.equals(Config.freePrice))) {
                itemItemHasBeenBoughtBinding.priceTextView.setText(message.message);
            } else {
                itemItemHasBeenBoughtBinding.priceTextView.setText(R.string.item_price_free);
            }
        }
    }

    protected void dispatched() {
        if (diffUtilDispatchedInterface != null) {
            diffUtilDispatchedInterface.onDispatched();
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == Constants.CHAT_DATE_UI) {

            ItemChatListTimeBinding itemChatListTimeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_time, parent, false, dataBindingComponent);

            return new TimeViewHolder(itemChatListTimeBinding);

        } else if (viewType == Constants.CHAT_SENDER_UI) {

            ItemChatListSenderAdapterBinding textSenderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_sender_adapter, parent, false, dataBindingComponent);

            return new TextSenderViewHolder(textSenderBinding);

        } else if (viewType == Constants.CHAT_SENDER_IMAGE_UI) {

            ItemChatListImageSenderAdapterBinding imageSenderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_image_sender_adapter, parent, false, dataBindingComponent);

            return new ImageSenderViewHolder(imageSenderBinding);

        } else if (viewType == Constants.CHAT_RECEIVER_UI) {

            ItemChatListReceiverAdapterBinding textReceiverBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_receiver_adapter, parent, false, dataBindingComponent);

            return new TextReceiverViewHolder(textReceiverBinding);

        } else if (viewType == Constants.CHAT_RECEIVER_IMAGE_UI) {

            ItemChatListImageReceiverAdapterBinding imageReceiverBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_image_receiver_adapter, parent, false, dataBindingComponent);

            return new ImageReceiverViewHolder(imageReceiverBinding);

        } else if (viewType == Constants.CHAT_SENDER_OFFER_UI) {

            ItemChatListMakeOfferSenderBinding makeOfferSenderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_make_offer_sender, parent, false, dataBindingComponent);

            return new MakeOfferSenderViewHolder(makeOfferSenderBinding);

        } else if (viewType == Constants.CHAT_RECEIVER_OFFER_UI) {

            ItemChatListMakeOfferReceiverBinding makeOfferReceiverBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_make_offer_receiver, parent, false, dataBindingComponent);

            return new MakeOfferReceiverViewHolder(makeOfferReceiverBinding);

        } else if (viewType == Constants.CHAT_SENDER_OFFER_ACCEPT_UI) {

            ItemChatListOfferAcceptedSenderBinding itemChatListOfferAcceptedSenderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_offer_accepted_sender, parent, false, dataBindingComponent);

            return new OfferSenderAcceptedViewHolder(itemChatListOfferAcceptedSenderBinding);

        } else if (viewType == Constants.CHAT_RECEIVER_OFFER_ACCEPT_UI) {

            ItemChatListOfferAcceptedReceiverBinding itemChatListOfferAcceptedReceiverBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_offer_accepted_receiver, parent, false, dataBindingComponent);

            return new OfferReceiverAcceptedViewHolder(itemChatListOfferAcceptedReceiverBinding);

        } else if (viewType == Constants.CHAT_SENDER_OFFER_REJECT_UI) {

            ItemChatListOfferRejectedSenderBinding itemChatListOfferRejectedSenderBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_offer_rejected_sender, parent, false, dataBindingComponent);

            return new OfferSenderRejectedViewHolder(itemChatListOfferRejectedSenderBinding);

        } else if (viewType == Constants.CHAT_RECEIVER_OFFER_REJECT_UI) {

            ItemChatListOfferRejectedReceiverBinding itemChatListOfferRejectedReceiverBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_offer_rejected_receiver, parent, false, dataBindingComponent);

            return new OfferReceiverRejectedViewHolder(itemChatListOfferRejectedReceiverBinding);

        } else if (viewType == Constants.CHAT_ITEM_HAS_BEEN_SOLD ) {

            ItemItemHasBeenSoldBinding itemItemHasBeenSoldBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_item_has_been_sold, parent, false, dataBindingComponent);

            return new ItemHasBeenSoldViewHolder(itemItemHasBeenSoldBinding);

        }  else if (viewType == Constants.CHAT_ITEM_HAS_BEEN_BOUGHT) {

        ItemItemHasBeenBoughtBinding itemItemHasBeenBoughtBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_item_has_been_bought, parent, false, dataBindingComponent);

        return new ItemHasBeenBoughtViewHolder(itemItemHasBeenBoughtBinding);

    } else {

            ItemChatListTimeBinding itemChatListTimeBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.item_chat_list_time, parent, false, dataBindingComponent);

            return new TimeViewHolder(itemChatListTimeBinding);

        }
    }

    @Override
    public int getItemViewType(int position) {

        if (messageList != null && messageList.size() > 0) {

            if (position != messageList.size()) {

                if (messageList.get(position).type == Constants.CHAT_TYPE_DATE) {

                    return Constants.CHAT_DATE_UI;

                } else {
                    if (messageList.get(position).sendByUserId.equals(userId)) { //sender

                        if (messageList.get(position).type == Constants.CHAT_TYPE_TEXT) {

                            if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_OFFER) {
                                return Constants.CHAT_SENDER_OFFER_UI;
                            } else if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_ACCEPT) {
                                return Constants.CHAT_SENDER_OFFER_ACCEPT_UI;
                            } else if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_REJECT) {
                                return Constants.CHAT_SENDER_OFFER_REJECT_UI;
                            } else {
                                return Constants.CHAT_SENDER_UI;
                            }

                        } else if (messageList.get(position).type == Constants.CHAT_TYPE_OFFER) {

                            return Constants.CHAT_SENDER_OFFER_UI;

                        } else if (messageList.get(position).type == Constants.CHAT_TYPE_SOLD) {

                            return Constants.CHAT_ITEM_HAS_BEEN_SOLD;

                        }  else if (messageList.get(position).type == Constants.CHAT_TYPE_BOUGHT) {

                            return Constants.CHAT_ITEM_HAS_BEEN_BOUGHT;

                        } else {

                            return Constants.CHAT_SENDER_IMAGE_UI;

                        }
                    } else { //receiver
                        if (messageList.get(position).type == Constants.CHAT_TYPE_TEXT) {

                            if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_OFFER) {
                                return Constants.CHAT_RECEIVER_OFFER_UI;
                            } else if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_ACCEPT) {
                                return Constants.CHAT_RECEIVER_OFFER_ACCEPT_UI;
                            } else if (messageList.get(position).offerStatus == Constants.CHAT_STATUS_REJECT) {
                                return Constants.CHAT_RECEIVER_OFFER_REJECT_UI;
                            } else {
                                return Constants.CHAT_RECEIVER_UI;
                            }

                        } else if (messageList.get(position).type == Constants.CHAT_TYPE_OFFER) {
                            return Constants.CHAT_RECEIVER_OFFER_UI;
                        } else if (messageList.get(position).type == Constants.CHAT_TYPE_SOLD) {
                            return Constants.CHAT_ITEM_HAS_BEEN_SOLD;
                        } else if (messageList.get(position).type == Constants.CHAT_TYPE_BOUGHT) {
                            return Constants.CHAT_ITEM_HAS_BEEN_BOUGHT;
                        } else {
                            return Constants.CHAT_RECEIVER_IMAGE_UI;
                        }
                    }
                }
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (messageList.get(position).itemId.equals(itemId)) {
            if (holder instanceof TextSenderViewHolder) {

                ((TextSenderViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof ImageSenderViewHolder) {

                ((ImageSenderViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof TextReceiverViewHolder) {

                ((TextReceiverViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof ImageReceiverViewHolder) {

                ((ImageReceiverViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof TimeViewHolder) {

                ((TimeViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof MakeOfferSenderViewHolder) {

                ((MakeOfferSenderViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof MakeOfferReceiverViewHolder) {

                ((MakeOfferReceiverViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof OfferSenderAcceptedViewHolder) {

                ((OfferSenderAcceptedViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof OfferReceiverAcceptedViewHolder) {

                ((OfferReceiverAcceptedViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof OfferSenderRejectedViewHolder) {

                ((OfferSenderRejectedViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof OfferReceiverRejectedViewHolder) {

                ((OfferReceiverRejectedViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof ItemHasBeenSoldViewHolder) {

                ((ItemHasBeenSoldViewHolder) holder).bind(messageList.get(position));

            } else if (holder instanceof ItemHasBeenBoughtViewHolder) {

                ((ItemHasBeenBoughtViewHolder) holder).bind(messageList.get(position));

            }

        }
    }

    public interface clickCallBack {

        void onImageClicked(Message message);

        void onSenderChatMessageClicked(Message message,ItemChatListSenderAdapterBinding binding);

        void onAcceptButtonClicked(Message message);

        void onRejectButtonClicked(Message message);

        void onMarkAsSoldButtonClicked(Message message);

        void onUserBoughtButtonClicked(Message message);

        void onProfileClicked();

        void onReceiverChatMessageClicked(Message message, ItemChatListReceiverAdapterBinding binding);

        void onGiveReviewClicked();
    }

    @Override
    public int getItemCount() {

        if (messageList != null && messageList.size() > 0) {
            return messageList.size();
        } else {
            return 0;
        }
    }

    @SuppressLint("StaticFieldLeak")
    @MainThread
    public void replaceMessageList(List<Message> update) {
        dataVersion++;
        if (messageList == null) {
            if (update == null) {
                return;
            }
            messageList = update;
            notifyDataSetChanged();
        } else if (update == null) {
            int oldSize = messageList.size();
            messageList = null;
            notifyItemRangeRemoved(0, oldSize);

        } else {
            final int startVersion = dataVersion;
            final List<Message> oldItems = messageList;
            new AsyncTask<Void, Void, DiffUtil.DiffResult>() {
                @Override
                protected DiffUtil.DiffResult doInBackground(Void... voids) {
                    return DiffUtil.calculateDiff(new DiffUtil.Callback() {
                        @Override
                        public int getOldListSize() {
                            return oldItems.size();
                        }

                        @Override
                        public int getNewListSize() {
                            return update.size();
                        }

                        @Override
                        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
                            Message oldItem = oldItems.get(oldItemPosition);
                            Message newItem = update.get(newItemPosition);

                            return Objects.equals(oldItem.addedDate, newItem.addedDate)
                                    && oldItem.message.equals(newItem.message);
                        }

                        @Override
                        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
                            Message oldItem = oldItems.get(oldItemPosition);
                            Message newItem = update.get(newItemPosition);

                            return Objects.equals(oldItem.addedDate, newItem.addedDate)
                                    && oldItem.message.equals(newItem.message);
                        }
                    });
                }

                @Override
                protected void onPostExecute(DiffUtil.DiffResult diffResult) {
                    if (startVersion != dataVersion) {
                        // ignore update
                        return;
                    }

                    diffResult.dispatchUpdatesTo(ChatListAdapter.this);
                    dispatched();

                    messageList = update;
                    notifyDataSetChanged();
                    diffResult.dispatchUpdatesTo(ChatListAdapter.this);
                }
            }.execute();
        }
    }

}





