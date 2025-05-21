package client;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class MainScreen extends JFrame implements ActionListener {

	private static final long serialVersionUID = 1L;

	public static int chattingRoom = -1;

	JList<String> connectedServerInfoJList;

	JList<String> onlineUserJList;
	JList<String> groupJList;

	JTabbedPane roomTabbedPane;
	List<RoomMessagesPanel> roomMessagesPanels;
	JList<String> roomUsersJList;

	JPanel enterMessagePanel;
	JTextArea messageArea;

	public MainScreen() {
		// Set look and feel
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		// Set colors
		Color backgroundColor = new Color(240, 242, 245);
		Color primaryColor = new Color(0, 120, 212);
		Color textColor = new Color(51, 51, 51);
		Color borderColor = new Color(200, 200, 200);

		// Set fonts
		Font titleFont = new Font("Segoe UI", Font.BOLD, 14);
		Font normalFont = new Font("Segoe UI", Font.PLAIN, 12);
		Font buttonFont = new Font("Segoe UI", Font.PLAIN, 12);
		Font emojiFont = new Font("Segoe UI Emoji", Font.PLAIN, 14);

		GBCBuilder gbc = new GBCBuilder(1, 1);
		JPanel mainContent = new JPanel(new GridBagLayout());
		mainContent.setBackground(backgroundColor);

		// Server info panel
		JPanel serverInfoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
		serverInfoPanel.setBackground(backgroundColor);
		serverInfoPanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(borderColor),
				String.format("Server %s (%s)", 
					Main.connectServerScreen.connectedServer.nickName,
					SocketController.serverName(Main.socketController.connectedServer.ip,
						Main.socketController.connectedServer.port)),
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.TOP,
				titleFont
			),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));

		connectedServerInfoJList = new JList<String>(new String[] {
			"IP: " + Main.socketController.connectedServer.ip,
			"Port: " + Main.socketController.connectedServer.port,
			"Số user online: " + Main.socketController.connectedServer.connectAccountCount
		});
		connectedServerInfoJList.setFont(normalFont);
		connectedServerInfoJList.setBackground(backgroundColor);
		connectedServerInfoJList.setForeground(textColor);
		serverInfoPanel.add(connectedServerInfoJList);

		// Online users panel
		onlineUserJList = new JList<String>();
		onlineUserJList.setFont(normalFont);
		onlineUserJList.setBackground(backgroundColor);
		onlineUserJList.setForeground(textColor);
		onlineUserJList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String clickedUser = onlineUserJList.getSelectedValue();
					System.out.println("Double click " + clickedUser);
					Room foundRoom = Room.findPrivateRoom(Main.socketController.allRooms, clickedUser);
					if (foundRoom == null) {
						Main.socketController.createPrivateRoom(clickedUser);
					} else {
						int roomTabIndex = -1;
						for (int i = 0; i < roomTabbedPane.getTabCount(); i++) {
							JScrollPane currentScrollPane = (JScrollPane) roomTabbedPane.getComponentAt(i);
							RoomMessagesPanel currentRoomMessagePanel = (RoomMessagesPanel) currentScrollPane.getViewport().getView();
							if (currentRoomMessagePanel.room.id == foundRoom.id) {
								roomTabIndex = i;
								break;
							}
						}

						if (roomTabIndex == -1) {
							newRoomTab(foundRoom);
							roomTabbedPane.setSelectedIndex(roomTabbedPane.getTabCount() - 1);
						} else {
							roomTabbedPane.setSelectedIndex(roomTabIndex);
						}
					}
				}
			}
		});

		JScrollPane onlineUserScrollPane = new JScrollPane(onlineUserJList);
		onlineUserScrollPane.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(borderColor),
				"Danh sách user đang online",
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.TOP,
				titleFont
			),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));

		// Group panel
		groupJList = new JList<String>();
		groupJList.setFont(normalFont);
		groupJList.setBackground(backgroundColor);
		groupJList.setForeground(textColor);
		groupJList.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					String clickedGroup = groupJList.getSelectedValue();
					System.out.println("Double click " + clickedGroup);
					Room foundRoom = Room.findGroup(Main.socketController.allRooms, clickedGroup);

					int roomTabIndex = -1;
					for (int i = 0; i < roomTabbedPane.getTabCount(); i++) {
						JScrollPane currentScrollPane = (JScrollPane) roomTabbedPane.getComponentAt(i);
						RoomMessagesPanel currentRoomMessagePanel = (RoomMessagesPanel) currentScrollPane.getViewport().getView();
						if (currentRoomMessagePanel.room.id == foundRoom.id) {
							roomTabIndex = i;
							break;
						}
					}

					if (roomTabIndex == -1) {
						newRoomTab(foundRoom);
						roomTabbedPane.setSelectedIndex(roomTabbedPane.getTabCount() - 1);
					} else {
						roomTabbedPane.setSelectedIndex(roomTabIndex);
					}
				}
			}
		});

		JScrollPane groupListScrollPane = new JScrollPane(groupJList);
		groupListScrollPane.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(borderColor),
				"Danh sách group",
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.TOP,
				titleFont
			),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));

		JButton createGroupButton = new JButton("Tạo group");
		createGroupButton.setFont(buttonFont);
		createGroupButton.setBackground(primaryColor);
		createGroupButton.setForeground(Color.WHITE);
		createGroupButton.setFocusPainted(false);
		createGroupButton.setBorderPainted(false);
		createGroupButton.setActionCommand("group");
		createGroupButton.addActionListener(this);

		JPanel groupPanel = new JPanel(new GridBagLayout());
		groupPanel.setBackground(backgroundColor);
		groupPanel.add(groupListScrollPane, gbc.setGrid(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
		groupPanel.add(createGroupButton, gbc.setGrid(1, 2).setWeight(1, 0));

		JSplitPane chatSubjectSplitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, onlineUserScrollPane, groupPanel);
		chatSubjectSplitPane.setDividerLocation(230);
		chatSubjectSplitPane.setBackground(backgroundColor);

		JPanel leftPanel = new JPanel(new GridBagLayout());
		leftPanel.setBackground(backgroundColor);
		leftPanel.add(serverInfoPanel, gbc.setGrid(1, 1).setWeight(1, 0).setFill(GridBagConstraints.BOTH));
		leftPanel.add(chatSubjectSplitPane, gbc.setGrid(1, 2).setWeight(1, 1));

		// Chat panel
		JPanel chatPanel = new JPanel(new GridBagLayout());
		chatPanel.setBackground(backgroundColor);

		enterMessagePanel = new JPanel(new GridBagLayout());
		enterMessagePanel.setBackground(Color.WHITE);
		enterMessagePanel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));

		JButton sendButton = new JButton("Gửi");
		sendButton.setFont(buttonFont);
		sendButton.setBackground(primaryColor);
		sendButton.setForeground(Color.WHITE);
		sendButton.setFocusPainted(false);
		sendButton.setBorderPainted(false);
		sendButton.setPreferredSize(new Dimension(60, 30));
		sendButton.setActionCommand("send");
		sendButton.addActionListener(this);

		JButton emojiButton = new JButton(new String(Character.toChars(0x1F601)));
		emojiButton.setFont(emojiFont);
		emojiButton.setBackground(primaryColor);
		emojiButton.setForeground(Color.WHITE);
		emojiButton.setFocusPainted(false);
		emojiButton.setBorderPainted(false);
		emojiButton.setPreferredSize(new Dimension(30, 30));
		emojiButton.setActionCommand("emoji");
		emojiButton.addActionListener(this);

		JButton fileButton = new JButton(Main.getScaledImage("/fileIcon.png", 16, 16));
		if (fileButton.getIcon() == null) {
			fileButton.setText("File");
		}
		fileButton.setFont(buttonFont);
		fileButton.setBackground(primaryColor);
		fileButton.setForeground(Color.WHITE);
		fileButton.setFocusPainted(false);
		fileButton.setBorderPainted(false);
		fileButton.setPreferredSize(new Dimension(60, 30));
		fileButton.setActionCommand("file");
		fileButton.addActionListener(this);

		JButton deleteAllButton = new JButton("Xóa tất cả");
		deleteAllButton.setFont(buttonFont);
		deleteAllButton.setBackground(new Color(220, 53, 69));
		deleteAllButton.setForeground(Color.WHITE);
		deleteAllButton.setFocusPainted(false);
		deleteAllButton.setBorderPainted(false);
		deleteAllButton.setPreferredSize(new Dimension(100, 30));
		deleteAllButton.setActionCommand("deleteAll");
		deleteAllButton.addActionListener(this);

		messageArea = new JTextArea();
		messageArea.setFont(normalFont);
		messageArea.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(borderColor),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));
		messageArea.setMinimumSize(new Dimension(100, 30));
		messageArea.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
		messageArea.setLineWrap(true);
		messageArea.setWrapStyleWord(true);
		InputMap input = messageArea.getInputMap();
		input.put(KeyStroke.getKeyStroke("shift ENTER"), "insert-break");
		input.put(KeyStroke.getKeyStroke("ENTER"), "text-submit");
		messageArea.getActionMap().put("text-submit", new AbstractAction() {
			private static final long serialVersionUID = 1L;

			@Override
			public void actionPerformed(ActionEvent e) {
				sendButton.doClick();
			}
		});

		enterMessagePanel.add(messageArea, gbc.setGrid(1, 1).setWeight(1, 1));
		enterMessagePanel.add(sendButton, gbc.setGrid(2, 1).setWeight(0, 0).setFill(GridBagConstraints.NONE).setAnchor(GridBagConstraints.NORTH));
		enterMessagePanel.add(emojiButton, gbc.setGrid(3, 1));
		enterMessagePanel.add(fileButton, gbc.setGrid(4, 1));
		enterMessagePanel.add(deleteAllButton, gbc.setGrid(5, 1));

		roomTabbedPane = new JTabbedPane();
		roomTabbedPane.setFont(normalFont);
		roomTabbedPane.setBackground(backgroundColor);
		roomTabbedPane.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				JScrollPane selectedTab = (JScrollPane) roomTabbedPane.getSelectedComponent();
				if (selectedTab != null) {
					RoomMessagesPanel selectedMessagePanel = (RoomMessagesPanel) selectedTab.getViewport().getView();
					chattingRoom = selectedMessagePanel.room.id;
					updateRoomUsersJList();
				}
			}
		});

		roomMessagesPanels = new ArrayList<RoomMessagesPanel>();
		roomUsersJList = new JList<String>();
		roomUsersJList.setFont(normalFont);
		roomUsersJList.setBackground(backgroundColor);
		roomUsersJList.setForeground(textColor);
		roomUsersJList.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createTitledBorder(
				BorderFactory.createLineBorder(borderColor),
				"User trong room hiện tại",
				javax.swing.border.TitledBorder.LEFT,
				javax.swing.border.TitledBorder.TOP,
				titleFont
			),
			BorderFactory.createEmptyBorder(5, 5, 5, 5)
		));

		chatPanel.add(roomTabbedPane, gbc.setGrid(1, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 1));
		chatPanel.add(enterMessagePanel, gbc.setGrid(1, 2).setWeight(1, 0));

		JSplitPane roomSplitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, chatPanel, roomUsersJList);
		roomSplitPane.setDividerLocation(420);
		roomSplitPane.setBackground(backgroundColor);

		JSplitPane mainSplitpane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, roomSplitPane);
		mainSplitpane.setBackground(backgroundColor);
		mainContent.add(mainSplitpane, gbc.setGrid(1, 1).setWeight(1, 1));

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				mainSplitpane.setDividerLocation(180);
			}
		});

		this.setPreferredSize(new Dimension(1000, 600));
		this.setTitle("Ứng dụng chat đăng nhập với tên " + Main.socketController.userName);
		this.setContentPane(mainContent);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
	}

	public void updateServerData() {
		Main.socketController.connectedServer.connectAccountCount = Main.socketController.onlineUsers.size();

		connectedServerInfoJList.setListData(new String[] { "IP: " + Main.socketController.connectedServer.ip,
				"Port: " + Main.socketController.connectedServer.port,
				"Số user online: " + Main.socketController.connectedServer.connectAccountCount });
	}

	public void newRoomTab(Room room) {
		System.out.println("=== Bắt đầu mở tab chat mới ===");
		System.out.println("Room ID: " + room.id + ", Room name: " + room.name);
		
		// Kiểm tra xem tab đã tồn tại chưa
		int existingTabIndex = -1;
		for (int i = 0; i < roomTabbedPane.getTabCount(); i++) {
			JScrollPane currentScrollPane = (JScrollPane) roomTabbedPane.getComponentAt(i);
			RoomMessagesPanel currentRoomMessagePanel = (RoomMessagesPanel) currentScrollPane.getViewport().getView();
			if (currentRoomMessagePanel.room.id == room.id) {
				existingTabIndex = i;
				System.out.println("Tìm thấy tab đã tồn tại tại vị trí: " + i);
				break;
			}
		}

		// Nếu tab chưa tồn tại, tạo tab mới
		if (existingTabIndex == -1) {
			System.out.println("Tab chưa tồn tại, tạo tab mới");
			RoomMessagesPanel roomMessagesPanel = new RoomMessagesPanel(room);
			roomMessagesPanels.add(roomMessagesPanel);

			JScrollPane messagesScrollPane = new JScrollPane(roomMessagesPanel);
			messagesScrollPane.setMinimumSize(new Dimension(50, 100));
			messagesScrollPane.getViewport().setBackground(Color.white);

			roomTabbedPane.addTab(room.name, messagesScrollPane);
			roomTabbedPane.setTabComponentAt(roomTabbedPane.getTabCount() - 1,
					new TabComponent(room.name, new ActionListener() {
						@Override
						public void actionPerformed(ActionEvent e) {
							System.out.println("=== Đóng tab chat ===");
							System.out.println("Đóng tab room ID: " + room.id + ", Room name: " + room.name);
							System.out.println("Số lượng tin nhắn trong room: " + room.messages.size());
							roomMessagesPanels.remove(roomMessagesPanel);
							roomTabbedPane.remove(messagesScrollPane);
							System.out.println("Đã đóng tab thành công");
						}
					}));

			// Gọi getRoomMessages để lấy danh sách tin nhắn từ server
			System.out.println("Gọi getRoomMessages để lấy tin nhắn từ server");
			Main.socketController.getRoomMessages(room.id);

			// Hiển thị các tin nhắn đã có trong room
			System.out.println("Hiển thị các tin nhắn đã có trong room");
			for (MessageData message : room.messages) {
				addNewMessageGUI(room.id, message);
			}
		} else {
			// Nếu tab đã tồn tại, chuyển đến tab đó
			System.out.println("Chuyển đến tab đã tồn tại tại vị trí: " + existingTabIndex);
			roomTabbedPane.setSelectedIndex(existingTabIndex);
		}
		System.out.println("=== Kết thúc mở tab chat ===");
	}

	public void updateOnlineUserJList() {
		onlineUserJList.setListData(Main.socketController.onlineUsers.toArray(new String[0]));
	}

	public void updateRoomUsersJList() {
		System.out.println("updateRoomUsersJList");
		Room theChattingRoom = Room.findRoom(Main.socketController.allRooms, chattingRoom);
		if (theChattingRoom != null)
			roomUsersJList.setListData(theChattingRoom.users.toArray(new String[0]));
	}

	public void updateGroupJList() {
		List<String> groupList = new ArrayList<String>();
		for (Room room : Main.socketController.allRooms) {
			if (room.type.equals("group"))
				groupList.add(room.name);
		}
		groupJList.setListData(groupList.toArray(new String[0]));
	}

	// ************** ROOM MESSAGES ***************
	public void addNewMessage(int roomID, String type, String whoSend, String content) {
		System.out.println("=== Thêm tin nhắn mới ===");
		System.out.println("Room ID: " + roomID);
		System.out.println("Type: " + type);
		System.out.println("Who send: " + whoSend);
		System.out.println("Content: " + content);

		Room receiveMessageRoom = Room.findRoom(Main.socketController.allRooms, roomID);
		if (receiveMessageRoom == null) {
			System.out.println("Không tìm thấy room với ID: " + roomID);
			return;
		}

		System.out.println("Số lượng tin nhắn hiện tại trong room: " + receiveMessageRoom.messages.size());

		MessageData messageData = new MessageData(whoSend, type, content);
		
		// Kiểm tra xem tin nhắn đã tồn tại chưa
		boolean messageExists = false;
		for (MessageData msg : receiveMessageRoom.messages) {
			if (msg.whoSend.equals(whoSend) && msg.type.equals(type) && msg.content.equals(content)) {
				messageExists = true;
				System.out.println("Tin nhắn đã tồn tại, bỏ qua");
				break;
			}
		}
		
		if (!messageExists) {
			System.out.println("Thêm tin nhắn mới vào room");
			receiveMessageRoom.messages.add(messageData);
			
			// Kiểm tra xem tab chat đã được mở chưa
			RoomMessagesPanel roomMessagesPanel = RoomMessagesPanel.findRoomMessagesPanel(roomMessagesPanels, roomID);
			if (roomMessagesPanel == null) {
				System.out.println("Tab chat chưa được mở, mở tab mới");
				newRoomTab(receiveMessageRoom);
			} else {
				System.out.println("Thêm tin nhắn vào GUI");
				addNewMessageGUI(roomID, messageData);
			}
		}

		System.out.println("=== Kết thúc thêm tin nhắn ===");
	}

	private void addNewMessageGUI(int roomID, MessageData messageData) {
		System.out.println("Thêm tin nhắn vào GUI - Room: " + roomID);
		RoomMessagesPanel receiveMessageRoomMessagesPanel = RoomMessagesPanel.findRoomMessagesPanel(roomMessagesPanels, roomID);
		if (receiveMessageRoomMessagesPanel != null) {
			MessagePanel newMessagePanel = new MessagePanel(messageData);
			newMessagePanel.setAlignmentX(Component.LEFT_ALIGNMENT);

			receiveMessageRoomMessagesPanel.add(Box.createHorizontalGlue());
			receiveMessageRoomMessagesPanel.add(newMessagePanel);
			receiveMessageRoomMessagesPanel.validate();
			receiveMessageRoomMessagesPanel.repaint();
			roomTabbedPane.validate();
			roomTabbedPane.repaint();
			System.out.println("Đã thêm tin nhắn vào GUI thành công");
		} else {
			System.out.println("Không tìm thấy RoomMessagesPanel cho room: " + roomID);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		switch (e.getActionCommand()) {
		case "group": {
			JDialog chooseUserDialog = new JDialog();
			JPanel chooseUserContent = new JPanel(new GridBagLayout());
			GBCBuilder gbc = new GBCBuilder(1, 1);

			JList<String> onlineUserJList = new JList<String>(Main.socketController.onlineUsers.toArray(new String[0]));
			JScrollPane onlineUserScrollPanel = new JScrollPane(onlineUserJList);
			onlineUserScrollPanel.setBorder(BorderFactory.createTitledBorder("Chọn user để thêm vào nhóm"));

			JLabel groupNameLabel = new JLabel("Tên group: ");
			JTextField groupNameField = new JTextField();
			JButton createButton = new JButton("Tạo group");
			createButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					String groupName = groupNameField.getText();
					if (groupName.isEmpty()) {
						JOptionPane.showMessageDialog(chooseUserDialog, "Tên group không được trống", "Lỗi tạo group",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					List<String> chosenUsers = onlineUserJList.getSelectedValuesList();
					if (chosenUsers.size() < 2) {
						JOptionPane.showMessageDialog(chooseUserDialog,
								"Group phải có từ 3 người trở lên (chọn 2 người trở lên)", "Lỗi tạo group",
								JOptionPane.WARNING_MESSAGE);
						return;
					}
					Main.socketController.createGroup(groupName, chosenUsers);
					chooseUserDialog.setVisible(false);
					chooseUserDialog.dispose();
				}
			});

			chooseUserContent.add(onlineUserScrollPanel,
					gbc.setSpan(2, 1).setFill(GridBagConstraints.BOTH).setWeight(1, 0));
			chooseUserContent.add(groupNameLabel, gbc.setGrid(1, 2).setSpan(1, 1).setWeight(0, 0));
			chooseUserContent.add(groupNameField, gbc.setGrid(2, 2).setWeight(1, 0));
			chooseUserContent.add(createButton,
					gbc.setGrid(1, 3).setSpan(2, 1).setWeight(0, 0).setFill(GridBagConstraints.NONE));

			chooseUserDialog.setMinimumSize(new Dimension(300, 150));
			chooseUserDialog.setContentPane(chooseUserContent);
			chooseUserDialog.setTitle("Tạo group mới");
			chooseUserDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
			chooseUserDialog.pack();
			chooseUserDialog.getRootPane().setDefaultButton(createButton);
			chooseUserDialog.setLocationRelativeTo(null);
			chooseUserDialog.setVisible(true);
			break;
		}

		case "send": {
			String content = messageArea.getText();
			if (content.isEmpty())
				break;
			if (chattingRoom != -1)
				Main.socketController.sendTextToRoom(chattingRoom, content);
			messageArea.setText("");
			break;
		}

		case "emoji": {
			JDialog emojiDialog = new JDialog();
			Object[][] emojiMatrix = new Object[6][6];
			int emojiCode = 0x1F601;
			for (int i = 0; i < 6; i++) {
				for (int j = 0; j < 6; j++)
					emojiMatrix[i][j] = new String(Character.toChars(emojiCode++));
			}

			JTable emojiTable = new JTable();
			emojiTable.setModel(new DefaultTableModel(emojiMatrix, new String[] { "", "", "", "", "", "" }) {
				private static final long serialVersionUID = 1L;

				@Override
				public boolean isCellEditable(int row, int column) {
					return false;
				}
			});
			emojiTable.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 14));
			emojiTable.setShowGrid(false);
			emojiTable.setIntercellSpacing(new Dimension(0, 0));
			emojiTable.setRowHeight(30);
			emojiTable.getTableHeader().setVisible(false);

			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
			for (int i = 0; i < emojiTable.getColumnCount(); i++) {
				emojiTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
				emojiTable.getColumnModel().getColumn(i).setMaxWidth(30);
			}
			emojiTable.setCellSelectionEnabled(true);
			emojiTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			emojiTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					messageArea.setText(messageArea.getText() + emojiTable
							.getValueAt(emojiTable.rowAtPoint(e.getPoint()), emojiTable.columnAtPoint(e.getPoint())));
				}
			});

			emojiDialog.setContentPane(emojiTable);
			emojiDialog.setTitle("Chọn emoji");
			emojiDialog.setModalityType(JDialog.DEFAULT_MODALITY_TYPE);
			emojiDialog.pack();
			emojiDialog.setLocationRelativeTo(this);
			emojiDialog.setVisible(true);
			break;
		}

		case "file": {
			if (chattingRoom == -1)
				break;
			JFileChooser jfc = new JFileChooser();
			jfc.setDialogTitle("Chọn file để gửi");
			int result = jfc.showDialog(null, "Chọn file");
			jfc.setVisible(true);

			if (result == JFileChooser.APPROVE_OPTION) {
				String fileName = jfc.getSelectedFile().getName();
				String filePath = jfc.getSelectedFile().getAbsolutePath();

				Main.socketController.sendFileToRoom(chattingRoom, fileName, filePath);
			}
			break;
		}

		case "deleteAll": {
			if (chattingRoom != -1) {
				int choice = JOptionPane.showConfirmDialog(this, 
					"Bạn có chắc muốn xóa tất cả tin nhắn trong cuộc trò chuyện này?", 
					"Xác nhận xóa", 
					JOptionPane.YES_NO_OPTION);
				if (choice == JOptionPane.YES_OPTION) {
					Room room = Room.findRoom(Main.socketController.allRooms, chattingRoom);
					if (room != null) {
						// Xóa tất cả tin nhắn trong room
						room.messages.clear();
						
						// Xóa tất cả tin nhắn trên UI
						RoomMessagesPanel roomMessagesPanel = RoomMessagesPanel.findRoomMessagesPanel(roomMessagesPanels, room.id);
						if (roomMessagesPanel != null) {
							roomMessagesPanel.removeAll();
							roomMessagesPanel.revalidate();
							roomMessagesPanel.repaint();
						}

						// Thông báo cho server xóa tất cả tin nhắn
						try {
							Main.socketController.sender.write("clearAllMessages");
							Main.socketController.sender.newLine();
							Main.socketController.sender.write(String.valueOf(room.id));
							Main.socketController.sender.newLine();
							Main.socketController.sender.flush();
						} catch (IOException ex) {
							ex.printStackTrace();
						}
					}
				}
			}
			break;
		}
		}
	}

	public static class AudioButton extends JButton implements ActionListener {
		private static final long serialVersionUID = 1L;

		public boolean isRecording;
		ImageIcon microphoneImage;
		ImageIcon stopImage;

		public AudioButton() {
			microphoneImage = Main.getScaledImage("/microphone.png", 16, 16);
			stopImage = Main.getScaledImage("/stop.png", 16, 16);
			if (microphoneImage == null) {
				microphoneImage = new ImageIcon();
			}
			if (stopImage == null) {
				stopImage = new ImageIcon();
			}

			this.setIcon(microphoneImage);
			this.addActionListener(this);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (chattingRoom == -1)
				return;

			isRecording = !isRecording;
			if (isRecording) {
				this.setIcon(stopImage);
				AudioController.startRecord();

			} else {
				this.setIcon(microphoneImage);
				byte[] audioBytes = AudioController.stopRecord();

				String[] options = { "Gửi", "Huỷ" };
				int choice = JOptionPane.showOptionDialog(Main.mainScreen, "Bạn muốn gửi đoạn âm thanh vừa ghi không?",
						"Câu hỏi", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

				if (choice == 0) {
					Main.socketController.sendAudioToRoom(chattingRoom, audioBytes);
				}
			}
		}
	}

	public static class RoomMessagesPanel extends JPanel {
		private static final long serialVersionUID = 1L;
		public Room room;

		public RoomMessagesPanel(Room room) {
			this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
			this.setBackground(Color.white);
			this.room = room;
		}

		public static RoomMessagesPanel findRoomMessagesPanel(List<RoomMessagesPanel> roomMessagesPanelList, int id) {
			for (RoomMessagesPanel roomMessagesPanel : roomMessagesPanelList) {
				if (roomMessagesPanel.room.id == id)
					return roomMessagesPanel;
			}
			return null;
		}
	}

	public static class TabComponent extends JPanel {

		private static final long serialVersionUID = 1L;

		public TabComponent(String tabTitle, ActionListener closeButtonListener) {
			JLabel titleLabel = new JLabel(tabTitle);
			JButton closeButton = new JButton(UIManager.getIcon("InternalFrame.closeIcon"));
			closeButton.addActionListener(closeButtonListener);
			closeButton.setPreferredSize(new Dimension(16, 16));

			this.setLayout(new FlowLayout());
			this.add(titleLabel);
			this.add(closeButton);
			this.setOpaque(false);
		}

	}

}
