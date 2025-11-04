package com.example.herculean.ai;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.herculean.MainActivity;
import com.example.herculean.R;
import com.example.herculean.workout.Upload;

import java.util.ArrayList;
import java.util.List;

public class ChatBot extends AppCompatActivity {

    private Button buttonHome;
    private Button sendButton;
    private EditText inputMessage;
    private RecyclerView chatRecycler;
    private ChatAdapter chatAdapter;
    private AdviceAI adviceAI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bot);

        buttonHome = findViewById(R.id.homeButton);
        sendButton = findViewById(R.id.sendButton);
        inputMessage = findViewById(R.id.inputMessage);
        chatRecycler = findViewById(R.id.chatRecycler);

        chatAdapter = new ChatAdapter();
        chatRecycler.setLayoutManager(new LinearLayoutManager(this));
        chatRecycler.setAdapter(chatAdapter);

        adviceAI = AdviceAI.getInstance(this);

        buttonHome.setOnClickListener(v -> {
            Intent intent = new Intent(ChatBot.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        sendButton.setOnClickListener(v ->
                sendMessage()
        );

        getSupportActionBar().hide();
        getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void sendMessage() {
        String userText = inputMessage.getText().toString().trim();
        if (TextUtils.isEmpty(userText)) {
            return;
        }

        chatAdapter.addMessage(new ChatMessage(userText, true));
        chatRecycler.scrollToPosition(chatAdapter.getItemCount() - 1);
        inputMessage.setText("");

        adviceAI.sendToGeminiText(userText, false, new AdviceAI.onResultTextCallback() {
                @Override
                public void onResultText(String text) {
                    runOnUiThread(() -> {
                        chatAdapter.addMessage(new ChatMessage(text, false));
                        chatRecycler.scrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }

                @Override
                public void onError(String errorMessage) {
                    runOnUiThread(() -> {
                        chatAdapter.addMessage(new ChatMessage("ERROR: " + errorMessage, false));
                        chatRecycler.scrollToPosition(chatAdapter.getItemCount() - 1);
                    });
                }
            }, null, null
        );
    }

    private static class ChatMessage {
        final String text;
        final boolean fromUser;

        ChatMessage(String text, boolean fromUser) {
            this.text = text;
            this.fromUser = fromUser;
        }
    }

    private static class ChatAdapter extends RecyclerView.Adapter<ChatAdapter.ChatHolder> {
        private final List<ChatMessage> messages = new ArrayList<>();

        void addMessage(ChatMessage msg) {
            messages.add(msg);
            notifyItemInserted(messages.size() - 1);
        }

        @Override
        public ChatHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = android.view.LayoutInflater.from(parent.getContext()).inflate(android.R.layout.simple_list_item_1, parent, false);
            return new ChatHolder(v);
        }

        @Override
        public void onBindViewHolder(ChatHolder holder, int position) {
            ChatMessage message = messages.get(position);
            holder.textView.setText(message.text);
            if (message.fromUser) {
                holder.textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_END);
                holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.holo_blue_light));
            }
            else {
                holder.textView.setTextAlignment(View.TEXT_ALIGNMENT_VIEW_START);
                holder.textView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(), android.R.color.darker_gray));
            }
            int pad = 18;
            holder.textView.setPadding(pad, pad, pad, pad);
        }

        @Override
        public int getItemCount() {
            return messages.size();
        }

        static class ChatHolder extends RecyclerView.ViewHolder {
            TextView textView;
            ChatHolder(View itemView) {
                super(itemView);
                textView = itemView.findViewById(android.R.id.text1);
            }
        }
    }
}
