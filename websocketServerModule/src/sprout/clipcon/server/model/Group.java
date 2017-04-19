package sprout.clipcon.server.model;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.websocket.EncodeException;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import sprout.clipcon.server.controller.UserController;
import sprout.clipcon.server.model.message.Message;

@Getter
@Setter
@NoArgsConstructor
public class Group {
	private String primaryKey;
	private String name;
	// private Server server = Server.getInstance();
	public Map<String, UserController> users = Collections.synchronizedMap(new HashMap<String, UserController>());

	public Group(String primaryKey, String name) {
		this.primaryKey = primaryKey;
		this.name = name;
	}

	public void send(Message message) throws IOException, EncodeException {
		for (String key : users.keySet()) {
			System.out.println(key);
			users.get(key).getSession().getBasicRemote().sendObject(message);
		}
	}

	public boolean addUser(String userEmail, UserController session) {
		users.put(userEmail, session);
		return true;
	}

	public User getUserByEmail(String email) {
		return users.get(email).getUser();
	}

	public Map<String, String> getUserList() {
		Map<String, String> list = new HashMap<String, String>();
		for (String key : users.keySet()) {
			list.put(key, users.get(key).getUser().getName());
		}
		return list;
	}
}
