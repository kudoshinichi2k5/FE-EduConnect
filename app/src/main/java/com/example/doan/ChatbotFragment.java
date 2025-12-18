package com.example.doan;

import android.os.Bundle;
import android.os.Handler;
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
import com.example.doan.model.ChatMessage;
import java.util.ArrayList;
import java.util.List;

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
        if (!text.isEmpty()) {
            // 1. Thêm tin nhắn User vào list
            messageList.add(new ChatMessage(text, true));
            chatAdapter.notifyItemInserted(messageList.size() - 1);
            rvChatMessages.scrollToPosition(messageList.size() - 1);
            edtChatInput.setText("");

            // 2. Giả lập Bot trả lời sau 1 giây
            new Handler().postDelayed(() -> {
                messageList.add(new ChatMessage("Đây là tin nhắn trả lời tự động. Chức năng AI đang được phát triển!", false));
                chatAdapter.notifyItemInserted(messageList.size() - 1);
                rvChatMessages.scrollToPosition(messageList.size() - 1);
            }, 1000);
        }
    }
}