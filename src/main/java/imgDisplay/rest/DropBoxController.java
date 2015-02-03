package imgDisplay.rest;

import imgDisplay.service.DropBoxService;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.dropbox.core.DbxException;

@Controller
public class DropBoxController {

	@Autowired
	DropBoxService dropBoxService;

	@RequestMapping(value = "/authorizeDropBox", method = RequestMethod.GET)
	public @ResponseBody String authorizeDropBox() throws IOException {
		return dropBoxService.getCodeUrl();
	}

	@RequestMapping(value = "/finnishDropBoxAuth", method = RequestMethod.GET)
	public @ResponseBody boolean authorizeDropBox(
			@RequestParam(value = "user", required = true) String user,
			@RequestParam(value = "code", required = true) String code)
			throws IOException, DbxException {
		return dropBoxService.finnishAuth(user, code);
	}

}
