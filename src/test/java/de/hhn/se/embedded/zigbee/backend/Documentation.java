package de.hhn.se.embedded.zigbee.backend;

import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.MediaType;
import org.springframework.restdocs.RestDocumentation;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.hhn.se.embedded.zigbee.backend.Command.CommandType;
import de.hhn.se.embedded.zigbee.backend.device.Device;
import de.hhn.se.embedded.zigbee.backend.device.Device.Type;
import de.hhn.se.embedded.zigbee.backend.room.Room;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
public class Documentation {

	private static final String DEVICE_ID = "a614eb20-4c6f-4d1c-91c5-61cdeddcf843";

	private static final String ROOM_ID = "d934eb20-4c6f-4d1c-91c5-61cdeddcf843";

	private static final String TOKEN = "eyJpZCI6MTEsInVzZXJuYW1lIjoidXNlciIsImV4cGlyZXMiOjE0NjMxNTY3MzE2NTksInJvbGVzIjpbIlVTRVIiXX0=.hGzDlevrhaISOrpiybm4JDOQW95frs6stJvfZXPzi7M=";

	@Autowired
	private ObjectMapper objectMapper;

	@Rule
	public RestDocumentation restDocumentation = new RestDocumentation(
			"build/generated-snippets");

	@Autowired
	private WebApplicationContext context;

	private MockMvc mockMvc;

	@Before
	public void setUp() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.context)
				.apply(springSecurity())
				.apply(documentationConfiguration(this.restDocumentation))
				.build();
	}

	
	@Test
	public void registerRoom() throws Exception {
		Room room = new Room();
		room.setRoomId(ROOM_ID);
		room.setName("Wohnzimmer");
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(room))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("register-room"));
	}
	
	@Test
	public void device() throws Exception {
		Room room = new Room();
		room.setRoomId(ROOM_ID);
		room.setName("Wohnzimmer");
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(room))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk());
		
		Device device = new Device();
		device.setName("Licht");
		device.setType(Type.LIGHT.name());
		device.setDeviceId(DEVICE_ID);
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId()+"1/devices/"+device.getDeviceId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(device))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("register-device-fail"));
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(device))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("register-device-success"));
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId()+"A")
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("get-device-fail"));
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId())
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("get-device-success"));
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId()+"/devices")
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("get-devices-success"));
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId()+"145/devices")
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("get-devices-fail"));
		
		this.mockMvc.perform(delete("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId())
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("delete-device-success"));
		
		this.mockMvc.perform(delete("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId()+"12")
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("delete-device-fail"));
		
		
	}
	
	@Test
	public void doCommand() throws Exception {
		
		
		Command c = new Command();
		c.setType(CommandType.SET.name());
		c.setValue(20);
		
		Room room = new Room();
		room.setRoomId(ROOM_ID);
		room.setName("Wohnzimmer");
		
		Device device = new Device();
		device.setName("Heizung");
		device.setType(Type.HEATING.name());
		device.setDeviceId(DEVICE_ID);
		
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(room))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk());	
		

		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(device))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("register-device-success"));
		
		
		
		this.mockMvc.perform(post("/api/rooms/"+room.getRoomId()+"/devices/"+ device.getDeviceId()+"/command")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(c))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("do-command-success"));
		
		
		this.mockMvc.perform(post("/api/rooms/"+room.getRoomId()+"36/devices/"+ device.getDeviceId()+"/command")
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(c))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("do-command-fail"));
		

	}
	
	@Test
	public void getRoom() throws Exception {
		Room room = new Room();
		room.setRoomId(ROOM_ID);
		room.setName("Wohnzimmer");
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(room))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk());
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId())
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("get-room-success"));
		
		this.mockMvc.perform(get("/api/rooms/"+room.getRoomId()+1)
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("get-room-fail"));
		
		this.mockMvc.perform(get("/api/rooms")
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("get-rooms-success"));
	}
	
	@Test
	public void deleteRoom() throws Exception {
		Room room = new Room();
		room.setRoomId(ROOM_ID);
		room.setName("Wohnzimmer");
		
		this.mockMvc.perform(put("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.content(this.objectMapper.writeValueAsString(room))
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk());
		
		this.mockMvc.perform(delete("/api/rooms/"+room.getRoomId()+"1")
				.contentType(MediaType.APPLICATION_JSON)
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().is(404)).andDo(document("delete-room-fail"));
		
		this.mockMvc.perform(delete("/api/rooms/"+room.getRoomId())
				.contentType(MediaType.APPLICATION_JSON)
				.header("X-AUTH-TOKEN", TOKEN))
				.andExpect(status().isOk()).andDo(document("delete-room-success"));
	}
	
	
	
	@Test
	public void register() throws Exception {
		this.mockMvc
		.perform(
				post("/api/register").contentType(
						MediaType.APPLICATION_JSON).content("{\"username\":\"testuser\", \"password\": \"123\"}"))
		.andExpect(status().is(422)).andDo(document("register_fail"));
		
		this.mockMvc
				.perform(
						post("/api/register").contentType(
								MediaType.APPLICATION_JSON).content("{\"username\":\"testuser\", \"password\": \"testpassword\"}"))
				.andExpect(status().isOk()).andDo(document("register_success"));
	}
	
	@Test
	public void login() throws Exception {
		this.mockMvc
		.perform(
				post("/api/login").contentType(
						MediaType.APPLICATION_JSON).content("{\"username\":\"user1\", \"password\": \"user\"}"))
		.andExpect(status().is(401)).andDo(document("login_fail"));
		
		this.mockMvc
				.perform(
						post("/api/login").contentType(
								MediaType.APPLICATION_JSON).content("{\"username\":\"user\", \"password\": \"user\"}"))
				.andExpect(status().isOk()).andDo(document("login_success"));
	}
}