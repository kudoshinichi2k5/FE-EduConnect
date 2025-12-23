package com.example.doan;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.doan.R;
import com.example.doan.api.ApiClient;
import com.example.doan.api.ApiService;
import com.example.doan.model.ChatMessage;
import com.example.doan.model.ChatbotRequest;
import com.example.doan.model.ChatbotResponse;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChatbotFragment extends Fragment {

    private RecyclerView rvChatMessages;
    private EditText edtChatInput;
    private ImageView btnSendChat, btnBackChat;
    private ChatAdapter chatAdapter;
    private List<ChatMessage> messageList;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chatbot, container, false);

        // 1. Ánh xạ
        rvChatMessages = view.findViewById(R.id.rvChatMessages);
        edtChatInput = view.findViewById(R.id.edtChatInput);
        btnSendChat = view.findViewById(R.id.btnSendChat);
        btnBackChat = view.findViewById(R.id.btnBackChat);

        requireActivity()
                .getWindow()
                .clearFlags(android.view.WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                        | android.view.WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);

        edtChatInput.setInputType(
                android.text.InputType.TYPE_CLASS_TEXT
                        | android.text.InputType.TYPE_TEXT_FLAG_MULTI_LINE
        );

        edtChatInput.setImeOptions(
                android.view.inputmethod.EditorInfo.IME_ACTION_SEND
        );

        edtChatInput.setSingleLine(false);
        edtChatInput.setFocusable(true);
        edtChatInput.setFocusableInTouchMode(true);
        edtChatInput.requestFocus();

        // rvChatMessages.setNestedScrollingEnabled(false);

        // 2. Setup RecyclerView
        messageList = new ArrayList<>();
        // Tin nhắn chào mừng mặc định
        messageList.add(new ChatMessage("Xin chào! Tôi là trợ lý AI EduConnect. Tôi có thể giúp gì cho bạn hôm nay?", false));

        chatAdapter = new ChatAdapter(messageList);
        rvChatMessages.setLayoutManager(new LinearLayoutManager(getContext()));
        rvChatMessages.setAdapter(chatAdapter);

        // 3. Xử lý nút Gửi
        btnSendChat.setOnClickListener(v -> sendMessage());

        // 4. Xử lý nút Back (Quay lại Home)
        btnBackChat.setOnClickListener(v -> {
            if (getActivity() instanceof Home) {
                ((Home) getActivity()).switchToTab(R.id.nav_home);
            }
        });

        return view;
    }

    private void sendMessage() {
        String text = edtChatInput.getText().toString().trim();
        if (text.isEmpty()) return;

        // 1. Add user message
        messageList.add(new ChatMessage(text, true));
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);
        edtChatInput.setText("");

        // 2. Add bot typing indicator
        ChatMessage typingMsg = new ChatMessage("Đang suy nghĩ...", false);
        messageList.add(typingMsg);
        chatAdapter.notifyItemInserted(messageList.size() - 1);
        rvChatMessages.scrollToPosition(messageList.size() - 1);

        // 3. Call API backend
        ApiService apiService = ApiClient.getClient().create(ApiService.class);
        apiService.askChatbot(new ChatbotRequest(text))
                .enqueue(new Callback<ChatbotResponse>() {
                    @Override
                    public void onResponse(
                            @NonNull Call<ChatbotResponse> call,
                            @NonNull Response<ChatbotResponse> response
                    ) {
                        int typingIndex = messageList.indexOf(typingMsg);
                        if (typingIndex != -1) {
                            messageList.remove(typingIndex);
                            chatAdapter.notifyItemRemoved(typingIndex);
                        }

                        String botReply;
                        if (response.isSuccessful() && response.body() != null) {
                            botReply = response.body().getAnswer();
                        } else {
                            botReply = "Xin lỗi, tôi chưa thể trả lời lúc này.";
                        }

                        messageList.add(new ChatMessage(botReply, false));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                        rvChatMessages.scrollToPosition(messageList.size() - 1);
                    }

                    @Override
                    public void onFailure(
                            @NonNull Call<ChatbotResponse> call,
                            @NonNull Throwable t
                    ) {
                        int typingIndex = messageList.indexOf(typingMsg);
                        if (typingIndex != -1) {
                            messageList.remove(typingIndex);
                            chatAdapter.notifyItemRemoved(typingIndex);
                        }

                        messageList.add(new ChatMessage("Lỗi kết nối server.", false));
                        chatAdapter.notifyItemInserted(messageList.size() - 1);
                    }
                });
    }
}