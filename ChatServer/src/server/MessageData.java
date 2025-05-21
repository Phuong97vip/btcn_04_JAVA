package server;

import java.util.ArrayList;
import java.util.List;

public class MessageData {
	public String whoSend;
	public String type;
	public String content;
	public List<String> deletedBy; // Danh sách người dùng đã xóa tin nhắn này

	public MessageData(String whoSend, String type, String content) {
		this.whoSend = whoSend;
		this.type = type;
		this.content = content;
		this.deletedBy = new ArrayList<>();
	}
} 