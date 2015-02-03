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

	@RequestMapping(value = "/viewAll", method = RequestMethod.GET)
	public @ResponseBody Page<Image> getImages(
			@RequestParam(value = "page", required = false, defaultValue = "1") int page) {
		Page<Image> imagePage = imageService.findImages(page);
		return imagePage;
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
			if (name == null || "".equals(name)) {
				name = file.getName();
			} else {
				// fix endings
				String fileName = file.getOriginalFilename();
				String ending = fileName.substring(fileName.lastIndexOf("."),
						fileName.length());
				if (!name.endsWith(ending)) {
					name = name + ending;
				}
			}
			Image image = imageService.addImage(file, name, comment);
			if (image != null) {
				return image.getImageUrl();
			} else {
				return "";
			}
		} else {
			return "You failed to upload " + name
					+ " because the file was empty.";
		}
	}

}
