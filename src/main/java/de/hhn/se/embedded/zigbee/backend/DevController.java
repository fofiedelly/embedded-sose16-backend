package de.hhn.se.embedded.zigbee.backend;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@Controller
@RestController
public class DevController {

	private RoomserverInfo roomserverInfo;

	@RequestMapping(value = "/dev/roomserver", method = RequestMethod.PUT)
	public ResponseEntity<String> putRoomserverInfo(
			@RequestBody final RoomserverInfo roomserverInfo) {

		this.roomserverInfo = roomserverInfo;

		return new ResponseEntity<String>("done!", HttpStatus.OK);
	}

	@RequestMapping(value = "/dev/roomserver", method = RequestMethod.GET)
	public ResponseEntity<RoomserverInfo> getRoomserverInfo() {

		if (this.roomserverInfo != null) {
			return new ResponseEntity<RoomserverInfo>(this.roomserverInfo,
					HttpStatus.OK);
		} else {
			return new ResponseEntity<RoomserverInfo>(HttpStatus.NOT_FOUND);
		}
	}

	class RoomserverInfo {
		private String ip;

		public RoomserverInfo(String ip) {
			this.ip = ip;
		}

		public String getIp() {
			return ip;
		}

		public void setIp(String ip) {
			this.ip = ip;
		}
	}

}
