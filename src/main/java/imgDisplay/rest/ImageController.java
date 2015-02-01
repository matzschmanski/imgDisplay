package imgDisplay.rest;

import imgDisplay.dao.Image;
import imgDisplay.service.ImageService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/")
public class ImageController {

	@Autowired
	Environment environment;

	@Autowired
	ImageService imageService;

	@RequestMapping(method = RequestMethod.GET)
	public @ResponseBody Page<Image> getImages() {
		return imageService.findImages(0);
	}

	@RequestMapping(value = "/view", method = RequestMethod.GET)
	public @ResponseBody Image getImage(
			@RequestParam(value = "img", required = false, defaultValue = "") Long imageId) {
		if (imageId == null || "".equals(imageId)) {
			return imageService.getLatestImage();
		} else {
			return imageService.getImageById(imageId);

		}
	}

	@RequestMapping(value = "/upload", method = RequestMethod.GET)
	public @ResponseBody String provideUploadInfo() {
		return "You can upload a file by posting to this same URL.";
	}

	@RequestMapping(value = "/upload", method = RequestMethod.POST)
	public @ResponseBody String handleFileUpload(
			@RequestParam("name") String name,
			@RequestParam("name") String comment,
			@RequestParam("file") MultipartFile file) {
		if (!file.isEmpty()) {
			return imageService.addImage(file, name, "comment").getImageUrl();
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

}
